package io.zino.mystore.storageEngine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.zino.mystore.clusterEngine.ClusterEngine;
import io.zino.mystore.storageEngine.fileStorageEngine.FileStorageEngine;
import io.zino.mystore.storageEngine.memoryStorageEngine.MemoryStorageEngine;

public class StorageEngine {

	private static StorageEngine instance = new StorageEngine();
	private Map<String, StorageEntry> data;
	private MemoryStorageEngine memoryStorageEngine = MemoryStorageEngine.getInstance();
	private FileStorageEngine fileStorageEngine = FileStorageEngine.getInstance();
	
	public static StorageEngine getInstance() {
		return instance;
	}

	private StorageEngine() {
		data = new ConcurrentHashMap<String, StorageEntry>();

		System.out.println("StringMapEngine Started! " + System.currentTimeMillis());
	}

	public StorageEntry get(StorageEntry storageEntry) {
		StorageEntry se = memoryStorageEngine.get(storageEntry);
		if(se!=null){
			return se;
		}else{
			return fileStorageEngine.get(storageEntry);
		}
	}

	public StorageEntry insert(StorageEntry storageEntry) {
		return memoryStorageEngine.insert(storageEntry);
	}

	public StorageEntry update(StorageEntry storageEntry) {
		StorageEntry se = memoryStorageEngine.update(storageEntry);
		if(se!=null){
			return se;
		}else{
			return fileStorageEngine.update(storageEntry);
		}
	}

	public StorageEntry delete(StorageEntry storageEntry) {
		StorageEntry se = memoryStorageEngine.delete(storageEntry);
		if(se!=null){
			return se;
		}else{
			return fileStorageEngine.delete(storageEntry);
		}
	}

	public QueryResult insert(String key, String value) {
		StorageEntry storageEntry = new StorageEntry(key, value);
		StorageEntry se = this.insert(storageEntry);
		if (se != null) {
			ClusterEngine.getInstance().addRequest(storageEntry);
			return new QueryResult(null, null, "INSERT_TRUE");
		} else {
			return new QueryResult(null, null, "INSERT_FALSE");
		}
	}

	public QueryResult update(String key, String value) {
		StorageEntry storageEntry = new StorageEntry(key, value);
		StorageEntry se = this.get(storageEntry);
		if (se != null) {
			if (se.getNodeId().equals(ClusterEngine.NODE_ID)) {
				this.update(storageEntry);
				return new QueryResult(se.getKey(), se.getData(), "UPDATE_TRUE");
			} else {
				StorageEntry clone = se.clone();
				clone.updateData(value);
				StorageEntry updateRequest = ClusterEngine.getInstance().updateRequest(clone);
				if (updateRequest != null) {
					this.update(updateRequest);
					return new QueryResult(updateRequest.getKey(), updateRequest.getData(), "GET_TRUE");
				} else {
					this.delete(clone);
					return new QueryResult(null, null, "UPDATE_FALSE");
				}
			}
		}
		return new QueryResult(null, null, "UPDATE_FALSE");
	}

	public QueryResult get(String key) {
		StorageEntry storageEntry = new StorageEntry(key, null);
		StorageEntry se = this.get(storageEntry);
		if (se == null)
			return new QueryResult(null, null, "GET_FALSE");
		if (se.getNodeId().equals(ClusterEngine.NODE_ID)) {
			return new QueryResult(se.getKey(), se.getData(), "GET_TRUE");
		} else {
			StorageEntry clone = se.clone();
			StorageEntry getRequest = ClusterEngine.getInstance().getRequest(clone);
			if (getRequest != null) {
				this.update(getRequest);
				return new QueryResult(getRequest.getKey(), getRequest.getData(), "GET_TRUE");
			} else {
				this.delete(clone);
				return new QueryResult(null, null, "GET_FALSE");
			}
		}
	}

	public QueryResult exist(String key) {
		StorageEntry storageEntry = new StorageEntry(key, null);
		StorageEntry se = this.get(storageEntry);
		if (se == null)
			return new QueryResult(null, null, "EXIST_FALSE");
		if (se.getNodeId().equals(ClusterEngine.NODE_ID)) {
			return new QueryResult(null, null, "EXIST_TRUE");
		} else {
			StorageEntry clone = se.clone();
			StorageEntry getRequest = ClusterEngine.getInstance().getRequest(clone);
			if (getRequest != null) {
				this.update(getRequest);
				return new QueryResult(null, null, "EXIST_TRUE");
			} else {
				this.delete(clone);
				return new QueryResult(null, null, "EXIST_FALSE");
			}
		}	
	}

	public QueryResult delete(String key) {
		StorageEntry storageEntry = new StorageEntry(key, null);
		StorageEntry se = this.get(storageEntry);
		if (se!=null) {
			if (se.getNodeId().equals(ClusterEngine.NODE_ID)) {
				this.delete(se);
				return new QueryResult(null, null, "DELETE_TRUE");
			} else {
				StorageEntry clone = se.clone();
				ClusterEngine.getInstance().deleteRequest(clone);
				this.delete(clone);
				return new QueryResult(null, null, "DELETE_TRUE");
			}		
		}
		return new QueryResult(null, null, "DELETE_FALSE");
	}
}
