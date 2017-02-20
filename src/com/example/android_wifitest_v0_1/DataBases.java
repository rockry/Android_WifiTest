package com.example.android_wifitest_v0_1;

import android.provider.BaseColumns;

// DataBase Table
public final class DataBases {
	
	public static final class CreateDB implements BaseColumns{
		public static final String SSID = "ap_ssid";
		public static final String PASSWD = "ap_passwd";
		public static final String _TABLENAME = "aplist";
		
		public static final String _CREATE = 
			"create table "+_TABLENAME+"(" 
					+_ID+" integer primary key autoincrement, " 	
					+SSID+" text not null , " 
					+PASSWD+" text not null );";

	
	}
}
