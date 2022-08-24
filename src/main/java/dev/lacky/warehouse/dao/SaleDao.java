package dev.lacky.warehouse.dao;

import dev.lacky.warehouse.connection.PoolConnectionBuilder;
import dev.lacky.warehouse.model.Product;
import dev.lacky.warehouse.model.Sale;
import dev.lacky.warehouse.pojo.CountableProduct;
import dev.lacky.warehouse.pojo.SaleDocument;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SaleDao extends BaseDao {

  public static final String GET_ALL_STORES = "SELECT sal.id as sale_id, sal.store_id, str.title as store_title, invoice_id FROM sales as sal JOIN stores as str ON sal.store_id=str.id";
  public static final String GET_SALE_BY_ID = "SELECT sal.id as sale_id, sal.store_id, str.title as store_title, invoice_id FROM sales as sal JOIN stores as str ON sal.store_id=str.id WHERE sal.id=?";
  public static final String DELETE_SALE = "DELETE FROM sales WHERE id=?";
  public static final String CREATE_SALE = "INSERT INTO sales(store_id, invoice_id) values(?,?)";
  public static final String UPDATE_SALE = "UPDATE sales SET store_id=?, invoice_id=? WHERE id=?";
  public static final String GET_ALL_PRODUCTS_IN_SALE = "SELECT * FROM invoices_products as inv JOIN products as pr ON inv.product_id=pr.id WHERE invoice_id=?";

  public SaleDao() {
    setConnectionBuilder(new PoolConnectionBuilder());
  }

  public List<Sale> getAllSales() {
    List<Sale> sales = new ArrayList<>();
    try (Connection con = getConnection()) {
      Statement statement = con.createStatement();
      ResultSet resultSet = statement.executeQuery(GET_ALL_STORES);

      while (resultSet.next()) {
        Sale sale = new Sale();
        sale.setId(resultSet.getInt("sale_id"));
        sale.setStoreId(resultSet.getInt("store_id"));
        sale.setStoreTitle(resultSet.getString("store_title"));
        sale.setInvoiceId(resultSet.getInt("invoice_id"));
        sales.add(sale);
      }
    } catch (SQLException e) {
      logger.error("Can't retrieve all sales; cause: {}", e.getMessage());
      return null;
    }
    return sales;
  }

  public List<SaleDocument> getAllSaleDocuments() {
    List<Sale> sales = getAllSales();
    List<SaleDocument> saleDocuments = new ArrayList<>();

    try (Connection con = getConnection()) {
      for (Sale sale : sales) {
        SaleDocument doc = new SaleDocument();
        doc.setId(sale.getId());
        doc.setStoreId(sale.getStoreId());
        doc.setStoreTitle(sale.getStoreTitle());
        doc.setInvoice_id(sale.getInvoiceId());
        doc.setCountableProducts(getAllCountableProductsInSale(sale.getInvoiceId()));
        saleDocuments.add(doc);
      }
      return saleDocuments;
    } catch (SQLException e) {
      logger.error("Can't retrieve all sale documents; cause: {}", e.getMessage());
      return null;
    }
  }

  public SaleDocument getSaleDocumentById(int id) {
    Sale sale = getSaleById(id);
    if (sale == null) {
      return null;
    }
    try (Connection con = getConnection()) {
      SaleDocument doc = new SaleDocument();
      doc.setId(sale.getId());
      doc.setStoreId(sale.getStoreId());
      doc.setStoreTitle(sale.getStoreTitle());
      doc.setInvoice_id(sale.getInvoiceId());
      doc.setCountableProducts(getAllCountableProductsInSale(sale.getInvoiceId()));
      return doc;
    } catch (SQLException e) {
      logger.error("Can't retrieve all sale documents; cause: {}", e.getMessage());
      return null;
    }
  }

  public List<CountableProduct> getAllCountableProductsInSale(int invoice_id) {
    if (invoice_id < 0) {
      return null;
    }
    List<CountableProduct> countableProducts = new ArrayList<>();
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(GET_ALL_PRODUCTS_IN_SALE);
      prepStatement.setInt(1, invoice_id);
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
        countableProduct.setPrice(resultSet.getBigDecimal("price"));
        countableProducts.add(countableProduct);
      }
      return countableProducts;
    } catch (SQLException e) {
      logger.error("Can't get all products in sale; cause: {}", e.getMessage());
    }
    return null;
  }

  public Sale getSaleById(int id) {
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(GET_SALE_BY_ID);
      prepStatement.setInt(1, id);
      ResultSet resultSet = prepStatement.executeQuery();

      while (resultSet.next()) {
        Sale sale = new Sale();
        sale.setId(resultSet.getInt("sale_id"));
        sale.setStoreId(resultSet.getInt("store_id"));
        sale.setStoreTitle(resultSet.getString("store_title"));
        sale.setInvoiceId(resultSet.getInt("invoice_id"));
        return sale;
      }
    } catch (SQLException e) {
      logger.error("Can't retrieve sale by id '{}'; cause: {}", id, e.getMessage());
    }
    return null;
  }

  public int deleteSale(int id) {
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(DELETE_SALE);
      prepStatement.setInt(1, id);
      return prepStatement.executeUpdate();
    } catch (SQLException e) {
      logger.error("Can't delete sale by id '{}'; cause: {}", id, e.getMessage());
    }
    return -1;
  }

  public int createSale(Sale sale) {
    if (sale == null) {
      return -1;
    }
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement =
          con.prepareStatement(CREATE_SALE, Statement.RETURN_GENERATED_KEYS);
      prepStatement.setInt(1, sale.getStoreId());
      prepStatement.setInt(2, sale.getInvoiceId());
      prepStatement.executeUpdate();
      ResultSet keys = prepStatement.getGeneratedKeys();

      if (keys.next()) {
        return keys.getInt(1);
      }
    } catch (SQLException e) {
      logger.error("Can't create sale; cause: {}", e.getMessage());
    }
    return -1;
  }

  public int updateSale(Sale sale) {
    if (sale == null) {
      return -1;
    }
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(UPDATE_SALE);
      prepStatement.setInt(1, sale.getStoreId());
      prepStatement.setInt(2, sale.getInvoiceId());
      prepStatement.setInt(3, sale.getId());
      return prepStatement.executeUpdate();
    } catch (SQLException e) {
      logger.error("Can't update sale with id={}; cause: {}", sale.getId(), e.getMessage());
    }
    return -1;
  }
}