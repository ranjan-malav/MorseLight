package com.ranjan.malav.morselight_flashlightwithmorsecode.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ranjan.malav.morselight_flashlightwithmorsecode.R;

import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.ranjan.malav.morselight_flashlightwithmorsecode.activity.MainActivity.camera;
import static com.ranjan.malav.morselight_flashlightwithmorsecode.activity.MainActivity.flashStatus;
import static com.ranjan.malav.morselight_flashlightwithmorsecode.activity.MainActivity.params;

/**
 * Created by Malav on 1/19/2017.
 */

public class MorseGenerator extends Fragment {
    static final String LOG_TAG = "MorseGenerator";
    static final Handler handler = new Handler();
    static final Handler handler2 = new Handler();
    static final Handler handler3 = new Handler();
    static int y;
    static boolean isChecked = true;
    static Button morse_sos, morse_signal;
    static ImageView morse_generator_background, morse_generator_background_mask;
    static EditText morse_message;
    private final Runnable offRunnable = new Runnable() {
        @Override
        public void run() {
            offFlashlight(getContext());
            morse_generator_background.setImageResource(R.drawable.morse_generator_off);
        }
    };
    private final Runnable onRunnable = new Runnable() {
        @Override
        public void run() {
            onFlashlight();
            morse_generator_background.setImageResource(R.drawable.morse_generator_on);
        }
    };
    public String message;
    int x;
    int z = 0;
    long time;
    private final Runnable colorChangeRunnable = new Runnable() {
        @Override
        public void run() {
            time = System.currentTimeMillis();
            z++;
            String text = morse_message.getText().toString().trim();
            char c;
            if (z < text.length()) {
                c = text.charAt(z);
                if (c == ' ') {
                    z++;
                }
            }
            changeTextColor(z);
        }
    };
    int multiplierA;
    int multiplierB;
    float multiplierString;
    StringBuilder sb;
    ArrayList<Float> timings = new ArrayList<>();
    HashMap<Character, String> hack = new HashMap<Character, String>() {{
        put('A', "1133");
        put('B', "31111113");
        put('C', "31113113");
        put('D', "311113");
        put('E', "13");
        put('F', "11113113");
        put('G', "313113");
        put('H', "11111113");
        put('I', "1113");
        put('J', "11313133");
        put('K', "311133");
        put('L', "11311113");
        put('M', "3133");
        put('N', "3113");
        put('O', "313133");
        put('P', "11313113");
        put('Q', "31311133");
        put('R', "113113");
        put('S', "111113");
        put('T', "33");
        put('U', "111133");
        put('V', "11111133");
        put('W', "113133");
        put('X', "31111133");
        put('Y', "31113133");
        put('Z', "31311113");
        put('0', "3131313133");
        put('1', "1131313133");
        put('2', "1111313133");
        put('3', "1111113133");
        put('4', "1111111133");
        put('5', "1111111113");
        put('6', "3111111113");
        put('7', "3131111113");
        put('8', "3131311113");
        put('9', "3131313113");
        put(' ', "7");
    }};
    HashMap<Character, Integer> back = new HashMap<Character, Integer>() {{
        put('A', 5);
        put('B', 9);
        put('C', 11);
        put('D', 7);
        put('E', 1);
        put('F', 9);
        put('G', 9);
        put('H', 7);
        put('I', 3);
        put('J', 13);
        put('K', 9);
        put('L', 9);
        put('M', 7);
        put('N', 5);
        put('O', 11);
        put('P', 11);
        put('Q', 13);
        put('R', 7);
        put('S', 5);
        put('T', 3);
        put('U', 7);
        put('V', 9);
        put('W', 9);
        put('X', 11);
        put('Y', 13);
        put('Z', 11);
        put('0', 19);
        put('1', 17);
        put('2', 15);
        put('3', 13);
        put('4', 11);
        put('5', 9);
        put('6', 11);
        put('7', 13);
        put('8', 15);
        put('9', 17);
        put(' ', 1);
    }};

    public static void resetAllAtEnd(Context context) {
        changeTextColorBlack();
        isChecked = true;
        offFlashlight(context);
        morse_generator_background.setImageResource(R.drawable.morse_generator_off);
        morse_signal.setClickable(true);
        morse_sos.setClickable(true);
    }

    public static void removeHandlerCallbacks() {
        try {
            handler.removeCallbacksAndMessages(null);
            handler2.removeCallbacksAndMessages(null);
            handler3.removeCallbacksAndMessages(null);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            Log.d(LOG_TAG, npe.toString());
        }
    }

    public static void offFlashlight(Context context) {
        if (camera == null || params == null) {
            Toast.makeText(context, "Couldn't connect to the camera.", Toast.LENGTH_SHORT).show();
            return;
        }
        params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(params);
        camera.stopPreview();
    }

    public static void changeTextColorBlack() {
        Spannable WordtoSpan = morse_message.getText();
        WordtoSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 0, WordtoSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        morse_message.setText(WordtoSpan);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public int getHotspotColor(ImageView mask, int x, int y) {
        mask.setDrawingCacheEnabled(true);
        Bitmap hotspot = Bitmap.createBitmap(mask.getDrawingCache());
        mask.setDrawingCacheEnabled(false);
        return hotspot.getPixel(x, y);
    }

    public boolean closeMatch(int color1, int color2, int tolerance) {
        if ((int) Math.abs(Color.red(color1) - Color.red(color2)) > tolerance)
            return false;
        if ((int) Math.abs(Color.green(color1) - Color.green(color2)) > tolerance)
            return false;
        if ((int) Math.abs(Color.blue(color1) - Color.blue(color2)) > tolerance)
            return false;
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_morse_generator, container, false);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        MainActivity.mWakeLock = MainActivity.pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, LOG_TAG);
        MainActivity.mWakeLock.acquire();

        morse_message = (EditText) rootView.findViewById(R.id.morse_message);
        morse_message.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(morse_message.getWindowToken(), 0);
                }
                return true;
            }

        });
        morse_generator_background = (ImageView) rootView.findViewById(R.id.morse_send);
        morse_generator_background_mask = (ImageView) rootView.findViewById(R.id.morse_send_background);
        morse_generator_background.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                final int action = ev.getAction();
                final int evX = (int) ev.getX();
                final int evY = (int) ev.getY();
                try {
                    time = System.currentTimeMillis();
                    morse_signal.setClickable(false);
                    defineMultipliers();
                    switch (action) {
                        case MotionEvent.ACTION_BUTTON_PRESS:
                        case MotionEvent.ACTION_UP:
                            int touchColor = getHotspotColor(morse_generator_background_mask, evX, evY);
                            int tolerance = 10;
                            if (closeMatch(Color.YELLOW, touchColor, tolerance)) {
                                if (isChecked) {
                                    removeHandlerCallbacks();
                                    isChecked = false;
                                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(
                                            Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(morse_message.getWindowToken(), 0);
                                    message = morse_message.getText().toString().trim().toUpperCase();
                                    char[] charMessage = convertToCharArray(message);
                                    try {
                                        if (charMessage.length != 0) {
                                            playWithFlash(charMessage, multiplierA, multiplierB, 1);
                                        }
                                    } catch (InterruptedException e) {
                                        Log.d(LOG_TAG, e.toString());
                                        e.printStackTrace();
                                    }
                                } else {
                                    z = 0;
                                    removeHandlerCallbacks();
                                    resetAllAtEnd(getContext());
                                    morse_signal.setClickable(true);
                                    changeTextColorBlack();
                                }
                            }
                            break;
                    } // end switch
                    return true;
                } catch (IllegalArgumentException e) {
                    morse_signal.setClickable(true);
                    return false;
                }
            }
        });

        morse_sos = (Button) rootView.findViewById(R.id.button_sos);
        morse_signal = (Button) rootView.findViewById(R.id.button_start_stop);
        morse_signal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                z = 0;
                morse_generator_background.setClickable(false);
                morse_sos.setClickable(false);
                if (isChecked) {
                    removeHandlerCallbacks();
                    morse_generator_background.setImageResource(R.drawable.morse_generator_off);
                    isChecked = false;
                    char[] charMessage = {'E', 'E', 'E'};
                    try {
                        playWithFlash(charMessage, 20, 1, 0);
                    } catch (InterruptedException e) {
                        Log.d(LOG_TAG, e.toString());
                        e.printStackTrace();
                    }
                } else {
                    removeHandlerCallbacks();
                    resetAllAtEnd(getContext());
                    morse_generator_background.setClickable(true);
                    morse_sos.setClickable(true);
                }
            }
        });
        morse_sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morse_message.setText("SOS");
                morse_signal.setClickable(false);

                defineMultipliers();

                if (isChecked) {
                    removeHandlerCallbacks();
                    morse_generator_background.setImageResource(R.drawable.morse_generator_off);
                    isChecked = false;
                    char[] charMessage = {'S', 'O', 'S'};
                    try {
                        playWithFlash(charMessage, multiplierA, multiplierB, 1);
                    } catch (InterruptedException e) {
                        Log.d(LOG_TAG, e.toString());
                        e.printStackTrace();
                    }
                } else {
                    removeHandlerCallbacks();
                    resetAllAtEnd(getContext());
                    morse_signal.setClickable(true);
                }
            }
        });
        return rootView;
    }

    private void defineMultipliers() {
        SharedPreferences sharedPref = getActivity().getPreferences(MODE_PRIVATE);
        multiplierString = sharedPref.getFloat("multiplier", 1.0f);
        if (multiplierString == 0.33f) {
            multiplierA = 3;
            multiplierB = 1;
        } else if (multiplierString == 0.5f) {
            multiplierA = 2;
            multiplierB = 1;
        } else if (multiplierString == 0.75f) {
            multiplierA = 4;
            multiplierB = 3;
        } else if (multiplierString == 1.0f) {
            multiplierA = 1;
            multiplierB = 1;
        }
    }

    public void playWithFlash(char[] charMessage, int A, int B, int flag) throws InterruptedException {
        sb = new StringBuilder();
        timings.clear();
        y = 0;
        z = 0;
        int p = 0;
        for (char c : charMessage) {
            timings.add((float) y / A * B);
            String code = hack.get(c);
            if (c == ' ') {
                timings.remove(p);
                p--;
                sb.replace(sb.length() - 1, sb.length(), code);
            } else {
                sb.append(code);
            }
            int k = back.get(c) + 3;
            y = y + k;
            p++;
        }

        sb.replace(sb.length() - 1, sb.length(), "");
        int delay = 0;
        for (int i = 0; i < sb.length(); i++) {
            x = Integer.parseInt(String.valueOf(sb.charAt(i)));
            if (!flashStatus) {
                float t = (float) delay / A * B;
                if (flag == 1 && timings.contains(t)) {
                    handler3.postDelayed(colorChangeRunnable, delay * 1000 / A * B);
                }
                handler.postDelayed(onRunnable, delay * 1000 / A * B);
                flashStatus = true;
            } else {

                handler2.postDelayed(offRunnable, delay * 1000 / A * B);
                flashStatus = false;
            }
            delay += x;
        }

        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                morse_generator_background.setImageResource(R.drawable.morse_generator_off);
                offFlashlight(getContext());
                isChecked = true;
                morse_signal.setClickable(true);
                morse_sos.setClickable(true);
                morse_generator_background.setClickable(true);
                changeTextColorBlack();
            }
        }, delay * 1000 / A * B);
        flashStatus = false;
    }

    public char[] convertToCharArray(String message) {
        char[] array = new char[message.length()];
        if (message.length() > 0) {
            return message.toCharArray();
        } else {
            Toast.makeText(getContext(), "Please enter some message!", Toast.LENGTH_SHORT).show();
            morse_generator_background.setImageResource(R.drawable.morse_generator_off);
            resetAllAtEnd(getContext());
            morse_signal.setClickable(true);
        }
        return array;
    }

    public void onFlashlight() {
        if (camera == null || params == null) {
            Toast.makeText(getActivity(), "Couldn't connect to the camera.", Toast.LENGTH_SHORT).show();
            return;
        }
        params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(params);
        camera.startPreview();
    }

    public void changeTextColor(int end) {
        Spannable WordtoSpan = morse_message.getText();
        WordtoSpan.setSpan(new ForegroundColorSpan(Color.CYAN), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        morse_message.setText(WordtoSpan);
    }

}
