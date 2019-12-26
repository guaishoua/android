package com.android.tacu.module.assets.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.LockChexAmountModel;
import com.android.tacu.module.assets.model.RewardCoinAccountModel;
import com.android.tacu.module.assets.model.RightMessageModel;
import com.android.tacu.module.assets.model.UnclaimedModel;

/**
 * Created by jiazhen on 2018/12/25.
 */
public class RewardAssetsContract {

    public interface IView extends IBaseMvpView {
        void setRewardCoinAccount(RewardCoinAccountModel model);

        void setUnclaimed(UnclaimedModel model);

        void setReceiveSuccess();

        void setLockChexAmount(LockChexAmountModel model);

        void setRightMessage(RightMessageModel model);
    }

    public interface IPresenter {
        void getRewardCoinAccount(boolean isShow);

        void getUnclaimed(boolean isShow);

        void getReceive(int id);

        void getLockChexAmount(boolean isShow);

        void getRightMessage(boolean isShow);
    }
}
