package io.zino.mystore.commandEngine;

import io.zino.mystore.storageEngine.QueryResult;

/**
 * The Class QueryResult.
 */
public class CMDQueryResult {

	/**
	 * The Enum QueryResultStatus.
	 */
	public enum CMDQueryResultStatus {
		/** The successful. */
		SUCCESSFUL,
		/** The failed. */
		FAILED,
		/** The query failed. */
		QUERY_FAILED,
		/** The close it. */
		CLOSE_IT;
	}

	/** The result. */
	private QueryResult[] result;

	/** The status. */
	private CMDQueryResultStatus status;

	/**
	 * Gets the result.
	 *
	 * @return the result
	 */
	public QueryResult[] getResult() {
		return result;
	}

	/**
	 * Gets the status.
	 *
	 * @return the status
	 */
	public CMDQueryResultStatus getStatus() {
		return status;
	}

	/**
	 * Instantiates a new CMD query result.
	 *
	 * @param result
	 *            the result
	 */
	public CMDQueryResult(QueryResult result) {
		this(new QueryResult[] { result },
				result.getStatus().succ ? CMDQueryResultStatus.SUCCESSFUL : CMDQueryResultStatus.FAILED);
	}

	/**
	 * Instantiates a new CMD query result.
	 *
	 * @param status
	 *            the status
	 */
	public CMDQueryResult(CMDQueryResultStatus status) {
		this(null, status);
	}

	/**
	 * Instantiates a new CMD query result.
	 *
	 * @param result
	 *            the result
	 * @param status
	 *            the status
	 */
	public CMDQueryResult(QueryResult result[], CMDQueryResultStatus status) {
		super();
		this.result = result;
		this.status = status;
	}

}
