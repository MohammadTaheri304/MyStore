package io.zino.mystore.storageEngine.fileStorageEngine;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.instrument.Instrumentation;

import org.apache.log4j.Logger;

import io.zino.mystore.storageEngine.StorageEntry;

public class DBFileEngine {
	final static Logger logger = Logger.getLogger(DBFileEngine.class);

	public DBFileEngine(RandomAccessFile dbFile) {
		super();
		this.dbFile = dbFile;
	}

	private static Instrumentation instrumentation;
	private long writeHead = 1L;
	public static int dirtyEntry = 0;

	RandomAccessFile dbFile;

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

	private String loadString() throws IOException {
		StringBuilder sb = new StringBuilder();
		int size = this.dbFile.readInt();
		for (int i = 0; i < size; i++) {
			sb.append(this.dbFile.readChar());
		}
		return sb.toString();
	}

	synchronized long saveEntry(StorageEntry entry) {
		final long wh = this.writeHead;
		final long size = this.saveEntry(wh, entry);
		if(size==-1){
			logger.debug("saveEntry Failed! "+entry.toString());
			return -1l;
		}
		this.writeHead += size;
		logger.debug("save Entry with key " + entry.getKey() + " with Head " + wh + " end is " + this.writeHead);
		return wh;
	}

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

	private void saveString(String string) throws IOException {
		this.dbFile.writeInt(string.length());
		for (int c : string.toCharArray())
			this.dbFile.writeChar(c);
	}

}
