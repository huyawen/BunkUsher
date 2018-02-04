package com.meiaomei.bankusher.view.spinner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.meiaomei.bankusher.R;
import java.util.List;

/**
 * 封装好的下拉列表，包括头部
 */
public class MySpinerView extends LinearLayout implements View.OnClickListener, MySpinerAdapter.IOnItemSelectListener {
    private String mExampleString = "";
    private int mExampleColor = Color.RED;
    private float mExampleDimension = 0;

    private Context context;
    private RelativeLayout spiner;
    private TextView tvSpiner;
    private ImageView ivSpiner;
    private List<String> nameList;

    private MySpinerPopWindow mSpinerPopWindow;
    TextData textData;

    public MySpinerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.sample_my_spiner_view, this);
        init(attrs, 0);
        spiner = (RelativeLayout) this.findViewById(R.id.spiner);
        spiner.setOnClickListener(this);
        tvSpiner = (TextView) this.findViewById(R.id.tv_spiner);
        ivSpiner = (ImageView) this.findViewById(R.id.iv_spiner);
    }

    public void setTextData(TextData textData) {
        this.textData = textData;
    }

    /**
     * 初始化布局的样式
     * @param attrs
     * @param defStyle
     */
    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.MySpinerView, defStyle, 0);
        // 得到布局里面设置的一些属性，有需要的话可以用进去。不需要的删掉也可以
        mExampleString = a.getString(R.styleable.MySpinerView_exampleString);
        mExampleColor = a.getColor(R.styleable.MySpinerView_exampleColor,mExampleColor);
        mExampleDimension = a.getDimension(R.styleable.MySpinerView_exampleDimension,mExampleDimension);
        a.recycle();

    }

    /**
     * 设置弹出下拉表单的数据，在Activity那边得到控件后记得调用否则抛异常
     * @param nameList
     */
    public void setData(List<String> nameList) {
        this.nameList = nameList;
        mSpinerPopWindow = new MySpinerPopWindow(context);
        mSpinerPopWindow.refreshData(nameList, 0);
        mSpinerPopWindow.setItemListener(this); // 设置下拉列表item点击的监听，响应onItemClick回调函数
    }

    /**
     * 头部点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.spiner:
                showSpinWindow();
                break;
        }
    }

    /**
     * 弹出下拉列表
     */
    public void showSpinWindow() {
        /**这里还有一步要做，就是让右边箭头向上。自己替换掉ivSpiner图片**/
        mSpinerPopWindow.setWidth(spiner.getWidth());
        ivSpiner.setImageResource(R.mipmap.j_up);
//        mSpinerPopWindow.setWidth(((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth());
        mSpinerPopWindow.showAsDropDown(spiner);//将目标view传进去
//        mSpinerPopWindow.showAtLocation();//改变popuwindow的位置
    }

    /**
     * 下拉列表项点击事件，把选中的值显示在界面上
     * @param pos
     */
    @Override
    public void onItemClick(int pos) {
        ivSpiner.setImageResource(R.mipmap.j_down);
        /**这里还有一步要做，就是让右边箭头还原向下。自己替换掉ivSpiner图片**/
        if (pos >= 0 && pos <= nameList.size()) { // pos为-1就是弹窗消失，不管他
            String value = nameList.get(pos);
            tvSpiner.setText(value);
            if (textData!=null) {
                textData.getText(value);//接口把值传出去
            }
        }
    }

    public interface TextData{
        void getText(String value);
    }

    /**
     * 初始化顶部显示数据
     * @param txt
     */
    public void initText(String txt){
        tvSpiner.setText(txt);
    }
}
