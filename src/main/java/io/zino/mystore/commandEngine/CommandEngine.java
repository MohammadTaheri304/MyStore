package io.zino.mystore.commandEngine;

import java.util.NoSuchElementException;
import java.util.Scanner;

import io.zino.mystore.storageEngine.QueryResult;
import io.zino.mystore.storageEngine.StorageEngine;

public class CommandEngine {
	public static String query(String query) {
		StorageEngine stringMapEngine = StorageEngine.getInstance();

		try {
			Scanner in = new Scanner(query);
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
			e.printStackTrace();
		}

		return new QueryResult(null, null, "QUERY_FAILED").toString();
	}
}
