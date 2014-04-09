package com.samiamharris.lolcatphoto;

import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

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
