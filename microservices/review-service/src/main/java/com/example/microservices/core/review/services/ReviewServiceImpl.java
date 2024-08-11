package com.example.microservices.core.review.services;

import com.example.api.core.review.Review;
import com.example.api.core.review.ReviewService;
import com.example.api.exceptions.InvalidInputException;
import com.example.microservices.core.review.persistence.ReviewEntity;
import com.example.microservices.core.review.persistence.ReviewRepository;
import com.example.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;
import java.util.logging.Level;

@RestController
public class ReviewServiceImpl implements ReviewService {
    private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);
    private final ServiceUtil serviceUtil;
    private final ReviewMapper mapper;
    private final ReviewRepository repository;
    private final Scheduler jdbcScheduler;

    public ReviewServiceImpl(ServiceUtil serviceUtil, ReviewMapper mapper, ReviewRepository repository, @Qualifier("jdbcScheduler") Scheduler jdbcScheduler) {
        this.serviceUtil = serviceUtil;
        this.mapper = mapper;
        this.repository = repository;
        this.jdbcScheduler = jdbcScheduler;
    }

    @Override
    public Flux<Review> getReviews(int productId) {
        LOG.debug("/review call to return reviews for productId={}", productId);

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        return Mono.fromCallable(() -> internalGetReviews(productId))
                .flatMapMany(Flux::fromIterable)
                .log(LOG.getName(), Level.FINE)
                .subscribeOn(jdbcScheduler);
    }

    private List<Review> internalGetReviews(int productId) {
        List<ReviewEntity> entityList = repository.findByProductId(productId);
        List<Review> reviews = mapper.entityListToApiList(entityList);
        reviews.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("getReviews: return reviews of size {} for productId={}", reviews.size(), productId);
        return reviews;
    }

    @Override
    public Mono<Review> createReview(Review body) {
        LOG.debug("createReview: try to create review for productId: {}, reviewId: {}", body.getProductId(), body.getReviewId());

        if (body.getProductId() < 1) {
            throw new InvalidInputException("Invalid productId: " + body.getProductId());
        }

        return Mono.fromCallable(() -> internalCreateReview(body)).subscribeOn(jdbcScheduler);
    }

    private Review internalCreateReview(Review body) {
        try {
            ReviewEntity entity = mapper.apiToEntity(body);
            ReviewEntity newEntity = repository.save(entity);

            LOG.debug("createReview: created a review entity: {}/{}", body.getProductId(), body.getReviewId());
            return mapper.entityToApi(newEntity);

        } catch (DataIntegrityViolationException dive) {
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Review Id:" + body.getReviewId());
        }
    }

    @Override
    public Mono<Void> deleteReviews(int productId) {
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }
        return Mono.fromRunnable(() -> internalDeleteReviews(productId)).subscribeOn(jdbcScheduler).then();
    }

    private void internalDeleteReviews(int productId) {
        LOG.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId));
    }
}
