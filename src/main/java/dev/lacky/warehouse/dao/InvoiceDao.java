package dev.lacky.warehouse.dao;

import dev.lacky.warehouse.connection.PoolConnectionBuilder;
import dev.lacky.warehouse.enums.ProductMovementTypes;
import dev.lacky.warehouse.model.Invoice;
import dev.lacky.warehouse.model.Product;
import dev.lacky.warehouse.pojo.CountableProduct;
import dev.lacky.warehouse.pojo.InvoiceDocument;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDao extends BaseDao {

  public static final String GET_ALL_INVOICES = "SELECT inv.id, inv.transaction_type as transaction_type, typ.title as transaction_type_title FROM invoices as inv JOIN transaction_types as typ ON inv.transaction_type=typ.id";
  public static final String GET_INVOICE_BY_ID = "SELECT inv.id, inv.transaction_type as transaction_type, typ.title as transaction_type_title FROM invoices as inv JOIN transaction_types as typ ON inv.transaction_type=typ.id WHERE inv.id=?";
  public static final String DELETE_INVOICE = "DELETE FROM invoices WHERE id=?";
  public static final String CREATE_INVOICE = "INSERT INTO invoices(transaction_type) values(?)";
  public static final String CHECK_TRANSACTION_TYPE_EXISTS = "SELECT EXISTS(SELECT id FROM transaction_types WHERE id=?)";
  public static final String UPDATE_INVOICE = "UPDATE invoices SET transaction_type=? WHERE id=?";
  public static final String CREATE_INVOICE_PRODUCT_ENTRY = "INSERT INTO invoices_products(invoice_id, product_id, price, amount) VALUES(?, ?, ?, ?);";
  public static final String GET_ALL_PRODUCT_IN_INVOICE = "SELECT * FROM invoices_products as inv JOIN products as pr ON inv.product_id=pr.id WHERE invoice_id=?";

  public InvoiceDao() {
    setConnectionBuilder(new PoolConnectionBuilder());
  }

  public List<Invoice> getAllInvoices() {
    List<Invoice> invoices = new ArrayList<>();
    try (Connection con = getConnection()) {
      Statement statement = con.createStatement();
      ResultSet resultSet = statement.executeQuery(GET_ALL_INVOICES);

      while (resultSet.next()) {
        Invoice invoice = new Invoice();
        invoice.setId(resultSet.getInt("id"));
        invoice.setTransactionType(resultSet.getInt("transaction_type"));
        invoice.setTransactionTypeTitle(resultSet.getString("transaction_type_title"));
        invoices.add(invoice);
      }
    } catch (SQLException e) {
      logger.error("Can't retrieve all invoices; cause: {}", e.getMessage());
    }
    return invoices;
  }

  public List<InvoiceDocument> getAllInvoicesDocuments() {
    List<Invoice> invoices = getAllInvoices();
    List<InvoiceDocument> invoiceDocuments = new ArrayList<>();
    for (Invoice invoice : invoices) {
      InvoiceDocument doc = new InvoiceDocument();
      doc.setId(invoice.getId());
      doc.setTransactionTypeId(invoice.getTransactionType());
      doc.setTransactionTypeTitle(invoice.getTransactionTypeTitle());
      doc.setCountableProducts(getAllCountableProductsInInvoice(invoice.getId()));
      invoiceDocuments.add(doc);
    }
    return invoiceDocuments;
  }

  public InvoiceDocument getInvoiceDocumentById(int id) {
    Invoice invoice = getInvoiceById(id);
    if (invoice == null) {
      return null;
    }
    InvoiceDocument doc = new InvoiceDocument();
    doc.setId(invoice.getId());
    doc.setTransactionTypeId(invoice.getTransactionType());
    doc.setTransactionTypeTitle(invoice.getTransactionTypeTitle());
    doc.setCountableProducts(getAllCountableProductsInInvoice(invoice.getId()));
    return doc;
  }

  public Invoice getInvoiceById(int id) {
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(GET_INVOICE_BY_ID);
      prepStatement.setInt(1, id);
      ResultSet resultSet = prepStatement.executeQuery();

      while (resultSet.next()) {
        Invoice invoice = new Invoice();
        invoice.setId(resultSet.getInt("id"));
        invoice.setTransactionType(resultSet.getInt("transaction_type"));
        invoice.setTransactionTypeTitle(resultSet.getString("transaction_type_title"));
        return invoice;
      }
    } catch (SQLException e) {
      logger.error("Can't retrieve invoice by id '{}'; cause: {}", id, e.getMessage());
    }
    return null;
  }

  public int deleteInvoice(int id) {
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(DELETE_INVOICE);
      prepStatement.setInt(1, id);
      return prepStatement.executeUpdate();
    } catch (SQLException e) {
      logger.error("Can't delete invoice by id '{}'; cause: {}", id, e.getMessage());
    }
    return -1;
  }

  public int createInvoice(Invoice invoice) {
    if (invoice == null) {
      return -1;
    }
    if (isTransactionTypeNotExists(invoice.getTransactionType())) {
      return -1;
    }

    try (Connection con = getConnection()) {
      PreparedStatement prepStatement =
          con.prepareStatement(CREATE_INVOICE, Statement.RETURN_GENERATED_KEYS);
      prepStatement.setInt(1, invoice.getTransactionType());
      prepStatement.executeUpdate();
      ResultSet keys = prepStatement.getGeneratedKeys();
      if (keys.next()) {
        return keys.getInt(1);
      }
    } catch (SQLException e) {
      logger.error("Can't create invoice; cause: {}", e.getMessage());
    }
    return -1;
  }

  public int updateInvoice(Invoice invoice) {
    if (invoice == null) {
      return -1;
    }
    if (isTransactionTypeNotExists(invoice.getTransactionType())) {
      return -1;
    }

    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(UPDATE_INVOICE);
      prepStatement.setInt(1, invoice.getTransactionType());
      prepStatement.setInt(2, invoice.getId());
      return prepStatement.executeUpdate();
    } catch (SQLException e) {
      logger.error("Can't update invoice with id={}; cause: {}", invoice.getId(), e.getMessage());
    }
    return -1;
  }

  public int createInvoiceProductsEntries(int invoice_id, ProductMovementTypes moveType,
      List<CountableProduct> cProducts) {
    if (invoice_id < 0 || cProducts == null || cProducts.isEmpty()) {
      return -1;
    }

    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(CREATE_INVOICE_PRODUCT_ENTRY);

      for (CountableProduct countableProduct : cProducts) {
        if (countableProduct.getPrice() == null) {
          countableProduct.setPrice(new BigDecimal(0));
        }
        Product product = countableProduct.getProduct();
        prepStatement.setInt(1, invoice_id);
        prepStatement.setInt(2, product.getId());
        if (moveType == ProductMovementTypes.MOVE) {
          prepStatement.setBigDecimal(3, new BigDecimal(0));
        } else {
          prepStatement.setBigDecimal(3, countableProduct.getPrice());
        }
        prepStatement.setInt(4, countableProduct.getAmount());
        prepStatement.addBatch();
      }

      int[] result = prepStatement.executeBatch();
      return result.length;
    } catch (SQLException e) {
      logger.error("Can't create invoice; cause: {}", e.getMessage());
    }
    return -1;
  }

  public List<CountableProduct> getAllCountableProductsInInvoice(int invoice_id) {
    if (invoice_id < 0) {
      return null;
    }
    List<CountableProduct> countableProducts = new ArrayList<>();
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(GET_ALL_PRODUCT_IN_INVOICE);
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
      logger.error("Can't get all products in invoice; cause: {}", e.getMessage());
    }
    return null;
  }

  public boolean checkTransactionTypeWithIdExists(int id) {
    if (id < 0) {
      return false;
    }
    boolean result = false;
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(CHECK_TRANSACTION_TYPE_EXISTS);
      prepStatement.setInt(1, id);
      ResultSet resultSet = prepStatement.executeQuery();

      while (resultSet.next()) {
        result = resultSet.getBoolean("exists");
      }
    } catch (SQLException e) {
      logger.error("Can't check transaction_type id; cause: {}", e.getMessage());
    }
    return result;
  }

  private boolean isTransactionTypeNotExists(int id) {
    return !checkTransactionTypeWithIdExists(id);
  }
}
