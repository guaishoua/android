package com.android.tacu.db;

import android.content.Context;

import com.android.tacu.base.MyApplication;
import com.android.tacu.db.dao.LockNewModelUtils;
import com.android.tacu.db.dao.SearchHistorysModelUtils;

/**
 * Dao的管理类
 * Created by jiazhen on 2018/4/26.
 */

public class DaoManager {

    private static SearchHistorysModelUtils searchHistoryUtils;
    private static LockNewModelUtils lockNewModelUtils;

    private static Context getContextValue() {
        return MyApplication.getInstance();
    }

    public static SearchHistorysModelUtils getSearchHistoryUtils() {
        if (searchHistoryUtils == null) {
            searchHistoryUtils = new SearchHistorysModelUtils(getContextValue());
        }
        return searchHistoryUtils;
    }

    public static LockNewModelUtils getLockNewModelUtils() {
        if (lockNewModelUtils == null) {
            lockNewModelUtils = new LockNewModelUtils(getContextValue());
        }
        return lockNewModelUtils;
    }
}
