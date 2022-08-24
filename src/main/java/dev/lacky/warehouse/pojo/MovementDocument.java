package dev.lacky.warehouse.pojo;

import java.util.List;

public class MovementDocument {

  private int id;
  private int fromStoreId;
  private int toStoreId;
  private String fromStoreTitle;
  private String toStoreTitle;
  private int invoice_id;
  private List<CountableProduct> countableProducts;

  public MovementDocument() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getFromStoreId() {
    return fromStoreId;
  }

  public void setFromStoreId(int fromStoreId) {
    this.fromStoreId = fromStoreId;
  }

  public int getToStoreId() {
    return toStoreId;
  }

  public void setToStoreId(int toStoreId) {
    this.toStoreId = toStoreId;
  }

  public String getFromStoreTitle() {
    return fromStoreTitle;
  }

  public void setFromStoreTitle(String fromStoreTitle) {
    this.fromStoreTitle = fromStoreTitle;
  }

  public String getToStoreTitle() {
    return toStoreTitle;
  }

  public void setToStoreTitle(String toStoreTitle) {
    this.toStoreTitle = toStoreTitle;
  }

  public int getInvoice_id() {
    return invoice_id;
  }

  public void setInvoice_id(int invoice_id) {
    this.invoice_id = invoice_id;
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

    MovementDocument that = (MovementDocument) o;

    if (id != that.id) {
      return false;
    }
    if (fromStoreId != that.fromStoreId) {
      return false;
    }
    if (toStoreId != that.toStoreId) {
      return false;
    }
    if (invoice_id != that.invoice_id) {
      return false;
    }
    if (fromStoreTitle != null ? !fromStoreTitle.equals(that.fromStoreTitle)
        : that.fromStoreTitle != null) {
      return false;
    }
    if (toStoreTitle != null ? !toStoreTitle.equals(that.toStoreTitle)
        : that.toStoreTitle != null) {
      return false;
    }
    return countableProducts != null ? countableProducts.equals(that.countableProducts)
        : that.countableProducts == null;
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + fromStoreId;
    result = 31 * result + toStoreId;
    result = 31 * result + (fromStoreTitle != null ? fromStoreTitle.hashCode() : 0);
    result = 31 * result + (toStoreTitle != null ? toStoreTitle.hashCode() : 0);
    result = 31 * result + invoice_id;
    result = 31 * result + (countableProducts != null ? countableProducts.hashCode() : 0);
    return result;
  }
}
