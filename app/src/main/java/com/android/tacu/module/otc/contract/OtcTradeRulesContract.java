package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;

public class OtcTradeRulesContract {

    public interface IView extends IBaseMvpView {
        void disclaimerSuccess();
    }

    public interface IPresenter {
        void disclaimer();
    }
}
