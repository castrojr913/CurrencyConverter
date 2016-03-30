package com.jacr.currencyconverter.model.dtos;

import com.google.gson.annotations.SerializedName;

/**
 * Rate
 * Created by Jesus Castro on 10/10/2015.
 */
public class Rate {

    @SerializedName("GBP")
    private float pound;

    @SerializedName("EUR")
    private float euro;

    @SerializedName("JPY")
    private float yen;

    @SerializedName("BRL")
    private float reais;

    //<editor-fold desc="Getters">

    public float getPound() {
        return convertNaturalNumber(pound);
    }

    public float getEuro() {
        return convertNaturalNumber(euro);
    }

    public float getYen() {
        return convertNaturalNumber(yen);
    }

    public float getReais() {
        return convertNaturalNumber(reais);
    }

    //</editor-fold>

    private float convertNaturalNumber(float number) {
        return number < 0 ? 0 : number;
    }

}
