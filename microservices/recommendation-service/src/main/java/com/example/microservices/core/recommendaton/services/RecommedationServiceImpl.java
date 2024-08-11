package com.example.microservices.core.recommendaton.services;

import com.example.api.core.recommendation.Recommendation;
import com.example.api.core.recommendation.RecommendationService;
import com.example.api.exceptions.InvalidInputException;
import com.example.microservices.core.recommendaton.persistence.RecommendationEntity;
import com.example.microservices.core.recommendaton.persistence.RecommendationRepository;
import com.example.util.http.ServiceUtil;
import org.springframework.dao.DuplicateKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Level;

@RestController
public class RecommedationServiceImpl implements RecommendationService {
    private static final Logger LOG = LoggerFactory.getLogger(RecommedationServiceImpl.class);
    private final ServiceUtil serviceUtil;
    private final RecommendationMapper mapper;
    private final RecommendationRepository repository;

    public RecommedationServiceImpl(ServiceUtil serviceUtil, RecommendationMapper mapper, RecommendationRepository repository) {
        this.serviceUtil = serviceUtil;
        this.mapper = mapper;
        this.repository = repository;
    }

    private Recommendation setRecommendationServiceAddress(Recommendation recommendation) {
        recommendation.setServiceAddress(serviceUtil.getServiceAddress());
        return recommendation;
    }

    @Override
    public Flux<Recommendation> getRecommendations(int productId) {
        LOG.debug("/recommendation call to return recommendations for productId={}", productId);

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        return repository.findByProductId(productId)
                .log(LOG.getName(), Level.FINE)
                .map(mapper::entityToApi)
                .map(this::setRecommendationServiceAddress);
    }

    @Override
    public Mono<Recommendation> createRecommendation(Recommendation body) {
        LOG.debug("createRecommendation: try to create recommendation for productId: {}, recommendationId: {}", body.getProductId(), body.getRecommendationId());

        if (body.getProductId() < 1) {
            throw new InvalidInputException("Invalid productId: "  + body.getProductId());
        }

        RecommendationEntity entity = mapper.apiToEntity(body);

        return repository.save(entity)
                .log(LOG.getName(), Level.FINE)
                .onErrorMap(
                        DuplicateKeyException.class,
                        e -> new InvalidInputException("Duplicate key, Product Id: %d, Recommendation Id: %d".formatted(body.getProductId(), body.getRecommendationId())))
                .map(mapper::entityToApi);
    }

    @Override
    public Mono<Void> deleteRecommendations(int productId) {
        LOG.debug("deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        return repository.deleteAll(repository.findByProductId(productId));
    }
}
