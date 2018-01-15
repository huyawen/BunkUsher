package com.meiaomei.bankusher.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.fragment.FaceTakeFragment;
import com.meiaomei.bankusher.fragment.SettingFragment;
import com.meiaomei.bankusher.fragment.VipRemarkFragment;
import com.meiaomei.bankusher.fragment.VipServerFragment;
import com.meiaomei.bankusher.manager.BankUsherDB;
import com.meiaomei.bankusher.manager.OkHttpManager;
import com.meiaomei.bankusher.service.MyService;
import com.meiaomei.bankusher.utils.DeviceInfoUtils;
import com.meiaomei.bankusher.utils.FileUtils;
import com.meiaomei.bankusher.utils.SharedPrefsUtil;

import java.util.ArrayList;

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
    String baseurl = "";
    OkHttpManager manager;

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


    private void initOther() {
        dbUtils = BankUsherDB.getDbUtils();
        //        BankUsherDB.deletTable();
        FileUtils.deleteFile(Environment.getExternalStorageDirectory());

        if (!DeviceInfoUtils.isServiceWork(MainActivity.this, "com.meiaomei.bankusher.service.MyService")) {
            Intent intent = new Intent(this, MyService.class);
            startService(intent);
        }

        baseurl = SharedPrefsUtil.getValue(MainActivity.this, "serverAddress", "");
        if (TextUtils.isEmpty(baseurl)) {
            baseurl = "http://192.168.0.183:8580";
        }
        manager = new OkHttpManager();

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

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume:=======MainActivity ");
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
