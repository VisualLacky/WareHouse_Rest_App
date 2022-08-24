package dev.lacky.warehouse.service;

import dev.lacky.warehouse.dao.InvoiceDao;
import dev.lacky.warehouse.dao.ProductDao;
import dev.lacky.warehouse.dao.SaleDao;
import dev.lacky.warehouse.dao.StoreDao;
import dev.lacky.warehouse.enums.ProductMovementTypes;
import dev.lacky.warehouse.exception.InvalidInputArgumentException;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.model.Invoice;
import dev.lacky.warehouse.model.Product;
import dev.lacky.warehouse.model.Sale;
import dev.lacky.warehouse.pojo.CountableProduct;
import dev.lacky.warehouse.pojo.SaleDocument;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SaleService {

  public static final int SALE_TRANSACTION_TYPE = 1;

  private SaleDao saleDao;
  private StoreDao storeDao;
  private ProductDao productDao;
  private InvoiceDao invoiceDao;

  public SaleService() {
    saleDao = new SaleDao();
    storeDao = new StoreDao();
    productDao = new ProductDao();
    invoiceDao = new InvoiceDao();
  }

  public SaleService(SaleDao saleDao, StoreDao storeDao,
      ProductDao productDao, InvoiceDao invoiceDao) {
    this.saleDao = saleDao;
    this.storeDao = storeDao;
    this.productDao = productDao;
    this.invoiceDao = invoiceDao;
  }

  public List<SaleDocument> getAllSaleDocuments() throws NoContentException {
    List<SaleDocument> saleDocuments = saleDao.getAllSaleDocuments();
    if (saleDocuments.size() == 0) {
      throw new NoContentException();
    }
    return saleDocuments;
  }

  public SaleDocument getSaleDocumentById(int id)
      throws NoContentException, InvalidInputArgumentException {
    if (id < 0) {
      throw new InvalidInputArgumentException("Invalid sale ID");
    }

    SaleDocument saleDocument = saleDao.getSaleDocumentById(id);
    if (saleDocument == null) {
      throw new NoContentException();
    }
    return saleDocument;
  }

  public void processSale(SaleDocument saleDocument)
      throws InvalidInputArgumentException, SQLException {
    if (saleDocument == null) {
      throw new InvalidInputArgumentException("Provided data is invalid");
    }

    int store_id = saleDocument.getStoreId();
    List<CountableProduct> countableProducts = saleDocument.getCountableProducts();
    List<Product> products = new ArrayList<>();
    for (CountableProduct countableProduct : countableProducts) {
      Product product = countableProduct.getProduct();
      product.setLastSalePrice(countableProduct.getPrice());
      products.add(product);
    }

    if (isStoreNotExists(store_id)) {
      throw new InvalidInputArgumentException("Invalid store ID");
    }
    if (isNotAllProductsExists(products)) {
      throw new InvalidInputArgumentException("Invalid product ID");
    }
    if (isInputProductsHaveZeroOrNegativeAmount(countableProducts)) {
      throw new InvalidInputArgumentException("Invalid product amount");
    }
    if (isInputProductsHaveInvalidPrices(countableProducts)) {
      throw new InvalidInputArgumentException("Invalid product price");
    }
    if (isProductsAmountInStoreIsNotEnough(store_id, countableProducts)) {
      throw new InvalidInputArgumentException("Not enough product amount in store to take");
    }

    Invoice invoice = new Invoice(SALE_TRANSACTION_TYPE);
    int invoice_id = invoiceDao.createInvoice(invoice);
    if (invoice_id < 0) {
      throw new SQLException();
    }

    int invoice_result = invoiceDao.createInvoiceProductsEntries(
        invoice_id,
        ProductMovementTypes.SALE,
        countableProducts);
    if (invoice_result < 0) {
      throw new SQLException();
    }

    Sale sale = new Sale(store_id, invoice_id);
    int sale_id = saleDao.createSale(sale);
    if (sale_id < 0) {
      throw new SQLException();
    }

    int products_taken = storeDao.takeProductsFromStore(store_id, countableProducts);
    if (products_taken < 0) {
      throw new SQLException();
    }

    int pricesUpdated = productDao.setSalePricesForProducts(products);
    if (pricesUpdated < 0) {
      throw new SQLException();
    }
  }

  private boolean isStoreNotExists(int id) {
    return !storeDao.checkStoreWithIdExists(id);
  }

  private boolean isNotAllProductsExists(List<Product> products) {
    return !productDao.checkSeveralProductsWithIdExists(products);
  }

  private boolean isProductsAmountInStoreIsNotEnough(int store_id,
      List<CountableProduct> products) {
    return !storeDao.checkProductsAmountInStoreIsEnough(store_id, products);
  }

  private boolean isInputProductsHaveZeroOrNegativeAmount(List<CountableProduct> products) {
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