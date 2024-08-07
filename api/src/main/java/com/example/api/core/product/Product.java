package com.example.api.core.product;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Product {
    int productId;
    String name;
    int weight;
    String serviceAddress;
}
