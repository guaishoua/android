package com.android.tacu.module.market.contract;

import com.android.tacu.base.IBaseMvpView;

/**
 * Created by jiazhen on 2018/8/21.
 */
public class SelfSelectionEditContract {

    public interface IView extends IBaseMvpView {
        void uploadSelfSuccess();

        void uploadSelfError();
    }

    public interface IPresenter {
        void uploadSelfList(String checkJson);
    }
}
