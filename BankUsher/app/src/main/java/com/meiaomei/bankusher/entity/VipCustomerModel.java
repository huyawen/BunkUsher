package com.meiaomei.bankusher.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.NotNull;
import com.lidroid.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by huyawen on 2017/11/6.
 */
@Table(name = "VipCustomerModel")
public class VipCustomerModel implements Serializable {

    @Id(column = "FaceId")  //对应云丛 useid
            String faceId;

    @Column(column = "Name") //对应云丛 username
            String name;

    @Column(column = "Sex") // 对应云丛 genderName  (0 女 1 男)
            String sex;

    @Column(column = "Remark")
            String remark;

    //身份证号
    @Column(column = "IdNumber") //对应 云丛 idCard
            String idNumber;

    //vip级别
    @Column(column = "VipOrder") // 对应 云丛 userLevelName
            String vipOrder;

    //爱好
    @Column(column = "Favorite") //对应 云丛 favorite
            String favorite;

    //生日
    @Column(column = "Birthday") //对应云丛的 birthday
            long birthday;

    //人脸图片路径
    @Column(column = "ImgUrl") //对应云丛 picName  imgBaseUrl+picName
            String imgUrl;


    @Column(column = "PhoneNumber") // 对应云丛 telephone
            String phoneNumber;

    public String getWorkNumber() {
        return workNumber;
    }

    public void setWorkNumber(String workNumber) {
        this.workNumber = workNumber;
    }

    @Column(column = "WorkNumber")  //工号
            String workNumber;

    @Column(column = "Email")  //电子邮件
    String email;

    @NotNull
    @Column(column = "DelFlag")//自己的查询条件 flag标记 0为未删除  1为已删除
            String delFlag;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }



    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public VipCustomerModel() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVipOrder() {
        return vipOrder;
    }

    public void setVipOrder(String vipOrder) {
        this.vipOrder = vipOrder;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }


}
