package com.meiaomei.bankusher.view;

import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 常用城市和历史城市选择的适配器
 */
public class NoScrollGridView extends GridView {

    public NoScrollGridView(android.content.Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置不滚动
     */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
