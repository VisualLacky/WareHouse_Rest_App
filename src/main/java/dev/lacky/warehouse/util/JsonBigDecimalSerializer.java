package dev.lacky.warehouse.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.math.BigDecimal;

public class JsonBigDecimalSerializer extends JsonSerializer<BigDecimal> {

  @Override
  public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider)
      throws IOException,
      JsonProcessingException {
    jgen.writeNumber(value.doubleValue());
  }
}