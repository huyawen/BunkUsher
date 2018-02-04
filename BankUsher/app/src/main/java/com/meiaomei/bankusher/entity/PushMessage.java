package com.meiaomei.bankusher.entity;

/**
 * Created by huyawen on 2017/12/6.
 * email:1754397982@qq.com
 *
 * 推送下来的消息  解析
 */

public class PushMessage {
    Body body;
    Header header;

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public static class Body {
        long bithday; //生日
        String c4;
        String favourite;
        String id;    //id是识别记录主建
        String path;  //path是现场照
        String picName; //注册照
        double score;   //识别分数
        long signTime;  //识别时间
        String userId;  //userid是vip主建
        int userLevel;  //vip等级的数字
        String userLevelName; //vip等级的汉字
        String userName; //用户的名字
        String cameraName; //摄像头名称
        String deviceId;//设备id


        public String getCameraName() {
            return cameraName;
        }

        public void setCameraName(String cameraName) {
            this.cameraName = cameraName;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public long getBithday() {
            return bithday;

        }

        public void setBithday(long bithday) {
            this.bithday = bithday;
        }

        public String getC4() {
            return c4;
        }

        public void setC4(String c4) {
            this.c4 = c4;
        }

        public String getFavourite() {
            return favourite;
        }

        public void setFavourite(String favourite) {
            this.favourite = favourite;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getPicName() {
            return picName;
        }

        public void setPicName(String picName) {
            this.picName = picName;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public long getSignTime() {
            return signTime;
        }

        public void setSignTime(long signTime) {
            this.signTime = signTime;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public int getUserLevel() {
            return userLevel;
        }

        public void setUserLevel(int userLevel) {
            this.userLevel = userLevel;
        }

        public String getUserLevelName() {
            return userLevelName;
        }

        public void setUserLevelName(String userLevelName) {
            this.userLevelName = userLevelName;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
    }

    public static class Header{
        String messageType;
        String imgBaseUrl;

        public String getMessageType() {
            return messageType;
        }

        public void setMessageType(String messageType) {
            this.messageType = messageType;
        }

        public String getImgBaseUrl() {
            return imgBaseUrl;
        }

        public void setImgBaseUrl(String imgBaseUrl) {
            this.imgBaseUrl = imgBaseUrl;
        }
    }
}
