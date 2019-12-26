package com.android.tacu.module.assets.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.NodeModel;

/**
 * Created by xiaohong on 2018/8/22.
 */

public class NodeContract {
    public interface INodeView extends IBaseMvpView {
        void nodeSuccess(String msg);

        void setNodeModel(NodeModel nodeModel);
    }

    public interface INodePresenter {
        void applyNode(int lockNodeNum, String fdPwd);

        void unlockNode(int unLockNodeNum, String fdPwd);

        void getNodeDetail();
    }
}
