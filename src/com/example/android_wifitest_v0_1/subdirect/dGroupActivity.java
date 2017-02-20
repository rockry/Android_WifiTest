package com.example.android_wifitest_v0_1.subdirect;

import com.example.android_wifitest_v0_1.R;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.UpdateLock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;



public class dGroupActivity extends Activity implements OnClickListener{
	private ToggleButton OnOffButton;
	private TextView succes;
	private TextView fail;
	private EditText loop;
	private WifiP2pManager.Channel mChannel;
	private Activity mActivity = null;
	private WifiP2pManager mWifiP2pManager;
	private IntentFilter mIntentFilter;
	private WifiP2pManager.GroupInfoListener mGroupInfoListener;
	private WifiManager mWifiManager;
	private int returnval;
	ScrollView scView;
	private static TextView LogText;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.direct_group);
		mWifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
		mWifiP2pManager = (WifiP2pManager) this.getSystemService(Context.WIFI_P2P_SERVICE);
		if(mWifiManager.getWifiState()==1){
        	mWifiManager.setWifiEnabled(true);
        }		
		Log.e("AutoJK", "CreateGroup start");
		OnOffButton = (ToggleButton)findViewById(R.id.dGroupButton);
		succes = (TextView)findViewById(R.id.dGroupsuccess);
		fail = (TextView)findViewById(R.id.dGroupfail);
		loop = (EditText)findViewById(R.id.dGroupLoop);		
		setting(this, mWifiP2pManager);
		
		LogText = (TextView)findViewById(R.id.log);
		scView = (ScrollView)findViewById(R.id.scroll);
		
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
	public void setting(Activity mActivity, WifiP2pManager mWifiP2pManager){
		
		mIntentFilter = new IntentFilter();
		//mWifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
		mChannel = mWifiP2pManager.initialize(mActivity, mActivity.getMainLooper(), null);
		mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		  if(mActivity != null){
	            mActivity.registerReceiver(mReceiver, mIntentFilter);
	       }
	}



	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		if(OnOffButton.isChecked()==true){
			Log.d("AutoJK", "create group gogogogogogo~~~~~");
			mCreateGroup(1, mWifiP2pManager);
		}
		else if(OnOffButton.isChecked()==false){
			Log.d("AutoJK", "hear~~~~~");
			mRemoveGroup(1,mWifiP2pManager);
		}
	}
	
	public int mCreateGroup(int p, WifiP2pManager mWifiP2pManager) {
		if(p==1){
			
			
			if (mWifiP2pManager != null) {
				SystemClock.sleep(500);
				mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
		            public void onSuccess() {}
		            public void onFailure(int reason) {}
		        });
				mWifiP2pManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
	                public void onSuccess() {
	                    Log.d("AutoJK", " create group success");
	                    returnval = 0;
	                    updateLog("Create Group success");
	                }
	                public void onFailure(int reason) {
	                    Log.d("AutoJK", " create group fail " + reason);
	                    returnval = 1;
	                    updateLog("Create Group Fail Reason : " + reason);
	                }
	            });
			}
		}else{
			
			
			if (mWifiP2pManager != null) {
				SystemClock.sleep(500);
				mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
		            public void onSuccess() {}
		            public void onFailure(int reason) {}
		        });
				mWifiP2pManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
	                public void onSuccess() {
	                    Log.d("AutoJK", " create group success");
	                    returnval = 0;
	                    //updateLog("Create Group success");
	                }
	                public void onFailure(int reason) {
	                    Log.d("AutoJK", " create group fail " + reason);
	                    returnval = 1;
	                    //updateLog("Create Group Fail Reason : " + reason);
	                }
	            });
			}
			
		}
		return returnval;
		
	}
	
	public int mRemoveGroup(int p, WifiP2pManager mWifiP2pManager) {
		if(p==1){
				
			 if (mWifiP2pManager != null) {
				 SystemClock.sleep(500);
	             mWifiP2pManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
	                 public void onSuccess() {
	                     Log.e("AutoJK", " remove group success");
	                     returnval = 0;
	                     updateLog("Remove Group Success");
	                 }
	                 public void onFailure(int reason) {
	                     Log.e("AutoJK", " remove group fail " + reason);
	                     returnval = 1;
	                     updateLog("Remove Group Fail Reason : " + reason);
	                 }
	             });
	         }
		}else {
	
			
			 if (mWifiP2pManager != null) {
				 SystemClock.sleep(500);
	             mWifiP2pManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
	                 public void onSuccess() {
	                     Log.e("AutoJK", " remove group success");
	                     returnval = 0;
	                     //updateLog("Remove Group Success");
	                 }
	                 public void onFailure(int reason) {
	                     Log.e("AutoJK", " remove group fail " + reason);
	                     returnval = 1;
	                     //updateLog("Remove Group Fail Reason : " + reason);
	                 }
	             });
	         }		
		}
		return returnval;
		
	}
	
	 private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
				Log.d("AutoJK", "WIFI_P2P_THIS_DEVICE_CHANGED_ACTION");
				mGroupInfoListener = new WifiP2pManager.GroupInfoListener() {
					public void onGroupInfoAvailable(WifiP2pGroup group) {
						// TODO Auto-generated method stub
						if (group != null){
						Toast.makeText(mActivity, "I am GO. " + " password: " + group.getPassphrase() + "  ssid: " + group.getNetworkName(), Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(mActivity, "I am not GO", Toast.LENGTH_SHORT).show();
						}
					}
					
				};
				mWifiP2pManager.requestGroupInfo(mChannel, mGroupInfoListener);
			}
		}
	 };

}
