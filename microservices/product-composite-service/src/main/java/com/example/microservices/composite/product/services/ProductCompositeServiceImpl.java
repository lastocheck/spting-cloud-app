package com.example.microservices.composite.product.services;

import com.example.api.composite.product.*;
import com.example.api.core.product.Product;
import com.example.api.core.recommendation.Recommendation;
import com.example.api.core.review.Review;
import com.example.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductCompositeServiceImpl implements ProductCompositeService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeServiceImpl.class);

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

    @Override
    public void createProduct(ProductAggregate body) {
        try {
            LOG.debug("createCompositeProduct: creates a new composite entity for productId: {}", body.getProductId());

            Product product = new Product(body.getProductId(), body.getName(), body.getWeight(), null);
            integration.createProduct(product);

            if (body.getRecommendations() != null) {
                body.getRecommendations().forEach(recommendation -> integration.createRecommendation(new Recommendation(
                        body.getProductId(),
                        recommendation.getRecommendationId(),
                        recommendation.getAuthor(),
                        recommendation.getRate(),
                        recommendation.getContent(),
                        null
                )));
            }
            if (body.getReviews() != null) {
                body.getReviews().forEach(review -> integration.createReview(new Review(
                        body.getProductId(),
                        review.getReviewId(),
                        review.getAuthor(),
                        review.getSubject(),
                        review.getContent(),
                        null
                )));
            }

            LOG.debug("createCompositeProduct: composite entities created for productId: {}", body.getProductId());
        } catch (RuntimeException re) {
            LOG.warn("createCompositeProduct failed", re);
            throw re;
        }
    }

    @Override
    public void deleteProduct(int productId) {
        LOG.debug("deleteCompositeProduct: Deletes a product aggregate for productId: {}", productId);
        integration.deleteProduct(productId);
        integration.deleteRecommendations(productId);
        integration.deleteReviews(productId);
        LOG.debug("deleteCompositeProduct: aggregate entities deleted for productId: {}", productId);
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
                        recommendation.getRate(),
                        recommendation.getContent()
                )).toList());
        List<ReviewSummary> reviewSummaries = (reviews == null ? null :
                reviews.stream().map(review -> new ReviewSummary(
                        review.getReviewId(),
                        review.getAuthor(),
                        review.getSubject(),
                        review.getContent()
                )).toList());
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
