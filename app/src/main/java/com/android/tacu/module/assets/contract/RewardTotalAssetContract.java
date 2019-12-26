package com.android.tacu.module.assets.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.RewardTotalAssetModel;

/**
 * Created by jiazhen on 2018/12/26.
 */
public class RewardTotalAssetContract {

    public interface IView extends IBaseMvpView {
        void setRewardListSuccess(RewardTotalAssetModel model);

        void setRewardListError();
    }

    public interface IPresenter {
        void getRewardList(String startTime, String endTime, int start);
    }
}
