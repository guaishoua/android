package com.android.tacu.api;

import com.android.tacu.base.BaseModel;
import com.android.tacu.module.assets.model.AmountModel;
import com.android.tacu.module.assets.model.AssetDetailsModel;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.assets.model.ChargeModel;
import com.android.tacu.module.assets.model.CoinAddressModel;
import com.android.tacu.module.assets.model.ExchangListModel;
import com.android.tacu.module.assets.model.ExchangeModel;
import com.android.tacu.module.assets.model.ExchangeShowModel;
import com.android.tacu.module.assets.model.MoneyFlowModel;
import com.android.tacu.module.assets.model.SelectTakeCoinAddressModel;
import com.android.tacu.module.assets.model.TakeCoinListModel;
import com.android.tacu.module.assets.model.TransInfoCoinModal;
import com.android.tacu.module.assets.model.TransferInfo;
import com.android.tacu.module.assets.model.TransferRecordModel;
import com.android.tacu.module.auth.model.AliModel;
import com.android.tacu.module.auth.model.AwsModel;
import com.android.tacu.module.auth.model.SelectAuthLevelModel;
import com.android.tacu.module.auth.model.UserInfoModel;
import com.android.tacu.module.main.model.ConvertModel;
import com.android.tacu.module.main.model.UuexSignModal;
import com.android.tacu.module.main.model.GoogleAuthModel;
import com.android.tacu.module.main.model.HomeModel;
import com.android.tacu.module.main.model.OwnCenterModel;
import com.android.tacu.module.main.model.UploadModel;
import com.android.tacu.module.login.model.LoginModel;
import com.android.tacu.module.market.model.KLineModel;
import com.android.tacu.module.market.model.NoticeModel;
import com.android.tacu.module.market.model.SelfModel;
import com.android.tacu.module.assets.model.CoinListModel;
import com.android.tacu.module.my.model.InvitedAllModel;
import com.android.tacu.module.my.model.InvitedInfoModel;
import com.android.tacu.module.transaction.model.DealDetailsModel;
import com.android.tacu.module.transaction.model.ShowOrderListModel;
import com.android.tacu.module.transaction.model.ShowTradeListModel;
import com.android.tacu.module.webview.model.EPayParam;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;


public interface Api {

    @Streaming
    @GET
    Observable<ResponseBody> downLoadFile(@NonNull @Url String url);

    /**
     * 应用更新
     */
    @POST("select")
    Observable<BaseModel<UploadModel>> update();

    /**
     * 首页接口
     */
    @POST("showChooseV3")
    Observable<BaseModel<HomeModel>> getHome();

    /**
     * 汇率
     */
    @POST("convertList")
    Observable<BaseModel<ConvertModel>> getConvertList();

    /**
     * 1.查询当前账户是否开启谷歌认证
     * 2.获取用户认证信息
     * 3.查询是否启用交易密码
     * 这三个接口合并成为一个接口 防止服务器压力大 但是单独的接口也能用
     */
    @POST("ownCenterApp")
    Observable<BaseModel<OwnCenterModel>> ownCenter();

    /**
     * 用户登录
     */
    @FormUrlEncoded
    @POST("loginGAFirstV2")
    Observable<BaseModel<LoginModel>> login(
            @Field("email") String email,
            @Field("pwd") String pwd,
            @Field("sliderToken") String sliderToken);

    /**
     * 用户登录第二步
     */
    @FormUrlEncoded
    @POST("loginGASecond")
    Observable<BaseModel<LoginModel>> loginGASecond(
            @Field("clientPassword") String clientPassword,
            @Field("email") String email);

    /**
     * 找回密码发送邮件
     */
    @FormUrlEncoded
    @POST("sendMailV2")
    Observable<BaseModel> sendMail(
            @Field("phoneCode") String phoneCode,
            @Field("email") String email,
            @Field("sliderToken") String sliderToken);

    /**
     * 找回密码 修改密码
     */
    @FormUrlEncoded
    @POST("resetPwdV2")
    Observable<BaseModel> resetPwd(
            @Field("phoneCode") String phoneCode,
            @Field("email") String email,
            @Field("newPwd") String newPwd,
            @Field("vercode") String vercode);

    /**
     * 注册前发送邮箱
     */
    @FormUrlEncoded
    @POST("sendEmailForRegisterV3")
    Observable<BaseModel> sendEmailForRegister(
            @Field("phoneCode") String phoneCode,
            @Field("email") String email,
            @Field("sliderToken") String sliderToken);

    /**
     * 用戶註冊
     */
    @FormUrlEncoded
    @POST("registerV3")
    Observable<BaseModel> register(
            @Field("phoneCode") String phoneCode,
            @Field("email") String email,
            @Field("pwd") String pwd,
            @Field("vercode") String vercode,
            @Field("inviteId") String inviteId
    );

    /**
     * 获取服务器自选列表
     */
    @POST("showChoose")
    Observable<BaseModel<SelfModel>> getSelfList();

    /**
     * 查询用户基本信息
     *
     * @return
     */
    @POST("customerCoinAccount2")
    Observable<BaseModel<AssetDetailsModel>> getAsAsets();

    /**
     * 获取充币地址
     *
     * @return
     */
    @FormUrlEncoded
    @POST("selectUserAddress")
    Observable<BaseModel<CoinAddressModel>> selectUserAddress(
            @Field("walletType") int walletType,
            @Field("currentyId") int currentyId);

    /**
     * 获取币种列表
     *
     * @return
     */
    @POST("coins")
    Observable<CoinListModel> coins();

    /**
     * 获取当前币种可用数量
     */
    @FormUrlEncoded
    @POST("customerCoinByOneCoin")
    Observable<AmountModel> customerCoinByOneCoin(
            @Field("currencyId") int currencyId
    );

    /**
     * 上传本地自选列表
     * type:3代表安卓
     */
    @FormUrlEncoded
    @POST("ownChoose")
    Observable<BaseModel> uploadSelfList(
            @Field("checkJson") String checkJson,
            @Field("type") int type);

    /**
     * 公告
     * type= 1重要公告 2活动公告 3新币上线 4全部类型
     */
    @FormUrlEncoded
    @POST("article")
    Observable<BaseModel<List<NoticeModel>>> getNoticeInfo(
            @Field("page") int page,
            @Field("perPage") int perPage,
            @Field("type") int type
    );

    /**
     * 下委托单
     *
     * @param buyOrSell  1：买；2：卖
     * @param currencyId 货币ID
     * @param fdPassword 根据配置设置，允许传空
     * @param num
     * @param price      市价传空
     * @param type       1=限价   2=市价
     */
    @FormUrlEncoded
    @POST("order")
    Observable<BaseModel> order(
            @Field("buyOrSell") int buyOrSell,
            @Field("currencyId") int currencyId,
            @Field("fdPassword") String fdPassword,
            @Field("num") String num,
            @Field("price") String price,
            @Field("type") int type,
            @Field("baseCurrencyId") int baseCurrencyId);

    /**
     * 撤销委托单
     *
     * @return
     */
    @FormUrlEncoded
    @POST("cancel/v2")
    Observable<BaseModel> cancel(
            @Field("orderNo") String orderNo,//委托单号
            @Field("fdPassword") String fdPassword,//密码
            @Field("enabledFdPassword") Integer enabledFdPassword);//快速撤单 web端用的

    /**
     * 批量撤销委托单
     */
    @FormUrlEncoded
    @POST("batchCancel")
    Observable<BaseModel> cancelList(
            @Field("orderNoList") String orderNoList,
            @Field("fdPassword") String fdPassword,
            @Field("enabledFdPassword") Integer enabledFdPassword,
            @Field("selectedParams") String selectedParams);

    /**
     * 当前委托／历史委托
     * status : 0=未成交 1=部分成交 0,1=当前委托 2=全部成交 4=已撤单 5=部分成交后撤单 2,4,5=历史委托全部
     * priceType : 0=按照交易号降序 3=按照成交价格降序 4=按照成交价格升序
     */
    @FormUrlEncoded
    @POST("trOrderListByCustomer/v2")
    Observable<BaseModel<ShowOrderListModel>> trOrderListByCustomer(
            @Field("beginTime") String beginTime,//开始日期
            @Field("endTime") String endTime,//結束日期
            @Field("start") Integer start,//起点  默认1，最大999
            @Field("size") Integer size,//数量
            @Field("status") String status,//委托单状态
            @Field("buyOrSell") int buyOrSell,//买卖方向 0=全部 1=买 2=卖
            @Field("currencyId") Integer currencyId,
            @Field("baseCurrencyId") Integer baseCurrencyId,
            @Field("priceType") Integer priceType);//价格类型

    /**
     * 成交记录
     * priceType : 0=按照交易号降序 3=按照成交价格降序 4=按照成交价格升序
     */
    @FormUrlEncoded
    @POST("trTradeListByCustomer/v2")
    Observable<BaseModel<ShowTradeListModel>> trTradeListByCustomer(
            @Field("beginTime") String beginTime,//开始日期
            @Field("endTime") String endTime,//結束日期
            @Field("start") Integer start,//起点  默认1，最大999
            @Field("size") Integer size,//数量
            @Field("orderNo") String orderNo,//委托单状态
            @Field("buyOrSell") int buyOrSell,//买卖方向 0=全部 1=买 2=卖
            @Field("currencyId") Integer currencyId,
            @Field("baseCurrencyId") Integer baseCurrencyId,
            @Field("priceType") Integer priceType);//价格类型

    /**
     * 委託詳情
     */
    @FormUrlEncoded
    @POST("orderTradeDetail")
    Observable<BaseModel<List<DealDetailsModel>>> getDealDetail(
            @Field("orderNo") String orderNo);

    /**
     * 邀请好友
     */
    @GET("get_register_time/{uid}")
    Observable<InvitedInfoModel> getInvitedInfo(@Path("uid") int uid);

    /**
     * 邀请好友
     */
    @FormUrlEncoded
    @POST("invitedAll")
    Observable<BaseModel<InvitedAllModel>> invitedAll(
            @Field("page") Integer page,
            @Field("size") Integer size
    );

    /**
     * K线
     */
    @FormUrlEncoded
    @POST("query")
    Observable<BaseModel<KLineModel>> getKline(
            @Field("symbol") String symbol,
            @Field("range") long range);

    /**
     * 选择提币地址
     *
     * @return
     */
    @FormUrlEncoded
    @POST("selectTakeCoin")
    Observable<BaseModel<SelectTakeCoinAddressModel>> selectTakeCoin(
            @Field("currencyId") int currencyId);

    /**
     * 删除提币地址
     *
     * @return
     */
    @FormUrlEncoded
    @POST("updateCoinAddress")
    Observable<BaseModel> updateCoinAddress(
            @Field("currencyId") int currencyId,
            @Field("walletAddressId") String walletAddressId);

    /**
     * 添加提币地址
     *
     * @return
     */
    @FormUrlEncoded
    @POST("insertTakeAddress")
    Observable<BaseModel> insertTakeAddress(
            @Field("address") String address,
            @Field("currencyId") int currencyId,
            @Field("note") String note);

    /**
     * 提幣发送邮箱验证码
     *
     * @return
     */
    @FormUrlEncoded
    @POST("emailTakeCoinV2")
    Observable<BaseModel> emailTakeCoin(
            @Field("type") int type);

    /**
     * 提币
     *
     * @return
     */
    @FormUrlEncoded
    @POST("takeCoinV2")
    Observable<BaseModel> takeCoin(
            @Field("actionId") String actionId,
            @Field("address") String address,
            @Field("amount") String amount,
            @Field("currencyId") int currencyId,
            @Field("fdPwd") String fdPwd,
            @Field("note") String note,
            @Field("emailCode") String emailCode,
            @Field("gAuth") String gAuth,
            @Field("msgCode") String msgCode);

    /**
     * 获取USDT实时价格
     */
    @FormUrlEncoded
    @POST("message")
    Observable<BaseModel<ExchangeModel>> getUSDT(
            @Field("currencyId") int currencyId
    );

    /**
     * 二次确认展示接口
     */
    @FormUrlEncoded
    @POST("confirmMessage")
    Observable<BaseModel<ExchangeShowModel>> confirmMessage(
            @Field("fdPwd") String fdPwd,
            @Field("currencyId") String currencyId,
            @Field("amount") String amount);

    /**
     * 二次确认,确定按钮
     */
    @FormUrlEncoded
    @POST("exchangeCoinUSDTToCode")
    Observable<BaseModel> exchangeCoinUSDTToCode(
            @Field("fdPwd") String fdPwd,
            @Field("currencyId") String currencyId,
            @Field("amount") String amount);

    /**
     * 用户认证状态查询
     *
     * @return
     */
    @POST("selectAuthLevel")
    Observable<BaseModel<SelectAuthLevelModel>> selectAuthLevel();

    /**
     * 用户注销
     */
    @POST("logout")
    Observable<BaseModel> logout();

    /**
     * 获取密钥
     * 此接口只能在启用ga前调用返回数据 否则不返回。
     */
    @POST("getSecretKey")
    Observable<BaseModel<GoogleAuthModel>> getSecretKey();

    /**
     * 谷歌验证发送验证码
     */
    @FormUrlEncoded
    @POST("bindGoogleSendMsgV3")
    Observable<BaseModel> bingGaSendMsg(
            @Field("type") int type,
            @Field("sliderToken") String sliderToken
    );

    /**
     * 绑定GA
     */
    @FormUrlEncoded
    @POST("bindGoogleAuthV3")
    Observable<BaseModel> bindGoogleAuth(
            @Field("clientPassword") String clientPassword,
            @Field("loginPassword") String loginPassword,
            @Field("vercode") String vercode,
            @Field("type") int type
    );

    /**
     * 关闭ga
     *
     * @return
     */
    @FormUrlEncoded
    @POST("closeGoogleAuthV3")
    Observable<BaseModel> closeGoogleAuth(
            @Field("clientPassword") String clientPassword,
            @Field("loginPassword") String loginPassword,
            @Field("vercode") String vercode,
            @Field("type") int type
    );


    /**
     * 获取oss阿里云的配置
     */
    @POST("getsts")
    Observable<BaseModel<AuthOssModel>> getSts();

    /**
     * 启用、不启用交易密码
     *
     * @param enabled
     * @param pwd
     * @return
     */
    @FormUrlEncoded
    @POST("updateFdPwdEnabled")
    Observable<BaseModel> updateFdPwdEnabled(
            @Field("enabled") int enabled,
            @Field("fdPwd") String pwd
    );

    /**
     * 修改密码
     *
     * @param newPwd
     * @param pwd
     * @return
     */
    @FormUrlEncoded
    @POST("resetPwdInUserCenterV3")
    Observable<BaseModel> resetPwdInUserCenter(
            @Field("newPwd") String newPwd,
            @Field("oldPwd") String pwd
    );

    /**
     * 更换绑定手机号
     *
     * @param code
     * @param newPhone 新手机号
     * @param oldPhone 旧手机号
     * @return
     */
    @FormUrlEncoded
    @POST("bindPhoneV3")
    Observable<BaseModel<Object>> bindPhone(
            @Field("phoneCode") String phoneCode,
            @Field("vercode") String code,
            @Field("oldVercode") String oldVercode,
            @Field("newPhone") String newPhone,
            @Field("oldPhone") String oldPhone
    );

    /**
     * 绑定交易密码
     *
     * @return
     */
    @FormUrlEncoded
    @POST("bindFdPwdV3")
    Observable<BaseModel<Object>> bindFdpwd(
            @Field("newFdPassWord") String newFdPassWord,
            @Field("oldFdPassWord") String oldFdPassWord,
            @Field("loginPassWord") String loginPassWord
    );

    /**
     * 资金流水
     */
    @FormUrlEncoded
    @POST("coinWater")
    Observable<BaseModel<MoneyFlowModel>> selectTakeList(
            @Field("start") int start,
            @Field("size") int size,
            @Field("type") String type,
            @Field("currencyId") Integer currencyId,
            @Field("beginTime") String beginTime,
            @Field("endTime") String endTime
    );

    /**
     * 充币记录
     */
    @FormUrlEncoded
    @POST("selectListByUuid")
    Observable<BaseModel<ChargeModel>> selectListByUuid(
            @Field("beginTime") String beginTime,
            @Field("endTime") String endTime,
            @Field("size") int size,
            @Field("currencyId") Integer currencyId,
            @Field("start") int start,
            @Field("status") int status
    );

    /**
     * 提币记录
     */
    @FormUrlEncoded
    @POST("selectTakeList")
    Observable<BaseModel<TakeCoinListModel>> takeList(
            @Field("beginTime") String beginTime,
            @Field("endTime") String endTime,
            @Field("size") int size,
            @Field("currentyId") Integer currencyId,
            @Field("start") int start,
            @Field("status") int status
    );

    /**
     * 兑换记录
     */
    @FormUrlEncoded
    @POST("exchangelistApp")
    Observable<BaseModel<ExchangListModel>> exchangelist(
            @Field("beginTime") String beginTime,
            @Field("endTime") String endTime,
            @Field("size") int size,
            @Field("currencyId") Integer currencyId,
            @Field("start") int start,
            @Field("status") Integer status
    );

    /**
     * 划转记录
     */
    @FormUrlEncoded
    @POST("transHistroy")
    Observable<BaseModel<TransferRecordModel>> transferList(
            @Field("pageIndex") int start,
            @Field("pageSize") int size,
            @Field("beginTime") String beginTime,
            @Field("endTime") String endTime
    );

    /**
     * 获取实名认证信息
     */
    @POST("authinfoNew")
    Observable<BaseModel<UserInfoModel>> authinfoNew();

    /**
     * 实名认证提交基本信息
     * isAllTime 0：不是长期有效  1：长期有效
     */
    @FormUrlEncoded
    @POST("authnew")
    Observable<BaseModel> authnew(
            @Field("country") String country,
            @Field("firstName") String firstName,
            @Field("secondName") String secondName,
            @Field("idNumber") String idNumber,
            @Field("birthday") String birthday,
            @Field("gender") String gender,
            @Field("isChina") String isChina,
            @Field("step") int step,
            @Field("singnTime") String singnTime,
            @Field("outofTime") String outofTime,
            @Field("isAllTime") int isAllTime,
            @Field("currentLocation") int currentLocation
    );

    /**
     * 实名认证提交图片正面
     */
    @FormUrlEncoded
    @POST("authnew")
    Observable<BaseModel> authnewPositive(
            @Field("positiveImages") String positiveImages,
            @Field("isChina") String isChina,
            @Field("step") int step
    );

    /**
     * 实名认证提交图片背面
     */
    @FormUrlEncoded
    @POST("authnew")
    Observable<BaseModel> authnewOpposite(
            @Field("oppositeImages") String oppositeImages,
            @Field("isChina") String isChina,
            @Field("step") int step
    );

    /**
     * 实名认证提交手持证件照
     */
    @FormUrlEncoded
    @POST("authnew")
    Observable<BaseModel> authnewHand(
            @Field("handImages") String handImages,
            @Field("isChina") String isChina,
            @Field("step") int step
    );

    /**
     * 设置交易密码发送验证码
     * type  //1.新手机 2.新邮箱 3. 原手机  4. 原邮箱(3/4不传phone)
     *
     * @return
     */
    @FormUrlEncoded
    @POST("bindPhoneSendMsgV3")
    Observable<BaseModel> bindPhoneSendMsg(
            @Field("phoneCode") String phoneCode,
            @Field("phone") String phone,
            @Field("type") int type,
            @Field("sliderToken") String sliderToken
    );

    /**
     * 设置密码前确认验证码
     * type //3.手机号 4.邮箱
     *
     * @return
     */
    @FormUrlEncoded
    @POST("validCodeV3")
    Observable<BaseModel> validCode(
            @Field("type") int type,
            @Field("vercode") String vercode
    );

    /**
     * 获取epay是否开启
     */
    @FormUrlEncoded
    @POST("transInfo")
    Observable<BaseModel<TransferInfo>> transInfo(
            @Field("currencyId") int currencyId
    );

    /**
     * 添加epay账号
     */
    @FormUrlEncoded
    @POST("addAccount")
    Observable<BaseModel> addAccount(
            @Field("account") String account,
            @Field("note") String note
    );

    /**
     * Epay转入
     */
    @FormUrlEncoded
    @POST("transIn")
    Observable<BaseModel<EPayParam>> transIn(
            @Field("currencyId") int currencyId,
            @Field("currencyName") String currencyName,
            @Field("transNum") String transNum
    );

    /**
     * Epay转出
     * actionId:9, // 行为Id，9:表示转出
     */
    @FormUrlEncoded
    @POST("transOut")
    Observable<BaseModel> transOut(
            @Field("actionId") String actionId,
            @Field("amount") String amount,
            @Field("transAccount") String transAccount,
            @Field("currencyId") int currencyId,
            @Field("currencyName") String currencyName,
            @Field("fdPassword") String fdPassword,
            @Field("sAuthCode") String sAuthCode,
            @Field("gAuthCode") String gAuthCode
    );

    /**
     * 运营活动
     * 浮层文字
     */
    @POST("listActivityWords")
    Observable<BaseModel<HashMap<Integer, String>>> listActivityWords();

    /**
     * 查询AWSS3秘钥
     */
    @POST("getKey")
    Observable<BaseModel<AwsModel>> getAwsSetting();

    /**
     * 二維碼掃描
     */
    @FormUrlEncoded
    @POST("scanning")
    Observable<BaseModel> sendZxing(
            @Field("uuid") String uuid
    );

    /**
     * Grin充币
     */
    @FormUrlEncoded
    @POST("rechargeGrin")
    Observable<BaseModel> rechargeGrin(
            @Field("txId") String txId,
            @Field("amount") String amount
    );

    /**
     * UUex获取那些币种可以
     */
    @POST("transInfoCoin")
    Observable<BaseModel<TransInfoCoinModal>> TransInfoCoin();

    /**
     * uuex转出
     */
    @FormUrlEncoded
    @POST("transOut")
    Observable<BaseModel> transOut(
            @Field("amount") String amount,
            @Field("currencyId") int currencyId,
            @Field("actionId") int actionId,
            @Field("fdPassword") String fdPassword,
            @Field("sAuthCode") String sAuthCode,
            @Field("gAuthCode") String gAuthCode
    );

    /**
     * uuex转入
     */
    @FormUrlEncoded
    @POST("transIn")
    Observable<BaseModel> transIn(
            @Field("amount") String amount,
            @Field("currencyId") int currencyId,
            @Field("actionId") int actionId
    );

    /**
     * uuex转入前获取余额
     */
    @FormUrlEncoded
    @POST("coinNum")
    Observable<BaseModel<String>> coinNum(
            @Field("currencyId") int currencyId
    );

    /**
     * uuex获取签名码
     */
    @POST("sign")
    Observable<BaseModel<UuexSignModal>> uuexSign();

    /**
     * 实人认证获取verifyToken
     */
    @POST("appToken")
    Observable<BaseModel<AliModel>> aliToken();

    /**
     * 确认实人认证是否成功
     */
    @POST("vedioAuth")
    Observable<BaseModel> vedioAuth();
}
