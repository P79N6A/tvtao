package com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.powyin.slide.widget.BannerSwitch;
import com.tvlife.imageloader.utils.ClassicOptions;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tvtaobao.biz.focus_impl.FocusFakeListView;
import com.yunos.tvtaobao.biz.request.bo.ItemListBean;
import com.yunos.tvtaobao.biz.request.bo.ShopDetailData;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.R2;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by GuoLiDong on 2018/10/20.
 */
public class ShopInfoItemVH extends FocusFakeListView.ViewHolder {
    Context context;
    Unbinder unbinder;
    @BindView(R2.id.shop_status_rest)
    ImageView shopStatusRest;
    @BindView(R2.id.shop_status_ordering)
    TextView shopStatusOrdering;
    @BindView(R2.id.shop_name)
    TextView shopName;
    @BindView(R2.id.good_discount)
    BannerSwitch goodDiscount;


    public ShopInfoItemVH(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.shop_info_item, parent,false));
        this.context = context;
        unbinder = ButterKnife.bind(this, itemView);
    }


    public void fillWith(ShopDetailData itemData) {
        ShopDetailData shopDetailData = itemData;
        shopStatusRest.setVisibility(View.INVISIBLE);
        shopStatusOrdering.setVisibility(View.INVISIBLE);

        String shopStatus = (shopDetailData == null || shopDetailData.getStoreDetailDTO() == null)
                ? "" : shopDetailData.getStoreDetailDTO().getShopStatus();
        shopStatus = shopStatus == null ? "" : shopStatus;
        switch (shopStatus) {
            case "BOOKING":    // 预定
                shopStatusOrdering.setVisibility(View.VISIBLE);
                shopStatusRest.setVisibility(View.GONE);
                if (shopDetailData.getStoreDetailDTO().shopStatusDetail != null &&
                        !TextUtils.isEmpty(shopDetailData.getStoreDetailDTO().shopStatusDetail.statusDesc)) {
                    shopStatusOrdering.setText(String.format("接受预定, 预计%s", shopDetailData.getStoreDetailDTO().shopStatusDetail.statusDesc));
                } else {
                    shopStatusOrdering.setText("接受预定");
                }
                break;
            case "RESTING":    // 休息
                shopStatusOrdering.setVisibility(View.GONE);
                shopStatusRest.setVisibility(View.VISIBLE);
                if (shopDetailData == null || shopDetailData.getItemGenreWithItemsList() == null) {
                    break;
                }
                for (ShopDetailData.ItemGenreWithItemsListBean itemGenreWithItemsListBean : shopDetailData.getItemGenreWithItemsList()) {
                    List<ItemListBean> itemList = itemGenreWithItemsListBean == null ? null : itemGenreWithItemsListBean.getItemList();
                    if (itemList != null) {
                        for (ItemListBean itemListBean : itemList) {
                            itemListBean.setIsRest(true);
                        }
                    }
                }
                break;
            case "":
            case "SELLING":
            case "WILLRESTING":
                shopStatusRest.setVisibility(View.GONE);
                shopStatusOrdering.setVisibility(View.GONE);
                break;
        }

        // 载入轮播优惠信息
        List<ShopDetailData.StoreDetailDTOBean.ActivityListBean> activityList =
                shopDetailData != null && shopDetailData.getStoreDetailDTO() != null ? shopDetailData.getStoreDetailDTO().getActivityList() : null;
        List<ShopDetailData.StoreDetailDTOBean.ServiceListBean> serviceList =
                shopDetailData != null && shopDetailData.getStoreDetailDTO() != null ? shopDetailData.getStoreDetailDTO().getServiceList() : null;
        int index = 0;
        for (int i = 0; activityList != null && i < activityList.size() && index < goodDiscount.getChildCount(); i++) {        //显示活动优惠
            if (TextUtils.isEmpty(activityList.get(i).getIcon()) || TextUtils.isEmpty(activityList.get(i).getDescription())) {
                continue;
            }
            FrameLayout frameLayout = (FrameLayout) goodDiscount.getChildAt(index);
            ImageView imageView = (ImageView) frameLayout.getChildAt(0);
            TextView textView = (TextView) frameLayout.getChildAt(1);
            ImageLoaderManager.getImageLoaderManager(context).displayImage(activityList.get(i).getIcon(), imageView, ClassicOptions.dio565);
            textView.setText(activityList.get(i).getDescription());
            index++;
        }
        for (int i = 0; serviceList != null && i < serviceList.size() && index < goodDiscount.getChildCount(); i++) {           //显示服务优惠
            if (TextUtils.isEmpty(serviceList.get(i).getIcon()) || TextUtils.isEmpty(serviceList.get(i).getDescription())) {
                continue;
            }
            FrameLayout frameLayout = (FrameLayout) goodDiscount.getChildAt(index);
            ImageView imageView = (ImageView) frameLayout.getChildAt(0);
            TextView textView = (TextView) frameLayout.getChildAt(1);
            ImageLoaderManager.getImageLoaderManager(context).displayImage(serviceList.get(i).getIcon(), imageView, ClassicOptions.dio565);
            textView.setText(serviceList.get(i).getDescription());
            index++;
        }
        for (; index < goodDiscount.getChildCount(); ) {//删除对于空view
            goodDiscount.removeViewAt(index);
        }

        // 展示店家名字
        if (shopDetailData.getStoreDetailDTO() != null) {
            shopName.setText(shopDetailData.getStoreDetailDTO().getName());
        }
    }
}
