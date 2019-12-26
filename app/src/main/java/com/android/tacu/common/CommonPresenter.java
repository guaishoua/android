package com.android.tacu.common;


import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.factory.ModelTransformerFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.my.model.InvitedInfoModel;

public class CommonPresenter extends BaseMvpPresenter implements CommonContract.IPresenter {

    private CommonContract.IView receptor;

    public CommonPresenter(CommonContract.IView receptor) {
        this.receptor = receptor;
    }

    @Override
    public void fetchInvitedInfo(int uid) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.DATAOP, Api.class)
                        .getInvitedInfo(uid),
                new NetDisposableObserver<InvitedInfoModel>((IBaseMvpView) getView()) {
                    @Override
                    public void onNext(InvitedInfoModel model) {
                        receptor.onInviteInfoReceieved(model);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        super.onComplete();
                        receptor.onInviteInfoReceieved(null);
                    }

                    @Override
                    public void onComplete() {
                        super.onComplete();
                    }


                }, ModelTransformerFactory.getNonStandardModelTransformer());
    }
}
