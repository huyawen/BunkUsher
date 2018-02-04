package com.meiaomei.bankusher.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.meiaomei.bankusher.entity.MyResponse;
import com.meiaomei.bankusher.entity.PushMessage;
import com.meiaomei.bankusher.entity.VipCustomerModel;
import com.meiaomei.bankusher.entity.VisitRecordModel;
import com.meiaomei.bankusher.manager.BankUsherDB;
import com.meiaomei.bankusher.manager.OkHttpManager;
import com.meiaomei.bankusher.manager.WebsocketPushClient;
import com.meiaomei.bankusher.utils.NotificationUtils;
import com.meiaomei.bankusher.utils.SharedPrefsUtil;

import org.java_websocket.drafts.Draft_17;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;


public class GetMsgService extends Service {
    public static WebsocketPushClient client;
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
    final String TAG = "GetMsgService";

    Handler handler = new Handler();

    @Override
    public void onCreate() {
        super.onCreate();
        draft_17 = new Draft_17();
        context = getApplicationContext();
        okHttpManager = new OkHttpManager();
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

        handler.postDelayed(new Runnable() {//防止 WebsocketPushClient 对象无法重复开启
            @Override
            public void run() {
                client.connect();
                client.setOrgCode("1000");
                client.setM_sOranization("");
            }
        }, 1000);

        //如果意外断开 或者 服务器还没启动每隔5秒重连一次
        reconnect();
        return START_REDELIVER_INTENT;//系统会自动重启该服务，并将Intent的值传入
    }

    private void reconnect() {
        client.setCallErroBack(new WebsocketPushClient.CallErroBack() {//递归后的新的client 再次监听错误接口
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
                            client.setOrgCode("1000");
                            client.setM_sOranization("");
                            reconnect();
                        }
                    }
                }, timeper);

                if (i <= 60) {//先6秒一次  之后一次加4秒  加到3分钟一次为止
                    timeper += 3000;
                    i++;
                }
            }
        });

        client.setCallback(new WebsocketPushClient.Callback() {//递归后的新的client 再次监听接受消息的接口
            @Override
            public void OnReciveData(String data) {
                Log.e("GetMsgService====", data);
                PushMessage message;
                try {
                    if (!TextUtils.isEmpty(data)) {
                        message = new Gson().fromJson(data, PushMessage.class);
                        if (message != null) {
                            final PushMessage.Body body = message.getBody();
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

                                //保存完成之后，发通知（延迟500毫秒）
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        NotificationUtils notificationUtils = new NotificationUtils(context);
                                        notificationUtils.sendSysNotification(body.getUserName());
                                    }
                                }, 100);
                            }
                        }
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });

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
        if (TextUtils.isEmpty(baseurl)) {
            baseurl = "http://192.168.0.183:8580";
        }
        String url = okHttpManager.getUrl(baseurl, 2);
        okHttpManager.postJson(url, js, new OkHttpManager.HttpCallBack() {
            @Override
            public void onSusscess(String data, String cookie) {
                Gson gson = new Gson();
                MyResponse myResponse = null;
                try {
                    myResponse = gson.fromJson(data, MyResponse.class);
                } catch (Exception e) {
                    Toast.makeText(context, "解析错误！", Toast.LENGTH_SHORT).show();
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
                        vipCustomerModel.setWorkNumber(myData.getWorkCode());
                        vipCustomerModel.setDelFlag("0");
                        vipCustomerModel.setFaceId(userId);
                        vipCustomerModel.setFavorite(body.getFavourite());
                        vipCustomerModel.setIdNumber(idCard);
                        String imgUrl = header.getImgBaseUrl() + body.getPicName();
                        vipCustomerModel.setImgUrl(imgUrl.contains("127.0.0.1") ? imgUrl.replace("127.0.0.1", "192.168.0.183") : imgUrl);
                        vipCustomerModel.setName(body.getUserName());
                        vipCustomerModel.setPhoneNumber(telephone);
                        vipCustomerModel.setRemark(remark);
                        vipCustomerModel.setSex(gener == 0 ? "女" : "男");
                        int level = body.getUserLevel();
                        String levelName = getlevelName(level);
                        vipCustomerModel.setVipOrder(levelName);//vip等级的名称
                        vipCustomerModel.setEmail(email);

                        try {
                            dbUtils.saveOrUpdate(vipCustomerModel);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }

                } else if (myResponse != null && "500".equals(myResponse.getRespCode())) {
                    Toast.makeText(context, "服务器错误！", Toast.LENGTH_SHORT).show();
                } /*else {
                    Toast.makeText(context, "发生了未知的错误！", Toast.LENGTH_SHORT).show();
                }*/
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

    public String getlevelName(int level) {
        String sLevel = "";
        //清空checkBox
        switch (level) {
            case 4:
                sLevel = "四级";
                break;
            case 3:
                sLevel = "三级";
                break;
            case 2:
                sLevel = "二级";
                break;
            case 1:
                sLevel = "一级";
                break;

            case 0://没有的话不传了
                sLevel = "普通";
                break;

            default:
                sLevel = "普通";
                break;
        }
        return sLevel;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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
        Intent intent = new Intent("com.meiaomei.bankusher.AwakeMyServiceReceiver");
        intent.putExtra("startService", "true");
        Log.e("MyService", "onDestroy: " + "startService");
        sendBroadcast(intent);
        super.onDestroy();
    }
}
