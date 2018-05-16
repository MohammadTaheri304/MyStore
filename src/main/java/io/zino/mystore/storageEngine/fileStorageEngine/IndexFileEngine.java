package io.zino.mystore.storageEngine.fileStorageEngine;

import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

public class IndexFileEngine {
	public static int counter = 0;

	final static Logger logger = Logger.getLogger(IndexFileEngine.class);

	private Map<String, Long> map;

	public IndexFileEngine(RandomAccessFile dbIndexFile) {
		counter++;
		this.map = new ConcurrentHashMap<>();
	}

	Long getKeyAddress(String key) {
		if(this.map.containsKey(key))
			return this.map.get(key);
		else return null;
	}

	void saveAddressKey(String key, long head) {
		this.map.put(key, head);
	}

	void deleteAddressKey(String key) {
		this.map.remove(key);
	}

	void updateAddressKey(String key, long newHead) {
		this.map.replace(key, newHead);
	}

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
