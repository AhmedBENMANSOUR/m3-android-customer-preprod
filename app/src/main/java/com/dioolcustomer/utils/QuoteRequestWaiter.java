package com.dioolcustomer.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.dioolcustomer.activities.QuoteRequestActivity;

/**
 * Created by ASUS on 17/04/2017.
 */

public class QuoteRequestWaiter extends Thread {

    private static final String TAG = Waiter.class.getName();
    private long lastUsed;
    private long period;
    private boolean stop;

    public Handler mHandler;



    public QuoteRequestWaiter(long period) {
        this.period = period;
        stop = false;
    }

   // Runnable runnable = new Runnable() {
        public void run() {
            Looper.prepare();

            long idle = 0;
            QuoteRequestWaiter.this.touch();
            do {
                idle = System.currentTimeMillis() - lastUsed;
                Log.e(TAG, "Application is idle for " + idle + " ms");
                try {
                    Thread.sleep(5000); // check every 5 seconds
                } catch (InterruptedException e) {
                    Log.d(TAG, "Waiter interrupted!");
                }
                if (idle > period) {
                    idle = 0;
                    // do something here - e.g. call popup or so
                    QuoteRequestActivity quoteRequestActivity  = new QuoteRequestActivity();
                    quoteRequestActivity.getQuoteRequests();
                    Log.e("Finish","zzz");
                }
            } while (!stop);
            Log.d(TAG, "Finishing Waiter thread");
        }
  //  };





    public synchronized void touch() {
        lastUsed = System.currentTimeMillis();
    }

    public synchronized void forceInterrupt() {
        this.interrupt();
    }

    // soft stopping of thread
    public synchronized void stopTimer() {
        stop = true;

    }


    public synchronized void setPeriod(long period) {
        this.period = period;
    }
}
