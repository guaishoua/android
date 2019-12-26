package com.android.tacu.module.assets.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.UnclaimedListModel;

/**
 * Created by jiazhen on 2018/12/26.
 */
public class InterestRecordContract {

    public interface IView extends IBaseMvpView {
        void setShanxibaoListSuccess(UnclaimedListModel model);

        void setShanxibaoListError();
    }

    public interface IPresenter {
        void getShanxibaoList(String startTime, String endTime, int start);
    }
}
