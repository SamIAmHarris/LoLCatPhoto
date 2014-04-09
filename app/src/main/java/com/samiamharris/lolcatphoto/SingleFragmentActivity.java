package com.samiamharris.lolcatphoto;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by samharris on 2/24/14.
 */

/**
 * Abstract class to cut down on code for Activity
 */

public abstract class SingleFragmentActivity extends FragmentActivity {

    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }

    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();

        }
    }
}
