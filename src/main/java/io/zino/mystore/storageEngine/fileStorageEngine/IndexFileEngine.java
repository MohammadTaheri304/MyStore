package io.zino.mystore.storageEngine.fileStorageEngine;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.log4j.Logger;

public class IndexFileEngine {
	final static Logger logger = Logger.getLogger(IndexFileEngine.class);
	RandomAccessFile dbIndexFile;

	public IndexFileEngine(RandomAccessFile dbIndexFile) {
		super();
		this.dbIndexFile = dbIndexFile;
	}

	private static final int BranchingFactore = 10;
	private static final int sizeOfOneIndexEntry = Long.BYTES + Long.BYTES;

	private long computeHash(String input, int depth) {
		long hash = 113l;
		for (int i = 0; i < input.length(); i++) {
			hash += ((input.charAt(i) + depth) * (input.charAt(i) + depth * depth)) * (i + 1);
		}
		return (hash % BranchingFactore);
	}

	long getKeyAddress(String key) {
		return this.getKeyAddress(key, 0, 1);
	}

	private long getKeyAddress(String key, long parentItem, int depth) {
		long c = this.computeHash(key, depth);
		long item = parentItem + c;
		long nxtParent = ((item + 1) * BranchingFactore);
		IndexEntry indexEntry = getIndexEntry(item);
		if (indexEntry==null)
			return-1L;
		else if (indexEntry.count == 0l) {
			return -1L;
		} else if (indexEntry.count == 1l) {
			return indexEntry.value;
		} else if (indexEntry.count > 1l) {
			return getKeyAddress(key, nxtParent, ++depth);
		} else {
			logger.debug("something is wrong "+indexEntry.toString());
		}
		return -1L;
	}

	public IndexEntry getIndexEntry(long item) {
		long itemHead = item * (sizeOfOneIndexEntry);
		try {
			this.dbIndexFile.seek(itemHead);
			long con = this.dbIndexFile.readLong();
			long value = this.dbIndexFile.readLong();
			IndexEntry indexEntry = new IndexEntry(con, value, itemHead);
			return indexEntry;
		} catch (IOException ex) {
			return null;
		}
	}

	public IndexEntry saveIndexEntry(long item, long count, long value) {
		long itemHead = item * (sizeOfOneIndexEntry);
		try {
			this.dbIndexFile.seek(itemHead);
			this.dbIndexFile.writeLong(count);
			this.dbIndexFile.writeLong(value);
			return new IndexEntry(count, value, itemHead);
		} catch (IOException ex) {
			System.out.println("12345");
			return null;
		}
	}

	void saveAddressKey(String key, long entryHead) {
		saveAddressKey(key, entryHead, 0, 1);
	}

	private void saveAddressKey(String key, long entryHead, long parentItem, int depth) {
		if(depth>100){
			logger.debug("WOW depth "+depth);
			return;
		}
		long c = this.computeHash(key, depth);
		long item = parentItem + c;
		long nxtParent = ((item + 1) * BranchingFactore);
		IndexEntry indexEntry = getIndexEntry(item);
		if (indexEntry==null || indexEntry.count == 0l) {
			this.saveIndexEntry(item, 1l, entryHead);
		} else if (indexEntry.count == 1l) {
			this.saveIndexEntry(item, 2l, 0l);
			{
				this.saveIndexEntry((nxtParent + c), 1l, indexEntry.value);
			}
			saveAddressKey(key, entryHead, nxtParent, ++depth);
		} else if (indexEntry.count > 1l) {
			saveAddressKey(key, entryHead, nxtParent, ++depth);
		} else {
			logger.debug("some thing is wrong");
		}
	}

	void deleteAddressKey(String key) {
		this.updateAddressKey(key, 0l, 0l, 0, 1);
	}

	void updateAddressKey(String key, long newHead) {
		this.updateAddressKey(key, 1L, newHead, 0, 1);
	}

	private void updateAddressKey(String key, long count, long newHead, long parentItem, int depth) {
		long c = this.computeHash(key, depth);
		long item = parentItem + c;
		long nxtParent = ((item + 1) * BranchingFactore);
		IndexEntry indexEntry = this.getIndexEntry(item);
		if (indexEntry==null){
			logger.debug("WTF??");
		}else if (indexEntry.count == 0l) {
		} else if (indexEntry.count == 1) {
			this.saveIndexEntry(item, count, newHead);
		} else if (indexEntry.count > 1l) {
			updateAddressKey(key, count, newHead, nxtParent, ++depth);
		} else {
			logger.debug("something is wrong");
		}
	}

}
