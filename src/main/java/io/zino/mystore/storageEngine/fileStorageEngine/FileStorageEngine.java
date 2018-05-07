package io.zino.mystore.storageEngine.fileStorageEngine;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import io.zino.mystore.ConfigMgr;
import io.zino.mystore.storageEngine.StorageEntry;
import io.zino.mystore.storageEngine.memoryStorageEngine.MemoryStorageEngine;

public class FileStorageEngine extends Thread {

	private static FileStorageEngine instance = new FileStorageEngine();
	private Map<String, StorageEntry> data;
	
	public static FileStorageEngine getInstance() {
		return instance;
	}

	private FileStorageEngine() {
		data = new ConcurrentHashMap<String, StorageEntry>();
		
		this.start();

		System.out.println("FileStorageEngine Started! " + System.currentTimeMillis());
	}

	@Override
	public void run() {
		long sleepDuration = 10000;
		double agingThreshold = Double
				.parseDouble(ConfigMgr.getInstance().get("FileStorageEngine.upgradeAgingThreshold"));
		while (true) {
			try {
				this.sleep(sleepDuration);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			boolean decSleppDuration = false;
			for (Entry<String, StorageEntry> entry : this.data.entrySet()) {
				long idealTime = System.currentTimeMillis() - entry.getValue().getLastAccess();
				double aging = entry.getValue().getTouchCount() / (idealTime+1);
				if (aging >= agingThreshold) {
					this.data.remove(entry.getKey());
					this.upgrade(entry.getValue());
					decSleppDuration = true;
				}
			}
			sleepDuration = (long) (decSleppDuration ? sleepDuration * (0.8) : sleepDuration * (1.2));
		}
	}

	private void upgrade(StorageEntry storageEntry) {
		MemoryStorageEngine.getInstance().insert(storageEntry);
		System.out.println("@@@");
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
