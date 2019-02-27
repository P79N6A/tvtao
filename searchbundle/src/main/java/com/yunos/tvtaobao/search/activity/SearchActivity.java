/**
 * $
 * PROJECT NAME: TVTaobao
 * PACKAGE NAME: com.yunos.tvtaobao.activity.search
 * FILE NAME: SearchActivity.java
 * CREATED TIME: 2014年10月16日
 * COPYRIGHT: Copyright(c) 2013 ~ 2014 All Rights Reserved.
 */
package com.yunos.tvtaobao.search.activity;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.AdapterView.OnItemClickListener;
import com.yunos.tv.app.widget.focus.FocusListView;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.FocusRelativeLayout;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.config.PageMapConfig;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.config.TvOptionsChannel;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.search.R;
import com.yunos.tvtaobao.search.adapter.SearchListAdapter;
import com.yunos.tvtaobao.search.bean.GridItem;
import com.yunos.tvtaobao.search.listener.OnKeyWordDetermineListener;
import com.yunos.tvtaobao.search.view.CustomAlphaView;
import com.yunos.tvtaobao.search.view.HotWordView;
import com.yunos.tvtaobao.search.widget.ImeExpandView;

import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Map;

/**
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2014年10月16日 下午4:18:18
 */
public class SearchActivity extends BaseActivity {

    private SparseArray<GridItem> sGridItems ;

    // 整个界面的ViewHolder
    private ViewHolder mViewHolder = null;

    // 搜索结果的ListAdapter
    private SearchListAdapter mAdapter = null;

    // 弹出窗口
    private PopupWindow mPopupWindow;

    // 热词列表
    private ArrayList<String> mHotWords;

    // 搜索关键字
    private String mKeyWord = null;

    //重新搜索
    private boolean mResearch = true;

    // huodong名称
    private String mHuoDongName;

    private BusinessRequest mBusinessRequest;

    // 新增类型参数 ：1：淘宝搜索 2：天猫超市搜索
    private String mType;

    private boolean mHotWordLoading;


    private OnKeyWordDetermineListener mImeKeyWordListener = new OnKeyWordDetermineListener() {

        @Override
        public void onClicked(String word) {
            if (mViewHolder != null) {
                mViewHolder.mSearchInputKey.setText(mViewHolder.mSearchInputKey.getText().toString() + word);
                Spannable text = mViewHolder.mSearchInputKey.getText();
                Selection.setSelection(text, text.length());
            }
        }

        @Override
        public void onDelete() {
            if (mViewHolder != null) {
                String strText = mViewHolder.mSearchInputKey.getText().toString();
                if (strText.length() > 0) {
                    strText = strText.substring(0, strText.length() - 1);
                    mViewHolder.mSearchInputKey.setText(strText);
                    Spannable text = mViewHolder.mSearchInputKey.getText();
                    Selection.setSelection(text, text.length());
                    if (strText.length() == 0) {
                        mViewHolder.mBottomMaskView.playAlpha();
                    }
                }
            }
        }

        @Override
        public void onClear() {
            if (mViewHolder != null) {
                mViewHolder.mSearchInputKey.setText("");
                showRight(R.id.search_example_layout);
                mViewHolder.mBottomMaskView.playAlpha();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TvOptionsConfig.setTvOptionVoiceSystem(getIntent().getStringExtra(CoreIntentKey.URI_FROM));
        TvOptionsConfig.setTvOptionsChannel(TvOptionsChannel.SEARCH);
        mKeyWord = getIntent().getStringExtra("keyword");
        mType = IntentDataUtil.getString(getIntent(), "type", "1");
        if (TextUtils.isEmpty(mType)) {
            mType = "1";
        }
        AppDebug.v(TAG, TAG + ".onCreate.mKeyWord = " + mKeyWord + ", mType = " + mType);

        mBusinessRequest = BusinessRequest.getBusinessRequest();
        mHotWordLoading = false;

        creatView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        hidePopWindow();
    }

    @Override
    protected void onDestroy() {
        if (mHotWords != null) {
            mHotWords.clear();
            mHotWords = null;
        }
        mViewHolder = null;
        super.onDestroy();
    }

    /**
     * 创建View
     */
    private void creatView() {
        mViewHolder = new ViewHolder();
        if (mViewHolder.mViewContents == null) {
            return;
        }
        setContentView(mViewHolder.mViewContents);
        initLeftGridViewData();
        initResultListView();
        initHotWords();

        if (!TextUtils.isEmpty(mKeyWord)) {
            mViewHolder.mSearchInputKey.setText(mKeyWord);
            mViewHolder.mSearchInputKey.setSelection(mKeyWord.length());
        }

        showRight(R.id.search_example_layout);
        mViewHolder.mImeBoard.requestFocus();
    }

    /**
     * 初始化搜索头字母
     */
    private void initLeftGridViewData() {
        if (sGridItems == null) {
            sGridItems = new SparseArray<GridItem>();
            sGridItems.put(1, new GridItem("1"));
            sGridItems.put(2, new GridItem("B", "A", "2", "C", null, true));
            sGridItems.put(3, new GridItem("E", "D", "3", "F", null, true));
            sGridItems.put(4, new GridItem("H", "G", "4", "I", null, true));
            sGridItems.put(5, new GridItem("K", "J", "5", "L", null, true));
            sGridItems.put(6, new GridItem("N", "M", "6", "O", null, true));
            sGridItems.put(7, new GridItem("Q", "P", "7", "R", "S", true));
            sGridItems.put(8, new GridItem("U", "T", "8", "V", null, true));
            sGridItems.put(9, new GridItem("X", "W", "9", "Y", "Z", true));
            sGridItems.put(10, new GridItem("清空", GridItem.DISPATCH_CLEAR));
            sGridItems.put(11, new GridItem("0"));
            sGridItems.put(12, new GridItem("删除", GridItem.DISPATCH_DELETE));
        }
    }

    /**
     * 初始化搜索结果界面
     */
    private void initResultListView() {
        if (mViewHolder != null && mViewHolder.mResultListView != null) {
            mAdapter = new SearchListAdapter(SearchActivity.this);
            mViewHolder.mResultListView.setAdapter(mAdapter);
            mViewHolder.mResultListView.setDrawSelectorOnTop(false);
            mViewHolder.mResultListView.setAnimateWhenGainFocus(false, false, false, false);
            mViewHolder.mResultListView.setOnFocusChangeListener(mFocusChangeListener);
            mViewHolder.mResultListView.setFocusBackground(true);
            mViewHolder.mResultListView.setOnItemSelectedListener(new ItemSelectedListener() {

                @Override
                public void onItemSelected(View itemView, int position, boolean focused, View parent) {
                }
            });

            mViewHolder.mResultListView.setOnItemClickListener(new OnItemClickListener() {

                @SuppressWarnings("deprecation")
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AppDebug.v(TAG, TAG + ".mResultListView.onItemClicked.parent = " + parent + ".view = " + view
                            + ".position = " + position + ".id = " + id);
                    SearchListAdapter adapter = (SearchListAdapter) parent.getAdapter();
                    if (adapter != null) {
                        String result = (String) adapter.getItem(position);
                        if (result != null) {
                            if (mViewHolder != null && mViewHolder.mResultListView.isScrolling()) {
                                mViewHolder.mResultListView.forceResetFocusParams(mViewHolder.mRootLayout);
                            }
                            mHuoDongName = "seach_b";
                            //统计搜索首字母对应的词的点击事件
                            Map<String, String> p = initTBSProperty();
                            if (!TextUtils.isEmpty(result)) {
                                p.put("input_key", mViewHolder.mSearchInputKey.getText().toString());
                                p.put("name", result);
                            }

//                            TBS.Adv.ctrlClicked(getFullPageName(), CT.ListItem,
//                                    Utils.getControlName(getFullPageName(), "S_P", position), Utils.getKvs(p));
                            p.put("spm" , SPMConfig.SEARCH_SPM_KEYWORDS_RESULT);
                            Utils.utControlHit(getFullPageName(), "ListItem-TtSearch_S_P_"+position, p);
                            Utils.updateNextPageProperties(SPMConfig.SEARCH_SPM_KEYWORDS_RESULT);

                            gotoSearchResult(result);

                        }
                    }
                }
            });

            mViewHolder.mResultListView.setOnScrollListener(new OnScrollListener() {

                @Override
                public void onScrollStateChanged(ViewGroup view, int scrollState) {
                    AppDebug.v(TAG, TAG + ".initResultListView.setOnScrollListener scrollState = " + scrollState
                            + ", view = " + view);
                    switch (scrollState) {
                        case OnScrollListener.SCROLL_STATE_IDLE:
                            showBottomMask();
                            break;

                        default:
                            break;
                    }
                }

                @Override
                public void onScroll(ViewGroup view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                }
            });
        }
    }




    /**
     * 获取热词并显示
     */
    private void initHotWords() {
        if (mViewHolder == null) {
            return;
        }

        if (mHotWordLoading) {
            return;
        }
        mHotWordLoading = true;

        if (mHotWords == null) {
            mHotWords = new ArrayList<String>();
        }

        mBusinessRequest.getHotWordsList(mType, new GetHotwordsRequestListener(new WeakReference<BaseActivity>(this)));
    }

    /**
     * 更新热词界面
     */
    private void updateHotWordsView() {
        if (mViewHolder == null) {
            return;
        }

        mViewHolder.mImeBoard.requestFocus();
        mViewHolder.mHotWordView.setHotWordList(mHotWords);
        mViewHolder.mHotWordView.setOnHotWordViewKeyDownListener(new HotWordView.OnHotWordViewKeyDownListener() {

            @Override
            public void onHotWordViewKeyDown(HotWordView.ItemInfo itemInfo) {
                AppDebug.v(TAG, TAG + ".onHotWordViewKeyDown.itemInfo = " + itemInfo);
                if (itemInfo != null) {
                    mHuoDongName = "seach_a";

                    //统计热词位置点击次数
                    Integer position = itemInfo.index;
                    Map<String, String> p = initTBSProperty();
                    if (!TextUtils.isEmpty(itemInfo.text)) {
                        p.put("name", itemInfo.text);
                    }
//                    TBS.Adv.ctrlClicked(getFullPageName(), CT.Text,
//                            Utils.getControlName(getFullPageName(), "R_P", position, itemInfo.text), Utils.getKvs(p));

                    p.put("spm" , SPMConfig.SEARCH_SPM_KEYWORDS_RECOMMEND);
                    Utils.utControlHit(getFullPageName(), "Text-TtSearch_S_P_"+position+"_"+itemInfo.text, p);
                    Utils.updateNextPageProperties(SPMConfig.SEARCH_SPM_KEYWORDS_RECOMMEND);

                    gotoSearchResult(itemInfo.text);

                }
            }
        });
    }

    /**
     * 显示键盘的扩展界面
     * @param v
     * @param gridItem
     */
    private void showExpandPopup(View v, GridItem gridItem) {
        if (mViewHolder == null || mViewHolder.mPinyinExtendView == null) {
            return;
        }
        hidePopWindow();
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(mViewHolder.mPinyinExtendView, getResources().getDimensionPixelSize(
                    R.dimen.dp_213), getResources().getDimensionPixelSize(R.dimen.dp_213));
        }
        mViewHolder.mPinyinExtendView.setParams(mPopupWindow, gridItem, new ImeExpandView.OnTextSelectedListener() {

            @Override
            public void onTextSelected(String text) {
                if (mImeKeyWordListener != null) {
                    mImeKeyWordListener.onClicked(text);
                }
            }
        });

        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setFocusable(true);

        AppDebug.v(TAG, TAG + ".showExpandPopup v = " + v + " width=" + v.getWidth() + " height=" + v.getHeight());
        mPopupWindow.showAsDropDown(v, -v.getWidth() / 2,
                -(v.getHeight() / 2 + (int) getResources().getDimension(R.dimen.dp_106_67)));

        //当popWindow出来后改变键盘状态
        mViewHolder.changeImeBoardStatus(true);
        //当popWindow消失后 恢复键盘状态
        mViewHolder.mPinyinExtendView.setOnImeExpandViewDismiss(new ImeExpandView.OnImeExpandViewDismiss() {

            @Override
            public void onImeExpandViewDismiss() {
                if (mViewHolder != null) {
                    mViewHolder.changeImeBoardStatus(false);
                }
            }
        });
    }

    /**
     * 隐藏键盘的扩展界面
     */
    void hidePopWindow() {
        if (mViewHolder != null && mViewHolder.mPinyinExtendView != null) {
            mViewHolder.mPinyinExtendView.hidePopWindow();
        }
    }

    /**
     * 显示右边的内容
     * @param id
     */
    private void showRight(int id) {
        if (mViewHolder == null) {
            return;
        }

        if (id == R.id.search_example_layout) { // 例子
            mViewHolder.mExampleLayout.setVisibility(View.VISIBLE);
            mViewHolder.mNodataLayout.setVisibility(View.GONE);
            mViewHolder.mSearchInputKey.setNextFocusRightId(R.id.layout_hotword_view);
            if (mHotWords == null || mHotWords.size() == 0) {
                mViewHolder.mImeBoard.setNextFocusRightId(View.NO_ID);
            } else {
                mViewHolder.mImeBoard.setNextFocusRightId(R.id.layout_hotword_view);
            }
            mViewHolder.mResultListView.setVisibility(View.GONE);
            mViewHolder.mBottomMaskView.setVisibility(View.GONE);
        } else if (id == R.id.nodata_lay) { // 无数据
            mViewHolder.mExampleLayout.setVisibility(View.GONE);
            mViewHolder.mNodataLayout.setVisibility(View.VISIBLE);
            mViewHolder.mImeBoard.setNextFocusRightId(View.NO_ID);
            mViewHolder.mRootLayout.requestFocus(mViewHolder.mImeBoard, View.FOCUS_LEFT);
            mViewHolder.mResultListView.setVisibility(View.GONE);
            mViewHolder.mBottomMaskView.setVisibility(View.GONE);
        } else if (id == R.id.result_listview) { // 返回数据
            mViewHolder.mExampleLayout.setVisibility(View.GONE);
            mViewHolder.mNodataLayout.setVisibility(View.GONE);
            mViewHolder.mResultListView.setVisibility(View.VISIBLE);
            mViewHolder.mImeBoard.setNextFocusRightId(mViewHolder.mResultListView.getId());
            mViewHolder.mBottomMaskView.setVisibility(View.VISIBLE);
            mAdapter.notifyDataSetChanged();
            mViewHolder.mResultListView.setSelection(0);

            showBottomMask();
        }
    }

    /**
     * 在XML中配置 键盘的点击事件
     * @param v
     */
    public void onImeItemClicked(View v) {
        Object tag = v.getTag();
        if (tag != null && TextUtils.isDigitsOnly(String.valueOf(tag))) {
            int itemPosition = Integer.parseInt(String.valueOf(tag));
            GridItem gridItem = sGridItems.get(itemPosition);
            if (gridItem.dispatchMode == GridItem.DISPATCH_CLEAR) {
                if (mImeKeyWordListener != null) {
                    mImeKeyWordListener.onClear();
                }
            } else if (gridItem.dispatchMode == GridItem.DISPATCH_DELETE) {
                if (mImeKeyWordListener != null) {
                    mImeKeyWordListener.onDelete();
                }
            } else {
                if (!gridItem.isExpand) {
                    if (mImeKeyWordListener != null) {
                        mImeKeyWordListener.onClicked(gridItem.center);
                    }
                } else {
                    showExpandPopup(v, gridItem);
                }
            }
        }
    }

    /**
     * 获取搜索结果
     */
    public void getSearchResult() {
        if (mViewHolder == null) {
            return;
        }
        String key = mViewHolder.mSearchInputKey.getText().toString();
        if (TextUtils.isEmpty(key.trim())) {
            showRight(R.id.search_example_layout);
            return;
        }

        OnWaitProgressDialog(true);
        String type = "";
        if ("1".equals(mType)) {// 天猫端的搜索
            type = "tmtv";
        } else {// 天猫超市搜索
            type = "tctv";
        }
        mBusinessRequest.requestGetTbSearchResultList(key.trim(), type, "utf-8", 50,
                new GetSearchResultRequestListener(new WeakReference<BaseActivity>(this)));
    }

    /**
     * 取消上一次的搜索
     */
    public void cancelSearch() {
    }

    /**
     * 进入搜索结果界面
     */
    private void gotoSearchResult(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            AppDebug.v(TAG, TAG + ". gotoSearchResult.keyword == null");
            return;
        }

        try {
            String url = "tvtaobao://home?module=goodsList&keywords=" + keyword + "&tab=mall&"
                    + CoreIntentKey.URI_FROM_APP + "=" + getAppName();
            // 如果搜索来自于天猫超市，则跳转到天猫超市的搜索结果页。
            if ("2".equals(mType)) {
                String page = URLEncoder.encode(PageMapConfig.getPageUrlMap().get("chaoshi_searchresult") + "?keyword="
                        + keyword);
                url = "tvtaobao://home?module=common&page=" + page + "&" + CoreIntentKey.URI_FROM_APP + "="
                        + getAppName();
            }

            AppDebug.v(TAG, TAG + ", gotoSearchResult.uri = " + url);
            Intent intent = new Intent();
            if (!TextUtils.isEmpty(mHuoDongName)) {
                setHuodong(mHuoDongName);
            }
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            AppDebug.v(TAG, TAG + ". gotoSearchResult." + getString(R.string.ytm_start_activity_error));
            Toast.makeText(SearchActivity.this, getString(R.string.ytm_start_activity_error), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    public OnFocusChangeListener mFocusChangeListener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            AppDebug.v(TAG, TAG + ".mFocusChangeListener.onFocusChange  v:" + v + ", hasFocus:" + hasFocus);
            if (mViewHolder == null) {
                return;
            }
            if (hasFocus) {
                if (v == mViewHolder.mResultListView) {// 当焦点在结果列表上时
                    mViewHolder.mRootLayout.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                            R.drawable.ytm_background_focus)));
                    mViewHolder.mResultListView.requestLayout();
                } else {
                    mViewHolder.mRootLayout.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                            R.drawable.ytm_touming)));
                }
            }
        }
    };

    /**
     * 显示界面上下的模板
     */
    private void showBottomMask() {
        if (mViewHolder == null) {
            return;
        }

        int firstVisiblePosition = mViewHolder.mResultListView.getFirstVisiblePosition();
        int lastVisiblePostion = mViewHolder.mResultListView.getLastVisiblePosition();
        int visibleCount = lastVisiblePostion - firstVisiblePosition + 1;
        int count = mViewHolder.mResultListView.getCount();
        AppDebug.v(TAG, TAG + ".showBottomMask.lastVisiblePostion = " + lastVisiblePostion
                + ", firstVisiblePosition = " + firstVisiblePosition + ", visibleCount = " + visibleCount + ",count = "
                + count);

        if (mResearch) {
            mResearch = false;
            if (count > 7) {// 每页显示行数为7
                if (!mViewHolder.mBottomMaskView.isShown()) {
                    mViewHolder.mBottomMaskView.stopAlpha();
                }
            } else {
                if (mViewHolder.mBottomMaskView.isShown()) {
                    mViewHolder.mBottomMaskView.playAlpha();
                }
            }
        } else {
            if (lastVisiblePostion >= count - 1) {
                if (mViewHolder.mBottomMaskView.isShown()) {
                    mViewHolder.mBottomMaskView.playAlpha();
                }
            } else {
                if (!mViewHolder.mBottomMaskView.isShown()) {
                    mViewHolder.mBottomMaskView.stopAlpha();
                }
            }
        }
    }

    @Override
    public String getPageName() {
        return "Search";
    }


    @Override
    public Map<String, String> getPageProperties() {

        Map<String, String> p = super.getPageProperties();
        if ("2".equals(mType)) {// 如果是天猫超市
            p.put("is_tmcs", "true");
        } else {
            p.put("is_tmcs", "false");
        }
        p.put(SPMConfig.SPM_CNT, SPMConfig.SEARCH_SPM+".0.0");
        return p;
    }


    /**
     * 初始化埋点信息
     *
     * @return
     */
    public Map<String, String> initTBSProperty() {

        Map<String, String> p = super.getPageProperties();
        if ("2".equals(mType)) {// 如果是天猫超市
            p.put("is_tmcs", "true");
        } else {
            p.put("is_tmcs", "false");
        }

        return p;

    }



//    @Override
//    public Map<String, String> getPageProperties() {
//        Map<String, String> p = super.getPageProperties();
//        if ("2".equals(mType)) {// 如果是天猫超市
//            p.put("is_tmcs", "true");
//        } else {
//            p.put("is_tmcs", "false");
//        }
//
//
//        return p;
//    }


    @Override
    protected void changedNetworkStatus(boolean available) {
        AppDebug.v(TAG, TAG + ".changedNetworkStatus.available = " + available);
        if (available) {// 重新加载数据
            if (mViewHolder == null || mViewHolder.mSearchInputKey == null) {
                return;
            }
            String key = mViewHolder.mSearchInputKey.getText().toString();
            // 如果输入的关键字为null，就更新热词页面
            if (key == null || TextUtils.isEmpty(key.trim())) {
                initHotWords();
            } else {
                getSearchResult();
            }
        }
    }

    class ViewHolder {

        ViewGroup mViewContents;

        FocusPositionManager mRootLayout;
        FocusListView mResultListView;
        CustomAlphaView mBottomMaskView;

        FocusRelativeLayout mImeBoard;
        ViewGroup mNodataLayout;
        ViewGroup mExampleLayout;
        ViewGroup mHotWordsLayoutWrap;
        HotWordView mHotWordView;
        EditText mSearchInputKey;
        ImageView mExampleImage;
        TextView mExampleTipText;

        ImeExpandView mPinyinExtendView;

        public ViewHolder() {
            initViewHolder();
        }

        void initViewHolder() {
            try {
                mViewContents = (ViewGroup) SearchActivity.this.getLayoutInflater().inflate(
                        R.layout.ytm_search_activity, null);

                mPinyinExtendView = (ImeExpandView) LayoutInflater.from(SearchActivity.this).inflate(
                        R.layout.ytm_ime_expand_view, null);
            } catch (InflateException e) {
                e.printStackTrace();
                return;
            }

            if (mViewContents == null) {
                return;
            }

            mRootLayout = (FocusPositionManager) mViewContents.findViewById(R.id.search_root);
            mResultListView = (FocusListView) mViewContents.findViewById(R.id.result_listview);
            mBottomMaskView = (CustomAlphaView) mViewContents.findViewById(R.id.favor_bottom_mask);

            mImeBoard = (FocusRelativeLayout) mViewContents.findViewById(R.id.ime_grid);
            mNodataLayout = (ViewGroup) mViewContents.findViewById(R.id.nodata_lay);
            mExampleLayout = (ViewGroup) mViewContents.findViewById(R.id.search_example_layout);
            mHotWordsLayoutWrap = (ViewGroup) mViewContents.findViewById(R.id.layout_hotwords_wrap);
            mHotWordView = (HotWordView) mViewContents.findViewById(R.id.layout_hotword_view);
            mSearchInputKey = (EditText) mViewContents.findViewById(R.id.search_input_key);
            mExampleImage = (ImageView) mViewContents.findViewById(R.id.example_image);
            mExampleTipText = (TextView) mViewContents.findViewById(R.id.example_tip_text);

            mRootLayout.setSelector(new StaticFocusDrawable(getResources().getDrawable(R.drawable.ytm_touming)));
            Spanned spannedPinyin = Html.fromHtml(getResources().getString(R.string.ytm_search_input_tip_text_pinyin));
            if (mExampleTipText != null) {
                mExampleTipText.setText(spannedPinyin);
            }

            mImeBoard.setFirstSelectedView(mImeBoard.findViewById(R.id.item5));
            mImeBoard.setOnFocusChangeListener(mFocusChangeListener);
            mHotWordView.setHotWordLines(3);

            mHotWordView.setOnFocusChangeListener(new OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    AppDebug.v(TAG, TAG + ".============onFocusChange.view = " + ".hasFocus = " + hasFocus);
                }
            });

            mSearchInputKey.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    cancelSearch();
                }

                @Override
                public void afterTextChanged(Editable s) {
                    getSearchResult();
                }
            });
        }

        // 改变键盘状态
        void changeImeBoardStatus(boolean bShowPopWindow) {
            for (int i = 0; i < mImeBoard.getChildCount(); i++) {
                View view = mImeBoard.getChildAt(i);
                if (bShowPopWindow) {
                    view.setEnabled(false);
                } else {
                    view.setEnabled(true);
                }
            }
        }
    }

    /**
     * 获取热门词
     * Class Descripton.
     * @version
     * @author mi.cao
     * @data 2015年2月12日 下午3:51:08
     */
    private static class GetHotwordsRequestListener extends BizRequestListener<ArrayList<String>> {

        public GetHotwordsRequestListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            SearchActivity activity = (SearchActivity) mBaseActivityRef.get();
            if (activity != null) {
                AppDebug.v(activity.TAG, activity.TAG + ",GetHotwordsRequestListener,onError resultCode = "
                        + resultCode + ", msg = " + msg);
                if (activity.isFinishing()) {
                    return true;
                }
                activity.mHotWordLoading = false;
            }

            return true;
        }

        @Override
        public void onSuccess(ArrayList<String> data) {
            SearchActivity activity = (SearchActivity) mBaseActivityRef.get();
            if (activity != null) {
                AppDebug.v(activity.TAG, activity.TAG + ",GetHotwordsRequestListener,onSuccess data = " + data);
                if (activity.isFinishing()) {
                    return;
                }
                activity.mHotWordLoading = false;
                ArrayList<String> list = (ArrayList<String>) data;
                if (activity.mHotWords != null) {
                    activity.mHotWords.clear();
                }

                activity.mHotWords = list;
                activity.showRight(R.id.search_example_layout);

                if (list != null && list.size() > 0) {
                    activity.updateHotWordsView();
                }
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    /**
     * 获取搜索结果列表
     * Class Descripton.
     * @version
     * @author mi.cao
     * @data 2015年2月12日 下午4:11:19
     */
    private static class GetSearchResultRequestListener extends BizRequestListener<ArrayList<String>> {

        public GetSearchResultRequestListener(WeakReference<BaseActivity> baseActivityRef) {
            super(baseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            SearchActivity activity = (SearchActivity) mBaseActivityRef.get();
            if (activity != null) {
                AppDebug.v(activity.TAG, activity.TAG + ",GetSearchResultRequestListener,onError resultCode = "
                        + resultCode + ", msg = " + msg);
                if (activity.isFinishing()) {
                    return true;
                }
                activity.OnWaitProgressDialog(false);
            }

            return false;
        }

        @Override
        public void onSuccess(ArrayList<String> data) {
            SearchActivity activity = (SearchActivity) mBaseActivityRef.get();
            if (activity != null) {
                AppDebug.v(activity.TAG, activity.TAG + ",GetSearchResultRequestListener,onSuccess data = " + data);
                if (activity.isFinishing()) {
                    return;
                }
                activity.OnWaitProgressDialog(false);

                ArrayList<String> resultList = data;

                if (activity.mViewHolder == null || activity.mViewHolder.mSearchInputKey == null
                        || activity.mAdapter == null) {
                    return;
                }

                String key = activity.mViewHolder.mSearchInputKey.getText().toString();
                if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(key.trim())) { // 判断关键字在结果列表中是否存在，如果不存在 则 加在列表末尾。
                    key = key.trim();
                    if (resultList == null) {
                        resultList = new ArrayList<String>();
                    }
                    boolean haveKeyword = false; // 假设列表中不含有关键字字符
                    for (int i = 0; i < resultList.size(); i++) {
                        String result = resultList.get(i);
                        if (key.compareToIgnoreCase(result) == 0) {
                            haveKeyword = true; // 列表中含有关键字
                            break;
                        }
                    }

                    if (!haveKeyword) { // 如果不含有
                        resultList.add(key);
                    }
                }

                activity.mAdapter.setSearchItemList(resultList);
                if (resultList == null || resultList.size() == 0) {
                    activity.showRight(R.id.nodata_lay);
                } else {
                    activity.mResearch = true;
                    activity.showRight(R.id.result_listview);
                }
            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }


    @Override
    protected FocusPositionManager getFocusPositionManager() {
        if (mViewHolder != null) {
            return mViewHolder.mRootLayout;
        }
        return null;
    }
}
