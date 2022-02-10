package com.rfid;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

//import device.common.rfid.RFIDCallback;
//import device.common.rfid.RecvPacket;
//import device.sdk.RFIDManager;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;

import com.rfid.data.local.DatabaseClient;
import com.rfid.util.PreferenceUtil;
import com.rfid.util.Utils;

public class RFIDApplication extends Application
{
    private final String TAG = "RFIDApplication";

    private String mCurrentActivity = "";
    private String mHomeActivity = "";

    public final String RFID_CONTROL_ACTIVITY = "com.rfid.ui.RFIDControlActivity";
    public final String RFID_DEMO_ACTIVITY = "com.rfid.ui.rfiddemo.RFIDDemoActivity";

    private static RFIDApplication mRFIDApplication;

//    private RFIDManager mRfidMgr;
    private PreferenceUtil mPrefUtil;
    private Utils mUtils;
//    private NotifyDataCallbacks mDataCallbacks;

    public boolean mIsSleep = false;

//    public interface NotifyDataCallbacks
//    {
//        void notifyDataPacket(RecvPacket recvPacket);
//
//        void notifyChangedState(int state);
//    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG, "onCreate");
        RxJavaPlugins.setErrorHandler(null);
        DatabaseClient.INSTANCE.createDatabase(this);
        mHomeActivity = "";
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();
        Log.d(TAG, "onTerminate");
        unregisterActivityLifecycleCallbacks(mActivityLifecycleCallbacks);
    }

    public void init()
    {
//        mRfidMgr = RFIDManager.getInstance();
//        mRfidMgr.RegisterRFIDCallback(mRFIDCallback);
    }

    public void terminate()
    {
//        mRfidMgr.UnregisterRFIDCallback(mRFIDCallback);
    }

//    public void setNotifyDataCallback(NotifyDataCallbacks callbacks)
//    {
//        mDataCallbacks = callbacks;
//        Log.d(TAG,"mDataCallbacks : " + mDataCallbacks);
//    }

    ActivityLifecycleCallbacks mActivityLifecycleCallbacks = new ActivityLifecycleCallbacks()
    {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState)
        {
            Log.d(TAG, "current created activity : " + activity);
            if(activity.toString().contains(RFID_CONTROL_ACTIVITY)
                    || activity.toString().contains(RFID_DEMO_ACTIVITY))
            {
                if(mHomeActivity.isEmpty())
                {
                    mHomeActivity = activity.toString();
                    Log.d(TAG, "mHomeActivity : " + mHomeActivity);

                    init();
                }
            }
        }

        @Override
        public void onActivityStarted(Activity activity)
        {
            Log.d(TAG, "current started activity : " + activity);
        }

        @Override
        public void onActivityResumed(Activity activity)
        {
            Log.d(TAG, "current resumed activity : " + activity);
            mCurrentActivity = activity.toString();
            Log.d(TAG, "mCurrentActivity : " + mCurrentActivity);
            mIsSleep = false;

        }

        @Override
        public void onActivityPaused(Activity activity)
        {
            Log.d(TAG, "current paused activity : " + activity);
            mIsSleep = true;
        }

        @Override
        public void onActivityStopped(Activity activity)
        {
            Log.d(TAG, "current stopped activity : " + activity);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState)
        {
            Log.d(TAG, "current SaveInstanceState activity : " + activity);
        }

        @Override
        public void onActivityDestroyed(Activity activity)
        {
            Log.d(TAG, "current destroyed activity : " + activity);
            if(activity.toString().contains(mHomeActivity)
                    && !mHomeActivity.isEmpty())
            {
                Log.d(TAG, "mHomeActivity : " + mHomeActivity);
                mHomeActivity = "";
                terminate();
            }
        }
    };

    private Handler mHandler = new Handler(new Handler.Callback()
    {
        @Override
        public boolean handleMessage(Message msg)
        {
            return false;
        }
    });

//    RFIDCallback mRFIDCallback = new RFIDCallback(mHandler)
//    {
//        @Override
//        public void onNotifyReceivedPacket(RecvPacket recvPacket)
//        {
//            super.onNotifyReceivedPacket(recvPacket);
//            Log.d(TAG, "onNotifyReceivedPacket : " + recvPacket.RecvString);
//            if(mDataCallbacks != null)
//                mDataCallbacks.notifyDataPacket(recvPacket);
//            else
//                Log.d(TAG,"mDataCallbacks is null");
//        }
//
//        @Override
//        public void onNotifyChangedState(int state)
//        {
//            super.onNotifyChangedState(state);
//            Log.d(TAG, "onNotifyChangedState : " + state);
//            Log.d(TAG, "mCurrentActivity : " + mCurrentActivity);
//            if(mCurrentActivity.contains(RFID_CONTROL_ACTIVITY)
//                    || mCurrentActivity.contains(RFID_DEMO_ACTIVITY))
//            {
//                if(mDataCallbacks != null)
//                    mDataCallbacks.notifyChangedState(state);
//                else
//                    Log.d(TAG,"mDataCallbacks is null");
//            }
//        }
//    };
}
