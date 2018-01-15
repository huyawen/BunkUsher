package com.meiaomei.bankusher.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.meiaomei.bankusher.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GuideActivity extends AppCompatActivity {

    @BindView(R.id.sp_bg)
    ImageView spBg;
    @BindView(R.id.sp_jump_btn)
    Button spJumpBtn;

    CountDownTimer countDownTimer = new CountDownTimer(3400L, 1000L) {
        @Override
        public void onTick(long l) {
            Log.e("GuideActivity", " " + l);
            spJumpBtn.setText("跳过(" +  l / 1000L + "s)");
        }

        @Override
        public void onFinish() {
            spJumpBtn.setText("跳过(0s)");
            gotoLoginOrMainActivity();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        startClock();
    }

    @OnClick({R.id.sp_bg, R.id.sp_jump_btn})
    public void clickButton(View view) {
        if (view.getId() == R.id.sp_bg) {
        } else if (view.getId() == R.id.sp_jump_btn) {
            gotoLoginOrMainActivity();
        }

    }

    private void gotoLoginOrMainActivity() {
        countDownTimer.cancel();
        gotoLoginActivity();
    }

    private void gotoLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }


    private void startClock() {
        this.spJumpBtn.setVisibility(View.VISIBLE);//可见
        this.countDownTimer.start();
    }

    @Override
    protected void onDestroy() {

        if (this.countDownTimer != null)
            this.countDownTimer.cancel();
        super.onDestroy();
    }
}
