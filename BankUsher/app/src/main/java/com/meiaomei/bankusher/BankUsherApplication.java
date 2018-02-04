package com.meiaomei.bankusher;

import android.app.Application;
import android.content.Context;
import android.view.WindowManager;

import com.meiaomei.bankusher.manager.BankUsherDB;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by huyawen on 2017/11/27.
 * email:1754397982@qq.com
 */

public class BankUsherApplication extends Application {

    public static BankUsherApplication application;
    public static Context context;
    public static WindowManager manager;
    public static RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
//        if (LeakCanary.isInAnalyzerProcess(this)) {//解决leakCancary报错的问题
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
        context = getApplicationContext();
        application=getApplication();
        manager= (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//        refWatcher= LeakCanary.install(this);
    }

//    public static RefWatcher getRefWatcher(){
//        return refWatcher;
//    }


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
