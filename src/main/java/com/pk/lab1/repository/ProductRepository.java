package com.pk.lab1.repository;

import com.pk.lab1.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.List;

public class ProductRepository {
    private final EntityManager entityManager;

    public ProductRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public List<Product> getAllProducts() {
        Query query = entityManager.createQuery("SELECT p FROM products p");
        return query.getResultList();
    }

    public Product getProductById(Long productId) {
        return entityManager.find(Product.class, productId);
    }

    public void addProduct(Product product) {
        entityManager.getTransaction().begin();
        entityManager.persist(product);
        entityManager.getTransaction().commit();
    }

    public void updateProduct(Product product) {
        entityManager.getTransaction().begin();
        entityManager.merge(product);
        entityManager.getTransaction().commit();
    }

    public void deleteProduct(Long productId) {
        Product product = getProductById(productId);
        entityManager.getTransaction().begin();
        entityManager.remove(product);
        entityManager.getTransaction().commit();
    }
}
