package com.example.review_service.controller;


import com.example.review_service.model.Review;
import com.example.review_service.repository.ReviewRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewRepository repository;

    @Value("${server.port}")
    private int port;

    @GetMapping
    public List<Map<String, Object>> getReviews(@RequestParam("productId") Long productId) {
        return List.of(
                Map.of(
                        "service", "REVIEW-SERVICE",
                        "instancePort", port,
                        "reviewId", 1,
                        "productId", productId,
                        "author", "John Doe",
                        "subject", "Excellent produit",
                        "content", "Rapport qualité/prix exceptionnel"
                )
        );
    }

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        return repository.save(review);
    }
}
