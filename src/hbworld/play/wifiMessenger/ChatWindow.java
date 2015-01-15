package hbworld.play.wifiMessenger;

import java.io.IOException;
import java.util.ArrayList;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatWindow extends Activity {
		
	Button btSend, btSelectUser;
	TextView textBox, msgLog;
	ScrollView scrollView;
	Intent intent;
	
		
	Device_Address addressUtility = new Device_Address();
	static ArrayList<CharSequence> users = new ArrayList();
	static ArrayList<String> userAddress = new ArrayList();

	int receiverInd = -1;
	static String receiverAddr = null;
	static String senderAddr = null;
	String selectedReceiver = null;
	BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
	String name = btAdapter.getName();
	String userName = name;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		WifiManager wifiManager = (WifiManager) this
				.getSystemService(Context.WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled()) {
			wifiManager.setWifiEnabled(true);
		}

		btSend = (Button) findViewById(R.id.btSend);
		btSelectUser = (Button) findViewById(R.id.btSelectUser);
		textBox = (TextView) findViewById(R.id.TextBox);
		msgLog = (TextView) findViewById(R.id.msgLog);
		scrollView = (ScrollView) findViewById(R.id.scrollView1);

		msgLog.setMovementMethod(new ScrollingMovementMethod());

		intent = new Intent(this, ChatService.class);

		registerReceiver(broadcastReceiver, new IntentFilter(
				ChatService.BROADCAST_ACTION));
		startService(new Intent(getApplicationContext(), ChatService.class));

		btSend.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String ent_query = textBox.getText().toString().trim();
				 if (ent_query == null || ent_query.isEmpty()) {
					Toast toast = Toast.makeText(ChatWindow.this,
							"Please enter a message ", Toast.LENGTH_SHORT);
					toast.show();
					
				}
				else	if (!textBox.getText().equals("")) {
					if (selectedReceiver != null) {
						msgLog.append(Html
								.fromHtml("<font color='black' style='bold'>"
										+ userName + ": </font>"));
						msgLog.append(Html.fromHtml("<font color='black'>"
								+ textBox.getText() + "</font> <br/>"));
						sendMessage(selectedReceiver, "msg" + userName + ": "
								+ textBox.getText().toString(), false);
						textBox.setText("");
					} else {
						Toast toast = Toast.makeText(ChatWindow.this,
								"Please choose a User",Toast.LENGTH_SHORT);
						toast.show();
					}
				}  
			}

		});

		btSelectUser.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CharSequence[] temp = new CharSequence[users.size()];
				for (int i = 0; i < users.size(); i++)
					temp[i] = users.get(i);
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						ChatWindow.this);
				builder.setTitle("Choose Recepient");
				builder.setSingleChoiceItems(temp, receiverInd,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								receiverInd = which;
								selectedReceiver = userAddress.get(which);
								Log.v("receiver addr set to", selectedReceiver);
								dialog.dismiss();
							}
						});

				AlertDialog alert = builder.create();
				alert.show();
			}

		});

		setUsers();
	}

	private void setUsers() {
		final Thread t = new Thread(new Runnable() {

			public void run() {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				sendMessage("255.255.255.255", "idf" + userName, true);
			}

		});

		t.start();

	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.v("msg", "received");
			String message = intent.getStringExtra("message");
			String code = message.substring(0, 3);
			String msg = null;
			if (message.length() > 3)
				msg = message.substring(3, message.length());
			if (code.equals("idf")) {
				if (receiverAddr != null) {
					addUser(msg, intent.getStringExtra("senderAddr"));
					Toast toast = Toast.makeText(ChatWindow.this, msg
							+ " entered chat room", Toast.LENGTH_SHORT);
					toast.show();
					sendMessage(receiverAddr, "irf" + userName, false);
				}
			}  else if (code.equals("msg")) {
				int temp = 0;
				while (msg.charAt(temp) != ' ')
					temp++;
				String sender;
				String message1;
				sender = msg.substring(0, temp);
				message1 = msg.substring(temp, msg.length());

				msgLog.append(Html.fromHtml("<font color='red'>" + sender
						+ " </font>"));
				msgLog.append(Html.fromHtml("<font color='black'>" + message1
						+ "</font> <br/>"));

				scrollView.post(new Runnable() {
					public void run() {
						scrollView.fullScroll(View.FOCUS_DOWN);
					}
				});

			}
		}
	};
	

	

	private void sendMessage(String address, String msg, boolean bcast) {

		ChatThread cThread;
		try {
			cThread = new ChatThread(address, 5555, msg, bcast);
			cThread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void addUser(String user, String addr) {
		for (int i = 0; i < userAddress.size(); i++)
			if (userAddress.get(i).equals(addr))
				return;
		users.add(user);
		userAddress.add(addr);
	}

	
	  public void onResume() { super.onResume();
	  registerReceiver(broadcastReceiver, new IntentFilter(
	  ChatService.BROADCAST_ACTION)); }
	  
	  public void onPause() { super.onPause();
	  unregisterReceiver(broadcastReceiver); }
	  
	  public void onDestroy() { super.onDestroy(); }
	 

}