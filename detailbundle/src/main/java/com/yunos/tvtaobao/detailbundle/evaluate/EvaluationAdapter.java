package com.yunos.tvtaobao.detailbundle.evaluate;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tvtaobao.biz.request.bo.ItemRates;
import com.yunos.tvtaobao.detailbundle.R;

import java.util.ArrayList;

/**
 * Created by xutingting on 2017/9/14.
 */

public class EvaluationAdapter extends BaseAdapter {
    private String TAG = "EvaluationAdapter";
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

    public EvaluationAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(context);
        mImageOptions = new DisplayImageOptions.Builder().cacheOnDisc(false).cacheInMemory(true).build();

        mBitmap_Width = mContext.getResources().getDimensionPixelSize(R.dimen.dp_88);
        mBitmap_Height = mContext.getResources().getDimensionPixelSize(R.dimen.dp_88);
        mImageView_padding = mContext.getResources().getDimensionPixelSize(R.dimen.dp_5);
    }

    public void setmItemRates(ArrayList<ItemRates> mItemRates){
        this.mItemRates = mItemRates;
        this.notifyDataSetChanged();

    }



    @Override
    public int getCount() {
//        if(mItemRates!=null){
//            return mItemRates.size();
//        }else {
//            return 0;
//        }
        return 20;
    }

    @Override
    public Object getItem(int position) {
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
        Log.d(TAG,position+"");

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.new_detail_evaluation_item, null);
            holder.ivHead = (ImageView) convertView.findViewById(R.id.iv_head);
            holder.ivUserStarType = (ImageView) convertView.findViewById(R.id.iv_user_star_type);
            holder.tvUserName = (TextView) convertView.findViewById(R.id.tv_user_name);
            holder.tvEvaluation = (TextView) convertView.findViewById(R.id.tv_evaluation);
            holder.tvGoodsInfo = (TextView) convertView.findViewById(R.id.tv_goods_info);
            holder.new_detail_view_pager= (ViewPager) convertView.findViewById(R.id.new_detail_view_pager);
            convertView.setTag(holder);

            holder.new_detail_view_pager.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    return false;

                }
            });
        }else {
            holder = (ViewHolder) convertView.getTag();


        }

        return convertView;
    }

    static class ViewHolder
    {
        public ImageView ivHead;
        public ImageView ivUserStarType;
        public TextView tvUserName;
        public TextView tvGoodsInfo;
        public TextView tvEvaluation;
        public ViewPager new_detail_view_pager;


    }
}
