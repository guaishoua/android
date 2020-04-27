package com.android.tacu.module.payinfo.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.payinfo.model.PayInfoModel;

import java.util.List;

public class PayInfoContract {

    public interface IView extends IBaseMvpView {
        void selectBank(List<PayInfoModel> list);
    }

    public interface IDetailView extends IBaseMvpView {
        void insertSuccess();

        void deleteSuccess();

        void getOssSetting(AuthOssModel model, String fileLocalNameAddress);

        void uselectUserInfo(String imageUrl);
    }

    public interface IPresenter {
        void selectBank(boolean isShowView);

        void insert(Integer type, String name, String bankName, String openBankName, String bankCard, String weChatNo, String weChatImg, String aliPayNo, String aliPayImg, String fdPassword);

        void delete(Integer id);

        void getOssSetting(String fileLocalNameAddress);

        void uselectUserInfo(String headImg);
    }
}
