package com.start.download;

import android.app.Application;

/**
 * Created by qiaoshi on 2018/1/28.
 */

public class DemoApplication extends Application {
    private static DemoApplication instance;
    public static DemoApplication getInstance() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
            @Override
            public void uncaughtException(Thread thread, final Throwable ex) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
    }
}
