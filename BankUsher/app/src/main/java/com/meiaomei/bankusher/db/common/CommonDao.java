package com.meiaomei.bankusher.db.common;

import java.util.List;
import java.util.Map;

/**
 * Created by kangjh on 2016/2/24.
 */
public interface CommonDao {
    //获取表名
    <T> String getTableName(T t);
    //获取关联主键
    <T> String[] getColIndexs(T t);
    //获取主键
    <T> String getId(T t);

    //通过对象获取数据库的值
    <T> boolean haveSingleEntity(T t);
    //调用xutils方法保存全部
    <T> void saveAll(List<T> t);
    //通过对象获取数据库的值
    <T> boolean deleteSingleEntity(T t);
    //查询全部返回map
    List<Map<String,String>> getCommonListMap(String sql, String[] contentvalue);
    //查询全部返回list
    <T>List<T> getCommonListEntity(Class<T> clazz, String sql, String[] contentvalue);
    //查询单条返回单条数据
    <T>T getSingleEntity(Class<T> clazz, String sql, String[] contentvalue);
    //删除表
    boolean dropTableIfExits(String tableName);
    //数据库信息设置
    void configDbInfo(String dbName);
    //执行sql方法
    void exeCuteSql(String sql, String[] contentvalue);
}
