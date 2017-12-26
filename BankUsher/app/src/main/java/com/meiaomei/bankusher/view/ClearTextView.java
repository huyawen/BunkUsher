package com.meiaomei.bankusher.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.meiaomei.bankusher.R;


/**
 * Created by huyawen on 2016/9/28.
 */
public class ClearTextView extends TextView implements TextWatcher {


    private Context mContext;
    private Drawable mDrawableRight;
    private Rect mBounds;

    public ClearTextView(Context context) {
        super(context);
        initialize(context);
    }

    public ClearTextView(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.textViewStyle);//设置textview的样式
        initialize(context);
    }

    private void initialize(Context context) {
        mContext = context;
        //获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        mDrawableRight = getCompoundDrawables()[2];
        if (mDrawableRight == null) {
            mDrawableRight = getResources().getDrawable(R.mipmap.clear);
        }
        //setBounds方法 x:组件在容器X轴上的起点 y:组件在容器Y轴上的起点 width:组件的长度 height:组件的
        mDrawableRight.setBounds(0, 0, mDrawableRight.getIntrinsicWidth(), mDrawableRight.getIntrinsicHeight());
        setClearIconVisible(false);//默认无值图标不显示
        addTextChangedListener(this);//文本内容改变的监听
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && mDrawableRight != null) {
            mBounds = mDrawableRight.getBounds();//清除图标的大小
            final int x = (int) event.getX();
            final int y = (int) event.getY();
            if (x >= (this.getWidth() - mBounds.width()) && x <= (this.getWidth() - this.getPaddingRight())
                    && y >= this.getPaddingTop() && y <= (this.getHeight() - this.getPaddingBottom())) {
                clear();
                event.setAction(MotionEvent.ACTION_CANCEL);
            }
        }

        return super.onTouchEvent(event);
    }

    public void setTextClearable(CharSequence text) {
        setText(text);
        if (text == null || text.length() == 0) {
            super.setCompoundDrawables(null, null, null, null);
        } else {
            super.setCompoundDrawables(null, null, mDrawableRight, null);
        }
    }

    private void clear() {
        setTextClearable("");//将文本内容设置为空
        super.setCompoundDrawables(null, null, null, null);
    }

    public void finalize() throws Throwable {//回收图片
        mDrawableRight = null;
        mBounds = null;
        super.finalize();
    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) {
        Drawable right = visible ? mDrawableRight : null;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(s.length()>0){
            setClearIconVisible(true);
        }else {
            setClearIconVisible(false);
        }
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {
    }
}
