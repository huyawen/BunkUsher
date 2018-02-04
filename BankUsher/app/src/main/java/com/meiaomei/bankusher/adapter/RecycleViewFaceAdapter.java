package com.meiaomei.bankusher.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.meiaomei.bankusher.R;
import com.meiaomei.bankusher.dialog.AlertDialogCommon;
import com.meiaomei.bankusher.entity.ThirteenParamModel;
import com.meiaomei.bankusher.entity.VisitRecordModel;
import com.meiaomei.bankusher.entity.event.StringModel;
import com.meiaomei.bankusher.utils.DateUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by huyawen on 2017/12/4.
 * email:1754397982@qq.com
 */

public class RecycleViewFaceAdapter extends RecyclerView.Adapter<RecycleViewFaceAdapter.MyHolder> implements View.OnClickListener {

    Context context;
    List<ThirteenParamModel> thirteenParamModelList;
    RecycleViewFaceAdapter.RecycleviewItemOnclickListener recycleviewItemOnclickListener;
    RecycleViewFaceAdapter.CheckItemClickListener checkItemClickListener;
    DbUtils dbUtils;
    int inflateLayout;
    private int max_count = 10;//最大显示数
    private Boolean isFootView = false;//是否添加了FootView
    private String footViewText = "";//FootView的内容
    boolean isCheckAll = false;//点击全选的checkbox
    boolean isCheckClick = false;//点击全选触发的事件
    private List<Integer> checkPositionlist = new ArrayList<>();


    private final int FOOT_TYPE = 1;//足视图
    private final int FACE_TAKE_TYPE = 2;//普通客户的识别
    ArrayList<HashMap<String, String>> excelList = new ArrayList<>();//List每remove掉一个元素以后，后面的元素都会向前移动
    LinkedHashMap<Integer, HashMap<String, String>> excelMap = new LinkedHashMap<>();


    public RecycleViewFaceAdapter(Context context, List<ThirteenParamModel> thirteenParamModelList
            , DbUtils dbUtils, int inflateLayout) {
        this.dbUtils = dbUtils;
        this.context = context;
        this.thirteenParamModelList = thirteenParamModelList;
        this.inflateLayout = inflateLayout;
    }


    public void setCheckItemClickListener(RecycleViewFaceAdapter.CheckItemClickListener checkItemClickListener) {
        this.checkItemClickListener = checkItemClickListener;
    }

    public void setRecycleviewItemOnclickListener(RecycleViewFaceAdapter.RecycleviewItemOnclickListener recycleviewItemOnclickListener) {
        this.recycleviewItemOnclickListener = recycleviewItemOnclickListener;
    }

    public interface RecycleviewItemOnclickListener {
        void onItemClik(View view, int position);
    }

    @Override//将点击的位置暴露出去
    public void onClick(View v) {
        if (recycleviewItemOnclickListener != null) {
            recycleviewItemOnclickListener.onItemClik(v, (int) v.getTag());
        }
    }

    public interface CheckItemClickListener {
        void onCheckClik(LinkedHashMap<Integer, HashMap<String, String>> excelMap);
    }


    public void refresh(List<ThirteenParamModel> thirteenParamModelList) {
        this.thirteenParamModelList = thirteenParamModelList;
        notifyDataSetChanged();
    }

    public void refreshCheckBox(boolean isCheckAll, boolean isCheckClick) {
        this.isCheckAll = isCheckAll;
        this.isCheckClick = isCheckClick;
        notifyDataSetChanged();
    }


    @Override//创建视图  此函数用来创建每一个item，最后返回的不是view，而是返回的一个ViewHolder。
    public RecycleViewFaceAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View foot_view = LayoutInflater.from(context).inflate(R.layout.recycle_footview, parent, false);
        View normalFaceTake = LayoutInflater.from(context).inflate(inflateLayout, parent, false);
        if (viewType == FACE_TAKE_TYPE) {
            normalFaceTake.setOnClickListener(this);
            return new RecycleViewFaceAdapter.MyHolder(normalFaceTake, FACE_TAKE_TYPE);
        } else {
            return new RecycleViewFaceAdapter.MyHolder(foot_view, FOOT_TYPE);
        }

    }

    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        //如果footview存在，并且当前位置ViewType是FOOT_TYPE
        if (isFootView && (getItemViewType(position) == FOOT_TYPE)) {
            holder.tvFootView.setText(footViewText);
            // 刷新太快 所以使用Hanlder延迟两秒
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    max_count += 10;
                    notifyDataSetChanged();
                }
            }, 1000);
        } else {
            final String faceId = thirteenParamModelList.get(position).getThirdPara();
            final HashMap<String, String> messageMap = new HashMap<>();
            holder.tv_face_catch_time.setText(DateUtils.longFromatDate(thirteenParamModelList.get(position).getFirstPara(), "yy-MM-dd HH:mm"));
            holder.tv_face_catch_address.setText(TextUtils.isEmpty(thirteenParamModelList.get(position).getSecondPara()) ? "未录入" : thirteenParamModelList.get(position).getSecondPara());
            holder.tv_face_catch_sex.setText(TextUtils.isEmpty(thirteenParamModelList.get(position).getSixthPara()) ? "未录入" : thirteenParamModelList.get(position).getSixthPara());


            holder.cb_face_item.setTag(new Integer(position));//设置tag 否则划回来时选中消失
            //checkbox  复用问题
            if (checkPositionlist != null) {
                holder.cb_face_item.setChecked((checkPositionlist.contains(new Integer(position)) ? true : false));
            } else {
                holder.cb_face_item.setChecked(false);
            }

            //单选checkbox的点击监听
            holder.rl_face_cb_item.setOnClickListener(new View.OnClickListener() {//参照是相对布局
                @Override
                public void onClick(View v) {
                    if (holder.cb_face_item.isChecked()) {//是选中的
                        if (checkPositionlist.contains(holder.cb_face_item.getTag())) {
                            Log.e("RecycleViewFaceAdapter", "rl_item_cb:positin==" + position + "==" + holder.cb_face_item.isChecked() + "-remove");
                            holder.cb_face_item.setChecked(false);     //ui界面
                            excelMap.remove(position);  //操作数据
                            checkPositionlist.remove(holder.cb_face_item.getTag());
                        }
                    } else {//没有被选中
                        if (!checkPositionlist.contains(holder.cb_face_item.getTag())) {
                            Log.e("RecycleViewFaceAdapter", "rl_item_cb::positin==" + position + "==" + holder.cb_face_item.isChecked() + "-add");
                            holder.cb_face_item.setChecked(true);//ui界面
                            messageMap.put("visitTime", DateUtils.longFromatDate(thirteenParamModelList.get(position).getFirstPara(), "yyyy-MM-dd HH:mm"));//操作数据
                            messageMap.put("visitAddress", thirteenParamModelList.get(position).getSecondPara());
                            messageMap.put("faceId", faceId);
                            messageMap.put("name", thirteenParamModelList.get(position).getFourthPara());
                            messageMap.put("idNumber", thirteenParamModelList.get(position).getSeventhPara());
                            messageMap.put("age", thirteenParamModelList.get(position).getFifthPara());
                            messageMap.put("sex", thirteenParamModelList.get(position).getSixthPara());
                            messageMap.put("vipOrder", thirteenParamModelList.get(position).getEighthPara());
                            excelMap.put(position, messageMap);
                            checkPositionlist.add(new Integer(position));
                        }

                    }
                    if (checkItemClickListener != null) {//最后一个值设置监听,改变进入状态
                        checkItemClickListener.onCheckClik(excelMap);//设置监听
                    }
                }
            });


            //全部选中的按钮的点击事件操作
            if (isCheckClick) {
                if (isCheckAll) {
                    if (!checkPositionlist.contains(holder.cb_face_item.getTag())) {
                        holder.cb_face_item.setChecked(true);//ui界面
                        messageMap.put("visitTime", DateUtils.longFromatDate(thirteenParamModelList.get(position).getFirstPara(), "yyyy-MM-dd HH:mm"));//操作数据
                        messageMap.put("visitAddress", thirteenParamModelList.get(position).getSecondPara());
                        messageMap.put("faceId", faceId);
                        messageMap.put("name", thirteenParamModelList.get(position).getFourthPara());
                        messageMap.put("idNumber", thirteenParamModelList.get(position).getSeventhPara());
                        messageMap.put("age", thirteenParamModelList.get(position).getFifthPara());
                        messageMap.put("sex", thirteenParamModelList.get(position).getSixthPara());
                        messageMap.put("vipOrder", thirteenParamModelList.get(position).getEighthPara());
                        excelMap.put(position, messageMap);
                        checkPositionlist.add(new Integer(position));
                    }
                } else {
                    if (checkPositionlist.contains(holder.cb_face_item.getTag())) {
                        holder.cb_face_item.setChecked(false);
                        if (excelMap.size() > 0) {
                            excelMap.remove(position);//操作数据  remove
                            checkPositionlist.remove(new Integer(position));
                        }

                        if (excelMap.size() == 0) {//这个else已被激活  全选的会影响单个的
                            isCheckClick = false;
                        }
                    }
                }

                if (checkItemClickListener != null) {//最后一个值设置监听,改变进入状态
                    checkItemClickListener.onCheckClik(excelMap);//设置监听
                }
            }


            //删除按钮的点击监听  在布局中设置跟随父布局（禁点子控件的点击事件）
            holder.rl_face_item_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialogCommon.Builder(context).setContents(new String[]{"确认要删除该条来访记录吗？"})
                            .setIsShowCancelBtn(true)
                            .setCancleBtnText("确定")//确定在左边
                            .setContentColor(R.color.alertTextContent)
                            .setCancleClickListener(new AlertDialogCommon.DialogCancleClickListener() {
                                @Override
                                public void cancleButtonClickListener() {
                                    try {
                                        VisitRecordModel visitRecordModel = dbUtils.findFirst(Selector.from(VisitRecordModel.class).where("FaceId", "=", faceId));
                                        if (visitRecordModel != null) {
                                            dbUtils.delete(visitRecordModel);
                                            EventBus.getDefault().post(new StringModel("update", "faceAdapter"));//数据删除完更新界面
                                        } else {

                                        }
                                    } catch (DbException e) {
                                        Log.e("删除失败，原因是：", e.toString());
                                        e.printStackTrace();
                                    }
                                    thirteenParamModelList.remove(thirteenParamModelList.get(position));//移除此条记录，刷新界面
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, thirteenParamModelList.size());
                                }
                            }).setSubmitBtnText("取消").setSubmitClickListener(new AlertDialogCommon.DialogSubmitClickListener() {
                        @Override
                        public void submitButtonClickListener() {

                        }
                    }).build().createAlertDialog();
                }
            });

            holder.itemView.setTag(position);//保存位置 单条item的点击效果就在这里
        }
    }


    //将item布局中的控件与ViewHolder中所定义的属性绑定，更便于在onBindViewHolder()中使用
    class MyHolder extends RecyclerView.ViewHolder {

        //footView的TextView属于独自的一个layout
        TextView tvFootView;

        //faceTake的item
        RelativeLayout rl_face_cb_item;
        CheckBox cb_face_item;
        TextView tv_face_catch_time;
        TextView tv_face_catch_address;
        RelativeLayout rl_face_item_detail;
        TextView tv_face_catch_sex;

        public MyHolder(View itemView, int type) {
            super(itemView);
            if (type == FACE_TAKE_TYPE) {
                cb_face_item = itemView.findViewById(R.id.cb_face_item);
                rl_face_cb_item = itemView.findViewById(R.id.rl_face_cb_item);
                rl_face_item_detail = itemView.findViewById(R.id.rl_face_item_detail);
                tv_face_catch_time = itemView.findViewById(R.id.tv_face_catch_time);
                tv_face_catch_address = itemView.findViewById(R.id.tv_face_catch_address);
                tv_face_catch_sex = itemView.findViewById(R.id.tv_face_catch_sex);
            } else if (type == FOOT_TYPE) {
                tvFootView = itemView.findViewById(R.id.bottom_title);
            }

        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == max_count - 1) {
            return FOOT_TYPE;
        }
        return FACE_TAKE_TYPE;
    }

    @Override
    public int getItemCount() {//设置显示的条目
        if (thirteenParamModelList.size() < max_count) {
            return thirteenParamModelList.size();
        }
        return max_count;
    }

    //创建一个方法来设置footView中的文字
    public void setFootViewText(String footViewText) {
        isFootView = true;
        this.footViewText = footViewText;
    }
}
