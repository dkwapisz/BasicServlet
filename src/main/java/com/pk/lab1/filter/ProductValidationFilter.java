//package com.pk.lab1.filter;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.util.List;
//
//import static com.pk.lab1.utils.Utils.getJsonData;
//import static java.util.Objects.nonNull;
//
//@WebFilter("/product")
//public class ProductValidationFilter extends HttpFilter {
//
//    private static final List<String> METHODS_FOR_VALIDATION = List.of("PUT", "POST");
//
//    @Override
//    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//
//        if (METHODS_FOR_VALIDATION.contains(request.getMethod())) {
//            String jsonData = getJsonData(request);
//
//            if (isValidProductData(jsonData)) {
//                chain.doFilter(request, response);
//            } else {
//                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                response.getWriter().write("Product data is incorrect.");
//            }
//        } else {
//            chain.doFilter(request, response);
//        }
//    }
//
//    private boolean isValidProductData(String jsonData) {
//        try {
//            JSONObject jsonObject = new JSONObject(jsonData);
//
//            String productName = jsonObject.getString("productName");
//            String productPrice = jsonObject.getString("productPrice");
//            String availableQuantity = jsonObject.getString("availableQuantity");
//
//            if (nonNull(productName) && nonNull(productPrice) && nonNull(availableQuantity)
//                    && !productName.isEmpty() && !productPrice.isEmpty() && !availableQuantity.isEmpty()
//                    && Integer.parseInt(productPrice) > 0 && Integer.parseInt(availableQuantity) >= 0) {
//                return true;
//            }
//        } catch (Exception e) {
//            System.err.println("Error occured: " + e.getMessage());
//            return false;
//        }
//
//        return false;
//    }
//}
