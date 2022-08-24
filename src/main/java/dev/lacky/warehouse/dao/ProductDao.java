package dev.lacky.warehouse.dao;

import dev.lacky.warehouse.connection.PoolConnectionBuilder;
import dev.lacky.warehouse.model.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductDao extends BaseDao {

  public static final String GET_ALL_PRODUCTS = "SELECT * FROM products";
  public static final String GET_PRODUCTS_WITH_FILTER = "SELECT * FROM products WHERE UPPER(title) LIKE UPPER(?)";
  public static final String GET_PRODUCT_BY_ID = "SELECT * FROM products WHERE id=?";
  public static final String DELETE_PRODUCT = "DELETE FROM products WHERE id=?";
  public static final String CREATE_PRODUCT = "INSERT INTO products(code, title, last_purchase_price, last_sale_price) values(?,?,?,?)";
  public static final String UPDATE_PRODUCT = "UPDATE products SET code=?, title=?, last_purchase_price=?, last_sale_price=? WHERE id=?";
  public static final String CHECK_CODE_EXISTS = "SELECT EXISTS(SELECT code FROM products WHERE code=?)";
  public static final String CHECK_ID_EXISTS = "SELECT EXISTS(SELECT id FROM products WHERE id=?)";
  public static final String UPDATE_PRODUCT_PURCHASE_PRICE = "UPDATE products SET last_purchase_price=? WHERE id=?";
  public static final String UPDATE_PRODUCT_SALE_PRICE = "UPDATE products SET last_sale_price=? WHERE id=?";
  public static final String GET_PRODUCT_CODE_BY_ID = "SELECT code FROM products WHERE id=?";

  public ProductDao() {
    setConnectionBuilder(new PoolConnectionBuilder());
  }

  public List<Product> getAllProducts() {
    List<Product> products = new ArrayList<>();
    try (Connection con = getConnection()) {
      Statement statement = con.createStatement();
      ResultSet resultSet = statement.executeQuery(GET_ALL_PRODUCTS);

      while (resultSet.next()) {
        Product product = new Product();
        product.setId(resultSet.getInt("id"));
        product.setCode(resultSet.getString("code"));
        product.setTitle(resultSet.getString("title"));
        product.setLastPurchasePrice(resultSet.getBigDecimal("last_purchase_price"));
        product.setLastSalePrice(resultSet.getBigDecimal("last_sale_price"));
        products.add(product);
      }
    } catch (SQLException e) {
      logger.error("Can't retrieve all products; cause: {}", e.getMessage());
    }
    return products;
  }

  public List<Product> getProductsByName(String title) {
    List<Product> products = new ArrayList<>();
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(GET_PRODUCTS_WITH_FILTER);
      prepStatement.setString(1, "%" + title + "%");
      ResultSet resultSet = prepStatement.executeQuery();

      while (resultSet.next()) {
        Product product = new Product();
        product.setId(resultSet.getInt("id"));
        product.setCode(resultSet.getString("code"));
        product.setTitle(resultSet.getString("title"));
        product.setLastPurchasePrice(resultSet.getBigDecimal("last_purchase_price"));
        product.setLastSalePrice(resultSet.getBigDecimal("last_sale_price"));
        products.add(product);
      }
    } catch (SQLException e) {
      logger.error("Can't retrieve products with filter '{}'; cause: {}", title, e.getMessage());
    }
    return products;
  }

  public String getProductCodeById(int id) {
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(GET_PRODUCT_BY_ID);
      prepStatement.setInt(1, id);
      ResultSet resultSet = prepStatement.executeQuery();

      while (resultSet.next()) {
        return resultSet.getString("code");
      }
    } catch (SQLException e) {
      logger.error("Can't retrieve code from product with id='{}'; cause: {}", id, e.getMessage());
    }
    return null;
  }

  public Product getProductById(int id) {
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(GET_PRODUCT_BY_ID);
      prepStatement.setInt(1, id);
      ResultSet resultSet = prepStatement.executeQuery();

      while (resultSet.next()) {
        Product product = new Product();
        product.setId(resultSet.getInt("id"));
        product.setCode(resultSet.getString("code"));
        product.setTitle(resultSet.getString("title"));
        product.setLastPurchasePrice(resultSet.getBigDecimal("last_purchase_price"));
        product.setLastSalePrice(resultSet.getBigDecimal("last_sale_price"));
        return product;
      }
    } catch (SQLException e) {
      logger.error("Can't retrieve product by id '{}'; cause: {}", id, e.getMessage());
    }
    return null;
  }

  public int deleteProduct(int id) {
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(DELETE_PRODUCT);
      prepStatement.setInt(1, id);
      return prepStatement.executeUpdate();
    } catch (SQLException e) {
      logger.error("Can't delete product by id '{}'; cause: {}", id, e.getMessage());
    }
    return -1;
  }

  public int createProduct(Product product) {
    if (product == null || product.getCode() == null || product.getTitle() == null) {
      return -1;
    }
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement =
          con.prepareStatement(CREATE_PRODUCT, Statement.RETURN_GENERATED_KEYS);
      prepStatement.setString(1, product.getCode());
      prepStatement.setString(2, product.getTitle());
      prepStatement.setBigDecimal(3, product.getLastPurchasePrice());
      prepStatement.setBigDecimal(4, product.getLastSalePrice());
      prepStatement.executeUpdate();
      ResultSet keys = prepStatement.getGeneratedKeys();

      if (keys.next()) {
        return keys.getInt(1);
      }
    } catch (SQLException e) {
      logger.error("Can't create product; cause: {}", e.getMessage());
    }
    return -1;
  }

  public int updateProduct(Product product) {
    if (product == null || product.getCode() == null || product.getTitle() == null) {
      return -1;
    }
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(UPDATE_PRODUCT);
      prepStatement.setString(1, product.getCode());
      prepStatement.setString(2, product.getTitle());
      prepStatement.setBigDecimal(3, product.getLastPurchasePrice());
      prepStatement.setBigDecimal(4, product.getLastSalePrice());
      prepStatement.setInt(5, product.getId());
      return prepStatement.executeUpdate();
    } catch (SQLException e) {
      logger.error("Can't update product with id={}; cause: {}", product.getId(), e.getMessage());
    }
    return -1;
  }

  public int setPurchasePricesForProducts(List<Product> products) {
    if (products == null) {
      return -1;
    }
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(UPDATE_PRODUCT_PURCHASE_PRICE);
      for (Product product : products) {
        prepStatement.setBigDecimal(1, product.getLastPurchasePrice());
        prepStatement.setInt(2, product.getId());
        prepStatement.addBatch();
      }
      int[] updated = prepStatement.executeBatch();
      if (updated.length == products.size()) {
        return updated.length;
      }
    } catch (SQLException e) {
      logger.error("Can't update products purchase prices ; cause: {}", e.getMessage());
    }
    return -1;
  }

  public int setSalePricesForProducts(List<Product> products) {
    if (products == null) {
      return -1;
    }
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(UPDATE_PRODUCT_SALE_PRICE);
      for (Product product : products) {
        prepStatement.setBigDecimal(1, product.getLastSalePrice());
        prepStatement.setInt(2, product.getId());
        prepStatement.addBatch();
      }
      int[] updated = prepStatement.executeBatch();

      if (updated.length == products.size()) {
        return updated.length;
      }
    } catch (SQLException e) {
      logger.error("Can't update products sale prices ; cause: {}", e.getMessage());
    }
    return -1;
  }

  public boolean checkProductWithCodeExists(String code) {
    if (code == null) {
      return false;
    }
    boolean result = false;
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(CHECK_CODE_EXISTS);
      prepStatement.setString(1, code);
      ResultSet resultSet = prepStatement.executeQuery();

      while (resultSet.next()) {
        result = resultSet.getBoolean("exists");
      }
    } catch (SQLException e) {
      logger.error("Can't check product code; cause: {}", e.getMessage());
    }
    return result;
  }

  public boolean checkProductWithIdExists(int id) {
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
      logger.error("Can't check product id; cause: {}", e.getMessage());
    }
    return result;
  }

  public boolean checkSeveralProductsWithIdExists(List<Product> products) {
    if (products == null || products.isEmpty()) {
      return false;
    }
    try (Connection con = getConnection()) {
      StringBuilder sb = new StringBuilder("SELECT COUNT(*) FROM products WHERE id IN (?");
      for (int i = 1; i < products.size(); i++) {
        sb.append(",?");
      }
      sb.append(")");
      String sql = sb.toString();
      PreparedStatement prepStatement = con.prepareStatement(sql);
      int i = 1;
      for (Product product : products) {
        prepStatement.setInt(i, product.getId());
        i++;
      }
      ResultSet resultSet = prepStatement.executeQuery();

      int result = 0;
      while (resultSet.next()) {
        result = resultSet.getInt("count");
      }
      if (result == products.size()) {
        return true;
      }
    } catch (SQLException e) {
      logger.error("Can't check several products id's; cause: {}", e.getMessage());
    }
    return false;
  }
}