package com.android.tacu.api;

import com.android.tacu.base.BaseModel;
import com.android.tacu.module.assets.model.AmountModel;
import com.android.tacu.module.assets.model.AssetDetailsModel;
import com.android.tacu.module.assets.model.AuthOssModel;
import com.android.tacu.module.assets.model.ChargeModel;
import com.android.tacu.module.assets.model.CoinAddressModel;
import com.android.tacu.module.assets.model.MoneyFlowModel;
import com.android.tacu.module.assets.model.OtcAmountModel;
import com.android.tacu.module.assets.model.PayInfoModel;
import com.android.tacu.module.assets.model.SelectTakeCoinAddressModel;
import com.android.tacu.module.assets.model.TakeCoinListModel;
import com.android.tacu.module.auth.model.SelectAuthLevelModel;
import com.android.tacu.module.auth.model.UserInfoModel;
import com.android.tacu.module.main.model.ConvertModel;
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
import com.android.tacu.module.auctionplus.modal.AuctionPayStatusModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusDataBaseModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusListByIdModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusListModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusPayInfoModel;
import com.android.tacu.module.auctionplus.modal.AuctionPlusWinLisyModel;
import com.android.tacu.module.vip.model.VipDetailModel;
import com.android.tacu.module.vip.model.VipDetailRankModel;

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
            @Field("vercode") String vercode
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
            @Field("currencyId") Integer currencyId
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
            @Field("isAllTime") int isAllTime
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
     * AuctionPlus的列表数据(全部 进行中 未开始 已结束)
     */
    @FormUrlEncoded
    @POST("list")
    Observable<BaseModel<AuctionPlusListModel>> auctionPlusList(
            @Field("start") int start,
            @Field("size") int size,
            @Field("status") int status //0=全部 1=进行中 2=未开始 3=已结束
    );

    /**
     * Auction的列表数据(收藏 围观 参与)
     */
    @FormUrlEncoded
    @POST("listByType")
    Observable<BaseModel<AuctionPlusListModel>> auctionPlusListByType(
            @Field("start") int start,
            @Field("size") int size,
            @Field("type") int type //1=收藏 2=围观 3=参与
    );

    /**
     * Auction的列表数据待支付
     */
    @FormUrlEncoded
    @POST("payWait")
    Observable<BaseModel<AuctionPlusListModel>> auctionPlusWait(
            @Field("start") int start,
            @Field("size") int size
    );

    /**
     * 轮询进行中和未开始的数据
     */
    @FormUrlEncoded
    @POST("data")
    Observable<BaseModel<AuctionPlusDataBaseModel>> auctionPlusData(
            @Field("ids") String ids
    );

    /**
     * 获取已完成状态的活动的支付状态
     */
    @FormUrlEncoded
    @POST("listpay")
    Observable<BaseModel<List<AuctionPayStatusModel>>> auctionPluslistpay(
            @Field("ids") String ids
    );

    /**
     * 查询明细
     */
    @FormUrlEncoded
    @POST("listById")
    Observable<BaseModel<AuctionPlusListByIdModel>> listPlusById(
            @Field("start") int start,
            @Field("size") int size,
            @Field("id") String id
    );

    /**
     * plus支付弹窗
     */
    @FormUrlEncoded
    @POST("payInfo")
    Observable<BaseModel<AuctionPlusPayInfoModel>> payInfo(
            @Field("id") String id
    );

    /**
     * 支付
     */
    @FormUrlEncoded
    @POST("pay")
    Observable<BaseModel> auctionPlusPay(
            @Field("id") String id,//type为0时代表标的id，type为1时代表获胜记录的id
            @Field("type") int type//0 支付整个标的 1 支付单个出价
    );

    /**
     * plus获胜记录
     */
    @FormUrlEncoded
    @POST("winLisy")
    Observable<BaseModel<AuctionPlusWinLisyModel>> plusWinLisy(
            @Field("start") int start,
            @Field("size") int size,
            @Field("payStatus") int payStatus,//1待支付 2已支付 3 支付过期 4 全部
            @Field("currency") int currency,
            @Field("beginTime") String beginTime,
            @Field("endTime") String endTime
    );

    /**
     * 是否收藏
     */
    @FormUrlEncoded
    @POST("collectCheck")
    Observable<BaseModel> collectCheck(
            @Field("id") String id
    );

    /**
     * 收藏
     */
    @FormUrlEncoded
    @POST("collect")
    Observable<BaseModel> auctionCollect(
            @Field("id") String id
    );

    /**
     * Auction出价
     */
    @FormUrlEncoded
    @POST("buy")
    Observable<BaseModel> auctionBuy(
            @Field("id") String id,
            @Field("count") String count
    );

    /**
     * 昵称头像
     */
    @FormUrlEncoded
    @POST("updateUserInfo")
    Observable<BaseModel> updateUserInfo(
            @Field("nickname") String nickname,
            @Field("headImg") String headImg
    );

    /**
     * 添加银行卡，微信，支付宝等支付渠道
     */
    @FormUrlEncoded
    @POST("insertBank")
    Observable<BaseModel> insertBank(
            @Field("type") Integer type, //1.银行卡 2.微信 3.支付宝
            @Field("bankName") String bankName,// 银行名称
            @Field("openBankName") String openBankName,//开户行名称
            @Field("openBankAdress") String openBankAdress,//开户行地址
            @Field("bankCard") String bankCard,// 卡号
            @Field("weChatNo") String weChatNo,//微信名
            @Field("weChatImg") String weChatImg,//收款码
            @Field("aliPayNo") String aliPayNo,//支付宝账号
            @Field("aliPayImg") String aliPayImg,//收款码
            @Field("fdPassword") String fdPassword//交易密码
    );

    /**
     * 查询银行卡等支付渠道
     */
    @POST("selectBank")
    Observable<BaseModel<List<PayInfoModel>>> selectBank();

    /**
     * 删除银行卡等支付渠道
     */
    @FormUrlEncoded
    @POST("deleteBank")
    Observable<BaseModel> deleteBank(
            @Field("id") Integer id
    );

    /**
     * 获取上传过的敏感图片的完整地址
     * 比如支付宝二维码，微信二维码
     */
    @FormUrlEncoded
    @POST("uselectUserInfo")
    Observable<BaseModel<String>> uselectUserInfo(
            @Field("headImg") String headImg
    );

    /**
     * 购买会员
     */
    @FormUrlEncoded
    @POST("buyVip")
    Observable<BaseModel> buyVip(
            @Field("type") Integer type, //1月度会员(30天)  2年度会员(12月) 3连续包年
            @Field("fdPassword") String fdPassword//交易密码
    );

    /**
     * 查看会员购买详情
     */
    @POST("selectVip")
    Observable<BaseModel<VipDetailModel>> selectVip();

    /**
     * vip各级别价位
     */
    @POST("selectVipDetail")
    Observable<BaseModel<List<VipDetailRankModel>>> selectVipDetail();

    /**
     * 取消自动续费
     */
    @POST("cancleVip")
    Observable<BaseModel> cancleVip();


    /**
     * 币币账号划转到OTC
     */
    @FormUrlEncoded
    @POST("CcToOtc")
    Observable<BaseModel> CcToOtc(
            @Field("amount") String amount,
            @Field("currencyId") Integer currencyId
    );

    /**
     * otc划转到币币账户
     */
    @FormUrlEncoded
    @POST("OtcToCC")
    Observable<BaseModel> OtcToCC(
            @Field("amount") String amount,
            @Field("currencyId") Integer currencyId
    );

    /**
     * 查看otc余额
     */
    @FormUrlEncoded
    @POST("OtcAccount")
    Observable<BaseModel<OtcAmountModel>> OtcAccount(
            @Field("currencyId") Integer currencyId
    );

    /**
     * 查看保证金余额
     */
    @FormUrlEncoded
    @POST("BondAccount")
    Observable<BaseModel<OtcAmountModel>> BondAccount(
            @Field("currencyId") Integer currencyId
    );
}
