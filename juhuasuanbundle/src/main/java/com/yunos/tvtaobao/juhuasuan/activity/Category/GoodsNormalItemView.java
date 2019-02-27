package com.yunos.tvtaobao.juhuasuan.activity.Category;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.tvlife.imageloader.core.assist.FailReason;
import com.tvlife.imageloader.core.listener.ImageLoadingListener;
import com.tvlife.imageloader.utils.ClassicOptions;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.AppHandler;
import com.yunos.tv.core.common.ImageHandleManager;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tvlife.app.widget.FocusedBasePositionManager;
import com.yunos.tvtaobao.biz.dialog.WaitProgressDialog;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.juhuasuan.activity.HomeActivity;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CategoryMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.constant.HandleWhat;
import com.yunos.tvtaobao.juhuasuan.clickcommon.ToItemDetail;
import com.yunos.tvtaobao.juhuasuan.config.SystemConfig;
import com.yunos.tvtaobao.juhuasuan.request.MyBusinessRequest;
import com.yunos.tvtaobao.juhuasuan.util.StringUtil;
import com.yunos.tvtaobao.juhuasuan.util.Utils;
import com.yunos.tvtaobao.juhuasuan.view.ImageView;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * 一般的商品显示信息 1.本商品展示信息 2.带有是否可以前后聚焦的信息 3.商品的图片跟其它信息是分开加载，为提高动画的速度
 *
 * @author tim
 */
public class GoodsNormalItemView extends LinearLayout implements FocusedBasePositionManager.ItemInterface {

    private static final String TAG = "ItemListGoodsNormalItemView";
    private final long IMAGE_LOAD_DELAY_TIME = 500; // 延时加载图片的延时时间，但闻毫秒
    private HomeActivity mActivity; // 上下文件
    private WaitProgressDialog mWaitDialog;
    private ImageLoaderManager mImageLoaderManager; // 加载图片
    private MyBusinessRequest mBusinessRequest; // 请求数据
    private ItemMO mItemData; // 商品信息数据
    // 商品信息的控件
    private ImageView mImage;
    private int mPosition = -1; // 本商品在商品展示列表中的位置序号
    private int mPage = 0;
    private CategoryMO mCate = null; // 本商品是属于哪个分类ID
    private boolean mCanNextFocus = true; // 默认可以聚焦next
    private boolean mCanPreFocus = true; // 默认可以聚焦pre
    private Fragment mFragment; // 商品属于哪个fragment里面
    private boolean mImageShowed; // 商品的图片是否已显示,未显示的请求显示
    private boolean mGoodsInfoShowed; // 商品信息是否已经显示,因信息的生成需要布局完后所以首次生成的时候会放到dispatchDraw里面
    private Bitmap mGoodsInfoBitmap; // 商品信息的Bitmap
    private Bitmap mLoadImageBitmap; // 商品图片的Bitmap,传给详情页
    private boolean mIsHide; // 页面是否隐藏
    private boolean mIsSlide; // 页面是否正在滑动
    private boolean mIsGoodsInfoLoading; // 商品信息是否正在加载中
    // 将显示动画分解掉，随意准备被中断
    private GoodsNormalItemViewHandle mHandler = new GoodsNormalItemViewHandle(this);
    // 商品折扣等信息生成图片的Runnable
    private Runnable mRunnable;

    /**
     * 商品折扣等信息生成图片的Runnable
     *
     * @author hanqi
     * @data 2015-2-12 上午10:22:41
     */
    private static class MyRunnable implements Runnable {

        private WeakReference<GoodsNormalItemView> mRef;
        private GoodsNormalItemViewInfoRequestData mData;

        public MyRunnable(GoodsNormalItemView view, GoodsNormalItemViewInfoRequestData data) {
            mRef = new WeakReference<GoodsNormalItemView>(view);
            mData = data;
        }

        public void run() {
            final GoodsNormalItemView view = mRef.get();
            if (null == view) {
                return;
            }
            Bitmap bmp = JuCustomerImageFactory.createGoodsNormalItemViewInfo(view.getContext(), mData);
            if (bmp != null) {
                view.mHandler.sendMessage(view.mHandler.obtainMessage(HandleWhat.ITEMVIEW_MSG_CREATEINFO_IMAGE, bmp));
            }
        }
    }

    /**
     * 构造方法
     *
     * @param context
     * @param attrs
     */
    public GoodsNormalItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mBusinessRequest = MyBusinessRequest.getInstance();
        LayoutInflater.from(context).inflate(R.layout.jhs_category_goods_normal_item, this, true);
        initUI(context);
        setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mFragment != null) {
                    CategoryViewPager viewPager = (CategoryViewPager) mFragment.getView().findViewById(R.id.goods_page);
                    viewPager.requestFocus();
                    // 是否需要重新更新所有的列表
                    AppDebug.i(TAG, TAG + ".onFocusChange ItemListGoodsNormalItemView need refresh all list = "
                            + viewPager.getNeedRefreshAllList());
                    if (viewPager.getNeedRefreshAllList()) {
                        v.clearAnimation();
                        viewPager.refreshList();
                    }
                    AppDebug.i(TAG, TAG + ".onFocusChange visible=" + v.getVisibility() + ", alpha=" + v.getAlpha()
                            + ", view=" + v);
                }
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        AppDebug.i(TAG, "ItemListGoodsNormalItemView onLayout changed=" + changed);
        if (changed) {
            super.onLayout(changed, l, t, r, b);
        }
    }

    /**
     * 设置当前商品页是否为隐藏
     *
     * @param isHide
     */
    public void setHide(boolean isHide) {
        mIsHide = isHide;
        if (mIsHide) {
            cancelLoadImage();
        }
    }

    /**
     * 设置当前页面是否正在滑动
     *
     * @param isSlide
     */
    public void setSlide(boolean isSlide) {
        mIsSlide = isSlide;
        if (!mIsSlide && SystemConfig.DIPEI_BOX) {
            refreshImage();
        }
    }

    /**
     * 设置该商品所在分类的ID
     *
     * @param cate
     */
    public void setCategory(CategoryMO cate) {
        mCate = cate;
    }

    /**
     * 设置是否可以聚焦到后面商品
     *
     * @param
     */
    public void setCanNextFocus(boolean can) {
        mCanNextFocus = can;
    }

    /**
     * 取得是否可以聚焦到后面商品
     *
     * @return
     */
    public boolean getCanNextFocus() {
        return mCanNextFocus;
    }

    /**
     * 设置是否可以聚焦到前面商品
     *
     * @param can
     */
    public void setCanPreFocus(boolean can) {
        mCanPreFocus = can;
    }

    /**
     * 取得是否可以聚焦到前面商品
     *
     * @return
     */
    public boolean getCanPreFocus() {
        return mCanPreFocus;
    }

    /**
     * 取得在商品列表中的位置
     *
     * @return
     */
    public int getPosition() {
        return mPosition;
    }

    /**
     * 设置fragment
     *
     * @param fragment
     */
    public void setFragment(Fragment fragment) {
        mFragment = fragment;
    }

    /**
     * 初始化商品
     *
     * @param context
     */
    private void initUI(Context context) {
        if (context instanceof HomeActivity) {
            mActivity = (HomeActivity) context;
        }
        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(getContext());
        mImage = (ImageView) findViewById(R.id.good_image);
        setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mItemData != null) {
                    onClickGoodsItem(mItemData);
                }
            }
        });
    }

    /**
     * 刷新商品信息（是直接请求加载图片还是后续再请求）
     *
     * @param refreshImageAtTime
     * @param itemData
     * @param position
     */
    public void refreshGoodsItem(boolean refreshImageAtTime, ItemMO itemData, int position, int page) {
        AppDebug.i(TAG, "refreshGoodsItem refreshImageAtTime=" + refreshImageAtTime + " position=" + position
                + ", page=" + page + ", itemData=" + itemData + ", visibility=" + this.getVisibility());
        setGoodsItemData(itemData, position, page);
        if (refreshImageAtTime) {
            refreshImage();
            // 因为商品信息的显示需要布局完成后才能做所以不在这里请求生成商品信息的bitmap
        } else {
            mGoodsInfoShowed = false;
            if (!mImageShowed || itemData.getItemId().longValue() != mItemData.getItemId().longValue()) {
                mImageShowed = false;
                mImage.setImageResource(R.drawable.jhs_item_default_image);
            }
        }
    }

    /**
     * 取消图片加载
     */
    public void cancelLoadImage() {
        AppDebug.i(
                TAG,
                TAG + ".cancelLoadImage mHandler.hasMessages()="
                        + mHandler.hasMessages(HandleWhat.ITEMVIEW_MSG_REFRESH_IMAGE));
        if (mHandler.hasMessages(HandleWhat.ITEMVIEW_MSG_REFRESH_IMAGE)) {
            mHandler.removeMessages(HandleWhat.ITEMVIEW_MSG_REFRESH_IMAGE);
        }
    }

    /**
     * 设置商品的数据
     *
     * @param itemData
     */
    public void setGoodsItemData(ItemMO itemData, int position, Integer page) {
        if (null != page) {
            mPage = page;
        }
        mPosition = position;
        mItemData = itemData;
    }

    /**
     * 回收商品信息的Bitmap
     */
    public void recycleGoodsInfoBitmap() {
        if (mGoodsInfoBitmap != null) {
            mGoodsInfoBitmap.recycle();
            mGoodsInfoBitmap = null;
        }
        mGoodsInfoShowed = false;
    }

    /**
     * 回收商品图片Bitmap
     */
    public void recycleLoadBitmap() {
        if (null != mLoadImageBitmap) {
            mLoadImageBitmap.recycle();
            mLoadImageBitmap = null;
        }
        if (mImageShowed) {
            AppDebug.i(TAG, TAG + ".recycleLoadBitmap ImageView mImage is bitmap is recycleing!");
            mImage.release();
            //            ImageViewUtil.recycleImageView(mImage);
        }
        mImageShowed = false;
    }

    public void release() {
        cancelLoadImage();
        recycleGoodsInfoBitmap();
        if (null != mRunnable) {
            mRunnable = null;
        }
        //        recycleLoadBitmap();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        // 将加载完成后的信息bitmap显示在商品里面
        if (mGoodsInfoBitmap != null) {
            mGoodsInfoShowed = true;
            canvas.drawBitmap(mGoodsInfoBitmap, 0, getHeight() / 2, null);
        } else {
            createGoodsInfor();
        }
    }

    /**
     * 更新图片(只有在图片还未加载过的时候显示)
     */
    public void refreshImage() {
        AppDebug.i(TAG, "refreshImage mImageShowed=" + mImageShowed + " mItemData not null " + (mItemData != null));
        if (mImageShowed == false && mItemData != null) {
            if (SystemConfig.DIPEI_BOX) {
                mGoodsInfoShowed = false;
                mImageShowed = false;
                mImage.setImageResource(R.drawable.jhs_item_default_image);
                if (!mIsHide && !mHandler.hasMessages(HandleWhat.ITEMVIEW_MSG_REFRESH_IMAGE)) {
                    mHandler.sendEmptyMessageDelayed(HandleWhat.ITEMVIEW_MSG_REFRESH_IMAGE, IMAGE_LOAD_DELAY_TIME);
                }
            } else {
                refreshImageEx(mItemData);
            }
        }
    }

    /**
     * 创建商品的信息
     */
    private void createGoodsInfor() {
        AppDebug.i(TAG, "createGoodsInfor mGoodsInfoShowed=" + mGoodsInfoShowed);
        if (needLockRefreshGoods()) {
            return;
        }
        // 请求生成商品信息的bitmap
        if (mGoodsInfoShowed == false && mIsGoodsInfoLoading == false && mItemData != null) {
            // 当需要重新生成信息的时候先初始化相关变量
            recycleGoodsInfoBitmap();
            mGoodsInfoShowed = false;
            mIsGoodsInfoLoading = true;
            GoodsNormalItemViewInfoRequestData data = new GoodsNormalItemViewInfoRequestData();
            data.mCate = mCate;
            data.mWith = getWidth();
            data.mHeight = (int) Math.ceil(getHeight() / 2.0);
            data.mItemData = mItemData;

            mRunnable = new MyRunnable(this, data);
            ImageHandleManager.getImageHandleManager(getContext()).executeTask(mRunnable);
        }
    }

    /**
     * 刷新商品的信息
     */
    public void refreshGoodsInfo() {
        AppDebug.i(TAG, "refreshGoodsInfo mGoodsInfoShowed=" + mGoodsInfoShowed + " mGoodsInfoBitmap not null "
                + (mGoodsInfoBitmap != null) + " pos=" + mPosition);
        // 未显示
        if (mGoodsInfoShowed == false) {
            invalidate();
        }
    }

    /**
     * 强制刷新商品信息
     *
     * @param itemData
     * @param position
     */
    public void enforceRefreshGoodsInfo(ItemMO itemData, int position) {
        AppDebug.i(TAG, "enforceRefreshGoodsInfo position=" + position);
        setGoodsItemData(itemData, position, null);
        mGoodsInfoShowed = false;
        recycleGoodsInfoBitmap();
        createGoodsInfor();
    }

    /**
     * 更新图片
     *
     * @param itemData
     */
    private void refreshImageEx(final ItemMO itemData) {
        AppDebug.i(TAG, "refreshImageEx mImageShowed=" + mImageShowed + ", itemData=" + itemData);
        if (needLockRefreshGoods()) {
            return;
        }
        if (mImageShowed) {
            return;
        }
        String itemImageUrl = Utils.getGoodsImageUrl(itemData);
        mImageLoaderManager.displayImage(itemImageUrl, mImage, ClassicOptions.dio565,new ImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                ((ImageView) view).setImageResource(R.drawable.jhs_item_default_image);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (!SystemConfig.DIPEI_BOX) {
                    mLoadImageBitmap = loadedImage;
                }
                if (needLockRefreshGoods()) {
                    mImageShowed = false;
                } else {
                    mImageShowed = true;
                    if (SystemConfig.DIPEI_BOX) {
                        mImage.setImageBitmap(loadedImage);
                    } else {
                        TransitionDrawable td = new TransitionDrawable(new Drawable[]{
                                new ColorDrawable(Color.TRANSPARENT),
                                new BitmapDrawable(getContext().getResources(), loadedImage)});
                        td.startTransition(200);
                        mImage.setImageDrawable(td, loadedImage);
                    }
                }
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                AppDebug.i(TAG, "refreshImageEx try again");
                // 如果被取消重新再加载
                refreshImageEx(itemData);
                // ((ImageView)
                // view).setImageResource(R.drawable.item_default_image);
            }
        });
    }

    /**
     * 是否需要锁住刷新商品
     *
     * @return
     */
    private boolean needLockRefreshGoods() {
        if (mIsHide || mIsSlide) {
            AppDebug.i(TAG, "needLockRefreshGoods true");
            return true;
        } else {
            AppDebug.i(TAG, "needLockRefreshGoods false");
            return false;
        }
    }

    /**
     * 点击商品
     *
     * @param itemData
     */
    private void onClickGoodsItem(final ItemMO itemData) {
        AppDebug.i(TAG, TAG + ".onClickGoodsItem itemData=" + itemData);
        if (mActivity != null && mActivity.isFinishing()) {
            return;
        }

        ToItemDetail.detail(mActivity, itemData.getJuId(), itemData.getItemId());

        // 统计聚划算各类目页各屏的点击情况
        String controlName = com.yunos.tv.core.util.Utils.getControlName("Ju_Category", "P", mPage);
        Map<String, String> p = com.yunos.tv.core.util.Utils.getProperties();
        if (!StringUtil.isEmpty(mCate.getName())) {
            p.put("cate_name", mCate.getName());
        }
        TBS.Adv.ctrlClicked(CT.Frame, controlName, com.yunos.tv.core.util.Utils.getKvs(p));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                onClickGoodsItem(mItemData);
                return true;
            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * Class Descripton.
     *
     * @author hanqi
     * @data 2014-11-18 上午11:39:20
     */
    private static final class GoodsNormalItemViewHandle extends AppHandler<GoodsNormalItemView> {

        /**
         * @param t
         */
        private GoodsNormalItemViewHandle(GoodsNormalItemView t) {
            super(t);
        }

        @Override
        public void handleMessage(Message msg) {
            GoodsNormalItemView view = getT();
            if (null == view) {
                return;
            }
            switch (msg.what) {
                case HandleWhat.ITEMVIEW_MSG_REFRESH_IMAGE:
                    AppDebug.i(TAG, TAG + ".mHandler refreshImageEx execute!");
                    view.refreshImageEx(view.mItemData);
                    break;
                case HandleWhat.ITEMVIEW_MSG_CREATEINFO_IMAGE:
                    view.mIsGoodsInfoLoading = false;
                    view.mGoodsInfoBitmap = (Bitmap) msg.obj;
                    if (view.needLockRefreshGoods()) {
                        view.mGoodsInfoShowed = false;
                    } else {
                        view.invalidate();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    /**
     * 商品信息的请求数据
     *
     * @author tim
     */
    public class GoodsNormalItemViewInfoRequestData {

        public CategoryMO mCate; // 商品分类的ID
        public int mWith; // 商品的宽度
        public int mHeight; // 商品的高度
        public ItemMO mItemData; // 商品的信息数据
    }

    @Override
    public int getItemWidth() {
        int width = getWidth();
        if (width <= 0) {
            width = getMeasuredWidth();
            if (width <= 0) {
                if (getId() == R.id.item1 || getId() == R.id.item2) {
                    width = getResources().getDimensionPixelSize(R.dimen.dp_530);
                } else {
                    width = getResources().getDimensionPixelSize(R.dimen.dp_350);
                }
            }
        }

        return width;
    }

    @Override
    public int getItemHeight() {
        int height = getHeight();
        if (height <= 0) {
            height = getMeasuredHeight();
            if (height <= 0) {
                if (getId() == R.id.item1 || getId() == R.id.item2) {
                    height = getResources().getDimensionPixelSize(R.dimen.dp_320);
                } else {
                    height = getResources().getDimensionPixelSize(R.dimen.jhs_category_item_below_height);
                }
            }
        }
        return height;
    }

    @Override
    public Rect getOriginalRect() {
        Rect rect = new Rect();
        rect.left = getLeft();
        rect.right = getRight();
        rect.top = getTop();
        rect.bottom = getBottom();
        return rect;
    }

    @Override
    public Rect getItemScaledRect(float scaledX, float scaledY) {
        Rect focusRect = new Rect(getLeft(), getTop(), getRight(), getBottom());
        int focusW = focusRect.right - focusRect.left;
        int focusH = focusRect.bottom - focusRect.top;
        focusRect.left = (int) ((focusRect.left - (scaledX - 1.0f) * focusW / 2) + 0.5f);
        focusRect.top = (int) ((focusRect.top - (scaledY - 1.0f) * focusH / 2) + 0.5f);
        focusRect.right = (int) (focusRect.left + focusW * scaledX + 0.5f);
        focusRect.bottom = (int) (focusRect.top + focusH * scaledY + 0.5f);

        return focusRect;
    }

    @Override
    public boolean getIfScale() {
        return true;
    }

    @Override
    public FocusedBasePositionManager.FrameInterpolator getFrameScaleInterpolator() {
        return new FocusedBasePositionManager.AccelerateFrameInterpolator();
    }

    @Override
    public FocusedBasePositionManager.FrameInterpolator getFrameFocusInterpolator() {
        return new FocusedBasePositionManager.AccelerateFrameInterpolator(0.5f);
    }

    @Override
    public Rect getFocusPadding(Rect originalRect, Rect lastOriginalRect, int direction, int focusFrameRate) {
        return null;
    }
}
