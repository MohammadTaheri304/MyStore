package io.zino.mystore.storageEngine.fileStorageEngine;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Class IndexFileEngine.
 */
final public class IndexFileEngine {

	/** The Constant logger. */
	final static Logger logger = LogManager.getLogger(IndexFileEngine.class);

	/** The write head. */
	private static long writeHead = 0;

	private RandomAccessFile indexFile;

	/**
	 * Instantiates a new index file engine.
	 *
	 * @param dbIndexFile
	 *            the db index file
	 */
	public IndexFileEngine(RandomAccessFile dbIndexFile) {
		this.indexFile = dbIndexFile;
	}

	/**
	 * Compute hash.
	 *
	 * @param key
	 *            the key
	 * @param depth
	 *            the depth
	 * @return the int
	 */
	private int computeHash(String key, int depth) {
		int d=depth%3;
		if(d==0)
			return this.hashDepth0(key, depth);
		else if(d==1)
			return this.hashDepth1(key, depth);
		else
			return this.hashDepth2(key, depth);
	}
	
	private int hashDepth0(String key, int depth){
		int hash = 0;
		for (char c : key.toCharArray()) {
			hash += (c * 133 + c * c * depth) + (-1 * depth);
		}

		return Math.abs(hash % 5) + 1;
	}
	
	private int hashDepth1(String key, int depth){
		int hash = 0;
		for (char c : key.toCharArray()) {
			hash += c*c;  
		}
		hash =+ (-1 * depth);

		return Math.abs(hash % 5) + 1;
	}
	
	private int hashDepth2(String key, int depth){
		int hash = 0;
		for (char c : key.toCharArray()) {
			hash += ((c + 133) * depth) ;
		}
		hash *=3;

		return Math.abs(hash % 5) + 1;
	}

	/**
	 * Load string.
	 *
	 * @return the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private String loadString() throws IOException {
		StringBuilder sb = new StringBuilder();
		int size = this.indexFile.readInt();
		for (int i = 0; i < size; i++) {
			sb.append(this.indexFile.readChar());
		}
		return sb.toString();
	}

	/**
	 * Save string.
	 *
	 * @param string
	 *            the string
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void saveString(String string) throws IOException {
		this.indexFile.writeInt(string.length());
		for (int c : string.toCharArray())
			this.indexFile.writeChar(c);
	}

	/**
	 * Load index file entry.
	 *
	 * @param head
	 *            the head
	 * @return the index file entry
	 */
	synchronized private IndexFileEntry loadIndexFileEntry(long head) {
		if (IndexFileEngine.writeHead <= head || head < 0) {
			logger.debug("OutOfBound head error on loadIndexFileEntry head=" + head + " and writeHead="
					+ IndexFileEngine.writeHead);
			return null;
		}
		try {
			this.indexFile.seek(head);
			long child1 = this.indexFile.readLong();
			long child2 = this.indexFile.readLong();
			long child3 = this.indexFile.readLong();
			long child4 = this.indexFile.readLong();
			long child5 = this.indexFile.readLong();
			long value = this.indexFile.readLong();
			String key = this.loadString();

			return new IndexFileEntry(child1, child2, child3, child4, child5, value, key);
		} catch (IOException e) {
			logger.error("Error on loadIndexFileEntry with head:" + head);
		}
		return null;
	}

	/**
	 * Save index file entry.
	 *
	 * @param head
	 *            the head
	 * @param entry
	 *            the entry
	 */
	synchronized private void saveIndexFileEntry(long head, IndexFileEntry entry) {
		try {
			this.indexFile.seek(head);

			this.indexFile.writeLong(entry.getChild1());
			this.indexFile.writeLong(entry.getChild2());
			this.indexFile.writeLong(entry.getChild3());
			this.indexFile.writeLong(entry.getChild4());
			this.indexFile.writeLong(entry.getChild5());
			this.indexFile.writeLong(entry.getValue());
			this.saveString(entry.getKey());

		} catch (IOException e) {
			logger.error("Error on saveIndexFileEntry in head=" + head, e);
		}
	}

	/**
	 * Gets the key address.
	 *
	 * @param key
	 *            the key
	 * @return the key address
	 */
	Long getKeyAddress(String key) {
		try {
			IndexFileEntry entry = this.getKeyAddress(key, 0, 1);
			if (entry != null) {
				return entry.getValue();
			}
		} catch (EOFException e) {
			logger.error("EOF Error on getKeyAddress", e);
		}
		return null;
	}

	/**
	 * Gets the key address.
	 *
	 * @param key
	 *            the key
	 * @param head
	 *            the head
	 * @param depth
	 *            the depth
	 * @return the key address
	 */
	private IndexFileEntry getKeyAddress(String key, long head, int depth) throws EOFException {
		IndexFileEntry entry = this.loadIndexFileEntry(head);
		if (entry == null) {
			return null;
		} else if (key.equals(entry.getKey()) && entry.getValue() != -1l) {
			return entry;
		} else {
			int computeHash = this.computeHash(key, depth + 1);
			long childAt = entry.getChildAt(computeHash);
			if (childAt == -1 || childAt == 0) {
				return null;
			} else
				return this.getKeyAddress(key, childAt, depth + 1);
		}
	}

	/**
	 * Save address key.
	 *
	 * @param key
	 *            the key
	 * @param head
	 *            the head
	 */
	void saveAddressKey(String key, long head) {
		this.saveAddressKey(key, head, 0, 1);
	}

	/**
	 * Save address key.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @param head
	 *            the head
	 * @param depth
	 *            the depth
	 * @return the index file entry
	 */
	private IndexFileEntry saveAddressKey(String key, long value, long head, int depth) {
		if (depth > 10)
			return null;

		IndexFileEntry entry = this.loadIndexFileEntry(head);
		if (entry == null) {
			IndexFileEntry savedEntry = new IndexFileEntry(-1l, -1l, -1l, -1l, -1l, value, key);
			try {
				this.saveIndexFileEntry(head, savedEntry);
				IndexFileEngine.writeHead = this.indexFile.getFilePointer();
				return savedEntry;
			} catch (IOException e) {
				logger.error("IO Error on saveAddressKey", e);
			}
		} else {
			int computeHash = this.computeHash(key, depth + 1);
			long childAt = entry.getChildAt(computeHash);
			if (childAt == -1 || childAt == 0) {
				entry.setChildAt(computeHash, writeHead);
				this.saveIndexFileEntry(head, entry);
				return this.saveAddressKey(key, value, writeHead, depth + 1);
			} else {
				if (childAt < 0) {
					System.out.println("@@@");
				}
				return this.saveAddressKey(key, value, childAt, depth + 1);
			}
		}
		return null;
	}

	/**
	 * Delete address key.
	 *
	 * @param key
	 *            the key
	 */
	void deleteAddressKey(String key) {
		this.deleteAddressKey(key, 0, 1);
	}

	/**
	 * Delete address key.
	 *
	 * @param key
	 *            the key
	 * @param head
	 *            the head
	 * @param depth
	 *            the depth
	 * @return the index file entry
	 */
	private IndexFileEntry deleteAddressKey(String key, long head, int depth) {
		IndexFileEntry entry = this.loadIndexFileEntry(head);
		if (entry == null) {
			return null;
		} else if (entry.getValue() != -1 && entry.getValue() != 0 && key.equals(entry.getKey())) {
			IndexFileEntry removeEntry = new IndexFileEntry(entry.getChild1(), entry.getChild2(), entry.getChild3(),
					entry.getChild4(), entry.getChild5(), -1, key);
			this.saveIndexFileEntry(head, removeEntry);
			return entry;
		} else {
			int computeHash = this.computeHash(key, depth + 1);
			long childAt = entry.getChildAt(computeHash);
			if (childAt == -1 || childAt == 0)
				return null;
			return this.deleteAddressKey(key, childAt, depth + 1);
		}
	}

	/**
	 * Update address key.
	 *
	 * @param key
	 *            the key
	 * @param newHead
	 *            the new head
	 */
	void updateAddressKey(String key, long newHead) {
		this.updateAddressKey(key, newHead, 0, 1);
	}

	private IndexFileEntry updateAddressKey(String key, long value, long head, int depth) {
		IndexFileEntry entry = this.loadIndexFileEntry(head);
		if (entry == null) {
			return null;
		} else if (entry.getValue() != -1 && entry.getValue() != 0 && key.equals(entry.getKey())) {
			IndexFileEntry deletedEntry = new IndexFileEntry(entry.getChild1(), entry.getChild2(), entry.getChild3(),
					entry.getChild4(), entry.getChild5(), -1l, key);
			this.saveIndexFileEntry(head, deletedEntry);

			return this.saveAddressKey(key, value, head, depth);
		} else {
			int computeHash = this.computeHash(key, depth + 1);
			long childAt = entry.getChildAt(computeHash);
			if (childAt == -1 || childAt == 0)
				return null;
			return this.updateAddressKey(key, value, childAt, depth + 1);
		}
	}

	/**
	 * Gets the index entry.
	 *
	 * @param item
	 *            the item
	 * @return the index entry
	 */
	public Long getIndexEntry(long item) {
		try {
			IndexFileEntry entry = this.getIndexEntry(item, 0, 0);
			if (entry != null)
				return entry.getValue();
		} catch (EOFException e) {
			logger.error("EOF Error on getIndexEntry", e);
		}

		return -1L;
	}

	
	public Iterable<Long> getIndexEntries(){
		return new Iterable<Long>() {
			
			@Override
			public Iterator<Long> iterator() {
				return new Iterator<Long>() {

					long current = 0;
					long head = 0;
					
					@Override
					public boolean hasNext() {
						try {
							return getIndexEntry(current+1, current, head)!=null ? true : false;
						} catch (EOFException e) {
							logger.error("EOF Error on getIndexEntries::next", e);
						}
						return false;
					}

					@Override
					public Long next() {
						try {
							return getIndexEntry(++current, current-1, head).getValue();
						} catch (EOFException e) {
							logger.error("EOF Error on getIndexEntries::next", e);
						}
						return -1l;
					}
				};
			}
		};
	}
	
	/**
	 * Gets the index entry.
	 *
	 * @param destItem
	 *            the dest item
	 * @param head
	 *            the head
	 * @return the index entry
	 */
	public IndexFileEntry getIndexEntry(long destItem, long currentItem, long head) throws EOFException {
		IndexFileEntry entry = this.loadIndexFileEntry(head);
		if (entry == null) {
			return null;
		} else if (currentItem == destItem) {
			return entry;
		} else {
			long childAt1 = entry.getChildAt(1);
			if (childAt1 != -1 || childAt1 != 0) {
				IndexFileEntry ch1 = getIndexEntry(destItem, ++currentItem, childAt1);
				if (ch1 != null)
					return ch1;
			}
			long childAt2 = entry.getChildAt(2);
			if (childAt2 != -1 || childAt2 != 0) {
				IndexFileEntry ch2 = getIndexEntry(destItem, ++currentItem, childAt2);
				if (ch2 != null)
					return ch2;
			}
			long childAt3 = entry.getChildAt(3);
			if (childAt3 != -1 || childAt3 != 0) {
				IndexFileEntry ch3 = getIndexEntry(destItem, ++currentItem, childAt3);
				if (ch3 != null)
					return ch3;
			}
			long childAt4 = entry.getChildAt(4);
			if (childAt4 != -1 || childAt4 != 0) {
				IndexFileEntry ch4 = getIndexEntry(destItem, ++currentItem, childAt4);
				if (ch4 != null)
					return ch4;
			}
			long childAt5 = entry.getChildAt(5);
			if (childAt5 != -1 || childAt5 != 0) {
				IndexFileEntry ch5 = getIndexEntry(destItem, ++currentItem, childAt5);
				if (ch5 != null)
					return ch5;
			}
			return null;
		}
	}
}
