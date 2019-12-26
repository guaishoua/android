package com.android.tacu.module.assets.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.contract.RewardAssetsContract;
import com.android.tacu.module.assets.model.LockChexAmountModel;
import com.android.tacu.module.assets.model.RewardCoinAccountModel;
import com.android.tacu.module.assets.model.RightMessageModel;
import com.android.tacu.module.assets.model.UnclaimedModel;

/**
 * Created by jiazhen on 2018/12/25.
 */
public class RewardAssetsPresenter extends BaseMvpPresenter implements RewardAssetsContract.IPresenter {
    @Override
    public void getRewardCoinAccount(boolean isShow) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).getRewardCoinAccount(), new NetDisposableObserver<BaseModel<RewardCoinAccountModel>>((IBaseMvpView) getView(), isShow) {
            @Override
            public void onNext(BaseModel<RewardCoinAccountModel> model) {
                RewardAssetsContract.IView view = (RewardAssetsContract.IView) getView();
                view.setRewardCoinAccount(model.attachment);
            }
        });
    }

    @Override
    public void getUnclaimed(boolean isShow) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.DEI, Api.class).getUnclaimed(), new NetDisposableObserver<BaseModel<UnclaimedModel>>((IBaseMvpView) getView(), isShow) {
            @Override
            public void onNext(BaseModel<UnclaimedModel> model) {
                RewardAssetsContract.IView view = (RewardAssetsContract.IView) getView();
                view.setUnclaimed(model.attachment);
            }
        });
    }

    @Override
    public void getReceive(int id) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.DEI, Api.class).getShanxibaoRecevie(id), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                RewardAssetsContract.IView view = (RewardAssetsContract.IView) getView();
                view.setReceiveSuccess();
            }
        });
    }

    @Override
    public void getLockChexAmount(boolean isShow) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.NODEHOST, Api.class).getLockChexAmount(), new NetDisposableObserver<BaseModel<LockChexAmountModel>>((IBaseMvpView) getView(), isShow) {
            @Override
            public void onNext(BaseModel<LockChexAmountModel> model) {
                RewardAssetsContract.IView view = (RewardAssetsContract.IView) getView();
                view.setLockChexAmount(model.attachment);
            }
        });
    }

    @Override
    public void getRightMessage(boolean isShow) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.NODEHOST, Api.class).getRightMessage(), new NetDisposableObserver<BaseModel<RightMessageModel>>((IBaseMvpView) getView(), isShow) {
            @Override
            public void onNext(BaseModel<RightMessageModel> model) {
                RewardAssetsContract.IView view = (RewardAssetsContract.IView) getView();
                view.setRightMessage(model.attachment);
            }
        });
    }
}
