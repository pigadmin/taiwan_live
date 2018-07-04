package com.live.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.gson.reflect.TypeToken;
import com.live.R;
import com.live.app.App;
import com.live.app.Req;
import com.live.bean.AJson;
import com.live.bean.WelcomeAd;
import com.live.event.DataMessage;
import com.live.event.ErrorMessage;
import com.live.tools.FULL;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends BaseActivity implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        find();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netreceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        unregisterReceiver(netreceiver);
    }

    private BroadcastReceiver netreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    ConnectivityManager connectivityManager = (ConnectivityManager) context
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetworkInfo = connectivityManager
                            .getActiveNetworkInfo();
                    int network_type = activeNetworkInfo.getType();
                    System.out.println("网络类型&Net Type：" + network_type);
                    if (network_type > -1) {
                        init();
                    }
                }
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
    };


    private ImageView ad_image;
    private VideoView ad_video;
    private TextView ad_time;
    private TextView ad_tips;
    private MediaPlayer mediaPlayer;

    private void find() {
        ad_image = findViewById(R.id.ad_image);
        ad_video = findViewById(R.id.ad_video);
        FULL.star(ad_video);
        ad_video.setOnPreparedListener(this);
        ad_video.setOnErrorListener(this);
        ad_video.setOnCompletionListener(this);


        ad_time = findViewById(R.id.ad_time);
        ad_tips = findViewById(R.id.ad_tips);

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playmusic();
            }
        });
    }

    private String url;

    private void init() {
        url = App.headurl + "open/ad?mac=" + App.mac + "&STBtype=1";
        Req.get(url);
    }

    private void toClass() {
        startActivity(new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    private int currentad;
    private WelcomeAd ad;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (playtime > 0) {
                        if (!ad_tips.isShown()) {
                            ad_tips.setVisibility(View.VISIBLE);
                        }
                        ad_time.setText(playtime + "");
                        playtime--;
                        handler.sendEmptyMessageDelayed(0, 1 * 1000);
                    } else {
                        toClass();
                    }
                    break;
                case 1:

                    if (currentad < welcomeAds.size()) {
                        ad = welcomeAds.get(currentad);
                        switch (ad.getAd().getType()) {
                            case 1:
                                if (ad_image.isShown()) {
                                    ad_image.setVisibility(View.GONE);
                                }
                                if (!ad_video.isShown()) {
                                    ad_video.setVisibility(View.VISIBLE);
                                }
                                videourl = ad.getAd().getPath();
                                playvideo();
                                break;
                            case 2:
                                if (!ad_image.isShown()) {
                                    ad_image.setVisibility(View.VISIBLE);
                                }
                                if (ad_video.isShown()) {
                                    ad_video.setVisibility(View.GONE);
                                }
                                videourl = ad.getAd().getBackFile();
                                Picasso.with(WelcomeActivity.this).load(ad.getAd().getPath()).into(ad_image);
                                playmusic();
                                break;
                        }
                        handler.sendEmptyMessageDelayed(1, ad.getPlayTime() * 1000);
                        currentad++;
                    }
//                    else {//循环取消注释
//                        currentad = 0;
//                        handler.sendEmptyMessage(1);
//                    }

                    break;
            }
        }
    };
    private int playtime;
    private List<WelcomeAd> welcomeAds = new ArrayList<WelcomeAd>();

    public void onEvent(DataMessage event) {
        if (event.getApi().equals(url)) {
            try {
                AJson<List<WelcomeAd>> data = App.gson.fromJson(
                        event.getData(), new TypeToken<AJson<List<WelcomeAd>>>() {
                        }.getType());
                welcomeAds = data.getData();
                if (!welcomeAds.isEmpty()) {
                    for (WelcomeAd ad : data.getData()) {
                        playtime += ad.getPlayTime();
                    }
                    handler.sendEmptyMessage(0);
                    handler.sendEmptyMessage(1);
                } else {
                    toClass();
                }
            } catch (Exception e) {
                toClass();
                e.printStackTrace();
            }

        }
    }

    private boolean isto;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != KeyEvent.KEYCODE_VOLUME_DOWN && keyCode != KeyEvent.KEYCODE_VOLUME_UP && keyCode != KeyEvent.KEYCODE_VOLUME_MUTE) {
            if (!isto) {
                isto = !isto;
                handler.removeMessages(0);
                handler.removeMessages(1);
                toClass();
            }
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onCompletion(MediaPlayer mp) {
        playvideo();
    }

    private String videourl;

    private void playvideo() {

        if (!videourl.equals("")) {
            System.out.println(videourl);
            ad_video.setVideoURI(Uri.parse(videourl));
        }
    }

    private void playmusic() {
        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(WelcomeActivity.this,
                    Uri.parse(videourl));
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();

    }
}
