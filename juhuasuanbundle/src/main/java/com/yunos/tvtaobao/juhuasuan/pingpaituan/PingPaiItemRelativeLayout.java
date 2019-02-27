package com.yunos.tvtaobao.juhuasuan.pingpaituan;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.tvlife.imageloader.core.assist.FailReason;
import com.tvlife.imageloader.core.listener.ImageLoadingListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.AppHandler;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.SystemUtil;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.BrandMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;
import com.yunos.tvtaobao.juhuasuan.classification.ImageHandleUnit;
import com.yunos.tvtaobao.juhuasuan.common.NetWorkCheck;
import com.yunos.tvtaobao.juhuasuan.util.ImageUtil;
import com.yunos.tvtaobao.juhuasuan.widget.CustomImageView;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedBasePositionManager.FrameInterpolator;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedBasePositionManager.AccelerateFrameInterpolator;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedBasePositionManager.ItemInterface;

public class PingPaiItemRelativeLayout extends FrameLayout implements ItemInterface {

    private String TAG = "PingPaiItemRelativeLayout";

    private BrandMO mBrandModel = null;
    private Activity mActivity = null;

    private PingPaiPagerAdapter mPingPaiPagerAdapter = null;

    private Context mContext = null;

    private CustomImageView mImageView_goods = null;
    private CustomImageView mImageView_logo = null;

    private TextView mTextView = null;
    private ImageLoaderManager mImageLoaderManager = null;

    // 表示的条目编号
    private int mItemIndex = 0;

    // 表示所在页
    private int mPageIndex = 0;

    // 商品加信息图片
    private Bitmap mBitmap = null;

    // LOGO 图片
    private Bitmap mLogoBitmap = null;

    private int mImageType = 0;

    private int mItemWidth = 0;
    private int mItemHeight = 0;

    private DispalyBitmapRun mDispalyBitmapRun = null;

    private PingPaiItemRelativeLayoutHandle mMainHandler = new PingPaiItemRelativeLayoutHandle(this);

    private static final class PingPaiItemRelativeLayoutHandle extends AppHandler<PingPaiItemRelativeLayout> {

        public PingPaiItemRelativeLayoutHandle(PingPaiItemRelativeLayout t) {
            super(t);
        }

    }

    public PingPaiItemRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub

        onInitItemFrameLayout(context);
    }

    public PingPaiItemRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        onInitItemFrameLayout(context);
    }

    public PingPaiItemRelativeLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        onInitItemFrameLayout(context);
    }

    /**
     * 初始化
     * @param context
     */
    private void onInitItemFrameLayout(Context context) {
        mContext = context;

        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(context);

        onInitItemFrameLayout(0);

        mBitmap = null;
        mLogoBitmap = null;
        mItemIndex = 0;
        mPageIndex = 0;
    }

    @Override
    public int getItemWidth() {
        // TODO Auto-generated method stub
        return getWidth();
    }

    @Override
    public int getItemHeight() {
        // TODO Auto-generated method stub
        return getHeight();
    }

    @Override
    public Rect getOriginalRect() {
        // TODO Auto-generated method stub
        Rect rect = new Rect();
        rect.left = getLeft();
        rect.right = getRight();
        rect.top = getTop();
        rect.bottom = getBottom();

        return rect;
    }

    @Override
    public Rect getItemScaledRect(float scaledX, float scaledY) {
        // TODO Auto-generated method stub
        Rect rect = new Rect();
        // int[] location = new int[2];
        // getLocationOnScreen(location);

        int imgW = getWidth();
        int imgH = getHeight();

        float left = getLeft() - (scaledX - 1.0f) * imgW / 2;
        float right = left + imgW * scaledX;
        float top = getTop() - (scaledY - 1.0f) * imgH / 2;
        float bottom = top + imgH * scaledY;

        rect.left = (int) Math.ceil(left + 0.5);
        rect.right = (int) Math.floor(right - 0.5);
        rect.top = (int) Math.ceil(top + 0.5);
        rect.bottom = (int) Math.floor(bottom - 0.5);

        //        rect.left = (int) Math.ceil((getLeft() - (scaledX - 1.0f) * imgW / 2 + 0.5f));
        //        rect.right = (int) Math.floor(rect.left + imgW * scaledX - 0.5f);
        //        rect.top = (int) Math.ceil(getTop() - (scaledY - 1.0f) * imgH / 2 + 0.5f);
        //        rect.bottom = (int) Math.floor(rect.top + imgH * scaledY - 0.5f);
        AppDebug.i(TAG, TAG + ".getItemScaledRect rect=" + rect + ", " + (rect.right - rect.left) + ", "
                + (imgW * scaledX) + ", imgW=" + imgW + ", " + getWidth() + ", " + (getRight() - getLeft())
                + ", scaledX=" + scaledX);

        Rect rect1 = new Rect();
        rect1.left = (int) Math.ceil((getLeft() - (scaledX - 1.0f) * imgW / 2 + 0.5f));
        rect1.right = (int) Math.ceil(rect.left + imgW * scaledX - 0.5f);
        rect1.top = (int) Math.ceil(getTop() - (scaledY - 1.0f) * imgH / 2 + 0.5f);
        rect1.bottom = (int) Math.ceil(rect.top + imgH * scaledY - 0.5f);
        AppDebug.i(TAG, TAG + ".getItemScaledRect rect=" + rect1);

        return rect;
    }

    @Override
    public boolean getIfScale() {
        // TODO Auto-generated method stub
        return true;
    }

    AccelerateFrameInterpolator mScaleInterpolator = new AccelerateFrameInterpolator();
    AccelerateFrameInterpolator mFocusInterpolator = new AccelerateFrameInterpolator(0.5f);

    @Override
    public FrameInterpolator getFrameScaleInterpolator() {
        // TODO Auto-generated method stub
        return mScaleInterpolator;
    }

    @Override
    public FrameInterpolator getFrameFocusInterpolator() {
        // TODO Auto-generated method stub
        return mFocusInterpolator;
    }

    @Override
    public Rect getFocusPadding(Rect originalRect, Rect lastOriginalRect, int direction, int focusFrameRate) {
        // TODO Auto-generated method stub
        return null;
    }

    public void onSetImageType(int type) {

        mImageType = type;
    }

    public int onGetImageType() {
        return mImageType;
    }

    /**
     * 初始化布局
     * @param imagetype
     */
    public void onInitItemFrameLayout(int imagetype) {
        mImageType = imagetype;

        FrameLayout.LayoutParams lpImageView_goods = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        lpImageView_goods.setMargins(0, 0, 0, 0);
        mImageView_goods = new CustomImageView(mContext);
        mImageView_goods.setAdjustViewBounds(true);
        mImageView_goods.setScaleType(ScaleType.FIT_XY);
        mImageView_goods.setVisibility(View.VISIBLE);
        addView(mImageView_goods, lpImageView_goods);

        FrameLayout.LayoutParams lpImageView_logo = new FrameLayout.LayoutParams(PingPaiDimension.LOGO_WIDTH,
                PingPaiDimension.LOGO__HEIGHT);
        lpImageView_logo.setMargins(PingPaiDimension.LOGO_MARGIN_LEFT, PingPaiDimension.LOGO_MARGIN_TOP, 0, 0);
        mImageView_logo = new CustomImageView(mContext);
        mImageView_logo.setAdjustViewBounds(true);
        mImageView_logo.setScaleType(ScaleType.FIT_XY);
        addView(mImageView_logo, lpImageView_logo);
        mImageView_logo.setVisibility(View.VISIBLE);

        setVisibility(View.GONE);

    }

    public void onSetItemSize(int width, int height) {
        mItemWidth = width;
        mItemHeight = height;
    }

    public int onGetItemWidth() {
        return mItemWidth;
    }

    public int onGetItemHeight() {
        return mItemHeight;
    }

    public void onSetDefultBitmap(Bitmap bm) {

        if (mImageView_goods == null)
            return;

        mImageView_goods.onSetDispalyBitmap(bm);

    }

    /**
     * 对将要显示的图片处理
     * @param bm
     * @param pageindex
     */
    public void onHandleDisplayBitmap(Bitmap bm, int pageindex) {

        AppDebug.i(TAG, "onHandleDisplayBitmap   bm -->  " + bm);
        AppDebug.i(TAG, "onHandleDisplayBitmap   pageindex -->  " + pageindex);

        // 如果不是当前页了，那么图片就不需要处理了
        if (pageindex != mPageIndex) {
            return;
        }

        final Bitmap bmp = PingPaiTuanImageHandleUint.onHandleDisplayBitmap(bm, PingPaiDimension.ITEM_WIDTH,
                PingPaiDimension.ITEM_HEIGHT, PingPaiDimension.ITEM_WIDTH, PingPaiDimension.INFOTABEL__HEIGHT,
                mBrandModel);

        mMainHandler.post(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                onDisplayImage(bmp);
            }

        });

    }

    /**
     * 显示图片
     * @param bm
     */
    public void onDisplayImage(Bitmap bm) {

        if (bm == null)
            return;

        if (mImageView_goods == null)
            return;

        mBitmap = bm;

        mImageView_goods.onSetDispalyBitmap(mBitmap);
    }

    public void onSetDisplayImageBitmap(Bitmap bm, int position) {

        //        Bitmap bmp = PingPaiTuanImageHandleUint.onHandleDisplayBitmap(bm, PingPaiDimension.ITEM_WIDTH, PingPaiDimension.ITEM_HEIGHT, PingPaiDimension.ITEM_WIDTH, PingPaiDimension.INFOTABEL__HEIGHT, mBrandModel);
        //
        //        if (bmp == null)
        //            return;
        //
        //        if (mImageView_goods == null)
        //            return;
        //
        //        mBitmap = bmp;
        //
        //        mImageView_goods.onSetDispalyBitmap(mBitmap);

        AppDebug.i(TAG, "onSetDisplayImageBitmap   mPageIndex -->  " + mPageIndex);
        AppDebug.i(TAG, "onSetDisplayImageBitmap   position -->  " + position);

        // 等图片加载完后，如果页数发生变化，那也不刷新图片数据
        if (mPageIndex != position) {
            return;
        }

        // 如果当前页是小于第三页的，则立即处理，并显示
        // 否则添加到后期的处理队列中，用另外的线程进行处理
        if (mPageIndex < 2) {
            Bitmap bmp = PingPaiTuanImageHandleUint.onHandleDisplayBitmap(bm, PingPaiDimension.ITEM_WIDTH,
                    PingPaiDimension.ITEM_HEIGHT, PingPaiDimension.ITEM_WIDTH, PingPaiDimension.INFOTABEL__HEIGHT,
                    mBrandModel);

            onDisplayImage(bmp);
        } else {
            AppDebug.i(TAG, "onSetDisplayImageBitmap   mPingPaiPagerAdapter -->  " + mPingPaiPagerAdapter);

            if (mPingPaiPagerAdapter != null) {
                Handler mDispalyHandler = mPingPaiPagerAdapter.onGetDispalyHandler();

                AppDebug.i(TAG, "onSetDisplayImageBitmap   mDispalyHandler -->  " + mDispalyHandler);

                if (mDispalyHandler != null) {

                    mDispalyBitmapRun = new DispalyBitmapRun(bm, mPageIndex);

                    mDispalyHandler.post(mDispalyBitmapRun);
                }

            }
        }

    }

    // 释放 商品和信息图片
    public void onRecycleDisplayBitmap(Bitmap defaultBm) {

        if (mDispalyBitmapRun != null) {
            if (mPingPaiPagerAdapter != null) {
                Handler mDispalyHandler = mPingPaiPagerAdapter.onGetDispalyHandler();

                AppDebug.i(TAG, "onSetDisplayImageBitmap   mDispalyHandler -->  " + mDispalyHandler);

                if (mDispalyHandler != null) {
                    mDispalyHandler.removeCallbacks(mDispalyBitmapRun);
                }

            }
        }

        mImageView_goods.onSetDispalyBitmap(defaultBm);

        if ((mBitmap != null) && (!mBitmap.isRecycled())) {
            mBitmap.recycle();
        }

        mBitmap = null;
    }

    // 释放LOGO图片
    public void onRecycleLogoBitmap(Bitmap logobm) {

        mImageView_logo.onSetDispalyBitmap(logobm);

        // 备注： 由JuImageLoader下载器中的虚引用释放图片

        //        if ((mLogoBitmap != null) && (!mLogoBitmap.isRecycled())) {
        //            mLogoBitmap.recycle();
        //        }

        mLogoBitmap = null;

    }

    public void onSetLogoBitmap(Bitmap bm, int position) {

        if (position != mPageIndex) {
            return;
        }

        if (bm == null)
            return;

        if (mImageView_logo == null)
            return;

        mLogoBitmap = bm;
        mImageView_logo.onSetDispalyBitmap(mLogoBitmap);
    }

    public void onSetTitle(String title) {
        if (title == null)
            return;
        if (mTextView == null)
            return;
        mTextView.setText(title);
    }

    public void onShowTitle(boolean show) {
        if (mTextView == null)
            return;

        if (show) {
            // mTextView.setVisibility(View.VISIBLE);
        } else {
            mTextView.setVisibility(View.GONE);
        }
    }

    public void onSetItemIndex(int index) {
        mItemIndex = index;
    }

    public int onGetItemIndex() {
        return mItemIndex;
    }

    public void onSetPageIndex(int pageindex) {
        mPageIndex = pageindex;
    }

    public int onGetPageIndex() {
        return mPageIndex;
    }

    public void onSetActivity(Activity activity) {
        mActivity = activity;
    }

    public void onSetPagerAdapter(PingPaiPagerAdapter classificationPagerAdapter) {
        mPingPaiPagerAdapter = classificationPagerAdapter;
    }

    //设置数据
    public BrandMO onGetBrandModel() {

        return mBrandModel;

    }

    /**
     * 刷新条目数据，并且请求图片
     * @param itemData
     * @param position
     */
    public void onRefreshItemInfo(final BrandMO itemData, final int position) {

        AppDebug.i(TAG, "onRefreshItemInfo -- >  itemData = " + itemData);
        AppDebug.i(TAG, "onRefreshItemInfo -- >  position = " + position);

        if (itemData == null)
            return;

        // 如果不是当前页的数据，不请求图片
        if (position != mPageIndex) {
            return;
        }

        mBrandModel = itemData;

        String itemLogoUrl = mBrandModel.getJuLogo();
        String itemImageUrl = mBrandModel.getJuBanner();

        if (mBrandModel.getJuLogo() != null && mBrandModel.getJuLogo().length() > 0) {
            itemLogoUrl = SystemUtil.mergeImageUrl(mBrandModel.getJuLogo())
                    + ImageUtil.getImageUrlExtraBySize(R.dimen.dp_120);
        }
        if (mBrandModel.getJuBanner() != null && mBrandModel.getJuBanner().length() > 0) {
            itemImageUrl = SystemUtil.mergeImageUrl(mBrandModel.getJuBanner())
                    + ImageUtil.getImageUrlExtraBySize(R.dimen.dp_360);
        }

        AppDebug.i(TAG, "onRefreshItemInfo -- > itemLogoUrl = " + itemLogoUrl);
        AppDebug.i(TAG, "onRefreshItemInfo -- > itemImageUrl = " + itemImageUrl);

        if (mImageLoaderManager == null)
            return;

        //        //        if (mBitmap == null) {
        //        //            mJuImageLoader.loadImage(itemImageUrl, mImageView_goods, new GoodsImageLoadingListener());
        //
        //        mJuImageLoader.loadImage(itemImageUrl, new GoodsImageLoadingListener(mPageIndex));
        //        //        }
        //
        //        //        if (mLogoBitmap == null) {
        //        //            mJuImageLoader.loadImage(itemLogoUrl, mImageView_logo, new LogoImageLoadingListener());
        //
        //        mJuImageLoader.loadImage(itemLogoUrl, new LogoImageLoadingListener(mPageIndex));
        //        //        }

        /**
         * 此处用了两个监听类，必须传入页数值
         */
        mImageLoaderManager.loadImage(itemImageUrl, new GoodsImageLoadingListener(mPageIndex));
        mImageLoaderManager.loadImage(itemLogoUrl, new LogoImageLoadingListener(mPageIndex));

    }

    /**
     * 此函数暂时无效。
     * @param classficationImageHandle
     * @param juitemSummary
     */
    public void onReUpdateInfo(ImageHandleUnit.ClassficationImageHandle classficationImageHandle, ItemMO juitemSummary) {

        AppDebug.i(TAG, "onReUpdateInfo   classficationImageHandle -->  " + classficationImageHandle
                + ", juitemSummary =  " + juitemSummary);

        if ((classficationImageHandle == null) || (juitemSummary == null))
            return;

        //         Bitmap bm = classficationImageHandle.onHandleDisplayBitmap(mBitmap, this, juitemSummary); 
        //         onSetDisplayImageBitmap(bm);
    }

    /**
     * 商品图像下载 --- 监听类
     * @author yunzhong.qyz
     */
    private class GoodsImageLoadingListener implements ImageLoadingListener {

        private int mPageIndex = -1;

        public GoodsImageLoadingListener(int pageIndex) {

            mPageIndex = pageIndex;
        }

        @Override
        public void onLoadingCancelled(String arg0, View arg1) {

            AppDebug.i(TAG, "GoodsImageLoadingListener  onLoadingCancelled -- > arg0 = " + arg0);
        }

        @Override
        public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {

            AppDebug.i(TAG, "GoodsImageLoadingListener  onLoadingComplete -- > arg0 = " + arg0);

            onSetDisplayImageBitmap(arg2, mPageIndex);
        }

        @Override
        public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {

            AppDebug.i(TAG, "GoodsImageLoadingListener  onLoadingFailed -- > arg0 = " + arg0);
        }

        @Override
        public void onLoadingStarted(String arg0, View arg1) {

            AppDebug.i(TAG, "GoodsImageLoadingListener  onLoadingStarted -- > arg0 = " + arg0);

            if (!NetWorkUtil.isNetWorkAvailable()) {
                NetWorkCheck.netWorkError(mContext);
            }
        }

    }

    /**
     * LOGO 图片的下载 --- 监听类
     * @author yunzhong.qyz
     */
    private class LogoImageLoadingListener implements ImageLoadingListener {

        private int mPageIndex = -1;

        public LogoImageLoadingListener(int pageindex) {

            mPageIndex = pageindex;
        }

        @Override
        public void onLoadingCancelled(String arg0, View arg1) {

            AppDebug.i(TAG, "LogoImageLoadingListener  onLoadingCancelled -- > arg0 = " + arg0);
        }

        @Override
        public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {

            AppDebug.i(TAG, "LogoImageLoadingListener  onLoadingComplete -- > arg0 = " + arg0);

            onSetLogoBitmap(arg2, mPageIndex);

        }

        @Override
        public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {

            AppDebug.i(TAG, "LogoImageLoadingListener  onLoadingFailed -- > arg0 = " + arg0);

        }

        @Override
        public void onLoadingStarted(String arg0, View arg1) {

            AppDebug.i(TAG, "LogoImageLoadingListener  onLoadingStarted -- > arg0 = " + arg0);

        }

    }

    /**
     * 图片处理任务类
     * @author yunzhong.qyz
     */
    public class DispalyBitmapRun implements Runnable {

        private Bitmap mBitmap = null;

        // 因为PAGE页需要重用
        private int mRunPageIndex = -1;

        public DispalyBitmapRun(Bitmap bm, int pageindex) {

            AppDebug.i(TAG, "DispalyBitmapRun   bm -->  " + bm);
            AppDebug.i(TAG, "DispalyBitmapRun   pageindex -->  " + pageindex);

            mBitmap = bm;

            mRunPageIndex = pageindex;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub 
            onHandleDisplayBitmap(mBitmap, mRunPageIndex);
        }

    };

}
