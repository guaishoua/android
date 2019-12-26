package com.android.tacu.module.assets.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.factory.ModelTransformerFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.contract.TransferRecordContract;
import com.android.tacu.module.assets.model.TransferInfo;
import com.android.tacu.module.webview.model.EPayParam;

/**
 * Created by xiaohong on 2018/8/21.
 */

public class TransferOTCPresenter extends BaseMvpPresenter implements TransferRecordContract.IPresenter {

    @Override
    public void transIn(int currencyId, String currencyName, String transNum) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.EPAY, Api.class).transIn(currencyId, currencyName, transNum), new NetDisposableObserver<BaseModel<EPayParam>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<EPayParam> baseModel) {
                TransferRecordContract.ITransferView view = (TransferRecordContract.ITransferView) getView();
                view.transIn(baseModel.attachment);
            }
        });
    }

    @Override
    public void transOut(String amount, String transAccount, int currencyId, String currencyName, String fdPassword, String sAuthCode, String gAuthCode) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.EPAY, Api.class).transOut("9", amount, transAccount, currencyId, currencyName, fdPassword, sAuthCode, gAuthCode), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel o) {
                TransferRecordContract.ITransferView view = (TransferRecordContract.ITransferView) getView();
                view.transOut();
            }
        });
    }

    @Override
    public void customerCoinByOneCoin(int currencyId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.EPAY, Api.class).transInfo(currencyId), new NetDisposableObserver<BaseModel<TransferInfo>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<TransferInfo> model) {
                TransferRecordContract.ITransferView view = (TransferRecordContract.ITransferView) getView();
                view.customerCoinByOneCoin(model.attachment);
            }
        }, ModelTransformerFactory.getNonStandardModelTransformer());
    }

    @Override
    public void addAccount(String epayAccount, String memo) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.EPAY, Api.class).addAccount(epayAccount, memo), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                TransferRecordContract.ITransferView view = (TransferRecordContract.ITransferView) getView();
                view.addAccount();
            }
        });
    }

    @Override
    public void emailTakeCoin(int type) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).emailTakeCoin(type), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel coinsAddress) {
                TransferRecordContract.ITransferView view = (TransferRecordContract.ITransferView) getView();
                view.success();
            }
        });
    }


}
