package io.zino.mystore.commandEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import io.zino.mystore.storageEngine.QueryResult;
import io.zino.mystore.storageEngine.StorageEngine;

// TODO: Auto-generated Javadoc
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
				return gson.toJson(stringMapEngine.insert(key, value));
			}
			case "UPDATE": {
				String key = in.next();
				String value = in.next();
				return gson.toJson(stringMapEngine.update(key, value));
			}
			case "DELETE": {
				String key = in.next();
				return gson.toJson(stringMapEngine.delete(key));
			}
			case "GET": {
				String key = in.next();
				return gson.toJson(stringMapEngine.get(key));
			}
			case "EXIST": {
				String key = in.next();
				return gson.toJson(stringMapEngine.exist(key));
			}
			case "MGET": {
				List<QueryResult> ress = new ArrayList<>();
				while(in.hasNext()){
					String key = in.next();
					ress.add(stringMapEngine.get(key));
				}
				return  gson.toJson(ress);
			}
			}
		} catch (NoSuchElementException e) {
			logger.error("Error on processing the request. query: "+query);
		} 

		return gson.toJson(new QueryResult(null, null, "QUERY_FAILED"));
	}
}
