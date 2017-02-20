package com.example.android_wifitest_v0_1.subadditionaltest;

import com.example.android_wifitest_v0_1.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class aBroadcastActivity extends Activity {

	private String tag = "WiFiTEST_Broadcast";

	private Button Start;
	private Button Stop;
	private TextView defalut;
	private EditText IPaddr_edit;
	private EditText port_edit;
	private EditText period_edit;
	private EditText cnt_edit;
	private EditText limit_edit;

	public static String IPaddr;
	public static int port;
	public static int sleep_period;
	public static int loop_cnt_broadcast;
	public static int limit;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addition_broadcast);

		Start = (Button) findViewById(R.id.menu5_start);
		Stop = (Button) findViewById(R.id.menu5_stop);
		defalut = (TextView) findViewById(R.id.menu5_defalt);

		String temp = "Defalut IP & Port\n" + "IP Address : 192.168.1.255\n"
				+ "Port : 5657\n" + "Broadcast packet count at Once : 50\n"
				+ "Sleep period : 10000ms";

		IPaddr_edit = (EditText) findViewById(R.id.ipaddr);
		port_edit = (EditText) findViewById(R.id.port);
		period_edit = (EditText) findViewById(R.id.sleep_num);
		limit_edit = (EditText) findViewById(R.id.broad_num);
		cnt_edit = (EditText) findViewById(R.id.count_num);

		defalut.setText(temp);
		IPaddr = "192.168.1.255";
		IPaddr_edit.setText("192.168.1.255");

		port = 5657;
		port_edit.setText("5657");

		sleep_period = 10000;
		period_edit.setText("10000");

		limit = 50;
		limit_edit.setText("50");

		Start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				/*
				 * if( IPaddr_edit.getText().toString().equals("")){
				 * 
				 * Log.d(tag, "IPaddr set to defalut!" + IPaddr); }else{
				 */
				IPaddr = IPaddr_edit.getText().toString();
				Log.d(tag, "IP Address = " + IPaddr);
				// }

				/*
				 * if(port_edit.getText().toString().equals("")){
				 * 
				 * Log.d(tag, "port set to defalut!" + port); }else{
				 */
				port = Integer.parseInt(port_edit.getText().toString());
				Log.d(tag, "Port = " + port);
				// }

				/*
				 * if(period_edit.getText().toString().equals("")){
				 * 
				 * Log.d(tag, "sleep_period set to defalut!" + sleep_period);
				 * }else{
				 */
				sleep_period = Integer.parseInt(period_edit.getText()
						.toString());
				Log.d(tag, "sleep period = " + sleep_period);
				// }

				/*
				 * if(limit_edit.getText().toString().equals("")){
				 * 
				 * Log.d(tag, "limit set to defalut!" + limit); }else{
				 */
				limit = Integer.parseInt(limit_edit.getText().toString());
				Log.d(tag, "Maximum i = " + limit);
				// }

				loop_cnt_broadcast = Integer.parseInt(cnt_edit.getText()
						.toString());

				Log.d(tag, "Loop_cnt = " + loop_cnt_broadcast);
				Toast.makeText(getApplicationContext(),
						"broadcast service start!", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(aBroadcastActivity.this,
						aBroadcast_Service.class);
				startService(intent);
			}
		});

		Stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				IPaddr = "192.168.1.255";
				IPaddr_edit.setText("192.168.1.255");

				port = 5657;
				port_edit.setText("5657");

				sleep_period = 10000;
				period_edit.setText("10000");

				limit = 50;
				limit_edit.setText("50");

				Log.d(tag, "IP Address = " + IPaddr);
				Log.d(tag, "Port = " + port);
				Log.d(tag, "sleep period = " + sleep_period);
				Log.d(tag, "Maximum i = " + limit);
				Log.d(tag, "Loop_cnt = " + loop_cnt_broadcast);

				Intent intent = new Intent(aBroadcastActivity.this,
						aBroadcast_Service.class);
				stopService(intent);
			}
		});
	}
}
