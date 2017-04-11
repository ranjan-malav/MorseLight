package com.ranjan.malav.morselight_flashlightwithmorsecode.activity;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Malav on 2/9/2017.
 */

public class IntroManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;

    IntroManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences("first", 0);
        editor = pref.edit();
    }

    public void setFirst(boolean isFirst) {
        editor.putBoolean("check", isFirst);
        editor.apply();
    }

    public boolean check() {
        return pref.getBoolean("check", true);
    }
}
