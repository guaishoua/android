package com.android.tacu.module.login.contract;

import com.android.tacu.base.BaseModel;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.login.model.LoginModel;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.market.model.SelfModel;

/**
 * Created by jiazhen on 2018/8/14.
 */
public class LoginContract {

    public interface IView extends IBaseMvpView {
        void showContent(BaseModel<LoginModel> model);

        void showGaStatus(BaseModel<LoginModel> model);

        void ownCenterSuccess(OwnCenterModel model);

        void ownCenterError();

        void getSelfSelectionValueSuccess(SelfModel selfModel);

        void getSelfSelectionValueError();
    }

    public interface IPresenter {
        void login(String email, String pwd, String token);

        void loginGASecond(String gaPwd, String email);

        void ownCenter();

        void getSelfList();
    }
}
