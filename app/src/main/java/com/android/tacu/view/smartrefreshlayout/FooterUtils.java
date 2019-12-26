package com.android.tacu.view.smartrefreshlayout;

import android.text.TextUtils;

import com.android.tacu.api.Constant;
import com.android.tacu.utils.user.UserInfoUtils;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;

/**
 * Created by jiazhen on 2019/6/12.
 */
public class FooterUtils {

    public static void setFooterText() {
        UserInfoUtils spUtil = UserInfoUtils.getInstance();

        if (TextUtils.equals(spUtil.getLanguage(), Constant.ZH_CN) || TextUtils.equals(spUtil.getLanguage(), Constant.ZH_TW)) {
            ClassicsFooter.REFRESH_FOOTER_PULLUP = "上拉加载更多";
            ClassicsFooter.REFRESH_FOOTER_RELEASE = "释放立即加载";
            ClassicsFooter.REFRESH_FOOTER_LOADING = "正在加载...";
            ClassicsFooter.REFRESH_FOOTER_REFRESHING = "正在刷新...";
            ClassicsFooter.REFRESH_FOOTER_FINISH = "加载完成";
            ClassicsFooter.REFRESH_FOOTER_FAILED = "加载失败";
            ClassicsFooter.REFRESH_FOOTER_ALLLOADED = "全部加载完成";
        } else if (TextUtils.equals(spUtil.getLanguage(), Constant.KO_KR)) {
            ClassicsFooter.REFRESH_FOOTER_PULLUP = "더 많은 짐을 당겨 라.";
            ClassicsFooter.REFRESH_FOOTER_RELEASE = "즉시 하중 해제";
            ClassicsFooter.REFRESH_FOOTER_LOADING = "로드 중...";
            ClassicsFooter.REFRESH_FOOTER_REFRESHING = "상쾌...";
            ClassicsFooter.REFRESH_FOOTER_FINISH = "로드 완료 됨";
            ClassicsFooter.REFRESH_FOOTER_FAILED = "로드 실패";
            ClassicsFooter.REFRESH_FOOTER_ALLLOADED = "모두로드 됨";
        } else {
            ClassicsFooter.REFRESH_FOOTER_PULLUP = "Pull up load more";
            ClassicsFooter.REFRESH_FOOTER_RELEASE = "Release immediate load";
            ClassicsFooter.REFRESH_FOOTER_LOADING = "loading...";
            ClassicsFooter.REFRESH_FOOTER_REFRESHING = "Refreshing...";
            ClassicsFooter.REFRESH_FOOTER_FINISH = "Loading completed";
            ClassicsFooter.REFRESH_FOOTER_FAILED = "Failed to load";
            ClassicsFooter.REFRESH_FOOTER_ALLLOADED = "All loaded";
        }
    }
}
