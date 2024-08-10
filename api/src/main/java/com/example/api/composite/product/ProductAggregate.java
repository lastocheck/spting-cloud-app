package com.example.api.composite.product;

import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@NoArgsConstructor(force = true)
public class ProductAggregate {
    int productId;
    String name;
    int weight;
    List<RecommendationSummary> recommendations;
    List<ReviewSummary> reviews;
    ServiceAddresses serviceAddresses;
}
