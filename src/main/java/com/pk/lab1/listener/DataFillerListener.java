package com.pk.lab1.listener;

import com.pk.lab1.model.Order;
import com.pk.lab1.model.Product;
import com.pk.lab1.repository.OrderRepository;
import com.pk.lab1.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@WebListener
public class DataFillerListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("derby-unit");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        try {
            OrderRepository orderRepository = new OrderRepository(entityManager);
            ProductRepository productRepository = new ProductRepository(entityManager);

            Product product1 = new Product("Product 1", 10.0, 5);
            Product product2 = new Product("Product 2", 15.0, 8);
            Product product3 = new Product("Product 3", 20.0, 3);

            List<Product> orderedProducts = new ArrayList<>();
            orderedProducts.add(product1);
            orderedProducts.add(product2);

            Order order = new Order(new Date(), "Supplier 1", "customer@example.com", "Address 123", "123-456-7890", "Additional Info", orderedProducts);

            orderRepository.addOrder(order);
            productRepository.addProduct(product1);
            productRepository.addProduct(product2);
            productRepository.addProduct(product3);

        } finally {
            entityManagerFactory.close();
        }
    }
}