package com.live.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.live.R;
import com.live.bean.LiveType;
import com.live.ui.BaseActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class LiveTypeAdapter extends BaseAdapter {
    private Context context;
    private List<LiveType> list = new ArrayList<>();

    public LiveTypeAdapter(Context context, List<LiveType> list) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.adapter_livetype, null);
            holder.livetype_icon = convertView
                    .findViewById(R.id.livetype_icon);
            holder.livetype_name = convertView
                    .findViewById(R.id.livetype_name);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.livetype_name.setText(addo(position) + list.get(position).getName());

        Picasso.with(context).load(list.get(position).getIcon()).error(R.mipmap.desk_icon).into(holder.livetype_icon);

        return convertView;

    }

    private String addo(int position) {
        if (position <= 9) {
            return "00" + (position + 1) + " ";
        } else if (position <= 99) {
            return "0" + (position + 1) + " ";
        }
        return (position + 1) + " ";
    }


    public class ViewHolder {
        private ImageView livetype_icon;
        private TextView livetype_name;

    }

}
