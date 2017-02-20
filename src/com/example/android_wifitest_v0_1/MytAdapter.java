package com.example.android_wifitest_v0_1;
import java.util.ArrayList;
import java.util.HashMap;



import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MytAdapter extends BaseAdapter {
	
	String Level;
	Context context;
	HashMap<Integer, ArrayList> list;
	
	int layout=0;
	View view;
	private final int TESTCASE = 0;
	private final int RESULT = 1;


	 @Override
	 public int getCount() {
	  return list.size();
	 }

	 @Override
	 public String getItem(int position) {
	  return ""+list.get(position);
	 }

	 @Override
	 public long getItemId(int position) {
	  return position;
	 }



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		
		if(convertView==null){
		LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(layout, parent, false);
		}
		if(list!=null){
		Log.e("js Adapter","is");
		TextView testcase = (TextView)convertView.findViewById(R.id.testcase_View);
		TextView testresult = (TextView)convertView.findViewById(R.id.testresult_textView);
		
		testcase.setText(""+list.get(position).get(TESTCASE));
		testresult.setText(""+list.get(position).get(RESULT));
		}
		
		
		
		return convertView;
	}
	

	public MytAdapter(Context context, int layout, HashMap<Integer, ArrayList> items) {
		super();
		Log.e("js myAdapter","is");
		this.context = context;
		this.list = items;
		this.layout = layout;
		
		// TODO Auto-generated constructor stub
	}

}

