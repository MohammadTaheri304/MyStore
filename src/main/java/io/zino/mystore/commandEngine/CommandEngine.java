package io.zino.mystore.commandEngine;

import java.util.NoSuchElementException;
import java.util.Scanner;

import org.apache.log4j.Logger;

import io.zino.mystore.storageEngine.QueryResult;
import io.zino.mystore.storageEngine.StorageEngine;

// TODO: Auto-generated Javadoc
/**
 * The Class CommandEngine.
 */
public class CommandEngine {
	
	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(CommandEngine.class);
	
	/**
	 * Query.
	 *
	 * @param query the query
	 * @return the string
	 */
	public static String query(String query) {
		StorageEngine stringMapEngine = StorageEngine.getInstance();

		try(Scanner in = new Scanner(query)) {
			String call = in.next().toUpperCase();
			switch (call) {
			case "ADD": {
				String key = in.next();
				String value = in.next();
				return stringMapEngine.insert(key, value).toString();
			}
			case "UPDATE": {
				String key = in.next();
				String value = in.next();
				return stringMapEngine.update(key, value).toString();
			}
			case "DELETE": {
				String key = in.next();
				return stringMapEngine.delete(key).toString();
			}
			case "GET": {
				String key = in.next();
				return stringMapEngine.get(key).toString();
			}
			case "EXIST": {
				String key = in.next();
				return stringMapEngine.exist(key).toString();
			}
			}
		} catch (NoSuchElementException e) {
			logger.error("Error on processing the request. query: "+query);
		} 

		return new QueryResult(null, null, "QUERY_FAILED").toString();
	}
}
