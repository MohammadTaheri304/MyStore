package io.zino.mystore.storageEngine;

import org.json.JSONObject;

// TODO: Auto-generated Javadoc
/**
 * The Class QueryResult.
 */
public class QueryResult {

	/** The key. */
	private String key;
	
	/** The value. */
	private String value;
	
	/** The status. */
	private String status; //status code
		
	/**
	 * Instantiates a new query result.
	 *
	 * @param key the key
	 * @param value the value
	 * @param status the status
	 */
	public QueryResult(String key, String value, String status) {
		super();
		this.key = key;
		this.value = value;
		this.status = status;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new JSONObject()
                .put("entry", new JSONObject()
                		.put("key", key)
                		.put("value", value)
                		)
                .put("status", status)
                .toString();
	}
}
