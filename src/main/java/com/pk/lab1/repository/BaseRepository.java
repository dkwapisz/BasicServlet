package com.pk.lab1.repository;

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

    public List<T> getAllEntities() {
        String stringQuery = String.format("SELECT entity FROM %s entity", tableName);
        Query query = entityManager.createQuery(stringQuery);
        return query.getResultList();
    }

    public T getEntityById(Long entityId) {
        return entityManager.find(entityType, entityId);
    }

    public void addEntity(T entity) {
        entityManager.persist(entity);
    }

    public void updateEntity(T entity) {
        entityManager.merge(entity);
    }

    public void deleteEntity(Long entityId) {
        T entity = getEntityById(entityId);
        entityManager.remove(entity);
    }
}
