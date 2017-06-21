package com.adaptris.core.services.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import com.adaptris.core.AdaptrisMessage;
import com.adaptris.core.AdaptrisMessageFactory;
import com.adaptris.core.json.jdbc.JsonResultSetTranslator;
import com.adaptris.jdbc.JdbcResult;
import com.adaptris.jdbc.JdbcResultRow;
import com.adaptris.jdbc.JdbcResultSet;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.spi.json.JsonSmartJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

/**
 * Tests for JDBC to JSON translator {@link JsonResultSetTranslator}.
 *
 * @author Ashley Anderson <ashley.anderson@reedbusiness.com>
 */
@SuppressWarnings("deprecation")
public class JsonResultSetTranslatorTest {

	private JSONObject expected;

	private final JdbcResult result = new JdbcResult();

	/**
	 * Initialise the unit test environment.
	 *
	 * @throws Exception
	 *           If initialisation could not occur.
	 */
	@Before
	public void setUp() throws Exception {
		expected = new JSONObject(
				"{\"result\":[{\"firstName\":\"John\",\"lastName\":\"Doe\"},{\"firstName\":\"Anna\",\"lastName\":\"Smith\"},{\"firstName\":\"Peter\",\"lastName\":\"Jones\"}]}");

		final List<String> fieldNames = Arrays.asList("firstName", "lastName");

		final JdbcResultRow row_1 = new JdbcResultRow();
		row_1.setFieldNames(fieldNames);
		row_1.setFieldValues(Arrays.asList((Object)"John", (Object)"Doe"));

		final JdbcResultRow row_2 = new JdbcResultRow();
		row_2.setFieldNames(fieldNames);
		row_2.setFieldValues(Arrays.asList((Object)"Anna", (Object)"Smith"));

		final JdbcResultRow row_3 = new JdbcResultRow();
		row_3.setFieldNames(fieldNames);
		row_3.setFieldValues(Arrays.asList((Object)"Peter", (Object)"Jones"));

		@SuppressWarnings("resource")
		final JdbcResultSet mockResultSet = mock(JdbcResultSet.class);
		when(mockResultSet.getRows()).thenReturn(Arrays.asList(row_1, row_2, row_3));

		result.setResultSets(Arrays.asList(mockResultSet));

	}

	/**
	 * Test the simple, valid path through the translation from a JDBC result to JSON.
	 *
	 * @throws Exception
	 *           Unexpected; should not happen.
	 */
	@Test
  public void testTranslate() throws Exception {
		final JsonResultSetTranslator jsonTranslator = new JsonResultSetTranslator();
		final AdaptrisMessage message = AdaptrisMessageFactory.getDefaultInstance().newMessage();

		jsonTranslator.translate(result, message);
    ReadContext ctx = createContext(message);
    assertNotNull(ctx.read("$.result"));
    assertNotNull(ctx.read("$.result.[0]"));
    assertEquals("Anna", ctx.read("$.result.[1].firstName"));
    assertEquals(expected.toString(), new String(message.getPayload()));
	}

  private ReadContext createContext(AdaptrisMessage msg) throws IOException {
    Configuration jsonConfig = new Configuration.ConfigurationBuilder().jsonProvider(new JsonSmartJsonProvider())
        .mappingProvider(new JacksonMappingProvider()).options(EnumSet.noneOf(Option.class)).build();
    return JsonPath.parse(msg.getInputStream(), jsonConfig);
  }
}
