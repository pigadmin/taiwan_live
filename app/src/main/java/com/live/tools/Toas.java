package com.live.tools;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.live.R;

public class Toas {
    private View view;
    private Toast toast;


    public void show(Context context) {
        try {
            view = LayoutInflater.from(context).inflate(R.layout.toast, null);
            TextView mytoast = (TextView) view.findViewById(R.id.toast_content);
            mytoast.setText(getMsg());
            toast = new Toast(context);
            toast.setGravity(getGravity(), getX(), getY());
            toast.setDuration(this.getDuration());
            toast.setView(view);
            toast.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    private int x = 10;
    private int y = 10;
    private int duration = 1;
    private String msg;
    private int gravity = Gravity.BOTTOM | Gravity.RIGHT;
}
