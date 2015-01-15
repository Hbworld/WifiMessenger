package hbworld.play.wifiMessenger;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;

public class ChatThread extends Thread {

	String address;
	String message;
	int portNum;
	InetAddress serverAddr;
	Socket socket;
	PrintWriter out;
	boolean broadcast;

	public ChatThread(String addr, int port, String msg, boolean bcast)
			throws IOException {
		address = addr;
		portNum = port;
		message = msg;
		broadcast = bcast;
	}

	public void run() {
		try {

			ChatService.portsocket.setBroadcast(broadcast);
			DatagramPacket packet1 = new DatagramPacket(message.getBytes(),
					message.length(), InetAddress.getByName(address), 5555);
			ChatService.portsocket.send(packet1);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}