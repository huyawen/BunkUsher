package com.meiaomei.bankusher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.entity.City;

import java.util.List;


/**
 * Created by next on 2016/3/25.
 */
public class HotCityAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<City> hotCities;
    ItemHotClick itemHotClick;

    public HotCityAdapter(Context context, List<City> hotCities) {
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        this.hotCities = hotCities;
    }

    public void setItemHotClick(ItemHotClick itemHotClick) {
        this.itemHotClick = itemHotClick;
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
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemHotClick!=null) {
                    itemHotClick.onItemClick(hotCities.get(position).getName());
                }
            }
        });
        city.setText(hotCities.get(position).getName());
        return convertView;
    }

    public  interface  ItemHotClick{
        void onItemClick(String cityStr);
    }
}
