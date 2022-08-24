package dev.lacky.warehouse.service;

import dev.lacky.warehouse.dao.IncomeDao;
import dev.lacky.warehouse.dao.InvoiceDao;
import dev.lacky.warehouse.dao.ProductDao;
import dev.lacky.warehouse.dao.StoreDao;
import dev.lacky.warehouse.enums.ProductMovementTypes;
import dev.lacky.warehouse.exception.InvalidInputArgumentException;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.model.Income;
import dev.lacky.warehouse.model.Invoice;
import dev.lacky.warehouse.model.Product;
import dev.lacky.warehouse.pojo.CountableProduct;
import dev.lacky.warehouse.pojo.IncomeDocument;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class IncomeService {

  public static final int INCOME_TRANSACTION_TYPE = 2;

  private IncomeDao incomeDao;
  private StoreDao storeDao;
  private ProductDao productDao;
  private InvoiceDao invoiceDao;

  public IncomeService() {
    incomeDao = new IncomeDao();
    storeDao = new StoreDao();
    productDao = new ProductDao();
    invoiceDao = new InvoiceDao();
  }

  public IncomeService(IncomeDao incomeDao, StoreDao storeDao,
      ProductDao productDao, InvoiceDao invoiceDao) {
    this.incomeDao = incomeDao;
    this.storeDao = storeDao;
    this.productDao = productDao;
    this.invoiceDao = invoiceDao;
  }

  public List<IncomeDocument> getAllIncomeDocuments() throws NoContentException {
    List<IncomeDocument> incomeDocuments = incomeDao.getAllIncomeDocuments();
    if (incomeDocuments.size() == 0) {
      throw new NoContentException();
    }
    return incomeDocuments;
  }

  public IncomeDocument getIncomeDocumentById(int id)
      throws InvalidInputArgumentException, NoContentException {
    if (id < 0) {
      throw new InvalidInputArgumentException("Invalid income ID");
    }

    IncomeDocument incomeDocument = incomeDao.getIncomeDocumentById(id);
    if (incomeDocument == null) {
      throw new NoContentException();
    }
    return incomeDocument;
  }

  public void processIncome(IncomeDocument incomeDocument)
      throws InvalidInputArgumentException, SQLException {
    if (incomeDocument == null) {
      throw new InvalidInputArgumentException("Provided data is invalid");
    }

    int store_id = incomeDocument.getStoreId();
    List<CountableProduct> countableProducts = incomeDocument.getCountableProducts();
    List<Product> products = new ArrayList<>();
    for (CountableProduct countableProduct : countableProducts) {
      Product product = countableProduct.getProduct();
      product.setLastPurchasePrice(countableProduct.getPrice());
      products.add(product);
    }

    if (isStoreNotExists(store_id)) {
      throw new InvalidInputArgumentException("Invalid store ID");
    }
    if (isNotAllProductsExists(products)) {
      throw new InvalidInputArgumentException("Invalid product ID");
    }
    if (isProductAmountNegativeOrZero(countableProducts)) {
      throw new InvalidInputArgumentException("Invalid product amount");
    }
    if (isInputProductsHaveInvalidPrices(countableProducts)) {
      throw new InvalidInputArgumentException("Invalid product price");
    }

    Invoice invoice = new Invoice(INCOME_TRANSACTION_TYPE);
    int invoice_id = invoiceDao.createInvoice(invoice);
    if (invoice_id < 0) {
      throw new SQLException();
    }

    int result = invoiceDao.createInvoiceProductsEntries(
        invoice_id,
        ProductMovementTypes.INCOME,
        countableProducts);
    if (result < 0) {
      throw new SQLException();
    }

    Income income = new Income(store_id, invoice_id);
    int income_id = incomeDao.createIncome(income);
    if (income_id < 0) {
      throw new SQLException();
    }

    int productsStuffed = storeDao.putProductsIntoStore(store_id, countableProducts);
    if (productsStuffed < 0) {
      throw new SQLException();
    }



    int updatedPrices = productDao.setPurchasePricesForProducts(products);
    if (updatedPrices < 0) {
      throw new SQLException();
    }
  }

  private boolean isStoreNotExists(int id) {
    return !storeDao.checkStoreWithIdExists(id);
  }

  private boolean isNotAllProductsExists(List<Product> products) {
    return !productDao.checkSeveralProductsWithIdExists(products);
  }

  private boolean isProductAmountNegativeOrZero(List<CountableProduct> products) {
    for (CountableProduct product : products) {
      if (product.getAmount() <= 0) {
        return true;
      }
    }
    return false;
  }

  private boolean isInputProductsHaveInvalidPrices(List<CountableProduct> products) {
    for (CountableProduct product : products) {
      if (product.getPrice() == null || product.getPrice().doubleValue() < 0) {
        return true;
      }
    }
    return false;
  }
}