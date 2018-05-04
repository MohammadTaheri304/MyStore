package io.zino.mystore.clusterEngine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.zino.mystore.ConfigMgr;
import io.zino.mystore.clusterEngine.ClusterRequest.RequestType;
import io.zino.mystore.storageEngine.StorageEngine;
import io.zino.mystore.storageEngine.StorageEntry;

public class ClusterEngine extends Thread {
	public static final String NODE_ID = ConfigMgr.getInstance().get("ClusterEngine.node.id");
	private static ClusterEngine instance = new ClusterEngine();
	private Map<String, ClusterNode> nodeMap;

	public static ClusterEngine getInstance() {
		return instance;
	}

	private ClusterEngine() {
		this.nodeMap = new ConcurrentHashMap();
		long nodeCount = Long.parseLong(ConfigMgr.getInstance().get("ClusterEngine.adjNodeCount"));
		for (int i = 1; i <= nodeCount; i++) {
			String nodeid = ConfigMgr.getInstance().get("ClusterEngine.node." + i + ".nodeUid");
			String address = ConfigMgr.getInstance().get("ClusterEngine.node." + i + ".address");
			int port = Integer.parseInt(ConfigMgr.getInstance().get("ClusterEngine.node." + i + ".port"));
			this.nodeMap.put(nodeid, new ClusterNode(nodeid, address, port));
		}
		this.start();

		System.out.println("ClusterEngine Started! " + System.currentTimeMillis());
	}

	public StorageEntry getRequest(StorageEntry storageEntry) {
		return this.sendRequest(new ClusterRequest(RequestType.GET, storageEntry));
	}

	public StorageEntry addRequest(StorageEntry storageEntry) {
		return this.sendRequest(new ClusterRequest(RequestType.ADD, storageEntry));
	}

	public StorageEntry updateRequest(StorageEntry storageEntry) {
		return this.sendRequest(new ClusterRequest(RequestType.UPDATE, storageEntry));
	}

	public StorageEntry deleteRequest(StorageEntry storageEntry) {
		return this.sendRequest(new ClusterRequest(RequestType.DELETE, storageEntry));
	}

	private StorageEntry sendRequest(ClusterRequest request) {
		ClusterNode dest = nodeMap.get(request.getStorageEntry().getNodeId());
		try {
			Socket clientSocket = new Socket(dest.getAddress(), dest.getPort());
			ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
			ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());

			out.writeObject(request);
			StorageEntry response = ((StorageEntry) input.readObject());
			out.close();
			clientSocket.close();
			return response;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void run() {
		try {
			int port = Integer.parseInt(ConfigMgr.getInstance().get("ClusterEngine.node.port"));

			@SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(port);
			while (true) {
				Socket clientSocket = serverSocket.accept();
				ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
				ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());

				ClusterRequest request = ((ClusterRequest) input.readObject());
				StorageEntry rse = request.getStorageEntry();
				StorageEntry result = null;
				switch (request.getRequestType()) {
				case GET: {
					result = StorageEngine.getInstance().get(rse);
					break;
				}
				case ADD: {
					result = StorageEngine.getInstance().insert(rse);
					break;
				}
				case UPDATE: {
					result = StorageEngine.getInstance().update(rse);
					break;
				}
				case DELETE: {
					result = StorageEngine.getInstance().delete(rse);
					break;
				}
				}

				out.writeObject(result);
				out.close();
				clientSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
