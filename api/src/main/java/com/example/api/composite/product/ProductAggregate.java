package com.example.api.composite.product;

import lombok.Value;

import java.util.List;

@Value
public class ProductAggregate {
    int productId;
    String name;
    int weight;
    List<RecommendationSummary> recommendations;
    List<ReviewSummary> reviews;
    ServiceAddresses serviceAddresses;
}
