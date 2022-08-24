package dev.lacky.warehouse.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import dev.lacky.warehouse.exception.InvalidInputArgumentException;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.pojo.MovementDocument;
import dev.lacky.warehouse.service.MovementService;
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

@WebServlet("/single-movement")
public class SingleMovementServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private MovementService movementService;

  @Override
  public void init() throws ServletException {
    movementService = (MovementService) getServletContext().getAttribute("movementService");
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setCharacterEncoding("UTF-8");
    PrintWriter out = resp.getWriter();
    String filter = req.getParameter("id");
    int id = RequestParameterParser.parseStringId(filter);

    MovementDocument movementDocument = null;
    try {
      movementDocument = movementService.getMovementDocumentById(id);
    } catch (InvalidInputArgumentException e) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      out.write(JsonParser.createJsonMessageString(e.getMessage()));
      return;
    } catch (NoContentException e) {
      resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
      return;
    }

    resp.setContentType("application/json");
    JsonNode node = JsonParser.toJson(movementDocument);
    JsonParser.removeRowsFromChildArray(node, "countableProducts", new String[]{"price"});
    String rs = JsonParser.stringify(node);
    out.write(rs);
    resp.setStatus(HttpServletResponse.SC_OK);
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

    MovementDocument movementDocument = null;
    try {
      movementDocument = JsonParser.fromJson(node, MovementDocument.class);
      movementService.processMovement(movementDocument);
      resp.setStatus(HttpServletResponse.SC_CREATED);
      out.write(JsonParser.createJsonMessageString("Products moved successfully"));
      return;
    } catch (InvalidInputArgumentException e) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      out.write(JsonParser.createJsonMessageString(e.getMessage()));
      return;
    } catch (SQLException e) {
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }
  }
}