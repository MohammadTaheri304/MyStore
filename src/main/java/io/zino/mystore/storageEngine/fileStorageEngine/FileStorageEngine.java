/*
 * 
 */
package io.zino.mystore.storageEngine.fileStorageEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.zino.mystore.ConfigMgr;
import io.zino.mystore.storageEngine.AbstractStorageEngine;
import io.zino.mystore.storageEngine.StorageEntry;

/**
 * The Class FileStorageEngine.
 */
final public class FileStorageEngine extends AbstractStorageEngine {
	
	/** The Constant logger. */
	final static Logger logger = LogManager.getLogger(FileStorageEngine.class);
	
	/** The instance. */
	private static FileStorageEngine instance = new FileStorageEngine();

	/** The index file engine. */
	IndexFileEngine indexFileEngine;
	
	/** The db file engine. */
	DBFileEngine dbFileEngine;

	/**
	 * Gets the file access.
	 *
	 * @param fileadrs the fileadrs
	 * @return the file access
	 */
	private RandomAccessFile getFileAccess(final String fileadrs) {
		File file = new File(fileadrs);
		if (file.exists()) {
			file.delete();
		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(file, "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return raf;
	}

	/**
	 * Gets the single instance of FileStorageEngine.
	 *
	 * @return single instance of FileStorageEngine
	 */
	public static FileStorageEngine getInstance() {
		return instance;
	}

	/**
	 * Instantiates a new file storage engine.
	 */
	private FileStorageEngine() {
		this.dbFileEngine = new DBFileEngine(
				this.getFileAccess(ConfigMgr.getInstance().get("FileStorageEngine.dbFile")));
		this.indexFileEngine = new IndexFileEngine(
				this.getFileAccess(ConfigMgr.getInstance().get("FileStorageEngine.dbIndexFile")));

		System.out.println("FileStorageEngine Started! " + System.currentTimeMillis());
	}

	/* (non-Javadoc)
	 * @see io.zino.mystore.storageEngine.AbstractStorageEngine#containsKey(io.zino.mystore.storageEngine.StorageEntry)
	 */
	@Override
	public boolean containsKey(final StorageEntry key) {
		return (this.get(key) == null) ? false : true;
	}

	/* (non-Javadoc)
	 * @see io.zino.mystore.storageEngine.AbstractStorageEngine#get(io.zino.mystore.storageEngine.StorageEntry)
	 */
	@Override
	public StorageEntry get(final StorageEntry storageEntry) {
		Long address = this.indexFileEngine.getKeyAddress(storageEntry.getKey());
		if (address==null || address == -1l || address == 0l)
			return null;
		StorageEntry loadEntry = this.dbFileEngine.loadEntry(address);
		if (loadEntry != null && storageEntry.getKey().equals(loadEntry.getKey())) {
			return loadEntry;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see io.zino.mystore.storageEngine.AbstractStorageEngine#insert(io.zino.mystore.storageEngine.StorageEntry)
	 */
	@Override
	public StorageEntry insert(final StorageEntry storageEntry) {
		if (this.containsKey(storageEntry))
			return null;
		long newAddress = this.dbFileEngine.saveEntry(storageEntry);
		if (newAddress == -1l)
			return null;
		this.indexFileEngine.saveAddressKey(storageEntry.getKey(), newAddress);
		return storageEntry;

	}

	/* (non-Javadoc)
	 * @see io.zino.mystore.storageEngine.AbstractStorageEngine#update(io.zino.mystore.storageEngine.StorageEntry)
	 */
	@Override
	public StorageEntry update(final StorageEntry storageEntry) {
		StorageEntry loadEntry = this.get(storageEntry);
		if (loadEntry == null)
			return null;
		DBFileEngine.dirtyEntry.incrementAndGet();
		long newAddress = this.dbFileEngine.saveEntry(storageEntry);
		if (newAddress == -1l)
			return null;
		this.indexFileEngine.updateAddressKey(loadEntry.getKey(), newAddress);
		return loadEntry;
	}

	/* (non-Javadoc)
	 * @see io.zino.mystore.storageEngine.AbstractStorageEngine#delete(io.zino.mystore.storageEngine.StorageEntry)
	 */
	@Override
	public StorageEntry delete(final StorageEntry storageEntry) {
		long address = this.indexFileEngine.getKeyAddress(storageEntry.getKey());
		if (address == -1l || address == 0l)
			return null;
		StorageEntry loadEntry = this.dbFileEngine.loadEntry(address);
		if (storageEntry.getKey().equals(loadEntry.getKey())) {
			DBFileEngine.dirtyEntry.incrementAndGet();
			this.indexFileEngine.deleteAddressKey(storageEntry.getKey());
			return loadEntry;
		}
		return null;
	}
}
