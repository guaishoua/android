package com.android.tacu.module.assets.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.LockChexListModel;

/**
 * Created by jiazhen on 2018/12/26.
 */
public class LockRecordContract {

    public interface IView extends IBaseMvpView {
        void setLockChexListSuccess(LockChexListModel model);

        void setLockChexListError();
    }

    public interface IPresenter {
        void getLockChexList(String startTime, String endTime, int start);
    }
}
