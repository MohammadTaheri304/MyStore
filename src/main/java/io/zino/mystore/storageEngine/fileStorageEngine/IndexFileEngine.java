package io.zino.mystore.storageEngine.fileStorageEngine;

import java.io.IOException;
import java.io.RandomAccessFile;

public class IndexFileEngine {
	RandomAccessFile dbIndexFile;

	private static final int BranchingFactore = 10;
	private static final int sizeOfOneIndexEntry = Long.BYTES + Long.BYTES;

	private long computeHash(String input, int depth) {
		long hash = -1l;
		for (int i = 0; i < input.length(); i++) {
			hash += (input.charAt(i)+depth)*i;
		}
		return (hash % BranchingFactore);
	}

	long getKeyAddress(String key){
		return this.getKeyAddress(key, 0, 1);
	}
	
	private long getKeyAddress(String key , long parentItem, int depth) {
		
		try {
			long c = this.computeHash(key, depth);
			long item = parentItem + c;
			long nxtParent = ((item + 1) * BranchingFactore);
			long itemHead = item * (sizeOfOneIndexEntry);

			this.dbIndexFile.seek(itemHead);
			long con = this.dbIndexFile.readLong();
			long value = this.dbIndexFile.readLong();

			if (con == 0l) {
				return -1L;
			} else if (con == 1l) {
				return value;
			} else if (con < 1l) {
				return getKeyAddress(key, nxtParent, ++depth);
			} else {
				// something is wrong
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1L;
	}

	void saveAddressKey(String key, long entryHead, long parentItem, int depth) {		
		try {
			long c = this.computeHash(key, depth);
			long item = parentItem + c;
			long nxtParent = ((item + 1) * BranchingFactore);
			long itemHead = item * (sizeOfOneIndexEntry);

			this.dbIndexFile.seek(itemHead);
			long con = this.dbIndexFile.readLong();
			long value = this.dbIndexFile.readLong();

			if (con == 0l) {
				this.dbIndexFile.seek(itemHead);
				this.dbIndexFile.writeLong(++con);
				this.dbIndexFile.writeLong(entryHead);
				return;
			} else if (con == 1l) {
				this.dbIndexFile.seek(itemHead);
				this.dbIndexFile.writeLong(++con);
				this.dbIndexFile.writeLong(0l);
				{
					long nextItemHead = (nxtParent + c) * (sizeOfOneIndexEntry);
					this.dbIndexFile.seek(nextItemHead);
					this.dbIndexFile.writeLong(1l);
					this.dbIndexFile.writeLong(value);
				}
				saveAddressKey(key, entryHead, nxtParent, ++depth);
				return;
			} else if (con < 1l) {
				saveAddressKey(key, entryHead, nxtParent, ++depth);
				return;
			} else {
				// something is wrong
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void deleteAddressKey(String key) {
		this.deleteAddressKey(key, 0, 1);
	}
	
	private void deleteAddressKey(String key, long parentItem, int depth) {
		
		try {
			long c = this.computeHash(key, depth);
			long item = parentItem + c;
			long nxtParent = ((item + 1) * BranchingFactore);
			long itemHead = item * (sizeOfOneIndexEntry);

			this.dbIndexFile.seek(itemHead);
			long con = this.dbIndexFile.readLong();
			long value = this.dbIndexFile.readLong();

			if (con == 0l) {
			} else if (con == 1l) {
				this.dbIndexFile.seek(itemHead);
				this.dbIndexFile.readLong();
				this.dbIndexFile.writeLong(0L);
			} else if (con < 1l) {
				deleteAddressKey(key, nxtParent, ++depth);
			} else {
				// something is wrong
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
