package io.zino.mystore.networkEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import io.zino.mystore.commandEngine.CMDQueryResult;
import io.zino.mystore.commandEngine.CMDQueryResult.CMDQueryResultStatus;
import io.zino.mystore.commandEngine.CommandEngine;

// TODO: Auto-generated Javadoc
/**
 * The Class NetworkRequestHandler.
 */
final public class NetworkRequestHandler extends Thread {

	/** The Constant logger. */
	final static Logger logger = LogManager.getLogger(NetworkRequestHandler.class);

	/** The request queue. */
	private Queue<Socket> requestQueue;

	/**
	 * Instantiates a new network request handler.
	 *
	 * @param requestQueue
	 *            the request queue
	 */
	public NetworkRequestHandler(Queue<Socket> requestQueue) {
		this.requestQueue = requestQueue;
		this.start();
		System.out.println("NetworkRequestHandler Started! " + System.currentTimeMillis());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		final Gson gson = new Gson();
		while (true) {
			Socket socket = this.requestQueue.poll();
			if (socket != null) {
				try {
					boolean addSocketToQueue = true;
					PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					if (in.ready()) {
						String inputLine = null;
						if ((inputLine = in.readLine()) != null) {
							CMDQueryResult queryResult = CommandEngine.query(inputLine);
							String result = gson.toJson(queryResult);
							out.println(result);
							addSocketToQueue = CMDQueryResultStatus.CLOSE_IT.equals(queryResult.getStatus()) ? false
									: true;
						}
					}
					if (addSocketToQueue) {
						this.requestQueue.add(socket);
					} else {
						socket.shutdownInput();
						socket.shutdownOutput();
						socket.close();
					}
				} catch (IOException e) {
					logger.error("error on sockets");
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