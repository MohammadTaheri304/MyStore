package io.zino.mystore.clusterEngine;

public class ClusterNode {
	private String nodeUid;
	private String address;
	private int port;
	public ClusterNode(String nodeUid, String address, int port) {
		super();
		this.nodeUid = nodeUid;
		this.address = address;
		this.port = port;
	}
	public String getNodeUid() {
		return nodeUid;
	}
	public void setNodeUid(String nodeUid) {
		this.nodeUid = nodeUid;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	
}
