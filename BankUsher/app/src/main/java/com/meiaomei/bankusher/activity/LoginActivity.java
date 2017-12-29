package com.meiaomei.bankusher.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.meiaomei.bankusher.entity.ResponseModel;
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

    String useName;
    String passWord;

    OkHttpManager manager;
    Dialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        ViewUtils.inject(this);
        BankUsherDB.creatNewTable();//创建数据库表
        manager = new OkHttpManager();
        progressDialog=new MyProgressDialog().createLoadingDialog(LoginActivity.this,"正在通讯，请稍后...");
        et_username.setText("admin");
        et_password.setText("123456");
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
                    useName = et_username.getText().toString();
                    passWord = et_password.getText().toString();
                    login();
                    break;

                case R.id.tv_forgetpwd:
                    Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
                    startActivity(intent);
//                    finish();
                    break;
            }
        }

    }

    private boolean checkLogin() {
        String userValue = SharedPrefsUtil.getValue(LoginActivity.this, "userName", "");
        String passValue = SharedPrefsUtil.getValue(LoginActivity.this, "passWord", "");
        if (TextUtils.isEmpty(userValue) && TextUtils.isEmpty(passValue)) {
            return false;
        } else {
            return false;//此处应为true
        }
    }


    //第一次登录
    private void login() {
        //非空检查
        if (TextUtils.isEmpty(et_username.getText().toString())) {
            Toast.makeText(LoginActivity.this, "请输入用户名！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(et_password.getText().toString())) {
            Toast.makeText(LoginActivity.this, "请输入密码！", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        //联网检查
        JSONObject localJSONObject = new JSONObject();
        String js = "";
        try {
            localJSONObject.put("username", et_username.getText().toString());
//            String md5=MD5.GetMD5(et_password.getText().toString());
            String md5 = MD5Utils.md5(et_password.getText().toString());
            localJSONObject.put("password", md5);
            js = localJSONObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String baseurl=SharedPrefsUtil.getValue(LoginActivity.this,"serverAddress","");
        if (TextUtils.isEmpty(baseurl)){
            baseurl="http://192.168.0.183:8580";
        }
        String url = OkHttpManager.getUrl(baseurl, 0);
        manager.postJson(url, js, new OkHttpManager.HttpCallBack() {
            @Override
            public void onSusscess(String data) {
                Gson gson = new Gson();
                ResponseModel responseModel = null;
                progressDialog.dismiss();
                try {
                    responseModel = gson.fromJson(data, ResponseModel.class);
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, "无法登录,发生了未知的错误！", Toast.LENGTH_SHORT).show();
                }
                if (responseModel != null && "200".equals(responseModel.getRespCode())) {//响应成功
                    //通过后
                    SharedPrefsUtil.putValue(LoginActivity.this, "userName", useName);
                    SharedPrefsUtil.putValue(LoginActivity.this, "passWord", passWord);
                    Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent1);
                    finish();
                } else if (responseModel != null && "500".equals(responseModel.getRespCode())) {
                    Toast.makeText(LoginActivity.this, "账号或密码错误！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "无法登录，联系管理员！", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(String meg) {
                super.onError(meg);
                //失败后
                progressDialog.dismiss();
                Log.e("errir", meg);
                Toast.makeText(LoginActivity.this, "无法连接到服务器，请检查网络！", Toast.LENGTH_SHORT).show();
            }
        });

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

}
