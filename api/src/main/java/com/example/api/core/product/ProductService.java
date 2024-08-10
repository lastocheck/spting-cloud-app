package com.example.api.core.product;

import org.springframework.web.bind.annotation.*;

public interface ProductService {

    @GetMapping(value = "/product/{productId}", produces = "application/json")
    Product getProduct(@PathVariable("productId") int productId);

    @PostMapping(
            value = "/product",
            consumes = "application/json",
            produces = "application/json"
    )
    Product createProduct(@RequestBody Product body);

    @DeleteMapping(value = "/product/{productId}")
    void deleteProduct(@PathVariable("productId") int productId);
}
