package io.zino.mystore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestClient {
	public static void main(String[] args) {

		TestClient client = new TestClient();
		try {
			client.startConnection("localhost", 12341);
			while (true) {
				String sendMessage = client.sendMessage("add "+Random()+" qecwvr"+Math.random());
				//System.out.println("client recive :: " + sendMessage);
				
				String sendMessage2 = client.sendMessage("get "+Random());
				//System.out.println("client recive :: " + sendMessage2);
				
				String sendMessage3 = client.sendMessage("update "+Random()+" qdecwc"+Math.random());
				//System.out.println("client recive :: " + sendMessage3);
				
				String sendMessage4 = client.sendMessage("delete "+Random());
				//System.out.println("client recive :: " + sendMessage4);
			}
			// client.stopConnection();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static int Random(){
		return (int)(Math.random()*1000000)%100;
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
