package io.zino.mystore.networkEngine;

import org.apache.log4j.Logger;

import io.zino.mystore.ConfigMgr;
import io.zino.mystore.storageEngine.StorageEngine;

public class NetworkRequestHandlerEngine{
	final static Logger logger = Logger.getLogger(NetworkRequestHandlerEngine.class);
	
	private static NetworkRequestHandlerEngine instance = new NetworkRequestHandlerEngine();
	private NetworkEngine networkEngine;
	private StorageEngine stringMapEngine;
	private NetworkRequestHandler[] handlers;

	public static NetworkRequestHandlerEngine getInstance() {
		return instance;
	}

	private NetworkRequestHandlerEngine() {
		this.networkEngine = NetworkEngine.getInstance();
		int size = Integer.parseInt(ConfigMgr.getInstance().get("NetworkRequestHandlerEngine.size"));
		this.handlers = new NetworkRequestHandler[size];
		for (int i = 0; i < handlers.length; i++) {
			handlers[i] = new NetworkRequestHandler(this.networkEngine.getSokects());
		}

		System.out.println("NetworkRequestHandlerEngine Started! " + System.currentTimeMillis());
	}
	
}
