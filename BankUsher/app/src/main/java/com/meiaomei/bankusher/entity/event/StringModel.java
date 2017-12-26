package com.meiaomei.bankusher.entity.event;

import java.util.HashMap;

/**
 * Created by Administrator on 2016/11/11.
 */
public class StringModel {
    String key;
    String msg;
    String fileName;
    HashMap<String,String> hashMap;

    public StringModel(String key) {
        this.key = key;
    }

    public StringModel(String key, String msg) {
        this.key = key;
        this.msg = msg;
    }

    public StringModel(String key, String msg, String fileName) {
        this.key = key;
        this.msg = msg;
        this.fileName=fileName;
    }

    public StringModel(String key , HashMap<String, String> hashMap) {
        this.key = key;
        this.hashMap = hashMap;
    }

    public HashMap<String, String> getHashMap() {
        return hashMap;
    }

    public void setHashMap(HashMap<String, String> hashMap) {
        this.hashMap = hashMap;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getMsg() {
        return msg;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
