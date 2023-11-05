package com.pk.lab1.service;

import com.pk.lab1.enums.OrderStatus;
import com.pk.lab1.model.Order;
import com.pk.lab1.model.OrderedProduct;
import com.pk.lab1.model.Product;
import com.pk.lab1.repository.OrderRepository;
import com.pk.lab1.repository.OrderedProductRepository;
import com.pk.lab1.repository.ProductRepository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    //private final OrderedProductRepository orderedProductRepository;
    private final EntityManager entityManager;

    public OrderService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.productRepository = new ProductRepository(entityManager);
        this.orderRepository = new OrderRepository(entityManager);
        //this.orderedProductRepository = new OrderedProductRepository(entityManager);
    }

    public List<Order> getAllOrders() {
        return orderRepository.getAllEntities();
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.getEntityById(orderId);
    }

    public OrderStatus addOrder(Order order) {
        entityManager.getTransaction().begin();

        Map<Long, Integer> productIdToQuantityMap = getOrderedProductsWithQuantity(order);

        boolean productsExistAndAvailable = isProductExistsAndAvailable(productIdToQuantityMap);

        if (productsExistAndAvailable) {
            orderRepository.addEntity(order);

            for (Map.Entry<Long, Integer> entry : productIdToQuantityMap.entrySet()) {
                Long productId = entry.getKey();
                int orderedQuantity = entry.getValue();

                Product product = productRepository.getEntityById(productId);
                product.setAvailableQuantity(product.getAvailableQuantity() - orderedQuantity);

                productRepository.updateEntity(product);
            }

            entityManager.getTransaction().commit();
            return OrderStatus.CREATED;
        } else {
            entityManager.getTransaction().rollback();
            return OrderStatus.PRODUCTS_NOT_AVAILABLE;
        }
    }

    public OrderStatus updateOrder(Order order) {
        entityManager.getTransaction().begin();

        Map<Long, Integer> productIdToNewQuantityMap = getOrderedProductsWithQuantity(order);

        boolean productsExistAndAvailable = isProductExistsAndAvailable(productIdToNewQuantityMap);

        if (productsExistAndAvailable) {
            orderRepository.updateEntity(order);

            for (Map.Entry<Long, Integer> entry : productIdToNewQuantityMap.entrySet()) {
                Long productId = entry.getKey();
                int newOrderedQuantity = entry.getValue();

                Product product = productRepository.getEntityById(productId);
                int oldOrderedQuantity = order.getOrderedProducts().stream()
                        .filter(orderedProduct -> orderedProduct.getProduct().getProductId().equals(productId))
                        .mapToInt(OrderedProduct::getQuantity)
                        .sum();

                int quantityChange = newOrderedQuantity - oldOrderedQuantity;
                product.setAvailableQuantity(product.getAvailableQuantity() - quantityChange);

                productRepository.updateEntity(product);
            }

            entityManager.getTransaction().commit();
            return OrderStatus.UPDATED;
        } else {
            entityManager.getTransaction().rollback();
            return OrderStatus.PRODUCTS_NOT_AVAILABLE;
        }
    }

    public OrderStatus deleteOrder(Long orderId) {
        entityManager.getTransaction().begin();
        Order order = orderRepository.getEntityById(orderId);

        if (order == null) {
            entityManager.getTransaction().rollback();
            return OrderStatus.NOT_FOUND;
        }

        Map<Long, Integer> productIdToQuantityMap = getOrderedProductsWithQuantity(order);

        for (Map.Entry<Long, Integer> entry : productIdToQuantityMap.entrySet()) {
            Long productId = entry.getKey();
            int orderedQuantity = entry.getValue();

            Product product = productRepository.getEntityById(productId);
            product.setAvailableQuantity(product.getAvailableQuantity() + orderedQuantity);

            productRepository.updateEntity(product);
        }

        orderRepository.deleteEntity(orderId);
        entityManager.getTransaction().commit();
        return OrderStatus.DELETED;
    }

    private Map<Long, Integer> getOrderedProductsWithQuantity(Order order) {
        return order.getOrderedProducts().stream()
                .collect(Collectors.toMap(
                        orderedProduct -> orderedProduct.getProduct().getProductId(),
                        OrderedProduct::getQuantity
                ));
    }

    private boolean isProductExistsAndAvailable(Map<Long, Integer> productIdToQuantityMap) {
        return productIdToQuantityMap.entrySet().stream()
                .allMatch(entry -> {
                    Long productId = entry.getKey();
                    int orderedQuantity = entry.getValue();
                    Product product = productRepository.getEntityById(productId);

                    return product != null && orderedQuantity <= product.getAvailableQuantity();
                });
    }
}
