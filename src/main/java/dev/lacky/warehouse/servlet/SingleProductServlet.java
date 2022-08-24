package dev.lacky.warehouse.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import dev.lacky.warehouse.exception.InvalidInputArgumentException;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.model.Product;
import dev.lacky.warehouse.service.ProductService;
import dev.lacky.warehouse.util.JsonParser;
import dev.lacky.warehouse.util.RequestParameterParser;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/single-product")
public class SingleProductServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private ProductService productService;

  @Override
  public void init() throws ServletException {
    productService = (ProductService) getServletContext().getAttribute("productService");
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setCharacterEncoding("UTF-8");
    PrintWriter out = resp.getWriter();
    String filter = req.getParameter("id");
    int id = RequestParameterParser.parseStringId(filter);

    Product product = null;
    try {
      product = productService.getProductById(id);
    } catch (NoContentException e) {
      resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
      return;
    } catch (InvalidInputArgumentException e) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      out.write(JsonParser.createJsonMessageString(e.getMessage()));
      return;
    }

    resp.setContentType("application/json");
    JsonNode node = JsonParser.toJson(product);
    JsonParser.removeRowsFromRoot(node, new String[]{"amount"});
    String rs = JsonParser.stringify(node);
    out.write(rs);
    resp.setStatus(HttpServletResponse.SC_OK);
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setCharacterEncoding("UTF-8");
    PrintWriter out = resp.getWriter();
    String stringIdToDelete = req.getParameter("id");
    int id = RequestParameterParser.parseStringId(stringIdToDelete);

    try {
      productService.deleteProduct(id);
      resp.setStatus(HttpServletResponse.SC_OK);
      out.write(JsonParser.createJsonMessageString("Product deleted successfully"));
    } catch (NoContentException e) {
      resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
    } catch (SQLException e) {
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    } catch (InvalidInputArgumentException e) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      out.write(JsonParser.createJsonMessageString(e.getMessage()));
      return;
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    PrintWriter out = resp.getWriter();
    String requestData = req.getReader().lines().collect(Collectors.joining());

    JsonNode node = null;
    try {
      node = JsonParser.parse(requestData);
    } catch (InvalidInputArgumentException e) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      out.write(JsonParser.createJsonMessageString("Provided data is invalid"));
      return;
    }

    Product product = null;
    try {
      product = JsonParser.fromJson(node, Product.class);
      productService.createProduct(product);
      resp.setStatus(HttpServletResponse.SC_CREATED);
      out.write(JsonParser.createJsonMessageString("Product created successfully"));
      return;
    } catch (InvalidInputArgumentException e) {
      out.write(JsonParser.createJsonMessageString(e.getMessage()));
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    } catch (SQLException e) {
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    req.setCharacterEncoding("UTF-8");
    PrintWriter out = resp.getWriter();
    String requestData = req.getReader().lines().collect(Collectors.joining());

    JsonNode node = null;
    try {
      node = JsonParser.parse(requestData);
    } catch (InvalidInputArgumentException e) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      out.write(JsonParser.createJsonMessageString(e.getMessage()));
      return;
    }

    Product product = null;
    try {
      product = JsonParser.fromJson(node, Product.class);
      productService.updateProduct(product);
      resp.setStatus(HttpServletResponse.SC_OK);
      out.write(JsonParser.createJsonMessageString("Product updated successfully"));
      return;
    } catch (InvalidInputArgumentException e) {
      out.write(JsonParser.createJsonMessageString(e.getMessage()));
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    } catch (SQLException e) {
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }
  }
}