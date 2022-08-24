package dev.lacky.warehouse.servlet;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import dev.lacky.warehouse.exception.InvalidInputArgumentException;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.model.Product;
import dev.lacky.warehouse.pojo.CountableProduct;
import dev.lacky.warehouse.pojo.SaleDocument;
import dev.lacky.warehouse.service.SaleService;
import dev.lacky.warehouse.util.JsonParser;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Stream;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class SingleSaleServletTest {

  @Mock
  private SaleService saleService;
  @Mock
  private ServletConfig servletConfig;
  @Mock
  private ServletContext servletContext;
  @Mock
  private PrintWriter writer;

  private SingleSaleServlet singleSaleServlet;
  private SaleDocument saleDoc;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private String incomeDocAsJsonString;
  private BufferedReader bufferedReader;

  @BeforeEach
  public void before() throws Exception {
    saleService = Mockito.mock(SaleService.class);
    request = Mockito.mock(HttpServletRequest.class);
    response = Mockito.mock(HttpServletResponse.class);
    servletConfig = Mockito.mock(ServletConfig.class);
    servletContext = Mockito.mock(ServletContext.class);

    saleDoc = new SaleDocument();
    saleDoc.setId(15);
    saleDoc.setStoreId(20);
    saleDoc.setStoreTitle("test title");
    Product product = new Product();
    CountableProduct countableProduct = new CountableProduct();
    countableProduct.setProduct(product);
    countableProduct.setPrice(new BigDecimal(1200));
    countableProduct.setAmount(10);
    saleDoc.setCountableProducts(Arrays.asList(countableProduct));

    when(servletConfig.getServletContext()).thenReturn(servletContext);
    when(servletContext.getAttribute("saleService")).thenReturn(saleService);
    singleSaleServlet = new SingleSaleServlet();
    singleSaleServlet.init(servletConfig);

    writer = Mockito.mock(PrintWriter.class);
    when(response.getWriter()).thenReturn(writer);

    bufferedReader = Mockito.mock(BufferedReader.class);
    when(request.getReader()).thenReturn(bufferedReader);
  }

  @Test
  public void doGet_ShouldReturn_BadRequest_IfFilterIsInvalid() throws Exception {
    when(request.getParameter("id")).thenReturn(null);
    when(saleService.getSaleDocumentById(-1))
        .thenThrow(new InvalidInputArgumentException("Invalid input"));

    singleSaleServlet.doGet(request, response);
    verify(writer, atLeast(1)).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void doGet_ShouldReturn_NoContent_IfServiceThrowNoContent() throws Exception {
    when(request.getParameter("id")).thenReturn("15");
    when(saleService.getSaleDocumentById(15))
        .thenThrow(new NoContentException());

    singleSaleServlet.doGet(request, response);
    verify(writer, never()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  @Test
  public void doGet_successful() throws Exception {
    String filter = "15";
    when(request.getParameter("id")).thenReturn(filter);
    when(saleService.getSaleDocumentById(15)).thenReturn(saleDoc);

    try {
      singleSaleServlet.doGet(request, response);
    } catch (Exception e) {
      fail();
    }
    verify(writer, only()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_OK);
  }

  @Test
  public void doPost_ShouldReturn_BadRequest_IfInputObjectIsInvalid() throws Exception {
    String invalidJson = "invalid";
    JsonNode node = JsonParser.toJson(invalidJson);
    incomeDocAsJsonString = JsonParser.stringify(node);
    when(bufferedReader.lines()).thenReturn(Stream.of(incomeDocAsJsonString));

    singleSaleServlet.doPost(request, response);
    verify(writer, atLeast(1)).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void doPost_ShouldReturn_InternalError_IfServiceFails() throws Exception {
    JsonNode node = JsonParser.toJson(saleDoc);
    incomeDocAsJsonString = JsonParser.stringify(node);
    when(bufferedReader.lines()).thenReturn(Stream.of(incomeDocAsJsonString));
    doThrow(new SQLException()).when(saleService).processSale(any());

    singleSaleServlet.doPost(request, response);
    verify(writer, never()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
  }

  @Test
  public void doPost_successful() throws Exception {
    JsonNode node = JsonParser.toJson(saleDoc);
    incomeDocAsJsonString = JsonParser.stringify(node);
    when(bufferedReader.lines()).thenReturn(Stream.of(incomeDocAsJsonString));

    try {
      singleSaleServlet.doPost(request, response);
    } catch (Exception e) {
      fail();
    }
    verify(writer, only()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_CREATED);
  }
}