package com.example.android_wifitest_v0_1.subhotspot;

import java.util.TimerTask;

import com.example.android_wifitest_v0_1.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.provider.Settings;

public class hConnectionActivity extends Activity implements OnClickListener{
	private ToggleButton OnOffButton;
	private TextView result;
	private EditText loop;
	ScrollView scView;
	private static TextView LogText;
	
	private static IntentFilter mFilter;
    private static BroadcastReceiver mReceiver;
    private WifiManager mWifiManager;
    private WifiConfiguration mWifiConfig;
    private boolean success;

    private Handler mHandler;
    private Runnable mRunnable;
    private Context mContext;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hotspot_connect);
		
		OnOffButton = (ToggleButton)findViewById(R.id.hConnectButton);
		result = (TextView)findViewById(R.id.hConnectsuccess);
		loop = (EditText)findViewById(R.id.hConnectLoop);
		LogText = (TextView)findViewById(R.id.log);
		scView = (ScrollView)findViewById(R.id.scroll);
		loop.setText("N/A");
		loop.setEnabled(false);
		success = false;
		mWifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
		mWifiConfig = mWifiManager.getWifiApConfiguration();
		mWifiConfig.SSID = "Wi-Fi_Sanity_AP";
		mWifiConfig.allowedKeyManagement.set(KeyMgmt.WPA2_PSK);
		mWifiConfig.preSharedKey = "lge12345";
		mFilter = new IntentFilter();
		mFilter.addAction("com.lge.wifi.sap.WIFI_SAP_DHCP_INFO_CHANGED");
		mHandler = new Handler();
		mContext = getBaseContext();
		OnOffButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if(isChecked){ 
		    		result.setText("Waiting on Connection for 60s");
		        	mWifiManager.setWifiApEnabled(mWifiConfig, true);
		        	registerReceiver(mReceiver, mFilter);
		        	mHandler.postDelayed(mRunnable, 60000);
		        	success = false;
		        }
		        else{
		        	unregisterReceiver(mReceiver);
		        	mWifiManager.setWifiApEnabled(null, false);
		        }
		        	
		    }
		});
		mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("com.lge.wifi.sap.WIFI_SAP_DHCP_INFO_CHANGED")){
                	result.setText("PASS");
                	success = true;
                	OnOffButton.setChecked(false);
                }
            }                
        };
        mRunnable = new Runnable() {

            @Override

            public void run() {
        		if(success == false){
        			result.setText("FAIL - Time Out 60s");
        			OnOffButton.setChecked(false);
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(OnOffButton.isChecked()==true){
			
		}
		else if(OnOffButton.isChecked()==false){
			
		}
	}
}
