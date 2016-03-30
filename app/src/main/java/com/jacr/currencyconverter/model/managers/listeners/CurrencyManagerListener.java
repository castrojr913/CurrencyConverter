package com.jacr.currencyconverter.model.managers.listeners;

import com.jacr.currencyconverter.model.dtos.Rate;

/**
 * CurrencyManagerListener
 * Created by jesus on 10/10/2015.
 */
public interface CurrencyManagerListener extends ManagerListener {

    void onSuccess(Rate rate);

}


