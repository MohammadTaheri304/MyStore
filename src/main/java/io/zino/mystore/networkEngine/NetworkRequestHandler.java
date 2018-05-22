package io.zino.mystore.networkEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;

import org.apache.log4j.Logger;

import io.zino.mystore.commandEngine.CommandEngine;

// TODO: Auto-generated Javadoc
/**
 * The Class NetworkRequestHandler.
 */
final public class NetworkRequestHandler extends Thread {
	
	/** The Constant logger. */
	final static Logger logger = Logger.getLogger(NetworkRequestHandler.class);
	
	/** The request queue. */
	private Queue<Socket> requestQueue;

	/**
	 * Instantiates a new network request handler.
	 *
	 * @param requestQueue the request queue
	 */
	public NetworkRequestHandler(Queue<Socket> requestQueue) {
		this.requestQueue = requestQueue;
		this.start();
		System.out.println("NetworkRequestHandler Started! " + System.currentTimeMillis());
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		while (true) {
			Socket socket = this.requestQueue.poll();
			if (socket != null) {
				try {
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

					String inputLine;
					if ((inputLine = in.readLine()) != null) {
						String result = CommandEngine.query(inputLine);
						out.println(result);
					}
					this.requestQueue.add(socket);
				} catch (IOException e) {
					logger.error("error on sockets", e);
				}
			} else {
				synchronized (this.requestQueue) {
					try {
						this.requestQueue.wait();
					} catch (InterruptedException e) {
						logger.error("error on wait!", e);
					}
				}
				
			}
		}
	}
}