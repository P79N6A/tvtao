package com.yunos.tvtaobao.detailbundle.adapter;


import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.yunos.tv.app.widget.ListView;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tvtaobao.biz.request.bo.AppendedFeed;
import com.yunos.tvtaobao.biz.request.bo.ItemRates;
import com.yunos.tvtaobao.detailbundle.R;
import com.yunos.tvtaobao.detailbundle.view.DetailEvaluatecItemView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class DetailEvaluateAdapter extends BaseAdapter {

    private String TAG = "DetailEvaluateAdapter";
    private Context mContext;
    private ArrayList<ItemRates> mItemRates;
    private LayoutInflater mInflater;
    // 图片下载管理器
    private ImageLoaderManager mImageLoaderManager;

    private DisplayImageOptions mImageOptions;

    // 图片的宽和高
    private int mBitmap_Width;
    private int mBitmap_Height;
    private int mImageView_padding;

    // 暂无评价的提示
    private String mNoRateString;

    // 图片类型
    private boolean mPicType;

    public DetailEvaluateAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(context);
        mImageOptions = new DisplayImageOptions.Builder().cacheOnDisc(false).cacheInMemory(true).build();

        mBitmap_Width = mContext.getResources().getDimensionPixelSize(R.dimen.dp_88);
        mBitmap_Height = mContext.getResources().getDimensionPixelSize(R.dimen.dp_88);
        mImageView_padding = mContext.getResources().getDimensionPixelSize(R.dimen.dp_5);

        mNoRateString = mContext.getResources().getString(R.string.ytsdk_no_rates);
    }

    @Override
    public int getCount() {
        int count = getRatesListSize();
        AppDebug.i(TAG, "getCount --> count = " + count);
        if (count == 0) {
            // 如果没有数据，那么最后一条为提示语
            count++;
        }
        return count;
    }

    @Override
    public ItemRates getItem(int position) {
        if (mItemRates == null || position < 0 || position >= mItemRates.size()) {
            return null;
        }
        return mItemRates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.ytm_detail_evaluate_context_item, null);
        }
        AppDebug.i(TAG, "getView --> convertView = " + convertView + "; position = " + position);

        ItemHolder itemHolder = ItemHolder.get(convertView);

        ItemRates itemRates = getItem(position);
        if (itemRates != null) {

            if (mPicType) {
                // 如果是有图的评价类型， 原来没有图片地址，那么就用追加评价的内容和图片地址替换
                ArrayList<String> piclist = itemRates.getPicUrlList();
                if (piclist == null || piclist.isEmpty()) {
                    AppendedFeed appendedFeed = itemRates.getAppendedFeed();
                    if (appendedFeed != null) {
                        ArrayList<String> picPathList = appendedFeed.getAppendFeedPicPathList();
                        if (picPathList != null && !picPathList.isEmpty()) {
                            itemRates.setPicUrlList(picPathList);
                            itemRates.setFeedback(appendedFeed.getAppendedFeedback());
                        }
                    }
                }
            }

            // 用户名称
            String userNick = itemRates.getUserNick();
            if (!TextUtils.isEmpty(userNick)) {
                itemHolder.evaluate_user.setText(userNick);
                itemHolder.evaluate_user.setVisibility(View.VISIBLE);
            } else {
                itemHolder.evaluate_user.setVisibility(View.GONE);
            }
            // 用户等级图片
            String userStarPic = itemRates.getUserStarPic();
            if (!TextUtils.isEmpty(userStarPic)) {
                mImageLoaderManager.displayImage(userStarPic, itemHolder.evaluate_user_star, mImageOptions);
            }
            // 评价内容
            String feedback = itemRates.getFeedback();
            if (!TextUtils.isEmpty(feedback)) {
                itemHolder.evaluate_describe.setGravity(Gravity.LEFT);
                itemHolder.evaluate_describe.setText(feedback);
                itemHolder.evaluate_describe.setVisibility(View.VISIBLE);
            } else {
                itemHolder.evaluate_describe.setVisibility(View.GONE);
            }

            // 评价日期
            String feedbackDate = itemRates.getFeedbackDate();
            if (!TextUtils.isEmpty(feedbackDate)) {
                itemHolder.evaluate_date.setText(feedbackDate);
                itemHolder.evaluate_date.setVisibility(View.VISIBLE);
            } else {
                itemHolder.evaluate_date.setVisibility(View.GONE);
            }

            // 商品SKU
            StringBuilder skuBuilder = new StringBuilder();
            Map<String, String> skumap = itemRates.getSkuMap();
            Iterator<Map.Entry<String, String>> it = skumap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> entry = it.next();
                skuBuilder.append(entry.getKey());
                skuBuilder.append(": ");
                skuBuilder.append(entry.getValue());
                skuBuilder.append("    ");
            }
            String sku = skuBuilder.toString();
            if (!TextUtils.isEmpty(sku)) {
                itemHolder.evaluate_sku.setText(sku);
                itemHolder.evaluate_sku.setVisibility(View.VISIBLE);
            } else {
                itemHolder.evaluate_sku.setVisibility(View.GONE);
            }

            // 评价的图片
            ArrayList<String> piclist = itemRates.getPicUrlList();
            LinearLayout picLayout = itemHolder.evaluate_pic_groupview;
            if (piclist != null && !piclist.isEmpty()) {
                itemHolder.evaluate_pic_groupview.removeAllViews();
                int size = piclist.size();
                for (int index = 0; index < size; index++) {
                    String url = piclist.get(index);
                    if (!TextUtils.isEmpty(url)) {
                        ImageView imageView = new ImageView(mContext);
                        imageView.setPadding(mImageView_padding, mImageView_padding, mImageView_padding,
                                mImageView_padding);
                        imageView.setScaleType(ScaleType.FIT_XY);
                        LayoutParams layoutParams = new LayoutParams(mBitmap_Width, mBitmap_Height);
                        picLayout.addView(imageView, layoutParams);
                        mImageLoaderManager.displayImage(url, imageView, mImageOptions);
                    }
                }
                picLayout.setVisibility(View.VISIBLE);
            } else {
                picLayout.setVisibility(View.GONE);
            }
        } else {

            //  隐藏用户名称
            itemHolder.evaluate_user.setVisibility(View.GONE);

            //  评价内容
            String feedback = mNoRateString;
            if (!TextUtils.isEmpty(feedback)) {
                itemHolder.evaluate_describe.setGravity(Gravity.CENTER_HORIZONTAL);
                itemHolder.evaluate_describe.setText(feedback);
                itemHolder.evaluate_describe.setVisibility(View.VISIBLE);
            } else {
                itemHolder.evaluate_describe.setVisibility(View.GONE);
            }

            //  隐藏评价日期

            itemHolder.evaluate_date.setVisibility(View.GONE);

            //  隐藏商品SKU

            itemHolder.evaluate_sku.setVisibility(View.GONE);

            //  隐藏评价的图片

            LinearLayout picLayout = itemHolder.evaluate_pic_groupview;

            picLayout.setVisibility(View.GONE);

        }

        if (convertView != null) {

            if (convertView instanceof DetailEvaluatecItemView) {
                DetailEvaluatecItemView detailEvaluatecItemView = (DetailEvaluatecItemView) convertView;
                int dividerhight = mContext.getResources().getDimensionPixelSize(R.dimen.dp_2);
                detailEvaluatecItemView.setDividerLeftRightSpace(0);
                detailEvaluatecItemView.setDividerDrawable(
                        mContext.getResources().getColor(R.color.ytsdk_detail_context_divider_color), dividerhight);
            }

            convertView
                    .setLayoutParams(new ListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        }

        return convertView;
    }

    private int getRatesListSize() {
        int size = 0;
        if (mItemRates != null) {
            size = mItemRates.size();
        }
        return size;
    }

    /**
     * 设置数据
     * @param itemRates
     */
    public void setItemRatesList(ArrayList<ItemRates> itemRates) {
        mItemRates = itemRates;
    }

    /**
     * 设置是否有图的TAB
     * @param have
     */
    public void setPicType(boolean have) {
        mPicType = have;
    }

    private static final class ItemHolder {

        // 用户名
        public final TextView evaluate_user;
        // 用户start
        public final ImageView evaluate_user_star;
        // 评价日期
        public final TextView evaluate_date;
        // 评价描述
        public final TextView evaluate_describe;
        // 商品SKU
        public final TextView evaluate_sku;
        // 评价的图片
        public final LinearLayout evaluate_pic_groupview;

        private ItemHolder(View v) {
            evaluate_user = (TextView) v.findViewById(R.id.evaluate_context_user);
            evaluate_user_star = (ImageView) v.findViewById(R.id.evaluate_context_user_star);
            evaluate_date = (TextView) v.findViewById(R.id.evaluate_context_date);
            evaluate_describe = (TextView) v.findViewById(R.id.evaluate_context_describe);
            evaluate_sku = (TextView) v.findViewById(R.id.evaluate_context_sku);
            evaluate_pic_groupview = (LinearLayout) v.findViewById(R.id.evaluate_context_pic);
            v.setTag(this);
        }

        public static ItemHolder get(View v) {
            if (v.getTag() instanceof ItemHolder) {
                return (ItemHolder) v.getTag();
            }
            return new ItemHolder(v);
        }
    }
}
