package com.samiamharris.lolcatphoto;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.Random;

/**
 * returns an instance of LolCatPhotoFragment
 * Fragment will take up entire screen
 */
public class LolCatPhotoActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new LolCatPhotoFragment();
    }


}
