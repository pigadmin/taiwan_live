package com.live.event;

import android.graphics.Bitmap;

public class BitmapMessage {
    public BitmapMessage(String api, Bitmap bitmap) {
        this.api = api;
        this.bitmap = bitmap;
    }

    public String getApi() {
        return api;
    }

    String api;

    public Bitmap getBitmap() {
        return bitmap;
    }

    private Bitmap bitmap;


}
