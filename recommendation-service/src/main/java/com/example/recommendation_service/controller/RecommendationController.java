package com.example.recommendation_service.controller;


import com.example.recommendation_service.model.Recommendation;
import com.example.recommendation_service.repository.RecommendationRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationRepository repository;

    @PostMapping
    public Recommendation create(@Valid @RequestBody Recommendation rec) {
        return repository.save(rec);
    }

    @GetMapping
    public List<Recommendation> list(@RequestParam Long productId) {
        return repository.findByProductId(productId);
    }
}
