package com.pk.lab1.databaseUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EntityManagerSingleton {

    private volatile static EntityManagerFactory factory;

    private static EntityManagerFactory getInstance() {
        if (factory == null) {
            synchronized (EntityManagerFactory.class) {
                if (factory == null) {
                    factory = Persistence.createEntityManagerFactory("derby-unit");
                }
            }
        }
        return factory;
    }

    public static EntityManager getEntityManager() {
        return getInstance().createEntityManager();
    }

}
