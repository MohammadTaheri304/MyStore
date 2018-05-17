package io.zino.mystore.storageEngine;

import org.apache.log4j.Logger;

import io.zino.mystore.clusterEngine.ClusterEngine;
import io.zino.mystore.storageEngine.fileStorageEngine.FileStorageEngine;
import io.zino.mystore.storageEngine.memoryStorageEngine.MemoryStorageEngine;

public class StorageEngine extends AbstractStorageEngine{

	final static Logger logger = Logger.getLogger(StorageEngine.class);
	
	private static StorageEngine instance = new StorageEngine();
	private MemoryStorageEngine memoryStorageEngine;
	private FileStorageEngine fileStorageEngine;

	public static StorageEngine getInstance() {
		return instance;
	}

	private StorageEngine() {
		this.memoryStorageEngine = MemoryStorageEngine.getInstance();
		this.fileStorageEngine = FileStorageEngine.getInstance();

		System.out.println("StringMapEngine Started! " + System.currentTimeMillis());
	}

	@Override
	public StorageEntry get(StorageEntry storageEntry) {
		StorageEntry se = memoryStorageEngine.get(storageEntry);
		if (se != null)
			return se;
		return fileStorageEngine.get(storageEntry);
	}

	@Override
	public StorageEntry insert(StorageEntry storageEntry) {
		boolean containsKey = this.containsKey(storageEntry);
		if(containsKey) return null;
		return memoryStorageEngine.insert(storageEntry);
	}

	@Override
	public StorageEntry update(StorageEntry storageEntry) {
		StorageEntry se = memoryStorageEngine.update(storageEntry);
		if (se != null)
			return se;
		return fileStorageEngine.update(storageEntry);
	}

	@Override
	public StorageEntry delete(StorageEntry storageEntry) {
		StorageEntry se = memoryStorageEngine.delete(storageEntry);
		if (se != null)
			return se;
		return fileStorageEngine.delete(storageEntry);
	}
	
	@Override
	public boolean containsKey(StorageEntry key) {
		boolean se = memoryStorageEngine.containsKey(key);
		if (se)
			return se;
		return fileStorageEngine.containsKey(key);
	}

	public QueryResult insert(String key, String value) {
		StorageEntry storageEntry = new StorageEntry(key, value);
		StorageEntry se = this.insert(storageEntry);
		if (se != null) {
			ClusterEngine.getInstance().addRequest(storageEntry);
			return new QueryResult(null, null, "INSERT_TRUE");
		}
		return new QueryResult(null, null, "INSERT_FALSE");
	}

	public QueryResult update(String key, String value) {
		StorageEntry storageEntry = new StorageEntry(key, value);
		StorageEntry se = this.get(storageEntry);
		if (se != null) {
			if (se.getNodeId().equals(ClusterEngine.NODE_ID)) {
				StorageEntry updateres = this.update(storageEntry);
				if (updateres != null)
					return new QueryResult(se.getKey(), se.getData(), "UPDATE_TRUE");
				else
					return new QueryResult(null, null, "UPDATE_FALSE");
			} else {
				StorageEntry clone = se.clone();
				clone.updateData(value);
				StorageEntry updateRequest = ClusterEngine.getInstance().updateRequest(clone);
				if (updateRequest != null) {
					this.update(updateRequest);
					return new QueryResult(updateRequest.getKey(), updateRequest.getData(), "UPDATE_TRUE");
				} else {
					this.delete(clone);
					return new QueryResult(null, null, "UPDATE_FALSE");
				}
			}
		} else
			return new QueryResult(null, null, "UPDATE_FALSE");
	}

	public QueryResult get(String key) {
		StorageEntry storageEntry = new StorageEntry(key, null);
		StorageEntry se = this.get(storageEntry);
		if (se == null)
			return new QueryResult(null, null, "GET_FALSE");
		else if (se.getNodeId().equals(ClusterEngine.NODE_ID))
			return new QueryResult(se.getKey(), se.getData(), "GET_TRUE");
		else {
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
		else if (se.getNodeId().equals(ClusterEngine.NODE_ID))
			return new QueryResult(null, null, "EXIST_TRUE");
		else {
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
		if (se == null)
			return new QueryResult(null, null, "DELETE_FALSE");
		else {
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
	}

}
