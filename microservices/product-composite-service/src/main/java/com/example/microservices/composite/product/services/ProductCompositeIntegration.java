package com.example.microservices.composite.product.services;

import com.example.api.core.product.Product;
import com.example.api.core.product.ProductService;
import com.example.api.core.recommendation.Recommendation;
import com.example.api.core.recommendation.RecommendationService;
import com.example.api.core.review.Review;
import com.example.api.core.review.ReviewService;
import com.example.api.exceptions.InvalidInputException;
import com.example.api.exceptions.NotFoundException;
import com.example.util.http.HttpErrorInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductCompositeIntegration implements ProductService, RecommendationService, ReviewService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String productServiceUrl;
    private final String recommendationServiceUrl;
    private final String reviewServiceUrl;

    @Autowired
    public ProductCompositeIntegration(
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${app.product-service.host}") String productServiceHost,
            @Value("${app.product-service.port}") int productServicePort,
            @Value("${app.recommendation-service.host}") String recommendationServiceHost,
            @Value("${app.recommendation-service.port}") int recommendationServicePort,
            @Value("${app.review-service.host}") String reviewServiceHost,
            @Value("${app.review-service.port}") int reviewServicePort
    ) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;

        productServiceUrl = "http://%s:%d/product".formatted(productServiceHost, productServicePort);
        recommendationServiceUrl = "http://%s:%d/recommendation?productId=".formatted(recommendationServiceHost, recommendationServicePort);
        reviewServiceUrl = "http://%s:%d/review?productId=".formatted(reviewServiceHost, reviewServicePort);
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return objectMapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException e) {
        return switch (HttpStatus.resolve(e.getStatusCode().value())) {
            case NOT_FOUND -> new NotFoundException(getErrorMessage(e));
            case UNPROCESSABLE_ENTITY -> new InvalidInputException(getErrorMessage(e));
            default -> {
                LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", e.getStatusCode());
                LOG.warn("Error body: {}", e.getResponseBodyAsString());
                yield e;
            }
        };
    }

    public Product getProduct(int productId) {
        try {
            String url =  productServiceUrl + "/" + productId;

            LOG.debug("Will call getProduct API on URL: {}", url);
            Product product = restTemplate.getForObject(url, Product.class);
            LOG.debug("Found a product with id: {}", product.getProductId());

            return product;
        } catch (HttpClientErrorException e) {
            throw handleHttpClientException(e);
        }
    }

    @Override
    public Product createProduct(Product body) {
        try {
            LOG.debug("Will post a new product to URL: {}", productServiceUrl);
            Product product = restTemplate.postForObject(productServiceUrl, body, Product.class);
            LOG.debug("Created a product with id: {}", product.getProductId());
            return product;
        } catch (HttpClientErrorException e) {
            throw handleHttpClientException(e);
        }
    }

    @Override
    public void deleteProduct(int productId) {
        try {
            String url = productServiceUrl + "/" + productId;
            LOG.debug("Will call the deleteProduct API on URL: {}", url);
            restTemplate.delete(url);
        } catch (HttpClientErrorException e) {
            throw handleHttpClientException(e);
        }
    }

    @Override
    public List<Recommendation> getRecommendations(int productId) {
        try {
            String url = recommendationServiceUrl + productId;

            LOG.debug("Will call getRecommendations API on URL: {}", url);
            List<Recommendation> recommendations = restTemplate.
                    exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Recommendation>>() {})
                    .getBody();
            LOG.debug("Found {} recommendations for a product with id: {}", recommendations.size(), productId);

            return recommendations;
        } catch (Exception e) {
            LOG.warn("Got an exception while requesting recommendations, return zero recommendations: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Recommendation createRecommendation(Recommendation body) {
        try {
            LOG.debug("Will post a new recommendation to URL: {}", recommendationServiceUrl);
            Recommendation recommendation = restTemplate.postForObject(recommendationServiceUrl, body, Recommendation.class);
            LOG.debug("Created a recommendation with id: {}", recommendation.getProductId());
            return recommendation;
        } catch (HttpClientErrorException e) {
            throw handleHttpClientException(e);
        }
    }

    @Override
    public void deleteRecommendations(int productId) {
        String url = "%s?productId=%d".formatted(recommendationServiceUrl, productId);
        LOG.debug("Will call the deleteRecommendations API on URL: {}", url);
        try {
            restTemplate.delete(url);
        } catch (HttpClientErrorException e) {
            throw handleHttpClientException(e);
        }
    }

    @Override
    public List<Review> getReviews(int productId) {
        try {
            String url = reviewServiceUrl + productId;

            LOG.debug("Will call getReviews API on URL: {}", url);
            List<Review> reviews = restTemplate.
                    exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Review>>() {})
                    .getBody();
            LOG.debug("Found {} reviews for a product with id: {}", reviews.size(), productId);

            return reviews;
        } catch (Exception e) {
            LOG.warn("Got an exception while requesting reviews, return zero reviews: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Review createReview(Review body) {
        try {
            LOG.debug("Will post a new review to URL: {}", reviewServiceUrl);
            Review review = restTemplate.postForObject(reviewServiceUrl, body, Review.class);
            LOG.debug("Created a review with id: {}", review.getProductId());
            return review;
        } catch (HttpClientErrorException e) {
            throw handleHttpClientException(e);
        }
    }

    @Override
    public void deleteReviews(int productId) {
        String url = "%s?productId=%d".formatted(reviewServiceUrl, productId);
        LOG.debug("Will call the deleteReviews API on URL: {}", url);
        try {
            restTemplate.delete(url);
        } catch (HttpClientErrorException e) {
            throw handleHttpClientException(e);
        }
    }
}
