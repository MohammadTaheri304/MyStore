package io.zino.mystore.clusterEngine;

import java.io.Serializable;

import io.zino.mystore.storageEngine.StorageEntry;

/**
 * The Class ClusterRequest.
 */
public class ClusterRequest implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The Enum RequestType.
	 */
	public enum RequestType{
		
		/** The update. */
		UPDATE, 
 /** The add. */
 ADD, 
 /** The delete. */
 DELETE, 
 /** The get. */
 GET;
	}
	
	/** The request type. */
	private RequestType requestType;
	
	/** The version. */
	private long version;
	
	/** The key. */
	private String key;
	
	/** The data. */
	private String data;
	
	/** The node id. */
	private String nodeId;
	
	/**
	 * Gets the request type.
	 *
	 * @return the request type
	 */
	public RequestType getRequestType() {
		return requestType;
	}
	
	/**
	 * Sets the request type.
	 *
	 * @param requestType the new request type
	 */
	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}
	
	/**
	 * Gets the storage entry.
	 *
	 * @return the storage entry
	 */
	public StorageEntry getStorageEntry() {
		return new StorageEntry(version, nodeId, key, data);
	}
	
	/**
	 * Sets the storage entry.
	 *
	 * @param storageEntry the new storage entry
	 */
	public void setStorageEntry(StorageEntry storageEntry) {
		this.version = storageEntry.getVersion();
		this.key = storageEntry.getKey();
		this.data = storageEntry.getData();
		this.nodeId = storageEntry.getNodeId();
	}
	
	/**
	 * Instantiates a new cluster request.
	 *
	 * @param requestType the request type
	 * @param storageEntry the storage entry
	 */
	public ClusterRequest(RequestType requestType, StorageEntry storageEntry) {
		super();
		this.requestType = requestType;
		this.setStorageEntry(storageEntry);
	}
}
