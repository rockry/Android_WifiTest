package com.example.android_wifitest_v0_1;

import com.example.android_wifitest_v0_1.subdirect.dConnectActivity;
import com.example.android_wifitest_v0_1.subdirect.dGroupActivity;
import com.example.android_wifitest_v0_1.subdirect.dOnOffActivity;
import com.example.android_wifitest_v0_1.subdirect.dScanActivity;
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

public class DirectActivity extends Activity implements OnItemClickListener{
	private String[] items={//"Direct On/Off",
							"Scan",
							"Gruop"
							//,"Connect"
							};
	private Intent intent;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_direct);
        
        ListView list = (ListView)findViewById(R.id.listView);
        
        list.setAdapter(new ArrayAdapter<String>(this,
        		R.layout.simple_white_list, items));
        
        list.setOnItemClickListener(this);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub
//		if(position == 0){						//Direct On/Off
//			intent = new Intent(this, dOnOffActivity.class);
//		}
		if(position == 0){						//Scan
			intent = new Intent(this, dScanActivity.class);
		}
		if(position == 1){						//Gruop
			intent = new Intent(this, dGroupActivity.class);
		}
//		if(position == 3){						//Connect
//			intent = new Intent(this, dConnectActivity.class);
//		}
		startActivity(intent);
	}
	
}