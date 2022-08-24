package dev.lacky.warehouse.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import dev.lacky.warehouse.dao.InvoiceDao;
import dev.lacky.warehouse.dao.MovementDao;
import dev.lacky.warehouse.dao.ProductDao;
import dev.lacky.warehouse.dao.StoreDao;
import dev.lacky.warehouse.enums.ProductMovementTypes;
import dev.lacky.warehouse.exception.InvalidInputArgumentException;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.model.Product;
import dev.lacky.warehouse.pojo.CountableProduct;
import dev.lacky.warehouse.pojo.MovementDocument;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class MovementServiceTest {

  @Mock
  private MovementDao movementDao;
  @Mock
  private StoreDao storeDao;
  @Mock
  private ProductDao productDao;
  @Mock
  private InvoiceDao invoiceDao;

  private MovementService movementService;
  private MovementDocument moveDoc;
  private CountableProduct countableProduct;
  private Product product;

  @BeforeEach
  public void before() throws Exception {
    movementDao = Mockito.mock(MovementDao.class);
    storeDao = Mockito.mock(StoreDao.class);
    productDao = Mockito.mock(ProductDao.class);
    invoiceDao = Mockito.mock(InvoiceDao.class);
    movementService = new MovementService(movementDao, storeDao, productDao, invoiceDao);

    moveDoc = new MovementDocument();
    moveDoc.setId(15);
    moveDoc.setFromStoreId(10);
    moveDoc.setToStoreId(20);
    countableProduct = new CountableProduct();
    product = new Product();
    countableProduct.setProduct(product);
    countableProduct.setAmount(10);
    countableProduct.setPrice(new BigDecimal(1200));
    moveDoc.setCountableProducts(Arrays.asList(countableProduct));

    List<Integer> stores = Arrays.asList(moveDoc.getFromStoreId(), moveDoc.getToStoreId());
    when(storeDao.checkSeveralStoresWithIdExists(stores)).thenReturn(true);
    when(productDao.checkSeveralProductsWithIdExists(anyList())).thenReturn(true);
    when(storeDao.checkProductsAmountInStoreIsEnough(anyInt(), anyList())).thenReturn(true);
    when(invoiceDao.createInvoice(any())).thenReturn(1);
    when(invoiceDao.createInvoiceProductsEntries(1, ProductMovementTypes.MOVE,
        moveDoc.getCountableProducts())).thenReturn(1);
    when(movementDao.createMovement(any())).thenReturn(1);
    when(storeDao.takeProductsFromStore(moveDoc.getFromStoreId(), moveDoc.getCountableProducts()))
        .thenReturn(1);
    when(storeDao.putProductsIntoStore(moveDoc.getToStoreId(), moveDoc.getCountableProducts()))
        .thenReturn(1);
  }

  @Test
  public void getAllMovementDocuments_ThrowException_IfNoDocsReturned() {
    when(movementDao.getAllMovementDocuments()).thenReturn(Collections.emptyList());
    assertThrows(NoContentException.class, () -> movementService.getAllMovementDocuments());
  }

  @Test
  public void getAllMovementDocuments_ShouldReturn_Documents() {
    when(movementDao.getAllMovementDocuments()).thenReturn(Arrays.asList(moveDoc));
    List<MovementDocument> docOut = null;

    try {
      docOut = movementService.getAllMovementDocuments();
    } catch (NoContentException e) {
      fail();
    }
    assertEquals(docOut.size(), 1);
    assertTrue(docOut.contains(moveDoc));
  }

  @Test
  public void getMovementDocumentById_ThrowException_IfIdNegative() {
    assertThrows(InvalidInputArgumentException.class, () ->
        movementService.getMovementDocumentById(-15));
  }

  @Test
  public void getMovementDocumentById_ThrowException_IfInputDocumentIsNull() {
    int id = 15;
    when(movementDao.getMovementDocumentById(id)).thenReturn(null);
    assertThrows(NoContentException.class, () -> movementService.getMovementDocumentById(id));
  }

  @Test
  public void getMovementDocumentById_ShouldReturn_Document() {
    MovementDocument docOut = null;
    int id = 15;
    when(movementDao.getMovementDocumentById(id)).thenReturn(moveDoc);

    try {
      docOut = movementService.getMovementDocumentById(id);
    } catch (Exception e) {
      fail();
    }
    assertEquals(moveDoc, docOut);
  }

  @Test
  public void processMovement_ThrowException_IfReturnedDocumentIsNull() {
    MovementDocument movementDocument = null;

    Exception exception = assertThrows(InvalidInputArgumentException.class, () ->
        movementService.processMovement(movementDocument));
    assertEquals("Provided data is invalid", exception.getMessage());
  }

  @Test
  public void processMovement_ThrowException_IfStoresNotExist() {
    List<Integer> stores = Arrays.asList(moveDoc.getFromStoreId(), moveDoc.getToStoreId());
    when(storeDao.checkSeveralStoresWithIdExists(stores)).thenReturn(false);

    Exception exception = assertThrows(InvalidInputArgumentException.class, () ->
        movementService.processMovement(moveDoc));
    assertEquals("Invalid store ID", exception.getMessage());
  }

  @Test
  public void processMovement_ThrowException_IfNotAllProductsExists() {
    when(productDao.checkSeveralProductsWithIdExists(anyList())).thenReturn(false);

    Exception exception = assertThrows(InvalidInputArgumentException.class, () ->
        movementService.processMovement(moveDoc));
    assertEquals("Invalid product ID", exception.getMessage());
  }

  @Test
  public void processMovement_ThrowException_IfProductAmountNegative() {
    countableProduct.setAmount(-10);

    Exception exception = assertThrows(InvalidInputArgumentException.class, () ->
        movementService.processMovement(moveDoc));
    assertEquals("Invalid product amount", exception.getMessage());
  }

  @Test
  public void processMovement_ThrowException_IfProductAmountZero() {
    countableProduct.setAmount(0);

    Exception exception = assertThrows(InvalidInputArgumentException.class, () ->
        movementService.processMovement(moveDoc));
    assertEquals("Invalid product amount", exception.getMessage());
  }

  @Test
  public void processMovement_ThrowException_IfProductsAmountsInStoreIsNotEnough() {
    when(storeDao.checkProductsAmountInStoreIsEnough(anyInt(), anyList())).thenReturn(false);

    Exception exception = assertThrows(InvalidInputArgumentException.class, () ->
        movementService.processMovement(moveDoc));
    assertEquals("Not enough product amount in store to take", exception.getMessage());
  }

  @Test
  public void processMovement_ThrowException_IfInvoiceCreationFail() {
    when(invoiceDao.createInvoice(any())).thenReturn(-1);
    assertThrows(SQLException.class, () -> movementService.processMovement(moveDoc));
  }

  @Test
  public void processMovement_ThrowException_IfInvoiceProductsEntriesCreationFail() {
    when(invoiceDao.createInvoiceProductsEntries(1, ProductMovementTypes.MOVE,
        moveDoc.getCountableProducts())).thenReturn(-1);

    assertThrows(SQLException.class, () -> movementService.processMovement(moveDoc));
  }

  @Test
  public void processMovement_ThrowException_IfMovementCreationFail() {
    when(movementDao.createMovement(any())).thenReturn(-1);
    assertThrows(SQLException.class, () -> movementService.processMovement(moveDoc));
  }

  @Test
  public void processMovement_ThrowException_IfTakeProductsFromStoreFail() {
    when(storeDao.takeProductsFromStore(moveDoc.getFromStoreId(), moveDoc.getCountableProducts()))
        .thenReturn(-1);
    assertThrows(SQLException.class, () -> movementService.processMovement(moveDoc));
  }

  @Test
  public void processMovement_ThrowException_IfPutProductsIntoStoreFail() {
    when(storeDao.putProductsIntoStore(moveDoc.getToStoreId(), moveDoc.getCountableProducts()))
        .thenReturn(-1);
    assertThrows(SQLException.class, () -> movementService.processMovement(moveDoc));
  }

  @Test
  public void processMovement_Successful() {
    try {
      movementService.processMovement(moveDoc);
    } catch (Exception e) {
      fail();
    }
  }
}