package com.example.api.core.recommendation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface RecommendationService {

    @GetMapping(value = "/recommendation", produces = "application/json")
    String getRecommendation(@RequestParam(value = "productId", required = true) int productId);
}
