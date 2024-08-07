package com.example.api.core.review;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface ReviewService {

    @GetMapping(value = "/review", produces = "application/json")
    String getReview(@RequestParam(value = "productId", required = true) int productId);
}
