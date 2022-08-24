package dev.lacky.warehouse.model;

public class Invoice {

  private int id;
  private int transactionType;
  private String transactionTypeTitle;

  public Invoice() {
  }

  public Invoice(int transactionType) {
    this.transactionType = transactionType;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getTransactionType() {
    return transactionType;
  }

  public void setTransactionType(int transactionType) {
    this.transactionType = transactionType;
  }

  public String getTransactionTypeTitle() {
    return transactionTypeTitle;
  }

  public void setTransactionTypeTitle(String transactionTypeTitle) {
    this.transactionTypeTitle = transactionTypeTitle;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Invoice invoice = (Invoice) o;

    if (id != invoice.id) {
      return false;
    }
    if (transactionType != invoice.transactionType) {
      return false;
    }
    return transactionTypeTitle != null ? transactionTypeTitle.equals(invoice.transactionTypeTitle)
        : invoice.transactionTypeTitle == null;
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + transactionType;
    result = 31 * result + (transactionTypeTitle != null ? transactionTypeTitle.hashCode() : 0);
    return result;
  }
}


