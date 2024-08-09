package com.example.microservices.core.recommendaton.services;

import com.example.api.core.recommendation.Recommendation;
import com.example.api.core.recommendation.RecommendationService;
import com.example.api.exceptions.InvalidInputException;
import com.example.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class RecommedationServiceImpl implements RecommendationService {
    private static final Logger LOG = LoggerFactory.getLogger(RecommedationServiceImpl.class);
    private final ServiceUtil serviceUtil;

    public RecommedationServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {
        LOG.debug("/recommendation call to return found recommendations for productId={}", productId);

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: + productId");
        }

        if (productId == 230) {
            LOG.debug("No recommendations found for productId: {}", productId);
            return new ArrayList<>();
        }

        List<Recommendation> recommendations = List.of(
                new Recommendation(productId, 1, "Author 1", 1, "Content 1", serviceUtil.getServiceAddress()),
                new Recommendation(productId, 2, "Author 2", 2, "Content 2", serviceUtil.getServiceAddress()),
                new Recommendation(productId, 3, "Author 3", 3, "Content 3", serviceUtil.getServiceAddress())
        );

        LOG.debug("/recommendation return the found recommendations of size {} for productId={}", recommendations.size(), productId);

        return recommendations;


    }



}
