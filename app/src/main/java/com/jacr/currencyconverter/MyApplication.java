package com.jacr.currencyconverter;

import android.app.Application;

import com.jacr.currencyconverter.utilities.LogHelper;

/**
 * MyApplication
 * Created by jesus on 10/10/2015.
 */
public class MyApplication extends Application {

    /*
     * This class is executed once time.
     */

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.ENABLE_LOG) {
            LogHelper.getInstance().enableLogs();
        }
    }

}
