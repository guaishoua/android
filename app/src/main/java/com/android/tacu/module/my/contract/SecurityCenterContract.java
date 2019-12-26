package com.android.tacu.module.my.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.main.model.OwnCenterModel;

/**
 * Created by jiazhen on 2018/8/27.
 */
public class SecurityCenterContract {

    public interface IView extends IBaseMvpView {
        void ownCenter(OwnCenterModel model);

        void updateFdPwdSuccess();

        void updateFdPwdError();
    }

    public interface IPresenter {
        void ownCenter();

        void updateFdPwdEnabled(int enabled, String fdPwd);
    }
}
