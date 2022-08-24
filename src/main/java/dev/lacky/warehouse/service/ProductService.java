package dev.lacky.warehouse.service;

import static dev.lacky.warehouse.model.Product.PRODUCT_CODE_MAX_LENGTH;
import static dev.lacky.warehouse.model.Product.PRODUCT_TITLE_MAX_LENGTH;

import dev.lacky.warehouse.dao.ProductDao;
import dev.lacky.warehouse.exception.InvalidInputArgumentException;
import dev.lacky.warehouse.exception.NoContentException;
import dev.lacky.warehouse.model.Product;
import java.sql.SQLException;
import java.util.List;

public class ProductService {

  private final ProductDao productDao;

  public ProductService() {
    productDao = new ProductDao();
  }

  public ProductService(ProductDao productDao) {
    this.productDao = productDao;
  }

  public Product getProductById(int id) throws NoContentException, InvalidInputArgumentException {
    if (id < 0) {
      throw new InvalidInputArgumentException("Invalid product ID");
    }

    Product product = productDao.getProductById(id);
    if (product == null) {
      throw new NoContentException();
    }
    return product;
  }

  public void deleteProduct(int id)
      throws NoContentException, SQLException, InvalidInputArgumentException {
    if (id < 0) {
      throw new InvalidInputArgumentException("Invalid product ID");
    }

    int result = productDao.deleteProduct(id);
    if (result < 0) {
      throw new SQLException();
    } else if (result == 0) {
      throw new NoContentException();
    }
  }

  public void createProduct(Product product) throws SQLException, InvalidInputArgumentException {
    if (product == null) {
      throw new InvalidInputArgumentException("Provided product's field is invalid");
    }

    boolean ProductFieldsInvalid = isProductFieldsInvalid(product);
    boolean ProductCodeAlreadyExist = isCodeAlreadyExists(product.getCode());

    if (ProductFieldsInvalid) {
      throw new InvalidInputArgumentException("Provided product's field is invalid");
    }
    if (ProductCodeAlreadyExist) {
      throw new InvalidInputArgumentException("Product with this code already exists");
    }

    int result = productDao.createProduct(product);
    if (result < 0) {
      throw new SQLException();
    }
  }

  public void updateProduct(Product product) throws SQLException, InvalidInputArgumentException {
    if (product == null || product.getCode() == null) {
      throw new InvalidInputArgumentException("Invalid input data");
    }
    String oldProductCode = productDao.getProductCodeById(product.getId());

    boolean productFieldsInvalid = isProductFieldsInvalid(product);
    boolean productNotExist = isProductWithIdDoesNotExists(product.getId());
    boolean codeAlreadyTaken = isCodeAlreadyExists(product.getCode());
    boolean oldAndNewCodesAreDifferent = !product.getCode().equals(oldProductCode);

    if (productFieldsInvalid) {
      throw new InvalidInputArgumentException("Provided product's field is invalid");
    }
    if (productNotExist) {
      throw new InvalidInputArgumentException("Invalid product ID");
    }
    if (oldAndNewCodesAreDifferent && codeAlreadyTaken) {
      throw new InvalidInputArgumentException("This code is already taken");
    }

    int result = productDao.updateProduct(product);
    if (result < 0) {
      throw new SQLException();
    }
  }

  public List<Product> getAllProducts() throws NoContentException {
    List<Product> products = productDao.getAllProducts();
    if (products.size() == 0) {
      throw new NoContentException();
    }
    return products;
  }

  public List<Product> getProductsByName(String filter) throws NoContentException {
    List<Product> products = productDao.getProductsByName(filter);
    if (products.size() == 0) {
      throw new NoContentException();
    }
    return products;
  }

  private boolean isProductFieldsInvalid(Product product) {
    if (product.getCode() == null
        || product.getCode().length() > PRODUCT_CODE_MAX_LENGTH || product.getTitle() == null
        || product.getTitle().length() > PRODUCT_TITLE_MAX_LENGTH
        || product.getLastPurchasePrice() == null || product.getLastSalePrice() == null) {
      return true;
    } else {
      return false;
    }
  }

  private boolean isCodeAlreadyExists(String code) {
    return productDao.checkProductWithCodeExists(code);
  }

  private boolean isProductWithIdDoesNotExists(int id) {
    return !productDao.checkProductWithIdExists(id);
  }
}
