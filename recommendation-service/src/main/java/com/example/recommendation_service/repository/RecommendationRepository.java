package com.example.recommendation_service.repository;


import com.example.recommendation_service.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByProductId(Long productId);
}
