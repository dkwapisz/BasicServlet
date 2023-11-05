package com.pk.lab1.servlet;

import com.pk.lab1.model.Product;
import com.pk.lab1.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/product")
public class ProductServlet extends HttpServlet {

    private ProductRepository productRepository;
    EntityManagerFactory entityManagerFactory;
    EntityManager entityManager;

    public ProductServlet() {
        this.entityManagerFactory = Persistence.createEntityManagerFactory("derby-unit");
        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @Override
    public void init() throws ServletException {
        super.init();
        productRepository = new ProductRepository(entityManager);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<Product> products = productRepository.getAllProducts();

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        StringBuilder htmlResponse = new StringBuilder();
        htmlResponse.append("<html><body><h1>Lista produktów:</h1><ul>");

        for (Product product : products) {
            htmlResponse.append("<li>")
                    .append("Nazwa: ").append(product.getName()).append(", ")
                    .append("Cena: ").append(product.getPrice()).append(", ")
                    .append("Ilość: ").append(product.getQuantity())
                    .append("</li>");
        }

        htmlResponse.append("</ul></body></html>");

        response.getWriter().write(htmlResponse.toString());
    }

    @Override
    public void destroy() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
        }
        super.destroy();
    }
}
