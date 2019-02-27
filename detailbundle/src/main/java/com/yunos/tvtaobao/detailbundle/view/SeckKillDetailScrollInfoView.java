package com.yunos.tvtaobao.detailbundle.view;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taobao.detail.clientDomain.TBDetailResultVO;
import com.taobao.detail.domain.base.Unit;
import com.yunos.tv.blitz.view.BlitzBridgeSurfaceView;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tvtaobao.biz.dialog.util.SnapshotUtil;
import com.yunos.tvtaobao.biz.dialog.util.SnapshotUtil.OnFronstedGlassSreenDoneListener;
import com.yunos.tvtaobao.detailbundle.R;
import com.yunos.tvtaobao.detailbundle.activity.SeckKillDetailFullDescActivity;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 滚动页面的UI
 */
public class SeckKillDetailScrollInfoView {

    private final String TAG = "DetailScrollInfoView";
    private final int DISTANCE = 150;

    //scrollbar
    private final int m_isShowVScrollBar = 1;

    private final int m_VScrollBarBGWidth = 10;
    private final int m_VScrollBarBlockWidth = 10;

    private final int m_VScrollBarBG_r = 133;
    private final int m_VScrollBarBG_g = 133;
    private final int m_VScrollBarBG_b = 133;
    private final int m_VScrollBarBG_a = 128;

    private final int m_VScrollBar_r = 0;
    private final int m_VScrollBar_g = 0;
    private final int m_VScrollBar_b = 0;
    private final int m_VScrollBar_a = 256;

    private final int m_VScrollBarIsFade = 0;
    private final int m_VScrollBarFadeDuration = 0;
    private final int m_VScrollBarFadeWait = 0;

    private WeakReference<Activity> mDetailActivityReference;

    // 商品详情
    private BlitzBridgeSurfaceView mBlitzBridgeSurfaceView;

    // 滚动页面的FrameLayout
    private FrameLayout mFrameLayout;

    // 立即购买textview
    private TextView buyTextView;

    //buytextview的父级
    private RelativeLayout buyRelativeLayout;

    private OnFronstedGlassSreenDoneListener screenShotListener;

    public SeckKillDetailScrollInfoView(WeakReference<Activity> weakReference) {
        mDetailActivityReference = weakReference;
        onInitScrollInfoView();

        if (buyTextView != null) {
            buyTextView.setOnKeyListener(new OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    return onKeyOfScrollView(keyCode, event);
                }
            });
        }
    }

    private void onInitScrollInfoView() {
        if (mDetailActivityReference != null && mDetailActivityReference.get() != null) {
            final SeckKillDetailFullDescActivity mDetailFullDescActivity = (SeckKillDetailFullDescActivity) mDetailActivityReference
                    .get();

            mFrameLayout = (FrameLayout) mDetailFullDescActivity.findViewById(R.id.detail_fulldesc_framelayout);
            buyTextView = (TextView) mDetailFullDescActivity.findViewById(R.id.detail_richtext_buy_text);
            buyRelativeLayout = (RelativeLayout) mDetailFullDescActivity.findViewById(R.id.detail_fulldesc_buy);

            //判断1080P和720P,分别设置距离高度为27dp, 11dp
            int bootom1080p = (int) mDetailFullDescActivity.getResources().getDimensionPixelSize(R.dimen.dp_28);
            int bottom720p = (int) mDetailFullDescActivity.getResources().getDimensionPixelSize(R.dimen.dp_13);
            int top1080p = (int) mDetailFullDescActivity.getResources().getDimensionPixelSize(R.dimen.dp_16);
            int top720p = (int) mDetailFullDescActivity.getResources().getDimensionPixelSize(R.dimen.dp_7);
            int left1080p = (int) mDetailFullDescActivity.getResources().getDimensionPixelSize(R.dimen.dp_20);
            int left720p = (int) mDetailFullDescActivity.getResources().getDimensionPixelSize(R.dimen.dp_10);
            int right1080p = (int) mDetailFullDescActivity.getResources().getDimensionPixelSize(R.dimen.dp_22);
            int right720p = (int) mDetailFullDescActivity.getResources().getDimensionPixelSize(R.dimen.dp_10);
            if (DeviceUtil.getScreenScaleFromDevice(mDetailFullDescActivity.getApplicationContext()) > 1.1f) {
                buyRelativeLayout.setPadding(left1080p, top1080p, right1080p, bootom1080p);
            } else {
                buyRelativeLayout.setPadding(left720p, top720p, right720p, bottom720p);
            }

            mBlitzBridgeSurfaceView = (BlitzBridgeSurfaceView) mDetailFullDescActivity
                    .findViewById(R.id.detail_fulldesc_webview_blitz);
            if (mBlitzBridgeSurfaceView != null) {
                mBlitzBridgeSurfaceView.setVisibility(View.VISIBLE);
                mBlitzBridgeSurfaceView.setFocusable(false);
            }
            buyTextView.requestFocus();
        }
    }

    /***
     * 初始化 scroll bar
     */
    private void onInitScrollbar() {
        if (mDetailActivityReference != null && mDetailActivityReference.get() != null) {
            final SeckKillDetailFullDescActivity mDetailFullDescActivity = (SeckKillDetailFullDescActivity) mDetailActivityReference
                    .get();

            mDetailFullDescActivity.showScrollBar(m_isShowVScrollBar);

            mDetailFullDescActivity.setScrollBarColor(m_VScrollBarBG_r, m_VScrollBarBG_g, m_VScrollBarBG_b,
                    m_VScrollBarBG_a, m_VScrollBar_r, m_VScrollBar_g, m_VScrollBar_b, m_VScrollBar_a);

            mDetailFullDescActivity
                    .setScrollBarFade(m_VScrollBarIsFade, m_VScrollBarFadeDuration, m_VScrollBarFadeWait);

            mDetailFullDescActivity.setScrollBarWidth(m_VScrollBarBGWidth, m_VScrollBarBlockWidth);
        }
    }

    /**
     * 处理滚动页面中的按键事件
     * @param keyCode
     * @param event
     * @return
     */
    private boolean onKeyOfScrollView(int keyCode, KeyEvent event) {
        int keyAction = event.getAction();
        if (keyAction == KeyEvent.ACTION_DOWN
                && (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP)) {
            int dy = 0;
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    dy = DISTANCE;
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    dy = -DISTANCE;
                    break;

                default:
                    break;
            }

            if (mDetailActivityReference != null && mDetailActivityReference.get() != null) {
                final SeckKillDetailFullDescActivity mDetailFullDescActivity = (SeckKillDetailFullDescActivity) mDetailActivityReference
                        .get();
                mDetailFullDescActivity.scrollBy(0, dy);
                onInitScrollbar();
                return true;
            }
        }
        return false;
    }

    /**
     * 释放 详情页面的VIEW
     */
    public void onCleanAndDestroy() {
        if (mBlitzBridgeSurfaceView != null) {
            mBlitzBridgeSurfaceView.setVisibility(View.GONE);
            mBlitzBridgeSurfaceView = null;
        }
    }

    /**
     * 加载内容
     * @param baseUrl
     * @param data
     * @param mimeType
     * @param encoding
     * @param failUrl
     */
    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String failUrl) {
        AppDebug.i(TAG, "loadDataWithBaseURL --> baseUrl = " + baseUrl + "; data = " + data + "; mimeType = "
                + mimeType);

        if (mDetailActivityReference != null && mDetailActivityReference.get() != null) {
            final SeckKillDetailFullDescActivity mDetailFullDescActivity = (SeckKillDetailFullDescActivity) mDetailActivityReference
                    .get();
            AppDebug.i(TAG, "loadWithData --> data = " + data);
            mDetailFullDescActivity.loadDataForWeb(data);
        }
    }

    public String getPropsHtml(TBDetailResultVO tBDetailResultVO) {
        String html = "";
        if (tBDetailResultVO == null) {
            return html;
        }

        // 商品属性规格
        List<Unit> props = tBDetailResultVO.props;
        if (props == null || props.isEmpty()) {
            return html;
        }

        int mSize = props.size();
        if (mSize > 0) {
            html = "<div style='display:block;clear:both; padding:0px 20px 40px 20px;'><div style='display:block;height:5px;background-color:#c6223f'></div>";
            html = html + "<div style='color:#c6223f;font-size:24px;padding:20px 0px 20px 0px'>商品参数</div>";
            html = html + "<div style='display: block;padding:0px 0px 20px 0px;clear: both;'>";
            for (int i = 0; i < mSize; i++) {
                Unit mUnit = props.get(i);
                html = html
                        + "<div style='float:left;width:47%;color:#666666;font-size:24px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;padding-right:20px;'>"
                        + mUnit.name + ":" + mUnit.value + "</div>";
                if (i % 2 != 0) {
                    html = html + "</div>";
                    if (i < mSize - 1) {
                        html = html + "<div style='display: block;padding:20px 0px 20px 0px;clear: both;'>";
                    }
                } else if (i == mSize - 1) {
                    html = html + "</div>";
                }
            }
            html = html + "</div>";
        }
        AppDebug.i(TAG, "getPropsHtml html=" + html);
        return html;
    }

    /**
     * 设置毛玻璃背景
     */
    public void setFronstedGlassSreen() {
        screenShotListener = new OnFronstedGlassSreenDoneListener() {

            @Override
            public void onFronstedGlassSreenDone(Bitmap bmp) {
                if (mFrameLayout != null) {
                    if ((bmp != null) && (!bmp.isRecycled())) {
                        mFrameLayout.setBackgroundDrawable(new BitmapDrawable(bmp));
                    }
                }
            }
        };
        SnapshotUtil.getFronstedSreenShot(mDetailActivityReference, 5, 0, screenShotListener);
    }

    public void setOnClickListener(OnClickListener l) {
        if (buyTextView != null) {
            buyTextView.setOnClickListener(l);
        }
    }

    public void setBuyText(String text) {
        if (buyTextView != null) {
            buyTextView.setText(text);
        }
    }
}
