package com.android.tacu.widget.popupwindow;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.module.assets.model.TransferInfo;
import com.android.tacu.module.assets.presenter.TransferOTCPresenter;
import com.android.tacu.utils.ShowToast;
import com.android.tacu.widget.dialog.DroidDialog;

/**
 * Created by xiaohong on 2018/8/23.
 */

public class TransferPopWindow extends PopupWindow {

    private View view;
    private Context context;
    private MyAdapter mAdapter;
    private LayoutInflater inflater;
    private TransferOTCPresenter presenter;
    private TransferInfo.UserInfo userInfo;

    private EditText et_wallet_name;
    private EditText et_address;

    public TransferPopWindow(Context context, TransferInfo.UserInfo transferInfo, BaseQuickAdapter.OnItemClickListener clickListener, TransferOTCPresenter presenter) {
        super(context);
        this.context = context;
        this.presenter = presenter;
        this.userInfo = transferInfo;

        inflater = LayoutInflater.from(context);

        init(clickListener);
    }

    private void init(BaseQuickAdapter.OnItemClickListener clickListener) {
        view = inflater.inflate(R.layout.popupwindow_layout, null);
        setContentView(view);
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x00);
        setBackgroundDrawable(dw);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        LinearLayout ll_add_coins_address = view.findViewById(R.id.ll_add_coins_address);

        mAdapter = new MyAdapter();
        mAdapter.setNewData(userInfo.accountList);
        mAdapter.setOnItemClickListener(clickListener);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(mAdapter);
        ll_add_coins_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAddresee();
                dismiss();
            }
        });
    }

    private void addAddresee() {
        View view = inflater.inflate(R.layout.dialog_take_coin_address, null);
        et_address = view.findViewById(R.id.et_address);
        et_wallet_name = view.findViewById(R.id.et_wallet_name);
        TextView tv_name = view.findViewById(R.id.tv_name);
        tv_name.setText(context.getResources().getString(R.string.epay_account));
        et_address.setHint(context.getResources().getString(R.string.epay_account));

        new DroidDialog.Builder(context)
                .title(context.getResources().getString(R.string.epay_account))
                .titleGravity(Gravity.CENTER)
                .viewCustomLayout(view)
                .positiveButton(context.getResources().getString(R.string.sure), new DroidDialog.onPositiveListener() {
                    @Override
                    public void onPositive(Dialog droidDialog) {
                        String account = et_address.getText().toString().trim();
                        String note = et_wallet_name.getText().toString().trim();
                        if (TextUtils.isEmpty(account)) {
                            ShowToast.error(context.getResources().getString(R.string.epay_account));
                            return;
                        }
                        if (account.length() < 3 || account.length() > 30) {
                            ShowToast.error(context.getResources().getString(R.string.epay_error));
                            return;
                        }
                        if (TextUtils.isEmpty(note)) {
                            ShowToast.error(context.getResources().getString(R.string.note));
                            return;
                        }
                        droidDialog.dismiss();
                        presenter.addAccount(account, note);
                    }
                })
                .show();
    }


    public class MyAdapter extends BaseQuickAdapter<TransferInfo.AccountList, BaseViewHolder> {

        public MyAdapter() {
            super(R.layout.item_spinner_item);
        }

        @Override
        protected void convert(BaseViewHolder helper, final TransferInfo.AccountList item) {
            helper.setGone(R.id.iv_del, false);
            helper.setText(R.id.tv_coins_name, item.note);
            helper.setText(R.id.tv_coins_address, item.account);
        }
    }
}
