package dev.lacky.warehouse.service;

import static dev.lacky.warehouse.model.Store.STORE_TITLE_MAX_LENGTH;

import dev.lacky.warehouse.dao.StoreDao;
import dev.lacky.warehouse.exception.InvalidInputArgumentException;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.model.Store;
import dev.lacky.warehouse.pojo.CountableProduct;
import java.sql.SQLException;
import java.util.List;

public class StoreService {

  private final StoreDao storeDao;

  public StoreService() {
    storeDao = new StoreDao();
  }

  public StoreService(StoreDao storeDao) {
    this.storeDao = storeDao;
  }

  public Store getStoreById(int id) throws NoContentException, InvalidInputArgumentException {
    if (id < 0) {
      throw new InvalidInputArgumentException("Invalid store ID");
    }
    Store store = storeDao.getStoreById(id);
    if (store == null) {
      throw new NoContentException();
    }
    return store;
  }

  public void deleteStore(int id)
      throws SQLException, NoContentException, InvalidInputArgumentException {
    if (id < 0) {
      throw new InvalidInputArgumentException("Invalid store ID");
    }
    int result = storeDao.deleteStore(id);
    if (result < 0) {
      throw new SQLException();
    } else if (result == 0) {
      throw new NoContentException();
    }
  }

  public void createStore(Store store) throws SQLException, InvalidInputArgumentException {
    if (isStoreFieldsInvalid(store)) {
      throw new InvalidInputArgumentException("Provided store field is invalid");
    }
    if (isStoreWithTitleAlreadyExists(store.getTitle())) {
      throw new InvalidInputArgumentException("Product with this title already exists");
    }

    int result = storeDao.createStore(store);
    if (result < 0) {
      throw new SQLException();
    }
  }

  public void updateStore(Store store) throws SQLException, InvalidInputArgumentException {
    if (isStoreFieldsInvalid(store)) {
      throw new InvalidInputArgumentException("Provided store field is invalid");
    }
    if (isIdDoesNotExists(store.getId())) {
      throw new InvalidInputArgumentException("Invalid store ID");
    }

    boolean titleAlreadyTaken = isStoreWithTitleAlreadyExists(store.getTitle());
    String oldStoreTitle = storeDao.getStoreTitleById(store.getId());
    if (!store.getTitle().equals(oldStoreTitle) && titleAlreadyTaken) {
      throw new InvalidInputArgumentException("This store title is already taken");
    }

    int result = storeDao.updateStore(store);
    if (result < 0) {
      throw new SQLException();
    }
  }

  public List<Store> getAllStores() throws NoContentException {
    List<Store> stores = storeDao.getAllStores();
    if (stores.size() == 0) {
      throw new NoContentException();
    }
    return stores;
  }

  public List<Store> getStoresByName(String filter) throws NoContentException {
    List<Store> stores = storeDao.getStoresByName(filter);
    if (stores.size() == 0) {
      throw new NoContentException();
    }
    return stores;
  }

  public List<CountableProduct> getCountableProductsRemainingInAllStores()
      throws NoContentException {
    List<CountableProduct> products = storeDao.getProductsRemainingInAllStores();
    if (products.isEmpty()) {
      throw new NoContentException();
    }
    return products;
  }

  public List<CountableProduct> getCountableProductsRemainingInStore(int id)
      throws NoContentException, InvalidInputArgumentException {
    if (id < 0) {
      throw new InvalidInputArgumentException("Invalid store ID");
    }

    List<CountableProduct> products = storeDao.getCountableProductsRemainingInStore(id);
    if (products.isEmpty()) {
      throw new NoContentException();
    }
    return products;
  }

  private boolean isStoreFieldsInvalid(Store store) {
    if (store == null || store.getTitle() == null
        || store.getTitle().length() > STORE_TITLE_MAX_LENGTH) {
      return true;
    } else {
      return false;
    }
  }

  private boolean isStoreWithTitleAlreadyExists(String title) {
    return storeDao.checkStoreWithTitleExists(title);
  }

  private boolean isIdDoesNotExists(int id) {
    return !storeDao.checkStoreWithIdExists(id);
  }
}
