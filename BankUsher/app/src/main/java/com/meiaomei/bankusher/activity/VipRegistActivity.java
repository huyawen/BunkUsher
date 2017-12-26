package com.meiaomei.bankusher.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.entity.VipCustomerModel;
import com.meiaomei.bankusher.manager.BankUsherDB;
import com.meiaomei.bankusher.utils.FileUtils;
import com.meiaomei.bankusher.utils.ImageUtils;
import com.meiaomei.bankusher.utils.ToastUtils;
import com.meiaomei.bankusher.utils.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.File;
import java.util.Date;

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
    String carNun = "";
    String name = "";
    String phone = "";
    String cardid = "";
    String remark = "";
    String gender = "";
    String level = "";
    @BindView(R.id.top_view)
    RelativeLayout topView;
    @BindView(R.id.sv_base)
    ScrollView svBase;

    private Bitmap picbitmap;
    String mFilePath = "";
    final static int REQUEST_CAMERA_1 = 21;
    final static String TAG = "VipRegistActivity";
    DbUtils dbUtils;

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                ToastUtils.showToast("保存成功!", VipRegistActivity.this, Toast.LENGTH_SHORT);
            } else if (msg.what == 2) {
                ToastUtils.showToast("保存失败!", VipRegistActivity.this, Toast.LENGTH_SHORT);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_vip_regist);
        initOther();
        ButterKnife.bind(this);
    }

    private void initOther() {
        dbUtils = BankUsherDB.getDbUtils();

    }


    @OnClick(R.id.btn_signin)
    public void btn_regist() {
        getInput();
        final String base64 = ImageUtils.imgToBase64(null, picbitmap);
        if (isEmpty()) {
            return;
        }

        if (!Utils.isChinaPhoneLegal(phone)) {
            ToastUtils.showToast("请输入正确的手机号！", VipRegistActivity.this, Toast.LENGTH_SHORT);
        }

        if (!TextUtils.isEmpty(cardid)) {
            if (!Utils.isIDCard(cardid)) {
                ToastUtils.showToast("请输入正确的身份证号！", VipRegistActivity.this, Toast.LENGTH_SHORT);
            }
        }

        if (base64 == null) {//传给后台的base64
            ToastUtils.showToast("请选择相机拍照！", VipRegistActivity.this, Toast.LENGTH_SHORT);
            return;
        }

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
        }

    }


    @OnClick(R.id.iv_img)
    public void iv_Photo(View view) {
        mFilePath = Environment.getExternalStorageDirectory().getPath();// 获取SD卡路径
        File photoFile = FileUtils.createDir("photo");
        mFilePath = photoFile + "/" + new Date().getTime() + ".jpg";// 指定路径
        //系统相机调用
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 启动系统相机
        Uri photoUri = Uri.fromFile(new File(mFilePath)); // 传递路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);// 更改系统默认存储路径
        startActivityForResult(intent, REQUEST_CAMERA_1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {//返回成功
            if (requestCode == REQUEST_CAMERA_1) {
                Bitmap bitmap = BitmapFactory.decodeFile(mFilePath);
                picbitmap = bitmap;
                if (bitmap != null) {
                    ivImg.setBackground(null);
                    Picasso.with(VipRegistActivity.this)
                            .load("file://" + mFilePath)
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
    public void bt_clean() {
        clean();
    }

    //清空
    private void clean() {
        Log.d(TAG, "clean: level=" + level + "--gender=" + gender);
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
                case "钻石":
                    radioBtnLevel1.setChecked(false);
                    break;
                case "铂金":
                    radioBtnLevel2.setChecked(false);
                    break;
                case "黄金":
                    radioBtnLevel3.setChecked(false);
                    break;
                case "白银":
                    radioBtnLevel4.setChecked(false);
                    break;
                case "青铜":
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
    public void iv_return(View view) {
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
                level = "钻石";
                break;
            case R.id.radioBtn_level2:
                level = "铂金";
                break;
            case R.id.radioBtn_level3:
                level = "黄金";
                break;
            case R.id.radioBtn_level4:
                level = "白银";
                break;
            case R.id.radioBtn_level5:
                level = "青铜";
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
        return false;
    }

    private void getInput() {
        carNun = etCarNum.getText().toString().replace("", "");
        name = etName.getText().toString().replace("", "");
        cardid = etCardid.getText().toString().replace("", "");
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
