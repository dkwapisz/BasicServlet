package com.pk.lab1.service;

import com.pk.lab1.enums.OrderStatus;
import com.pk.lab1.model.Order;
import com.pk.lab1.model.OrderedProduct;
import com.pk.lab1.model.Product;
import com.pk.lab1.repository.OrderRepository;
import com.pk.lab1.repository.ProductRepository;
import jakarta.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final EntityManager entityManager;

    public OrderService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.productRepository = new ProductRepository(entityManager);
        this.orderRepository = new OrderRepository(entityManager);
    }

    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.getAllEntities();

        for (Order order : orders) {
            entityManager.refresh(order);
        }

        return orders;
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

    public OrderStatus updateOrder(Order order, String orderIdString) {
        entityManager.getTransaction().begin();

        Map<Long, Integer> productIdToNewQuantityMap = getOrderedProductsWithQuantity(order);

        boolean productsExistAndAvailable = isProductExistsAndAvailable(productIdToNewQuantityMap);

        if (productsExistAndAvailable) {
            Long orderId = Long.valueOf(orderIdString);
            order.setOrderId(orderId);

            Order oldOrder = new Order();
            oldOrder.setOrderedProducts(new ArrayList<>(orderRepository.getEntityById(orderId).getOrderedProducts()));
            orderRepository.updateEntity(order);

            for (Map.Entry<Long, Integer> entry : productIdToNewQuantityMap.entrySet()) {
                Long productId = entry.getKey();
                int newOrderedQuantity = entry.getValue();

                Product product = productRepository.getEntityById(productId);
                int oldOrderedQuantity = oldOrder.getOrderedProducts().stream()
                        .filter(orderedProduct -> orderedProduct.getProduct().getProductId().equals(productId))
                        .mapToInt(OrderedProduct::getQuantity)
                        .sum();

                int quantityChange = Math.abs(newOrderedQuantity - oldOrderedQuantity);
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

    public List<OrderedProduct> createOrderedProductList(List<String> productNames, List<Integer> productQuantities) {
        if (productNames.size() != productQuantities.size()) {
            throw new RuntimeException("OrderedProducts cannot be created");
        }

        List<OrderedProduct> orderedProducts = new ArrayList<>();

        IntStream.range(0, productNames.size()).forEach(i -> {
            Product foundProduct = productRepository.findProductByName(productNames.get(i));
            orderedProducts.add(new OrderedProduct(foundProduct, productQuantities.get(i)));
        });

        return orderedProducts;
    }

    private Map<Long, Integer> getOrderedProductsWithQuantity(Order order) {
        return order.getOrderedProducts().stream()
                .filter(orderedProduct -> orderedProduct.getProduct() != null)
                .collect(Collectors.toMap(
                        orderedProduct -> productRepository.findProductByName(orderedProduct.getProduct().getName())
                                .getProductId(),
                        OrderedProduct::getQuantity
                ));
    }

    private boolean isProductExistsAndAvailable(Map<Long, Integer> productIdToQuantityMap) {
        if (productIdToQuantityMap.isEmpty()) {
            return false;
        }

        return productIdToQuantityMap.entrySet().stream()
                .allMatch(entry -> {
                    Long productId = entry.getKey();
                    int orderedQuantity = entry.getValue();
                    Product product = productRepository.getEntityById(productId);

                    return product != null && orderedQuantity <= product.getAvailableQuantity();
                });
    }
}
