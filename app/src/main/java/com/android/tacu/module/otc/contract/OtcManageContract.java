package com.android.tacu.module.otc.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.main.model.OwnCenterModel;

public class OtcManageContract {

    public interface IView extends IBaseMvpView {
        void ownCenter(OwnCenterModel model);
    }

    public interface IChildView extends IBaseMvpView {

    }

    public interface IPresenter {
        void ownCenter();
    }
}
