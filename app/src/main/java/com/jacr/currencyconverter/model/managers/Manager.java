package com.jacr.currencyconverter.model.managers;

import com.google.gson.Gson;
import com.jacr.currencyconverter.model.ErrorCodes;
import com.jacr.currencyconverter.model.managers.listeners.ManagerListener;
import com.jacr.currencyconverter.utilities.LogHelper;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Manager
 * Created by Jesus Castro on 09/10/2015.
 */
public abstract class Manager {

    final Gson gson = new Gson();

    //<editor-fold desc="Abstract Methods">

    protected abstract Class<?> getLogTag();

    protected abstract void manageResponse(String url, byte[] response, ManagerListener listener);

    //</editor-fold>

    protected final Callback createHttpCallback(final String url, final ManagerListener listener) {
        final Class<?> logTag = getLogTag();
        return new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                int errorType;
                String exception = e.toString();
                if (exception.contains("ConnectException") || exception.contains("UnknownHostException")) {
                    errorType = ErrorCodes.CONNECTIVITY_FAILURE;
                } else if (exception.contains("SocketTimeoutException")) {
                    errorType = ErrorCodes.TIMEOUT_FAILURE;
                } else {
                    errorType = ErrorCodes.WEBSERVICE_FAILURE;
                }
                // Print details about the exception
                LogHelper.getInstance().exception(logTag, e, exception);
                // Notifies the controller that there was an error
                listener.onError(errorType);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String requestType = response.request().method();
                byte[] responseStream = response.body().bytes();
                if (response.isSuccessful()) { // status code: [200, 300)
                    // Delegate the concrete manager how it should manage this successful response
                    LogHelper.getInstance().debugResponse(logTag, url, requestType, response.code(), responseStream);
                    manageResponse(url, responseStream, listener);
                } else {
                    // Print details about this failed response
                    LogHelper.getInstance().errorResponse(logTag, url, requestType, response.code(), responseStream);
                    // Notifies the controller that there was an error
                    listener.onError(ErrorCodes.WEBSERVICE_FAILURE);
                }
            }

        };
    }

}
