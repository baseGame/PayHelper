package com.tools.payhelper.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tools.payhelper.R;

import java.util.List;

public class LogListAdapter extends BaseAdapter {

    private Context mCtx;

    private List<String> datas;

    public LogListAdapter(Context context, List<String> datas) {
        mCtx = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mCtx).inflate(R.layout.log_item, null);
        }
        TextView logText = (TextView) convertView.findViewById(R.id.item_text);
        logText.setText(datas.get(position));
        return convertView;
    }
}
