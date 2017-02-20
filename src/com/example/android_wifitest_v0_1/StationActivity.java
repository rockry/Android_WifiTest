package com.example.android_wifitest_v0_1;

import com.example.android_wifitest_v0_1.substation.AgingTestActivity;
import com.example.android_wifitest_v0_1.substation.ApAutoConnectActivity;
import com.example.android_wifitest_v0_1.substation.ConnectActivity;
import com.example.android_wifitest_v0_1.substation.HiddenApConnectActivity;
import com.example.android_wifitest_v0_1.substation.OnOffActivity;
import com.example.android_wifitest_v0_1.substation.StaticIpActivity;
import com.example.android_wifitest_v0_1.substation.SuspendResumeActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class StationActivity extends Activity implements OnItemClickListener{
//	private String[] items={"Wi-Fi On/Off",					//0
//							"Wi-Fi Connect",				//1
//							"Static IP",					//2
//							"Hidden AP Connect",			//3
//							"Suspend/Resume",				//4
//							"AP AutoConnect"};				//5
	private String[] items={"Wi-Fi On/Off",					//0
			"Wi-Fi Connect",				//1
			"Static IP",					//2
			"Hidden AP Connect",			//3
			"BrowserAgingTest",
			"Reset Test",
			"Suspend/Resume",
			"AP AutoConnect"			
		};

	private Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_station);
        
        ListView list = (ListView)findViewById(R.id.listView);
        
        list.setAdapter(new ArrayAdapter<String>(this,
        		R.layout.simple_white_list, items));
        
        list.setOnItemClickListener(this);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub
		if(position == 0){						//Wi-Fi On/Off
			intent = new Intent(this, OnOffActivity.class);
		}
		if(position == 1){						//Wi-Fi Connect
			intent = new Intent(this, ConnectActivity.class);
		}
		if(position == 2){						//Static IP
			intent = new Intent(this, StaticIpActivity.class);
		}
		if(position == 3){						//Hidden AP Connect
			intent = new Intent(this, HiddenApConnectActivity.class);
		}
		if(position == 4){						//Suspend/Resume
			intent = new Intent(this, AgingTestActivity.class);		
		}	
		if(position == 5){						//Suspend/Resume
			intent = new Intent(this, OnOffActivity.class);		
		}
		if(position == 6){						//Suspend/Resume
			intent = new Intent(this, SuspendResumeActivity.class);		
		}
		if(position == 7){						//Suspend/Resume
			intent = new Intent(this, ApAutoConnectActivity.class);		
		}
		startActivity(intent);
	}
	
}
