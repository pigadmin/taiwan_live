package com.live.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.logging.Logger;

import de.greenrobot.event.EventBus;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }



}
