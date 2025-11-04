package com.example.product_composite_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "recommendation-service")
public interface RecommendationClient {

    @GetMapping("/recommendations")
    List<Map<String, Object>> getRecommendations(@RequestParam("productId") Long productId);

    @PostMapping("/recommendations")
    Map<String, Object> createRecommendation(@RequestBody Map<String, Object> recommendation);

    @DeleteMapping("/recommendations/{id}")
    void deleteRecommendation(@PathVariable("id") Long id);
}
