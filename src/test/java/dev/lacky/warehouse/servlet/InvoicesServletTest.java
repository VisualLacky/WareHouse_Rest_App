package dev.lacky.warehouse.servlet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.model.Product;
import dev.lacky.warehouse.pojo.CountableProduct;
import dev.lacky.warehouse.pojo.InvoiceDocument;
import dev.lacky.warehouse.service.InvoiceService;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Arrays;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class InvoicesServletTest {

  @Mock
  private InvoiceService invoiceService;
  @Mock
  private ServletConfig servletConfig;
  @Mock
  private ServletContext servletContext;
  @Mock
  private PrintWriter writer;

  private InvoicesServlet invoicesServlet;
  private InvoiceDocument invoiceDoc;
  private HttpServletRequest request;
  private HttpServletResponse response;

  @BeforeEach
  public void before() throws Exception {
    invoiceService = Mockito.mock(InvoiceService.class);
    request = Mockito.mock(HttpServletRequest.class);
    response = Mockito.mock(HttpServletResponse.class);
    servletConfig = Mockito.mock(ServletConfig.class);
    servletContext = Mockito.mock(ServletContext.class);

    invoiceDoc = new InvoiceDocument();
    invoiceDoc.setId(15);
    invoiceDoc.setTransactionTypeId(1);
    Product product = new Product();
    CountableProduct countableProduct = new CountableProduct();
    countableProduct.setProduct(product);
    countableProduct.setPrice(new BigDecimal(1200));
    countableProduct.setAmount(10);
    invoiceDoc.setCountableProducts(Arrays.asList(countableProduct));

    writer = Mockito.mock(PrintWriter.class);
    when(response.getWriter()).thenReturn(writer);

    when(servletConfig.getServletContext()).thenReturn(servletContext);
    when(servletContext.getAttribute("invoiceService")).thenReturn(invoiceService);
    invoicesServlet = new InvoicesServlet();
    invoicesServlet.init(servletConfig);
  }

  @Test
  public void doGet_ShouldReturn_NoContent() throws Exception {
    when(invoiceService.getAllInvoiceDocuments()).thenThrow(new NoContentException());

    invoicesServlet.doGet(request, response);
    verify(writer, never()).write(anyString());
    verify(response, only()).setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  @Test
  public void doGet_successful() throws Exception {
    when(invoiceService.getAllInvoiceDocuments()).thenReturn(Arrays.asList(invoiceDoc));

    try {
      invoicesServlet.doGet(request, response);
    } catch (Exception e) {
      fail();
    }
    verify(writer, only()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_OK);
  }
}