package com.example.android_wifitest_v0_1.subhotspot;


import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiMonitor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


public class WifiAP_OnOff_Service extends Service {
    private String tag = "WifiAP_OnOffService";
    private int Loop_cnt = hOnOffActivity.Loop_cnt;
    private WifiManager mWifiManager;
    private Thread Loop_thread;
    private int i = 1;
    private Boolean isRunning = true;
    
    private boolean result = false;
    private boolean Onresult = false;
    private boolean Offresult = false;
    @Override
    public void onCreate() {
        Log.i(tag, hOnOffActivity.Loop_cnt + "WifiAPStart.");
        super.onCreate();
        
        mWifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        Toast toast = Toast.makeText(getApplicationContext(),
                "WiFiAPOnOff_service Start!", Toast.LENGTH_SHORT);
        toast.show();
        updateWidget();
    }

    // wifion.OnOff_Count.setText("      "+String.valueOf(i));

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    // //thread�� �̿��Ͽ� anr����
    private void updateWidget() {
        mHandler.sendEmptyMessage(0);

    }

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d(tag, "Got message in handler");
            Loop_thread = new Thread(new Runnable() {
                public void run() {
                    Loop_cnt = hOnOffActivity.Loop_cnt;

                    while (isRunning && i <= Loop_cnt) {
                        Log.d(tag, i + "-th Loop\n.");

                        if (i <= Loop_cnt) {
                            // Thread ������ Toast�� �ϱ� ���� post�� ����Ͽ���
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

                        //���Ŀ� BroadcastReceiver�� �޾Ƽ� ó���ϴ� ������ ���� ����
                        for(int n = 0; n < 2; n++) {
                            result = WiFiAPStart();
                            if(mWifiManager.isWifiApEnabled()) {
                                Log.d(tag, "WiFiAP On Success = " + result);
                                Onresult = result;
                            } else {
                                Log.d(tag, "WiFiAP Off Success = " + result);
                                Offresult = result;
                            }
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        if (Onresult && Offresult) {
                            Intent intent = new Intent(hOnOffActivity.WIFIAP_ONOFF_SUCCESS);
                            sendBroadcast(intent);
                        } else {
                            Intent intent = new Intent(hOnOffActivity.WIFIAP_ONOFF_FAIL);
                            sendBroadcast(intent);
                        }
                        i++;
                    }
                    
                    // Thread ������ Toast�� �ϱ� ���� post�� ����Ͽ���
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "complete Test",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });                    
                    
                    Intent intent = new Intent(hOnOffActivity.WIFIAP_ONOFF_FINISH);
                    sendBroadcast(intent);
                }
            });
            Loop_thread.start();
            Log.d(tag, "Thread id = " + Loop_thread.getName());
        }
    };

    // ���� �ִ��ϱ�
    private synchronized boolean WiFiAPStart() {
        if (mWifiManager.isWifiApEnabled()) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            return mWifiManager.setWifiApEnabled(null, false);
        } else {
        	mWifiManager.setWifiEnabled(false);
        	try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
        	return mWifiManager.setWifiApEnabled(null, true);
        }
    }

    public void onDestroy() {
        Log.d(tag, "WiFiAP_service onDestroy");
        if (Loop_thread != null && Loop_thread.isAlive()) {
            Loop_thread.interrupt();
            isRunning = false;
        }
        i = 1;
		mWifiManager.setWifiApEnabled(null, false);
        super.onDestroy();
    }
}
