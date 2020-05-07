package com.android.tacu.module.my.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.my.contract.InvitedinfoContract;
import com.android.tacu.module.my.model.InvitedAllDetailModel;
import com.android.tacu.module.my.model.InvitedAllModel;

/**
 * Created by jiazhen on 2018/8/26.
 */
public class InvitedinfoPresenter extends BaseMvpPresenter implements InvitedinfoContract.IPresenter {

    @Override
    public void getInvitedInfo() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).invitedAll(1, 1), new NetDisposableObserver<BaseModel<InvitedAllModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<InvitedAllModel> model) {
                InvitedinfoContract.IView view = (InvitedinfoContract.IView) getView();
                view.showInvitedInfo(model.attachment);
            }
        });
    }

    @Override
    public void getInvitedAllDetail(boolean isShowView, int page, int size, int status) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).getInvitedAllDetail(page, size, status, 1), new NetDisposableObserver<BaseModel<InvitedAllDetailModel>>((IBaseMvpView) getView(), isShowView) {
            @Override
            public void onNext(BaseModel<InvitedAllDetailModel> model) {
                InvitedinfoContract.IRecordChildView view = (InvitedinfoContract.IRecordChildView) getView();
                view.showInvitedInfo(model.attachment);
            }
        });
    }
}
