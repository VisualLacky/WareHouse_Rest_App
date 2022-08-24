package dev.lacky.warehouse.servlet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.lacky.warehouse.exception.InvalidInputArgumentException;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.model.Product;
import dev.lacky.warehouse.pojo.CountableProduct;
import dev.lacky.warehouse.service.StoreService;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class RemainingServletTest {

  @Mock
  private StoreService storeService;
  @Mock
  private ServletConfig servletConfig;
  @Mock
  private ServletContext servletContext;
  @Mock
  private PrintWriter writer;

  private RemainingServlet remainingServlet;
  private List<CountableProduct> countableProducts;
  private HttpServletRequest request;
  private HttpServletResponse response;

  @BeforeEach
  public void before() throws Exception {
    storeService = Mockito.mock(StoreService.class);
    request = Mockito.mock(HttpServletRequest.class);
    response = Mockito.mock(HttpServletResponse.class);
    servletConfig = Mockito.mock(ServletConfig.class);
    servletContext = Mockito.mock(ServletContext.class);

    Product product = new Product();
    CountableProduct countableProduct = new CountableProduct();
    countableProduct.setProduct(product);
    countableProduct.setPrice(new BigDecimal(1200));
    countableProduct.setAmount(10);
    countableProducts = new ArrayList<>();
    countableProducts.add(countableProduct);

    writer = Mockito.mock(PrintWriter.class);
    when(response.getWriter()).thenReturn(writer);

    when(servletConfig.getServletContext()).thenReturn(servletContext);
    when(servletContext.getAttribute("storeService")).thenReturn(storeService);
    remainingServlet = new RemainingServlet();
    remainingServlet.init(servletConfig);
  }

  @Test
  public void doGet_ShouldReturn_NoContent_IfFilterIsNull() throws Exception {
    when(request.getParameter("id")).thenReturn(null);
    when(storeService.getCountableProductsRemainingInAllStores())
        .thenThrow(new NoContentException());

    remainingServlet.doGet(request, response);
    verify(writer, never()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  @Test
  public void doGet_successful_IfFilterIsNull() throws Exception {
    when(request.getParameter("id")).thenReturn(null);
    when(storeService.getCountableProductsRemainingInAllStores()).thenReturn(countableProducts);

    try {
      remainingServlet.doGet(request, response);
    } catch (Exception e) {
      fail();
    }
    verify(writer, only()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_OK);
  }

  @Test
  public void doGet_successful_IfFilterIsPresent() throws Exception {
    String filter = "15";
    when(request.getParameter("id")).thenReturn(filter);
    when(storeService.getCountableProductsRemainingInAllStores()).thenReturn(countableProducts);

    try {
      remainingServlet.doGet(request, response);
    } catch (Exception e) {
      fail();
    }
    verify(writer, only()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_OK);
  }

  @Test
  public void doGet_ShouldReturn_NoContent_IfFilterPresentAndServiceThrowException()
      throws Exception {
    String filter = "15";
    when(request.getParameter("id")).thenReturn(filter);
    when(storeService.getCountableProductsRemainingInStore(15))
        .thenThrow(new NoContentException());

    remainingServlet.doGet(request, response);
    verify(writer, never()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  @Test
  public void doGet_ShouldReturn_NoContent_IfFilterIsNegative() throws Exception {
    when(request.getParameter("id")).thenReturn("-15");
    when(storeService.getCountableProductsRemainingInStore(-15))
        .thenThrow(new InvalidInputArgumentException("Invalid input"));

    remainingServlet.doGet(request, response);
    verify(writer, atLeast(1)).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_BAD_REQUEST);
  }
}