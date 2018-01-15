package com.meiaomei.bankusher.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.utils.DeviceInfoUtils;
import com.meiaomei.bankusher.utils.SharedPrefsUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.et_AppId)
    EditText etAppId;
    @BindView(R.id.et_AppSecret)
    EditText etAppSecret;
    @BindView(R.id.et_ServerAddress)
    EditText etServerAddress;
    @BindView(R.id.et_DeviceId)
    EditText etDeviceId;
    @BindView(R.id.et_register)
    EditText etRegister;
    @BindView(R.id.btn_save_set)
    Button btnSaveSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        etDeviceId.setText(DeviceInfoUtils.getDevicedId());
        etDeviceId.setFocusable(false);
        etAppId.setText("user");
        etAppSecret.setText("12345");
        etRegister.setText("");
        etServerAddress.setText("http://192.168.0.183:8580");
    }


    @OnClick({R.id.btn_save_set})
    public void Onclick(View view) {
        switch (view.getId()) {
            case R.id.btn_save_set:
                String appId = etAppId.getText().toString();
                String appSecret = etAppSecret.getText().toString();
                String serverAddress = etServerAddress.getText().toString();
                String deviedId = etDeviceId.getText().toString();
                String register = etRegister.getText().toString();
                SharedPrefsUtil.putValue(SettingActivity.this, "appId", appId);
                SharedPrefsUtil.putValue(SettingActivity.this, "appSecret", appSecret);
                SharedPrefsUtil.putValue(SettingActivity.this, "serverAddress", serverAddress);
                SharedPrefsUtil.putValue(SettingActivity.this, "deviedId", deviedId);
                SharedPrefsUtil.putValue(SettingActivity.this, "register", register);
                Toast.makeText(SettingActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }

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
