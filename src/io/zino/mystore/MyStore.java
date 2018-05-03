package io.zino.mystore;

import io.zino.mystore.networkEngine.NetworkEngine;
import io.zino.mystore.networkEngine.NetworkRequestHandlerEngine;
import io.zino.mystore.storageEngine.StringMapEngine;

public class MyStore {

	public static void main(String[] args) {
		System.out.println("Hello there!!!");
		
		StringMapEngine.getInstance();
		NetworkEngine.getInstance();
		NetworkRequestHandlerEngine.getInstance();
		
		System.out.println("MyStore Started! "+System.currentTimeMillis());
	}
}
