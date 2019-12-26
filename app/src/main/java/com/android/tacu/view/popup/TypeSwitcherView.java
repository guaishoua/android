package com.android.tacu.view.popup;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.android.tacu.R;


public class TypeSwitcherView extends PopupBaseView {

    public interface Listener {
        void onItemPressed(int flag);
        void onDismiss();
    }

    private int typeRecord;
    private TextView withdraw;
    private TextView recharge;
    Listener listener;


    public TypeSwitcherView(Context context, int type, Listener listener) {
        super(context);
        this.listener = listener;
        this.typeRecord = type;
    }

    @Override
    protected void initView(View root) {
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                if (listener != null)
                    listener.onDismiss();
            }
        });
        withdraw = root.findViewById(R.id.btn_record_switch_withdraw);
        recharge = root.findViewById(R.id.btn_record_switch_recharge);
        withdraw.setOnClickListener(onClicked);
        recharge.setOnClickListener(onClicked);
        switch (typeRecord) {
            case 0:
                recharge.setTextColor(getContext().getResources().getColor(R.color.color_default));
                break;
            case 1:
                withdraw.setTextColor(getContext().getResources().getColor(R.color.color_default));
                break;
        }
    }

    View.OnClickListener onClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (listener != null)
                listener.onItemPressed(v.getId() == R.id.btn_record_switch_recharge ? 0 : 1);
            dismiss();
        }
    };


    @Override
    protected int getViewLayoutId() {
        return R.layout.record_switch;
    }

}