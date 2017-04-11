package com.ranjan.malav.morselight_flashlightwithmorsecode.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.ranjan.malav.morselight_flashlightwithmorsecode.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.ranjan.malav.morselight_flashlightwithmorsecode.activity.MainActivity.camera;
import static com.ranjan.malav.morselight_flashlightwithmorsecode.activity.MainActivity.flashStatus;
import static com.ranjan.malav.morselight_flashlightwithmorsecode.activity.MainActivity.params;

/**
 * Created by Malav on 1/19/2017.
 */

public class MorseDecoder extends Fragment {
    static long dit, dah, code, letter, word;
    static long avgOnMin = 0L;
    static long avgOnMax = 0L;
    static long avgOffMin = 0L;
    static long avgOffMax = 0L;
    static long avgOffMid = 0L;
    static int whichCase;
    static double indices1A = 0.0, indices1B = 0.0, indices1C = 0.0; //offMin/onMin, offMid/onMin, offMax/onMin
    static double indices2A = 0.0, indices2B = 0.0, indices2C = 0.0;
    static StringBuilder sb = new StringBuilder();
    static ArrayList<Integer> decodedTimings = new ArrayList<>();
    static ArrayList<Integer> onCodes = new ArrayList<>();
    static ArrayList<Integer> offCodes = new ArrayList<>();
    static ArrayList<Character> onDecoded = new ArrayList<>();
    static ArrayList<Character> offDecoded = new ArrayList<>();
    static HashMap<String, Character> hack = new HashMap<String, Character>() {{
        put(".,-", 'A');
        put("-,.,.,.", 'B');
        put("-,.,-,.", 'C');
        put("-,.,.", 'D');
        put(".", 'E');
        put(".,.,-,.", 'F');
        put("-,-,.", 'G');
        put(".,.,.,.", 'H');
        put(".,.", 'I');
        put(".,-,-,-", 'J');
        put("-,.,-", 'K');
        put(".,-,.,.", 'L');
        put("-,-", 'M');
        put("-,.", 'N');
        put("-,-,-", 'O');
        put(".,-,-,.", 'P');
        put("-,-,.,-", 'Q');
        put(".,-,.", 'R');
        put(".,.,.", 'S');
        put("-", 'T');
        put(".,.,-", 'U');
        put(".,.,.,-", 'V');
        put(".,-,-", 'W');
        put("-,.,.,-", 'X');
        put("-,.,-,-", 'Y');
        put("-,-,.,.", 'Z');
        put("-,-,-,-,-", '0');
        put(".,-,-,-,-", '1');
        put(".,.,-,-,-", '2');
        put(".,.,.,-,-", '3');
        put(".,.,.,.,-", '4');
        put(".,.,.,.,.", '5');
        put("-,.,.,.,.", '6');
        put("-,-,.,.,.", '7');
        put("-,-,-,.,.", '8');
        put("-,-,-,-,.", '9');
        put("/", ' ');
    }};
    static ArrayList<Integer> timings = new ArrayList<>();
    final Handler handler = new Handler();
    final Handler handler2 = new Handler();
    int x;
    ImageView background, morse_decoder_mask;
    private final Runnable offRunnable = new Runnable() {
        @Override
        public void run() {
            offFlashlight();
            background.setImageResource(R.drawable.morse_decoder_off);
        }
    };
    private final Runnable onRunnable = new Runnable() {
        @Override
        public void run() {
            onFlashlight();
            background.setImageResource(R.drawable.morse_decoder_on);
        }
    };
    boolean isChecked = true;
    private String LOG_TAG = "Receiver";

    public MorseDecoder() {

    }

    public static void resetAll() {
        whichCase = 0;
        timings.clear();
        decodedTimings.clear();
        onCodes.clear();
        offCodes.clear();
        onDecoded.clear();
        offDecoded.clear();
        sb.delete(0, sb.length());
        avgOnMin = 0L;
        avgOnMax = 0L;
        avgOffMin = 0L;
        avgOffMax = 0L;
        avgOffMid = 0L;
        indices1A = indices1B = indices1C = indices2A = indices2B = indices2C = 0;
    }

    public static void findIndices() {
        if (avgOffMin == 0L) whichCase = 15; //E or T only
        if (avgOffMin != 0L) {
            indices1A = indices2A = (double) avgOffMin / avgOnMin;
            indices1A = roundIndices(indices1A);
            indices2A = roundIndices(indices2A);
        }
        if (avgOffMid != 0L) {
            indices1B = indices2B = (double) avgOffMid / avgOnMin;
            indices1B = roundIndices(indices1B);
            indices2B = roundIndices(indices2B);
        }
        if (avgOffMax != 0L) {
            indices1C = indices2C = (double) avgOffMax / avgOnMin;
            indices1C = roundIndices(indices1C);
            indices2C = roundIndices(indices2C);
        }
    }

    public static double roundIndices(double number) {
        if (avgOnMax != 0) {
            if (number > 0 && number <= 1.5) return 1;
            if (number > 1.5 && number <= 5) return 3;
            else return 7;
        } else {
            if (number <= 0.66) return 0.33;
            if (number > 0.66 && number <= 1.66) return 1;
            if (number > 1.66 && number <= 2.66) return 2.33;
            if (number > 2.66 && number < 5) return 3;
            else return 7;
        }
    }

    public static void findCaseAndDefineUnits() { //3/1 = 1; 7/1 =1; 7/3 = 2.33

        if (avgOnMax != 0) {
            if (indices2A == 1 && indices2B == 0 && indices2C == 0) {
                whichCase = 17;
                dit = avgOnMin;
                dah = avgOnMax;
                code = avgOffMin;
            }
            if (indices2A == 3 && indices2B == 0 && indices2C == 0) {
                whichCase = 18;
                dit = avgOnMin;
                dah = avgOnMax;
                letter = avgOffMin;
            }
            if (indices2A == 7 && indices2B == 0 && indices2C == 0) {
                whichCase = 19;
                dit = avgOnMin;
                dah = avgOnMax;
                word = avgOffMin;
            }
            if (indices2A == 1 && indices2B == 3 && indices2C == 0) {
                whichCase = 20;
                dit = avgOnMin;
                dah = avgOnMax;
                code = avgOffMin;
                word = avgOffMid;
            }
            if (indices2A == 1 && indices2B == 7 && indices2C == 0) {
                whichCase = 21;
                dit = avgOnMin;
                dah = avgOnMax;
                code = avgOffMin;
                letter = avgOffMax;
            }
            if (indices2A == 3 && indices2B == 7 && indices2C == 0) {
                whichCase = 22;
                dit = avgOnMin;
                dah = avgOnMax;
                word = avgOffMin;
                letter = avgOffMid;
            }
            if (indices2A != 0 && indices2B != 0 && indices2C != 0) {
                whichCase = 16;
                dit = avgOnMin;
                dah = avgOnMax;
                code = avgOffMin;
                letter = avgOffMid;
                word = avgOffMax;
            }
        } else {
            if (indices1A == 1 && indices1B == 0 && indices1C == 0) {
                whichCase = 1;
                dit = avgOnMin;
                code = avgOffMin;
            }
            if (indices1A == 3 && indices1B == 0 && indices1C == 0) {
                whichCase = 2;
                dit = avgOnMin;
                letter = avgOffMin;
            }
            if (indices1A == 7 && indices1B == 0 && indices1C == 0) {
                whichCase = 3;
                dit = avgOnMin;
                word = avgOffMin;
            }
            if (indices1A == 1 && indices1B == 3 && indices1C == 0) {
                whichCase = 4;
                dit = avgOnMin;
                code = avgOffMin;
                letter = avgOffMid;
            }
            if (indices1A == 1 && indices1B == 0 && indices1C == 7) {
                whichCase = 5;
                dit = avgOnMin;
                code = avgOffMin;
                word = avgOffMax;
            }
            if (indices1A == 3 && indices1B == 7 && indices1C == 0) {
                whichCase = 6;
                dit = avgOnMin;
                letter = avgOffMin;
                word = avgOffMid;
            }
            if (indices1A == 1 && indices1B == 3 && indices1C == 7) {
                whichCase = 7;
                dit = avgOnMin;
                code = avgOffMin;
                letter = avgOffMid;
                word = avgOffMax;
            }
            if (indices1A == 0.33 && indices1B == 0 && indices1C == 0) {
                whichCase = 8;
                dah = avgOnMin;
                code = avgOffMin;
            }
            if (indices1A == 2.33 && indices1B == 0 && indices1C == 0) {
                whichCase = 10;
                dah = avgOnMin;
                word = avgOffMin;
            }
            if (indices1A == 0.33 && indices1B == 1 && indices1C == 0) {
                whichCase = 11;
                dah = avgOnMin;
                code = avgOffMin;
                letter = avgOffMid;
            }
            if (indices1A == 0.33 && indices1B == 0 && indices1C == 2.33) {
                whichCase = 12;
                dah = avgOnMin;
                code = avgOffMin;
                word = avgOffMax;
            }
            if (indices1A == 1 && indices1B == 2.33 && indices1C == 0) {
                whichCase = 13;
                dah = avgOnMin;
                letter = avgOffMin;
                word = avgOffMid;
            }
            if (indices1A == 0.33 && indices1B == 1 && indices1C == 2.33) {
                whichCase = 14;
                dah = avgOnMin;
                code = avgOffMin;
                letter = avgOffMid;
                word = avgOffMax;
            }
        }
    }

    public static String decrypt(StringBuilder message) {
        String[] myArray = message.toString().split(" ");
        StringBuilder show = new StringBuilder();
        for (int i = 0; i < myArray.length; i++) {
            try {
                char c = hack.get(myArray[i]);
                show.append(c);
            } catch (Exception e) {
                show.append('?');
            }
        }
        return show.toString();
    }

    static void convertOffCodes() {
        switch (whichCase) {
            case 1:
                for (int i = 0; i < offCodes.size(); i++) {
                    offDecoded.add(i, ',');
                }
                break;
            case 2:
                for (int i = 0; i < offCodes.size(); i++) {
                    offDecoded.add(i, ' ');
                }
                break;
            case 3:
                for (int i = 0; i < offCodes.size(); i++) {
                    offDecoded.add(i, '/');
                }
                break;
            case 4:
                for (int i = 0; i < offCodes.size(); i++) {
                    if (offCodes.get(i) < 2 * code) {
                        offDecoded.add(i, ',');
                    } else offDecoded.add(i, ' ');
                }
                break;
            case 5:
                for (int i = 0; i < offCodes.size(); i++) {
                    if (offCodes.get(i) < 2 * code) {
                        offDecoded.add(i, ',');
                    } else offDecoded.add(i, '/');
                }
                break;
            case 6:
                for (int i = 0; i < offCodes.size(); i++) {
                    if (offCodes.get(i) < 2 * letter) {
                        offDecoded.add(i, ' ');
                    } else offDecoded.add(i, '/');
                }
                break;
            case 7:
                for (int i = 0; i < offCodes.size(); i++) {
                    if (offCodes.get(i) < 2 * code) {
                        offDecoded.add(i, ',');
                    } else if (offCodes.get(i) > 2 * code && offCodes.get(i) < 4.5 * code) {
                        offDecoded.add(i, ' ');
                    } else offDecoded.add(i, '/');
                }
                break;
            case 8:
                for (int i = 0; i < offCodes.size(); i++) {
                    offDecoded.add(i, ',');
                }
                break;
            case 10:
                for (int i = 0; i < offCodes.size(); i++) {
                    offDecoded.add(i, '/');
                }
                break;
            case 11:
                for (int i = 0; i < offCodes.size(); i++) {
                    if (offCodes.get(i) < 2 * code) {
                        offDecoded.add(i, ',');
                    } else offDecoded.add(i, ' ');
                }
                break;
            case 12:
                for (int i = 0; i < offCodes.size(); i++) {
                    if (offCodes.get(i) < 2 * code) {
                        offDecoded.add(i, ',');
                    } else offDecoded.add(i, '/');
                }
                break;
            case 13:
                for (int i = 0; i < offCodes.size(); i++) {
                    if (offCodes.get(i) < 2 * letter) {
                        offDecoded.add(i, ' ');
                    } else offDecoded.add(i, '/');
                }
                break;
            case 14:
                for (int i = 0; i < offCodes.size(); i++) {
                    if (offCodes.get(i) < 2 * code) {
                        offDecoded.add(i, ',');
                    } else if (offCodes.get(i) > 2 * code && offCodes.get(i) < 4.5 * code) {
                        offDecoded.add(i, ' ');
                    } else offDecoded.add(i, '/');
                }
                break;
            case 15:
                break;
            case 16:
                for (int i = 0; i < offCodes.size(); i++) {
                    if (offCodes.get(i) < 2 * code) {
                        offDecoded.add(i, ',');
                    } else if (offCodes.get(i) > 2 * code && offCodes.get(i) < 4.5 * code) {
                        offDecoded.add(i, ' ');
                    } else offDecoded.add(i, '/');
                }
                break;
            case 17:
                for (int i = 0; i < offCodes.size(); i++) {
                    offDecoded.add(i, ',');
                }
                break;
            case 18:
                for (int i = 0; i < offCodes.size(); i++) {
                    offDecoded.add(i, ' ');
                }
                break;
            case 19:
                for (int i = 0; i < offCodes.size(); i++) {
                    offDecoded.add(i, '/');
                }
                break;
            case 20:
                for (int i = 0; i < offCodes.size(); i++) {
                    if (offCodes.get(i) < 2 * code) {
                        offDecoded.add(i, ',');
                    } else offDecoded.add(i, ' ');
                }
                break;
            case 21:
                for (int i = 0; i < offCodes.size(); i++) {
                    if (offCodes.get(i) < 2 * code) {
                        offDecoded.add(i, ',');
                    } else offDecoded.add(i, '/');
                }
                break;
            case 22:
                for (int i = 0; i < offCodes.size(); i++) {
                    if (offCodes.get(i) < 2 * letter) {
                        offDecoded.add(i, ' ');
                    } else offDecoded.add(i, '/');
                }
                break;
        }
    }

    public static void convertOnCodes() {
        for (int i = 0; i < onCodes.size(); i++) {
            if (onCodes.get(i) < 2 * dit) {
                onDecoded.add(i, '.');
            } else {
                onDecoded.add(i, '-');
            }
        }
    }

    public static void findOnUnit(ArrayList<Integer> arrayList) {
        ArrayList<Integer> tempON;
        tempON = new ArrayList<Integer>(arrayList);
        Collections.sort(tempON);
        long min = tempON.get(0);
        long sumOfMins = 0L, sumOfBigUnit = 0L;
        int noMin = 0;
        int noMax = 0;
        for (int i = 0; i < tempON.size(); i++) {
            if (tempON.get(i) < 2 * min) {
                sumOfMins += tempON.get(i);
                noMin++;
            } else {
                sumOfBigUnit += tempON.get(i);
                noMax++;
            }
        }
        avgOnMin = sumOfMins / noMin;
        if (noMax != 0) {
            avgOnMax = sumOfBigUnit / noMax;
        }
        //find off min unit by comparing least value of offCodes with avgOnMin;
    }

    public static void findOffUnit(ArrayList<Integer> arrayList) {
        ArrayList<Integer> tempOff = new ArrayList<>(arrayList);
        Collections.sort(tempOff);
        long min = tempOff.get(0);
        long sumOfMins = 0L, sumOfBigUnit = 0L;
        long sumOFMids = 0L;
        int noMin = 0;
        int noMax = 0;
        int noMid = 0;
        for (int i = 0; i < tempOff.size(); i++) {
            if (tempOff.get(i) < 2 * min) {
                sumOfMins += tempOff.get(i);
                noMin++;
            } else if (tempOff.get(i) > 2 * min && tempOff.get(i) < 5 * min) {
                sumOFMids += tempOff.get(i);
                noMid++;
            } else {
                sumOfBigUnit += tempOff.get(i);
                noMax++;
            }
        }
        if (noMin != 0) {
            avgOffMin = sumOfMins / noMin;
        }
        if (noMax != 0) {
            avgOffMax = sumOfBigUnit / noMax;
        }
        if (noMid != 0) {
            avgOffMid = sumOFMids / noMid;
        }
    }

    public static ArrayList<Integer> timingsDifference(ArrayList<Integer> arrayList) {
        ArrayList<Integer> temp = new ArrayList<>();
        for (int i = 0; i < arrayList.size() - 1; i++) {
            temp.add((int) (arrayList.get(i + 1) - arrayList.get(i)));
        }
        return temp;
    }

    public static void separateCodes(ArrayList<Integer> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (i % 2 == 0) {
                onCodes.add(arrayList.get(i));
            } else {
                offCodes.add(arrayList.get(i));
            }
        }
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

    public void offFlashlight() {
        if (camera == null || params == null) {
            Toast.makeText(getActivity(), "Couldn't connect to the camera.", Toast.LENGTH_SHORT).show();
            return;
        }
        params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(params);
        camera.stopPreview();
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_morse_decoder, container, false);
        final Button signal = (Button) rootView.findViewById(R.id.button_signal);
        final Button decoder = (Button) rootView.findViewById(R.id.decoder);
        background = (ImageView) rootView.findViewById(R.id.morse_receiver_background);
        morse_decoder_mask = (ImageView) rootView.findViewById(R.id.morse_receiver_mask);
        ImageButton help = (ImageButton) rootView.findViewById(R.id.decoder_help);

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent help = new Intent(getContext(), Tutorial.class);
                startActivity(help);
            }
        });

        background.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent ev) {
                final int action = ev.getAction();
                final int evX = (int) ev.getX();
                final int evY = (int) ev.getY();
                long currentTime;
                try {
                    switch (action & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_DOWN:
                            int touchColor = getHotspotColor(morse_decoder_mask, evX, evY);
                            int tolerance = 10;
                            if (closeMatch(Color.YELLOW, touchColor, tolerance)) {
                                v.setPressed(true);
                                currentTime = SystemClock.uptimeMillis();
                                timings.add((int) currentTime);
                                background.setImageDrawable(getResources().getDrawable(R.drawable.morse_decoder_on));
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            v.setPressed(false);
                        case MotionEvent.ACTION_OUTSIDE:
                        case MotionEvent.ACTION_CANCEL:
                            v.setPressed(false);
                            currentTime = SystemClock.uptimeMillis();
                            timings.add((int) currentTime);
                            background.setImageDrawable(getResources().getDrawable(R.drawable.morse_decoder_off));
                            break;
                    }
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }
        });


        signal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChecked) {
                    removeHandlerCallbacks();
                    background.setImageResource(R.drawable.morse_decoder_off);
                    isChecked = false;
                    try {
                        playWithFlash(20, 1);
                    } catch (InterruptedException e) {
                        Log.d(LOG_TAG, e.toString());
                        e.printStackTrace();
                    }
                } else {
                    removeHandlerCallbacks();
                    isChecked = true;
                    offFlashlight();
                    background.setImageResource(R.drawable.morse_decoder_off);
                }
            }
        });

        decoder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws NullPointerException {
                if (timings.size() != 0) {
                    try {
                        decodedTimings = timingsDifference(timings);
                        separateCodes(decodedTimings);

                        if (onCodes.size() != 0) findOnUnit(onCodes);
                        if (offCodes.size() != 0) findOffUnit(offCodes);

                        findIndices();
                        findCaseAndDefineUnits();

                        convertOnCodes();
                        convertOffCodes();

                        for (int i = 0; i < onDecoded.size(); i++) {
                            if (i >= offDecoded.size()) {
                                sb.append(onDecoded.get(i));
                            } else {
                                if (offDecoded.get(i) == '/') {
                                    sb.append(onDecoded.get(i));
                                    sb.append(' ');
                                    sb.append(offDecoded.get(i));
                                    sb.append(' ');
                                } else {
                                    sb.append(onDecoded.get(i));
                                    sb.append(offDecoded.get(i));
                                }
                            }
                        }

                        String answer = decrypt(sb);
                        String answer2;
                        Log.d(LOG_TAG, decodedTimings.toString());

                        if (whichCase == 1) {
                            String temp = sb.toString();
                            temp = temp.replace('.', '-');
                            temp = temp.replace(',', ' ');
                            StringBuilder sb2 = new StringBuilder(temp);
                            answer2 = decrypt(sb2);
                            String message = "This message is ambiguous. It's either \n" + answer + "\n" + "or \n" + answer2;
                            createDialog(getContext(), message);

                        } else if (whichCase == 15) {
                            String message = "This message is ambiguous. It's either E or T";
                            createDialog(getContext(), message);
                        } else {
                            createDialog(getContext(), answer);
                        }

                        resetAll();

                    } catch (NullPointerException npe) {
                        Log.d(LOG_TAG, npe.toString());
                        String message = "Oops! something went wrong with your input";
                        createDialog(getContext(), message);

                        resetAll();
                    }
                } else {
                    Toast.makeText(getContext(), "Expecting some input!", Toast.LENGTH_SHORT).show();
                }
            }

        });

        return rootView;
    }

    private void removeHandlerCallbacks() {
        handler.removeCallbacksAndMessages(null);
        handler2.removeCallbacksAndMessages(null);
    }

    public void playWithFlash(int A, int B) throws InterruptedException {
        String sb = "13131";
        int delay = 0;
        for (int i = 0; i < sb.length(); i++) {
            x = Integer.parseInt(String.valueOf(sb.charAt(i)));
            if (!flashStatus) {
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
                background.setImageResource(R.drawable.morse_decoder_off);
                offFlashlight();
                isChecked = true;
            }
        }, delay * 1000 / A * B);
        flashStatus = false;
    }

    private void createDialog(Context context, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Message");
        alert.setMessage(message);
        alert.setPositiveButton("OK", null);
        alert.setCancelable(false);
        alert.show();
    }
}