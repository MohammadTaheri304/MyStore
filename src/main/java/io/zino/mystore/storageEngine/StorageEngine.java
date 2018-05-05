package io.zino.mystore.storageEngine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.zino.mystore.clusterEngine.ClusterEngine;

public class StorageEngine {

	private static StorageEngine instance = new StorageEngine();
	private Map<String, StorageEntry> data;
	private MemoryStorageEngine memoryStorageEngine = MemoryStorageEngine.getInstance();

	public static StorageEngine getInstance() {
		return instance;
	}

	private StorageEngine() {
		data = new ConcurrentHashMap<String, StorageEntry>();

		System.out.println("StringMapEngine Started! " + System.currentTimeMillis());
	}

	public StorageEntry get(StorageEntry storageEntry) {
		return memoryStorageEngine.get(storageEntry);
	}

	public StorageEntry insert(StorageEntry storageEntry) {
		return memoryStorageEngine.insert(storageEntry);
	}

	public StorageEntry update(StorageEntry storageEntry) {
		return memoryStorageEngine.update(storageEntry);
	}

	public StorageEntry delete(StorageEntry storageEntry) {
		return memoryStorageEngine.delete(storageEntry);
	}

	public QueryResult insert(String key, String value) {
		StorageEntry storageEntry = new StorageEntry(key, value);
		StorageEntry se = this.memoryStorageEngine.insert(storageEntry);
		if (se != null) {
			ClusterEngine.getInstance().addRequest(storageEntry);
			return new QueryResult(null, null, "INSERT_TRUE");
		} else {
			return new QueryResult(null, null, "INSERT_FALSE");
		}
	}

	public QueryResult update(String key, String value) {
		StorageEntry storageEntry = new StorageEntry(key, value);
		StorageEntry se = this.memoryStorageEngine.get(storageEntry);
		if (se != null) {
			if (se.getNodeId().equals(ClusterEngine.NODE_ID)) {
				this.memoryStorageEngine.update(storageEntry);
				return new QueryResult(se.getKey(), se.getData(), "GET_TRUE");
			} else {
				StorageEntry clone = se.clone();
				clone.updateData(value);
				StorageEntry updateRequest = ClusterEngine.getInstance().updateRequest(clone);
				if (updateRequest != null) {
					this.memoryStorageEngine.update(updateRequest);
					return new QueryResult(updateRequest.getKey(), updateRequest.getData(), "GET_TRUE");
				} else {
					this.memoryStorageEngine.delete(clone);
					return new QueryResult(null, null, "UPDATE_FALSE");
				}
			}
		}
		return new QueryResult(null, null, "UPDATE_FALSE");
	}

	public QueryResult get(String key) {
		StorageEntry storageEntry = new StorageEntry(key, null);
		StorageEntry se = this.memoryStorageEngine.get(storageEntry);
		if (se == null)
			return new QueryResult(null, null, "GET_FALSE");
		if (se.getNodeId().equals(ClusterEngine.NODE_ID)) {
			return new QueryResult(se.getKey(), se.getData(), "GET_TRUE");
		} else {
			StorageEntry clone = se.clone();
			StorageEntry getRequest = ClusterEngine.getInstance().getRequest(clone);
			if (getRequest != null) {
				this.memoryStorageEngine.update(getRequest);
				return new QueryResult(getRequest.getKey(), getRequest.getData(), "GET_TRUE");
			} else {
				this.memoryStorageEngine.delete(clone);
				return new QueryResult(null, null, "GET_FALSE");
			}
		}
	}

	public QueryResult exist(String key) {
		StorageEntry storageEntry = new StorageEntry(key, null);
		StorageEntry se = this.memoryStorageEngine.get(storageEntry);
		if (se == null)
			return new QueryResult(null, null, "EXIST_FALSE");
		if (se.getNodeId().equals(ClusterEngine.NODE_ID)) {
			return new QueryResult(null, null, "EXIST_TRUE");
		} else {
			StorageEntry clone = se.clone();
			StorageEntry getRequest = ClusterEngine.getInstance().getRequest(clone);
			if (getRequest != null) {
				this.memoryStorageEngine.update(getRequest);
				return new QueryResult(null, null, "EXIST_TRUE");
			} else {
				this.memoryStorageEngine.delete(clone);
				return new QueryResult(null, null, "EXIST_FALSE");
			}
		}	
	}

	public QueryResult delete(String key) {
		StorageEntry storageEntry = new StorageEntry(key, null);
		StorageEntry se = this.memoryStorageEngine.get(storageEntry);
		if (se!=null) {
			if (se.getNodeId().equals(ClusterEngine.NODE_ID)) {
				this.memoryStorageEngine.delete(se);
				return new QueryResult(null, null, "DELETE_TRUE");
			} else {
				StorageEntry clone = se.clone();
				ClusterEngine.getInstance().deleteRequest(clone);
				this.memoryStorageEngine.delete(clone);
				return new QueryResult(null, null, "DELETE_TRUE");
			}		
		}
		return new QueryResult(null, null, "DELETE_FALSE");
	}
}
