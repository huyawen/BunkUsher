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
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.activity.LoginActivity;
import com.meiaomei.bankusher.activity.MainActivity;
import com.meiaomei.bankusher.broadcastreceiver.AwakeMyServiceReceiver;
import com.meiaomei.bankusher.entity.MyResponse;
import com.meiaomei.bankusher.entity.PushMessage;
import com.meiaomei.bankusher.entity.VipCustomerModel;
import com.meiaomei.bankusher.entity.VisitRecordModel;
import com.meiaomei.bankusher.manager.BankUsherDB;
import com.meiaomei.bankusher.manager.OkHttpManager;
import com.meiaomei.bankusher.manager.WebsocketPushClient;
import com.meiaomei.bankusher.utils.MD5Utils;
import com.meiaomei.bankusher.utils.NotificationUtils;
import com.meiaomei.bankusher.utils.SharedPrefsUtil;

import org.java_websocket.drafts.Draft_17;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;

import static android.util.TypedValue.COMPLEX_UNIT_SP;


public class MyService extends Service {
    public static WebsocketPushClient client;
    Handler handler = new Handler();
    private NotificationManager notificationManager;
    OkHttpManager okHttpManager;

    Context context;
    Draft_17 draft_17;
    int notifyId = 0;

    String ws = "";
    //    String ws11 = "ws://192.168.0.183:8080/webSocket/ServerSocket";
    long timeper = 6000;
    int i = 0;
    String baseurl = "";
    DbUtils dbUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        draft_17 = new Draft_17();
        context = getApplicationContext();
        okHttpManager=new OkHttpManager();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        dbUtils = BankUsherDB.getDbUtils();
        ws = SharedPrefsUtil.getValue(context, "serverAddress", "");
        if (TextUtils.isEmpty(ws)) {
            ws = "ws://192.168.0.183:8580/webSocketServer";
        } else {
            ws = String.format("ws://%s/webSocketServer", new Object[]{ws.replaceAll("(?i)http://", "").replaceAll("(?i)https://", "")});
        }
        client = new WebsocketPushClient(URI.create(ws), draft_17);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Build bd = new Build();
        final String hardware = bd.HARDWARE;
        String hdNum = bd.SERIAL;
        String id = hardware + hdNum;
        String md5id = MD5Utils.md5(id);
        String deviceID = md5id;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                client.connect();
                client.setOrgCode("1000");
                client.setM_sOranization("");

            }
        }, 1000);

        client.setCallback(new WebsocketPushClient.Callback() {
            @Override
            public void OnReciveData(String data) {
                Log.e("得到数据", data);
                PushMessage message;
                try {
                    if (!TextUtils.isEmpty(data)) {
                        message = new Gson().fromJson(data, PushMessage.class);
                        if (message != null) {
                            PushMessage.Body body = message.getBody();
                            PushMessage.Header header = message.getHeader();
                            if (body != null && header != null) {
                                String userId = body.getUserId();//用户的id
                                findById(userId, body, header);
                                String visitId = body.getId();

                                VisitRecordModel recordModel = new VisitRecordModel();
                                recordModel.setVisitId(visitId);
                                recordModel.setCameraName(body.getCameraName());
                                recordModel.setFaceId(userId);
                                recordModel.setHandleFlag("0");
                                recordModel.setVisitAddress(body.getCameraName());
                                recordModel.setVisitTime(body.getSignTime());
                                dbUtils.saveOrUpdate(recordModel);

                                //保存完成之后，发通知
                                NotificationUtils notificationUtils = new NotificationUtils(context);
                                notificationUtils.sendMyNotification(body.getUserName());
                            }
                        }
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }

            }
        });

        //如果意外断开 或者 服务器还没启动每隔5秒重连一次
        reconnect();
        return START_REDELIVER_INTENT;//系统会自动重启该服务，并将Intent的值传入
    }


    //根据用户的id查出详细信息
    private void findById(final String userId, final PushMessage.Body body, final PushMessage.Header header) {
        //联网检查
        JSONObject localJSONObject = new JSONObject();
        String js = "";
        try {
            localJSONObject.put("appId", "user");
            localJSONObject.put("appSecret", "12345");
            localJSONObject.put("id", userId);
            js = localJSONObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        baseurl = SharedPrefsUtil.getValue(context, "serverAddress", "");
        String url = okHttpManager.getUrl(baseurl, 2);
        okHttpManager.postJson(url, js, new OkHttpManager.HttpCallBack() {
            @Override
            public void onSusscess(String data, String cookie) {
                Gson gson = new Gson();
                MyResponse myResponse = null;
                try {
                    myResponse = gson.fromJson(data, MyResponse.class);
                } catch (Exception e) {
                    Toast.makeText(context, "发生了未知的错误！", Toast.LENGTH_SHORT).show();
                }
                if (myResponse != null && "200".equals(myResponse.getRespCode())) {//响应成功
                    MyResponse.MyData myData = myResponse.getData();
                    if (myData != null) {
                        int gener = myData.getGender();
                        String email = myData.getEmail();
                        String idCard = myData.getIdCard();
                        String remark = myData.getRemark();
                        String telephone = myData.getTelephone();

                        VipCustomerModel vipCustomerModel = new VipCustomerModel();
                        vipCustomerModel.setBirthday(body.getBithday());
                        vipCustomerModel.setCarNumber("");
                        vipCustomerModel.setDelFlag("0");
                        vipCustomerModel.setFaceId(userId);
                        vipCustomerModel.setFavorite(body.getFavourite());
                        vipCustomerModel.setIdNumber(idCard);
                        vipCustomerModel.setImgUrl(header.getImgBaseUrl() + body.getPicName());
                        vipCustomerModel.setName(body.getUserName());
                        vipCustomerModel.setPhoneNumber(telephone);
                        vipCustomerModel.setRemark(remark);
                        vipCustomerModel.setSex(gener == 0 ? "女" : "男");
                        vipCustomerModel.setVipOrder(body.getUserLevelName());//vip等级的名成
                        vipCustomerModel.setEmail(email);
                        try {
                            dbUtils.saveOrUpdate(vipCustomerModel);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }


                    }

                } else if (myResponse != null && "500".equals(myResponse.getRespCode())) {
                    Toast.makeText(context, "服务器错误！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "发生了未知的错误！", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(String meg) {
                super.onError(meg);
                //失败后
                Log.e("errir", meg);
                Toast.makeText(context, "无法连接到服务器，请检查网络！", Toast.LENGTH_SHORT).show();
            }
        });
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
                            client = new WebsocketPushClient(URI.create(ws), draft_17);
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

    @Override
    public void onDestroy() {
        Intent intent = new Intent(this, AwakeMyServiceReceiver.class);
        intent.putExtra("startService", "true");
        sendBroadcast(intent);
    }

}
