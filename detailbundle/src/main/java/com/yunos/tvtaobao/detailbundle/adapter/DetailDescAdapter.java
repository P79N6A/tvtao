package com.yunos.tvtaobao.detailbundle.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taobao.detail.domain.base.Unit;
import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.ImageLoader;
import com.tvlife.imageloader.core.assist.FailReason;
import com.tvlife.imageloader.core.assist.ImageScaleType;
import com.tvlife.imageloader.core.assist.ImageSize;
import com.tvlife.imageloader.core.listener.ImageLoadingListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.DeviceJudge;
import com.yunos.tvtaobao.biz.request.bo.TBDetailResultV6;
import com.yunos.tvtaobao.detailbundle.R;
import com.yunos.tvtaobao.detailbundle.bean.DescImage;

import java.util.List;

/**
 * Created by wuhaoteng on 2018/9/19.
 */

public class DetailDescAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "DetailDescAdapter";
    public static final int TYPE_FOOTER_VIEW = 1;
    private List<DescImage> mDescImageList;
    private TBDetailResultV6 mTbDetailResultV6;
    private Context mContext;

    public DetailDescAdapter(List<DescImage> descImageList, Context context) {
        this.mDescImageList = descImageList;
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if (viewType == TYPE_FOOTER_VIEW) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_new_detail_foot, parent, false);
            holder = new DetailFootViewHolder(view);
        } else {
            ImageView view = new ImageView(mContext);
            view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            view.setAdjustViewBounds(true);
            holder = new DetailImageDescViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DetailImageDescViewHolder) {
            DescImage descImage = mDescImageList.get(position);
            ((DetailImageDescViewHolder) holder).bindView(descImage);
        } else if (holder instanceof DetailFootViewHolder) {
            ((DetailFootViewHolder) holder).bindView();
        }

    }


    @Override
    public int getItemCount() {
        //+1 为 footView
        return mDescImageList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        /*当position是最后一个的时候，也就是比list的数量多一个的时候，则表示FooterView*/
        if (position == mDescImageList.size()) {
            return TYPE_FOOTER_VIEW;
        }
        return super.getItemViewType(position);
    }


    //设置footView的参数
    public void initFootViewOptions(TBDetailResultV6 tbDetailResultV6) {
        mTbDetailResultV6 = tbDetailResultV6;
    }

    public class DetailImageDescViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public DetailImageDescViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }

        public void bindView(DescImage descImage) {
            if (descImage != null) {
                DeviceJudge.MemoryType memoryType = DeviceJudge.getMemoryType();
                String src = descImage.getSrc();
                DisplayImageOptions imageOptions =
                        new DisplayImageOptions.Builder()
                                .bitmapConfig(Bitmap.Config.RGB_565)
                                .imageScaleType(ImageScaleType.EXACTLY)
                                .cacheOnDisc(true)
                                //低内存机子禁用内存缓存
                                .cacheInMemory(memoryType == DeviceJudge.MemoryType.LowMemoryDevice ? false : true)
                                .build();
                ImageLoader.getInstance().displayImage(src, imageView, imageOptions, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        int bitmapSize = getBitmapSize(loadedImage);
                        AppDebug.i(TAG, "bitmap的大小:" + bitmapSize);
                        //imageView.setImageBitmap(loadedImage);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });

            }
        }

    }

    public static int getBitmapSize(Bitmap bitmap) {

        if(bitmap==null){
            return 0;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        // 在低版本中用一行的字节x高度
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }


    public class DetailFootViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mItemView;
        LinearLayout mContainerLayout;

        public DetailFootViewHolder(View itemView) {
            super(itemView);
            mItemView = (LinearLayout) itemView;
            mContainerLayout = (LinearLayout) itemView.findViewById(R.id.layout_container);

        }

        private void bindView() {
            if (mTbDetailResultV6 == null) {
                return;
            }

            // 商品属性规格
            List<Unit> domainUnit = mTbDetailResultV6.getDomainUnit();
            if (domainUnit == null || domainUnit.isEmpty()) {
                return;
            }

            try {
                mContainerLayout.removeAllViews();
                int mSize = domainUnit.size();
                int optionLines = mSize / 2;
                if (mSize % 2 != 0) {
                    //mSize为单数需要多加一行
                    optionLines++;
                }

                for (int i = 1; i <= optionLines; i++) {
                    View optionsView = LayoutInflater.from(mContext).inflate(R.layout.layout_new_detail_foot_options, mContainerLayout, false);
                    TextView leftOptionsTxt = (TextView) optionsView.findViewById(R.id.txt_left_options);
                    TextView rightOptionsTxt = (TextView) optionsView.findViewById(R.id.txt_right_options);
                    int indexLeft = i * 2 - 2;
                    Unit unitLeft = domainUnit.get(indexLeft);
                    String nameLeft = unitLeft.name;
                    String valueLeft = unitLeft.value;
                    leftOptionsTxt.setText(nameLeft + "：" + valueLeft);
                    leftOptionsTxt.setVisibility(View.VISIBLE);

                    int indexRight = i * 2 - 1;
                    if (indexRight < domainUnit.size()) {
                        Unit unitRight = domainUnit.get(indexRight);
                        String nameRight = unitRight.name;
                        String valueRight = unitRight.value;
                        rightOptionsTxt.setText(nameRight + "：" + valueRight);
                        rightOptionsTxt.setVisibility(View.VISIBLE);
                    } else {
                        rightOptionsTxt.setVisibility(View.INVISIBLE);
                    }
                    mContainerLayout.addView(optionsView);
                    if (mItemView.getVisibility() == View.GONE) {
                        mItemView.setVisibility(View.VISIBLE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                mItemView.setVisibility(View.GONE);
            }
        }
    }
}
