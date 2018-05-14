package io.zino.mystore.storageEngine.fileStorageEngine;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.instrument.Instrumentation;

import io.zino.mystore.storageEngine.StorageEntry;

public class DBFileEngine {
	private static Instrumentation instrumentation;
	public static long writeHead = 0L;
	public static int dirtyEntry=0;
	
	RandomAccessFile dbFile;

	public static long entrySize(StorageEntry entry) {
		long size=0;
		size += Long.BYTES ;//Version
		size += Long.BYTES ;//LastAccess
		size += Long.BYTES ;//TouchCount

		size += Integer.BYTES ;//NodeId lenght
		size += Character.BYTES*entry.getNodeId().length() ;//NodeId

		size += Integer.BYTES ;//Key lenght
		size += Character.BYTES*entry.getKey().length() ;//Key
		
		size += Integer.BYTES ;//Data lenght
		size += Character.BYTES*entry.getData().length() ;//Data
		
		return size;
	}
	
	StorageEntry loadEntry(long head) {
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
			e.printStackTrace();
		}
		return null;
	}

	private String loadString() throws IOException {
		int size = this.dbFile.readInt();
		byte[] bytes = new byte[Character.BYTES * size];
		this.dbFile.read(bytes);
		return new String(bytes);
	}

	private long saveEntry(StorageEntry entry) {
		long size = DBFileEngine.entrySize(entry);
		long wh = DBFileEngine.writeHead;
		DBFileEngine.writeHead += size;
		this.saveEntry(wh, entry);
		return wh;
	}		
	
	private void saveEntry(long head, StorageEntry entry) {		
		try {
			this.dbFile.seek(head);

			this.dbFile.writeLong(entry.getVersion());
			this.dbFile.writeLong(entry.getLastAccess());
			this.dbFile.writeLong(entry.getTouchCount());

			this.saveString(entry.getNodeId());
			this.saveString(entry.getKey());
			this.saveString(entry.getData());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveString(String string) throws IOException {
		this.dbFile.writeInt(string.length());
		this.dbFile.writeChars(string);
	}

}
