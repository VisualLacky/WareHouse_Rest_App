package dev.lacky.warehouse.servlet;

import com.fasterxml.jackson.databind.JsonNode;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.pojo.InvoiceDocument;
import dev.lacky.warehouse.service.InvoiceService;
import dev.lacky.warehouse.util.JsonParser;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/invoices")
public class InvoicesServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
  private InvoiceService invoiceService;

  @Override
  public void init() throws ServletException {
    invoiceService = (InvoiceService) getServletContext().getAttribute("invoiceService");
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    List<InvoiceDocument> invoiceDocuments = null;
    try {
      invoiceDocuments = invoiceService.getAllInvoiceDocuments();
    } catch (NoContentException e) {
      resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
      return;
    }

    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    JsonNode node = JsonParser.toJson(invoiceDocuments);
    JsonParser.removeRowsFromChildArray(node, "products",
        new String[]{"lastPurchasePrice", "lastSalePrice"});
    String rs = JsonParser.stringify(node);
    PrintWriter out = resp.getWriter();
    out.write(rs);
    resp.setStatus(HttpServletResponse.SC_OK);
  }
}
