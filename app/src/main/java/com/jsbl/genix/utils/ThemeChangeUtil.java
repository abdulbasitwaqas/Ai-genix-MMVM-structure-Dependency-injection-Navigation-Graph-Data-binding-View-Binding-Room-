package com.jsbl.genix.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.jsbl.genix.R;

import static android.content.Context.MODE_PRIVATE;


public class ThemeChangeUtil {

    static final String THEME_PREF = "themePrefs";

    public static void changeToTheme(Activity activity) {
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
        activity.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        if (getTheme(activity) == 0) {
            activity.setTheme(R.style.Theme_MyApp);
        } else {
            activity.setTheme(R.style.Theme_MyApp_dark);
        }
    }

    public static void setThemePref(Context context, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(THEME_PREF, MODE_PRIVATE).edit();
        editor.putInt(THEME_PREF, value);
        editor.apply();
    }

    public static int getTheme(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(THEME_PREF, MODE_PRIVATE);
        return prefs.getInt(THEME_PREF, 0);
    }

}