package com.pk.lab1.databaseUtils;

import com.pk.lab1.model.Order;
import com.pk.lab1.model.OrderedProduct;
import com.pk.lab1.model.Product;
import com.pk.lab1.service.OrderService;
import com.pk.lab1.service.ProductService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.util.*;

@WebListener
public class DataFillerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        EntityManager entityManager = EntityManagerSingleton.getEntityManager();
        ProductService productService = new ProductService(entityManager);
        OrderService orderService = new OrderService(entityManager);

        Product product1 = new Product("Product 1", 10, 100);
        Product product2 = new Product("Product 2", 15, 150);
        Product product3 = new Product("Product 3", 20, 200);

        productService.addProduct(product1);
        productService.addProduct(product2);
        productService.addProduct(product3);

        OrderedProduct orderedProduct1 = new OrderedProduct(product1, 5);
        OrderedProduct orderedProduct2 = new OrderedProduct(product2, 2);
        OrderedProduct orderedProduct3 = new OrderedProduct(product3, 3);

        List<OrderedProduct> orderedProducts = new ArrayList<>();
        orderedProducts.add(orderedProduct1);
        orderedProducts.add(orderedProduct2);
        orderedProducts.add(orderedProduct3);

        Order order = new Order(new Date(), "Supplier 1", "customer@example.com",
                "Address 123", "123-456-7890", "Additional Info", orderedProducts);

        orderService.addOrder(order);
    }
}
