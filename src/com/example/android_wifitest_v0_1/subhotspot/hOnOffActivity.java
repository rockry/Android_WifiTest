package com.example.android_wifitest_v0_1.subhotspot;


import com.example.android_wifitest_v0_1.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class hOnOffActivity extends Activity implements OnClickListener{
	private String Tag = "hOnOffActivity";
	private ToggleButton OnOffButton;
	private static TextView success;
	private static TextView fail;
	private EditText loop;
	ScrollView scView;
	private static TextView LogText;
	
	private static ComponentName WifiAPServiceName;
	private static IntentFilter mFilter;
    private static BroadcastReceiver mReceiver;
    
	public static int success_num;
    public static int fail_num;
	public static int Loop_cnt;
	public static boolean isTestRunning = false;
	public static WifiManager mWifiManager;
	public static int wifiApState;
	
	public static final String WIFIAP_ONOFF_SUCCESS = "com.lge.action.WIFIAP_ONOFF_SUCCESS";
    public static final String WIFIAP_ONOFF_FAIL = "com.lge.action.WIFIAP_ONOFF_FAIL";
    public static final String WIFIAP_ONOFF_FINISH = "com.lge.action.WIFIAP_SERVICE_FINISH";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hotspot_onoff);
		mWifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
		wifiApState = mWifiManager.getWifiApState();
		OnOffButton = (ToggleButton)findViewById(R.id.hOnOffButton);
		success = (TextView)findViewById(R.id.hOnOffsuccess);
		fail = (TextView)findViewById(R.id.hOnOfffail);
		loop = (EditText)findViewById(R.id.hOnOffLoop);
		LogText = (TextView)findViewById(R.id.log);
		scView = (ScrollView)findViewById(R.id.scroll);
		OnOffButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if(isChecked) {
		        	if(!isTestRunning) {
		            updateResult(0);
		            WifiAPServiceStart();
		        	}
		        } else {
		            WifiAPServiceStop();
		        }
		    }
		});
		mFilter = new IntentFilter();
        mFilter.addAction(WIFIAP_ONOFF_SUCCESS);
        mFilter.addAction(WIFIAP_ONOFF_FAIL);
        mFilter.addAction(WIFIAP_ONOFF_FINISH);
        
		mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (WIFIAP_ONOFF_SUCCESS.equals(action)) {
                    updateResult(1);
                    updateLog(WIFIAP_ONOFF_SUCCESS);
                }
                if (WIFIAP_ONOFF_FAIL.equals(action)) {
                    updateResult(-1);
                    updateLog(WIFIAP_ONOFF_FAIL);
                }
                if (WIFIAP_ONOFF_FINISH.equals(action)) {
                    OnOffButton.setChecked(false);
                    Loop_cnt = 0;
                    updateLog(WIFIAP_ONOFF_FINISH);
                }
            }
        };
        
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
	    Log.d(Tag, "[hOnOffActivity]OnResume");
	    registerReceiver(mReceiver, mFilter);
	}
	@Override
	public void onPause() {
	    super.onPause();
	    Log.d(Tag, "[hOnOffActivity]OnPause");
	    unregisterReceiver(mReceiver);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(OnOffButton.isChecked()==true){
			
		}
		else if(OnOffButton.isChecked()==false){
			
		}
	}
	
	private void WifiAPServiceStart() {
        Log.d(Tag, "OnOffActivity Checked!!");
        getLoop_cnt();
        Intent intent = new Intent(hOnOffActivity.this, WifiAP_OnOff_Service.class);
        
        WifiAPServiceName = startService(intent);
        Toast toast = Toast.makeText(getApplicationContext(),
                "WiFiAPOnOff_service Start!", Toast.LENGTH_SHORT);
        toast.show();
        isTestRunning = true;
    }
    
    private void WifiAPServiceStop() {
        Loop_cnt = 0;

        Log.d(Tag, "OnOffActivity unChecked!!");

        Intent i = new Intent();
        i.setComponent(WifiAPServiceName);
        stopService(i);
               
        Toast toast = Toast.makeText(getApplicationContext(),
                "WiFiAPOnOff_service destroy!", Toast.LENGTH_LONG);
        toast.show();   
        isTestRunning = false;
    }

	
    private void getLoop_cnt() {
        String temp = loop.getText().toString();
        Loop_cnt = Integer.parseInt(temp);
        Log.d(Tag, "[hOnOffActivity]Loop_cnt  =  " + Loop_cnt);
    }
    
    //°ø¿ë
    private void updateResult(int passfail) {
        if(passfail == 1) {
            success_num++;
            success.setText(Integer.toString(success_num));
            Log.i(Tag, "[hOnOffActivity]success_num = " + success_num);
        } else if(passfail == -1) {
            fail_num++;
            fail.setText(Integer.toString(fail_num));
            Log.i(Tag, "[hOnOffActivity]fail_num = " + fail_num);
        } else if(passfail == 0) {
            success_num = 0;
            success.setText(Integer.toString(success_num));
            fail_num = 0;
            fail.setText(Integer.toString(fail_num));
        }
    }
}
