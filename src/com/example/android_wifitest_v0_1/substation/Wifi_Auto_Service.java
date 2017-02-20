package com.example.android_wifitest_v0_1.substation;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;


public class Wifi_Auto_Service extends Service {
    private String tag = "Wifi_Auto_Service";
    
    private int Loop_cnt = ApAutoConnectActivity.Loop_cnt;
    private WifiManager mWifiManager;
    private Thread Loop_thread;
    private int i = 1;
    private Boolean isRunning = true;
    
    private boolean result = false;
    private boolean Onresult = false;
    private boolean Offresult = false;
    private boolean isScreenon = true;
    
    private static IntentFilter mScreenFilter;
    private ScreenStateReceiver mScreenReceiver;
    private PowerManager.WakeLock mWakeLock;
    private PowerManager powerManager;

    @Override
    public void onCreate() {
        Log.i(tag, ApAutoConnectActivity.Loop_cnt + "WifiStart.");
        super.onCreate();
        
        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        Loop_cnt = ApAutoConnectActivity.Loop_cnt;
        
        mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP , tag);
        mWakeLock.acquire();

        mScreenFilter = new IntentFilter();
        mScreenFilter.addAction(Intent.ACTION_SCREEN_OFF);
        mScreenFilter.addAction(Intent.ACTION_SCREEN_ON);

        mScreenReceiver = new ScreenStateReceiver();
        registerReceiver(mScreenReceiver, mScreenFilter);
        
        updateWidget();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    private void updateWidget() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        Set_Sleep_Policy_Never();
        mHandler.sendEmptyMessage(0);

    }

	public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d(tag, "Got message in handler");

            Loop_thread = new Thread(new Runnable() {
                public void run() {

                    //Show Start Notify Message
                    showStartNotification();

                    while (isRunning && i <= Loop_cnt) {
                        Log.d(tag, i + "-th Loop.");

                        if (i <= Loop_cnt) {
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

                        for(int n = 0; n < 2; n++) {
                            result = WiFiStart();
                            
                            if(isScreenon) {
                                Log.d(tag, "Screen On Success = " + result);
                                Onresult = result;
                            } else {
                                Log.d(tag, "Screen Off Success = " + result);
                                Offresult = result;
                            }
                        }
                        
                        if (Onresult && Offresult) {
                            Intent intent = new Intent(ApAutoConnectActivity.WIFI_AUTOCONN_SUCCESS);
                            sendBroadcast(intent);
                            Log.d(tag, "Send Success Broadcast");
                        } else {
                            Intent intent = new Intent(ApAutoConnectActivity.WIFI_AUTOCONN_FAIL);
                            sendBroadcast(intent);
                            Log.d(tag, "Send Fail Broadcast");
                        }
                        i++;
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "complete Test",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });                    
                    
                    Intent intent = new Intent(ApAutoConnectActivity.WIFI_AUTOCONN_FINISH);
                    sendBroadcast(intent);
                }
            });
            Loop_thread.start();
            Log.d(tag, "Thread id = " + Loop_thread.getName());
        }
    };

    private synchronized boolean WiFiStart() {
        if (isScreenon) {
            Log.d(tag, "Screen on -> off");
            gotosleep();

            try {
                Thread.sleep(150000); //wait for 2min 30sec.
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(tag, "isScreenon == " + isScreenon + " mWifiManager.getConnectionInfo().getIpAddress() = " + mWifiManager.getConnectionInfo().getIpAddress());
            if (isScreenon == false && mWifiManager.getConnectionInfo().getIpAddress() == 0 ){
                return true;
            }

        } else if (!isScreenon) {
            Log.d(tag, "Screen off -> on");
            wakeLock();
            
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d(tag, "isScreenon == " + isScreenon + "isScreenon getIpAddress = " + mWifiManager.getConnectionInfo().getIpAddress());

            if (isScreenon == true && mWifiManager.getConnectionInfo().getIpAddress() != 0){
                return true;
            }
            
        }
  
        return false;
    }

    public void onDestroy() {
        Log.d(tag, "Wifi_Auto_Service onDestroy");
        if (Loop_thread != null && Loop_thread.isAlive()) {
            Loop_thread.interrupt();
            isRunning = false;
        }
        i = 1;

        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        
        unregisterReceiver(mScreenReceiver);
        
        super.onDestroy();
    }
    
    private void Set_Sleep_Policy_Never() {
        int wifiSleepPolicy = Settings.System.getInt(getContentResolver(),
                Settings.System.WIFI_SLEEP_POLICY, 
                Settings.System.WIFI_SLEEP_POLICY_NEVER);
        if (wifiSleepPolicy != Settings.System.WIFI_SLEEP_POLICY_DEFAULT) {
            try {
                Settings.System.putInt(getContentResolver(), Settings.System.WIFI_SLEEP_POLICY,
                        Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
            } catch (NumberFormatException e) {
                Toast.makeText(getApplicationContext(), "Sleep Policy Error",
                        Toast.LENGTH_SHORT).show();
            }

            wifiSleepPolicy = Settings.System.getInt(getContentResolver(),
                    Settings.System.WIFI_SLEEP_POLICY, 
                    Settings.System.WIFI_SLEEP_POLICY_NEVER); //NEVER means always on JB.

            Log.d(tag, "wifiSleepPolicy set to " + wifiSleepPolicy);
        }
    }

	private void wakeLock() {
        if(mWakeLock == null) {
            mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP| PowerManager.ON_AFTER_RELEASE, tag);
        }
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        Log.d(tag, "wakeLock!");

        mWakeLock.acquire();
        
    }


    private void gotosleep() {

        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        Log.d(tag, "gotosleep!");
        powerManager.goToSleep(SystemClock.uptimeMillis());

    }
    
    private void showStartNotification() {
    	//Show Start Notify Message
    	mHandler.post(new Runnable() {
    		@Override
    		public void run() {

    			Toast T = Toast.makeText(
    					getApplicationContext(),
    					"AutoConnect test will begin within 5 seconds.\n It takes about 30 minutes." , Toast.LENGTH_SHORT);
    			T.show();
    		}
    	});
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    class ScreenStateReceiver extends BroadcastReceiver {

        String Tag = "ScreenStateReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            
            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                isScreenon = true;
                Log.d(Tag, "ACTION_SCREEN_ON");
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                isScreenon = false;
                Log.d(Tag, "ACTION_SCREEN_OFF");
            }
        }
    };
}
