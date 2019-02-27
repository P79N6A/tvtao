package com.yunos.tvtaobao.search.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.config.TvOptionsChannel;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.base.BaseMVPActivity;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.widget.CustomDialog;
import com.yunos.tvtaobao.search.R;
import com.yunos.tvtaobao.search.adapter.AssociateWordsAdapter;
import com.yunos.tvtaobao.search.adapter.FlowAdapter;
import com.yunos.tvtaobao.search.contract.KeySearchContract;
import com.yunos.tvtaobao.search.listener.OnKeyWordItemClickListener;
import com.yunos.tvtaobao.search.model.KeySearchModel;
import com.yunos.tvtaobao.search.presenter.KeySearchPresenter;
import com.yunos.tvtaobao.search.view.CenterRecyclerView;
import com.yunos.tvtaobao.search.view.FlowLayoutManager;
import com.yunos.tvtaobao.search.view.SpaceItemDecoration;
import com.yunos.tvtaobao.search.widget.ImeFullView;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by xtt
 * on 2018/5/25.
 * desc 新版搜索页
 */
public class KeySearchActivity extends BaseMVPActivity<KeySearchPresenter> implements KeySearchContract.View {
    public static final String TAG = "Page_ttsearch";
    public static final String UT_HISTORY_ITEM_EVENT_NAME = "keywords_history_";
    public static final String UT_RECOMMEND_ITEM_EVENT_NAME = "keywords_recommend_";
    private TextView tvDeleteSearchHistory, tvSearchHistory, tvSearchDiscovery;
    private RecyclerView rvSearchHistory, rvSearchDiscovery;
    private CenterRecyclerView rvAssiocateWords;
    private ImeFullView imeFullView;
    private EditText searchInput;
    private AssociateWordsAdapter wordsAdapter;
    private ImageView notSearchRelated;

    private FlowAdapter historyFlowAdapter;
    private FlowAdapter discoverFlowAdapter;
    private CustomDialog commonDialog;
    // 热词列表
    private ArrayList<String> mHotWords;

    private static final String TYPE_TAOBAO = "1";
    private static final String TYPE_CHAOSHI = "2";

    private boolean isRequestFocus;

    public static boolean isClearHistory;
    public static int clickDiscoveryPosition = -1;


    @Override
    protected void onResume() {
        super.onResume();
        //若联想词列表未显示，则刷新搜索历史
        if (rvAssiocateWords.getVisibility() == View.GONE) {
            mPresenter.getSearchHistory();
            if (isRequestFocus) {
                rvSearchHistory.requestFocus();
            }
        }
    }

    @Override
    public void initView() {
        super.initView();
        TvOptionsConfig.setTvOptionVoiceSystem(getIntent().getStringExtra(CoreIntentKey.URI_FROM));
        TvOptionsConfig.setTvOptionsChannel(TvOptionsChannel.SEARCH);
        tvSearchHistory = (TextView) findViewById(R.id.tv_search_history);
        tvSearchDiscovery = (TextView) findViewById(R.id.tv_search_discovery);
        tvDeleteSearchHistory = (TextView) findViewById(R.id.tv_delete_search_history);
        tvDeleteSearchHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //埋点，清空历史
                Utils.utControlHit(TAG, "forget", initTBSProperty(SPMConfig.SEARCH_SPM_BUTTON_FORGET));
                showDeletedSearchHistory();
            }
        });
        rvSearchHistory = (RecyclerView) findViewById(R.id.recycler_search_history);
        rvSearchDiscovery = (RecyclerView) findViewById(R.id.recycler_search_discovery);
        rvSearchHistory.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_15),
                getResources().getDimensionPixelSize(R.dimen.dp_16)));
        rvSearchDiscovery.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_15),
                getResources().getDimensionPixelSize(R.dimen.dp_16)));
        rvAssiocateWords = (CenterRecyclerView) findViewById(R.id.rv_search_think);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAssiocateWords.setLayoutManager(layoutManager);
        rvSearchHistory.setLayoutManager(new FlowLayoutManager());
        rvSearchDiscovery.setLayoutManager(new FlowLayoutManager());
        imeFullView = (ImeFullView) findViewById(R.id.ime_full_view);
        searchInput = (EditText) findViewById(R.id.key_search_content);
        notSearchRelated = (ImageView) findViewById(R.id.iv_not_search_related);
        imeFullView.requestFocused();
        imeFullView.setImeKeyWordListener(new ImeKeyBoardListener());
        searchInput.addTextChangedListener(new TextChangeListener());

    }

    @Override
    public void initData() {
        super.initData();
        initHotWords();
    }


    /**
     * 获取热词并显示
     */
    private void initHotWords() {
        if (mHotWords == null) {
            mHotWords = new ArrayList<String>();
        }
        // 新增类型参数 ：1：淘宝搜索 2：天猫超市搜索
        String mType = IntentDataUtil.getString(getIntent(), "type", TYPE_TAOBAO);
        if (TextUtils.isEmpty(mType)) {
            mType = TYPE_TAOBAO;
        }
        mPresenter.requestSearchDiscovery(mType, this);
    }

    @Override
    protected KeySearchPresenter createPresenter() {
        return new KeySearchPresenter(this, new KeySearchModel(), this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_key_search;
    }

    /**
     * 设置搜索历史数据
     */
    @Override
    public void setSearchHistoryData(ArrayList<String> listHistory) {
        AppDebug.i(TAG, TAG + ".setSearchHistoryData : " + listHistory);
        if (listHistory != null && listHistory.size() > 0) {
            showSearchHistory();
            if (historyFlowAdapter == null) {
                historyFlowAdapter = new FlowAdapter(listHistory, this);
            }
            rvSearchHistory.setAdapter(historyFlowAdapter);
            historyFlowAdapter.setList(listHistory);
            historyFlowAdapter.setTextClickListener(new KeyWordItemClickListener(UT_HISTORY_ITEM_EVENT_NAME, SPMConfig.SEARCH_SPM_HISTORY_ITEM));
        } else {
            hideSearchHistory();
        }
    }

    /**
     * 设置搜索历史数据为空
     */
    @Override
    public void setSearchHistoryNoData() {
        AppDebug.i(TAG, TAG + ".setSearchHistoryNoData");
        hideSearchHistory();

    }

    /**
     * 设置搜索发现数据
     */
    @Override
    public void setSearchDiscoveryData(ArrayList<String> listDiscovery) {
        AppDebug.i(TAG, TAG + ".setSearchDiscoveryData : " + listDiscovery);
        if (listDiscovery != null && listDiscovery.size() > 0) {
            showSearchDiscovery();
            if (discoverFlowAdapter == null) {
                discoverFlowAdapter = new FlowAdapter(listDiscovery, this);
            }
            rvSearchDiscovery.setAdapter(discoverFlowAdapter);
            discoverFlowAdapter.setList(listDiscovery);
            discoverFlowAdapter.setTextClickListener(new KeyWordItemClickListener(UT_RECOMMEND_ITEM_EVENT_NAME, SPMConfig.SEARCH_SPM_RECOMMEND_ITEM));
        } else {
            hideSearchDiscovery();
        }
    }

    /**
     * 设置搜索发现数据
     */
    @Override
    public void setSearchDiscoveryNoData() {
        AppDebug.i(TAG, TAG + ".setSearchHistoryNoData");
        hideSearchDiscovery();

    }

    @Override
    public void setAssociateWord(ArrayList<String> words) {
        //todo

        if (words != null && words.size() > 0) {
            hideSearchRecommend();
            hideNotSearchRelated();
            showAssociateWords();
            if (wordsAdapter == null) {
                wordsAdapter = new AssociateWordsAdapter(this);
                wordsAdapter.setItemClickListener(new ItemClickListener());
            }
            rvAssiocateWords.setAdapter(wordsAdapter);
            wordsAdapter.setItemData(words);
            wordsAdapter.notifyDataSetChanged();
        } else {
            hideSearchRecommend();
            hideAssociateWords();
            showNotSearchRelated();
        }
    }

    @Override
    public void showProgressDialog(boolean show) {

    }

    private class ImeKeyBoardListener implements ImeFullView.OnKeyWordDetermineListener {

        @Override
        public void onClicked(String display) {
            AppDebug.i(TAG, TAG + ".onClicked display : " + display);
            searchInput.setText(searchInput.getText().toString() + display);
            Spannable text = searchInput.getText();
            Selection.setSelection(text, text.length());
        }

        @Override
        public void onDelete() {
            AppDebug.i(TAG, TAG + ".onDelete");
            //埋点
            Utils.utControlHit(TAG, "back", initTBSProperty(SPMConfig.SEARCH_SPM_BUTTON_KEYBOARD_BACK));
            String strText = searchInput.getText().toString();
            if (strText.length() > 0) {
                strText = strText.substring(0, strText.length() - 1);
                searchInput.setText(strText);
                Spannable text = searchInput.getText();
                Selection.setSelection(text, text.length());
            }
        }

        @Override
        public void onClear() {
            AppDebug.i(TAG, TAG + ".onClear");
            //埋点，清空
            Utils.utControlHit(TAG, "delete", initTBSProperty(SPMConfig.SEARCH_SPM_BUTTON_KEYBOARD_DELETE));
            searchInput.setText("");
        }
    }

    private class TextChangeListener implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            AppDebug.i(TAG, TAG + ".beforeTextChanged " + charSequence);
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            AppDebug.i(TAG, TAG + ".onTextChanged " + charSequence);
            if (!TextUtils.isEmpty(charSequence)) {
                mPresenter.requestAssociateWord(KeySearchActivity.this, charSequence.toString());
            } else {
                hideNotSearchRelated();
                hideAssociateWords();
                showSearchRecommend();
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            AppDebug.i(TAG, TAG + ".afterTextChanged " + editable.toString());
        }
    }

    private class ItemClickListener implements AssociateWordsAdapter.OnItemClickListener {

        @Override
        public void onItemClick(View view, String word, int position) {
            AppDebug.i(TAG, TAG + ".onItemClick word " + word + " ,position : " + position);
            mPresenter.saveHistoryDao(word);
            isRequestFocus = false;
            //埋点，清空
            Utils.utControlHit(TAG, "listitem_" + word, initTBSProperty(SPMConfig.SEARCH_SPM_ASSOCIATION_ITEM));
            Intent intent = new Intent();
            intent.setClass(KeySearchActivity.this, SearchResultActivity.class);
            intent.putExtra(BaseConfig.INTENT_KEY_KEYWORDS, word);
            startActivity(intent);
        }
    }

    //搜索历史和搜索发现item点击事件
    private class KeyWordItemClickListener implements OnKeyWordItemClickListener {
        private String eventName;
        private String spm;

        public KeyWordItemClickListener(String eventName, String spm) {
            this.eventName = eventName;
            this.spm = spm;
        }

        @Override
        public void onItemClick(String word, int position) {
            AppDebug.i(TAG, TAG + ".onItemClick word ");
            mPresenter.saveHistoryDao(word);
            //产品要求这么写的：eventName+word
            Utils.utControlHit(TAG, eventName + word, initTBSProperty(spm));
            if (eventName.equals(UT_HISTORY_ITEM_EVENT_NAME)) {
                isRequestFocus = true;
            } else {
                isRequestFocus = false;
                if (rvSearchHistory != null && rvSearchHistory.getVisibility() == View.GONE) {
                    clickDiscoveryPosition = position;
                }
            }
            Intent intent = new Intent();
            intent.setClass(KeySearchActivity.this, SearchResultActivity.class);
            intent.putExtra(BaseConfig.INTENT_KEY_KEYWORDS, word);
            startActivity(intent);
        }
    }


    /**
     * 隐藏搜索历史和搜索发现
     */
    private void hideSearchRecommend() {
        hideSearchHistory();
        hideSearchDiscovery();
    }

    /**
     * 显示搜索历史和搜索发现
     */
    private void showSearchRecommend() {
        //重新获取搜索历史
        mPresenter.getSearchHistory();
        showSearchDiscovery();
    }

    /**
     * onD     * 隐藏搜索联想请求词
     */
    private void hideAssociateWords() {
        if (rvAssiocateWords.getVisibility() == View.VISIBLE) {
            rvAssiocateWords.setVisibility(View.GONE);

        }
    }


    /**
     * 显示搜索历史
     */
    public void showSearchHistory() {
        tvSearchHistory.setVisibility(View.VISIBLE);
        tvDeleteSearchHistory.setVisibility(View.VISIBLE);
        rvSearchHistory.setVisibility(View.VISIBLE);

    }

    /**
     * 隐藏搜索历史
     */
    public void hideSearchHistory() {
        tvSearchHistory.setVisibility(View.GONE);
        tvDeleteSearchHistory.setVisibility(View.GONE);
        rvSearchHistory.setVisibility(View.GONE);
    }

    /**
     * 显示搜索发现
     */
    public void showSearchDiscovery() {
        tvSearchDiscovery.setVisibility(View.VISIBLE);
        rvSearchDiscovery.setVisibility(View.VISIBLE);

    }

    /**
     * 隐藏搜索发现
     */
    public void hideSearchDiscovery() {
        tvSearchDiscovery.setVisibility(View.GONE);
        rvSearchDiscovery.setVisibility(View.GONE);
    }


    /**
     * 显示搜索发现
     */
    private void showAssociateWords() {
        if (rvAssiocateWords.getVisibility() == View.GONE) {
            rvAssiocateWords.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 显示没有搜索联想词提示标签
     */
    private void showNotSearchRelated() {
        if (notSearchRelated.getVisibility() == View.GONE) {
            notSearchRelated.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 隐藏没有搜索联想词提示标签
     */
    private void hideNotSearchRelated() {
        if (notSearchRelated.getVisibility() == View.VISIBLE) {
            notSearchRelated.setVisibility(View.GONE);
        }
    }

    /**
     * 显示删除搜索记录对话框
     */
    private void showDeletedSearchHistory() {
        if (isFinishing()) {
            return;
        }
        if (commonDialog != null && commonDialog.isShowing()) {
            commonDialog.dismiss();
        }
        commonDialog = new CustomDialog.Builder(this).setType(2)
                .setMessage(getResources().getString(R.string.delete_search_history))
                .setPositiveButton(getResources().getString(R.string.ytbv_delete),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPresenter.deleteSearchHistory();
                                isClearHistory = true;
                                commonDialog.dismiss();

                            }
                        })
                .setNegativeButton(getResources().getString(R.string.ytbv_save),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                commonDialog.dismiss();
                            }

                        }).create();
        commonDialog.show();
    }

    @Override
    protected void onDestroy() {

        if (mPresenter != null) {
            mPresenter.setCursorClose();
        }

        super.onDestroy();
        if (commonDialog != null) {
            commonDialog.dismiss();
        }
    }

    @Override
    public String getPageName() {
        return "search";
    }

    @Override
    protected String getAppTag() {
        return "tt";
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();

        p.put(SPMConfig.SPM_CNT, SPMConfig.SEARCH_SPM + ".0.0");
        if (CoreApplication.getLoginHelper(CoreApplication.getApplication()).isLogin()) {
            p.put("is_login", "1");
        } else {
            p.put("is_login", "0");
        }

        if (!TextUtils.isEmpty(AppInfo.getAppVersionName())) {
            p.put("version", AppInfo.getAppVersionName());
        }

        if (!TextUtils.isEmpty(Config.getAppKey())) {
            p.put("appkey", Config.getAppKey());

        }
        return p;
    }

    /**
     * 初始化埋点信息
     *
     * @return
     */
    public Map<String, String> initTBSProperty(String spm) {

        Map<String, String> p = Utils.getProperties();
        if (!TextUtils.isEmpty(Config.getAppKey())) {
            p.put("appkey", Config.getAppKey());
        }
        try {
            if (!TextUtils.isEmpty(AppInfo.getAppVersionName())) {
                p.put("version", AppInfo.getAppVersionName());
            }
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }
        if (!TextUtils.isEmpty(spm)) {
            p.put("spm", spm);
        }
        return p;
    }


}
