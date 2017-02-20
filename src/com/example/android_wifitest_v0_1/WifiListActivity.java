package com.example.android_wifitest_v0_1;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;





public class WifiListActivity extends Activity implements OnClickListener{
	
	EditText apssidEdit;
	EditText appasswdEdit;
	ListView mListView;
	Button addbutton;
	Button printbutton;
	
	private DbOpenHelper mDbOpenHelper;
	private Cursor mCursor;
	private InfoClass mInfoClass;
	private ArrayList<InfoClass> mInfoArray;
	private CustomAdapter mAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.dialog_list);
		
		setLayout();
		addbutton = (Button)findViewById(R.id.add_button);
		printbutton = (Button)findViewById(R.id.print_button);
		
		addbutton.setOnClickListener(this);
		printbutton.setOnClickListener(this);
		
		mListView = (ListView)findViewById(R.id.dblistview);
		
		mDbOpenHelper = new DbOpenHelper(this);		
		mInfoArray = new ArrayList<InfoClass>();        
        mAdapter = new CustomAdapter(this, mInfoArray);        
        mListView.setAdapter(mAdapter);        
        mListView.setOnItemLongClickListener(longClickListener);
        
	}
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
				
        mDbOpenHelper.open();                
		if(view.getId() == R.id.add_button){
			if(mEditTexts[Constants.SSID].getText().toString().equals("")){
				Toast.makeText(getApplicationContext(), "input ssid", 
						Toast.LENGTH_SHORT).show();
			} else {
				mDbOpenHelper.insertColumn
				(
					mEditTexts[Constants.SSID].getText().toString().trim(),
					mEditTexts[Constants.PASSWD].getText().toString().trim()
				
				);
		
				mInfoArray.clear();
				doWhileCursorToArray();
				mAdapter.setArrayList(mInfoArray);			
				mAdapter.notifyDataSetChanged();			
				mCursor.close();
			}
			
		} else if(view.getId() == R.id.print_button) {
			mInfoArray.clear();
			mAdapter.setArrayList(mInfoArray);
			doWhileCursorToArray();
			mAdapter.notifyDataSetChanged();			
		}

	}
	private OnItemLongClickListener longClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				int position, long arg3) {
			
			boolean result = mDbOpenHelper.deleteColumn(mInfoArray.get(position).ap_ssid.toString());
//			boolean result = mDbOpenHelper.deleteColumn(position + 1);
			
			
			if(result){
				mInfoArray.remove(position);
				mAdapter.setArrayList(mInfoArray);
				mAdapter.notifyDataSetChanged();
			}else {
				Toast.makeText(getApplicationContext(), "Delete Fail", 
						Toast.LENGTH_SHORT).show();
			}
		
			
			return false;
		}
	};
	@Override
    protected void onDestroy() {
    	mDbOpenHelper.close();
    	super.onDestroy();
    }
    
    
   
	private void doWhileCursorToArray(){
		Log.e("jinseok.oh","doWhileCursorToArray1");
		mCursor = null;
		Log.e("jinseok.oh","doWhileCursorToArray2");
		mCursor = mDbOpenHelper.getAllColumns();
		Log.e("jinseok.oh","doWhileCursorToArray3");
		
		while (mCursor.moveToNext()) {
			Log.e("jinseok.oh","doWhileCursorToArray4");
			mInfoClass = new InfoClass(
					mCursor.getInt(mCursor.getColumnIndex("_id")),
					mCursor.getString(mCursor.getColumnIndex("ap_ssid")),
					mCursor.getString(mCursor.getColumnIndex("ap_passwd"))
					);
			
			mInfoArray.add(mInfoClass);
		}
		
		mCursor.close();
	}
    

	
	
	 private EditText[] mEditTexts;
	 
	    
    private void setLayout(){
    	mEditTexts = new EditText[]{
    			(EditText)findViewById(R.id.input_ssid),
    			(EditText)findViewById(R.id.input_passwd)
    	};
    	
    	
    }
	
}
