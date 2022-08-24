package dev.lacky.warehouse.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.lacky.warehouse.util.JsonBigDecimalSerializer;
import java.math.BigDecimal;

public class Product {

  public static final int PRODUCT_CODE_MAX_LENGTH = 30;
  public static final int PRODUCT_TITLE_MAX_LENGTH = 30;

  private int id;
  private String code;
  private String title;
  @JsonSerialize(using = JsonBigDecimalSerializer.class)
  private BigDecimal lastPurchasePrice;
  @JsonSerialize(using = JsonBigDecimalSerializer.class)
  private BigDecimal lastSalePrice;

  public Product() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public BigDecimal getLastPurchasePrice() {
    return lastPurchasePrice;
  }

  public void setLastPurchasePrice(BigDecimal lastPurchasePrice) {
    this.lastPurchasePrice = lastPurchasePrice;
  }

  public BigDecimal getLastSalePrice() {
    return lastSalePrice;
  }

  public void setLastSalePrice(BigDecimal lastSalePrice) {
    this.lastSalePrice = lastSalePrice;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Product product = (Product) o;

    if (id != product.id) {
      return false;
    }
    if (code != null ? !code.equals(product.code) : product.code != null) {
      return false;
    }
    if (title != null ? !title.equals(product.title) : product.title != null) {
      return false;
    }
    if (lastPurchasePrice != null ? !lastPurchasePrice.equals(product.lastPurchasePrice)
        : product.lastPurchasePrice != null) {
      return false;
    }
    return lastSalePrice != null ? lastSalePrice.equals(product.lastSalePrice)
        : product.lastSalePrice == null;
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + (code != null ? code.hashCode() : 0);
    result = 31 * result + (title != null ? title.hashCode() : 0);
    result = 31 * result + (lastPurchasePrice != null ? lastPurchasePrice.hashCode() : 0);
    result = 31 * result + (lastSalePrice != null ? lastSalePrice.hashCode() : 0);
    return result;
  }
}
