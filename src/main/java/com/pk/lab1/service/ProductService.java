package com.pk.lab1.service;

import com.pk.lab1.enums.ProductStatus;
import com.pk.lab1.model.Order;
import com.pk.lab1.model.Product;
import com.pk.lab1.repository.OrderRepository;
import com.pk.lab1.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ProductService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final EntityManager entityManager;

    public ProductService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.productRepository = new ProductRepository(entityManager);
        this.orderRepository = new OrderRepository(entityManager);
    }

    public List<Product> getAllProducts() {
        List<Product> products = productRepository.getAllEntities();

        for (Product product : products) {
            entityManager.refresh(product);
        }

        return products;
    }

    public Product getProductById(Long productId) {
        return productRepository.getEntityById(productId);
    }

    public ProductStatus addProduct(Product product) {
        entityManager.getTransaction().begin();

        Product matchedProduct = productRepository.findProductByName(product.getName());

        if (nonNull(matchedProduct)) {
            matchedProduct.setAvailableQuantity(product.getAvailableQuantity());
            matchedProduct.setProductPrice(product.getProductPrice());
            productRepository.updateEntity(matchedProduct);

            entityManager.getTransaction().commit();
            return ProductStatus.UPDATED_EXISTING_PRODUCT;
        } else {
            productRepository.addEntity(product);

            entityManager.getTransaction().commit();
            return ProductStatus.CREATED;
        }
    }

    public ProductStatus updateProduct(Product product, String productIdString) {
        entityManager.getTransaction().begin();

        Long productId = Long.parseLong(productIdString);

        if (isNull(productRepository.getEntityById(productId))) {
            entityManager.getTransaction().rollback();
            return ProductStatus.PRODUCT_NOT_EXIST;
        }

        Product matchedProduct = productRepository.findProductByName(product.getName());

        if (nonNull(matchedProduct)) {
            matchedProduct.setAvailableQuantity(product.getAvailableQuantity());
            matchedProduct.setProductPrice(product.getProductPrice());
            productRepository.updateEntity(matchedProduct);

            entityManager.getTransaction().commit();
            return ProductStatus.UPDATED_EXISTING_PRODUCT;
        } else {
            product.setProductId(productId);
            productRepository.updateEntity(product);

            entityManager.getTransaction().commit();
            return ProductStatus.UPDATED;
        }
    }

    public ProductStatus deleteProduct(String productIdString) {
        entityManager.getTransaction().begin();

        Long productId = Long.parseLong(productIdString);

        if (isNull(productRepository.getEntityById(productId))) {
            entityManager.getTransaction().rollback();
            return ProductStatus.PRODUCT_NOT_EXIST;
        } else if (isProductExistOnOrderList(productId)) {
            entityManager.getTransaction().rollback();
            return ProductStatus.PRODUCT_EXIST_ON_ORDER;
        }

        productRepository.deleteEntity(productId);

        entityManager.getTransaction().commit();
        return ProductStatus.DELETED;
    }

    public boolean isProductExistOnOrderList(Long productId) {
        List<Order> orderList = orderRepository.getAllEntities();

        return orderList.stream()
                .flatMap(order -> order.getOrderedProducts().stream())
                .anyMatch(orderedProduct -> Objects.equals(orderedProduct.getProduct().getProductId(), productId));
    }

    public Product createProductObject(String jsonData) {
        JSONObject jsonObject = new JSONObject(jsonData);

        String productName = jsonObject.getString("productName");
        int productPrice = Integer.parseInt(jsonObject.getString("productPrice"));
        int availableQuantity = Integer.parseInt(jsonObject.getString("availableQuantity"));

        return new Product(productName, productPrice, availableQuantity);
    }

    public Product updateProductObject(String jsonData, String productId) {
        JSONObject jsonObject = new JSONObject(jsonData);

        Product product = getProductById(Long.parseLong(productId));

        String productName = jsonObject.has("productName") ? jsonObject.getString("productName") : product.getName();
        int productPrice = jsonObject.has("productPrice") ? Integer.parseInt(jsonObject.getString("productPrice")) : product.getProductPrice();
        int availableQuantity = jsonObject.has("availableQuantity") ? Integer.parseInt(jsonObject.getString("availableQuantity")) : product.getAvailableQuantity();

        product.setName(productName);
        product.setProductPrice(productPrice);
        product.setAvailableQuantity(availableQuantity);

        return product;
    }
}
