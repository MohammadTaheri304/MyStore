/*
 * 
 */
package io.zino.mystore.storageEngine.fileStorageEngine;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.zino.mystore.storageEngine.StorageEntry;

/**
 * The Class DBFileEngine.
 */
final class DBFileEngine {
	
	/** The Constant logger. */
	final static Logger logger = LogManager.getLogger(DBFileEngine.class);

	/**
	 * Instantiates a new DB file engine.
	 *
	 * @param dbFile the db file
	 */
	DBFileEngine(RandomAccessFile dbFile) {
		super();
		this.dbFile = dbFile;
	}

	/** The write head. */
	private AtomicLong writeHead = new AtomicLong(1L);
	
	/** The dirty entry. */
	public static AtomicInteger dirtyEntry = new AtomicInteger(0);

	/** The db file. */
	RandomAccessFile dbFile;

	/**
	 * Load entry.
	 *
	 * @param head the head
	 * @return the storage entry
	 */
	synchronized StorageEntry loadEntry(long head) {
		try {
			this.dbFile.seek(head);

			long version = this.dbFile.readLong();
			long lastAccess = this.dbFile.readLong();
			long touchCount = this.dbFile.readLong();
			String nodeId = loadString();
			String key = loadString();
			String data = loadString();

			return new StorageEntry(version, nodeId, key, data, lastAccess, touchCount);
		} catch (IOException e) {
			logger.error("Error on load Entry with Head " + head, e);
		}
		return null;
	}

	/**
	 * Load string.
	 *
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String loadString() throws IOException {
		StringBuilder sb = new StringBuilder();
		int size = this.dbFile.readInt();
		for (int i = 0; i < size; i++) {
			sb.append(this.dbFile.readChar());
		}
		return sb.toString();
	}

	/**
	 * Save entry.
	 *
	 * @param entry the entry
	 * @return the long
	 */
	synchronized long saveEntry(StorageEntry entry) {
		final long wh = this.writeHead.longValue();
		final long size = this.saveEntry(wh, entry);
		if(size==-1){
			logger.debug("saveEntry Failed! "+entry.toString());
			return -1l;
		}
		long newWH = this.writeHead.addAndGet(size);
		logger.debug("save Entry with key " + entry.getKey() + " with Head " + wh + " end is " + newWH);
		return wh;
	}

	/**
	 * Save entry.
	 *
	 * @param head the head
	 * @param entry the entry
	 * @return the long
	 */
	private long saveEntry(long head, StorageEntry entry) {
		try {
			this.dbFile.seek(head);

			this.dbFile.writeLong(entry.getVersion());
			this.dbFile.writeLong(entry.getLastAccess());
			this.dbFile.writeLong(entry.getTouchCount());

			this.saveString(entry.getNodeId());
			this.saveString(entry.getKey());
			this.saveString(entry.getData());

			return this.dbFile.getFilePointer() - head;

		} catch (IOException e) {
			logger.error("Error on saving on Head " + head + " Entry: " + entry.toString(), e);
			return -1l;
		}
	}

	/**
	 * Save string.
	 *
	 * @param string the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void saveString(String string) throws IOException {
		this.dbFile.writeInt(string.length());
		for (int c : string.toCharArray())
			this.dbFile.writeChar(c);
	}

}
