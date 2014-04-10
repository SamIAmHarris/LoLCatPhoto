package com.samiamharris.lolcatphoto;

import android.support.v4.app.Fragment;

/**
 * Created by samharris on 4/10/14.
 */
public class WebPageActivity extends SingleFragmentActivity {

    //Same old Single Frag Activity set up
    @Override
    protected Fragment createFragment() {
        return new WebPageFragment();
    }
}
