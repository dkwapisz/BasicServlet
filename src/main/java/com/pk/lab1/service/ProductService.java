package com.pk.lab1.service;

import com.pk.lab1.model.Product;
import com.pk.lab1.repository.ProductRepository;
import jakarta.persistence.EntityManager;

import java.util.List;

import static java.util.Objects.nonNull;

public class ProductService {
    private final ProductRepository productRepository;
    private final EntityManager entityManager;

    public ProductService(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.productRepository = new ProductRepository(entityManager);
    }

    public List<Product> getAllProducts() {
        return productRepository.getAllEntities();
    }

    public Product getProductById(Long productId) {
        return productRepository.getEntityById(productId);
    }

    public void addProduct(Product product) {
        entityManager.getTransaction().begin();

        Product matchedProduct = productRepository.findProductByNameAndPrice(product.getName(), product.getProductPrice());

        if (nonNull(matchedProduct)) {
            matchedProduct.setAvailableQuantity(matchedProduct.getAvailableQuantity() + product.getAvailableQuantity());
            productRepository.updateEntity(matchedProduct);
        } else {
            productRepository.addEntity(product);
        }

        entityManager.getTransaction().commit();
    }

    public void updateProduct(Product product) {
        entityManager.getTransaction().begin();
        productRepository.updateEntity(product);
        entityManager.getTransaction().commit();
    }

    public void deleteProduct(Long productId) {
        entityManager.getTransaction().begin();
        productRepository.deleteEntity(productId);
        entityManager.getTransaction().commit();
    }
}
