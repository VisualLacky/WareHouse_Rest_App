package dev.lacky.warehouse.model;

public class Income {

  private int id;
  private int storeId;
  private String storeTitle;
  private int invoiceId;

  public Income() {
  }

  public Income(int storeId, int invoiceId) {
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

    Income income = (Income) o;

    if (id != income.id) {
      return false;
    }
    if (storeId != income.storeId) {
      return false;
    }
    if (invoiceId != income.invoiceId) {
      return false;
    }
    return storeTitle != null ? storeTitle.equals(income.storeTitle) : income.storeTitle == null;
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
