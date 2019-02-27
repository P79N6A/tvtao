package com.tvtaobao.voicesdk.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.gson.reflect.TypeToken;
import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.R;
import com.tvtaobao.voicesdk.adapter.SearchAdapter;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.bo.JinnangDo;
import com.tvtaobao.voicesdk.bo.PageReturn;
import com.tvtaobao.voicesdk.bo.ProductDo;
import com.tvtaobao.voicesdk.bo.SearchObject;
import com.tvtaobao.voicesdk.dialogs.base.BaseDialog;
import com.tvtaobao.voicesdk.register.LPR;
import com.tvtaobao.voicesdk.register.bo.Register;
import com.tvtaobao.voicesdk.register.type.ActionType;
import com.tvtaobao.voicesdk.register.type.RegisterType;
import com.tvtaobao.voicesdk.request.VoiceSearch;
import com.tvtaobao.voicesdk.utils.DialogManager;
import com.tvtaobao.voicesdk.utils.GsonUtil;
import com.tvtaobao.voicesdk.utils.LogPrint;
import com.tvtaobao.voicesdk.utils.Util;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.config.TvOptionsChannel;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.ActivityPathRecorder;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.BusinessRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by pan on 2017/10/27.
 */

public class SearchDialog extends BaseDialog {
    private final String TAG = "SearchDialog";

    private Context mContext;

    private RecyclerView mListView;
    private SearchAdapter mAdapter;

    //private AutoTextView mReply;
    private ViewFlipper mReplyFlipper;
    private LinearLayout mSelectContainerLayout;
    private TextView mSearchReplyTxt;

    private int currentPage = 1; //当前第几页
    private List<ProductDo> totalProduct;
    private String currentKeyWords;

    private List<String> tips = new ArrayList<>();

    private List<JinnangDo> jinnangList;

    private Timer mTimer;

    private SearchObject searchObject;

    private enum FlipperFlag {
        JINNANG,
        REPLY,

    }

    public SearchDialog(Context context) {
        super(context);
        this.mContext = context;

        this.setContentView(R.layout.dialog_search);
        initView();
    }

    public void initView() {
        Log.d(TAG, TAG + ".initView");
        TvOptionsConfig.setTvOptionsVoice(true);
        TvOptionsConfig.setTvOptionsSystem(false);
        TvOptionsConfig.setTvOptionsChannel(TvOptionsChannel.VOICE_SEARCH_ORDER);
        mListView = (RecyclerView) findViewById(R.id.voice_card_products);
        mReplyFlipper = findViewById(R.id.search_reply_flipper);
        mSelectContainerLayout = findViewById(R.id.select_container_layout);
        mSearchReplyTxt = findViewById(R.id.search_reply_txt);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mListView.setLayoutManager(layoutManager);
        mListView.addItemDecoration(new SpaceItemDecoration(Util.dip2px(mContext, 36),
                Util.dip2px(mContext, 32), 0, Util.dip2px(mContext, 32)));
        mAdapter = new SearchAdapter(mContext, this);

        mListView.setAdapter(mAdapter);
    }

    @Override
    public void show() {
        super.show();
        DialogManager.getManager().pushDialog(this);
    }

    @Override
    public PageReturn onASRNotify(DomainResultVo object) {
        PageReturn pageReturn = super.onASRNotify(object);
        switch (object.getIntent()) {
            case ActionType.JINNANG:
                String keyword = object.getResultVO().getKeywords();
                LogPrint.e(TAG, TAG + ".onASRReturn contains jingnang : " + keyword);
                requestSearch(keyword);

                Map<String, String> map = getProperties();
                map.put("screencase", keyword);
                Utils.utCustomHit("VoiceCard_search_screen", map);

                pageReturn.isHandler = true;
                pageReturn.feedback = "正在帮您搜索";
                break;
            case ActionType.NEXT_PAGE:
                onNextPage(false);

                pageReturn.isHandler = true;
                pageReturn.feedback = "好的";
                break;
            case ActionType.PREVIOUS_PAGE:
                onPreviousPage(false);

                pageReturn.isHandler = true;
                pageReturn.feedback = "好的";
                break;
            case ActionType.OPEN_INDEX:
                mAdapter.setClickType(1);
                gotoAction(Integer.parseInt(object.getResultVO().getNorm()));

                pageReturn.isHandler = true;
                pageReturn.feedback = "正在为您跳转详情";
                break;
            case ActionType.BUY_INDEX:
                mAdapter.setClickType(0);
                gotoAction(Integer.parseInt(object.getResultVO().getNorm()));

                pageReturn.isHandler = true;
                pageReturn.feedback = "正在为您跳转购买";
                break;
            case ActionType.HAVE_MORE:
                gotoSearchActivity(currentKeyWords);
                dismiss();

                pageReturn.isHandler = true;
                pageReturn.feedback = "正在为您跳转搜索";
                break;
            case ActionType.GOODS_SEARCH_SIFT:
                //TODO 语点注册需要修改
                requestSortSearch(null, null);

                pageReturn.isHandler = true;
                pageReturn.feedback = "正在为您跳转购买";
                break;
        }

        return pageReturn;
    }

    public void requestSearch(String keywords) {
        if (searchObject == null) {
            searchObject = new SearchObject();
        }
        searchObject.clearSift();
        searchObject.keyword = keywords;

        requestSearch(searchObject);
    }

    /**
     * 进行排序搜索
     * @param priceScope
     * @param sorting
     */
    public void requestSortSearch(String priceScope, String sorting) {
        if (searchObject == null) {
            searchObject = new SearchObject();
        }
        searchObject.clearSift();
        searchObject.keyword = currentKeyWords;
        searchObject.priceScope = priceScope;
        searchObject.sorting = sorting;

        requestSearch(searchObject);
    }

    public void requestSearch(SearchObject searchObject) {
        this.searchObject = searchObject;
        BusinessRequest.getBusinessRequest().baseRequest(
                new VoiceSearch(searchObject, TvOptionsConfig.getTvOptions()), new SearchRequestListener(), false);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (mListView != null) {
            int childcount = mListView.getChildCount();

            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
//                LogPrint.d(TAG, TAG + ".dispatchKeyEvent childView.isFocused : " + mListView.getChildAt(childcount - 1).isFocused());
                if (mListView.getChildAt(childcount - 1) != null && mListView.getChildAt(childcount - 1).isFocused()) {
                    onNextPage(true);
                }
            }

            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
//                LogPrint.d(TAG, TAG + ".dispatchKeyEvent childView.isFocused : " + mListView.getChildAt(0).isFocused());
                if (mListView.getChildAt(0) != null
                        && mListView.getChildAt(0).isFocused()) {
                    onPreviousPage(true);
                }
            }

            LogPrint.d(TAG, TAG + ".dispatchKeyEvent mListView.hasFocus : " + mListView.hasFocus());
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mListView.isFocused()) {
                    setFocusable(true);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                setSelectItemFocus(true);
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 换页，上一页
     */
    public void onPreviousPage(boolean isOnKey) {
        if (currentPage > 1) {
            currentPage--;

            int startIndex = 3 * (currentPage - 1);

            List<ProductDo> productDos = totalProduct.subList(startIndex, startIndex + 3);
            mAdapter.setAction(isOnKey);
            mAdapter.initData(productDos);
            View firstView = mListView.getChildAt(0);
            if (firstView != null) {
                firstView.setFocusable(true);
                firstView.requestFocus();
            }
            setSelectItemFocus(false);
        }
    }

    /**
     * 换页，下一页
     */
    private boolean onNextPage(boolean isOnKey) {
        LogPrint.e(TAG, TAG + ".onNextPage");
        currentPage++;

        int startIndex = 3 * (currentPage - 1);
        if (startIndex > totalProduct.size()) {
            currentPage--;
            return true;
        }

        List<ProductDo> productDos = null;
        if (totalProduct.size() - startIndex >= 3) {
            productDos = totalProduct.subList(startIndex, startIndex + 3);
        } else {
            productDos = totalProduct.subList(startIndex, totalProduct.size());
        }
        mAdapter.setAction(isOnKey);
        mAdapter.initData(productDos);
        View firstView = mListView.getChildAt(0);
        if (firstView != null) {
            firstView.setFocusable(true);
            firstView.requestFocus();
        }
        setSelectItemFocus(false);
        return true;
    }

    private void setSelectItemFocus(boolean focus) {
        int childCount = mSelectContainerLayout.getChildCount();
        boolean hasFocusView = false;
        int focusViewPosition = 0;
        for (int i = 0; i < childCount; i++) {
            View itemview = mSelectContainerLayout.getChildAt(i);
            itemview.setFocusable(focus);
            boolean itemfocus = itemview.isFocused();
            if (itemfocus) {
                hasFocusView = true;
                focusViewPosition = i;
            }
        }
        View nextView = null;
        if (!hasFocusView) {
            if (mListView.getChildAt(0) != null && mListView.getChildAt(0).isFocused()) {
                nextView = mSelectContainerLayout.getChildAt(0);
            } else if (mListView.getChildAt(1) != null && mListView.getChildAt(1).isFocused()) {
                nextView = mSelectContainerLayout.getChildAt(3);
            } else if (mListView.getChildAt(2) != null && mListView.getChildAt(2).isFocused()) {
                nextView = mSelectContainerLayout.getChildAt(childCount - 1);
            }
        }
        if (nextView != null) {
            nextView.requestFocus();
        }
    }

    public void setData(String spoken, String keywords, List<ProductDo> data, List<JinnangDo> jinnangDos) {
        if (totalProduct == null)
            totalProduct = new ArrayList<>();

        totalProduct.clear();
        this.currentKeyWords = keywords;
        this.currentPage = 1;
        totalProduct.addAll(data);
        this.jinnangList = jinnangDos;
        if (data.size() == 0) {
            tips.clear();
            //tips.add("抱歉，没有搜索到与" + keywords + "相关的商品");
            //dealASRResult(null,"抱歉，没有搜索到与" + keywords + "相关的商品");
            tips.add(spoken);
            dealASRResult(null, spoken);
            return;
        } else {
            tips.clear();
            tips.add(spoken);
            //tips.add("为您找到以下几款" + keywords + "，确定购买后为您直接下单，您要买第几个？");
            if (jinnangDos.size() >= 3) {
                tips.add("您还可以说:\r\r\r" + jinnangDos.get(0).getName() + "\r\r|\r\r" + jinnangDos.get(1).getName() + "\r\r|\r\r" + jinnangDos.get(2).getName() + "\r\r\r找到您最想要的");
            }
            //dealASRResult(jinnangDos,"为您找到以下几款" + keywords + "，确定购买后为您直接下单，您要买第几个？");
            dealASRResult(jinnangDos, spoken);
            Register register = new Register();
            ConcurrentHashMap<String, String> map = register.getRegistedMap();
            for (int i = 0; i < jinnangDos.size(); i++) {
                String name = jinnangDos.get(i).getName();
                String content = jinnangDos.get(i).getContent();
                map.put(name, content);
            }

            register.setRegistedMap(map);
            register.resgistedType = RegisterType.ADD;
            register.className = SearchDialog.class.getCanonicalName();
            register.bizType = "jinNang";
            LPR.getInstance().registed(register);
        }

        List<ProductDo> productDos;
        if (data.size() > 3) {
            productDos = data.subList(0, 3);
        } else {
            productDos = data;
        }

        mAdapter.setKeyWords(keywords);
        mAdapter.initData(productDos);

        LogPrint.e(TAG, TAG + ".VoiceSearchListener.onSuccess size : " + data.size());
    }


    private void dealASRResult(List<JinnangDo> jinnangDos, String reply) {
        rebuildFlipperView(jinnangDos, reply);
        displayReplyView(jinnangDos);
        setPrompt(tips);
    }


    public void setPrompt(List<String> reply) {
        LogPrint.d(TAG, TAG + ".setPrompt");
        if (reply != null && reply.size() > 0) {
            //mReply.autoScroll(reply);
            ASRNotify.getInstance().playTTS(reply.get(0));
        }
    }

    private void rebuildFlipperView(List<JinnangDo> jinnangDos, String reply) {
        mSelectContainerLayout.removeAllViews();
        if (reply != null) {
            mSearchReplyTxt.setText(reply);
        }
        if (jinnangDos != null) {
            int textLenght = 6;
            int textsize = mContext.getResources().getDimensionPixelSize(R.dimen.sp_22);
            int margin = mContext.getResources().getDimensionPixelSize(R.dimen.dp_10);
            int padding = mContext.getResources().getDimensionPixelSize(R.dimen.dp_22);
            int height = mContext.getResources().getDimensionPixelSize(R.dimen.dp_37);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
            lp.setMargins(margin, 0, 0, 0);
            lp.gravity = Gravity.CENTER;
            for (int i = 0; i < jinnangDos.size(); i++) {
                final JinnangDo jinnangDo = jinnangDos.get(i);
                TextView jinnangTxt = new TextView(mContext);
                jinnangTxt.setText(jinnangDo.getName());
                jinnangTxt.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
                jinnangTxt.setTextColor(0xffffffff);
                jinnangTxt.setIncludeFontPadding(false);
                jinnangTxt.setFocusable(true);
                jinnangTxt.setPadding(padding, 0, padding, 0);
                jinnangTxt.setGravity(Gravity.CENTER);
                jinnangTxt.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.item_jingnang_background));
                jinnangTxt.setLayoutParams(lp);
                jinnangTxt.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SearchObject searchObject = new SearchObject();
                        searchObject.keyword = jinnangDo.getContent();
                        searchObject.endIndex = 30;
                        BusinessRequest.getBusinessRequest().baseRequest(new VoiceSearch(searchObject, TvOptionsConfig.getTvOptions()), new SearchRequestListener(), false);
                        //mContext.requestSearch(jinnangDo.getContent());
                    }
                });

                mSelectContainerLayout.addView(jinnangTxt);

                if (i >= 7)
                    break;

                if (textLenght > 26) {
                    break;
                } else {
                    textLenght += jinnangDos.get(i).getName().length();
                }
            }
        }

    }

    /**
     * 控制replayView和锦囊的显示
     */
    private void displayReplyView(List<JinnangDo> jinnangDos) {
        int size = 0;
        if (jinnangDos != null) {
            size = jinnangDos.size();
        }
        mReplyFlipper.showNext();// 显示下一组件
        if (mTimer == null) {
            mTimer = new Timer();
        }
        if (size > 0) {
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mReplyFlipper.showNext();// 显示下一组件
                        }
                    });
                }
            }, 3000);
        }

    }

    /**
     * 去对第几个做操作
     *
     * @param num
     */
    private void gotoAction(int num) {
        int childCount = mListView.getChildCount();
        LogPrint.e(TAG, TAG + ".gotoAction num : " + num + " ,childCount : " + childCount);

        if (num > childCount) {
            return;
        }
        try {
            RecyclerView.ViewHolder mHolder = mListView.getChildViewHolder(mListView.getChildAt(num - 1));
            LogPrint.e(TAG, TAG + ".gotoAction ItemHolder : " + mHolder);
            mHolder.itemView.performClick();
        } catch (Exception e) {
            LogPrint.e(TAG, TAG + ".gotoAction Exception : " + e.getMessage());
        }
    }

    private void setFocusable(boolean focusable) {
        int count = mListView.getChildCount();

        for (int i = 0; i < count; i++) {
            mListView.getChildAt(i).setFocusable(focusable);
        }
    }

    /**
     * 进入搜索结果界面
     */
    private void gotoSearchActivity(String keyword) {
        if (mContext == null)
            return;

        try {

            String url = "tvtaobao://home?module=goodsList&keywords=" + keyword + "&tab=mall";

            AppDebug.v(TAG, TAG + ", gotoSearchResult.uri = " + url);
            Intent intent = new Intent();

            intent.setData(Uri.parse(url + "&from_voice=voice"));
            intent.putExtra(ActivityPathRecorder.INTENTKEY_FIRST, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class SearchRequestListener implements RequestListener<JSONObject> {

        @Override
        public void onRequestDone(JSONObject data, int resultCode, String msg) {
            LogPrint.i(TAG, TAG + ".SearchResult resultCode : " + resultCode + " .msg : " + msg);
            if (resultCode == 200) {
                try {
                    String keyword = data.getString("keyword");
                    String spoken = data.getString("spoken");

                    List<ProductDo> mProducts = new ArrayList<>();
                    if (data.has("model")) {
                        JSONArray model = data.getJSONArray("model");
                        LogPrint.e(TAG, TAG + ".SearchResponse size : " + model.length());
                        for (int i = 0; i < model.length(); i++) {
                            mProducts.add(GsonUtil.parseJson(model.getJSONObject(i).toString(), new TypeToken<ProductDo>() {
                            }));
                        }
                    }

                    List<JinnangDo> mJinnangs = new ArrayList<>();
                    if (data.has("jinNangItems")) {
                        JSONArray jinnang = data.getJSONArray("jinNangItems");
                        for (int i = 0; i < jinnang.length(); i++) {
                            mJinnangs.add(GsonUtil.parseJson(jinnang.getJSONObject(i).toString(), new TypeToken<JinnangDo>() {
                            }));
                        }
                    }

                    setData(spoken, keyword, mProducts, mJinnangs);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    /**
     * 设置items间距
     */
    class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        int left, top, right, bottom;

        /**
         * Retrieve any offsets for the given item. Each field of <code>outRect</code> specifies
         * the number of pixels that the item view should be inset by, similar to padding or margin.
         * The default implementation sets the bounds of outRect to 0 and returns.
         * <p>
         * <p>
         * If this ItemDecoration does not affect the positioning of item views, it should set
         * all four fields of <code>outRect</code> (left, top, right, bottom) to zero
         * before returning.
         * <p>
         * <p>
         * If you need to access Adapter for additional data, you can call
         * {@link RecyclerView#getChildAdapterPosition(View)} to get the adapter position of the
         * View.
         *
         * @param outRect Rect to receive the output.
         * @param view    The child view to decorate
         * @param parent  RecyclerView this ItemDecoration is decorating
         * @param state   The current state of RecyclerView.
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = left;
            outRect.bottom = bottom;
            outRect.top = top;
            outRect.right = right;
        }

        public SpaceItemDecoration(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = null;
    }
}
