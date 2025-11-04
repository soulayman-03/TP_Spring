package com.example.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AuthorizationFilter.Config> {

    private final WebClient webClient = WebClient.builder()
            .baseUrl("http://localhost:8090") // ✅ ton authorization-service
            .build();

    public AuthorizationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            var request = exchange.getRequest();
            String username = request.getHeaders().getFirst("username");
            String password = request.getHeaders().getFirst("password");
            String role = request.getHeaders().getFirst("role");

            // 🚫 Vérifie les headers
            if (username == null || password == null || role == null) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // ✅ Appelle authorization-service pour valider
            return webClient.post()
                    .uri("/auth/check")
                    .header("username", username)
                    .header("password", password)
                    .header("role", role)
                    .retrieve()
                    .toBodilessEntity()
                    .flatMap(response -> {
                        if (response.getStatusCode().is2xxSuccessful()) {
                            return chain.filter(exchange); // autorisé
                        } else {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }
                    })
                    .onErrorResume(e -> {
                        System.out.println("⚠️ Authorization service unreachable: " + e.getMessage());
                        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
                        return exchange.getResponse().setComplete();
                    });
        };
    }

    public static class Config { }
}
