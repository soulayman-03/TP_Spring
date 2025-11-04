package com.example.product_composite_service.controller;

import com.example.product_composite_service.client.ProductClient;
import com.example.product_composite_service.client.ReviewClient;
import com.example.product_composite_service.client.RecommendationClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


import java.util.*;

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

    // ✅ GET - produit complet (déjà existant)
    @GetMapping("/{productId}")
    @CircuitBreaker(name = "compositeBreaker", fallbackMethod = "fallbackComposite")
    public Map<String, Object> getProductComposite(@PathVariable Long productId) {
        Map<String, Object> result = new HashMap<>();
        result.put("instancePort", port);
        result.put("product", productClient.getProduct(productId));
        result.put("reviews", reviewClient.getReviews(productId));
        result.put("recommendations", recommendationClient.getRecommendations(productId));
        return result;
    }

    // 🩹 Méthode de secours appelée si un des microservices échoue
    private Map<String, Object> fallbackComposite(Long productId, Throwable ex) {
        log.error("⚠️ Circuit Breaker triggered for productId {}: {}", productId, ex.getMessage());

        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("instancePort", port);
        fallbackResponse.put("message", "⚠️ Service temporarily unavailable (fallback activated)");
        fallbackResponse.put("productId", productId);
        fallbackResponse.put("error", ex.getClass().getSimpleName());
        fallbackResponse.put("details", ex.getMessage());

        return fallbackResponse;
    }

    // ✅ GET - produit seul
    @GetMapping("/products/{productId}")
    public Map<String, Object> getProductOnly(@PathVariable Long productId) {
        return productClient.getProduct(productId);
    }

    // ✅ GET - avis d’un produit
    @GetMapping("/reviews/{productId}")
    public List<Map<String, Object>> getReviewsByProduct(@PathVariable Long productId) {
        return reviewClient.getReviews(productId);
    }

    // ✅ GET - recommandations d’un produit
    @GetMapping("/recommendations/{productId}")
    public List<Map<String, Object>> getRecommendationsByProduct(@PathVariable Long productId) {
        return recommendationClient.getRecommendations(productId);
    }

    // ✅ POST - créer produit complet
    @PostMapping
    public Map<String, Object> createComposite(@RequestBody Map<String, Object> body) {
        Map<String, Object> product = (Map<String, Object>) body.get("product");
        List<Map<String, Object>> reviews = (List<Map<String, Object>>) body.get("reviews");
        List<Map<String, Object>> recommendations = (List<Map<String, Object>>) body.get("recommendations");

        // Création du produit
        Map<String, Object> savedProduct = productClient.createProduct(product);
        Long productId = ((Number) savedProduct.get("productId")).longValue();

        // Création des sous-éléments
        if (reviews != null) {
            for (Map<String, Object> r : reviews) {
                r.put("productId", productId);
                reviewClient.createReview(r);
            }
        }

        if (recommendations != null) {
            for (Map<String, Object> rec : recommendations) {
                rec.put("productId", productId);
                recommendationClient.createRecommendation(rec);
            }
        }

        return Map.of("message", "Product composite created successfully", "productId", productId);
    }

    // ✅ DELETE - supprimer produit complet
    @DeleteMapping("/{productId}")
    public Map<String, Object> deleteComposite(@PathVariable Long productId) {
        var reviews = reviewClient.getReviews(productId);
        var recos = recommendationClient.getRecommendations(productId);

        // Supprime les dépendances
        reviews.forEach(r -> reviewClient.deleteReview(((Number) r.get("reviewId")).longValue()));
        recos.forEach(r -> recommendationClient.deleteRecommendation(((Number) r.get("recommendationId")).longValue()));

        // Supprime le produit
        productClient.deleteProduct(productId);

        return Map.of("message", "Product composite deleted successfully", "productId", productId);
    }
}
