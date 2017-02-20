package com.example.android_wifitest_v0_1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.android_wifitest_v0_1.substation.ConnectActivity;
import com.example.android_wifitest_v0_1.substation.HiddenApConnectActivity;
import com.example.android_wifitest_v0_1.substation.Wifi_Connect_Service;
import com.example.android_wifitest_v0_1.testconfig.TestConfiguration;

import android.R.integer;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener{
	private Intent intent;
//	private String[] items={"ALLTEST", "Station", "Hotspot","Direct", "Display", "Ap ON/OFF", "Ap Hidden ON/OFF", "Verify Ap connect", "Verify Hidden Ap connect"};
//	private final int ALLTEST =0,STATION=1, HOTSPOT=2, DIRECT=3, DISPLAY=4, APON=5, APHON=6, VAP=7, VHAP=8;
	private final int ALLTEST =0,AGINGTEST=1, CONFIG=2;
	private Button AllButton, AgingButton, ConfigButton, AdditionalButton;
	
	public HashMap<Integer, ArrayList> hashMap;

	private Thread VAP_thread;
	boolean connected = false;
	WifiManager mWifiManager;
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AllButton = (Button)findViewById(R.id.allbutton);
        AgingButton = (Button)findViewById(R.id.agingbutton);
        ConfigButton = (Button)findViewById(R.id.configbutton);
        AdditionalButton = (Button) findViewById(R.id.additionalbutton);
        
        AllButton.setOnClickListener(buttonListener);
        AgingButton.setOnClickListener(buttonListener);
        ConfigButton.setOnClickListener(buttonListener);
        AdditionalButton.setOnClickListener(buttonListener);
        
//        ListView list = (ListView)findViewById(R.id.listView);
        
//        list.setAdapter(new ArrayAdapter<String>(this,
//        		android.R.layout.simple_list_item_1, items));
        
//        list.setOnItemClickListener(this);
        
        
    }
    
	View.OnClickListener buttonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.allbutton:
				intent = new Intent(MainActivity.this, allActivity.class);
				startActivity(intent);
				break;
			case R.id.agingbutton:
				//intent = new Intent(MainActivity.this, AgingTest.class);
				intent = new Intent(MainActivity.this, AgingTest2.class);
				startActivity(intent);
				break;
			case R.id.configbutton:
				intent = new Intent(MainActivity.this, TestConfiguration.class);
				startActivity(intent);
				break;
			case R.id.additionalbutton:
				intent = new Intent(MainActivity.this, AdditionalActivity.class);
				startActivity(intent);
				break;
			}
			
		}
	};


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}	
}
