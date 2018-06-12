package com.live.app;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.live.R;
import com.live.event.NetChange;
import com.live.event.UpdateTime;
import com.live.tools.Toas;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import okhttp3.OkHttpClient;

public class App extends Application {
    public static Gson gson;
    private SharedPreferences config;
    public static OkHttpClient client;

    private Toas toas = new Toas();
    ;

    @Override
    public void onCreate() {
        super.onCreate();

        gson = new GsonBuilder().setDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss").create();
        config = getSharedPreferences("config", Context.MODE_PRIVATE);
        config();
        getip();
        mac();
        client = new OkHttpClient();

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        registerReceiver(receiver, filter);


//        startService(new Intent(this, MyService.class));

    }

    public static int network_type = -1;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager
                        .getActiveNetworkInfo();
                network_type = activeNetworkInfo.getType();
                System.out.println("网络类型&Net Type：" + network_type);

                switch (network_type) {
                    case -1:
                        toas.setMsg(getString(R.string.disnetwork));
                        break;
                    case 0:
                        toas.setMsg(getString(R.string.gps_network));
                        break;
                    case 1:
                        toas.setMsg(getString(R.string.wifi_network));
                        break;
                    case 9:
                        toas.setMsg(getString(R.string.eth_network));
                        break;
                }
                EventBus.getDefault().post(new NetChange(network_type));
                toas.show(App.this);

            } else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                EventBus.getDefault().post(new UpdateTime(System.currentTimeMillis()));
            } else if (intent.getAction().equals(Intent.ACTION_TIME_CHANGED)) {
                System.out.println("同步服务器时间&synchronous server time");
            }

        }
    };

    private boolean fstart;
    private static String ip = "192.168.2.25:8109";
    //private static String ip = "192.168.2.89:8108";
    public static String version;

    private void config() {
        try {
            fstart = config.getBoolean("fstart", false);
            if (!fstart) {
                SharedPreferences.Editor editor = config.edit();
                editor.putBoolean("fstart", true);
                System.out.println("---fstart---");
                editor.putString("ip", ip);
                System.out.println("---ip---\n" + ip);
                editor.commit();
            }
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String headurl;

    private void getip() {
        String tmp = config.getString("ip", "");
        if (!tmp.equals("")) {
            headurl = "http://" + tmp + "/ktv/api/live/";
            System.out.println("---headurl---\n" + headurl);
        }
    }

    public static String mac;

    private void mac() {
        try {
            Process pro = Runtime.getRuntime().exec(
                    "cat /sys/class/net/eth0/address");
            InputStreamReader inReader = new InputStreamReader(
                    pro.getInputStream());
            BufferedReader bReader = new BufferedReader(inReader);
            String line = null;
            while ((line = bReader.readLine()) != null) {
                mac = line.trim();
            }
            System.out.println("---mac---\n" + mac);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap logo = null;

    public Bitmap getLogo() {
        return logo;
    }

    public void setLogo(Bitmap logo) {
        this.logo = logo;
    }

    private List<Bitmap> back = new ArrayList<>();

    public List<Bitmap> getBack() {
        return back;
    }

    public void setBack(List<Bitmap> back) {
        this.back = back;
    }

    public static boolean isInstall(Context context, String packageName) {
        try {
            PackageInfo pin = context.getPackageManager().getPackageInfo(
                    packageName, 0);
            if (pin != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean startApk(Context context, String pkgNmae, String className) {
        try {
            if (isInstall(context, pkgNmae)) {
                if (!className.trim().equals("")) {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName(pkgNmae, className));
                    context.startActivity(intent);
                } else {
                    Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkgNmae);
                    context.startActivity(intent);

                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }

    private String Key = "";

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }
}