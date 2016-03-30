package com.jacr.currencyconverter.controllers;

import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewTreeObserver;

import com.jacr.currencyconverter.R;
import com.jacr.currencyconverter.utilities.ViewHelper;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.ColorRes;
import org.androidannotations.annotations.res.DrawableRes;
import org.androidannotations.annotations.res.StringRes;

import java.util.ArrayList;

/**
 * DrawerActivity
 * Created by Jesus Castro on 09/10/2015.
 */
@EActivity(R.layout.activity_drawer)
public class DrawerActivity extends AppCompatActivity {

    //<editor-fold desc="Constants & Variables">

    private static final int DRAWER_ITEM_CURRENCY = 1;
    private static final int DRAWER_ITEM_EXIT = 2;

    //</editor-fold>

    //<editor-fold desc="View Instances">

    @ViewById(R.id.activity_drawer_toolbar)
    Toolbar toolbar;

    //</editor-fold>

    //<editor-fold desc="String Resources">

    @StringRes(R.string.activity_drawer_item_currency)
    String currencyItemName;

    @StringRes(R.string.activity_drawer_item_exit)
    String exitItemName;

    //</editor-fold>

    //<editor-fold desc="Drawable & Color Resources">

    @DrawableRes(R.drawable.drawer_header_background)
    Drawable headerBackground;

    @DrawableRes(R.drawable.ic_launcher)
    Drawable headerIcon;

    @ColorRes(R.color.app_drawer_text_selected)
    int drawerSelectedText;

    //</editor-fold>

    @AfterViews
    void init() {
        // Setting up about Action Bar
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        // Build and set listeners for drawer items
        setupDrawer();
    }

    //<editor-fold desc="Drawer Management">

    private void setupDrawer() {
        // Header
        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(headerBackground)
                .withSelectionListEnabled(false)
                .addProfiles(new ProfileDrawerItem().withIcon(headerIcon))
                .build();
        // Creating a list as for drawer items
        ArrayList<IDrawerItem> drawerItems = new ArrayList<>();
        drawerItems.add(new PrimaryDrawerItem()
                .withIdentifier(DRAWER_ITEM_CURRENCY)
                .withName(currencyItemName)
                .withTag(currencyItemName)
                .withSelectedTextColor(drawerSelectedText));
        drawerItems.add(new PrimaryDrawerItem()
                .withIdentifier(DRAWER_ITEM_EXIT)
                .withName(exitItemName)
                .withTag(exitItemName)
                .withSelectedTextColor(drawerSelectedText));
        // Building the drawer
        final Drawer drawer = new DrawerBuilder()
                .withActivity(this)
                .withActionBarDrawerToggle(true)
                .withToolbar(toolbar)
                .withAccountHeader(header)
                .withDrawerItems(drawerItems)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem drawerItem) {
                        loadFragmentAsToDrawerItem(drawerItem);
                        return false;
                    }
                }).build();
        // Load the default view with respect to the drawer. Thus, we add an observer that notifies
        // when the drawer is rendered
        drawer.getContent().getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        drawer.setSelection(DRAWER_ITEM_CURRENCY);
                        // Remove observer
                        ViewHelper.removeGlobalLayoutListener(drawer.getContent().getViewTreeObserver(), this);
                    }

                });
    }

    private void loadFragmentAsToDrawerItem(IDrawerItem drawerItem) {
        if (drawerItem.getIdentifier() == DRAWER_ITEM_EXIT) {
            finish();
        } else {
            setTitle(drawerItem.getTag().toString()); // Show title in regard to drawer item on Action Bar
            Fragment fragment = drawerItem.getIdentifier() == DRAWER_ITEM_CURRENCY ? new CurrencyFragment_() : new Fragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.activity_drawer_view_fragment, fragment);
            transaction.commit();
        }
    }

    //</editor-fold>

}

