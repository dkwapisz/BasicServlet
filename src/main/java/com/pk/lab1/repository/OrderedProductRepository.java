package com.pk.lab1.repository;

import com.pk.lab1.model.OrderedProduct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class OrderedProductRepository extends BaseRepository<OrderedProduct> {

    private final EntityManager entityManager;

    public OrderedProductRepository(EntityManager entityManager) {
        super(entityManager, OrderedProduct.class, "orderedProducts");
        this.entityManager = entityManager;
    }

    public boolean doesProductExistInOrderList(Long productId) {
        String jpql = "SELECT COUNT(op) FROM orderedProducts op WHERE op.product.productId = :productId";
        TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class)
                .setParameter("productId", productId);

        Long count = query.getSingleResult();

        return count > 0;
    }

}
