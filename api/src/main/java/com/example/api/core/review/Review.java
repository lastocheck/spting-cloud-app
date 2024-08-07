package com.example.api.core.review;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Review {
    int productId;
    int reviewId;
    String author;
    String subject;
    String content;
    String serviceAddress;
}
