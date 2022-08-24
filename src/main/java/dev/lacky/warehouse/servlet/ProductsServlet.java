package dev.lacky.warehouse.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.model.Product;
import dev.lacky.warehouse.service.ProductService;
import dev.lacky.warehouse.util.JsonParser;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/products")
public class ProductsServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private ProductService productService;

  @Override
  public void init() throws ServletException {
    productService = (ProductService) getServletContext().getAttribute("productService");
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String filter = req.getParameter("filter");

    List<Product> products;
    try {
      if (filter == null) {
        products = productService.getAllProducts();
      } else {
        products = productService.getProductsByName(filter);
      }
    } catch (NoContentException e) {
      resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
      return;
    }

    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    JsonNode node = JsonParser.toJson(products);
    JsonParser.removeRowsFromArray(node, new String[]{"amount"});
    String rs = JsonParser.stringify(node);
    PrintWriter out = resp.getWriter();
    out.write(rs);
    resp.setStatus(HttpServletResponse.SC_OK);
  }
}