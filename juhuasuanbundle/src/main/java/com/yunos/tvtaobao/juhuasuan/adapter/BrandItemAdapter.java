package com.yunos.tvtaobao.juhuasuan.adapter;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.assist.FailReason;
import com.tvlife.imageloader.core.assist.ImageScaleType;
import com.tvlife.imageloader.core.listener.ImageLoadingListener;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppHandler;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CountList;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;
import com.yunos.tvtaobao.juhuasuan.config.SystemConfig;
import com.yunos.tvtaobao.juhuasuan.util.ImageUtil;
import com.yunos.tvtaobao.juhuasuan.util.JuApiUtils;
import com.yunos.tvtaobao.juhuasuan.util.SystemUtil;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tvtaobao.biz.graphics.TvCanvas;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author wb-daishulin
 */
public class BrandItemAdapter extends BaseAdapter {

    private static final String TAG = "BrandItemAdapter";
    private CountList<ItemMO> mItemList;
    //    private LinkedList<ViewHolderRunnable> mRunnableList;
    private Activity mContext;
    private Map<Integer, Bitmap> bitmapData;
    public boolean isScroing = false;
    public boolean actionMoveLeft = false;
    public boolean actionMoveRight = false;
    private Handler adapterHandler;
    private int IMAGEWIDTH_HEIGHT = 640;
    private final int MAXBITMAPCACHESIZE = 4;
    private ImageLoaderManager mImageLoaderManager;
    private DisplayImageOptions imageOptions;
    private boolean isSrolling = false;

    public BrandItemAdapter(CountList<ItemMO> list, Activity context) {
        onDestroy();
        //        mRunnableList = new LinkedList<ViewHolderRunnable>();
        mItemList = list;
        mContext = context;
        bitmapData = new HashMap<Integer, Bitmap>();
        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(mContext);// 图片加载

        imageOptions = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true)
                .bitmapConfig(Config.RGB_565).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .showImageOnLoading(R.drawable.jhs_brand_item_default_image)
                .showImageForEmptyUri(R.drawable.jhs_brand_item_default_image)
                .showImageOnFail(R.drawable.jhs_brand_item_default_image).build();

        adapterHandler = new MyHandler(this);

    }

    @Override
    public int getCount() {
        if (mItemList != null && mItemList.size() > 3) {
            return Integer.MAX_VALUE;
        } else if (mItemList != null && mItemList.size() > 0) {
            return mItemList.size();
        } else
            return 0;
    }

    @Override
    public ItemMO getItem(int position) {
        if (position < 0) {
            return null;
        }
        return mItemList.get(position % mItemList.size());
    }

    @Override
    public long getItemId(int position) {
        if (mItemList != null && mItemList.size() > 0)
            return position % mItemList.size();
        else
            return 0;
    }

    /**
     * 更新商品列表
     * @param position
     * @param item
     */
    public void updateItemOfList(int position, ItemMO item) {
        if (mItemList == null || position < 0 || position >= mItemList.size() || item == null) {
            return;
        }

        mItemList.remove(position);
        mItemList.add(position, item);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // AppDebug.i(TAG, "getDropDownView position" + position);
        return super.getDropDownView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // AppDebug.i(TAG, "BrandItemAdapter getView()= " + position
        // + " convertView=" + convertView + "  isScroing==" + isScroing);
        ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.jhs_home_category_item_image, null); // mContext指的是调用的Activtty
            holder.discounter = (TextView) convertView.findViewById(R.id.discounter);
            holder.old_price = (TextView) convertView.findViewById(R.id.old_price);
            holder.num_sold = (TextView) convertView.findViewById(R.id.num_sold);
            holder.money_head = (TextView) convertView.findViewById(R.id.money_head);
            holder.money = (TextView) convertView.findViewById(R.id.money);
            holder.point = (TextView) convertView.findViewById(R.id.money_sub);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.item_image = (ImageView) convertView.findViewById(R.id.item_image);
            holder.detail_layout = (RelativeLayout) convertView.findViewById(R.id.detail_layout);
            holder.discount_layout = (RelativeLayout) convertView.findViewById(R.id.discount_layout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.position = position;

        if (null == mItemList) {
            return convertView;
        }

        ItemMO juItemSummary = mItemList.get(position % mItemList.size());
        if (String.valueOf(juItemSummary.getDiscount()).equals("10.0")){
            holder.discount_layout.setVisibility(View.INVISIBLE);

        }else{
            holder.discount_layout.setVisibility(View.VISIBLE);
            holder.discounter.setText(""+ String.valueOf(juItemSummary.getDiscount()));
        }
        //holder.discounter.setText("" + String.valueOf(juItemSummary.getDiscount()));

        holder.message.setText("" + String.valueOf(juItemSummary.getLongName()));
        holder.num_sold.setText("" + String.valueOf(juItemSummary.getSoldCount()));
        
        String longMoneyString = String.valueOf(juItemSummary.getActivityPrice());
        
        AppDebug.i(TAG, "getView --> longMoneyString = " + longMoneyString + "; juItemSummary = " + juItemSummary);
        
        if (!TextUtils.isEmpty(longMoneyString)) {
              int len = longMoneyString.length();
              if (len > 2) {
                  holder.money.setText("" + String.valueOf(longMoneyString.substring(0, longMoneyString.length() - 2)));
                  holder.point.setText("." + longMoneyString.substring(longMoneyString.length() - 2, longMoneyString.length()));
              } else if (len == 2) {
                  holder.money.setText("0"); 
                  holder.point.setText("." + longMoneyString.substring(longMoneyString.length() - 2, longMoneyString.length()));
              } else if (len == 1) {
                  holder.money.setText("0"); 
                  holder.point.setText(".0" + longMoneyString.substring(longMoneyString.length() - 1, longMoneyString.length()));
              } 
        }
        
        holder.money.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//
        // 加粗

        // 判断状态
        // AppDebug.i(TAG, "juItemSummary.itemStatus 1= "
        // + juItemSummary.itemStatus);
        if (juItemSummary.getItemStatus() != null && JuApiUtils.isNotStart(juItemSummary.getItemStatus())) {// 即将开始
            // AppDebug.i(TAG, "juItemSummary.itemStatus true= "
            // + juItemSummary.itemStatus);
            holder.money_head.setTextColor(Color.parseColor("#118c67"));
            holder.money.setTextColor(Color.parseColor("#118c67"));
            holder.point.setTextColor(Color.parseColor("#118c67"));
            holder.detail_layout.setBackgroundDrawable(mContext.getResources().getDrawable(
                    R.drawable.jhs_brand_detail_botton_bg_pre));
        } else {
            holder.money_head.setTextColor(mContext.getResources().getColor(R.color.jhs_brand_detail_red_font_color));
            holder.money.setTextColor(mContext.getResources().getColor(R.color.jhs_brand_detail_red_font_color));
            holder.point.setTextColor(mContext.getResources().getColor(R.color.jhs_brand_detail_red_font_color));
            holder.detail_layout.setBackgroundResource(R.drawable.jhs_brand_detail_botton_bg);
        }

        DecimalFormat df = new DecimalFormat("###.00");
        holder.old_price.setText(mContext.getResources().getString(R.string.jhs_dollar_sign)
                + df.format(Double.valueOf(String.valueOf(juItemSummary.getOriginalPrice())) / 100.00) + " ");
        holder.old_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //

        // 中划线
        AppDebug.i(TAG, TAG + ".getView SystemConfig.DIPEI_BOX=" + SystemConfig.DIPEI_BOX);
        holder.logoUrl = SystemUtil.mergeImageUrl(String.valueOf(juItemSummary.getPicUrl()))
                + ImageUtil.getImageUrlExtraBySize(R.dimen.dp_640);
        Bitmap tempBitmap = bitmapData.get(position % mItemList.size());
        if (tempBitmap != null) {
            if (SystemConfig.DIPEI_BOX) {
                holder.item_image.setImageBitmap(tempBitmap);
            } else {
                TransitionDrawable td = new TransitionDrawable(new Drawable[] {
                        new ColorDrawable(android.R.color.transparent),
                        new BitmapDrawable(mContext.getResources(), tempBitmap) });
                td.startTransition(200);
                holder.item_image.setImageDrawable(td);
            }
            holder.isLoadLogo = true;
            //            preLoadBitmap(position, 2);
        } else {
            //            initData(String.valueOf(itemLogoUrl), holder.item_image, position % mItemList.size());
            //            instanceView(holder, juItemSummary, position);
            if (!isSrolling) {
                loadImage(convertView);
            }
            //            preLoadBitmap(position, 2);
        }
        //         }
        return convertView;

    }

    public void loadImage(View view) {
        if (null == view) {
            return;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        if (null == holder || holder.isLoadLogo) {
            return;
        }
        adapterHandler.sendMessageDelayed(adapterHandler.obtainMessage(2, holder), 200);
    }

    public void stopLoadImage() {
        isSrolling = true;
        if (null != adapterHandler) {
            return;
        }
        adapterHandler.removeCallbacksAndMessages(null);
    }

    public void stopScorll() {
        isSrolling = false;
    }

    //    public void instanceView(ViewHolder holder, ItemMO item, int holderPosition) {
    //        for (ViewHolderRunnable runnable : mRunnableList) {
    //            if (runnable.holderPosition == holderPosition) {
    //                return;
    //            }
    //        }
    //        if (mRunnableList.size() > 3) {
    //            ViewHolderRunnable runnable = mRunnableList.remove();
    //            AppDebug.i(TAG, TAG + ".instanceView runnable=" + runnable.holderPosition + ", holderPosition="
    //                    + holderPosition);
    //            adapterHandler.removeCallbacks(runnable);
    //        }
    //        ViewHolderRunnable mDismissOnScreenControlRunner = new ViewHolderRunnable();
    //        mDismissOnScreenControlRunner.mHolder = holder;
    //        mDismissOnScreenControlRunner.mItem = item;
    //        mDismissOnScreenControlRunner.holderPosition = holderPosition;
    //        mRunnableList.addLast(mDismissOnScreenControlRunner);
    //        adapterHandler.postDelayed(mDismissOnScreenControlRunner, 300);// 延迟
    //
    //    }

    //    // 预加载
    //    public void preLoadBitmap(int positon, int preSize) {
    //        int currentIndexPos = 0;
    //        if (actionMoveLeft) {
    //            currentIndexPos = (positon + mItemList.size() - preSize) % mItemList.size();
    //        } else if (actionMoveRight) {
    //            currentIndexPos = (positon + preSize) % mItemList.size();
    //        } else {
    //            currentIndexPos = (positon + preSize) % mItemList.size();
    //            proloadSubmit(currentIndexPos);
    //            currentIndexPos = (positon + mItemList.size() - preSize) % mItemList.size();
    //            proloadSubmit(currentIndexPos);
    //            return;
    //        }
    //        //        proloadSubmit(currentIndexPos);
    //        // AppDebug.i(TAG, "preLoadBitmap  currentIndexPos= " + currentIndexPos
    //        // + "  yuan positon=" + positon % mItemList.size());
    //
    //    }

    //    private void proloadSubmit(int currentIndexPos) {
    //        Bitmap bitmap = bitmapData.get(currentIndexPos);
    //        if (bitmap != null) {
    //            return;
    //        }
    //        ItemMO tempItemSummary = mItemList.get(currentIndexPos);
    //        if (null != tempItemSummary) {
    //            String itemLogoUrl = SystemUtil.mergeImageUrl(String.valueOf(tempItemSummary.getImgUrl()))
    //                    + ImageUtil.getImageUrlExtraBySize(R.dimen.dp_640);
    //            //            mJuImageLoader.displayImage(itemLogoUrl, imageOptions, holder.item_image, new JuImageLoadingListener(currentIndexPos));
    //            mJuImageLoader.loadImage(itemLogoUrl, imageOptions, new JuImageLoadingListener(currentIndexPos));
    //        }
    //    }

    private static class MyHandler extends AppHandler<BrandItemAdapter> {

        public MyHandler(BrandItemAdapter t) {
            super(t);
        }

        public void handleMessage(android.os.Message msg) {
            BrandItemAdapter adapter = getT();
            if (null == adapter) {
                return;
            }
            switch (msg.what) {
                case 1:
                    String message = (String) msg.obj;
                    if (message != null) {
                        CoreApplication.toast(message);

                    }
                    break;
                case 2:
                    ViewHolder holder = (ViewHolder) msg.obj;
                    AppDebug.i(TAG, TAG + ".MyHandler holder=" + holder);
                    if (null != holder) {
                        adapter.mImageLoaderManager.displayImage(holder.logoUrl, holder.item_image, adapter.imageOptions,
                                adapter.new JuImageLoadingListener(holder));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    //    public class ViewHolderRunnable implements Runnable {
    //
    //        public ViewHolder mHolder;
    //        public ItemMO mItem;
    //        public int holderPosition;
    //
    //        @Override
    //        public void run() {
    //
    //            String itemLogoUrl = SystemUtil.mergeImageUrl(String.valueOf(mItem.getImgUrl()))
    //                    + ImageUtil.getImageUrlExtraBySize(R.dimen.dp_640);
    //            initData(String.valueOf(itemLogoUrl), mHolder.item_image, holderPosition % mItemList.size());
    //            if (null != mRunnableList) {
    //                mRunnableList.remove(this);
    //            }
    //            AppDebug.i(TAG,
    //                    TAG + ".ViewHolderRunnable.run holderPosition=" + holderPosition + ", mItem=" + mItem.getItemId());
    //            //            preLoadBitmap(holderPosition, 3);
    //        }
    //
    //    }

    public class ViewHolder {
        RelativeLayout discount_layout;
        TextView discounter; // 折扣
        TextView old_price; // 原价
        TextView num_sold; // 已卖数量
        TextView money_head; // ￥
        TextView money; // 价格
        TextView point; // 价格小数位
        ImageView item_image; // logo
        TextView message; // 详情
        RelativeLayout detail_layout;
        String logoUrl; // LOGO地址
        int position;
        boolean isLoadLogo = false;
    }

    //    public void initData(final String url, final ImageView imageView, final int position) {
    //        mJuImageLoader.displayImage(url, imageOptions, imageView, new JuImageLoadingListener(position));
    //    }

    public class JuImageLoadingListener implements ImageLoadingListener {

        private int listenerPosition = -1;
        private ViewHolder mHolder;

        private JuImageLoadingListener(ViewHolder holder) {
            mHolder = holder;
            listenerPosition = mHolder.position;
        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
            // AppHolder.toast(mContext
            // .getString(R.string.brand_detail_load_image_fail));
        }

        @Override
        public void onLoadingComplete(String imageUri, final View view, Bitmap loadedImage) {
            final Bitmap bitmap = juToRoundCorner(loadedImage, 12, false);
            if (bitmapData == null) {
                return;
            }
            if (bitmapData.size() > MAXBITMAPCACHESIZE) {
                cleanExtraBitmapData(listenerPosition);
            }
            if (null != view) {
                if (SystemConfig.DIPEI_BOX) {
                    ((ImageView) view).setImageBitmap(bitmap);
                } else {
                    TransitionDrawable td = new TransitionDrawable(new Drawable[] {
                            new ColorDrawable(android.R.color.transparent),
                            new BitmapDrawable(mContext.getResources(), bitmap) });
                    td.startTransition(200);
                    ((ImageView) view).setImageDrawable(td);
                }
                mHolder.isLoadLogo = true;
            }
        }

        private void cleanExtraBitmapData(int positon) {
            Set<Integer> keys = bitmapData.keySet();
            for (Iterator<Integer> iterator = keys.iterator(); iterator.hasNext();) {
                Integer keyPositon = (Integer) iterator.next();
                if (!(keyPositon == positon || Math.abs(keyPositon - positon) < MAXBITMAPCACHESIZE / 4)) {
                    // AppDebug.i(TAG, " keyPositon =" + keyPositon
                    // + "     positon=" + positon);
                    iterator.remove();
                    bitmapData.remove(keyPositon);
                }

            }
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {
            // AppDebug.i(TAG, "refreshImageEx try again");
            // 如果被取消重新再加载
        }

        @Override
        public void onLoadingStarted(String arg0, View arg1) {

        }
    }

    public Bitmap juToRoundCorner(Bitmap bitmap, int pixels, boolean recycle) {
        if (bitmap == null)
            return null;
        // AppDebug.i(TAG, "juToRoundCorner");
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        TvCanvas canvas = new TvCanvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        //        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCornerRect(rectF, roundPx, paint, true, true);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_ATOP));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public void onDestroy() {
        if (null != adapterHandler) {
            adapterHandler.removeCallbacksAndMessages(null);
        }
        // 缓冲回收
        if (null != bitmapData) {
            Iterator<Bitmap> dataIterable = bitmapData.values().iterator();
            while (dataIterable.hasNext()) {
                Bitmap temp = dataIterable.next();
                if (!temp.isRecycled()) {
                    temp.recycle();
                    temp = null;
                }
            }
            bitmapData.clear();
            bitmapData = null;
        }
        //        if (mRunnableList != null) {
        //            mRunnableList.clear();
        //            mRunnableList = null;
        //
        //        }
        if (mItemList != null) {
            mItemList.clear();
            mItemList = null;
        }

        System.gc();
    }
}
