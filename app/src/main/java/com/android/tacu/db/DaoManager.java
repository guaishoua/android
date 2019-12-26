package com.android.tacu.db;

import android.content.Context;

import com.android.tacu.base.MyApplication;
import com.android.tacu.db.dao.LockModelUtils;
import com.android.tacu.db.dao.SearchHistorysModelUtils;

/**
 * Dao的管理类
 * Created by jiazhen on 2018/4/26.
 */

public class DaoManager {

    private static SearchHistorysModelUtils searchHistoryUtils;
    private static LockModelUtils lockModelUtils;

    private static Context getContextValue() {
        return MyApplication.getInstance();
    }

    public static SearchHistorysModelUtils getSearchHistoryUtils() {
        if (searchHistoryUtils == null) {
            searchHistoryUtils = new SearchHistorysModelUtils(getContextValue());
        }
        return searchHistoryUtils;
    }

    public static LockModelUtils getLockModelUtils() {
        if (lockModelUtils == null) {
            lockModelUtils = new LockModelUtils(getContextValue());
        }
        return lockModelUtils;
    }
}
