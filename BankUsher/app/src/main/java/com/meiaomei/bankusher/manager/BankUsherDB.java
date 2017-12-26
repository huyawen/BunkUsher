package com.meiaomei.bankusher.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.meiaomei.bankusher.BankUsherApplication;
import com.meiaomei.bankusher.entity.UserInfoModel;
import com.meiaomei.bankusher.entity.VipCustomerModel;
import com.meiaomei.bankusher.entity.PropertyModel;
import com.meiaomei.bankusher.entity.VisitRecordModel;

/**
 * Created by huyawen on 2017/11/6.
 */

public class BankUsherDB {

    Context context;
    static DbUtils dbUtils;

    public BankUsherDB(Context context) {
        this.context = context;
        dbUtils = DbUtils.create(context, "bankusher.db");
        dbUtils.configAllowTransaction(true);
    }


    /**
     * 取得
     */
    public static DbUtils getDbUtils() {
        initDBUtil(BankUsherApplication.getAppContext());
        return dbUtils;
    }


    /**
     * 初始化DBUtils
     *
     * @param context 上下文
     */
    public static void initDBUtil(Context context) {
        try {
            //数据库非空校验
            if (dbUtils == null) {
                dbUtils = DbUtils.create(context, "bankusher.db");
                dbUtils.configAllowTransaction(true);           //配置事务处理
                return;
            }

            //数据库名称校验   得到dbutils的数据库名称
            String dbName = dbUtils.getDaoConfig().getDbName();
            if (dbName == null || !dbName.equals("bankusher.db")) {
                dbUtils.close();
                dbUtils = DbUtils.create(context, "bankusher.db");
                dbUtils.configAllowTransaction(true);           //配置事务处理
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void creatNewTable() {//第一次登录创建新表
        initDBUtil(BankUsherApplication.getAppContext());
        try {
            dbUtils.createTableIfNotExist(VipCustomerModel.class);//有关联关系的表  必须先建主表
            dbUtils.createTableIfNotExist(VisitRecordModel.class);
            dbUtils.createTableIfNotExist(PropertyModel.class);
            dbUtils.createTableIfNotExist(UserInfoModel.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static void deletTable() {//第一次登录创建新表
        initDBUtil(BankUsherApplication.getAppContext());
        try {
            dbUtils.dropTable(PropertyModel.class);
            dbUtils.dropTable(VipCustomerModel.class);
            dbUtils.dropTable(VisitRecordModel.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static void beginTransaction() {
        initDBUtil(BankUsherApplication.getAppContext());
        SQLiteDatabase database = dbUtils.getDatabase();
        database.beginTransaction();
    }

    public static void setTransactionSuccessful() {
        initDBUtil(BankUsherApplication.getAppContext());
        SQLiteDatabase database = dbUtils.getDatabase();
        database.setTransactionSuccessful();
    }

    public static void endTransaction() {
        initDBUtil(BankUsherApplication.getAppContext());
        SQLiteDatabase database = dbUtils.getDatabase();
        database.endTransaction();
    }
}
