package com.pk.lab1.databaseUtils;

import com.pk.lab1.model.Order;
import com.pk.lab1.model.OrderedProduct;
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

        try (entityManagerFactory) {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            OrderRepository orderRepository = new OrderRepository(entityManager);
            ProductRepository productRepository = new ProductRepository(entityManager);

            Product product1 = new Product("Product 1", 10, 5);
            Product product2 = new Product("Product 2", 15, 8);
            Product product3 = new Product("Product 3", 20, 3);

            Order order = new Order(new Date(), "Supplier 1", "customer@example.com", "Address 123", "123-456-7890", "Additional Info");

            orderRepository.addEntity(order);

            productRepository.addEntity(product1);
            productRepository.addEntity(product2);
            productRepository.addEntity(product3);

            OrderedProduct orderedProduct1 = new OrderedProduct(2, 20, order, product1);
            OrderedProduct orderedProduct2 = new OrderedProduct(3, 45, order, product2);
            OrderedProduct orderedProduct3 = new OrderedProduct(1, 20, order, product3);

            List<OrderedProduct> orderedProducts = new ArrayList<>();
            orderedProducts.add(orderedProduct1);
            orderedProducts.add(orderedProduct2);
            orderedProducts.add(orderedProduct3);

            order.setOrderedProducts(orderedProducts);

            orderRepository.updateEntity(order);

            entityManager.close();
        }
        entityManagerFactory.close();
    }
}