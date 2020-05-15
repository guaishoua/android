package com.android.tacu.module.auction.presenter;

import com.android.tacu.api.Api;
import com.android.tacu.api.ApiHost;
import com.android.tacu.base.BaseModel;
import com.android.tacu.base.BaseMvpPresenter;
import com.android.tacu.base.IBaseMvpView;
import com.android.tacu.http.factory.APIServiceFactory;
import com.android.tacu.http.network.NetDisposableObserver;
import com.android.tacu.module.auction.contract.MyAddressContract;
import com.android.tacu.module.auction.model.AddressModel;
import com.android.tacu.module.auction.model.AreaModel;
import com.android.tacu.module.auction.model.CityModel;
import com.android.tacu.module.auction.model.ProvinceModel;

import java.util.List;

public class MyAddressPresenter extends BaseMvpPresenter implements MyAddressContract.IPresenter {

    @Override
    public void getProvince() {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USEADDRESS, Api.class).getProvince(), new NetDisposableObserver<BaseModel<List<ProvinceModel>>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<List<ProvinceModel>> model) {
                MyAddressContract.IView view = (MyAddressContract.IView) getView();
                view.getProvince(model.attachment);
            }
        });
    }

    @Override
    public void getCity(String province) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USEADDRESS, Api.class).getCity(province), new NetDisposableObserver<BaseModel<List<CityModel>>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<List<CityModel>> model) {
                MyAddressContract.IView view = (MyAddressContract.IView) getView();
                view.getCity(model.attachment);
            }
        });
    }

    @Override
    public void getArea(String city) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USEADDRESS, Api.class).getArea(city), new NetDisposableObserver<BaseModel<List<AreaModel>>>((IBaseMvpView) getView(), false) {
            @Override
            public void onNext(BaseModel<List<AreaModel>> model) {
                MyAddressContract.IView view = (MyAddressContract.IView) getView();
                view.getArea(model.attachment);
            }
        });
    }

    @Override
    public void insertAddress(String consignee, String phone, String provinceCode, String provinceName, String cityCode, String cityName, String areaCode, String areaName, String detailedAddress, int status) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USEADDRESS, Api.class).insertAddress(consignee, phone, provinceCode, provinceName, cityCode, cityName, areaCode, areaName, detailedAddress, status), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                MyAddressContract.IView view = (MyAddressContract.IView) getView();
                view.insertAddressSuccess();
            }
        });
    }

    /**
     * @param type 1=仅仅调用  2=需要掉用chooseAddr接口
     */
    @Override
    public void getAddressList(final int type) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.USEADDRESS, Api.class).getAddressList(), new NetDisposableObserver<BaseModel<List<AddressModel>>>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel<List<AddressModel>> model) {
                MyAddressContract.IView view = (MyAddressContract.IView) getView();
                view.getAddressList(type, model.attachment);
            }
        });
    }

    @Override
    public void chooseAddr(Integer id, String addrId) {
        this.subscribeNetwork(APIServiceFactory.createAPIService(ApiHost.AUCTIONNEW, Api.class).chooseAddr(id, addrId), new NetDisposableObserver<BaseModel>((IBaseMvpView) getView()) {
            @Override
            public void onNext(BaseModel model) {
                MyAddressContract.IView view = (MyAddressContract.IView) getView();
                view.chooseAddrSuccess();
            }
        });
    }
}
