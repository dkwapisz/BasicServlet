package com.pk.lab1.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;

import static com.pk.lab1.utils.Utils.getJsonData;
import static java.util.Objects.nonNull;

@WebFilter("/product")
public class ProductValidationFilter extends HttpFilter {

    private static final String POST = "POST";
    private static final String PUT = "PUT";

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);
        String jsonData = getJsonData(mutableRequest);

        if (isValidProductData(jsonData, request.getMethod())) {
            chain.doFilter(mutableRequest, response);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Product data is incorrect.");
        }
    }

    private boolean isValidProductData(String jsonData, String method) {
        if (POST.equals(method)) {
            return isValidProductDataCreate(jsonData);
        } else if (PUT.equals(method)) {
            return isValidProductDataUpdate(jsonData);
        } else {
            return true;
        }
    }

    private boolean isValidProductDataCreate(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            String productName = jsonObject.getString("productName");
            Integer productPrice = jsonObject.has("productPrice") ? Integer.parseInt(jsonObject.getString("productPrice")) : null;
            Integer availableQuantity = jsonObject.has("availableQuantity") ? Integer.parseInt(jsonObject.getString("availableQuantity")) : null;

            if (isValidProductName(productName) && isValidProductPrice(productPrice) &&
                    isValidAvailableQuantity(availableQuantity)) {
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            return false;
        }

        return false;
    }

    private boolean isValidProductDataUpdate(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            String productName = jsonObject.has("productName") ? jsonObject.getString("productName") : null;
            Integer productPrice = jsonObject.has("productPrice") ? Integer.parseInt(jsonObject.getString("productPrice")) : null;
            Integer availableQuantity = jsonObject.has("availableQuantity") ? Integer.parseInt(jsonObject.getString("availableQuantity")) : null;

            if (isValidProductName(productName) || isValidProductPrice(productPrice) ||
                    isValidAvailableQuantity(availableQuantity)) {
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            return false;
        }

        return false;
    }

    private boolean isValidProductName(String productName) {
        return nonNull(productName) && !productName.isEmpty();
    }

    private boolean isValidProductPrice(Integer productPrice) {
        return nonNull(productPrice) && productPrice > 0;
    }

    private boolean isValidAvailableQuantity(Integer availableQuantity) {
        return nonNull(availableQuantity) && availableQuantity >= 0;
    }
}
