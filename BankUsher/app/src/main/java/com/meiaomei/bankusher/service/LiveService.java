package com.meiaomei.bankusher.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.meiaomei.bankusher.manager.ScreenBroadcastListener;
import com.meiaomei.bankusher.manager.ScreenManager;

/**
 * Created by huyawen on 2018/1/19.
 * email:1754397982@qq.com
 * 1px  Activity的Service
 */

public class LiveService extends Service {

    public static void toLiveService(Context pContext) {
        Intent intent = new Intent(pContext, LiveService.class);
        pContext.startService(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //屏幕关闭的时候启动一个1像素的Activity，开屏的时候关闭Activity
        final ScreenManager screenManager = ScreenManager.getInstance(LiveService.this);
        ScreenBroadcastListener listener = new ScreenBroadcastListener(this);
        listener.registerListener(new ScreenBroadcastListener.ScreenStateListener() {
            @Override
            public void onScreenOn() {
                Log.e("LiveService", "onScreenOn: "+"屏幕开启了" );
                screenManager.finishActivity();
            }

            @Override
            public void onScreenOff() {
                Log.e("LiveService", "onScreenOn: "+"屏幕关闭了" );
                screenManager.startActivity();
            }
        });
        return START_REDELIVER_INTENT;
    }
}
