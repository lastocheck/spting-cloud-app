package com.example.api.composite.product;

import lombok.Value;

@Value
public class RecommendationSummary {
    int recommendationId;
    String author;
    int rate;
}
