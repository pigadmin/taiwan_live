package com.live.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.live.R;
import com.live.bean.Live;

import java.util.ArrayList;
import java.util.List;


public class LiveListAdapter extends BaseAdapter {
    private Activity activity;

    private List<Live> list = new ArrayList<>();

    public LiveListAdapter(Activity activity, List<Live> list) {
        // TODO Auto-generated constructor stub
        this.activity = activity;
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
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.adapter_livelist, null);
            holder.livelist_no = convertView
                    .findViewById(R.id.livelist_no);
            holder.livelist_name = convertView
                    .findViewById(R.id.livelist_name);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.livelist_no.setText(liveno(position));

        holder.livelist_name.setText(list.get(position).getName());

        return convertView;

    }

    private String liveno(int position) {
        if (position < 10) {
            return "00" + (position + 1);
        } else if (position < 100) {
            return "0" + (position + 1);
        }
        return (position + 1) + "";
    }

    public class ViewHolder {
        private TextView livelist_no;
        private TextView livelist_name;

    }

}
