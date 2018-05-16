package io.zino.mystore;

import org.apache.log4j.Logger;

import io.zino.mystore.clusterEngine.ClusterEngine;
import io.zino.mystore.networkEngine.NetworkEngine;
import io.zino.mystore.networkEngine.NetworkRequestHandlerEngine;
import io.zino.mystore.storageEngine.StorageEngine;

public class MyStore {
	final static Logger logger = Logger.getLogger(MyStore.class);

	public static void main(String[] args) {
		System.out.println("Hello there!!!");
		
		StorageEngine.getInstance();
		NetworkEngine.getInstance();
		NetworkRequestHandlerEngine.getInstance();
		ClusterEngine.getInstance();
		
		
		System.out.println("MyStore Started! "+System.currentTimeMillis());
	}
}
