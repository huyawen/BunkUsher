package com.meiaomei.bankusher.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.utils.DensityUtil;


/**
 * Created by huyawen on 2016/4/16.
 * <p>
 * 提示弹窗的公共类
 */

public class AlertDialogCommon {
    private Builder mBuilder;
    private Context context;//上下文
    private int lineNo = 0;//行数
    private int windowsHeight = 0; //手机屏幕高度
    private int windowsWidth = 0; //手机屏幕宽度

    public AlertDialogCommon(Builder mBuilder) {
        this.mBuilder = mBuilder;
        this.context = mBuilder.mContext;
    }

    public void createAlertDialog() {
        final AlertDialog dlg = new AlertDialog.Builder(context).create();
        if (!mBuilder.ifDismiss) {//设置对话框为点击对话框外区域时对话框不消失
            dlg.setCanceledOnTouchOutside(false);
            dlg.setCancelable(false);
        }
        dlg.show();
        Window window = dlg.getWindow();
        lineNo = measureHeight(mBuilder.contents);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);//获取窗口管理对象
        WindowManager.LayoutParams p = window.getAttributes(); // 获取对话框当前的参数值
        if (lineNo > 16) {
            windowsHeight = wm.getDefaultDisplay().getHeight();//获取屏幕高度
            p.height = (int) (windowsHeight * 0.535); // 高度设置为屏幕的0.3
        }
        windowsWidth = wm.getDefaultDisplay().getWidth();//获取屏幕宽度
        p.width = (int) (windowsWidth * 0.345); // 宽度设置为屏幕宽的
        window.setAttributes(p);//设置属性
        window.setContentView(R.layout.dialog_base);

        if (mBuilder.title != null) {  //设置标题显示
            TextView title = (TextView) window.findViewById(R.id.tv_title);
            title.setText(mBuilder.title);
            if (mBuilder.titleSize != 0) {//设置标题尺寸
                title.setTextSize(mBuilder.titleSize);
            }
            if (mBuilder.titleColor != 0) {//设置标题颜色
                title.setTextColor(mBuilder.titleColor);
            }
        }

        if (mBuilder.contents != null) {  //设置内容显示
            if (lineNo > 16) {  //多于16行的处理
                ScrollView scrollView = (ScrollView) window.findViewById(R.id.sv_base);
                ViewGroup.LayoutParams lp = scrollView.getLayoutParams();
                lp.height = (int) ((windowsHeight * 0.6 * 2) / 3);
                scrollView.setLayoutParams(lp);
            }
            for (int i = 0; i < mBuilder.contents.length; i++) {
                TextView tv = new TextView(context);
                RelativeLayout rl_content = (RelativeLayout) window.findViewById(R.id.ly_detail);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                layoutParams.topMargin = DensityUtil.dip2px(context, 25);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                layoutParams.leftMargin = DensityUtil.dip2px(context, 20);//距离左端
                layoutParams.rightMargin = DensityUtil.dip2px(context, 10);//距离右端
                tv.setText(mBuilder.contents[i]);
                tv.setLineSpacing(2, 1.3f);
                if (mBuilder.contentSize == 0) {  //设置文字大小
                    tv.setTextSize(14);//默认字体大小
                } else {
                    tv.setTextSize(mBuilder.contentSize);
                }
                if (mBuilder.contentColor == 0) { //设置文本颜色
                    tv.setTextColor(context.getResources().getColor(R.color.alertTextContent));//默认字体颜色
                } else {
                    tv.setTextColor(context.getResources().getColor(mBuilder.contentColor));
                }
                tv.setId(i + 1); //添加Id
                tv.setLayoutParams(layoutParams);//添加布局参数
                rl_content.addView(tv);
            }
        }

        //取消按钮是否显示，默认是隐藏
        Button btn_cancel = (Button) window.findViewById(R.id.btn_cancel);
        LinearLayout ll = (LinearLayout) window.findViewById(R.id.linear_ll);

        if (mBuilder.cancleBtnText != null) { //取消按钮文字
            btn_cancel.setText(mBuilder.cancleBtnText);
        }
        if (mBuilder.isShowCancelBtn) { //取消按钮是否显示
            ll.setVisibility(View.VISIBLE);//取消按钮和两个按钮之间的竖线显示
            btn_cancel.setOnClickListener(new View.OnClickListener() {  //取消按钮点击事件
                @Override
                public void onClick(View v) {
                    dlg.cancel();
                    if (mBuilder.cancleClickListener != null) {
                        mBuilder.cancleClickListener.cancleButtonClickListener();
                    }
                }
            });
        } else {
            ll.setVisibility(View.GONE);//取消按钮和两个按钮之间的竖线隐藏
        }

        //确定按钮点击处理
        Button btn_submit_single = (Button) window.findViewById(R.id.btn_submit_single);
        if (mBuilder.submitBtnText != null) {//确定按钮文字控制
            btn_submit_single.setText(mBuilder.submitBtnText);
        }
        btn_submit_single.setVisibility(View.VISIBLE);
        btn_submit_single.setOnClickListener(new View.OnClickListener() {  //确定按钮点击事件
            @Override
            public void onClick(View v) {
                dlg.cancel();
                if (mBuilder.submitClickListener != null) {
                    mBuilder.submitClickListener.submitButtonClickListener();
                }
            }
        });

        //dialog出现时，点back键时的回调
        if (mBuilder.onCancleListener != null) {
            dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    dialog.dismiss();
                    mBuilder.onCancleListener.onCancleCallBack();
                }
            });
        }

    }

    //获取行数
    public int measureHeight(String[] contents) {
        int lineNo = 0;
        for (int i = 0; i < contents.length; i++) {
            if (contents[i].length() == 0) {
                lineNo++;
            } else {
                if (contents[i].length() % 20 != 0) {
                    lineNo += (contents[i].length() / 20) + 1;
                } else {
                    lineNo += contents[i].length() / 20;
                }
            }
        }
        return lineNo;
    }

    public static class Builder {
        private Context mContext;
        private String title;  //标题
        private int titleSize = 0;  //标题大小
        private int titleColor = 0; //标题颜色
        private String[] contents;//内容
        private int contentSize = 0;  //内容大小
        private int contentColor = 0; //内容的颜色
        private boolean isShowCancelBtn = false;//是否显示取消按钮
        private boolean ifDismiss = true;//当点击对话框外区域时候，对话框是否消失
        private boolean centerHorizentle = false;//是否位于中央显示
        private DialogSubmitClickListener submitClickListener;//点击确定按钮事件处理
        private DialogCancleClickListener cancleClickListener; //点击取消事件的处理
        private DialogOnCancleListener onCancleListener;//对话框要消失的回调
        private String submitBtnText;//确定按钮显示内容
        private String cancleBtnText;//取消按钮显示内容

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setTitle(String title) {    //设置title
            this.title = title;
            return this;
        }

        public Builder setSubmitBtnText(String submitBtnText) {
            this.submitBtnText = submitBtnText;
            return this;
        }

        public Builder setCancleBtnText(String cancleBtnText) {
            this.cancleBtnText = cancleBtnText;
            return this;
        }

        public Builder setTitleSize(int titleSize) {    //设置title大小
            this.titleSize = titleSize;
            return this;
        }

        public Builder setTitleColor(int titleColor) {    //设置title颜色
            this.titleColor = titleColor;
            return this;
        }

        public Builder setSubmitClickListener(DialogSubmitClickListener submitClickListener) { //设置确定按钮点击事件
            this.submitClickListener = submitClickListener;
            return this;
        }

        public Builder setCancleClickListener(DialogCancleClickListener cancleClickListener) { //设置取消按钮点击事件
            this.cancleClickListener = cancleClickListener;
            return this;
        }

        public Builder setOnCancleListener(DialogOnCancleListener onCancleListener) {//dialog出现时，设置back键的点击事件
            this.onCancleListener = onCancleListener;
            return this;
        }

        public Builder setContents(String[] contents) {    //设置内容
            this.contents = contents;
            return this;
        }

        public Builder setContentSize(int contentSize) {    //设置内容大小
            this.contentSize = contentSize;
            return this;
        }

        public Builder setContentColor(int contentColor) {    //设置内容颜色
            this.contentColor = contentColor;
            return this;
        }

        public Builder setIsShowCancelBtn(boolean isShowCancelBtn) { //是否显示取消按钮
            this.isShowCancelBtn = isShowCancelBtn;
            return this;
        }

        public Builder setIfDismiss(boolean ifDismiss) {    //点击回退键是否消失
            this.ifDismiss = ifDismiss;
            return this;
        }

        public Builder setCenterHorizentle(boolean centerHorizentle) {    //设置水平居中属性
            this.centerHorizentle = centerHorizentle;
            return this;
        }

        public AlertDialogCommon build() {
            return new AlertDialogCommon(this);
        }
    }

    public interface DialogCancleClickListener {
        public void cancleButtonClickListener(); //取消按钮点击事件
    }

    public interface DialogOnCancleListener {
        public void onCancleCallBack();//dialog出现时，点back键时的回调
    }

    public interface DialogSubmitClickListener {
        public void submitButtonClickListener(); //确定按钮点击事件
    }

}
