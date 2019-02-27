package com.yunos.tvtaobao.takeoutbundle.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.process.BitmapProcessor;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderInfoDetails;
import com.yunos.tvtaobao.biz.request.bo.TakeOutOrderProductInfoBase;
import com.yunos.tvtaobao.biz.util.StringUtil;
import com.yunos.tvtaobao.takeoutbundle.R;

/**
 * Created by zifuma on 14/12/2017.
 */
public class TOOrderDetailAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private TakeOutOrderInfoDetails orderInfoDetails;
    Context mContext;
    private ImageLoaderManager imageLoaderManager; // 图片加载器
    private DisplayImageOptions displayImageOptions; // 图片加载的参数设置

    public TOOrderDetailAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imageLoaderManager = ImageLoaderManager.getImageLoaderManager(context);
        displayImageOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true).preProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        if (bitmap == null || (bitmap.getHeight() < 400 && bitmap.getWidth() < 400))
                            return bitmap;
                        Bitmap bm = Bitmap.createScaledBitmap(bitmap, 400, 400, false);//TODO adjust without changing w/h ratio
                       // bitmap.recycle();
                        return bm;
                    }
                }).build();
    }

    public TakeOutOrderInfoDetails getOrderInfoDetails() {
        return orderInfoDetails;
    }

    public void setOrderInfoDetails(TakeOutOrderInfoDetails orderInfoDetails) {
        this.orderInfoDetails = orderInfoDetails;
    }

    @Override
    public int getCount() {
        if (orderInfoDetails == null || orderInfoDetails.getProductInfoList() == null) {
            return 0;
        }
        return orderInfoDetails.getProductInfoList().size();
    }

    @Override
    public Object getItem(int position) {
        return orderInfoDetails.getProductInfoList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TO_OrderDD_HolderView holderView;
        if (null == convertView) {
            AppDebug.e("thisthisthis", "2222222222" + position);
            holderView = new TO_OrderDD_HolderView();
            convertView = mInflater.inflate(R.layout.item_to_order_detail_product, null);
            holderView.productName = (TextView) convertView.findViewById(R.id.take_out_order_detail_product_name);
            holderView.price = (TextView) convertView.findViewById(R.id.take_out_order_detail_product_price);
            holderView.skuTitle = (TextView) convertView.findViewById(R.id.take_out_order_detail_product_sku);
            holderView.originalPrice = (TextView) convertView.findViewById(R.id.take_out_order_detail_product_original_price);
            holderView.originalPrice.setPaintFlags(
                    holderView.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holderView.count = (TextView) convertView.findViewById(R.id.take_out_order_detail_product_count);
            holderView.productLogo = (ImageView) convertView.findViewById(R.id.take_out_order_detail_product_logo);
//            holderView.productLogo.setCornerRadius(
//                    mContext.getResources().getDimensionPixelSize(R.dimen.to_ol_detail_corner_shape_size),
//                    mContext.getResources().getDimensionPixelSize(R.dimen.to_ol_detail_corner_shape_size), 0, 0);
            convertView.setTag(holderView);
        } else {
            holderView = (TO_OrderDD_HolderView) convertView.getTag();
            AppDebug.e("thisthisthis", "11111111111 " + position);
        }

        if(position>=0) {
            TakeOutOrderProductInfoBase productInfoBase = orderInfoDetails.getProductInfoList().get(position);
            if (productInfoBase != null) {
                // 商品的图片
                holderView.productName.setText(productInfoBase.getProductTitle());
                holderView.price.setText(String.format("￥%.2f", productInfoBase.getDiscountPrice() / 100.f));
                if (productInfoBase.getDiscountPrice() == productInfoBase.getDiscountPrice()) {
                    holderView.originalPrice.setText("");
                } else {
                    holderView.originalPrice.setText(String.format("%.2f", productInfoBase.getOriginalPrice() / 100.f));
                }
                holderView.count.setText("x" + productInfoBase.getQuantity());

//            if (StringUtil.isEmpty(holderView.productLogo.getDisplayUrl()) ||
//                    !holderView.productLogo.getDisplayUrl().contentEquals(productInfoBase.getProductLogo())) {
//                holderView.productLogo.setImageDrawable(null);
//            }

//            imageLoaderManager.displayImage(
//                    productInfoBase.getProductLogo(),
//                    holderView.productLogo, displayImageOptions);

                if (StringUtil.isEmpty(holderView.displayUrl) ||
                        !holderView.displayUrl.contentEquals(productInfoBase.getProductLogo())) {
                    holderView.productLogo.setImageDrawable(null);
                    AppDebug.d("Test", "productLogo:" + productInfoBase.getProductLogo());
                    imageLoaderManager.displayImage(
                            productInfoBase.getProductLogo(),
                            holderView.productLogo, displayImageOptions);
                    holderView.displayUrl = productInfoBase.getProductLogo();
                }


                if (StringUtil.isEmpty(productInfoBase.getSkuName())) {
                    holderView.skuTitle.setVisibility(View.INVISIBLE);
                } else {
                    holderView.skuTitle.setText(productInfoBase.getSkuName());
                    holderView.skuTitle.setVisibility(View.VISIBLE);
                }
            }
        }

        return convertView;
    }

    static class TO_OrderDD_HolderView {
        public ImageView productLogo;
        public TextView productName;
        public TextView price;
        public TextView count;
        public TextView originalPrice;
        public TextView skuTitle;

        public String displayUrl;
    }
}
