package com.samiamharris.lolcatphoto;

/**
 * Created by samharris on 4/9/14.
 */

/**
 * Simple data class for a photo
 */

public class LolCatPhoto {

    private String mTitle;
    private String mId;
    private String mOwner;
    private String mUrl;

    public String toString() {
        return mTitle;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getId() {
        return mId;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String mOwner) {
        this.mOwner = mOwner;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    //Specifically for the Long Click WebPage
    public String getPhotoPageUrl() {
        return "http://www.flickr.com/photos/" + mOwner + "/" + mId;}

}
