package com.android.tacu.http.intercept;

import android.text.TextUtils;

import com.android.tacu.api.Constant;
import com.android.tacu.utils.user.UserInfoUtils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 添加头参数
 * Created by Administrator on 2017/3/21 0021.
 */

public class CommonHeaderInterceptor implements Interceptor {

    public CommonHeaderInterceptor() {
    }

    public Response intercept(Chain chain) throws IOException {
        String token = UserInfoUtils.getInstance().getToken();
        String uid = String.valueOf(UserInfoUtils.getInstance().getUserUid());
        String local = UserInfoUtils.getInstance().getLanguage();

        //如果不是中文 那么就全部让后台返回英文 主要 俄语、法语、越南语、荷兰语、意大利语、西班牙语、德语 这七个国际后台暂时不做国际化 只有前端做
        if (TextUtils.equals(local, Constant.ZH_TW) || TextUtils.equals(local, Constant.ZH_CN)) {
            local = Constant.ZH_TW;
        } else {
            local = Constant.EN_US;
        }

        Request originalRequest = chain.request();
        Request request = null;

        HttpUrl modifiedUrl = originalRequest.url().newBuilder()
                .addQueryParameter("local", local)
                .addQueryParameter("token", token)
                .addQueryParameter("uid", uid)
                .addQueryParameter("source", "3")
                .build();
        request = originalRequest.newBuilder().addHeader("User-Agent", "Android").url(modifiedUrl).build();

        /*//判断请求类型
        if (TextUtils.equals(originalRequest.method(), "POST")) {
            //Post方式
            FormBody.Builder newFormBody = new FormBody.Builder();
            newFormBody.add("local", local)
                    .add("token", token)
                    .add("uid", uid)
                    .add("source", "3");

            if (originalRequest.body() instanceof FormBody) {
                FormBody oldFormBody = (FormBody) originalRequest.body();
                if (oldFormBody != null && oldFormBody.size() > 0) {
                    for (int i = 0; i < oldFormBody.size(); i++) {
                        newFormBody.addEncoded(oldFormBody.encodedName(i), oldFormBody.encodedValue(i));
                    }
                }
            }
            RequestBody requestBody = newFormBody.build();
            request = originalRequest.newBuilder().addHeader("User-Agent", "Android").post(requestBody).build();
        } else {//Get和其他请求方式
            //Get方式
            HttpUrl modifiedUrl = originalRequest.url().newBuilder()
                    .addQueryParameter("local", local)
                    .addQueryParameter("token", token)
                    .addQueryParameter("uid", uid)
                    .addQueryParameter("source", "3")
                    .build();
            request = originalRequest.newBuilder().addHeader("User-Agent", "Android").url(modifiedUrl).build();
        }*/

        return chain.proceed(request);
    }
}
