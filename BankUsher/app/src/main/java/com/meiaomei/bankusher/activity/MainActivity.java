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
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.entity.MyResponse;
import com.meiaomei.bankusher.entity.PushMessage;
import com.meiaomei.bankusher.entity.VipCustomerModel;
import com.meiaomei.bankusher.fragment.FaceTakeFragment;
import com.meiaomei.bankusher.fragment.SettingFragment;
import com.meiaomei.bankusher.fragment.VipRemarkFragment;
import com.meiaomei.bankusher.fragment.VipServerFragment;
import com.meiaomei.bankusher.manager.BankUsherDB;
import com.meiaomei.bankusher.manager.OkHttpManager;
import com.meiaomei.bankusher.service.MyService;
import com.meiaomei.bankusher.utils.DeviceInfoUtils;
import com.meiaomei.bankusher.utils.FileUtils;
import com.meiaomei.bankusher.utils.JsonUtils;
import com.meiaomei.bankusher.utils.SharedPrefsUtil;

import org.json.JSONException;
import org.json.JSONObject;

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

    //根据用户的id查出详细信息
    private void findById() {
        //联网检查
        JSONObject localJSONObject = new JSONObject();
        String js = "";
        try {
            localJSONObject.put("appId", "user");
            localJSONObject.put("appSecret", "12345");
            localJSONObject.put("id", "f9cce309fc4d407299c722b37c07ede0");
            js = localJSONObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = OkHttpManager.getUrl(baseurl, 2);
        manager.postJson(url, js, new OkHttpManager.HttpCallBack() {
            @Override
            public void onSusscess(String data, String cookie) {
                Gson gson = new Gson();
                MyResponse myResponse = null;
                try {
                    myResponse = gson.fromJson(data, MyResponse.class);
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "发生了未知的错误！", Toast.LENGTH_SHORT).show();
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

                    }

                } else if (myResponse != null && "500".equals(myResponse.getRespCode())) {
                    Toast.makeText(MainActivity.this, "服务器错误！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "发生了未知的错误！", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(String meg) {
                super.onError(meg);
                //失败后
                Log.e("errir", meg);
                Toast.makeText(MainActivity.this, "无法连接到服务器，请检查网络！", Toast.LENGTH_SHORT).show();
            }
        });
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

        if (!DeviceInfoUtils.isServiceWork(MainActivity.this, "com.meiaomei.bankusher.service.MyService")) {
            Intent intent = new Intent(this, MyService.class);
            startService(intent);
        }

        baseurl = SharedPrefsUtil.getValue(MainActivity.this, "serverAddress", "");
        if (TextUtils.isEmpty(baseurl)) {
            baseurl = "http://192.168.0.183:8580";
        }
        manager = new OkHttpManager();

//测试  json------------------------------------------------
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
//------------------------------------------------------------
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
