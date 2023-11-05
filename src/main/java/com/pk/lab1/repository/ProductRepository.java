package com.pk.lab1.repository;

import com.pk.lab1.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.List;

public class ProductRepository extends BaseRepository<Product>{

    public ProductRepository(EntityManager entityManager) {
        super(entityManager, Product.class, "products");
    }
}
