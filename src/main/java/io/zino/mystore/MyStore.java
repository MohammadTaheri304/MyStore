package io.zino.mystore;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.zino.mystore.clusterEngine.ClusterEngine;
import io.zino.mystore.networkEngine.NetworkEngine;
import io.zino.mystore.networkEngine.NetworkRequestHandlerEngine;
import io.zino.mystore.storageEngine.StorageEngine;
import io.zino.mystore.storageEngine.fileStorageEngine.FileStorageEngineOptimizer;

/**
 * The Class MyStore.
 * This is main class for server!
 */
public class MyStore {
	
	/** The Constant logger. */
	final static Logger logger = LogManager.getLogger(MyStore.class);

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
		FileStorageEngineOptimizer.getInstance();
		ClusterEngine.getInstance();
		NetworkEngine.getInstance();
		NetworkRequestHandlerEngine.getInstance();
		
		System.out.println("MyStore Started! "+System.currentTimeMillis());
	}
}
