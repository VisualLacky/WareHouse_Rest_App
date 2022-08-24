package dev.lacky.warehouse.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.model.Store;
import dev.lacky.warehouse.service.StoreService;
import dev.lacky.warehouse.util.JsonParser;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/stores")
public class StoresServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private StoreService storeService;

  @Override
  public void init() throws ServletException {
    storeService = (StoreService) getServletContext().getAttribute("storeService");
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    String filter = req.getParameter("filter");

    List<Store> stores;
    try {
      if (filter == null) {
        stores = storeService.getAllStores();
      } else {
        stores = storeService.getStoresByName(filter);
      }
    } catch (NoContentException e) {
      resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
      return;
    }

    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    JsonNode node = JsonParser.toJson(stores);
    String rs = JsonParser.stringify(node);
    PrintWriter out = resp.getWriter();
    out.write(rs);
    resp.setStatus(HttpServletResponse.SC_OK);
  }
}