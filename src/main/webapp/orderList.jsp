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
<form>
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

    <table class="product-table">
        <tr>
            <td><button type="button" onclick="addOrder()">Add Order</button></td>
        </tr>
        <tr>
            <th>Product Name</th>
            <th>Quantity</th>
            <th>Delete</th>
        </tr>
        <tr>
            <td><input type="text" name="productName0" required></td>
            <td><input type="number" name="productQuantity0" required></td>
            <td></td>
        </tr>
    </table>

    <button type="button" onclick="addProductRow(this)">Add New Product</button>
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
        <td><%= order.getOrderId() %>
        </td>
        <td><%= order.getOrderDate() %>
        </td>
        <td><%= order.getDeliveryDate() %>
        </td>
        <td><%= order.getSupplier() %>
        </td>
        <td><%= order.getCustomerEmail() %>
        </td>
        <td><%= order.getCustomerAddress() %>
        </td>
        <td><%= order.getCustomerPhone() %>
        </td>
        <td><%= order.getAdditionalInformation() %>
        </td>
        <td>
            <table>
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
                    <td><%= product.getProduct().getName() %>
                    </td>
                    <td><%= product.getQuantity() %>
                    </td>
                    <td><%= product.getProduct().getProductPrice() %>
                    </td>
                </tr>
                <%
                    }
                %>
            </table>
        </td>
        <td>
            <button type="button" onclick="modifyOrder(<%= order.getOrderId() %>)">Modify</button>
            <button type="button" onclick="deleteOrder(<%= order.getOrderId() %>)">Delete</button>
        </td>
    </tr>
    <%
            }
        }
    %>
</table>

<script>
    let productCounter = 1;

    function addOrder() {

        const deliveryDate = document.getElementById("deliveryDate").value;
        const supplier = document.getElementById("supplier").value;
        const customerEmail = document.getElementById("customerEmail").value;
        const customerAddress = document.getElementById("customerAddress").value;
        const customerPhone = document.getElementById("customerPhone").value;
        const additionalInformation = document.getElementById("additionalInformation").value;

        const productNames = [];
        const productQuantities = [];

        for (let i = 0; i < productCounter; i++) {
            const productName = document.querySelector('input[name="productName' + i + '"]');
            const productQuantity = document.querySelector('input[name="productQuantity' + i + '"]');

            if (productName && productQuantity) {
                productNames.push(productName.value);
                productQuantities.push(productQuantity.value);
            }
        }

        const orderData = {
            deliveryDate: deliveryDate,
            supplier: supplier,
            customerEmail: customerEmail,
            customerAddress: customerAddress,
            customerPhone: customerPhone,
            additionalInformation: additionalInformation,
            productNames: productNames,
            productQuantities: productQuantities
        };

        if (hasDuplicates(productNames)) {
            alert("Remove duplicated products.");
            clearForm();
            return;
        }

        console.log(productNames)

        fetch("order", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(orderData)
        })
            .then(response => {
                if (response.status === 201) {
                    alert("New order has been added.");
                    productCounter = 1;
                    location.reload();
                } else if (response.status === 406) {
                    alert("Selected products are not available.");
                } else {
                    console.error("Cannot add a new order.");
                }
            })
            .catch(error => {
                console.error("Error occurred when making a POST request: ", error);
            });
        clearForm();
    }

    function modifyOrder(orderId) {

        clearForm();
    }

    function deleteOrder(orderId) {
        const endpointUrl = "order?orderId=" + orderId;

        fetch(endpointUrl, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
            },
        })
            .then(response => {
                if (response.status === 204) {
                    alert("Order " + orderId + " has been removed.")
                    location.reload();
                } else if (response.status === 404) {
                    alert("Not found product to delete.")
                } else {
                    console.error("Cannot remove order.");
                }
            })
            .catch(error => {
                console.error("Error occurred when making a DELETE request: ", error);
            });
    }

    function addProductRow() {
        const productTable = document.querySelector('.product-table');
        const newRow = productTable.insertRow(productTable.rows.length);
        const cell1 = newRow.insertCell(0);
        const cell2 = newRow.insertCell(1);
        const cell3 = newRow.insertCell(2);

        const productNameInput = document.createElement('input');
        productNameInput.type = 'text';
        productNameInput.name = 'productName' + productCounter;
        productNameInput.required = true;
        cell1.appendChild(productNameInput);

        const productQuantityInput = document.createElement('input');
        productQuantityInput.type = 'number';
        productQuantityInput.name = 'productQuantity' + productCounter;
        productQuantityInput.required = true;
        cell2.appendChild(productQuantityInput);

        const removeButton = document.createElement('button');
        removeButton.type = 'button';
        removeButton.textContent = 'Remove Product';
        removeButton.onclick = function() {
            removeProductRow(this);
        };
        cell3.appendChild(removeButton);

        productCounter++;
    }

    function removeProductRow(button) {
        const row = button.parentNode.parentNode;

        const productNameInput = row.querySelector('input[name^="productName"]');
        const productIndex = parseInt(productNameInput.name.match(/\d+/));

        if (productIndex !== 0) {
            row.parentNode.removeChild(row);
            productCounter--;
        }
    }

    function clearForm() {
        document.getElementById("deliveryDate").value = "";
        document.getElementById("supplier").value = "";
        document.getElementById("customerEmail").value = "";
        document.getElementById("customerAddress").value = "";
        document.getElementById("customerPhone").value = "";
        document.getElementById("additionalInformation").value = "";

        const productInputs = document.querySelectorAll('input[name^="productName"]');
        productInputs.forEach(input => input.value = "");

        const quantityInputs = document.querySelectorAll('input[name^="productQuantity"]');
        quantityInputs.forEach(input => input.value = "");
    }

    function hasDuplicates(array) {
        return (new Set(array)).size !== array.length;
    }

</script>

</body>
</html>
