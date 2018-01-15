package com.meiaomei.bankusher.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.dialog.AlertDialogCommon;
import com.meiaomei.bankusher.entity.MyResponse;
import com.meiaomei.bankusher.manager.OkHttpManager;
import com.meiaomei.bankusher.service.DownLoadService;
import com.meiaomei.bankusher.utils.DeviceInfoUtils;
import com.meiaomei.bankusher.utils.SharedPrefsUtil;
import com.meiaomei.bankusher.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.meiaomei.bankusher.fragment.VipRemarkFragment.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends BaseFragment {


    @BindView(R.id.tv_versionMessage)
    TextView tvVersionMessage;
    @BindView(R.id.ll_relaseMessage)
    LinearLayout llRelaseMessage;
    @BindView(R.id.tv_detail)
    TextView tvDetail;
    @BindView(R.id.ll_function)
    LinearLayout llFunction;
    @BindView(R.id.tv_update)
    TextView tvUpdate;
    @BindView(R.id.ll_update)
    LinearLayout llUpdate;
    @BindView(R.id.tv_version)
    TextView tvVersion;
    @BindView(R.id.tv_checkUpdate)
    TextView tvCheckUpdate;

    private Unbinder unbinder;
    String baseUrl = "";
    OkHttpManager okHttpManager;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        ButterKnife.bind(this, view);
        okHttpManager = new OkHttpManager();
        tvVersion.setText("当前版本：" + DeviceInfoUtils.getAppVersionName());
        return view;
    }


    @OnClick({R.id.ll_relaseMessage, R.id.ll_function, R.id.ll_update, R.id.tv_checkUpdate})
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.ll_relaseMessage:
                Log.e(TAG, "onClick: " + "ll_relaseMessage");
                break;

            case R.id.ll_function:
                Log.e(TAG, "onClick: " + "ll_function");
                break;

            case R.id.ll_update:
                Log.e(TAG, "onClick: " + "ll_update");
                break;

            case R.id.tv_checkUpdate:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        baseUrl = SharedPrefsUtil.getValue(getActivity(), "serverAddress", "");
                        if (TextUtils.isEmpty(baseUrl)) {
                            baseUrl = "http://192.168.0.183:8580";
                        }
                        String deviceId = "1111111111111111";
                        String appVersion = DeviceInfoUtils.getAppVersionName();
                        String urlUpdate = OkHttpManager.getUrl(baseUrl, 10);
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("deviceId", deviceId);
                            jsonObject.put("versionNum", appVersion);
                            jsonObject.put("applyName", "welcomeTerminalAnd");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        final AlertDialogCommon.Builder builder = new AlertDialogCommon.Builder(getActivity());
                        okHttpManager.postJson(urlUpdate, jsonObject.toString(), new OkHttpManager.HttpCallBack() {
                            @Override
                            public void onSusscess(String data, String cookie) {
                                Gson gson = new Gson();
                                MyResponse myResponse = gson.fromJson(data, MyResponse.class);
                                if (myResponse != null && "200".equals(myResponse.getRespCode())) {
                                    final String apkUrl = myResponse.getData().getProductUrl();
                                    builder.setContents(new String[]{"服务器有新版本，确认下载吗？"})
                                            .setIsShowCancelBtn(true)
                                            .setCancleBtnText("取消")//确定在左边
                                            .setContentColor(R.color.alertTextContent)
                                            .setSubmitBtnText("确定").setSubmitClickListener(new AlertDialogCommon.DialogSubmitClickListener() {
                                        @Override
                                        public void submitButtonClickListener() {
//                                            点击确定  开启服务
                                            Intent intent=new Intent(getActivity(), DownLoadService.class);
                                            intent.putExtra("path",apkUrl);
                                            getActivity().startService(intent);
                                        }

                                    }).build().createAlertDialog();
                                } else if ("500".equals(myResponse.getRespCode())) {
                                    //此处应先检查是否有新版本，有的话弹出下载的弹窗。没有的话只弹一个弹窗。点击确定消失
                                    builder.setContents(new String[]{"当前已是最新版本！"})
                                            .setIsShowCancelBtn(false)
                                            .setSubmitBtnText("确定").build().createAlertDialog();

                                } else {//出现其他服务器异常进去
                                    ToastUtils.showToast(myResponse.getRespDesc(), getActivity(), 2);
                                }

                            }

                            @Override
                            public void onError(String meg) {
                                super.onError(meg);
                            }
                        });
                    }
                }).start();


                break;
        }

    }


    //用完注意解绑
    @Override
    public void onDestroy() {
        Log.e(TAG, "---onDestroy--- ");
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }
}
