package com.jacr.currencyconverter.utilities;

import android.os.Build;
import android.view.ViewTreeObserver;

/**
 * ViewHelper
 * Created by Jesus Castro on 09/10/2015.
 */
public class ViewHelper {

    public static void removeGlobalLayoutListener(ViewTreeObserver observer, ViewTreeObserver.OnGlobalLayoutListener
            listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            observer.removeOnGlobalLayoutListener(listener);
        } else {
            //noinspection deprecation
            observer.removeGlobalOnLayoutListener(listener);
        }
    }

}

