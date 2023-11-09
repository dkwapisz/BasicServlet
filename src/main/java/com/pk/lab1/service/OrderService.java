package com.pk.lab1.service;

import com.pk.lab1.deliveryStrategy.*;
import com.pk.lab1.enums.DeliveryStatus;
import com.pk.lab1.enums.OrderStatus;
import com.pk.lab1.enums.SupplierType;
import com.pk.lab1.model.Order;
import com.pk.lab1.model.OrderedProduct;
import com.pk.lab1.model.Product;
import com.pk.lab1.repository.OrderRepository;
import com.pk.lab1.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.pk.lab1.utils.Utils.parseDate;
import static java.util.Objects.nonNull;

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
            setDeliveryStrategy(order);
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
            setDeliveryStrategy(order);
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

    public Order createOrderObject(String jsonData) throws ParseException {
        JSONObject jsonObject = new JSONObject(jsonData);

        Date deliveryDate = parseDate(jsonObject.getString("deliveryDate"));
        SupplierType supplier = SupplierType.valueOf(jsonObject.getString("supplier"));
        String customerEmail = jsonObject.getString("customerEmail");
        String customerAddress = jsonObject.getString("customerAddress");
        String customerPhone = jsonObject.getString("customerPhone");
        String additionalInformation = jsonObject.getString("additionalInformation");

        JSONArray productNamesArray = jsonObject.getJSONArray("productNames");
        JSONArray productQuantitiesArray = jsonObject.getJSONArray("productQuantities");

        List<String> productNames = new ArrayList<>();
        List<Integer> productQuantities = new ArrayList<>();

        for (int i = 0; i < productNamesArray.length(); i++) {
            productNames.add(productNamesArray.getString(i));
            productQuantities.add(Integer.parseInt(productQuantitiesArray.getString(i)));
        }

        List<OrderedProduct> orderedProducts = createOrderedProductList(productNames, productQuantities);

        return new Order(deliveryDate, supplier, customerEmail, customerAddress, customerPhone, additionalInformation,
                orderedProducts);
    }

    public Order updateOrder(String jsonData, String orderId) throws ParseException {
        JSONObject jsonObject = new JSONObject(jsonData);

        Order oldOrder = getOrderById(Long.parseLong(orderId));

        Date deliveryDate = jsonObject.has("deliveryDate") ? parseDate(jsonObject.getString("deliveryDate")) : oldOrder.getDeliveryDate();
        SupplierType supplier = jsonObject.has("supplier") ? SupplierType.valueOf(jsonObject.getString("supplier")) : oldOrder.getSupplier();
        String customerEmail = jsonObject.has("customerEmail") ? jsonObject.getString("customerEmail") : oldOrder.getCustomerEmail();
        String customerAddress = jsonObject.has("customerAddress") ? jsonObject.getString("customerAddress") : oldOrder.getCustomerAddress();
        String customerPhone = jsonObject.has("customerPhone") ? jsonObject.getString("customerPhone") : oldOrder.getCustomerPhone();
        String additionalInformation = jsonObject.has("additionalInformation") ? jsonObject.getString("additionalInformation") : oldOrder.getAdditionalInformation();
        DeliveryStatus deliveryStatus = jsonObject.has("deliveryStatus") ? DeliveryStatus.valueOf(jsonObject.getString("deliveryStatus")) : oldOrder.getDeliveryStatus();

        JSONArray productNamesArray = jsonObject.has("productNames") ? jsonObject.getJSONArray("productNames") : null;
        JSONArray productQuantitiesArray = jsonObject.has("productQuantities") ? jsonObject.getJSONArray("productQuantities") : null;

        List<OrderedProduct> orderedProducts;

        if (nonNull(productNamesArray) && nonNull(productQuantitiesArray)) {
            List<String> productNames = new ArrayList<>();
            List<Integer> productQuantities = new ArrayList<>();

            for (int i = 0; i < productNamesArray.length(); i++) {
                productNames.add(productNamesArray.getString(i));
                productQuantities.add(Integer.parseInt(productQuantitiesArray.getString(i)));
            }
            orderedProducts = createOrderedProductList(productNames, productQuantities);
        } else {
            orderedProducts = oldOrder.getOrderedProducts();
        }

        Order newOrder = new Order(deliveryDate, supplier, customerEmail, customerAddress, customerPhone, additionalInformation,
                orderedProducts, deliveryStatus);

        setDeliveryStrategy(newOrder);

        return newOrder;
    }

    private List<OrderedProduct> createOrderedProductList(List<String> productNames, List<Integer> productQuantities) {
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

    private void setDeliveryStrategy(Order order) {
        if (order.getSupplier().equals(SupplierType.DHL)) {
            order.setDeliveryStrategy(new DhlDelivery());
        } else if (order.getSupplier().equals(SupplierType.DPD)) {
            order.setDeliveryStrategy(new DpdDelivery());
        } else if (order.getSupplier().equals(SupplierType.INPOST)) {
            order.setDeliveryStrategy(new InpostDelivery());
        } else if (order.getSupplier().equals(SupplierType.UPS)) {
            order.setDeliveryStrategy(new UpsDelivery());
        } else if (order.getSupplier().equals(SupplierType.POLISH_POST)) {
            order.setDeliveryStrategy(new PolishPostDelivery());
        } else {
            throw new RuntimeException("Wrong delivery type");
        }
    }
}
