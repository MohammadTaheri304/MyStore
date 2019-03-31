package io.zino.mystore.clusterEngine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.Key;

import javax.crypto.spec.SecretKeySpec;


import com.google.gson.Gson;

import io.zino.mystore.clusterEngine.ClusterRequest.RequestType;
import io.zino.mystore.storageEngine.StorageEngine;
import io.zino.mystore.storageEngine.StorageEntry;
import io.zino.mystore.util.security.CryptographyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class IncommingClusterRequestHandler.
 */
class IncommingClusterRequestHandler implements Runnable {
	
	/** The Constant logger. */
	final static Logger logger = LoggerFactory.getLogger(IncommingClusterRequestHandler.class);
	
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
	
	/** The gson. */
	private final Gson gson = new Gson();
	
	/**
	 * Instantiates a new incomming cluster request handler.
	 *
	 * @param clientSocket the client socket
	 */
	public IncommingClusterRequestHandler(final Socket clientSocket) {
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
			request.setStorageEntry(ClusterEngine.decryptStorageEntry(request.getStorageEntry(), inputSocketDesKey));
			StorageEntry result = handleRequest(request);
			try {
				result = ClusterEngine.encryptStorageEntry(result, inputSocketDesKey);
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
		ClusterRequest registerRequest = null;
		try {
			registerRequest = ((ClusterRequest) this.input.readObject());
		} catch (ClassNotFoundException | IOException e) {
			logger.error("Error on Cluster listener", e);
			try {
				this.clientSocket.close();
			} catch (IOException e1) {
				logger.error("Error on closing client socket", e1);
			}
			return false;
		}

		if (!RequestType.NODE_REGISTER.equals(registerRequest.getRequestType())) {
			logger.error("Not a register request!");
			try {
				this.clientSocket.close();
			} catch (IOException e1) {
				logger.error("Error on closing client socket", e1);
				return false;
			} 
		} else {
			this.inputSocketPublicKey = registerRequest.getPublicKey();
			try {
				this.out.writeObject(CryptographyUtil.getPublicKey());
			} catch (IOException e) {
				logger.error("Error on Cluster listener", e);
				try {
					this.clientSocket.close();
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
	private StorageEntry handleRequest(final ClusterRequest request){
		StorageEntry rse = request.getStorageEntry();
		StorageEntry result = null;
		switch (request.getRequestType()) {
		case GET: {
			result = StorageEngine.getInstance().get(rse);
			logger.debug("GET request recived result="+gson.toJson(result));
			break;
		}
		case ADD: {
			result = StorageEngine.getInstance().insert(rse);
			logger.debug("ADD request recived result="+gson.toJson(result));
			break;
		}
		case UPDATE: {
			result = StorageEngine.getInstance().update(rse);
			logger.debug("UPDATE request recived result="+gson.toJson(result));
			break;
		}
		case DELETE: {
			result = StorageEngine.getInstance().delete(rse);
			logger.debug("DELETE request recived result="+gson.toJson(result));
			break;
		}
		default:
			logger.debug("Unexpected request recived="+gson.toJson(request));
			break;
		}
		return result;
	}
} 