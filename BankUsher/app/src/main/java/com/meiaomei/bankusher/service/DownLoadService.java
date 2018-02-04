package com.meiaomei.bankusher.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.meiaomei.bankusher.utils.DownUtils;

/**
 * Created by huyawen on 2018/1/15.
 * email:1754397982@qq.com
 * <p>
 * 设置下载进度条的服务  用IntentService 下载完成自动调用onDestory
 */

public class DownLoadService extends IntentService {

    //必须写这个方法
    public DownLoadService() {
        this("MyIntentThread");//name 为子线程的名称  IntentService自动帮我们开一个子线程
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public DownLoadService(String name) {
        super(name);
    }

    // 要做的操作写在这个方法中，方法执行结束执行onDestory()，可以通过Intent来接受传递过来的数据
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String path = intent.getStringExtra("path");
        Log.e("========", "path===" + path);
        Context context = this;
        new DownUtils(context).getData(path, "bankUser.apk");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("DownLoadService", "===onDestroy===");
    }
}
