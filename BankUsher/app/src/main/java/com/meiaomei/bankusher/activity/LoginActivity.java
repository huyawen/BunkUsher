package com.meiaomei.bankusher.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.dialog.MyProgressDialog;
import com.meiaomei.bankusher.entity.MyResponse;
import com.meiaomei.bankusher.entity.Protocol;
import com.meiaomei.bankusher.manager.BankUsherDB;
import com.meiaomei.bankusher.manager.OkHttpManager;
import com.meiaomei.bankusher.utils.MD5Utils;
import com.meiaomei.bankusher.utils.SharedPrefsUtil;
import com.meiaomei.bankusher.view.ClearEditText;
import com.meiaomei.bankusher.view.EtpEditText;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.username)
    ClearEditText et_username;
    @BindView(R.id.password)
    EtpEditText et_password;
    @BindView(R.id.login_imbtn)
    ImageButton loginImbtn;
    @BindView(R.id.tv_forgetpwd)
    TextView tv_setting;

    OkHttpManager manager;
    Dialog progressDialog;
    String baseurl = "";
    boolean canLogin = false;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        ViewUtils.inject(this);
        BankUsherDB.creatNewTable();//创建数据库表
        getAppUserPermission();//动态获取权限

        manager = new OkHttpManager();
        progressDialog = new MyProgressDialog().createLoadingDialog(LoginActivity.this, "正在通讯，请稍后...");
        baseurl = SharedPrefsUtil.getValue(LoginActivity.this, "serverAddress", "");
        if (TextUtils.isEmpty(baseurl)) {
            baseurl = "http://192.168.0.183:8580";
        }
        et_username.setText("admin");
        et_password.setText("");
        boolean isLogined = checkLogin();
        if (isLogined) {
            Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent1);
            finish();
        }
        loginImbtn.setOnClickListener(new MyClicListener());
        tv_setting.setOnClickListener(new MyClicListener());

    }


    class MyClicListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.login_imbtn:
                    String useName = et_username.getText().toString();
                    String passWord = et_password.getText().toString();

                    //保存完成之后，发通知测试
                  /*  handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            NotificationUtils notificationUtils = new NotificationUtils(LoginActivity.this);
                            notificationUtils.sendSysNotification("胡亚文");
                        }
                    },10000);*/
                    login(useName, passWord);
                    break;

                case R.id.tv_forgetpwd:
                    Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
                    startActivity(intent);
                    break;
            }
        }

    }

    private boolean checkLogin() {
        String userValue = SharedPrefsUtil.getValue(LoginActivity.this, "userName", "");
        String passValue = SharedPrefsUtil.getValue(LoginActivity.this, "passWord", "");
        return checkLoginFromNet(userValue, passValue);
    }


    //第一次登录
    private void login(String use_Name, String pass_Word) {
        //非空检查
        if (TextUtils.isEmpty(use_Name)) {
            Toast.makeText(LoginActivity.this, "请输入用户名！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(pass_Word)) {
            Toast.makeText(LoginActivity.this, "请输入密码！", Toast.LENGTH_SHORT).show();
            return;
        }

        checkLoginFromNet(use_Name, pass_Word);
    }

    private boolean checkLoginFromNet(final String use_Name, final String pass_Word) {
        //联网检查
        progressDialog.show();
        JSONObject localJSONObject = new JSONObject();
        String js = "";
        try {
            localJSONObject.put("username", use_Name);
            String md5 = MD5Utils.md5(pass_Word);
            localJSONObject.put("password", md5);
            js = localJSONObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = OkHttpManager.getUrl(baseurl, 0);
        manager.postJson(url, js, new OkHttpManager.HttpCallBack() {
            @Override
            public void onSusscess(String data, String cookie) {
                Gson gson = new Gson();
                MyResponse myResponse = null;
                progressDialog.dismiss();
                try {
                    myResponse = gson.fromJson(data, MyResponse.class);
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, "无法登录,发生了未知的错误！", Toast.LENGTH_SHORT).show();
                }
                if (myResponse != null && "200".equals(myResponse.getRespCode())) {//响应成功
                    //通过后
                    canLogin = true;
                    if (!TextUtils.isEmpty(cookie) && cookie.contains("JSESSIONID=")) {
                        String cookie1 = cookie.substring(0, 43);
                        Protocol.setCookedId(cookie1);
                    }
                    SharedPrefsUtil.putValue(LoginActivity.this, "userName", use_Name);
                    SharedPrefsUtil.putValue(LoginActivity.this, "passWord", pass_Word);
                    Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent1);
                    finish();
                } else if (myResponse != null && "500".equals(myResponse.getRespCode())) {
                    Toast.makeText(LoginActivity.this, "账号或密码错误！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "无法登录，联系管理员！", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(String meg) {
                super.onError(meg);
                //失败后
                canLogin = false;
                progressDialog.dismiss();
                Log.e("errir", meg + "");
                Toast.makeText(LoginActivity.this, "无法连接到服务器，请检查网络！", Toast.LENGTH_SHORT).show();
            }
        });


        return canLogin;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {//把别的事件下发下去
            return true;
        }
        return onTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            //获取输入框当前的location位置
            v.getLocationInWindow(leftTop);
            int left = leftTop[0];
            int top = leftTop[1];
            int bottom = top + v.getHeight();
            int right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击的是输入框区域，保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    //6.0 动态获取权限
    public void getAppUserPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23 && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {//android6.0需要动态申请权限
                ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);//发起申请读写文件的权限
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
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);//发起申请读写文件的权限
                }
                break;
        }
    }
}
