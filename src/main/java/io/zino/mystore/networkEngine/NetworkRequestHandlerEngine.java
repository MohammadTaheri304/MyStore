package io.zino.mystore.networkEngine;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.zino.mystore.ConfigMgr;

/**
 * The Class NetworkRequestHandlerEngine.
 */
final public class NetworkRequestHandlerEngine{
	
	/** The Constant logger. */
	final static Logger logger = LogManager.getLogger(NetworkRequestHandlerEngine.class);
	
	/** The instance. */
	private static NetworkRequestHandlerEngine instance = new NetworkRequestHandlerEngine();
	
	/** The network engine. */
	private NetworkEngine networkEngine;
	
	/** The handlers. */
	private NetworkRequestHandler[] handlers;

	/**
	 * Gets the single instance of NetworkRequestHandlerEngine.
	 *
	 * @return single instance of NetworkRequestHandlerEngine
	 */
	public static NetworkRequestHandlerEngine getInstance() {
		return instance;
	}

	/**
	 * Instantiates a new network request handler engine.
	 */
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
