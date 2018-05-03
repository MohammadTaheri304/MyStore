package io.zino.mystore.networkEngine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkEngine extends Thread{

	private static NetworkEngine instance = new NetworkEngine();
	private Queue<Socket> sokects;
	
	public static NetworkEngine getInstance() {
		return instance;
	}
	
	private NetworkEngine() {
		this.sokects = new ConcurrentLinkedQueue<Socket>();
		this.start();
		
		System.out.println("SimpleNetworkEngine Started! "+System.currentTimeMillis());
	}
	
	@Override
	public void run() {
		try {
			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(20509);
			while (true) {
				Socket socket = serverSocket.accept();
				sokects.add(socket);
				System.out.println("new socket added to socket queue");
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public Queue<Socket> getSokects() {
		return sokects;
	}

	public void setSokects(Queue<Socket> sokects) {
		this.sokects = sokects;
	}	
	
}
