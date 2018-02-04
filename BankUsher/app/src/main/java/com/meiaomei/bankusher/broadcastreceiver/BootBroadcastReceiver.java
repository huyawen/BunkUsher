package com.meiaomei.bankusher.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.meiaomei.bankusher.service.GetMsgService;
import com.meiaomei.bankusher.utils.SharedPrefsUtil;

/**
 * Created by huyawen on 2017/3/20.
 * 开机后启动 识别服务的广播
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        String address = SharedPrefsUtil.getValue(context, "serverAddress", "");//地址如果没有  第一次不应该自动启动服务
        if (intent.getAction().equals(ACTION) && !TextUtils.isEmpty(address)) {
            Intent i = new Intent(context, GetMsgService.class);
            context.startService(i);
        }
    }
}

