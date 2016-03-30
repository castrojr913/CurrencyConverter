package com.jacr.currencyconverter.test.utilities.helpers;

/**
 * TimeHelper.
 * Created by Jesus Castro on 10/11/2015.
 */
public class TimeHelper {

    private static final Class<?> LOG_TAG = TimeHelper.class;

    public static void pause(final int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            LogHelper.getInstance().error(LOG_TAG, e);
        }
    }

    public static void pause() {
        // It applies an default delay for the thread on execution
        pause(1000);
    }

}
