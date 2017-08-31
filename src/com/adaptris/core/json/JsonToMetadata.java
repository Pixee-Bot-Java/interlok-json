package com.adaptris.core.json;

import java.io.BufferedReader;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.adaptris.annotation.AdapterComponent;
import com.adaptris.annotation.ComponentProfile;
import com.adaptris.annotation.InputFieldDefault;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.CoreException;
import com.adaptris.core.ServiceException;
import com.adaptris.core.ServiceImp;
import com.adaptris.core.services.path.json.JsonPathService;
import com.adaptris.core.util.ExceptionHelper;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Takes a JSON payload and adds every field at the root element as metadata.
 * 
 * <p>
 * This class is included as a convenience service for you to extract all the fields of a simple JSON document as metadata. In the
 * situation where you have a complex nested structure, then you continue use {@link JsonPathService} as normal. In the event that
 * there are nested objects, they will be turned into strings and added as-is.
 * </p>
 * <pre>
  {@code
    {
        "category": "fiction",
        "title": "The Lord of the Rings Trilogy",
        "price": 22.99,
        "volumes": [1, 2, 3],
        "suggested": {
            "title": "The Hobbit"
        }
    }
  }
  </pre>
 * <p>
 * will give you 5 metadata keys {@code category, title, price, volumes, suggested}. The field {@code volumes} is not a string (it
 * would be an array of integers), but it will be set as metadata as though it were (i.e the value is {@code [1,2,3]}); similarly,
 * {@code suggested} is a nested JSON object, but it will be simply rendered as a string and added as metadata (i.e the value will
 * be {@code {"title:"The Hobbit"}}).
 * <p>
 * 
 * @config json-to-metadata
 */
@XStreamAlias("json-to-metadata")
@AdapterComponent
@ComponentProfile(summary = "Extract all root fields from a JSON document as metadata", tag = "service,json,metadata")
public class JsonToMetadata extends ServiceImp {

  @InputFieldDefault(value = "")
  private String metadataPrefix;

  public JsonToMetadata() {
    super();
  }

  @Override
  public void doService(final AdaptrisMessage msg) throws ServiceException {
    try {
      BufferedReader buf = new BufferedReader(msg.getReader());

      ObjectMapper mapper = new ObjectMapper();
      JsonParser parser = mapper.getFactory().createParser(buf);
      if (parser.nextToken() == JsonToken.START_OBJECT) {
        ObjectNode node = mapper.readTree(parser);
        for (String key : makeIterable(node.fieldNames())) {
          JsonNode field = node.get(key);
          String metadataKey = metadataPrefix() + key;
          if (field.isValueNode()) {
            msg.addMessageHeader(metadataKey, field.asText());
          } else {
            msg.addMessageHeader(metadataKey, field.toString());
          }
        }
      } else {
        log.warn("Message did not start with {}, nothing to do", JsonToken.START_OBJECT.asString());
      }
    }
    catch (JsonParseException e) {
      log.warn("Message not JSON [{}]", e.getMessage());
    } catch (Exception e) {
      throw ExceptionHelper.wrapServiceException(e);
    }
  }

  @Override
  public void prepare() throws CoreException {}

  @Override
  protected void closeService() {}

  @Override
  protected void initService() throws CoreException {}

  public String getMetadataPrefix() {
    return metadataPrefix;
  }

  /**
   * Specify a metadata prefix if you need one.
   * 
   * @param prefix the prefix, defaults to the empty string.
   */
  public void setMetadataPrefix(String prefix) {
    this.metadataPrefix = prefix;
  }

  String metadataPrefix() {
    return StringUtils.defaultIfEmpty(getMetadataPrefix(), "");
  }

  private Iterable<String> makeIterable(final Iterator<String> itr) {
    return new Iterable<String>() {
      @Override
      public Iterator<String> iterator() {
        return itr;
      }
    };
  }

}
