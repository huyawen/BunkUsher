package com.meiaomei.bankusher.dialog;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meiaomei.bankusher.R;


/**
 * Created by huyawen on 2017/6/10.
 */
public class MyProgressDialog {

    private ObjectAnimator mOjectAnimator;
    ImageView spaceshipImage=null;
    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @return
     */
    public Dialog createLoadingDialog(Context context, String content) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.layout_loading_dialog, null); // 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view); // 加载布局
        spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView textView= (TextView) v.findViewById(R.id.tv_content);
        startLoadingAnimator();
        textView.setText(content);
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog); // 创建自定义样式dialog
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return loadingDialog;
    }

    private  void startLoadingAnimator() {
        if(mOjectAnimator==null){
            mOjectAnimator = ObjectAnimator.ofFloat(spaceshipImage, "rotation", 0f, 360f);
        }
        spaceshipImage.setVisibility(View.VISIBLE);
        mOjectAnimator.setDuration(1200);
        mOjectAnimator.setRepeatCount(-1);
        mOjectAnimator.start();
    }
}
