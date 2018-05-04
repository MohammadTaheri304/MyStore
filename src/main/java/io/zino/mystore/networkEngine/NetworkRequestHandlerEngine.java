package io.zino.mystore.networkEngine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Queue;

import io.zino.mystore.ConfigMgr;
import io.zino.mystore.commandEngine.CommandEngine;
import io.zino.mystore.storageEngine.StorageEngine;

public class NetworkRequestHandlerEngine extends Thread {

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

		this.start();

		System.out.println("NetworkRequestHandlerEngine Started! " + System.currentTimeMillis());
	}

	@Override
	public void run() {
		//TODO what to do?
	}

	
}
