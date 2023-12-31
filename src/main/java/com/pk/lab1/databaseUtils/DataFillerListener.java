package com.pk.lab1.databaseUtils;

import com.pk.lab1.enums.SupplierType;
import com.pk.lab1.model.Order;
import com.pk.lab1.model.OrderedProduct;
import com.pk.lab1.model.Product;
import com.pk.lab1.service.OrderService;
import com.pk.lab1.service.ProductService;
import jakarta.persistence.EntityManager;
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
        EntityManager entityManager = EntityManagerSingleton.getEntityManager();
        ProductService productService = new ProductService(entityManager);
        OrderService orderService = new OrderService(entityManager);

        Product product1 = new Product("Product1", 10, 1000);
        Product product2 = new Product("Product2", 15, 1000);
        Product product3 = new Product("Product3", 20, 1000);
        Product product4 = new Product("Product4", 500, 200);
        Product product5 = new Product("Product5", 150, 300);
        Product product6 = new Product("Product6", 300, 400);

        productService.addProduct(product1);
        productService.addProduct(product2);
        productService.addProduct(product3);
        productService.addProduct(product4);
        productService.addProduct(product5);
        productService.addProduct(product6);

        Order order = getOrder(product1, product2, product3);

        orderService.addOrder(order);
    }

    private static Order getOrder(Product product1, Product product2, Product product3) {
        OrderedProduct orderedProduct1 = new OrderedProduct(product1, 5);
        OrderedProduct orderedProduct2 = new OrderedProduct(product2, 2);
        OrderedProduct orderedProduct3 = new OrderedProduct(product3, 3);

        List<OrderedProduct> orderedProducts = new ArrayList<>();
        orderedProducts.add(orderedProduct1);
        orderedProducts.add(orderedProduct2);
        orderedProducts.add(orderedProduct3);

        return new Order(new Date(), SupplierType.UPS, "customer@example.com",
                "Address 123", "123-456-7890", "Additional Info", orderedProducts);
    }
}
