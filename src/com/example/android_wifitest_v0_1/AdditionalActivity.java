package com.example.android_wifitest_v0_1;

import com.example.android_wifitest_v0_1.subadditionaltest.aBroadcastActivity;
import com.example.android_wifitest_v0_1.subadditionaltest.aConnectionTime_Activity;
import com.example.android_wifitest_v0_1.substation.ApAutoConnectActivity;
import com.example.android_wifitest_v0_1.substation.OnOffActivity;
import com.example.android_wifitest_v0_1.substation.SuspendResumeActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AdditionalActivity extends Activity implements OnItemClickListener{

	private String[] items={//"Reset Test",					//0
			"Wi-Fi Connect Time Measure",					//1
//			"Suspend/Resume",								//2
//			"AP AutoConnect",								//3
			"BroadCast Random Packet"						//4
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
		if(position == 0){						//Reset Test
			intent = new Intent(this, aConnectionTime_Activity.class);
		}
		if(position == 1){						//Wi-Fi Connect Time Measure
			intent = new Intent(this, aBroadcastActivity.class);
		}
//		if(position == 2){						//Suspend/Resume
//			intent = new Intent(this, SuspendResumeActivity.class);
//		}
//		if(position == 3){						//ApAutoConnect
//			intent = new Intent(this, ApAutoConnectActivity.class);
//		}
//		if(position == 4){						//BroadCast Random Packet
//		intent = new Intent(this, aBroadcastActivity.class);
//	}

		startActivity(intent);
	}
	
}
