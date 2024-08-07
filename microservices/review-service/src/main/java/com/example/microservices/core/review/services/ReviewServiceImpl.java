package com.example.microservices.core.review.services;

import com.example.api.core.review.Review;
import com.example.api.core.review.ReviewService;
import com.example.api.exceptions.InvalidInputException;
import com.example.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ReviewServiceImpl implements ReviewService {
    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);
    private final ServiceUtil serviceUtil;

    public ReviewServiceImpl(ServiceUtil serviceUtil) {
        this.serviceUtil = serviceUtil;
    }

    @Override
    public List<Review> getReviews(int productId) {
        LOG.debug("/review return found reviews for productId={}", productId);

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: + productId");
        }

        if (productId == 228) {
            LOG.debug("No reviews found for productId: {}", productId);
            return new ArrayList<>();
        }

        List<Review> reviews = List.of(
                new Review(productId, 1, "Author 1", "Subject 1", "Content 1", serviceUtil.getServiceAddress()),
                new Review(productId, 2, "Author 2", "Subject 2", "Content 2", serviceUtil.getServiceAddress()),
                new Review(productId, 3, "Author 3", "Subject 3", "Content 3", serviceUtil.getServiceAddress())
        );

        LOG.debug("/recommendations return the found recommendations of size {} for productId={}", reviews.size(), productId);

        return reviews;

    }
}
