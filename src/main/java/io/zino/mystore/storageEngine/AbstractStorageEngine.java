package io.zino.mystore.storageEngine;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractStorageEngine.
 */
abstract public class AbstractStorageEngine {

	/**
	 * Contains key.
	 *
	 * @param storageEntry the storage entry
	 * @return true, if successful
	 */
	public abstract boolean containsKey(StorageEntry storageEntry);

	/**
	 * Gets the.
	 *
	 * @param storageEntry the storage entry
	 * @return the storage entry
	 */
	public abstract StorageEntry get(StorageEntry storageEntry);

	/**
	 * Insert.
	 *
	 * @param storageEntry the storage entry
	 * @return the storage entry
	 */
	public abstract StorageEntry insert(StorageEntry storageEntry);

	/**
	 * Update.
	 *
	 * @param storageEntry the storage entry
	 * @return the storage entry
	 */
	public abstract StorageEntry update(StorageEntry storageEntry);

	/**
	 * Delete.
	 *
	 * @param storageEntry the storage entry
	 * @return the storage entry
	 */
	public abstract StorageEntry delete(StorageEntry storageEntry);

}
