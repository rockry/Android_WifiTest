package com.example.android_wifitest_v0_1;

import com.example.android_wifitest_v0_1.subhotspot.hConnectionActivity;
import com.example.android_wifitest_v0_1.subhotspot.hOnOffActivity;
import com.example.android_wifitest_v0_1.substation.ApAutoConnectActivity;
import com.example.android_wifitest_v0_1.substation.ConnectActivity;
import com.example.android_wifitest_v0_1.substation.HiddenApConnectActivity;
import com.example.android_wifitest_v0_1.substation.OnOffActivity;
import com.example.android_wifitest_v0_1.substation.StaticIpActivity;
import com.example.android_wifitest_v0_1.substation.SuspendResumeActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;


public class HotspotActivity extends Activity implements OnItemClickListener{
	private String[] items={"Wi-Fi Hotspot ON/OFF",
							"Wi-Fi Hotspot Connection"};
	private Intent intent;
	private WifiManager mWifiManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hotspot);
        
        ListView list = (ListView)findViewById(R.id.listView);
        
        list.setAdapter(new ArrayAdapter<String>(this,
        		R.layout.simple_white_list, items));
        
        list.setOnItemClickListener(this);
        mWifiManager = (WifiManager) this.getSystemService(WIFI_SERVICE);
        if(mWifiManager.isWifiApEnabled()){
        	mWifiManager.setWifiApEnabled(null, false);
        }
        if(mWifiManager.isWifiEnabled()){
        	mWifiManager.setWifiEnabled(false);
        }
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub
		if(position == 0){						//Wi-Fi Hotspot ON/OFF
			intent = new Intent(this, hOnOffActivity.class);
		}
		if(position == 1){						//Wi-Fi Connect
			intent = new Intent(this, hConnectionActivity.class);
		}
		startActivity(intent);
		
	}
	
}