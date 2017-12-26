package com.meiaomei.bankusher.entity;

/**
 * Created by huyawen on 2017/12/4.
 * email:1754397982@qq.com
 * <p>
 * "respCode" : "200"
 * "respDesc" : "请求已完成"
 */

public class DeviceModel {


    private String id;          //设备的id
    private String name;        //设置名称
    private String code;        //设备编码
    private String position;    //设备的位置
    private String type;        //设置的类型
    private String ip;          //设置的ip
    private String createTime;  //创建设备的时间

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
