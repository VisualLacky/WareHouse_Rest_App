package dev.lacky.warehouse.dao;

import dev.lacky.warehouse.connection.PoolConnectionBuilder;
import dev.lacky.warehouse.model.Product;
import dev.lacky.warehouse.model.Store;
import dev.lacky.warehouse.pojo.CountableProduct;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StoreDao extends BaseDao {

  public static final String GET_ALL_STORES = "SELECT * FROM stores";
  public static final String GET_STORES_WITH_FILTER = "SELECT * FROM stores WHERE UPPER(title) LIKE UPPER(?)";
  //public static final String ALL_REMAININGS = "SELECT p.id, p.code, p.title, p.last_purchase_price as last_purchase_price, p.last_sale_price as last_sale_price, sp.amount FROM products as p JOIN stores_products as sp ON p.id=sp.product_id";
  public static final String ALL_REMAININGS = "SELECT p.id, p.code, p.title, p.last_purchase_price as last_purchase_price, p.last_sale_price as last_sale_price, SUM(sp.amount) as amount FROM products as p JOIN stores_products as sp ON p.id=sp.product_id GROUP BY p.id";
  public static final String REMAININGS_WITH_FILTER = "SELECT p.id, p.code, p.title, p.last_purchase_price as last_purchase_price, p.last_sale_price as last_sale_price, sp.amount FROM products as p JOIN stores_products as sp ON p.id=sp.product_id WHERE sp.store_id=?";
  public static final String GET_STORE_BY_ID = "SELECT * FROM stores WHERE id=?";
  public static final String DELETE_STORE = "DELETE FROM stores WHERE id=?";
  public static final String CREATE_STORE = "INSERT INTO stores(title) values(?)";
  public static final String UPDATE_STORE = "UPDATE stores SET title=? WHERE id=?";
  public static final String CHECK_ID_EXISTS = "SELECT EXISTS(SELECT id FROM stores WHERE id=?)";
  public static final String CREATE_STORES_PRODUCTS_ENTRY = "INSERT INTO stores_products(store_id, product_id, amount) values(?,?,?)";
  public static final String UPDATE_STORES_PRODUCTS_ENTRY = "UPDATE stores_products SET amount=? WHERE store_id=? AND product_id=?";
  public static final String DELETE_STORES_PRODUCTS_ENTRY = "DELETE FROM stores_products WHERE store_id=? AND product_id=?";
  public static final String GET_PRODUCTS_AMOUNT_FROM_STORE = "SELECT * FROM stores_products WHERE store_id=?";
  public static final String CHECK_TITLE_ALREADY_TAKEN = "SELECT EXISTS(SELECT id FROM stores WHERE title=?)";

  public StoreDao() {
    setConnectionBuilder(new PoolConnectionBuilder());
  }

  public List<Store> getAllStores() {
    List<Store> stores = new ArrayList<>();
    try (Connection con = getConnection()) {
      Statement statement = con.createStatement();
      ResultSet resultSet = statement.executeQuery(GET_ALL_STORES);

      while (resultSet.next()) {
        Store store = new Store();
        store.setId(resultSet.getInt("id"));
        store.setTitle(resultSet.getString("title"));
        stores.add(store);
      }
    } catch (SQLException e) {
      logger.error("Can't retrieve all stores; cause: {}", e.getMessage());
    }
    return stores;
  }

  public List<Store> getStoresByName(String title) {
    List<Store> stores = new ArrayList<>();
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(GET_STORES_WITH_FILTER);
      prepStatement.setString(1, "%" + title + "%");
      ResultSet resultSet = prepStatement.executeQuery();

      while (resultSet.next()) {
        Store store = new Store();
        store.setId(resultSet.getInt("id"));
        store.setTitle(resultSet.getString("title"));
        stores.add(store);
      }
    } catch (SQLException e) {
      logger.error("Can't retrieve stores with filter '{}'; cause: {}", title, e.getMessage());
    }
    return stores;
  }

  public Store getStoreById(int id) {
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(GET_STORE_BY_ID);
      prepStatement.setInt(1, id);
      ResultSet resultSet = prepStatement.executeQuery();

      while (resultSet.next()) {
        Store store = new Store();
        store.setId(resultSet.getInt("id"));
        store.setTitle(resultSet.getString("title"));
        return store;
      }
    } catch (SQLException e) {
      logger.error("Can't retrieve store by id '{}'; cause: {}", id, e.getMessage());
    }
    return null;
  }

  public String getStoreTitleById(int id) {
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(GET_STORE_BY_ID);
      prepStatement.setInt(1, id);
      ResultSet resultSet = prepStatement.executeQuery();

      while (resultSet.next()) {
        return resultSet.getString("title");
      }
    } catch (SQLException e) {
      logger.error("Can't retrieve store title by id '{}'; cause: {}", id, e.getMessage());
    }
    return null;
  }

  public List<CountableProduct> getProductsRemainingInAllStores() {
    List<CountableProduct> countableProducts = new ArrayList<>();
    try (Connection con = getConnection()) {
      Statement statement = con.createStatement();
      ResultSet resultSet = statement.executeQuery(ALL_REMAININGS);

      while (resultSet.next()) {
        Product product = new Product();
        product.setId(resultSet.getInt("id"));
        product.setCode(resultSet.getString("code"));
        product.setTitle(resultSet.getString("title"));
        product.setLastPurchasePrice(resultSet.getBigDecimal("last_purchase_price"));
        product.setLastSalePrice(resultSet.getBigDecimal("last_sale_price"));
        CountableProduct countableProduct = new CountableProduct(product);
        countableProduct.setAmount(resultSet.getInt("amount"));
        countableProducts.add(countableProduct);
      }
    } catch (SQLException e) {
      logger.error("Can't retrieve all products amounts; cause: {}", e.getMessage());
    }
    return countableProducts;
  }

  public List<CountableProduct> getCountableProductsRemainingInStore(int id) {
    List<CountableProduct> countableProducts = new ArrayList<>();
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(REMAININGS_WITH_FILTER);
      prepStatement.setInt(1, id);
      ResultSet resultSet = prepStatement.executeQuery();

      while (resultSet.next()) {
        Product product = new Product();
        product.setId(resultSet.getInt("id"));
        product.setCode(resultSet.getString("code"));
        product.setTitle(resultSet.getString("title"));
        product.setLastPurchasePrice(resultSet.getBigDecimal("last_purchase_price"));
        product.setLastSalePrice(resultSet.getBigDecimal("last_sale_price"));
        CountableProduct countableProduct = new CountableProduct(product);
        countableProduct.setAmount(resultSet.getInt("amount"));
        countableProducts.add(countableProduct);
      }
    } catch (SQLException e) {
      logger.error("Can't retrieve products amounds with filter '{}'; cause: {}", id,
          e.getMessage());
    }
    return countableProducts;
  }

  public int deleteStore(int id) {
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(DELETE_STORE);
      prepStatement.setInt(1, id);
      return prepStatement.executeUpdate();
    } catch (SQLException e) {
      logger.error("Can't delete store by id '{}'; cause: {}", id, e.getMessage());
    }
    return -1;
  }

  public int createStore(Store store) {
    if (store == null || store.getTitle() == null) {
      return -1;
    }
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement =
          con.prepareStatement(CREATE_STORE, Statement.RETURN_GENERATED_KEYS);
      prepStatement.setString(1, store.getTitle());
      prepStatement.executeUpdate();
      ResultSet keys = prepStatement.getGeneratedKeys();

      if (keys.next()) {
        return keys.getInt(1);
      }
    } catch (SQLException e) {
      logger.error("Can't create store; cause: {}", e.getMessage());
    }
    return -1;
  }

  public int updateStore(Store store) {
    if (store == null || store.getTitle() == null) {
      return -1;
    }
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(UPDATE_STORE);
      prepStatement.setString(1, store.getTitle());
      prepStatement.setInt(2, store.getId());
      return prepStatement.executeUpdate();
    } catch (SQLException e) {
      logger.error("Can't update store with id={}; cause: {}", store.getId(), e.getMessage());
    }
    return -1;
  }

  public int putProductsIntoStore(int store_id, List<CountableProduct> countableProducts) {
    if (countableProducts == null) {
      return -1;
    }
    try (Connection con = getConnection()) {
      PreparedStatement prepStatementForInsert =
          con.prepareStatement(CREATE_STORES_PRODUCTS_ENTRY, Statement.RETURN_GENERATED_KEYS);
      PreparedStatement prepStatementForUpdate =
          con.prepareStatement(UPDATE_STORES_PRODUCTS_ENTRY, Statement.RETURN_GENERATED_KEYS);

      Map<Integer, Integer> productsAmountMap = getProductsAmountFromStoresProductsAsMap(store_id);
      for (CountableProduct countableProduct : countableProducts) {
        Product product = countableProduct.getProduct();

        if (productsAmountMap.containsKey(product.getId())) {
          int amount = countableProduct.getAmount() + productsAmountMap.get(product.getId());
          prepStatementForUpdate.setInt(1, amount);
          prepStatementForUpdate.setInt(2, store_id);
          prepStatementForUpdate.setInt(3, product.getId());
          prepStatementForUpdate.addBatch();
        } else {
          prepStatementForInsert.setInt(1, store_id);
          prepStatementForInsert.setInt(2, product.getId());
          prepStatementForInsert.setInt(3, countableProduct.getAmount());
          prepStatementForInsert.addBatch();
        }
      }
      int[] updated = prepStatementForUpdate.executeBatch();
      int[] created = prepStatementForInsert.executeBatch();
      return updated.length + created.length;
    } catch (SQLException e) {
      logger.error("Can't create stores-products entries; cause: {}", e.getMessage());
    }
    return -1;
  }

  public int takeProductsFromStore(int store_id, List<CountableProduct> countableProducts) {
    List<Product> products = countableProducts.
        stream()
        .map(x -> x.getProduct())
        .collect(Collectors.toList());

    if (products == null) {
      return -1;
    }
    try (Connection con = getConnection()) {
      PreparedStatement prepStatementForDelete =
          con.prepareStatement(DELETE_STORES_PRODUCTS_ENTRY, Statement.RETURN_GENERATED_KEYS);
      PreparedStatement prepStatementForUpdate =
          con.prepareStatement(UPDATE_STORES_PRODUCTS_ENTRY, Statement.RETURN_GENERATED_KEYS);

      Map<Integer, Integer> productAmountMap = getProductsAmountFromStoresProductsAsMap(store_id);
      for (CountableProduct countableProduct : countableProducts) {
        if (countableProduct.getAmount() <= 0) {
          continue;
        }

        Product product = countableProduct.getProduct();
        boolean isProductNotPresentInStore = !productAmountMap.containsKey(product.getId());
        if (isProductNotPresentInStore) {
          return -1;
        }
        int resultAmount = productAmountMap.get(product.getId()) - countableProduct.getAmount();
        if (resultAmount > 0) {
          prepStatementForUpdate.setInt(1, resultAmount);
          prepStatementForUpdate.setInt(2, store_id);
          prepStatementForUpdate.setInt(3, product.getId());
          prepStatementForUpdate.addBatch();
        } else {
          prepStatementForDelete.setInt(1, store_id);
          prepStatementForDelete.setInt(2, product.getId());
          prepStatementForDelete.addBatch();
        }

      }
      int[] updated = prepStatementForUpdate.executeBatch();
      int[] deleted = prepStatementForDelete.executeBatch();
      return updated.length + deleted.length;
    } catch (SQLException e) {
      logger.error("Can't create stores-products entries; cause: {}", e.getMessage());
    }
    return -1;
  }

  public boolean checkStoreWithIdExists(int id) {
    if (id < 0) {
      return false;
    }
    boolean result = false;
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(CHECK_ID_EXISTS);
      prepStatement.setInt(1, id);
      ResultSet resultSet = prepStatement.executeQuery();

      while (resultSet.next()) {
        result = resultSet.getBoolean("exists");
      }
    } catch (SQLException e) {
      logger.error("Can't check store id; cause: {}", e.getMessage());
    }
    return result;
  }

  public boolean checkStoreWithTitleExists(String title) {
    if (title == null) {
      return false;
    }
    boolean result = false;
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(CHECK_TITLE_ALREADY_TAKEN);
      prepStatement.setString(1, title);
      ResultSet resultSet = prepStatement.executeQuery();

      while (resultSet.next()) {
        result = resultSet.getBoolean("exists");
      }
    } catch (SQLException e) {
      logger.error("Can't check store title; cause: {}", e.getMessage());
    }
    return result;
  }

  public boolean checkProductsAmountInStoreIsEnough(int store_id, List<CountableProduct> products) {
    Map<Integer, Integer> productsAmountMap = getProductsAmountFromStoresProductsAsMap(store_id);
    for (CountableProduct countableProduct : products) {
      if (!productsAmountMap.containsKey(countableProduct.getProduct().getId())
          && countableProduct.getAmount() > 0) {
        return false;
      }
      int amount = productsAmountMap.get(countableProduct.getProduct().getId());
      if (amount < countableProduct.getAmount()) {
        return false;
      }
    }
    return true;
  }

  public boolean checkSeveralStoresWithIdExists(List<Integer> stores) {
    if (stores == null || stores.isEmpty()) {
      return false;
    }
    try (Connection con = getConnection()) {
      StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM stores WHERE id IN (?");
      for (int i = 1; i < stores.size(); i++) {
        sb.append(",?");
      }
      sb.append(")");
      String sql = sb.toString();
      PreparedStatement prepStatement = con.prepareStatement(sql);
      int i = 1;
      for (Integer store_id : stores) {
        prepStatement.setInt(i, store_id);
        i++;
      }
      ResultSet resultSet = prepStatement.executeQuery();

      int result = 0;
      while (resultSet.next()) {
        result = resultSet.getInt("count");
      }
      if (result == stores.size()) {
        return true;
      }
    } catch (SQLException e) {
      logger.error("Can't check several stores id's; cause: {}", e.getMessage());
    }
    return false;
  }

  private Map<Integer, Integer> getProductsAmountFromStoresProductsAsMap(int id) {
    Map<Integer, Integer> map = new HashMap<>();
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(GET_PRODUCTS_AMOUNT_FROM_STORE);
      prepStatement.setInt(1, id);
      ResultSet resultSet = prepStatement.executeQuery();
      while (resultSet.next()) {
        int product_id = resultSet.getInt("product_id");
        int amount = resultSet.getInt("amount");
        map.put(product_id, amount);
      }
    } catch (SQLException e) {
      logger.error("Can't get products amount map from stores_products id; cause: {}",
          e.getMessage());
    }
    return map;
  }
}