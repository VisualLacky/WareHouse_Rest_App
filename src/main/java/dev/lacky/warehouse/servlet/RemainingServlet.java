package dev.lacky.warehouse.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import dev.lacky.warehouse.exception.InvalidInputArgumentException;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.pojo.CountableProduct;
import dev.lacky.warehouse.service.StoreService;
import dev.lacky.warehouse.util.JsonParser;
import dev.lacky.warehouse.util.RequestParameterParser;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/remaining")
public class RemainingServlet extends HttpServlet {

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
    String filter = req.getParameter("id");
    int id = RequestParameterParser.parseStringId(filter);

    List<CountableProduct> countableProducts;
    try {
      if (filter == null) {
        countableProducts = storeService.getCountableProductsRemainingInAllStores();
      } else {
        countableProducts = storeService.getCountableProductsRemainingInStore(id);
      }
    } catch (InvalidInputArgumentException e) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      out.write(JsonParser.createJsonMessageString(e.getMessage()));
      return;
    } catch (NoContentException e) {
      resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
      return;
    }

    resp.setContentType("application/json");
    JsonNode node = JsonParser.toJson(countableProducts);
    JsonParser.removeRowsFromArray(node, new String[]{"price"});
    String rs = JsonParser.stringify(node);
    out.write(rs);
    resp.setStatus(HttpServletResponse.SC_OK);
  }
}