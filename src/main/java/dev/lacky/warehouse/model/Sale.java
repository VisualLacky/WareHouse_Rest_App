package dev.lacky.warehouse.model;

public class Sale {

  private int id;
  private int storeId;
  private String storeTitle;
  private int invoiceId;

  public Sale() {
  }

  public Sale(int storeId, int invoiceId) {
    this.storeId = storeId;
    this.invoiceId = invoiceId;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Sale sale = (Sale) o;

    if (id != sale.id) {
      return false;
    }
    if (storeId != sale.storeId) {
      return false;
    }
    if (invoiceId != sale.invoiceId) {
      return false;
    }
    return storeTitle != null ? storeTitle.equals(sale.storeTitle) : sale.storeTitle == null;
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + storeId;
    result = 31 * result + (storeTitle != null ? storeTitle.hashCode() : 0);
    result = 31 * result + invoiceId;
    return result;
  }
}
