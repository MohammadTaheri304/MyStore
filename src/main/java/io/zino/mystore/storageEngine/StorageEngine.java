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
	
	public boolean insert(String key, String value){
		if(!this.data.containsKey(key)){
			StorageEntry storageEntry = new StorageEntry(key, value);
			this.data.put(key, storageEntry);
			ClusterEngine.getInstance().addRequest(storageEntry);
			return true;
		}
		return false;
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

	public boolean update(String key, String value){
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
			return true;
		}
		return false;
	}
	
	public String get(String key){
		StorageEntry entry = this.data.get(key);
		if(entry==null) return null;
		if(entry.getNodeId().equals(ClusterEngine.NODE_ID)){
			return entry.getData();
		}else{
			StorageEntry clone = entry.clone();
			StorageEntry getRequest = ClusterEngine.getInstance().getRequest(clone);
			this.data.replace(getRequest.getKey(), getRequest);
			return getRequest.getData();
		}
	}
	
	public boolean exist(String key){
		StorageEntry entry = this.data.get(key);
		if(entry.getNodeId().equals(ClusterEngine.NODE_ID)){
			return true;
		}else{
			StorageEntry clone = entry.clone();
			StorageEntry getRequest = ClusterEngine.getInstance().getRequest(clone);
			if(getRequest.getKey()!=null){
				this.data.replace(getRequest.getKey(), getRequest);
				return true;
			}
		}
		return false;
	}
	
	public boolean delete(String key){		
		if(this.data.containsKey(key)){
			StorageEntry entry = this.data.get(key);
			if(entry.getNodeId().equals(ClusterEngine.NODE_ID)){
				this.data.remove(key);
			}else{
				StorageEntry clone = entry.clone();
				ClusterEngine.getInstance().deleteRequest(clone);
				this.data.remove(key);
			}
			return true;
		}
		return false;
	}
}
