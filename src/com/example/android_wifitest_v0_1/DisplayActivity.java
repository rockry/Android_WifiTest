package com.example.android_wifitest_v0_1;

import com.example.android_wifitest_v0_1.subdisplay.mConnectActivity;
import com.example.android_wifitest_v0_1.subdisplay.mOnOffActivity;
import com.example.android_wifitest_v0_1.subdisplay.mSearchActivity;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class DisplayActivity extends Activity implements OnItemClickListener{
	private String[] items={"Display On/Off",
							"Search",
							"Connect"};
	private Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display);
        
        ListView list = (ListView)findViewById(R.id.listView);
        
        list.setAdapter(new ArrayAdapter<String>(this,
        		android.R.layout.simple_list_item_1, items));
        
        list.setOnItemClickListener(this);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub
		if(position == 0){						//Display On/Off
			intent = new Intent(this, mOnOffActivity.class);
		}
		if(position == 1){						//Search
			intent = new Intent(this, mSearchActivity.class);
		}
		if(position == 2){						//Connect
			intent = new Intent(this, mConnectActivity.class);
		}
		startActivity(intent);
	}
	
}