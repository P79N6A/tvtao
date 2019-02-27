package com.tvtaobao.voicesdk.dialogs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.R;
import com.tvtaobao.voicesdk.adapter.LogisticsQueryAdapter;
import com.tvtaobao.voicesdk.bo.DomainResultVo;
import com.tvtaobao.voicesdk.dialogs.base.BaseDialog;
import com.tvtaobao.voicesdk.bo.LogisticsData;
import com.tvtaobao.voicesdk.bo.LogisticsDo;
import com.tvtaobao.voicesdk.bo.PageReturn;
import com.tvtaobao.voicesdk.register.type.ActionType;
import com.tvtaobao.voicesdk.utils.DialogManager;
import com.tvtaobao.voicesdk.utils.Util;
import com.tvtaobao.voicesdk.view.AutoTextView;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.ActivityPathRecorder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pan on 2017/10/27.
 */

public class LogisticsQueryDialog extends BaseDialog {
    private final String TAG = "SearchDialog";
    private Context mContext;

    private AutoTextView mReply;
    private RecyclerView recyclerView;
    private LogisticsQueryAdapter logisticsQueryAdapter;

    private List<LogisticsDo> logisticsList;
    private int currentPage = 1; //当前第几页
    private List<String> tips = new ArrayList<>();
    private LinearLayout llLogisticsInfo;
    private ImageView ivEmpty;

    public LogisticsQueryDialog(Context context) {
        super(context);
        this.mContext = context;

        this.setContentView(R.layout.dialog_logistics_query);
        initView();
    }

    public void initView() {
        Log.d(TAG, TAG + ".initView");
        logisticsList = new ArrayList<>();
        mReply = (AutoTextView) findViewById(R.id.voice_card_search_reply);
        ivEmpty = (ImageView) findViewById(R.id.iv_empty);
        llLogisticsInfo = (LinearLayout) findViewById(R.id.ll_logistics_info);
        recyclerView = (RecyclerView) findViewById(R.id.rv_logistics);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        //设置item间距，30dp
        recyclerView.addItemDecoration(new SpaceItemDecoration(Util.dip2px(mContext, 20)));
        logisticsQueryAdapter = new LogisticsQueryAdapter(mContext);
        recyclerView.setAdapter(logisticsQueryAdapter);
        logisticsQueryAdapter.initData(logisticsList);
    }

    @Override
    public void show() {
        super.show();
        DialogManager.getManager().pushDialog(this);
        delayDismiss(10 * 1000);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mReply.clear();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        int childcount = recyclerView.getChildCount();

        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (recyclerView.getChildAt(childcount - 1) != null
                    && recyclerView.getChildAt(childcount - 1).isFocused()) {
                onNextPage(true);
            }

        }

        if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (recyclerView.getChildAt(0) != null
                    && recyclerView.getChildAt(0).isFocused()) {
                onPreviousPage(true);
            }
        }

//        MLog.d(TAG, TAG + ".dispatchKeyEvent mListView.hasFocus : " + recyclerView.hasFocus());
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (recyclerView.isFocused()) {
                    setFocusable(true);
                }
                break;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void setFocusable(boolean focusable) {
        int count = recyclerView.getChildCount();

        for (int i = 0; i < count; i++) {
            recyclerView.getChildAt(i).setFocusable(focusable);
        }
    }

    @Override
    public PageReturn onASRNotify(DomainResultVo object) {
        PageReturn pageReturn = super.onASRNotify(object);
        switch (object.getIntent()) {
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
            case ActionType.HAVE_MORE:
                gotoOrderActivity();
                dismiss();

                pageReturn.isHandler = true;
                pageReturn.feedback = "正在为您跳转搜索";
                break;
        }

        return pageReturn;
    }

    /**
     * 换页，下一页
     */
    public boolean onNextPage(boolean isOnKey) {
        currentPage++;
        int startIndex = 3 * (currentPage - 1);
        if (startIndex > logisticsList.size()) {
            currentPage--;
            return true;
        }
        List<LogisticsDo> logisticsDos = null;
        if (logisticsList.size() - startIndex >= 3) {
            logisticsDos = logisticsList.subList(startIndex, startIndex + 3);
        } else {
            logisticsDos = logisticsList.subList(startIndex, logisticsList.size());
        }
        if(logisticsDos.size()<=0){
            return false;
        }
        logisticsQueryAdapter.setAction(isOnKey);
        logisticsQueryAdapter.initData(logisticsDos);

        return true;
    }

    /**
     * 换页，上一页
     */
    public void onPreviousPage(boolean isOnKey) {
        if (currentPage > 1) {
            currentPage--;
            int startIndex = 3 * (currentPage - 1);
            List<LogisticsDo> productDos = logisticsList.subList(startIndex, startIndex + 3);
            logisticsQueryAdapter.setAction(isOnKey);
            logisticsQueryAdapter.setAction(isOnKey);
            logisticsQueryAdapter.initData(productDos);
        }
    }


    /**
     * 进入我的订单页
     */
    private void gotoOrderActivity() {
        if (mContext == null)
            return;

        try {

            String url = "tvtaobao://home?app=taobaosdk&module=orderList&notshowloading=true";

            AppDebug.v(TAG, TAG + ", gotoLogisticsResult.uri = " + url);
            Intent intent = new Intent();

            intent.setData(Uri.parse(url + "&from_voice=voice"));
            intent.putExtra(ActivityPathRecorder.INTENTKEY_FIRST, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 进入我的订单页
     */
    private void gotoHomeActivity() {
        if (mContext == null)
            return;

        try {

            String url = "tvtaobao://home?module=main&showloading=true";
            Intent intent = new Intent();
            intent.setData(Uri.parse(url));
            intent.putExtra(ActivityPathRecorder.INTENTKEY_FIRST, true);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(LogisticsData logisticsData) {
        if (logisticsData.getTts() != null) {
            tips.add(logisticsData.getTts());
        }
//        if (logisticsData.getTip() != null) {
//            tips.add(logisticsData.getTip());
//        }
        setPrompt(tips);
        if (!TextUtils.isEmpty(logisticsData.getCode())) {
            String code = logisticsData.getCode();
            if (code.equals("OK")) {
                showLogistics(logisticsData);
            } else if (code.equals("DATETIME_NOT_IDENTIFY")) {
                showEmpty(R.drawable.icon_bill_datetime_not_identify);
            } else if (code.equals("EMPTY_DATA")) {
                showEmpty(R.drawable.icon_bill_empty_data);
            } else {
                showEmpty(R.drawable.icon_bill_other_problem);
            }
        }
    }

    public void showLogistics(LogisticsData logisticsData) {
        ivEmpty.setVisibility(View.GONE);
        llLogisticsInfo.setVisibility(View.VISIBLE);
        if (logisticsList == null) {
            logisticsList = new ArrayList<>();
        }
        logisticsList.clear();
        this.currentPage = 1;
        if (logisticsData.getModel() != null) {
            List<LogisticsDo> data = logisticsData.getModel();
            logisticsList.addAll(data);
            List<LogisticsDo> logisticsDos;
            if (data.size() > 3) {
                logisticsDos = data.subList(0, 3);
            } else {
                logisticsDos = data;
            }
            logisticsQueryAdapter.initData(logisticsDos);
            Log.e(TAG, TAG + ".VoiceSearchListener.onSuccess size : " + data.size());
        }
    }

    public void showEmpty(int drawableResource) {
        ivEmpty.setVisibility(View.VISIBLE);
        llLogisticsInfo.setVisibility(View.GONE);
        ivEmpty.setImageResource(drawableResource);
    }

    public void setPrompt(List<String> reply) {

        if (reply != null && reply.size() > 0) {
            mReply.autoScroll(reply);

            ASRNotify.getInstance().playTTS(reply.get(0));
        }
    }

    static class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        int mSpace;

        /**
         * @param outRect Rect to receive the output.
         * @param view    The child view to decorate
         * @param parent  RecyclerView this ItemDecoration is decorating
         * @param state   The current state of RecyclerView.
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = mSpace * 2;
            outRect.bottom = mSpace;
            outRect.top = mSpace;


        }

        public SpaceItemDecoration(int space) {
            this.mSpace = space;
        }
    }

}
