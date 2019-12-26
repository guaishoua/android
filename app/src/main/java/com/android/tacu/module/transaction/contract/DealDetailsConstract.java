package com.android.tacu.module.transaction.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.transaction.model.DealDetailsModel;

import java.util.List;

/**
 * Created by jiazhen on 2018/10/16.
 */
public class DealDetailsConstract {

    public interface IView extends IBaseMvpView {
        void dealDetail(List<DealDetailsModel> list);
    }

    public interface IPresenter {
        void dealDetail(String orderNo);
    }
}
