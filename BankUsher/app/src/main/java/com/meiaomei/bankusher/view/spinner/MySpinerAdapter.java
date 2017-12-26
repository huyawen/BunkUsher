package com.meiaomei.bankusher.view.spinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meiaomei.bankusher.R;

import java.util.ArrayList;
import java.util.List;

public class MySpinerAdapter<T> extends BaseAdapter {

    private Context mContext;
    private List<T> mObjects = new ArrayList<T>();

    private LayoutInflater mInflater;

    public MySpinerAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * 刷新数据
     * @param objects
     * @param selIndex
     */
    public void refreshData(List<T> objects, int selIndex) {
        mObjects = objects;
        if (selIndex < 0) {
            selIndex = 0;
        }
        if (selIndex >= mObjects.size()) {
            selIndex = mObjects.size() - 1;
        }
    }


    @Override
    public int getCount() {
        return mObjects.size();
    }

    @Override
    public Object getItem(int pos) {
        return mObjects.get(pos).toString();
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup arg2) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.spiner_item_layout, null);
            viewHolder = new ViewHolder();
            viewHolder.mTextView = (TextView) convertView.findViewById(R.id.textView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String item = (String) getItem(pos);
        viewHolder.mTextView.setText(item);
        return convertView;
    }

    public static class ViewHolder {
        public TextView mTextView;
    }

    /**
     * 提供一个设置点击事件监听的接口回调
     */
    public interface IOnItemSelectListener {
        void onItemClick(int pos);
    }

}
