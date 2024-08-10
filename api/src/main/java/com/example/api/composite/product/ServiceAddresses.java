package com.example.api.composite.product;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class ServiceAddresses {
    String compositeAddress;
    String productAddress;
    String reviewAddress;
    String recommendationAddress;
}
