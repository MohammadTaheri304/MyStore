package io.zino.mystore.clusterEngine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.zino.mystore.ConfigMgr;
import io.zino.mystore.clusterEngine.ClusterRequest.RequestType;
import io.zino.mystore.storageEngine.StorageEngine;
import io.zino.mystore.storageEngine.StorageEntry;

/**
 * The Class ClusterEngine.
 */
final public class ClusterEngine extends Thread {

	/** The Constant logger. */
	final static Logger logger = LogManager.getLogger(ClusterEngine.class);

	/** The Constant NODE_ID. */
	public static final String NODE_ID = ConfigMgr.getInstance().get("ClusterEngine.node.id");

	/** The instance. */
	private static ClusterEngine instance = new ClusterEngine();

	/** The node map. */
	private Map<String, ClusterNode> nodeMap;

	/**
	 * Gets the single instance of ClusterEngine.
	 *
	 * @return single instance of ClusterEngine
	 */
	public static ClusterEngine getInstance() {
		return instance;
	}

	/**
	 * Instantiates a new cluster engine.
	 */
	private ClusterEngine() {
		this.nodeMap = new ConcurrentHashMap<String, ClusterNode>();
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

	/**
	 * Gets the request.
	 *
	 * @param storageEntry
	 *            the storage entry
	 * @return the request
	 */
	public StorageEntry getRequest(StorageEntry storageEntry) {
		return this.sendRequest(storageEntry.getNodeId(), new ClusterRequest(RequestType.GET, storageEntry));
	}

	/**
	 * Adds the request.
	 *
	 * @param storageEntry
	 *            the storage entry
	 * @return the storage entry
	 */
	public StorageEntry addRequest(StorageEntry storageEntry) {
		nodeMap.keySet().forEach(nodeId -> this.sendRequest(nodeId, new ClusterRequest(RequestType.ADD, storageEntry)));
		return storageEntry;
	}

	/**
	 * Update request.
	 *
	 * @param storageEntry
	 *            the storage entry
	 * @return the storage entry
	 */
	public StorageEntry updateRequest(StorageEntry storageEntry) {
		return this.sendRequest(storageEntry.getNodeId(), new ClusterRequest(RequestType.UPDATE, storageEntry));
	}

	/**
	 * Delete request.
	 *
	 * @param storageEntry
	 *            the storage entry
	 * @return the storage entry
	 */
	public StorageEntry deleteRequest(StorageEntry storageEntry) {
		return this.sendRequest(storageEntry.getNodeId(), new ClusterRequest(RequestType.DELETE, storageEntry));
	}

	/**
	 * Send request.
	 *
	 * @param destId
	 *            the dest id
	 * @param request
	 *            the request
	 * @return the storage entry
	 */
	private StorageEntry sendRequest(String destId, ClusterRequest request) {
		ClusterNode dest = nodeMap.get(destId);
		try {
			Socket clientSocket = new Socket(dest.getAddress(), dest.getPort());
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		int port = Integer.parseInt(ConfigMgr.getInstance().get("ClusterEngine.node.port"));

		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
			logger.info("ClusterEngine start on port "+port);
		} catch (IOException e) {
			logger.error("Error on Cluster listener -cannot start ServerSocket", e);
			return;
		}
		while (true) {
			Socket clientSocket;
			try {
				clientSocket = serverSocket.accept();
			} catch (IOException e) {
				logger.error("Error on Cluster listener", e);
				continue;
			}
			new Thread(() -> {
				ObjectOutputStream out = null;
				ObjectInputStream input = null;
				try {
					out = new ObjectOutputStream(clientSocket.getOutputStream());
					input = new ObjectInputStream(clientSocket.getInputStream());
				} catch (IOException e) {
					logger.error("Error on Cluster listener", e);
					return;
				}

				ClusterRequest request = null;
				try {
					request = ((ClusterRequest) input.readObject());
				} catch (ClassNotFoundException | IOException e) {
					logger.error("Error on Cluster listener", e);
					return;
				}
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

				try {
					out.writeObject(result);
					out.close();
					clientSocket.close();
				} catch (IOException e) {
					logger.error("Error on Cluster listener", e);
				}

			}).start();
		}
	}
}
