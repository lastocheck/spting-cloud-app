package com.example.api.core.recommendation;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class Recommendation {
    int productId;
    int recommendationId;
    String author;
    int rate;
    String content;
    String serviceAddress;
}
