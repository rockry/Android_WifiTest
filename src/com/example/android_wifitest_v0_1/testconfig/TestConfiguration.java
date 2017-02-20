package com.example.android_wifitest_v0_1.testconfig;

import com.example.android_wifitest_v0_1.R;
import com.example.android_wifitest_v0_1.R.layout;
import com.example.android_wifitest_v0_1.substation.ConnectActivity;
import com.example.android_wifitest_v0_1.substation.HiddenApConnectActivity;
import com.example.android_wifitest_v0_1.substation.Wifi_Connect_Service;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class TestConfiguration extends Activity implements OnCheckedChangeListener{
	private String Tag = "Wifi_TestConfiguration";
	static int checked = 0;
	WifiManager mWifiManager ;
	WifiConfiguration mWifiConfig ;
	CheckBox config_1, config_2, config_3, config_4;
	TextView status;
	
	WifiConfiguration newConfig;
	private Thread VAP_thread;
	boolean connected = false;
	int netId = -1;
    public static String connect_ssid = "Wi-Fi_Sanity_AP";
    public static String hidden_connect_ssid = "Wi-Fi_Hidden_AP";
    public static String connect_password = "lge12345";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mWifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
		mWifiConfig = mWifiManager.getWifiApConfiguration();
		
		setContentView(R.layout.test_configuration);
		status = (TextView)findViewById(R.id.text);
		config_1  = (CheckBox)findViewById(R.id.config_1);
		config_2  = (CheckBox)findViewById(R.id.config_2);
		config_3  = (CheckBox)findViewById(R.id.config_3);
		config_4  = (CheckBox)findViewById(R.id.config_4);
		config_1.setOnCheckedChangeListener(this);
		config_2.setOnCheckedChangeListener(this);
		config_3.setOnCheckedChangeListener(this);
		config_4.setOnCheckedChangeListener(this);
	}
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// TODO Auto-generated method stub
		if(config_1.equals(arg0)){  // Hotspot ON / OFF 
			if(arg1 == true){
				onWifiAP();
				config_2.setClickable(false);
				config_3.setClickable(false);
				config_4.setClickable(false);
				config_1.setText("On");
				status.setText("SSID : Wi-Fi_Sanity_AP\nPASSWORD : lge12345");
			}
			else{
				mWifiManager.setWifiApEnabled(mWifiConfig, false);
				config_2.setClickable(true);
				config_3.setClickable(true);
				config_4.setClickable(true);
				config_1.setText("On/Off");
				status.setText("");
			}
		}
		else if(config_2.equals(arg0)){ // Hidden Hotspot ON / OFF
			if(arg1 == true){
				onWifiHAP();
				config_1.setClickable(false);
				config_3.setClickable(false);
				config_4.setClickable(false);
				config_2.setText("On");
				status.setText("SSID : Wi-Fi_Hidden_AP\nPASSWORD : lge12345");
			}
			else{
				mWifiManager.setWifiApEnabled(mWifiConfig, false);
				config_1.setClickable(true);
				config_3.setClickable(true);
				config_4.setClickable(true);
				config_2.setText("On/Off");
				status.setText("");
			}
		}
		else if(config_3.equals(arg0)){ // Trying Connect AP
			if(arg1 == true){
				verifyAP();
				config_1.setClickable(false);
				config_2.setClickable(false);
				config_4.setClickable(false);
				config_3.setText("On");
			}
			else{
				if(VAP_thread != null) {
					VAP_thread.interrupt();
				}
					
				if(netId != -1) {
					mWifiManager.disableNetwork(netId);
					mWifiManager.removeNetwork(netId);
					connected = false;
				}
				mWifiManager.setWifiEnabled(false);
				config_1.setClickable(true);
				config_2.setClickable(true);
				config_4.setClickable(true);
				config_3.setText("On/Off");
				status.setText("");
			}			
		}
		else if(config_4.equals(arg0)){ // Trying Connect Hidden AP
			if(arg1 == true){
				verifyHiddenAP();
				config_1.setClickable(false);
				config_2.setClickable(false);
				config_3.setClickable(false);
				config_4.setText("On");
			}
			else{
				if(VAP_thread != null) {
					VAP_thread.interrupt();
				}
					
				if(netId != -1) {
					mWifiManager.disableNetwork(netId);
					mWifiManager.removeNetwork(netId);
					connected = false;
				}
				mWifiManager.setWifiEnabled(false);
				config_1.setClickable(true);
				config_2.setClickable(true);
				config_3.setClickable(true);
				config_4.setText("On/Off");
				status.setText("");
			}			
		}
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	public void onDestroy() {
		if(VAP_thread != null) {
			VAP_thread.interrupt();
		}
			
		if(netId != -1) {
			mWifiManager.disableNetwork(netId);
			mWifiManager.removeNetwork(netId);
			connected = false;
		}
        super.onDestroy();
    }
	
	public void onWifiAP(){
		if(mWifiManager.isWifiApEnabled() || mWifiManager.isWifiEnabled()){
			mWifiManager.setWifiEnabled(false);
			mWifiManager.setWifiApEnabled(null, false);
		}
		mWifiConfig.SSID = "Wi-Fi_Sanity_AP";
		mWifiConfig.preSharedKey = "lge12345";
		mWifiConfig.allowedKeyManagement.set(KeyMgmt.WPA2_PSK);
		mWifiConfig.hiddenSSID = false;
		mWifiManager.setWifiApEnabled(mWifiConfig, true);
	}
	public void onWifiHAP(){
		if(mWifiManager.isWifiApEnabled() || mWifiManager.isWifiEnabled()){
			mWifiManager.setWifiEnabled(false);
			mWifiManager.setWifiApEnabled(null, false);
		}
		mWifiConfig.SSID = "Wi-Fi_Hidden_AP";
		mWifiConfig.preSharedKey = "lge12345";
		mWifiConfig.allowedKeyManagement.set(KeyMgmt.WPA2_PSK);
		Context mContext = getBaseContext();
		mWifiConfig.hiddenSSID = true;
		Settings.System.putInt(mContext.getContentResolver(), "wifi_ssid_visibility"/*SettingsEx.System.WIFI_AP_SSID_VISIBILITY*/, 1);
		mWifiManager.setWifiApEnabled(mWifiConfig, true);
	}
	
	public void updateSuccessVAP() {
		status.setText("Success to connect with \"Wi-Fi_Sanity_AP\"");
	}
	public void updateSuccessVHAP() {
		status.setText("Success to connect with \"Wi-Fi_HIdden_AP\"");
	}
	public void updateFailVAP() {
		status.setText("Fail to connect with AP");
	}
	
	public void verifyAP(){
		netId = -1;
		connected = false;

		if(mWifiManager == null)
			mWifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        mWifiManager.disconnect();

		newConfig = new WifiConfiguration();

		newConfig.networkId = -1;
		newConfig.SSID = convertToQuotedString(connect_ssid);
		newConfig.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
		newConfig.allowedKeyManagement.set(KeyMgmt.WPA2_PSK);
		newConfig.preSharedKey = convertToQuotedString(connect_password);
		newConfig.hiddenSSID = false;
		
		netId = mWifiManager.addNetwork(newConfig);

		if( mWifiManager.enableNetwork(netId, true)) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Log.d(Tag, "IP : " + mWifiManager.getConnectionInfo().getIpAddress() + " ,SSID :" + mWifiManager.getConnectionInfo().getSSID());	
			
			if(mWifiManager.getConnectionInfo().getIpAddress() != 0 && 
					mWifiManager.getConnectionInfo().getSSID().equals("Wi-Fi_Sanity_AP")) {
				Toast.makeText(this, "Success to connect with \"Wi-Fi_Sanity_AP\"", Toast.LENGTH_LONG).show();
				connected = true;
				updateSuccessVAP();				
			}
		}
		mHandler.sendEmptyMessage(0);
	}
	public void verifyHiddenAP(){
		netId = -1;
		connected = false;
		
		if(mWifiManager == null)
			mWifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        mWifiManager.disconnect();

		newConfig = new WifiConfiguration();

		newConfig.networkId = -1;
		newConfig.SSID = convertToQuotedString(hidden_connect_ssid);
		newConfig.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
		newConfig.allowedKeyManagement.set(KeyMgmt.WPA2_PSK);
		newConfig.preSharedKey = convertToQuotedString(connect_password);
		newConfig.hiddenSSID = true;
		netId = mWifiManager.addNetwork(newConfig);

		if( mWifiManager.enableNetwork(netId, true)) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if(mWifiManager.getConnectionInfo().getIpAddress() != 0 &&
					mWifiManager.getConnectionInfo().getSSID().equals("Wi-Fi_Hidden_AP")) {
				Toast.makeText(this, "Success to connect with \"Wi-Fi_Hidden_AP\"", Toast.LENGTH_LONG).show();
				connected = true;
				updateSuccessVHAP();
				Log.d(Tag, "Success to connect with \"Wi-Fi_Hidden_AP\" Directly");	
			}
		}
		mHandler.sendEmptyMessage(1);
	}
	
    public static String convertToQuotedString(String string) {
        return "\"" + string + "\"";
    }
	
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			final boolean hidden;
			if(msg.what == 0 || msg.what == 1) {
				Log.d(Tag, "Get in handler : what : 0 or 1");
				if(msg.what == 0)
					hidden = false;
				else
					hidden = true;

				VAP_thread = new Thread(new Runnable() {
					public void run() {
						int num = 0;
						while(connected==false){
							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							if (mWifiManager.isWifiEnabled()) {		
								mWifiManager.setWifiEnabled(false);
							} else {
								mWifiManager.setWifiEnabled(true);
								mHandler.post(new Runnable() {
									@Override
									public void run() {
										Toast T = Toast.makeText(
												getApplicationContext(),
												"Trying to connect ...", Toast.LENGTH_LONG);
										T.show();
									}
								});
								mWifiManager.addNetwork(newConfig);
								mWifiManager.enableNetwork(netId, true);
								try {
									Thread.sleep(10000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
								
								if(hidden == false &&
										mWifiManager.getConnectionInfo().getIpAddress() != 0 && 
										mWifiManager.getConnectionInfo().getSSID().equals(ConnectActivity.connect_ssid)) {
									mHandler.post(new Runnable() {
										@Override
										public void run() {
											Toast T = Toast.makeText(
													getApplicationContext(),
													"Success to connect with \"Wi-Fi_Sanity_AP\"", Toast.LENGTH_LONG);
											T.show();
										}
									});
									connected = true;
									mHandler.sendEmptyMessage(2);
									Log.d(Tag, "Success to connect with Wi-Fi_Sanity_AP");
									break;
								}
								if(hidden == true &&
										mWifiManager.getConnectionInfo().getIpAddress() != 0 &&
										mWifiManager.getConnectionInfo().getSSID().equals(HiddenApConnectActivity.connect_ssid)) {
									mHandler.post(new Runnable() {
										@Override
										public void run() {
											Toast T = Toast.makeText(
													getApplicationContext(),
													"Success to connect with \"Wi-Fi_Hidden_AP\"", Toast.LENGTH_LONG);
											T.show();
										}
									});
									connected = true;
									mHandler.sendEmptyMessage(3);
									Log.d(Tag, "Success to connect with Wi-Fi_Hidden_AP");
									break;
								}	
								//	            wManager2.enableNetwork(OnOffActivity.networkID, true);
							}
							Log.d(Tag, "handler : num = " + num);
							if(num++ > 3) {
								mHandler.sendEmptyMessage(4);
								Log.d(Tag, "Fail to Connect with AP");
								break;
							}
							
						}
					}
				});
				VAP_thread.start();
			}
			if(msg.what == 2) {
				updateSuccessVAP();
			}
			if(msg.what == 3) {
				updateSuccessVHAP();
			}
			if(msg.what == 4) {
				updateFailVAP();
			}
		}
	};

}
	