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
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.entity.ThirteenParamModel;
import com.meiaomei.bankusher.entity.VisitRecordModel;
import com.meiaomei.bankusher.utils.DateUtils;
import com.meiaomei.bankusher.utils.ImageUtils;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

/**
 * 待接待的vip列表
 * Created by huyawen on 2017/12/8.
 * email:1754397982@qq.com
 */

public class RecycleVipSerunDisposAdapter extends RecyclerView.Adapter<RecycleVipSerunDisposAdapter.MyHolder> {

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
    BtnClick btnClick;

    public void setBtnClick(BtnClick btnClick) {
        this.btnClick = btnClick;
    }

    public RecycleVipSerunDisposAdapter(Context context, DbUtils dbUtils,
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

    public interface BtnClick {
        void Click(ThirteenParamModel thirteenParamModel);
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(inflateLayout, parent, false);
        View view_foot = LayoutInflater.from(context).inflate(R.layout.recycle_footview, parent, false);
        if (FOOT_TYPE == viewType) {
            return new MyHolder(view_foot, FOOT_TYPE);
        } else {
            return new MyHolder(view, NORMAL_TYPE);
        }
    }

    @Override
    public void onBindViewHolder(MyHolder holder, final int position) {
        if (isFootView && getItemViewType(position) == FOOT_TYPE) {
            holder.tvFootView.setText(footViewText);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    max_count += 5;
                    notifyDataSetChanged();
                }
            }, 2000);
        } else {
            String base64=thirteenParamModelList.get(position).getTenthPara();
            if (base64.length()>48) {
                Bitmap bitmap = ImageUtils.base64ToBitmap(base64);
                holder.iv_vips_face.setImageBitmap(bitmap);
            }else {
                Picasso.with(context).load("file://" + base64).error(R.mipmap.liu).into(holder.iv_vips_face);
            }
            holder.tv_vip_name.setText(TextUtils.isEmpty(thirteenParamModelList.get(position).getFourthPara())?"未录入":thirteenParamModelList.get(position).getFourthPara());
            holder.tv_vip_grade.setText(TextUtils.isEmpty(thirteenParamModelList.get(position).getEighthPara())?"未录入":thirteenParamModelList.get(position).getEighthPara());
            holder.tv_area.setText(TextUtils.isEmpty(thirteenParamModelList.get(position).getSecondPara())?"未录入":thirteenParamModelList.get(position).getSecondPara());
            holder.tv_time.setText(DateUtils.longFromatDate(thirteenParamModelList.get(position).getFirstPara(), "yyyy-MM-dd HH:mm"));//时间必须传过来
            holder.tv_idcard.setText(TextUtils.isEmpty(thirteenParamModelList.get(position).getSeventhPara())?"未录入":thirteenParamModelList.get(position).getSeventhPara());
            holder.tv_phone.setText(TextUtils.isEmpty(thirteenParamModelList.get(position).getFifthPara())?"未录入":thirteenParamModelList.get(position).getFifthPara());
            holder.tv_undis_page.setText(position+1+"");

            holder.bt_handle.setOnClickListener(new View.OnClickListener() {//点击接待的按钮
                @Override
                public void onClick(View v) {
                    String visitId = thirteenParamModelList.get(position).getEleventhPara();
                    try {
                        VisitRecordModel visitRecordModel = dbUtils.findFirst(Selector.from(VisitRecordModel.class).where("VisitId", "=", visitId));
                        visitRecordModel.setHandleFlag("0");
                        dbUtils.update(visitRecordModel);

                        if (null != btnClick) {
                            btnClick.Click(thirteenParamModelList.get(position));
                        }
                        thirteenParamModelList.remove(position);
                        notifyItemRemoved(position);//添加的删除动画
                        notifyItemRangeChanged(position,thirteenParamModelList.size());//对于被删掉的位置及其后range大小范围内的view进行重新onBindViewHolder
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            });
            holder.itemView.setTag(position);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == max_count - 1) {
            return FOOT_TYPE;
        }

        return NORMAL_TYPE;
    }

    @Override
    public int getItemCount() {
        if (thirteenParamModelList.size() < max_count) {
            return thirteenParamModelList.size();
        } else {
            return max_count;
        }
    }

    class MyHolder extends RecyclerView.ViewHolder {
        @ViewInject(R.id.rl_waithandle)
        RelativeLayout rl_waithandle;
        @ViewInject(R.id.iv_vips_face)
        ImageView iv_vips_face;
        @ViewInject(R.id.tv_vip_name)
        TextView tv_vip_name;
        @ViewInject(R.id.tv_vip_grade)
        TextView tv_vip_grade;
        @ViewInject(R.id.tv_area)
        TextView tv_area;
        @ViewInject(R.id.tv_time)
        TextView tv_time;
        @ViewInject(R.id.tv_idcard)
        TextView tv_idcard;
        @ViewInject(R.id.tv_phone)
        TextView tv_phone;
        @ViewInject(R.id.bt_handle)
        Button bt_handle;
        @ViewInject(R.id.tv_server_tishi)
        TextView tv_server_tishi;
        @ViewInject(R.id.tv_undis_page)
        TextView tv_undis_page;

        //footView的TextView属于独自的一个layout
        TextView tvFootView;

        public MyHolder(View itemView, int type) {
            super(itemView);
            if (type == FOOT_TYPE) {
                tvFootView = itemView.findViewById(R.id.bottom_title);
            } else if (type == NORMAL_TYPE) {
                com.lidroid.xutils.ViewUtils.inject(this, itemView);
            }
        }
    }

    //创建一个方法来设置footView中的文字
    public void setFootViewText(String footViewText) {
        isFootView = true;
        this.footViewText = footViewText;
    }
}
