package com.meiaomei.bankusher.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 资产表暂时未用
 * Created by huyawen on 2017/11/27.
 * email:1754397982@qq.com
 */

@Table(name="PropertyModel", execAfterTableCreated = "CREATE UNIQUE INDEX pt_index ON VipCustomerModel(FaceId)")
public class PropertyModel {
    int id;

    @Column(column = "FaceId")
    String faceId;

    //资产名称
    @Column(column = "PropertyName")
    String propertyName;

    //资产数量
    @Column(column = "PropertyNumber")
    String propertyNumber;

    //资产单位
    @Column(column = "PropertyUnit")
    String propertyUnit;

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyNumber() {
        return propertyNumber;
    }

    public void setPropertyNumber(String propertyNumber) {
        this.propertyNumber = propertyNumber;
    }

    public String getPropertyUnit() {
        return propertyUnit;
    }

    public void setPropertyUnit(String propertyUnit) {
        this.propertyUnit = propertyUnit;
    }
}
