package com.yunos.tvtaobao.biz.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.assist.FailReason;
import com.tvlife.imageloader.core.display.BitmapDisplayer;
import com.tvlife.imageloader.core.display.drawable.RoundedAndReviseSizeDrawable;
import com.tvlife.imageloader.core.listener.ImageLoadingListener;
import com.yunos.tv.app.widget.focus.FocusFlipGridView;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageHandleManager;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.biz.listener.VerticalItemHandleListener;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.widget.TabFlipGridViewHeaderView;
import com.yunos.tvtaobao.biz.widget.TabGoodsItemView;
import com.yunos.tvtaobao.businessview.R;

import java.lang.ref.WeakReference;

public abstract class TabGoodsBaseAdapter extends BaseAdapter {

    public final static String TAG = "TabGoodsBaseAdapter";

    private Context mContext;
    // 关联的GridView
    private FocusFlipGridView mFocusFlipGridView;

    // 选中的行发生变化的监听
    private VerticalItemHandleListener mVerticalItemHandleListener;

    // 主 Handler
    private Handler mMainHandler;

    // 关联的 TabKey
    protected String mTabKey;

    // 图片下载器
    private ImageLoaderManager mImageLoaderManager;

    // 选中新行之前的行数
    private int mOldRow;
    
    // 商品图片的圆角值 
    protected int mCornerRadius;

    // 是否带有HeaderView 
    private boolean mHaveHeaderView;

    // ImageLoader选项
    private DisplayImageOptions mImageOptions;

    // 已经检查过图片的加载
    private boolean mHaveCheckBitmapLoading;

    // 是否已经调用了 notifyDataSetChanged 函数
    private boolean mIsNotifyDataSetChanged;
 
    
    public TabGoodsBaseAdapter(Context context, boolean haveHeaderView) {

        mContext = context;

        mHaveHeaderView = haveHeaderView;
        mHaveCheckBitmapLoading = false;
        mIsNotifyDataSetChanged = false;

        mOldRow = 0;
        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(mContext);

        mCornerRadius = mContext.getResources().getDimensionPixelSize(R.dimen.dp_0);

        boolean beta = GlobalConfig.instance != null && GlobalConfig.instance.isBeta();
        mImageOptions = new DisplayImageOptions.Builder().cacheOnDisc(beta).cacheInMemory(!beta).build();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View resultView = getFillView(position, convertView, parent);

        AppDebug.i(TAG, "getView  -->  position = " + position + "; convertView = " + convertView + "; resultView = "
                + resultView);

        if (resultView instanceof TabGoodsItemView) {
            // 如果是  TabGoodsItemView 的子类， 那么处理相同的功能
            TabGoodsItemView tabGoodsItemView = (TabGoodsItemView) resultView;
            commonfillView(position, tabGoodsItemView, parent);
        }

        return resultView;
    }

    /**
     * 设置选择新行时的监听
     * @param l
     */
    public void setOnVerticalItemHandleListener(VerticalItemHandleListener l) {
        mVerticalItemHandleListener = l;
    }

    /**
     * 设置是否已经改变数据
     * @param isNotifyDataSetChanged
     */
    public void setIsNotifyDataSetChanged(boolean isNotifyDataSetChanged) {
        mIsNotifyDataSetChanged = isNotifyDataSetChanged;
    }

    
    /**
     * 设置主线程的Handler
     * @param handler
     */
    public void onSetMainHandler(Handler handler) {
        AppDebug.i(TAG, "onSetMainHandler   handler = " + handler);
        mMainHandler = handler;
    }
 
    /**
     * 设置关联的 tabKey
     * @param tabKey
     */
    public void onSetTabKey(String tabKey) {
        mTabKey = tabKey;
    }

   
    /**
     * 获取关联的tabKey
     * @return
     */
    public String onGetTabKey() {
        return mTabKey;
    }

    /**
     * 设置关联的FocusFlipGridView
     * @param focusFlipGridView
     */
    public void onSetFocusFlipGridView(FocusFlipGridView focusFlipGridView) {
        mFocusFlipGridView = focusFlipGridView;
    }

    /**
     * 获取 图片的圆角值
     * @return
     */
    public int onGetCornerRadius() {
        return mCornerRadius;
    }

    /**
     * 获取选中新行之前的行
     * @return
     */
    public int getOldRow() {
        return mOldRow;
    }

    /**
     * 是否是有headerview
     * @return
     */
    public boolean isHaveHeaderView() {
        return mHaveHeaderView;
    }

    /**
     * 通知更新
     */
    public void onNotifyDataSetChanged() {
        this.notifyDataSetChanged();
    }
 
    /**
     * 处理共同的功能，包括TabKey， Position的设置
     * @param position
     * @param convertView
     * @param parent
     */
    private void commonfillView(int position, TabGoodsItemView convertView, ViewGroup parent) {

        TabGoodsItemView goodListItemFrameLayout = convertView;

        AppDebug.i(TAG, "commonfillView   position = " + position + ";  convertView = " + convertView);

        if (goodListItemFrameLayout == null) {
            return;
        }

        goodListItemFrameLayout.onSetTabKey(mTabKey);
        goodListItemFrameLayout.onSetPosition(position);

        if (!mIsNotifyDataSetChanged) {
            // 隐藏信息内容
            goodListItemFrameLayout.onHideInfoImageView();
            // 设置默认图
            goodListItemFrameLayout.onSetGoodsListDefaultDrawable(getDefaultDisplayPicture(), position);
        }

        if (((!mHaveCheckBitmapLoading) && (position < (getColumnsCounts() * 2))) || (mIsNotifyDataSetChanged)) {
            // 加载新的信息内容
            onRequestInfoOfItem(goodListItemFrameLayout, mTabKey, position);
            // 加载新的商品图片
            onRequestImageOfItem(goodListItemFrameLayout, mTabKey, position);
        }

        AppDebug.i(TAG, "commonfillView   position = " + position + ";  goodListItemFrameLayout = "
                + goodListItemFrameLayout + "; mIsNotifyDataSetChanged = " + mIsNotifyDataSetChanged + "; mHaveCheckBitmapLoading = " + mHaveCheckBitmapLoading + "; mTabKey = " + mTabKey);

    }

    /**
     * 设置是否需要检查图片加载
     * @param check
     */
    public void onSetCheckVisibleItem(boolean check) {
        mHaveCheckBitmapLoading = check;
    }

   
    /**
     * 检查可见条目，并启动商品图片和商品信息图片的加载
     */
    public void onCheckVisibleItemAndLoadBitmap() {
        ImageHandleManager.getImageHandleManager(mContext).executeTask(
                new LoadBitmapRunnable(new WeakReference<TabGoodsBaseAdapter>(this)));
    }

    
    /**
     * 商品信息图片的显示处理
     * @param fenLeiItemView
     * @param position
     */
    public void onDisplayHandleInfo(TabGoodsItemView fenLeiItemView, int position) {
        ImageHandleManager.getImageHandleManager(mContext).executeTask(
                new DisplayInfoRunnable(new WeakReference<TabGoodsBaseAdapter>(this),
                        new WeakReference<TabGoodsItemView>(fenLeiItemView), position));
    }

   
    /**
     * 商品图片的显示处理
     * @param fenLeiItemView
     * @param position
     * @param bm
     */
    public void onDisplayHandleGoods(TabGoodsItemView fenLeiItemView, int position, Bitmap bm) {
        ImageHandleManager.getImageHandleManager(mContext).executeTask(
                new DisplayGoodsBitmapRunnable(new WeakReference<TabGoodsBaseAdapter>(this),
                        new WeakReference<TabGoodsItemView>(fenLeiItemView), position, new WeakReference<Bitmap>(bm)));

    }
 
    /**
     * 处理当选中条目时的情况， 用于加载数据
     * @param position
     * @param isSelected
     * @param fatherView
     */
    public void onItemSelected(int position, boolean isSelected, View fatherView) {

        AppDebug.i(TAG, "onGetview  ---->  position = " + position + ";  isSelected = " + isSelected
                + ", fatherView = " + fatherView);

        if (!isSelected) {
            return;
        }

        if (isHaveHeaderView()) {
            position -= 1;
        }

        if (position < 0) {
            return;
        }

        int currentRow = position / getColumnsCounts();

        if (mOldRow != currentRow) {
            // 检查是否要加载数据
            if (mVerticalItemHandleListener != null) {
                mVerticalItemHandleListener.onGetview(mFocusFlipGridView, mTabKey, position);
            }
        }
        mOldRow = currentRow;
    }

    /**
     * 程序退出时，释放资源
     */
    public void onClearAndDestroy() {

    }
 
    /**
     * 请求处理商品信息图片
     * @param fenLeiItemView
     * @param ordey
     * @param position
     */
    public void onRequestInfoOfItem(TabGoodsItemView fenLeiItemView, String ordey, final int position) {

        AppDebug.i(TAG, "onRequestInfoOfItem   position = " + position);

        fenLeiItemView.setHideInfoDrawable(false);
        onDisplayHandleInfo(fenLeiItemView, position);
    }
 
    /**
     * 请求处理商品图片
     * @param fenLeiItemView
     * @param tabkey
     * @param position
     */
    public void onRequestImageOfItem(TabGoodsItemView fenLeiItemView, String tabkey, final int position) {

        final String mPicUrl = getNetPicUrl(tabkey, position);

        AppDebug.i(TAG, "onRequestImageOfItem   position =  " + position + "; mPicUrl = " + mPicUrl
                + "; mImageLoaderManager = " + mImageLoaderManager);

        if (mImageLoaderManager != null && fenLeiItemView != null) {
            fenLeiItemView.setDefaultDrawable(false);
            mImageLoaderManager.loadImage(mPicUrl, mImageOptions, new GoodsImageLoadingListener(
                    new WeakReference<TabGoodsBaseAdapter>(this), new WeakReference<TabGoodsItemView>(fenLeiItemView),
                    position));
        }
    }

    /**
     * 获取商品的标题
     * @param tabkey
     * @param position
     * @return
     */
    public abstract String getGoodsTitle(String tabkey, int position);

    
    /**
     * 获取每行显示的个数
     * @return
     */
    public abstract int getColumnsCounts();

   
    /**
     * 获取默认图
     * @return
     */
    public abstract Drawable getDefaultDisplayPicture(); 
   
    /**
     * 获取商品信息图
     * @param tabkey
     * @param position
     * @return
     */
    public abstract Drawable getInfoDiaplayDrawable(String tabkey, int position);

   
    /**
     * 获取商品图片地址
     * @param tabkey
     * @param position
     * @return
     */
    public abstract String getNetPicUrl(String tabkey, int position);

    
    /**
     * 适配器中的获取VIEW
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public abstract View getFillView(int position, View convertView, ViewGroup parent);

    /**
     * 商品图像下载 --- 监听类
     * @author yunzhong.qyz
     */
    private static class GoodsImageLoadingListener implements ImageLoadingListener {

        private final int mPosition;
        private final WeakReference<TabGoodsItemView> mFenLeiItemViewRef;
        protected final WeakReference<TabGoodsBaseAdapter> weakReference;

        public GoodsImageLoadingListener(WeakReference<TabGoodsBaseAdapter> weakReference,
                WeakReference<TabGoodsItemView> fenLeiItemViewRef, int position) {
            this.weakReference = weakReference;
            mFenLeiItemViewRef = fenLeiItemViewRef;
            mPosition = position;
        }

        @Override
        public void onLoadingCancelled(String arg0, View arg1) {
            AppDebug.i(TAG, "GoodsImageLoadingListener ---> onLoadingCancelled -->  mPosition =  " + mPosition
                    + ";    mPicUrl = " + arg0);
        }

        @Override
        public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
            AppDebug.i(TAG, "GoodsImageLoadingListener ---> onLoadingComplete -->  mPosition =  " + mPosition
                    + ";    mPicUrl = " + arg0);
            TabGoodsItemView fenLeiItemView = null;
            if (mFenLeiItemViewRef != null) {
                fenLeiItemView = mFenLeiItemViewRef.get();
            }
            if (weakReference != null) {
                TabGoodsBaseAdapter fenLeiGoodsAdapter = weakReference.get();
                if (fenLeiGoodsAdapter != null) {
                    fenLeiGoodsAdapter.onDisplayHandleGoods(fenLeiItemView, mPosition, arg2);
                }
            }
        }

        @Override
        public void onLoadingStarted(String arg0, View arg1) {
            AppDebug.i(TAG, "GoodsImageLoadingListener ---> onLoadingStarted -->  mPosition =  " + mPosition
                    + ";    mPicUrl = " + arg0);
        }

        @Override
        public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
            AppDebug.i(TAG, "GoodsImageLoadingListener ---> onLoadingFailed -->  mPosition =  " + mPosition
                    + ";    mPicUrl = " + arg0);
        }

    }

    private static class DisplayGoodsBitmapRunnable implements Runnable {

        private final int mPosition;
        private final WeakReference<Bitmap> mNetBmRef;
        private final WeakReference<TabGoodsItemView> mFenLeiItemViewRef;
        protected final WeakReference<TabGoodsBaseAdapter> weakReference;

        public DisplayGoodsBitmapRunnable(WeakReference<TabGoodsBaseAdapter> weakReference,
                WeakReference<TabGoodsItemView> fenLeiItemViewRef, int position, WeakReference<Bitmap> netBmRef) {
            mPosition = position;
            mNetBmRef = netBmRef;
            mFenLeiItemViewRef = fenLeiItemViewRef;
            this.weakReference = weakReference;
        }

        @Override
        public void run() {
            final TabGoodsBaseAdapter tabGoodsBaseAdapter = weakReference.get();
            if (tabGoodsBaseAdapter != null) {
                if (mNetBmRef != null) {
                    Bitmap mNetBm = mNetBmRef.get();
                    if (mNetBm != null) {
                        final BitmapDrawable drawable = new BitmapDrawable(mNetBm);

                        final Handler mtabHandler = tabGoodsBaseAdapter.mMainHandler;

                        if (mtabHandler != null) {
                            mtabHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                    if (mFenLeiItemViewRef != null) {
                                        TabGoodsItemView fenLeiItemView = mFenLeiItemViewRef.get();
                                        if (fenLeiItemView != null) {
                                            fenLeiItemView.onSetGoodsListDrawable(drawable, mPosition);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    private static class DisplayInfoRunnable implements Runnable {

        private final int mPosition;
        private final WeakReference<TabGoodsItemView> mFenLeiItemViewRef;
        protected final WeakReference<TabGoodsBaseAdapter> weakReference;

        public DisplayInfoRunnable(WeakReference<TabGoodsBaseAdapter> weakReference,
                WeakReference<TabGoodsItemView> fenLeiItemViewRef, int position) {
            mPosition = position;
            mFenLeiItemViewRef = fenLeiItemViewRef;
            this.weakReference = weakReference;
        }

        @Override
        public void run() {
            final TabGoodsBaseAdapter tabGoodsBaseAdapter = weakReference.get();
            if (tabGoodsBaseAdapter != null) {
                final Drawable drawable = tabGoodsBaseAdapter.getInfoDiaplayDrawable(tabGoodsBaseAdapter.mTabKey,
                        mPosition);
                final Handler mtabHandler = tabGoodsBaseAdapter.mMainHandler;
                if (mtabHandler != null && drawable != null) {
                    mtabHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (mFenLeiItemViewRef != null) {
                                TabGoodsItemView fenLeiItemView = mFenLeiItemViewRef.get();
                                if (fenLeiItemView != null) {
                                    fenLeiItemView.onSetInfoListDrawable(drawable, mPosition);
                                }
                            }
                        }
                    });
                }
            }

        }
    }

    /**
     * 图片和商品信息的检查类
     * @author yunzhong.qyz
     */
    private static class LoadBitmapRunnable implements Runnable {

        protected final WeakReference<TabGoodsBaseAdapter> weakReference;

        public LoadBitmapRunnable(WeakReference<TabGoodsBaseAdapter> weakReference) {
            this.weakReference = weakReference;
        }

        @Override
        public void run() {

            final TabGoodsBaseAdapter tabGoodsBaseAdapter = weakReference.get();
            if (tabGoodsBaseAdapter != null) {
                final Handler mtabHandler = tabGoodsBaseAdapter.mMainHandler;
                if (mtabHandler != null) {
                    mtabHandler.post(new Runnable() {

                        @Override
                        public void run() {

                            FocusFlipGridView focusFlipGridView = tabGoodsBaseAdapter.mFocusFlipGridView;
                            if (focusFlipGridView == null) {
                                return;
                            }

                            final int firstVisibleItem = focusFlipGridView.getFirstVisiblePosition();
                            final int endVisibleItem = focusFlipGridView.getLastVisiblePosition() + 1;

                            AppDebug.i(TAG, "LoadBitmapRunnable ---> " + this + "; firstVisibleItem = "
                                    + firstVisibleItem + "; endVisibleItem = " + endVisibleItem);

                            if (firstVisibleItem < 0) {
                                return;
                            }

                            for (int itempos = firstVisibleItem; itempos < endVisibleItem; itempos++) {

                                int index = itempos - firstVisibleItem;
                                View childView = focusFlipGridView.getChildAt(index);

                                AppDebug.i(TAG, "LoadBitmapRunnable --->  index = " + index + "; childView = "
                                        + childView);

                                if (childView instanceof TabGoodsItemView) {
                                    // 如果是TabGoodsItemView 的子类，那么处理
                                    TabGoodsItemView fenLeiItemView = (TabGoodsItemView) childView;
                                    int position = fenLeiItemView.onGetPosition();
                                    // 加载商品图片
                                    tabGoodsBaseAdapter.onRequestImageOfItem(fenLeiItemView,
                                            tabGoodsBaseAdapter.mTabKey, position);
                                    // 加载信息内容
                                    tabGoodsBaseAdapter.onRequestInfoOfItem(fenLeiItemView,
                                            tabGoodsBaseAdapter.mTabKey, position);

                                    AppDebug.i(TAG, "LoadBitmapRunnable --->  fenLeiItemView = " + fenLeiItemView
                                            + "; position = " + position);
                                } else if (childView instanceof TabFlipGridViewHeaderView) {
                                    // 把 TabFlipGridViewHeaderView也加入图片检查的队列中
                                    int cornerRadius = tabGoodsBaseAdapter.onGetCornerRadius();
                                    TabFlipGridViewHeaderView tabFlipGridViewHeaderView = (TabFlipGridViewHeaderView) childView;
                                    if (tabFlipGridViewHeaderView != null) {
                                        tabFlipGridViewHeaderView.onHandleHeaderContent(cornerRadius);
                                    }
                                    AppDebug.i(TAG, "LoadBitmapRunnable --->  tabFlipGridViewHeaderView = "
                                            + tabFlipGridViewHeaderView + "; cornerRadius = " + cornerRadius);
                                }
                            }
                        }
                    });
                }
            }
        }
    }
}
