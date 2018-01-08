package com.meiaomei.bankusher.fragment;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.activity.MainActivity;
import com.meiaomei.bankusher.adapter.RecycleViewFaceAdapter;
import com.meiaomei.bankusher.datetime.date.DatePickerDialog;
import com.meiaomei.bankusher.datetime.time.TimePickerDialog;
import com.meiaomei.bankusher.db.common.impl.CommonDaoImpl;
import com.meiaomei.bankusher.dialog.AlertDialogCommon;
import com.meiaomei.bankusher.entity.ThirteenParamModel;
import com.meiaomei.bankusher.entity.event.StringModel;
import com.meiaomei.bankusher.manager.BankUsherDB;
import com.meiaomei.bankusher.utils.DateUtils;
import com.meiaomei.bankusher.utils.ExcelUtil;
import com.meiaomei.bankusher.utils.ImageUtils;
import com.meiaomei.bankusher.utils.ToastUtils;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FaceTakeFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    @ViewInject(R.id.et_face_address)//选择城市的按钮
            EditText et_face_address;
    @ViewInject(R.id.tv_face_start_time)//开始时间
            TextView tv_face_start_time;
    @ViewInject(R.id.tv_face_end_time)//结束时间
            TextView tv_face_end_time;
    @ViewInject(R.id.bt_face_quary)
    Button bt_face_quary;
    @ViewInject(R.id.recycle_facetake)
    RecyclerView recycle_facetake;
    @ViewInject(R.id.iv_faceTake)//人的头像
            ImageView iv_faceTake;
    @ViewInject(R.id.bt_face_export_aline)//单个导出
            RadioButton bt_face_export_aline;
    @ViewInject(R.id.bt_face_export_all)//多个导出
            RadioButton bt_face_export_all;
    @ViewInject(R.id.cb_face_all)//全选
            CheckBox cb_face_all;
    @ViewInject(R.id.rl_face_all)
    RelativeLayout rl_face_all;
    @ViewInject(R.id.tv_face_name_value)
    TextView tv_face_name_value;
    @ViewInject(R.id.tv_face_address_value)
    TextView tv_face_address_value;
    @ViewInject(R.id.tv_face_sex_value)
    TextView tv_face_sex_value;
    @ViewInject(R.id.face_num)
    TextView face_num;
    @ViewInject(R.id.rl_face_norecord)
    RelativeLayout rl_face_norecord;
    @ViewInject(R.id.rb_nv)
    RadioButton rb_nv;
    @ViewInject(R.id.rb_nan)
    RadioButton rb_nan;
    @ViewInject(R.id.rb_null)
    RadioButton rb_null;

    LinkedHashMap<Integer, HashMap<String, String>> linkedHashMap = new LinkedHashMap<>();
    private DatePickerDialog dpd;//日期选择控件
    private TimePickerDialog tpd;//时间选择控件
    String date = "";
    String time = "";
    String timeFlag = "";
    RecycleViewFaceAdapter faceAdapter;
    DbUtils dbUtils;
    String[] title = {"抓拍时间", "抓拍地点"};
    String TAG = getClass().getName();
    String gender = "";
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    List<ThirteenParamModel> tenList;
    CommonDaoImpl dao;
    List<ThirteenParamModel> tenListInit = new ArrayList<>();
    StringBuffer sb;
    String qendTime;
    String qstartTime;
    String qSex;
    String qAddress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_face_take, container, false);
        ViewUtils.inject(this, view);
        initOther();
        initeData();
        initview();
        return view;
    }

    private void initOther() {
        dbUtils = BankUsherDB.getDbUtils();
        dao = new CommonDaoImpl(getActivity(), dbUtils);
        EventBus.getDefault().register(this);
        String sql = getActivity().getString(R.string.getAllVisitRecordByDelFlag);
        sb = new StringBuffer(sql);
        MainActivity.MyTouchListener myTouchListener = new MainActivity.MyTouchListener() {
            @Override
            public void onTouchEvent(MotionEvent ev) {
                if (ev.getAction() == MotionEvent.ACTION_DOWN) {
                    View v = getActivity().getCurrentFocus();//软键盘消失
                    if (isShouldHideKeyboard(v, ev)) {
                        hideKeyboard(v.getWindowToken());
                    }
                }
            }
        };

        // 将myTouchListener注册到分发列表
        ((MainActivity) this.getActivity()).registerMyTouchListener(myTouchListener);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getMessage(StringModel stringModel){
        String key = stringModel.getKey();
        String msg =stringModel.getMsg();
        if ("update".equals(msg) || "remarkAdapter".equals(msg)) {
            Log.e(TAG, "getMessage: 接收到消息了" );
            initeData();
            faceAdapter.refresh(tenListInit);
        }
    }

    private void initview() {
        recycle_facetake.setItemAnimator(new DefaultItemAnimator());//设置默认动画
        LinearLayoutManager linermanager = new LinearLayoutManager(getActivity());
        linermanager.setOrientation(OrientationHelper.VERTICAL);//设置滚动方向，横向滚动
        recycle_facetake.setLayoutManager(linermanager);
        faceAdapter = new RecycleViewFaceAdapter(getActivity(), tenListInit, dbUtils, R.layout.recycle_facetake_item);
        recycle_facetake.setAdapter(faceAdapter);
        faceAdapter.setFootViewText("正在加载。");
        faceAdapter.setRecycleviewItemOnclickListener(new RecycleViewFaceAdapter.RecycleviewItemOnclickListener() {
            @Override
            public void onItemClik(View view, int position) {
                tv_face_name_value.setText(TextUtils.isEmpty(tenListInit.get(position).getFourthPara())?"未录入":tenListInit.get(position).getFourthPara());
                tv_face_address_value.setText(TextUtils.isEmpty(tenListInit.get(position).getSecondPara())?"未录入":tenListInit.get(position).getSecondPara());
                tv_face_sex_value.setText(TextUtils.isEmpty(tenListInit.get(position).getSixthPara())?"未录入":tenListInit.get(position).getSixthPara());
                String base64=tenListInit.get(position).getTenthPara();
                if (base64.length()>150) {
                    String path = ImageUtils.base64ToBitmapPath(base64, new Date().getTime()+".jpg");
                    Picasso.with(getActivity()).load("file://" + path).into(iv_faceTake);
                }else {
                    Picasso.with(getActivity()).load("file://" + base64).into(iv_faceTake);
                }
            }
        });

        faceAdapter.setCheckItemClickListener(new RecycleViewFaceAdapter.CheckItemClickListener() {
            @Override
            public void onCheckClik(LinkedHashMap<Integer, HashMap<String, String>> excelMap) {
//                Log.e("excelMap", "onCheckClik: " + excelMap.size());
                face_num.setText(excelMap.size() + "");
                linkedHashMap = excelMap;
            }
        });


        et_face_address.setOnClickListener(new MyClickListener());
        tv_face_start_time.setOnClickListener(new MyClickListener());
        tv_face_end_time.setOnClickListener(new MyClickListener());
        rl_face_all.setOnClickListener(new MyClickListener());
        cb_face_all.setOnCheckedChangeListener(new MyCheck());
        bt_face_export_aline.setOnClickListener(new MyClickListener());
        bt_face_export_all.setOnClickListener(new MyClickListener());
        bt_face_quary.setOnClickListener(new MyClickListener());

        et_face_address.addTextChangedListener(new MyText());
        tv_face_start_time.addTextChangedListener(new MyText());
        tv_face_end_time.addTextChangedListener(new MyText());
        //点击完成键后 软键盘消失
        et_face_address.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {//点击软键盘完成控件时触发的行为
                    //关闭光标并且关闭软键盘
                    et_face_address.setCursorVisible(false);
                    InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(getActivity().getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return true;//消费掉该行为
            }
        });

    }

    private void initeData() {
        tenListInit = dao.getCommonListEntity(ThirteenParamModel.class, getActivity().getString(R.string.getAllVisitRecordByDelFlag), new String[]{"0"});
        if (tenListInit.size() == 0) {
            recycle_facetake.setVisibility(View.GONE);
            rl_face_norecord.setVisibility(View.VISIBLE);
        } else if (tenListInit != null) {
            recycle_facetake.setVisibility(View.VISIBLE);
            rl_face_norecord.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.rb_nan, R.id.rb_nv, R.id.rb_null})
    public void gender(View view) {
        switch (view.getId()) {

            case R.id.rb_nv:
                gender = "女";
                break;
            case R.id.rb_nan:
                gender = "男";
                break;

            case R.id.rb_null:
                gender = "不选";
                break;
        }
    }


    class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.et_face_address:
                    break;

                case R.id.tv_face_start_time:
                    initeDate("startTime");
                    break;

                case R.id.tv_face_end_time:
                    initeDate("endTime");
                    break;

                case R.id.rl_face_all:
                    if (!cb_face_all.isChecked()) {
                        cb_face_all.setChecked(true);
                    } else {
                        cb_face_all.setChecked(false);
                    }
                    break;

                case R.id.bt_face_export_aline:
                    if (linkedHashMap != null && !linkedHashMap.isEmpty()) {
                        ExcelUtil.writeExcel(getActivity(), linkedHashMap, "ex陌生人" + System.currentTimeMillis(), title, "所选条目导出成功！文件在sd卡目录下xls文件夹下。");
                    } else {
                        ToastUtils.showToast("请选中数据后再进行导出操作！", getActivity(), Toast.LENGTH_SHORT);
                    }
                    break;

                case R.id.bt_face_export_all:
                    LinkedHashMap<Integer, HashMap<String, String>> linkedMap = new LinkedHashMap<>();
                    for (int i = 0; i < tenListInit.size(); i++) {
                        HashMap<String, String> messageMap = new HashMap<>();
                        messageMap.put("visitTime", DateUtils.longFromatDate(tenListInit.get(i).getFirstPara(), "yyyy-MM-dd HH:mm"));//操作数据
                        messageMap.put("visitAddress", tenListInit.get(i).getSecondPara());
                        messageMap.put("faceId", tenListInit.get(i).getThirdPara());
                        messageMap.put("name", tenListInit.get(i).getFourthPara());
                        messageMap.put("idNumber", tenListInit.get(i).getSeventhPara());
                        messageMap.put("age", tenListInit.get(i).getFifthPara());
                        messageMap.put("sex", tenListInit.get(i).getSixthPara());
                        messageMap.put("vipOrder", tenListInit.get(i).getEighthPara());
                        linkedMap.put(i, messageMap);
                    }
                    if (!linkedMap.isEmpty()) {
                        ExcelUtil.writeExcel(getActivity(), linkedMap,
                                "excel-" + "all", title, "全部数据导出成功！文件在sd卡目录下xls文件夹下。");
                    } else {
                        ToastUtils.showToast("没有可导出的数据！", getActivity(), Toast.LENGTH_SHORT);
                    }
                    break;

                case R.id.bt_face_quary:
                    //将之前选中的checkbox清空 清空已点击过的item
                    cb_face_all.setChecked(false);
                    tv_face_name_value.setText("");
                    tv_face_sex_value.setText("");
                    tv_face_address_value.setText("");

                    //开始时间单独校验
                    if (!TextUtils.isEmpty(tv_face_start_time.getText())) {
                        try {//不能选择未来时间
                            if (format.parse(tv_face_start_time.getText().toString()).getTime() > System.currentTimeMillis()) {
                                new AlertDialogCommon
                                        .Builder(getActivity())
                                        .setContents(new String[]{"查询时间不能选择未来时间!"})
                                        .build()
                                        .createAlertDialog();
                                break;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    //结束时间单独校验
                    if (!TextUtils.isEmpty(tv_face_end_time.getText())) {
                        try {//不能选择未来时间
                            if (format.parse(tv_face_end_time.getText().toString()).getTime() > System.currentTimeMillis()) {
                                new AlertDialogCommon
                                        .Builder(getActivity())
                                        .setContents(new String[]{"查询时间不能选择未来时间!"})
                                        .build()
                                        .createAlertDialog();
                                break;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    if (!TextUtils.isEmpty(tv_face_start_time.getText()) && !TextUtils.isEmpty(tv_face_end_time.getText())) {
                        try {
                            //不能选择未来时间
                            if (format.parse(tv_face_start_time.getText().toString()).getTime() > System.currentTimeMillis()
                                    || format.parse(tv_face_end_time.getText().toString()).getTime() > System.currentTimeMillis()) {
                                new AlertDialogCommon
                                        .Builder(getActivity())
                                        .setContents(new String[]{"查询时间不能选择未来时间!"})
                                        .build()
                                        .createAlertDialog();
                                break;
                            }

                            //开始时间应该小于结束时间
                            if (format.parse(tv_face_start_time.getText().toString()).getTime() > format.parse(tv_face_end_time.getText().toString()).getTime()) {
                                new AlertDialogCommon
                                        .Builder(getActivity())
                                        .setContents(new String[]{"查询开始时间应该小于结束时间!"})
                                        .build()
                                        .createAlertDialog();
                                break;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }


                    if (!TextUtils.isEmpty(et_face_address.getText())) {
                        if (sb.toString().contains(" and vis.VisitAddress=")) {
                            int start = sb.indexOf(" and vis.VisitAddress=");
                            int lenth = (" and vis.VisitAddress=\'" + qAddress + "\'").length();
                            sb.replace(start, start + lenth, " and vis.VisitAddress=\'" + et_face_address.getText() + "\'");
                        } else {
                            sb.append(" and vis.VisitAddress=\'" + et_face_address.getText() + "\'");
                        }
                    }

                    if (!TextUtils.isEmpty(gender)) {
                        if (sb.toString().contains(" and vip.Sex=")) {
                            int st = sb.indexOf(" and vip.Sex=");
                            int len = (" and vip.Sex=\'" + qSex + "\'").length();
                            if ("不选".equals(gender)) {
                                sb.delete(st, st + len);
                            } else {
                                sb.replace(st, len + st, " and vip.Sex=\'" + gender + "\'");
                            }
                        } else {
                            if (!"不选".equals(gender)) {
                                sb.append(" and vip.Sex=\'" + gender + "\'");
                            }
                        }
                    }

                    if (!TextUtils.isEmpty(tv_face_start_time.getText()) && TextUtils.isEmpty(tv_face_end_time.getText())) {//只有开始时间
                        try {
                            long slong = format.parse(tv_face_start_time.getText().toString()).getTime();//选中日期
                            if (sb.toString().contains(" and vis.VisitTime >=")) {//binary replace
                                int start = sb.indexOf(" and vis.VisitTime >=");
                                long qslong = format.parse(qstartTime).getTime();//last search starttime
                                int lenth = (" and vis.VisitTime >=" + qslong).length();
                                sb.replace(start, start + lenth, " and vis.VisitTime >=" + slong);
                            } else {
                                sb.append(" and vis.VisitTime >=" + slong); //库 >=输入
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    } else if (TextUtils.isEmpty(tv_face_start_time.getText()) && !TextUtils.isEmpty(tv_face_end_time.getText())) {//只有结束时间
                        try {//选中日期
                            long elong = format.parse(tv_face_end_time.getText().toString()).getTime();
                            if (sb.toString().contains(" and vis.VisitTime <=")) {//binary replace
                                int start = sb.indexOf(" and vis.VisitTime <=");
                                long qelong = format.parse(qendTime).getTime();//last search endtime
                                int lenth = (" and vis.VisitTime <=" + qelong).length();
                                sb.replace(start, start + lenth, " and vis.VisitTime <=" + elong);
                            } else {
                                sb.append(" and vis.VisitTime <=" + elong); //库< 输入
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    } else if (!TextUtils.isEmpty(tv_face_start_time.getText()) && !TextUtils.isEmpty(tv_face_end_time.getText())) {//都有
                        try {//选中日期
                            long elong = format.parse(tv_face_end_time.getText().toString()).getTime();
                            long slong = format.parse(tv_face_start_time.getText().toString()).getTime();
                            if (sb.toString().contains(" and vis.VisitTime >=") && sb.toString().contains(" and vis.VisitTime <=")) {
                                int start = sb.indexOf(" and vis.VisitTime >=");
                                long qslong = format.parse(qstartTime).getTime();//last search starttime
                                long qelong = format.parse(qendTime).getTime();//last search endtime
                                int lenth = (" and vis.VisitTime >=" + qslong + " and vis.VisitTime <=" + qelong).length();
                                sb.replace(start, start + lenth, " and vis.VisitTime >=" + slong + " and vis.VisitTime <=" + elong);
                            } else {
                                sb.append(" and vis.VisitTime >=" + slong + " and vis.VisitTime <=" + elong);
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    //更新数据源
                    if (sb.toString().contains(" order by vis.VisitTime desc")) {
                        int st=sb.indexOf(" order by vis.VisitTime desc");
                        int len=(" order by vis.VisitTime desc").length();
                        sb.delete(st,st+len);
                        sb.append(" order by vis.VisitTime desc");//保证加在尾巴
                    }else {
                        sb.append(" order by vis.VisitTime desc");//保证加在尾巴
                    }
                    tenList = dao.getCommonListEntity(ThirteenParamModel.class, sb.toString(), new String[]{"0"});
                    Log.e(TAG, "sql-facetake= " + sb.toString());
                    tenListInit = tenList;//查过之后就要把查过的数据源给初始的list
                    if (tenList.size() == 0) {
                        recycle_facetake.setVisibility(View.GONE);
                        rl_face_norecord.setVisibility(View.VISIBLE);
                    } else {
                        recycle_facetake.setVisibility(View.VISIBLE);
                        rl_face_norecord.setVisibility(View.GONE);
                        faceAdapter.refresh(tenList);
                    }

                    //保存数据
                    qAddress = et_face_address.getText().toString();
                    qendTime = tv_face_end_time.getText().toString();
                    qstartTime = tv_face_start_time.getText().toString();
                    qSex = gender;
                    break;
            }
        }
    }

    class MyText implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                if (sb.toString().contains(" and vis.VisitAddress=")) {
                    int start = sb.indexOf(" and vis.VisitAddress=");
                    int lenth = (" and vis.VisitAddress=\'" + qAddress + "\'").length();
                    sb.delete(start, start + lenth);
                }

                if (sb.toString().contains(" and vis.VisitTime >=")) {//delect sql visitstime
                    int start = sb.indexOf(" and vis.VisitTime >=");
                    long qslong = 0;//last search starttime
                    try {
                        qslong = format.parse(qstartTime).getTime();
                        int lenth = (" and vis.VisitTime >=" + qslong).length();
                        sb.delete(start, start + lenth);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if (sb.toString().contains(" and vis.VisitTime <=")) {//delect sql visitetime
                    int start = sb.indexOf(" and vis.VisitTime <=");
                    long qelong = 0;//last search endtime
                    try {
                        qelong = format.parse(qendTime).getTime();
                        int lenth = (" and vis.VisitTime <=" + qelong).length();
                        sb.delete(start, start + lenth);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }

    private void initeDate(String flag) {
        timeFlag = flag;
        Calendar now = Calendar.getInstance();
        if (dpd == null) {
            dpd = DatePickerDialog.newInstance(
                    FaceTakeFragment.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        } else {
            dpd.initialize(
                    FaceTakeFragment.this,
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
                    FaceTakeFragment.this,
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    false
            );
        } else {
            tpd.initialize(
                    FaceTakeFragment.this,
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
        Log.e("time", "onDateSet==== " + time);
        if ("startTime".equals(timeFlag)) {
            tv_face_start_time.setText(date + time);
        } else if ("endTime".equals(timeFlag)) {
            tv_face_end_time.setText(date + time);
        }
    }

    class MyCheck implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.cb_face_all) {
                if (isChecked) {
                    Log.e("cb_all", "onCheckedChanged: 全部的checkbox被选中了");
                    faceAdapter.refreshCheckBox(true, true);
                } else {
                    Log.e("cb_all", "onCheckedChanged: 不选了");
                    faceAdapter.refreshCheckBox(false, true);
                }
            }
        }
    }


    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     *
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
