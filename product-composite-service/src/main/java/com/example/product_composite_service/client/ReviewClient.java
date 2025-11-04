package com.example.product_composite_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "review-service")
public interface ReviewClient {

    @GetMapping("/reviews")
    List<Map<String, Object>> getReviews(@RequestParam("productId") Long productId);

    @PostMapping("/reviews")
    Map<String, Object> createReview(@RequestBody Map<String, Object> review);

    @DeleteMapping("/reviews/{id}")
    void deleteReview(@PathVariable("id") Long id);
}
