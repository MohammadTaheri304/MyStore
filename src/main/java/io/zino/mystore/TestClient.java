package io.zino.mystore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


/**
 * The Class TestClient.
 */
public class TestClient {
	
	/** The Constant logger. */
	final static Logger logger = LoggerFactory.getLogger(TestClient.class);
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		runCliClient();
		//test();
	}

	/**
	 * Run cli client.
	 */
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
				
				if(count%10000==0){
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			// client.stopConnection();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Random.
	 *
	 * @return the int
	 */
	private static int Random() {
		return (int) (Math.random() * 10000000) % 1000000;
	}

	/** The client socket. */
	private Socket clientSocket;
	
	/** The out. */
	private PrintWriter out;
	
	/** The in. */
	private BufferedReader in;

	/**
	 * Start connection.
	 *
	 * @param ip the ip
	 * @param port the port
	 * @throws UnknownHostException the unknown host exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void startConnection(String ip, int port) throws UnknownHostException, IOException {
		clientSocket = new Socket(ip, port);
		out = new PrintWriter(clientSocket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	}

	/**
	 * Send message.
	 *
	 * @param msg the msg
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String sendMessage(String msg) throws IOException {
		out.println(msg);
		String resp = in.readLine();
		return resp;
	}

	/**
	 * Stop connection.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void stopConnection() throws IOException {
		in.close();
		out.close();
		clientSocket.close();
	}
}
