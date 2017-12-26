package com.meiaomei.bankusher.db.common.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Id;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.exception.DbException;
import com.meiaomei.bankusher.BankUsherApplication;
import com.meiaomei.bankusher.db.common.CommonDao;
import com.meiaomei.bankusher.manager.BankUsherDB;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kangjh on 2016/2/24.
 */
public class CommonDaoImpl implements CommonDao {
    private Context context;
    private SQLiteDatabase db;
    private String idName = "";  //id的key
    Map<String, String> kvs;     //列名（注解的列）以及对应的值
    DbUtils dbUtils;      //借用第三方xutils的方法

    public CommonDaoImpl(Context context) {
        this.context = context;
        dbUtils = BankUsherDB.getDbUtils();
        db = dbUtils.getDatabase();
    }

    public CommonDaoImpl(Context context, DbUtils dbUtils) {
        this.context = context;
        this.dbUtils = dbUtils;
        db = dbUtils.getDatabase();
    }

    @Override
    public void configDbInfo(String dbName) {
        if (dbUtils == null)
            dbUtils = DbUtils.create(BankUsherApplication.getAppContext(), dbName);
        db = dbUtils.getDatabase();
    }

    @Override
    public void exeCuteSql(String sql, String[] contentvalue) {
        db.execSQL(sql, contentvalue);
    }

    @Override
    public <T> String getTableName(T t) {  //获取表名
        String tableName = null;
        Table table = t.getClass().getAnnotation(Table.class);
        if (table != null) {
            tableName = table.name();
        }
        return tableName;
    }

    @Override
    public <T> String[] getColIndexs(T t) {
        String colIds[] = null;
        Table table = t.getClass().getAnnotation(Table.class);
        String tableCoIndex = table.execAfterTableCreated();//关联主键
        if (!tableCoIndex.equals("")) {
            if (kvs == null) {
                kvs = setAnnoValues(t);//通过反射将kvs
            }
            String splitFirst = tableCoIndex.split("\\(")[1];
            String splitSecond = splitFirst.split("\\)")[0];
            colIds = splitSecond.split(",");
        }
        return colIds;
    }

    @Override
    public <T> String getId(T t) {
        String id = null;
        if (kvs == null) {
            kvs = setAnnoValues(t);//通过反射将kvs
        }
        if (!idName.equals("")) {  //如果有ID
            id = kvs.get(idName);

        }
        return id;
    }

    @Override
    public <T> boolean haveSingleEntity(T t) {
        boolean flag = false;
        String tablename = getTableName(t);//表名
        kvs = setAnnoValues(t);//通过反射将kvs
        StringBuffer sql = new StringBuffer("select * from " + tablename + " where ");

        if (getColIndexs(t) != null) { //有关联主键
            String[] colIds = getColIndexs(t);
            for (int i = 0; i < colIds.length; i++) {
                sql.append(colIds[i] + "= '" + kvs.get(colIds[i]) + "' and ");
            }
        }

        if (getId(t) != null) {  //有Id
            sql.append(idName + "= '" + kvs.get(idName) + "' and ");
        }

        String newSql = sql.toString().substring(0, sql.length() - 5);
        Cursor cursor = db.rawQuery(newSql, new String[]{});
        if (cursor.moveToNext()) {
            flag = true;
        }
        cursor.close();

        //对于没有主键的表的处理
        if (getId(t) == null && getColIndexs(t) == null) {
            flag = false;
        }
        return flag;
    }

    @Override
    public <T> void saveAll(List<T> t) {
        if (dbUtils == null) {
            dbUtils = BankUsherDB.getDbUtils();
        }
        if (t != null) {
            for (int i = 0; i < t.size(); i++) {
                try {
                    if (haveSingleEntity(t.get(i))) {

                        deleteSingleEntity(t.get(i));
                        dbUtils.save(t.get(i));
                    } else {
                        dbUtils.save(t.get(i));
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public <T> boolean deleteSingleEntity(T t) {
        boolean flag = true;
        String tablename = getTableName(t);//表名
        kvs = setAnnoValues(t);//通过反射将kvs
        StringBuffer sql = new StringBuffer("delete from " + tablename + " where ");

        if (getColIndexs(t) != null) { //有关联主键
            String[] colIds = getColIndexs(t);
            for (int i = 0; i < colIds.length; i++) {
                sql.append(colIds[i] + "= '" + kvs.get(colIds[i]) + "' and ");
            }
        }

        if (getId(t) != null) {  //有Id
            sql.append(idName + "= '" + kvs.get(idName) + "' and ");
        }

        String newSql = sql.toString().substring(0, sql.length() - 5);
        try {
            db.execSQL(newSql);
        } catch (Exception e) {
            flag = false;
        }

        return flag;
    }

    public <T> Map<String, String> setAnnoValues(T m) {
        Map<String, String> kvs = new HashMap<String, String>();
        Field[] fields = m.getClass().getDeclaredFields();
        for (Field item : fields) {
            item.setAccessible(true);//让私有的Field也可以被操作，赋予权限
            Column columnName = item.getAnnotation(Column.class);
            Id id = item.getAnnotation(Id.class);
            //对id的处理
            if (id != null) {
                String value = "";
                try {
                    value = item.get(m).toString();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                idName = item.getName();
                if (id.column() != null && !id.column().equals("")) {//对ID的特殊处理
                    idName = id.column();
                }
                kvs.put(idName, value);//将列名
            }
            //对列名的处理
            if (columnName != null) {
                String key = columnName.column();   //注解的key
                String value; //entity的value
                try {
                    value = item.get(m).toString();
                    kvs.put(key, value);// 会存在问题，没有把主键id给剔除，也存进去；而且id是int类型，类型转换异常
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return kvs;
    }


    @Override
    public List<Map<String, String>> getCommonListMap(String sql, String[] contentvalue) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Cursor cursor = db.rawQuery(sql, contentvalue);
        while (cursor.moveToNext()) {
            Map<String, String> contents = new HashMap<String, String>();
            String[] keys = cursor.getColumnNames();
            for (int i = 0; i < keys.length; i++) {
                contents.put(keys[i], cursor.getString(cursor.getColumnIndex(keys[i])));
            }
            list.add(contents);
        }
        cursor.close();
        return list;
    }

    @Override
    public <T> List<T> getCommonListEntity(Class<T> clazz, String sql, String[] contentvalue) {
        List<Map<String, String>> maplist = getCommonListMap(sql, contentvalue);
        List<T> entitylist = new ArrayList<>();
        try {
            for (int i = 0; i < maplist.size(); i++) {
                Map<String, String> kvs = maplist.get(i);
                T t = clazz.newInstance();
                Field[] fields = clazz.getDeclaredFields();
                for (Field item : fields) {
                    item.setAccessible(true);
                    Id id = item.getAnnotation(Id.class);
                    Column column = item.getAnnotation(Column.class);
                    if (id != null) {
                        t = id.column().equals("") ? setItemValues(t, item, kvs.get(item.getName())) : setItemValues(t, item, kvs.get(id.column()));
                    } else if (column != null) {
                        t = setItemValues(t, item, kvs.get(column.column()));
                    } else if (kvs.get(item.getName()) != null) {
                        t = setItemValues(t, item, kvs.get(item.getName()));
                    }
                }
                entitylist.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entitylist;
    }

    private <T> T setItemValues(T t, Field item, String value) {
        try {
            if (value == null) return t;

            if (item.getGenericType().toString().contains("String")) {//对String类型的判断
                item.set(t, value);
            }
            if (item.getGenericType().toString().contains("int")) {//对int类型的判断
                item.set(t, Integer.parseInt(value));
            }
            if (item.getGenericType().toString().contains("Integer")) {//对int类型的判断
                item.set(t, new Integer(value));
            }
            if (item.getGenericType().toString().contains("long")) {//对long类型的判断
                if (value.equals("")) {
                    item.set(t, 0l);
                } else {
                    item.set(t, Long.valueOf(value));
                }
            }
            if (item.getGenericType().toString().contains("float")) {//对float类型的判断
                item.set(t, Float.valueOf(value));
            }
            if (item.getGenericType().toString().contains("Date")) {//对date类型的判断
                if (value.equals("0") || value.equals("")) {
                    item.set(t, null);
                } else {
                    item.set(t, new Date(Long.valueOf(value)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    @Override
    public <T> T getSingleEntity(Class<T> clazz, String sql, String[] contentvalue) {
        T t = null;
        try {
            t = clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Cursor cursor = db.rawQuery(sql, contentvalue);
        if (cursor.moveToNext()) {
            String[] keys = cursor.getColumnNames();
            Field[] fields = clazz.getDeclaredFields();
            for (Field item : fields) {
                item.setAccessible(true);
                if (item.getGenericType().toString().contains("String")) {//对String类型的判断
                    try {
                        Id id = item.getAnnotation(Id.class);
                        if (id != null) {
                            if (id.column() != null && !id.column().equals("")) {
                                item.set(t, cursor.getString(cursor.getColumnIndex(id.column())));
                            } else {
                                item.set(t, cursor.getString(cursor.getColumnIndex(item.getName())));
                            }
                        }
                        Column column = item.getAnnotation(Column.class);
                        if (column != null) {
                            if (item.getGenericType().toString().contains("String")) {//对String类型的判断
                                item.set(t, cursor.getString(cursor.getColumnIndex(column.column())));
                            }
                            if (item.getGenericType().toString().contains("Integer")) {//对int类型的判断
                                item.set(t, Integer.parseInt(cursor.getString(cursor.getColumnIndex(column.column()))));
                            }
                            if (item.getGenericType().toString().contains("Long")) {//对int类型的判断
                                item.set(t, Long.valueOf(cursor.getString(cursor.getColumnIndex(column.column()))));
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        cursor.close();
        return t;
    }

    @Override
    public boolean dropTableIfExits(String tableName) {
        try {
            db.execSQL(" DROP TABLE IF EXISTS " + tableName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
