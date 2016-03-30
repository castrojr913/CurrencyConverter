package com.jacr.currencyconverter.model;

import com.jacr.currencyconverter.BuildConfig;

/**
 * ApiUrl
 * Created by Jesus Castro on 09/10/2015.
 */
public class ApiUrl {

    private static final String API_HOST = BuildConfig.TEST_ENABLE_STUBS_PROXY ?
            "http://localhost:" + BuildConfig.TEST_STUBS_PROXY_PORT : "http://api.fixer.io";
    public static final String CURRENCY = API_HOST + "/latest";

}
