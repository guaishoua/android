package com.android.tacu.module.my.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.main.model.OwnCenterModel;

import java.util.List;

public class EditPersonalDataContact {

    public interface IView extends IBaseMvpView {
        void updateUserInfoSuccess();

        void ownCenter(OwnCenterModel model);

        void selectBank(List<PayInfoModel> list);

        void getOssSetting(AuthOssModel model, String fileLocalNameAddress);
    }

    public interface IPresenter {
        void updateUserInfo(String nickname, String headImg);

        void ownCenter();

        void selectBank();

        void getOssSetting(String fileLocalNameAddress);
    }
}
