//package com.pk.lab1.filter;
//
//import com.pk.lab1.enums.SupplierType;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.util.Date;
//import java.util.List;
//
//import static com.pk.lab1.utils.Utils.getJsonData;
//import static com.pk.lab1.utils.Utils.parseDate;
//import static java.util.Objects.isNull;
//import static java.util.Objects.nonNull;
//
//@WebFilter("/order")
//public class OrderValidationFilter extends HttpFilter {
//
//    private static final List<String> METHODS_FOR_VALIDATION = List.of("PUT", "POST");
//    private static final String EMAIL_REGEXP = "^[A-Za-z0-9+_.-]+@(.+)$";
//    private static final String PHONE_REGEXP = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$";
//
//    @Override
//    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//
//        if (METHODS_FOR_VALIDATION.contains(request.getMethod())) {
//            String jsonData = getJsonData(request);
//
//            if (isValidOrderData(jsonData)) {
//                chain.doFilter(request, response);
//            } else {
//                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//                response.getWriter().write("Order data is incorrect.");
//            }
//        } else {
//            chain.doFilter(request, response);
//        }
//    }
//
//    private boolean isValidOrderData(String jsonData) {
//        try {
//            JSONObject jsonObject = new JSONObject(jsonData);
//
//            Date deliveryDate = parseDate(jsonObject.getString("deliveryDate"));
//            SupplierType supplier = SupplierType.valueOf(jsonObject.getString("supplier"));
//            String customerEmail = jsonObject.getString("customerEmail");
//            String customerAddress = jsonObject.getString("customerAddress");
//            String customerPhone = jsonObject.getString("customerPhone");
//
//            JSONArray productNamesArray = jsonObject.getJSONArray("productNames");
//            JSONArray productQuantitiesArray = jsonObject.getJSONArray("productQuantities");
//
//            for (int i = 0; i < productNamesArray.length(); i++) {
//                if (isNull(productNamesArray.getString(i)) || productNamesArray.getString(i).isEmpty()
//                        || isNull(productQuantitiesArray.getString(i)) || productQuantitiesArray.getString(i).isEmpty()) {
//                    return false;
//                }
//            }
//
//            if (nonNull(deliveryDate) && nonNull(supplier) && nonNull(customerAddress) && nonNull(customerEmail)
//                    && nonNull(customerPhone) && customerEmail.matches(EMAIL_REGEXP) && customerPhone.matches(PHONE_REGEXP)) {
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
