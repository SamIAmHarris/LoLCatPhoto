package com.samiamharris.lolcatphoto;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;


/**
 * Created by samharris on 4/9/14.
 */
public class LolCatPhotoFragment extends Fragment {

    private static final String TAG = "LolCatPhotoFragment";

    final int MAX_SEC = 100;

    private ImageView mImageView;
    private ArrayList<LolCatPhoto> mItems;
    LolCatPhoto mItem;
    ThumbnailDownloader<ImageView> mThumbnailThread;
    static int sCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //retains the fragment state when parent Activity is destroyed
        setRetainInstance(true);
        //starts the AsyncTask and runs doInBackground()
        new FetchItemsTask().execute();

        //creates a new thread and passes a handler to it
        mThumbnailThread = new ThumbnailDownloader<ImageView>(new Handler());
        mThumbnailThread.setListener(new ThumbnailDownloader.Listener<ImageView>() {
            @Override
            public void onThumbnailDownloaded(ImageView imageView, Bitmap thumbnail) {
                //checks to see if the fragment is visible first
                if (isVisible()) {
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
                //triggers the image downloading
                mThumbnailThread.queueThumbnail(mImageView, mItem.getUrl());
            }
        });

        //Take you to the actual web page of the Flickr site
        mImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Uri photoPageUri = Uri.parse(mItem.getPhotoPageUrl());
                Intent i = new Intent(getActivity(), WebPageActivity.class);
                i.setData(photoPageUri);

                startActivity(i);

                return false;
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

        //fragments can exist unattached from the activity
        if(getActivity() == null || mImageView == null) {
            return;
        }

        if(mItems != null) {
            //triggers the image downloading
            mThumbnailThread.queueThumbnail(mImageView, mItems.get(sCount).getUrl());
        }
    }

    //creates AsyncTask that will return an Array of LolCatPhoto objects and
    //show the first photo
    private class FetchItemsTask extends AsyncTask<Void,Integer,ArrayList<LolCatPhoto>> {

        private ProgressDialog progressDialog = new ProgressDialog(getActivity());
        @Override
        protected void onPreExecute() {
            progressDialog.setMax(MAX_SEC);
            progressDialog.setProgress(0);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("Loading Cats...");
            progressDialog.show();
        }

        @Override
        protected ArrayList<LolCatPhoto> doInBackground(Void... params) {

            ArrayList<LolCatPhoto> backgroundArray =  new ArrayList<LolCatPhoto>();

            //Broke up FlickrFetchr methods so that the progress bar would
            //actually represent real progress being made

            try {
                publishProgress(MAX_SEC/5);
                FlickrFetchr flickrFetchr = new FlickrFetchr();

                publishProgress((MAX_SEC*2)/5);
                String url = flickrFetchr.search();

                publishProgress((MAX_SEC*3)/5);
                String xmlString = flickrFetchr.getUrl(url);

                publishProgress((MAX_SEC*4)/5);
                backgroundArray = flickrFetchr.downloadGalleryItems(xmlString);

                publishProgress(MAX_SEC);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
                return backgroundArray;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressDialog.setProgress(values[0]);

        }

        @Override
        protected void onPostExecute(ArrayList<LolCatPhoto> items) {//run in the main UI thread, not the background
            progressDialog.dismiss();
            mItems = items;
            setImage();
        }
    }

}
