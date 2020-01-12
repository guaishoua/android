package com.android.tacu.db.dao;

import android.content.Context;

import com.android.tacu.db.BaseDao;
import com.android.tacu.db.model.LockNewModel;

import java.util.List;

/**
 * Created by jiazhen on 2018/8/13.
 */
public class LockNewModelUtils extends BaseDao<LockNewModel> {

    public LockNewModelUtils(Context context) {
        super(context, LockNewModel.class);
    }

    /**
     * 插入单个对象 重复就替换
     */
    public boolean insertOrReplaceLockNewModel(LockNewModel LockNewModel) {
        return insertOrReplaceObject(LockNewModel);
    }

    public void insertOrReplaceLockNewModel(String accountString, String accountPwd) {
        List<LockNewModel> list = getLockNewModelList(accountString);
        if (list != null && list.size() > 0) {
            LockNewModel lockNewModel = list.get(0);
            lockNewModel.setAccountString(accountString);
            lockNewModel.setAccountPwd(accountPwd);
            insertOrReplaceLockNewModel(lockNewModel);
        } else {
            insertOrReplaceLockNewModel(new LockNewModel(null, accountString, accountPwd, "", false));
        }
    }

    public void updateLockNewModel(String accountString, String gestureString, boolean isFinger) {
        List<LockNewModel> list = getLockNewModelList(accountString);
        if (list != null && list.size() > 0) {
            LockNewModel lockNewModel = list.get(0);
            lockNewModel.setGestureString(gestureString);
            lockNewModel.setIsFinger(isFinger);
            insertOrReplaceLockNewModel(lockNewModel);
        }
    }

    public LockNewModel getLockNewModel(String accountString) {
        List<LockNewModel> list = getLockNewModelList(accountString);
        if (list != null && list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public String getGestureString(String accountString) {
        LockNewModel lockNewModel = getLockNewModel(accountString);
        if (lockNewModel != null) {
            return lockNewModel.getGestureString();
        }
        return null;
    }

    public boolean getIsFinger(String accountString) {
        LockNewModel lockNewModel = getLockNewModel(accountString);
        if (lockNewModel != null) {
            return lockNewModel.getIsFinger();
        }
        return false;
    }

    private List<LockNewModel> getLockNewModelList(String accountString) {
        return QueryObject(" WHERE ACCOUNT_STRING=?", new String[]{String.valueOf(accountString)});
    }
}
