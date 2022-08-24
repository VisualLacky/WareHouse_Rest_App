package dev.lacky.warehouse.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

import dev.lacky.warehouse.dao.StoreDao;
import dev.lacky.warehouse.exception.InvalidInputArgumentException;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.model.Product;
import dev.lacky.warehouse.model.Store;
import dev.lacky.warehouse.pojo.CountableProduct;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

class StoreServiceTest {

  @Mock
  private StoreDao storeDao;

  private StoreService storeService;
  private Store testStore;
  private Product product;
  private CountableProduct countableProduct;
  private List<CountableProduct> countableProducts;

  @BeforeEach
  public void before() throws Exception {
    storeDao = Mockito.mock(StoreDao.class);
    storeService = new StoreService(storeDao);

    testStore = new Store();
    testStore.setId(15);
    testStore.setTitle("test store");
    product = new Product();
    countableProduct = new CountableProduct();
    countableProduct.setProduct(product);
    countableProduct.setAmount(10);
    countableProduct.setPrice(new BigDecimal(1200));
    countableProducts = new ArrayList<>();
    countableProducts.add(countableProduct);

    when(storeDao.checkStoreWithIdExists(testStore.getId())).thenReturn(true);
    when(storeDao.getStoreTitleById(testStore.getId())).thenReturn("some title");
    when(storeDao.checkStoreWithTitleExists(testStore.getTitle())).thenReturn(false);
    when(storeDao.updateStore(testStore)).thenReturn(1);
  }

  @Test
  public void getStoreById_ThrowException_IfIdNegative() {
    assertThrows(InvalidInputArgumentException.class, () ->
        storeService.getStoreById(-15));
  }

  @Test
  public void getStoreById_ThrowException_IfReturnedStoreIsNull() {
    int id = 15;
    when(storeDao.getStoreById(id)).thenReturn(null);
    assertThrows(NoContentException.class, () -> storeService.getStoreById(id));
  }

  @Test
  public void getStoreById_ShouldReturn_Store() {
    int id = testStore.getId();
    Store storeOut = null;
    when(storeDao.getStoreById(id)).thenReturn(testStore);

    try {
      storeOut = storeService.getStoreById(testStore.getId());
    } catch (Exception e) {
      fail();
    }
    assertEquals(storeOut, testStore);
  }

  @Test
  public void deleteStore_ThrowException_IfIdNegative() {
    assertThrows(InvalidInputArgumentException.class, () ->
        storeService.deleteStore(-15));
  }

  @Test
  public void deleteStore_ThrowException_IfDeleteFails() {
    int id = 15;
    when(storeDao.deleteStore(id)).thenReturn(-1);
    assertThrows(SQLException.class, () -> storeService.deleteStore(id));
  }

  @Test
  public void deleteStore_ThrowException_IfDeleteReturnZero() {
    int id = 15;
    when(storeDao.deleteStore(id)).thenReturn(0);
    assertThrows(NoContentException.class, () -> storeService.deleteStore(id));
  }

  @Test
  public void deleteStore_Successful() {
    int id = 15;
    when(storeDao.deleteStore(id)).thenReturn(1);

    try {
      storeService.deleteStore(id);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void createStore_ThrowException_IfStoreIsNull() {
    Exception exception = assertThrows(InvalidInputArgumentException.class,
        () -> storeService.createStore(null));
    assertEquals("Provided store field is invalid", exception.getMessage());
  }

  @Test
  public void createStore_ThrowException_IfStoreTitleIsNull() {
    testStore.setTitle(null);
    assertThrows(InvalidInputArgumentException.class, () -> storeService.createStore(testStore));
  }

  @Test
  public void createStore_ThrowException_IfStoreTitleTooLong() {
    testStore.setTitle("TooLongTitleToProccessItInStoreServiceMethod");
    assertThrows(InvalidInputArgumentException.class, () -> storeService.createStore(testStore));
  }

  @Test
  public void createStore_ThrowException_IfStoreTitleAlreadyExist() {
    when(storeDao.checkStoreWithTitleExists(testStore.getTitle())).thenReturn(true);
    assertThrows(InvalidInputArgumentException.class, () -> storeService.createStore(testStore));
  }

  @Test
  public void createStore_ThrowException_IfCreateStoreFail() {
    when(storeDao.checkStoreWithTitleExists(testStore.getTitle())).thenReturn(false);
    when(storeDao.createStore(testStore)).thenReturn(-1);
    assertThrows(SQLException.class, () -> storeService.createStore(testStore));
  }

  @Test
  public void createStore_SuccessfulCreate() {
    when(storeDao.checkStoreWithTitleExists(testStore.getTitle())).thenReturn(false);
    when(storeDao.createStore(testStore)).thenReturn(1);
    try {
      storeService.createStore(testStore);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void updateStore_ThrowException_IfStoreIsNull() {
    Exception exception = assertThrows(InvalidInputArgumentException.class,
        () -> storeService.updateStore(null));
    assertEquals("Provided store field is invalid", exception.getMessage());
  }

  @Test
  public void updateStore_ThrowException_IfStoreTitleIsNull() {
    testStore.setTitle(null);
    Exception exception = assertThrows(InvalidInputArgumentException.class,
        () -> storeService.updateStore(testStore));
    assertEquals("Provided store field is invalid", exception.getMessage());
  }

  @Test
  public void updateStore_ThrowException_IfStoreTitleTooLong() {
    testStore.setTitle("TooLongTitleToProccessItInStoreServiceMethod");
    assertThrows(InvalidInputArgumentException.class, () -> storeService.updateStore(testStore));
  }

  @Test
  public void updateStore_ThrowException_IfStoreWithIdNotExist() {
    when(storeDao.checkStoreWithIdExists(testStore.getId())).thenReturn(false);
    assertThrows(InvalidInputArgumentException.class, () -> storeService.updateStore(testStore));
  }

  @Test
  public void updateStore_ThrowException_IfStoreTitleConflictsWithOtherStoreTitle() {
    when(storeDao.checkStoreWithTitleExists(testStore.getTitle())).thenReturn(true);
    Exception exception = assertThrows(InvalidInputArgumentException.class,
        () -> storeService.updateStore(testStore));
    assertEquals("This store title is already taken", exception.getMessage());
  }

  @Test
  public void updateStore_ThrowException_IfUpdateStoreFails() {
    when(storeDao.updateStore(testStore)).thenReturn(-1);
    Exception exception = assertThrows(SQLException.class,
        () -> storeService.updateStore(testStore));
  }

  @Test
  public void updateStore_SuccessfullyUpdated() {
    try {
      storeService.updateStore(testStore);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void getAllStores_ThrowException_IfNoStoresReturned() {
    when(storeDao.getAllStores()).thenReturn(Collections.emptyList());
    assertThrows(NoContentException.class, () -> storeService.getAllStores());
  }

  @Test
  public void getAllStores_ShouldReturn_Stores() {
    when(storeDao.getAllStores()).thenReturn(Arrays.asList(testStore));
    List<Store> stores = null;

    try {
      stores = storeService.getAllStores();
    } catch (NoContentException e) {
      fail();
    }
    assertEquals(stores.size(), 1);
    assertTrue(stores.contains(testStore));
  }

  @Test
  public void getStoresByName_ThrowException_IfNoStoresReturned() {
    String filter = "some filter";
    when(storeDao.getStoresByName(filter)).thenReturn(Collections.emptyList());
    assertThrows(NoContentException.class, () -> storeService.getStoresByName(filter));
  }

  @Test
  public void getStoresByName_ShouldReturn_Stores() {
    String filter = "some filter";
    when(storeDao.getStoresByName(filter)).thenReturn(Arrays.asList(testStore));
    List<Store> stores = null;

    try {
      stores = storeService.getStoresByName(filter);
    } catch (NoContentException e) {
      fail();
    }
    assertEquals(stores.size(), 1);
    assertTrue(stores.contains(testStore));
  }

  @Test
  public void getCountableProductsRemainingInAllStores_ThrowException_IfReturnedEmptyList() {
    when(storeDao.getProductsRemainingInAllStores()).thenReturn(Collections.emptyList());
    assertThrows(NoContentException.class, () ->
        storeService.getCountableProductsRemainingInAllStores());
  }

  @Test
  public void getCountableProductsRemainingInAllStores_ShouldReturn_Products() {
    when(storeDao.getProductsRemainingInAllStores()).thenReturn(countableProducts);
    List<CountableProduct> products = null;

    try {
      products = storeService.getCountableProductsRemainingInAllStores();
    } catch (Exception e) {
      fail();
    }
    assertEquals(products.size(), 1);
    assertTrue(products.contains(countableProduct));
  }

  @Test
  public void getCountableProductsRemainingInStore_ThrowException_IfIdNegative() {
    assertThrows(InvalidInputArgumentException.class, () ->
        storeService.getStoreById(-15));
  }

  @Test
  public void getCountableProductsRemainingInStore_ThrowException_IfReturnEmptyList() {
    int id = 15;
    when(storeDao.getCountableProductsRemainingInStore(id)).thenReturn(Collections.emptyList());
    assertThrows(NoContentException.class, () ->
        storeService.getCountableProductsRemainingInStore(id));
  }

  @Test
  public void getCountableProductsRemainingInStore_ShouldReturn_Products() {
    int id = 15;
    when(storeDao.getCountableProductsRemainingInStore(id)).thenReturn(countableProducts);
    List<CountableProduct> products = null;

    try {
      products = storeService.getCountableProductsRemainingInStore(id);
    } catch (Exception e) {
      fail();
    }
    assertEquals(products.size(), 1);
    assertTrue(products.contains(countableProduct));
  }
}