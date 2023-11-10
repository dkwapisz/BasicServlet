package com.pk.lab1.servlet;

import com.pk.lab1.databaseUtils.EntityManagerSingleton;
import com.pk.lab1.enums.DeliveryStatus;
import com.pk.lab1.enums.OrderStatus;
import com.pk.lab1.model.Order;
import com.pk.lab1.service.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

import static com.pk.lab1.utils.Utils.getJsonData;

@WebServlet("/order")
public class OrderServlet extends HttpServlet {

    public static final String ORDER_ID = "orderId";
    private OrderService orderService;

    @Override
    public void init() throws ServletException {
        super.init();
        orderService = new OrderService(EntityManagerSingleton.getEntityManager());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        List<Order> orders = orderService.getAllOrders();
        request.setAttribute("orders", orders);
        request.getRequestDispatcher("orderList.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String jsonData = getJsonData(request);

            Order order = orderService.createOrderObject(jsonData);

            OrderStatus orderStatus = orderService.addOrder(order);

            if (orderStatus.equals(OrderStatus.CREATED)) {
                response.setStatus(HttpServletResponse.SC_CREATED);
            } else if (orderStatus.equals(OrderStatus.PRODUCTS_NOT_AVAILABLE)) {
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Cannot add order:  " + e.getMessage());
        }
    }


    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String orderId = request.getParameter(ORDER_ID);
            String jsonData = getJsonData(request);

            Order order = orderService.createUpdateOrder(jsonData, orderId);

            OrderStatus orderStatus = orderService.updateOrder(order, orderId);
            performDeliveryIfDone(order);

            if (orderStatus.equals(OrderStatus.UPDATED)) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else if (orderStatus.equals(OrderStatus.PRODUCTS_NOT_AVAILABLE)) {
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Cannot update order:  " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String orderId = request.getParameter(ORDER_ID);
            OrderStatus orderStatus = orderService.deleteOrder(Long.parseLong(orderId));

            if (orderStatus.equals(OrderStatus.DELETED)) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else if (orderStatus.equals(OrderStatus.NOT_FOUND)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Cannot remove order:  " + e.getMessage());
        }
    }

    private void performDeliveryIfDone(Order order) {
        if (order.getDeliveryStatus().equals(DeliveryStatus.ORDER_SENT)) {
            order.getDeliveryStrategy().deliver(order);
        }
    }
}
