package com.live.ui;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.live.R;
import com.live.app.App;
import com.live.app.Req;
import com.live.bean.AJson;
import com.live.bean.LiveType;
import com.live.event.DataMessage;
import com.live.ui.adapter.LiveTypeAdapter;
import com.live.ui.play.PlayerActivity;

import java.util.List;

public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        find();
        init();
    }

    private void init() {
        type();

    }

    private void type() {
        Req.get(Req.type);
    }

    private GridView live_cat;

    private void find() {
        live_cat = findViewById(R.id.live_cat);
        live_cat.setOnItemClickListener(this);
    }


    public void onEvent(DataMessage event) {
        if (event.getApi().equals(Req.type)) {
            AJson<List<LiveType>> data = App.gson.fromJson(
                    event.getData(), new TypeToken<AJson<List<LiveType>>>() {
                    }.getType());
            liveTypes = data.getData();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    resetGrid();
                }
            });
        }
    }

    Handler handler = new Handler();

    private List<LiveType> liveTypes;
    LiveTypeAdapter adapter;

    private void resetGrid() {
        adapter = new LiveTypeAdapter(this, liveTypes);
        live_cat.setAdapter(adapter);

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
}
