package com.meiaomei.bankusher.activity;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.entity.PushMessage;
import com.meiaomei.bankusher.fragment.FaceTakeFragment;
import com.meiaomei.bankusher.fragment.SettingFragment;
import com.meiaomei.bankusher.fragment.VipRemarkFragment;
import com.meiaomei.bankusher.fragment.VipServerFragment;
import com.meiaomei.bankusher.manager.BankUsherDB;
import com.meiaomei.bankusher.service.MyService;
import com.meiaomei.bankusher.utils.DeviceInfoUtils;
import com.meiaomei.bankusher.utils.FileUtils;
import com.meiaomei.bankusher.utils.JsonUtils;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @ViewInject(R.id.button_vipserver)
    RadioButton button_vipserver;
    @ViewInject(R.id.button_facetake)
    RadioButton button_facetake;
    @ViewInject(R.id.button_vipremark)
    RadioButton button_vipremark;
    @ViewInject(R.id.button_setting)
    RadioButton button_setting;
    @ViewInject(R.id.radioGroup)
    RadioGroup radioGroup;

    FragmentManager fragmentManager;
    FaceTakeFragment faceTakeFragment;
    VipServerFragment vipServerFragment;
    SettingFragment settingFragment;
    VipRemarkFragment vipRemarkFragment;
    DbUtils dbUtils;
    final String TAG = getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Log.e(TAG, "onCreate:=======MainActivity ");
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);
        initOther();
        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume:=======MainActivity ");
    }

    private void initOther() {
        dbUtils = BankUsherDB.getDbUtils();
        //        BankUsherDB.deletTable();
        FileUtils.deleteFile(Environment.getExternalStorageDirectory());
        BankUsherDB.creatNewTable();
        getAppUserPermission();
        if (!DeviceInfoUtils.isServiceWork(MainActivity.this, "com.meiaomei.bankusher.service.MyService")) {
            Intent intent = new Intent(this, MyService.class);
            startService(intent);
        }

        //测试  json-----------
        PushMessage.Body body = new PushMessage.Body();
        body.setId("111");
        body.setBithday(new Date().getTime());
        body.setC4("2");
        body.setFavourite("哈哈哈哈哈哈");
        body.setPath("img/现场照片路径");
        body.setPicName("zhuce照片路径");
        body.setScore(87);
        body.setSignTime(new Date().getTime());
        body.setUserId(FileUtils.generateUuid());
        body.setUserLevel(1);
        body.setUserLevelName("黄金");
        body.setUserName("蝴蝶");

        PushMessage.Header header = new PushMessage.Header();
        header.setImgBaseUrl("tupianjichulujing");
        header.setMessageType("record");
        PushMessage message = new PushMessage();
        message.setBody(body);
        message.setHeader(header);
        String jsons = JsonUtils.GsonString(message);
        String SSS = jsons;

    }

    public void initView() {
        fragmentManager = getFragmentManager();
        if (vipServerFragment == null) {
            vipServerFragment = new VipServerFragment();
        }
        fragmentManager.beginTransaction().add(R.id.rl_right, vipServerFragment).commit();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //一开始全部隐藏掉
                fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                hide(fragmentTransaction);

                switch (checkedId) {
                    case R.id.button_vipserver:
                        if (vipServerFragment == null) {
                            vipServerFragment = new VipServerFragment();
                            fragmentTransaction.add(R.id.rl_right, vipServerFragment);
                        } else {
                            fragmentTransaction.show(vipServerFragment);
                        }
                        break;

                    case R.id.button_facetake:
                        if (faceTakeFragment == null) {
                            faceTakeFragment = new FaceTakeFragment();
                            fragmentTransaction.add(R.id.rl_right, faceTakeFragment);
                        } else {
                            fragmentTransaction.show(faceTakeFragment);
                        }
                        break;

                    case R.id.button_vipremark:
                        if (vipRemarkFragment == null) {
                            vipRemarkFragment = new VipRemarkFragment();
                            fragmentTransaction.add(R.id.rl_right, vipRemarkFragment);
                        } else {
                            fragmentTransaction.show(vipRemarkFragment);
                        }
                        break;

                    case R.id.button_setting:
                        if (settingFragment == null) {
                            settingFragment = new SettingFragment();
                            fragmentTransaction.add(R.id.rl_right, settingFragment);
                        } else {
                            fragmentTransaction.show(settingFragment);
                        }
                        break;
                }
                fragmentTransaction.commit();
            }
        });
    }

    private void hide(FragmentTransaction fragmentTransaction) {

        if (vipServerFragment != null) {
            fragmentTransaction.hide(vipServerFragment);
        }
        if (vipRemarkFragment != null) {
            fragmentTransaction.hide(vipRemarkFragment);
        }
        if (settingFragment != null) {
            fragmentTransaction.hide(settingFragment);
        }
        if (faceTakeFragment != null) {
            fragmentTransaction.hide(faceTakeFragment);
        }
    }


    //6.0 动态获取权限
    public void getAppUserPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23 && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//android6.0需要动态申请权限
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);//发起申请读写文件的权限
            }
        }
    }


    //权限请求的回调
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean writeAccepted = false;
        switch (requestCode) {
            case 1://动态请求的第一个权限
                writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (writeAccepted) {
                    break;
                } else {
                    Toast.makeText(this, "请授权，我们需要这个权限", Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);//发起申请读写文件的权限
                }
                break;
        }
    }


    //------------------------给fragment添加点击事件------------------------------------------
    public interface MyTouchListener {
         void onTouchEvent(MotionEvent event);
    }

    // 保存MyTouchListener接口的列表
    private ArrayList<MyTouchListener> myTouchListeners = new ArrayList<MainActivity.MyTouchListener>();

    /**
     * 提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法
     *
     * @param listener
     */
    public void registerMyTouchListener(MyTouchListener listener) {
        myTouchListeners.add(listener);
    }

    /**
     * 提供给Fragment通过getActivity()方法来取消注册自己的触摸事件的方法
     *
     * @param listener
     */
    public void unRegisterMyTouchListener(MyTouchListener listener) {
        myTouchListeners.remove(listener);
    }

    /**
     * 分发触摸事件给所有注册了MyTouchListener的接口
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyTouchListener listener : myTouchListeners) {
            listener.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }
}
