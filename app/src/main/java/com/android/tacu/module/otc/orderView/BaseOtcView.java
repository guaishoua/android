package com.android.tacu.module.otc.orderView;

import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.tacu.R;
import com.android.tacu.module.otc.model.OtcMarketInfoModel;
import com.android.tacu.module.otc.model.OtcTradeModel;
import com.android.tacu.utils.CommonUtils;
import com.android.tacu.utils.FormatterUtils;
import com.android.tacu.utils.ShowToast;
import com.android.tacu.utils.user.UserInfoUtils;

public class BaseOtcView {

    private TextView tv_people_title;

    private TextView tv_orderno;
    private TextView tv_people;
    private TextView tv_price;
    private TextView tv_num;
    private TextView tv_order_amount;
    private TextView tv_xiatime;
    private TextView tv_coined_time;
    private RelativeLayout rl_coined;

    public void setBaseView(View view, final Context context) {
        tv_people_title = view.findViewById(R.id.tv_people_title);

        tv_orderno = view.findViewById(R.id.tv_orderno);
        tv_people = view.findViewById(R.id.tv_people);
        tv_price = view.findViewById(R.id.tv_price);
        tv_num = view.findViewById(R.id.tv_num);
        tv_order_amount = view.findViewById(R.id.tv_order_amount);
        tv_xiatime = view.findViewById(R.id.tv_xiatime);
        tv_coined_time = view.findViewById(R.id.tv_coined_time);
        rl_coined = view.findViewById(R.id.rl_coined);

        tv_orderno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cmb.setText(tv_orderno.getText().toString()); //将内容放入粘贴管理器,在别的地方长按选择"粘贴"即可
                ShowToast.success(context.getResources().getString(R.string.copy_success));
            }
        });
    }

    public void setBaseValue(Context context, OtcTradeModel model, UserInfoUtils spUtil) {
        rl_coined.setVisibility(View.GONE);
        if (model != null) {
            Boolean isBuy = null;
            if (model.buyuid == spUtil.getUserUid()) {
                isBuy = true;
            } else if (model.selluid == spUtil.getUserUid()) {
                isBuy = false;
            }
            if (isBuy != null) {
                if (isBuy) {
                    tv_people_title.setText(context.getResources().getString(R.string.sell_people));
                } else {
                    tv_people_title.setText(context.getResources().getString(R.string.buy_people));
                }
            }

            tv_orderno.setText(model.orderNo);
            tv_price.setText(FormatterUtils.getFormatValue(model.price));
            tv_num.setText(FormatterUtils.getFormatValue(model.num));
            tv_order_amount.setText(FormatterUtils.getFormatValue(model.amount));
            tv_xiatime.setText(model.createTime);
        }
    }

    public void setBaseValue1(Context context, OtcTradeModel model, UserInfoUtils spUtil) {
        setBaseValue(context, model, spUtil);
        rl_coined.setVisibility(View.VISIBLE);
        if (model != null) {
            tv_coined_time.setText(model.updateTime);
        }
    }

    public void setUserInfo(OtcMarketInfoModel infoModel) {
        if (infoModel != null) {
            tv_people.setText(infoModel.nickname + "(" + CommonUtils.nameXing(infoModel.firstName, infoModel.secondName) + ")");
        }
    }
}
