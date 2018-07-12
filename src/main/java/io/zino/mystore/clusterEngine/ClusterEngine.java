package io.zino.mystore.clusterEngine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.Key;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.zino.mystore.ConfigMgr;
import io.zino.mystore.clusterEngine.ClusterRequest.RequestType;
import io.zino.mystore.storageEngine.StorageEngine;
import io.zino.mystore.storageEngine.StorageEntry;
import io.zino.mystore.util.security.CryptographyUtil;

// TODO: Auto-generated Javadoc
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
			if (dest==null || dest.objectOutputStream == null 
					|| dest.objectInputStream == null 
					|| dest.publickey == null
					|| dest.connectionDesKey == null) {
				try {
					prepareClusterNodeForSendingRequest(dest);
				} catch (IOException e) {
					logger.error("Error on resetting I/O Streams for cluster node", e);
					return null;
				}
			}
			
			request.setStorageEntry(encryptStorageEntry(request.getStorageEntry(), dest.connectionDesKey));
			dest.objectOutputStream.writeObject(request);
			StorageEntry response = ((StorageEntry) dest.objectInputStream.readObject());
			response = decryptStorageEntry(response, dest.connectionDesKey);
			
			return response;
		} catch (ClassNotFoundException | IOException e) {
			logger.error("Error on sending request", e);
		}

		return null;
	}

	/**
	 * Prepare cluster node for sending request.
	 *
	 * @param clusterNode the cluster node
	 * @throws UnknownHostException the unknown host exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	private void prepareClusterNodeForSendingRequest(ClusterNode clusterNode) throws UnknownHostException, IOException, ClassNotFoundException {
		// Open socket and get in/out streams
		Socket clientSocket = new Socket(clusterNode.getAddress(), clusterNode.getPort());
		clusterNode.objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
		clusterNode.objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
		
		// do the node_register request and get other side`s public key
		ClusterRequest registerRequest = new ClusterRequest(CryptographyUtil.getPublicKey(),
				RequestType.NODE_REGISTER);
		clusterNode.objectOutputStream.writeObject(registerRequest);
		clusterNode.objectOutputStream.flush();
		clusterNode.publickey = (Key) clusterNode.objectInputStream.readObject();
		
		// do NODE_DES_REGISTER request
		clusterNode.connectionDesKey = CryptographyUtil.generateDESKey();
		byte[] encodedKey = clusterNode.connectionDesKey.getEncoded();
		byte[] encryptedEncodedKey = CryptographyUtil.EncryptRSA(encodedKey, clusterNode.publickey);
		ClusterRequest decRegisterRequest = new ClusterRequest(RequestType.NODE_DES_REGISTER);
		decRegisterRequest.setDesKey(encryptedEncodedKey);
		clusterNode.objectOutputStream.writeObject(decRegisterRequest);
		clusterNode.objectOutputStream.flush();
	}

	/**
	 * Encrypt storage entry.
	 *
	 * @param storageEntry the storage entry
	 * @param key the key
	 * @return the storage entry
	 */
	private StorageEntry encryptStorageEntry(final StorageEntry storageEntry, final Key key){
		if(storageEntry==null) return null;
		StorageEntry clone = storageEntry.clone();
		clone.setKey(CryptographyUtil.EncryptDES(clone.getKey(), key));
		clone.setData(CryptographyUtil.EncryptDES(clone.getData(), key));
		return clone;
	}
	
	/**
	 * Decrypt storage entry.
	 *
	 * @param storageEntry the storage entry
	 * @param key the key
	 * @return the storage entry
	 */
	private StorageEntry decryptStorageEntry(final StorageEntry storageEntry, final Key key){
		if(storageEntry==null) return null;
		StorageEntry clone = storageEntry.clone();
		clone.setKey(CryptographyUtil.DecryptDES(clone.getKey(), key));
		clone.setData(CryptographyUtil.DecryptDES(clone.getData(), key));
		return clone;
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
			new Thread(new IncommingClusterRequestHandler(clientSocket)).start();
		}
	}
	
	/**
	 * The Class IncommingClusterRequestHandler.
	 */
	public class IncommingClusterRequestHandler implements Runnable {
		
		/** The client socket. */
		private Socket clientSocket;
		
		/** The input socket public key. */
		private Key inputSocketPublicKey = null;
		
		/** The input socket des key. */
		private Key inputSocketDesKey = null;
		
		/** The out. */
		private ObjectOutputStream out = null;
		
		/** The input. */
		private ObjectInputStream input = null;
		
		/**
		 * Instantiates a new incomming cluster request handler.
		 *
		 * @param clientSocket the client socket
		 */
		public IncommingClusterRequestHandler(Socket clientSocket) {
			this.clientSocket = clientSocket;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				out = new ObjectOutputStream(clientSocket.getOutputStream());
				input = new ObjectInputStream(clientSocket.getInputStream());
			} catch (IOException e) {
				logger.error("Error on Cluster listener", e);
				try {
					clientSocket.close();
				} catch (IOException e1) {
					logger.error("Error on closing client socket", e1);
				} finally {
					return;
				}
			}

			if(!registerNode()) return;
			
			if(!decRegisteration()) return;

			while (true) {
				ClusterRequest request = null;
				try {
					request = ((ClusterRequest) input.readObject());
				} catch (ClassNotFoundException | IOException e) {
					logger.error("Error on Cluster listener", e);
					break;
				}
				request.setStorageEntry(decryptStorageEntry(request.getStorageEntry(), inputSocketDesKey));
				StorageEntry result = handleRequest(request);
				try {
					result = encryptStorageEntry(result, inputSocketDesKey);
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
		}

		/**
		 * Dec registeration.
		 *
		 * @return true, if successful
		 */
		private boolean decRegisteration() {
			ClusterRequest decRegisterationRequest = null;
			try {
				decRegisterationRequest = ((ClusterRequest) this.input.readObject());
			} catch (ClassNotFoundException | IOException e) {
				logger.error("Error on Cluster listener", e);
				try {
					clientSocket.close();
				} catch (IOException e1) {
					logger.error("Error on closing client socket", e1);
				} 
				return false;
			}
			if (!RequestType.NODE_DES_REGISTER.equals(decRegisterationRequest.getRequestType())) {
				logger.error("Not a dec register request!");
				try {
					clientSocket.close();
				} catch (IOException e1) {
					logger.error("Error on closing client socket", e1);
					return false;
				}
			} else {
				byte[] encryptedEncodedKey = decRegisterationRequest.getDesKey();
				byte[] encodedKey = CryptographyUtil.DecryptRSA(encryptedEncodedKey);
				this.inputSocketDesKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "DES"); 
			}
			return true;
		}

		/**
		 * Register node.
		 *
		 * @return true, if successful
		 */
		private boolean registerNode() {
			Key inputSocketPublicKey;
			ClusterRequest registerRequest = null;
			try {
				registerRequest = ((ClusterRequest) this.input.readObject());
			} catch (ClassNotFoundException | IOException e) {
				logger.error("Error on Cluster listener", e);
				try {
					clientSocket.close();
				} catch (IOException e1) {
					logger.error("Error on closing client socket", e1);
				}
				return false;
			}

			if (!RequestType.NODE_REGISTER.equals(registerRequest.getRequestType())) {
				logger.error("Not a register request!");
				try {
					clientSocket.close();
				} catch (IOException e1) {
					logger.error("Error on closing client socket", e1);
					return false;
				} 
			} else {
				inputSocketPublicKey = registerRequest.getPublicKey();
				try {
					out.writeObject(CryptographyUtil.getPublicKey());
				} catch (IOException e) {
					logger.error("Error on Cluster listener", e);
					try {
						clientSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					return false;
				}
			}
			return true;
		}
		
		/**
		 * Handle request.
		 *
		 * @param request the request
		 * @return the storage entry
		 */
		private StorageEntry handleRequest(ClusterRequest request){
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
			return result;
		}
	} 
}
