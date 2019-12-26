package com.android.tacu.module.my.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.factory.ModelTransformerFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.my.contract.InvitedinfoContract;
import com.android.tacu.module.my.model.InvitedAllModel;

/**
 * Created by jiazhen on 2018/8/26.
 */
public class InvitedinfoPresenter extends BaseMvpPresenter implements InvitedinfoContract.IPresenter {

    @Override
    public void getInvitedInfo(Integer page, Integer size) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USER, Api.class).invitedAll(page, size), new NetDisposableObserver<BaseModel<InvitedAllModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<InvitedAllModel> model) {
                InvitedinfoContract.IView view = (InvitedinfoContract.IView) getView();
                view.showInvitedInfo(model.attachment);
            }
        }, ModelTransformerFactory.getNonStandardModelTransformer());
    }
}
