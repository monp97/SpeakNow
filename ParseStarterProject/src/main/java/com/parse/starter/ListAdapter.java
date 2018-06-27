package com.parse.starter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jpardogo.android.flabbylistview.lib.FlabbyLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by monikapandey on 22/06/18.
 */

public class ListAdapter extends ArrayAdapter<String> {
    private Context mContext;
    private List<String> mItems=new ArrayList<>();
    private Random mRandomizer = new Random();


    public ListAdapter(Context context, List<String> items) {
        super(context,0,items);
        mContext = context;
        mItems = items;
    }

    @Override
    public int getCount() {

        return mItems.size();

    }

    @Override
    public String getItem(int position) {

        return mItems.get(position);

    }

    @Override
    public long getItemId(int position) {

        return position;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.sentences, parent, false);

            holder = new ViewHolder(convertView);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();

        }

        int color = Color.argb(255,20, 70, 120);

        ((FlabbyLayout)convertView).setFlabbyColor(color);

        holder.text.setText(getItem(position));



        return convertView;

    }

    static class ViewHolder {
        @InjectView(R.id.text)
        TextView text;
        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}