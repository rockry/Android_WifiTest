package com.example.android_wifitest_v0_1.subdisplay;

import com.example.android_wifitest_v0_1.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

public class mConnectActivity extends Activity implements OnClickListener{
	private ToggleButton OnOffButton;
	private TextView succes;
	private TextView fail;
	private EditText loop;
	ScrollView scView;
	private static TextView LogText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.miracast_connect);
		OnOffButton = (ToggleButton)findViewById(R.id.mConnectButton);
		succes = (TextView)findViewById(R.id.mConnectsuccess);
		fail = (TextView)findViewById(R.id.mConnectfail);
		loop = (EditText)findViewById(R.id.mConnectLoop);
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
