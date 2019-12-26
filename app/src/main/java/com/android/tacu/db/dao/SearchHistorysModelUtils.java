package com.android.tacu.db.dao;

import android.content.Context;

import com.android.tacu.db.BaseDao;
import com.android.tacu.db.model.SearchHistorysModel;

import java.util.List;

/**
 * Created by jiazhen on 2018/5/12.
 */

public class SearchHistorysModelUtils extends BaseDao<SearchHistorysModel> {

    public SearchHistorysModelUtils(Context context) {
        super(context, SearchHistorysModel.class);
    }

    /**
     * 插入单个对象
     */
    public boolean insertSearchHistoryModel(int uid, int currencyId, int baseCurrencyId, String currencyNameEn, String baseCurrencyNameEn) {
        SearchHistorysModel searchHistory = new SearchHistorysModel(null, uid, currencyId, baseCurrencyId, currencyNameEn, baseCurrencyNameEn);
        return insertObject(searchHistory);
    }

    /**
     * 批量条件删除
     */
    public void deleteMultSearchHistoryModel(int uid) {
        List<SearchHistorysModel> list = getSearchHistoryModelList(uid);
        if (list != null && list.size() > 0) {
            deleteMultObject(list);
        }
    }

    public void deleteSearchHistoryModel(SearchHistorysModel searchHistory) {
        deleteObject(searchHistory);
    }

    /**
     * 查询指定币种的查询历史
     */
    public boolean getSearchHistoryModel(int uid, String currencyNameEn, String baseCurrencyNameEn) {
        List<SearchHistorysModel> list = QueryObject(" WHERE UID=? AND CURRENCY_NAME_EN=? AND BASE_CURRENCY_NAME_EN=? ORDER BY ID DESC LIMIT 0,20", new String[]{String.valueOf(uid), currencyNameEn, baseCurrencyNameEn});
        if (list != null && list.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 查询指定币种的查询历史列表
     */
    public List<SearchHistorysModel> getSearchHistoryModelList(int uid) {
        return QueryObject(" WHERE UID=?", new String[]{String.valueOf(uid)});
    }
}
