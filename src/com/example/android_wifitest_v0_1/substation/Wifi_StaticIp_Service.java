package com.example.android_wifitest_v0_1.substation;

import java.net.InetAddress;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
//import android.net.wifi.WifiConfiguration.IpAssignment;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

//import android.widget.Toast;

public class Wifi_StaticIp_Service extends Service {
    private String tag = "Wifi_StaticIp_Service";
    private int Loop_cnt = StaticIpActivity.Loop_cnt;
    private static WifiManager mWifiManager;
    private Thread Loop_thread;
    
    private static IntentFilter mDefaultFilter;
    private static DefaultBroadcastReceiver mDefaultReceiver;

    private LinkProperties mLinkProperties = new LinkProperties();
    private WifiConfiguration newConfig;
    private int netId = -1;
    private int i = 1;

    private Boolean isRunning = false;
    private Boolean isConnected = false;
    private boolean result = false;
    private boolean ConnectResult = false;
    private boolean DisconnectResult = false;

    @Override
    public void onCreate() {
        Log.i(tag, StaticIpActivity.Loop_cnt + "WifiStart.");
        super.onCreate();

        mDefaultFilter = new IntentFilter();
        mDefaultFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mDefaultFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mDefaultFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION);

        mDefaultReceiver = new DefaultBroadcastReceiver();
        registerReceiver(mDefaultReceiver, mDefaultFilter);
        
        updateWidget();
    }

    // wifion.OnOff_Count.setText("      "+String.valueOf(i));

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
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
            Log.d(tag, "Got message in handler");

            Loop_thread = new Thread(new Runnable() {
                public void run() {
                    Loop_cnt = StaticIpActivity.Loop_cnt;

                    while (isRunning && i <= Loop_cnt) {
                        Log.d(tag, i + "-th Loop.");

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast T = Toast.makeText(
                                        getApplicationContext(),
                                        i + "-th Loop", Toast.LENGTH_SHORT);
                                T.show();
                            }
                        });

                        Log.d(tag, "Not interrupted");

                        for(int n = 0; n < 2; n++) {
                            result = WiFiStart();
                            if(n == 0) {
                                Log.d(tag, "ConnectResult = " + result);
                                ConnectResult = result;
                            } else {
                                Log.d(tag, "DisconnectResult = " + result);
                                DisconnectResult = result;
                            }
//                            try {
//                                Thread.sleep(2000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
                        }

                        if (ConnectResult && DisconnectResult) {
                            Intent intent = new Intent(StaticIpActivity.WIFI_CONNECT_SUCCESS);
                            sendBroadcast(intent);
                        } else {
                            Intent intent = new Intent(StaticIpActivity.WIFI_CONNECT_FAIL);
                            sendBroadcast(intent);
                        }
                        i++;
                    }
                    Log.d(tag, "Send WIFI_CONNECT_FINISH");
                    Intent intent = new Intent(StaticIpActivity.WIFI_CONNECT_FINISH);
                    sendBroadcast(intent);
                }
            });
            Loop_thread.start();
            Log.d(tag, "Thread id = " + Loop_thread.getName());
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
            newConfig.SSID = convertToQuotedString(StaticIpActivity.connect_ssid);
            newConfig.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
            newConfig.preSharedKey = convertToQuotedString(StaticIpActivity.connect_password);
            newConfig.hiddenSSID = true;
//            newConfig.ipAssignment = IpAssignment.STATIC;
            validateIpConfigFields(mLinkProperties);
//            newConfig.linkProperties = new LinkProperties(mLinkProperties);
            netId = mWifiManager.addNetwork(newConfig);

            //            netId = mWifiConfigStoreProxy.addNetwork(newConfig);
            Log.i(tag, "Try to connect with " + newConfig.SSID + " password : " + newConfig.preSharedKey);

            try {
                result = mWifiManager.enableNetwork(netId, true);
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }

            return result;

        } else if (isConnected == true) {
            Log.i(tag, "Try to disconnect");
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

    public void onDestroy() {
        Log.d(tag, "WiFi_service onDestroy");
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
    }

    private static String convertToQuotedString(String string) {
        return "\"" + string + "\"";
    }
    
    private boolean validateIpConfigFields(LinkProperties linkProperties) {
    	Log.i(tag, "validateIpConfigFields");
    	//Ip Address Setting
        String ipAddr = new String("192.168.1.128");
        InetAddress inetAddr = null;
        try {
            inetAddr = NetworkUtils.numericToInetAddress(ipAddr);
        } catch (IllegalArgumentException e) {
        	Log.e(tag, "NetworkUtils.numericToInetAddress(ipAddr);");
            return false;
        }

        //Network Prefix Setting
        int networkPrefixLength = 24;
        try {
            linkProperties.addLinkAddress(new LinkAddress(inetAddr, networkPrefixLength));
        } catch (NumberFormatException e) {
            // Set the hint as default after user types in ip address
        	Log.e(tag, "linkProperties.addLinkAddress(new LinkAddress(inetAddr, networkPrefixLength));");
        	return false;
        }

        //Gateway Setting
        String gateway = new String("192.168.1.1");

        InetAddress gatewayAddr = null;
        try {
        	gatewayAddr = NetworkUtils.numericToInetAddress(gateway);
        } catch (IllegalArgumentException e) {
        	Log.e(tag, "NetworkUtils.numericToInetAddress(gateway);");
        	return false;
        }
        linkProperties.addRoute(new RouteInfo(gatewayAddr));

        //DNS Setting
        String dns = new String("192.168.1.1");
        InetAddress dnsAddr = null;

        try {
        	dnsAddr = NetworkUtils.numericToInetAddress(dns);
        } catch (IllegalArgumentException e) {
        	Log.e(tag, "NetworkUtils.numericToInetAddress(dns);");
        	return false;
        }
        linkProperties.addDns(dnsAddr);

        return true;
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
                        Log.i(tag, "CONNECTED");
                        isConnected = true;
                        break;
                    case DISCONNECTED:
                        Log.i(tag, "DISCONNECTED");
                        isConnected = false;
                        break;
                    case AUTHENTICATING:
                        Log.i(tag, "AUTHENTICATING");
                        break;
                    case BLOCKED:
                        Log.i(tag, "BLOCKED");
                        break;
                    case CONNECTING:
                        Log.i(tag, "CONNECTING");
                        break;
                    case DISCONNECTING:
                        Log.i(tag, "DISCONNECTING");
                        break;
                    case FAILED:
                        Log.i(tag, "FAILED");
                        break;
                    case IDLE:
                        Log.i(tag, "IDLE");
                        break;
                    case OBTAINING_IPADDR:
                        Log.i(tag, "OBTAINING_IPADDR");
                        break;
                    case SCANNING:
                        Log.i(tag, "SCANNING");
                        break;
                    case SUSPENDED:
                        Log.i(tag, "SUSPENDED");
                        break;
                    default:
                        Log.i(tag, "Unkown State");
                        break;
                }
            }

            if (WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION.equals(action)) {
                Log.d(tag, "SUPPLICANT_CONNECTION_CHANGE_ACTION");
            }
            if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {

                WifiInfo wi = Wifi_StaticIp_Service.mWifiManager.getConnectionInfo();
                state = wi.getSupplicantState();

                ////////////////////////////////////////////////////////////////
                /////////////Supplicant change에 따라 다음과 같이 메세지를 나눔////////////
                //http://developer.android.com/reference/android/net/wifi/SupplicantState.html//
                ////////////////////////////////////////////////////////////////
                //Log.d(tag, "Got Message~!");

                //Toast toast = Toast.makeText(arg0, WIFI_ACTION,1);
                //detail_state=WifiInfo.getDetailedStateOf(WifiManager.getSupplicantState());
                switch(state){

                    case ASSOCIATED :
                        Log.i(tag, "[SUPPLICANT_STATE_CHANGED_ACTION]ASSOCIATED"   );
                        WIFI_ACTION = "kr.LGE.action.ASSOCIATED" ;
                        break;

                    case ASSOCIATING :
                        Log.i(tag, "[SUPPLICANT_STATE_CHANGED_ACTION]ASSOCIATING"    );
                        WIFI_ACTION = "kr.LGE.action.ASSOCIATION" ;
                        break;

                    case AUTHENTICATING :
                        Log.i(tag, "[SUPPLICANT_STATE_CHANGED_ACTION]AUTHENTICATING"     );
                        WIFI_ACTION = "kr.LGE.action.AUTHENTICATING" ;
                        break;

                    case COMPLETED :
//                        Log.i(tag, "[SUPPLICANT_STATE_CHANGED_ACTION]COMPLETED");
                        WIFI_ACTION = "kr.LGE.action.COMPLETED" ;
                        break;

                    case DISCONNECTED :
                        Log.i(tag, "[SUPPLICANT_STATE_CHANGED_ACTION]DISCONNECTED");
                        WIFI_ACTION = "kr.LGE.action.DISCONNECTED" ;
                        break;

                    case DORMANT :
                        Log.i(tag, "[SUPPLICANT_STATE_CHANGED_ACTION]DORMANT"     );
                        WIFI_ACTION = "kr.LGE.action.DORMANT" ;
                        break;

                    case FOUR_WAY_HANDSHAKE :
                        Log.i(tag, "[SUPPLICANT_STATE_CHANGED_ACTION]FOUR_WAY_HANDSHAKE"     );
                        WIFI_ACTION = "kr.LGE.action.FOUR_WAY_HANDSHAKE" ;
                        break;

                    case GROUP_HANDSHAKE :
                        Log.i(tag, "[SUPPLICANT_STATE_CHANGED_ACTION]GROUP_HANDSHAKE"     );
                        WIFI_ACTION = "kr.LGE.action.GROUP_HANDSHAKE" ;
                        break;

                    case INACTIVE :
                        Log.i(tag, "[SUPPLICANT_STATE_CHANGED_ACTION]INACTIVE"  );
                        WIFI_ACTION = "kr.LGE.action.INACTIVE" ;
                        break;

                    case INTERFACE_DISABLED :
                        Log.i(tag, "[SUPPLICANT_STATE_CHANGED_ACTION]INTERFACE_DISABLED");
                        WIFI_ACTION = "kr.LGE.action.INTERFACE_DISABLED" ;
                        break;

                    case INVALID :
                        Log.i(tag, "[SUPPLICANT_STATE_CHANGED_ACTION]INVALID"     );
                        WIFI_ACTION = "kr.LGE.action.INVALID" ;
                        break;

                    case SCANNING :
                        //connect_service.nStart = System.currentTimeMillis();
                        Log.i(tag, "[SUPPLICANT_STATE_CHANGED_ACTION]SCANNING"     );
                        WIFI_ACTION = "kr.LGE.action.SCANNING" ;
//                        mHandler.sendEmptyMessage(UPDATE_CONNECT_RESULT);
//                        mHandler.sendEmptyMessage(TRY_DISCONNECT);
                        break;

                    case UNINITIALIZED :
                        Log.i(tag, "[SUPPLICANT_STATE_CHANGED_ACTION]UNINITIALIZED"     );
                        WIFI_ACTION = "kr.LGE.action.UNINITIALIZED" ;
                        break;

                    default :
                        Log.i(tag, "[SUPPLICANT_STATE_CHANGED_ACTION]default~!");
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
}
