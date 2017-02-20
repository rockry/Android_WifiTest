package com.example.android_wifitest_v0_1.subdisplay;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

//WfdManager
import com.lge.systemservice.core.LGContextImpl;
import com.lge.systemservice.core.LGContext;
import com.lge.systemservice.core.wfdmanager.WfdManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;


public class WfdScanTestService extends Service {
    private String tag = "WfdScanTestService";

    private static final int WFD_SCAN_START = 0;
    private static final int WFD_SCAN_COMPLETE = 1;
    
    private static Context mContext;
    private static int Loop_cnt, Curr_cnt;
    private static boolean Scanresult = false;
    private static BroadcastReceiver mReceiver;
    private static IntentFilter mFilter;
    private static int currentWfdState = WfdManager.WFD_STATE_UNKNOWN;
    private static int expectedWfdState = WfdManager.WFD_STATE_UNKNOWN;
    private static int isScanCompleted = WFD_SCAN_COMPLETE;
    private static WfdManager mWfdManager=null;
    private static WifiP2pManager mWifiP2pManager=null;
    private static WifiP2pManager.ActionListener mScanListener;
    private static Channel mChannel;
    private static WfdScanTestServiceThread WfdProc;
    private static boolean mQuitWfdProc;
            
    @Override
    public void onCreate() {
        Log.i(tag, "WfdScanTestService start");
        super.onCreate();
        mContext = getApplicationContext();
    }
    
    @Override
    public int onStartCommand(Intent intent,int flags, int startId) {
    	Log.d(tag, "onStartCommand : intent-" + intent.getExtras() +
    			 ", flags-" + flags +
    			 ", startId-" + startId);
    	super.onStartCommand(intent, flags, startId);
    	mFilter = new IntentFilter();
        mFilter.addAction(WfdManager.WFD_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (WfdManager.WFD_STATE_CHANGED_ACTION.equals(action)) {
                	Log.d(tag, "Received WFD_STATE_CHANGED_ACTION\n.");
                	handleWFDStateChanged(intent.getIntExtra(WfdManager.EXTRA_WFD_STATE, 
                			WfdManager.WFD_STATE_DISABLED));
                } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
                	Log.d(tag, "Received WIFI_P2P_PEERS_CHANGED_ACTION\n.");
                	handleP2pPeerChanged();
                }
            }
        };
        registerReceiver(mReceiver, mFilter);
        
       	mQuitWfdProc = false;
       	WfdProc = new WfdScanTestServiceThread(this, mHandler);
    	WfdProc.start();   
    	
    	Toast toast = Toast.makeText(mContext, "WfdScanTestService start!", Toast.LENGTH_SHORT);
        toast.show(); 
    	return START_STICKY;
    }
    
    public void onDestroy() {
        Log.d(tag, "WfdScanTestService stop");
        super.onDestroy();
        unregisterReceiver(mReceiver);
        if (WfdProc != null && WfdProc.isAlive()) {
        	mQuitWfdProc = true; //WfdScanTestServiceThread end
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
   
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.d(tag, "Got message in handler");
            if (msg.what == 0) {
            }
        }
    };

    private synchronized void WfdOnOff(boolean onoff) {
    	if(onoff) {
   			mWfdManager.setWifiDisplayEnabled(true);
       		expectedWfdState = WfdManager.WFD_STATE_NOT_CONNECTED;
    	} else {
    		mWfdManager.setWifiDisplayEnabled(false);
    		expectedWfdState = WfdManager.WFD_STATE_DISABLED;
    	}
    }
    
    private void handleWFDStateChanged(int state) {
        Log.v(tag, "handleWFDStateChange: " + state );
        currentWfdState = state;        
        if(state == expectedWfdState){
        	currentWfdState = expectedWfdState;
        }
     }
    
    private void handleP2pPeerChanged(){
    	Log.v(tag, "handleP2pPeerChanged: WFD_SCAN_COMPLETE");
    	isScanCompleted = WFD_SCAN_COMPLETE;    	
    }
    
    class WfdScanTestServiceThread extends Thread {
    	WfdScanTestService mParent;
    	Handler mHandler;
    	
    	public WfdScanTestServiceThread(WfdScanTestService parent, Handler handler) {
    		mParent = parent;
    		mHandler = handler;    		
    	}
    	
    	public boolean waitingForScanComplete(int timeout){
    		boolean result = false;
    		
    		for(int i=0; i<timeout; i++){
    			try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
    			if(isScanCompleted == WFD_SCAN_COMPLETE) {
    				result = true;
    				break;
    			}
    		}
    		return result;
    	}
    	
    	public boolean waitingForExpectedWfdState(int timeout){
    		boolean result = false;
    		for(int i=0; i<timeout; i++){
    			try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
    			
    			if(expectedWfdState == currentWfdState) {
    				result = true;
    				break;
    			}
    		}
    		return result;
    	}
       	
    	public void run() {
    		LGContext mServiceContext = new LGContextImpl(mContext);
            mWfdManager = (WfdManager)mServiceContext.getLGSystemService(WfdManager.SERVICE_NAME);
    		if(mWfdManager==null) {
    			Intent intent = new Intent(mSearchActivity.WFD_SCAN_FINISH);
                sendBroadcast(intent);
                return;
    		} 
    		currentWfdState = mWfdManager.getWfdState();
           	if(currentWfdState == WfdManager.WFD_STATE_DISABLED) {
           		WfdOnOff(true);
           		waitingForExpectedWfdState(5);
           	}
           	
           	mWifiP2pManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
            if(mWifiP2pManager != null) {
            	mChannel = mWifiP2pManager.initialize(mContext,mContext.getMainLooper(), null);
            	if (mChannel == null) {
                    //Failure to set up connection
                    Log.e(tag, "Failed to set up connection with wifi p2p service");
                    mWifiP2pManager = null;
                }
            }
            if(mWifiP2pManager==null) {
    			WfdOnOff(true);
    			Intent intent = new Intent(mSearchActivity.WFD_SCAN_FINISH);
                sendBroadcast(intent);
                return;
    		}
           	mScanListener = new WifiP2pManager.ActionListener() {
                public void onSuccess() {
                	Log.d(tag, " discover success");
                }
                public void onFailure(int reason) {
                	Log.d(tag, " discover fail");
                }
            };
            
            Loop_cnt = mSearchActivity.Loop_cnt;
    		Curr_cnt = 1;            
    		while ((Curr_cnt <= Loop_cnt) && (!mQuitWfdProc)) {
    			Log.d(tag, Curr_cnt + "-th Loop\n.");
    			
            	mHandler.post(new Runnable() {
                	@Override
                	public void run() {
                		Toast T = Toast.makeText(
                				mContext,
                				Curr_cnt + "-th Loop", 
                				Toast.LENGTH_SHORT);
                		T.show();
                	}
                });
                
                mWifiP2pManager.discoverPeers(mChannel, mScanListener);
                isScanCompleted = WFD_SCAN_START;
                
                //waiting for expectedWfdState with timeout
                Scanresult = waitingForScanComplete(5);
                if (Scanresult) {
                    Intent intent = new Intent(mSearchActivity.WFD_SCAN_SUCCESS);
                    sendBroadcast(intent);
                    Curr_cnt++;
                } else {
                    Intent intent = new Intent(mSearchActivity.WFD_SCAN_FAIL);
                    sendBroadcast(intent);
                    break;
                }
            }
    		
    		if(currentWfdState != WfdManager.WFD_STATE_DISABLED){
    			WfdOnOff(false);
    		}
            
            // Thread 내에서 Toast를 하기 위해 post를 사용하였음
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "End of WfdTest",
                            Toast.LENGTH_SHORT).show();
                }
            });                    
            
            Intent intent = new Intent(mSearchActivity.WFD_SCAN_FINISH);
            sendBroadcast(intent);
    	}
    }
}
