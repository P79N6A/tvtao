package com.yunos.tvtaobao.newcart.ui.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ItemComponent;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ItemQuantity;
import com.taobao.wireless.trade.mcart.sdk.constant.CartFrom;
import com.taobao.wireless.trade.mcart.sdk.engine.CartEngine;
import com.taobao.wireless.trade.mcart.sdk.utils.CartResult;
import com.tvlife.imageloader.core.DisplayImageOptions;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.info.GlobalConfigInfo;
import com.yunos.tvtaobao.biz.widget.GroupBaseAdapter;
import com.yunos.tvtaobao.biz.widget.InnerFocusGroupListView;
import com.yunos.tvtaobao.newcart.R;
import com.yunos.tvtaobao.newcart.entity.CartBuilder;
import com.yunos.tvtaobao.newcart.entity.CartGoodsComponent;
import com.yunos.tvtaobao.newcart.entity.ShopCartGoodsBean;
import com.yunos.tvtaobao.newcart.itemview.NewShopCartItemInfoView;
import com.yunos.tvtaobao.newcart.ui.activity.CouponActivity;
import com.yunos.tvtaobao.newcart.ui.activity.NewShopCartListActivity;
import com.yunos.tvtaobao.newcart.util.RebateManager;
import com.yunos.tvtaobao.biz.widget.CustomDialog;
import com.yunos.tvtaobao.newcart.view.NewShopCartShopSelectView;
import com.yunos.tvtaobao.newcart.view.ShopCartHintFocusLayout;
import com.yunos.tvtaobao.newcart.view.ShopCartItemFocusLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 购物车列表的适配器
 */
public class NewShopCartListAdapter extends GroupBaseAdapter {

    private final String TAG = "ShopCartListAdapter";
    private CartBuilder mCartBuilder;
    private Toast toast;
    private OnShopControllerClickListener mOnShopControllerClickListener;
    private List<ItemComponent> itemList = new ArrayList<>();
    private CustomDialog commonDialog;

    // 商品动作类型
    public enum ShopItemAction {
        CHECKED, DELETE, DELETE_ALL, DETAIL, EDIT, SHOP_CHECKED
    }

    private ImageLoaderManager mImageLoaderManager; // 图片加载器
    private DisplayImageOptions mImageOptions; // 图片加载的参数设置
    private Rect mHintRect = new Rect(); // 商家分组的标识区域大小
    private Rect mHintFocusPadding = new Rect(); // focus选中调整的区域值
    private Rect mShopItemRect = new Rect(); // 每个列表商品的区域大小
    private Rect mShopItemFocusPaddingRect = new Rect(); // focus选中调整的区域值
    private List<ShopSellerItemUiData> mShopSellerItemUiDataList; // 商家数据列表
    private Map<String, List<CartGoodsComponent>> mShopItemData; // 商品列表的数据
    private Drawable mTaobaoDrawable; // 淘宝商品的标识
    private Drawable mTmallDrawable; // 天猫商品的标识
    private Context mContext; // 上下文
    private String mSpaceString; // 空隔文字
    private String mCheckAllString; // 全选的文字
    private String mCheckString; // 选择的文字
    private String mCancelCheckString; // 取消的文字
    private AbsoluteSizeSpan mPriceCountAbsoluteSizeSpan; // 价格上面的商品个数文字的大小
    private AbsoluteSizeSpan mPreSellAbsoluteSizeSpan; // 价格上面的商品个数文字的大小
    private ForegroundColorSpan mValidPriceForegroundColorSpan; // 有效的商品价格文字颜色
    private ForegroundColorSpan mPriceForegroundColorSpan; // 价格的文字颜色
    private ForegroundColorSpan mPriceCountForegroundColorSpan; // 价格上面商品个数文字的颜色
    private int mShopItemCardWidth; // 商品卡片位的宽度
    private InnerFocusGroupListView mFocusGroupHorizonalListView; // 购物车列表
    private int mShopItemInforDetailMargin; // 购物车详细信息的间隔
    private int mShopItemInforControllerHeight; // 购物车详细信息的高度
    private int mShopHintTextPaddingTop; // 购物车列表组文字的padding的大小
    private OnShopItemControllerClickListener mOnShopItemControllerClickListener; // 商品操作功能按钮的点击事件
    private Map<String, ShopSellerItemUiData> mCheckedShopSellerItemMap; // 加入结算商铺相关UI操作信息
    private Handler mHandler = new Handler(); // UI的Handler

    //区分天猫超市结算toast文案
    private static String TM_TOAST = "天猫超市商品不能和已选商品合并下单，您可以单独购买";

    public NewShopCartListAdapter(Context context, CartBuilder cartBuilder, InnerFocusGroupListView listView) {
        mContext = context;
        mCartBuilder = cartBuilder;
        mFocusGroupHorizonalListView = listView;

        mCheckedShopSellerItemMap = new HashMap<String, ShopSellerItemUiData>();
        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(context);
        mImageOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).cacheOnDisc(true).cacheInMemory(false).build();

        // 初始化里面item的大小区域
        mHintRect.set(0, 0, getDimensionPixelSize(R.dimen.dp_880),
                getDimensionPixelSize(R.dimen.dp_52));

        mShopItemRect.set(0, 0, getDimensionPixelSize(R.dimen.dp_880),
                getDimensionPixelSize(R.dimen.dp_240));
        mHintFocusPadding.set(getDimensionPixelSize(R.dimen.newcart_hint_focus_padding_left),
                getDimensionPixelSize(R.dimen.newcart_hint_focus_padding_top),
                getDimensionPixelSize(R.dimen.newcart_hint_focus_padding_right),
                getDimensionPixelSize(R.dimen.newcart_hint_focus_padding_bottom));
        mShopItemFocusPaddingRect.set(getDimensionPixelSize(R.dimen.newcart_item_focus_padding_left),
                getDimensionPixelSize(R.dimen.newcart_item_focus_padding_top),
                getDimensionPixelSize(R.dimen.newcart_item_focus_padding_right),
                getDimensionPixelSize(R.dimen.newcart_item_focus_padding_bottom));

        mShopItemInforDetailMargin = getDimensionPixelSize(R.dimen.dp_12_7);
        mShopItemInforControllerHeight = getDimensionPixelSize(R.dimen.newcart_item_controller_height);
        mShopHintTextPaddingTop = getDimensionPixelSize(R.dimen.newcart_item_infor_selected_icon_padding);

        // 店铺平台图片
//        mTaobaoDrawable = mContext.getResources().getDrawable(R.drawable.ytm_shop_cart_item_taobao_hint);
//        mTmallDrawable = mContext.getResources().getDrawable(R.drawable.ytm_shop_cart_item_tmall_hint);
        // 公用的文字
        mSpaceString = mContext.getString(R.string.ytm_space_text); // 空隔
        mCheckAllString = mContext.getString(R.string.newcart_hint_text_checked_all);
        mCancelCheckString = mContext.getString(R.string.newcart_text_cancel_checked);
        mCheckString = mContext.getString(R.string.newcart_text_checked);
        // 同一行文字不同显示效果的Span
        mPriceCountAbsoluteSizeSpan = new AbsoluteSizeSpan(mContext.getResources().getDimensionPixelSize(R.dimen.sp_18));
        mPreSellAbsoluteSizeSpan = new AbsoluteSizeSpan(mContext.getResources().getDimensionPixelSize(R.dimen.sp_26));
        mPriceCountForegroundColorSpan = new ForegroundColorSpan(mContext.getResources().getColor(
                R.color.newcart_infor_shop_count_text_color));
        mPriceForegroundColorSpan = new ForegroundColorSpan(mContext.getResources().getColor(
                R.color.newcart_infor_price_text_color));
        mValidPriceForegroundColorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.ytm_black));

        mShopItemCardWidth = mContext.getResources().getDimensionPixelSize(R.dimen.newcart_item_infor_card_width);
    }

    /**
     * 设置控制按钮的监听方法
     *
     * @param listener
     */
    public void setOnShopItemControllerClickListener(OnShopItemControllerClickListener listener) {
        mOnShopItemControllerClickListener = listener;
    }

    public void setOnShopControllerClickListener(OnShopControllerClickListener listener) {
        mOnShopControllerClickListener = listener;
    }

    /**
     * 设置购物车列表数据
     *
     * @param data
     */
    public void setShopItemData(Map<String, List<CartGoodsComponent>> data) {
        mShopItemData = data;
        if (mShopItemData != null) {
            // 将数据整理成商家数据列表
            buildShopSellerItemData(data);
            // 创建相关组数据
            buildGroup();
        }
    }

    /**
     * 取得商品的数据（如果当前位置是组标识就返回为null）
     *
     * @param position
     * @return
     */
    public CartGoodsComponent getGoodsItemData(int position) {
        int groupId = getGroupPos(position);
        int itemId = getGroupItemPos(position);
        if (groupId >= 0 && itemId >= 0 && itemId != Integer.MAX_VALUE) {
            return getShopItem(groupId, itemId);
        }
        return null;
    }

    /**
     * 取得组内所有商品列表（如果当前位置不是组标识就返回为null）
     *
     * @param position
     * @return
     */
    public List<CartGoodsComponent> getGoodsItemDataList(int position) {
        int groupId = getGroupPos(position);
        int itemId = getGroupItemPos(position);
        if (groupId >= 0 && itemId == Integer.MAX_VALUE) {
            return getShopList(groupId);
        }
        return null;
    }

    public List<CartGoodsComponent> getGoodsItemDataListNotGroup(int position) {
        int groupId = getGroupPos(position);
        int itemId = getGroupItemPos(position);
        if (groupId >= 0) {
            return getShopList(groupId);
        }
        return null;
    }

    @Override
    public int getGroupCount() {
        if (mShopSellerItemUiDataList != null) {
            return mShopSellerItemUiDataList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemCount(int groupId) {
        List<CartGoodsComponent> itemList = getShopList(groupId);
        if (itemList != null) {
            return itemList.size();
        }
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupHintView(int position, int groupId, View convertView) {
        AppDebug.i(TAG, "getGroupHintView position=" + position + " convertView=" + convertView);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_new_shop_cart_sticky_head, null);
            // 设置手动调整focus区域
            ShopCartHintFocusLayout layout = (ShopCartHintFocusLayout) convertView;
            layout.setCustomerPaddingRect(mHintFocusPadding);
        }
        // 解决重用之前已经放大过的view显示问题

        convertView.setScaleX(1.0f);
        convertView.setScaleY(1.0f);
        updateShopHintView(position, groupId, (ShopCartHintFocusLayout) convertView);
        return convertView;
    }

    public void setSelectedPos(int selectedPos) {
        this.selectedPos = selectedPos;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupItemView(int position, int groupId, int itemId, View convertView) {
        AppDebug.i(TAG, TAG + ".getGroupItemView position=" + position + ".groupId = " + groupId + ".itemId = "
                + itemId + " convertView=" + convertView);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_new_shop_cart_item, null);
        }
        // 解决重用之前已经放大过的view显示问题
        convertView.setScaleX(1.0f);
        convertView.setScaleY(1.0f);
        if (position + 1 != selectedPos && convertView instanceof ShopCartItemFocusLayout) {
            ((ShopCartItemFocusLayout) convertView).resetState();
        }
        updateShopItemView(position, groupId, itemId, convertView);
        // 如果是当前选中的商品就展示选中的UI
//        int selected = mFocusGroupHorizonalListView.getSelectedItemPosition();
//        if (selected == position) {
//            selectedShopItemView(true, position, convertView);
//        } else {
//            // 默认情况下不显示控件按钮
//            updateShopItemControllerView(false, groupId, itemId, convertView);
//        }
        return convertView;
    }

    @Override
    public Rect getGroupHintRect() {
        return mHintRect;
    }

    @Override
    public Rect getGroupItemRect() {
        return mShopItemRect;
    }

    /**
     * 选中商品
     *
     * @param selected
     * @param position
     * @param convertView
     */
    public void selectedShopItemView(boolean selected, int position, View convertView) {
        int realPos = position - 1;
        selectedPos = position;
        int groupId = getGroupPos(realPos);
        int itemId = getGroupItemPos(realPos);
        AppDebug.v(TAG, TAG + ".selectedShopItemView.selected = " + selected + ".position = " + position
                + ".convertView = " + convertView + ", groupId = " + groupId + ", itemId = " + itemId);
        // 位置是否有效
        if (groupId >= 0 && itemId < Integer.MAX_VALUE) {
//            updateShopItemControllerView(selected, groupId, itemId, convertView);
            if (selected) {
                // 自动Focus到商品里面的checked按钮上面，加个消息循环是为了让选中后View的变化完成再进行手动查找
                mHandler.post(new Runnable() {

                    public void run() {
                        mFocusGroupHorizonalListView.clearInnerFocusState();
                        mFocusGroupHorizonalListView.manualFindFocusInner(KeyEvent.KEYCODE_DPAD_RIGHT);
                    }
                });
            }
        }
    }

    private int selectedPos = -1;

    /**
     * 选中组Hint的View
     *
     * @param selected
     * @param hintView
     */
    public void selectedHintView(boolean selected, int position, ShopCartHintFocusLayout hintView) {
        AppDebug.i(TAG, "selectedHintView selected=" + selected + " position=" + position);
        //TODO 焦点设置最好放在itemview而不是listView中，容易造成混乱
        if (selected) mHandler.post(new Runnable() {

            public void run() {
                mFocusGroupHorizonalListView.clearInnerFocusState();
                mFocusGroupHorizonalListView.manualFindFocusInner(KeyEvent.KEYCODE_DPAD_LEFT);
            }
        });
        // do nothing
    }

    /**
     * 选择是否将整组商品加入结算或者取消结算
     *
     * @param position
     * @param convertView
     */
    public void checkShopHint(int position, ShopCartHintFocusLayout convertView) {
        int groupId = getGroupPos(position);
        int hintId = getGroupItemPos(position);
        AppDebug.v(TAG, TAG + ".checkShopHint.position = " + position + ".groupId = " + groupId + ".hintId = " + hintId);
        // 位置是否有效
        if (groupId >= 0) {
            ShopSellerItemUiData itemData = getShopSellerItemUiData(groupId);

            // 是否为无效的商品组
            if (itemData!=null&&!itemData.mIsInValid) {
                if (itemData.isAllValidChecked()) {
                    mCheckedShopSellerItemMap.remove(itemData.mShopId);
                    // 取消所有
                    itemData.cleanChecked();
                    updateShopHintView(position, groupId, convertView);
                    setAllShopItemChecked(position, groupId, false);
                } else {
                    // 商铺里面的商口结算做互斥
                    itemData.checkedAll();
                    setAllShopItemChecked(position, groupId, true);
                    if (setCheckedOrRadio(itemData.mShopId)) {
                        itemData.cleanChecked();
                        setAllShopItemChecked(position, groupId, false);
                    } else {
                        // 设置所有
                        setAllShopItemChecked(position, groupId, false);

                        AppDebug.v(TAG, TAG + ".itemData.goodsList.size = " + itemData.goodsList.size());
                        if (itemData.goodsList != null && itemData.goodsList.size() > 0) {
                            for (int i = 0; i < itemData.goodsList.size(); i++) {
                                AppDebug.v(TAG, TAG + ".isPreBuyItem = " + itemData.goodsList.get(i).getItemComponent().isPreBuyItem() +
                                        ".isPreSell = " + itemData.goodsList.get(i).getItemComponent().isPreSell());

                                if(itemData.goodsList.get(i) != null && itemData.goodsList.get(i).getItemComponent()!=null){
                                    if (itemData.goodsList.get(i) != null &&(itemData.goodsList.get(i).getItemComponent().isPreBuyItem() ||itemData.goodsList.get(i).getItemComponent().isPreSell())) {
                                        if (itemData.goodsList.get(i).getItemComponent().isValid()) {
                                            updateShopHintView(position, groupId, convertView);
                                            setAllShopItemChecked(position, groupId, true);
                                            mCheckedShopSellerItemMap.put(itemData.mShopId, itemData);
                                        } else {
                                            itemData.deleteChecked(1);
                                        }
                                    } else {
                                        if (itemData.goodsList.get(i) != null &&itemData.goodsList.get(i).getItemComponent().isValid()) {
                                            updateShopHintView(position, groupId, convertView);
                                            setAllShopItemChecked(position, groupId, true);
                                            mCheckedShopSellerItemMap.put(itemData.mShopId, itemData);
                                        } else {

                                            //                                        showToast(itemData.goodsList.get(i).getItemComponent().getCodeMsg());
                                            itemData.deleteChecked(1);
                                            updateShopHintView(position, groupId, convertView);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // 删除全部
                AppDebug.i(TAG, "checkShopHint delete all");
                if (mOnShopItemControllerClickListener != null) {
                    mOnShopItemControllerClickListener.onShopItemControllerClick(ShopItemAction.DELETE_ALL,
                            mFocusGroupHorizonalListView.getSelectedItemPosition());
                }
            }
        }
    }

    /**
     * 选择是否需要结算的商品(加入结算并且更新UI)
     *
     * @param position    列表中的位置
     * @param convertView ShopCartItemFocusLayout
     */
    public void checkShopItem(int position, ShopCartItemFocusLayout convertView) {
        int groupId = getGroupPos(position);
        int itemId = getGroupItemPos(position);
        AppDebug.v(TAG, TAG + ".checkShopItem.position = " + position + ".groupId = " + groupId + ".itemId = " + itemId);
        if (groupId >= 0 && itemId >= 0 && itemId != Integer.MAX_VALUE) {
            CartGoodsComponent itemData = getShopItem(groupId, itemId);
            if (itemData != null && itemData.getItemComponent() != null) {
                int hintPosition = position - (itemId + 1); // 取得组标识的位置序号
                AppDebug.v(TAG, TAG + ".checkShopItem.hintPosition = " + hintPosition + ", itemData = " + itemData
                        + "isChecked = " + itemData.getItemComponent().isChecked());
                // 将商品设置为结算的同时更新组的标识View
                if (itemData.getItemComponent().isChecked()) {

                    itemData.getItemComponent().setChecked(false, false);
                    boolean changedNotAllChecked = cutCheckedShopCount(groupId, itemData.getItemComponent().getShopId());
                    if (changedNotAllChecked) {
                        // 取得指定的组标识的View
                        View childView = getListPositionView(hintPosition);
                        if (childView instanceof ShopCartHintFocusLayout) {
                            updateShopHintView(position, groupId, (ShopCartHintFocusLayout) childView);
                        }
                    }
                } else {
                    // 商铺里面的商口结算做互斥
                    ShopSellerItemUiData shopUiData = getShopSellerItemUiData(groupId);
                    if (shopUiData != null) {
//                        clearAllOtherShopSellerChecked(shopUiData.mShopId);
                        itemData.getItemComponent().setChecked(true, false);
                        if (!setCheckedOrRadio(shopUiData.mShopId)) {

                            boolean allChecked = addCheckedShopCount(groupId, itemData.getItemComponent().getShopId());
                            if (allChecked) {
                                // 取得指定的组标识的View
                                View childView = getListPositionView(hintPosition);
                                if (childView instanceof ShopCartHintFocusLayout) {
                                    updateShopHintView(position, groupId, (ShopCartHintFocusLayout) childView);
                                }
                            }
                        } else {
                            itemData.getItemComponent().setChecked(false, false);
                        }
                    } else {
                        itemData.getItemComponent().setChecked(true, false);
                        boolean allChecked = addCheckedShopCount(groupId, itemData.getItemComponent().getShopId());
                        if (allChecked) {
                            // 取得指定的组标识的View
                            View childView = getListPositionView(hintPosition);
                            if (childView instanceof ShopCartHintFocusLayout) {
                                updateShopHintView(position, groupId, (ShopCartHintFocusLayout) childView);
                            }
                        }
                    }

                }
                // 更新当前选中的商品信息
                updateShopItemView(itemData, position, groupId, itemId, convertView);
                // 更新控件的信息
                updateShopItemControllerView(true, groupId, itemId, convertView);
            }
        }
    }

    /**
     * 在商铺的数据上增加结算商品（前提条件是当前选中的商品之前是非checked状态，需要做提前判断，否则会乱）
     *
     * @param groupId
     * @param shopId
     * @return boolean true全部选中（从之前的未全部选中状态），false状态未发生变化或者异常
     */
    private boolean addCheckedShopCount(int groupId, String shopId) {
        ShopSellerItemUiData itemData = getShopSellerItemUiData(groupId);
        // 只有商铺ID相同才有效
        if (itemData != null && itemData.mShopId!=null&&itemData.mShopId.equals(shopId)) {
            itemData.mCheckCount++;
            mCheckedShopSellerItemMap.put(itemData.mShopId, itemData);
            // 说明商铺里面的商品全部被选中
            if (itemData.isAllChecked()) {
                // 更新组的标识
                return true;
            }
        }
        return false;
    }

    /**
     * 在商铺的数据上减少结算商品（前提条件是当前选中的商品之前是checked状态，需要做提前判断，否则会乱）
     *
     * @param groupId
     * @param shopId
     * @return boolean true未全部选中（从已全部选中的状态变化过来），false状态未发生变化或者异常
     */
    private boolean cutCheckedShopCount(int groupId, String shopId) {
        ShopSellerItemUiData itemData = getShopSellerItemUiData(groupId);
        // 只有商铺ID相同才有效
        if (itemData != null && itemData.mShopId.equals(shopId)) {
            boolean result = false;
            // 说明商铺里面的商品之前是全部被选中状态
            if (itemData.isAllChecked()) {
                result = true;
            }
            // 变成未选中状态
            itemData.mCheckCount--;
            if (itemData.mCheckCount == 0) {
                mCheckedShopSellerItemMap.remove(itemData.mShopId);
            }
            if (result) {
                return true;
            }
        }
        return false;
    }

    /**
     * 将组内所有商品加入结算
     *
     * @param position
     * @param groupId
     * @param checked  是设置还是取消结算
     */
    private void setAllShopItemChecked(int position, int groupId, boolean checked) {
        for (int i = position + 1; i < getCount() && getGroupPos(i) == groupId; i++) {
            int itemId = getGroupItemPos(i);
            setShopItemChecked(i, groupId, itemId, checked, true);
            manageRebateChecked(groupId, itemId, checked);
        }
    }

    /**
     * 将组内所有商品加入结算
     *
     * @param position
     * @param groupId
     */
    private void getAllShopItem(int position, int groupId) {
        for (int i = position + 1; i < getCount() && getGroupPos(i) == groupId; i++) {
            int itemId = getGroupItemPos(i);
            CartGoodsComponent goodsData = getShopItem(groupId, itemId);
            if(goodsData!=null&&goodsData.getItemComponent()!=null){
                itemList.add(goodsData.getItemComponent());
            }
        }
    }

    /**
     * 将指定商品加入结算(并且更新UI)
     *
     * @param position
     * @param groupId
     * @param itemId
     * @param checked
     * @return boolean true成功处理，false未成功处理
     */
    private boolean setShopItemChecked(int position, int groupId, int itemId, boolean checked, boolean needClean) {
        CartGoodsComponent goodsData = getShopItem(groupId, itemId);
        boolean result = false;
        if (goodsData != null) {
            ItemComponent itemComponent = goodsData.getItemComponent();
            if(itemComponent!=null){
                AppDebug.v(TAG, TAG + ".setShopItemChecked.position = " + position + ".groupId = " + groupId
                        + ", itemId = " + itemId + "checked = " + checked + "isChecked = "
                        + itemComponent.isChecked());
                // 只有不相同的才会进行变更
                if (!itemComponent.isPreBuyItem()) {
                    if (itemComponent.isPreSell()) {
                        if (!itemComponent.isValid()) {
                            itemComponent.setChecked(true);
                        }
                    }
                } else {
                    itemComponent.setChecked(true);
                }
//
                if (itemComponent.isChecked() != checked) {
                    result = true;
                    if (needClean) {
                        itemComponent.setChecked(checked, false);
                    } else {
                        itemComponent.setChecked(!checked, false);
                    }

                    //勾选店铺时计算返利
                    View childView = getListPositionView(position);
                    // 更新当前选中的商品信息
                    if (childView instanceof ShopCartItemFocusLayout) {
                        updateShopItemView(goodsData, position, groupId, itemId, (ShopCartItemFocusLayout) childView);
                    }
                }
            }

        }
        return result;
    }

    /**
     * 管理去结算返利
     * */
    private void manageRebateChecked(int groupId, int itemId,boolean checked){
        CartGoodsComponent goodsData = getShopItem(groupId, itemId);
        if (goodsData != null) {
            ItemComponent itemComponent = goodsData.getItemComponent();
            if(itemComponent!=null){
                //如果result为true，则说明有不同店铺的商品在之前被选中结算
                if(checked){
                    ItemQuantity itemQuantity = itemComponent.getItemQuantity();
                    if(itemQuantity!=null && itemComponent.isValid()){
                        RebateManager.getInstance().add(itemComponent.getShopId(),
                                itemComponent.getCartId(),itemQuantity.getQuantity());
                    }

                } else {
                    RebateManager.getInstance().remove(itemComponent.getCartId());

                }
            }
        }

    }

    /**
     * 取得商铺UI数据的信息
     *
     * @param groupId
     * @return
     */
    private ShopSellerItemUiData getShopSellerItemUiData(int groupId) {
        if (mShopSellerItemUiDataList != null && mShopSellerItemUiDataList.size() > groupId) {
            return mShopSellerItemUiDataList.get(groupId);
        }
        return null;
    }

    /**
     * 更新商铺的标识View
     *
     * @param position
     * @param groupId
     * @param convertView
     */
    private void updateShopHintView(final int position, int groupId, final ShopCartHintFocusLayout convertView) {
        TextView tvShopName = (TextView) convertView.findViewById(R.id.tv_shopname);
        TextView tvGoIntoShop = (TextView) convertView.findViewById(R.id.tv_into_shop);
        LinearLayout layoutGetCoupon = (LinearLayout) convertView.findViewById(R.id.layout_get_coupon);
        TextView tvGetCoupon = (TextView) convertView.findViewById(R.id.tv_get_coupon);


        NewShopCartShopSelectView newShopCartShopSelectView = (NewShopCartShopSelectView) convertView.findViewById(R.id.img_select_button);
        RelativeLayout layoutValidShop = (RelativeLayout) convertView.findViewById(R.id.layout_valid_shop);
        final TextView tvDeleteInvlidGoods = (TextView) convertView.findViewById(R.id.tv_delete_invalid_goods);
         LinearLayout layoutDeleteInvlidGoods = (LinearLayout) convertView.findViewById(R.id.layout_delete_invalid_goods);
        ShopSellerItemUiData shopData = getShopSellerItemUiData(groupId);
        final CartGoodsComponent itemData = getShopItem(groupId, 0);
        ImageView ivShopIcon = (ImageView) convertView.findViewById(R.id.img_shop_type);
        newShopCartShopSelectView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int groupId = getGroupPos(position);
                getAllShopItem(position, groupId);
                mOnShopControllerClickListener.onShopControllerClick(ShopItemAction.SHOP_CHECKED, position, convertView, itemList);
            }
        });

        tvGetCoupon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //埋点
                Utils.utControlHit(((NewShopCartListActivity) mContext).getFullPageName(), "Button-ReceiveCoupons", initTBSProperty(null, SPMConfig.NEW_SHOP_CART_LIST_SPM_COUPON_BUTTON));
                Utils.updateNextPageProperties(SPMConfig.NEW_SHOP_CART_LIST_SPM_COUPON_BUTTON);
                CouponActivity.launch((NewShopCartListActivity) mContext, itemData.getShopComponent().getSellerId() + "", itemData.getShopComponent().getTitle());
            }
        });
        if (shopData != null && !shopData.mIsInValid) {
            // 有效商品组
            layoutValidShop.setVisibility(View.VISIBLE);
            layoutDeleteInvlidGoods.setVisibility(View.GONE);
            if (itemData != null && itemData.getShopComponent() != null && itemData.getShopComponent().getTitle() != null) {
                tvShopName.setText(itemData.getShopComponent().getTitle());
            }
            if (itemData != null && itemData.getShopComponent() != null && itemData.getShopComponent().isHasBonus()) {
                layoutGetCoupon.setVisibility(View.VISIBLE);
            } else {
                layoutGetCoupon.setVisibility(View.GONE);
            }
            TextView tvShopActivity = (TextView) convertView.findViewById(R.id.tv_shop_activities);
            if (itemData!=null&&itemData.getShopComponent()!=null&&itemData.getShopComponent().getCoudan() != null) {
                tvShopActivity.setText(itemData.getShopComponent().getCoudan().getTitle());
            } else {
                tvShopActivity.setText(null);
            }
            newShopCartShopSelectView.setCanCheck(shopData.canCheck());
            if (shopData.canCheck()) {
                newShopCartShopSelectView.setAllChecked(shopData.isAllValidChecked());
            }

            if(itemData!=null&&itemData.getShopComponent()!=null){
                if (!itemData.getShopComponent().getType().equals("ALITRIP") && itemData.getShopComponent().getIcon() != null) {
                    ivShopIcon.setVisibility(View.VISIBLE);
                    ImageLoaderManager.getImageLoaderManager(mContext).displayImage(itemData.getShopComponent().getIcon(), ivShopIcon,mImageOptions);
                } else if ( itemData.getShopComponent().getType() != null) {
                    ivShopIcon.setVisibility(View.VISIBLE);
                    // 商店标识图片
                    if (itemData.getShopComponent().getType().equals("B")) {
                        //天猫
                        ivShopIcon.setImageResource(R.drawable.new_shop_tmall);
                    } else if (itemData.getShopComponent().getType().equals("SM")) {
                        //天猫超市
                        ivShopIcon.setImageResource(R.drawable.new_shop_sm);
                    } else if (itemData.getShopComponent().getType().equals("HK")) {
                        //天猫国际
                        ivShopIcon.setImageResource(R.drawable.new_shop_hk);
                    } else if (itemData.getShopComponent().getType().equals("C")) {
                        //淘宝
                        if (itemData.getShopComponent().getTitle().contains("企业品牌店")) {
                            ivShopIcon.setVisibility(View.VISIBLE);
                            ivShopIcon.setImageResource(R.drawable.new_shop_company);
                        } else {
                            ivShopIcon.setImageResource(R.drawable.new_shop_taobao);
                        }
                    } else if (itemData.getShopComponent().getType().equals("ALITRIP")) {
                        //飞猪
                        ivShopIcon.setImageResource(R.drawable.new_shop_alitrip);
                    } else {
                        ivShopIcon.setImageResource(R.drawable.new_shop_taobao);
                    }
                }
            }
        } else {
            // 无效商品组
            ivShopIcon.setVisibility(View.GONE);
            layoutValidShop.setVisibility(View.GONE);
            layoutDeleteInvlidGoods.setVisibility(View.VISIBLE);
            tvShopName.setText("已失效宝贝(" + mCartBuilder.getInvalidCartGoodsCount() + ")");
        }
        tvGoIntoShop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoShopIndex(itemData);
            }
        });
        tvDeleteInvlidGoods.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    tvDeleteInvlidGoods.setTextColor(mContext.getResources().getColor(R.color.ytm_white));
                } else {
                    tvDeleteInvlidGoods.setTextColor(mContext.getResources().getColor(R.color.new_cart_black));
                }
            }
        });
        tvDeleteInvlidGoods.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnShopItemControllerClickListener.onShopItemControllerClick(ShopItemAction.DELETE_ALL, position);
            }
        });
    }

    //    // 进入店铺
    private void gotoShopIndex(CartGoodsComponent cartGoodsComponent) {
        if (cartGoodsComponent != null && cartGoodsComponent.getShopComponent() != null) {
            String shopId = cartGoodsComponent.getShopComponent().getShopId();
            Utils.utControlHit(((NewShopCartListActivity) mContext).getFullPageName(), "Button-EnterShop", initTBSProperty(shopId, SPMConfig.NEW_SHOP_CART_LIST_SPM_GOTO_SHOP));
        }
        Utils.updateNextPageProperties(SPMConfig.NEW_SHOP_CART_LIST_SPM_GOTO_SHOP);

        ((NewShopCartListActivity) mContext).OnWaitProgressDialog(false);
        Intent intent = new Intent();
        if (cartGoodsComponent != null && cartGoodsComponent.getShopComponent()!=null&&"SM".equals(cartGoodsComponent.getShopComponent().getType())) {
            intent.setClassName(mContext, BaseConfig.SWITCH_TO_CHAOSHI_ACTIVITY);
        } else {
            GlobalConfig globalConfig = GlobalConfigInfo.getInstance().getGlobalConfig();
            if (globalConfig == null || !globalConfig.isBlitzShop()) {
                intent.setClassName(mContext, BaseConfig.SWITCH_TO_SHOP_ACTIVITY);
            } else {
                intent.setClassName(mContext, BaseConfig.SWITCH_TO_SHOP_BLIZ_ACTIVITY);
            }
            if (cartGoodsComponent != null && cartGoodsComponent.getShopComponent() != null) {
                intent.putExtra(BaseConfig.SELLER_NUMID, cartGoodsComponent.getShopComponent().getSellerId() + "");
                intent.putExtra(BaseConfig.SELLER_TYPE, cartGoodsComponent.getShopComponent().getType());
//                String tag_path = ActivityPat/**/hRecorder.getInstance().getCurrentPath(((NewShopCartListActivity) mContext)) + "";
//                if (!tag_path.contains("module=shop")) //判断是否从店铺会场进入
//                    intent.putExtra(ActivityPathRecorder.INTENTKEY_FIRST, true);
            }
        }
        mContext.startActivity(intent);
    }

    /**
     * 更新商品卡片位的信息（商铺信息，商品图片，商品详情）
     *
     * @param itemData
     * @param position
     * @param groupId
     * @param itemId
     * @param convertView
     */
    private void updateShopItemView(CartGoodsComponent itemData, final int position, int groupId, int itemId,
                                    View convertView) {
        if (convertView == null) {
            return;
        }
        if (itemData == null) {
            return;
        }
        ItemComponent infoComponent = itemData.getItemComponent();
        if (infoComponent == null) {
            return;
        }

        // 店铺的信息
        final NewShopCartItemInfoView newShopCartItemInfoView = (NewShopCartItemInfoView) convertView.findViewById(R.id.shopcart_item_infoview);
        ShopCartGoodsBean shopCartGoodsBean = new ShopCartGoodsBean();
        if (null != itemData.getItemComponent() && !itemData.getItemComponent().isValid() &&
                !itemData.getItemComponent().isPreBuyItem() && !itemData.getItemComponent().isPreSell()) {
            shopCartGoodsBean.setInvalid(true);
        } else {
            shopCartGoodsBean.setInvalid(false);
        }
        shopCartGoodsBean.setCartGoodsComponent(itemData);
        newShopCartItemInfoView.setData(shopCartGoodsBean);

        newShopCartItemInfoView.setOnShopItemClickListener(new NewShopCartItemInfoView.OnShopItemClickListener() {
            @Override
            public void onShopItemClick(ShopItemAction action) {
                if (mOnShopItemControllerClickListener != null) {
                    mOnShopItemControllerClickListener.onShopItemControllerClick(action,
                            position);
                }
            }

            @Override
            public void onShopItemCheck(ShopCartGoodsBean bean) {
                if (mOnShopItemControllerClickListener != null) {
                    mOnShopItemControllerClickListener.onShopItemControllerCheck(bean,
                            position);

                }
            }
        });

    }


    /**
     * 更新UI信息
     *
     * @param position
     * @param groupId
     * @param itemId
     * @param convertView
     */
    private void updateShopItemView(int position, int groupId, int itemId, View convertView) {
        CartGoodsComponent itemData = getShopItem(groupId, itemId);
        updateShopItemView(itemData, position, groupId, itemId, convertView);
    }

    /**
     * 更新商品上的控件按钮UI
     * 1.更新有效的操作按钮
     * 2.更新无效的操作按钮
     *
     * @param show        是否显示控件按钮
     * @param convertView
     */
    private void updateShopItemControllerView(boolean show, int groupId, int itemId, View convertView) {
        AppDebug.i(TAG, "updateShopItemControllerView show = " + show + ", groupId = " + groupId + ", itemId = "
                + itemId + ", convertView = " + convertView);
        final CartGoodsComponent itemData = getShopItem(groupId, itemId);
        if (itemData == null) {
            return;
        }
        if (itemData.getItemComponent() == null) {
            return;
        }

        // 店铺的信息
        NewShopCartItemInfoView newShopCartItemInfoView = (NewShopCartItemInfoView) convertView.findViewById(R.id.shopcart_item_infoview);
        ShopCartGoodsBean shopCartGoodsBean = new ShopCartGoodsBean();
        if (!itemData.getItemComponent().isValid() &&
                !itemData.getItemComponent().isPreBuyItem() && !itemData.getItemComponent().isPreSell()) {
            shopCartGoodsBean.setInvalid(true);

        } else {
            shopCartGoodsBean.setInvalid(false);
        }
        shopCartGoodsBean.setCartGoodsComponent(itemData);
        newShopCartItemInfoView.setData(shopCartGoodsBean);
    }

    private void showToast(String codeMsg) {
        if (commonDialog != null && commonDialog.isShowing()) {
            commonDialog.dismiss();
        }
        commonDialog = new CustomDialog.Builder(mContext).setType(1)
                .setResultMessage(codeMsg)
                .create();
        commonDialog.show();
//        if (toast == null) {
//            toast = Toast.makeText(mContext, "",
//                    Toast.LENGTH_SHORT);
//        }
//        if (!TextUtils.isEmpty(codeMsg)) {
//            toast.setText(codeMsg);
//            toast.show();
//        }
    }

    /**
     * 隐藏所有商品上的UI按钮控件
     */
    public void hideShopItemControllerView() {
        if (mFocusGroupHorizonalListView != null) {
            int firstPosition = mFocusGroupHorizonalListView.getFirstPosition();
            int lastPosition = mFocusGroupHorizonalListView.getLastVisiblePosition();
            AppDebug.i(TAG, TAG + ".hideShopItemControllerView.mFocusGroupHorizonalListView.childCount = "
                    + mFocusGroupHorizonalListView.getChildCount() + ", firstPosition = " + firstPosition
                    + ", lastPosition = " + lastPosition);

            for (int i = 0; i < lastPosition - firstPosition + 1; i++) {
                View child = mFocusGroupHorizonalListView.getChildAt(i);
                //AppDebug.v(TAG, TAG + ".hideShopItemControllerView.i = " + i + ", child = " + child);
                if (child instanceof ShopCartItemFocusLayout) {// 如果是购物车商品
                    selectedShopItemView(false, i + firstPosition, (ShopCartItemFocusLayout) child);
                }
            }
            mFocusGroupHorizonalListView.invalidate();
        }
    }

    //统计用的信息
    private void tbsClicked(String controlName, CartGoodsComponent itemData) {
        Map<String, String> p = Utils.getProperties();
        try {
            p.put("item_id", itemData.getItemComponent().getItemId());
            p.put("name", itemData.getItemComponent().getTitle());
            p.put("shop_id", itemData.getItemComponent().getShopId());
        } catch (Exception e) {
        }
        p.put("user_id", User.getUserId());
        TBS.Adv.ctrlClicked(CT.Button, controlName, Utils.getKvs(p));
    }

    /**
     * 取得单组商品列表数据
     *
     * @param groupId
     * @return
     */
    private List<CartGoodsComponent> getShopList(int groupId) {
        if (mShopItemData != null && mShopSellerItemUiDataList != null && mShopSellerItemUiDataList.size() > 0) {
            if (groupId >= 0 && groupId < mShopSellerItemUiDataList.size()) {
                ShopSellerItemUiData itemData = mShopSellerItemUiDataList.get(groupId);
                if (itemData != null) {
                    return mShopItemData.get(itemData.mShopId);
                }
            }
        }
        return null;
    }

    /**
     * 取得指定的item
     *
     * @param groupId
     * @param itemId
     * @return
     */
    private CartGoodsComponent getShopItem(int groupId, int itemId) {
        List<CartGoodsComponent> itemList = getShopList(groupId);
        if (itemList != null && itemId < itemList.size() && itemId >= 0) {
            return itemList.get(itemId);
        }
        return null;
    }

    /**
     * 取得指定的列表View
     *
     * @param position
     * @return
     */
    private View getListPositionView(int position) {
        View childView = null;
        if (mFocusGroupHorizonalListView != null) {
            childView = mFocusGroupHorizonalListView.getChildAt(position
                    - mFocusGroupHorizonalListView.getFirstPosition() + 1);
        }
        return childView;
    }

    /**
     * 将sum图片替换成指定的分辨率图片
     *
     * @param picUrl
     * @return
     */
    private String getShopItemImageUrl(String picUrl) {
        int lastStringCount = 10;
        if (picUrl.length() > lastStringCount) {
            String beforeString = picUrl.substring(0, (picUrl.length() - lastStringCount));
            String afterString = picUrl.substring((picUrl.length() - lastStringCount), picUrl.length());
            afterString = afterString.replace("_sum.", "_" + mShopItemCardWidth + "x" + mShopItemCardWidth + ".");
            return beforeString + afterString;
        }
        return picUrl;
    }

    /**
     * 从原始数据里面生成商铺相关的交互数据
     */
    private void buildShopSellerItemData(Map<String, List<CartGoodsComponent>> data) {
        AppDebug.v(TAG, TAG + ".buildShopSellerItemData.mShopItemData = " + mShopItemData);
        if (mShopItemData == null) {
            return;
        }
        AppDebug.v(TAG, TAG + ".buildShopSellerItemData.mShopItemData.size = " + mShopItemData.size()
                + ", mCheckedShopSellerItemUiData = " + mCheckedShopSellerItemMap);
        mShopSellerItemUiDataList = new ArrayList<ShopSellerItemUiData>();
        int groupId = 0;
        for (Iterator<?> it = mShopItemData.keySet().iterator(); it.hasNext(); ) {
            ShopSellerItemUiData itemData = new ShopSellerItemUiData();
            String key = (String) it.next();
            List<CartGoodsComponent> goodsList = mShopItemData.get(key);
            if (goodsList != null&&goodsList.size()>0) {
                itemData.mGoodsTotalCount = goodsList.size();
                // 循环查找已经加入结算的商品
                for (CartGoodsComponent goods : goodsList) {
                    if (goods.getItemComponent() != null) {
                        AppDebug.v(TAG, TAG + ".buildShopSellerItemData.isChecked = "
                                + goods.getItemComponent().isChecked() + ", shopId = " + key);
                    }

                    // 当前没有选择的店铺,或者不为当前店铺，则该店铺做取消check处理
                    if (mCheckedShopSellerItemMap.size() == 0
                            || !mCheckedShopSellerItemMap.containsKey(key)) {
                        if (goods.getItemComponent() != null && goods.getItemComponent().isChecked()) {
                            goods.getItemComponent().setChecked(false, false);
                        }
                    } else {
                        if (goods.getItemComponent() != null && goods.getItemComponent().isChecked()) {
                            if (!goods.getItemComponent().isPreBuyItem()) {
                                if (goods.getItemComponent().isPreSell()) {
                                    if (goods.getItemComponent().isValid()) {
                                        itemData.mCheckCount++;
                                    } else {
                                    }
                                } else {
                                    itemData.mCheckCount++;
                                }
                            } else {
                            }

                        }
                    }
                }
            }
            itemData.mShopId = key;
            itemData.mGroupId = groupId;
            itemData.setGoodsList(goodsList);
            if (key.equals("invalid")) {
                itemData.mIsInValid = true;
            }

            // 保存当前已经加入结算的商品
            if (itemData.mCheckCount > 0) {
                if(itemData.mShopId!=null){
                    mCheckedShopSellerItemMap.put(itemData.mShopId, itemData);
                }
            }
            groupId++;
            AppDebug.v(TAG, TAG + ".buildShopSellerItemData.itemData = " + itemData
                    + ", mCheckedShopSellerItemUiData = " + mCheckedShopSellerItemMap);
            mShopSellerItemUiDataList.add(itemData);
        }
    }

    /**
     * 取消所有其它商铺的结算商品做到互斥（并且更新已经显示的着的页面）
     *
     * @param newCheckedId
     */
    boolean hasDiffSellor;

    public boolean clearAllOtherShopSellerChecked(String newCheckedId, String errMsg) {
        AppDebug.i(TAG, TAG + ".clearAllOtherShopSellerChecked.mCheckedShopSellerItemUiData = "
                + mCheckedShopSellerItemMap + " newCheckedId=" + newCheckedId);
        if (mCheckedShopSellerItemMap.size() != 0) {
            if (!mCheckedShopSellerItemMap.containsKey(newCheckedId)) {
                // 清除结算状态
                for (String key : mCheckedShopSellerItemMap.keySet()) {
                    AppDebug.e("SHOPCART", "key : " + key);
//                    mCheckedShopSellerItemMap.get(key).cleanChecked();
                    // 更新所有显示中的页面
                    int listCount = getCount();
                    AppDebug.i(TAG, TAG + ".clearAllOtherShopSellerChecked listCount=" + listCount);
                    hasDiffSellor = false;
                    for (int i = 0; i < listCount; i++) {
                        int groupId = getGroupPos(i);
                        int itemId = getGroupItemPos(i);
                        // 找到之前加入结算的组
                        if (groupId == mCheckedShopSellerItemMap.get(key).mGroupId && mCheckedShopSellerItemMap.get(key).goodsList != null) {
                            for (int j = 0; j < mCheckedShopSellerItemMap.get(key).goodsList.size(); j++) {
                                CartGoodsComponent cartGoodsComponent = mCheckedShopSellerItemMap.get(key).goodsList.get(j);
                                if (cartGoodsComponent != null && cartGoodsComponent.getItemComponent() != null && cartGoodsComponent.getItemComponent().isChecked()) {
                                    itemId = j;
                                    i = i + j + 1;
                                    break;
                                }
                            }
                            if (itemId != Integer.MAX_VALUE) {
                                boolean result = setShopItemChecked(i, mCheckedShopSellerItemMap.get(key).mGroupId, itemId, false, false);
                                // 如果成功设置说明有不同店铺的商品在之前被选中结算
                                if (result) {
                                    hasDiffSellor = true;
                                    showToast(errMsg);
                                }
                                return result;
                            } else {
                                View childView = getListPositionView(i);
                                if (childView instanceof ShopCartHintFocusLayout) {
                                    updateShopHintView(i, groupId, (ShopCartHintFocusLayout) childView);
                                }
                            }
                        }
                    }
                }

                mCheckedShopSellerItemMap.clear();
            }
        }
        return false;
    }

    private boolean clearTianmaoChaoshiChecked(String newCheckedId) {
        AppDebug.i(TAG, TAG + ".clearAllOtherShopSellerChecked.mCheckedShopSellerItemUiData = "
                + mCheckedShopSellerItemMap + " newCheckedId=" + newCheckedId);

        if (mCheckedShopSellerItemMap.size() == 0)
            return false;

        boolean hasDiffSellor = false;
        for (String key : mCheckedShopSellerItemMap.keySet()) {
            ShopSellerItemUiData shopSellerItemUiData = mCheckedShopSellerItemMap.get(key);
            if (shopSellerItemUiData != null) {
                if (!mCheckedShopSellerItemMap.containsKey(newCheckedId)) {
                    // 清除结算状态
//                    shopSellerItemUiData.cleanChecked();
                    // 更新所有显示中的页面
                    int listCount = getCount();
                    AppDebug.i(TAG, TAG + ".clearAllOtherShopSellerChecked listCount=" + listCount);
                    for (int i = 0; i < listCount; i++) {
                        int groupId = getGroupPos(i);
                        int itemId = getGroupItemPos(i);
                        // 找到之前加入结算的组
                        if (groupId == shopSellerItemUiData.mGroupId && shopSellerItemUiData.goodsList != null) {
                            for (int j = 0; j < shopSellerItemUiData.goodsList.size(); j++) {
                                CartGoodsComponent cartGoodsComponent = shopSellerItemUiData.goodsList.get(j);
                                if (cartGoodsComponent != null && cartGoodsComponent.getItemComponent() != null && cartGoodsComponent.getItemComponent().isChecked()) {
                                    itemId = j;
                                    i = i + j + 1;
                                    break;
                                }
                            }
                            if (itemId != Integer.MAX_VALUE) {
                                boolean result = setShopItemChecked(i, shopSellerItemUiData.mGroupId, itemId, false, false);
                                // 如果成功设置说明有不同店铺的商品在之前被选中结算
                                if (result) {
                                    hasDiffSellor = true;
                                    showToast(TM_TOAST);
                                }
                                return result;
                            } else {
                                View childView = getListPositionView(i);
                                if (childView instanceof ShopCartHintFocusLayout) {
                                    updateShopHintView(i, groupId, (ShopCartHintFocusLayout) childView);
                                }
                            }
                        }
                    }
                    // 不同店铺选择商品时给出提示
                }
            }
        }
        mCheckedShopSellerItemMap.clear();
        return false;
    }

    /**
     * 设置天猫超市商品与其他商品选折互斥
     *
     * @param newCheckedId
     */
    private boolean clearChaoshiShopOrOtherShopChecked(String newCheckedId) {
        if (mCheckedShopSellerItemMap.size() != 0) {
            if (newCheckedId.equals("67597230") || mCheckedShopSellerItemMap.containsKey("67597230")) {
                return clearTianmaoChaoshiChecked(newCheckedId);
            }
        }
        return false;
    }

    /**
     * 设置单选或者多选
     *
     * @param newCheckedId
     */
    private boolean setCheckedOrRadio(String newCheckedId) {
        CartResult cartResult = CartEngine.getInstance(CartFrom.DEFAULT_CLIENT).checkSubmitItems();
        GlobalConfig gc = GlobalConfigInfo.getInstance().getGlobalConfig();
        if (gc != null && gc.getDouble11ShopCart() != null && gc.getDouble11ShopCart().isBoolShopCartMergeOrders()) {
            if (!cartResult.isSuccess()) {
                return clearAllOtherShopSellerChecked(newCheckedId, mContext.getString(R.string.newcart_only_support));
            } else {
                return clearChaoshiShopOrOtherShopChecked(newCheckedId);
            }
        } else if (!cartResult.isSuccess()) {
            return clearAllOtherShopSellerChecked(newCheckedId, mContext.getString(R.string.newcart_only_support));
        } else if (Config.isAgreementPay()) {//协议支付忽略全局配置
            return clearChaoshiShopOrOtherShopChecked(newCheckedId);
        }
        return false;
//        else {
//            return clearAllOtherShopSellerChecked(newCheckedId, mContext.getString(R.string.newcart_only_support_same_shop));
//        }
    }

    /**
     * 取得大小的资源值
     *
     * @param resId
     * @return
     */
    private int getDimensionPixelSize(int resId) {
        return mContext.getResources().getDimensionPixelSize(resId);
    }

    /**
     * 店铺相关的信息（仅仅是为了UI交互需要组装出来商铺信息）
     */
    private class ShopSellerItemUiData {

        int mGroupId; // 商铺的序号
        String mShopId; // 商品ID
        int mGoodsTotalCount; // 商铺里面的商品个数
        int mValidGoodsCount;//商铺里有效的商品个数
        int mInvalidGoodsCount;//商铺里无效的商品个数
        int mCheckCount; // 商铺里面结算的商品个数
        boolean mIsInValid; // 是否为无效的商品组
        private List<CartGoodsComponent> goodsList;

        public void setGoodsList(List<CartGoodsComponent> goodsList) {
            this.goodsList = goodsList;
            for (CartGoodsComponent component : goodsList) {
//                if (component.getItemComponent().isValid() && !component.getItemComponent().isPreBuyItem() && !component.getItemComponent().isPreSell()) {
                if (component.getItemComponent().isValid()) {
                    mValidGoodsCount++;
                } else {
                    mInvalidGoodsCount++;
                }
            }
        }

        //只要店铺包含任意一个有效的商品，店铺就可选
        boolean canCheck() {
            boolean canCheck = false;
            if (mValidGoodsCount > 0) {
                canCheck = true;
            }
            return canCheck;
        }

        boolean isAllChecked() {
            return mCheckCount >= mGoodsTotalCount;
        }

        //店铺下所有可选中的商品已选中
        boolean isAllValidChecked() {
            return mCheckCount >= mValidGoodsCount;
        }

        void cleanChecked() {
            mCheckCount = 0;
        }

        void deleteChecked(int num) {
            mCheckCount = mCheckCount - num;
        }

        void checkedAll() {
            mCheckCount = mGoodsTotalCount;
        }

        @Override
        public String toString() {
            String text = "[ mGroupId = " + mGroupId + ", mShopId = " + mShopId + ", mGoodsTotalCount = "
                    + mGoodsTotalCount + ", mCheckCount = " + mCheckCount + ", mIsInValid = " + mIsInValid + " ]";
            return text;
        }
    }

    /**
     * 商品的操作点击事件的监听方法
     */
    public interface OnShopItemControllerClickListener {

        void onShopItemControllerClick(ShopItemAction action, int position);

        void onShopItemControllerCheck(ShopCartGoodsBean bean, int position);
    }

    /**
     * 商品的操作点击事件的监听方法
     */
    public interface OnShopControllerClickListener {

        public void onShopControllerClick(ShopItemAction action, int position, View view, List<ItemComponent> itemComponents);
    }


    //    /**
//     * 初始化埋点信息
//     *
//     * @return
//     */
    public Map<String, String> initTBSProperty(String shopId, String spm) {

        Map<String, String> p = Utils.getProperties();
        if (!TextUtils.isEmpty(shopId)) {
            p.put("item_id", shopId);
        }

        if (!TextUtils.isEmpty(spm)) {
            p.put("spm", spm);
        }

        if (CoreApplication.getLoginHelper(mContext.getApplicationContext()).isLogin()) {
            if (!TextUtils.isEmpty(User.getUserId())) {
                p.put("user_id", User.getUserId());
            }
        }
        return p;

    }
}