package com.meiaomei.bankusher.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.entity.ThirteenParamModel;
import com.meiaomei.bankusher.utils.DateUtils;
import com.meiaomei.bankusher.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * 已接待的vip列表
 * Created by huyawen on 2017/12/8.
 * email:1754397982@qq.com
 */

public class RecycleVipSerDisposAdapter extends RecyclerView.Adapter<RecycleVipSerDisposAdapter.MyHolder> {

    Context context;
    DbUtils dbUtils;
    int inflateLayout;
    List<ThirteenParamModel> thirteenParamModelList;
    private int max_count = 20;//最大显示数
    private Boolean isFootView = false;//是否添加了FootView
    private String footViewText = "";//FootView的内容
    //三个final int类型表示ViewType的两种类型
    private final int NORMAL_TYPE = 0;//正常
    private final int FOOT_TYPE = 1;//足视图

    public RecycleVipSerDisposAdapter(Context context, DbUtils dbUtils,
                                      int inflateLayout, List<ThirteenParamModel> thirteenParamModelList) {
        this.context = context;
        this.dbUtils = dbUtils;
        this.inflateLayout = inflateLayout;
        this.thirteenParamModelList = thirteenParamModelList;
    }

    public void refresh(List<ThirteenParamModel> thirteenParamModelList) {
        this.thirteenParamModelList = thirteenParamModelList;
        notifyDataSetChanged();
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(inflateLayout, parent, false);
        View foot_view = LayoutInflater.from(context).inflate(R.layout.recycle_footview, parent, false);
        if (viewType == FOOT_TYPE) {
            return new MyHolder(foot_view, FOOT_TYPE);
        } else {
            return new MyHolder(view, NORMAL_TYPE);
        }
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        if (isFootView && getItemViewType(position)==FOOT_TYPE){
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    max_count+=5;
                    notifyDataSetChanged();
                }
            },2000);
        }else {
            String base64=thirteenParamModelList.get(position).getTenthPara();
            if (base64.length()>150) {
                Bitmap bp = ImageUtils.base64ToBitmap(base64);
                holder.iv_al_vips_face.setImageBitmap(bp);
            }else {
                Picasso.with(context).load(base64).error(R.mipmap.backimg).error(R.mipmap.backimg).into(holder.iv_al_vips_face);
            }
            holder.tv_al_vip_name.setText(TextUtils.isEmpty(thirteenParamModelList.get(position).getFourthPara())?"未录入":thirteenParamModelList.get(position).getFourthPara());
            holder.tv_al_vip_grade.setText(TextUtils.isEmpty(thirteenParamModelList.get(position).getEighthPara())?"未录入":thirteenParamModelList.get(position).getEighthPara());
            holder.tv_al_area.setText(TextUtils.isEmpty(thirteenParamModelList.get(position).getSecondPara())?"未录入":thirteenParamModelList.get(position).getSecondPara());
            holder.tv_al_time.setText(DateUtils.longFromatDate(thirteenParamModelList.get(position).getFirstPara(),"yyyy-MM-dd HH:mm"));
            holder.tv_al_idcard.setText(TextUtils.isEmpty(thirteenParamModelList.get(position).getSeventhPara())?"未录入":thirteenParamModelList.get(position).getSeventhPara());
            holder.tv_al_phone.setText(TextUtils.isEmpty(thirteenParamModelList.get(position).getFifthPara())?"未录入":thirteenParamModelList.get(position).getSeventhPara());
            holder.tv_dis_page.setText(position+1+"");
            holder.itemView.setTag(position);
        }

    }


    @Override
    public int getItemCount() {
        if (thirteenParamModelList.size() < max_count) {
            return thirteenParamModelList.size();
        } else {
            return max_count;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == max_count - 1) {
            return FOOT_TYPE;
        }

        return NORMAL_TYPE;
    }

    class MyHolder extends RecyclerView.ViewHolder {

        @ViewInject(R.id.rl_al_handle)
        RelativeLayout rl_al_handle;
        @ViewInject(R.id.iv_al_vips_face)
        ImageView iv_al_vips_face;
        @ViewInject(R.id.tv_al_vip_name)
        TextView tv_al_vip_name;
        @ViewInject(R.id.tv_al_vip_grade)
        TextView tv_al_vip_grade;
        @ViewInject(R.id.tv_al_area)
        TextView tv_al_area;
        @ViewInject(R.id.tv_al_time)
        TextView tv_al_time;
        @ViewInject(R.id.tv_al_idcard)
        TextView tv_al_idcard;
        @ViewInject(R.id.tv_al_phone)
        TextView tv_al_phone;
        @ViewInject(R.id.bt_al_handle)
        Button bt_al_handle;
        @ViewInject(R.id.tv_al_tishi)
        TextView tv_al_tishi;
        @ViewInject(R.id.tv_dis_page)
        TextView tv_dis_page;

        TextView footview;

        public MyHolder(View itemView, int type) {
            super(itemView);

            if (FOOT_TYPE == type) {
                footview = itemView.findViewById(R.id.bottom_title);
            } else if (NORMAL_TYPE == type) {
                ViewUtils.inject(this, itemView);

            }

        }
    }

    public void setFootText(String text) {
        isFootView = true;
        this.footViewText = text;

    }
}
