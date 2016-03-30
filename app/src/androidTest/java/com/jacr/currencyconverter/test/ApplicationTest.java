package com.jacr.currencyconverter.test;

import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.espresso.action.ViewActions;
import android.test.ActivityInstrumentationTestCase2;

import com.jacr.currencyconverter.R;
import com.jacr.currencyconverter.controllers.DrawerActivity_;
import com.jacr.currencyconverter.test.utilities.helpers.LogHelper;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * ApplicationTest
 * <p/>
 * Note: The class pointed out, in the generic parameter of ActivityInstrumentationTestCase2 and
 * constructor of this class, is the initial point of all application tests.
 * <p/>
 * Created by Jesus Castro on 10/11/2015.
 */
public abstract class ApplicationTest extends ActivityInstrumentationTestCase2<DrawerActivity_> {

    //<editor-fold desc="Constants & Variables">

    /*
     * This ID is in order to identify the positive button of the library for dialogs
     * (com.afollestad:material-dialogs)
     */
    protected static final int DIALOG_POSITIVE_BUTTON_ID = R.id.buttonDefaultPositive;

    /*
    * Because of the introduction of Toolbar component (since Support Library v21),
    * We can't call this view  with its ID number, instead of, we use its description "Navigate up".
    * http://stackoverflow.com/questions/27527988/how-do-i-test-the-home-button-on-the-action-bar-with-espresso
    */
    private static final String ACTION_BAR_HOME_BUTTON_ID = "Navigate up";

    private static final String MOCK_SERVER_CONTENT_TYPE = "Content-type: application/json";

    //</editor-fold>

    public MockWebServer proxy;

    protected ApplicationTest() {
        super(DrawerActivity_.class);
    }

    public abstract Class<?> getLogTag();

    //<editor-fold desc="Overrides">

    @Override
    public void setUp() throws Exception {
        super.setUp();
        //injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        getActivity(); // start activity (test)
    }

    @Override
    protected void tearDown() throws Exception {
        if (proxy != null) {
            try {
                proxy.shutdown();
            } catch (Exception exception) {
                LogHelper.getInstance().error(getLogTag(), exception);
            }
        }
        super.tearDown();
    }

    //</editor-fold>


    //<editor-fold desc="Useful Methods for tests">

    public static MockResponse createServerResponse(int statusCode, String responseBody) {
        return new MockResponse().setResponseCode(statusCode).setBody(responseBody).addHeader(MOCK_SERVER_CONTENT_TYPE);
    }

    protected String readTestFile(String filePath) {
        StringBuilder out = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    getInstrumentation().getContext().getResources().getAssets().open(filePath)));
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
                out.append(System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            LogHelper.getInstance().error(getLogTag(), e);
        } finally {
            return out.toString();
        }
    }

    protected void checkIfDialogAppears() {
        onView(withId(DIALOG_POSITIVE_BUTTON_ID)).check(matches(isDisplayed()));
        onView(withId(DIALOG_POSITIVE_BUTTON_ID)).perform(ViewActions.click());
    }

    protected ViewAction slideOnScreenTowardsLeft() {
        return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_LEFT,
                GeneralLocation.CENTER_RIGHT, Press.FINGER);
    }

    // </editor-fold>

}
