package com.android.tacu.module.auction.contract;

import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.module.auction.model.AddressModel;
import com.android.tacu.module.auction.model.AreaModel;
import com.android.tacu.module.auction.model.CityModel;
import com.android.tacu.module.auction.model.ProvinceModel;

import java.util.List;

public class MyAddressContract {

    public interface IView extends IBaseMvpView {
        void getProvince(List<ProvinceModel> list);

        void getCity(List<CityModel> list);

        void getArea(List<AreaModel> list);

        void insertAddressSuccess();

        void getAddressList(int type, List<AddressModel> list);

        void chooseAddrSuccess();
    }

    public interface IPresenter {
        void getProvince();

        void getCity(String province);

        void getArea(String city);

        void insertAddress(String consignee,
                           String phone,
                           String provinceCode,
                           String provinceName,
                           String cityCode,
                           String cityName,
                           String areaCode,
                           String areaName,
                           String detailedAddress,
                           int status);

        void getAddressList(int type);

        void chooseAddr(Integer id, String addrId);
    }
}
