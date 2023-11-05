package com.pk.lab1.repository;

import com.pk.lab1.model.OrderedProduct;
import jakarta.persistence.EntityManager;

public class OrderedProductRepository extends BaseRepository<OrderedProduct> {

    public OrderedProductRepository(EntityManager entityManager) {
        super(entityManager, OrderedProduct.class, "orderedProduct");
    }

}
