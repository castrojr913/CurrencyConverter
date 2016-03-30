package com.jacr.currencyconverter.model.managers;

import com.jacr.currencyconverter.model.ApiUrl;
import com.jacr.currencyconverter.model.ErrorCodes;
import com.jacr.currencyconverter.model.HttpClient;
import com.jacr.currencyconverter.model.dtos.Currency;
import com.jacr.currencyconverter.model.managers.listeners.CurrencyManagerListener;
import com.jacr.currencyconverter.model.managers.listeners.ManagerListener;
import com.jacr.currencyconverter.utilities.LogHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * CurrencyManager
 * Created by Jesus Castro on 09/10/2015.
 */
public class CurrencyManager extends Manager {

    private static CurrencyManager singleton;

    private CurrencyManager() {
        // Blank
    }

    public static CurrencyManager getInstance() {
        if (singleton == null) {
            singleton = new CurrencyManager();
        }
        return singleton;
    }

    //<editor-fold desc="Manager Overrides">

    @Override
    protected Class<?> getLogTag() {
        return CurrencyManager.class;
    }

    @Override
    protected void manageResponse(String url, byte[] response, final ManagerListener listener) {
        try {
            if (url.equals(ApiUrl.CURRENCY) && listener instanceof CurrencyManagerListener) {
                Currency dto = gson.fromJson(new String(response), Currency.class);
                ((CurrencyManagerListener) listener).onSuccess(dto.getRate());
            } else {
                listener.onError(ErrorCodes.WEBSERVICE_FAILURE);
            }
        } catch (Exception e) {
            LogHelper.getInstance().exception(getLogTag(), e, e.toString());
            listener.onError(ErrorCodes.WEBSERVICE_FAILURE);
        }
    }

    //</editor-fold>

    //<editor-fold desc="Currency Rate">

    public void getCurrencyRatesAsToUSD(ManagerListener listener) {
        getCurrencyRates("USD", listener);
    }

    private void getCurrencyRates(final String currencyCode, ManagerListener listener) {
        String url = ApiUrl.CURRENCY;
        Map<String, String> parameters = new HashMap<String, String>() {{
            put("base", currencyCode);
        }};
        LogHelper.getInstance().debugRequest(getLogTag(), url, parameters.toString());
        HttpClient.getInstance().get(url, parameters, createHttpCallback(url, listener));
    }

    //</editor-fold>

}
