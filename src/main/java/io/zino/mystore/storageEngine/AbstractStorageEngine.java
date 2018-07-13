package io.zino.mystore.storageEngine;

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
	public abstract boolean containsKey(final StorageEntry storageEntry);

	/**
	 * Gets the.
	 *
	 * @param storageEntry the storage entry
	 * @return the storage entry
	 */
	public abstract StorageEntry get(final StorageEntry storageEntry);

	/**
	 * Insert.
	 *
	 * @param storageEntry the storage entry
	 * @return the storage entry
	 */
	public abstract StorageEntry insert(final StorageEntry storageEntry);

	/**
	 * Update.
	 *
	 * @param storageEntry the storage entry
	 * @return the storage entry
	 */
	public abstract StorageEntry update(final StorageEntry storageEntry);

	/**
	 * Delete.
	 *
	 * @param storageEntry the storage entry
	 * @return the storage entry
	 */
	public abstract StorageEntry delete(final StorageEntry storageEntry);

}
