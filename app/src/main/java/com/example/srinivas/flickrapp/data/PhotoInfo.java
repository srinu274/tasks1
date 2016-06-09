package com.example.srinivas.flickrapp.data;

/**
 * Created by Srinivas on 07-06-2016.
 */
public class PhotoInfo {
    private String mDescription;
    private String mTitle;
    private String mLocation;

    public String getDescription() {
        return mDescription;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setDescription(String description) {
        mDescription=description;
    }

    public void setTitle(String title) {
        mTitle=title;
    }

    public void setLocation(String location) {
        mLocation=location;
    }
}
