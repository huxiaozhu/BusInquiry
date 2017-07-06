package com.hch.businquiry.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hch.businquiry.R;

import java.util.List;

/**
 * Created by Administrator on 2017/4/20.
 */

public class GroupAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<String> history;
    public GroupAdapter(Context context,List<String> history){
        this.history = history;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return history.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.history_item, null);
            holder = new ViewHolder();
            holder.key = (TextView) convertView.findViewById(R.id.key_tv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.key.setText(history.get(position));
        return convertView;
    }
    private class ViewHolder {

        private TextView key;
    }
}
