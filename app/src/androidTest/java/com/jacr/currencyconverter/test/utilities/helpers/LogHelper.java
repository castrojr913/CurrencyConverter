package com.jacr.currencyconverter.test.utilities.helpers;

import timber.log.Timber;

/**
 * LogHelper
 * Created by Jesus Castro on 10/11/2015.
 */
public class LogHelper {

    private static LogHelper singleton;

    private LogHelper() {
        Timber.plant(new Timber.DebugTree());
    }

    public static LogHelper getInstance() {
        if (singleton == null) {
            singleton = new LogHelper();
        }
        return singleton;
    }

    public void error(Class<?> source, Exception e) {
        Timber.tag(source.getSimpleName()).e("There was an exception during the test: %s", e.toString());
    }

    public void error(Class<?> source, Throwable t) {
        Timber.tag(source.getSimpleName()).e("There was an exception during the test: %s", t.toString());
    }

}
