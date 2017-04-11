package com.ranjan.malav.morselight_flashlightwithmorsecode.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ranjan.malav.morselight_flashlightwithmorsecode.R;

import java.util.ArrayList;
import java.util.List;

import static android.hardware.Camera.getNumberOfCameras;
//git
public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {
    public static Camera camera;
    public static Camera.Parameters params;
    public static Boolean flashStatus = false;
    public static ArrayList<Double> multiplierOptions;
    public static PowerManager.WakeLock mWakeLock;
    public static PowerManager pm;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 99;
    String packageName = "com.ranjan.malav.morselight_flashlightwithmorsecode.activity";
    private boolean hasFlash;
    private ViewPager viewPager;
    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private String LOG_TAG = "Main Activity";
    private TabLayout tabLayout;
    private IntroManager introManager;
    private float[] multipliers = {0.33f, 0.5f, 0.75f, 1.0f};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.MyMaterialTheme);
        Log.d(LOG_TAG, "onCreate is called");
        super.onCreate(savedInstanceState);
        introManager = new IntroManager(this);

        if (introManager.check()) {
            Intent i = new Intent(this, Introduction.class);
            startActivity(i);
            finish();
        }
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        if (!hasFlash) {
            Toast.makeText(this,"Flashlight not found! Some functions won't work.", Toast.LENGTH_SHORT).show();
        }

        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        Flashlight.turnOffFlash();
                        break;
                    case 1:
                        MorseGenerator.removeHandlerCallbacks();
                        MorseGenerator.resetAllAtEnd(getBaseContext());
                        if (mWakeLock != null) {
                            Log.v(LOG_TAG, "Releasing wakelock");
                            try {
                                mWakeLock.release();
                            } catch (Throwable th) {
                                // ignoring this exception, probably wakeLock was already released
                            }
                        } else {
                            // should never happen during normal workflow
                            Log.e(LOG_TAG, "Wakelock reference is null");
                        }
                        break;
                    case 2:
                        MorseDecoder.resetAll();
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Flashlight(), "Flashlight");
        adapter.addFragment(new MorseGenerator(), "Text To Morse");
        adapter.addFragment(new MorseDecoder(), "Morse Decoder");
        viewPager.setAdapter(adapter);
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey! Check out this awesome app!! You can send and decode morse code using this. " +
                "https://play.google.com/store/apps/details?id=" + getBaseContext().getPackageName());
        return shareIntent;
    }

    private Intent createRatingIntent() {
        Uri uri = Uri.parse("market://details?id=" + getBaseContext().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        return goToMarket;
    }

    private int findBackCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.d(LOG_TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    public void getCamera() {
        int id = findBackCamera();
        if (camera == null) {
            try {
                camera = Camera.open(id);
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e(LOG_TAG, e.getMessage());
                Toast.makeText(MainActivity.this,"couldn't connect to the camera.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Flashlight.turnOffFlash();
        try {
            MorseGenerator.removeHandlerCallbacks();
            MorseGenerator.resetAllAtEnd(this);
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, e.toString());
        }
        MorseDecoder.resetAll();
        if (mWakeLock != null) {
            Log.v(LOG_TAG, "Releasing wakelock");
            try {
                mWakeLock.release();
            } catch (Throwable th) {
                // ignoring this exception, probably wakeLock was already released
            }
        } else {
            // should never happen during normal workflow
            Log.e(LOG_TAG, "Wakelock reference is null");
        }
        if (camera != null) {
            camera.release();
        }
        camera = null;
        Log.d(LOG_TAG, "onPause called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean permission = checkCameraPermission();
        if (permission) {
            getCamera();
        } else {
            AlertDialog ad = new AlertDialog.Builder(MainActivity.this).create();
            ad.setTitle("Permission required!");
            ad.setMessage("Some of the functions won't work because this app doesn't have permission to use the camera");
            ad.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + packageName));
                        startActivity(intent);

                    } catch (ActivityNotFoundException e) {
                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        switch (position) {
            case 0: //User Name
                final SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = prefs.edit();
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle("Enter Your Name");
                // Set an EditText view to get user input
                final EditText input = new EditText(this);
                input.setHint(prefs.getString("username", "Enter your name."));
                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        editor.putString("username", input.getText().toString());
                        editor.apply();
                    }
                });
                alert.show();
                break;
            case 1: //purpose
                Intent intent3 = new Intent(this, Purpose.class);
                startActivity(intent3);
                break;
            case 2: //Morse Info
                Intent intent2 = new Intent(this, MorseDetail.class);
                startActivity(intent2);
                break;
            case 3: //transmission speed
                Dialog dialog = onCreateDialog();
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();
                break;
            case 4:
                Intent intent4 = new Intent(this, Tutorial.class);
                startActivity(intent4);
                break;
            case 5: //Help
                Intent intentHelp = new Intent(this, Introduction.class);
                startActivity(intentHelp);
                break;
            case 6: //share
                startActivity(createShareIntent());
                break;
            case 7: //rating
                Intent intent = createRatingIntent();
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            //Uri.parse("http://play.google.com/store/apps/details?id=" + getBaseContext().getPackageName())));
                            Uri.parse("https://www.google.com")));
                }
                break;
            case 8: //contact us
                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setType("message/rfc822");
                i.setData(Uri.parse("mailto:ranjan2192@gmail.com"));
                Intent mailer = Intent.createChooser(i, "Send mail..");
                startActivity(mailer);
                break;
        }

    }

    private Dialog onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Choose transmission speed")
                .setItems(R.array.pref_multiplier_values, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putFloat("multiplier", multipliers[which]);
                        editor.apply();
                    }
                });
        return builder.create();
    }

    public boolean checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_CAMERA);
            }
            return false;
        } else {
            return true;
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }
}
