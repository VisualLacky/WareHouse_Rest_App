package dev.lacky.warehouse.pojo;

import java.util.List;

public class InvoiceDocument {

  private int id;
  private int transactionTypeId;
  private String transactionTypeTitle;
  private List<CountableProduct> countableProducts;

  public InvoiceDocument() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getTransactionTypeId() {
    return transactionTypeId;
  }

  public void setTransactionTypeId(int transactionTypeId) {
    this.transactionTypeId = transactionTypeId;
  }

  public String getTransactionTypeTitle() {
    return transactionTypeTitle;
  }

  public void setTransactionTypeTitle(String transactionTypeTitle) {
    this.transactionTypeTitle = transactionTypeTitle;
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

    InvoiceDocument that = (InvoiceDocument) o;

    if (id != that.id) {
      return false;
    }
    if (transactionTypeId != that.transactionTypeId) {
      return false;
    }
    if (transactionTypeTitle != null ? !transactionTypeTitle.equals(that.transactionTypeTitle)
        : that.transactionTypeTitle != null) {
      return false;
    }
    return countableProducts != null ? countableProducts.equals(that.countableProducts)
        : that.countableProducts == null;
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + transactionTypeId;
    result = 31 * result + (transactionTypeTitle != null ? transactionTypeTitle.hashCode() : 0);
    result = 31 * result + (countableProducts != null ? countableProducts.hashCode() : 0);
    return result;
  }
}
