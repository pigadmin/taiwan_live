package com.live.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.live.event.BitmapMessage;
import com.live.event.DataMessage;
import com.live.event.ErrorMessage;

import de.greenrobot.event.EventBus;
import okhttp3.Request;
import okhttp3.Response;

public class Req {
    public static String type = App.headurl + "type?mac=" + App.mac;

    public static void get(final String url) {
        System.out.println(url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String json;
                    Request request = new Request.Builder().url(url).build();
                    Response response = App.client.newCall(request).execute();
                    if (response.code() == 200) {
                        json = response.body().string();
                        System.out.println(json);
                        EventBus.getDefault().post(new DataMessage(url, json));
                    } else {
                        EventBus.getDefault().post(new ErrorMessage(url, response.code()));
                    }
                } catch (Exception e) {
                    System.out.println("api request fail:" + url);
                    EventBus.getDefault().post(new ErrorMessage(url, 0));

                    // e.printStackTrace();
                }
            }
        }).start();
    }


    public static void img(final String api, final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Request request = new Request.Builder().url(url).build();
                    Response response = App.client.newCall(request).execute();
                    if (response.code() == 200) {
                        byte[] b = response.body().bytes();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                        EventBus.getDefault().post(new BitmapMessage(api, bitmap));
                    } else {
                        EventBus.getDefault().post(new ErrorMessage(api, response.code()));
                    }
                } catch (Exception e) {
                    System.out.println("api request fail:" + url);
                    EventBus.getDefault().post(new ErrorMessage(api, 0));
//                    e.printStackTrace();
                }
            }
        }).start();
    }

}
