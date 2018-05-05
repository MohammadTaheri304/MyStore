package io.zino.mystore.storageEngine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.zino.mystore.clusterEngine.ClusterEngine;

public class StorageEngine {

	private static StorageEngine instance = new StorageEngine();
	private Map<String, StorageEntry> data;
	
	public static StorageEngine getInstance(){
		return instance;
	}
	
	private StorageEngine(){
		data = new ConcurrentHashMap<String, StorageEntry>();
		
		System.out.println("StringMapEngine Started! "+System.currentTimeMillis());
	}
	
	public QueryResult insert(String key, String value){
		if(!this.data.containsKey(key)){
			StorageEntry storageEntry = new StorageEntry(key, value);
			this.data.put(key, storageEntry);
			ClusterEngine.getInstance().addRequest(storageEntry);
			return new QueryResult(null, null, "INSERT_TRUE");
		}
		return new QueryResult(null, null, "INSERT_FALSE");
	}
	
	public StorageEntry get(StorageEntry storageEntry){
		if(this.data.containsKey(storageEntry.getKey())){
			return this.data.get(storageEntry.getKey());
		}
		return null;
	}
	
	public StorageEntry insert(StorageEntry storageEntry){
		if(!this.data.containsKey(storageEntry.getKey())){
			this.data.put(storageEntry.getKey(), storageEntry);
			return storageEntry;
		}
		return null;
	}
	
	public StorageEntry update(StorageEntry storageEntry){
		if(this.data.containsKey(storageEntry.getKey())){
			return this.data.get(storageEntry.getKey()).updateData(storageEntry.getData());
		}
		return null;
	}
	
	public StorageEntry delete(StorageEntry storageEntry){
		if(this.data.containsKey(storageEntry.getKey())){
			return this.data.remove(storageEntry.getKey());
		}
		return null;
	}

	public QueryResult update(String key, String value){
		if(this.data.containsKey(key)){
			StorageEntry entry = this.data.get(key);
			if(entry.getNodeId().equals(ClusterEngine.NODE_ID)){
				entry.updateData(value);
			}else{
				StorageEntry clone = entry.clone();
				clone.updateData(value);
				StorageEntry updateRequest = ClusterEngine.getInstance().updateRequest(clone);
				this.data.replace(updateRequest.getKey(), updateRequest);
			}
			return new QueryResult(null, null, "UPDATE_TRUE");
		}
		return new QueryResult(null, null, "UPDATE_FALSE");
	}
	
	public QueryResult get(String key){
		StorageEntry entry = this.data.get(key);
		if(entry==null) return null;
		if(entry.getNodeId().equals(ClusterEngine.NODE_ID)){
			return new QueryResult(key, entry.getData(), "GET_TRUE");
		}else{
			StorageEntry clone = entry.clone();
			StorageEntry getRequest = ClusterEngine.getInstance().getRequest(clone);
			this.data.replace(getRequest.getKey(), getRequest);
			return new QueryResult(key, getRequest.getData(), "GET_TRUE");
		}
	}
	
	public QueryResult exist(String key){
		StorageEntry entry = this.data.get(key);
		if(entry.getNodeId().equals(ClusterEngine.NODE_ID)){
			return new QueryResult(null, null, "EXIST_TRUE");
		}else{
			StorageEntry clone = entry.clone();
			StorageEntry getRequest = ClusterEngine.getInstance().getRequest(clone);
			if(getRequest.getKey()!=null){
				this.data.replace(getRequest.getKey(), getRequest);
				return new QueryResult(null, null, "EXIST_TRUE");
			}
		}
		return new QueryResult(null, null, "EXIST_FALSE");
	}
	
	public QueryResult delete(String key){		
		if(this.data.containsKey(key)){
			StorageEntry entry = this.data.get(key);
			if(entry.getNodeId().equals(ClusterEngine.NODE_ID)){
				this.data.remove(key);
			}else{
				StorageEntry clone = entry.clone();
				ClusterEngine.getInstance().deleteRequest(clone);
				this.data.remove(key);
			}
			return new QueryResult(null, null, "DELETE_TRUE");
		}
		return new QueryResult(null, null, "DELETE_FALSE");
	}
}
