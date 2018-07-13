package io.zino.mystore.storageEngine;

import org.json.JSONObject;

/**
 * The Class QueryResult.
 */
public class QueryResult {
	
	/**
	 * The Enum QueryResultStatus.
	 */
	public enum QueryResultStatus{
		
		/** The insert true. */
		INSERT_TRUE(true),
		
		/** The insert false. */
		INSERT_FALSE(false),
		
		/** The update true. */
		UPDATE_TRUE(true),
		
		/** The update false. */
		UPDATE_FALSE(false),
		
		/** The get true. */
		GET_TRUE(true),
		
		/** The get false. */
		GET_FALSE(false),
		
		/** The exist true. */
		EXIST_TRUE(true),
		
		/** The exist false. */
		EXIST_FALSE(false),
		
		/** The delete true. */
		DELETE_TRUE(true),
		
		/** The delete false. */
		DELETE_FALSE(false);
		
		/** The succ. */
		public final boolean succ;

		/**
		 * Instantiates a new query result status.
		 *
		 * @param succ the succ
		 */
		private QueryResultStatus(boolean succ) {
			this.succ = succ;
		}
		
	}

	/** The key. */
	private String key;
	
	/** The value. */
	private String value;
	
	/** The status code. */
	private QueryResultStatus status;
		
	/**
	 * Instantiates a new query result.
	 *
	 * @param key the key
	 * @param value the value
	 * @param status the status
	 */
	public QueryResult(String key, String value, QueryResultStatus status) {
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

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public QueryResultStatus getStatus() {
		return status;
	}
	
}
