package dev.lacky.warehouse.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.lacky.warehouse.exception.InvalidInputArgumentException;
import dev.lacky.warehouse.servlet.SingleProductServlet;
import dev.lacky.warehouse.pojo.ServerResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonParser {

  private static ObjectMapper objectMapper = getDefaultObjectMapper();
  private static final Logger logger = LoggerFactory.getLogger(SingleProductServlet.class);

  private static ObjectMapper getDefaultObjectMapper() {
    ObjectMapper defaultObjectMapper = new ObjectMapper();
    return defaultObjectMapper;
  }

  public static JsonNode parse(String source) throws InvalidInputArgumentException {
    try {
      return objectMapper.readTree(source);
    } catch (JsonProcessingException e) {
      throw new InvalidInputArgumentException("Input data is invalid");
    }
  }

  public static <T> T fromJson(JsonNode node, Class<T> clazz) throws InvalidInputArgumentException {
    try {
      return objectMapper.treeToValue(node, clazz);
    } catch (JsonProcessingException e) {
      logger.debug("Can't parse json to class {}, cause: {}", clazz.getClass(), e.getMessage());
      throw new InvalidInputArgumentException("Input data is invalid");
    }
  }

  public static JsonNode toJson(Object a) {
    return objectMapper.valueToTree(a);
  }

  public static String stringify(JsonNode node) throws JsonProcessingException {
    ObjectWriter objectWriter = objectMapper.writer();
    objectWriter = objectWriter.with(SerializationFeature.INDENT_OUTPUT);
    return objectWriter.writeValueAsString(node);
  }

  public static void removeRowsFromArray(JsonNode originNode, String... rowsToDelete) {
    for (JsonNode currentNode : originNode) {
      ObjectNode objectNode = (ObjectNode) currentNode;
      for (int i = 0; i < rowsToDelete.length; i++) {
        objectNode.remove(rowsToDelete[i]);
      }
    }
  }

  public static void removeRowsFromChildArray(JsonNode originNode, String childName,
      String... rowsToDelete) {
    for (JsonNode currentNode : originNode) {
      for (JsonNode nodeForModification : currentNode) {

        ObjectNode objectNode = null;
        try {
          objectNode = (ObjectNode) nodeForModification;
        } catch (ClassCastException e) {
          continue;
        }

        for (int i = 0; i < rowsToDelete.length; i++) {
          objectNode.remove(rowsToDelete[i]);
        }
      }
    }
  }

  public static void removeRowsFromRoot(JsonNode rootNode, String... rowsToDelete) {
    ObjectNode objectNode = (ObjectNode) rootNode;
    for (int i = 0; i < rowsToDelete.length; i++) {
      objectNode.remove(rowsToDelete[i]);
    }
  }

  public static String createJsonMessageString(String message) {
    try {
      return stringify(toJson(new ServerResponseMessage(message)));
    } catch (JsonProcessingException e) {
      return "empty";
    }
  }
}