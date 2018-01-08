package com.meiaomei.bankusher.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.activity.MainActivity;
import com.meiaomei.bankusher.entity.event.StringModel;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

/**
 * Created by huyawen on 2017/12/15.
 * email:1754397982@qq.com
 */

public class NotificationUtils {


    Context context;

    public NotificationUtils(Context context) {
        this.context = context;
    }

    public Bitmap buildUpdate(String time) {
        Bitmap myBitmap = Bitmap.createBitmap(160, 84, Bitmap.Config.ARGB_4444);
        Canvas myCanvas = new Canvas(myBitmap);
        Paint paint = new Paint();
        Typeface clock = Typeface.createFromAsset(context.getAssets(), "Clockopia.ttf");
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(clock);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(65);
        paint.setTextAlign(Paint.Align.CENTER);
        myCanvas.drawText(time, 80, 60, paint);
        return myBitmap;
    }


    //自定义通知栏
    public void sendMyNotification(String name) {
        //判断系统通知栏背景颜色
        boolean darkNotiFicationBar = isDarkNotiFicationBar(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.remoteview);

        //设置通知栏背景
        remoteViews.setTextColor(R.id.appNameTextView, darkNotiFicationBar == true ? Color.WHITE : Color.BLACK);

        remoteViews.setTextColor(R.id.title_TextView, darkNotiFicationBar == true ? Color.WHITE : Color.BLACK);
        remoteViews.setTextColor(R.id.content_TextView, darkNotiFicationBar == true ? Color.WHITE : Color.BLACK);

        remoteViews.setTextColor(R.id.tv_time, darkNotiFicationBar == true ? Color.WHITE : Color.BLACK);

        //设置文字大小
        remoteViews.setTextViewTextSize(R.id.title_TextView, COMPLEX_UNIT_SP, 14);
        remoteViews.setTextViewTextSize(R.id.content_TextView, COMPLEX_UNIT_SP, 12);
        remoteViews.setTextViewTextSize(R.id.tv_time, COMPLEX_UNIT_SP, 14);


        //添加内容
        remoteViews.setImageViewResource(R.id.iconImageView, R.mipmap.icon);
        remoteViews.setTextViewText(R.id.appNameTextView, "紫禁城");//小的

        remoteViews.setTextViewText(R.id.title_TextView, "欢迎VIP : " + name + " 的到来！");
        remoteViews.setTextViewText(R.id.content_TextView, "大堂经理请接待！");
        SimpleDateFormat format = new SimpleDateFormat("a hh:mm");//a是上下午  EEEE是星期
        String time = format.format(new Date(System.currentTimeMillis()));
        remoteViews.setTextViewText(R.id.tv_time, time);
//        remoteViews.setImageViewBitmap(R.id.iv_time, buildUpdate(time));//改变字体的样式没用

        //实例化通知栏构造器。
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        //系统收到通知时，通知栏上面显示的文字。
        mBuilder.setTicker("来VIP了！");
        mBuilder.setContent(remoteViews);//获得Notification定高
        mBuilder.setCustomBigContentView(remoteViews);
        //显示在通知栏上的小图标
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setFullScreenIntent(getDefalutIntent(PendingIntent.FLAG_UPDATE_CURRENT), false);//设置通知以headup形式出现
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
        mm.notify(new Random().nextInt(10000), notification);
//        Picasso.with(this).load("")//message 就是图片链接地址
//                .resize(200, 200)
//                .centerCrop()
//                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                .into(remoteViews, R.id.ImageView, 1, notification);

        //通知vipServer界面更新数据
        StringModel model = new StringModel("update", "update");
        EventBus.getDefault().post(model);
    }


    //设置系统默认通知
    public void sendSysNotification(String name) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Notification notify = mBuilder.build();
        mBuilder.setContentTitle("欢迎VIP : " + name + " 的到来！")//设置通知栏标题
                .setContentText("大堂经理请接待！")
                .setDeleteIntent(deletIntent(notify))
                .setContentIntent(getDefalutIntent(PendingIntent.FLAG_UPDATE_CURRENT)) //设置通知栏点击意图
//                .setNumber(4) //设置通知集合的数量
                .setTicker("VIP客户来了！") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                .setAutoCancel(true)//点击消费
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_SOUND)//设置默认铃声
//                .setSound(Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "2"))//获取Android多媒体库内的铃声
//                .setDefaults(Notification.DEFAULT_LIGHTS)//设置三色灯
                .setVibrate(new long[]{0, 300, 300, 300})//设置震动  实现效果：延迟0ms，然后振动300ms，在延迟300ms，接着在振动300ms。
                .setLights(0xff0000ff, 300, 0)// .setLights(intledARGB ,intledOnMS ,intledOffMS 只有在设置了标志符Flags为Notification.FLAG_SHOW_LIGHTS的时候，才支持三色灯提醒。 这边的颜色跟设备有关，不是所有的颜色都可以，要看具体设备。
                .setSmallIcon(R.mipmap.icon)//设置通知小ICON
                .setFullScreenIntent(getDefalutIntent(PendingIntent.FLAG_UPDATE_CURRENT), false);//设置通知以headup形式出现

        mNotificationManager.notify(new Random().nextInt(10000), mBuilder.build());
    }


    //相应点击通知的事件
    public PendingIntent getDefalutIntent(int flags) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1, intent, flags);
        return pendingIntent;
    }

    //响应点击清除通知的事件
    public PendingIntent deletIntent(Notification notification) {
        Intent deleteIntent = new Intent();
        deleteIntent.setClass(context, MainActivity.class);
        deleteIntent.setAction(Intent.ACTION_DELETE);
        notification.deleteIntent = PendingIntent.getBroadcast(context, 0, deleteIntent, 0);
        return notification.deleteIntent;
    }


    private final static String TAG = NotificationUtils.class.getSimpleName();
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
    private static final double COLOR_THRESHOLD = 180.0;
    private static int titleColor;

    /**
     * 判断通知栏背景颜色，现在手机通知栏大部分不是白色就是黑色背景
     *
     * @param context
     * @return
     */
    public static boolean isDarkNotiFicationBar(Context context) {
        return !isColorSimilar(Color.BLACK, getNotificationColor(context));
    }

    private static int getNotificationColor(Context context) {
        if (context instanceof AppCompatActivity) {
            return getNotificationColorCompat(context);
        } else {
            return getNotificationColorInternal(context);
        }
    }

    private static boolean isColorSimilar(int baseColor, int color) {
        int simpleBaseColor = baseColor | 0xff000000;
        int simpleColor = color | 0xff000000;
        int baseRed = Color.red(simpleBaseColor) - Color.red(simpleColor);
        int baseGreen = Color.green(simpleBaseColor) - Color.green(simpleColor);
        int baseBlue = Color.blue(simpleBaseColor) - Color.blue(simpleColor);
        double value = Math.sqrt(baseRed * baseRed + baseGreen * baseGreen + baseBlue * baseBlue);
        if (value < COLOR_THRESHOLD) {
            return true;
        }
        return false;
    }

    private static int getNotificationColorInternal(Context context) {
        final String DUMMY_TITLE = "DUMMY_TITLE";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setContentText(DUMMY_TITLE);
        Notification notification = builder.build();
        RemoteViews contentView = notification.contentView;
        if (contentView == null) {
            return 0;
        }
        ViewGroup notificationRoot = (ViewGroup) contentView.apply(context, new FrameLayout(context));
        final TextView titleView = (TextView) notificationRoot.findViewById(android.R.id.title);
        if (titleView == null) {
            iteratoryView(notificationRoot, new Filter() {
                @Override
                public void filter(View view) {
                    if (view instanceof TextView) {
                        TextView textView = (TextView) view;
                        if (DUMMY_TITLE.equals(textView.getText().toString())) {
                            titleColor = textView.getCurrentTextColor();
                        }
                    }
                }
            });
            return titleColor;
        } else {
            return titleView.getCurrentTextColor();
        }
    }

    private static int getNotificationColorCompat(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = builder.build();
        RemoteViews contentView = notification.contentView;
        if (contentView == null) {
            return 0;
        }
        int layoutId = contentView.getLayoutId();
        ViewGroup notificationRoot = (ViewGroup) LayoutInflater.from(context).inflate(layoutId, null);
        final TextView titleView = (TextView) notificationRoot.findViewById(android.R.id.title);
        if (titleView == null) {
            final List<TextView> textViews = new ArrayList<>();
            iteratoryView(notificationRoot, new Filter() {
                @Override
                public void filter(View view) {
                    textViews.add((TextView) view);
                }
            });
            float minTextSize = Integer.MIN_VALUE;
            int index = 0;
            for (int i = 0, j = textViews.size(); i < j; i++) {
                float currentSize = textViews.get(i).getTextSize();
                if (currentSize > minTextSize) {
                    minTextSize = currentSize;
                    index = i;
                }
            }
            return textViews.get(index).getCurrentTextColor();
        } else {
            return titleView.getCurrentTextColor();
        }
    }

    private static void iteratoryView(View view, Filter filter) {
        if (view == null || filter == null) {
            return;
        }
        filter.filter(view);
        if (view instanceof ViewGroup) {
            ViewGroup container = (ViewGroup) view;
            for (int i = 0, j = container.getChildCount(); i < j; i++) {
                View child = container.getChildAt(i);
                iteratoryView(child, filter);
            }
        }
    }

    private interface Filter {
        void filter(View view);
    }

}
