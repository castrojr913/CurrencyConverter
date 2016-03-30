package com.jacr.currencyconverter.test.currency;

import com.jacr.currencyconverter.R;
import com.jacr.currencyconverter.model.ErrorCodes;
import com.jacr.currencyconverter.model.dtos.Rate;
import com.jacr.currencyconverter.model.managers.CurrencyManager;
import com.jacr.currencyconverter.model.managers.listeners.CurrencyManagerListener;
import com.jacr.currencyconverter.model.managers.listeners.ManagerListener;
import com.jacr.currencyconverter.test.ApplicationTest;
import com.jacr.currencyconverter.test.BuildConfig;
import com.jacr.currencyconverter.test.utilities.constants.ApiResponseStubs;
import com.jacr.currencyconverter.test.utilities.constants.PossibleValues;
import com.jacr.currencyconverter.test.utilities.helpers.LogHelper;
import com.jacr.currencyconverter.test.utilities.helpers.TimeHelper;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.hamcrest.Matchers;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * CurrencyTest
 * Created by Jesus Castro on 10/11/2015.
 */
public class CurrencyTest extends ApplicationTest {

    @Override
    public Class<?> getLogTag() {
        return CurrencyTest.class;
    }

    //<editor-fold desc="Black Box Tests">

    private void typeQuantityToConvert(String textToType) {
        onView(withId(R.id.fragment_currency_edittext_quantity)).perform(typeText(textToType));
        onView(withId(R.id.fragment_currency_edittext_quantity)).perform(pressImeActionButton());
    }

    private void checkIfThereAreCurrenciesResults() {
        // Does it show up a result in regard to the query about the currency rate?
        onView(withId(R.id.fragment_currency_layout_currencies)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_currency_bar_chart)).check(matches(isDisplayed()));
    }

    public void testCheckExitOnDrawerPanel() {
        onView(withId(android.R.id.content)).perform(slideOnScreenTowardsLeft());
        TimeHelper.pause();
        onView(withText("Exit")).check(matches(isDisplayed()));
        onView(withText("Exit")).perform(click());
    }

    public void testCalculateRateWithNumericalCharacters() {
        try {
            proxy = new MockWebServer();
            proxy.enqueue(createServerResponse(200, readTestFile(ApiResponseStubs.CURRENCY_SUCCESS)));
            proxy.start(BuildConfig.TEST_STUBS_PROXY_PORT);
            typeQuantityToConvert(PossibleValues.CURRENCY_USD_VALUE_1);
            checkIfThereAreCurrenciesResults();
        } catch (Exception exception) {
            LogHelper.getInstance().error(getLogTag(), exception);
        }
    }

    public void testCalculateRateWithNonNumericalCharacters() {
        try {
            proxy = new MockWebServer();
            proxy.enqueue(createServerResponse(200, readTestFile(ApiResponseStubs.CURRENCY_SUCCESS)));
            proxy.start(BuildConfig.TEST_STUBS_PROXY_PORT);
            typeQuantityToConvert(PossibleValues.CURRENCY_USD_VALUE_2);
            // When the user types non-numerical characters, the EditText must be empty.
            onView(withId(R.id.fragment_currency_edittext_quantity)).check(matches(withText(Matchers.isEmptyOrNullString())));
            checkIfThereAreCurrenciesResults();
        } catch (Exception exception) {
            LogHelper.getInstance().error(getLogTag(), exception);
        }
    }

    public void testCalculateRateWithNegativeQuantity() {
        try {
            proxy = new MockWebServer();
            proxy.enqueue(createServerResponse(200, readTestFile(ApiResponseStubs.CURRENCY_SUCCESS)));
            proxy.start(BuildConfig.TEST_STUBS_PROXY_PORT);
            typeQuantityToConvert(PossibleValues.CURRENCY_USD_VALUE_3);
            checkIfDialogAppears(); // It must show up a dialog with a message
        } catch (Exception exception) {
            LogHelper.getInstance().error(getLogTag(), exception);
        }
    }

    public void testBadFormedResponseFromApi() {
        try {
            proxy = new MockWebServer();
            proxy.enqueue(createServerResponse(200, readTestFile(ApiResponseStubs.CURRENCY_BAD_FORMED)));
            proxy.start(BuildConfig.TEST_STUBS_PROXY_PORT);
            typeQuantityToConvert(PossibleValues.CURRENCY_USD_VALUE_1);
            checkIfDialogAppears();
        } catch (Exception exception) {
            LogHelper.getInstance().error(getLogTag(), exception);
        }
    }

    public void testBadRequestResponseFromApi() {
        try {
            proxy = new MockWebServer();
            proxy.enqueue(createServerResponse(404, ""));
            proxy.start(BuildConfig.TEST_STUBS_PROXY_PORT);
            typeQuantityToConvert(PossibleValues.CURRENCY_USD_VALUE_1);
            checkIfDialogAppears();
        } catch (Exception exception) {
            LogHelper.getInstance().error(getLogTag(), exception);
        }
    }

    public void testServerErrorResponseFromApi() {
        try {
            proxy = new MockWebServer();
            proxy.enqueue(createServerResponse(500, ""));
            proxy.start(BuildConfig.TEST_STUBS_PROXY_PORT);
            typeQuantityToConvert(PossibleValues.CURRENCY_USD_VALUE_1);
            checkIfDialogAppears();
        } catch (Exception exception) {
            LogHelper.getInstance().error(getLogTag(), exception);
        }
    }

    //</editor-fold>

    //<editor-fold desc="White Box Tests">

    private ManagerListener currencyManagerListener = new CurrencyManagerListener() {
        @Override
        public void onSuccess(Rate rate) {
            assertNotNull(rate);
            assertTrue(rate.getEuro() >= 0 && rate.getReais() >= 0 && rate.getPound() >= 0 && rate.getYen() >= 0);
        }

        @Override
        public void onError(int errorCode) {
            // Error code, from Api, must be among these values
            assertTrue((errorCode == ErrorCodes.TIMEOUT_FAILURE || errorCode == ErrorCodes.CONNECTIVITY_FAILURE
                    || errorCode == ErrorCodes.WEBSERVICE_FAILURE));
        }
    };

    public void testCheckErrorCodesReturnedByModel() {
        try {
            proxy = new MockWebServer();
            proxy.enqueue(createServerResponse(404, ""));
            proxy.start(BuildConfig.TEST_STUBS_PROXY_PORT);
            CurrencyManager.getInstance().getCurrencyRatesAsToUSD(currencyManagerListener);
        } catch (Exception exception) {
            LogHelper.getInstance().error(getLogTag(), exception);
        }
    }

    public void testCheckRateFormatReturnedByModel() {
        try {
            proxy = new MockWebServer();
            proxy.enqueue(createServerResponse(200, ApiResponseStubs.CURRENCY_SUCCESS));
            proxy.start(BuildConfig.TEST_STUBS_PROXY_PORT);
            CurrencyManager.getInstance().getCurrencyRatesAsToUSD(currencyManagerListener);
        } catch (Exception exception) {
            LogHelper.getInstance().error(getLogTag(), exception);
        }
    }

    //</editor-fold>

}
