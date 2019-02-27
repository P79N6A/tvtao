package com.yunos.tvtaobao.homebundle.detainment;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.AdapterView.OnItemClickListener;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.blitz.account.LoginHelper;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.TvOptionsChannel;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.dialog.util.SnapshotUtil;
import com.yunos.tvtaobao.biz.request.bo.DetainMentBo;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeFieldsVO;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeGoodsBean;
import com.yunos.tvtaobao.homebundle.R;
import com.yunos.tvtaobao.homebundle.activity.HomeActivity;
import com.yunos.tvtaobao.payment.config.DebugConfig;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DetainMentDialog extends Dialog {

    private Context mContext;
    private final static String TAG = "DetainMentDialog";

    private final static String PAGENAME = "Home";
    public final static String OPT_RESULT = "opt_result";
    public final static int RESULT_CODE = 10;
    public final static int REQUEST_CODE = 1;

    private FocusPositionManager mFocusPositionManager;
    private DetainMentListView mFocusHListView;
    private DetainMentSyncLoginListener mDetainMentSyncLoginListener;

    private DetainMentFronstedGlassListenner mDetainMentFronstedGlassListenner;

    private DetainmentDataBuider mDetainmentDataBuider;

    private TouchFocusLinearLayout mFocusLinearLayout;
    private DetainMentAdapter mDetainMentAdapter;
    private DetainMentBo[] mDetainMentBo_Data;


    public DetainMentDialog(Context context) {
        super(context, R.style.ytbv_homeDialog);
        onInitDetainMentActivity(context);
    }

    public void onDestroy() {
        if (mDetainMentFronstedGlassListenner != null) {
            mDetainMentFronstedGlassListenner.onRecycleBitmap();
        }
        if (mDetainMentFronstedGlassListenner != null) {
            mDetainMentFronstedGlassListenner.onDestroy();
        }

        if (mDetainMentAdapter != null) {
            mDetainMentAdapter.onDestroy();
        }

        removeAccountListen();
    }

    @Override
    public void show() {
        super.show();
        Utils.utCustomHit("Expore_Exit", getProperties());

        if (mContext != null && mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            SnapshotUtil.getFronstedSreenShot(new WeakReference<Activity>(activity), 5, 0,
                    mDetainMentFronstedGlassListenner);
        }

        if (mDetainMentAdapter == null) {
            mDetainMentAdapter = new DetainMentAdapter(mContext);
            mFocusHListView.setAdapter(mDetainMentAdapter);
        }
        if (mFocusLinearLayout != null) {
            mFocusLinearLayout.setOnFocusChanged();
        }

        AppDebug.v(TAG, TAG + ".show --> mDetainMentBo_Data = " + mDetainMentBo_Data);
        if (mDetainMentBo_Data != null && mDetainMentAdapter != null) {
            mDetainMentAdapter.setData(mDetainMentBo_Data);
            mDetainMentAdapter.notifyDataSetChanged();
            firstRequestFocus();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void onBackPressed() {
        View focusView = mFocusPositionManager.getFocused();
        if (focusView != null && focusView instanceof DetainMentListView) {
            firstRequestFocus();
        } else {
            Utils.utControlHit(PAGENAME, "Button_Back", getProperties());
            if (mContext != null && mContext instanceof HomeActivity) {
                HomeActivity homeActivity = (HomeActivity) mContext;
                homeActivity.handleExit();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK&&!DebugConfig.whetherIsMonkey()) {
            onBackPressed();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event != null) {
            int keycode = event.getKeyCode();
            if (keycode == KeyEvent.KEYCODE_DPAD_DOWN || keycode == KeyEvent.KEYCODE_DPAD_UP
             || keycode == KeyEvent.KEYCODE_DPAD_LEFT || keycode == KeyEvent.KEYCODE_DPAD_RIGHT ) {
                if (mFocusPositionManager != null && !mFocusPositionManager.IsFocusStarted()) {
                    mFocusPositionManager.focusShow();
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev != null) {
            int action = ev.getAction();
            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                if (mFocusPositionManager != null && mFocusPositionManager.IsFocusStarted()) {
                    mFocusPositionManager.focusHide();
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    public boolean hasData() {
        if (mDetainMentAdapter != null && !mDetainMentAdapter.isEmpty()) {
            // 适配器中有数据(即之前显示过)
            return true;
        } else if (mDetainMentBo_Data != null && mDetainMentBo_Data.length > 0) {
            // 缓冲中有数据(之前没有显示过)
           return true;
        }
        return false;
    }

    private void addAccountListen() {
        if (mDetainMentSyncLoginListener == null) {
            mDetainMentSyncLoginListener = new DetainMentSyncLoginListener(new WeakReference<DetainMentDialog>(this));
        }
        CoreApplication.getLoginHelper(mContext.getApplicationContext()).addSyncLoginListener(
                mDetainMentSyncLoginListener);
    }

    private void removeAccountListen() {
        if (mDetainMentSyncLoginListener != null) {
            CoreApplication.getLoginHelper(mContext.getApplicationContext()).removeSyncLoginListener(
                    mDetainMentSyncLoginListener);
        }
    }

    private void onInitDetainMentActivity(Context context) {
        setContentView(R.layout.ytm_activity_detainment);

        mContext = context;
        mFocusPositionManager = (FocusPositionManager) findViewById(R.id.detainment_focusmanager);
        mFocusPositionManager.setSelector(new StaticFocusDrawable(mContext.getResources().getDrawable(
                R.drawable.ytbv_common_focus)));

        mDetainMentFronstedGlassListenner = new DetainMentFronstedGlassListenner(new WeakReference<View>(
                mFocusPositionManager));

        onInitDetainMent();

        addAccountListen();

        mDetainMentBo_Data = null;

        mDetainmentDataBuider = new DetainmentDataBuider(context.getApplicationContext());

        onRequestDetainMent();
    }

    private void onInitDetainMent() {
        mFocusHListView = (DetainMentListView) findViewById(R.id.listview);

        int spacing = mContext.getResources().getDimensionPixelSize(R.dimen.dp_18);
        mFocusHListView.setSpacing(spacing);

        mFocusHListView.setFlipScrollFrameCount(3);

        mFocusHListView.setOnItemSelectedListener(new ItemSelectedListener() {

            @Override
            public void onItemSelected(View v, int position, boolean isSelected, View view) {
                if (!isSelected) {
                    HandleSelectView(v, position, isSelected);
                } else {
                    if (position < DetainMentAdapter.PING_COUNT
                            || position > (DetainMentAdapter.TOTAL_COUNT - DetainMentAdapter.PING_COUNT / 2)) {
                        HandleSelectView(v, position, isSelected);
                        if (isSelected && mFocusPositionManager != null) {
                            mFocusPositionManager.forceDrawFocus();
                        }
                    }
                }
            }
        });

        mFocusHListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(ViewGroup view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    int position = mFocusHListView.getSelectedItemPosition();
                    View itemView = mFocusHListView.getSelectedView();
                    HandleSelectView(itemView, position, true);
                    if (mFocusPositionManager != null) {
                        mFocusPositionManager.forceDrawFocus();
                    }
                }
            }

            @Override
            public void onScroll(ViewGroup view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        mFocusHListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mFocusHListView != null) {
                    ListAdapter listAdapter = mFocusHListView.getAdapter();
                    if (listAdapter != null) {
                        Object object = listAdapter.getItem(position);
                        if (object != null && object instanceof DetainMentBo) {
                            DetainMentBo detainMentBo = (DetainMentBo) object;
                            String itemId = detainMentBo.getNid();
                            enterDisplayDetail(itemId);

                            Map<String, String> pMap = getProperties();
                            pMap.put("item_id", itemId);
                            pMap.put("position", position + "");
                            Utils.utControlHit(PAGENAME, "Button_Card", pMap);
                        }

                    }
                }
            }
        });

        mFocusHListView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg1) {
                    int position = mFocusHListView.getSelectedItemPosition();
                    View itemView = mFocusHListView.getSelectedView();
                    HandleSelectView(itemView, position, true);
                }
            }
        });

        DetainMentTextView detainMentTextView_leave = (DetainMentTextView) findViewById(R.id.leave_app);

        detainMentTextView_leave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Utils.utControlHit(PAGENAME, "Button_Leave", getProperties());
                if (mContext != null && mContext instanceof HomeActivity && !DebugConfig.whetherIsMonkey()) {
                    HomeActivity homeActivity = (HomeActivity) mContext;
                    homeActivity.handleExit();
                }
            }
        });

        DetainMentTextView detainMentTextView_shopping = (DetainMentTextView) findViewById(R.id.goshoping);
        detainMentTextView_shopping.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Utils.utControlHit(PAGENAME, "Button_Stay", getProperties());
                dismiss();
            }
        });

        initButton();
    }

    /**
     * 进入详情页
     *
     * @param itemId 商品的ID号
     */
    private void enterDisplayDetail(String itemId) {

        AppDebug.i(TAG, "enterDisplayDetail itemId  = " + itemId);

        if (mContext != null && mContext instanceof HomeActivity) {
            HomeActivity baseActivity = (HomeActivity) mContext;
            if (TextUtils.isEmpty(itemId)) {
                return;
            }
            if (!NetWorkUtil.isNetWorkAvailable()) {
                baseActivity.showNetworkErrorDialog(false);
                return;
            }

            try {
                String url = "tvtaobao://home?app=taobaosdk&module=detail&itemId=" + itemId;
                AppDebug.i("url", "url = " + url);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                TvOptionsConfig.setTvOptionsChannel(TvOptionsChannel.GUESSLIKE);
//                intent.putExtra(CoreIntentKey.URI_FROM_APP_BUNDLE, );
                baseActivity.startActivity(intent);
                return;
            } catch (Exception e) {
                String name = baseActivity.getResources().getString(R.string.ytbv_not_open);
                Toast.makeText(baseActivity, name, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 处理选中的条目
     *
     * @param position
     * @param isSelect
     */
    private void HandleSelectView(View selectview, int position, boolean isSelect) {

        AppDebug.i(TAG, "HandleSelectView  -->    position = " + position + "; isSelect  = " + isSelect
                + "; selectview = " + selectview);

        if (selectview instanceof DetainMentItemLayout && selectview != null) {
            DetainMentItemLayout detainMentItemLayout = (DetainMentItemLayout) selectview;
            detainMentItemLayout.setSelect(isSelect);

            TextView title = (TextView) detainMentItemLayout.findViewById(R.id.detainment_item_title);
            if (title == null) {
                return;
            }

            int item_W = selectview.getWidth();
            if (isSelect) {
                int select_H = selectview.getResources().getDimensionPixelSize(R.dimen.dp_85);
                int select_top = selectview.getResources().getDimensionPixelSize(R.dimen.dp_184);
                FrameLayout.LayoutParams titleLp = new FrameLayout.LayoutParams(item_W, select_H);
                titleLp.setMargins(0, select_top, 0, 0);
                title.setLayoutParams(titleLp);
                title.setLines(2);
            } else {
                int no_select_H = selectview.getResources().getDimensionPixelSize(R.dimen.dp_50);
                int no_select_top = selectview.getResources().getDimensionPixelSize(R.dimen.dp_222);
                FrameLayout.LayoutParams titleLp = new FrameLayout.LayoutParams(item_W, no_select_H);
                titleLp.setMargins(0, no_select_top, 0, 0);
                title.setLayoutParams(titleLp);
                title.setLines(1);
            }
            detainMentItemLayout.invalidate();
        }
    }

    public void getAccount() {
        CoreApplication.getLoginHelper(mContext.getApplicationContext()).login(mContext);
    }

    public void onRequestDetainMent() {
        String current_userId = User.getUserId();
        AppDebug.v(TAG, TAG + ".onRequestDetainMent --> current_userId = " + current_userId);
        if (mDetainmentDataBuider != null) {
            if (mContext != null && mContext instanceof BaseActivity) {
                mDetainmentDataBuider.onRequestData(current_userId, new DetainmentRequestDonelistener(
                        new WeakReference<DetainMentDialog>(DetainMentDialog.this)));
            }
        }
    }

    public void backgroundRequest() {
        if (mDetainmentDataBuider != null) {
            mDetainmentDataBuider.checkDetainmentData();
        }
    }

    private void firstRequestFocus() {
        DetainMentTextView detainMentTextView_shopping = (DetainMentTextView) findViewById(R.id.goshoping);
        detainMentTextView_shopping.setSelected(false);
        detainMentTextView_shopping.setScaleX(1.0f);
        detainMentTextView_shopping.setScaleY(1.0f);

        DetainMentTextView detainMentTextView_leave = (DetainMentTextView) findViewById(R.id.leave_app);
        detainMentTextView_leave.setSelected(true);

        if (mFocusPositionManager != null && mFocusPositionManager.IsFocusStarted()) {
            mFocusPositionManager.setFirstFocusChild(mFocusLinearLayout);
            mFocusLinearLayout.forceInitNode();

            setButtonBackground(detainMentTextView_shopping, false);
            setButtonBackground(detainMentTextView_leave, true);

            mFocusLinearLayout.setSelectedView(detainMentTextView_leave, 0);
            mFocusPositionManager.requestFocus(mFocusLinearLayout, View.FOCUS_DOWN);

            AppDebug.i(TAG, "firstRequestFocus -->   detainMentTextView_leave = " + detainMentTextView_leave
                    + "; detainMentTextView_shopping = " + detainMentTextView_shopping);

        } else {
            setButtonBackground(detainMentTextView_shopping, false);
            setButtonBackground(detainMentTextView_leave, false);
        }
    }

    private void setButtonBackground(View button, boolean isSelected) {
        if (button != null) {
            if (isSelected && mFocusPositionManager != null && mFocusPositionManager.IsFocusStarted()) {
                button.setBackgroundResource(R.drawable.ytm_button_common_focus);
            } else {
                button.setBackgroundResource(R.drawable.ytm_detainment_button_bg);
            }
        }
    }

    private void initButton() {
        mFocusLinearLayout = (TouchFocusLinearLayout) findViewById(R.id.button_linearLayout);

        mFocusLinearLayout.setAutoSearchFocus(false);
        mFocusLinearLayout.setOnItemSelectedListener(new ItemSelectedListener() {

            @Override
            public void onItemSelected(View v, int position, boolean isSelected, View view) {
                setButtonBackground(v, isSelected);
            }
        });
    }

    public void onResponseSuccess(GuessLikeGoodsBean guessLikeGoodsBean) {
        if(guessLikeGoodsBean!=null){
            GuessLikeGoodsBean.ResultVO result = guessLikeGoodsBean.getResult();
            if(result!=null){
                List<GuessLikeGoodsBean.ResultVO.RecommendVO> recommedResult = result.getRecommedResult();
                if(recommedResult != null&& recommedResult.size() > 0){
                    List<DetainMentBo> detainMentBos = new ArrayList<>();
                    for (int i = 0 ;i<recommedResult.size() ;i++){
                        DetainMentBo detainMentBo = new DetainMentBo();
                        GuessLikeGoodsBean.ResultVO.RecommendVO recommendVO = recommedResult.get(i);
                        if(recommendVO != null){
                            if (!recommendVO.getType().equals("item")) {
                                continue;
                            }
                            GuessLikeFieldsVO fields = recommendVO.getFields();
                            if(fields != null){
                                GuessLikeFieldsVO.TitleVO title = fields.getTitle();
                                GuessLikeFieldsVO.MasterPicVO masterPic = fields.getMasterPic();
                                GuessLikeFieldsVO.PriceVO price = fields.getPrice();
                                String itemId = fields.getItemId();
                                detainMentBo.setNid(itemId);
                                if(masterPic != null){
                                    String picUrl = masterPic.getPicUrl();
                                    detainMentBo.setPictUrl(picUrl);

                                }
                                if(title != null){
                                    GuessLikeFieldsVO.TitleVO.ContextVO context = title.getContext();
                                    if(context != null){
                                        detainMentBo.setTitle(context.getContent());
                                    }
                                }
                                if(price != null){
                                detainMentBo.setPrice("¥" + price.getYuan());
                                }
                            }
                        }
                        detainMentBos.add(detainMentBo);
                    }

                    if (mFocusHListView != null) {
                        if (mDetainMentAdapter == null || mDetainMentAdapter.getCount() > 0) {
                            mDetainMentBo_Data= detainMentBos.toArray(new DetainMentBo[detainMentBos.size()]);
                        } else {
                            mDetainMentAdapter.setData(detainMentBos.toArray(new DetainMentBo[detainMentBos.size()]));
                            mDetainMentAdapter.notifyDataSetChanged();
                            firstRequestFocus();
                        }
                    }
                }
            }
        }
    }

    private static class DetainmentRequestDonelistener implements DetainmentDataBuider.DetainmentRequestListener {

        protected final WeakReference<DetainMentDialog> mRefDetainMentDialog;

        public DetainmentRequestDonelistener(WeakReference<DetainMentDialog> baseActivityRef) {
            mRefDetainMentDialog = baseActivityRef;
        }

        @Override
        public boolean onDetainmentRequestDone(GuessLikeGoodsBean guessLikeGoodsBean) {
            if (mRefDetainMentDialog != null && mRefDetainMentDialog.get() != null) {
                DetainMentDialog detainMentDialog = mRefDetainMentDialog.get();
                detainMentDialog.onResponseSuccess(guessLikeGoodsBean);
            }
            return true;
        }


        /**
         * 过滤猜你喜欢的数据中出现（为你推荐）字样
         *
         * @param data
         * @return
         */
        private DetainMentBo[] filterRecommendData(DetainMentBo[] data) {
            if (data == null || data.length <= 0) {
                return data;
            }
                String filterText = CoreApplication.getApplication().getResources().getString(R.string.ytsdk_detainment_weinituijian);
                int length = data.length;
                for (int i = 0; i < length; i++) {
                    DetainMentBo bo = data[i];
                    if (bo != null) {
                        String title_temp = bo.getTitle();
                        if (!TextUtils.isEmpty(title_temp)) {
                            int index = title_temp.indexOf(filterText, 0);
                            if (index >= 0 && index < filterText.length()) {
                                int start = index + filterText.length();
                                try {
                                    String subString = title_temp.substring(start, title_temp.length());
                                    bo.setTitle(subString);
                                } catch (Exception e) {
                                }
                            }
                        }
                }
            }
            return data;
        }

    }

    private static class DetainMentSyncLoginListener implements LoginHelper.SyncLoginListener {

        private WeakReference<DetainMentDialog> mRefDetainMentDialog;

        public DetainMentSyncLoginListener(WeakReference<DetainMentDialog> ref) {
            mRefDetainMentDialog = ref;
        }

        @Override
        public void onLogin(boolean success) {
            AppDebug.i(TAG, "refreshData onLogin -->   =" + success);
            if (success) {
                if (mRefDetainMentDialog != null && mRefDetainMentDialog.get() != null) {
                    DetainMentDialog detainMentDialog = mRefDetainMentDialog.get();
                    detainMentDialog.onRequestDetainMent();
                }
            }
        }
    }

    private Map<String, String> getProperties() {
        Map<String, String> properties = Utils.getProperties();
        properties.put("is_login", User.isLogined() ? "true" : "false");

        return properties;
    }
}
