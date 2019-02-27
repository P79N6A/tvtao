package com.yunos.tvtaobao.homebundle.detainment;


import android.content.Context;
import android.graphics.Bitmap;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvlife.imageloader.core.assist.FailReason;
import com.tvlife.imageloader.core.listener.ImageLoadingListener;
import com.yunos.tv.app.widget.focus.FocusHListView;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tvtaobao.biz.request.bo.DetainMentBo;
import com.yunos.tvtaobao.homebundle.R;

import java.util.ArrayList;

public class DetainMentAdapter extends BaseAdapter {

    private final String TAG = "DetainMentAdapter";

    public static int PING_COUNT = 5;
    public static int TOTAL_COUNT = 20;

    private Context mContext;
    private LayoutInflater mInflater;
    private ImageLoaderManager mImageLoaderManager;
    private DetainMentBo[] mItems;

    private String mPriceUnit;
    private int mPriceDocSize;

    private boolean mIs1080P;
    private ArrayList<Integer> mList;

    public DetainMentAdapter(Context context) {

        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(mContext);

        mPriceUnit = mContext.getResources().getString(R.string.ytbv_price_unit_text);
        mPriceDocSize = mContext.getResources().getDimensionPixelSize(R.dimen.dp_20);

        mIs1080P = is1080p(context);

        AppDebug.i(TAG, "DetainMentAdapter --> getItem -->  mIs1080P = " + mIs1080P);
    }

    @Override
    public int getCount() {
        int count = 0;
        if (mItems != null) {
            count = mItems.length;
        }

        count = Math.min(count, TOTAL_COUNT);

        return count;
    }

    @Override
    public DetainMentBo getItem(int position) {
        if (null == mItems||mItems.length==0) {
            return null;
        }

        synchronized (mItems) {
            if (position >= 0 && position < getCount()) {
                if (mList!=null&&mList.size()>0&&position<mList.size()){
                    return mItems[mList.get(position)];
                }else {
                    return  mItems[position];
                }

            }
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AppDebug.i(TAG, "getView  -->    position = " + position + ";  convertView = " + convertView);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.ytm_detainment_item, null);
        }

        DetainMentBo detainMentBo = getItem(position);
        if (detainMentBo != null) {

            TextView titleView = (TextView) convertView.findViewById(R.id.detainment_item_title);
            if (titleView != null) {
                String title = detainMentBo.getTitle();
                if (!TextUtils.isEmpty(title)) {
                    titleView.setText(title);
                    titleView.setVisibility(View.VISIBLE);
                    titleView.setLines(1);
                } else {
                    titleView.setVisibility(View.GONE);
                }
            }

            TextView priceView = (TextView) convertView.findViewById(R.id.detainment_item_price);
            if (priceView != null) {
                String price = detainMentBo.getPrice();
                if (!TextUtils.isEmpty(price)) {
                    priceView.setText(price.replace(",",""));
                    //priceView.setText(mPriceUnit);
                    /*int doc_index = price.indexOf(".", 0);
                    if (doc_index > 0) {
                        String doc_sub = price.substring(doc_index + 1);
                        if (isEqualZero(doc_sub)) {
                            price = price.substring(0, doc_index);
                            if (!TextUtils.isEmpty(price)) {
                                priceView.append(price);
                            }
                        } else {
                            Spannable wordtoSpan = new SpannableString(price);
                            wordtoSpan.setSpan(new AbsoluteSizeSpan(mPriceDocSize), doc_index + 1, price.length(),
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            priceView.append(wordtoSpan);
                        }
                    } else {
                        priceView.append(price);
                    }*/

                    priceView.setVisibility(View.VISIBLE);
                } else {
                    priceView.setVisibility(View.GONE);
                }
            }

            final ImageView picImageView = (ImageView) convertView.findViewById(R.id.detainment_item_img);
            if (picImageView != null) {
                String picurl = detainMentBo.getPictUrl();
                picurl = getPicUrl(picurl);
                if (!TextUtils.isEmpty(picurl) && mImageLoaderManager != null) {
                    mImageLoaderManager.loadImage(picurl, new ImageLoadingListener() {

                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            picImageView.setImageResource(R.drawable.ytm_detainment_default);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            picImageView.setImageResource(R.drawable.ytm_detainment_default);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            if (loadedImage != null && !loadedImage.isRecycled()) {
                                picImageView.setImageBitmap(loadedImage);
                            } else {
                                picImageView.setImageResource(R.drawable.ytm_detainment_default);
                            }
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {
                            picImageView.setImageResource(R.drawable.ytm_detainment_default);
                        }
                    });
                }
            }
        }

        // 占满父view
        convertView.setLayoutParams(new FocusHListView.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        return convertView;
    }

    private boolean isEqualZero(String src) {
        boolean equal = false;
        if (!TextUtils.isEmpty(src)) {
            if (TextUtils.equals(src, "00") || TextUtils.equals(src, "0")) {
                equal = true;
            }
        } else {
            // 如果src是空的，那么默认为相等
            equal = true;
        }
        AppDebug.i(TAG, "isEqualZero --> src = " + src + "; equal = " + equal);
        return equal;
    }

    private String getPicUrl(String pic_src) {
        if (TextUtils.isEmpty(pic_src)) {
            return pic_src;
        }
        String pic_des = pic_src;
        if (mIs1080P) {
            pic_des = pic_src + "_430x430.jpg";
        } else {
            pic_des = pic_src + "_250x250.jpg";
        }
        return pic_des;
    }

    private boolean is1080p(Context ctx) {
        boolean is1080p = false;
        if (DeviceUtil.getScreenScaleFromDevice(ctx) > 1.2f) {
            is1080p = true;
        }
        return is1080p;
    }

    public void setData(DetainMentBo[] items) {
        AppDebug.e(TAG, "setData.items = " + items.length);
        if (items != null && items.length > 0) {
            mItems = new DetainMentBo[items.length];
            System.arraycopy(items, 0, mItems, 0, items.length);
        } else {
            mItems = items;
        }
        mList=new ArrayList<>();
        int size = items.length >20 ? 20 :items.length;
        for (int i = 0 ;i < size ; i++){
            mList.add(i);
        }
        /* Random random2 = new Random();
        mList=new ArrayList<>();
        boolean isContain=true;
        int i=0;
        int count=0;
        if (mItems != null) {
            count = Math.max(mItems.length, TOTAL_COUNT);
        }
        Set set=new HashSet();
        while (isContain){
            int result=random2.nextInt(count/2);
            if(result>=mItems.length){
                continue;
            }
            if (set.add(result)){
                i++;
                mList.add(result);
            }
            if (i==15){
                isContain=false;
            }else {
                isContain=true;
            }
        }
        boolean isNext=true;
        Random random=new Random();
        Set  set2=new HashSet();
        int j=0;
        while (isNext){
            int result=random.nextInt(count/2)+100;
            if(result>=mItems.length){
                continue;
            }
            if (set2.add(result)){
                j++;
                mList.add(result);
            }
            if (j==5){
                isNext=false;
            }else {
                isNext=true;
            }
        }

        AppDebug.e(TAG," 随机后数据 = "+mList.toString());
        AppDebug.i(TAG, "DetainMentAdapter --> setData -->  mItems = " + mItems.length);*/
    }

    /**
     * 销毁数据
     */
    public void onDestroy() {
        mItems = null;
    }

}
