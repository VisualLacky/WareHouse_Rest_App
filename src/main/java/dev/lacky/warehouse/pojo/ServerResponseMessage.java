package dev.lacky.warehouse.pojo;

public class ServerResponseMessage {

  private String message;

  public ServerResponseMessage() {
  }

  public ServerResponseMessage(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}

