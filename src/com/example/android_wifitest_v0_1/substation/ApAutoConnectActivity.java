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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ApAutoConnectActivity extends Activity{

    private String Tag = "ApAutoConnectActivity";
    
    private ToggleButton AutoButton;
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

    public static final String WIFI_AUTOCONN_SUCCESS = "com.lge.action.WIFI_AUTOCONN_SUCCESS";
    public static final String WIFI_AUTOCONN_FAIL = "com.lge.action.WIFI_AUTOCONN_FAIL";
    public static final String WIFI_AUTOCONN_FINISH = "com.lge.action.WIFI_AUTOCONN_FINISH";

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.station_autoconnect);

		AutoButton = (ToggleButton)findViewById(R.id.sAutoButton);
		success = (TextView)findViewById(R.id.sAutoSuccess);
		fail = (TextView)findViewById(R.id.sAutoFail);
		loop = (EditText)findViewById(R.id.sAutoLoop);
		LogText = (TextView)findViewById(R.id.log);
		scView = (ScrollView)findViewById(R.id.scroll);

		AutoButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
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
		mFilter.addAction(WIFI_AUTOCONN_SUCCESS);
		mFilter.addAction(WIFI_AUTOCONN_FAIL);
		mFilter.addAction(WIFI_AUTOCONN_FINISH);

		mReceiver = new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {

		        String action = intent.getAction();
		        if (WIFI_AUTOCONN_SUCCESS.equals(action)) {
		            updateResult(1);
		            updateLog(WIFI_AUTOCONN_SUCCESS);
		        }
		        if (WIFI_AUTOCONN_FAIL.equals(action)) {
		            updateResult(-1);
		            updateLog(WIFI_AUTOCONN_FAIL);
		        }
		        if (WIFI_AUTOCONN_FINISH.equals(action)) {
		            AutoButton.setChecked(false);
		            Loop_cnt = 0;
		            updateLog(WIFI_AUTOCONN_FINISH);
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
        Log.d(Tag, "[ApAutoConnectActivity]OnResume");
        registerReceiver(mReceiver, mFilter);
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(Tag, "[ApAutoConnectActivity]OnPause");
        unregisterReceiver(mReceiver);
    }

	private void WifiServiceStart() {
        Log.d(Tag, "ApAutoConnectActivity Checked!!");
        getLoop_cnt();
        Intent intent = new Intent(ApAutoConnectActivity.this, Wifi_Auto_Service.class);

//      String temp = null;
//      OnOff_Count.setText(temp);
        // 설명서 없애기
        
        WifiServiceName = startService(intent);
        isTestRunning = true;
    }
    
    private void WifiServiceStop() {
//      networkID = 0;
        Loop_cnt = 0;

        // 설명서 다시 설정
//      String temp = "1. WiFi Button Click\n2. Test 할 Ap 선택\n3. Execution Time 에 실행할 횟수 입력\n"
//              + "4. Stars Button Click\n5. Stop 을 눌러 중지";
//      OnOff_Count.setText(temp);

        Log.d(Tag, "ApAutoConnectActivity unChecked!!");
//      Intent intent = new Intent(ApAutoConnectActivity.this, Wifi_Service.class);
//      stopService(intent);

        Intent i = new Intent();
        i.setComponent(WifiServiceName);
        stopService(i);
        
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
