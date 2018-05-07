package io.zino.mystore.clusterEngine;

import java.io.Serializable;

import io.zino.mystore.storageEngine.StorageEntry;

public class ClusterRequest implements Serializable{
	public enum RequestType{
		UPDATE, ADD, DELETE, GET;
	}
	
	private RequestType requestType;
	
	private long version;
	private String key;
	private String data;
	private String nodeId;
	
	public RequestType getRequestType() {
		return requestType;
	}
	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}
	public StorageEntry getStorageEntry() {
		return new StorageEntry(version, nodeId, key, data);
	}
	public void setStorageEntry(StorageEntry storageEntry) {
		this.version = storageEntry.getVersion();
		this.key = storageEntry.getKey();
		this.data = storageEntry.getData();
		this.nodeId = storageEntry.getNodeId();
	}
	public ClusterRequest(RequestType requestType, StorageEntry storageEntry) {
		super();
		this.requestType = requestType;
		this.setStorageEntry(storageEntry);
	}
}
