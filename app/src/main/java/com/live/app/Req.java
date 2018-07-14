package com.live.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.live.event.BitmapMessage;

import de.greenrobot.event.EventBus;
import okhttp3.Request;
import okhttp3.Response;

public class Req {
    private Api api;

    public Req(Api api) {
        this.api = api;
    }

    public interface Api {
        void finish(String tag, String json);

        void error(String tag, String json);
    }

    public void Get(final String tag, final String url) {
        System.out.println(url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String json = null;
                Request request = null;
                Response response = null;
                try {
                    request = new Request.Builder().url(url).build();
                    response = App.client.newCall(request).execute();
                    if (response.code() == 200) {
                        json = response.body().string();
                        if (api == null)
                            return;
                        api.finish(tag, json);
                    } else {
                        if (api == null)
                            return;
                        api.error(tag, response.code() + "");
                    }
                } catch (Exception e) {
                    if (api == null)
                        return;
                    api.error(tag, response.code() + "");
                    // e.printStackTrace();
                }
            }
        }).start();
    }

//    public static void get(final String url) {
//        System.out.println(url);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    String json = "";
//                    Request request = new Request.Builder().url(url).build();
//                    Response response = App.client.newCall(request).execute();
//                    if (response.code() == 200) {
//                        json = response.body().string();
//                        System.out.println(json);
//                        EventBus.getDefault().post(new DataMessage(url, json));
//                    } else {
//                        EventBus.getDefault().post(new ErrorMessage(url, json));
//                    }
//                } catch (Exception e) {
//                    System.out.println("api request fail:" + url);
//                    EventBus.getDefault().post(new ErrorMessage(url, ""));
//                    // e.printStackTrace();
//                }
//            }
//        }).start();
//    }


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
//                        EventBus.getDefault().post(new ErrorMessage(api, ""));
                    }
                } catch (Exception e) {
                    System.out.println("api request fail:" + url);
//                    EventBus.getDefault().post(new ErrorMessage(api, ""));
//                    e.printStackTrace();
                }
            }
        }).start();
    }

}
