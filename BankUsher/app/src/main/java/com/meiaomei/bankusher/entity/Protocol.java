package com.meiaomei.bankusher.entity;

/**
 * Created by huyawen on 2018/1/3.
 * email:1754397982@qq.com
 *
 * 保持cookie的实体类
 */

public class Protocol {

    private static String imgBaseUrl = "";
    private static String cookedId = "";

    public static String getCookedId() {
        return cookedId;
    }

    public static void setCookedId(String cookedId) {
        Protocol.cookedId = cookedId;
    }

    public static String getImgBaseUrl() {
        return imgBaseUrl;
    }

    public static void setImgBaseUrl(String imgBaseUrl) {
        Protocol.imgBaseUrl = imgBaseUrl;
    }


}
