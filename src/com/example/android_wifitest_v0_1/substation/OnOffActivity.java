package com.example.android_wifitest_v0_1.substation;

import com.example.android_wifitest_v0_1.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class OnOffActivity extends Activity implements OnClickListener {
    private String Tag = "OnOffActivity";
    
    private ToggleButton OnOffButton;
	private static TextView success;
	private static TextView fail;
	private EditText loop;
	ScrollView scView;
	private static TextView LogText;

	private static ComponentName WifiServiceName;
	private static IntentFilter mFilter;
    private static BroadcastReceiver mReceiver;

	public static int success_num;
    public static int fail_num;
    public static int Loop_cnt;
    public static boolean isTestRunning = false;

    public static final String WIFI_ONOFF_SUCCESS = "com.lge.action.WIFI_ONOFF_SUCCESS";
    public static final String WIFI_ONOFF_FAIL = "com.lge.action.WIFI_ONOFF_FAIL";
    public static final String WIFI_ONOFF_FINISH = "com.lge.action.WIFI_SERVICE_FINISH";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.station_onoff);
		
		OnOffButton = (ToggleButton)findViewById(R.id.sOnOffButton);
		success = (TextView)findViewById(R.id.sOnOffSuccess);
		fail = (TextView)findViewById(R.id.sOnOffFail);
		loop = (EditText)findViewById(R.id.sOnOffLoop);
		LogText = (TextView)findViewById(R.id.log);
		scView = (ScrollView)findViewById(R.id.scroll);

		
		OnOffButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if(isChecked) {
		        	if(!isTestRunning) {
		        		updateResult(0);
		        		WifiServiceStart(); 
		        	}
		        } else {
		            WifiServiceStop();
		        }
		    }
		});

        mFilter = new IntentFilter();
        mFilter.addAction(WIFI_ONOFF_SUCCESS);
        mFilter.addAction(WIFI_ONOFF_FAIL);
        mFilter.addAction(WIFI_ONOFF_FINISH);
        
		mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if (WIFI_ONOFF_SUCCESS.equals(action)) {
                    updateResult(1);
                    updateLog(WIFI_ONOFF_SUCCESS);
                }
                if (WIFI_ONOFF_FAIL.equals(action)) {
                    updateResult(-1);
                    updateLog(WIFI_ONOFF_FAIL);
                }
                if (WIFI_ONOFF_FINISH.equals(action)) {
                    OnOffButton.setChecked(false);
                    Loop_cnt = 0;
                    updateLog(WIFI_ONOFF_FINISH);
                }
            }
        };
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
	public void onResume(){
	    super.onResume();
	    Log.d(Tag, "[OnOffActivity]OnResume");
	    registerReceiver(mReceiver, mFilter);
	}
	@Override
	public void onPause() {
	    super.onPause();
	    Log.d(Tag, "[OnOffActivity]OnPause");
	    unregisterReceiver(mReceiver);
	}
	@Override
	public void onClick(View v) {
		if(OnOffButton.isChecked()==true){
		}
		else if(OnOffButton.isChecked()==false){
		}
	}
    
    private void WifiServiceStart() {
        Log.d(Tag, "OnOffActivity Checked!!");
        getLoop_cnt();
        Intent intent = new Intent(OnOffActivity.this, Wifi_OnOff_Service.class);

//      String temp = null;
//      OnOff_Count.setText(temp);
        // 설명서 없애기
        
        WifiServiceName = startService(intent);
        Toast toast = Toast.makeText(getApplicationContext(),
                "WiFiOnOff_service Start!", Toast.LENGTH_SHORT);
        toast.show();
        updateLog("WiFiOnOff_service Start!");
        isTestRunning = true;
    }
    
    private void WifiServiceStop() {
//      networkID = 0;
        Loop_cnt = 0;

        // 설명서 다시 설정
//      String temp = "1. WiFi Button Click\n2. Test 할 Ap 선택\n3. Execution Time 에 실행할 횟수 입력\n"
//              + "4. Stars Button Click\n5. Stop 을 눌러 중지";
//      OnOff_Count.setText(temp);

        Log.d(Tag, "OnOffActivity unChecked!!");
//      Intent intent = new Intent(OnOffActivity.this, Wifi_Service.class);
//      stopService(intent);

        Intent i = new Intent();
        i.setComponent(WifiServiceName);
        stopService(i);
               
        Toast toast = Toast.makeText(getApplicationContext(),
                "WiFiOnOff_service destroy!", Toast.LENGTH_LONG);
        toast.show();   
        updateLog("WifiOnOff_service destroy!");
        isTestRunning = false;
    }

    //공용
    private void getLoop_cnt() {
        String temp = loop.getText().toString();
        Loop_cnt = Integer.parseInt(temp);
        Log.d(Tag, "Loop_cnt  =  " + Loop_cnt);
    }
    
    //공용
    private void updateResult(int passfail) {
        if(passfail == 1) {
            success_num++;
            success.setText(Integer.toString(success_num));
            Log.i(Tag, "success_num = " + success_num);
        } else if(passfail == -1) {
            fail_num++;
            fail.setText(Integer.toString(fail_num));
            Log.i(Tag, "fail_num = " + fail_num);
        } else if(passfail == 0) {
            success_num = 0;
            success.setText(Integer.toString(success_num));
            fail_num = 0;
            fail.setText(Integer.toString(fail_num));
        }
    }
    
}
