package com.example.product_composite_service.controller;

import com.example.product_composite_service.client.ProductClient;
import com.example.product_composite_service.client.ReviewClient;
import com.example.product_composite_service.client.RecommendationClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/product-composite")
@RequiredArgsConstructor
@Slf4j
public class ProductCompositeController {

    private final ProductClient productClient;
    private final ReviewClient reviewClient;
    private final RecommendationClient recommendationClient;

    @Value("${server.port}")
    private int port;

    @GetMapping("/{productId}")
    public Map<String, Object> getProductComposite(@PathVariable Long productId) {
        log.info("➡️ [Composite:{}] Appel du produit complet pour ID {}", port, productId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("compositeInstancePort", port);

        // 🧩 1️⃣ Product Service avec son propre disjoncteur
        var product = getProductSafe(productId);
        result.put("product", product);
        result.put("productServiceInstance", extractInstancePort(product));

        // 🧩 2️⃣ Review Service avec son propre disjoncteur
        var reviews = getReviewsSafe(productId);
        result.put("reviews", reviews);
        result.put("reviewServiceInstances", extractInstancePortsList(reviews));

        // 🧩 3️⃣ Recommendation Service avec son propre disjoncteur
        var recommendations = getRecommendationsSafe(productId);
        result.put("recommendations", recommendations);
        result.put("recommendationServiceInstances", extractInstancePortsList(recommendations));

        return result;
    }

    // =================================================================
    // 1️⃣ Product Service avec disjoncteur dédié
    // =================================================================
    @CircuitBreaker(name = "productServiceBreaker", fallbackMethod = "fallbackProduct")
    @TimeLimiter(name = "productServiceBreaker")
    @Retry(name = "productServiceBreaker")
    public Map<String, Object> getProductSafe(Long productId) {
        return productClient.getProduct(productId);
    }

    public Map<String, Object> fallbackProduct(Long productId, Throwable ex) {
        log.error("⚠️ Fallback activé pour product-service (ID={}): {}", productId, ex.getMessage());
        return Map.of(
                "productId", productId,
                "name", "Produit indisponible",
                "weight", 0,
                "instancePort", "fallback"
        );
    }

    // =================================================================
    // 2️⃣ Review Service avec disjoncteur dédié
    // =================================================================
    @CircuitBreaker(name = "reviewServiceBreaker", fallbackMethod = "fallbackReviews")
    @TimeLimiter(name = "reviewServiceBreaker")
    @Retry(name = "reviewServiceBreaker")
    public List<Map<String, Object>> getReviewsSafe(Long productId) {
        return reviewClient.getReviews(productId);
    }

    public List<Map<String, Object>> fallbackReviews(Long productId, Throwable ex) {
        log.warn("⚠️ Fallback activé pour review-service (ID={}): {}", productId, ex.getMessage());
        return List.of(Map.of(
                "reviewId", -1,
                "author", "fallback",
                "subject", "Aucun avis disponible",
                "instancePort", "fallback"
        ));
    }

    // =================================================================
    // 3️⃣ Recommendation Service avec disjoncteur dédié
    // =================================================================
    @CircuitBreaker(name = "recommendationServiceBreaker", fallbackMethod = "fallbackRecommendations")
    @TimeLimiter(name = "recommendationServiceBreaker")
    @Retry(name = "recommendationServiceBreaker")
    public List<Map<String, Object>> getRecommendationsSafe(Long productId) {
        return recommendationClient.getRecommendations(productId);
    }

    public List<Map<String, Object>> fallbackRecommendations(Long productId, Throwable ex) {
        log.warn("⚠️ Fallback activé pour recommendation-service (ID={}): {}", productId, ex.getMessage());
        return List.of(Map.of(
                "recommendationId", -1,
                "author", "fallback",
                "content", "Aucune recommandation disponible",
                "instancePort", "fallback"
        ));
    }

    // ============================================================
    // Utilitaires
    // ============================================================
    private String extractInstancePort(Object response) {
        if (response instanceof Map<?, ?> map && map.containsKey("instancePort")) {
            return String.valueOf(map.get("instancePort"));
        }
        return "unknown";
    }

    private List<String> extractInstancePortsList(Object responseList) {
        if (responseList instanceof List<?> list) {
            List<String> ports = new ArrayList<>();
            for (Object obj : list) {
                if (obj instanceof Map<?, ?> map && map.containsKey("instancePort")) {
                    ports.add(String.valueOf(map.get("instancePort")));
                }
            }
            return ports;
        }
        return List.of("unknown");
    }
}
