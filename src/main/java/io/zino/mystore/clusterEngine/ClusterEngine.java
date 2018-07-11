package io.zino.mystore.clusterEngine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Key;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.zino.mystore.ConfigMgr;
import io.zino.mystore.clusterEngine.ClusterRequest.RequestType;
import io.zino.mystore.storageEngine.StorageEngine;
import io.zino.mystore.storageEngine.StorageEntry;
import io.zino.mystore.util.security.CryptographyUtil;

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
			if (dest.objectOutputStream == null || dest.objectInputStream == null || dest.publickey == null
					|| dest.connectionDesKey == null) {
				try {
					Socket clientSocket = new Socket(dest.getAddress(), dest.getPort());
					dest.objectOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
					dest.objectInputStream = new ObjectInputStream(clientSocket.getInputStream());
					ClusterRequest registerRequest = new ClusterRequest(CryptographyUtil.getPublicKey(),
							RequestType.NODE_REGISTER);
					dest.objectOutputStream.writeObject(registerRequest);
					dest.objectOutputStream.flush();
					dest.publickey = (Key) dest.objectInputStream.readObject();
					dest.connectionDesKey = CryptographyUtil.generateDESKey();
					
					byte[] encodedKey = dest.connectionDesKey.getEncoded();
					byte[] encryptedEncodedKey = CryptographyUtil.EncryptRSA(encodedKey, dest.publickey);
					ClusterRequest decRegisterRequest = new ClusterRequest(RequestType.NODE_DES_REGISTER);
					decRegisterRequest.setDesKey(encryptedEncodedKey);
					dest.objectOutputStream.writeObject(decRegisterRequest);
					dest.objectOutputStream.flush();
				} catch (IOException e) {
					logger.error("Error on resetting I/O Streams for cluster node", e);
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

	private StorageEntry encryptStorageEntry(StorageEntry storageEntry, Key key){
		if(storageEntry==null) return null;
		storageEntry.setKey(CryptographyUtil.EncryptDES(storageEntry.getKey(), key));
		storageEntry.setData(CryptographyUtil.EncryptDES(storageEntry.getData(), key));
		return storageEntry;
	}
	
	private StorageEntry decryptStorageEntry(StorageEntry storageEntry, Key key){
		if(storageEntry==null) return null;
		storageEntry.setKey(CryptographyUtil.DecryptDES(storageEntry.getKey(), key));
		storageEntry.setData(CryptographyUtil.DecryptDES(storageEntry.getData(), key));
		return storageEntry;
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
				Key inputSocketDesKey = null;
				ObjectOutputStream out = null;
				ObjectInputStream input = null;
				try {
					out = new ObjectOutputStream(clientSocket.getOutputStream());
					input = new ObjectInputStream(clientSocket.getInputStream());
				} catch (IOException e) {
					logger.error("Error on Cluster listener", e);
					try {
						clientSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					} finally {
						return;
					}
				}

				ClusterRequest register_request = null;
				try {
					register_request = ((ClusterRequest) input.readObject());
				} catch (ClassNotFoundException | IOException e) {
					logger.error("Error on Cluster listener", e);
					try {
						clientSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					} finally {
						return;
					}
				}

				if (!RequestType.NODE_REGISTER.equals(register_request.getRequestType())) {
					logger.error("Not a register request!");
					try {
						clientSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					} finally {
						return;
					}
				} else {
					inputSocketPublicKey = register_request.getPublicKey();
					try {
						out.writeObject(CryptographyUtil.getPublicKey());
					} catch (IOException e) {
						logger.error("Error on Cluster listener", e);
						try {
							clientSocket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						} finally {
							return;
						}
					}
				}
				
				ClusterRequest decRegisterationRequest = null;
				try {
					decRegisterationRequest = ((ClusterRequest) input.readObject());
				} catch (ClassNotFoundException | IOException e) {
					logger.error("Error on Cluster listener", e);
					try {
						clientSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					} finally {
						return;
					}
				}
				if (!RequestType.NODE_DES_REGISTER.equals(decRegisterationRequest.getRequestType())) {
					logger.error("Not a dec register request!");
					try {
						clientSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					} finally {
						return;
					}
				} else {
					byte[] encryptedEncodedKey = decRegisterationRequest.getDesKey();
					byte[] encodedKey = CryptographyUtil.DecryptRSA(encryptedEncodedKey);
					inputSocketDesKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "DES"); 
				}

				while (true) {
					ClusterRequest request = null;
					try {
						request = ((ClusterRequest) input.readObject());
					} catch (ClassNotFoundException | IOException e) {
						logger.error("Error on Cluster listener", e);
						break;
					}
					StorageEntry rse = request.getStorageEntry();
					rse = decryptStorageEntry(rse, inputSocketDesKey);
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
			}).start();
		}
	}
}
