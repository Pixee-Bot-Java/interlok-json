package com.adaptris.core.json.jdbc;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.adaptris.core.AdaptrisComponent;
import com.adaptris.core.AdaptrisMessage;
import com.adaptris.jdbc.JdbcResult;
import com.adaptris.jdbc.JdbcResultRow;
import com.adaptris.jdbc.JdbcResultSet;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Service to translate a JDBC result set to JSON.
 *
 * @author Ashley Anderson <ashley.anderson@reedbusiness.com>
 */
@XStreamAlias("jdbc-json-payload-translator")
public class JsonResultSetTranslator extends JsonResultSetTranslatorImpl {

	
  @Deprecated
	private String uniqueId;

	/**
	 * Perform JDBC data set to JSON translation.
	 *
	 * {@inheritDoc}.
	 */
	@Override
	public void translate(final JdbcResult source, final AdaptrisMessage target) {
		final JSONObject json = new JSONObject();

		for (final JdbcResultSet result : source.getResultSets()) {

			/* add result set to JSON array */
			final JSONArray jsonArray = new JSONArray();
			for (final JdbcResultRow row : result.getRows()) {

				/* add row data to JSON object */
				final JSONObject jsonRow = new JSONObject();
				for (final String field : row.getFieldNames()) {

					try {
						jsonRow.put(field, row.getFieldValue(field));
					} catch (final JSONException e) {
						log.warn("Could not create JSON from object for field : " + field, e);
					}

				}

				jsonArray.put(jsonRow);
			}

			try {
				json.put("result", jsonArray);
			} catch (final JSONException e) {
				log.error("Could not create JSON result", e);
			}

		}

		target.setPayload(json.toString().getBytes());
	}

  /**
   * Not required as this component doesn't need to extend {@link AdaptrisComponent}
   * 
   * @deprecated since 3.6.3
   */
  @Deprecated
  public String getUniqueId() {
    return uniqueId;
  }

  /**
   * Not required as this component doesn't need to extend {@link AdaptrisComponent}
   * 
   * @deprecated since 3.6.3
   */
  @Deprecated
  public void setUniqueId(String uniqueId) {
    this.uniqueId = uniqueId;
  }
}
