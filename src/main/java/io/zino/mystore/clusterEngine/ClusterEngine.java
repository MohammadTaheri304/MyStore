package io.zino.mystore.clusterEngine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import io.zino.mystore.ConfigMgr;
import io.zino.mystore.clusterEngine.ClusterRequest.RequestType;
import io.zino.mystore.storageEngine.StorageEngine;
import io.zino.mystore.storageEngine.StorageEntry;

public class ClusterEngine extends Thread {
	final static Logger logger = Logger.getLogger(ClusterEngine.class);

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
		return this.sendRequest(storageEntry.getNodeId(), new ClusterRequest(RequestType.GET, storageEntry));
	}

	public StorageEntry addRequest(StorageEntry storageEntry) {
		nodeMap.keySet().forEach(nodeId -> this.sendRequest(nodeId, new ClusterRequest(RequestType.ADD, storageEntry)));
		return storageEntry;
	}

	public StorageEntry updateRequest(StorageEntry storageEntry) {
		return this.sendRequest(storageEntry.getNodeId(), new ClusterRequest(RequestType.UPDATE, storageEntry));
	}

	public StorageEntry deleteRequest(StorageEntry storageEntry) {
		return this.sendRequest(storageEntry.getNodeId(), new ClusterRequest(RequestType.DELETE, storageEntry));
	}

	private StorageEntry sendRequest(String destId, ClusterRequest request) {
		ClusterNode dest = nodeMap.get(destId);
		try {
			Socket clientSocket = new Socket(dest.getAddress(), dest.getPort());
			if (clientSocket == null)
				return null;

			ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
			ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());

			out.writeObject(request);
			StorageEntry response = ((StorageEntry) input.readObject());
			out.close();
			clientSocket.close();
			return response;
		} catch (ClassNotFoundException | IOException e) {
			logger.error("Error on sending request", e);
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
		} catch (ClassNotFoundException | IOException e) {
			logger.error("Error on Cluster listener", e);
		}
	}
}
