package com.example.api.core.product;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

public interface ProductService {

    @GetMapping(value = "/product/{productId}", produces = "application/json")
    Mono<Product> getProduct(@PathVariable("productId") int productId);

    @PostMapping(
            value = "/product",
            consumes = "application/json",
            produces = "application/json"
    )
    Mono<Product> createProduct(@RequestBody Product body);

    @DeleteMapping(value = "/product/{productId}")
    Mono<Void> deleteProduct(@PathVariable("productId") int productId);
}
