package com.example.android_wifitest_v0_1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

public class allActivity extends Activity implements OnItemClickListener, OnClickListener, OnCheckedChangeListener{
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
	
	private CheckBox Station_cb;
	private CheckBox HotSpot_cb;
	private CheckBox Direct_cb;
	private CheckBox Miracast_cb;
	private Button start_bn;
	
	private ListView slistView;
	private ListView hlistView;
	private ListView dlistView;
	private ListView mlistView;
	
	private Thread mThread;
	private ProgressBar alltest_progress;
	private TextView alltest_progress_text;
	
//	private String[] sString={"Wi-Fi On/Off",					//0
//								"Wi-Fi Connect",				//1
//								"Static IP",					//2
//								"Hidden AP Connect",			//3
//								"Suspend/Resume",				//4
//								"AP AutoConnect"};				//5
	
	private String[] sString={"Wi-Fi On/Off",					//0
			"Wi-Fi Connect",				//1
			"Static IP",					//2
			"Hidden AP Connect"			//3
//			"Browser Aging Test"			//4
			};
	
	private String[] hString={"Hotspot ON/OFF"};	//1
	
	private String[] dString={	"Scan",							//0
								"Gruop",						//1
								};

	private String[] mString={	"Miracast On/Off",							//0
			"Miracast Scan",												//1
			};
	
	private WifiP2pManager mWifiP2pManager;
	private WifiManager mWifiManager;
	
	private AllTest at;
	public static Context context;
	
	ScrollView scView;
	private static TextView LogText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_page);
        alltest_progress = (ProgressBar)findViewById(R.id.alltest_progress);
        alltest_progress_text = (TextView) findViewById(R.id.alltest_progress_text);
        start_bn = (Button)findViewById(R.id.all_startbn);
        Station_cb = (CheckBox)findViewById(R.id.station_allbox);
        HotSpot_cb = (CheckBox)findViewById(R.id.hotspot_allbox);
        Direct_cb = (CheckBox)findViewById(R.id.direct_allbox);
        Miracast_cb = (CheckBox)findViewById(R.id.miracast_allbox);
        
        mWifiP2pManager = (WifiP2pManager) this.getSystemService(Context.WIFI_P2P_SERVICE);
        mWifiManager = (WifiManager)getSystemService(WIFI_SERVICE);

        start_bn.setOnClickListener(this);
        sitems = new HashMap<Integer, ArrayList>();
        hitems = new HashMap<Integer, ArrayList>();
		ditems = new HashMap<Integer, ArrayList>();
		mitems = new HashMap<Integer, ArrayList>();
		
		at =  new AllTest(this, mWifiP2pManager,mWifiManager);
        sadapter = new MytAdapter(allActivity.this,R.layout.list_main,sitems);
        hadapter = new MytAdapter(allActivity.this,R.layout.list_main,hitems);
        dadapter = new MytAdapter(allActivity.this,R.layout.list_main,ditems);
        madapter = new MytAdapter(allActivity.this,R.layout.list_main,mitems);
//        Log.e("jinseok.oh","          	"+sString[0]);//.toString());
        
		
        
        slistView = (ListView)findViewById(R.id.slistView);
        hlistView = (ListView)findViewById(R.id.hlistView);
        dlistView = (ListView)findViewById(R.id.dlistView);
        mlistView = (ListView)findViewById(R.id.mlistView);
        Station_cb.setOnCheckedChangeListener(this);
        HotSpot_cb.setOnCheckedChangeListener(this);
        Direct_cb.setOnCheckedChangeListener(this);
        Miracast_cb.setOnCheckedChangeListener(this);
        
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
		initResult();
		alltest_progress.setVisibility(View.VISIBLE);
		alltest_progress_text.setVisibility(View.VISIBLE);
		
		mThread = null;
		mThread = new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				if(Station_cb.isChecked() == true){
					for(int i=0;i<sString.length;i++){
						if(Thread.interrupted()) {
							break;
						}
						sattribute = at.Stationall(sattribute, i);
						sitems.put(i, sattribute[i]);
						mHandler.sendEmptyMessage(0);
					}
				}
				if(HotSpot_cb.isChecked() == true && !Thread.interrupted()){
					hattribute = at.HotSpotall(hattribute);
					for(int i=0;i<hString.length;i++){
						hitems.put(i, hattribute[i]);
					}
					mHandler.sendEmptyMessage(1);
				}
				if(Direct_cb.isChecked() == true && !Thread.interrupted()){
					dattribute = at.Directall(dattribute);
					for(int i=0;i<dString.length;i++){
						ditems.put(i, dattribute[i]);
					}
					mHandler.sendEmptyMessage(2);	
				}
				if(Miracast_cb.isChecked() == true && !Thread.interrupted()){
					mattribute = at.Miracastall(mattribute);
					for(int i=0;i<mString.length;i++){
						mitems.put(i, mattribute[i]);
					}
					mHandler.sendEmptyMessage(3);	
				}
				
				
				mHandler.sendEmptyMessage(4);
				Looper.loop();
				
			}
		});
		
		mThread.start();
	}



	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		// TODO Auto-generated method stub
		Log.e("arg1",""+arg1);
		if((Station_cb == arg0)&&(arg1 == true)){
			
	        Station_cb.setTextColor(getResources().getColor(R.color.white));
		} else if((Station_cb == arg0)&&(arg1 == false)){
//	        for(int i=0;i<sString.length;i++){
//	        	sattribute[i].clear();
//	        	sitems.put(i, sattribute[i]);
//	        }	
//	        sitems.clear();
//	        slistView.setAdapter(sadapter);	
			Station_cb.setTextColor(getResources().getColor(R.color.grey));
		}
		if((HotSpot_cb == arg0)&&(arg1 == true)){

			HotSpot_cb.setTextColor(getResources().getColor(R.color.white));
		} else if((HotSpot_cb == arg0)&&(arg1 == false)){
//	        for(int i=0;i<hString.length;i++){
//	        	hattribute[i].clear();
//	        	hitems.put(i, hattribute[i]);
//	        }	
//	        hitems.clear();
//	        hlistView.setAdapter(hadapter);	
	        HotSpot_cb.setTextColor(getResources().getColor(R.color.grey));
		}
		if((Direct_cb == arg0)&&(arg1 == true)){

			Direct_cb.setTextColor(getResources().getColor(R.color.white));
		} else if((Direct_cb == arg0)&&(arg1 == false)){
//	        for(int i=0;i<dString.length;i++){
//	        	dattribute[i].clear();
//	        	ditems.put(i, dattribute[i]);
//	        }	
//	        ditems.clear();
//	        dlistView.setAdapter(dadapter);	
	        Direct_cb.setTextColor(getResources().getColor(R.color.grey));
		}
		
		if((Miracast_cb == arg0)&&(arg1 == true)){

			Miracast_cb.setTextColor(getResources().getColor(R.color.white));
		} else if((Miracast_cb == arg0)&&(arg1 == false)){
			Miracast_cb.setTextColor(getResources().getColor(R.color.grey));
		}
	}
	public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d(Tag, "Got message in handler");
            switch(msg.what) {
            case 0 : 
                slistView.setAdapter(sadapter);
                break;
            case 1 :
            	hlistView.setAdapter(hadapter);
            	break;
            case 2 :
            	dlistView.setAdapter(dadapter);
            	break;
            case 3 :
            	mlistView.setAdapter(madapter);
            	break;
            case 4 :
            	boolean b = mThread != null;
            	Log.d(Tag, "case3 : mThread = " + b + "mThread.isAlive = " + mThread.isAlive());
            	if(mThread != null && mThread.isAlive()) {
            		Log.d(Tag, "mThread Destroy");
            		mThread.interrupt();
            	}
            	alltest_progress.setVisibility(View.GONE);
            	alltest_progress_text.setVisibility(View.GONE);
            	break;
            }
        }
	};
  
	private void initResult() {
		if(sattribute != null) {
			for(int i=0;i<sString.length;i++){
				sattribute[i].add(1, "");
				sitems.put(i, sattribute[i]);
			}
			mHandler.sendEmptyMessage(0);
		}
		if(hattribute != null) {
			for(int i=0;i<hString.length;i++){
				hattribute[i].add(1, "");
				hitems.put(i, hattribute[i]);
			}
			mHandler.sendEmptyMessage(1);
		}
		if(dattribute != null) {
			for(int i=0;i<dString.length;i++){
				dattribute[i].add(1, "");
				ditems.put(i, dattribute[i]);
			}
			mHandler.sendEmptyMessage(2);
		}
		if(mattribute != null) {
			for(int i=0;i<mString.length;i++){
				mattribute[i].add(1, "");
				mitems.put(i, mattribute[i]);
			}
			mHandler.sendEmptyMessage(3);
		}
	}
	
    
}
