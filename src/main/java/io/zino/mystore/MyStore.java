package io.zino.mystore;

import io.zino.mystore.clusterEngine.ClusterEngine;
import io.zino.mystore.networkEngine.NetworkEngine;
import io.zino.mystore.networkEngine.NetworkRequestHandlerEngine;
import io.zino.mystore.storageEngine.StorageEngine;
import io.zino.mystore.storageEngine.fileStorageEngine.FileStorageEngineOptimizer;
import io.zino.mystore.util.security.CryptographyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class MyStore.
 * This is main class for server!
 */
public class MyStore {
	
	/** The Constant logger. */
	final static Logger logger = LoggerFactory.getLogger(MyStore.class);

	/**
	 * The main method.
	 * Get Instance from different modules to make them run!
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		System.out.println("Hello there!!!");
		
		CryptographyUtil.getPublicKey();
		ConfigMgr.getInstance();
		StorageEngine.getInstance();
		FileStorageEngineOptimizer.getInstance();
		ClusterEngine.getInstance();
		NetworkEngine.getInstance();
		NetworkRequestHandlerEngine.getInstance();
		
		System.out.println("MyStore Started! "+System.currentTimeMillis());
	}
}
