package com.dioolcustomer;

import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.auth0.lock.Lock;
import com.auth0.lock.LockProvider;
import com.dioolcustomer.activities.CustomPinActivity;
import com.dioolcustomer.constants.GlobalConstants;
import com.dioolcustomer.utils.Waiter;
import com.github.orangegangsters.lollipin.lib.managers.AppLock;
import com.github.orangegangsters.lollipin.lib.managers.LockManager;
import com.google.android.gms.analytics.Tracker;
import com.segment.analytics.Analytics;

import java.util.List;


/**
 * Created by mypc on 01/03/2016.
 */
public class MyMoneyMobileApplication extends Application implements LockProvider {
    //private Tracker mTracker;
    private static MyMoneyMobileApplication instance;
    private Lock lock;
    private RequestQueue mRequestQueue;
    public static LockManager<CustomPinActivity> lockManager;
    private static final int REQUEST_CODE_ENABLE = 11;
    private Waiter waiter;
    private static boolean mIsConnected = false;


    SharedPreferences.Editor editor;
    private SharedPreferences shared;


    //Volley Job
    public static final String TAG = MyMoneyMobileApplication.class
            .getSimpleName();

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     *
     * @return tracker
     * <p/>
     * synchronized public Tracker getDefaultTracker() {
     * if (mTracker == null) {
     * GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
     * // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
     * mTracker = analytics.newTracker(R.xml.global_tracker);
     * }
     * return mTracker;
     * }
     */

    public MyMoneyMobileApplication() {
        instance = this;
    }

    public static boolean ismIsConnected() {
        return mIsConnected;
    }

    public static void setmIsConnected(boolean mIsConnected) {
        MyMoneyMobileApplication.mIsConnected = mIsConnected;
    }

    /**
     * @return application context
     */
    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public static MyMoneyMobileApplication getInstance() {
        return instance;
    }

    public static void setInstance(MyMoneyMobileApplication instance) {
        MyMoneyMobileApplication.instance = instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Fabric.with(this, new Crashlytics());

        // Prepare Preferences
        shared = getSharedPreferences(GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE);
        editor = getSharedPreferences(
                GlobalConstants.MMM_SHARED_PREFERENCES, MODE_PRIVATE).edit();

        Analytics analytics = new Analytics.Builder(getApplicationContext(), GlobalConstants.ANONYMOUS_ID1)
                .build();

        Analytics.setSingletonInstance(analytics);
        //Intercom.initialize(this, GlobalConstants.INTERCOM_API_KEY, GlobalConstants.INTERCOM_APP_ID);
        lock = new Lock.Builder()
                .loadFromApplication(this)
                /** Other configuration goes here */
                .closable(true)
                .build();

        lockManager = LockManager.getInstance();
        lockManager.enableAppLock(this, CustomPinActivity.class);
        lockManager.getAppLock().setLogoId(R.drawable.ic_launcher);
        lockManager.getAppLock().setFingerprintAuthEnabled(false);
        //lockManager.getAppLock().setTimeout(2000);

        waiter = new Waiter(4 * 60 * 10); //15 mins
        waiter.start();

    }


    public void touch() {
        waiter.touch();
    }

    @Override
    public void onTerminate() {
        // TODO Auto-generated method stub
        super.onTerminate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    public Lock getLock() {
        return lock;
    }


    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            System.out.println("getApplicationContext() : "+getApplicationContext());
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    public void start() {
        if (!waiter.isAlive())
            waiter.start();
    }

    public void stop() {
        waiter.stopTimer();
        waiter.forceInterrupt();
        waiter = new Waiter(4 * 60 * 1000);

    }

    public void launchCodPinScreen() {
        if (!isAppIsInBackground(this)) {
            if ((ismIsConnected())&&(!shared.getString("USER_TOKEN", "").equals(""))) {
                MyMoneyMobileApplication.getInstance().stop();
                Intent mIntent = new Intent(this, CustomPinActivity.class);

                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK);

                if(lockManager.getAppLock().checkPasscode("")){
                    mIntent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
                }
                else if (lockManager.getAppLock().isPasscodeSet())
                    mIntent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
                 else
                    mIntent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
                startActivity(mIntent);
            }
        }

    }


    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }


}
