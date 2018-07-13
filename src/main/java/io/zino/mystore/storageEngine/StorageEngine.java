package io.zino.mystore.storageEngine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.zino.mystore.clusterEngine.ClusterEngine;
import io.zino.mystore.storageEngine.QueryResult.QueryResultStatus;
import io.zino.mystore.storageEngine.fileStorageEngine.FileStorageEngine;
import io.zino.mystore.storageEngine.memoryStorageEngine.MemoryStorageEngine;

/**
 * The Class StorageEngine.
 */
final public class StorageEngine extends AbstractStorageEngine {

	/** The Constant logger. */
	final static Logger logger = LogManager.getLogger(StorageEngine.class);

	/** The instance. */
	private static StorageEngine instance = new StorageEngine();

	/** The memory storage engine. */
	private MemoryStorageEngine memoryStorageEngine;

	/** The file storage engine. */
	private FileStorageEngine fileStorageEngine;

	/**
	 * Gets the single instance of StorageEngine.
	 *
	 * @return single instance of StorageEngine
	 */
	public static StorageEngine getInstance() {
		return instance;
	}

	/**
	 * Instantiates a new storage engine.
	 */
	private StorageEngine() {
		this.memoryStorageEngine = MemoryStorageEngine.getInstance();
		this.fileStorageEngine = FileStorageEngine.getInstance();

		System.out.println("StorageEngine Started! " + System.currentTimeMillis());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.zino.mystore.storageEngine.AbstractStorageEngine#get(io.zino.mystore.
	 * storageEngine.StorageEntry)
	 */
	@Override
	public StorageEntry get(final StorageEntry storageEntry) {
		StorageEntry se = memoryStorageEngine.get(storageEntry);
		if (se != null)
			return se;
		return fileStorageEngine.get(storageEntry);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.zino.mystore.storageEngine.AbstractStorageEngine#insert(io.zino.
	 * mystore.storageEngine.StorageEntry)
	 */
	@Override
	public StorageEntry insert(final StorageEntry storageEntry) {
		boolean containsKey = this.containsKey(storageEntry);
		if (containsKey)
			return null;
		return memoryStorageEngine.insert(storageEntry);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.zino.mystore.storageEngine.AbstractStorageEngine#update(io.zino.
	 * mystore.storageEngine.StorageEntry)
	 */
	@Override
	public StorageEntry update(final StorageEntry storageEntry) {
		StorageEntry se = memoryStorageEngine.update(storageEntry);
		if (se != null)
			return se;
		return fileStorageEngine.update(storageEntry);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.zino.mystore.storageEngine.AbstractStorageEngine#delete(io.zino.
	 * mystore.storageEngine.StorageEntry)
	 */
	@Override
	public StorageEntry delete(final StorageEntry storageEntry) {
		StorageEntry se = memoryStorageEngine.delete(storageEntry);
		if (se != null)
			return se;
		return fileStorageEngine.delete(storageEntry);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.zino.mystore.storageEngine.AbstractStorageEngine#containsKey(io.zino.
	 * mystore.storageEngine.StorageEntry)
	 */
	@Override
	public boolean containsKey(final StorageEntry key) {
		boolean se = memoryStorageEngine.containsKey(key);
		if (se)
			return se;
		return fileStorageEngine.containsKey(key);
	}

	/**
	 * Insert.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @return the query result
	 */
	public QueryResult insert(final String key, final String value) {
		StorageEntry storageEntry = new StorageEntry(key, value);
		StorageEntry se = this.insert(storageEntry);
		if (se != null) {
			ClusterEngine.getInstance().addRequest(storageEntry);
			return new QueryResult(storageEntry.getKey(), storageEntry.getData(), QueryResultStatus.INSERT_TRUE);
		}
		return new QueryResult(null, null, QueryResultStatus.INSERT_FALSE);
	}

	/**
	 * Update.
	 *
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 * @return the query result
	 */
	public QueryResult update(final String key, final String value) {
		StorageEntry storageEntry = new StorageEntry(key, value);
		StorageEntry se = this.get(storageEntry);
		if (se != null) {
			if (se.getNodeId().equals(ClusterEngine.NODE_ID)) {
				StorageEntry updateres = this.update(storageEntry);
				if (updateres != null)
					return new QueryResult(updateres.getKey(), updateres.getData(), QueryResultStatus.UPDATE_TRUE);
				else
					return new QueryResult(null, null, QueryResultStatus.UPDATE_FALSE);
			} else {
				StorageEntry clone = se.cloneWithNewData(value);
				StorageEntry updateRequest = ClusterEngine.getInstance().updateRequest(clone);
				if (updateRequest != null) {
					this.update(updateRequest);
					return new QueryResult(updateRequest.getKey(), updateRequest.getData(), QueryResultStatus.UPDATE_TRUE);
				} else {
					this.delete(clone);
					return new QueryResult(null, null, QueryResultStatus.UPDATE_FALSE);
				}
			}
		} else
			return new QueryResult(null, null, QueryResultStatus.UPDATE_FALSE);
	}

	/**
	 * Gets the.
	 *
	 * @param key
	 *            the key
	 * @return the query result
	 */
	public QueryResult get(final String key) {
		StorageEntry storageEntry = new StorageEntry(key, null);
		StorageEntry se = this.get(storageEntry);
		if (se == null)
			return new QueryResult(null, null, QueryResultStatus.GET_FALSE);
		else if (se.getNodeId().equals(ClusterEngine.NODE_ID))
			return new QueryResult(se.getKey(), se.getData(), QueryResultStatus.GET_TRUE);
		else {
			StorageEntry clone = se.clone();
			StorageEntry getRequest = ClusterEngine.getInstance().getRequest(clone);
			if (getRequest != null) {
				this.update(getRequest);
				return new QueryResult(getRequest.getKey(), getRequest.getData(), QueryResultStatus.GET_TRUE);
			} else {
				this.delete(clone);
				return new QueryResult(null, null, QueryResultStatus.GET_FALSE);
			}
		}
	}

	/**
	 * Exist.
	 *
	 * @param key
	 *            the key
	 * @return the query result
	 */
	public QueryResult exist(final String key) {
		StorageEntry storageEntry = new StorageEntry(key, null);
		StorageEntry se = this.get(storageEntry);
		if (se == null)
			return new QueryResult(null, null, QueryResultStatus.EXIST_FALSE);
		else if (se.getNodeId().equals(ClusterEngine.NODE_ID))
			return new QueryResult(null, null, QueryResultStatus.EXIST_TRUE);
		else {
			StorageEntry clone = se.clone();
			StorageEntry getRequest = ClusterEngine.getInstance().getRequest(clone);
			if (getRequest != null) {
				this.update(getRequest);
				return new QueryResult(null, null, QueryResultStatus.EXIST_TRUE);
			} else {
				this.delete(clone);
				return new QueryResult(null, null, QueryResultStatus.EXIST_FALSE);
			}
		}
	}

	/**
	 * Delete.
	 *
	 * @param key
	 *            the key
	 * @return the query result
	 */
	public QueryResult delete(final String key) {
		StorageEntry storageEntry = new StorageEntry(key, null);
		StorageEntry se = this.get(storageEntry);
		if (se == null)
			return new QueryResult(null, null, QueryResultStatus.DELETE_FALSE);
		else {
			if (se.getNodeId().equals(ClusterEngine.NODE_ID)) {
				this.delete(se);
				return new QueryResult(null, null, QueryResultStatus.DELETE_TRUE);
			} else {
				StorageEntry clone = se.clone();
				ClusterEngine.getInstance().deleteRequest(clone);
				this.delete(clone);
				return new QueryResult(null, null, QueryResultStatus.DELETE_TRUE);
			}
		}
	}

}
