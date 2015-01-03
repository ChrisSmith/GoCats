package org.collegelabs.gocats.app;

import android.graphics.Bitmap;

/**
*/
public class ImageInfo {
    public Bitmap image;
    public String id;

    public ImageInfo(String id, Bitmap image){

        this.id = id;
        this.image = image;
    }
}
