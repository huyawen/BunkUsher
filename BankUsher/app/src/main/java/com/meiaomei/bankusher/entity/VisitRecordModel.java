package com.meiaomei.bankusher.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * Created by huyawen on 2017/11/27.
 * email:1754397982@qq.com
 * 来访记录表
 */

@Table(name = "VisitRecordModel", execAfterTableCreated = "CREATE UNIQUE INDEX vr_index ON VipCustomerModel(FaceId)")
public class VisitRecordModel {

    @Id(column = "VisitId")  //对应云丛 识别记录主键 id
    String visitId;

    @Column(column = "FaceId") //关联VIP表的主键
    String faceId;

    @Column(column = "VisitTime")//对用云丛 识别记录时间 signTime
    long visitTime;

    @Column(column = "CameraName")//对应云丛 cameraName 摄像头名称
    String cameraName;

    @Column(column = "VisitAddress")//暂时得不到
    String visitAddress;

    @Column(column = "HardWareId") //暂时用不到
    String hardWareId;

    @Column(column = "HandleFlag") //处理的标记  0未处理  1已处理
    String handleFlag;


    public String getCameraName() {
        return cameraName;
    }

    public void setCameraName(String cameraName) {
        this.cameraName = cameraName;
    }

    public String getVisitId() {
        return visitId;
    }

    public void setVisitId(String visitId) {
        this.visitId = visitId;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public long getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(long visitTime) {
        this.visitTime = visitTime;
    }

    public String getVisitAddress() {
        return visitAddress;
    }

    public void setVisitAddress(String visitAddress) {
        this.visitAddress = visitAddress;
    }

    public String getHardWareId() {
        return hardWareId;
    }

    public void setHardWareId(String hardWareId) {
        this.hardWareId = hardWareId;
    }

    public String getHandleFlag() {
        return handleFlag;
    }

    public void setHandleFlag(String handleFlag) {
        this.handleFlag = handleFlag;
    }
}
