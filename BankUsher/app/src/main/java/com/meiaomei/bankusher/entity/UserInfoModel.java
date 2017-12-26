package com.meiaomei.bankusher.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;

/**
 * 用户表
 */

@Table(name = "UserInfoModel")
public class UserInfoModel {

    @Id(column = "UserId")
    public String userId;//	用户ID
    @Column(column = "UserAcnt")
    public String userAcnt;//用户帐号
    @Column(column = "PrePwd")
    public String prePwd;//	前密码
    @Column(column = "NowPwd")
    public String nowPwd;//当前密码
    @Column(column = "userName")
    public String userName;//	用户姓名
    @Column(column = "UserType")
    public String userType;//用户类型
    @Column(column = "UserSt")
    public String userSt;//	用户状态
    @Column(column = "UserOdate")
    public String userOdate;//	用户登记时间
    @Column(column = "LastUpTime")
    public long lastUpTime;//	最终更新时间


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserAcnt() {
        return userAcnt;
    }

    public void setUserAcnt(String userAcnt) {
        this.userAcnt = userAcnt;
    }

    public String getPrePwd() {
        return prePwd;
    }

    public void setPrePwd(String prePwd) {
        this.prePwd = prePwd;
    }

    public String getNowPwd() {
        return nowPwd;
    }

    public void setNowPwd(String nowPwd) {
        this.nowPwd = nowPwd;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserSt() {
        return userSt;
    }

    public void setUserSt(String userSt) {
        this.userSt = userSt;
    }

    public long getLastUpTime() {
        return lastUpTime;
    }

    public void setLastUpTime(long lastUpTime) {
        this.lastUpTime = lastUpTime;
    }

    public String getUserOdate() {
        return userOdate;
    }

    public void setUserOdate(String userOdate) {
        this.userOdate = userOdate;
    }
}
