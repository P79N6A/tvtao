package com.yunos.tvtaobao.mytaobao.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.assist.FailReason;
import com.tvlife.imageloader.core.assist.ImageScaleType;
import com.tvlife.imageloader.core.listener.ImageLoadingListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tvtaobao.biz.adapter.LifeUiBaseAdapter;
import com.yunos.tvtaobao.biz.request.bo.Collect;
import com.yunos.tvtaobao.mytaobao.R;
import com.yunos.tvtaobao.mytaobao.view.ItemLayoutForCollect;

import java.util.ArrayList;

public class CollectsAdapter extends LifeUiBaseAdapter {

    private String TAG = "CollectsAdapter";

    private ArrayList<Collect> mCollects;
    Context mContext;
    DisplayImageOptions options = null;

    private SparseArray<ViewHolder> mCurrentViewHolderMap = null;

    public int currentSelectedPosition = -1;
    /** 是否暂停 加载图片 */
    private boolean mPauseWork;

    public CollectsAdapter(Context context, ArrayList<Collect> collects) {
        super(context);
        this.mCollects = collects;
        this.mContext = context;

        options = new DisplayImageOptions.Builder().cacheInMemory(false)
                .cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();

        mCurrentViewHolderMap = new SparseArray<ViewHolder>();
        mCurrentViewHolderMap.clear();

    }

    @Override
    public int getCount() {
        if (mCollects != null) {
            return mCollects.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mCollects != null && position < mCollects.size()) {
            return mCollects.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void fillView(int position, View convertView, boolean newConvertView, ViewGroup parent) {

        AppDebug.i(TAG, "fillView --> position = " + position + ";  convertView = " + convertView
                + "; newConvertView  = " + newConvertView);

        ItemLayoutForCollect itemLayoutForCollect = (ItemLayoutForCollect) convertView;

        if (itemLayoutForCollect == null)
            return;

        if (mCollects == null) {
            return;
        }

        if (mCollects.isEmpty()) {
            return;
        }

        // 设置当前条目的位置
        itemLayoutForCollect.onSetPosition(position);

        Collect c = mCollects.get(position);
        if (c == null) {
            return;
        }

        if (mPauseWork) {
            itemLayoutForCollect.getItemImageView().setImageResource(R.drawable.mytaobao_comment_view_image_default);
        } else {
            // ImageLoaderManager.getImageLoaderManager(mContext).displayImage(c.getImgUrl(),
            // itemLayoutForCollect.getItemImageView(), options);
            ImageLoaderManager.getImageLoaderManager(mContext).displayImage(c.getImg(),
                    itemLayoutForCollect.getItemImageView(), options, new ImageLoadingListener() {

                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            if (view != null) {
                                // 判断一下view是否为null，因为可能存在view被回收的情况
                                if (view instanceof ImageView) {
                                    ImageView temp = (ImageView) view;
                                    temp.setImageResource(R.drawable.mytaobao_comment_view_image_default);
                                }
                            }
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String arg0, View view, FailReason arg2) {
                            if (view != null) {
                                // 判断一下view是否为null，因为可能存在view被回收的情况
                                if (view instanceof ImageView) {
                                    ImageView temp = (ImageView) view;
                                    temp.setImageResource(R.drawable.mytaobao_comment_view_image_default);
                                }
                            }
                        }
                    });
        }

        // 设置标题
        itemLayoutForCollect.getItemSummaryView().setText(c.getTitle());
        // 价格
        String price = c.getPromotionPrice();
        if (!TextUtils.isEmpty(price)) {
            String spaceStr = " ";// 增加空格是为了与其他字符对齐，因为直接读取人民币符号编码是英文字符。
            String priceString = String.format(spaceStr + mContext.getString(R.string.ytsdk_collects_price), price);
            itemLayoutForCollect.getItemPriceView().setText(priceString);
        }

        // 已被收藏次数
        String collectorCount = c.getCollectCount();
        if (!TextUtils.isEmpty(collectorCount)) {
            String collectNumString = String.format(mContext.getString(R.string.ytsdk_collects_collect_num),
                    c.getCollectCount());
            itemLayoutForCollect.getItemCollectNumView().setText(collectNumString);
        }
    }

    public synchronized void setPauseWork(boolean isPause) {
        this.mPauseWork = isPause;
    }

    @Override
    public int getLayoutID() {
        return R.layout.ytsdk_collect_item;
    }

    // 释放资源
    public void onDestroy() {

        if (mCurrentViewHolderMap != null) {
            int count = mCurrentViewHolderMap.size();

            for (int index = 0; index < count; index++) {

                int key = mCurrentViewHolderMap.keyAt(index);

                ViewHolder viewHolder = mCurrentViewHolderMap.get(key, null);

                if (viewHolder != null) {
                    // viewHolder.itemImg.setBackgroundDrawable(null);
                    // viewHolder.itemImg.setImageBitmap(mRecycledBitmap);

                    if (viewHolder.itemImg != null) {
                        viewHolder.itemImg.setBackgroundDrawable(null);
                    }

                    // 取消 ImageLoader 的显示
                    ImageLoaderManager.getImageLoaderManager(mContext).cancelDisplayTask(viewHolder.itemImg);

                    // mCurrentViewHolderMap.remove(key);
                    // mCurrentViewHolderMap.remove(key);
                }
                viewHolder = null;

            }
            mCurrentViewHolderMap.clear();

        }

        mCollects = null;
        options = null;
        mCurrentViewHolderMap = null;
        mContext = null;

        System.gc();
    }

    public class ViewHolder {

        /**
         * 收藏宝贝的图片
         */
        public ImageView itemImg;
        /**
         * 收藏宝贝的简单描述(标题)
         */
        public TextView itemSummary;
        /**
         * 宝贝价格
         */
        public TextView price;
        /**
         * 已收藏数量
         */
        public TextView collectNum;
        public ViewGroup contentArea;
    }

}
