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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.activity.MainActivity;
import com.meiaomei.bankusher.adapter.RecycleViewVipRemarkAdapter;
import com.meiaomei.bankusher.datetime.date.DatePickerDialog;
import com.meiaomei.bankusher.datetime.time.TimePickerDialog;
import com.meiaomei.bankusher.db.common.CommonDao;
import com.meiaomei.bankusher.db.common.impl.CommonDaoImpl;
import com.meiaomei.bankusher.dialog.AlertDialogCommon;
import com.meiaomei.bankusher.entity.ThirteenParamModel;
import com.meiaomei.bankusher.entity.event.StringModel;
import com.meiaomei.bankusher.manager.BankUsherDB;
import com.meiaomei.bankusher.utils.DateUtils;
import com.meiaomei.bankusher.utils.ExcelUtil;
import com.meiaomei.bankusher.utils.ImageUtils;
import com.meiaomei.bankusher.utils.ToastUtils;
import com.meiaomei.bankusher.view.ClearTextView;
import com.meiaomei.bankusher.view.spinner.MySpinerView;
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
public class VipRemarkFragment extends BaseFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    @ViewInject(R.id.bt_quary)
    Button bt_quary;
    @ViewInject(R.id.recycle)
    RecyclerView recycle;
    @ViewInject(R.id.tv_remark_name_value)
    TextView tv_remark_name_value;
    @ViewInject(R.id.tv_remark_phone_value)
    TextView tv_remark_phone_value;
    @ViewInject(R.id.tv_remark_viporder_value)
    TextView tv_remark_viporder_value;
    @ViewInject(R.id.num)
    TextView num;
    @ViewInject(R.id.rl_norecord)
    RelativeLayout rl_norecord;
    @ViewInject(R.id.et_name)//选择贵宾姓名的按钮
            TextView et_name;
    @ViewInject(R.id.tv_time2)//开始时间
            ClearTextView tv_start_time2;
    @ViewInject(R.id.tv_end_time2)//结束时间
            ClearTextView tv_end_time2;
    @ViewInject(R.id.iv_vip_remark)//人的头像
            ImageView iv_vip_remark;
    @ViewInject(R.id.cb_all)//全选
            CheckBox cb_all;
    @ViewInject(R.id.sp_viporder)//vip 级别的选择spinner
            MySpinerView sp_viporder;
    @ViewInject(R.id.rl_all)//抓拍类
            RelativeLayout rl_all;
    @ViewInject(R.id.bt_export_aline)//单个导出
            Button bt_export_aline;
    @ViewInject(R.id.bt_export_all)//多个导出
            Button bt_export_all;
    @ViewInject(R.id.rl_root)
    RelativeLayout rl_root;

    String[] title = {"抓拍时间", "抓拍地点", "姓名", "电话", "性别", "VIP级别", "身份证号"};
    ArrayList<String> vipArryaList = new ArrayList<>();
    LinkedHashMap<Integer, HashMap<String, String>> linkedHashMap = new LinkedHashMap<>();
    private DatePickerDialog dpd;//日期选择控件
    private TimePickerDialog tpd;//时间选择控件
    String date = "";
    String time = "";
    String timeFlag = "";
    RecycleViewVipRemarkAdapter recycleAdapter;
    DbUtils dbUtils;
    String spitemStr = "";
    CommonDao dao;
    final static String TAG = "VipRemarkFragment";
    List<ThirteenParamModel> tenListStart = new ArrayList<>();
    List<ThirteenParamModel> tenList;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    StringBuffer sb;
    String qstartTime;
    String qendTime;
    String qName;
    String qOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vip_remark, container, false);
        ViewUtils.inject(this, view);
        setupViews();
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
        if ("update".equals(msg) || "faceAdapter".equals(msg)) {
            Log.e(TAG, "getMessage: 接收到消息了" );
            initeData();
            recycleAdapter.refresh(tenListStart);
        }
    }

    private void initeData() {
        tenListStart = dao.getCommonListEntity(ThirteenParamModel.class, getActivity().getString(R.string.getAllVisitRecordByDelFlag), new String[]{"0"});
        if (tenListStart.size() == 0) {
            recycle.setVisibility(View.GONE);
            rl_norecord.setVisibility(View.VISIBLE);
        } else if (tenListStart != null) {
            recycle.setVisibility(View.VISIBLE);
            rl_norecord.setVisibility(View.GONE);
        }
    }


    /**
     * 初始化spinner 下拉列表
     */
    private void setupViews() {
        sp_viporder.initText("");//默认传入值为""
        String[] names2 = {"青铜", "白银", "黄金", "铂金", "钻石", "无限制"};
        for (int i = 0; i < names2.length; i++) {
            vipArryaList.add(names2[i]);
        }
        sp_viporder.setData(vipArryaList);
    }

    private void initview() {
        recycle.setItemAnimator(new DefaultItemAnimator());//设置默认动画
        final LinearLayoutManager linermanager = new LinearLayoutManager(getActivity());
        linermanager.setOrientation(OrientationHelper.VERTICAL);//设置滚动方向，横向滚动
        recycle.setLayoutManager(linermanager);
        recycleAdapter = new RecycleViewVipRemarkAdapter(getActivity(), tenListStart, dbUtils, R.layout.recycle_remark_item);
        recycle.setAdapter(recycleAdapter);
        recycleAdapter.setRecycleviewItemOnclickListener(new RecycleViewVipRemarkAdapter.RecycleviewItemOnclickListener() {
            @Override
            public void onItemClik(View view, int position) {
                tv_remark_name_value.setText(TextUtils.isEmpty(tenListStart.get(position).getFourthPara())?"未录入":tenListStart.get(position).getFourthPara());
                tv_remark_phone_value.setText(TextUtils.isEmpty(tenListStart.get(position).getFifthPara())?"未录入":tenListStart.get(position).getFifthPara());
                tv_remark_viporder_value.setText(TextUtils.isEmpty(tenListStart.get(position).getEighthPara())?"未录入":tenListStart.get(position).getEighthPara());
                String base64=tenListStart.get(position).getTenthPara();
                if (base64.length()>48) {
                    String path = ImageUtils.base64ToBitmapPath(base64, new Date().getTime()+".jpg");
                    Picasso.with(getActivity()).load("file://" + path).into(iv_vip_remark);
                }else {
                    Picasso.with(getActivity()).load("file://" + base64).into(iv_vip_remark);
                }
            }
        });

        recycleAdapter.setCheckItemClickListener(new RecycleViewVipRemarkAdapter.CheckItemClickListener() {
            @Override
            public void onCheckClik(LinkedHashMap<Integer, HashMap<String, String>> excelMap) {
//                Log.e("excelMap", "onCheckClik: " + excelMap.size());
                num.setText(excelMap.size() + "");
                linkedHashMap = excelMap;
            }
        });
        recycleAdapter.setFootViewText("正在加载...");

        //得到spinner VIP级别的值
        sp_viporder.setTextData(new MySpinerView.TextData() {
            @Override
            public void getText(String value) {
                Log.e("value", "getText: " + value);
                spitemStr = value;
            }
        });

        et_name.setOnClickListener(new MyClickListener());
        tv_start_time2.setOnClickListener(new MyClickListener());
        tv_end_time2.setOnClickListener(new MyClickListener());
        rl_all.setOnClickListener(new MyClickListener());
        cb_all.setOnCheckedChangeListener(new MyCheckBoxCheckedListener());
        bt_export_aline.setOnClickListener(new MyClickListener());
        bt_export_all.setOnClickListener(new MyClickListener());
        bt_quary.setOnClickListener(new MyClickListener());
        et_name.addTextChangedListener(new MyTextWher());
        tv_start_time2.addTextChangedListener(new MyTextWher());
        tv_end_time2.addTextChangedListener(new MyTextWher());

        et_name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {//点击软键盘完成控件时触发的行为
                    //关闭光标并且关闭软键盘
                    et_name.setCursorVisible(false);
                    InputMethodManager im = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    im.hideSoftInputFromWindow(getActivity().getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return true;//消费掉该行为
            }
        });
    }

    private void initeDate(String flag) {
        timeFlag = flag;
        Calendar now = Calendar.getInstance();
        if (dpd == null) {
            dpd = DatePickerDialog.newInstance(
                    VipRemarkFragment.this,
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
        } else {
            dpd.initialize(
                    VipRemarkFragment.this,
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
                    VipRemarkFragment.this,
                    now.get(Calendar.HOUR_OF_DAY),
                    now.get(Calendar.MINUTE),
                    false
            );
        } else {
            tpd.initialize(
                    VipRemarkFragment.this,
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
            tv_start_time2.setText(date + time);
        } else if ("endTime".equals(timeFlag)) {
            tv_end_time2.setText(date + time);
        }
    }


    class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.et_name:
                    break;

                case R.id.tv_time2:
                    initeDate("startTime");
                    break;

                case R.id.tv_end_time2:
                    initeDate("endTime");
                    break;

                case R.id.rl_all:
                    if (!cb_all.isChecked()) {
                        cb_all.setChecked(true);
                    } else {
                        cb_all.setChecked(false);
                    }
                    break;

                case R.id.bt_export_aline:
                    if (linkedHashMap != null && !linkedHashMap.isEmpty()) {
                        ExcelUtil.writeExcel(getActivity(), linkedHashMap, "excel-vip" + System.currentTimeMillis(), title, "所选条目导出成功！文件在sd卡目录下xls文件夹下。");
                    } else {
                        ToastUtils.showToast("请选中数据后再进行导出操作！", getActivity(), Toast.LENGTH_SHORT);
                    }
                    break;

                case R.id.bt_export_all:
                    LinkedHashMap<Integer, HashMap<String, String>> linkedMap = new LinkedHashMap<>();
                    for (int i = 0; i < tenListStart.size(); i++) {
                        HashMap<String, String> messageMap = new HashMap<>();
                        messageMap.put("visitTime", DateUtils.longFromatDate(tenListStart.get(i).getFirstPara(), "yyyy-MM-dd HH:mm"));//操作数据
                        messageMap.put("visitAddress", tenListStart.get(i).getSecondPara());
                        messageMap.put("faceId", tenListStart.get(i).getThirdPara());
                        messageMap.put("name", tenListStart.get(i).getFourthPara());
                        messageMap.put("idNumber", tenListStart.get(i).getSeventhPara());
                        messageMap.put("age", tenListStart.get(i).getFifthPara());
                        messageMap.put("sex", tenListStart.get(i).getSixthPara());
                        messageMap.put("vipOrder", tenListStart.get(i).getEighthPara());
                        linkedMap.put(i, messageMap);
                    }
                    if (!linkedMap.isEmpty()) {
                        ExcelUtil.writeExcel(getActivity(), linkedMap,
                                "excel-" + "all", title, "全部数据导出成功！文件在sd卡目录下xls文件夹下。");
                    } else {
                        ToastUtils.showToast("没有可导出的数据！", getActivity(), Toast.LENGTH_SHORT);
                    }
                    break;

                case R.id.bt_quary:
                    //将之前选中的checkbox清空 清空已点击过的item
                    cb_all.setChecked(false);
                    tv_remark_name_value.setText("");
                    tv_remark_phone_value.setText("");
                    tv_remark_viporder_value.setText("");

                    //开始时间单独校验
                    if (!TextUtils.isEmpty(tv_start_time2.getText())) {
                        try {//不能选择未来时间
                            if (format.parse(tv_start_time2.getText().toString()).getTime() > System.currentTimeMillis()) {
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
                    if (!TextUtils.isEmpty(tv_end_time2.getText())) {
                        try {//不能选择未来时间
                            if (format.parse(tv_end_time2.getText().toString()).getTime() > System.currentTimeMillis()) {
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

                    if (!TextUtils.isEmpty(tv_start_time2.getText()) && !TextUtils.isEmpty(tv_end_time2.getText())) {
                        try {
                            //不能选择未来时间
                            if (format.parse(tv_start_time2.getText().toString()).getTime() > System.currentTimeMillis()
                                    || format.parse(tv_end_time2.getText().toString()).getTime() > System.currentTimeMillis()) {
                                new AlertDialogCommon
                                        .Builder(getActivity())
                                        .setContents(new String[]{"查询时间不能选择未来时间!"})
                                        .build()
                                        .createAlertDialog();
                                break;
                            }


                            //开始时间应该小于结束时间
                            if (format.parse(tv_start_time2.getText().toString()).getTime() > format.parse(tv_end_time2.getText().toString()).getTime()) {
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

                    if (!TextUtils.isEmpty(et_name.getText())) {
                        if (sb.toString().contains(" and vip.Name=")) {
                            int start = sb.indexOf(" and vip.Name=");
                            int lenth = (" and vip.Name=\'" + qName + "\'").length();
                            sb.replace(start, start + lenth, " and vip.Name=\'" + et_name.getText() + "\'");
                        } else {
                            sb.append(" and vip.Name=\'" + et_name.getText() + "\'");
                        }
                    }

                    if (!TextUtils.isEmpty(spitemStr)) {
                        if (sb.toString().contains(" and vip.VipOrder=")) {//binary replace
                            int start = sb.indexOf(" and vip.VipOrder=");
                            int lenth = (" and vip.VipOrder=\'" + qOrder + "\'").length();
                            if ("无限制".equals(spitemStr)) {//此次为无限制，删除上条限制
                                sb.delete(start, start + lenth);
                            } else {
                                sb.replace(start, start + lenth, " and vip.VipOrder=\'" + spitemStr + "\'");
                            }
                        } else {//once  add
                            if (!"无限制".equals(spitemStr)) {
                                sb.append(" and vip.VipOrder=\'" + spitemStr + "\'");
                            }
                        }
                    }

                    if (!TextUtils.isEmpty(tv_start_time2.getText()) && TextUtils.isEmpty(tv_end_time2.getText())) {//只有开始时间
                        try {
                            long slong = format.parse(tv_start_time2.getText().toString()).getTime();//选中日期
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

                    } else if (TextUtils.isEmpty(tv_start_time2.getText()) && !TextUtils.isEmpty(tv_end_time2.getText())) {//只有结束时间
                        try {//选中日期
                            long elong = format.parse(tv_end_time2.getText().toString()).getTime();
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

                    } else if (!TextUtils.isEmpty(tv_start_time2.getText()) && !TextUtils.isEmpty(tv_end_time2.getText())) {//all
                        try {//选中日期
                            long elong = format.parse(tv_end_time2.getText().toString()).getTime();
                            long slong = format.parse(tv_start_time2.getText().toString()).getTime();
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
                        int st = sb.indexOf(" order by vis.VisitTime desc");
                        int len = (" order by vis.VisitTime desc").length();
                        sb.delete(st, st + len);
                        sb.append(" order by vis.VisitTime desc");//保证加在尾巴
                    } else {
                        sb.append(" order by vis.VisitTime desc");//保证加在尾巴
                    }
                    Log.e(TAG, "sql-vipremark= " + sb.toString());
                    tenList = dao.getCommonListEntity(ThirteenParamModel.class, sb.toString(), new String[]{"0"});
                    tenListStart = tenList;//查过之后就要把查过的数据源给初始的list
                    if (tenList.size() == 0) {
                        recycle.setVisibility(View.GONE);
                        rl_norecord.setVisibility(View.VISIBLE);
                    } else {
                        recycle.setVisibility(View.VISIBLE);
                        rl_norecord.setVisibility(View.GONE);
                        recycleAdapter.refresh(tenList);
                    }

                    //保存数据
                    qendTime = tv_end_time2.getText().toString();
                    qstartTime = tv_start_time2.getText().toString();
                    qName = et_name.getText().toString();
                    qOrder = spitemStr;
                    break;
            }
        }
    }

    class MyTextWher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                if (sb.toString().contains(" and vip.Name=")) {//delect sql name
                    int start = sb.indexOf(" and vip.Name=");
                    int lenth = (" and vip.Name=\'" + qName + "\'").length();
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


    //全部选中的checkbox
    class MyCheckBoxCheckedListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == R.id.cb_all) {
                if (isChecked) {
                    Log.e("cb_all", "onCheckedChanged: 全部的checkbox被选中了");
                    recycleAdapter.refreshCheckBox(true, true);
                } else {
                    Log.e("cb_all", "onCheckedChanged: 不选了");
                    recycleAdapter.refreshCheckBox(false, true);
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
