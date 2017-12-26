package com.meiaomei.bankusher.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.activity.MainActivity;
import com.meiaomei.bankusher.manager.WebsocketPushClient;
import com.meiaomei.bankusher.utils.MD5Utils;
import com.meiaomei.bankusher.utils.NotificationUtils;

import org.java_websocket.drafts.Draft_17;

import java.net.URI;

import static android.util.TypedValue.COMPLEX_UNIT_SP;


public class MyService extends Service {
    public static WebsocketPushClient client;
    Handler handler = new Handler();
    private NotificationManager manager;
    Context context;
    Draft_17 draft_17;
    int notifyId=0;
    //ws: 开头的
    String ws = "ws://192.168.0.183:8080/websocket01/ws/chat/server11";
    String ws11 = "ws://192.168.0.183:8080/webSocket/ServerSocket"; //服务器的项目名+url-pattern
    long timeper = 6000;
    int i = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        draft_17 = new Draft_17();
        client = new WebsocketPushClient(URI.create(ws11), draft_17);
        context = getApplicationContext();
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Build bd = new Build();
        String hardware = bd.HARDWARE;
        String hdNum = bd.SERIAL;
        String id = hardware + hdNum;
        String md5id = MD5Utils.md5(id);
        String deviceID = md5id;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                client.setOrgCode("111");
                client.connect();
            }
        },10000);

        client.setCallback(new WebsocketPushClient.Callback() {
            @Override
            public void OnReciveData(String data) {
                Log.e("得到数据", data );
                NotificationUtils notificationUtils=new NotificationUtils(context);
                notificationUtils.sendSysNotification();
            }
        });

        //如果意外断开 或者 服务器还没启动每隔5秒重连一次
        reconnect();
        return START_REDELIVER_INTENT;//系统会自动重启该服务，并将Intent的值传入
    }

    private void reconnect() {

        client.setCallErroBack(new WebsocketPushClient.CallErroBack() {
            @Override
            public void OnReciveError() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (null != client) {
                            client = null;
                            System.gc();
                            client = new WebsocketPushClient(URI.create(ws11), draft_17);
                            client.connect();
                            reconnect();
                        }
                    }
                }, timeper);

                if (i <= 60) {//先6秒一次  之后一次加4秒  加到3分钟一次停止
                    timeper += 5000;
                    i++;
                }
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //自定义通知栏
    public void sendMyNotification() {
        //判断系统通知栏背景颜色
        boolean darkNotiFicationBar = NotificationUtils.isDarkNotiFicationBar(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.remoteview);
        //设置通知栏背景
        remoteViews.setTextColor(R.id.appNameTextView, darkNotiFicationBar == true ? Color.WHITE : Color.BLACK);
        remoteViews.setTextColor(R.id.title_TextView, darkNotiFicationBar == true ? Color.WHITE : Color.BLACK);
        remoteViews.setTextColor(R.id.content_TextView, darkNotiFicationBar == true ? Color.WHITE : Color.BLACK);
        //设置文字大小
        remoteViews.setTextViewTextSize(R.id.title_TextView, COMPLEX_UNIT_SP, 16);
        remoteViews.setTextViewTextSize(R.id.content_TextView, COMPLEX_UNIT_SP, 14);
        //添加内容
        remoteViews.setImageViewResource(R.id.iconImageView, R.mipmap.ic_launcher);
        remoteViews.setTextViewText(R.id.appNameTextView, "紫禁城");
        remoteViews.setTextViewText(R.id.title_TextView, "紫禁城放假了");
        remoteViews.setTextViewText(R.id.content_TextView, "紫禁城免费了");
        //实例化通知栏构造器。
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        //系统收到通知时，通知栏上面显示的文字。
        mBuilder.setTicker("来通知了！");
        mBuilder.setContent(remoteViews);//获得Notification定高
        mBuilder.setCustomBigContentView(remoteViews);
        //显示在通知栏上的小图标
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        //设置大图标，即通知条上左侧的图片（如果只设置了小图标，则此处会显示小图标）
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        //添加声音
        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);//默认的声音和铃声 吱吱
        Intent intent = new Intent(context, MainActivity.class);//点击通知，进入应用
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);//点击通知栏后的意图
        mBuilder.setAutoCancel(true);//设置这个标志当用户单击面板就可以让通知将自动取消
        //设置为不可清除模式
//        mBuilder.setOngoing(true);
        //显示通知，id必须不重复，否则新的通知会覆盖旧的通知（利用这一特性，可以对通知进行更新）
        NotificationManager mm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        mm.notify(notifyId++, notification);
//        Picasso.with(this).load("")//message 就是图片链接地址
//                .resize(200, 200)
//                .centerCrop()
//                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                .into(remoteViews, R.id.ImageView, 1, notification);
    }


    /**
     * 检查当前网络是否可用
     *
     * @param context
     * @return
     */
    public boolean isNetworkAvailable(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
