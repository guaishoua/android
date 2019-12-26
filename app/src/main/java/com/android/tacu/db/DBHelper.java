package com.android.tacu.db;

import android.content.Context;

import com.greendao.gen.DaoMaster;
import com.greendao.gen.DaoSession;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * Created by jiazhen on 2018/4/26.
 */

public class DBHelper {

    private static final String DB_NAME = "12lian.db";

    private volatile static DBHelper instance;
    private DaoMaster.DevOpenHelper migrationHelper;
    private DaoMaster daoMaster;
    private DaoSession daoSession;
    private Context context;

    /**
     * 使用单例模式获得操作数据库的对象
     *
     * @return
     */
    public static DBHelper getInstance() {
        if (instance == null) {
            synchronized (DBHelper.class) {
                if (instance == null) {
                    instance = new DBHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化Context对象
     *
     * @param context
     */
    public void init(Context context) {
        this.context = context;
    }

    /**
     * 判断数据库是否存在，如果不存在则创建
     */
    public DaoMaster getDaoMaster() {
        if (daoMaster == null) {
            migrationHelper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
            daoMaster = new DaoMaster(migrationHelper.getWritableDatabase());
        }
        return daoMaster;
    }

    /**
     * 取得数据库实现增删改查
     */
    public DaoSession getDaoSession() {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster();
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    /**
     * 设置debug模式开启或关闭，默认关闭
     */
    public void setDebug(boolean flag) {
        //SQL的语句
        QueryBuilder.LOG_SQL = flag;
        //SQL的参数
        QueryBuilder.LOG_VALUES = flag;
    }
}
