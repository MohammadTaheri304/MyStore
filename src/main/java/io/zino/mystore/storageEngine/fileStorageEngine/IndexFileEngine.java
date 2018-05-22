package io.zino.mystore.storageEngine.fileStorageEngine;

import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

/**
 * The Class IndexFileEngine.
 */
final public class IndexFileEngine {
	
	/** The counter. */
	public static int counter = 0;

	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(IndexFileEngine.class);

	/** The map. */
	private Map<String, Long> map;

	/**
	 * Instantiates a new index file engine.
	 *
	 * @param dbIndexFile the db index file
	 */
	public IndexFileEngine(RandomAccessFile dbIndexFile) {
		counter++;
		this.map = new ConcurrentHashMap<>();
	}

	/**
	 * Gets the key address.
	 *
	 * @param key the key
	 * @return the key address
	 */
	Long getKeyAddress(String key) {
		if(this.map.containsKey(key))
			return this.map.get(key);
		else return null;
	}

	/**
	 * Save address key.
	 *
	 * @param key the key
	 * @param head the head
	 */
	void saveAddressKey(String key, long head) {
		this.map.put(key, head);
	}

	/**
	 * Delete address key.
	 *
	 * @param key the key
	 */
	void deleteAddressKey(String key) {
		this.map.remove(key);
	}

	/**
	 * Update address key.
	 *
	 * @param key the key
	 * @param newHead the new head
	 */
	void updateAddressKey(String key, long newHead) {
		this.map.replace(key, newHead);
	}

	/**
	 * Gets the index entry.
	 *
	 * @param item the item
	 * @return the index entry
	 */
	public Long getIndexEntry(long item) {
		Iterator<Long> iterator = this.map.values().iterator();
		try {
			for (int i = 0; i < item; i++) {
				iterator.next();
			}
			return iterator.next();
		} catch (NoSuchElementException e) {
			return null;
		}
	}
}
