package com.pk.lab1.repository;

import com.pk.lab1.model.Product;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.List;

public class BaseRepository<T> {
    private final EntityManager entityManager;
    private final Class<T> entityType;
    private final String tableName;

    public BaseRepository(EntityManager entityManager, Class<T> entityType, String tableName) {
        this.entityManager = entityManager;
        this.entityType = entityType;
        this.tableName = tableName;
    }

    public List<Product> getAllEntities() {
        String stringQuery = String.format("SELECT entity FROM %s entity", tableName);
        Query query = entityManager.createQuery(stringQuery);
        return query.getResultList();
    }

    public T getEntityById(Long productId) {
        return entityManager.find(entityType, productId);
    }

    public void addEntity(T product) {
        entityManager.getTransaction().begin();
        entityManager.persist(product);
        entityManager.getTransaction().commit();
    }

    public void updateEntity(T product) {
        entityManager.getTransaction().begin();
        entityManager.merge(product);
        entityManager.getTransaction().commit();
    }

    public void deleteEntity(Long entityId) {
        T product = getEntityById(entityId);
        entityManager.getTransaction().begin();
        entityManager.remove(product);
        entityManager.getTransaction().commit();
    }
}
