package com.android.tacu.module.market.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.market.contract.NoticeContract;
import com.android.tacu.module.market.model.NoticeModel;

import java.util.List;

/**
 * Created by jiazhen on 2018/8/21.
 */
public class NoticePresenter extends BaseMvpPresenter implements NoticeContract.IPresenter {

    @Override
    public void getNoticeInfo(int page, int perPage, boolean isShowLoadingView) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ZEN, Api.class).getNoticeInfo(page, perPage, 4), new NetDisposableObserver<BaseModel<List<NoticeModel>>>((IBaseMvpView) getView(), isShowLoadingView) {
            @Override
            public void onNext(BaseModel<List<NoticeModel>> model) {
                NoticeContract.IView view = (NoticeContract.IView) getView();
                view.showNoticeList(model.attachment);
            }
        });
    }
}
