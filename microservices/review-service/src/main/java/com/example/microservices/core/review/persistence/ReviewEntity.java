package com.example.microservices.core.review.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "reviews", indexes = {@Index(name = "reviews_unique_idx", unique = true, columnList = "productId,reviewId")})
public class ReviewEntity {
    @Id
    @GeneratedValue
    private int id;
    @Version
    private int version;

    int productId;
    int reviewId;
    String author;
    String subject;
    String content;

    public ReviewEntity(int productId, int reviewId, String author, String subject, String content) {
        this.productId = productId;
        this.reviewId = reviewId;
        this.author = author;
        this.subject = subject;
        this.content = content;
    }
}
