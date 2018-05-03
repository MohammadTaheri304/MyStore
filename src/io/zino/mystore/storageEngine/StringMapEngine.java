package io.zino.mystore.storageEngine;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StringMapEngine {

	private static StringMapEngine instance = new StringMapEngine();
	private Map<String, String> data;
	
	public static StringMapEngine getInstance(){
		return instance;
	}
	
	private StringMapEngine(){
		data = new ConcurrentHashMap<String, String>();
		
		System.out.println("StringMapEngine Started! "+System.currentTimeMillis());
	}
	
	public boolean insert(String key, String value){
		if(!this.data.containsKey(key)){
			this.data.put(key, value);
			return true;
		}
		return false;
	}

	public boolean update(String key, String value){
		if(this.data.containsKey(key)){
			this.data.replace(key, value);
			return true;
		}
		return false;
	}
	
	public String get(String key){
		return this.data.get(key);
	}
	
	public boolean exist(String key){
		if(this.data.containsKey(key)){
			return true;
		}
		return false;
	}
	
	public boolean delete(String key){
		if(this.data.containsKey(key)){
			this.data.remove(key);
			return true;
		}
		return false;
	}
}
