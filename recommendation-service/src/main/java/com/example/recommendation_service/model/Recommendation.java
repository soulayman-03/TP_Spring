package com.example.recommendation_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

@Entity
@Data
public class Recommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recommendationId;

    @NotNull
    private Long productId;

    @NotBlank
    private String author;

    @PositiveOrZero
    @Max(100)
    private Integer rate;

    @NotBlank
    private String content;
}
