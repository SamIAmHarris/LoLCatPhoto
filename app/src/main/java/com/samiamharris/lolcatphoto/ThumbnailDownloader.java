package com.samiamharris.lolcatphoto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by samharris on 4/9/14.
 */
public class ThumbnailDownloader<Token> extends HandlerThread{

    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;  //what - user defined int

    Handler mHandler;
    Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());  //stores and receives the URL associated with a particular Token
    Handler mResponseHandler;  //handler passed from the main thread
    Listener<Token> mListener;

    public interface Listener<Token> {
        void onThumbnailDownloaded(Token token, Bitmap thumbnail);
    }

    public void setListener(Listener<Token> listener) {
        mListener = listener;
    }

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }


    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            public void handleMessage(Message msg) {  //check the message type, retrieve the Token and pass it to handleRequest
                if(msg.what == MESSAGE_DOWNLOAD) {
                    Token token = (Token)msg.obj;  //user defined object to be sent with the message
                    Log.i(TAG, "Got a request for url: " + requestMap.get(token));
                    handleRequest(token);
                }
            }
        };
    }

    /*
    adds the passed in Token-URL pair to the map
     */
    public void queueThumbnail(Token token, String url) {
        Log.i(TAG, "Got an URL: " + url);
        requestMap.put(token, url);

        mHandler.obtainMessage(MESSAGE_DOWNLOAD, token).sendToTarget();  //obtain the message, give it a Token, then send it to the message queue
    }

    /*
    where the downloading happens
     */
    private void handleRequest(final Token token) {
        try {
            final String url = requestMap.get(token);
            if(url == null) {  //check for the existence of a URL
                return;
            }
            byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);  //pass the URL to a new instance of FlickrFetcher
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);  //construct a bitmap with the array of bytes returned from getUrlBytes
            Log.i(TAG, "Bitmap created");

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(requestMap.get(token) != url)
                        return;
                    requestMap.remove(token);  //removes the Token from the requestMap
                    mListener.onThumbnailDownloaded(token, bitmap); // sets the bitmap on the Token
                }
            });
        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
    }
    /*
    cleans all requests out of the queue incase the user rotates the screen
     */
    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }
}

