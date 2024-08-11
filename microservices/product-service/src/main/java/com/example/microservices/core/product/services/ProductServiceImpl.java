package com.example.microservices.core.product.services;

import com.example.api.core.product.Product;
import com.example.api.core.product.ProductService;
import com.example.api.exceptions.InvalidInputException;
import com.example.api.exceptions.NotFoundException;
import com.example.microservices.core.product.persistence.ProductRepository;
import com.example.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.logging.Level;

@RestController
public class ProductServiceImpl implements ProductService {
    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ServiceUtil serviceUtil;
    private final ProductMapper mapper;
    private final ProductRepository repository;

    public ProductServiceImpl(ServiceUtil serviceUtil, ProductMapper mapper, ProductRepository repository) {
        this.serviceUtil = serviceUtil;
        this.mapper = mapper;
        this.repository = repository;
    }

    private Product setProductServiceAddress(Product product) {
        product.setServiceAddress(serviceUtil.getServiceAddress());
        return product;
    }

    @Override
    public Mono<Product> getProduct(int productId) {
        LOG.debug("/product call with productId={}", productId);

        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: "  + productId);
        }

        return repository.findByProductId(productId)
                .switchIfEmpty(Mono.error(new NotFoundException("No product found for productId: " + productId)))
                .log(LOG.getName(), Level.FINE)
                .map(mapper::entityToApi)
                .map(this::setProductServiceAddress);
    }

    @Override
    public Mono<Product> createProduct(Product body) {
        LOG.debug("createProduct: try to create product for productId: {}", body.getProductId());

        if (body.getProductId() < 1) {
            throw new InvalidInputException("Invalid productId: "  + body.getProductId());
        }

        var entity = mapper.apiToEntity(body);

        return repository.save(entity)
                .log(LOG.getName(), Level.FINE)
                .onErrorMap(
                        DuplicateKeyException.class,
                        e -> new InvalidInputException("Duplicate key, Product Id: " + body.getProductId())
                )
                .map(mapper::entityToApi);
    }

    @Override
    public Mono<Void> deleteProduct(int productId) {
        LOG.debug("deleteProduct: tries to delete an entity with productId: {}", productId);
        if (productId < 1) {
            throw new InvalidInputException("Invalid productId: "  + productId);
        }

    return repository.findByProductId(productId).log(LOG.getName(), Level.FINE).map(repository::delete).flatMap(e -> e);
    }
}
