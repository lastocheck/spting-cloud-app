package com.example.api.core.review;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class Review {
    int productId;
    int reviewId;
    String author;
    String subject;
    String content;
    String serviceAddress;
}
