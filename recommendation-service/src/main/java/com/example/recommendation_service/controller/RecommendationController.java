package com.example.recommendation_service.controller;

import com.example.recommendation_service.model.Recommendation;
import com.example.recommendation_service.repository.RecommendationRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationRepository repository;

    @Value("${server.port}")
    private int port;

    // ✅ 1. Endpoint principal : utilisé par le ProductComposite
    @GetMapping("/product/{productId}")
    public Map<String, Object> getByProduct(@PathVariable Long productId) {
        List<Recommendation> recommendations = repository.findByProductId(productId);

        return Map.of(
                "service", "RECOMMENDATION-SERVICE",
                "instancePort", port,
                "data", recommendations
        );
    }

    // ✅ 2. Créer une nouvelle recommandation
    @PostMapping
    public ResponseEntity<Recommendation> create(@Valid @RequestBody Recommendation recommendation) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(recommendation));
    }

    // ✅ 3. Supprimer une recommandation
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
