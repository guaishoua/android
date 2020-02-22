package com.android.tacu.module.vip.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.AmountModel;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.vip.model.BondRecordModel;

import java.util.List;

public class RechargeDepositContract {

    public interface IView extends IBaseMvpView {
        void customerCoinByOneCoin(AmountModel model);

        void BondAccount(OtcAmountModel model);

        void CcToBondSuccess();

        void BondToCcSuccess();

        void selectBondRecord(List<BondRecordModel> list);
    }

    public interface IPresenter {
        void customerCoinByOneCoin(int currencyId);

        void BondAccount(int currencyId);

        void CcToBond(String amount, int currencyId, String fdPassword);

        void BondToCc(String amount, int currencyId, String fdPassword);

        void selectBondRecord();
    }
}
