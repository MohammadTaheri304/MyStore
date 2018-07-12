package io.zino.mystore.storageEngine;

import java.io.Serializable;

import org.json.JSONObject;

import com.google.gson.Gson;

import io.zino.mystore.clusterEngine.ClusterEngine;

// TODO: Auto-generated Javadoc
/**
 * The Class StorageEntry.
 */
public class StorageEntry implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The version. */
	private final long version;
	
	/** The key. */
	private final String key;
	
	/** The data. */
	private final String data;
	
	/** The node id. */
	private final String nodeId;
	
	/** The last access. */
	//use two below for memory hir.
	private long lastAccess=0;
	
	/** The touch count. */
	private long touchCount=1;

	/**
	 * Touch.
	 */
	private void touch(){
		this.touchCount++;
		this.lastAccess = System.currentTimeMillis();
	}
	
	/**
	 * Instantiates a new storage entry.
	 *
	 * @param key the key
	 * @param data the data
	 */
	public StorageEntry(String key, String data) {
		this(0l, key, data);
	}

	/**
	 * Instantiates a new storage entry.
	 *
	 * @param version the version
	 * @param key the key
	 * @param data the data
	 */
	public StorageEntry(long version, String key, String data) {
		this(version, ClusterEngine.NODE_ID, key, data);
	}

	/**
	 * Instantiates a new storage entry.
	 *
	 * @param version the version
	 * @param nodeUid the node uid
	 * @param key the key
	 * @param data the data
	 */
	public StorageEntry(long version, String nodeUid, String key, String data) {
		this(version, nodeUid, key, data, 0, 1);	
	}
	
	/**
	 * Instantiates a new storage entry.
	 *
	 * @param version the version
	 * @param nodeUid the node uid
	 * @param key the key
	 * @param data the data
	 * @param lastAccess the last access
	 * @param touchCount the touch count
	 */
	public StorageEntry(long version, String nodeUid, String key, String data, long lastAccess, long touchCount) {
		this.version = version;
		this.data = data;
		this.nodeId = nodeUid;
		this.key = key;
		this.lastAccess = lastAccess;
		this.touchCount = touchCount;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public long getVersion() {
		return version;
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public String getData() {
		this.touch();
		return data;
	}

	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Gets the node id.
	 *
	 * @return the node id
	 */
	public String getNodeId() {
		return nodeId;
	}
	
	/**
	 * Gets the last access.
	 *
	 * @return the last access
	 */
	public long getLastAccess() {
		return lastAccess;
	}

	/**
	 * Gets the touch count.
	 *
	 * @return the touch count
	 */
	public long getTouchCount() {
		return touchCount;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public StorageEntry clone() {
		 return cloneWithNewKeyAndNewData(this.getKey(), this.getData());
	}
	
	/**
	 * Clone with new data.
	 *
	 * @param data the data
	 * @return the storage entry
	 */
	public StorageEntry cloneWithNewData(String data) {
		 return cloneWithNewKeyAndNewData(this.getKey(), data);
	}
	
	public StorageEntry cloneWithNewKeyAndNewData(String key ,String data) {
		 StorageEntry clone = new StorageEntry(this.version, this.getNodeId(), key, data);
		 clone.touchCount = this.touchCount;
		 clone.lastAccess = this.lastAccess;
		 return clone;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
