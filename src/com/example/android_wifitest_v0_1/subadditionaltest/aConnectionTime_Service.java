package com.example.android_wifitest_v0_1.subadditionaltest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class aConnectionTime_Service extends Service {
	private final String TAG = "WiFiTEST";
	private final String sub_TAG = "aConnectionTime_Service";
	
	private int Loop_cnt = aConnectionTime_Activity.Loop_cnt;
    private static WifiManager mWifiManager;
    private Thread Loop_thread;
    private int i = 1;
    private Boolean isRunning = false;
    private Boolean isConnected = false;
    
    private static IntentFilter mDefaultFilter;
    private static DefaultBroadcastReceiver mDefaultReceiver;

    private WifiConfiguration newConfig;
    private int netId = -1;
    
    private boolean result = false;
    private boolean ConnectResult = false;
    private boolean DisconnectResult = false;	
	
    private FileOutputStream fos;
    private long mStart;
    private long mDuration;
    
    
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
        super.onCreate();

        Log.i(TAG, sub_TAG + aConnectionTime_Activity.Loop_cnt + "WifiStart.");
        mDefaultFilter = new IntentFilter();
        mDefaultFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mDefaultFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mDefaultFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);

        mDefaultReceiver = new DefaultBroadcastReceiver();
        registerReceiver(mDefaultReceiver, mDefaultFilter);
        
        updateWidget();
		
	}
    // //thread를 이용하여 anr방지
    private void updateWidget() {
        isRunning = true;
        mWifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        mWifiManager.disconnect();
        
        mHandler.sendEmptyMessage(0);
    }
    
	public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d(TAG, sub_TAG + "Got message in handler");

            Loop_thread = new Thread(new Runnable() {
                public void run() {
                    Loop_cnt = aConnectionTime_Activity.Loop_cnt;

                    while (isRunning && i <= Loop_cnt) {
                        Log.d(TAG, sub_TAG + i + "-th Loop.");

                        // Thread 내에서 Toast를 하기 위해 post를 사용하였음
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast T = Toast.makeText(getApplicationContext(),
                                        i + "-th Loop", Toast.LENGTH_SHORT);
                                T.show();
                            }
                        });

                        for(int n = 0; n < 2; n++) {
                            result = WiFiStart();
                            if(n == 0) {
                                Log.d(TAG, sub_TAG + "ConnectResult = " + result);
                                ConnectResult = result;
                            } else {
                                Log.d(TAG, sub_TAG + "DisconnectResult = " + result);
                                DisconnectResult = result;
                            }
//                            try {
//                                Thread.sleep(2000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                        }
                        
                        if (ConnectResult && DisconnectResult) {
                            Intent intent = new Intent(aConnectionTime_Activity.WIFI_CONNECT_SUCCESS);
                            sendBroadcast(intent);
                        } else {
                            Intent intent = new Intent(aConnectionTime_Activity.WIFI_CONNECT_FAIL);
                            sendBroadcast(intent);
                        }
                        i++;
                    }
                    
                    Intent intent = new Intent(aConnectionTime_Activity.WIFI_CONNECT_FINISH);
                    sendBroadcast(intent);
                }
            });
            Loop_thread.start();
            Log.d(TAG, sub_TAG + "Thread id = " + Loop_thread.getName());
        }
    };
 // 껏다 켯다하기
    private synchronized boolean WiFiStart() {
        boolean result = false;
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        if (isConnected == false) {            
            newConfig = new WifiConfiguration();

            newConfig.networkId = -1;
            newConfig.SSID = convertToQuotedString(aConnectionTime_Activity.connect_ssid);
            newConfig.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
            newConfig.preSharedKey = convertToQuotedString(aConnectionTime_Activity.connect_password);
            newConfig.hiddenSSID = false;
            netId = mWifiManager.addNetwork(newConfig);
            
            //            netId = mWifiConfigStoreProxy.addNetwork(newConfig);
            Log.i(TAG, sub_TAG + "Try to connect with " + newConfig.SSID + " password : " + newConfig.preSharedKey);

            mStart = System.currentTimeMillis();
            try {
                result = mWifiManager.enableNetwork(netId, true);
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }

            return result;

        } else if (isConnected == true) {
            Log.i(TAG, sub_TAG + "Try to disconnect");
            result = mWifiManager.disableNetwork(netId) && mWifiManager.removeNetwork(netId);
			
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            
            return result;
        }
        return result;
    }

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
        Log.d(TAG, sub_TAG + "WiFi_service onDestroy");
        if (Loop_thread != null && Loop_thread.isAlive()) {
            Loop_thread.interrupt();
            isRunning = false;
        }
        i = 1;
        result = false;
        mWifiManager.disableNetwork(netId);
        mWifiManager.removeNetwork(netId);

        unregisterReceiver(mDefaultReceiver);

        super.onDestroy();
		super.onDestroy();
	}
    public static String convertToQuotedString(String string) {
        return "\"" + string + "\"";
    }
    
    class DefaultBroadcastReceiver extends BroadcastReceiver {
        public String WIFI_ACTION;
        private SupplicantState state;

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();

            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                NetworkInfo info = (NetworkInfo) intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                NetworkInfo.DetailedState state = info.getDetailedState();
                switch (state) {
                    case CONNECTED:
                        Log.i(TAG, "CONNECTED");
                        isConnected = true;
                        mDuration = System.currentTimeMillis() - mStart;
                        Log.d(TAG, " Duration : " + mDuration);
                        
                        if(mDuration < 20000){
                        	// write connection time
	            			String temp =  i + "-th Connection Time :"
	            					//+ Long.toString(mDuration)
	            					+ mDuration
	            					+ "ms\n";
	            			FILE_WRITE_string(temp, "Connect_time.txt");
	            			
	            			aConnectionTime_Activity.arrayList.add(temp);
	            			aConnectionTime_Activity.listView.setAdapter(aConnectionTime_Activity.arrayAdapter);
                        }
                        
                        break;
                    case DISCONNECTED:
                        Log.i(TAG, "DISCONNECTED");
                        isConnected = false;
                        break;
                    case AUTHENTICATING:
                        Log.i(TAG, "AUTHENTICATING");
                        break;
                    case BLOCKED:
                        Log.i(TAG, "BLOCKED");
                        break;
                    case CONNECTING:
                        Log.i(TAG, "CONNECTING");
                        break;
                    case DISCONNECTING:
                        Log.i(TAG, "DISCONNECTING");
                        break;
                    case FAILED:
                        Log.i(TAG, "FAILED");
                        break;
                    case IDLE:
                        Log.i(TAG, "IDLE");
                        break;
                    case OBTAINING_IPADDR:
                        Log.i(TAG, "OBTAINING_IPADDR");
                        break;
                    case SCANNING:
                        Log.i(TAG, "SCANNING");
                        break;
                    case SUSPENDED:
                        Log.i(TAG, "SUSPENDED");
                        break;
                    default:
                        Log.i(TAG, "Unkown State");
                        break;
                }
            }

            if (WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION.equals(action)) {
                Log.d(TAG, "SUPPLICANT_CONNECTION_CHANGE_ACTION");
            }
            if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {

                WifiInfo wi = aConnectionTime_Service.mWifiManager.getConnectionInfo();
                state = wi.getSupplicantState();

                ////////////////////////////////////////////////////////////////
                /////////////Supplicant change에 따라 다음과 같이 메세지를 나눔////////////
                //http://developer.android.com/reference/android/net/wifi/SupplicantState.html//
                ////////////////////////////////////////////////////////////////
                //Log.d(TAG, "Got Message~!");

                //Toast toast = Toast.makeText(arg0, WIFI_ACTION,1);
                //detail_state=WifiInfo.getDetailedStateOf(WifiManager.getSupplicantState());
                switch(state){

                    case ASSOCIATED :
                        Log.i(TAG, "[SUPPLICANT_STATE_CHANGED_ACTION]ASSOCIATED"   );
                        WIFI_ACTION = "kr.LGE.action.ASSOCIATED" ;
                        break;

                    case ASSOCIATING :
                        Log.i(TAG, "[SUPPLICANT_STATE_CHANGED_ACTION]ASSOCIATING"    );
                        WIFI_ACTION = "kr.LGE.action.ASSOCIATION" ;
                        break;

                    case AUTHENTICATING :
                        Log.i(TAG, "[SUPPLICANT_STATE_CHANGED_ACTION]AUTHENTICATING"     );
                        WIFI_ACTION = "kr.LGE.action.AUTHENTICATING" ;
                        break;

                    case COMPLETED :
//                        Log.i(TAG, "[SUPPLICANT_STATE_CHANGED_ACTION]COMPLETED");
                        WIFI_ACTION = "kr.LGE.action.COMPLETED" ;
                        break;

                    case DISCONNECTED :
                        Log.i(TAG, "[SUPPLICANT_STATE_CHANGED_ACTION]DISCONNECTED");
                        WIFI_ACTION = "kr.LGE.action.DISCONNECTED" ;
                        break;

                    case DORMANT :
                        Log.i(TAG, "[SUPPLICANT_STATE_CHANGED_ACTION]DORMANT"     );
                        WIFI_ACTION = "kr.LGE.action.DORMANT" ;
                        break;

                    case FOUR_WAY_HANDSHAKE :
                        Log.i(TAG, "[SUPPLICANT_STATE_CHANGED_ACTION]FOUR_WAY_HANDSHAKE"     );
                        WIFI_ACTION = "kr.LGE.action.FOUR_WAY_HANDSHAKE" ;
                        break;

                    case GROUP_HANDSHAKE :
                        Log.i(TAG, "[SUPPLICANT_STATE_CHANGED_ACTION]GROUP_HANDSHAKE"     );
                        WIFI_ACTION = "kr.LGE.action.GROUP_HANDSHAKE" ;
                        break;

                    case INACTIVE :
                        Log.i(TAG, "[SUPPLICANT_STATE_CHANGED_ACTION]INACTIVE"  );
                        WIFI_ACTION = "kr.LGE.action.INACTIVE" ;
                        break;

                    case INTERFACE_DISABLED :
                        Log.i(TAG, "[SUPPLICANT_STATE_CHANGED_ACTION]INTERFACE_DISABLED");
                        WIFI_ACTION = "kr.LGE.action.INTERFACE_DISABLED" ;
                        break;

                    case INVALID :
                        Log.i(TAG, "[SUPPLICANT_STATE_CHANGED_ACTION]INVALID"     );
                        WIFI_ACTION = "kr.LGE.action.INVALID" ;
                        break;

                    case SCANNING :
                        //connect_service.nStart = System.currentTimeMillis();
                        Log.i(TAG, "[SUPPLICANT_STATE_CHANGED_ACTION]SCANNING"     );
                        WIFI_ACTION = "kr.LGE.action.SCANNING" ;
//                        mHandler.sendEmptyMessage(UPDATE_CONNECT_RESULT);
//                        mHandler.sendEmptyMessage(TRY_DISCONNECT);
                        break;

                    case UNINITIALIZED :
                        Log.i(TAG, "[SUPPLICANT_STATE_CHANGED_ACTION]UNINITIALIZED"     );
                        WIFI_ACTION = "kr.LGE.action.UNINITIALIZED" ;
                        break;

                    default :
                        Log.i(TAG, "[SUPPLICANT_STATE_CHANGED_ACTION]default~!");
                        WIFI_ACTION = "kr.LGE.action.DEFAULT";
                        break;

                }
                //위에서 바꿔준 WIFI_ACTION을 사용하여 BroadCast를 쏜다.
                //Scanning state, Complete state를 받는 2개의 리시버가 받음.

                Intent broadcast_intent = new Intent();
                broadcast_intent.setAction(WIFI_ACTION);
                context.sendBroadcast(broadcast_intent);
            }
        }
    };
    
	private void FILE_WRITE_string(String temp, String Filename) {
		// TODO Auto-generated method stub
		try {
			byte[] temp1 = temp.getBytes();
			fos = openFileOutput(Filename, Context.MODE_APPEND);
			fos.write(temp1);
			Log.d(TAG, "Connect_Service Duration File OPEN Success");
			Log.d(TAG, Filename + "data is " + temp);
			// Toast.makeText(getApplicationContext(), "reset_cnt = " +
			// reset_cnt, Toast.LENGTH_SHORT).show();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
					Log.d(TAG, "Connect_Service Duration File Close Success");
				} catch (IOException e) {

				}
			}
		}
	}

	
}
