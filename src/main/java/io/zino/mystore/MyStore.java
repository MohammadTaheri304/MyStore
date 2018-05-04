package io.zino.mystore;

import io.zino.mystore.clusterEngine.ClusterEngine;
import io.zino.mystore.networkEngine.NetworkEngine;
import io.zino.mystore.networkEngine.NetworkRequestHandlerEngine;
import io.zino.mystore.storageEngine.StorageEngine;

public class MyStore {

	public static void main(String[] args) {
		System.out.println("Hello there!!!");
		
		StorageEngine.getInstance();
		NetworkEngine.getInstance();
		NetworkRequestHandlerEngine.getInstance();
		ClusterEngine.getInstance();
		
		System.out.println("MyStore Started! "+System.currentTimeMillis());
	}
}
