package com.meiaomei.bankusher;

import android.app.Application;
import android.content.Context;
import android.view.WindowManager;

import com.meiaomei.bankusher.manager.BankUsherDB;

/**
 * Created by huyawen on 2017/11/27.
 * email:1754397982@qq.com
 */

public class BankUsherApplication extends Application {

    public static BankUsherApplication application;
    public static Context context;
    public static WindowManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        application=getApplication();
        manager= (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    }


    public static BankUsherApplication getApplication() {
        return application;
    }



    /**
     * 获取上下文
     *
     * @return
     */
    public static Context getAppContext() {
        return context;
    }
}
