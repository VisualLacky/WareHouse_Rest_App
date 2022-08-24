package dev.lacky.warehouse.service;

import dev.lacky.warehouse.dao.InvoiceDao;
import dev.lacky.warehouse.dao.MovementDao;
import dev.lacky.warehouse.dao.ProductDao;
import dev.lacky.warehouse.dao.StoreDao;
import dev.lacky.warehouse.enums.ProductMovementTypes;
import dev.lacky.warehouse.exception.InvalidInputArgumentException;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.model.Invoice;
import dev.lacky.warehouse.model.Movement;
import dev.lacky.warehouse.model.Product;
import dev.lacky.warehouse.pojo.CountableProduct;
import dev.lacky.warehouse.pojo.MovementDocument;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MovementService {

  public static final int MOVEMENT_TRANSACTION_TYPE = 3;

  private final MovementDao movementDao;
  private final StoreDao storeDao;
  private final ProductDao productDao;
  private final InvoiceDao invoiceDao;

  public MovementService() {
    movementDao = new MovementDao();
    storeDao = new StoreDao();
    productDao = new ProductDao();
    invoiceDao = new InvoiceDao();
  }

  public MovementService(MovementDao movementDao, StoreDao storeDao,
      ProductDao productDao, InvoiceDao invoiceDao) {
    this.movementDao = movementDao;
    this.storeDao = storeDao;
    this.productDao = productDao;
    this.invoiceDao = invoiceDao;
  }

  public List<MovementDocument> getAllMovementDocuments() throws NoContentException {
    List<MovementDocument> movementDocuments = movementDao.getAllMovementDocuments();
    if (movementDocuments.size() == 0) {
      throw new NoContentException();
    }
    return movementDocuments;
  }

  public MovementDocument getMovementDocumentById(int id)
      throws InvalidInputArgumentException, NoContentException {
    if (id < 0) {
      throw new InvalidInputArgumentException("Invalid movement ID");
    }

    MovementDocument movementDocument = movementDao.getMovementDocumentById(id);
    if (movementDocument == null) {
      throw new NoContentException();
    }
    return movementDocument;
  }


  public void processMovement(MovementDocument movementDocument)
      throws InvalidInputArgumentException, SQLException {
    if (movementDocument == null) {
      throw new InvalidInputArgumentException("Provided data is invalid");
    }

    int from_store_id = movementDocument.getFromStoreId();
    int to_store_id = movementDocument.getToStoreId();
    List<CountableProduct> countableProducts = movementDocument.getCountableProducts();
    List<Product> products = countableProducts
        .stream()
        .map(x -> x.getProduct())
        .collect(Collectors.toList());

    if (isStoresNotExists(Arrays.asList(from_store_id, to_store_id))) {
      throw new InvalidInputArgumentException("Invalid store ID");
    }
    if (isNotAllProductsExists(products)) {
      throw new InvalidInputArgumentException("Invalid product ID");
    }
    if (isInputProductsHaveZeroOrNegativeAmounts(countableProducts)) {
      throw new InvalidInputArgumentException("Invalid product amount");
    }
    if (isProductsAmountsInStoreIsNotEnough(from_store_id, countableProducts)) {
      throw new InvalidInputArgumentException("Not enough product amount in store to take");
    }

    Invoice invoice = new Invoice(MOVEMENT_TRANSACTION_TYPE);
    int invoice_id = invoiceDao.createInvoice(invoice);
    if (invoice_id < 0) {
      throw new SQLException();
    }

    int result = invoiceDao.createInvoiceProductsEntries(
        invoice_id,
        ProductMovementTypes.MOVE,
        countableProducts);
    if (result < 0) {
      throw new SQLException();
    }

    Movement movement = new Movement(from_store_id, to_store_id, invoice_id);
    int movement_id = movementDao.createMovement(movement);
    if (movement_id < 0) {
      throw new SQLException();
    }

    int productsTaken = storeDao.takeProductsFromStore(from_store_id, countableProducts);
    if (productsTaken < 0) {
      throw new SQLException();
    }

    int productsPut = storeDao.putProductsIntoStore(to_store_id, countableProducts);
    if (productsPut < 0) {
      throw new SQLException();
    }
  }

  private boolean isStoresNotExists(List<Integer> storeIdList) {
    return !storeDao.checkSeveralStoresWithIdExists(storeIdList);
  }

  private boolean isNotAllProductsExists(List<Product> products) {
    return !productDao.checkSeveralProductsWithIdExists(products);
  }

  private boolean isProductsAmountsInStoreIsNotEnough(int store_id,
      List<CountableProduct> products) {
    return !storeDao.checkProductsAmountInStoreIsEnough(store_id, products);
  }

  private boolean isInputProductsHaveZeroOrNegativeAmounts(List<CountableProduct> products) {
    for (CountableProduct product : products) {
      if (product.getAmount() <= 0) {
        return true;
      }
    }
    return false;
  }
}