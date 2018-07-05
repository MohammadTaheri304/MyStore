package io.zino.mystore.clusterEngine;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// TODO: Auto-generated Javadoc
/**
 * The Class ClusterNode.
 */
public class ClusterNode {
	
	/** The node uid. */
	private String nodeUid;
	
	/** The address. */
	private String address;
	
	/** The port. */
	private int port;
	
	ObjectOutputStream objectOutputStream;
	ObjectInputStream objectInputStream;

	
	/**
	 * Instantiates a new cluster node.
	 *
	 * @param nodeUid the node uid
	 * @param address the address
	 * @param port the port
	 */
	public ClusterNode(String nodeUid, String address, int port) {
		super();
		this.nodeUid = nodeUid;
		this.address = address;
		this.port = port;
	}
	
	/**
	 * Gets the node uid.
	 *
	 * @return the node uid
	 */
	public String getNodeUid() {
		return nodeUid;
	}
	
	/**
	 * Sets the node uid.
	 *
	 * @param nodeUid the new node uid
	 */
	public void setNodeUid(String nodeUid) {
		this.nodeUid = nodeUid;
	}
	
	/**
	 * Gets the address.
	 *
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	
	/**
	 * Sets the address.
	 *
	 * @param address the new address
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	
	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Sets the port.
	 *
	 * @param port the new port
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	
}
