package io.zino.mystore.networkEngine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

import io.zino.mystore.ConfigMgr;

/**
 * The Class NetworkEngine.
 */
public class NetworkEngine extends Thread{
	
	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(NetworkEngine.class);

	/** The instance. */
	private static NetworkEngine instance = new NetworkEngine();
	
	/** The sockets. */
	private Queue<Socket> sockets;
	
	/**
	 * Gets the single instance of NetworkEngine.
	 *
	 * @return single instance of NetworkEngine
	 */
	public static NetworkEngine getInstance() {
		return instance;
	}
	
	/**
	 * Instantiates a new network engine.
	 */
	private NetworkEngine() {
		this.sockets = new ConcurrentLinkedQueue<Socket>();
		this.start();
		
		System.out.println("NetworkEngine Started! "+System.currentTimeMillis());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			int port = Integer.parseInt(ConfigMgr.getInstance().get("NetworkEngine.port"));
			
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				Socket socket = serverSocket.accept();
				sockets.add(socket);
				synchronized (this.sockets) {
					if(this.sockets.size()==1){
						this.sockets.notify();
					}
				}
				System.out.println("new socket added to socket queue");
			}	
		} catch (IOException e) {
			logger.error("Error on accept new socket!", e);
		}
	}

	
	/**
	 * Gets the sokects.
	 *
	 * @return the sokects
	 */
	public Queue<Socket> getSokects() {
		return sockets;
	}

	/**
	 * Sets the sokects.
	 *
	 * @param sokects the new sokects
	 */
	public void setSokects(Queue<Socket> sokects) {
		this.sockets = sokects;
	}	
	
}
