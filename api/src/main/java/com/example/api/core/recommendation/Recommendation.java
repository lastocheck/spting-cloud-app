package com.example.api.core.recommendation;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Recommendation {
    int productId;
    int recommendationId;
    String author;
    int rate;
    String content;
    String serviceAddress;
}
