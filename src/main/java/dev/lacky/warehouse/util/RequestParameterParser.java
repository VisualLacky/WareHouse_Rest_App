package dev.lacky.warehouse.util;

public class RequestParameterParser {

  public static int parseStringId(String stringId) {
    if (stringId == null) {
      return -1;
    }
    int id = -1;

    try {
      id = Integer.parseInt(stringId);
    } catch (NumberFormatException e) {
      return -1;
    }
    return id;
  }
}
