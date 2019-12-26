package com.android.tacu.db.dao;

import android.content.Context;

import com.android.tacu.db.BaseDao;
import com.android.tacu.db.model.LockModel;

import java.util.List;

/**
 * Created by jiazhen on 2018/8/13.
 */
public class LockModelUtils extends BaseDao<LockModel> {

    public LockModelUtils(Context context) {
        super(context, LockModel.class);
    }

    /**
     * 插入单个对象 重复就替换
     */
    public boolean insertOrReplaceLockModel(LockModel lockModel) {
        return insertOrReplaceObject(lockModel);
    }

    public void insertOrReplaceLockModel(int uid, String valString) {
        List<LockModel> list = getLockModelList(uid);
        if (list != null && list.size() > 0) {
            LockModel lockModel = list.get(0);
            lockModel.setValString(valString);
            insertOrReplaceLockModel(lockModel);
        } else {
            insertOrReplaceLockModel(new LockModel(null, uid, valString));
        }
    }

    public LockModel selectLockModel(int uid) {
        List<LockModel> list = getLockModelList(uid);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return new LockModel(null, uid, "");
        }
    }

    private List<LockModel> getLockModelList(int uid) {
        return QueryObject(" WHERE UID=?", new String[]{String.valueOf(uid)});
    }
}
