package com.example.android_wifitest_v0_1;

import java.net.InetAddress;
import java.util.ArrayList;
import com.example.android_wifitest_v0_1.substation.*;

import com.example.android_wifitest_v0_1.subdirect.dGroupActivity;
import com.example.android_wifitest_v0_1.subdirect.dScanActivity;
import com.example.android_wifitest_v0_1.subhotspot.hConnectionActivity;
import com.example.android_wifitest_v0_1.subhotspot.hOnOffActivity;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.IpAssignment;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.os.PowerManager;

//WfdManager
import com.lge.systemservice.core.LGContextImpl;
import com.lge.systemservice.core.LGContext;
import com.lge.systemservice.core.wfdmanager.WfdManager;

public class AllTest{
	private String Tag = "AllTest";
	private int PF=1;
	private dGroupActivity mGroupActivity;
	private dScanActivity mScanActivity;
	
	private hOnOffActivity mhOnOffActivity;
	private hConnectionActivity mhConnectionActivity;
	
	private Context context;
	private String StationResult;
	
	private WifiP2pManager.Channel mChannel;
	private allActivity mActivity = null;
	private WifiP2pManager mWifiP2pManager;
	private IntentFilter mIntentFilter;
	private WifiP2pManager.GroupInfoListener mGroupInfoListener;
	private WifiManager mWifiManager;
	
	private WfdManager mWfdManager;
	
	private LinkProperties mLinkProperties = new LinkProperties();
	boolean isConnected = false;
	boolean isScreenon = true;
	int Loop_cnt = 1;
	int netId = -1;
	BroadcastReceiver mReceiver;
	private PowerManager powerManager;
	private PowerManager.WakeLock mWakeLock;
	
	public ArrayList<String>[] Stationall(ArrayList<String>[] sattribute, int step){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        if(step == 0) {
        	Log.d(Tag, "StationOnOffTest Start.");
        	if( StationOnOffTest() ) {
        		sattribute[0].add(PF, "PASS");
        	} else {
        		sattribute[0].add(PF, "FAIL");
        	}
        } else if(step == 1) {
        	Log.d(Tag, "StationConnectTest Start.");
        	if( StationConnectTest() ) {
        		sattribute[1].add(PF, "PASS");
        	} else {
        		sattribute[1].add(PF, "FAIL");
        	}
        } else if(step == 2) {
        	Log.d(Tag, "StationStaticTest Start.");
        	if( StationStaticTest() ) {
        		sattribute[2].add(PF, "PASS");
        	} else {
        		sattribute[2].add(PF, "FAIL");
        	}
        } else if(step == 3) {
        	Log.d(Tag, "StationHiddenTest Start.");
        	if( StationHiddenTest() ) {
        		sattribute[3].add(PF, "PASS");
        	} else {
        		sattribute[3].add(PF, "FAIL");
        	}
        }
/*        Log.d(Tag, "StationSuspendResumeTest Start.");
        if( StationSuspendResumeTest() ) {
        	sattribute[4].add(PF, "PASS");
        } else {
        	sattribute[4].add(PF, "FAIL");
        }
        Log.d(Tag, "StationAutoConnectTest Start.");
        if( StationAutoConnectTest() ) {
        	sattribute[5].add(PF, "PASS");
        } else {
        	sattribute[5].add(PF, "FAIL");
        }
*/		return sattribute;
	}

	public ArrayList<String>[] HotSpotall(ArrayList<String>[] hattribute){
		if ( HotspotOnOffTest()) {
			hattribute[0].add(PF, "PASS");
		} else {
			hattribute[0].add(PF, "FAIL");
		}
		return hattribute;		
	}
	
	public ArrayList<String>[] Directall(ArrayList<String>[] dattribute){
		mGroupActivity = new dGroupActivity();
		mScanActivity = new dScanActivity();
		
		//wifi 켜기
		mScanActivity.WifiON(mWifiManager);
		//스캔
		
		if(mScanActivity.DirectScan(0, mActivity,mWifiP2pManager)==0){
			Log.d(Tag, "DirectScan Pass");
			dattribute[0].add(PF, "PASS");
		} else {
			Log.d(Tag, "DirectScan Fail");
			dattribute[0].add(PF, "FAIL");
		}
		
		
		//그룹만들기
		mGroupActivity.setting(mActivity, mWifiP2pManager);
		if( mGroupActivity.mCreateGroup(0,mWifiP2pManager)==0 ){//&&(mGroupActivity.mRemoveGroup(0)==0)
			dattribute[1].add(PF, "PASS");
		} else {
			dattribute[1].add(PF, "FAIL");
		}
		mGroupActivity.mRemoveGroup(0, mWifiP2pManager);
		
		
		return dattribute;		
	}
	
	public ArrayList<String>[] Miracastall(ArrayList<String>[] mattribute){
		
		if ( MiracastOnOffTest()) {
			mattribute[0].add(PF, "PASS");
		} else {
			mattribute[0].add(PF, "FAIL");
		}
		
		return mattribute;		
	}

	AllTest(Activity mActivity, WifiP2pManager mWifiP2pManager, WifiManager mWifiManager) {
		this.mActivity = (allActivity) mActivity;
		this.mWifiP2pManager = mWifiP2pManager;
		this.mWifiManager = mWifiManager;
	}
	private boolean StationOnOffTest() {
		boolean result = false;
		boolean Onresult = false;
		boolean Offresult = false;
		int pass = 0;
		int fail = 0;
		int i = 0;

        if(mWifiManager == null) {
        	mWifiManager = (WifiManager) allActivity.context.getSystemService(Context.WIFI_SERVICE);
        }
		Log.d(Tag, "AllTest Station OnOff!!");

		while (i < Loop_cnt) {
			Log.d(Tag, i + "-th Loop.");

			if (i <= Loop_cnt) {
				/*				// Thread 내에서 Toast를 하기 위해 post를 사용하였음
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						Toast T = Toast.makeText(
								getApplicationContext(),
								i + "-th Loop", Toast.LENGTH_SHORT);
						T.show();
					}
				});
			}
				 */
				Log.d(Tag, "Not interrupted");

				//추후에 BroadcastReceiver로 받아서 처리하는 것으로 구현 예정
				for(int n = 0; n < 2; n++) {
					result = WiFiOnOffStart();
					
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if(mWifiManager.isWifiEnabled()) {
						Log.d(Tag, "WiFi On Success = " + result);
						Onresult = result;
					} else {
						Log.d(Tag, "WiFi Off Success = " + result);
						Offresult = result;
					}
				}

				if (Onresult && Offresult) {
					pass++;
				} else {
					fail++;
				}
				i++;
			}
		}
        if(fail == 0 && pass > 0) {
        	return true;
		} else {
			return false;
		}
	}
	private synchronized boolean WiFiOnOffStart() {
		if (mWifiManager.isWifiEnabled()) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
			return mWifiManager.setWifiEnabled(false);
		} else {
			return mWifiManager.setWifiEnabled(true);
		}
	}
	
	private synchronized boolean WiFiApOnOffStart() {
		if (mWifiManager.isWifiApEnabled()) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
			return mWifiManager.setWifiApEnabled(null, true);
		} else {
			return mWifiManager.setWifiApEnabled(null, false);
		}
	}
	private boolean StationConnectTest() {
		boolean result = false;
	    boolean ConnectResult = false;
	    boolean DisconnectResult = false;
		int pass = 0;
		int fail = 0;
        int i = 0;

        if(mWifiManager == null) {
        	mWifiManager = (WifiManager) allActivity.context.getSystemService(Context.WIFI_SERVICE);
        }
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        mWifiManager.disconnect();
        
        while (i < Loop_cnt) {
            Log.d(Tag, i + "-th Loop.");

			/*				// Thread 내에서 Toast를 하기 위해 post를 사용하였음
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Toast T = Toast.makeText(
							getApplicationContext(),
							i + "-th Loop", Toast.LENGTH_SHORT);
					T.show();
				}
			});
		}
			 */

            Log.d(Tag, "Not interrupted");

            for(int n = 0; n < 2; n++) {
                result = WiFiConnectStart();
                if(n == 0) {
                    Log.d(Tag, "ConnectResult = " + result);
                    ConnectResult = result;
                } else {
                    Log.d(Tag, "DisconnectResult = " + result);
                    DisconnectResult = result;
                }
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }

            if (ConnectResult && DisconnectResult) {
				pass++;
            } else {
                fail++;
            }
            i++;
        }
        
        if(fail == 0 && pass > 0) {
        	return true;
		} else {
			return false;
		}
    
	}
	private synchronized boolean WiFiConnectStart() {
        boolean result = false;
        WifiConfiguration newConfig;
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        if (isConnected == false) {            
            newConfig = new WifiConfiguration();

            newConfig.networkId = -1;
            newConfig.SSID = convertToQuotedString(ConnectActivity.connect_ssid);
            newConfig.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
            newConfig.preSharedKey = convertToQuotedString(ConnectActivity.connect_password);
            newConfig.hiddenSSID = false;
            netId = mWifiManager.addNetwork(newConfig);
            
            //            netId = mWifiConfigStoreProxy.addNetwork(newConfig);
            Log.i(Tag, "Try to connect with " + newConfig.SSID + " password : " + newConfig.preSharedKey);

            try {
                result = mWifiManager.enableNetwork(netId, true);
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            isConnected = true;
            return result;

        } else if (isConnected == true) {
            Log.i(Tag, "Try to disconnect");
            result = mWifiManager.disableNetwork(netId) && mWifiManager.removeNetwork(netId);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            isConnected = false;
            
            return result;
        }
        return result;		
	}
    private static String convertToQuotedString(String string) {
        return "\"" + string + "\"";
    }

    private boolean StationStaticTest() {
		boolean result = false;
	    boolean ConnectResult = false;
	    boolean DisconnectResult = false;
		int pass = 0;
		int fail = 0;
        int i = 0;
    	
        if(mWifiManager == null) {
        	mWifiManager = (WifiManager) allActivity.context.getSystemService(Context.WIFI_SERVICE);
        }
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        mWifiManager.disconnect();
        
        while (i < Loop_cnt) {
            Log.d(Tag, i + "-th Loop.");
/*
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast T = Toast.makeText(
                            getApplicationContext(),
                            i + "-th Loop", Toast.LENGTH_SHORT);
                    T.show();
                }
            });

            Log.d(tag, "Not interrupted");
*/
            for(int n = 0; n < 2; n++) {
                result = WiFiStaticStart();
                if(n == 0) {
                    Log.d(Tag, "ConnectResult = " + result);
                    ConnectResult = result;
                } else {
                    Log.d(Tag, "DisconnectResult = " + result);
                    DisconnectResult = result;
                }
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }

            if (ConnectResult && DisconnectResult) {
                pass++;
            } else {
                fail++;
            }
            i++;
        }
        if(fail == 0 && pass > 0) {
        	return true;
		} else {
			return false;
		}
    }
    private synchronized boolean WiFiStaticStart() {
        boolean result = false;
        WifiConfiguration newConfig;
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        if (isConnected == false) {            
            newConfig = new WifiConfiguration();

            newConfig.networkId = -1;
            newConfig.SSID = convertToQuotedString(StaticIpActivity.connect_ssid);
            newConfig.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
            newConfig.preSharedKey = convertToQuotedString(StaticIpActivity.connect_password);
            newConfig.hiddenSSID = true;
            newConfig.ipAssignment = IpAssignment.STATIC;
            validateIpConfigFields(mLinkProperties);
            newConfig.linkProperties = new LinkProperties(mLinkProperties);
            netId = mWifiManager.addNetwork(newConfig);

            //            netId = mWifiConfigStoreProxy.addNetwork(newConfig);
            Log.i(Tag, "Try to connect with " + newConfig.SSID + " password : " + newConfig.preSharedKey);

            try {
                result = mWifiManager.enableNetwork(netId, true);
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            isConnected = true;
            return result;

        } else if (isConnected == true) {
            Log.i(Tag, "Try to disconnect");
            result = mWifiManager.disableNetwork(netId) && mWifiManager.removeNetwork(netId);
            
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            isConnected = false;
            return result;
        }
        return result;    	
    }
    private boolean validateIpConfigFields(LinkProperties linkProperties) {
    	Log.i(Tag, "validateIpConfigFields");
    	//Ip Address Setting
        String ipAddr = new String("192.168.1.128");
        InetAddress inetAddr = null;
        try {
            inetAddr = NetworkUtils.numericToInetAddress(ipAddr);
        } catch (IllegalArgumentException e) {
        	Log.e(Tag, "NetworkUtils.numericToInetAddress(ipAddr);");
            return false;
        }

        //Network Prefix Setting
        int networkPrefixLength = 24;
        try {
            linkProperties.addLinkAddress(new LinkAddress(inetAddr, networkPrefixLength));
        } catch (NumberFormatException e) {
            // Set the hint as default after user types in ip address
        	Log.e(Tag, "linkProperties.addLinkAddress(new LinkAddress(inetAddr, networkPrefixLength));");
        	return false;
        }

        //Gateway Setting
        String gateway = new String("192.168.1.1");

        InetAddress gatewayAddr = null;
        try {
        	gatewayAddr = NetworkUtils.numericToInetAddress(gateway);
        } catch (IllegalArgumentException e) {
        	Log.e(Tag, "NetworkUtils.numericToInetAddress(gateway);");
        	return false;
        }
        linkProperties.addRoute(new RouteInfo(gatewayAddr));

        //DNS Setting
        String dns = new String("192.168.1.1");
        InetAddress dnsAddr = null;

        try {
        	dnsAddr = NetworkUtils.numericToInetAddress(dns);
        } catch (IllegalArgumentException e) {
        	Log.e(Tag, "NetworkUtils.numericToInetAddress(dns);");
        	return false;
        }
        linkProperties.addDns(dnsAddr);

        return true;
    }
    
    private boolean StationHiddenTest() {
		boolean result = false;
	    boolean ConnectResult = false;
	    boolean DisconnectResult = false;
		int pass = 0;
		int fail = 0;
        int i = 0;
    	        
        if(mWifiManager == null) {
        	mWifiManager = (WifiManager) allActivity.context.getSystemService(Context.WIFI_SERVICE);
        }
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        mWifiManager.disconnect();
        
        while (i < Loop_cnt) {
            Log.d(Tag, i + "-th Loop.");
/*
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast T = Toast.makeText(
                            getApplicationContext(),
                            i + "-th Loop", Toast.LENGTH_SHORT);
                    T.show();
                }
            });

            Log.d(tag, "Not interrupted");
*/
            for(int n = 0; n < 2; n++) {
                result = WiFiHiddenStart();
                if(n == 0) {
                    Log.d(Tag, "ConnectResult = " + result);
                    ConnectResult = result;
                } else {
                    Log.d(Tag, "DisconnectResult = " + result);
                    DisconnectResult = result;
                }
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }

            if (ConnectResult && DisconnectResult) {
                pass++;
            } else {
                fail++;
            }
            i++;
        }
        if(fail == 0 && pass > 0) {
        	return true;
		} else {
			return false;
		}
    }
    private synchronized boolean WiFiHiddenStart() {
        boolean result = false;
        WifiConfiguration newConfig;
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        if (isConnected == false) {            
            newConfig = new WifiConfiguration();

            newConfig.networkId = -1;
            newConfig.SSID = convertToQuotedString(HiddenApConnectActivity.connect_ssid);
            newConfig.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
            newConfig.preSharedKey = convertToQuotedString(HiddenApConnectActivity.connect_password);
            newConfig.hiddenSSID = true;
            netId = mWifiManager.addNetwork(newConfig);
            
            //            netId = mWifiConfigStoreProxy.addNetwork(newConfig);
            Log.i(Tag, "Try to connect with " + newConfig.SSID + " password : " + newConfig.preSharedKey);

            try {
                result = mWifiManager.enableNetwork(netId, true);
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            isConnected = true;
            return result;

        } else if (isConnected == true) {
            Log.i(Tag, "Try to disconnect");
            result = mWifiManager.disableNetwork(netId) && mWifiManager.removeNetwork(netId);

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }
            isConnected = false;
            return result;
        }
        return result;		
    }
    
    @SuppressWarnings("unused")
	private boolean StationSuspendResumeTest() {
    	boolean result = false;
    	boolean Onresult = false;
    	boolean Offresult = false;
    	int pass = 0;
    	int fail = 0;
    	int i = 0;
    	WifiConfiguration newConfig;

    	if(powerManager == null) {
    		powerManager = (PowerManager) allActivity.context.getSystemService(Context.POWER_SERVICE);
    	}
    	mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
    			| PowerManager.ACQUIRE_CAUSES_WAKEUP , Tag);
    	mWakeLock.acquire();
    	
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }else {
        	mWifiManager.setWifiEnabled(false);
    		try {
    			Thread.sleep(5000);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
        	mWifiManager.setWifiEnabled(true);
        }
    	Set_Sleep_Policy_Always();
    	
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
    	if( mWifiManager.getConnectionInfo().getIpAddress() == 0 ) {            
    		newConfig = new WifiConfiguration();

    		newConfig.networkId = -1;
    		newConfig.SSID = convertToQuotedString(ConnectActivity.connect_ssid);
    		newConfig.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
    		newConfig.preSharedKey = convertToQuotedString(ConnectActivity.connect_password);
    		newConfig.hiddenSSID = false;
    		netId = mWifiManager.addNetwork(newConfig);

    		//            netId = mWifiConfigStoreProxy.addNetwork(newConfig);
    		Log.i(Tag, "Try to connect with " + newConfig.SSID + " password : " + newConfig.preSharedKey);

    		try {
    			result = mWifiManager.enableNetwork(netId, true);
    			Thread.sleep(5000);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    			return false;
    		}
    	}

    	while (i < Loop_cnt) {
    		Log.d(Tag, i + "-th Loop.");
    		/*
            if (i <= Loop_cnt) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast T = Toast.makeText(
                                getApplicationContext(),
                                i + "-th Loop", Toast.LENGTH_SHORT);
                        T.show();
                    }
                });
            }
    		 */
    		Log.d(Tag, "Not interrupted");

    		for(int n = 0; n < 2; n++) {
    			result = WiFiSuspendResumeStart();

    			if(isScreenon) {
    				Log.d(Tag, "Screen On Success = " + result);
    				Onresult = result;
    			} else {
    				Log.d(Tag, "Screen Off Success = " + result);
    				Offresult = result;
    			}
    		}

    		if (Onresult && Offresult) {
    			pass++;
    		} else {
    			fail++;
    		}
    		i++;
    		try {
    			Thread.sleep(5000);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    	}
    	if(fail == 0 && pass > 0) {
    		return true;
    	} else {
    		return false;
    	}
    }
    private synchronized boolean WiFiSuspendResumeStart() {
    	if (isScreenon) {
    		Log.d(Tag, "Screen on -> off");
    		gotosleep();
    		isScreenon = false;
    		try {
    			Thread.sleep(150000); //wait for 2min 30sec.
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}

    		Log.d(Tag, "isScreenon = "+ isScreenon + ", IpAddr = " + mWifiManager.getConnectionInfo().getIpAddress());
    		if (isScreenon == false && mWifiManager.getConnectionInfo().getIpAddress() != 0 ){
    			return true;
    		}

    	} else if (!isScreenon) {
    		Log.d(Tag, "Screen off -> on");
    		wakeLock();
    		isScreenon = true;
    		try {
    			Thread.sleep(10000);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    		if (isScreenon == true && mWifiManager.getConnectionInfo().getIpAddress() != 0 ){
    			return true;
    		}
    	}
    	return false;
    }
    private void Set_Sleep_Policy_Always() {
        int wifiSleepPolicy = Settings.System.getInt(allActivity.context.getContentResolver(),
                Settings.System.WIFI_SLEEP_POLICY, 
                Settings.System.WIFI_SLEEP_POLICY_NEVER);
        if (wifiSleepPolicy != Settings.System.WIFI_SLEEP_POLICY_NEVER) {
            try {
                Settings.System.putInt(allActivity.context.getContentResolver(), Settings.System.WIFI_SLEEP_POLICY,
                        Settings.System.WIFI_SLEEP_POLICY_NEVER);
            } catch (NumberFormatException e) {
                Toast.makeText(allActivity.context.getApplicationContext(), "Sleep Policy Error",
                        Toast.LENGTH_SHORT).show();
            }
            
            wifiSleepPolicy = Settings.System.getInt(allActivity.context.getContentResolver(),
                    Settings.System.WIFI_SLEEP_POLICY, 
                    Settings.System.WIFI_SLEEP_POLICY_NEVER); //NEVER means always on JB.

            Log.d(Tag, "wifiSleepPolicy set to " + wifiSleepPolicy);
        }
    }
	private void wakeLock() {
        if(mWakeLock == null) {
            mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP| PowerManager.ON_AFTER_RELEASE, Tag);
        }
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        Log.d(Tag, "wakeLock!");

        mWakeLock.acquire();
    }
    private void gotosleep() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
        Log.d(Tag, "gotosleep!");
        powerManager.goToSleep(SystemClock.uptimeMillis());
    }

    @SuppressWarnings("unused")
    private boolean StationAutoConnectTest() {
    	boolean result = false;
    	boolean Onresult = false;
    	boolean Offresult = false;
    	int pass = 0;
    	int fail = 0;
    	int i = 0;
    	WifiConfiguration newConfig;

    	if(powerManager == null) {
    		powerManager = (PowerManager) allActivity.context.getSystemService(Context.POWER_SERVICE);
    	}
    	mWakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
    			| PowerManager.ACQUIRE_CAUSES_WAKEUP , Tag);
    	mWakeLock.acquire();
    	
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }else {
        	mWifiManager.setWifiEnabled(false);
    		try {
    			Thread.sleep(5000);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
        	mWifiManager.setWifiEnabled(true);
        }
    	Set_Sleep_Policy_Never();
    	
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
    	if( mWifiManager.getConnectionInfo().getIpAddress() == 0 ) {            
    		newConfig = new WifiConfiguration();

    		newConfig.networkId = -1;
    		newConfig.SSID = convertToQuotedString(ConnectActivity.connect_ssid);
    		newConfig.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
    		newConfig.preSharedKey = convertToQuotedString(ConnectActivity.connect_password);
    		newConfig.hiddenSSID = false;
    		netId = mWifiManager.addNetwork(newConfig);

    		//            netId = mWifiConfigStoreProxy.addNetwork(newConfig);
    		Log.i(Tag, "Try to connect with " + newConfig.SSID + " password : " + newConfig.preSharedKey);

    		try {
    			result = mWifiManager.enableNetwork(netId, true);
    			Thread.sleep(5000);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    			return false;
    		}
    	}
    	while (i < Loop_cnt) {
    		Log.d(Tag, i + "-th Loop.");
    		/*
            if (i <= Loop_cnt) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast T = Toast.makeText(
                                getApplicationContext(),
                                i + "-th Loop", Toast.LENGTH_SHORT);
                        T.show();
                    }
                });
            }
    		 */
    		Log.d(Tag, "Not interrupted");

    		for(int n = 0; n < 2; n++) {
    			result = WiFiAutoConnectStart();

    			if(isScreenon) {
    				Log.d(Tag, "Screen On Success = " + result);
    				Onresult = result;
    			} else {
    				Log.d(Tag, "Screen Off Success = " + result);
    				Offresult = result;
    			}
    		}

    		if (Onresult && Offresult) {
    			pass++;
    		} else {
    			fail++;
    		}
    		i++;
    		try {
    			Thread.sleep(5000);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    	}
    	if(fail == 0 && pass > 0) {
    		return true;
    	} else {
    		return false;
    	}
    }
    private synchronized boolean WiFiAutoConnectStart() {
    	if (isScreenon) {
    		Log.d(Tag, "Screen on -> off");
    		gotosleep();
    		isScreenon = false;

    		try {
    			Thread.sleep(150000); //wait for 2min 30sec.
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    		
    		Log.d(Tag, "isScreenon = "+ isScreenon + ", IpAddr = " + mWifiManager.getConnectionInfo().getIpAddress());
    		if (isScreenon == false && mWifiManager.getConnectionInfo().getIpAddress() == 0 ){
    			return true;
    		}

    	} else if (!isScreenon) {
    		Log.d(Tag, "Screen off -> on");
    		wakeLock();
    		isScreenon = true;

    		try {
    			Thread.sleep(10000);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    		if (isScreenon == true && mWifiManager.getConnectionInfo().getIpAddress() != 0 ){
    			return true;
    		}
    	}
    	return false;
    }
    private void Set_Sleep_Policy_Never() {
        int wifiSleepPolicy = Settings.System.getInt(allActivity.context.getContentResolver(),
                Settings.System.WIFI_SLEEP_POLICY, 
                Settings.System.WIFI_SLEEP_POLICY_NEVER);
        if (wifiSleepPolicy != Settings.System.WIFI_SLEEP_POLICY_DEFAULT) {
            try {
                Settings.System.putInt(allActivity.context.getContentResolver(), Settings.System.WIFI_SLEEP_POLICY,
                        Settings.System.WIFI_SLEEP_POLICY_DEFAULT);
            } catch (NumberFormatException e) {
                Toast.makeText(allActivity.context.getApplicationContext(), "Sleep Policy Error",
                        Toast.LENGTH_SHORT).show();
            }

            wifiSleepPolicy = Settings.System.getInt(allActivity.context.getContentResolver(),
                    Settings.System.WIFI_SLEEP_POLICY, 
                    Settings.System.WIFI_SLEEP_POLICY_NEVER); //NEVER means always on JB.

            Log.d(Tag, "wifiSleepPolicy set to " + wifiSleepPolicy);
        }
    }
 
    private boolean HotspotOnOffTest() {
		boolean result = false;
		boolean Onresult = false;
		boolean Offresult = false;
		int pass = 0;
		int fail = 0;
		int i = 0;
		WifiConfiguration mWifiConfig;
        if(mWifiManager == null) {
        	mWifiManager = (WifiManager) allActivity.context.getSystemService(Context.WIFI_SERVICE);
        }
        mWifiConfig = mWifiManager.getWifiApConfiguration();
		Log.d(Tag, "AllTest Hotspot OnOff!!");
		if(mWifiManager.isWifiApEnabled() || mWifiManager.isWifiEnabled()){
			mWifiManager.setWifiApEnabled(null, false);
			mWifiManager.setWifiEnabled(false);
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mWifiConfig.SSID = "Wi-Fi_Sanity_AP";
		mWifiConfig.allowedKeyManagement.set(KeyMgmt.WPA2_PSK);
		mWifiConfig.preSharedKey = "lge12345";

		while (i < Loop_cnt) {
			Log.d(Tag, i + "-th Loop.");

			if (i <= Loop_cnt) {

				Log.d(Tag, "Not interrupted");

				for(int n = 0; n < 2; n++) {
					result = WiFiAPOnOffStart(mWifiConfig);
					try {
						Thread.sleep(4000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						return false;
					}
					if(mWifiManager.isWifiApEnabled()) {
						Log.d(Tag, "WiFiAP On Success = " + result);
						Onresult = result;
					} else {
						Log.d(Tag, "WiFiAP Off Success = " + result);
						Offresult = result;
					}
				}

				if (Onresult && Offresult) {
					pass++;
				} else {
					fail++;
				}
				i++;
			}
		}
        if(fail == 0 && pass > 0) {
        	return true;
		} else {
			return false;
		}
	}
	private synchronized boolean WiFiAPOnOffStart(WifiConfiguration mWifiConfig) {
		if (mWifiManager.isWifiApEnabled()) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			}
			return mWifiManager.setWifiApEnabled(null, false);
		} else {
			return mWifiManager.setWifiApEnabled(mWifiConfig, true);
		}
	}
	
	private boolean MiracastOnOffTest() {
		boolean rst;
		int wfdst = WfdManager.WFD_STATE_UNKNOWN;
		int i;
		
		if(mWfdManager == null) {
			 LGContext mServiceContext = new LGContextImpl(context);
			 mWfdManager = (WfdManager)mServiceContext.getLGSystemService(WfdManager.SERVICE_NAME);
		}
		 
		mWfdManager.setWifiDisplayEnabled(true);
		 
		for(i=0; i<10; i++){
 			try {
                 Thread.sleep(1000);
             } catch (InterruptedException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }
 			
 			//check WfdState
 			wfdst = mWfdManager.getWfdState();
 			if(wfdst == WfdManager.WFD_STATE_NOT_CONNECTED ||
 					wfdst == WfdManager.WFD_STATE_LINK_CONNECTED){
 				break;
 			}
 		}
		
		if(wfdst != WfdManager.WFD_STATE_NOT_CONNECTED &&
				wfdst != WfdManager.WFD_STATE_LINK_CONNECTED){
			mWfdManager.setWifiDisplayEnabled(false);
			return false;
		}
		
		mWfdManager.setWifiDisplayEnabled(false);
		
		for(i=0; i<10; i++){
 			try {
                 Thread.sleep(1000);
             } catch (InterruptedException e) {
                 // TODO Auto-generated catch block
                 e.printStackTrace();
             }
 			
 			//check WfdState
 			wfdst = mWfdManager.getWfdState();
 			if(wfdst == WfdManager.WFD_STATE_DISABLED){
 				break;
 			}
 		}
		
		if(wfdst != WfdManager.WFD_STATE_DISABLED){
			return false;
		}
		
		return true;
	}
}
/*
 * 작업은 여기에 각 담당자분들의 함수에서 하시면 됩니다.
 * 각 단계가 끝나면 PASS/FAIL만 *attribute에 저장하시면 됩니다.
 *  Station에 Wifi on/off 가 성공일 결우 sattribute[0].add(PF, "PASS");
 *  Station에 connect 가 성공일 결우 sattribute[1].add(PF, "PASS");
 *  Station에 Autoconnect 가 실패일 결우 sattribute[5].add(PF, "FAIL");
 *  Station
 * 	"Wi-Fi On/Off",					//0
 *  "Wi-Fi Connect",				//1
 *  "Static IP",					//2
 *  "Hidden AP Connect",			//3
 *  "Suspend/Resume",				//4
 *  "AP AutoConnect"};				//5
 *  
 *  HotSpot
 *  "Hotspot ON/OFF",				//0
 *  "Hotspot Connection"};			//1
 *  
 *  Direct
 *  "Direct On/Off",				//0
 *  "Scan",							//1
 *  "Gruop",						//2
 *  "Connect"};						//3
 */
