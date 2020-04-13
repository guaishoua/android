package com.android.tacu.module.vip.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.vip.model.BondRecordModel;
import com.android.tacu.module.vip.model.SelectBondModel;

import java.util.List;

public class RechargeDepositContract {

    public interface IView extends IBaseMvpView {
        void customerCoinByOneCoin(Double value);

        void BondAccount(OtcAmountModel model);

        void otcAmount(OtcAmountModel model);

        void c2cAmount(OtcAmountModel model);

        void selectBond(List<SelectBondModel> list);

        void CcToBondSuccess();

        void BondToCcSuccess();

        void otcToBondSuccess();

        void BondToOtcSuccess();

        void c2cToBondSuccess();

        void BondToC2cSuccess();
    }

    public interface IRecordView extends IBaseMvpView {
        void selectBondRecord(List<BondRecordModel> list);

        void cancelBondRecordSuccess();
    }

    public interface IPresenter {
        void customerCoinByOneCoin(boolean isShowView, int currencyId);

        void BondAccount(boolean isShowView, int currencyId);

        void otcAmount(boolean isShowView, int currencyId);

        void c2cAmount(boolean isShowView, int currencyId);

        void selectBond();

        void CcToBond(String amount, int currencyId, String fdPassword);

        void BondToCc(String amount, int currencyId, String fdPassword);

        void otcToBond(String amount, int currencyId, String fdPassword);

        void bondToOtc(String amount, int currencyId, String fdPassword);

        void c2cToBond(String amount, int currencyId, String fdPassword);

        void bondToC2c(String amount, int currencyId, String fdPassword);

        void selectBondRecord(boolean isShowview);

        void cancelBondRecord(String id);
    }
}
