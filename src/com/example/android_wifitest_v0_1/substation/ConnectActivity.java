package com.example.android_wifitest_v0_1.substation;

import com.example.android_wifitest_v0_1.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ConnectActivity extends Activity implements OnClickListener{
    private String Tag = "ConnectActivity";
    
	private ToggleButton ConnectButton;
	private EditText ssid;
	private EditText passwd;
	private EditText loop;
	private TextView success;
	private TextView fail;
	ScrollView scView;
	private static TextView LogText;

    private static ComponentName WifiServiceName;
    private static IntentFilter mDefaultFilter;
    private static DefaultBroadcastReceiver mDefaultReceiver;
    
    public static int success_num;
    public static int fail_num;
    public static int Loop_cnt;
    public static boolean isTestRunning = false;
    public static String connect_ssid = "Wi-Fi_Sanity_AP";
    public static String connect_password = "lge12345";

    public static final String WIFI_CONNECT_SUCCESS = "com.lge.action.WIFI_CONNECT_SUCCESS";
    public static final String WIFI_CONNECT_FAIL = "com.lge.action.WIFI_CONNECT_FAIL";
    public static final String WIFI_CONNECT_FINISH = "com.lge.action.WIFI_CONNECT_FINISH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      
        super.onCreate(savedInstanceState);
        setContentView(R.layout.station_connect);
        ConnectButton = (ToggleButton)findViewById(R.id.sConnectButton);
        ssid = (EditText)findViewById(R.id.sConnectSSID);
        passwd = (EditText)findViewById(R.id.sConnectPASS);
        loop = (EditText)findViewById(R.id.sConnectLoop);
        success = (TextView)findViewById(R.id.sConnectSuccess);
        fail = (TextView)findViewById(R.id.sConnectFail);
        LogText = (TextView)findViewById(R.id.log);
		scView = (ScrollView)findViewById(R.id.scroll);

        ConnectButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                	if(!isTestRunning) {
                		updateResult(0);
                		updateAPconf();
                		WifiServiceStart();
                	}
                } else {
                    WifiServiceStop();
                }
            }
        });

        mDefaultFilter = new IntentFilter();
        mDefaultFilter.addAction(WIFI_CONNECT_SUCCESS);
        mDefaultFilter.addAction(WIFI_CONNECT_FAIL);
        mDefaultFilter.addAction(WIFI_CONNECT_FINISH);

        mDefaultReceiver = new DefaultBroadcastReceiver();
        
    }
    public void updateLog(String string) {
		LogText.setText(LogText.getText() + "\n" + string);
		scView.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				scView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
	}
    @Override
    public void onResume(){
        super.onResume();
        Log.d(Tag, "[ConnectActivity]OnResume");
        connect_ssid = "Wi-Fi_Sanity_AP";
        connect_password = "lge12345";
        registerReceiver(mDefaultReceiver, mDefaultFilter);
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(Tag, "[ConnectActivity]OnPause");
        unregisterReceiver(mDefaultReceiver);
    }
//    @Override
//    public void onDestroy(){
//        super.onDestroy();
//        Log.d(Tag, "[ConnectActivity]onDestroy");
//        WifiServiceStop();
//    }
	@Override
	public void onClick(View v) {
		if(ConnectButton.isChecked()==true){		
		}
		else if(ConnectButton.isChecked()==false){
		}
	}
	
    private void WifiServiceStart() {
        Log.d(Tag, "ConnectActivity Checked!!");
        
        getLoop_cnt();
        Intent intent = new Intent(ConnectActivity.this, Wifi_Connect_Service.class);
        
        WifiServiceName = startService(intent);
        Toast toast = Toast.makeText(getApplicationContext(),
                "WiFi_Connect_service Start!", Toast.LENGTH_SHORT);
        toast.show();
        isTestRunning = true;
    }
    
    private void WifiServiceStop() {
//      networkID = 0;
        Loop_cnt = 0;

        Log.d(Tag, "ConnectActivity unChecked!!");

        Intent i = new Intent();
        i.setComponent(WifiServiceName);
        stopService(i);
               
        Toast toast = Toast.makeText(getApplicationContext(),
                "WiFi_Connect_service destroy!", Toast.LENGTH_LONG);
        toast.show();
        isTestRunning = false;
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
        if(ssid.getText().toString()!=null && !(ssid.getText().toString().equals(""))) {
            connect_ssid = ssid.getText().toString();
        }
        if(passwd.getText().toString()!=null && !passwd.getText().toString().equals("")) {
            connect_password = passwd.getText().toString();
        }
    }
    
    class DefaultBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (WIFI_CONNECT_SUCCESS.equals(action)) {
                updateResult(1);
                updateLog(WIFI_CONNECT_SUCCESS);
            }
            if (WIFI_CONNECT_FAIL.equals(action)) {
                updateResult(-1);
                updateLog(WIFI_CONNECT_FAIL);
            }
            if (WIFI_CONNECT_FINISH.equals(action)) {
                ConnectButton.setChecked(false);
                Loop_cnt = 0;
                updateLog(WIFI_CONNECT_FINISH);
            }
        }
    };
}
