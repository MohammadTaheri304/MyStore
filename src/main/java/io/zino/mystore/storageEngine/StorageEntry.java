package io.zino.mystore.storageEngine;

import java.io.Serializable;

import io.zino.mystore.clusterEngine.ClusterEngine;

public class StorageEntry implements Serializable {
	private long version;
	private String key;
	private String data;
	private String nodeId;

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
		return data;
	}

	public StorageEntry updateData(String data) {
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
	
	@Override
	protected StorageEntry clone() {
		return new StorageEntry(this.version, this.getNodeId(), this.getKey(), this.getData());
	}
}
