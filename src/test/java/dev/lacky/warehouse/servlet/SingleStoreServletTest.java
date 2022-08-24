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
import dev.lacky.warehouse.model.Store;
import dev.lacky.warehouse.service.StoreService;
import dev.lacky.warehouse.util.JsonParser;
import java.io.BufferedReader;
import java.io.PrintWriter;
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

class SingleStoreServletTest {

  @Mock
  private StoreService storeService;
  @Mock
  private ServletConfig servletConfig;
  @Mock
  private ServletContext servletContext;
  @Mock
  private PrintWriter writer;

  private SingleStoreServlet singleStoreServlet;
  private Store store;
  private HttpServletRequest request;
  private HttpServletResponse response;
  private String incomeDocAsJsonString;
  private BufferedReader bufferedReader;

  @BeforeEach
  public void before() throws Exception {
    storeService = Mockito.mock(StoreService.class);
    request = Mockito.mock(HttpServletRequest.class);
    response = Mockito.mock(HttpServletResponse.class);
    servletConfig = Mockito.mock(ServletConfig.class);
    servletContext = Mockito.mock(ServletContext.class);

    store = new Store();
    store.setId(1);
    store.setTitle("test title");

    when(servletConfig.getServletContext()).thenReturn(servletContext);
    when(servletContext.getAttribute("storeService")).thenReturn(storeService);
    singleStoreServlet = new SingleStoreServlet();
    singleStoreServlet.init(servletConfig);

    writer = Mockito.mock(PrintWriter.class);
    when(response.getWriter()).thenReturn(writer);

    bufferedReader = Mockito.mock(BufferedReader.class);
    when(request.getReader()).thenReturn(bufferedReader);
  }

  @Test
  public void doGet_ShouldReturn_BadRequest_IfFilterIsInvalid() throws Exception {
    when(request.getParameter("id")).thenReturn(null);
    when(storeService.getStoreById(-1))
        .thenThrow(new InvalidInputArgumentException("Invalid input"));

    singleStoreServlet.doGet(request, response);
    verify(writer, atLeast(1)).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void doGet_ShouldReturn_NoContent_IfServiceThrowNoContent() throws Exception {
    when(request.getParameter("id")).thenReturn("15");
    when(storeService.getStoreById(15))
        .thenThrow(new NoContentException());

    singleStoreServlet.doGet(request, response);
    verify(writer, never()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  @Test
  public void doGet_successful() throws Exception {
    String filter = "15";
    when(request.getParameter("id")).thenReturn(filter);
    when(storeService.getStoreById(15)).thenReturn(store);

    try {
      singleStoreServlet.doGet(request, response);
    } catch (Exception e) {
      fail();
    }
    verify(writer, only()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_OK);
  }

  @Test
  public void doDelete_ShouldReturn_InternalError_IfServiceFails() throws Exception {
    when(request.getParameter("id")).thenReturn("15");
    doThrow(new SQLException()).when(storeService).deleteStore(15);

    singleStoreServlet.doDelete(request, response);
    verify(writer, never()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
  }

  @Test
  public void doDelete_ShouldReturn_NoContent_IfServiceThrowNoContent() throws Exception {
    when(request.getParameter("id")).thenReturn("15");
    doThrow(new NoContentException()).when(storeService).deleteStore(15);

    singleStoreServlet.doDelete(request, response);
    verify(writer, never()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  @Test
  public void doDelete_ShouldReturn_BadRequest_IfFilterIsInvalid() throws Exception {
    when(request.getParameter("id")).thenReturn(null);
    doThrow(new InvalidInputArgumentException("Invalid input")).when(storeService).deleteStore(-1);

    singleStoreServlet.doDelete(request, response);
    verify(writer, atLeast(1)).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void doDelete_successful() throws Exception {
    String filter = "15";
    when(request.getParameter("id")).thenReturn(filter);
    when(storeService.getStoreById(15)).thenReturn(store);

    try {
      singleStoreServlet.doDelete(request, response);
    } catch (Exception e) {
      fail();
    }
    verify(writer, never()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_OK);
  }

  @Test
  public void doPost_ShouldReturn_BadRequest_IfInputObjectIsInvalid() throws Exception {
    String invalidJson = "invalid";
    JsonNode node = JsonParser.toJson(invalidJson);
    incomeDocAsJsonString = JsonParser.stringify(node);
    when(bufferedReader.lines()).thenReturn(Stream.of(incomeDocAsJsonString));

    singleStoreServlet.doPost(request, response);
    verify(writer, atLeast(1)).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void doPost_ShouldReturn_InternalError_IfServiceFails() throws Exception {
    JsonNode node = JsonParser.toJson(store);
    incomeDocAsJsonString = JsonParser.stringify(node);
    when(bufferedReader.lines()).thenReturn(Stream.of(incomeDocAsJsonString));
    doThrow(new SQLException()).when(storeService).createStore(any());

    singleStoreServlet.doPost(request, response);
    verify(writer, never()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
  }

  @Test
  public void doPost_successful() throws Exception {
    JsonNode node = JsonParser.toJson(store);
    incomeDocAsJsonString = JsonParser.stringify(node);
    when(bufferedReader.lines()).thenReturn(Stream.of(incomeDocAsJsonString));

    try {
      singleStoreServlet.doPost(request, response);
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

    singleStoreServlet.doPut(request, response);
    verify(writer, atLeast(1)).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }

  @Test
  public void doPut_ShouldReturn_InternalError_IfServiceFails() throws Exception {
    JsonNode node = JsonParser.toJson(store);
    incomeDocAsJsonString = JsonParser.stringify(node);
    when(bufferedReader.lines()).thenReturn(Stream.of(incomeDocAsJsonString));
    doThrow(new SQLException()).when(storeService).updateStore(any());

    singleStoreServlet.doPut(request, response);
    verify(writer, never()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
  }

  @Test
  public void doPut_successful() throws Exception {
    JsonNode node = JsonParser.toJson(store);
    incomeDocAsJsonString = JsonParser.stringify(node);
    when(bufferedReader.lines()).thenReturn(Stream.of(incomeDocAsJsonString));

    try {
      singleStoreServlet.doPut(request, response);
    } catch (Exception e) {
      fail();
    }
    verify(writer, only()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_OK);
  }
}