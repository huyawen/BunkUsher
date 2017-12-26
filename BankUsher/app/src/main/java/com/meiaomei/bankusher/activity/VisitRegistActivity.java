package com.meiaomei.bankusher.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.datetime.date.DatePickerDialog;
import com.meiaomei.bankusher.datetime.time.TimePickerDialog;
import com.meiaomei.bankusher.entity.VipCustomerModel;
import com.meiaomei.bankusher.entity.VisitRecordModel;
import com.meiaomei.bankusher.entity.event.StringModel;
import com.meiaomei.bankusher.manager.BankUsherDB;
import com.meiaomei.bankusher.utils.DateUtils;
import com.meiaomei.bankusher.utils.FileUtils;
import com.meiaomei.bankusher.utils.NotificationUtils;
import com.meiaomei.bankusher.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Calendar;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VisitRegistActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    @BindView(R.id.tv_visitime)
    TextView tvVisitime;
    @BindView(R.id.et_cameraName)
    EditText et_cameraName;
    @BindView(R.id.et_address)
    EditText et_address;
    @BindView(R.id.bt_visit)
    Button btVisit;
    @BindView(R.id.bt_send)
    Button btSend;
    private DatePickerDialog dpd;//日期选择控件
    private TimePickerDialog tpd;//时间选择控件
    String date = "";
    String time = "";
    DbUtils dbUtils;
    int i=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_visit);
        dbUtils = BankUsherDB.getDbUtils();
        ButterKnife.bind(this);
    }

    @OnClick(R.id.bt_send)
    public void btnSend(){
        NotificationUtils notificationUtils=new NotificationUtils(VisitRegistActivity.this);
        notificationUtils.sendMyNotification();
    }

    @OnClick(R.id.bt_visit)
    public void btnVisit() {
        if (TextUtils.isEmpty(et_cameraName.getText().toString()) ||
                TextUtils.isEmpty(et_address.getText().toString())) {
            return;
        }

        long lTime = DateUtils.string2Long(date + time);
        VisitRecordModel visit = new VisitRecordModel();
        List<VipCustomerModel> list = null;
        try {
            list = dbUtils.findAll(VipCustomerModel.class);
            if (list != null&&list.size()>0) {
                Random random = new Random();
                visit.setFaceId(list.get(list.size()==1?0:random.nextInt(list.size() - 1)).getFaceId());//随机取一个人  nextInt方法必须是>0的参数  否则会抛异常
                visit.setVisitTime(lTime);
                visit.setCameraName(et_cameraName.getText().toString());
                visit.setVisitAddress(et_address.getText().toString());
                visit.setHandleFlag("0");
                visit.setVisitId(FileUtils.generateUuid());
                visit.setHardWareId(FileUtils.generateUuid());
                dbUtils.save(visit);
                ToastUtils.showToast("保存事件成功", VisitRegistActivity.this, Toast.LENGTH_SHORT);
            }else {
                ToastUtils.showToast("没有匹配的贵宾", VisitRegistActivity.this, Toast.LENGTH_SHORT);
            }
        } catch (DbException e) {
            ToastUtils.showToast("保存事件失败", VisitRegistActivity.this, Toast.LENGTH_SHORT);
            e.printStackTrace();
        }

    }


    @OnClick(R.id.tv_visitime)
    public void tvTime() {
        initeDate();
    }

    private void initeDate() {
        Calendar now = Calendar.getInstance();
        if (dpd == null) {
            dpd = DatePickerDialog.newInstance(
                    VisitRegistActivity.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        } else {
            dpd.initialize(
                    VisitRegistActivity.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        }
        dpd.setAccentColor(Color.parseColor("#00d3c5"));
        dpd.setVersion(DatePickerDialog.Version.VERSION_1);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    //得到日期
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        date = year + "-" + (++monthOfYear) + "-" + dayOfMonth;
        Log.e("date", "onDateSet==== " + date);
        Calendar now = Calendar.getInstance();
        if (tpd == null) {
            tpd = TimePickerDialog.newInstance(
                    VisitRegistActivity.this,
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    false
            );
        } else {
            tpd.initialize(
                    VisitRegistActivity.this,
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    now.get(Calendar.SECOND),
                    false
            );
        }
        tpd.setAccentColor(Color.parseColor("#00d3c5"));
        tpd.setVersion(TimePickerDialog.Version.VERSION_1);
        tpd.show(getFragmentManager(), "Datepickerdialog");

    }

    //得到时间
    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
        String secondString = second < 10 ? "0" + second : "" + second;
        time = " " + hourString + ":" + minuteString;
        Log.e("time", "onDateSet==== " + date + time);
        tvVisitime.setText(date + time);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StringModel model = new StringModel("update", "update");
        EventBus.getDefault().post(model);
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

    public  boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = { 0, 0 };
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
