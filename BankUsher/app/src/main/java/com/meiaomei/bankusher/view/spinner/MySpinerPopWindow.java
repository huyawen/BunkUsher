package com.meiaomei.bankusher.view.spinner;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.meiaomei.bankusher.R;

import java.util.List;

/**
 * 下拉列表弹窗
 */
public class MySpinerPopWindow extends PopupWindow implements OnItemClickListener {

    private Context mContext;
    private ListView mListView;
    private MySpinerAdapter mAdapter;
    private MySpinerAdapter.IOnItemSelectListener mItemSelectListener;


    public MySpinerPopWindow(Context context) {
        super(context);
        mContext = context;
        init();
    }

    /**
     * 为MySpinnerView提供一个item点击事件的监听器，MySpinnerView实例调用此方法设置监听
     *
     * @param listener
     */
    public void setItemListener(MySpinerAdapter.IOnItemSelectListener listener) {
        mItemSelectListener = listener;
    }

    /**
     * 初始化弹窗布局
     */
    private void init() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.spiner_window_layout, null);
        setContentView(view);
        setWidth(LayoutParams.WRAP_CONTENT);
        setHeight(LayoutParams.WRAP_CONTENT);

        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);

        mListView = (ListView) view.findViewById(R.id.listview);

        mAdapter = new MySpinerAdapter(mContext);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        this.setOnDismissListener(




                new OnDismissListener() { // 弹窗消失的监听
            @Override
            public void onDismiss() {
                if (mItemSelectListener != null) {
                    mItemSelectListener.onItemClick(-1); // 为什么这么做？弹窗消失，要让布局那边的箭头还原向下，所以那边还得判断一下，>=0的数据在显示，-1就忽略掉，箭头还原
                }
            }
        });
    }

    /**
     * 刷新下拉列表的数据
     *
     * @param list
     * @param selIndex
     */
    public void refreshData(List<String> list, int selIndex) {
        if (list != null && selIndex != -1) {
            mAdapter.refreshData(list, selIndex);
        }
    }

    /**
     * 下拉列表ListView的点击事件。
     *
     * @param arg0
     * @param view
     * @param pos
     * @param arg3
     */
    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
        dismiss();
        if (mItemSelectListener != null) {
            mItemSelectListener.onItemClick(pos); //点击后调用此方法，则MySpinerView会监听到（因为mItemSelectListener是MySpinerView那边设置的监听器）
        }
    }
}
