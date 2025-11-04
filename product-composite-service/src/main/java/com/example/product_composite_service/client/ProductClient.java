package com.example.product_composite_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/products/{id}")
    Map<String, Object> getProduct(@PathVariable("id") Long id);

    @PostMapping("/products")
    Map<String, Object> createProduct(@RequestBody Map<String, Object> product);

    @DeleteMapping("/products/{id}")
    void deleteProduct(@PathVariable("id") Long id);
}
