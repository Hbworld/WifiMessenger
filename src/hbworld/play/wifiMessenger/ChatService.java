package hbworld.play.wifiMessenger;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ChatService extends Service {

	public static String serverIP;

	public static int serverPort = 5555;

	private serverThread sThread;

	public static final String BROADCAST_ACTION = "UpdateEvent";

	public static DatagramSocket portsocket;

	Intent intent;

	int i = 0;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	public void onCreate() {
		try {
			portsocket = new DatagramSocket(5555);
			sThread = new serverThread();
			sThread.start();
		} catch (SocketException e) {

			e.printStackTrace();
		}
	}

	private class serverThread extends Thread {

		public void run() {
			try {
				serverIP = getLocalIpAddress();
				Log.v("addr", serverIP);
				if (serverIP != null) {

					Log.v("socket", "created");
					while (true) {

						try {
							byte[] buf = new byte[1024];
							DatagramPacket packet = new DatagramPacket(buf,
									buf.length);
							ChatService.portsocket.receive(packet);
							byte[] result = new byte[packet.getLength()];
							System.arraycopy(packet.getData(), 0, result, 0,
									packet.getLength());
							String msg = new String(result);
							updateGui(msg, packet.getAddress());

						} catch (Exception e) {
						}
					}
				}
			} catch (Exception e) {
			}
		}

	}

	private void updateGui(String msg, InetAddress addr) {

		intent = new Intent(BROADCAST_ACTION);
		intent.putExtra("message", msg);
		intent.putExtra("senderAddr",
				addr.toString().substring(1, addr.toString().length()));
		if (msg.substring(0, 3).equals("idf")) {
			ChatWindow.receiverAddr = addr.toString().substring(1,
					addr.toString().length());

		}
		sendBroadcast(intent);
	}

	private String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("ServerActivity", ex.toString());
		}
		return null;
	}

	public void onDestroy() {
	}

}