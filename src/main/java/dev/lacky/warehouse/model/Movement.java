package dev.lacky.warehouse.model;

public class Movement {

  private int id;
  private int fromStoreId;
  private String fromStoreTitle;
  private int toStoreId;
  private String toStoreTitle;
  private int invoiceId;

  public Movement() {
  }

  public Movement(int fromStoreId, int toStoreId, int invoiceId) {
    this.fromStoreId = fromStoreId;
    this.toStoreId = toStoreId;
    this.invoiceId = invoiceId;
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

  public String getFromStoreTitle() {
    return fromStoreTitle;
  }

  public void setFromStoreTitle(String fromStoreTitle) {
    this.fromStoreTitle = fromStoreTitle;
  }

  public int getToStoreId() {
    return toStoreId;
  }

  public void setToStoreId(int toStoreId) {
    this.toStoreId = toStoreId;
  }

  public String getToStoreTitle() {
    return toStoreTitle;
  }

  public void setToStoreTitle(String toStoreTitle) {
    this.toStoreTitle = toStoreTitle;
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

    Movement movement = (Movement) o;

    if (id != movement.id) {
      return false;
    }
    if (fromStoreId != movement.fromStoreId) {
      return false;
    }
    if (toStoreId != movement.toStoreId) {
      return false;
    }
    if (invoiceId != movement.invoiceId) {
      return false;
    }
    if (fromStoreTitle != null ? !fromStoreTitle.equals(movement.fromStoreTitle)
        : movement.fromStoreTitle != null) {
      return false;
    }
    return toStoreTitle != null ? toStoreTitle.equals(movement.toStoreTitle)
        : movement.toStoreTitle == null;
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + fromStoreId;
    result = 31 * result + (fromStoreTitle != null ? fromStoreTitle.hashCode() : 0);
    result = 31 * result + toStoreId;
    result = 31 * result + (toStoreTitle != null ? toStoreTitle.hashCode() : 0);
    result = 31 * result + invoiceId;
    return result;
  }
}
