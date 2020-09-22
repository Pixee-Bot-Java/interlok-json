package com.adaptris.core.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JacksonDeserializerTest extends DeserializerCase {

  private JacksonJsonDeserializer s = new JacksonJsonDeserializer();

  @Test
  public void testValidObject() throws Exception {
    assertNotNull(s.deserialize(jsonObject));
    assertEquals(ObjectNode.class, s.deserialize(jsonObject).getClass());

  }

  @Test(expected = JsonProcessingException.class)
  public void testInvalidObject() throws Exception {
    s.deserialize(invalidJsonObj);
  }

  @Test
  public void testValidArray() throws Exception {
    assertNotNull(s.deserialize(jsonArray));
    assertEquals(ArrayNode.class, s.deserialize(jsonArray).getClass());

  }

  @Test(expected = JsonProcessingException.class)
  public void testInvalidArray() throws Exception {
    s.deserialize(invalidJsonArray);
  }

  @Test(expected = JsonProcessingException.class)
  public void testNotJson() throws Exception {
    s.deserialize(notJson);
  }

}
