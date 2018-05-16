package io.zino.mystore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class TestClient {
	final static Logger logger = Logger.getLogger(TestClient.class);
	public static void main(String[] args) {
		runCliClient();
		//test();
	}

	private static void runCliClient() {
		Map<String, String> map = new HashMap<>();
		TestClient client = new TestClient();
		
		String base = "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr" 
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr"
		+ "qecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvrqecwvr";
		
		try {
			client.startConnection("localhost", 12341);
			long count=0;
			while (true) {
				count++;
				{
					String key = Random() + "";
					String value = base + Math.random();
					String sendMessage = client.sendMessage("add " + key + " " + value);
					System.out.println((!map.containsKey(key))+" client recive :: " + sendMessage);
					map.put(key, value);
				}

				{
					String key = Random() + "";
					String sendMessage2 = client.sendMessage("get " + key);
					System.out.println(map.get(key)+" client recive :: " + sendMessage2);
				}

				{
					String key = Random() + "";
					String value = "qdecwc" + Math.random();
					map.replace(key, value);
					String sendMessage3 = client.sendMessage("update " + key + " " + value);
					System.out.println("client recive :: " + sendMessage3);
				}

				{
					String key = Random() + "";
					String sendMessage4 = client.sendMessage("delete " + Random());
					System.out.println(map.containsKey(key)+" client recive :: " + sendMessage4);
					map.remove(key);
				}
				
//				if(count%10000==0){
//					try {
//						Thread.sleep(1000L);
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
			}
			// client.stopConnection();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static int Random() {
		return (int) (Math.random() * 10000000) % 1000000;
	}

	private Socket clientSocket;
	private PrintWriter out;
	private BufferedReader in;

	public void startConnection(String ip, int port) throws UnknownHostException, IOException {
		clientSocket = new Socket(ip, port);
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}

	public String sendMessage(String msg) throws IOException {
		out.println(msg);
		String resp = in.readLine();
		return resp;
	}

	public void stopConnection() throws IOException {
		in.close();
		out.close();
		clientSocket.close();
	}
}
