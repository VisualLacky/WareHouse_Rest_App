package dev.lacky.warehouse.model;

public class Store {

  public static final int STORE_TITLE_MAX_LENGTH = 30;

  private int id;
  private String title;

  public Store() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Store store = (Store) o;

    if (id != store.id) {
      return false;
    }
    return title != null ? title.equals(store.title) : store.title == null;
  }

  @Override
  public int hashCode() {
    int result = id;
    result = 31 * result + (title != null ? title.hashCode() : 0);
    return result;
  }
}
