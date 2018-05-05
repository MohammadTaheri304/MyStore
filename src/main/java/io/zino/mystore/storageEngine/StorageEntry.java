package io.zino.mystore.storageEngine;

import java.io.Serializable;

import io.zino.mystore.clusterEngine.ClusterEngine;

public class StorageEntry implements Serializable {
	private long version;
	private String key;
	private String data;
	private String nodeId;
	
	//use two below for memory hir.
	private long lastAccess;
	private long touchCount;

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
		this.version = version;
		this.data = data;
		this.nodeId = nodeUid;
		this.key = key;
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
}
