package com.example.microservices.core.review.persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ReviewRepository extends CrudRepository<ReviewEntity, Integer> {
    @Transactional
    List<ReviewEntity> findByProductId(int productId);
}
