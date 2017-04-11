package com.ranjan.malav.morselight_flashlightwithmorsecode.activity;

/**
 * Created by Malav on 2/8/2017.
 */

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ranjan.malav.morselight_flashlightwithmorsecode.R;

public class IntroductionFragment extends Fragment {
    private int position;

    public IntroductionFragment() {

    }

    @SuppressLint("ValidFragment")
    public IntroductionFragment(int position) {
        this.position = position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = null;
        switch (position) {
            case 0:
                rootView = (ViewGroup) inflater.inflate(R.layout.activity_introduction_swipe1, container, false);
                break;
            case 1:
                rootView = (ViewGroup) inflater.inflate(R.layout.activity_introduction_swipe2, container, false);
                break;
            case 2:
                rootView = (ViewGroup) inflater.inflate(R.layout.activity_introduction_swipe3, container, false);
                break;
            case 3:
                rootView = (ViewGroup) inflater.inflate(R.layout.activity_introduction_swipe4, container, false);
                break;
            case 4:
                rootView = (ViewGroup) inflater.inflate(R.layout.activity_introduction_swipe5, container, false);
                break;
        }
        return rootView;
    }
}