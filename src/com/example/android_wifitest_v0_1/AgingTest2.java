package com.example.android_wifitest_v0_1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.example.android_wifitest_v0_1.subdirect.dGroupActivity;
import com.example.android_wifitest_v0_1.subdirect.dScanActivity;
import com.example.android_wifitest_v0_1.subdisplay.mConnectActivity;
import com.example.android_wifitest_v0_1.subdisplay.mOnOffActivity;
import com.example.android_wifitest_v0_1.subdisplay.mSearchActivity;
import com.example.android_wifitest_v0_1.subhotspot.hConnectionActivity;
import com.example.android_wifitest_v0_1.subhotspot.hOnOffActivity;
import com.example.android_wifitest_v0_1.substation.AgingTestActivity;
import com.example.android_wifitest_v0_1.substation.ApAutoConnectActivity;
import com.example.android_wifitest_v0_1.substation.ConnectActivity;
import com.example.android_wifitest_v0_1.substation.HiddenApConnectActivity;
import com.example.android_wifitest_v0_1.substation.OnOffActivity;
import com.example.android_wifitest_v0_1.substation.StaticIpActivity;
import com.example.android_wifitest_v0_1.substation.SuspendResumeActivity;

import android.R.integer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class AgingTest2 extends Activity implements OnItemClickListener{
	private String Tag = "Wifi_allActivity";
	private Intent intent;
	private final int STATION=0, HOTSPOT=1, DIRECT=2, DISPLAY=3;
	
	private MytAdapter sadapter;
	private MytAdapter hadapter;
	private MytAdapter dadapter;
	private MytAdapter madapter;
	
	private HashMap<Integer, ArrayList> sitems;
	private HashMap<Integer, ArrayList> hitems;
	private HashMap<Integer, ArrayList> ditems;
	private HashMap<Integer, ArrayList> mitems;
	
	private ArrayList<String>[] sattribute;
	private ArrayList<String>[] dattribute;
	private ArrayList<String>[] hattribute;
	private ArrayList<String>[] mattribute;
	
	private TextView Station_cb;
	private TextView HotSpot_cb;
	private TextView Direct_cb;
	private TextView Miracast_cb;
	
	private ListView slistView;
	private ListView hlistView;
	private ListView dlistView;
	private ListView mlistView;
	
	private Thread mThread;
	
//	private String[] sString={"Wi-Fi On/Off",					//0
//								"Wi-Fi Connect",				//1
//								"Static IP",					//2
//								"Hidden AP Connect",			//3
//								"Suspend/Resume",				//4
//								"AP AutoConnect"};				//5
	
	private String[] sString={"Wi-Fi On/Off",					//0
			"Wi-Fi Connect",				//1
			"Static IP",					//2
			"Hidden AP Connect",			//3
			"Browser Aging Test",			//4
			"Reset Test",					//5
			"Suspend/Resume(Sleep policy Never)",				//6
			"Suspend/Resume(Sleep policy Off)"				//7
			};
	
	private String[] hString={"Hotspot ON/OFF"		//0
			//"Wi-Fi Hotspot Connection"				//1
	};
	
	private String[] dString={	"Scan",							//0
								"Gruop",						//1
								};
	
	private String[] mString={	"Miracast ON/OFF",							//0
			"Miracast Scan",						//1
			};

	public static Context context;
	
	ScrollView scView;
	private static TextView LogText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aging_test);
        Station_cb = (TextView)findViewById(R.id.station_allbox);
        HotSpot_cb = (TextView)findViewById(R.id.hotspot_allbox);
        Direct_cb = (TextView)findViewById(R.id.direct_allbox);      
        Miracast_cb = (TextView)findViewById(R.id.miracast_allbox); 
        
        sitems = new HashMap<Integer, ArrayList>();
        hitems = new HashMap<Integer, ArrayList>();
		ditems = new HashMap<Integer, ArrayList>();
		mitems = new HashMap<Integer, ArrayList>();
		
        sadapter = new MytAdapter(AgingTest2.this,R.layout.list_main,sitems);
        hadapter = new MytAdapter(AgingTest2.this,R.layout.list_main,hitems);
        dadapter = new MytAdapter(AgingTest2.this,R.layout.list_main,ditems);
        madapter = new MytAdapter(AgingTest2.this,R.layout.list_main,mitems);
        
        slistView = (ListView)findViewById(R.id.slistView);
        hlistView = (ListView)findViewById(R.id.hlistView);
        dlistView = (ListView)findViewById(R.id.dlistView);
        mlistView = (ListView)findViewById(R.id.mlistView);
        
        //listView.setAdapter(adapter);
        context = getBaseContext();
        LogText = (TextView)findViewById(R.id.log);
		scView = (ScrollView)findViewById(R.id.scroll);

		sattribute = new ArrayList[sString.length];
        for(int i=0;i<sString.length;i++){
        	sattribute[i] = new ArrayList<String>();
        	sattribute[i].add(sString[i]);
        	sattribute[i].add(" ");
        	sitems.put(i, sattribute[i]);
        }
        slistView.setAdapter(sadapter);	  
        
        hattribute = new ArrayList[hString.length];
		for(int i=0;i<hString.length;i++){
			hattribute[i] = new ArrayList<String>();
			hattribute[i].add(hString[i]);
        	hattribute[i].add(" ");    	
        	hitems.put(i, hattribute[i]);
        }			
		hlistView.setAdapter(hadapter);	
		
		dattribute = new ArrayList[dString.length];
		for(int i=0;i<dString.length;i++){
			dattribute[i] = new ArrayList<String>();
			dattribute[i].add(dString[i]);
        	dattribute[i].add(" ");
        	ditems.put(i, dattribute[i]);
		}
		dlistView.setAdapter(dadapter);	
		
		mattribute = new ArrayList[mString.length];
		for(int i=0;i<mString.length;i++){
			mattribute[i] = new ArrayList<String>();
			mattribute[i].add(mString[i]);
        	mattribute[i].add(" ");
        	mitems.put(i, mattribute[i]);
		}
		mlistView.setAdapter(madapter);
		
		slistView.setOnItemClickListener(this);
		hlistView.setOnItemClickListener(this);
		dlistView.setOnItemClickListener(this);
		mlistView.setOnItemClickListener(this);
    }

    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
    	Log.d(Tag, "onDestroy");
    	
    	if(mThread != null && mThread.isAlive())
    		mThread.interrupt();
		super.onDestroy();
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
	public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
		// TODO Auto-generated method stub
		if(arg0.getId() == slistView.getId()) {
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
		} else if(arg0.getId() == hlistView.getId()) {
			if(position == 0){						//Wi-Fi Hotspot ON/OFF
				intent = new Intent(this, hOnOffActivity.class);
			}
//			if(position == 1){						//Wi-Fi Connect
//				intent = new Intent(this, hConnectionActivity.class);
//			}
		} else if(arg0.getId() == dlistView.getId()) {
			if(position == 0){						//Scan
				intent = new Intent(this, dScanActivity.class);
			}
			if(position == 1){						//Gruop
				intent = new Intent(this, dGroupActivity.class);
			}
		} else if(arg0.getId() == mlistView.getId()) {
			if(position == 0){						//On/Off
				intent = new Intent(this, mOnOffActivity.class);
			}
			if(position == 1){						//Connect
				intent = new Intent(this, mSearchActivity.class);
			}
		}
		startActivity(intent);
	}

}
