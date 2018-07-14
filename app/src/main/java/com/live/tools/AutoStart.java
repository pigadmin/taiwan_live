package com.live.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.live.R;
import com.live.app.App;
import com.live.app.Req;
import com.live.ui.WelcomeActivity;

public class AutoStart extends BroadcastReceiver {
    String TAG = "AutoStart";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // context.startActivity(new Intent(context, WelcomeActivity.class)
            // .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")) {
            String packageName = intent.getData().getSchemeSpecificPart();
            String url = App.headurl + "upgrade/set?mac=" + App.mac + "&STBtype=1&version=" + App.version;
            new Req(null).Get(url, url);
            if (context.getPackageName().equals(packageName)) {
                System.out.println("升级完成" + context.getPackageName());
                Toast.makeText(context, context.getString(R.string.update_tips) + " Version:" + App.version, Toast.LENGTH_LONG).show();
                context.startActivity(new Intent(context, WelcomeActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        }
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            String packageName = intent.getDataString();
        }
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            String packageName = intent.getDataString();
        }
    }

}
