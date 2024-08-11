package com.example.api.core.review;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReviewService {

    @GetMapping(value = "/review", produces = "application/json")
    Flux<Review> getReviews(@RequestParam(value = "productId", required = true) int productId);

    @PostMapping(
            value = "/review",
            consumes = "application/json",
            produces = "application/json"
    )
    Mono<Review> createReview(@RequestBody Review body);

    @DeleteMapping(value = "/review")
    Mono<Void> deleteReviews(@RequestParam(value = "productId", required = true)  int productId);
}
