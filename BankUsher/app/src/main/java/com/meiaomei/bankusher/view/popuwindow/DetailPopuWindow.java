package com.meiaomei.bankusher.view.popuwindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.activity.VipRegistActivity;
import com.meiaomei.bankusher.adapter.Adapter;
import com.meiaomei.bankusher.adapter.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 点击弹出的popuwindow弹窗
 */

public class DetailPopuWindow extends BasePopupWindow {
    private RelativeLayout pop_layout;
    private ListView lv_content;
    private Button t_down;
    private String title;
    private Button t_editer;
    LinkedHashMap<String, String> linkedHashMap;
    ArrayList<String> keyList = new ArrayList<>();
    ArrayList<String> valueList = new ArrayList<>();

    public DetailPopuWindow(Activity act, LinkedHashMap<String, String> map) {
        this(act, map, null);
    }

    public DetailPopuWindow(Activity act, LinkedHashMap<String, String> map, String title) {
        super(act, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        this.title = title;
        this.linkedHashMap = map;

        for (String key : linkedHashMap.keySet()) {
            if (!"id".equals(key)) {
                keyList.add(key);
                valueList.add(linkedHashMap.get(key));
            }
        }


    }

    @Override
    protected void setListener() {
        pop_layout.setOnClickListener(this);
        t_down.setOnClickListener(this);
        t_editer.setOnClickListener(this);
    }


    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight;
        int height = (int) (mAct.getResources().getDimension(R.dimen.x672));
        if (params.height > height) {//超過上限高度為定高
            params.height = height;
        }
        listView.setLayoutParams(params);
    }

    @Override
    protected void findViews() {
        pop_layout = (RelativeLayout) findViewById(R.id.pop_layout);
        t_down = (Button) findViewById(R.id.t_down);
        lv_content = (ListView) findViewById(R.id.lv_content);
        t_editer = (Button) findViewById(R.id.t_editer);
    }

    @Override
    protected void onShowPre() {
        super.onShowPre();
        lv_content.setAdapter(new MyAdapter(mAct, keyList, R.layout.item_popuwindow_background));
        setListViewHeightBasedOnChildren(lv_content);
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.popuwindow_detail;

    }

    @Override
    protected void initViewDrawableSelectCache() {
    }

    @Override
    protected void onItemClick(View view, PopupWindow pop) {
        switch (view.getId()) {
            case R.id.pop_layout:
                break;
            case R.id.t_down:
                pop.dismiss();
                break;
            case R.id.t_editer:
                pop.dismiss();
                Intent intent = new Intent(mAct, VipRegistActivity.class);
                HashMap<String, String> hashMap = linkedHashMap;//可以传hashmap 不可以传linkedhashmap
                intent.putExtra("linkedHashMap", hashMap);
                intent.putExtra("imgPath", title);
                mAct.startActivity(intent);
                break;
        }
    }

    class MyAdapter extends Adapter<String> {

        public MyAdapter(Context context, List<String> mDatas, int itemLayoutId) {
            super(context, mDatas, itemLayoutId);
        }

        @Override
        public void convert(ViewHolder helper, String item, int position) {
            ((TextView) helper.getView(R.id.tv_key)).setText(keyList.get(position));
            ((TextView) helper.getView(R.id.tv_value)).setText(valueList.get(position));
        }
    }
}
