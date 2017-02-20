/* This is old file.
 * Changed to using AgingTest2.java
 * Please refer AgingTest2.java */
package com.example.android_wifitest_v0_1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AgingTest extends Activity implements OnItemClickListener{
	private Intent intent;
	private String[] items={"Station", "Hotspot","Direct"};
	private final int STATION =0,HOTSPOT=1, DIRECT=2;	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aging);
        
        ListView list = (ListView)findViewById(R.id.listView);
        
        list.setAdapter(new ArrayAdapter<String>(this, R.layout.simple_white_list, items));
        
        list.setOnItemClickListener(this);
        
        
    }
    
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// TODO Auto-generated method stub	
		if(position == STATION){
			intent = new Intent(AgingTest.this, StationActivity.class);
			startActivity(intent);		
		}		
		if(position == HOTSPOT){
			intent = new Intent(AgingTest.this, HotspotActivity.class);
			startActivity(intent);
		}		
		if(position == DIRECT){
			intent = new Intent(AgingTest.this, DirectActivity.class);
			startActivity(intent);
		}		
	}
}