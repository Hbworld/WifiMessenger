package hbworld.play.wifiMessenger;

import java.net.InetAddress;
import java.util.ArrayList;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

public class Device_Address {

	public static String ipAddress = "";

	public static ArrayList<String> addresses = new ArrayList();

	public static String myAddress;

	WifiManager wifiManager;

	WifiInfo wifiInfo;

	ProgressDialog progressDialog;

	int progress;

	private Handler progressbarHandler = new Handler();

	void setdomain(Context context) {
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		wifiInfo = (WifiInfo) wifiManager.getConnectionInfo();
		int addr = wifiInfo.getIpAddress();
		ipAddress = String.format("%d.%d.%d.", (addr & 0xff), (addr >> 8 & 0xff),
				(addr >> 16 & 0xff));

	}

	ArrayList<String> scanAddresses(Context context) {

		ChatWindow.users.clear();
		ChatWindow.userAddress.clear();

		progressDialog = new ProgressDialog(context);
		progressDialog.setCancelable(false);
		progressDialog.setMessage("Scanning Users");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setProgress(0);
		progressDialog.setMax(255);
		progressDialog.show();

		new Thread(new Runnable() {
			public void run() {

				InetAddress in;
				try {
					for (int i = 1; i < 256; i++) {
						in = InetAddress.getByName(ipAddress + Integer.toString(i));
						if (in.isReachable(50)) {
							if (!addresses.contains(ipAddress
									+ Integer.toString(i))) {
								addresses.add(ipAddress + Integer.toString(i));
								Log.v("address found",
										ipAddress + Integer.toString(i));
							}
						}

						progress = i;

						progressbarHandler.post(new Runnable() {

							public void run() {
								progressDialog.setProgress(progress);
							}

						});
					}
					progressDialog.dismiss();
				} catch (Exception e) {
				}
			}
		}).start();

		return addresses;
	}

}