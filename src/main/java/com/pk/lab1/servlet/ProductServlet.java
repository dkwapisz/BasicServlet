package com.pk.lab1.servlet;

import com.pk.lab1.databaseUtils.EntityManagerSingleton;
import com.pk.lab1.enums.ProductStatus;
import com.pk.lab1.model.Product;
import com.pk.lab1.service.ProductService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet("/product")
public class ProductServlet extends HttpServlet {

    private static final String PRODUCT_ID = "productId";

    private ProductService productService;

    @Override
    public void init() throws ServletException {
        super.init();
        productService = new ProductService(EntityManagerSingleton.getEntityManager());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<Product> products = productService.getAllProducts();
        request.setAttribute("products", products);
        request.getRequestDispatcher("productList.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String jsonData = getJsonData(request);

            Product product = createProductObject(jsonData);

            ProductStatus productStatus = productService.addProduct(product);

            if (productStatus.equals(ProductStatus.CREATED)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else if (productStatus.equals(ProductStatus.UPDATED_EXISTING_PRODUCT)) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Cannot add product:  " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String productId = request.getParameter(PRODUCT_ID);
            String jsonData = getJsonData(request);

            Product product = createProductObject(jsonData);

            ProductStatus productStatus = productService.updateProduct(product, productId);

            if (productStatus.equals(ProductStatus.UPDATED)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else if (productStatus.equals(ProductStatus.UPDATED_EXISTING_PRODUCT)) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else if (productStatus.equals(ProductStatus.PRODUCT_NOT_EXIST)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Cannot update product:  " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String productId = request.getParameter(PRODUCT_ID);
            ProductStatus productStatus = productService.deleteProduct(productId);

            if (productStatus.equals(ProductStatus.DELETED)) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else if (productStatus.equals(ProductStatus.PRODUCT_NOT_EXIST)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else if (productStatus.equals(ProductStatus.PRODUCT_EXIST_ON_ORDER)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Cannot remove product:  " + e.getMessage());
        }
    }

    private String getJsonData(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }

        return stringBuilder.toString();
    }

    // TODO need validation.
    private Product createProductObject(String jsonData) {
        JSONObject jsonObject = new JSONObject(jsonData);

        String productName = jsonObject.getString("productName");
        int productPrice = Integer.parseInt(jsonObject.getString("productPrice"));
        int availableQuantity = Integer.parseInt(jsonObject.getString("availableQuantity"));

        return new Product(productName, productPrice, availableQuantity);
    }
}
