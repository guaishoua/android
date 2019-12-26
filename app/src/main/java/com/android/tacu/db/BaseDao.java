package com.android.tacu.db;

import android.content.Context;

import com.android.tacu.BuildConfig;
import com.android.tacu.utils.LogUtils;
import com.greendao.gen.DaoSession;

import java.util.List;

/**
 * Created by jiazhen on 2018/4/26.
 */

public class BaseDao<T> {

    public static final boolean DUBUG = BuildConfig.DEBUG;

    private DBHelper dbHelper;
    private DaoSession dbSession;
    private Class cls;

    public BaseDao(Context context, Class cls) {
        this.dbHelper = DBHelper.getInstance();
        this.dbHelper.init(context);
        this.dbSession = this.dbHelper.getDaoSession();
        this.cls = cls;
        this.dbHelper.setDebug(DUBUG);
    }

    /**************************数据库插入操作***********************/

    /**
     * 插入单个对象
     */
    public boolean insertObject(T object) {
        boolean flag = false;
        try {
            flag = dbSession.insert(object) != -1 ? true : false;
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
        return flag;
    }

    /**
     * 插入单个对象 如果主键重复就会自动替换
     */
    public boolean insertOrReplaceObject(T object) {
        boolean flag = false;
        if (object == null) {
            return false;
        }
        try {
            flag = dbSession.insertOrReplace(object) != -1 ? true : false;
        } catch (Exception e) {
            flag = false;
            LogUtils.e("insertObject：" + e.toString());
        }
        return flag;
    }

    /**
     * 插入多个对象，并开启新的线程
     */
    public boolean insertMultObject(final List<T> objects) {
        boolean flag = false;
        if (objects == null || objects.size() == 0) {
            return false;
        }
        try {
            dbSession.runInTx(new Runnable() {
                @Override
                public void run() {
                    for (T object : objects) {
                        dbSession.insert(object);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            flag = false;
            LogUtils.e("insertMultObject：" + e.toString());
        }
        return flag;
    }

    /**
     * 插入多个对象，并开启新的线程
     */
    public boolean insertOrReplaceMultObject(final List<T> objects) {
        boolean flag = false;
        if (objects == null || objects.size() == 0) {
            return false;
        }
        try {
            dbSession.runInTx(new Runnable() {
                @Override
                public void run() {
                    for (T object : objects) {
                        dbSession.insertOrReplace(object);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            flag = false;
            LogUtils.e("insertMultObject：" + e.toString());
        }
        return flag;
    }

    /**************************数据库更新操作***********************/

    /**
     * 以对象形式进行数据修改
     * 其中必须要知道对象的主键ID
     */
    public void updateObject(T object) {
        if (object == null) {
            return;
        }
        try {
            dbSession.update(object);
        } catch (Exception e) {
            LogUtils.e("updateObject：" + e.toString());
        }
    }

    /**
     * 批量更新数据
     */
    public void updateMultObject(final List<T> objects) {
        if (objects == null || objects.isEmpty()) {
            return;
        }
        try {
            dbSession.runInTx(new Runnable() {
                @Override
                public void run() {
                    for (T object : objects) {
                        dbSession.update(object);
                    }
                }
            });
        } catch (Exception e) {
            LogUtils.e("updateMultObject：" + e.toString());
        }
    }


    /**************************数据库删除操作***********************/

    /**
     * 删除某个数据库表
     */
    public boolean deleteAll() {
        boolean flag = false;
        try {
            dbSession.deleteAll(cls);
            flag = true;
        } catch (Exception e) {
            flag = false;
            LogUtils.e("deleteAll：" + e.toString());
        }
        return flag;
    }

    /**
     * 删除某个对象
     */
    public void deleteObject(T object) {
        try {
            dbSession.delete(object);
        } catch (Exception e) {
            LogUtils.e("deleteObject：" + e.toString());
        }
    }

    /**
     * 异步批量删除数据
     */
    public boolean deleteMultObject(final List<T> objects) {
        boolean flag = false;
        if (objects == null || objects.isEmpty()) {
            return false;
        }
        try {
            dbSession.runInTx(new Runnable() {
                @Override
                public void run() {
                    for (T object : objects) {
                        dbSession.delete(object);
                    }
                }
            });
            flag = true;
        } catch (Exception e) {
            flag = false;
            LogUtils.e("deleteMultObject：" + e.toString());
        }
        return flag;
    }

    /**************************数据库查询操作***********************/

    /**
     * 获得某个表名
     *
     * @return
     */
    public String getTablename() {
        return dbSession.getDao(cls).getTablename();
    }


    /**
     * 根据主键ID来查询 主键必须是long类型
     */
    public T QueryById(long id) {
        return (T) dbSession.getDao(cls).loadByRowId(id);
    }

    /**
     * 查询某条件下的对象
     */
    public List<T> QueryObject(String where, String... params) {
        Object obj = null;
        List<T> objects = null;
        try {
            obj = dbSession.getDao(cls);
            if (obj == null) {
                return null;
            }
            objects = dbSession.getDao(cls).queryRaw(where, params);
        } catch (Exception e) {
            LogUtils.e("QueryObject：" + e.toString());
        }
        return objects;
    }

    /**
     * 查询所有对象
     */
    public List<T> QueryAll() {
        List<T> objects = null;
        try {
            objects = (List<T>) dbSession.getDao(cls).loadAll();
        } catch (Exception e) {
            LogUtils.e("QueryAll：" + e.toString());
        }
        return objects;
    }
}
