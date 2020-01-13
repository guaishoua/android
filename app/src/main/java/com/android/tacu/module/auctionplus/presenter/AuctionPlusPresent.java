package com.android.tacu.module.auctionplus.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.factory.ModelTransformerFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.model.AmountModel;
import com.android.tacu.module.auctionplus.contract.AuctionPlusContract;
import com.android.tacu.module.auctionplus.modal.AuctionPayStatusModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusDataBaseModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusListByIdModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusListModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusPayInfoModel;

import java.util.List;

/**
 * Created by jiazhen on 2019/4/17.
 */
public class AuctionPlusPresent extends BaseMvpPresenter implements AuctionPlusContract.IPresenter {

    /**
     * @param start
     * @param size
     * @param status
     * @param isTop  是否返回到头部
     * @param isFlag
     */
    @Override
    public void auctionPlusList(int start, int size, int status, final boolean isTop, final boolean isFlag) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.AUCTIONPLUS, Api.class).auctionPlusList(start, size, status), new NetDisposableObserver<BaseModel<AuctionPlusListModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<AuctionPlusListModel> model) {
                AuctionPlusContract.IListView view = (AuctionPlusContract.IListView) getView();
                view.auctionPlusList(model.attachment, isTop, isFlag);
            }
        });
    }

    @Override
    public void auctionPlusListByType(int start, int size, int type, final boolean isTop, final boolean isFlag) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.AUCTIONPLUS, Api.class).auctionPlusListByType(start, 10, type), new NetDisposableObserver<BaseModel<AuctionPlusListModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<AuctionPlusListModel> model) {
                AuctionPlusContract.IListView view = (AuctionPlusContract.IListView) getView();
                view.auctionPlusList(model.attachment, isTop, isFlag);
            }
        });
    }

    @Override
    public void auctionPlusListWait(int start, int size, final boolean isTop, final boolean isFlag) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.AUCTIONPLUS, Api.class).auctionPlusWait(start, 10), new NetDisposableObserver<BaseModel<AuctionPlusListModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<AuctionPlusListModel> model) {
                AuctionPlusContract.IListView view = (AuctionPlusContract.IListView) getView();
                view.auctionPlusList(model.attachment, isTop, isFlag);
            }
        });
    }

    @Override
    public void auctionPlusData(String ids) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.AUCTIONPLUS, Api.class).auctionPlusData(ids), new NetDisposableObserver<BaseModel<AuctionPlusDataBaseModel>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<AuctionPlusDataBaseModel> model) {
                AuctionPlusContract.IListView view = (AuctionPlusContract.IListView) getView();
                view.auctionPlusData(model.attachment);
            }
        });
    }

    /**
     * @param model
     * @param type              1=列表页 2=详情页
     */
    @Override
    public void customerCoinByOneCoin(final Integer currencyId, final AuctionPlusModel model, final int type) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.ASSET, Api.class).customerCoinByOneCoin(currencyId), new NetDisposableObserver<AmountModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(AmountModel o) {
                if (type == 1) {
                    AuctionPlusContract.IListView view = (AuctionPlusContract.IListView) getView();
                    view.customerCoinByOneCoin(o, model);
                } else if (type == 2) {
                    AuctionPlusContract.IDetailView view = (AuctionPlusContract.IDetailView) getView();
                    view.customerCoinByOneCoin(o, model);
                }
            }
        }, ModelTransformerFactory.getNonStandardModelTransformer());
    }

    /**
     * @param type 1=列表页 2=详情页
     */
    @Override
    public void auctionPlusListPay(String ids, final int type) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.AUCTIONPLUS, Api.class).auctionPluslistpay(ids), new NetDisposableObserver<BaseModel<List<AuctionPayStatusModel>>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<List<AuctionPayStatusModel>> model) {
                if (type == 1) {
                    AuctionPlusContract.IListView view = (AuctionPlusContract.IListView) getView();
                    view.auctionPlusListPay(model.attachment);
                } else if (type == 2) {
                    AuctionPlusContract.IDetailView view = (AuctionPlusContract.IDetailView) getView();
                    view.auctionPlusListPay(model.attachment);
                }
            }
        });
    }

    /**
     * @param start
     * @param id
     * @param type  1=详情页 2=出价列表页
     */
    @Override
    public void listPlusById(int start, int size, String id, final int type, boolean isShowLoadingView) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.AUCTIONPLUS, Api.class).listPlusById(start, size, id), new NetDisposableObserver<BaseModel<AuctionPlusListByIdModel>>((IBaseMvpView) getView(), isShowLoadingView) {
            @Override
            public void onNext(BaseModel<AuctionPlusListByIdModel> model) {
                if (type == 1) {
                    AuctionPlusContract.IDetailView view = (AuctionPlusContract.IDetailView) getView();
                    view.listPlusById(model.attachment);
                } else if (type == 2) {
                    AuctionPlusContract.IOfferView view = (AuctionPlusContract.IOfferView) getView();
                    view.listPlusById(model.attachment);
                }
            }
        });
    }

    @Override
    public void collectCheck(String id) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.AUCTIONPLUS, Api.class).collectCheck(id), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel model) {
                AuctionPlusContract.IDetailView view = (AuctionPlusContract.IDetailView) getView();
                view.collectCheck((boolean) model.attachment);
            }
        });
    }

    @Override
    public void collect(String id) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.AUCTIONPLUS, Api.class).auctionCollect(id), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                AuctionPlusContract.IDetailView view = (AuctionPlusContract.IDetailView) getView();
                view.collectSuccess();
            }

            @Override
            public void onError(Throwable throwable) {
                super.onError(throwable);
                AuctionPlusContract.IDetailView view = (AuctionPlusContract.IDetailView) getView();
                view.collectError();
            }
        });
    }

    /**
     * @param id
     * @param count
     * @param type  1=详情页 2=出价列表页
     */
    @Override
    public void auctionBuy(String id, String count, final int type) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.AUCTIONPLUS, Api.class).auctionBuy(id, count), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                if (type == 1) {
                    AuctionPlusContract.IDetailView view = (AuctionPlusContract.IDetailView) getView();
                    view.auctionBuySuccess();
                } else if (type == 2) {
                    AuctionPlusContract.IListView view = (AuctionPlusContract.IListView) getView();
                    view.auctionBuySuccess();
                }
            }
        });
    }

    /**
     * @param type 1=列表页 2=详情页
     */
    @Override
    public void auctionPayInfo(final AuctionPlusModel auctionPlusModel, final String balance, final int type) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.AUCTIONPLUS, Api.class).payInfo(auctionPlusModel.id), new NetDisposableObserver<BaseModel<AuctionPlusPayInfoModel>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<AuctionPlusPayInfoModel> model) {
                if (type == 1) {
                    AuctionPlusContract.IListView view = (AuctionPlusContract.IListView) getView();
                    view.auctionPayInfo(model.attachment, auctionPlusModel, balance);
                } else if (type == 2) {
                    AuctionPlusContract.IDetailView view = (AuctionPlusContract.IDetailView) getView();
                    view.auctionPayInfo(model.attachment, auctionPlusModel, balance);
                }
            }
        });
    }

    /**
     * @param viewType 0=AuctionPlus列表 1=详情页
     */
    @Override
    public void auctionPay(String id, int type, final int viewType) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.AUCTIONPLUS, Api.class).auctionPlusPay(id, type), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                if (viewType == 0) {
                    AuctionPlusContract.IListView view = (AuctionPlusContract.IListView) getView();
                    view.auctionPaySuccess();
                } else if (viewType == 1) {
                    AuctionPlusContract.IDetailView view = (AuctionPlusContract.IDetailView) getView();
                    view.auctionPaySuccess();
                }
            }
        });
    }
}
