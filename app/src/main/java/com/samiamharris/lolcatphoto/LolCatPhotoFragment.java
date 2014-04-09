package com.samiamharris.lolcatphoto;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by samharris on 4/9/14.
 */
public class LolCatPhotoFragment extends Fragment {

    private static final String TAG = "LolCatPhotoFragment";

    private ImageView mImageView;
    private ArrayList<LolCatPhoto> mItems;
    LolCatPhoto mItem;
    ThumbnailDownloader<ImageView> mThumbnailThread;
    static int sCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);  //retains the fragment state when parent Activity is destroyed
        new FetchItemsTask().execute(); //starts the AsyncTask and runs doInBackground()

        mThumbnailThread = new ThumbnailDownloader<ImageView>(new Handler());  //creates a new thread and passes a handler to it
        mThumbnailThread.setListener(new ThumbnailDownloader.Listener<ImageView>() {
            @Override
            public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
                if(isVisible()) {  //checks to see if the fragment is visible first
                    imageView.setImageBitmap(thumbnail);
                }
            }
        });
        mThumbnailThread.start();
        mThumbnailThread.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_lol_cat_photo, container, false);

        mImageView = (ImageView) v.findViewById(R.id.lol_cat_imageView);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sCount = new Random().nextInt(99);
                mItem = mItems.get(sCount);
                mThumbnailThread.queueThumbnail(mImageView, mItem.getUrl());  //triggers the image downloading
            }
        });

        setImage();

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailThread.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailThread.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    void setImage() {

        if(getActivity() == null || mImageView == null) {  //fragments can exist unattached from the activity
            return;
        }

        if(mItems != null) {
            //Set a default image while waiting to load
            mImageView.setImageResource(R.drawable.ic_launcher);
            mThumbnailThread.queueThumbnail(mImageView, mItems.get(sCount).getUrl());  //triggers the image downloading
        }
    }

    //creates AsyncTask that will grab the LolCat Photo
    private class FetchItemsTask extends AsyncTask<Void,Void,ArrayList<LolCatPhoto>> {
        @Override
        protected ArrayList<LolCatPhoto> doInBackground(Void... params) {
            return new FlickrFetchr().search();

        }

        @Override
        protected void onPostExecute(ArrayList<LolCatPhoto> items) {//run in the main UI thread, not the background

            mItems = items;
            setImage();
        }
    }

}
