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
import dev.lacky.warehouse.service.ProductService;
import dev.lacky.warehouse.util.JsonParser;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.stream.Stream;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class SingleProductServletTest {

  @Mock
  private ProductService productService;
  @Mock
  private ServletConfig servletConfig;
  @Mock
  private ServletContext servletContext;
  @Mock
  private PrintWriter writer;

  private SingleProductServlet singleProductServlet;
  private Product product;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private String incomeDocAsJsonString;
  private BufferedReader bufferedReader;

  @BeforeEach
  public void before() throws Exception {
    productService = Mockito.mock(ProductService.class);
    request = Mockito.mock(HttpServletRequest.class);
    response = Mockito.mock(HttpServletResponse.class);
    servletConfig = Mockito.mock(ServletConfig.class);
    servletContext = Mockito.mock(ServletContext.class);

    product = new Product();
    product.setId(15);
    product.setCode("test code");
    product.setTitle("test title");
    product.setLastPurchasePrice(new BigDecimal(1200));
    product.setLastSalePrice(new BigDecimal(1500));

    when(servletConfig.getServletContext()).thenReturn(servletContext);
    when(servletContext.getAttribute("productService")).thenReturn(productService);
    singleProductServlet = new SingleProductServlet();
    singleProductServlet.init(servletConfig);

    writer = Mockito.mock(PrintWriter.class);
    when(response.getWriter()).thenReturn(writer);

    bufferedReader = Mockito.mock(BufferedReader.class);
    when(request.getReader()).thenReturn(bufferedReader);
  }

  @Test
  public void doGet_ShouldReturn_BadRequest_IfFilterIsInvalid() throws Exception {
    when(request.getParameter("id")).thenReturn(null);
    when(productService.getProductById(-1))
        .thenThrow(new InvalidInputArgumentException("Invalid input"));

    singleProductServlet.doGet(request, response);
    verify(writer, atLeast(1)).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void doGet_ShouldReturn_NoContent_IfServiceThrowNoContent() throws Exception {
    when(request.getParameter("id")).thenReturn("15");
    when(productService.getProductById(15))
        .thenThrow(new NoContentException());

    singleProductServlet.doGet(request, response);
    verify(writer, never()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  @Test
  public void doGet_successful() throws Exception {
    String filter = "15";
    when(request.getParameter("id")).thenReturn(filter);
    when(productService.getProductById(15)).thenReturn(product);

    try {
      singleProductServlet.doGet(request, response);
    } catch (Exception e) {
      fail();
    }
    verify(writer, only()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_OK);
  }

  @Test
  public void doDelete_ShouldReturn_InternalError_IfServiceFails() throws Exception {
    when(request.getParameter("id")).thenReturn("15");
    doThrow(new SQLException()).when(productService).deleteProduct(15);

    singleProductServlet.doDelete(request, response);
    verify(writer, never()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
  }

  @Test
  public void doDelete_ShouldReturn_NoContent_IfServiceThrowNoContent() throws Exception {
    when(request.getParameter("id")).thenReturn("15");
    doThrow(new NoContentException()).when(productService).deleteProduct(15);

    singleProductServlet.doDelete(request, response);
    verify(writer, never()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  @Test
  public void doDelete_ShouldReturn_BadRequest_IfFilterIsInvalid() throws Exception {
    when(request.getParameter("id")).thenReturn(null);
    doThrow(new InvalidInputArgumentException("Invalid input")).when(productService)
        .deleteProduct(-1);

    singleProductServlet.doDelete(request, response);
    verify(writer, atLeast(1)).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void doDelete_successful() throws Exception {
    String filter = "15";
    when(request.getParameter("id")).thenReturn(filter);
    when(productService.getProductById(15)).thenReturn(product);

    try {
      singleProductServlet.doDelete(request, response);
    } catch (Exception e) {
      fail();
    }
    verify(writer, atLeast(1)).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_OK);
  }

  @Test
  public void doPost_ShouldReturn_BadRequest_IfInputObjectIsInvalid() throws Exception {
    String invalidJson = "invalid";
    JsonNode node = JsonParser.toJson(invalidJson);
    incomeDocAsJsonString = JsonParser.stringify(node);
    when(bufferedReader.lines()).thenReturn(Stream.of(incomeDocAsJsonString));

    singleProductServlet.doPost(request, response);
    verify(writer, atLeast(1)).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void doPost_ShouldReturn_InternalError_IfServiceFails() throws Exception {
    JsonNode node = JsonParser.toJson(product);
    incomeDocAsJsonString = JsonParser.stringify(node);
    when(bufferedReader.lines()).thenReturn(Stream.of(incomeDocAsJsonString));
    doThrow(new SQLException()).when(productService).createProduct(any());

    singleProductServlet.doPost(request, response);
    verify(writer, never()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
  }

  @Test
  public void doPost_successful() throws Exception {
    JsonNode node = JsonParser.toJson(product);
    incomeDocAsJsonString = JsonParser.stringify(node);
    when(bufferedReader.lines()).thenReturn(Stream.of(incomeDocAsJsonString));

    try {
      singleProductServlet.doPost(request, response);
    } catch (Exception e) {
      fail();
    }
    verify(writer, only()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_CREATED);
  }

  @Test
  public void doPut_ShouldReturn_BadRequest_IfInputObjectIsInvalid() throws Exception {
    String invalidJson = "invalid";
    JsonNode node = JsonParser.toJson(invalidJson);
    incomeDocAsJsonString = JsonParser.stringify(node);
    when(bufferedReader.lines()).thenReturn(Stream.of(incomeDocAsJsonString));

    singleProductServlet.doPut(request, response);
    verify(writer, atLeast(1)).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void doPut_ShouldReturn_InternalError_IfServiceFails() throws Exception {
    JsonNode node = JsonParser.toJson(product);
    incomeDocAsJsonString = JsonParser.stringify(node);
    when(bufferedReader.lines()).thenReturn(Stream.of(incomeDocAsJsonString));
    doThrow(new SQLException()).when(productService).updateProduct(any());

    singleProductServlet.doPut(request, response);
    verify(writer, never()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
  }

  @Test
  public void doPut_successful() throws Exception {
    JsonNode node = JsonParser.toJson(product);
    incomeDocAsJsonString = JsonParser.stringify(node);
    when(bufferedReader.lines()).thenReturn(Stream.of(incomeDocAsJsonString));

    try {
      singleProductServlet.doPut(request, response);
    } catch (Exception e) {
      fail();
    }
    verify(writer, only()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_OK);
  }
}