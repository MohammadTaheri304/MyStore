package io.zino.mystore.networkEngine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.zino.mystore.ConfigMgr;

public class NetworkEngine extends Thread{

	private static NetworkEngine instance = new NetworkEngine();
	private Queue<Socket> sockets;
	
	public static NetworkEngine getInstance() {
		return instance;
	}
	
	private NetworkEngine() {
		this.sockets = new ConcurrentLinkedQueue<Socket>();
		this.start();
		
		System.out.println("SimpleNetworkEngine Started! "+System.currentTimeMillis());
	}
	
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
			e.printStackTrace();
		}
	}

	
	public Queue<Socket> getSokects() {
		return sockets;
	}

	public void setSokects(Queue<Socket> sokects) {
		this.sockets = sokects;
	}	
	
}
