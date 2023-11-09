package com.pk.lab1.filter;

import com.pk.lab1.enums.DeliveryStatus;
import com.pk.lab1.enums.SupplierType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import static com.pk.lab1.utils.Utils.getJsonData;
import static com.pk.lab1.utils.Utils.parseDate;
import static java.util.Objects.nonNull;

@WebFilter("/order")
public class OrderValidationFilter extends HttpFilter {

    private static final String POST = "POST";
    private static final String PUT = "PUT";

    private static final String EMAIL_REGEXP = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_REGEXP = "^[\\+]?[(]?[0-9]{3}[)]?[-\\s\\.]?[0-9]{3}[-\\s\\.]?[0-9]{4,6}$";

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(request);
        String jsonData = getJsonData(mutableRequest);

        if (isValidOrderData(jsonData, request.getMethod())) {
            chain.doFilter(mutableRequest, response);
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Order data is incorrect.");
        }
    }

    private boolean isValidOrderData(String jsonData, String method) {
        if (POST.equals(method)) {
            return isValidOrderDataCreate(jsonData);
        } else if (PUT.equals(method)) {
            return isValidOrderDataUpdate(jsonData);
        } else {
            return true;
        }
    }

    private boolean isValidOrderDataCreate(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            Date deliveryDate = parseDate(jsonObject.getString("deliveryDate"));
            SupplierType supplier = SupplierType.valueOf(jsonObject.getString("supplier"));
            String customerEmail = jsonObject.getString("customerEmail");
            String customerAddress = jsonObject.getString("customerAddress");
            String customerPhone = jsonObject.getString("customerPhone");

            JSONArray productNamesArray = jsonObject.getJSONArray("productNames");
            JSONArray productQuantitiesArray = jsonObject.getJSONArray("productQuantities");

            List<String> productNames = new ArrayList<>();
            List<Integer> productQuantities = new ArrayList<>();

            if (nonNull(productNamesArray) && nonNull(productQuantitiesArray)) {
                IntStream.range(0, productNamesArray.length())
                        .forEach(i -> {
                            productNames.add(productNamesArray.getString(i));
                            productQuantities.add(Integer.parseInt(productQuantitiesArray.getString(i)));
                        });
            }

            if (isValidDeliveryDate(deliveryDate) && isValidSupplier(supplier) && isValidCustomerEmail(customerEmail) &&
                    isValidCustomerAddress(customerAddress) && isValidCustomerPhone(customerPhone) &&
                    isValidOrderedProducts(productNames, productQuantities)) {
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error occured: " + e.getMessage());
            return false;
        }

        return false;
    }

    private boolean isValidOrderDataUpdate(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            Date deliveryDate = jsonObject.has("deliveryDate") ? parseDate(jsonObject.getString("deliveryDate")) : null;
            SupplierType supplier = jsonObject.has("supplier") ? SupplierType.valueOf(jsonObject.getString("supplier")) : null;
            String customerEmail = jsonObject.has("customerEmail") ? jsonObject.getString("customerEmail") : null;
            String customerAddress = jsonObject.has("customerAddress") ? jsonObject.getString("customerAddress") : null;
            String customerPhone = jsonObject.has("customerPhone") ? jsonObject.getString("customerPhone") : null;
            DeliveryStatus deliveryStatus = jsonObject.has("deliveryStatus") ? DeliveryStatus.valueOf(jsonObject.getString("deliveryStatus")) : null;

            JSONArray productNamesArray = jsonObject.has("productNames") ? jsonObject.getJSONArray("productNames") : null;
            JSONArray productQuantitiesArray = jsonObject.has("productQuantities") ? jsonObject.getJSONArray("productQuantities") : null;

            List<String> productNames = new ArrayList<>();
            List<Integer> productQuantities = new ArrayList<>();

            if (nonNull(productNamesArray) && nonNull(productQuantitiesArray)) {
                IntStream.range(0, productNamesArray.length())
                        .forEach(i -> {
                            productNames.add(productNamesArray.getString(i));
                            productQuantities.add(Integer.parseInt(productQuantitiesArray.getString(i)));
                        });
            }

            if (isValidDeliveryDate(deliveryDate) || isValidSupplier(supplier) || isValidCustomerEmail(customerEmail) ||
                    isValidCustomerAddress(customerAddress) || isValidCustomerPhone(customerPhone) ||
                    isValidDeliveryStatus(deliveryStatus) || isValidOrderedProducts(productNames, productQuantities)) {
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error occured: " + e.getMessage());
            return false;
        }

        return false;
    }

    private boolean isValidDeliveryDate(Date deliveryDate) {
        return nonNull(deliveryDate) && deliveryDate.after(new Date());
    }

    private boolean isValidSupplier(SupplierType supplier) {
        return nonNull(supplier);
    }

    private boolean isValidCustomerEmail(String customerEmail) {
        return nonNull(customerEmail) && customerEmail.matches(EMAIL_REGEXP);
    }

    private boolean isValidCustomerAddress(String customerAddress) {
        return nonNull(customerAddress) && !customerAddress.isEmpty();
    }

    private boolean isValidCustomerPhone(String customerPhone) {
        return nonNull(customerPhone) && customerPhone.matches(PHONE_REGEXP);
    }

    private boolean isValidDeliveryStatus(DeliveryStatus deliveryStatus) {
        return nonNull(deliveryStatus);
    }

    private boolean isValidOrderedProducts(List<String> productNames, List<Integer> productQuantities) {
        return nonNull(productNames) && nonNull(productQuantities) &&
                productNames.size() == productQuantities.size() &&
                IntStream.range(0, productNames.size())
                        .noneMatch(i -> productNames.get(i).isEmpty() || productQuantities.get(i) <= 0);
    }
}
