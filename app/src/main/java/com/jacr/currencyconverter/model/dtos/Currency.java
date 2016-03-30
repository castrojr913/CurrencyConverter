package com.jacr.currencyconverter.model.dtos;

import com.google.gson.annotations.SerializedName;

/**
 * Currency
 * Created by Jesus Castro on 10/10/2015.
 */
public class Currency {

    @SerializedName("rates")
    private Rate rate;

    public Rate getRate() {
        return rate;
    }

}
