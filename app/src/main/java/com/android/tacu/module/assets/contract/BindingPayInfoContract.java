package com.android.tacu.module.assets.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.assets.model.PayInfoModel;

import java.util.List;

public class BindingPayInfoContract {

    public interface IView extends IBaseMvpView {
        void selectBank(List<PayInfoModel> list);
    }

    public interface IYhkView extends IBaseMvpView {
        void insertBankSuccess();

        void deleteBankSuccess();
    }

    public interface IWxView extends IBaseMvpView {
        void insertBankSuccess();

        void deleteBankSuccess();

        void getOssSetting(AuthOssModel model, String fileLocalNameAddress);
    }

    public interface IZfbView extends IBaseMvpView {
        void insertBankSuccess();

        void deleteBankSuccess();

        void getOssSetting(AuthOssModel model, String fileLocalNameAddress);
    }

    public interface IPresenter {
        void selectBank();

        void insertBank(Integer type, String bankName, String openBankName, String openBankAdress, String bankCard, String weChatNo, String weChatImg, String aliPayNo, String aliPayImg);

        void deleteBank(Integer type, Integer id);

        void getOssSetting(Integer type, String fileLocalNameAddress);
    }
}
