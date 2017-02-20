package com.example.android_wifitest_v0_1.subadditionaltest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import com.example.android_wifitest_v0_1.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class aConnectionTime_Activity extends Activity{
	private final String TAG = "WiFiTEST";
	private final String sub_TAG = "aConnectionTime_Activity"; 
	
	private ToggleButton ConnectButton;
	private EditText ssid;
	private EditText passwd;
	private EditText loop;
	private TextView success;
	private TextView fail;
	private ScrollView scrView;
	
    private static ComponentName WifiServiceName;
    private static IntentFilter mDefaultFilter;
    private static DefaultBroadcastReceiver mDefaultReceiver;

	public static ListView listView;    
    public static ArrayAdapter<String> arrayAdapter;        
    public static ArrayList<String> arrayList = new ArrayList<String>();
    public static int success_num;
    public static int fail_num;
    public static int Loop_cnt;
    public static boolean isTestRunning = false;
    public static String connect_ssid = "Wi-Fi_Sanity_AP";
    public static String connect_password = "lge12345";

	private FileInputStream fis = null;
    
    public static final String WIFI_CONNECT_SUCCESS = "com.lge.action.WIFI_CONNECT_SUCCESS";
    public static final String WIFI_CONNECT_FAIL = "com.lge.action.WIFI_CONNECT_FAIL";
    public static final String WIFI_CONNECT_FINISH = "com.lge.action.WIFI_CONNECT_FINISH";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        
		setContentView(R.layout.addition_connect_time);
        
		ConnectButton = (ToggleButton)findViewById(R.id.sConnectButton);
        ssid = (EditText)findViewById(R.id.sConnectSSID);
        passwd = (EditText)findViewById(R.id.sConnectPASS);
        loop = (EditText)findViewById(R.id.sConnectLoop);
        success = (TextView)findViewById(R.id.sConnectSuccess);
        fail = (TextView)findViewById(R.id.sConnectFail);
        scrView = (ScrollView)findViewById(R.id.scrView);
        
        ConnectButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                	if(!isTestRunning) {
                		updateResult(0);
                		updateAPconf();
                		WifiServiceStart();
                	}
                } else {
                    WifiServiceStop();
                }
            }
        });

        mDefaultFilter = new IntentFilter();
        mDefaultFilter.addAction(WIFI_CONNECT_SUCCESS);
        mDefaultFilter.addAction(WIFI_CONNECT_FAIL);
        mDefaultFilter.addAction(WIFI_CONNECT_FINISH);

        mDefaultReceiver = new DefaultBroadcastReceiver();
        
        arrayAdapter = new ArrayAdapter<String>(aConnectionTime_Activity.this, R.layout.simple_white_list, arrayList);
        listView = (ListView)findViewById(R.id.timeList);
        listView.setAdapter(arrayAdapter);
        arrayAdapter.clear();
	}
	
    public void updateLog(String string) {
		//LogText.setText(LogText.getText() + "\n" + string);
		scrView.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				scrView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
	}
    
    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "[ConnectActivity]OnResume");
        connect_ssid = "Wi-Fi_Sanity_AP";
        connect_password = "lge12345";
        registerReceiver(mDefaultReceiver, mDefaultFilter);
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "[ConnectActivity]OnPause");
        unregisterReceiver(mDefaultReceiver);
    }
    
	public void onClick(View v) {
		if(ConnectButton.isChecked()==true){		
		}
		else if(ConnectButton.isChecked()==false){
		}
	}
	
    private void WifiServiceStart() {
        Log.d(TAG, "ConnectActivity Checked!!");
        
        getLoop_cnt();
        
 /*       arrayAdapter = new ArrayAdapter<String>(aConnectionTime_Activity.this, R.layout.simple_white_list, arrayList);
        listView = (ListView)findViewById(R.id.timeList);
        listView.setAdapter(arrayAdapter);
        arrayAdapter.clear();*/
        
        
        Intent intent = new Intent(aConnectionTime_Activity.this, aConnectionTime_Service.class);
        WifiServiceName = startService(intent);
        Toast toast = Toast.makeText(getApplicationContext(),
                "WiFi_Connect_service Start!", Toast.LENGTH_SHORT);
        toast.show();
        isTestRunning = true;
    }
    private void WifiServiceStop() {
//      networkID = 0;
        Loop_cnt = 0;

        Log.d(TAG, "ConnectActivity unChecked!!");

        Intent i = new Intent();
        i.setComponent(WifiServiceName);
        stopService(i);
               
        Toast toast = Toast.makeText(getApplicationContext(),
                "WiFi_Connect_service destroy!", Toast.LENGTH_LONG);
        toast.show();
        isTestRunning = false;
    }
    
    private void getLoop_cnt() {
    	if(loop.getText().toString() != null && !loop.getText().toString().equals("")) {
	        String temp = loop.getText().toString();
	        Loop_cnt = Integer.parseInt(temp);
	        Log.d(TAG, "Loop_cnt  =  " + Loop_cnt);
    	}
    }
    
    private void updateResult(int passfail) {
        if(passfail == 1) {
//            success_num++;
//            success.setText(Integer.toString(success_num));
//            Log.i(TAG, "success_num = " + success_num);
        } else if(passfail == -1) {
//            fail_num++;
//            fail.setText(Integer.toString(fail_num));
//            Log.i(TAG, "fail_num = " + fail_num);
        } else if(passfail == 0) {
//            success_num = 0;
//            success.setText(Integer.toString(success_num));
//            fail_num = 0;
//            fail.setText(Integer.toString(fail_num));
        }
    }

    private void updateAPconf() {
        if(ssid.getText().toString()!=null && !(ssid.getText().toString().equals(""))) {
            connect_ssid = ssid.getText().toString();
        }
        if(passwd.getText().toString()!=null && !passwd.getText().toString().equals("")) {
            connect_password = passwd.getText().toString();
        }
    }
    
    class DefaultBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (WIFI_CONNECT_SUCCESS.equals(action)) {
                updateResult(1);
                updateLog(WIFI_CONNECT_SUCCESS);
            }
            if (WIFI_CONNECT_FAIL.equals(action)) {
                updateResult(-1);
                updateLog(WIFI_CONNECT_FAIL);
            }
            if (WIFI_CONNECT_FINISH.equals(action)) {
                ConnectButton.setChecked(false);
                Loop_cnt = 0;
                updateLog(WIFI_CONNECT_FINISH);
            }
        }
    };
    
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 1, 0, "result");
		//menu.add(0, 2, 0, "average time result");
		menu.add(0, 2, 0, "Quit");
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1: // 
			String mStr = null;
			try {
				arrayAdapter.clear();
				int i = 0;
				String[] strArray = {,};
				mStr = FILE_READ_all("Connect_time.txt");
				// Toast.makeText(getApplicationContext(), mStr,
				// Toast.LENGTH_LONG).show();
				//Log.e(TAG, " source = " + mStr);

				strArray = mStr.split("\n");
				Log.d(TAG, " length = " + strArray.length);
				while (i < strArray.length) {
					arrayList.add(strArray[i].toString());
					Log.d(TAG, strArray[i]);
					i++;
				}
				Log.d(TAG, "done");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, e.getMessage(), e);
				e.printStackTrace();
			}
			arrayAdapter = new ArrayAdapter<String>(aConnectionTime_Activity.this,
					R.layout.simple_white_list, arrayList);
			listView.setAdapter(arrayAdapter);

			return true;

/*		case 2:
			arrayAdapter.clear();
			try {
				int i = 0;
				String[] strArray = {,};
				mStr = FILE_READ_all("Connect_time_average.txt");
				// Toast.makeText(getApplicationContext(), mStr,
				// Toast.LENGTH_LONG).show();
				Log.e(TAG, " source = " + mStr);

				strArray = temp.split("\n");
				Log.d(TAG, " length = " + strArray.length);
				while (i < strArray.length) {
					arrayList.add(strArray[i].toString());
					Log.d(TAG, strArray[i]);
					i++;
				}
				Log.d(TAG, "done");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, e.getMessage(), e);
				e.printStackTrace();
			}
			arrayAdapter = new ArrayAdapter<String>(aConnectionTime_Activity.this,
					R.layout.simple_white_list, arrayList);
			listView.setAdapter(arrayAdapter);

			return true;
*/
/*		case 3:
			arrayAdapter.clear();
			try {
				int i = 0;
				String[] strArray = {,};
				mStr = FILE_READ_all("dhcp_Connect_time.txt");
				// Toast.makeText(getApplicationContext(), mStr,
				// Toast.LENGTH_LONG).show();
				Log.e(TAG, " source = " + mStr);

				strArray = temp.split("\n");
				Log.d(TAG, " length = " + strArray.length);
				while (i < strArray.length) {
					arrayList.add(strArray[i].toString());
					Log.d(TAG, strArray[i]);
					i++;
				}
				Log.d(TAG, "done");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, e.getMessage(), e);
				e.printStackTrace();
			}
			arrayAdapter = new ArrayAdapter<String>(aConnectionTime_Activity.this,
					R.layout.simple_white_list, arrayList);
			listView = (ListView) findViewById(R.id.listView);
			listView.setAdapter(arrayAdapter);
			return true;

		case 4:
			arrayAdapter.clear();
			try {
				int i = 0;
				String[] strArray = {,};
				mStr = FILE_READ_all("dhcp_Connect_time_average.txt");
				// Toast.makeText(getApplicationContext(), mStr,
				// Toast.LENGTH_LONG).show();
				Log.e(TAG, " source = " + mStr);

				strArray = temp.split("\n");
				Log.d(TAG, " length = " + strArray.length);
				while (i < strArray.length) {
					arrayList.add(strArray[i].toString());
					Log.d(TAG, strArray[i]);
					i++;
				}
				Log.d(TAG, "done");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, e.getMessage(), e);
				e.printStackTrace();
			}
			arrayAdapter = new ArrayAdapter<String>(aConnectionTime_Activity.this,
					R.layout.simple_white_list, arrayList);
			listView = (ListView) findViewById(R.id.listView);
			listView.setAdapter(arrayAdapter);
			return true;
*/
		case 2: // 
			arrayAdapter.clear();
			recreate();
			// timeAdt.clear();
			return true;
		}
		return false;
	}
	
	private String FILE_READ_all(String Filename) throws IOException {
		String result = null;
		int readcount = 0;
		try {
			fis = this.openFileInput(Filename);
			readcount = (int) fis.available();
			byte[] temp = new byte[readcount];
			while (fis.read(temp) != -1) {
			}
//			Log.d(TAG, "READ_TIME_stoptime File Open&Read Success");
//			Log.d(TAG, "byte is" + temp);
			result = new String(temp);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.getMessage(), e);
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.getMessage(), e);
			e.printStackTrace();
		}

		fis.close();
		return result;
	}
	
	
	
}
