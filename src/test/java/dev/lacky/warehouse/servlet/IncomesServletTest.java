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
import dev.lacky.warehouse.pojo.IncomeDocument;
import dev.lacky.warehouse.service.IncomeService;
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

class IncomesServletTest {

  @Mock
  private IncomeService incomeService;
  @Mock
  private ServletConfig servletConfig;
  @Mock
  private ServletContext servletContext;
  @Mock
  private PrintWriter writer;

  private IncomesServlet incomesServlet;
  private IncomeDocument incomeDoc;
  private HttpServletRequest request;
  private HttpServletResponse response;


  @BeforeEach
  public void before() throws Exception {
    incomeService = Mockito.mock(IncomeService.class);
    request = Mockito.mock(HttpServletRequest.class);
    response = Mockito.mock(HttpServletResponse.class);
    servletConfig = Mockito.mock(ServletConfig.class);
    servletContext = Mockito.mock(ServletContext.class);

    incomeDoc = new IncomeDocument();
    incomeDoc.setId(15);
    incomeDoc.setStoreId(20);
    incomeDoc.setStoreTitle("test title");
    Product product = new Product();
    CountableProduct countableProduct = new CountableProduct();
    countableProduct.setProduct(product);
    countableProduct.setPrice(new BigDecimal(1200));
    countableProduct.setAmount(10);
    incomeDoc.setCountableProducts(Arrays.asList(countableProduct));

    writer = Mockito.mock(PrintWriter.class);
    when(response.getWriter()).thenReturn(writer);

    when(servletConfig.getServletContext()).thenReturn(servletContext);
    when(servletContext.getAttribute("incomeService")).thenReturn(incomeService);
    incomesServlet = new IncomesServlet();
    incomesServlet.init(servletConfig);
  }

  @Test
  public void doGet_ShouldReturn_NoContent() throws Exception {
    when(incomeService.getAllIncomeDocuments()).thenThrow(new NoContentException());

    incomesServlet.doGet(request, response);
    verify(writer, never()).write(anyString());
    verify(response, only()).setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  @Test
  public void doGet_successful() throws Exception {
    when(incomeService.getAllIncomeDocuments()).thenReturn(Arrays.asList(incomeDoc));

    try {
      incomesServlet.doGet(request, response);
    } catch (Exception e) {
      fail();
    }
    verify(writer, only()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_OK);
  }
}