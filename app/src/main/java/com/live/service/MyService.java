package com.live.service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;

import com.live.R;
import com.live.app.App;
import com.live.bean.AdList;
import com.live.bean.RollTitles;
import com.live.service.msg.IScrollState;
import com.live.service.msg.MarqueeToast;
import com.live.service.msg.TextSurfaceView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MyService extends Service implements Runnable, IScrollState {
    private final String tag = "MyService";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    App app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = (App) getApplication();
        websocket();
    }

    private Socket socket;

    private List<RollTitles> rollTitles = new ArrayList<>();
    private AdList adLists;

    private void websocket() {

        try {
            socket = IO.socket(App.socketurl);
            socket.on("warning", new Emitter.Listener() {

                public void call(Object... arg0) {
                    // TODO Auto-generated method stub
                    try {
                        final String json = arg0[0].toString();
                        Log.d(tag + "---" + "warning", json);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                warning(json);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            socket.on("rollTitles", new Emitter.Listener() {

                public void call(Object... arg0) {//跑马灯
                    // TODO Auto-generated method stub
                    try {
                        String json = arg0[0].toString();
                        Log.d(tag + "---" + "rollTitles", json);
                        rollTitles = new ArrayList<>(Arrays.asList(App.gson.fromJson(json, RollTitles[].class)));
                        currentmsg = 0;
                        handler.post(MyService.this);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            socket.on("adList", new Emitter.Listener() {

                public void call(Object... arg0) {//广告
                    // TODO Auto-generated method stub
                    try {
                        String json = arg0[0].toString();
                        Log.d(tag + "---" + "adList", json);
                        System.out.println("初始化广告");
                        AdList tmp = new ArrayList<>(Arrays.asList(App.gson.fromJson(json, AdList[].class))).get(0);
                        adLists = tmp;
                        app.setAdLists(adLists);
                        adList();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            socket.on("add_ad", new Emitter.Listener() {

                public void call(Object... arg0) {//新增广告
                    // TODO Auto-generated method stub
                    try {
                        String json = arg0[0].toString();
                        Log.d(tag + "---" + "add_ad", json);
                        System.out.println("新增广告");
                        AdList tmp = App.gson.fromJson(json, AdList.class);
                        adLists = tmp;
                        app.setAdLists(adLists);
                        add_ad();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            socket.on("update_ad", new Emitter.Listener() {

                public void call(Object... arg0) {//更新广告
                    // TODO Auto-generated method stub
                    try {
                        String json = arg0[0].toString();
                        Log.d(tag + "---" + "update_ad", json);
                        System.out.println("更新广告");
                        AdList tmp = App.gson.fromJson(json, AdList.class);
                        adLists = tmp;
                        app.setAdLists(adLists);
                        update_ad();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            socket.on("delete_ad", new Emitter.Listener() {

                public void call(Object... arg0) {//删除广告
                    // TODO Auto-generated method stub
                    try {
                        String json = arg0[0].toString();
                        Log.d(tag + "---" + "delete_ad", json);
                        System.out.println("删除广告");

//                        delete_ad();
                        if (null != adLists) {
                            if (Integer.parseInt(json) == adLists.getId()) {
                                delete_ad();
                                adLists = null;
                                app.setAdLists(null);
                            }
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                public void call(Object... arg0) {
                    // TODO Auto-generated method stub
                    try {
                        System.out.println("Socket连接成功----online");
                        register();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                public void call(Object... arg0) {
                    // TODO Auto-generated method stub
                    System.out.println("Socket断开连接----offline");
                }

            });
            socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {

                public void call(Object... arg0) {
                    // TODO Auto-generated method stub
                    System.out.println("Socket连接失败----online fail");
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        socket.connect();
    }
//    删除：delete_ad
//    返回：adId

    private void delete_ad() {
        Intent intent = new Intent(App.DeleteAdList);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable("key", (Serializable) adLists);
//        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    //    修改：update_ad
//    返回数据：AdGroupEntity adGroupEntity
    private void update_ad() {
        Intent intent = new Intent(App.UpdateAdList);
        Bundle bundle = new Bundle();
        bundle.putSerializable("key", (Serializable) adLists);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    //    新增：add_ad
//    返回数据：AdGroupEntity adGroupEntity
    private void add_ad() {
        Intent intent = new Intent(App.UpdateAdList);
        Bundle bundle = new Bundle();
        bundle.putSerializable("key", (Serializable) adLists);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    //    广告：adList
//            返回数据类型
    private void adList() {
        Intent intent = new Intent(App.InitAdList);
        Bundle bundle = new Bundle();
        bundle.putSerializable("key", (Serializable) adLists);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }


    //    跑马灯：rollTitles
    //            返回数据类型
//    List<RollTitlesEntity> rollTitles
    public int currentmsg;
    private MarqueeToast toast;
    private TextSurfaceView Text;

    public void rollTitles() {
        try {
            if (rollTitles != null && !rollTitles.isEmpty()) {
//                System.out.println("开始跑马灯");
                if (rollTitles.size() <= currentmsg)
                    currentmsg = 0;
                if (System.currentTimeMillis() > rollTitles.get(currentmsg).getEndtime()) {
                    rollTitles.remove(currentmsg);
                    currentmsg = 0;
                    handler.post(this);
                    return;
                }


                if (toast != null)
                    toast.hid();
                toast = new MarqueeToast(getApplicationContext());
                Text = new TextSurfaceView(getApplicationContext(), this);
                Text.setOrientation(1);
                Text.setFontSize(rollTitles.get(currentmsg).getSize());
                Text.setSpeed((int) ((101 - rollTitles.get(currentmsg).getSpeed()) * 0.3));
//                Text.setSpeed(30);
//                Text.setBackgroundColor(Color.parseColor(rollTitles.get(currentmsg).getColor().replace("#", "#99")));
                toast.setHeight(rollTitles.get(currentmsg).getSize());
                Text.setFontColor(rollTitles.get(currentmsg).getColor());

                if (rollTitles.get(currentmsg).getContent().equals("")
                        && rollTitles.get(currentmsg).getContent() == null) {
                    Text.setBackgroundColor(Color.TRANSPARENT);
                    Text.setContent("");
                } else {
                    Text.setContent(rollTitles.get(currentmsg).getContent());
//                    Text.setContent("《" + rollTitles.get(currentmsg).getName() + "》\t" + rollTitles.get(currentmsg).getContent());
                }
                toast.setView(Text);
                toast.setGravity(Gravity.TOP | Gravity.LEFT, 1280, 0, 0);
                toast.show();
                currentmsg++;
            } else {
                System.out.println("跑马灯没了");
                if (toast != null) {
                    toast.hid();
                    toast = null;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    //    register
//    参数：String mac
//    int type  apk类型  1直播  2ktv
    private void register() {
        socket.emit("register", App.mac, 1);
//        socket.emit("register", App.mac, 1);
    }

    //    提醒续费：
//    发送数据：
//    warning  String类型（直接显示出来，并且给个确认键让用户确认）
    private AlertDialog.Builder builder;
    AlertDialog ad;

    private void warning(String json) {
//        Toast.makeText(MyService.this, json, Toast.LENGTH_SHORT).show();
        builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_dialog_info)
                .setMessage(json)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        ad = builder.create();
        ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        ad.show();
    }

    @Override
    public void start() {

    }

    private static final long SHOW_MSG_PERIOD = 1L * 10L * 1000L;
    private Handler handler = new Handler();

    @Override
    public void stop() {
        // TODO Auto-generated method stub
        Text.setLoop(false);
        Looper.prepare();
        handler.postDelayed(this, SHOW_MSG_PERIOD);
        Looper.loop();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        rollTitles();
    }
}
