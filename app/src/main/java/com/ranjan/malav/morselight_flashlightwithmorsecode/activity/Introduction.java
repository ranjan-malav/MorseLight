package com.ranjan.malav.morselight_flashlightwithmorsecode.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ranjan.malav.morselight_flashlightwithmorsecode.R;

public class Introduction extends AppCompatActivity {
    private static final int NUM_PAGES = 5;
    int i;
    SharedPreferences pref;
    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;
    private Button btn_next, btn_skip;
    ViewPager.OnPageChangeListener pagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            i = position;
            if (position < 4) {
                btn_next.setText("Next");
            } else if (position == 4) {
                btn_next.setText("Done");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
    private IntroManager introManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_skip = (Button) findViewById(R.id.btn_skip);
        boolean firstTime;
        pref = getBaseContext().getSharedPreferences("first", 0);
        firstTime = pref.getBoolean("check", true);
        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabDots);
        tabLayout.setupWithViewPager(mPager, true);
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(pagerListener);

        final boolean finalFirstTime = firstTime;

        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalFirstTime) {
                    introManager = new IntroManager(getBaseContext());
                    introManager.setFirst(false);
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    finish();
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (i < 4) {
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                } else if (i == 4) {
                    if (finalFirstTime) {
                        introManager = new IntroManager(getBaseContext());
                        introManager.setFirst(false);
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(intent);
                    } else {
                        finish();
                    }
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new IntroductionFragment(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}