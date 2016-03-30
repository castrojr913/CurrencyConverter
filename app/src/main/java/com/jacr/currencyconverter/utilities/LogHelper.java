package com.jacr.currencyconverter.utilities;

import timber.log.Timber;

/**
 * LogHelper
 * Created by Jesus Castro on 09/10/2015.
 */
public class LogHelper {

    private static LogHelper singleton;

    private LogHelper() {
        // blank
    }

    public static LogHelper getInstance() {
        if (singleton == null) {
            singleton = new LogHelper();
        }
        return singleton;
    }

    public void enableLogs() {
        Timber.plant(new Timber.DebugTree());
    }

    //<editor-fold desc="General Purpose Logs">

    public void debug(Class<?> sourceClass, String message) {
        Timber.tag(sourceClass.getSimpleName()).d(message);
    }

    public void exception(Class<?> sourceClass, Throwable exception, String message, Object... args) {
        Timber.tag(sourceClass.getSimpleName()).e(exception, message, args);
    }

    //</editor-fold>

    //<editor-fold desc="Http Requests Logs">

    private String buildResponseMessage(String url, String message, int statusCode, byte[] responseBody) {
        final String separator = "_____________________";
        return String.format("\n%s " +
                "\n- URL:%s " +
                "\n- STATUS_CODE:%d " +
                "\n- BODY:'%s' \n" + separator, message, url, statusCode, responseBody != null ? new String(responseBody) : "");
    }

    private String buildRequestMessage(String url, String message) {
        final String separator = "_____________________";
        return String.format("\n%s " + "\n- URL:%s \n" + separator, message, url);
    }

    public void debugRequest(Class<?> sourceClass, String url, String message) {
        Timber.tag(sourceClass.getSimpleName()).d(buildRequestMessage(url, message));
    }

    public void debugResponse(Class<?> sourceClass, String url, String message, int statusCode, byte[] responseBody) {
        Timber.tag(sourceClass.getSimpleName()).d(buildResponseMessage(url, message, statusCode, responseBody));
    }

    public void errorResponse(Class<?> sourceClass, String url, String message, int statusCode, byte[] responseBody) {
        Timber.tag(sourceClass.getSimpleName()).d(buildResponseMessage(url, message, statusCode, responseBody));
    }

    //</editor-fold>

}
