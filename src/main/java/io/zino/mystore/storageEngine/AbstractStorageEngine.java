package io.zino.mystore.storageEngine;

abstract public class AbstractStorageEngine {

	public abstract boolean containsKey(StorageEntry storageEntry);

	public abstract StorageEntry get(StorageEntry storageEntry);

	public abstract StorageEntry insert(StorageEntry storageEntry);

	public abstract StorageEntry update(StorageEntry storageEntry);

	public abstract StorageEntry delete(StorageEntry storageEntry);

}
