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

public class SuspendResumeActivity extends Activity{

    private String Tag = "SuspendResumeActivity";
    
    private ToggleButton SRButton;
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

    public static final String WIFI_SR_SUCCESS = "com.lge.action.WIFI_SR_SUCCESS";
    public static final String WIFI_SR_FAIL = "com.lge.action.WIFI_SR_FAIL";
    public static final String WIFI_SR_FINISH = "com.lge.action.WIFI_SR_FINISH";

    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.station_sr);

		SRButton = (ToggleButton)findViewById(R.id.sSRButton);
		success = (TextView)findViewById(R.id.sSRSuccess);
		fail = (TextView)findViewById(R.id.sSRFail);
		loop = (EditText)findViewById(R.id.sSRLoop);
		LogText = (TextView)findViewById(R.id.log);
		scView = (ScrollView)findViewById(R.id.scroll);

		SRButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
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
		mFilter.addAction(WIFI_SR_SUCCESS);
		mFilter.addAction(WIFI_SR_FAIL);
		mFilter.addAction(WIFI_SR_FINISH);

		mReceiver = new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {

		        String action = intent.getAction();
		        if (WIFI_SR_SUCCESS.equals(action)) {
		            updateResult(1);
		            updateLog(WIFI_SR_SUCCESS);
		        }
		        if (WIFI_SR_FAIL.equals(action)) {
		            updateResult(-1);
		            updateLog(WIFI_SR_FAIL);
		        }
		        if (WIFI_SR_FINISH.equals(action)) {
		            SRButton.setChecked(false);
		            Loop_cnt = 0;
		            updateLog(WIFI_SR_FINISH);
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
        Log.d(Tag, "[SuspendResumeActivity]OnResume");
        registerReceiver(mReceiver, mFilter);
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(Tag, "[SuspendResumeActivity]OnPause");
        unregisterReceiver(mReceiver);
    }

	private void WifiServiceStart() {
        Log.d(Tag, "SuspendResumeActivity Checked!!");
        getLoop_cnt();
        Intent intent = new Intent(SuspendResumeActivity.this, Wifi_SR_Service.class);

//      String temp = null;
//      OnOff_Count.setText(temp);
        // ���� ���ֱ�
        
        WifiServiceName = startService(intent);
        isTestRunning = true;
    }
    
    private void WifiServiceStop() {
//      networkID = 0;
        Loop_cnt = 0;

        // ���� �ٽ� ����
//      String temp = "1. WiFi Button Click\n2. Test �� Ap ����\n3. Execution Time �� ������ Ƚ�� �Է�\n"
//              + "4. Stars Button Click\n5. Stop �� ���� ����";
//      OnOff_Count.setText(temp);

        Log.d(Tag, "SuspendResumeActivity unChecked!!");
//      Intent intent = new Intent(SuspendResumeActivity.this, Wifi_Service.class);
//      stopService(intent);

        Intent i = new Intent();
        i.setComponent(WifiServiceName);
        stopService(i);

        isTestRunning = false;
    }

    //����
    private void getLoop_cnt() {
        String temp = loop.getText().toString();
        Loop_cnt = Integer.parseInt(temp);
        Log.d(Tag, "Loop_cnt  =  " + Loop_cnt);
    }
    
    //����
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
