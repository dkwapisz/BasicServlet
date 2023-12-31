<%@ page import="com.pk.lab1.model.Product" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Products</title>
    <link rel="stylesheet" type="text/css" href="style.css">
</head>
<body>
<h1>Product Management</h1>
<form action="index.jsp" method="get">
    <button type="submit">Return to Home</button>
</form>

<h2>Add New Product</h2>
<form>
    <button type="reset" onclick="clearForm()">Clear Form</button>
    <br><br>

    <label for="productName">Product Name:</label>
    <input type="text" id="productName" name="productName" required><br><br>

    <label for="productPrice">Product Price:</label>
    <input type="number" id="productPrice" name="productPrice" required><br><br>

    <label for="availableQuantity">Available Quantity:</label>
    <input type="number" id="availableQuantity" name="availableQuantity" required><br><br>
</form>

    <table>
        <tr>
            <th>Product ID</th>
            <th>Product Name</th>
            <th>Product Price</th>
            <th>Available Quantity</th>
            <th>Modify Product</th>
            <th>Delete Product</th>
        </tr>
        <%
            List<Product> products = (List<Product>) request.getAttribute("products");
            if (products != null) {
                for (Product product : products) {
        %>
        <tr>
            <td><%= product.getProductId() %></td>
            <td><%= product.getName() %></td>
            <td><%= product.getProductPrice() %></td>
            <td><%= product.getAvailableQuantity() %></td>
            <td><button type="button" onclick="modifyProduct(<%= product.getProductId() %>)">Modify</button></td>
            <td><button type="button" onclick="deleteProduct(<%= product.getProductId() %>)">Delete</button></td>
        </tr>
        <%
                }
            }
        %>
    </table>
    <button type="submit" onclick="addProduct()">Add Product</button>

<script>
    function addProduct() {
        const productName = document.getElementById("productName").value;
        const productPrice = document.getElementById("productPrice").value;
        const availableQuantity = document.getElementById("availableQuantity").value;

        const productData = {
            productName: productName,
            productPrice: productPrice,
            availableQuantity: availableQuantity
        };

        fetch("product", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(productData)
        })
            .then(response => {
                if (response.status === 201) {
                    alert("New product has been added.");
                    location.reload();
                } else if (response.status === 200) {
                    alert("Product with given name already exists. Updated existing product.");
                    location.reload();
                } else if (response.status === 400) {
                    alert("Incorrect data. Please check form.")
                }  else {
                    console.error("Cannot add a new product. Status: " + response.status);
                }
            })
            .catch(error => {
                console.error("Error occurred when making a POST request: ", error);
            });

        clearForm();
    }

    function modifyProduct(productId) {
        const endpointUrl = "product?productId=" + productId;
        const productData = {};

        const addFieldIfNotEmpty = (field, value) => {
            if (value !== "") {
                productData[field] = value;
            }
        };

        addFieldIfNotEmpty("productName", document.getElementById("productName").value);
        addFieldIfNotEmpty("productPrice", document.getElementById("productPrice").value);
        addFieldIfNotEmpty("availableQuantity", document.getElementById("availableQuantity").value);

        fetch(endpointUrl, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(productData)
        })
            .then(response => {
                if (response.status === 201) {
                    alert("Product has been updated.");
                    location.reload();
                } else if (response.status === 200) {
                    alert("Existing product has been updated.");
                    location.reload();
                } else if (response.status === 400) {
                    alert("Incorrect data. Please check form.")
                } else if (response.status === 404) {
                    alert("Product with given ID does not exist.");
                } else {
                    console.error("Cannot update product. Status: " + response.status);
                }
            })
            .catch(error => {
                console.error("Error occurred when making a PUT request: ", error);
            });

        clearForm();
    }

    function deleteProduct(productId) {
        const endpointUrl = "product?productId=" + productId;

        fetch(endpointUrl, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
            },
        })
            .then(response => {
                if (response.status === 204) {
                    alert("Product " + productId + " has been removed.")
                    location.reload();
                } else if (response.status === 404) {
                    alert("Not found product to delete.")
                } else if (response.status === 403) {
                    alert("Product cannot be removed because it exists on the order. " +
                        "Set available quantity to 0 instead.")
                } else {
                    console.error("Cannot remove product. Status: " + response.status);
                }
            })
            .catch(error => {
                console.error("Error occurred when making a DELETE request: ", error);
            });

        clearForm();
    }

    function clearForm() {
        document.getElementById("productName").value = "";
        document.getElementById("productPrice").value = "";
        document.getElementById("availableQuantity").value = "";
    }
</script>
</body>
</html>
