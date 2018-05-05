package io.zino.mystore.storageEngine;

import org.json.JSONObject;

public class QueryResult {

	private String key;
	private String value;
	private String status; //status code
		
	public QueryResult(String key, String value, String status) {
		super();
		this.key = key;
		this.value = value;
		this.status = status;
	}



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
