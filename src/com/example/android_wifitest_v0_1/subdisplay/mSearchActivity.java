package com.example.android_wifitest_v0_1.subdisplay;

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

public class mSearchActivity extends Activity implements OnClickListener{
	
	private String Tag = "mSearchActivity";
	
	private ToggleButton OnOffButton;
	private static TextView success;
	private static TextView fail;
	private EditText loop;
	ScrollView scView;
	private static TextView LogText;
	
	public static final String WFD_SCAN_SUCCESS = "com.lge.action.WFD_SCAN_SUCCESS";
	public static final String WFD_SCAN_FAIL = "com.lge.action.WFD_SCAN_FAIL";
	public static final String WFD_SCAN_FINISH = "com.lge.action.WFD_SCAN_SERVICE_FINISH";
    
    private static ComponentName WfdTestServiceName;
	private static IntentFilter mFilter;
	private static BroadcastReceiver mReceiver;
	public static int success_num;
    public static int fail_num;
    public static int Loop_cnt;
    public static boolean isTestRunning = false;
    
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.miracast_onoff);
		
		OnOffButton = (ToggleButton)findViewById(R.id.mOnOffButton);
		success = (TextView)findViewById(R.id.mOnOffsuccess);
		fail = (TextView)findViewById(R.id.mOnOfffail);
		loop = (EditText)findViewById(R.id.mOnOffLoop);
		LogText = (TextView)findViewById(R.id.log);
		scView = (ScrollView)findViewById(R.id.scroll);
		
		OnOffButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if(isChecked) {
		        	if(!isTestRunning) {
		            updateResult(0);
		            WfdScanTestServiceStart();
		        	}
		        } else {
		            WfdScanTestServiceStop();
		        }
		    }
		});
		
		mFilter = new IntentFilter();
        mFilter.addAction(WFD_SCAN_SUCCESS);
        mFilter.addAction(WFD_SCAN_FAIL);
        mFilter.addAction(WFD_SCAN_FINISH);
        
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (WFD_SCAN_SUCCESS.equals(action)) {
                    updateResult(1);
                    updateLog(WFD_SCAN_SUCCESS);
                }
                if (WFD_SCAN_FAIL.equals(action)) {
                    updateResult(-1);
                    updateLog(WFD_SCAN_FAIL);
                }
                if (WFD_SCAN_FINISH.equals(action)) {
                    OnOffButton.setChecked(false);
                    Loop_cnt = 0;
                    updateLog(WFD_SCAN_FINISH);
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
	    Log.d(Tag, "[mSearchActivity]OnResume");
	    registerReceiver(mReceiver, mFilter);
	}
	@Override
	public void onPause() {
	    super.onPause();
	    Log.d(Tag, "[mSearchActivity]OnPause");
	    unregisterReceiver(mReceiver);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(OnOffButton.isChecked()==true){
			
		}
		else if(OnOffButton.isChecked()==false){
			
		}
	}
	
	//°ø¿ë
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
    
    private void getLoop_cnt() {
        String temp = loop.getText().toString();
        Loop_cnt = Integer.parseInt(temp);
        Log.d(Tag, "[hOnOffActivity]Loop_cnt  =  " + Loop_cnt);
    }
    
	private void WfdScanTestServiceStart() {
        getLoop_cnt();
        Intent intent = new Intent(mSearchActivity.this, WfdScanTestService.class);
        WfdTestServiceName = startService(intent);
        Toast toast = Toast.makeText(getApplicationContext(),
                "WfdScanTestService Start!", Toast.LENGTH_SHORT);
        toast.show();
        isTestRunning = true;
    }
    
    private void WfdScanTestServiceStop() {
        Loop_cnt = 0;
        Intent i = new Intent();
        i.setComponent(WfdTestServiceName);
        stopService(i);
        Toast toast = Toast.makeText(getApplicationContext(),
                "WfdScanTestService Stop", Toast.LENGTH_LONG);
        toast.show();   
        isTestRunning = false;
    }
}
