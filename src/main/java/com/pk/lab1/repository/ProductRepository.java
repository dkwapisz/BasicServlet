package com.pk.lab1.repository;

import com.pk.lab1.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class ProductRepository extends BaseRepository<Product>{

    private final EntityManager entityManager;

    public ProductRepository(EntityManager entityManager) {
        super(entityManager, Product.class, "products");
        this.entityManager = entityManager;
    }

    public Product findProductByNameAndPrice(String name, int price) {
        String jpql = "SELECT p FROM products p WHERE p.name = :name AND p.productPrice = :price";
        TypedQuery<Product> query = entityManager.createQuery(jpql, Product.class)
                .setParameter("name", name)
                .setParameter("price", price);

        List<Product> results = query.getResultList();

        if (results.isEmpty()) {
            return null;
        }

        return results.get(0);
    }

    public Product findProductByName(String name) {
        String jpql = "SELECT p FROM products p WHERE p.name = :name";
        TypedQuery<Product> query = entityManager.createQuery(jpql, Product.class)
                .setParameter("name", name);

        List<Product> results = query.getResultList();

        if (results.isEmpty()) {
            return null;
        }

        return results.get(0);
    }
}
