package io.zino.mystore.storageEngine.memoryStorageEngine;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import io.zino.mystore.ConfigMgr;
import io.zino.mystore.storageEngine.StorageEntry;
import io.zino.mystore.storageEngine.fileStorageEngine.FileStorageEngine;

public class MemoryStorageEngine extends Thread {

	private static MemoryStorageEngine instance = new MemoryStorageEngine();
	private Map<String, StorageEntry> data;

	public static MemoryStorageEngine getInstance() {
		return instance;
	}

	private MemoryStorageEngine() {
		data = new ConcurrentHashMap<String, StorageEntry>();
		this.start();

		System.out.println("MemoryStorageEngine Started! " + System.currentTimeMillis());
	}

	@Override
	public void run() {
		int configSize = Integer.parseInt(ConfigMgr.getInstance().get("MemoryStorageEngine.size"));
		long sleepDuration = 2500;
		while (true) {
			try {
				this.sleep(sleepDuration);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			int dataSize = this.data.size();
			if (dataSize > (configSize * (0.8))) {
				sleepDuration *= (0.8);

				double sumOfages = 0;
				for (Entry<String, StorageEntry> entry : this.data.entrySet()) {
					double aging = evalAging(entry);
					sumOfages += aging;
				}
				double agingThresh = sumOfages/(this.data.size());
				for (Entry<String, StorageEntry> entry : this.data.entrySet()) {
					double aging = evalAging(entry);
					if(aging<=agingThresh){
						this.data.remove(entry.getKey());
						this.downgarde(entry.getValue());
					}
				}
				
			} else {
				sleepDuration *= (1.2);
			}
		}
	}

	private double evalAging(Entry<String, StorageEntry> entry) {
		long idealTime = System.currentTimeMillis() - entry.getValue().getLastAccess();
		double aging = entry.getValue().getTouchCount()/(idealTime+1);
		return aging;
	}

	private void downgarde(StorageEntry storageEntry){
		FileStorageEngine.getInstance().insert(storageEntry);
		System.out.println("###");
	}
	
	public boolean containsKey(String key) {
		return this.data.containsKey(key);
	}

	public StorageEntry get(StorageEntry storageEntry) {
		if (this.data.containsKey(storageEntry.getKey())) {
			return this.data.get(storageEntry.getKey());
		}
		return null;
	}

	public StorageEntry insert(StorageEntry storageEntry) {
		if (!this.data.containsKey(storageEntry.getKey())) {
			this.data.put(storageEntry.getKey(), storageEntry);
			return storageEntry;
		}
		return null;
	}

	public StorageEntry update(StorageEntry storageEntry) {
		if (this.data.containsKey(storageEntry.getKey())) {
			return this.data.get(storageEntry.getKey()).updateData(storageEntry.getData());
		}
		return null;
	}

	public StorageEntry delete(StorageEntry storageEntry) {
		if (this.data.containsKey(storageEntry.getKey())) {
			return this.data.remove(storageEntry.getKey());
		}
		return null;
	}
}
