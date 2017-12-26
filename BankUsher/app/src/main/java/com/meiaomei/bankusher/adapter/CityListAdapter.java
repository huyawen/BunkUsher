package com.meiaomei.bankusher.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.entity.City;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by next on 2016/3/25.
 */
public class CityListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<City> allCities;
    private HotCityAdapter hotCityAdapter; //热门城市
    private RecentCityAdapter  recentCityAdapter; //热门城市
    private String[] firstLetterArray;// 存放存在的汉语拼音首字母
    private Map<String, Integer> letterIndex;
    private final int VIEW_TYPE = 5;
    public ItemClick itemClick;


    public CityListAdapter(Context context, List<City> allCities, RecentCityAdapter recentCityAdapter, Map<String, Integer> letterIndex, HotCityAdapter hotCityAdapter) {
        this.context = context;
        this.allCities = allCities;
        this.recentCityAdapter=recentCityAdapter;
        this.letterIndex = letterIndex;
        this.hotCityAdapter=hotCityAdapter;
        inflater = LayoutInflater.from(this.context);

        setup();
    }

    public void setItemClick(ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    private void setup() {
        firstLetterArray = new String[allCities.size()];
        for (int i = 0; i < allCities.size(); i++) {
            // 当前汉语拼音首字母
            String currentStr = getAlpha(allCities.get(i).getPinyin());
            // 上一个汉语拼音首字母，如果不存在为" "
            String previewStr = (i - 1) >= 0 ? getAlpha(allCities.get(i - 1).getPinyin()) : " ";
            if (!previewStr.equals(currentStr)) {
                String name = getAlpha(allCities.get(i).getPinyin());
                letterIndex.put(name, i);
                firstLetterArray[i] = name;
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE;
    }

    @Override
    public int getItemViewType(int position) {
        return position < 4 ? position : 4;
    }

    @Override
    public int getCount() {
        return allCities.size();
    }

    @Override
    public Object getItem(int position) {
        return allCities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        if (viewType == 0) {//定位
            convertView = inflater.inflate(R.layout.item_city_location, null);
        } else if (viewType == 1) {//最近访问
            convertView = inflater.inflate(R.layout.item_city_grid, null);
            GridView recentCityView = (GridView) convertView.findViewById(R.id.grid_city);
            recentCityView.setAdapter(recentCityAdapter);
            TextView recentHint = (TextView) convertView.findViewById(R.id.recentHint);
            recentHint.setText("最近访问的城市");
        } else if (viewType == 2) {//热门城市
            convertView = inflater.inflate(R.layout.item_city_grid, null);
            final GridView hotCity = (GridView) convertView.findViewById(R.id.grid_city);
            hotCity.setAdapter(hotCityAdapter);
            TextView hotHint = (TextView) convertView.findViewById(R.id.recentHint);
            hotHint.setText("热门城市");
        } else if (viewType == 3) {
            convertView = inflater.inflate(R.layout.item_city_total_tag, null);
        } else {
            final Holder holder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_city_list, null);
                holder = new Holder();
                holder.letter = (TextView) convertView.findViewById(R.id.tv_letter);
                holder.name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            if (position >= 1) {
                holder.name.setText(allCities.get(position).getName());
                holder.name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemClick!=null) {
                            itemClick.onItemClick(allCities.get(position).getName());
                        }
                    }
                });
                String currentStr = getAlpha(allCities.get(position).getPinyin());
                String previewStr = (position - 1) >= 0 ? getAlpha(allCities.get(position - 1).getPinyin()) : " ";
                if (!previewStr.equals(currentStr)) {
                    holder.letter.setVisibility(View.VISIBLE);
                    holder.letter.setText(currentStr);
                } else {
                    holder.letter.setVisibility(View.GONE);
                }
            }
        }

        return convertView;
    }

    class Holder {
        TextView letter, name;
    }

    public  interface  ItemClick{
        void onItemClick(String cityStr);
    }


    // 获得汉语拼音首字母
    private String getAlpha(String str) {
        if (str == null) {
            return "#";
        }
        if (str.trim().length() == 0) {
            return "#";
        }
        char c = str.trim().substring(0, 1).charAt(0);
        // 正则表达式，判断首字母是否是英文字母
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        if (pattern.matcher(c + "").matches()) {
            return (c + "").toUpperCase();
        } else if (str.equals("0")) {
            return "定位";
        } else if (str.equals("1")) {
            return "最近";
        } else if (str.equals("2")) {
            return "热门";
        } else if (str.equals("3")) {
            return "全部";
        } else {
            return "#";
        }
    }
}
