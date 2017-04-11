package com.ranjan.malav.morselight_flashlightwithmorsecode.activity;

/**
 * Created by Malav on 1/19/2017.
 */

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.ranjan.malav.morselight_flashlightwithmorsecode.R;

import static com.ranjan.malav.morselight_flashlightwithmorsecode.activity.MainActivity.camera;
import static com.ranjan.malav.morselight_flashlightwithmorsecode.activity.MainActivity.flashStatus;
import static com.ranjan.malav.morselight_flashlightwithmorsecode.activity.MainActivity.params;

public class Flashlight extends Fragment {
    static ImageView flashlight_background;
    String LOG_TAG = "Flashlight_activity";
    static Context context;
    public Flashlight() {
    }

    public static void turnOffFlash() {
        if (flashStatus) {
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            flashStatus = false;
            toggleButtonImage();
        }
    }

    private static void toggleButtonImage() {
        if (flashStatus) {
            flashlight_background.setImageResource(R.drawable.flashlight_background_on);
        } else {
            flashlight_background.setImageResource(R.drawable.flashlight_background_off);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_flashlight, container, false);
        flashlight_background = (ImageView) rootView.findViewById(R.id.flashlight_background);
        Button button = (Button) rootView.findViewById(R.id.flashlight_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashStatus) {
                    turnOffFlash();
                } else {
                    turnOnFlash();
                }
            }
        });
        return rootView;
    }

    public void turnOnFlash() {
        if (!flashStatus) {
            if (camera == null || params == null) {
                return;
            }
            params = camera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            flashStatus = true;
            toggleButtonImage();
        }
    }

}