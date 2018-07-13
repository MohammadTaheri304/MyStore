package io.zino.mystore.commandEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import io.zino.mystore.commandEngine.CMDQueryResult.CMDQueryResultStatus;
import io.zino.mystore.storageEngine.QueryResult;
import io.zino.mystore.storageEngine.StorageEngine;

/**
 * The Class CommandEngine.
 */
public class CommandEngine {

	/** The Constant logger. */
	final static Logger logger = LogManager.getLogger(CommandEngine.class);

	final static private Gson gson = new Gson();

	/**
	 * Query.
	 *
	 * @param query
	 *            the query
	 * @return the string
	 */
	public static CMDQueryResult query(String query) {
		StorageEngine stringMapEngine = StorageEngine.getInstance();

		try (Scanner in = new Scanner(query)) {
			String call = in.next().toUpperCase();
			switch (call) {
			case "ADD": {
				String key = in.next();
				String value = in.next();
				logger.debug("ADD request recived (" + key + " , " + value + ")");
				return new CMDQueryResult(stringMapEngine.insert(key, value));
			}
			case "UPDATE": {
				String key = in.next();
				String value = in.next();
				logger.debug("UPDATE request recived (" + key + " , " + value + ")");
				return new CMDQueryResult(stringMapEngine.update(key, value));
			}
			case "DELETE": {
				String key = in.next();
				logger.debug("DELETE request recived (" + key + ")");
				return new CMDQueryResult(stringMapEngine.delete(key));
			}
			case "GET": {
				String key = in.next();
				logger.debug("GET request recived (" + key + ")");
				return new CMDQueryResult(stringMapEngine.get(key));
			}
			case "EXIST": {
				String key = in.next();
				logger.debug("EXIST request recived (" + key + ")");
				return new CMDQueryResult(stringMapEngine.exist(key));
			}
			case "MGET": {
				List<QueryResult> ress = new ArrayList<>();
				while (in.hasNext()) {
					String key = in.next();
					ress.add(stringMapEngine.get(key));
				}
				logger.debug("MGET request recived " + gson.toJson(ress));
				return new CMDQueryResult(ress.toArray(new QueryResult[1]), CMDQueryResultStatus.SUCCESSFUL);
			}
			case "EXIT": {
				logger.debug("EXIT request recived");
				return new CMDQueryResult(CMDQueryResultStatus.CLOSE_IT);
			}
			}
		} catch (NoSuchElementException e) {
			logger.error("Error on processing the request. query: " + query);
		}

		return new CMDQueryResult(CMDQueryResultStatus.FAILED);
	}
}
