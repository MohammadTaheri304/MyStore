package io.zino.mystore.clusterEngine;

import java.io.Serializable;

import io.zino.mystore.storageEngine.StorageEntry;

public class ClusterRequest implements Serializable{
	public enum RequestType{
		UPDATE, ADD, DELETE, GET;
	}
	
	private RequestType requestType;
	private StorageEntry storageEntry;
	public RequestType getRequestType() {
		return requestType;
	}
	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}
	public StorageEntry getStorageEntry() {
		return storageEntry;
	}
	public void setStorageEntry(StorageEntry storageEntry) {
		this.storageEntry = storageEntry;
	}
	public ClusterRequest(RequestType requestType, StorageEntry storageEntry) {
		super();
		this.requestType = requestType;
		this.storageEntry = storageEntry;
	}
}
