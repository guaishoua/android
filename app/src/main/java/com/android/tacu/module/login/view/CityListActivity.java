package com.android.tacu.module.login.view;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.tacu.R;
import com.android.tacu.api.Constant;
import com.android.tacu.base.BaseActivity;
import com.android.tacu.module.login.model.CityListConstant;
import com.android.tacu.module.login.model.CountryModel;
import com.android.tacu.widget.SideBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 选择手机区号
 */
public class CityListActivity extends BaseActivity {
    @BindView(R.id.et_search)
    EditText et_search;
    @BindView(R.id.iv_search)
    ImageView iv_search;
    @BindView(R.id.city_list)
    RecyclerView recyclerView;
    @BindView(R.id.sider)
    SideBar mSideBar;
    @BindView(R.id.tv_tips)
    TextView tvTips;

    private String searshStr;
    private CityAdapter cityAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<CountryModel> cityList = new ArrayList<>();
    private List<CountryModel> searchList = new ArrayList<>();

    @Override
    protected void setView() {
        setContentView(R.layout.activity_city_list);
    }

    @Override
    protected void initView() {
        mTopBar.setTitle(getString(R.string.country));

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        cityAdapter = new CityAdapter();
        recyclerView.setAdapter(cityAdapter);

        mSideBar.setOnLetterChangeListener(new SideBar.OnLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                tvTips.setVisibility(View.VISIBLE);
                tvTips.setText(letter);
                if (TextUtils.equals(spUtil.getLanguage(), Constant.ZH_TW) || TextUtils.equals(spUtil.getLanguage(), Constant.ZH_CN)) {
                    for (int i = 0; i < cityList.size(); i++) {
                        if (TextUtils.equals(cityList.get(i).piyin.charAt(0) + "", letter.toLowerCase())) {
                            MoveToPosition(linearLayoutManager, recyclerView, i);
                            break;
                        }
                    }
                } else {
                    for (int i = 0; i < cityList.size(); i++) {
                        if (TextUtils.equals(cityList.get(i).en.charAt(0) + "", letter)) {
                            MoveToPosition(linearLayoutManager, recyclerView, i);
                            break;
                        }
                    }
                }
            }

            @Override
            public void onReset() {
                tvTips.setVisibility(View.GONE);
            }
        });

        initData();
        textChanged();
    }

    /**
     * 取消
     */
    @OnClick(R.id.tv_cancel)
    void cancel() {
        finish();
    }

    /**
     * 搜索按钮
     */
    @OnClick(R.id.iv_search)
    void search() {
        searchCity();
    }

    private void textChanged() {
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchCity();
            }
        });
    }

    //app没有默认的语言，刚进入app没有设置语言会有问题，暂时判断英文的
    private void searchCity() {
        searchList.clear();
        searshStr = et_search.getText().toString().trim().toLowerCase();
        if (TextUtils.isEmpty(searshStr)) {
            setAdapter(cityList);
            return;
        }
        if (cityList != null && cityList.size() > 0) {
            for (int i = 0; i < cityList.size(); i++) {
                if (cityList.get(i).en.toLowerCase().contains(searshStr) || cityList.get(i).zh.toLowerCase().contains(searshStr) || String.valueOf(cityList.get(i).code).contains(searshStr)) {
                    searchList.add(cityList.get(i));
                }
            }
        }
        setAdapter(searchList);
    }

    private void setAdapter(List<CountryModel> list) {
        if (list != null && list.size() > 0) {
            cityAdapter.setNewData(list);
        } else {
            cityAdapter.setNewData(null);

        }
    }

    /**
     * 导入城市数据
     */
    private void initData() {
        JSONArray jsonArray = null;
        CountryModel model;
        JSONObject jsonObject;
        try {
            if (TextUtils.equals(spUtil.getLanguage(), Constant.ZH_TW) || TextUtils.equals(spUtil.getLanguage(), Constant.ZH_CN)) {
                jsonArray = new JSONArray(CityListConstant.Country_ZH);
            } else {
                jsonArray = new JSONArray(CityListConstant.Country_EN);
            }

            for (int i = 0; i < jsonArray.length(); i++) {
                model = new CountryModel();
                jsonObject = jsonArray.getJSONObject(i);
                model.en = jsonObject.getString("en");
                model.zh = jsonObject.getString("zh");
                model.code = jsonObject.getInt("code");
                model.piyin = jsonObject.getString("piyin");
                cityList.add(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cityList != null && cityList.size() > 0) {
            cityAdapter.setNewData(cityList);
        }
    }

    /**
     * RecyclerView 移动到当前位置
     */
    public static void MoveToPosition(LinearLayoutManager manager, RecyclerView mRecyclerView, int n) {
        int firstItem = manager.findFirstVisibleItemPosition();
        int lastItem = manager.findLastVisibleItemPosition();
        if (n <= firstItem) {
            mRecyclerView.scrollToPosition(n);
        } else if (n <= lastItem) {
            int top = mRecyclerView.getChildAt(n - firstItem).getTop();
            mRecyclerView.scrollBy(0, top);
        } else {
            mRecyclerView.scrollToPosition(n);
        }
    }

    public class CityAdapter extends BaseQuickAdapter<CountryModel, BaseViewHolder> {
        public CityAdapter() {
            super(R.layout.item_city);
        }

        @Override
        protected void convert(BaseViewHolder helper, final CountryModel item) {
            if (TextUtils.equals(spUtil.getLanguage(), Constant.ZH_TW) || TextUtils.equals(spUtil.getLanguage(), Constant.ZH_CN)) {
                helper.setText(R.id.tv_en_name, item.zh);
            } else {
                helper.setText(R.id.tv_en_name, item.en);
            }

            helper.setGone(R.id.tv_zh_name, true);
            helper.setText(R.id.tv_zh_name, "+" + item.code);

            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    if (TextUtils.equals(spUtil.getLanguage(), Constant.ZH_TW) || TextUtils.equals(spUtil.getLanguage(), Constant.ZH_CN)) {
                        intent.putExtra("city", item.zh);
                    } else {
                        intent.putExtra("city", item.en);
                    }
                    intent.putExtra("code", item.code);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }
}
