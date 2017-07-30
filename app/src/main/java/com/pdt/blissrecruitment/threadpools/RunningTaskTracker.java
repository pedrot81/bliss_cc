package com.pdt.blissrecruitment.threadpools;

import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by pdt on 27/07/2017.
 */

public class RunningTaskTracker {
    private static final String TAG = "RunningTaskTracker";

    private final int MAX_THREAD = Runtime.getRuntime().availableProcessors();
    private final int MAX_THREAD_WAIT = 30;
    private ThreadPoolExecutor mThreadPoolExecutor = null;
    private BlockingQueue<Runnable> mWorkQueue = null;

    private static volatile RunningTaskTracker sInstance;

    private ScheduledExecutorService mThreadScheduledExecutor = null;

    public static RunningTaskTracker getInstance() {
        if (sInstance == null) {
            synchronized (RunningTaskTracker.class) {
                if (sInstance == null) {
                    sInstance = new RunningTaskTracker();
                }
            }
        }

        return sInstance;
    }

    private RunningTaskTracker() {
        this.mWorkQueue = new LinkedBlockingQueue<Runnable>();
        this.mThreadPoolExecutor = new ThreadPoolExecutor(MAX_THREAD, MAX_THREAD_WAIT, 0L, TimeUnit.MILLISECONDS, mWorkQueue);
    }

    public Future<?> submitTask(Runnable runnable) {
        Log.v(TAG, "New task submitted to pool: " + runnable.toString());
        return mThreadPoolExecutor.submit(runnable);
    }
}
