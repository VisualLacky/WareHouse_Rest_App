package dev.lacky.warehouse.pojo;

import java.util.List;

public class IncomeDocument {

  private int id;
  private int storeId;
  private String storeTitle;
  private int invoiceId;
  private List<CountableProduct> countableProducts;

  public IncomeDocument() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getStoreId() {
    return storeId;
  }

  public void setStoreId(int storeId) {
    this.storeId = storeId;
  }

  public String getStoreTitle() {
    return storeTitle;
  }

  public void setStoreTitle(String storeTitle) {
    this.storeTitle = storeTitle;
  }

  public int getInvoiceId() {
    return invoiceId;
  }

  public void setInvoiceId(int invoiceId) {
    this.invoiceId = invoiceId;
  }

  public List<CountableProduct> getCountableProducts() {
    return countableProducts;
  }

  public void setCountableProducts(
      List<CountableProduct> countableProducts) {
    this.countableProducts = countableProducts;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    IncomeDocument that = (IncomeDocument) o;

    if (id != that.id) {
      return false;
    }
    if (storeId != that.storeId) {
      return false;
    }
    if (invoiceId != that.invoiceId) {
      return false;
    }
    if (storeTitle != null ? !storeTitle.equals(that.storeTitle) : that.storeTitle != null) {
      return false;
    }
    return countableProducts != null ? countableProducts.equals(that.countableProducts)
        : that.countableProducts == null;
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + storeId;
    result = 31 * result + (storeTitle != null ? storeTitle.hashCode() : 0);
    result = 31 * result + invoiceId;
    result = 31 * result + (countableProducts != null ? countableProducts.hashCode() : 0);
    return result;
  }
}
