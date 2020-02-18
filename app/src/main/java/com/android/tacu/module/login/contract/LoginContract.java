package com.android.tacu.module.login.contract;

import com.android.tacu.base.BaseModel;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.login.model.LoginModel;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.market.model.SelfModel;

import java.util.List;

/**
 * Created by jiazhen on 2018/8/14.
 */
public class LoginContract {

    public interface IView extends IBaseMvpView {
        void showContent(BaseModel<LoginModel> model);

        void ownCenterSuccess(OwnCenterModel model);

        void ownCenterError();

        void getSelfSelectionValueSuccess(SelfModel selfModel);

        void getSelfSelectionValueError();

        void selectBankSuccess(List<PayInfoModel> list);

        void selectBankError();
    }

    public interface IPresenter {
        void login(String email, String pwd, String token);

        void ownCenter();

        void getSelfList();

        void selectBank();
    }
}
