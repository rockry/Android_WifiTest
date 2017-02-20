package com.example.android_wifitest_v0_1.subadditionaltest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class aBroadcast_Service extends Service {

	private String tag = "WiFiTEST_broadcast_service";
	private Thread cast_Thread;
	private InetAddress sendAddress;
	private boolean isRunning = true;
	private int j = 0;

	@Override
	public void onCreate() {
		Log.i(tag, "broadcast service Start.");
		super.onCreate();

		bHandler.sendEmptyMessage(0);

		// onDestroy();
	}

	@SuppressLint({ "HandlerLeak", "HandlerLeak" })
	public Handler bHandler = new Handler() {
		@SuppressLint("HandlerLeak")
		public void handleMessage(Message mgs) {
			Log.d(tag, "Got message in handler");
			cast_Thread = new Thread(new Runnable() {

				@SuppressLint("HandlerLeak")
				@Override
				public void run() {

					// TODO Auto-generated method stub

					String sendData = "asdfasdfasdfasdf";
					DatagramSocket dsock = null;
					Log.d(tag, "sendData = " + sendData);
					try {
						sendAddress = InetAddress.getByName(aBroadcastActivity.IPaddr);
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Log.d(tag, "sendAddr = " + aBroadcastActivity.IPaddr);
					try {
						dsock = new DatagramSocket(aBroadcastActivity.port);
					} catch (SocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						dsock.setBroadcast(true);
					} catch (SocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					DatagramPacket sendPacket = new DatagramPacket(
							sendData.getBytes(), sendData.length(),
							sendAddress, aBroadcastActivity.port);
					while (isRunning && j < aBroadcastActivity.loop_cnt_broadcast) {
						for (int i = 0; i < aBroadcastActivity.limit; i++) {
							try {
								dsock.send(sendPacket);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						bHandler.post(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast T = Toast.makeText(
										getApplicationContext(), j
												+ "-th Packet send!",
										Toast.LENGTH_SHORT);
								T.show();

							}
						});
						Log.d(tag, j + " th Packet Send!!");
						j++;
						// Toast.makeText(getApplicationContext(), "send!",
						// Toast.LENGTH_SHORT).show();
						try {
							Thread.sleep(aBroadcastActivity.sleep_period);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			});
			cast_Thread.start();
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onDestroy() {
		aBroadcastActivity.IPaddr = "192.168.1.255";
		aBroadcastActivity.port = 5657;
		aBroadcastActivity.sleep_period = 10000;
		aBroadcastActivity.limit = 50;
		aBroadcastActivity.loop_cnt_broadcast = 1000;

		if (cast_Thread != null && cast_Thread.isAlive()) {
			cast_Thread.interrupt();
			isRunning = false;
		}
		Log.d(tag, "broadcast_service destroy!");
		super.onDestroy();
	}

}
