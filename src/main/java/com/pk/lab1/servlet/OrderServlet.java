package com.pk.lab1.servlet;

import com.pk.lab1.databaseUtils.EntityManagerSingleton;
import com.pk.lab1.model.Order;
import com.pk.lab1.repository.OrderRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/order")
public class OrderServlet extends HttpServlet {

    private OrderRepository orderRepository;

    @Override
    public void init() throws ServletException {
        super.init();
        orderRepository = new OrderRepository(EntityManagerSingleton.getEntityManager());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Order> orders = orderRepository.getAllEntities();

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        StringBuilder htmlResponse = new StringBuilder();
        htmlResponse.append("<html><body><h1>Lista produktów:</h1><ul>");

        for (Order order : orders) {
            htmlResponse.append("<li>");
            htmlResponse.append("Data zamówienia: ").append(order.getOrderDate()).append(", ");
            htmlResponse.append("Dostawca: ").append(order.getSupplier()).append(", ");
            htmlResponse.append("Email klienta: ").append(order.getCustomerEmail()).append(", ");
            htmlResponse.append("Adres klienta: ").append(order.getCustomerAddress()).append(", ");
            htmlResponse.append("Telefon klienta: ").append(order.getCustomerPhone()).append(", ");
            htmlResponse.append("Dodatkowe informacje: ").append(order.getAdditionalInformation()).append(", ");
            htmlResponse.append("Produkty: ").append(order.getOrderedProducts()).append(", ");
            htmlResponse.append("</li>");
        }

        htmlResponse.append("</ul></body></html>");

        response.getWriter().write(htmlResponse.toString());
    }

//    @Override
//    public void destroy() {
//        if (entityManagerFactory != null) {
//            entityManagerFactory.close();
//        }
//        super.destroy();
//    }
}
