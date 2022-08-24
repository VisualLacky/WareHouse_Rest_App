package dev.lacky.warehouse.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import dev.lacky.warehouse.dao.IncomeDao;
import dev.lacky.warehouse.dao.InvoiceDao;
import dev.lacky.warehouse.dao.ProductDao;
import dev.lacky.warehouse.dao.StoreDao;
import dev.lacky.warehouse.enums.ProductMovementTypes;
import dev.lacky.warehouse.exception.InvalidInputArgumentException;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.model.Product;
import dev.lacky.warehouse.pojo.CountableProduct;
import dev.lacky.warehouse.pojo.IncomeDocument;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class IncomeServiceTest {

  @Mock
  private IncomeDao incomeDao;
  @Mock
  private StoreDao storeDao;
  @Mock
  private ProductDao productDao;
  @Mock
  private InvoiceDao invoiceDao;

  private IncomeService incomeService;
  private IncomeDocument incomeDoc;
  private CountableProduct countableProduct;
  private Product product;

  @BeforeEach
  public void before() throws Exception {
    incomeDao = Mockito.mock(IncomeDao.class);
    storeDao = Mockito.mock(StoreDao.class);
    productDao = Mockito.mock(ProductDao.class);
    invoiceDao = Mockito.mock(InvoiceDao.class);
    incomeService = new IncomeService(incomeDao, storeDao, productDao, invoiceDao);

    incomeDoc = new IncomeDocument();
    incomeDoc.setId(15);
    incomeDoc.setStoreId(10);
    countableProduct = new CountableProduct();
    product = new Product();
    countableProduct.setProduct(product);
    countableProduct.setAmount(10);
    countableProduct.setPrice(new BigDecimal(1200));
    incomeDoc.setCountableProducts(Arrays.asList(countableProduct));

    when(storeDao.checkStoreWithIdExists(incomeDoc.getStoreId())).thenReturn(true);
    when(productDao.checkSeveralProductsWithIdExists(anyList())).thenReturn(true);
    when(invoiceDao.createInvoice(any())).thenReturn(1);
    when(invoiceDao.createInvoiceProductsEntries(1, ProductMovementTypes.INCOME,
        incomeDoc.getCountableProducts())).thenReturn(1);
    when(incomeDao.createIncome(any())).thenReturn(1);
    when(storeDao.putProductsIntoStore(incomeDoc.getStoreId(), incomeDoc.getCountableProducts()))
        .thenReturn(1);
    when(productDao.setPurchasePricesForProducts(anyList()))
        .thenReturn(1);
  }

  @Test
  public void getAllIncomeDocuments_ThrowException_IfNoDocsReturned() {
    when(incomeDao.getAllIncomeDocuments()).thenReturn(Collections.emptyList());
    assertThrows(NoContentException.class, () -> incomeService.getAllIncomeDocuments());
  }

  @Test
  public void getAllIncomeDocuments_ShouldReturn_Documents() {
    when(incomeDao.getAllIncomeDocuments()).thenReturn(Arrays.asList(incomeDoc));
    List<IncomeDocument> incomeDocuments = null;

    try {
      incomeDocuments = incomeService.getAllIncomeDocuments();
    } catch (NoContentException e) {
      fail();
    }
    assertEquals(incomeDocuments.size(), 1);
    assertTrue(incomeDocuments.contains(incomeDoc));
  }

  @Test
  public void getIncomeDocumentById_ThrowException_IfIdNegative() {
    assertThrows(InvalidInputArgumentException.class, () ->
        incomeService.getIncomeDocumentById(-15));
  }

  @Test
  public void getIncomeDocumentById_ThrowException_IfReturnedDocumentIsNull() {
    int id = 15;
    when(incomeDao.getIncomeDocumentById(id)).thenReturn(null);
    assertThrows(NoContentException.class, () -> incomeService.getIncomeDocumentById(id));
  }

  @Test
  public void getIncomeDocumentById_ShouldReturn_Document() {
    IncomeDocument docOut = null;
    int id = 15;
    when(incomeDao.getIncomeDocumentById(id)).thenReturn(incomeDoc);

    try {
      docOut = incomeService.getIncomeDocumentById(id);
    } catch (Exception e) {
      fail();
    }
    assertEquals(incomeDoc, docOut);
  }

  @Test
  public void processIncome_ThrowException_IfInputDocumentIsNull() {
    IncomeDocument incomeDocument = null;

    Exception exception = assertThrows(InvalidInputArgumentException.class, () ->
        incomeService.processIncome(incomeDocument));
    assertEquals("Provided data is invalid", exception.getMessage());
  }

  @Test
  public void processIncome_ThrowException_IfStoreNotExist() {
    when(storeDao.checkStoreWithIdExists(incomeDoc.getStoreId())).thenReturn(false);

    Exception exception = assertThrows(InvalidInputArgumentException.class, () ->
        incomeService.processIncome(incomeDoc));
    assertEquals("Invalid store ID", exception.getMessage());
  }

  @Test
  public void processIncome_ThrowException_IfNotAllProductsExists() {
    when(productDao.checkSeveralProductsWithIdExists(anyList())).thenReturn(false);

    Exception exception = assertThrows(InvalidInputArgumentException.class, () ->
        incomeService.processIncome(incomeDoc));
    assertEquals("Invalid product ID", exception.getMessage());
  }

  @Test
  public void processIncome_ThrowException_IfProductAmountNegative() {
    countableProduct.setAmount(-10);

    Exception exception = assertThrows(InvalidInputArgumentException.class, () ->
        incomeService.processIncome(incomeDoc));
    assertEquals("Invalid product amount", exception.getMessage());
  }

  @Test
  public void processIncome_ThrowException_IfProductAmountZero() {
    countableProduct.setAmount(0);

    Exception exception = assertThrows(InvalidInputArgumentException.class, () ->
        incomeService.processIncome(incomeDoc));
    assertEquals("Invalid product amount", exception.getMessage());
  }

  @Test
  public void processIncome_ThrowException_IfInputProductsHaveNullPrices() {
    countableProduct.setPrice(null);

    Exception exception = assertThrows(InvalidInputArgumentException.class, () ->
        incomeService.processIncome(incomeDoc));
    assertEquals("Invalid product price", exception.getMessage());
  }

  @Test
  public void processIncome_ThrowException_IfInputProductsHaveNegativePrices() {
    countableProduct.setPrice(new BigDecimal(-10));

    Exception exception = assertThrows(InvalidInputArgumentException.class, () ->
        incomeService.processIncome(incomeDoc));
    assertEquals("Invalid product price", exception.getMessage());
  }

  @Test
  public void processIncome_ThrowException_IfInvoiceCreationFail() {
    when(invoiceDao.createInvoice(any())).thenReturn(-1);
    assertThrows(SQLException.class, () -> incomeService.processIncome(incomeDoc));
  }

  @Test
  public void processIncome_ThrowException_IfInvoiceProductsEntriesCreationFail() {
    when(invoiceDao.createInvoiceProductsEntries(1, ProductMovementTypes.INCOME,
        incomeDoc.getCountableProducts())).thenReturn(-1);
    assertThrows(SQLException.class, () -> incomeService.processIncome(incomeDoc));
  }

  @Test
  public void processIncome_ThrowException_IfIncomeCreationFail() {
    when(incomeDao.createIncome(any())).thenReturn(-1);
    assertThrows(SQLException.class, () -> incomeService.processIncome(incomeDoc));
  }

  @Test
  public void processIncome_ThrowException_IfPutProductsIntoStoreFail() {
    when(storeDao.putProductsIntoStore(incomeDoc.getStoreId(), incomeDoc.getCountableProducts()))
        .thenReturn(-1);
    assertThrows(SQLException.class, () -> incomeService.processIncome(incomeDoc));
  }

  @Test
  public void processIncome_ThrowException_IfSetPurchasePricesForProductsFail() {
    when(productDao.setPurchasePricesForProducts(anyList()))
        .thenReturn(-1);
    assertThrows(SQLException.class, () -> incomeService.processIncome(incomeDoc));
  }

  @Test
  public void processIncome_Successful() {
    try {
      incomeService.processIncome(incomeDoc);
    } catch (Exception e) {
      fail();
    }
  }
}