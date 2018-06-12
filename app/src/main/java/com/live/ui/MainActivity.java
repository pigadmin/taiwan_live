package com.live.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.List;

public class MainActivity extends BaseActivity {

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
    }


    public void onEvent(DataMessage event) {
        if (event.getApi().equals(Req.type)) {
            AJson<List<LiveType>> data = App.gson.fromJson(
                    event.getData(), new TypeToken<AJson<List<LiveType>>>() {
                    }.getType());
            liveType = data.getData();
            resetGrid();
        }
    }

    private List<LiveType> liveType;
    LiveTypeAdapter adapter;

    private void resetGrid() {
        adapter = new LiveTypeAdapter(this, liveType);
        live_cat.setAdapter(adapter);

    }

}
