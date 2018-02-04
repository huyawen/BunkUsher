package com.meiaomei.bankusher.activity;

import android.app.Dialog;
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
import android.support.v4.content.FileProvider;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.dialog.MyProgressDialog;
import com.meiaomei.bankusher.entity.MyResponse;
import com.meiaomei.bankusher.entity.VipCustomerModel;
import com.meiaomei.bankusher.entity.event.StringModel;
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

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;
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
    @BindView(R.id.tv_mind)
    TextView tvMind;
    @BindView(R.id.layout_content)
    LinearLayout layoutContent;

    private Bitmap picbitmap;
    String imgFilePath = "";
    final static int REQUEST_CAMERA_1 = 21;
    final static String TAG = "VipRegistActivity";
    DbUtils dbUtils;
    boolean isEditer = false;
    String useId = "";
    OkHttpManager okHttpManager = new OkHttpManager();
    Dialog progressDialog;

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
                String msgss = (String) msg.obj;
                ToastUtils.showToast(msgss, VipRegistActivity.this, Toast.LENGTH_SHORT);
            } else if (msg.what == 5) {
                ToastUtils.showToast("服务器无响应,请再次点击保存按钮!", VipRegistActivity.this, Toast.LENGTH_SHORT);
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
        progressDialog = new MyProgressDialog().createLoadingDialog(VipRegistActivity.this, "正在保存，请稍后...");
        editVipMessage();
    }

    private void editVipMessage() {
        Intent intent = getIntent();
        HashMap<String, String> linkedHashMap = (HashMap<String, String>) intent.getSerializableExtra("linkedHashMap");
        String path = intent.getStringExtra("imgPath");
        if (linkedHashMap != null && !TextUtils.isEmpty(path)) {//修改人员信息
            isEditer = true;
            tvMind.setText("编辑信息不允许修改头像！");
            tvTitle.setText("VIP 人员修改");
            ivImg.setFocusable(false);
            Picasso.with(VipRegistActivity.this)
                    .load(path)
                    .resize(200, 200)
                    .transform(new CircleTransform()).into(ivImg);
            etName.setText(linkedHashMap.get("姓名"));
            if ("男".equals(linkedHashMap.get("性别"))) {
                radioBtnMale.setChecked(true);
                radioBtnFemale.setVisibility(View.GONE);
            } else if ("女".equals(linkedHashMap.get("性别"))) {
                radioBtnFemale.setChecked(true);
                radioBtnMale.setVisibility(View.GONE);
            }
            useId = linkedHashMap.get("id");
            etPhone.setText(linkedHashMap.get("电话").equals("未录入") ? "" : linkedHashMap.get("电话"));
            etCarNum.setText(linkedHashMap.get("客户编号").equals("未录入") ? "" : linkedHashMap.get("客户编号"));
            etCardid.setText(linkedHashMap.get("身份证号").equals("未录入") ? "" : linkedHashMap.get("身份证号"));
            etRemark.setText(linkedHashMap.get("备注").equals("未录入") ? "" : linkedHashMap.get("备注"));
            switch (linkedHashMap.get("客户等级")) {
                case "一级":
                    level="一级";
                    radioBtnLevel4.setChecked(true);
                    break;
                case "二级":
                    level="二级";
                    radioBtnLevel3.setChecked(true);
                    break;
                case "三级":
                    level="三级";
                    radioBtnLevel2.setChecked(true);
                    break;
                case "四级":
                    level="四级";
                    radioBtnLevel1.setChecked(true);
                    break;
                case "普通":
                    level="普通";
                    radioBtnLevel5.setChecked(true);
                    break;
                default:
                    break;
            }
        }
    }


    @OnClick(R.id.btn_signin)
    public void btn_regist() { //base64  导致app滞后
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

        progressDialog.show();//检验通过后show dialog
        progressDialog.setCancelable(false);
        if (isEditer) {//编辑进来的
            String editUrl = OkHttpManager.getUrl(baseUrl, 3);
            JSONObject js = new JSONObject();
            try {
                if (UpLoadImage.SUCCESS.equals("33")) {//走不到
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
                int le = getIntLevel(level);//0 是普通 在服务端为没有
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
                    progressDialog.dismiss();
                    Gson gson = new Gson();
                    if (!TextUtils.isEmpty(data)) {
                        MyResponse myResponse = gson.fromJson(data, MyResponse.class);
                        if (myResponse != null) {
                            if ("200".equals(myResponse.getRespCode())) {//编辑上传服务器成功后  本地修改数据库
                                VipCustomerModel vip = null;
                                try {
                                    vip = dbUtils.findFirst(Selector.from(VipCustomerModel.class).where("FaceId", "=", useId));
                                    if (vip != null) {
                                        vip.setName(name);
                                        vip.setWorkNumber(workNum);
                                        vip.setIdNumber(idCard);
                                        vip.setPhoneNumber(phone);
                                        vip.setVipOrder(level);
                                        dbUtils.update(vip);
                                        EventBus.getDefault().post(new StringModel("update", "VipRegistActivity"));//编辑完得更新本地列表
                                    }
                                } catch (DbException e) {
                                    e.printStackTrace();
                                }

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
                    progressDialog.dismiss();
                    handler.obtainMessage(5).sendToTarget();
                }
            });
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
                int ge = getIntGender(gender); // 0 女 1男
                js.put("gender", ge);//不传性别
            } catch (JSONException e) {
                e.printStackTrace();
            }

            okHttpManager.postJson(url, js.toString(), new OkHttpManager.HttpCallBack() {
                @Override
                public void onSusscess(String data, String cookie) {
                    progressDialog.dismiss();
                    Gson gson = new Gson();
                    if (!TextUtils.isEmpty(data)) {MyResponse myResponse = gson.fromJson(data, MyResponse.class);
                        if (myResponse != null) {
                            if ("200".equals(myResponse.getRespCode())) {
                                handler.obtainMessage(3).sendToTarget();
                            } else if ("500".equals(myResponse.getRespCode())) {
                                handler.obtainMessage(4, myResponse.getRespDesc()).sendToTarget();
                            }
                        }
                    }
                }

                @Override
                public void onError(String meg) {
                    super.onError(meg);
                    progressDialog.dismiss();
                    handler.obtainMessage(5).sendToTarget();
                }
            });
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

                case "普通"://没有的话不传了
                    intLevel = 0;
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
        imgFilePath = photoFile + "/" + "img"+System.currentTimeMillis() + ".jpg";// 指定路径
        //系统相机调用
        Intent intent = new Intent();// 启动系统相机
        /*获取当前系统的android版本号*/
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        Log.e("currentapiVersion","currentapiVersion====>"+currentapiVersion);
        if (currentapiVersion<24){
            Uri photoUri = Uri.fromFile(new File(imgFilePath)); // 传递路径
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);// 更改系统默认存储路径
            startActivityForResult(intent, REQUEST_CAMERA_1);
        }else {
            File file = new File(imgFilePath);
            Uri imageUri = FileProvider.getUriForFile(VipRegistActivity.this, "com.meiaomei.bankusher.fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent,REQUEST_CAMERA_1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {//返回成功
            if (requestCode == REQUEST_CAMERA_1) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFilePath);//这个路径 华为有问题
                picbitmap = bitmap;
                if (bitmap != null) {
                    ivImg.setBackground(null);
                    Picasso.with(VipRegistActivity.this)
                            .load("file://" + imgFilePath)
                            .centerCrop()
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
            ToastUtils.showToast("请填入客户姓名！", VipRegistActivity.this, Toast.LENGTH_SHORT);
            return true;
        }

        if (TextUtils.isEmpty(gender) && !isEditer) {//编辑的时候不用检查性别
            ToastUtils.showToast("请选择客户性别！", VipRegistActivity.this, Toast.LENGTH_SHORT);
            return true;
        }

        if (TextUtils.isEmpty(phone.trim())) {
            ToastUtils.showToast("请填入客户手机号！", VipRegistActivity.this, Toast.LENGTH_SHORT);
            return true;
        }

        if (TextUtils.isEmpty(workNum.trim())) {
            ToastUtils.showToast("请填入客户编号！", VipRegistActivity.this, Toast.LENGTH_SHORT);
            return true;
        }

        if (TextUtils.isEmpty(idCard.trim()) && !isEditer) {//编辑的时候不用校验身份证号
            ToastUtils.showToast("请填入客户身份证号！", VipRegistActivity.this, Toast.LENGTH_SHORT);
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
