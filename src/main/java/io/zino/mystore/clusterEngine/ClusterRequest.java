package io.zino.mystore.clusterEngine;

import java.io.Serializable;
import java.security.Key;

import com.google.gson.Gson;

import io.zino.mystore.storageEngine.StorageEntry;

/**
 * The Class ClusterRequest.
 */
public class ClusterRequest implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 2L;

	/**
	 * The Enum RequestType.
	 */
	public enum RequestType {
		
		/** The node des register. */
		NODE_DES_REGISTER,
		/** The node register. */
		NODE_REGISTER,
		/** The update. */
		UPDATE,
		/** The add. */
		ADD,
		/** The delete. */
		DELETE,
		/** The get. */
		GET;
	}

	/** The public key. */
	private Key publicKey;
	
	/** The des key. */
	private byte[] desKey;
	
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
	 * @param requestType
	 *            the new request type
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
	 * @param storageEntry
	 *            the new storage entry
	 */
	public void setStorageEntry(StorageEntry storageEntry) {
		this.version = storageEntry.getVersion();
		this.key = storageEntry.getKey();
		this.data = storageEntry.getData();
		this.nodeId = storageEntry.getNodeId();
	}

	public Key getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(Key publicKey) {
		this.publicKey = publicKey;
	}

	public byte[] getDesKey() {
		return desKey;
	}

	public void setDesKey(byte[] desKey) {
		this.desKey = desKey;
	}

	/**
	 * Instantiates a new cluster request.
	 *
	 * @param publicKey the public key
	 * @param requestType the request type
	 */
	public ClusterRequest(Key publicKey, RequestType requestType) {
		super();
		this.requestType = requestType;
		this.publicKey = publicKey;
	}
	

	/**
	 * Instantiates a new cluster request.
	 *
	 * @param requestType
	 *            the request type
	 */
	public ClusterRequest(RequestType requestType) {
		super();
		this.requestType = requestType;
	}
	
	/**
	 * Instantiates a new cluster request.
	 *
	 * @param requestType
	 *            the request type
	 * @param storageEntry
	 *            the storage entry
	 */
	public ClusterRequest(RequestType requestType, StorageEntry storageEntry) {
		super();
		this.requestType = requestType;
		this.setStorageEntry(storageEntry);
	}
	
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
