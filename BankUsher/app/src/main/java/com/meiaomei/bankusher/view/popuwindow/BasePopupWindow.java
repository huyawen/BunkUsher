package com.meiaomei.bankusher.view.popuwindow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.meiaomei.bankusher.R;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("UseSparseArrays")
public abstract class BasePopupWindow implements OnClickListener {
    protected PopupWindow mPop;
    protected View mContentView;
    protected Activity mAct;
    protected int mWidth = -2;
    protected int mHeight = -2;
    private String mTag;
    protected boolean mDismiss = true;
    protected Map<Integer, Integer> mViewDrawableSelectCache;
    protected boolean openAnimation;

    public BasePopupWindow(Activity act, int width, int height) {//宽高充满屏幕
        mAct = act;
        mWidth = width;
        mHeight = height;
        init();
    }


    private void init() {
        onCreate();
        findViews();
        setListenerNative();
        initViewDrawableSelectCache();
        loadData();
        initPopAnimationStyle();
    }

    protected void initPopAnimationStyle() {
        if (openAnimation) {
            mPop.setAnimationStyle(R.style.listPopAnimation);//设置弹窗出现的动画效果
        }
    }

    private void setListenerNative() {
        mPop.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mAct instanceof ICoveringLayer) {
                    ICoveringLayer layout = (ICoveringLayer) mAct;
                    // 影藏activity的覆盖层
                    layout.hideCoveringLayer();
                }
            }
        });
        setListener();
    }

    protected void loadData() {
    }

    protected abstract void setListener();


    protected abstract void findViews();

    protected void onCreate() {
        mViewDrawableSelectCache = new HashMap<Integer, Integer>();
        PopupWindow pop = mPop = createPopWindow();
        mPop.setAnimationStyle(R.style.listPopAnimation);// 设置窗口显示的动画效果
        pop.setBackgroundDrawable(new ColorDrawable(0));// 点击窗口外消失
        pop.setFocusable(true);
        pop.setTouchable(true);
        pop.setOutsideTouchable(true);// 点击窗口外消失,需要设置背景、焦点、touchable、update
    }

    protected PopupWindow createPopWindow() {
        if (mContentView == null) {
            int id = getContentViewLayoutId();
            mContentView = inflate(id, null, true);
        }
        return new PopupWindow(mContentView, mWidth, mHeight);
    }

    @SuppressWarnings("unchecked")
    protected <T> T findViewById(int id) {
        return (T) mContentView.findViewById(id);
    }

    protected abstract int getContentViewLayoutId();

    protected ViewGroup inflate(int id, ViewGroup root, boolean attachToRoot) {
        return (ViewGroup) mAct.getLayoutInflater().inflate(id, root, attachToRoot);
    }

    @SuppressWarnings("unused")
    private String getTag() {
        if (TextUtils.isEmpty(mTag)) {
            mTag = getClass().getCanonicalName();
        }
        return mTag;
    }

    public View getContentView() {
        return mContentView;
    }

    public boolean isShow() {
        return mPop.isShowing();
    }

    /**
     * 初始化view在点击时的图片集合 key view的id value view所对应被点击时的背景图片
     *
     * @return
     */
    protected abstract void initViewDrawableSelectCache();

    @Override
    public void onClick(View v) {
        Integer resid = mViewDrawableSelectCache.get(v.getId());
        if (resid != null) {
            v.setBackgroundResource(resid);
        } else {
            int color = getDefaultBackgroundColor();
            if (color != -1) {
                v.setBackgroundColor(color);
            }
        }
        onItemClick(v);
    }

    protected int getDefaultBackgroundColor() {
        return -1;
    }

    private void onItemClick(final View v) {
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mDismiss) {
                    mPop.dismiss();
                }
                onItemClick(v, mPop);
            }
        }, 100);
    }

    protected abstract void onItemClick(View view, PopupWindow pop);

    public void show() {
        onShowPre();
        ShowLocation location = getShowLocation();
        if (mAct instanceof ICoveringLayer) {
            ICoveringLayer layout = (ICoveringLayer) mAct;
            // 显示activity的覆盖层(遮盖)
            layout.showCoveringLayer();
        }
        location.parent = location.parent == null ? mAct.getWindow().getDecorView() : location.parent;
        PopupWindow pop = mPop;
        pop.showAtLocation(location.parent, location.gravity, location.x, location.y);
        pop.setFocusable(false);
        pop.update();
        location.parent.post(new Runnable() {

            @Override
            public void run() {
                onShowPost();
            }
        });
    }


    protected void startShowAnimition() {
    }

    public void dismiss() {
        startDismissAnimationAndDismiss();
    }

    protected void startDismissAnimationAndDismiss() {
        mPop.dismiss();
    }

    /**
     * PopupWindow显示之前调用
     */
    protected void onShowPre() {
    }

    /**
     * PopupWindow显示之后调用
     */
    protected void onShowPost() {
        startShowAnimition();
    }

    protected ShowLocation getShowLocation() {
        return new ShowLocation(mAct.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    protected class ShowLocation {
        public int x;
        public int y;
        public int gravity = Gravity.NO_GRAVITY;
        public View parent;

        public ShowLocation(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public ShowLocation(int gravity, int x, int y) {
            this(x, y);
            this.gravity = gravity;
        }

        public ShowLocation(View parent, int gravity, int x, int y) {
            this(gravity, x, y);
            this.parent = parent;
        }

    }

    public void setAnimationStyle(int animationStyle) {
        mPop.setAnimationStyle(animationStyle);
    }

    /**
     * @author Administrator
     *         显示关闭POP时原ACTIVITY是否显示关闭覆盖层
     */
    public interface ICoveringLayer {
        /**
         * 显示覆盖层
         */
        void showCoveringLayer();

        /**
         * 隐藏覆盖层
         */
        void hideCoveringLayer();
    }


}
