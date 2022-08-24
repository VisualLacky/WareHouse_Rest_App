package dev.lacky.warehouse.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import dev.lacky.warehouse.exception.InvalidInputArgumentException;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.model.Store;
import dev.lacky.warehouse.service.StoreService;
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

@WebServlet("/single-store")
public class SingleStoreServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private StoreService storeService;

  @Override
  public void init() throws ServletException {
    storeService = (StoreService) getServletContext().getAttribute("storeService");
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setCharacterEncoding("UTF-8");
    PrintWriter out = resp.getWriter();
    String stringId = req.getParameter("id");
    int id = RequestParameterParser.parseStringId(stringId);

    Store store = null;
    try {
      store = storeService.getStoreById(id);
    } catch (NoContentException e) {
      resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
      return;
    } catch (InvalidInputArgumentException e) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      out.write(JsonParser.createJsonMessageString("Provided data is invalid"));
      return;
    }

    resp.setContentType("application/json");
    JsonNode node = JsonParser.toJson(store);
    String rs = JsonParser.stringify(node);
    out.write(rs);
    resp.setStatus(HttpServletResponse.SC_OK);
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    PrintWriter out = resp.getWriter();
    String stringIdToDelete = req.getParameter("id");
    int id = RequestParameterParser.parseStringId(stringIdToDelete);

    try {
      storeService.deleteStore(id);
      resp.setStatus(HttpServletResponse.SC_OK);
      return;
    } catch (SQLException e) {
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    } catch (NoContentException e) {
      resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
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

    Store store = null;
    try {
      node = JsonParser.parse(requestData);
      store = JsonParser.fromJson(node, Store.class);
      storeService.createStore(store);
      resp.setStatus(HttpServletResponse.SC_CREATED);
      out.write(JsonParser.createJsonMessageString("Store created successfully"));
      return;
    } catch (SQLException e) {
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    } catch (InvalidInputArgumentException e) {
      out.write(JsonParser.createJsonMessageString(e.getMessage()));
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
    Store store = null;
    try {
      node = JsonParser.parse(requestData);
      store = JsonParser.fromJson(node, Store.class);
      storeService.updateStore(store);
      resp.setStatus(HttpServletResponse.SC_OK);
      out.write(JsonParser.createJsonMessageString("Store updated successfully"));
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