package com.example.microservices.core.recommendaton.services;

import com.example.api.core.recommendation.Recommendation;
import com.example.api.core.recommendation.RecommendationService;
import com.example.api.exceptions.InvalidInputException;
import com.example.microservices.core.recommendaton.persistence.RecommendationEntity;
import com.example.microservices.core.recommendaton.persistence.RecommendationRepository;
import com.example.util.http.ServiceUtil;
import com.mongodb.DuplicateKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<Recommendation> getRecommendations(int productId) {
        LOG.debug("/recommendation call to return recommendations for productId={}", productId);

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: " + productId);
        }

        List<RecommendationEntity> entities = repository.findByProductId(productId);
        List<Recommendation> apis = mapper.entityListToApiList(entities);
        apis.forEach(a -> a.setServiceAddress(serviceUtil.getServiceAddress()));

        LOG.debug("/recommendation return recommendations of size {} for productId={}", apis.size(), productId);

        return apis;
    }

    @Override
    public Recommendation createRecommendation(Recommendation body) {
        LOG.debug("createRecommendation: try to create recommendation for productId: {}, recommendationId: {}", body.getProductId(), body.getRecommendationId());
        try {
            RecommendationEntity entity = mapper.apiToEntity(body);
            RecommendationEntity newEntity = repository.save(entity);

            LOG.debug("createRecommendation: created a recommendation entity: {}/{}", body.getProductId(), body.getRecommendationId());
            return mapper.entityToApi(newEntity);
        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Product Id: %d, Recommendation Id: %d".formatted(body.getProductId(), body.getRecommendationId()));
        }
    }

    @Override
    public void deleteRecommendations(int productId) {
        LOG.debug("deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
        repository.deleteAll(repository.findByProductId(productId));
    }
}
