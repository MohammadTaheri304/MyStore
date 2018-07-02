package io.zino.mystore.clusterEngine;

import java.io.Serializable;
import java.security.Key;

import io.zino.mystore.storageEngine.StorageEntry;

// TODO: Auto-generated Javadoc
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

	/**
	 * Gets the public key.
	 *
	 * @return the public key
	 */
	public Key getPublicKey() {
		return publicKey;
	}

	/**
	 * Sets the public key.
	 *
	 * @param publicKey the new public key
	 */
	public void setPublicKey(Key publicKey) {
		this.publicKey = publicKey;
	}
	
	/**
	 * Instantiates a new cluster request.
	 *
	 * @param publicKey the public key
	 */
	public ClusterRequest(Key publicKey) {
		super();
		this.requestType = RequestType.NODE_REGISTER;
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
}
