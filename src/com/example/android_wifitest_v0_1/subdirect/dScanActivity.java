package com.example.android_wifitest_v0_1.subdirect;

import com.example.android_wifitest_v0_1.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class dScanActivity extends Activity implements OnClickListener{
	private ToggleButton OnOffButton;
	private TextView succes;
	private TextView fail;
	private EditText loop;
	private int alloop=0;
	private WifiP2pManager.Channel mChannel;
	private Activity mActivity = null;
	private int returnum;
	private int passcount=1;
	private int failcount=1;
	private int numRecursive = 0;
	private WifiP2pManager mWifiP2pManager;
	private int returnval;
	private WifiManager mWifiManager;
	ScrollView scView;
	private static TextView LogText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.direct_scan);
		OnOffButton = (ToggleButton)findViewById(R.id.dScanButton);
		succes = (TextView)findViewById(R.id.dScansuccess);
		fail = (TextView)findViewById(R.id.dScanfail);
		loop = (EditText)findViewById(R.id.dScanLoop);
		LogText = (TextView)findViewById(R.id.log);
		scView = (ScrollView)findViewById(R.id.scroll);
		mActivity = dScanActivity.this;
		mWifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
		if(mWifiManager.getWifiState()==1){
        	mWifiManager.setWifiEnabled(true);
        }	
		mWifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		
		
		OnOffButton.setOnClickListener(this);
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
	public void WifiON(WifiManager mWifiManager){
		
		if(mWifiManager.getWifiState()==1){
        	mWifiManager.setWifiEnabled(true);
        }	
	}
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(OnOffButton.isChecked()==true){
			
			Log.e("AutoJK", "~~~~~~~~~~~~~~~~~~1");
			Log.e("AutoJK","parseInt     "+Integer.parseInt(loop.getText().toString()));
					
			DirectScan(1, this, mWifiP2pManager);
		}
		else if(OnOffButton.isChecked()==false){
			passcount = 1;
			failcount = 1;
		}
	}
	@SuppressLint("NewApi")
	public int DirectScan(int p, Activity mActivity, WifiP2pManager mWifiP2pManager){
		if(p == 1){
			final Activity jActivity = mActivity;		
			
			if (mWifiP2pManager != null ) {
				mChannel = mWifiP2pManager.initialize(mActivity, mActivity.getMainLooper(), null);
	            if (mChannel == null) {                
	                mWifiP2pManager = null;
	            }
	        }
	        mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
	            public void onSuccess() {
	            	returnum = 1;
	            	Log.e("AutoJK", "success number 1");
	            	OnOffButton.setChecked(false);
	            	returnval = 0;
	            	Toast.makeText(jActivity, "discover success", Toast.LENGTH_SHORT).show();
	            	updateLog("Discover Success");
	            }
	            public void onFailure(int reason) {
	            	returnum = 0;
	            	Log.e("AutoJK", "fail number 0");
	            	fail.setText(""+failcount++);
	            	SystemClock.sleep(3*1000);
	            	if (Integer.parseInt(fail.getText().toString())<
	                		Integer.parseInt(loop.getText().toString())) {
	                		DirectScan(1, jActivity, dScanActivity.this.mWifiP2pManager);
	                	}
	            	returnval = 1;
	            	updateLog("Discover Fail Reason : " + reason);
	            }
	        });
        } else {				//alltest½Ã µé¾î¿È
        	final WifiP2pManager allWifiP2pManager = mWifiP2pManager;
        	final Activity jActivity = mActivity;
        	
			if (mWifiP2pManager != null ) {
				mChannel = mWifiP2pManager.initialize(mActivity, mActivity.getMainLooper(), null);
	            if (mChannel == null) {                
	                mWifiP2pManager = null;
	            }
	        }
	        mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
	            public void onSuccess() {
	            	returnum = 1;
	            	Log.e("AutoJK", "success number 1");
	            	returnval = 0;
	            	Toast.makeText(jActivity, "discover success", Toast.LENGTH_SHORT).show();
	            	//updateLog("Discover Success");
	            }
	            public void onFailure(int reason) {
	            	returnum = 0;
	            	Log.e("AutoJK", "fail number 0");
//	            	SystemClock.sleep(3*1000);
//	            	if (alloop<10) {
//	                		DirectScan(0, jActivity,allWifiP2pManager);
//	                		Toast.makeText(jActivity, "Trying to discover again..", Toast.LENGTH_SHORT).show();
//	            	}
	            	returnval = 1;
	            	//updateLog("Discover Fail Reason : " + reason);
	            }
	        });
        }
		Log.e("AutoJK", "returnval = " + returnval);
		return returnval;
        
	}

	public void onPeersAvailable(WifiP2pDeviceList peers) {
		// TODO Auto-generated method stub
		
	}

}
