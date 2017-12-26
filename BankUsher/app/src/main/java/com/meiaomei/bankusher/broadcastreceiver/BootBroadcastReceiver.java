package com.meiaomei.bankusher.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.meiaomei.bankusher.service.MyService;

/**
 * Created by huyawen on 2017/3/20.
 * 开机后启动 服务的广播
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Intent i = new Intent(context, MyService.class);
            context.startService(i);
        }
    }
}

