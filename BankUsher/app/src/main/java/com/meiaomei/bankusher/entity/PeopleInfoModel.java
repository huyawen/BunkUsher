package com.meiaomei.bankusher.entity;

/**
 * Created by huyawen on 2017/12/4.
 * email:1754397982@qq.com
 */

public class PeopleInfoModel {

    private String id;          //人员id
    private String name;        //人员姓名
    private String gener;       //性别
    private String userType;    //用户类型
    private String idCard;      //身份证号
    private String birthday;    //生日 格式yyyy-MM-dd HH:mm:ss
    private String eamil;       //email
    private String telephone;   //手机号
    private String base64Img;   //base64格式人脸照片
    private String deviceCodes; //注册设备号（数组类型）

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGener() {
        return gener;
    }

    public void setGener(String gener) {
        this.gener = gener;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getEamil() {
        return eamil;
    }

    public void setEamil(String eamil) {
        this.eamil = eamil;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getBase64Img() {
        return base64Img;
    }

    public void setBase64Img(String base64Img) {
        this.base64Img = base64Img;
    }

    public String getDeviceCodes() {
        return deviceCodes;
    }

    public void setDeviceCodes(String deviceCodes) {
        this.deviceCodes = deviceCodes;
    }
}
