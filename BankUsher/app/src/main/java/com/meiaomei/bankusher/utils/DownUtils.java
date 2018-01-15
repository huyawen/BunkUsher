package com.meiaomei.bankusher.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.meiaomei.bankusher.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by huyawen on 2016/6/15.
 */
public class DownUtils {

    Context context;

    public DownUtils(Context context) {
        this.context = context;
    }

    public String getData(String apkUrl, String name) {
        File file = getFile(name);
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            URL url = new URL(apkUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            //获取下载文件的总长度(有可能文件总长度大于整型的最大值，如果超过整形最大值并称负数)
            long total = connection.getContentLength();
            if (connection.getResponseCode() == 200) {
                InputStream is = connection.getInputStream();
                //一次读取的字节数
                byte[] bytes = new byte[1024];
                //一次读取字节数的长度
                int temp = 0;
                long temp_total = 0;
                while ((temp = is.read(bytes)) != -1) {
                    //下载的累计的总长度
                    temp_total += temp;
                    //文件下载进度
                    int percent = (int) (temp_total * 100 / total);

                    showNotification(file.getAbsolutePath(), percent);
                    //写入文件到本地
                    outputStream.write(bytes, 0, temp);
                }
                outputStream.flush();
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }


    //得到sd卡的缓存路径
    public File getFile(String name) {
        String temp_file = "";
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // /sdcard/Android/data/<application package>/cache 这个路径  程序卸载后会被删除
            temp_file = context.getExternalCacheDir().getAbsolutePath();
        } else {
            // /data/data/<application package>/cache 这个路径 程序卸载后会被删除
            temp_file = context.getCacheDir().getAbsolutePath();
        }
        return new File(temp_file, name);
    }

    public void showNotification(String path, int progress) {
        NotificationManager manager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setSmallIcon(R.mipmap.icon);
        builder.setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.message));//获取Android多媒体库内的铃声
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.notification_remote);
        Log.e("DownUtils", "showNotification: " + progress);
        remoteViews.setProgressBar(R.id.progressBar_n, 100, progress, false);
        remoteViews.setTextViewText(R.id.tv_n, "正在下载");
        remoteViews.setTextViewText(R.id.tv_progress, progress + "%");
        builder.setContent(remoteViews);


        PendingIntent p1 = PendingIntent.getActivity(
                context, 100, new Intent(), 0);
        builder.setFullScreenIntent(p1, false);// 设置通知以headup形式出现


        if (progress == 100) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
            remoteViews.setTextViewText(R.id.tv_n, "下载完成,请点击安装");//点击安装之后才会消费掉这个 notification
            PendingIntent p = PendingIntent.getActivity(
                    context, 100, intent, 0);
            builder.setContentIntent(p);


            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1000L);// 参数是震动时间(long类型)
            context.startActivity(intent);// 下载完成之后自动弹出安装界面
            manager.cancel(0);
        } else {
            manager.notify(0, builder.build()); //(点击安装和  自动安装智能选其中一个（点击的话 去掉else ） )
        }

    }

}
