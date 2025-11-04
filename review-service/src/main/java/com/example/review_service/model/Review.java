package com.example.review_service.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @NotNull
    private Long productId;

    @NotBlank
    private String author;

    @NotBlank
    private String subject;

    @NotBlank
    private String content;
}
