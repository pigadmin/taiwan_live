package com.live.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.live.R;
import com.live.app.App;
import com.live.app.Req;
import com.live.bean.AJson;
import com.live.bean.AdEntities;
import com.live.bean.AdList;
import com.live.bean.LiveType;
import com.live.bean.Update;
import com.live.tools.ApkUpdate;
import com.live.tools.LtoDate;
import com.live.ui.adapter.LiveTypeAdapter;
import com.live.ui.play.PlayerActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener, Req.Api {

    private App app;
    private String TAG;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (App) getApplication();
        req = new Req(this);
        find();
        init();
        checkad();
        regad();
    }

    private Req req;

    private Animation ad_alpha, ad_rotate, ad_scale, ad_translate;

    private void regad() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(App.InitAdList);
        intentFilter.addAction(App.UpdateAdList);
        intentFilter.addAction(App.DeleteAdList);
        registerReceiver(receiver, intentFilter);

        ad_alpha = AnimationUtils.loadAnimation(this, R.anim.ad_alpha);
        ad_rotate = AnimationUtils.loadAnimation(this, R.anim.ad_rotate);
        ad_scale = AnimationUtils.loadAnimation(this, R.anim.ad_scale);
        ad_translate = AnimationUtils.loadAnimation(this, R.anim.ad_translate);


    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        super.onDestroy();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(App.InitAdList) || intent.getAction().equals(App.UpdateAdList)) {
                adLists = (AdList) intent.getSerializableExtra("key");
                adEntities = adLists.getAdEntities();
                currentad = 0;

                adhandler.removeMessages(0);
                adhandler.sendEmptyMessage(0);

                adhandler.removeMessages(1);
                Log.d(TAG, "广告结束时间" + LtoDate.yMdHmsE(adLists.getEndtime() - System.currentTimeMillis()));
                adhandler.sendEmptyMessageDelayed(1, adLists.getEndtime() - System.currentTimeMillis());
            } else if (intent.getAction().equals(App.DeleteAdList)) {
                adhandler.sendEmptyMessage(1);
            }
        }
    };

    private List<AdEntities> adEntities = new ArrayList<>();
    private int currentad = 0;
    private Handler adhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                switch (msg.what) {
                    case 0:
                        hidead();
                        if (currentad < adEntities.size()) {
                            switch (adEntities.get(currentad).getAppearWay()) {
                                case 1:
                                    startAnim(ad_alpha);
                                    break;
                                case 2:
                                    startAnim(ad_translate);
                                    break;
                                case 3:
                                    startAnim(ad_scale);
                                    break;
                                case 4:
                                    startAnim(ad_rotate);
                                    break;
                            }
                            adhandler.sendEmptyMessageDelayed(0, adEntities.get(currentad).getPlaytime() * 1000);
                            currentad++;
                        } else {
                            currentad = 0;
                            adhandler.sendEmptyMessage(0);
                        }
                        break;
                    case 1:
                        Log.d(TAG, "广告结束");
                        adhandler.removeMessages(0);
                        hidead();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void startAnim(Animation animation) {
        try {
            String weizhi = adLists.getPosition();
            if (weizhi.contains("2")) {
                ad_top.startAnimation(animation);
                Picasso.with(MainActivity.this).load(adEntities.get(currentad).getNgPath()).into(ad_top);
            }
            if (weizhi.contains("3")) {
                ad_bottom.startAnimation(animation);
                Picasso.with(MainActivity.this).load(adEntities.get(currentad).getNgPath()).into(ad_bottom);
            }
            if (weizhi.contains("4")) {
                ad_left.startAnimation(animation);
                Picasso.with(MainActivity.this).load(adEntities.get(currentad).getNgPath()).into(ad_left);
            }
            if (weizhi.contains("5")) {
                ad_right.startAnimation(animation);
                Picasso.with(MainActivity.this).load(adEntities.get(currentad).getNgPath()).into(ad_right);
            }


        } catch (Exception e) {
        }


    }

    private void hidead() {
        try {
            ad_left.setImageResource(R.color.transparent);
            ad_right.setImageResource(R.color.transparent);
            ad_top.setImageResource(R.color.transparent);
            ad_bottom.setImageResource(R.color.transparent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AdList adLists = new AdList();

    private void checkad() {
        try {
            adLists = app.getAdLists();
            adEntities = adLists.getAdEntities();
            if (adLists == null)
                return;
            if (adEntities.isEmpty())
                return;

            adhandler.sendEmptyMessage(0);
            Log.d(TAG, "广告结束时间" + LtoDate.yMdHmsE(adLists.getEndtime() - System.currentTimeMillis()));
            adhandler.sendEmptyMessageDelayed(1, adLists.getEndtime() - System.currentTimeMillis());
        } catch (Exception e) {
        }

    }

    private String livetype = App.headurl + "live/type?mac=" + App.mac + "&STBtype=1";
    private String updateurl = App.headurl + "upgrade/get?mac=" + App.mac + "&STBtype=1&version=" + App.version;

    private void init() {
        req.Get(livetype, livetype);
        req.Get(updateurl, updateurl);
    }


    private GridView live_cat;


    private void find() {
        live_cat = findViewById(R.id.live_cat);
        live_cat.setOnItemClickListener(this);

        ad_left = findViewById(R.id.ad_left);
        ad_right = findViewById(R.id.ad_right);
        ad_top = findViewById(R.id.ad_top);
        ad_bottom = findViewById(R.id.ad_bottom);
    }

    private ImageView ad_left, ad_right, ad_top, ad_bottom;


    private Handler handler = new Handler();

    private List<LiveType> liveTypes = new ArrayList<>();
    private LiveTypeAdapter adapter;

    private void resetGrid() {
        if (!liveTypes.isEmpty()) {
            adapter = new LiveTypeAdapter(this, liveTypes);
            live_cat.setAdapter(adapter);
        }
    }

    private LiveType liveType;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == live_cat) {
            liveType = liveTypes.get(position);
            Bundle bundle = new Bundle();
            bundle.putSerializable("key", liveType);
            startActivity(new Intent(MainActivity.this, PlayerActivity.class)
                    .putExtras(bundle));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void finish(String tag, String json) {
        try {
            if (tag.equals(livetype)) {
                AJson<List<LiveType>> data = App.gson.fromJson(
                        json, new TypeToken<AJson<List<LiveType>>>() {
                        }.getType());
                liveTypes = data.getData();
                if (liveTypes == null)
                    return;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        resetGrid();
                    }
                });
            } else if (tag.equals(updateurl)) {
                final AJson<Update> data = App.gson.fromJson(
                        json, new TypeToken<AJson<Update>>() {
                        }.getType());
                if (data.getData() != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new ApkUpdate(MainActivity.this, data.getData().getPath()).downloadAndInstall();
                        }
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void error(String tag, String json) {

    }
}
