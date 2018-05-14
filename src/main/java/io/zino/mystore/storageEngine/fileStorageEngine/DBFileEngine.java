package io.zino.mystore.storageEngine.fileStorageEngine;

import java.io.IOException;
import java.io.RandomAccessFile;

import io.zino.mystore.storageEngine.StorageEntry;

public class DBFileEngine {
	RandomAccessFile dbFile;

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

	void saveEntry(long head, StorageEntry entry) {
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
