/**
 *
 */
package com.yunos.tvtaobao.flashsale.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListAdapter;

import com.yunos.tv.app.widget.focus.FocusFlipGridView;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.app.widget.focus.params.ScaleParams;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.util.ActivityPathRecorder;
import com.yunos.tvtaobao.biz.request.core.ServiceCode;
import com.yunos.tvtaobao.biz.widget.RefreshDataFocusFlipGridView;
import com.yunos.tvtaobao.flashsale.AppManager;
import com.yunos.tvtaobao.flashsale.activity.FlashSaleBaseActivity;
import com.yunos.tvtaobao.flashsale.activity.MainActivity;
import com.yunos.tvtaobao.flashsale.activity.RecommendActivity;
import com.yunos.tvtaobao.flashsale.adapter.GoodsAdapter;
import com.yunos.tvtaobao.flashsale.bo.CategoryItem;
import com.yunos.tvtaobao.flashsale.bo.GoodsInfo;
import com.yunos.tvtaobao.flashsale.listener.ContextListener;
import com.yunos.tvtaobao.flashsale.listener.OnMaskListener;
import com.yunos.tvtaobao.flashsale.request.RequestManager;
import com.yunos.tvtaobao.flashsale.utils.AppConfig;
import com.yunos.tvtaobao.flashsale.utils.CommUtil;
import com.yunos.tvtaobao.flashsale.utils.DateUtils;

import java.util.List;

public class GoodsGridView extends RefreshDataFocusFlipGridView implements
        ItemSelectedListener {
    private static final String TAG = "GoodsGridView";
    public Context mContext;

    private int mFlag = FliperItemView.DRAW_MASK_TOP;
    private int mPrivFlag = FliperItemView.DRAW_NOTHING;

    private ItemSelectedListener mItemSelectedListener;

    private Paint mPaint = new Paint();

    private RequestManager mRequestManager;

    private ContextListener mContextListener;

    private OnMaskListener mOnMaskListener;


    public void setOnMaskListener(OnMaskListener l) {
        mOnMaskListener = l;
    }

    public OnMaskListener getOnMaskListener() {
        return mOnMaskListener;
    }

    public void forceSelectItem() {
        ListAdapter adapter = (ListAdapter) super.getAdapter();

        if (null != adapter && adapter instanceof GoodsAdapter) {
            ((GoodsAdapter) adapter).forceSelectItem();
        }
    }

    private void setPrivateFlag(int flag) {
        if (0 != (mFlag & flag)) {
            mPrivFlag = flag;
        } else {
            mPrivFlag = FliperItemView.DRAW_NOTHING;
        }
    }

    public int getPrivateFlag() {
        return mPrivFlag;
    }

    @Override
    public void setOnItemSelectedListener(ItemSelectedListener listener) {
        mItemSelectedListener = listener;
    }

    public void setContextListener(ContextListener contextListener) {
        mContextListener = contextListener;
    }

    /**
     * @param contxt
     */
    public GoodsGridView(Context contxt) {
        super(contxt);
        mContext = contxt;
        super.setOnItemSelectedListener(this);
        mPaint.setColor(Color.YELLOW);
        ScaleParams scale = mParams.getScaleParams();

        scale.setScale(ScaleParams.SCALED_FIXED_COEF, 1.05f, 1.05f);

        onInitGoodListLifeUiGridView(contxt);

        mRequestManager = AppManager.getInstance(contxt).getRequestManager();
    }

    /**
     * @param contxt
     * @param attrs
     */
    public GoodsGridView(Context contxt, AttributeSet attrs) {
        super(contxt, attrs);
        onInitGoodListLifeUiGridView(contxt);
        mRequestManager = AppManager.getInstance(contxt).getRequestManager();
    }

    /**
     * @param contxt
     * @param attrs
     * @param defStyle
     */
    public GoodsGridView(Context contxt, AttributeSet attrs, int defStyle) {
        super(contxt, attrs, defStyle);
        onInitGoodListLifeUiGridView(contxt);
    }

    public void onInitGoodListLifeUiGridView(Context contxt) {

    }

    @Override
    public void onItemSelected(View v, int position, boolean isSelected,
                               View view) {
        if (CommUtil.isVisibleTopMask(super.getCount(), super.getColumnNum(),
                AppConfig.ONE_PAGE_ROWS_GRIDVIEW, position)) {
            setPrivateFlag(FliperItemView.DRAW_MASK_TOP);
        } else if (CommUtil.isVisibleBottomMask(super.getCount(),
                super.getColumnNum(), AppConfig.ONE_PAGE_ROWS_GRIDVIEW,
                position)) {
            setPrivateFlag(FliperItemView.DRAW_MASK_BOTTOM);
        } else {
            setPrivateFlag(FliperItemView.DRAW_NOTHING);
        }

        if (null != mItemSelectedListener) {
            mItemSelectedListener.onItemSelected(v, position, isSelected, view);
        }

        AppDebug.d(TAG, "position " + position + ";isSelected = " + isSelected);

    }

    private GoodsInfo mGoodsInfo = null;

    protected void handlerItemClick(GoodsInfo goodsInfo) {
        AppDebug.d(TAG, "handlerItemClick");

        if (null == goodsInfo) {
            return;
        }

        mGoodsInfo = goodsInfo;

        String id = null;
        if (goodsInfo.getType() == GoodsInfo.ITEM_TYPE_STOCK_INFO) {

            if (null != mContextListener) {
                mContextListener.OnWaitProgressDialog(true);
            }

            id = goodsInfo.getSeckillId();
            mRequestManager.getStockByBatchId(id, mGetGoodsInfoListener);
        } else if (goodsInfo.getType() == GoodsInfo.ITEM_TYPE_SECKILL_INFO) {
            enterDetail(goodsInfo);
        }

    }

    private RequestListener<List<GoodsInfo>> mGetGoodsInfoListener = new RequestListener<List<GoodsInfo>>() {
        @Override
        public void onRequestDone(List<GoodsInfo> data, int resultCode,
                                  String msg) {

            if (null != mContextListener) {
                mContextListener.OnWaitProgressDialog(false);
            }

            if (resultCode == RequestManager.CODE_SUCCESS) {
                loadingDataSuccess(GoodsInfo.ITEM_TYPE_STOCK_INFO, data);
            } else {
                for (ServiceCode serviceCode : ServiceCode.values()) {
                    if (resultCode == serviceCode.getCode()) {
                        String errorMsg = serviceCode.getMsg();
                        if (null != mContextListener && null != errorMsg) {
                            mContextListener.showErrorDialog(errorMsg, false);
                        }
                    }
                }
            }

        }
    };

    public boolean isSaleOut(GoodsInfo info) {
        return info.getStockPercent() == 100;
    }

    public FlashSaleBaseActivity getFlashSaleBaseActivity() {
        ContextListener l = mContextListener;

        if (null != l) {
            return l.getFlashSaleBaseActivity();
        }
        return null;
    }

    public void startRecommendActivity(GoodsInfo info) {
        Intent intent = new Intent();

        intent.putExtra("itemId", info.getItemId());
        intent.putExtra("name", info.getName());
        intent.putExtra("salePrice", info.getSalePrice());
        intent.putExtra("price", info.getPrice());
        intent.putExtra("saleMessage", info.getSoldOutInfo());
        intent.putExtra("picUrl", info.getPicUrl());
        intent.putExtra("now_time", info.getStartTime());
        intent.putExtra("end_time", info.getEndTime());

        CategoryItem item = null;
        FlashSaleBaseActivity activity = getFlashSaleBaseActivity();

        Context context = null;
        if (null != activity) {
            context = activity;
            if (activity instanceof MainActivity) {
                item = ((MainActivity) activity).getQianggou();
            }
        } else {
            context = mContext;
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (null != item) {
            if (DateUtils.string2Timestamp(item.getStartTime()) > DateUtils
                    .string2Timestamp(info.getStartTime())) {
                intent.putExtra("status", CategoryItem.STATUS_PAST);
            } else {
                intent.putExtra("status", CategoryItem.STATUS_CURRENT);
            }
            intent.putExtra("time", DateUtils.getTime(item.getStartTime()));
        } else {
            intent.putExtra("status", CategoryItem.STATUS_PAST);
            intent.putExtra("time", "23:00");
        }

		intent.setClassName(context, "com.yunos.tvtaobao.flashsale.activity.RecommendActivity");
//		intent.putExtra(ActivityPathRecorder.INTENTKEY_FIRST, true);
		context.startActivity(intent);
    }

    private int mKeyDownCount = 0;

    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        boolean ret = super.preOnKeyDown(keyCode, event);
        mKeyDownCount++;
        if (mKeyDownCount == 1) {
            /** 不是长按住 */
            if (!ret && null != mDirectionListener && mItemCount > 0) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    mDirectionListener.onDirection(this,
                            DirectionListener.DIR_BOTTOM);
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    mDirectionListener.onDirection(this,
                            DirectionListener.DIR_TOP);
                }
            }
        }
        return ret;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        mKeyDownCount = 0;
        return super.onKeyUp(keyCode, event);
    }

    public interface DirectionListener {
        public static byte DIR_BOTTOM = 0;
        public static byte DIR_TOP = 1;

        /**
         * 选中状态变化回调
         *
         * @param type 到达的状态
         */
        public void onDirection(FocusFlipGridView flipView, int type);
    }

    private DirectionListener mDirectionListener;

    public void setDirectionListener(DirectionListener l) {
        mDirectionListener = l;
    }

    private void loadingDataSuccess(int type, Object reqData) {
        @SuppressWarnings("unchecked")
        List<GoodsInfo> goodsList = (List<GoodsInfo>) reqData;

        GoodsInfo goodsInfo = null;
        if (null != goodsList && goodsList.size() > 0) {
            goodsInfo = goodsList.get(0);

            if (null != mGoodsInfo) {
                if (TextUtils.equals(mGoodsInfo.getSeckillId(),
                        goodsInfo.getSeckillId())) {
                    GoodsInfo.copyGoodsInfo(mGoodsInfo, goodsInfo);
                }
            }

        }
        goodsInfo = mGoodsInfo;
        if (null == goodsInfo) {
            if (AppConfig.DEBUG) {
                AppDebug.e(TAG, "null == goodsInfo");
            }
            return;
        }
        if (AppConfig.REMINDSETTINGACTIVITY_DEBUG) {
            // for test begin
            String startTimeStr = goodsInfo.getStartTime();
            if (type == GoodsInfo.ITEM_TYPE_STOCK_INFO
                    && goodsInfo.isFuture()) {
//				Intent intent = new Intent();
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				intent.setClass(mAppContext,
//						RemindSettingsActivityTest.class);
//				intent.putExtra("type", type);
//				intent.putExtra("id", goodsInfo.getSeckillId());
//				intent.putExtra("startTimeStr", startTimeStr);
//				intent.putExtra("remindTime",
//						goodsInfo.getRemindTime());
//				mAppContext.startActivity(intent);
                return;
            }
        }


        openDetail(goodsInfo);
    }

    protected void openDetail(GoodsInfo goodsInfo) {
        if (isSaleOut(goodsInfo)) {
            startRecommendActivity(goodsInfo);
            return;
        }
        enterDetail(goodsInfo);
    }

    private void enterDetail(GoodsInfo goodsInfo) {
        FlashSaleBaseActivity activity = null;
        if (null != mContextListener) {
            activity = mContextListener.getFlashSaleBaseActivity();
        }
        if (null != activity) {
            activity.enterDetail(goodsInfo);
        }
    }

    @Override
    public void onFocusFinished() {
        super.onFocusFinished();
        OnMaskListener l = getOnMaskListener();
        if (null != l) {
            l.onMask();
        }
        AppDebug.i(TAG, "onFocusFinished: " + this);
    }

}
