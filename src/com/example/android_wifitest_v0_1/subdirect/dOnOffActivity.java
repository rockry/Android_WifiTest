package com.example.android_wifitest_v0_1.subdirect;

import com.example.android_wifitest_v0_1.R;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class dOnOffActivity extends Activity implements OnClickListener{
	private ToggleButton OnOffButton;
	private TextView succes;
	private TextView fail;
	private EditText loop;
	private WifiManager mWifiManager;
	ScrollView scView;
	private static TextView LogText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mWifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
		if(mWifiManager.getWifiState()==1){
        	mWifiManager.setWifiEnabled(true);
        }
		setContentView(R.layout.direct_onoff);
		OnOffButton = (ToggleButton)findViewById(R.id.dOnOffButton);
		succes = (TextView)findViewById(R.id.dOnOffsuccess);
		fail = (TextView)findViewById(R.id.dOnOfffail);
		loop = (EditText)findViewById(R.id.dOnOffLoop);
		LogText = (TextView)findViewById(R.id.log);
		scView = (ScrollView)findViewById(R.id.scroll);
		OnOffButton.setOnClickListener(this);
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(OnOffButton.isChecked()==true){
			
		}
		else if(OnOffButton.isChecked()==false){
			
		}
	}

}
