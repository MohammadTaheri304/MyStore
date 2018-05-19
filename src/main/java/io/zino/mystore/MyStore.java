package io.zino.mystore;

import org.apache.log4j.Logger;

import io.zino.mystore.clusterEngine.ClusterEngine;
import io.zino.mystore.networkEngine.NetworkEngine;
import io.zino.mystore.networkEngine.NetworkRequestHandlerEngine;
import io.zino.mystore.storageEngine.StorageEngine;

/**
 * The Class MyStore.
 * This is main class for server!
 */
public class MyStore {
	
	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(MyStore.class);

	/**
	 * The main method.
	 * Get Instance from different modules to make them run!
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		System.out.println("Hello there!!!");
		
		ConfigMgr.getInstance();
		StorageEngine.getInstance();
		ClusterEngine.getInstance();
		NetworkEngine.getInstance();
		NetworkRequestHandlerEngine.getInstance();
		
		System.out.println("MyStore Started! "+System.currentTimeMillis());
	}
}
