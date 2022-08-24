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
import dev.lacky.warehouse.service.ProductService;
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

class ProductsServletTest {

  @Mock
  private ProductService productService;
  @Mock
  private ServletConfig servletConfig;
  @Mock
  private ServletContext servletContext;
  @Mock
  private PrintWriter writer;

  private ProductsServlet productsServlet;
  private Product product;
  private HttpServletRequest request;
  private HttpServletResponse response;

  @BeforeEach
  public void before() throws Exception {
    productService = Mockito.mock(ProductService.class);
    request = Mockito.mock(HttpServletRequest.class);
    response = Mockito.mock(HttpServletResponse.class);
    servletConfig = Mockito.mock(ServletConfig.class);
    servletContext = Mockito.mock(ServletContext.class);

    product = new Product();
    product.setId(15);
    product.setCode("Test code");
    product.setTitle("Test title");
    product.setLastPurchasePrice(new BigDecimal(1200));
    product.setLastSalePrice(new BigDecimal(1500));

    writer = Mockito.mock(PrintWriter.class);
    when(response.getWriter()).thenReturn(writer);

    when(servletConfig.getServletContext()).thenReturn(servletContext);
    when(servletContext.getAttribute("productService")).thenReturn(productService);
    productsServlet = new ProductsServlet();
    productsServlet.init(servletConfig);
  }

  @Test
  public void doGet_ShouldReturn_NoContent_IfFilterIsNull() throws Exception {
    when(request.getParameter("filter")).thenReturn(null);
    when(productService.getAllProducts()).thenThrow(new NoContentException());

    productsServlet.doGet(request, response);
    verify(writer, never()).write(anyString());
    verify(response, only()).setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  @Test
  public void doGet_successful_IfFilterIsNull() throws Exception {
    when(request.getParameter("filter")).thenReturn(null);
    when(productService.getAllProducts()).thenReturn(Arrays.asList(product));

    try {
      productsServlet.doGet(request, response);
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
    when(productService.getProductsByName(filter)).thenThrow(new NoContentException());

    productsServlet.doGet(request, response);
    verify(writer, never()).write(anyString());
    verify(response, only()).setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  @Test
  public void doGet_successful_IfFilterIsPresent() throws Exception {
    String filter = "some filter";
    when(request.getParameter("filter")).thenReturn(filter);
    when(productService.getProductsByName(filter)).thenReturn(Arrays.asList(product));

    try {
      productsServlet.doGet(request, response);
    } catch (Exception e) {
      fail();
    }
    verify(writer, only()).write(anyString());
    verify(response, atLeast(1)).setStatus(HttpServletResponse.SC_OK);
  }
}