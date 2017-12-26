package com.meiaomei.bankusher.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.meiaomei.bankusher.R;

/**
 * Created by huyawen on 2017/11/30.
 * email:1754397982@qq.com
 */

public class ToastUtils {




    public static void showToast(String title, Context context,int duration){
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.book_reading_seekbar_toast, null);
        TextView percentageTV = (TextView) view.findViewById(R.id.tv_down);
        percentageTV.setText(title);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(duration);
        toast.setView(view);
        toast.show();
    }
}
