package com.example.api.composite.product;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class RecommendationSummary {
    int recommendationId;
    String author;
    int rate;
    String content;
}
