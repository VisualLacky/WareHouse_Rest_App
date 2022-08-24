package dev.lacky.warehouse.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.pojo.MovementDocument;
import dev.lacky.warehouse.service.MovementService;
import dev.lacky.warehouse.util.JsonParser;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/movements")
public class MovementsServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private MovementService movementService;

  @Override
  public void init() throws ServletException {
    movementService = (MovementService) getServletContext().getAttribute("movementService");
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    List<MovementDocument> movementDocuments = null;
    try {
      movementDocuments = movementService.getAllMovementDocuments();
    } catch (NoContentException e) {
      resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
      return;
    }

    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    JsonNode node = JsonParser.toJson(movementDocuments);
    removePriceFromProducts(node);
    String rs = JsonParser.stringify(node);
    PrintWriter out = resp.getWriter();
    out.write(rs);
    resp.setStatus(HttpServletResponse.SC_OK);
  }

  private void removePriceFromProducts(JsonNode originNode) {
    for (JsonNode currentNode : originNode) {
      JsonParser.removeRowsFromChildArray(currentNode, "countableProducts", new String[]{"price"});
    }
  }
}