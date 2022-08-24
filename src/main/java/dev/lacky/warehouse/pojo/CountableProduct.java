package dev.lacky.warehouse.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.lacky.warehouse.model.Product;
import dev.lacky.warehouse.util.JsonBigDecimalSerializer;
import java.math.BigDecimal;

public class CountableProduct {

  int amount;
  @JsonSerialize(using = JsonBigDecimalSerializer.class)
  private BigDecimal price;
  Product product;

  public CountableProduct() {
  }

  public CountableProduct(Product product) {
    this.product = product;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CountableProduct that = (CountableProduct) o;

    if (amount != that.amount) {
      return false;
    }
    if (price != null ? !price.equals(that.price) : that.price != null) {
      return false;
    }
    return product != null ? product.equals(that.product) : that.product == null;
  }

  @Override
  public int hashCode() {
    int result = amount;
    result = 31 * result + (price != null ? price.hashCode() : 0);
    result = 31 * result + (product != null ? product.hashCode() : 0);
    return result;
  }
}
