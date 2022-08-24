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
import dev.lacky.warehouse.pojo.MovementDocument;
import dev.lacky.warehouse.service.MovementService;
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

class SingleMovementServletTest {

  @Mock
  private MovementService movementService;
  @Mock
  private ServletConfig servletConfig;
  @Mock
  private ServletContext servletContext;
  @Mock
  private PrintWriter writer;

  private SingleMovementServlet singleMovementServlet;
  private MovementDocument moveDoc;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private String incomeDocAsJsonString;
  private BufferedReader bufferedReader;

  @BeforeEach
  public void before() throws Exception {
    movementService = Mockito.mock(MovementService.class);
    request = Mockito.mock(HttpServletRequest.class);
    response = Mockito.mock(HttpServletResponse.class);
    servletConfig = Mockito.mock(ServletConfig.class);
    servletContext = Mockito.mock(ServletContext.class);

    moveDoc = new MovementDocument();
    moveDoc.setId(15);
    moveDoc.setFromStoreId(20);
    moveDoc.setToStoreId(30);
    moveDoc.setFromStoreTitle("test title");
    moveDoc.setToStoreTitle("test title");
    Product product = new Product();
    CountableProduct countableProduct = new CountableProduct();
    countableProduct.setProduct(product);
    countableProduct.setPrice(new BigDecimal(1200));
    countableProduct.setAmount(10);
    moveDoc.setCountableProducts(Arrays.asList(countableProduct));

    when(servletConfig.getServletContext()).thenReturn(servletContext);
    when(servletContext.getAttribute("movementService")).thenReturn(movementService);
    singleMovementServlet = new SingleMovementServlet();
    singleMovementServlet.init(servletConfig);

    writer = Mockito.mock(PrintWriter.class);
    when(response.getWriter()).thenReturn(writer);

    bufferedReader = Mockito.mock(BufferedReader.class);
    when(request.getReader()).thenReturn(bufferedReader);
  }

  @Test
  public void doGet_ShouldReturn_BadRequest_IfFilterIsInvalid() throws Exception {
    when(request.getParameter("id")).thenReturn(null);
    when(movementService.getMovementDocumentById(-1))
        .thenThrow(new InvalidInputArgumentException("Invalid input"));

    singleMovementServlet.doGet(request, response);
    verify(writer, atLeast(1)).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void doGet_ShouldReturn_NoContent_IfServiceThrowNoContent() throws Exception {
    when(request.getParameter("id")).thenReturn("15");
    when(movementService.getMovementDocumentById(15))
        .thenThrow(new NoContentException());

    singleMovementServlet.doGet(request, response);
    verify(writer, never()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  @Test
  public void doGet_successful() throws Exception {
    String filter = "15";
    when(request.getParameter("id")).thenReturn(filter);
    when(movementService.getMovementDocumentById(15)).thenReturn(moveDoc);

    try {
      singleMovementServlet.doGet(request, response);
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

    singleMovementServlet.doPost(request, response);
    verify(writer, atLeast(1)).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void doPost_ShouldReturn_InternalError_IfServiceFails() throws Exception {
    JsonNode node = JsonParser.toJson(moveDoc);
    incomeDocAsJsonString = JsonParser.stringify(node);
    when(bufferedReader.lines()).thenReturn(Stream.of(incomeDocAsJsonString));
    doThrow(new SQLException()).when(movementService).processMovement(any());

    singleMovementServlet.doPost(request, response);
    verify(writer, never()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
  }

  @Test
  public void doPost_successful() throws Exception {
    JsonNode node = JsonParser.toJson(moveDoc);
    incomeDocAsJsonString = JsonParser.stringify(node);
    when(bufferedReader.lines()).thenReturn(Stream.of(incomeDocAsJsonString));

    try {
      singleMovementServlet.doPost(request, response);
    } catch (Exception e) {
      fail();
    }
    verify(writer, only()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_CREATED);
  }
}