package dev.lacky.warehouse.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import dev.lacky.warehouse.exception.InvalidInputArgumentException;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.pojo.SaleDocument;
import dev.lacky.warehouse.service.SaleService;
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

@WebServlet("/single-sale")
public class SingleSaleServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private SaleService saleService;

  @Override
  public void init() throws ServletException {
    saleService = (SaleService) getServletContext().getAttribute("saleService");
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setCharacterEncoding("UTF-8");
    PrintWriter out = resp.getWriter();
    String filter = req.getParameter("id");
    int id = RequestParameterParser.parseStringId(filter);

    SaleDocument saleDocument = null;
    try {
      saleDocument = saleService.getSaleDocumentById(id);
    } catch (InvalidInputArgumentException e) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      out.write(JsonParser.createJsonMessageString(e.getMessage()));
      return;
    } catch (NoContentException e) {
      resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
      return;
    }

    resp.setContentType("application/json");
    JsonNode node = JsonParser.toJson(saleDocument);
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

    SaleDocument saleDocument = null;
    try {
      saleDocument = JsonParser.fromJson(node, SaleDocument.class);
      saleService.processSale(saleDocument);
      resp.setStatus(HttpServletResponse.SC_CREATED);
      out.write(JsonParser.createJsonMessageString("Sale completed successfully"));
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