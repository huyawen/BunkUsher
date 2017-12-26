package com.meiaomei.bankusher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meiaomei.bankusher.R;

import java.util.List;

/**
 * Created by next on 2016/3/25.
 */
public class RecentCityAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<String> hotCities;
    ItemRecentClick itemRecentClick;

    public RecentCityAdapter(Context context, List<String> hotCities) {
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        this.hotCities = hotCities;
    }

    public void setItemRecentClick(ItemRecentClick itemRecentClick) {
        this.itemRecentClick = itemRecentClick;
    }

    @Override
    public int getCount() {
        return hotCities.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_city_cell, null);
        TextView city = (TextView) convertView.findViewById(R.id.city);
        city.setText(hotCities.get(position));
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemRecentClick!=null) {
                    itemRecentClick.onItemClick(hotCities.get(position));
                }
            }
        });
        return convertView;
    }

    public  interface  ItemRecentClick{
        void onItemClick(String cityStr);
    }
}