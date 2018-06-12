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
    public static String marquee = App.headurl + "getSubTitle?mac=" + App.mac;
    public static String check = App.headurl + "checkAuth";
    public static String menu = App.headurl + "tmenu?mac=" + App.mac;
    public static String logo = App.headurl + "getLogo?mac=" + App.mac + "&type=3";
    public static String wea = App.headurl + "getWeather";
    public static String update = App.headurl + "getUpgrade?mac=" + App.mac + "&version=" + App.version;
    public static String singlelive = App.headurl + "live?mac=" + App.mac + "&type=2";
    public static String notice = App.headurl + "addNotice?mac=" + App.mac;
    public static String youhui = App.headurl + "getInfo?mac=" + App.mac + "&type=1";
    public static String intro = App.headurl + "getInfo?mac=" + App.mac + "&type=2";
    public static String help = App.headurl + "getInfo?mac=" + App.mac + "&type=3";
    public static String videotype = App.headurl + "vodtype?mac=" + App.mac;
    public static String video = App.headurl + "vod?mac=" + App.mac + "&pageNo=1&pageSize=99999";
    public static String vrecord = App.headurl + "vrecord?mac=" + App.mac;
    public static String dishstyle = App.headurl + "getDishStyle?mac=" + App.mac;
    public static String dish = App.headurl + "getDish?mac=" + App.mac;
    public static String teachtype = App.headurl + "teach/type?mac=" + App.mac;
    public static String teach = App.headurl + "teach?mac=" + App.mac + "&pageNo=1&pageSize=9999";
    public static String game = App.headurl + "getApp?mac=" + App.mac;

    public static void get(final String url) {
//        System.out.println(url);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String json;
                    Request request = new Request.Builder().url(url).build();
                    Response response = App.client.newCall(request).execute();
                    if (response.code() == 200) {
                        json = response.body().string();
//                        System.out.println(json);
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
