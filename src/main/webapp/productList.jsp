<%@ page import="com.pk.lab1.model.Product" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Products</title>
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
<h1>Product List</h1>
<table>
    <tr>
        <th>Product ID</th>
        <th>Name</th>
        <th>Price</th>
        <th>Available Quantity</th>
    </tr>
    <%
        List<Product> products = (List<Product>) request.getAttribute("products");
        if (products != null) {
            for (Product product : products) {
    %>
    <tr>
        <td><%= product.getProductId() %>
        </td>
        <td><%= product.getName() %>
        </td>
        <td><%= product.getProductPrice() %>
        </td>
        <td><%= product.getAvailableQuantity() %>
        </td>
    </tr>
    <%
            }
        }
    %>
</table>

<form action="product" method="post">
    <input type="submit" value="Add New Product">
</form>
</body>
</html>
