package io.zino.mystore.storageEngine;

import java.io.Serializable;

import org.json.JSONObject;

import io.zino.mystore.clusterEngine.ClusterEngine;

public class StorageEntry implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long version;
	private String key;
	private String data;
	private String nodeId;
	
	//use two below for memory hir.
	private long lastAccess=0;
	private long touchCount=1;

	private void touch(){
		this.touchCount++;
		this.lastAccess = System.currentTimeMillis();
	}
	
	public StorageEntry(String key, String data) {
		this(0l, key, data);
	}

	public StorageEntry(long version, String key, String data) {
		this(version, ClusterEngine.NODE_ID, key, data);
	}

	public StorageEntry(long version, String nodeUid, String key, String data) {
		this(version, nodeUid, key, data, 0, 1);	
	}
	
	public StorageEntry(long version, String nodeUid, String key, String data, long lastAccess, long touchCount) {
		this.version = version;
		this.data = data;
		this.nodeId = nodeUid;
		this.key = key;
		this.lastAccess = lastAccess;
		this.touchCount = touchCount;
	}

	public long getVersion() {
		return version;
	}

	public String getData() {
		this.touch();
		return data;
	}

	public StorageEntry updateData(String data) {
		this.touch();
		this.version++;
		this.data = data;
		return this;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getNodeId() {
		return nodeId;
	}
	
	public long getLastAccess() {
		return lastAccess;
	}

	public long getTouchCount() {
		return touchCount;
	}

	@Override
	protected StorageEntry clone() {
		 StorageEntry clone = new StorageEntry(this.version, this.getNodeId(), this.getKey(), this.getData());
		 clone.touchCount = this.touchCount;
		 clone.lastAccess = this.lastAccess;
		 return clone;
	}
	
	@Override
	public String toString() {
		return new JSONObject()
				.put("version", this.version)
                .put("key", this.key)
                .put("data", this.data)
                .put("nodeId", this.nodeId)
                .put("lastAccess", this.lastAccess)
                .put("touchCount", this.touchCount)
                .toString();
	}
}
