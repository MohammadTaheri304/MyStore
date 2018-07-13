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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;

import io.zino.mystore.ConfigMgr;
import io.zino.mystore.clusterEngine.ClusterRequest.RequestType;
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

	private final Gson gson = new Gson();
	
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
	public StorageEntry getRequest(final StorageEntry storageEntry) {
		return this.sendRequest(storageEntry.getNodeId(), new ClusterRequest(RequestType.GET, storageEntry));
	}

	/**
	 * Adds the request.
	 *
	 * @param storageEntry
	 *            the storage entry
	 * @return the storage entry
	 */
	public StorageEntry addRequest(final StorageEntry storageEntry) {
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
	public StorageEntry updateRequest(final StorageEntry storageEntry) {
		return this.sendRequest(storageEntry.getNodeId(), new ClusterRequest(RequestType.UPDATE, storageEntry));
	}

	/**
	 * Delete request.
	 *
	 * @param storageEntry
	 *            the storage entry
	 * @return the storage entry
	 */
	public StorageEntry deleteRequest(final StorageEntry storageEntry) {
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
	private StorageEntry sendRequest(final String destId, final ClusterRequest request) {
		if(request==null) return null;
		ClusterNode dest = nodeMap.get(destId);
		if(dest==null){
			logger.error("ClusterNode with id="+destId +"is null!!!");
			return null;
		}
		try {
			if (dest.objectOutputStream == null 
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
			
			logger.debug("Sending request "+gson.toJson(request));
			request.setStorageEntry(encryptStorageEntry(request.getStorageEntry(), dest.connectionDesKey));
			dest.objectOutputStream.writeObject(request);
			StorageEntry response = ((StorageEntry) dest.objectInputStream.readObject());
			response = decryptStorageEntry(response, dest.connectionDesKey);
			logger.debug("Recive request result. request="+gson.toJson(request)+" response="+gson.toJson(response));
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
		@SuppressWarnings("resource")
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
	static StorageEntry encryptStorageEntry(final StorageEntry storageEntry, final Key key){
		if(storageEntry==null) return null;
		return storageEntry.cloneWithNewKeyAndNewData(
				CryptographyUtil.EncryptDES(storageEntry.getKey(), key), 
				CryptographyUtil.EncryptDES(storageEntry.getData(), key));	 
	}
	
	/**
	 * Decrypt storage entry.
	 *
	 * @param storageEntry the storage entry
	 * @param key the key
	 * @return the storage entry
	 */
	static StorageEntry decryptStorageEntry(final StorageEntry storageEntry, final Key key){
		if(storageEntry==null) return null;
		return storageEntry.cloneWithNewKeyAndNewData(
				CryptographyUtil.DecryptDES(storageEntry.getKey(), key),
				CryptographyUtil.DecryptDES(storageEntry.getData(), key));
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
	
}
