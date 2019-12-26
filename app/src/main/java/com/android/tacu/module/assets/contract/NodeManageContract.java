package com.android.tacu.module.assets.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.NodeDetailsModel;
import com.android.tacu.module.assets.model.NodeListModel;
import com.android.tacu.module.assets.model.NodeProfitModel;

/**
 * Created by xiaohong on 2018/8/22.
 */

public class NodeManageContract {
    public interface INodeManageView extends IBaseMvpView {
        void setNodeList(NodeListModel node);

        void getMessage(double chexAmount, double allProfit, double yesterdayProfit);
    }

    public interface INodeView extends IBaseMvpView {
        void profitList(NodeProfitModel model);

        void detailsList(NodeDetailsModel model);
    }

    public interface INodeManagePresenter {
        void getNodeListByNodeIds(String nodeIds);

        void getNodeList(int page, boolean isFlag);

        void getNodePrifit(boolean isLoading);

        void getProfitList(String startTime, String endTime, int page, boolean isShowLoadingView);
    }
}
