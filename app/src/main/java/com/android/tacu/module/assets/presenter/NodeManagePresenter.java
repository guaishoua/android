package com.android.tacu.module.assets.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.factory.ModelTransformerFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.assets.contract.NodeManageContract;
import com.android.tacu.module.assets.model.NodeDetailsModel;
import com.android.tacu.module.assets.model.NodeListModel;
import com.android.tacu.module.assets.model.NodeProfitModel;

import org.json.JSONObject;

/**
 * Created by xiaohong on 2018/8/22.
 */

public class NodeManagePresenter extends BaseMvpPresenter implements NodeManageContract.INodeManagePresenter {
    @Override
    public void getNodeListByNodeIds(String nodeIds) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.NODEHOST, Api.class).getNodeListByNodeIds(nodeIds), new NetDisposableObserver<NodeDetailsModel>((IBaseMvpView) getView(), true) {
            @Override
            public void onNext(NodeDetailsModel nodeDetailsModel) {
                NodeManageContract.INodeView view = (NodeManageContract.INodeView) getView();
                view.detailsList(nodeDetailsModel);
            }
        }, ModelTransformerFactory.getNonStandardModelTransformer());
    }

    @Override
    public void getNodeList(int page, boolean isFlag) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.NODEHOST, Api.class).getNodeList(page), new NetDisposableObserver<BaseModel<NodeListModel>>((IBaseMvpView) getView(), isFlag) {
            @Override
            public void onNext(BaseModel<NodeListModel> model) {
                NodeManageContract.INodeManageView view = (NodeManageContract.INodeManageView) getView();
                view.setNodeList(model.attachment);
            }
        });
    }

    @Override
    public void getNodePrifit(boolean isLoading) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.NODEHOST, Api.class).rightMessage(), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView(), isLoading) {
            @Override
            public void onNext(BaseModel model) {
                if (model != null && model.attachment != null) {
                    NodeManageContract.INodeManageView view = (NodeManageContract.INodeManageView) getView();
                    try {
                        JSONObject jsonObject = new JSONObject(model.attachment.toString());
                        double chexAmount = jsonObject.getDouble("chexAmount");//折算成人民币价格， 这个价格是实时
                        double allProfit = jsonObject.getDouble("allProfit");
                        double yesterdayProfit = jsonObject.getDouble("yesterdayProfit");
                        view.getMessage(chexAmount, allProfit, yesterdayProfit);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, ModelTransformerFactory.getNonStandardModelTransformer());
    }

    @Override
    public void getProfitList(String startTime, String endTime, int page, boolean isShowLoadingView) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.NODEHOST, Api.class).getProfitList(startTime, endTime, "", page), new NetDisposableObserver<BaseModel<NodeProfitModel>>((IBaseMvpView) getView(), isShowLoadingView) {
            @Override
            public void onNext(BaseModel<NodeProfitModel> o) {
                NodeManageContract.INodeView view = (NodeManageContract.INodeView) getView();
                view.profitList(o.attachment);
            }
        });
    }
}
