package com.example.microservices.composite.product.services;

import com.example.api.composite.product.*;
import com.example.api.core.product.Product;
import com.example.api.core.recommendation.Recommendation;
import com.example.api.core.review.Review;
import com.example.util.http.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductCompositeServiceImpl implements ProductCompositeService {
    private final ProductCompositeIntegration integration;
    private final ServiceUtil serviceUtil;

    @Autowired
    public ProductCompositeServiceImpl(ProductCompositeIntegration integration, ServiceUtil serviceUtil) {
        this.integration = integration;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public ProductAggregate getProduct(int productId) {
        Product product = integration.getProduct(productId);
        List<Recommendation> recommendations = integration.getRecommendations(productId);
        List<Review> reviews = integration.getReviews(productId);

        return createAggregate(product, recommendations, reviews, serviceUtil.getServiceAddress());
    }

    private ProductAggregate createAggregate(
            Product product,
            List<Recommendation> recommendations,
            List<Review> reviews,
            String serviceAddress) {

        List<RecommendationSummary> recommendationSummaries = (recommendations == null ? null :
                recommendations.stream().map(recommendation -> new RecommendationSummary(
                        recommendation.getRecommendationId(),
                        recommendation.getAuthor(),
                        recommendation.getRate())).toList()
                );
        List<ReviewSummary> reviewSummaries = (reviews == null ? null :
                reviews.stream().map(review -> new ReviewSummary(
                        review.getReviewId(),
                        review.getAuthor(),
                        review.getSubject()
                )).toList()
                );
        String productAddress = product.getServiceAddress();
        String recommendationsAddress = (recommendations != null && recommendations.size() > 0) ?
                recommendations.get(0).getServiceAddress() :
                null;
        String reviewsAddress = (reviews != null && reviews.size() > 0) ?
                reviews.get(0).getServiceAddress() :
                null;
        var serviceAddresses = new ServiceAddresses(serviceAddress, productAddress, recommendationsAddress, reviewsAddress);

        return new ProductAggregate(
                product.getProductId(),
                product.getName(),
                product.getWeight(),
                recommendationSummaries,
                reviewSummaries,
                serviceAddresses);
    }
}
