package com.android.tacu.module.assets.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.TransferInfo;
import com.android.tacu.module.webview.model.EPayParam;

/**
 * Created by xiaohong on 2018/8/21.
 */

public class TransferRecordContract {
    public interface ITransferView extends IBaseMvpView {
        void success();

        void addAccount();

        void customerCoinByOneCoin(TransferInfo model);

        void transIn(EPayParam ePayParam);

        void transOut();
    }

    public interface IPresenter {
        void emailTakeCoin(int type);

        void customerCoinByOneCoin(int currencyId);

        void addAccount(String epayAccount, String memo);

        void transIn(int currencyId, String currencyName, String transNum);

        void transOut(String amount, String transAccount, int currencyId, String currencyName, String fdPassword, String sAuthCode, String gAuthCode);
    }
}
