package dev.lacky.warehouse.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import dev.lacky.warehouse.dao.InvoiceDao;
import dev.lacky.warehouse.dao.ProductDao;
import dev.lacky.warehouse.dao.SaleDao;
import dev.lacky.warehouse.dao.StoreDao;
import dev.lacky.warehouse.enums.ProductMovementTypes;
import dev.lacky.warehouse.exception.InvalidInputArgumentException;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.model.Product;
import dev.lacky.warehouse.pojo.CountableProduct;
import dev.lacky.warehouse.pojo.SaleDocument;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class SaleServiceTest {

  @Mock
  private SaleDao saleDao;
  @Mock
  private StoreDao storeDao;
  @Mock
  private ProductDao productDao;
  @Mock
  private InvoiceDao invoiceDao;

  private SaleService saleService;
  private SaleDocument saleDoc;
  private CountableProduct countableProduct;
  private Product product;

  @BeforeEach
  public void before() {
    saleDao = Mockito.mock(SaleDao.class);
    storeDao = Mockito.mock(StoreDao.class);
    productDao = Mockito.mock(ProductDao.class);
    invoiceDao = Mockito.mock(InvoiceDao.class);
    saleService = new SaleService(saleDao, storeDao, productDao, invoiceDao);

    saleDoc = new SaleDocument();
    saleDoc.setId(15);
    saleDoc.setStoreId(10);
    countableProduct = new CountableProduct();
    product = new Product();
    countableProduct.setProduct(product);
    countableProduct.setAmount(10);
    countableProduct.setPrice(new BigDecimal(1200));
    saleDoc.setCountableProducts(Arrays.asList(countableProduct));

    when(storeDao.checkStoreWithIdExists(saleDoc.getStoreId())).thenReturn(true);
    when(productDao.checkSeveralProductsWithIdExists(anyList())).thenReturn(true);
    when(storeDao.checkProductsAmountInStoreIsEnough(anyInt(), anyList())).thenReturn(true);
    when(invoiceDao.createInvoice(any())).thenReturn(1);
    when(invoiceDao.createInvoiceProductsEntries(1, ProductMovementTypes.SALE,
        saleDoc.getCountableProducts())).thenReturn(1);
    when(saleDao.createSale(any())).thenReturn(1);
    when(storeDao.takeProductsFromStore(saleDoc.getStoreId(), saleDoc.getCountableProducts()))
        .thenReturn(1);
    when(productDao.setSalePricesForProducts(anyList()))
        .thenReturn(1);
  }

  @Test
  public void getAllSaleDocuments_ThrowException_IfNoDocsReturned() {
    when(saleDao.getAllSaleDocuments()).thenReturn(Collections.emptyList());
    assertThrows(NoContentException.class, () -> saleService.getAllSaleDocuments());
  }

  @Test
  public void getAllSaleDocuments_ShouldReturn_Documents() {
    when(saleDao.getAllSaleDocuments()).thenReturn(Arrays.asList(saleDoc));
    List<SaleDocument> saleDocuments = null;

    try {
      saleDocuments = saleService.getAllSaleDocuments();
    } catch (NoContentException e) {
      fail();
    }
    assertEquals(saleDocuments.size(), 1);
    assertTrue(saleDocuments.contains(saleDoc));
  }

  @Test
  public void getSaleDocumentById_ThrowException_IfIdNegative() {
    assertThrows(InvalidInputArgumentException.class, () ->
        saleService.getSaleDocumentById(-15));
  }

  @Test
  public void getSaleDocumentById_ThrowException_IfReturnedDocumentIsNull() {
    int id = 15;
    when(saleDao.getSaleDocumentById(id)).thenReturn(null);
    assertThrows(NoContentException.class, () -> saleService.getSaleDocumentById(id));
  }

  @Test
  public void getSaleDocumentById_ShouldReturn_Document() {
    SaleDocument docOut = null;
    int id = 15;
    when(saleDao.getSaleDocumentById(id)).thenReturn(saleDoc);

    try {
      docOut = saleService.getSaleDocumentById(id);
    } catch (Exception e) {
      fail();
    }
    assertEquals(saleDoc, docOut);
  }

  @Test
  public void processSale_ThrowException_IfInputDocumentIsNull() {
    SaleDocument saleDocument = null;

    Exception exception = assertThrows(InvalidInputArgumentException.class, () ->
        saleService.processSale(saleDocument));
    assertEquals("Provided data is invalid", exception.getMessage());
  }

  @Test
  public void processSale_ThrowException_IfStoreNotExist() {
    when(storeDao.checkStoreWithIdExists(saleDoc.getStoreId())).thenReturn(false);

    Exception exception = assertThrows(InvalidInputArgumentException.class, () ->
        saleService.processSale(saleDoc));
    assertEquals("Invalid store ID", exception.getMessage());
  }

  @Test
  public void processSale_ThrowException_IfNotAllProductsExists() {
    when(productDao.checkSeveralProductsWithIdExists(anyList())).thenReturn(false);

    Exception exception = assertThrows(InvalidInputArgumentException.class, () ->
        saleService.processSale(saleDoc));
    assertEquals("Invalid product ID", exception.getMessage());
  }

  @Test
  public void processSale_ThrowException_IfProductAmountNegative() {
    countableProduct.setAmount(-10);

    Exception exception = assertThrows(InvalidInputArgumentException.class, () ->
        saleService.processSale(saleDoc));
    assertEquals("Invalid product amount", exception.getMessage());
  }

  @Test
  public void processSale_ThrowException_IfProductAmountZero() {
    countableProduct.setAmount(0);

    Exception exception = assertThrows(InvalidInputArgumentException.class, () ->
        saleService.processSale(saleDoc));
    assertEquals("Invalid product amount", exception.getMessage());
  }

  @Test
  public void processSale_ThrowException_IfInputProductsHaveNullPrices() {
    countableProduct.setPrice(null);

    Exception exception = assertThrows(InvalidInputArgumentException.class, () ->
        saleService.processSale(saleDoc));
    assertEquals("Invalid product price", exception.getMessage());
  }

  @Test
  public void processSale_ThrowException_IfInputProductsHaveNegativePrices() {
    countableProduct.setPrice(new BigDecimal(-10));

    Exception exception = assertThrows(InvalidInputArgumentException.class, () ->
        saleService.processSale(saleDoc));
    assertEquals("Invalid product price", exception.getMessage());
  }

  @Test
  public void processSale_ThrowException_IfProductsAmountsInStoreIsNotEnough() {
    when(storeDao.checkProductsAmountInStoreIsEnough(anyInt(), anyList())).thenReturn(false);

    Exception exception = assertThrows(InvalidInputArgumentException.class, () ->
        saleService.processSale(saleDoc));
    assertEquals("Not enough product amount in store to take", exception.getMessage());
  }

  @Test
  public void processSale_ThrowException_IfInvoiceCreationFail() {
    when(invoiceDao.createInvoice(any())).thenReturn(-1);
    assertThrows(SQLException.class, () -> saleService.processSale(saleDoc));
  }

  @Test
  public void processSale_ThrowException_IfInvoiceProductsEntriesCreationFail() {
    when(invoiceDao.createInvoiceProductsEntries(1, ProductMovementTypes.SALE,
        saleDoc.getCountableProducts())).thenReturn(-1);
    assertThrows(SQLException.class, () -> saleService.processSale(saleDoc));
  }

  @Test
  public void processSale_ThrowException_IfSaleCreationFail() {
    when(saleDao.createSale(any())).thenReturn(-1);
    assertThrows(SQLException.class, () -> saleService.processSale(saleDoc));
  }

  @Test
  public void processSale_ThrowException_IfTakeProductsFromStoreFail() {
    when(storeDao.takeProductsFromStore(saleDoc.getStoreId(), saleDoc.getCountableProducts()))
        .thenReturn(-1);
    assertThrows(SQLException.class, () -> saleService.processSale(saleDoc));
  }

  @Test
  public void processIncome_ThrowException_IfSetPurchasePricesForProductsFail() {
    when(productDao.setSalePricesForProducts(anyList()))
        .thenReturn(-1);
    assertThrows(SQLException.class, () -> saleService.processSale(saleDoc));
  }

  @Test
  public void processSale_Successful() {
    try {
      saleService.processSale(saleDoc);
    } catch (Exception e) {
      fail();
    }
  }
}