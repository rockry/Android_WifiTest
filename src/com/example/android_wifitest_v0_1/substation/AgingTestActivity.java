package com.example.android_wifitest_v0_1.substation;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.android_wifitest_v0_1.R;

//import com.android.settings.wifi.WifiApDialog;


public class AgingTestActivity extends Activity{
	private String Tag = "AgingTestActivity";
	private String tag = "AgingTestActivity";
	private ToggleButton ConnectButton;
	private Button ScanList;
	private EditText loop;
	private TextView success;
	private TextView fail;
	public static WebView webview;
	
	
    private static IntentFilter mDefaultFilter;
    private static Default_BroadcastReceiver mDefault_BroadcastReceiver;
    
    
    public static int success_num;
    public static int fail_num;
    public static int Loop_cnt;
    public static String connect_ssid = "Wi-Fi_Sanity_AP";
    public static String connect_password = "lge12345";

    public static final String WIFI_CONNECT_FINISH = "com.lge.action.WIFI_CONNECT_FINISH";

    public static final String AGING_TEST_SUCCESS = "com.lge.action.AGING_TEST_SUCCESS";
    public static final String AGING_TEST_FAIL = "com.lge.action.AGING_TEST_FAIL";
    public static final String AGING_TEST_FINISHED = "com.lge.action.AGING_TEST_FINISHED";
    

    private boolean result = false;
    private boolean ConnectResult = false;
    private boolean DisconnectResult = false;
    private WifiConfiguration newConfig;
    private int netId = -1;
    private String[] urlAddr = {"http://www.google.co.kr",
    							"http://m.naver.com",
    							"http://m.daum.net",
    							"http://m.youtube.com",
    							"http://m.facebook.com",
    							"http://developer.android.com",
    							"http://m.msn.com",
    							"http://m.myspace.com",
    							"http://m.amazon.com",
    							"http://m.ebay.com"
    						};
    private static WifiManager mWifiManager;
    private Boolean isRunning = false;
    private Boolean isConnected = false;
    private Thread Loop_thread;
    private int i = 1;
    
    
    
    protected void onCreate(Bundle savedInstanceState) {
      
        super.onCreate(savedInstanceState);
        setContentView(R.layout.station_aging);
        ConnectButton = (ToggleButton)findViewById(R.id.sAgingButton);
        loop = (EditText)findViewById(R.id.sAgingLoop);
        success = (TextView)findViewById(R.id.sAgingSuccess);
        fail = (TextView)findViewById(R.id.sAgingFail);
        webview = (WebView)findViewById(R.id.sAgingWebview);
      
        
        mWifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        
        ConnectButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    updateResult(0);
                    updateAPconf();
                    AgingServiceStart();           
                } else {
                    AgingServiceStop();
                }
            }
        });
        
        mDefaultFilter = new IntentFilter();
       // mDefaultFilter.addAction(AGING_TEST_SUCCESS);
       // mDefaultFilter.addAction(AGING_TEST_FAIL);
        mDefaultFilter.addAction(AGING_TEST_FINISHED);
        mDefaultFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        
        //mDefaultReceiver = new DefaultBroadcastReceiver();
        mDefault_BroadcastReceiver = new Default_BroadcastReceiver();
        
    }
    
    @Override
    public void onResume(){
        super.onResume();
        Log.d(Tag, "[AgingTestActivity]OnResume");
        connect_ssid = "Wi-Fi_Sanity_AP";
        connect_password = "lge12345";
        //registerReceiver(mDefaultReceiver, mDefaultFilter);
        registerReceiver(mDefault_BroadcastReceiver, mDefaultFilter);
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(Tag, "[AgingTestActivity]OnPause");
        //unregisterReceiver(mDefaultReceiver);
        unregisterReceiver(mDefault_BroadcastReceiver);
    }
    
	/*public void onClick(View v) {
		if(ConnectButton.isChecked()==true){		
		}
		else if(ConnectButton.isChecked()==false){
		}
	}*/
	
    private void AgingServiceStart() {
        Log.d(Tag, "AgingTestActivity Checked!!");
        
        getLoop_cnt();
       // Intent intent = new Intent(AgingTestActivity.this, Wifi_Aging_Service.class);
        updateWidget();
        //AgingServiceName = startService(intent);
        
        Toast toast = Toast.makeText(getApplicationContext(),
                "Wifi_Aging_Service Start!", Toast.LENGTH_SHORT);
        toast.show();
    }
    
    private void AgingServiceStop() {
//      networkID = 0;
        Loop_cnt = 0;

        Log.d(Tag, "AgingTestActivity unChecked!!");
        
        //Intent i = new Intent();
        //i.setComponent(AgingServiceName);
        //stopService(i);
        if (Loop_thread != null && Loop_thread.isAlive()) {
            Loop_thread.interrupt();
            isRunning = false;
        }
        i = 1;
        
        Toast toast = Toast.makeText(getApplicationContext(),
                "Wifi_Aging_Service destroy!", Toast.LENGTH_LONG);
        toast.show();   
    }
	
    private void getLoop_cnt() {
        String temp = loop.getText().toString();
        Loop_cnt = Integer.parseInt(temp);
        Log.d(Tag, "Loop_cnt  =  " + Loop_cnt);
    }
    
    private void updateResult(int passfail) {
        if(passfail == 1) {
            success_num++;
            success.setText(Integer.toString(success_num));
            Log.i(Tag, "success_num = " + success_num);
        } else if(passfail == -1) {
            fail_num++;
            fail.setText(Integer.toString(fail_num));
            Log.i(Tag, "fail_num = " + fail_num);
        } else if(passfail == 0) {
            success_num = 0;
            success.setText(Integer.toString(success_num));
            fail_num = 0;
            fail.setText(Integer.toString(fail_num));
        }
    }

    private void updateAPconf() {
        /*if(ssid.getText().toString()!=null && !(ssid.getText().toString().equals(""))) {
            connect_ssid = ssid.getText().toString();
        }
        if(passwd.getText().toString()!=null && !passwd.getText().toString().equals("")) {
            connect_password = passwd.getText().toString();
        }*/
    	
    	//Intent intent = new Intent(com.android.settings.WifiApDialog.class);
    	//startActivity(intent);
    }
    
    class DefaultBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (AGING_TEST_SUCCESS.equals(action)) {
                updateResult(1);
            }
            if (AGING_TEST_FAIL.equals(action)) {
                updateResult(-1);
            }
            if (AGING_TEST_FINISHED.equals(action)) {
                ConnectButton.setChecked(false);
                Loop_cnt = 0;
            }
        }
    }
    
    private void updateWidget() {
        isRunning = true;
        mWifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        

        
        mHandler.sendEmptyMessage(0);
    }

    @SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d(tag, "Got message in handler");

            Loop_thread = new Thread(new Runnable() {
                @SuppressLint("SetJavaScriptEnabled")
				public void run() {
                    //Loop_cnt = AgingTestActivity.Loop_cnt;
                    if(!isConnected){
                    	isWifiConnected();
                    }
                    while (isRunning && i <= Loop_cnt) {
                        Log.d(tag, i + "-th Loop\n.");
                        if (!mWifiManager.isWifiEnabled()) {
                        	Log.d(Tag,"Wifi on");
                            mWifiManager.setWifiEnabled(true);
                        }
                        try {
                        	Log.d(tag, "thread sleep for 10seconds");
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //AP 연결이 해제된 경우
                        if(!isConnected){
                        	Log.e(Tag, "Wi-Fi disconnected!!");
                        	//result = isWifiConnected();
                        }
                        
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
                        
						new WaitTask().execute((long)10000);
                        runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								webview.setInitialScale(50);
								WebSettings set = webview.getSettings();
								set.setJavaScriptEnabled(true);
								set.setBuiltInZoomControls(true);
								set.setUseWideViewPort(true);
								set.setLoadWithOverviewMode(true);
								
								webview.loadUrl(urlAddr[i%10]);
								Log.d(Tag, "Connect to "+urlAddr[i%10]);
//								if(i%2 == 1){
//									webview.loadUrl("http://www.google.co.kr");
//									Log.d(Tag, "Connect to www.google.co.kr");
//								}else if(i%2 == 0){
//									webview.loadUrl("http://m.naver.com");
//									Log.d(Tag, "Connect to m.naver.com");
//								}
							}
						});
                        
                        
                        Log.d(tag, "Not interrupted");


                        try {
                        	Log.d(tag, "thread sleep for 10seconds");
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        
                        
                        webview.setWebViewClient(new WebViewClient(){
                        	public void onPageFinished(WebView webview, String url){
                        		Log.d(tag, "onPageFinished!");
                        		updateResult(1);
                        	}
                        	public void onReceivedError(WebView view, int errorCode, String description, 
                        			String failingUrl){
                        		String temp = "";
                        		//Intent intent = new Intent(AgingTestActivity.AGING_TEST_FAIL);
                        		//sendBroadcast(intent);
                        		updateResult(-1);
                        		switch(errorCode){
                        		case 0xfffffff8:
                        			temp = "ERROR_TIMEOUT";
                        			break;
                        		case 0xfffffffa :
                        			temp = "ERROR_CONNECT";
                        			break;
                        		case 0xfffffffc :
                        			temp = "ERROR_AUTHENTICATION";
                        			break;
                        		case 0xfffffff9 :
                        			temp = "ERROR_IO";
                        			break;
                        		case 0xfffffffb :
                        			temp = "ERROR_PROXY_AUTHENTICATION";
                        			break;
                        		case 0xfffffff5 :
                        			temp = "ERROR_FAILED_SSL_HANDSHAKE";
                        			break;
                        		case 0xfffffffe :
                        			temp = "ERROR_HOST_LOOKUP";
                        			break;
                        		default :
                        			temp = String.valueOf(errorCode);
                        			break;
                        		}

                        		Log.e(tag, "onReceivedError : " + temp);
                        	}
                        });
                        
                        i++;
                        Log.d(Tag,"Wifi off");
                        mWifiManager.setWifiEnabled(false);
                        try {
                        	Log.d(tag, "thread sleep for 5seconds");
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    
                    Intent intent = new Intent(AgingTestActivity.AGING_TEST_FINISHED);
                    sendBroadcast(intent);
                    
                }
            });
            
            
            Loop_thread.start();
            Log.d(tag, "Thread id = " + Loop_thread.getName());
        }
    };
    
    
    
    
    

    // this code should be changed to Aging test code 
    private synchronized boolean isWifiConnected() {
        boolean result = false;

        //if disconnected -> try to connect
        if (isConnected == false) {            
            newConfig = new WifiConfiguration();

            newConfig.networkId = -1;
            newConfig.SSID = convertToQuotedString(AgingTestActivity.connect_ssid);
            newConfig.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
            newConfig.preSharedKey = convertToQuotedString(AgingTestActivity.connect_password);
            newConfig.hiddenSSID = false;
            netId = mWifiManager.addNetwork(newConfig);
            
            //            netId = mWifiConfigStoreProxy.addNetwork(newConfig);
            Log.i(tag, "Try to connect with " + newConfig.SSID + " password : " + newConfig.preSharedKey);

            try {
                result = mWifiManager.enableNetwork(netId, true);
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }

        }
        return result;
    }

    public void onDestroy() {
        Log.d(tag, "Wifi_Aging_Service onDestroy");
        if (Loop_thread != null && Loop_thread.isAlive()) {
            Loop_thread.interrupt();
            isRunning = false;
        }
        i = 1;
        result = false;
        mWifiManager.disableNetwork(netId);
        mWifiManager.removeNetwork(netId);

       // unregisterReceiver(mDefaultReceiver);

        super.onDestroy();
    }

    private static String convertToQuotedString(String string) {
        return "\"" + string + "\"";
    }
    
    class Default_BroadcastReceiver extends BroadcastReceiver {
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

            
        }
    };
    
    class WaitTask extends AsyncTask<Long, Void, Void> {
    	
		@Override
		protected Void doInBackground(Long... time) {
			Log.d(Tag, "WaitTask doInBackground");
			SystemClock.sleep((long)time[0]);
			return (null);
		}
		@Override
		protected void onProgressUpdate(Void... item) {

		}

		// doInBackground()
		@Override
		protected void onPostExecute(Void unused) {
			 
		}
	}
    
    
    
}
