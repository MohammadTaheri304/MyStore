package io.zino.mystore.commandEngine;

import java.util.Scanner;

import io.zino.mystore.storageEngine.StringMapEngine;

public class CommandEngine {
	public static String query(String query) {
		StringMapEngine stringMapEngine = StringMapEngine.getInstance();
	
		Scanner in = new Scanner(query);
		String call = in.next().toUpperCase();
		switch (call) {
		case "ADD": {
			String key = in.next();
			String value = in.next();
			return stringMapEngine.insert(key, value)+"";
		} case "UPDATE": {
			String key = in.next();
			String value = in.next();
			return stringMapEngine.update(key, value)+"";
		} case "DELETE": {
			String key = in.next();
			return stringMapEngine.delete(key)+"";
		} case "GET": {
			String key = in.next();
			return stringMapEngine.get(key);
		} case "EXIST": {
			String key = in.next();
			return stringMapEngine.exist(key)+"";
		}
		}

		return null;
	}
}
