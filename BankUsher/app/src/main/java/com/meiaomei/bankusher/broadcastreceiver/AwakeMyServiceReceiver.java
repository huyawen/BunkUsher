package com.meiaomei.bankusher.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.meiaomei.bankusher.service.GetMsgService;

/**
 * Created by huyawen on 2018/1/8.
 * email:1754397982@qq.com
 * <p>
 * 服务一旦异常被杀死 ，该广播唤醒后台服务继续执行
 */

public class AwakeMyServiceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("AwakeMyServiceReceiver", "=========onReceive");
        if (intent.getAction().equals("com.meiaomei.bankusher.AwakeMyServiceReceiver")) {
            String result = intent.getStringExtra("startService");
            if (result != null) {
                if (result.equals("true")) {
                    Intent startServiceIntent = new Intent(context, GetMsgService.class);
                    context.startService(startServiceIntent);
                }
            }
        }
    }
}
