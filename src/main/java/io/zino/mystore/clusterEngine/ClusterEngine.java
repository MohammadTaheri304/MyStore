package io.zino.mystore.clusterEngine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.zino.mystore.ConfigMgr;
import io.zino.mystore.clusterEngine.ClusterRequest.RequestType;
import io.zino.mystore.storageEngine.StorageEngine;
import io.zino.mystore.storageEngine.StorageEntry;
import io.zino.mystore.utile.security.cryptographyUtil;

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
			ClusterNode clusterNode = new ClusterNode(nodeid, address, port);
			try {
				Socket clientSocket = new Socket(address, port);
				clusterNode.objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
				clusterNode.objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
			} catch (IOException e) {
				logger.error("Error on setting I/O Streams for cluster node", e);
			}
			this.nodeMap.put(nodeid, clusterNode);

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
			// old code <<< need to be here until cryptoUtile be fixed >>>
			// Socket clientSocket = new Socket(dest.getAddress(),
			// dest.getPort());
			// ObjectOutputStream out = new
			// ObjectOutputStream(clientSocket.getOutputStream());
			// ObjectInputStream input = new
			// ObjectInputStream(clientSocket.getInputStream());
			//
			// ClusterRequest registerRequest = new
			// ClusterRequest(cryptographyUtil.getPublicKey());
			// out.writeObject(registerRequest);
			// Key serverPublickey = (Key) input.readObject();
			//
			// request = this.encryptClusterRequest(request, serverPublickey);
			//
			// out.writeObject(request);
			// StorageEntry response = ((StorageEntry) input.readObject());
			// out.close();
			// clientSocket.close();
			// =======
			if (dest.objectOutputStream == null || dest.objectInputStream == null) {
				try {
					Socket clientSocket = new Socket(dest.getAddress(), dest.getPort());
					dest.objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
					dest.objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
				} catch (IOException e) {
					logger.error("Error on resetting I/O Streams for cluster node", e);
				}
			}

			dest.objectOutputStream.writeObject(request);
			StorageEntry response = ((StorageEntry) dest.objectInputStream.readObject());

			return response;
		} catch (ClassNotFoundException | IOException e) {
			logger.error("Error on sending request", e);
		}

		return null;
	}

	/**
	 * Encrypt cluster request.
	 *
	 * @param clusterRequest
	 *            the cluster request
	 * @param serverKey
	 *            the server key
	 * @return the cluster request
	 */
	private ClusterRequest encryptClusterRequest(ClusterRequest clusterRequest, Key serverKey) {
		String key = clusterRequest.getStorageEntry().getKey();
		String data = clusterRequest.getStorageEntry().getData();
		key = cryptographyUtil.SignByOwnedKeyAndEncryptWithGivenKey(key, serverKey);
		data = cryptographyUtil.SignByOwnedKeyAndEncryptWithGivenKey(data, serverKey);
		clusterRequest.getStorageEntry().setKey(key);
		clusterRequest.getStorageEntry().setData(data);
		return clusterRequest;
	}

	/**
	 * Decrypt cluster request.
	 *
	 * @param clusterRequest
	 *            the cluster request
	 * @param serverKey
	 *            the server key
	 * @return the cluster request
	 */
	private ClusterRequest decryptClusterRequest(ClusterRequest clusterRequest, Key serverKey) {
		String key = clusterRequest.getStorageEntry().getKey();
		String data = clusterRequest.getStorageEntry().getData();
		key = cryptographyUtil.DecryptByGivenKeyANDDecryptByOwnedKey(key, serverKey);
		data = cryptographyUtil.DecryptByGivenKeyANDDecryptByOwnedKey(data, serverKey);
		clusterRequest.getStorageEntry().setKey(key);
		clusterRequest.getStorageEntry().setData(data);
		return clusterRequest;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@SuppressWarnings("resource")
	@Override
	public void run() {
		int port = Integer.parseInt(ConfigMgr.getInstance().get("ClusterEngine.node.port"));

		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
			logger.info("ClusterEngine start on port " + port);
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
				Key inputSocketPublicKey = null;
				ObjectOutputStream out = null;
				ObjectInputStream input = null;
				try {
					out = new ObjectOutputStream(clientSocket.getOutputStream());
					input = new ObjectInputStream(clientSocket.getInputStream());
				} catch (IOException e) {
					logger.error("Error on Cluster listener", e);
					return;
				}

				//// old code <<< need to be here until cryptoUtile be fixed >>>
				// ClusterRequest register_request = null;
				// try {
				// register_request = ((ClusterRequest) input.readObject());
				// } catch (ClassNotFoundException | IOException e) {
				// logger.error("Error on Cluster listener", e);
				// return;
				// }
				//
				// if
				//// (!RequestType.NODE_REGISTER.equals(register_request.getRequestType()))
				//// {
				// logger.error("Not a register request!");
				// return;
				// } else {
				// inputSocketPublicKey = register_request.getPublicKey();
				// }
				//
				// ClusterRequest request = null;
				// try {
				// request = ((ClusterRequest) input.readObject());
				// } catch (ClassNotFoundException | IOException e) {
				// logger.error("Error on Cluster listener", e);
				// return;
				// }
				//
				// request = decryptClusterRequest(request,
				//// inputSocketPublicKey);
				// StorageEntry rse = request.getStorageEntry();
				// StorageEntry result = null;
				// switch (request.getRequestType()) {
				// case NODE_REGISTER: {
				// logger.error("Someone trying to reset it`s publicKey.
				//// InetAddress="
				// + clientSocket.getInetAddress().toString());
				// break;
				// }
				// case GET: {
				// result = StorageEngine.getInstance().get(rse);
				// break;
				// }
				// case ADD: {
				// result = StorageEngine.getInstance().insert(rse);
				// break;
				// }
				// case UPDATE: {
				// result = StorageEngine.getInstance().update(rse);
				// break;
				// }
				// case DELETE: {
				// result = StorageEngine.getInstance().delete(rse);
				// break;
				// }
				// }
				// =======
				while (true) {
					ClusterRequest request = null;
					try {
						request = ((ClusterRequest) input.readObject());
					} catch (ClassNotFoundException | IOException e) {
						logger.error("Error on Cluster listener", e);
						break;
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
					} catch (IOException e) {
						logger.error("Error on Cluster listener", e);
					}
				}
				try {
					out.close();
				} catch (IOException e) {
					logger.error("Error on closing connection", e);
				}
			}).start();
		}
	}
}
