package com.jacr.currencyconverter.controllers;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.Button;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.jacr.currencyconverter.R;
import com.jacr.currencyconverter.model.ErrorCodes;
import com.jacr.currencyconverter.model.dtos.Rate;
import com.jacr.currencyconverter.model.managers.CurrencyManager;
import com.jacr.currencyconverter.model.managers.listeners.CurrencyManagerListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.EditorAction;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;

/**
 * CurrencyFragment
 * Created by Jesus Castro on 09/10/2015.
 */
@EFragment(R.layout.fragment_currency)
public class CurrencyFragment extends Fragment {

    //<editor-fold desc="Variables & Constants">

    private static final int BAR_CHART_ANIMATION_TIME = 1500; // ms
    private static final String BAR_CHART_Y_AXIS_FORMAT = "$ %.0f";
    private static final String CURRENCY_VALUES_FORMAT = "%s :   %.2f"; // Title - Value

    //</editor-fold>

    //<editor-fold desc="Views Instances">

    @ViewById(R.id.fragment_currency_edittext_quantity)
    MaterialEditText quantityEditText;

    @ViewById(R.id.fragment_currency_text_euro)
    TextView euroTextView;

    @ViewById(R.id.fragment_currency_text_yen)
    TextView yenTextView;

    @ViewById(R.id.fragment_currency_text_pound)
    TextView poundTextView;

    @ViewById(R.id.fragment_currency_text_reais)
    TextView reaisTextView;

    @ViewById(R.id.fragment_currency_button_calculate)
    Button calculateButton;

    @ViewById(R.id.fragment_currency_view_progressbar)
    View progressBarView;

    @ViewById(R.id.fragment_currency_layout_currencies)
    RelativeLayout currenciesLayout;

    @ViewById(R.id.fragment_currency_bar_chart)
    BarChart barChart;

    //</editor-fold>

    //<editor-fold desc="String Resources">

    @StringRes(R.string.dialog_error_title)
    String dialogErrorTitle;

    @StringRes(R.string.dialog_error_button_ok)
    String dialogErrorButtonOk;

    @StringRes(R.string.error_connectivity)
    String connectivityError;

    @StringRes(R.string.error_timeout)
    String timeoutError;

    @StringRes(R.string.error_webservice)
    String webserviceError;

    @StringRes(R.string.error_number_negative)
    String negativeNumberError;

    @StringRes(R.string.fragment_currency_value_euro)
    String euroValueText;

    @StringRes(R.string.fragment_currency_value_yen)
    String yenValueText;

    @StringRes(R.string.fragment_currency_value_uk_pound)
    String poundValueText;

    @StringRes(R.string.fragment_currency_value_reais)
    String reaisValueText;

    @StringRes(R.string.fragment_currency_progressbar_text)
    String progressBarText;

    @StringRes(R.string.fragment_currency_bar_chart_legend)
    String barGraphLegendText;

    //</editor-fold>

    @AfterViews
    void init() {
        TextView progressBarTextView = (TextView) progressBarView.findViewById(R.id.progressbar_text);
        progressBarTextView.setText(progressBarText);
        setupBarChart();
    }

    //<editor-fold desc="Events Handling">

    @EditorAction(R.id.fragment_currency_edittext_quantity)
    public boolean calculateCurrencies(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            calculateCurrencies();
        }
        // It indicates IME action was consumed: false -> hide keyboard.
        return false;
    }


    @Click(R.id.fragment_currency_button_calculate)
    public void calculateCurrencies() {
        String quantityString = quantityEditText.getText().toString();
        final int quantity = quantityString.isEmpty() ? 0 : Integer.parseInt(quantityString);
        if (quantity < 0) {
            showErrorDialog(negativeNumberError);
        } else {
            setProgressBarVisibility(true);
            CurrencyManager.getInstance().getCurrencyRatesAsToUSD(new CurrencyManagerListener() {

                @Override
                public void onSuccess(Rate rate) {
                    updateCurrenciesValues(quantity, rate);
                }

                @Override
                public void onError(int errorCode) {
                    // There was an problem in regard to webservice, so let's show a error message
                    // with a dialog
                    showErrorMessageAsForApi(errorCode);
                }

            });
        }
    }

    //</editor-fold>

    private void setProgressBarVisibility(boolean isVisible) {
        progressBarView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        currenciesLayout.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }

    //<editor-fold desc="Showing Currencies Values">

    @UiThread
    void updateCurrenciesValues(int quantity, Rate rate) {
        setProgressBarVisibility(false);
        float[] currenciesValues = {quantity * rate.getEuro(), quantity * rate.getYen(),
                quantity * rate.getPound(), quantity * rate.getReais()};
        // Show currencies values
        euroTextView.setText(String.format(CURRENCY_VALUES_FORMAT, euroValueText, currenciesValues[0]));
        yenTextView.setText(String.format(CURRENCY_VALUES_FORMAT, yenValueText, currenciesValues[1]));
        poundTextView.setText(String.format(CURRENCY_VALUES_FORMAT, poundValueText, currenciesValues[2]));
        reaisTextView.setText(String.format(CURRENCY_VALUES_FORMAT, reaisValueText, currenciesValues[3]));
        // Update Bar Chart with the new values
        setBarChartData(currenciesValues);
    }

    private void setupBarChart() {
        // Setting up Bar Chart
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);
        barChart.getAxisLeft().setStartAtZero(true); // Useful so that animation starts at zero
        barChart.getAxisRight().setEnabled(false); // Hide Y-Axis on right side
        // Setting up Bar Axises
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setLabelCount(6, false);
        leftAxis.setSpaceTop(15f);
        // It formats the values which we want to show on Y Axis
        leftAxis.setValueFormatter(new YAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return String.format(BAR_CHART_Y_AXIS_FORMAT, value);
            }

        });
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        // Setting up legend
        Legend l = barChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
    }

    private void setBarChartData(float[] dataArray) {
        // Creating data list for Y Axis
        ArrayList<BarEntry> yAxisValues = new ArrayList<>();
        for (int i = 0; i < dataArray.length; i++) {
            yAxisValues.add(new BarEntry(dataArray[i], i));
        }
        // Let's name the data in order to show it in the legend of the chart
        final BarDataSet yAxisBarDataSet = new BarDataSet(yAxisValues, barGraphLegendText);
        yAxisBarDataSet.setBarSpacePercent(35f);
        // Creating instance to contain the data for both axises, X and Y
        BarData data = new BarData(new ArrayList<String>() {{
            add(euroValueText);
            add(yenValueText);
            add(poundValueText);
            add(reaisValueText);
        }}, new ArrayList<BarDataSet>() {{
            add(yAxisBarDataSet);
        }});
        data.setValueTextSize(10f);
        // Let's draw the chart
        barChart.setData(data);
        barChart.animateY(BAR_CHART_ANIMATION_TIME);
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }

    //</editor-fold>

    //<editor-fold desc="Error Messages">

    @UiThread
    void showErrorMessageAsForApi(int errorCode) {
        setProgressBarVisibility(false);
        String message = (errorCode == ErrorCodes.CONNECTIVITY_FAILURE ? connectivityError :
                (errorCode == ErrorCodes.TIMEOUT_FAILURE) ? timeoutError : webserviceError);
        showErrorDialog(message);
    }

    private void showErrorDialog(String message) {
        new MaterialDialog.Builder(getContext())
                .title(dialogErrorTitle)
                .content(message)
                .positiveText(dialogErrorButtonOk)
                .cancelable(false)
                .show();
    }

    //</editor-fold>

}
