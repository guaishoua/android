package com.android.tacu.widget.popupwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.module.assets.model.SelectTakeCoinAddressModel;
import com.android.tacu.module.assets.presenter.CoinsPresenter;
import com.android.tacu.utils.ClipboardUtil;
import com.android.tacu.utils.ShowToast;
import com.android.tacu.utils.user.UserInfoUtils;
import com.android.tacu.widget.dialog.DroidDialog;

/**
 * Created by xiaohong on 2018/8/23.
 */

public class SpinerPopWindow extends PopupWindow {

    private View view;
    private Context context;
    private MyAdapter mAdapter;
    private LayoutInflater inflater;
    private CoinsPresenter coinsPresenter;
    private SelectTakeCoinAddressModel addressModel;

    private EditText et_wallet_name;
    private EditText et_address;
    private Button confirm;
    private View paste;

    private UserInfoUtils spUtils;


    public SpinerPopWindow(Context context, SelectTakeCoinAddressModel addressModel, BaseQuickAdapter.OnItemClickListener clickListener, CoinsPresenter coinsPresenter) {
        super(context);
        this.context = context;
        this.addressModel = addressModel;
        this.coinsPresenter = coinsPresenter;

        spUtils = UserInfoUtils.getInstance();
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
        mAdapter.setNewData(addressModel.resp.addressList);
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
        confirm = view.findViewById(R.id.qmuibt_submit);
        paste = view.findViewById(R.id.btn_paste_content);

        paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                et_address.setText(ClipboardUtil.getPasteContent(context));

            }
        });

        /** DroidDialog 有 positive, negative button 可以用 */
        final DroidDialog dialog = new DroidDialog.Builder(context)
                .viewCustomLayout(view)
                .show();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String walletName = et_wallet_name.getText().toString().trim();
                String walletAddress = et_address.getText().toString().trim();
                if (TextUtils.isEmpty(walletName)) {
                    ShowToast.error(context.getResources().getString(R.string.wallet_name));
                    return;
                }
                if (TextUtils.isEmpty(walletAddress)) {
                    ShowToast.error(context.getResources().getString(R.string.wallet_address));
                    return;
                }

                coinsPresenter.insertTakeAddress(walletAddress, addressModel.detail.currencyId, walletName);
                dialog.dismiss();
            }
        });

    }


    public class MyAdapter extends BaseQuickAdapter<SelectTakeCoinAddressModel.RespBean.AddressListBean, BaseViewHolder> {
        public MyAdapter() {
            super(R.layout.item_spinner_item);
        }

        @Override
        protected void convert(BaseViewHolder helper, final SelectTakeCoinAddressModel.RespBean.AddressListBean item) {
            helper.setText(R.id.tv_coins_name, item.note);
            helper.setText(R.id.tv_coins_address, item.address);
            helper.setOnClickListener(R.id.iv_del, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    coinsPresenter.updateCoinAddress(addressModel.detail.currencyId, item.id);
                }
            });
        }
    }
}
