package com.meiaomei.bankusher.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.meiaomei.bankusher.utils.DownUtils;

/**
 * Created by huyawen on 2018/1/15.
 * email:1754397982@qq.com
 *
 * 设置下载进度条的服务
 */

public class DownLoadService extends Service {


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final   String path=intent.getStringExtra("path");
        Log.e("========","path   service"+path);
        final Context context=this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                new DownUtils(context).getData(path,"bankUser.apk");
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
