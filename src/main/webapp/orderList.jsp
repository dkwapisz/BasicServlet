<%@ page import="com.pk.lab1.model.Order" %>
<%@ page import="java.util.List" %>
<%@ page import="com.pk.lab1.model.OrderedProduct" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Orders</title>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            border: 1px solid black;
            padding: 8px;
            text-align: left;
        }
    </style>
</head>
<body>
<h1>Order Management</h1>
<form action="index.jsp" method="get">
    <input type="submit" value="Return to Home">
</form>

<h2>Add New Order</h2>
<form action="addOrder" method="post">
    <input type="reset" value="Clear Form">
    <br><br>

    <label for="deliveryDate">Delivery Date:</label>
    <input type="date" id="deliveryDate" name="deliveryDate" required><br><br>

    <label for="supplier">Supplier:</label>
    <input type="text" id="supplier" name="supplier" required><br><br>

    <label for="customerEmail">Customer Email:</label>
    <input type="email" id="customerEmail" name="customerEmail" required><br><br>

    <label for="customerAddress">Customer Address:</label>
    <input type="text" id="customerAddress" name="customerAddress" required><br><br>

    <label for="customerPhone">Customer Phone:</label>
    <input type="tel" id="customerPhone" name="customerPhone" required><br><br>

    <label for="additionalInformation">Additional Information:</label>
    <textarea id="additionalInformation" name="additionalInformation" rows="4"></textarea><br><br>

    <table>
        <tr>
            <th>Product Name</th>
            <th>Quantity</th>
        </tr>
        <tr>
            <td><input type="text" name="productName" required></td>
            <td><input type="number" name="productQuantity" required></td>
        </tr>
    </table>

    <input type="submit" value="Add Order">
</form>

<h2>Order List</h2>
<table>
    <tr>
        <th>Order ID</th>
        <th>Order Date</th>
        <th>Delivery Date</th>
        <th>Supplier</th>
        <th>Customer Email</th>
        <th>Customer Address</th>
        <th>Customer Phone</th>
        <th>Additional Information</th>
        <th>Ordered Products</th>
        <th>Action</th>
    </tr>
    <%
        List<Order> orders = (List<Order>) request.getAttribute("orders");
        if (orders != null) {
            for (Order order : orders) {
    %>
    <tr>
        <td><%= order.getOrderId() %></td>
        <td><%= order.getOrderDate() %></td>
        <td><%= order.getDeliveryDate() %></td>
        <td><%= order.getSupplier() %></td>
        <td><%= order.getCustomerEmail() %></td>
        <td><%= order.getCustomerAddress() %></td>
        <td><%= order.getCustomerPhone() %></td>
        <td><%= order.getAdditionalInformation() %></td>
        <td>
            <table class="product-table">
                <tr>
                    <th>Product Name</th>
                    <th>Quantity</th>
                    <th>Price</th>
                </tr>
                <%
                    List<OrderedProduct> orderedProducts = order.getOrderedProducts();
                    for (OrderedProduct product : orderedProducts) {
                %>
                <tr>
                    <td><%= product.getProduct().getName() %></td>
                    <td><%= product.getQuantity() %></td>
                    <td><%= product.getProduct().getProductPrice() %></td>
                </tr>
                <%
                    }
                %>
            </table>
        </td>
        <td>
            <form action="modifyOrder" method="post">
                <input type="hidden" name="orderId" value="<%= order.getOrderId() %>">
                <input type="submit" value="Modify">
            </form>
            <form action="deleteOrder" method="post">
                <input type="hidden" name="orderId" value="<%= order.getOrderId() %>">
                <input type="submit" value="Delete">
            </form>
        </td>
    </tr>
    <%
            }
        }
    %>
</table>
</body>
</html>
