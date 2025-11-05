package com.example.review_service.controller;


import com.example.review_service.model.Review;
import com.example.review_service.repository.ReviewRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewRepository repository;

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        return repository.save(review);
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam Long productId) {
        return repository.findByProductId(productId);
    }
}
