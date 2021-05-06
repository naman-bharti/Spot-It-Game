package ca.cmpt276.userstories1.model;

/*********************************************************************
// *********************************************************************
// Gallery item used for individual pictures in FlickrFetchr
// Code from the Chapter 27 Android Programming The Big Nerd Ranch Guide
// CITATION: https://www.bignerdranch.com/solutions/AndroidProgramming3e.zip
// *********************************************************************
*/

public class GalleryItem {
    private String mCaption;
    private String mId;
    private String mUrl;

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

    @Override
    public String toString() {
        return mCaption;
    }
}
