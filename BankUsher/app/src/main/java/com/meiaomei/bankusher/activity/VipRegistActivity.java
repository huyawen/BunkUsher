package com.meiaomei.bankusher.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.DbUtils;
import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.entity.MyResponse;
import com.meiaomei.bankusher.manager.BankUsherDB;
import com.meiaomei.bankusher.manager.OkHttpManager;
import com.meiaomei.bankusher.utils.FileUtils;
import com.meiaomei.bankusher.utils.ImageUtils;
import com.meiaomei.bankusher.utils.SharedPrefsUtil;
import com.meiaomei.bankusher.utils.ToastUtils;
import com.meiaomei.bankusher.utils.UpLoadImage;
import com.meiaomei.bankusher.utils.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class VipRegistActivity extends AppCompatActivity {


    @BindView(R.id.iv_return)
    ImageView ivReturn;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.addpic)
    TextView addpic;
    @BindView(R.id.iv_img)
    ImageView ivImg;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.radioBtn_male)
    RadioButton radioBtnMale;
    @BindView(R.id.radioBtn_female)
    RadioButton radioBtnFemale;
    @BindView(R.id.radiogroup_gender)
    RadioGroup radiogroupGender;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_carNum)
    EditText etCarNum;
    @BindView(R.id.et_cardid)
    EditText etCardid;
    @BindView(R.id.radioBtn_level1)
    RadioButton radioBtnLevel1;
    @BindView(R.id.radioBtn_level2)
    RadioButton radioBtnLevel2;
    @BindView(R.id.radioBtn_level3)
    RadioButton radioBtnLevel3;
    @BindView(R.id.radioBtn_level4)
    RadioButton radioBtnLevel4;
    @BindView(R.id.radioBtn_level5)
    RadioButton radioBtnLevel5;
    @BindView(R.id.radiogroup_level)
    RadioGroup radiogroupLevel;
    @BindView(R.id.et_remark)
    EditText etRemark;
    @BindView(R.id.btn_signin)
    Button btnSignin;
    @BindView(R.id.btn_clear)
    Button btnClear;
    @BindView(R.id.root_regvip)
    RelativeLayout rootRegvip;
    String workNum = "";
    String name = "";
    String phone = "";
    String idCard = "";
    String remark = "";
    String gender = "";
    String level = "";
    @BindView(R.id.top_view)
    RelativeLayout topView;
    @BindView(R.id.sv_base)
    ScrollView svBase;
    String baseUrl = "";

    private Bitmap picbitmap;
    String imgFilePath = "";
    final static int REQUEST_CAMERA_1 = 21;
    final static String TAG = "VipRegistActivity";
    DbUtils dbUtils;
    boolean isEditer = false;
    String useId = "";
    OkHttpManager okHttpManager = new OkHttpManager();

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                ToastUtils.showToast("保存成功!", VipRegistActivity.this, Toast.LENGTH_SHORT);
            } else if (msg.what == 2) {
                ToastUtils.showToast("保存失败!", VipRegistActivity.this, Toast.LENGTH_SHORT);
            } else if (msg.what == 3) {
                ToastUtils.showToast("上传服务器成功!", VipRegistActivity.this, Toast.LENGTH_SHORT);
            } else if (msg.what == 4) {
                ToastUtils.showToast("上传服务失败!", VipRegistActivity.this, Toast.LENGTH_SHORT);
            } else if (msg.what == 5) {
                ToastUtils.showToast("服务器出错了，上传失败!", VipRegistActivity.this, Toast.LENGTH_SHORT);
            } else if (msg.what == 6) {
                ToastUtils.showToast("修改成功!", VipRegistActivity.this, Toast.LENGTH_SHORT);
            } else if (msg.what == 7) {
                ToastUtils.showToast("修改失败！", VipRegistActivity.this, Toast.LENGTH_SHORT);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_vip_regist);
        ButterKnife.bind(this);

        initOther();
    }

    private void initOther() {
        dbUtils = BankUsherDB.getDbUtils();
        tvTitle.setText("VIP 人员注册");
        Intent intent = getIntent();
        HashMap<String, String> linkedHashMap = (HashMap<String, String>) intent.getSerializableExtra("linkedHashMap");
        String path = intent.getStringExtra("imgPath");
        if (linkedHashMap != null && !TextUtils.isEmpty(path)) {
            isEditer = true;
            tvTitle.setText("VIP 人员修改");
            Picasso.with(VipRegistActivity.this)
                    .load(path)
                    .resize(200, 200)
                    .transform(new CircleTransform()).into(ivImg);
            etName.setText(linkedHashMap.get("姓名"));
            if ("男".equals(linkedHashMap.get("性别"))) {
                radioBtnMale.setChecked(true);
            } else if ("女".equals(linkedHashMap.get("性别"))) {
                radioBtnFemale.setChecked(true);
            }
            useId = linkedHashMap.get("id");
            etPhone.setText(linkedHashMap.get("电话"));
            etCarNum.setText(linkedHashMap.get("工号").equals("未录入") ? "" : linkedHashMap.get("工号"));
            etCardid.setText(linkedHashMap.get("身份证号").equals("未录入") ? "" : linkedHashMap.get("身份证号"));
            etRemark.setText(linkedHashMap.get("备注").equals("未录入") ? "" : linkedHashMap.get("备注"));
            switch (linkedHashMap.get("客户等级")) {
                case "一级":
                    radioBtnLevel4.setChecked(true);
                    break;
                case "二级":
                    radioBtnLevel3.setChecked(true);
                    break;
                case "三级":
                    radioBtnLevel2.setChecked(true);
                    break;
                case "四级":
                    radioBtnLevel1.setChecked(true);
                    break;
                default:
                    break;
            }
        }
    }


    @OnClick(R.id.btn_signin)
    public void btn_regist() {
        getInput();
        final String base64 = ImageUtils.imgToBase64(null, picbitmap); //imgpath 和 bitmap 传一个即可
        if (isEmpty()) {//非空项校验
            return;
        }

        if (!Utils.isChinaPhoneLegal(phone)) {
            ToastUtils.showToast("请输入正确的手机号！", VipRegistActivity.this, Toast.LENGTH_SHORT);
        }

        if (!TextUtils.isEmpty(idCard)) {
            if (!Utils.isIDCard(idCard)) {
                ToastUtils.showToast("请输入正确的身份证号！", VipRegistActivity.this, Toast.LENGTH_SHORT);
            }
        }

        if (base64 == null && !isEditer) {//传给后台的base64  如果是编辑进来的忽略此检查
            ToastUtils.showToast("请选择相机拍照！", VipRegistActivity.this, Toast.LENGTH_SHORT);
            return;
        }

        //传递给后台将录入的数据
        baseUrl = SharedPrefsUtil.getValue(VipRegistActivity.this, "serverAddress", "");
        if (TextUtils.isEmpty(baseUrl)) {
            baseUrl = "http://192.168.0.183:8580";
        }

        if (isEditer) {//编辑进来的
            if (!TextUtils.isEmpty(imgFilePath)) {
                //修改了图片，先把图片传至服务器
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String uploadUrl = OkHttpManager.getUrl(baseUrl, 9);//uploadUrl
                        String s=UpLoadImage.uploadFile(new File(imgFilePath),uploadUrl);
                        Log.e(TAG, "uploadFile: " + s);
//                        String response = UpLoadImage.uploadFiletwo(new File(imgFilePath), uploadUrl);
//                        Log.e(TAG, "uploadFiletwo: " + response);
                        okHttpManager.upLoadFile(uploadUrl, imgFilePath, new OkHttpManager.ReqCallBack<String>() {
                            @Override
                            public void onReqSuccess(String result) {
                                Log.e(TAG, "run: " + result);
                            }

                            @Override
                            public void onReqFailed(String errorMsg) {
                                Log.e(TAG, "run: " + errorMsg);
                            }
                        });

                        String editUrl = OkHttpManager.getUrl(baseUrl, 3);
                        JSONObject js = new JSONObject();
                        try {
                            if (UpLoadImage.SUCCESS.equals("")){
                                js.put("updateModules", "10"); //更新基本信息就是 2  更新图片和基本信息就是 10
                            } else {
                                js.put("updateModules", "2");
                            }
                            js.put("appId", "user");
                            js.put("appSecret", "12345");
                            js.put("id", useId);
                            js.put("cango", false);
                            js.put("name", name);
                            js.put("favourite", "");
                            js.put("orgCode", "1000");
                            js.put("telephone", phone);
                            int le = getIntLevel(level);
                            js.put("userLevel", le);
                            js.put("userType", "2");
                            js.put("workCode", workNum);//为工号
                            js.put("idCard", idCard);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        okHttpManager.postJson(editUrl, js.toString(), new OkHttpManager.HttpCallBack() {
                            @Override
                            public void onSusscess(String data, String cookie) {
                                Gson gson = new Gson();
                                if (!TextUtils.isEmpty(data)) {
                                    MyResponse myResponse = gson.fromJson(data, MyResponse.class);
                                    if (myResponse != null) {
                                        if ("200".equals(myResponse.getRespCode())) {
                                            handler.obtainMessage(6).sendToTarget();
                                        } else if ("500".equals(myResponse.getRespCode())) {
                                            handler.obtainMessage(7).sendToTarget();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onError(String meg) {
                                super.onError(meg);
                                handler.obtainMessage(5).sendToTarget();
                            }
                        });
                    }
                }).start();
            }

        } else if (!isEditer) {//新添加vip进来的
            String url = OkHttpManager.getUrl(baseUrl, 5);
            JSONObject js = new JSONObject();
            try {
                js.put("c4", "1");
                js.put("appId", "user");
                js.put("appSecret", "12345");
                js.put("base64Img", base64);
                js.put("cango", false);
                js.put("name", name);
                js.put("favourite", "");
                js.put("orgCode", "1000");
                js.put("telephone", phone);
                int le = getIntLevel(level);
                js.put("userLevel", le);
                js.put("userType", "2");
                js.put("workCode", workNum);//为工号
                js.put("idCard", idCard);
//            int ge = getIntGender(gender);
//            js.put("gender", ge);//不传性别
            } catch (JSONException e) {
                e.printStackTrace();
            }
            okHttpManager.postJson(url, js.toString(), new OkHttpManager.HttpCallBack() {
                @Override
                public void onSusscess(String data, String cookie) {
                    Gson gson = new Gson();
                    if (!TextUtils.isEmpty(data)) {
                        MyResponse myResponse = gson.fromJson(data, MyResponse.class);
                        if (myResponse != null) {
                            if ("200".equals(myResponse.getRespCode())) {
                                handler.obtainMessage(3).sendToTarget();
                            } else if ("500".equals(myResponse.getRespCode())) {
                                handler.obtainMessage(4).sendToTarget();
                            }
                        }
                    }
                }

                @Override
                public void onError(String meg) {
                    super.onError(meg);
                    handler.obtainMessage(5).sendToTarget();
                }
            });

            //传服务器报存 本地不保存
        /*
        VipCustomerModel vip = new VipCustomerModel();
        vip.setName(name);
        vip.setCarNumber(carNun);
        vip.setDelFlag("0");
        vip.setFaceId(FileUtils.generateUuid());
        vip.setIdNumber(cardid);
        vip.setPhoneNumber(phone);
        vip.setSex(gender);
        vip.setVipOrder(level);
        vip.setImgUrl(mFilePath);

        try {
            dbUtils.save(vip);
            handler.obtainMessage(1).sendToTarget();
        } catch (DbException e) {
            handler.obtainMessage(2).sendToTarget();
            e.printStackTrace();
        }*/

        }
    }

    private int getIntLevel(String slevel) {
        int intLevel = 0;
        //清空checkBox
        if (!TextUtils.isEmpty(slevel)) {
            switch (slevel) {

                case "四级":
                    intLevel = 4;
                    break;
                case "三级":
                    intLevel = 3;
                    break;
                case "二级":
                    intLevel = 2;
                    break;
                case "一级":
                    intLevel = 1;
                    break;

                case "普通":
                    intLevel = 1;
                    break;
            }
        }

        return intLevel;
    }

    private int getIntGender(String slevel) {
        int intLevel = 0;
        //清空checkBox
        if (!TextUtils.isEmpty(slevel)) {
            switch (slevel) {
                case "男":
                    intLevel = 1;
                    break;
                case "女":
                    intLevel = 0;
                    break;
            }
        }
        return intLevel;
    }


    @OnClick(R.id.iv_img)
    public void iv_Photo(View view) {
        File photoFile = FileUtils.createDir("photo");
        imgFilePath = photoFile + "/" + "img" + ".jpg";// 指定路径
        //系统相机调用
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 启动系统相机
        Uri photoUri = Uri.fromFile(new File(imgFilePath)); // 传递路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);// 更改系统默认存储路径
        startActivityForResult(intent, REQUEST_CAMERA_1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {//返回成功
            if (requestCode == REQUEST_CAMERA_1) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFilePath);
                picbitmap = bitmap;
                if (bitmap != null) {
                    ivImg.setBackground(null);
                    Picasso.with(VipRegistActivity.this)
                            .load("file://" + imgFilePath)
                            .resize(200, 200)
                            .transform(new CircleTransform()).into(ivImg);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    // Picasso.with(getContext()).load(mAccount.getHeadImageUrl()).transform(new CircleTransform()).into(mHeadImageView);
    // 声明一个Picasso 裁剪图片的类，设置为圆形头像。
    class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

    @OnClick(R.id.btn_clear)
    public void bt_clean() {//清空的按钮
        clean();
    }

    //清空
    private void clean() {
        Log.e(TAG, "clean: level=" + level + "--gender=" + gender);
        //清空文本
        etName.setText("");
        etPhone.setText("");
        etCardid.setText("");
        etRemark.setText("");
        etCarNum.setText("");

        //清空图片
        picbitmap = null;
        ivImg.setImageBitmap(null);
        ivImg.setBackground(getResources().getDrawable(R.mipmap.vip_default_photo));

        //清空checkBox
        if (!TextUtils.isEmpty(level)) {
            switch (level) {
                case "四级":
                    radioBtnLevel1.setChecked(false);
                    break;
                case "三级":
                    radioBtnLevel2.setChecked(false);
                    break;
                case "二级":
                    radioBtnLevel3.setChecked(false);
                    break;
                case "一级":
                    radioBtnLevel4.setChecked(false);
                    break;
                case "普通":
                    radioBtnLevel5.setChecked(false);
                    break;
            }
            level = null;
        }

        if (!TextUtils.isEmpty(gender)) {
            switch (gender) {
                case "男":
                    radioBtnMale.setChecked(false);
                    break;

                case "女":
                    radioBtnFemale.setChecked(false);
                    break;
            }
            gender = null;
        }
    }


    @OnClick(R.id.iv_return)
    public void iv_return(View view) {//结束的按钮
        finish();
    }

    @OnClick({R.id.radioBtn_male, R.id.radioBtn_female})
    public void gender(View view) {
        switch (view.getId()) {

            case R.id.radioBtn_female:
                gender = "女";
                break;
            case R.id.radioBtn_male:
                gender = "男";
                break;
        }
    }

    @OnClick({R.id.radioBtn_level1, R.id.radioBtn_level2, R.id.radioBtn_level3, R.id.radioBtn_level4, R.id.radioBtn_level5})
    public void level(View view) {
        switch (view.getId()) {

            case R.id.radioBtn_level1:
                level = "四级"; //4
                break;
            case R.id.radioBtn_level2:
                level = "三级"; //3
                break;
            case R.id.radioBtn_level3:
                level = "二级"; //2
                break;
            case R.id.radioBtn_level4:
                level = "一级";  //1
                break;
            case R.id.radioBtn_level5:
                level = "普通";  // null
                break;

        }

    }

    /**
     * 非空判断
     */
    private boolean isEmpty() {
        //非空验证
        if (TextUtils.isEmpty(name.trim())) {
            ToastUtils.showToast("姓名不能为空！", VipRegistActivity.this, Toast.LENGTH_SHORT);
            return true;
        }
        if (TextUtils.isEmpty(phone.trim())) {
            ToastUtils.showToast("手机号不能为空！", VipRegistActivity.this, Toast.LENGTH_SHORT);
            return true;
        }

        if (TextUtils.isEmpty(workNum.trim())) {
            ToastUtils.showToast("工号不能为空！", VipRegistActivity.this, Toast.LENGTH_SHORT);
            return true;
        }

        if (TextUtils.isEmpty(idCard.trim()) && !isEditer) {//编辑的时候不用校验身份证号
            ToastUtils.showToast("身份证号不能为空！", VipRegistActivity.this, Toast.LENGTH_SHORT);
            return true;
        }
        return false;
    }

    private void getInput() {
        workNum = etCarNum.getText().toString().replace("", "");
        name = etName.getText().toString().replace("", "");
        idCard = etCardid.getText().toString().replace("", "");
        phone = etPhone.getText().toString().replace("", "");
        remark = etRemark.getText().toString().replace("", "");
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
        Log.e(TAG, "onDestroy: ");
    }
}
