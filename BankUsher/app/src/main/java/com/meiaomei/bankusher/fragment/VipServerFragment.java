package com.meiaomei.bankusher.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.activity.VipRegistActivity;
import com.meiaomei.bankusher.activity.VisitRegistActivity;
import com.meiaomei.bankusher.adapter.RecycleVipSerDisposAdapter;
import com.meiaomei.bankusher.adapter.RecycleVipSerunDisposAdapter;
import com.meiaomei.bankusher.db.common.CommonDao;
import com.meiaomei.bankusher.db.common.impl.CommonDaoImpl;
import com.meiaomei.bankusher.entity.ThirteenParamModel;
import com.meiaomei.bankusher.entity.event.StringModel;
import com.meiaomei.bankusher.manager.BankUsherDB;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class VipServerFragment extends BaseFragment {

    @ViewInject(R.id.bt_disposed)
    Button bt_disposed;
    @ViewInject(R.id.bt_undisposed)
    Button bt_undisposed;


    @ViewInject(R.id.icon_disposed)
    View icon_disposed;
    @ViewInject(R.id.icon_undisposed)
    View icon_undisposed;
    @ViewInject(R.id.rl_norecord)
    RelativeLayout rl_norecord;

    @ViewInject(R.id.recycle_vipserver_undis)
    RecyclerView recycle_vipserver_undis;

    @ViewInject(R.id.recycle_vipserver_dis)//已处理
            RecyclerView recycle_vipserver_dis;
    @ViewInject(R.id.rl_vipser_message)
    RelativeLayout rl_vipser_message;
    @ViewInject(R.id.tv_num_vipser_message)
    TextView tv_num_vipser_message;
    @ViewInject(R.id.bt_regist)
    Button bt_regist;

    DbUtils dbUtils;
    String TAG = getClass().getName();
    RecycleVipSerDisposAdapter recycleVipSerDisposAdapter;//已处理的
    RecycleVipSerunDisposAdapter recycleVipSerunDisposAdapter;//已处理的
    List<ThirteenParamModel> tenListStartDispose = new ArrayList<>();
    List<ThirteenParamModel> tenListStartunDispose = new ArrayList<>();
    CommonDao dao;
    int waitNum = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_vip_server, container, false);
        ViewUtils.inject(this, view);
        initOther();
        Log.e(TAG, "=====onCreateView==== ");
        iniData();
        initView();
        bt_disposed.setOnClickListener(new MyClickListener());
        bt_undisposed.setOnClickListener(new MyClickListener());
        bt_regist.setOnClickListener(new MyClickListener());
        bt_regist.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent visitIntent = new Intent(getActivity(), VisitRegistActivity.class);
                startActivity(visitIntent);
                return false;
            }
        });
        return view;
    }

    private void initOther() {
        dbUtils = BankUsherDB.getDbUtils();
        dao = new CommonDaoImpl(getActivity());
        EventBus.getDefault().register(this);
    }

    //处理eventbus传递来的消息
    @Subscribe(threadMode = ThreadMode.MAIN)//主线程中更新ui
    public void getMessage(StringModel stringModel) {
        String key = stringModel.getKey();
        String msg =stringModel.getMsg();
        if ("update".equals(msg) || "remarkAdapter".equals(msg) || "faceAdapter".equals(msg)) {
            Log.e(TAG, "getMessage: 接收到消息了" );
            iniData();
            recycleVipSerunDisposAdapter.refresh(tenListStartunDispose);
        }
    }

    private void iniData() {
        tenListStartunDispose = dao.getCommonListEntity(ThirteenParamModel.class, getActivity().getString(R.string.getAllVisitRecordByHandleFlag), new String[]{"0"});
        waitNum = tenListStartunDispose.size();
        tenListStartDispose = dao.getCommonListEntity(ThirteenParamModel.class, getActivity().getString(R.string.getAllVisitRecordByHandleFlag), new String[]{"1"});
        if (tenListStartunDispose.size() == 0) {//待处理的个数为0
            rl_vipser_message.setVisibility(View.GONE);
            recycle_vipserver_undis.setVisibility(View.GONE);
            rl_norecord.setVisibility(View.VISIBLE);
        } else if (tenListStartunDispose != null) {//待处理的个数大于0
            rl_vipser_message.setVisibility(View.VISIBLE);
            tv_num_vipser_message.setText(tenListStartunDispose.size() + "");
            recycle_vipserver_undis.setVisibility(View.VISIBLE);
            rl_norecord.setVisibility(View.GONE);
        }
    }



    private void initView() {
        //待处理的
        recycle_vipserver_undis.setItemAnimator(new DefaultItemAnimator());//设置默认动画
        final LinearLayoutManager linermanager = new LinearLayoutManager(getActivity());
        linermanager.setOrientation(OrientationHelper.VERTICAL);//设置滚动方向，横向滚动
        recycle_vipserver_undis.setLayoutManager(linermanager);
        recycleVipSerunDisposAdapter = new RecycleVipSerunDisposAdapter(getActivity(), dbUtils, R.layout.recycle_vipserver_undis_item, tenListStartunDispose);
        recycle_vipserver_undis.setAdapter(recycleVipSerunDisposAdapter);
        recycleVipSerunDisposAdapter.setFootViewText("正在加载中...");
        recycleVipSerunDisposAdapter.setBtnClick(new RecycleVipSerunDisposAdapter.BtnClick() {
            @Override
            public void Click(ThirteenParamModel thirteenParamModel) {
                if (waitNum >= 0) {//避免频繁查数据库
                    waitNum--;
                    if (waitNum == 0) {
                        rl_norecord.setVisibility(View.VISIBLE);
                        rl_vipser_message.setVisibility(View.GONE);
                    } else {
                        tv_num_vipser_message.setText(waitNum + "");//待处理的个数要-1
                    }
                }

                tenListStartDispose.add(0, thirteenParamModel);
                recycleVipSerDisposAdapter.refresh(tenListStartDispose);//刷新已处理的数据源
            }
        });


        //已处理的
        recycle_vipserver_dis.setItemAnimator(new DefaultItemAnimator());//设置默认动画
        final LinearLayoutManager linermanager1 = new LinearLayoutManager(getActivity());
        linermanager1.setOrientation(OrientationHelper.VERTICAL);//设置滚动方向，横向滚动
        recycle_vipserver_dis.setLayoutManager(linermanager1);
        recycleVipSerDisposAdapter = new RecycleVipSerDisposAdapter(getActivity(), dbUtils, R.layout.recycle_vipserver_dis_item, tenListStartDispose);
        recycle_vipserver_dis.setAdapter(recycleVipSerDisposAdapter);
        recycleVipSerDisposAdapter.setFootText("正在加载中...");
    }

    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_disposed:// 1 已处理
                    icon_disposed.setVisibility(View.VISIBLE);
                    icon_undisposed.setVisibility(View.GONE);
                    recycle_vipserver_undis.setVisibility(View.GONE);
                    if (tenListStartDispose.size() == 0) {
                        rl_norecord.setVisibility(View.VISIBLE);
                        recycle_vipserver_dis.setVisibility(View.GONE);
                    } else if (tenListStartDispose != null) {
                        recycle_vipserver_dis.setVisibility(View.VISIBLE);
                        rl_norecord.setVisibility(View.GONE);
                    }
                    break;

                case R.id.bt_undisposed:// 0 待处理
                    icon_undisposed.setVisibility(View.VISIBLE);
                    icon_disposed.setVisibility(View.GONE);
                    recycle_vipserver_dis.setVisibility(View.GONE);
                    if (tenListStartunDispose.size() == 0) {
                        recycle_vipserver_undis.setVisibility(View.GONE);
                        rl_vipser_message.setVisibility(View.GONE);
                        rl_norecord.setVisibility(View.VISIBLE);
                    } else if (tenListStartunDispose != null) {//有数据 列表和条数显示
                        recycle_vipserver_undis.setVisibility(View.VISIBLE);
                        rl_vipser_message.setVisibility(View.VISIBLE);
                        rl_norecord.setVisibility(View.GONE);
                    }
                    break;

                case R.id.bt_regist:
                    Intent intent = new Intent(getActivity(), VipRegistActivity.class);
                    startActivity(intent);

                    break;
            }
        }
    }

    //切换fragment的时候这个方法坑肯定走
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            Log.e("不可见了", "hidden===" + hidden);
        } else {
            Log.e("可见了", "hidden===" + hidden);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
