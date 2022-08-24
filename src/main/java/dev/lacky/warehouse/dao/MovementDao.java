package dev.lacky.warehouse.dao;

import dev.lacky.warehouse.connection.PoolConnectionBuilder;
import dev.lacky.warehouse.model.Movement;
import dev.lacky.warehouse.model.Product;
import dev.lacky.warehouse.pojo.CountableProduct;
import dev.lacky.warehouse.pojo.MovementDocument;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MovementDao extends BaseDao {

  public static final String GET_ALL_MOVENETS = "SELECT mov.id as movement_id, mov.from_store_id, strfrom.title as from_store_title, mov.to_store_id, strto.title as to_store_title, mov.invoice_id FROM movements as mov JOIN stores as strfrom ON mov.from_store_id=strfrom.id JOIN stores as strto ON mov.to_store_id=strto.id";
  public static final String GET_MOVEMENT_BY_ID = "SELECT mov.id as movement_id, mov.from_store_id, strfrom.title as from_store_title, mov.to_store_id, strto.title as to_store_title, mov.invoice_id FROM movements as mov JOIN stores as strfrom ON mov.from_store_id=strfrom.id JOIN stores as strto ON mov.to_store_id=strto.id where mov.id=?";
  public static final String DELETE_MOVEMENT = "DELETE FROM movements WHERE id=?";
  public static final String CREATE_MOVEMENT = "INSERT INTO movements(from_store_id, to_store_id, invoice_id) values(?,?,?)";
  public static final String UPDATE_MOVEMENT = "UPDATE movements SET from_store_id=?, to_store_id=?, invoice_id=? WHERE id=?";
  public static final String GET_ALL_PRODUCTS_IN_MOVEMENT = "SELECT * FROM invoices_products as inv JOIN products as pr ON inv.product_id=pr.id WHERE invoice_id=?";

  public MovementDao() {
    setConnectionBuilder(new PoolConnectionBuilder());
  }

  public List<Movement> getAllMovements() {
    List<Movement> movements = new ArrayList<>();
    try (Connection con = getConnection()) {
      Statement statement = con.createStatement();
      ResultSet resultSet = statement.executeQuery(GET_ALL_MOVENETS);

      while (resultSet.next()) {
        Movement movement = new Movement();
        movement.setId(resultSet.getInt("movement_id"));
        movement.setFromStoreId(resultSet.getInt("from_store_id"));
        movement.setFromStoreTitle(resultSet.getString("from_store_title"));
        movement.setToStoreId(resultSet.getInt("to_store_id"));
        movement.setToStoreTitle(resultSet.getString("to_store_title"));
        movement.setInvoiceId(resultSet.getInt("invoice_id"));
        movements.add(movement);
      }
    } catch (SQLException e) {
      logger.error("Can't retrieve all movements; cause: {}", e.getMessage());
      return null;
    }
    return movements;
  }

  public List<MovementDocument> getAllMovementDocuments() {
    List<Movement> movements = getAllMovements();
    List<MovementDocument> movementDocuments = new ArrayList<>();

    try (Connection con = getConnection()) {
      for (Movement movement : movements) {
        MovementDocument doc = new MovementDocument();
        doc.setId(movement.getId());
        doc.setFromStoreId(movement.getFromStoreId());
        doc.setFromStoreTitle(movement.getFromStoreTitle());
        doc.setToStoreId(movement.getToStoreId());
        doc.setToStoreTitle(movement.getToStoreTitle());
        doc.setInvoice_id(movement.getInvoiceId());
        doc.setCountableProducts(getAllCountableProductsInMovement(movement.getInvoiceId()));
        movementDocuments.add(doc);
      }
      return movementDocuments;
    } catch (SQLException e) {
      logger.error("Can't retrieve all movement documents; cause: {}", e.getMessage());
      return null;
    }
  }

  public MovementDocument getMovementDocumentById(int id) {
    Movement movement = getMovementById(id);
    if (movement == null) {
      return null;
    }
    try (Connection con = getConnection()) {
      MovementDocument doc = new MovementDocument();
      doc.setId(movement.getId());
      doc.setFromStoreId(movement.getFromStoreId());
      doc.setFromStoreTitle(movement.getFromStoreTitle());
      doc.setToStoreId(movement.getToStoreId());
      doc.setToStoreTitle(movement.getToStoreTitle());
      doc.setInvoice_id(movement.getInvoiceId());
      doc.setCountableProducts(getAllCountableProductsInMovement(movement.getInvoiceId()));
      return doc;
    } catch (SQLException e) {
      logger.error("Can't retrieve all movement documents; cause: {}", e.getMessage());
      return null;
    }
  }

  public List<CountableProduct> getAllCountableProductsInMovement(int invoice_id) {
    if (invoice_id < 0) {
      return null;
    }
    List<CountableProduct> countableProducts = new ArrayList<>();
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(GET_ALL_PRODUCTS_IN_MOVEMENT);
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
      logger.error("Can't get all products in movement; cause: {}", e.getMessage());
    }
    return null;
  }

  public Movement getMovementById(int id) {
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(GET_MOVEMENT_BY_ID);
      prepStatement.setInt(1, id);
      ResultSet resultSet = prepStatement.executeQuery();

      while (resultSet.next()) {
        Movement movement = new Movement();
        movement.setId(resultSet.getInt("movement_id"));
        movement.setFromStoreId(resultSet.getInt("from_store_id"));
        movement.setFromStoreTitle(resultSet.getString("from_store_title"));
        movement.setToStoreId(resultSet.getInt("to_store_id"));
        movement.setToStoreTitle(resultSet.getString("to_store_title"));
        movement.setInvoiceId(resultSet.getInt("invoice_id"));
        return movement;
      }
    } catch (SQLException e) {
      logger.error("Can't retrieve movement by id '{}'; cause: {}", id, e.getMessage());
    }
    return null;
  }

  public int deleteMovement(int id) {
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(DELETE_MOVEMENT);
      prepStatement.setInt(1, id);
      return prepStatement.executeUpdate();
    } catch (SQLException e) {
      logger.trace("Can't delete movement by id '{}'; cause: {}", id, e.getMessage());
    }
    return -1;
  }

  public int createMovement(Movement movement) {
    if (movement == null) {
      return -1;
    }
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement =
          con.prepareStatement(CREATE_MOVEMENT, Statement.RETURN_GENERATED_KEYS);
      prepStatement.setInt(1, movement.getFromStoreId());
      prepStatement.setInt(2, movement.getToStoreId());
      prepStatement.setInt(3, movement.getInvoiceId());
      prepStatement.executeUpdate();
      ResultSet keys = prepStatement.getGeneratedKeys();

      if (keys.next()) {
        return keys.getInt(1);
      }
    } catch (SQLException e) {
      logger.error("Can't create movement; cause: {}", e.getMessage());
    }
    return -1;
  }

  public int updateMovement(Movement movement) {
    if (movement == null) {
      return -1;
    }
    try (Connection con = getConnection()) {
      PreparedStatement prepStatement = con.prepareStatement(UPDATE_MOVEMENT);
      prepStatement.setInt(1, movement.getFromStoreId());
      prepStatement.setInt(2, movement.getToStoreId());
      prepStatement.setInt(3, movement.getInvoiceId());
      prepStatement.setInt(4, movement.getId());
      return prepStatement.executeUpdate();
    } catch (SQLException e) {
      logger.error("Can't update movement with id={}; cause: {}", movement.getId(), e.getMessage());
    }
    return -1;
  }
}
