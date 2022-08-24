package dev.lacky.warehouse.servlet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.model.Store;
import dev.lacky.warehouse.service.StoreService;
import java.io.PrintWriter;
import java.util.Arrays;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class StoresServletTest {

  @Mock
  private StoreService storeService;
  @Mock
  private ServletConfig servletConfig;
  @Mock
  private ServletContext servletContext;
  @Mock
  private PrintWriter writer;

  private StoresServlet storesServlet;
  private Store store;
  private HttpServletRequest request;
  private HttpServletResponse response;

  @BeforeEach
  public void before() throws Exception {
    storeService = Mockito.mock(StoreService.class);
    request = Mockito.mock(HttpServletRequest.class);
    response = Mockito.mock(HttpServletResponse.class);
    servletConfig = Mockito.mock(ServletConfig.class);
    servletContext = Mockito.mock(ServletContext.class);

    store = new Store();
    store.setId(15);
    store.setTitle("Test title");

    writer = Mockito.mock(PrintWriter.class);
    when(response.getWriter()).thenReturn(writer);

    when(servletConfig.getServletContext()).thenReturn(servletContext);
    when(servletContext.getAttribute("storeService")).thenReturn(storeService);
    storesServlet = new StoresServlet();
    storesServlet.init(servletConfig);
  }

  @Test
  public void doGet_ShouldReturn_NoContent_IfFilterIsNull() throws Exception {
    when(request.getParameter("filter")).thenReturn(null);
    when(storeService.getAllStores()).thenThrow(new NoContentException());

    storesServlet.doGet(request, response);
    verify(writer, never()).write(anyString());
    verify(response, only()).setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  @Test
  public void doGet_successful_IfFilterIsNull() throws Exception {
    when(request.getParameter("filter")).thenReturn(null);
    when(storeService.getAllStores()).thenReturn(Arrays.asList(store));

    try {
      storesServlet.doGet(request, response);
    } catch (Exception e) {
      fail();
    }
    verify(writer, only()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_OK);
  }

  @Test
  public void doGet_ShouldReturn_NoContent_IfFilterIsPresent() throws Exception {
    String filter = "some filter";
    when(request.getParameter("filter")).thenReturn(filter);
    when(storeService.getStoresByName(filter)).thenThrow(new NoContentException());

    storesServlet.doGet(request, response);
    verify(writer, never()).write(anyString());
    verify(response, only()).setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  @Test
  public void doGet_successful_IfFilterIsPresent() throws Exception {
    String filter = "some filter";
    when(request.getParameter("filter")).thenReturn(filter);
    when(storeService.getStoresByName(filter)).thenReturn(Arrays.asList(store));

    try {
      storesServlet.doGet(request, response);
    } catch (Exception e) {
      fail();
    }
    verify(writer, only()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_OK);
  }
}