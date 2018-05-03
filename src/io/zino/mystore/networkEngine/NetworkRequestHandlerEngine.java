package io.zino.mystore.networkEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;

import io.zino.mystore.commandEngine.CommandEngine;
import io.zino.mystore.storageEngine.StringMapEngine;

public class NetworkRequestHandlerEngine extends Thread {

	private static NetworkRequestHandlerEngine instance = new NetworkRequestHandlerEngine();
	private NetworkEngine networkEngine;
	private StringMapEngine stringMapEngine;
	private NetworkRequestHandler[] handlers;

	public static NetworkRequestHandlerEngine getInstance() {
		return instance;
	}

	private NetworkRequestHandlerEngine() {
		this.networkEngine = NetworkEngine.getInstance();
		this.handlers = new NetworkRequestHandler[1];
		for (int i = 0; i < handlers.length; i++) {
			handlers[i] = new NetworkRequestHandler(this.networkEngine.getSokects());
		}

		this.start();

		System.out.println("NetworkRequestHandlerEngine Started! " + System.currentTimeMillis());
	}

	@Override
	public void run() {

	}

	private class NetworkRequestHandler extends Thread {
		private Queue<Socket> requestQueue;

		public NetworkRequestHandler(Queue<Socket> requestQueue) {
			this.requestQueue = requestQueue;
			this.start();
			System.out.println("NetworkRequestHandler Started! " + System.currentTimeMillis());
		}

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
						e.printStackTrace();
					}
				}
			}
		}
	}
}
