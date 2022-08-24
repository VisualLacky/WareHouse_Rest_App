package dev.lacky.warehouse.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.pojo.IncomeDocument;
import dev.lacky.warehouse.service.IncomeService;
import dev.lacky.warehouse.util.JsonParser;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/incomes")
public class IncomesServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private IncomeService incomeService;

  @Override
  public void init() throws ServletException {
    incomeService = (IncomeService) getServletContext().getAttribute("incomeService");
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    List<IncomeDocument> incomeDocuments = null;
    try {
      incomeDocuments = incomeService.getAllIncomeDocuments();
    } catch (NoContentException e) {
      resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
      return;
    }

    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    JsonNode node = JsonParser.toJson(incomeDocuments);
    String rs = JsonParser.stringify(node);
    PrintWriter out = resp.getWriter();
    out.write(rs);
    resp.setStatus(HttpServletResponse.SC_OK);
  }
}