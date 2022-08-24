package dev.lacky.warehouse.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import dev.lacky.warehouse.dao.ProductDao;
import dev.lacky.warehouse.exception.InvalidInputArgumentException;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.model.Product;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class ProductServiceTest {

  @Mock
  private ProductDao productDao;

  private ProductService productService;
  private Product testProduct;

  @BeforeEach
  public void before() throws Exception {
    productDao = Mockito.mock(ProductDao.class);
    productService = new ProductService(productDao);

    testProduct = new Product();
    testProduct.setId(15);
    testProduct.setCode("test code");
    testProduct.setTitle("test title");
    testProduct.setLastPurchasePrice(new BigDecimal(1200));
    testProduct.setLastSalePrice(new BigDecimal(1500));

    when(productDao.checkProductWithCodeExists(testProduct.getCode())).thenReturn(false);
    when(productDao.checkProductWithIdExists(testProduct.getId())).thenReturn(true);
  }

  @Test
  public void getProductById_ThrowException_IfIdNegative() {
    assertThrows(InvalidInputArgumentException.class, () ->
        productService.getProductById(-15));
  }

  @Test
  public void getProductById_ThrowException_IfReturnedProductIsNull() {
    int id = 15;
    when(productDao.getProductById(id)).thenReturn(null);
    assertThrows(NoContentException.class, () -> productService.getProductById(id));
  }

  @Test
  public void getProductById_ShouldReturn_Product() {
    Product productOut = null;
    int id = 15;
    when(productDao.getProductById(id)).thenReturn(testProduct);

    try {
      productOut = productService.getProductById(id);
    } catch (Exception e) {
      fail();
    }
    assertEquals(productOut, testProduct);
  }

  @Test
  public void deleteProduct_ThrowException_IfIdNegative() {
    assertThrows(InvalidInputArgumentException.class, () ->
        productService.deleteProduct(-15));
  }

  @Test
  public void deleteProduct_ThrowException_IfDeleteFails() {
    int id = 15;
    when(productDao.deleteProduct(id)).thenReturn(-1);
    assertThrows(SQLException.class, () -> productService.deleteProduct(id));
  }

  @Test
  public void deleteProduct_ThrowException_IfDeleteReturnZero() {
    int id = 15;
    when(productDao.deleteProduct(id)).thenReturn(0);
    assertThrows(NoContentException.class, () -> productService.deleteProduct(id));
  }

  @Test
  public void deleteProduct_Successful() {
    int id = 15;
    when(productDao.deleteProduct(id)).thenReturn(1);

    try {
      productService.deleteProduct(id);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void createProduct_ThrowException_IfProductIsNull() {
    Exception exception = assertThrows(InvalidInputArgumentException.class,
        () -> productService.createProduct(null));
    assertEquals("Provided product's field is invalid", exception.getMessage());
  }

  @Test
  public void createProduct_ThrowException_IfProductCodeIsNull() {
    testProduct.setCode(null);
    Exception exception = assertThrows(InvalidInputArgumentException.class,
        () -> productService.createProduct(testProduct));
    assertEquals("Provided product's field is invalid", exception.getMessage());
  }

  @Test
  public void createProduct_ThrowException_IfProductCodeTooLong() {
    testProduct.setCode("TooLongProductCodeToProcessItInProductServiceMethod");
    Exception exception = assertThrows(InvalidInputArgumentException.class,
        () -> productService.createProduct(testProduct));
    assertEquals("Provided product's field is invalid", exception.getMessage());
  }

  @Test
  public void createProduct_ThrowException_IfProductTitleIsNull() {
    testProduct.setTitle(null);
    Exception exception = assertThrows(InvalidInputArgumentException.class,
        () -> productService.createProduct(testProduct));
    assertEquals("Provided product's field is invalid", exception.getMessage());
  }

  @Test
  public void createProduct_ThrowException_IfProductTitleTooLong() {
    testProduct.setTitle("TooLongProductTitleToProcessItInProductServiceMethod");
    Exception exception = assertThrows(InvalidInputArgumentException.class,
        () -> productService.createProduct(testProduct));
    assertEquals("Provided product's field is invalid", exception.getMessage());
  }

  @Test
  public void createProduct_ThrowException_IfLastPurchasePriceIsNull() {
    testProduct.setLastPurchasePrice(null);
    Exception exception = assertThrows(InvalidInputArgumentException.class,
        () -> productService.createProduct(testProduct));
    assertEquals("Provided product's field is invalid", exception.getMessage());
  }

  @Test
  public void createProduct_ThrowException_IfLastSalePriceIsNull() {
    testProduct.setLastSalePrice(null);
    Exception exception = assertThrows(InvalidInputArgumentException.class,
        () -> productService.createProduct(testProduct));
    assertEquals("Provided product's field is invalid", exception.getMessage());
  }

  @Test
  public void createProduct_ThrowException_IfProductCodeAlreadyExist() {
    when(productDao.checkProductWithCodeExists(testProduct.getCode())).thenReturn(true);
    Exception exception = assertThrows(InvalidInputArgumentException.class,
        () -> productService.createProduct(testProduct));
    assertEquals("Product with this code already exists", exception.getMessage());
  }

  @Test
  public void createProduct_ThrowException_IfCreateProductFails() {
    when(productDao.createProduct(testProduct)).thenReturn(-1);
    assertThrows(SQLException.class, () -> productService.createProduct(testProduct));
  }

  @Test
  public void createProduct_Successful() {
    when(productDao.createProduct(testProduct)).thenReturn(1);

    try {
      productService.createProduct(testProduct);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void updateProduct_ThrowException_IfProductIsNull() {
    Exception exception = assertThrows(InvalidInputArgumentException.class,
        () -> productService.updateProduct(null));
    assertEquals("Invalid input data", exception.getMessage());
  }

  @Test
  public void updateProduct_ThrowException_IfProductCodeIsNull() {
    testProduct.setCode(null);
    Exception exception = assertThrows(InvalidInputArgumentException.class,
        () -> productService.updateProduct(testProduct));
    assertEquals("Invalid input data", exception.getMessage());
  }

  @Test
  public void updateProduct_ThrowException_IfProductCodeTooLong() {
    testProduct.setCode("TooLongProductCodeToProcessItInProductServiceMethod");
    Exception exception = assertThrows(InvalidInputArgumentException.class,
        () -> productService.updateProduct(testProduct));
    assertEquals("Provided product's field is invalid", exception.getMessage());
  }

  @Test
  public void updateProduct_ThrowException_IfProductTitleIsNull() {
    testProduct.setTitle(null);
    Exception exception = assertThrows(InvalidInputArgumentException.class,
        () -> productService.updateProduct(testProduct));
    assertEquals("Provided product's field is invalid", exception.getMessage());
  }

  @Test
  public void updateProduct_ThrowException_IfProductTitleTooLong() {
    testProduct.setTitle("TooLongProductTitleToProcessItInProductServiceMethod");
    Exception exception = assertThrows(InvalidInputArgumentException.class,
        () -> productService.updateProduct(testProduct));
    assertEquals("Provided product's field is invalid", exception.getMessage());
  }

  @Test
  public void updateProduct_ThrowException_IfLastPurchasePriceIsNull() {
    testProduct.setLastPurchasePrice(null);
    Exception exception = assertThrows(InvalidInputArgumentException.class,
        () -> productService.updateProduct(testProduct));
    assertEquals("Provided product's field is invalid", exception.getMessage());
  }

  @Test
  public void updateProduct_ThrowException_IfLastSalePriceIsNull() {
    testProduct.setLastSalePrice(null);
    Exception exception = assertThrows(InvalidInputArgumentException.class,
        () -> productService.updateProduct(testProduct));
    assertEquals("Provided product's field is invalid", exception.getMessage());
  }

  @Test
  public void updateProduct_ThrowException_IfProductNotExist() {
    when(productDao.checkProductWithIdExists(testProduct.getId())).thenReturn(false);
    Exception exception = assertThrows(InvalidInputArgumentException.class,
        () -> productService.updateProduct(testProduct));
    assertEquals("Invalid product ID", exception.getMessage());
  }

  @Test
  public void updateProduct_ThrowException_IfNewProductCodeCollidesWithOtherProduct() {
    when(productDao.getProductCodeById(testProduct.getId())).thenReturn("some different code");
    when(productDao.checkProductWithCodeExists(testProduct.getCode())).thenReturn(true);
    Exception exception = assertThrows(InvalidInputArgumentException.class,
        () -> productService.updateProduct(testProduct));
    assertEquals("This code is already taken", exception.getMessage());
  }

  @Test
  public void updateProduct_ThrowException_IfUpdateFails() {
    when(productDao.updateProduct(testProduct)).thenReturn(-1);
    assertThrows(SQLException.class, () -> productService.updateProduct(testProduct));
  }

  @Test
  public void updateProduct_Successful() {
    when(productDao.updateProduct(testProduct)).thenReturn(1);

    try {
      productService.updateProduct(testProduct);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void getAllProducts_ThrowException_IfNoProductsReturned() {
    when(productDao.getAllProducts()).thenReturn(Collections.emptyList());
    assertThrows(NoContentException.class, () -> productService.getAllProducts());
  }

  @Test
  public void getAllProducts_ShouldReturn_Products() {
    when(productDao.getAllProducts()).thenReturn(Arrays.asList(testProduct));
    List<Product> products = null;

    try {
      products = productService.getAllProducts();
    } catch (NoContentException e) {
      fail();
    }
    assertEquals(products.size(), 1);
    assertTrue(products.contains(testProduct));
  }

  @Test
  public void getProductsByName_ThrowException_IfNoProductsReturned() {
    String filter = "some filter";
    when(productDao.getProductsByName(filter)).thenReturn(Collections.emptyList());
    assertThrows(NoContentException.class, () -> productService.getProductsByName(filter));
  }

  @Test
  public void getProductsByName_ShouldReturn_Products() {
    String filter = "some filter";
    when(productDao.getProductsByName(filter)).thenReturn(Arrays.asList(testProduct));
    List<Product> products = null;

    try {
      products = productService.getProductsByName(filter);
    } catch (NoContentException e) {
      fail();
    }
    assertEquals(products.size(), 1);
    assertTrue(products.contains(testProduct));
  }
}