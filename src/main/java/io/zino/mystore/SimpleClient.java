package io.zino.mystore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * The Class SimpleClient.
 */
public class SimpleClient {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		SimpleClient client = new SimpleClient();
		try(Scanner scanner = new Scanner(System.in)) {
			client.startConnection("localhost", 12341);
			while (true) {
				String sendMessage = client.sendMessage(scanner.nextLine());
				System.out.println("client recive :: " + sendMessage);
			}
			// client.stopConnection();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

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
