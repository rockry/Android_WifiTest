package com.example.android_wifitest_v0_1.substation;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

//import android.widget.Toast;

public class Wifi_OnOff_Service extends Service {
    private String tag = "Wifi_Service";
    private int Loop_cnt = OnOffActivity.Loop_cnt;
    private WifiManager mWifiManager;
    private Thread Loop_thread;
    private int i = 1;
    private Boolean isRunning = true;
    
    private boolean result = false;
    private boolean Onresult = false;
    private boolean Offresult = false;

    @Override
    public void onCreate() {
        Log.i(tag, OnOffActivity.Loop_cnt + "WifiStart.");
        super.onCreate();

        mWifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
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
        mHandler.sendEmptyMessage(0);

    }

	public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d(tag, "Got message in handler");

            Loop_thread = new Thread(new Runnable() {
                public void run() {
                    Loop_cnt = OnOffActivity.Loop_cnt;

                    while (isRunning && i <= Loop_cnt) {
                        Log.d(tag, i + "-th Loop.");

                        if (i <= Loop_cnt) {
                            // Thread 내에서 Toast를 하기 위해 post를 사용하였음
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast T = Toast.makeText(
                                            getApplicationContext(),
                                            i + "-th Loop", Toast.LENGTH_SHORT);
                                    T.show();
                                }
                            });
                        }

                        Log.d(tag, "Not interrupted");

                        //추후에 BroadcastReceiver로 받아서 처리하는 것으로 구현 예정
                        for(int n = 0; n < 2; n++) {
                            result = WiFiStart();
                            if(mWifiManager.isWifiEnabled()) {
                                Log.d(tag, "WiFi On Success = " + result);
                                Onresult = result;
                            } else {
                                Log.d(tag, "WiFi Off Success = " + result);
                                Offresult = result;
                            }
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (Onresult && Offresult) {
                            Intent intent = new Intent(OnOffActivity.WIFI_ONOFF_SUCCESS);
                            sendBroadcast(intent);
                        } else {
                            Intent intent = new Intent(OnOffActivity.WIFI_ONOFF_FAIL);
                            sendBroadcast(intent);
                        }
                        i++;
                    }
                    
                    // Thread 내에서 Toast를 하기 위해 post를 사용하였음
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "complete Test",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });                    
                    
                    Intent intent = new Intent(OnOffActivity.WIFI_ONOFF_FINISH);
                    sendBroadcast(intent);
                }
            });
            Loop_thread.start();
            Log.d(tag, "Thread id = " + Loop_thread.getName());
        }
    };

    // 껏다 켯다하기
    private synchronized boolean WiFiStart() {
        if (mWifiManager.isWifiEnabled()) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            return mWifiManager.setWifiEnabled(false);
        } else {
            return mWifiManager.setWifiEnabled(true);
//            wManager2.enableNetwork(OnOffActivity.networkID, true);
        }
    }

    public void onDestroy() {
        Log.d(tag, "WiFi_service onDestroy");
        if (Loop_thread != null && Loop_thread.isAlive()) {
            Loop_thread.interrupt();
            isRunning = false;
        }
        i = 1;
        super.onDestroy();
    }

}
