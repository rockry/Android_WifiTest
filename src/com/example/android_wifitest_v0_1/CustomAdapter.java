package com.example.android_wifitest_v0_1;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class CustomAdapter extends BaseAdapter{
	
	private LayoutInflater inflater;
	private ArrayList<InfoClass> infoList;
	private ViewHolder viewHolder;
	
	public CustomAdapter(Context c , ArrayList<InfoClass> array){
		inflater = LayoutInflater.from(c);
		infoList = array;
	}

	@Override
	public int getCount() {
		return infoList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertview, ViewGroup parent) {
		
		View v = convertview;
		
		if(v == null){
			viewHolder = new ViewHolder();
			v = inflater.inflate(R.layout.dialog_list_sub, null);
			viewHolder.ap_ssid = (TextView)v.findViewById(R.id.ssid_textView);
			viewHolder.ap_passwd = (TextView)v.findViewById(R.id.passwd_textView);
			v.setTag(viewHolder);
			
		}else {
			viewHolder = (ViewHolder)v.getTag();
		}
		
		viewHolder.ap_ssid.setText(infoList.get(position).ap_ssid);
		viewHolder.ap_passwd.setText(infoList.get(position).ap_passwd);
		
		
		return v;
	}
	
	public void setArrayList(ArrayList<InfoClass> arrays){
		this.infoList = arrays;
	}
	
	public ArrayList<InfoClass> getArrayList(){
		return infoList;
	}
	
	
	/*
	 * ViewHolder
	 */
	class ViewHolder{
		TextView ap_ssid;
		TextView ap_passwd;
	}
	

}







