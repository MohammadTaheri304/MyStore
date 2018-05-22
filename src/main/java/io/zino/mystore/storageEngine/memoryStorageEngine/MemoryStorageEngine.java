package io.zino.mystore.storageEngine.memoryStorageEngine;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import io.zino.mystore.ConfigMgr;
import io.zino.mystore.storageEngine.AbstractStorageEngine;
import io.zino.mystore.storageEngine.StorageEntry;
import io.zino.mystore.storageEngine.fileStorageEngine.FileStorageEngine;

/**
 * The Class MemoryStorageEngine.
 */
public class MemoryStorageEngine extends AbstractStorageEngine {

	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(MemoryStorageEngine.class);

	/** The instance. */
	private static MemoryStorageEngine instance = new MemoryStorageEngine();

	/** The data. */
	private Map<String, StorageEntry> data;

	/**
	 * Gets the single instance of MemoryStorageEngine.
	 *
	 * @return single instance of MemoryStorageEngine
	 */
	public static MemoryStorageEngine getInstance() {
		return instance;
	}

	/**
	 * Instantiates a new memory storage engine.
	 */
	private MemoryStorageEngine() {
		data = new ConcurrentHashMap<String, StorageEntry>();
		new Thread(new Runnable() {
			@Override
			public void run() {
				evalFordowngarde();
			}
		}).start();

		System.out.println("MemoryStorageEngine Started! " + System.currentTimeMillis());
	}

	/**
	 * Eval fordowngarde.
	 */
	private void evalFordowngarde() {
		int configSize = Integer.parseInt(ConfigMgr.getInstance().get("MemoryStorageEngine.size"));
		long sleepDuration = 2500;
		long MAX_SLEEP_DURATION = 5 * 60 * 1000;
		while (true) {
			try {
				Thread.sleep(sleepDuration);
			} catch (InterruptedException e) {
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
				double agingThresh = sumOfages / (this.data.size());
				for (Entry<String, StorageEntry> entry : this.data.entrySet()) {
					double aging = evalAging(entry);
					if (aging <= agingThresh) {
						this.data.remove(entry.getKey());
						this.downgarde(entry.getValue());
					}
				}

			} else {
				sleepDuration *= (1.2);
				sleepDuration = (MAX_SLEEP_DURATION < sleepDuration) ? MAX_SLEEP_DURATION : sleepDuration;
			}
		}
	}

	/**
	 * Eval aging.
	 *
	 * @param entry
	 *            the entry
	 * @return the double
	 */
	private double evalAging(Entry<String, StorageEntry> entry) {
		long idealTime = System.currentTimeMillis() - entry.getValue().getLastAccess();
		double aging = entry.getValue().getTouchCount() / (idealTime + 1);
		return aging;
	}

	/**
	 * Downgarde.
	 *
	 * @param storageEntry
	 *            the storage entry
	 */
	private void downgarde(StorageEntry storageEntry) {
		StorageEntry insert = FileStorageEngine.getInstance().insert(storageEntry);
		if (insert == null) {
			logger.error("Insert faild in donwgrading " + storageEntry.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.zino.mystore.storageEngine.AbstractStorageEngine#containsKey(io.zino.
	 * mystore.storageEngine.StorageEntry)
	 */
	@Override
	public boolean containsKey(StorageEntry key) {
		return this.data.containsKey(key.getKey());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.zino.mystore.storageEngine.AbstractStorageEngine#get(io.zino.mystore.
	 * storageEngine.StorageEntry)
	 */
	@Override
	public StorageEntry get(StorageEntry storageEntry) {
		if (this.data.containsKey(storageEntry.getKey())) {
			return this.data.get(storageEntry.getKey());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.zino.mystore.storageEngine.AbstractStorageEngine#insert(io.zino.
	 * mystore.storageEngine.StorageEntry)
	 */
	@Override
	public StorageEntry insert(StorageEntry storageEntry) {
		if (!this.data.containsKey(storageEntry.getKey())) {
			this.data.put(storageEntry.getKey(), storageEntry);
			return storageEntry;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.zino.mystore.storageEngine.AbstractStorageEngine#update(io.zino.
	 * mystore.storageEngine.StorageEntry)
	 */
	@Override
	public StorageEntry update(StorageEntry storageEntry) {
		if (this.data.containsKey(storageEntry.getKey())) {
			return this.data.get(storageEntry.getKey()).updateData(storageEntry.getData());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.zino.mystore.storageEngine.AbstractStorageEngine#delete(io.zino.
	 * mystore.storageEngine.StorageEntry)
	 */
	@Override
	public StorageEntry delete(StorageEntry storageEntry) {
		if (this.data.containsKey(storageEntry.getKey())) {
			return this.data.remove(storageEntry.getKey());
		}
		return null;
	}
}
