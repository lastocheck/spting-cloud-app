package com.example.api.composite.product;

import lombok.Value;

@Value
public class ReviewSummary {
    int reviewId;
    String author;
    String subject;
}
