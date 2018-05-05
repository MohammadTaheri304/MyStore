package io.zino.mystore.storageEngine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FileStorageEngine {

	private static FileStorageEngine instance = new FileStorageEngine();
	private Map<String, StorageEntry> data;
	
	public static FileStorageEngine getInstance(){
		return instance;
	}
	
	private FileStorageEngine(){
		data = new ConcurrentHashMap<String, StorageEntry>();
		
		System.out.println("FileStorageEngine Started! "+System.currentTimeMillis());
	}
	
	public boolean containsKey(String key){
		return this.data.containsKey(key);
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
}
