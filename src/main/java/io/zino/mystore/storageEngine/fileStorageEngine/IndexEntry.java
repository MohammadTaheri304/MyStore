package io.zino.mystore.storageEngine.fileStorageEngine;

import org.json.JSONObject;

public class IndexEntry {

	long count ;
	long value ;
	long head ; 
	
	public IndexEntry(long count, long value, long head) {
		super();
		this.count = count;
		this.value = value;
		this.head = head;
	}
	
	@Override
	public String toString() {
		return new JSONObject()
				.put("count", this.count)
                .put("value", this.value)
                .put("head", this.head)
                .toString();
	}
}
