package dev.lacky.warehouse.dao;

import dev.lacky.warehouse.connection.PoolConnectionBuilder;
import dev.lacky.warehouse.model.Income;
import dev.lacky.warehouse.model.Product;
import dev.lacky.warehouse.pojo.CountableProduct;
import dev.lacky.warehouse.pojo.IncomeDocument;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class IncomeDao extends BaseDao {

  public static final String GET_ALL_INCOMES = "SELECT inc.id as income_id, inc.store_id, str.title as store_title, invoice_id FROM incomes as inc JOIN stores as str ON inc.store_id=str.id";
  public static final String GET_INCOME_BY_ID = "SELECT inc.id as income_id, inc.store_id, str.title as store_title, invoice_id FROM incomes as inc JOIN stores as str ON inc.store_id=str.id WHERE inc.id=?";
  public static final String DELETE_INCOME = "DELETE FROM incomes WHERE id=?";
  public static final String CREATE_INCOME = "INSERT INTO incomes(store_id, invoice_id) values(?,?)";
  public static final String UPDATE_INCOME = "UPDATE incomes SET store_id=?, invoice_id=? WHERE id=?";
  public static final String GET_ALL_PRODUCTS_IN_INCOME = "SELECT * FROM invoices_products as inv JOIN products as pr ON inv.product_id=pr.id WHERE invoice_id=?";

  public IncomeDao() {
    setConnectionBuilder(new PoolConnectionBuilder());
  }

  public List<Income> getAllIncomes() {
    List<Income> incomes = new ArrayList<>();
    try (Connection con = getConnection()) {
      Statement statement = con.createStatement();
      ResultSet resultSet = statement.executeQuery(GET_ALL_INCOMES);

      while (resultSet.next()) {
        Income income = new Income();
        income.setId(resultSet.getInt("income_id"));
        income.setStoreId(resultSet.getInt("store_id"));
        income.setStoreTitle(resultSet.getString("store_title"));
        income.setInvoiceId(resultSet.getInt("invoice_id"));
        incomes.add(income);
      }
    } catch (SQLException e) {
      logger.error("Can't retrieve all incomes; cause: {}", e.getMessage());
      return null;
    }
    return incomes;
  }

  public List<IncomeDocument> getAllIncomeDocuments() {
    List<Income> incomes = getAllIncomes();
    List<IncomeDocument> incomeDocuments = new ArrayList<>();

    for (Income income : incomes) {
      IncomeDocument doc = new IncomeDocument();
      doc.setId(income.getId());
      doc.setStoreId(income.getStoreId());
      doc.setStoreTitle(income.getStoreTitle());
      doc.setInvoiceId(income.getInvoiceId());
      doc.setCountableProducts(getAllCountableProductsInIncome(income.getInvoiceId()));
      incomeDocuments.add(doc);
    }
    return incomeDocuments;
  }

  public IncomeDocument getIncomeDocumentById(int id) {
    Income income = getIncomeById(id);
    if (income == null) {
      return null;
    }
    IncomeDocument doc = new IncomeDocument();
    doc.setId(income.getId());
    doc.setStoreId(income.getStoreId());
    doc.setStoreTitle(income.getStoreTitle());
    doc.setInvoiceId(income.getInvoiceId());
    doc.setCountableProducts(getAllCountableProductsInIncome(income.getInvoiceId()));
    return doc;
  }

  public List<CountableProduct> getAllCountableProductsInIncome(int invoice_id) {
    if (invoice_id < 0) {
      return null;
    }
    List<CountableProduct> countableProducts = new ArrayList<>();
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(GET_ALL_PRODUCTS_IN_INCOME);
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
      logger.error("Can't get all products in income; cause: {}", e.getMessage());
    }
    return null;
  }

  public Income getIncomeById(int id) {
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(GET_INCOME_BY_ID);
      prepStatement.setInt(1, id);
      ResultSet resultSet = prepStatement.executeQuery();

      while (resultSet.next()) {
        Income income = new Income();
        income.setId(resultSet.getInt("income_id"));
        income.setStoreId(resultSet.getInt("store_id"));
        income.setStoreTitle(resultSet.getString("store_title"));
        income.setInvoiceId(resultSet.getInt("invoice_id"));
        return income;
      }
    } catch (SQLException e) {
      logger.error("Can't retrieve income by id '{}'; cause: {}", id, e.getMessage());
    }
    return null;
  }

  public int deleteIncome(int id) {
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(DELETE_INCOME);
      prepStatement.setInt(1, id);
      return prepStatement.executeUpdate();
    } catch (SQLException e) {
      logger.error("Can't delete income by id '{}'; cause: {}", id, e.getMessage());
    }
    return -1;
  }

  public int createIncome(Income income) {
    if (income == null) {
      return -1;
    }
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement =
          con.prepareStatement(CREATE_INCOME, Statement.RETURN_GENERATED_KEYS);
      prepStatement.setInt(1, income.getStoreId());
      prepStatement.setInt(2, income.getInvoiceId());
      prepStatement.executeUpdate();
      ResultSet keys = prepStatement.getGeneratedKeys();
      if (keys.next()) {
        return keys.getInt(1);
      }
    } catch (SQLException e) {
      logger.error("Can't create income; cause: {}", e.getMessage());
    }
    return -1;
  }

  public int updateIncome(Income income) {
    if (income == null) {
      return -1;
    }
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(UPDATE_INCOME);
      prepStatement.setInt(1, income.getStoreId());
      prepStatement.setInt(2, income.getInvoiceId());
      prepStatement.setInt(3, income.getId());
      return prepStatement.executeUpdate();
    } catch (SQLException e) {
      logger.error("Can't update income with id={}; cause: {}", income.getId(), e.getMessage());
    }
    return -1;
  }
}
