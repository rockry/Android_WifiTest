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


public class WfdOnOffTestService extends Service {
    private String tag = "WfdOnOffTestService";
    
    private static Context mContext;
    private int Loop_cnt, Curr_cnt;
    private boolean Onresult = false;
    private boolean Offresult = false;
       
    private static BroadcastReceiver mReceiver;
    private static IntentFilter mFilter;
    
    private static int currentWfdState = WfdManager.WFD_STATE_UNKNOWN;
    private static int expectedWfdState = WfdManager.WFD_STATE_UNKNOWN;
    private WfdManager mWfdManager=null;
    private WfdOnOffTestServiceThread WfdProc;
    private static boolean mQuitWfdProc;
            
    @Override
    public void onCreate() {
        Log.i(tag, "WfdOnOffTestService start");
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
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (WfdManager.WFD_STATE_CHANGED_ACTION.equals(action)) {
                	Log.d(tag, "Received WFD_STATE_CHANGED_ACTION\n.");
                	handleWFDStateChanged(intent.getIntExtra(WfdManager.EXTRA_WFD_STATE, 
                			WfdManager.WFD_STATE_DISABLED));
                }
            }
        };
        registerReceiver(mReceiver, mFilter);
    	
       	mQuitWfdProc = false;
       	WfdProc = new WfdOnOffTestServiceThread(this, mHandler);
    	WfdProc.start();   
    	
    	Toast toast = Toast.makeText(mContext, "WfdOnOffTestService start!", Toast.LENGTH_SHORT);
        toast.show(); 
    	return START_STICKY;
    }
    
    public void onDestroy() {
        Log.d(tag, "WfdOnOffTestService stop");
        super.onDestroy();
        unregisterReceiver(mReceiver);
        if (WfdProc != null && WfdProc.isAlive()) {
        	mQuitWfdProc = true; //WfdOnOffTestServiceThread end
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
    
    class WfdOnOffTestServiceThread extends Thread {
    	WfdOnOffTestService mParent;
    	Handler mHandler;
    	
    	public WfdOnOffTestServiceThread(WfdOnOffTestService parent, Handler handler) {
    		mParent = parent;
    		mHandler = handler;    		
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
    			
    			Log.d(tag, "waitingForExpectedWfdState e = " + expectedWfdState + " c = " + currentWfdState + "\n.");
    			if(expectedWfdState == WfdManager.WFD_STATE_NOT_CONNECTED) {
    				if(expectedWfdState == currentWfdState || WfdManager.WFD_STATE_LINK_CONNECTED == currentWfdState) {
    					result = true;
    					break;
    				}    				
    			} else {
    				if(expectedWfdState == currentWfdState) {
    					result = true;
    					break;
    				}
    			}
    		}
    		return result;
    	}
    	
    	public boolean checkForExpectedWfdState(int timeout){
    		boolean result = false;
    		for(int i=0; i<timeout; i++){
    			try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
    			
    			currentWfdState = mWfdManager.getWfdState();
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
    		
    		Loop_cnt = mOnOffActivity.Loop_cnt;
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
                
                WfdOnOff(true);
                //waiting for expectedWfdState with timeout
                Onresult = waitingForExpectedWfdState(5);
                Log.d(tag, "WFD on "+Onresult);
                
                WfdOnOff(false);
                //waiting for expectedWfdState with time out
                Offresult = waitingForExpectedWfdState(5);
                Log.d(tag, "WFD off "+Offresult);

                if (Onresult && Offresult) {
                    Intent intent = new Intent(mOnOffActivity.WFD_ONOFF_SUCCESS);
                    sendBroadcast(intent);
                    Curr_cnt++;
                } else {
                    Intent intent = new Intent(mOnOffActivity.WFD_ONOFF_FAIL);
                    sendBroadcast(intent);
                    Curr_cnt++;
//                    break;
                }
            }
    		            
            // Thread 내에서 Toast를 하기 위해 post를 사용하였음
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "End of WfdTest",
                            Toast.LENGTH_SHORT).show();
                }
            });                    
            
            Intent intent = new Intent(mOnOffActivity.WFD_ONOFF_FINISH);
            sendBroadcast(intent);
    	}
    }
}
