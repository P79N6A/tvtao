package com.yunos.tvtaobao.cartbag.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.taobao.wireless.trade.mcart.sdk.co.biz.Icon;
import com.taobao.wireless.trade.mcart.sdk.co.biz.ItemComponent;
import com.taobao.wireless.trade.mcart.sdk.constant.CartFrom;
import com.taobao.wireless.trade.mcart.sdk.engine.CartEngine;
import com.taobao.wireless.trade.mcart.sdk.utils.CartResult;
import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.assist.ImageScaleType;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.config.Config;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.info.GlobalConfigInfo;
import com.yunos.tvtaobao.biz.widget.GroupBaseAdapter;
import com.yunos.tvtaobao.biz.widget.InnerFocusGroupHorizonalListView;
import com.yunos.tvtaobao.biz.widget.InnerFocusLayout.OnInnerItemSelectedListener;
import com.yunos.tvtaobao.cartbag.R;
import com.yunos.tvtaobao.cartbag.component.CartGoodsComponent;
import com.yunos.tvtaobao.cartbag.view.CartBuilder;
import com.yunos.tvtaobao.cartbag.view.ShopCartHintFocusLayout;
import com.yunos.tvtaobao.cartbag.view.ShopCartItemFocusLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 购物车列表的适配器
 */
public class ShopCartListAdapter extends GroupBaseAdapter {

    private final String TAG = "ShopCartListAdapter";
    private Toast toast;

    // 商品动作类型
    public enum ShopItemAction {
        CHECKED, DELETE, DELETE_ALL, DETAIL, EDIT
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
    private InnerFocusGroupHorizonalListView mFocusGroupHorizonalListView; // 购物车列表
    private int mShopItemInforDetailMargin; // 购物车详细信息的间隔
    private int mShopItemInforControllerHeight; // 购物车详细信息的高度
    private int mShopHintTextPaddingTop; // 购物车列表组文字的padding的大小
    private OnShopItemControllerClickListener mOnShopItemControllerClickListener; // 商品操作功能按钮的点击事件
    private Map<String, ShopSellerItemUiData> mCheckedShopSellerItemMap; // 加入结算商铺相关UI操作信息
    private Handler mHandler = new Handler(); // UI的Handler
    private SparseArray<View> mItemViewMap;

    //区分天猫超市结算toast文案
    private static String TM_TOAST = "天猫超市商品不能和已选商品合并下单，您可以单独购买";

    public ShopCartListAdapter(Context context, InnerFocusGroupHorizonalListView listView) {
        mContext = context;
        mFocusGroupHorizonalListView = listView;
        mItemViewMap = new SparseArray<View>();

        mCheckedShopSellerItemMap = new HashMap<String, ShopSellerItemUiData>();
        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(context);
        mImageOptions = new DisplayImageOptions.Builder().bitmapConfig(Bitmap.Config.RGB_565).imageScaleType(ImageScaleType.EXACTLY).cacheOnDisc(true).cacheInMemory(false).build();

        // 初始化里面item的大小区域
        mHintRect.set(0, 0, getDimensionPixelSize(R.dimen.ytm_shop_cart_item_hint_width),
                getDimensionPixelSize(R.dimen.ytm_shop_cart_item_hint_height));

        mShopItemRect.set(0, 0, getDimensionPixelSize(R.dimen.ytm_shop_cart_item_card_width),
                getDimensionPixelSize(R.dimen.ytm_shop_cart_item_card_height));
        mHintFocusPadding.set(getDimensionPixelSize(R.dimen.ytm_shop_cart_hint_focus_padding_left),
                getDimensionPixelSize(R.dimen.ytm_shop_cart_hint_focus_padding_top),
                getDimensionPixelSize(R.dimen.ytm_shop_cart_hint_focus_padding_right),
                getDimensionPixelSize(R.dimen.ytm_shop_cart_hint_focus_padding_bottom));
        mShopItemFocusPaddingRect.set(getDimensionPixelSize(R.dimen.ytm_shop_cart_item_focus_padding_left),
                getDimensionPixelSize(R.dimen.ytm_shop_cart_item_focus_padding_top),
                getDimensionPixelSize(R.dimen.ytm_shop_cart_item_focus_padding_right),
                getDimensionPixelSize(R.dimen.ytm_shop_cart_item_focus_padding_bottom));

        mShopItemInforDetailMargin = getDimensionPixelSize(R.dimen.dp_12_7);
        mShopItemInforControllerHeight = getDimensionPixelSize(R.dimen.ytm_shop_cart_item_controller_height);
        mShopHintTextPaddingTop = getDimensionPixelSize(R.dimen.ytm_shop_cart_item_infor_selected_icon_padding);

        // 店铺平台图片
        mTaobaoDrawable = mContext.getResources().getDrawable(R.drawable.ytm_shop_cart_item_taobao_hint);
        mTmallDrawable = mContext.getResources().getDrawable(R.drawable.ytm_shop_cart_item_tmall_hint);
        // 公用的文字
        mSpaceString = mContext.getString(R.string.ytm_space_text); // 空隔
        mCheckAllString = mContext.getString(R.string.ytm_shop_cart_hint_text_checked_all);
        mCancelCheckString = mContext.getString(R.string.ytm_shop_cart_text_cancel_checked);
        mCheckString = mContext.getString(R.string.ytm_shop_cart_text_checked);
        // 同一行文字不同显示效果的Span
        mPriceCountAbsoluteSizeSpan = new AbsoluteSizeSpan(mContext.getResources().getDimensionPixelSize(R.dimen.sp_18));
        mPreSellAbsoluteSizeSpan = new AbsoluteSizeSpan(mContext.getResources().getDimensionPixelSize(R.dimen.sp_26));
        mPriceCountForegroundColorSpan = new ForegroundColorSpan(mContext.getResources().getColor(
                R.color.ytm_shop_cart_infor_shop_count_text_color));
        mPriceForegroundColorSpan = new ForegroundColorSpan(mContext.getResources().getColor(
                R.color.ytm_shop_cart_infor_price_text_color));
        mValidPriceForegroundColorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.ytm_black));

        mShopItemCardWidth = mContext.getResources().getDimensionPixelSize(R.dimen.ytm_shop_cart_item_infor_card_width);
    }

    /**
     * 设置控制按钮的监听方法
     *
     * @param listener
     */
    public void setOnShopItemControllerClickListener(OnShopItemControllerClickListener listener) {
        mOnShopItemControllerClickListener = listener;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.ytm_cart_shop_hint, null);
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

    @SuppressLint("InflateParams")
    @Override
    public View getGroupItemView(int position, int groupId, int itemId, View convertView) {
        AppDebug.i(TAG, TAG + ".getGroupItemView position=" + position + ".groupId = " + groupId + ".itemId = "
                + itemId + " convertView=" + convertView);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.ytm_cart_shop_list_item, null);
            // 设置手动调整focus区域
            ShopCartItemFocusLayout layout = (ShopCartItemFocusLayout) convertView;
            // 设置监听方法
            layout.setOnInnerItemSelectedListener(mOnInnerItemSelectedListener);
        }
        // 解决重用之前已经放大过的view显示问题
        convertView.setScaleX(1.0f);
        convertView.setScaleY(1.0f);
        ShopCartItemFocusLayout layout = (ShopCartItemFocusLayout) convertView;

        updateShopItemView(position, groupId, itemId, layout);
        // 如果是当前选中的商品就展示选中的UI
        int selected = mFocusGroupHorizonalListView.getSelectedItemPosition();
        if (selected == position) {
            selectedShopItemView(true, position, layout);
        } else {
            // 默认情况下不显示控件按钮
            updateShopItemControllerView(false, groupId, itemId, layout);
        }
        mItemViewMap.put(position, convertView);
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
    public void selectedShopItemView(boolean selected, int position, ShopCartItemFocusLayout convertView) {
        int groupId = getGroupPos(position);
        int itemId = getGroupItemPos(position);
        AppDebug.v(TAG, TAG + ".selectedShopItemView.selected = " + selected + ".position = " + position
                + ".convertView = " + convertView + ", groupId = " + groupId + ", itemId = " + itemId);
        // 位置是否有效
        if (groupId >= 0 && itemId < Integer.MAX_VALUE) {
            updateShopItemControllerView(selected, groupId, itemId, convertView);
            if (selected) {
                // 自动Focus到商品里面的checked按钮上面，加个消息循环是为了让选中后View的变化完成再进行手动查找
                mHandler.post(new Runnable() {

                    public void run() {
                        mFocusGroupHorizonalListView.clearInnerFocusState();
                        mFocusGroupHorizonalListView.manualFindFocusInner(KeyEvent.KEYCODE_DPAD_UP);
                    }
                });
            }
        }
    }

    /**
     * 选中组Hint的View
     *
     * @param selected
     * @param hintView
     */
    public void selectedHintView(boolean selected, int position, ShopCartHintFocusLayout hintView) {
        AppDebug.i(TAG, "selectedHintView selected=" + selected + " position=" + position);
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
        if (groupId >= 0 && hintId == Integer.MAX_VALUE) {
            ShopSellerItemUiData itemData = getShopSellerItemUiData(groupId);

            // 是否为无效的商品组
            if (!itemData.mIsInValid) {
                if (itemData.isAllChecked()) {
                    mCheckedShopSellerItemMap.remove(itemData.mShopId);
                    // 取消所有
                    itemData.cleanChecked();
                    updateShopHintView(position, groupId, convertView);
                    setAllShopItemChecked(position, groupId, false);
                } else {
                    // 商铺里面的商口结算做互斥
                    itemData.checkedAll();
                    setAllShopItemChecked(position, groupId, true);
//                    addCheckedShopCount(groupId, itemData.mShopId);
//                    clearAllOtherShopSellerChecked(itemData.mShopId);
                    if (setCheckedOrRadio(itemData.mShopId)) {
                        itemData.cleanChecked();
                        setAllShopItemChecked(position, groupId, false);
//                        cutCheckedShopCount(groupId, itemData.mShopId);
                    } else {
                        // 设置所有
                        setAllShopItemChecked(position, groupId, false);
                        mCheckedShopSellerItemMap.put(itemData.mShopId, itemData);
                        AppDebug.v(TAG, TAG + ".itemData.goodsList.size = " + itemData.goodsList.size());
                        if (itemData.goodsList != null && itemData.goodsList.size() > 0) {
                            for (int i = 0; i < itemData.goodsList.size(); i++) {
                                AppDebug.v(TAG, TAG + ".isPreBuyItem = " + itemData.goodsList.get(i).getItemComponent().isPreBuyItem() +
                                        ".isPreSell = " + itemData.goodsList.get(i).getItemComponent().isPreSell());
                                if (itemData.goodsList.get(i) != null && (itemData.goodsList.get(i).getItemComponent().isPreBuyItem() ||
                                        itemData.goodsList.get(i).getItemComponent().isPreSell())) {
                                    if (itemData.goodsList.get(i).getItemComponent().isValid()) {
                                        updateShopHintView(position, groupId, convertView);
                                        setAllShopItemChecked(position, groupId, true);
                                    } else {
                                        showToast(itemData.goodsList.get(i).getItemComponent().getCodeMsg());
                                        itemData.deleteChecked(1);
//                                        mCheckedShopSellerItemMap.remove(itemData.mShopId);
                                    }
                                } else {
                                    updateShopHintView(position, groupId, convertView);
                                    setAllShopItemChecked(position, groupId, true);
                                }
                            }

                        } else {
//                            updateShopHintView(position, groupId, convertView);
//                            setAllShopItemChecked(position, groupId, true);
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
        if (itemData != null && itemData.mShopId.equals(shopId)) {
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
        if (goodsData != null && goodsData.getItemComponent() != null) {
            AppDebug.v(TAG, TAG + ".setShopItemChecked.position = " + position + ".groupId = " + groupId
                    + ", itemId = " + itemId + "checked = " + checked + "isChecked = "
                    + goodsData.getItemComponent().isChecked());
            // 只有不相同的才会进行变更
//            if (checked && goodsData.getItemComponent().getBundleType().equals("combo")) {
//                goodsData.getItemComponent().setChecked(false);
//            } else if (goodsData.getItemComponent().isChecked() && goodsData.getItemComponent().getBundleType().equals("combo")) {
//                goodsData.getItemComponent().setChecked(true);
//            } else if (goodsData.getItemComponent().getBundleType().equals("combo")) {
//                goodsData.getItemComponent().setChecked(true);
//            }
            if (!goodsData.getItemComponent().isPreBuyItem()) {
                if (goodsData.getItemComponent().isPreSell()) {
                    if (goodsData.getItemComponent().isValid()) {

                    } else {
                        goodsData.getItemComponent().setChecked(true);
                    }
                } else {

                }
            } else {
                goodsData.getItemComponent().setChecked(true);
            }
//            if (!goodsData.getItemComponent().isValid() && (goodsData.getItemComponent().isPreBuyItem() || goodsData.getItemComponent().isPreSell())) {
//                goodsData.getItemComponent().setChecked(true);
//            }
            if (goodsData.getItemComponent().isChecked() != checked) {
                result = true;
                if (needClean) {
                    goodsData.getItemComponent().setChecked(checked, false);
                } else {

                    goodsData.getItemComponent().setChecked(!checked, false);
                }

                View childView = getListPositionView(position);
                // 更新当前选中的商品信息
                if (childView instanceof ShopCartItemFocusLayout) {
                    updateShopItemView(goodsData, position, groupId, itemId, (ShopCartItemFocusLayout) childView);
                }
            }
        }
        return result;
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
    private void updateShopHintView(int position, int groupId, ShopCartHintFocusLayout convertView) {
        TextView hintView = (TextView) convertView.findViewById(R.id.hint_text);
        ShopSellerItemUiData shopData = getShopSellerItemUiData(groupId);
        if (shopData != null && !shopData.mIsInValid) {
            // 有效商品组
            if (shopData.isAllChecked()) {
                hintView.setText(mCancelCheckString);
                hintView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ytm_button_common_focus));
                hintView.setCompoundDrawablesWithIntrinsicBounds(null,
                        mContext.getResources().getDrawable(R.drawable.ytm_shop_cart_hint_checked_icon), null, null);
                hintView.setPadding(0, mShopHintTextPaddingTop, 0, 0);
            } else {
                hintView.setText(mCheckAllString);
                hintView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ytm_button_common_normal));
                hintView.setCompoundDrawablesWithIntrinsicBounds(null,
                        mContext.getResources().getDrawable(R.drawable.ytm_shop_cart_hint_icon), null, null);
                hintView.setPadding(0, mShopHintTextPaddingTop, 0, 0);
            }
        } else {
            // 无效商品组
            hintView.setText(mContext.getString(R.string.ytm_shop_cart_text_invalid));
            hintView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.ytm_button_common_normal));
            hintView.setCompoundDrawablesWithIntrinsicBounds(null,
                    mContext.getResources().getDrawable(R.drawable.ytm_shop_cart_hint_delete_icon), null, null);
            hintView.setPadding(0, mShopHintTextPaddingTop, 0, 0);
        }
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
    private void updateShopItemView(CartGoodsComponent itemData, int position, int groupId, int itemId,
                                    ShopCartItemFocusLayout convertView) {
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
        TextView sellerView = (TextView) convertView.findViewById(R.id.shop_item_seller_name);
        // 是否有效
        if (itemId == 0 && itemData.getItemComponent() != null && (itemData.getItemComponent().isValid() || itemData.getItemComponent().isPreBuyItem()
                || itemData.getItemComponent().isPreSell())
                && itemData.getShopComponent() != null) {
            sellerView.setText(itemData.getShopComponent().getTitle());
            if (itemData.getItemComponent() != null) {
                String toBuy = itemData.getItemComponent().getToBuy();
                // 淘宝天猫标识图片
                if (toBuy != null && toBuy.equalsIgnoreCase("tmall")) {
                    sellerView.setCompoundDrawablesWithIntrinsicBounds(mTmallDrawable, null, null, null);
                } else {
                    if (itemData.getShopComponent() != null && itemData.getShopComponent().getType() != null
                            && itemData.getShopComponent().getType().equals("B")) {
                        sellerView.setCompoundDrawablesWithIntrinsicBounds(mTmallDrawable, null, null, null);
                    } else {
                        sellerView.setCompoundDrawablesWithIntrinsicBounds(mTaobaoDrawable, null, null, null);
                    }
                }
            }
            sellerView.setVisibility(View.VISIBLE);
        } else {
            sellerView.setVisibility(View.INVISIBLE);
        }

        // 商品的图片
        ImageView imageView = (ImageView) convertView.findViewById(R.id.shop_item_image);
        imageView.setImageDrawable(null);
        String imageUrl = getShopItemImageUrl(infoComponent.getPic());
        mImageLoaderManager.displayImage(imageUrl, imageView, mImageOptions);

        // 选中的标识图片(判断是否为选中状态)
        ImageView checkedHint = (ImageView) convertView.findViewById(R.id.shop_item_check_hint);
        if (itemData.getItemComponent() != null) {
            AppDebug.v(TAG, TAG + ".updateShopItemView.isChecked = " + itemData.getItemComponent().isChecked()
                    + ", position = " + position);
        }
        if (itemData.getItemComponent() != null && itemData.getItemComponent().isChecked()) {
            if (itemData.getItemComponent().isPreBuyItem()) {
                if (itemData.getItemComponent().isPreSell()) {
                    if (itemData.getItemComponent().isValid()) {
                        checkedHint.setVisibility(View.VISIBLE);
                    } else {
                        checkedHint.setVisibility(View.GONE);
                    }
                } else {
                    checkedHint.setVisibility(View.GONE);
                }
            } else {
                checkedHint.setVisibility(View.VISIBLE);
            }
        } else {
            checkedHint.setVisibility(View.GONE);
        }

        // 商品的名称
        TextView shopName = (TextView) convertView.findViewById(R.id.shop_item_name);
        shopName.setText(infoComponent.getTitle());

        // 商品的SKU信息
        TextView shopSku = (TextView) convertView.findViewById(R.id.shop_item_sku_info);
        if (itemData.getItemComponent().getSku() != null) {
            shopSku.setText(itemData.getItemComponent().getSku().getTitle());
        } else {
            shopSku.setText("");
        }

        // 商品的价格信息(价格跟数量显示不同的UI)
        TextView priceView = (TextView) convertView.findViewById(R.id.shop_item_price);
        if (itemData.getItemComponent().getItemPay() != null && itemData.getItemComponent().getItemQuantity() != null) {
            String countString = "x" + itemData.getItemComponent().getItemQuantity().getQuantity();
            String price = itemData.getItemComponent().getItemPay().getNowTitle();
            String priceString = price + mSpaceString + countString;
            int countStringIndex = priceString.length() - countString.length();
            SpannableStringBuilder style = new SpannableStringBuilder(priceString);
            if (itemData.getItemComponent() != null && (itemData.getItemComponent().isValid() || itemData.getItemComponent().isPreBuyItem()
                    || itemData.getItemComponent().isPreSell())) {
                style.setSpan(mPriceForegroundColorSpan, 0, countStringIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            } else {
                style.setSpan(mValidPriceForegroundColorSpan, 0, countStringIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            if (price.contains("预售价")) {
                style.setSpan(mPreSellAbsoluteSizeSpan, 0, 3,
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
            style.setSpan(mPriceCountAbsoluteSizeSpan, countStringIndex, priceString.length(),
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            style.setSpan(mPriceCountForegroundColorSpan, countStringIndex, priceString.length(),
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            priceView.setText(style);
        } else {
            priceView.setText("");
        }

        // 失效商品
        View validView = convertView.findViewById(R.id.shop_item_invalid_layout);
        if (itemData.getItemComponent() != null && (itemData.getItemComponent().isValid() || itemData.getItemComponent().isPreBuyItem()
                || itemData.getItemComponent().isPreSell())) {
            validView.setVisibility(View.GONE);
        } else {
            validView.setVisibility(View.VISIBLE);
        }

        // 商品的状态信息，库存紧张
        TextView statusIconView = (TextView) convertView.findViewById(R.id.shop_item_state);
        statusIconView.setVisibility(View.GONE);
        HashMap<String, List<Icon>> iconComponent = itemData.getItemComponent().getBizIcon();
        if (iconComponent != null && iconComponent.size() > 0) {
            List<Icon> iconItem = iconComponent.get(0);
            if (iconItem != null) {
                for (int i = 0; i < iconItem.size(); i++) {
                    if (iconItem != null && iconItem.get(i) != null) {
                        String netString = iconItem.get(i).getText().trim();
                        // 对状态提示字符串做限制
                        String text = null;
                        // 使用字符来匹配，这段代码太戳但是PD一定要加。
                        String costDownNextText = mContext.getString(R.string.ytm_shop_cart_item_cost_down_net_text);
                        if (netString.contains(costDownNextText)) {
                            // 修改成商品降价
                            text = mContext.getString(R.string.ytm_shop_cart_item_cost_down_text);
                        } else if (netString.compareTo(mContext.getString(R.string.ytm_shop_cart_item_tight)) == 0) {
                            // 库存紧张
                            text = netString;
                        }
                        if (!TextUtils.isEmpty(text)) {
                            statusIconView.setVisibility(View.VISIBLE);
                            statusIconView.setText(text);
                        }
                    }
                }
            }
        }
    }

    /**
     * 更新UI信息
     *
     * @param position
     * @param groupId
     * @param itemId
     * @param convertView
     */
    private void updateShopItemView(int position, int groupId, int itemId, ShopCartItemFocusLayout convertView) {
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
    private void updateShopItemControllerView(boolean show, int groupId, int itemId, ShopCartItemFocusLayout convertView) {
        AppDebug.i(TAG, "updateShopItemControllerView show = " + show + ", groupId = " + groupId + ", itemId = "
                + itemId + ", convertView = " + convertView);
        final CartGoodsComponent itemData = getShopItem(groupId, itemId);
        if (itemData == null) {
            return;
        }
        if (itemData.getItemComponent() == null) {
            return;
        }

        ViewGroup controller = (ViewGroup) convertView.findViewById(R.id.shop_item_controller);
        ViewGroup invalidController = (ViewGroup) convertView.findViewById(R.id.shop_item_invalid_controll_layout);
        TextView extraView = (TextView) convertView.findViewById(R.id.shop_exa);
        //有效商品
        if (itemData.getItemComponent().isValid() || itemData.getItemComponent().isPreBuyItem() || itemData.getItemComponent().isPreSell()) {
            // 隐藏失效商品的操作按钮
            invalidController.setVisibility(View.GONE);
            ViewGroup inforDetailLayout = (ViewGroup) convertView.findViewById(R.id.shop_item_detail_info_layout);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) inforDetailLayout.getLayoutParams();
            if (show) {
                layoutParams.setMargins(0, 0, 0, mShopItemInforControllerHeight);
                controller.setVisibility(View.VISIBLE);
                // 选择按钮
                TextView shopChecked = (TextView) controller.findViewById(R.id.shop_item_checked);
                convertView.setFirstFocusView(shopChecked);
                if (itemData != null && itemData.getItemComponent() != null && itemData.getItemComponent().isChecked()) {
                    shopChecked.setText(mCancelCheckString);
                } else {
                    shopChecked.setText(mCheckString);
                }

                if (itemData.getItemComponent().isPreSell() && itemData.getItemComponent().getItemExtra() != null && itemData.getItemComponent().getItemExtra().getData() != null) {
                    com.alibaba.fastjson.JSONObject jsonObject = itemData.getItemComponent().getItemExtra().getData();
                    String preSellTips = jsonObject.getString("preSellTips");
                    if (preSellTips != null) {
                        extraView.setText(preSellTips);
                        if (itemData.getItemComponent().isValid()) {
                            extraView.setVisibility(View.VISIBLE);
                        } else if (itemData.getItemComponent().isPreBuyItem()) {
                            extraView.setVisibility(View.VISIBLE);
                        } else {
                            extraView.setVisibility(View.GONE);
                        }
                    } else {
                        extraView.setVisibility(View.GONE);
                    }
                }
                shopChecked.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        AppDebug.i(TAG, "checked click view=" + v);
                        if (mOnShopItemControllerClickListener != null) {
                            if (!itemData.getItemComponent().isPreBuyItem()) {
                                if (itemData.getItemComponent().isPreSell()) {
                                    if (itemData.getItemComponent().isValid()) {
                                        mOnShopItemControllerClickListener.onShopItemControllerClick(ShopItemAction.CHECKED,
                                                mFocusGroupHorizonalListView.getSelectedItemPosition());
//                                        if (itemData.getItemComponent().getBundleType().equals("combo")) {
//
//                                        }
                                    } else {
                                        showToast(itemData.getItemComponent().getCodeMsg());
                                    }
                                } else {
                                    mOnShopItemControllerClickListener.onShopItemControllerClick(ShopItemAction.CHECKED,
                                            mFocusGroupHorizonalListView.getSelectedItemPosition());
                                }
                            } else {
                                showToast(itemData.getItemComponent().getCodeMsg());
                            }
                        }
                    }
                });

                // 删除按钮
                TextView delete = (TextView) controller.findViewById(R.id.shop_item_delete);
                // 默认无效
                delete.setFocusable(false);
                // 详情
                TextView detail = (TextView) controller.findViewById(R.id.shop_item_detail);
                detail.setFocusable(true);
                detail.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        AppDebug.i(TAG, "detail click view=" + v);
                        tbsClicked("Detail", itemData);
                        if (mOnShopItemControllerClickListener != null) {
                            mOnShopItemControllerClickListener.onShopItemControllerClick(ShopItemAction.DETAIL,
                                    mFocusGroupHorizonalListView.getSelectedItemPosition());
                        }
                    }
                });
                // 编辑
                TextView edit = (TextView) controller.findViewById(R.id.shop_item_edit);
                // 默认无效
                edit.setFocusable(false);
                // 编辑跟删除是否有效按服务的数据来确定
                if (itemData.getItemComponent().getItemOperate() != null
                        && itemData.getItemComponent().getItemOperate() != null) {
                    List<String> poerateList = itemData.getItemComponent().getItemOperate();
                    for (String operate : poerateList) {
                        String name = operate;
                        AppDebug.i(TAG, "operate name=" + name + " action=" + operate);
                        // 编辑
                        if (name.equals("edit")) {
                            edit.setEnabled(true);
                            edit.setFocusable(true);
                            edit.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    AppDebug.i(TAG, "edit click view=" + v);
                                    tbsClicked("Edit", itemData);
                                    if (mOnShopItemControllerClickListener != null) {
                                        mOnShopItemControllerClickListener.onShopItemControllerClick(
                                                ShopItemAction.EDIT,
                                                mFocusGroupHorizonalListView.getSelectedItemPosition());
                                    }
                                }
                            });
                        } else if (name.equals("delete")) {
                            // 删除
                            delete.setFocusable(true);
                            delete.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    AppDebug.i(TAG, "delete click view=" + v);
                                    tbsClicked("Delete", itemData);
                                    if (mOnShopItemControllerClickListener != null) {
                                        mOnShopItemControllerClickListener.onShopItemControllerClick(
                                                ShopItemAction.DELETE,
                                                mFocusGroupHorizonalListView.getSelectedItemPosition());
                                    }
                                }
                            });
                        }
                    }
                }
            } else {
                layoutParams.setMargins(0, 0, 0, mShopItemInforDetailMargin);
                controller.setVisibility(View.GONE);
                extraView.setVisibility(View.GONE);
            }
        } else {
            // 无效商品
            // 隐藏有效商品的操作按钮
            controller.setVisibility(View.GONE);
            if (show) {
                invalidController.setVisibility(View.VISIBLE);
                // 删除按钮
                TextView btn2 = (TextView) invalidController.findViewById(R.id.shop_item_invalid_btn2);
                // 默认无效
                btn2.setFocusable(false);
                // 编辑跟删除是否有效按服务的数据来确定
                if (itemData.getItemComponent().getItemOperate() != null
                        && itemData.getItemComponent().getItemOperate() != null) {
                    List<String> poerateList = itemData.getItemComponent().getItemOperate();
                    for (String operate : poerateList) {
                        String name = operate;
                        AppDebug.i(TAG, "invalid operate name=" + name + " action=" + operate);
                        if (name.equals("delete")) {
                            // 删除
                            btn2.setText(mContext.getString(R.string.ytm_shop_cart_text_delete));
                            btn2.setFocusable(true);
                            btn2.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    AppDebug.i(TAG, "invalid delete click view=" + v);
                                    if (mOnShopItemControllerClickListener != null) {
                                        mOnShopItemControllerClickListener.onShopItemControllerClick(
                                                ShopItemAction.DELETE,
                                                mFocusGroupHorizonalListView.getSelectedItemPosition());
                                    }
                                }
                            });
                        }
                    }
                }
                // 如果删除有效，那个btn1就是详情
                if (btn2.isFocusable()) {
                    // 详情
                    TextView btn1 = (TextView) invalidController.findViewById(R.id.shop_item_invalid_btn1);
                    btn1.setVisibility(View.VISIBLE);
                    btn1.setFocusable(true);
                    btn1.setText(mContext.getString(R.string.ytm_shop_cart_text_detail));
                    btn1.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            AppDebug.i(TAG, "invalid detail click view=" + v);
                            if (mOnShopItemControllerClickListener != null) {
                                mOnShopItemControllerClickListener.onShopItemControllerClick(ShopItemAction.DETAIL,
                                        mFocusGroupHorizonalListView.getSelectedItemPosition());
                            }
                        }
                    });
                } else {
                    // 如果删除无效，那么btn2就是详情，并且隐藏btn1
                    btn2.setFocusable(true);
                    btn2.setText(mContext.getString(R.string.ytm_shop_cart_text_detail));
                    btn2.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            AppDebug.i(TAG, "invalid detail click view=" + v);
                            if (mOnShopItemControllerClickListener != null) {
                                mOnShopItemControllerClickListener.onShopItemControllerClick(ShopItemAction.DETAIL,
                                        mFocusGroupHorizonalListView.getSelectedItemPosition());
                            }
                        }
                    });
                    // 隐藏btn1
                    TextView btn1 = (TextView) invalidController.findViewById(R.id.shop_item_invalid_btn1);
                    btn1.setVisibility(View.INVISIBLE);
                }

                convertView.setFirstFocusView(btn2);

            } else {
                invalidController.setVisibility(View.GONE);
            }
        }
    }

    private void showToast(String codeMsg) {
        if (toast == null) {
            toast = Toast.makeText(mContext, "",
                    Toast.LENGTH_SHORT);
        }
        if (!TextUtils.isEmpty(codeMsg)) {
            toast.setText(codeMsg);
            toast.show();
        }
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
                    - mFocusGroupHorizonalListView.getFirstPosition());
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
            if (goodsList != null) {
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
            itemData.goodsList = goodsList;
            if (key.equals("invalid")) {
                itemData.mIsInValid = true;
            }

            // 保存当前已经加入结算的商品
            if (itemData.mCheckCount > 0) {
                mCheckedShopSellerItemMap.put(itemData.mShopId, itemData);

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
                // 不同店铺选择商品时给出提示
                if (hasDiffSellor) {

                }
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
        if (hasDiffSellor) {

        }
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
            return clearChaoshiShopOrOtherShopChecked(newCheckedId);
        } else if (!cartResult.isSuccess()) {
            return clearAllOtherShopSellerChecked(newCheckedId, mContext.getString(R.string.ytm_shop_cart_only_support));
//            clearChaoshiShopOrOtherShopChecked(newCheckedId);
        } else if (Config.isAgreementPay()) {//协议支付忽略全局配置
            return clearChaoshiShopOrOtherShopChecked(newCheckedId);
        }
        return false;
//        else {
//            return clearAllOtherShopSellerChecked(newCheckedId, mContext.getString(R.string.ytm_shop_cart_only_support_same_shop));
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
     * 内部选中的监听方法
     */
    private OnInnerItemSelectedListener mOnInnerItemSelectedListener = new OnInnerItemSelectedListener() {

        @Override
        public void onInnerItemSelected(View view, boolean isSelected, View parentView) {
            AppDebug.i(TAG, "inner selected view=" + view + " isSelected=" + isSelected);
            // 选中时更新不同的状态

            int i = view.getId();
            if (i == R.id.shop_item_checked) {
                TextView checkedView = (TextView) view;
                if (isSelected) {
                    checkedView.setTextColor(mContext.getResources().getColor(R.color.ytm_white));
                    checkedView.setBackgroundColor(mContext.getResources().getColor(R.color.ytm_button_focus));
                } else {
                    checkedView.setTextColor(mContext.getResources().getColor(
                            R.color.ytm_shop_cart_item_controller_btn_color));
                    checkedView.setBackgroundDrawable(null);
                }


            } else if (i == R.id.shop_item_delete) {
                TextView deleteView = (TextView) view;
                if (isSelected) {
                    deleteView.setBackgroundColor(mContext.getResources().getColor(R.color.ytm_button_focus));
                    deleteView.setText(mContext.getString(R.string.ytm_shop_cart_text_delete));
                } else {
                    deleteView.setBackgroundDrawable(mContext.getResources().getDrawable(
                            R.drawable.ytm_shop_cart_item_delete));
                    deleteView.setText("");
                }


            } else if (i == R.id.shop_item_detail) {
                TextView detailView = (TextView) view;
                if (isSelected) {
                    detailView.setBackgroundColor(mContext.getResources().getColor(R.color.ytm_button_focus));
                    detailView.setText(mContext.getString(R.string.ytm_shop_cart_text_detail));
                } else {
                    detailView.setBackgroundDrawable(mContext.getResources().getDrawable(
                            R.drawable.ytm_shop_cart_item_detail));
                    detailView.setText("");
                }


            } else if (i == R.id.shop_item_edit) {
                TextView editView = (TextView) view;
                if (isSelected) {
                    editView.setBackgroundColor(mContext.getResources().getColor(R.color.ytm_button_focus));
                    editView.setText(mContext.getString(R.string.ytm_shop_cart_text_edit));
                } else {
                    editView.setBackgroundDrawable(mContext.getResources().getDrawable(
                            R.drawable.ytm_shop_cart_item_edit));
                    editView.setText("");
                }

            } else if (i == R.id.shop_item_invalid_btn2) {
                TextView btn2 = (TextView) view;
                if (isSelected) {
                    btn2.setTextColor(mContext.getResources().getColor(R.color.ytm_white));
                    btn2.setBackgroundColor(mContext.getResources().getColor(R.color.ytm_button_focus));
                } else {
                    btn2.setTextColor(mContext.getResources().getColor(
                            R.color.ytm_shop_cart_item_controller_btn_color));
                    btn2.setBackgroundColor(mContext.getResources().getColor(R.color.ytm_white));
                }

            } else if (i == R.id.shop_item_invalid_btn1) {
                TextView btn1 = (TextView) view;
                if (isSelected) {
                    btn1.setTextColor(mContext.getResources().getColor(R.color.ytm_white));
                    btn1.setBackgroundColor(mContext.getResources().getColor(R.color.ytm_button_focus));
                } else {
                    btn1.setTextColor(mContext.getResources().getColor(
                            R.color.ytm_shop_cart_item_controller_btn_color));
                    btn1.setBackgroundColor(mContext.getResources().getColor(R.color.ytm_white));
                }

            }
        }
    };

    /**
     * 店铺相关的信息（仅仅是为了UI交互需要组装出来商铺信息）
     */
    private class ShopSellerItemUiData {

        int mGroupId; // 商铺的序号
        String mShopId; // 商品ID
        int mGoodsTotalCount; // 商铺里面的商品个数
        int mCheckCount; // 商铺里面结算的商品个数
        boolean mIsInValid; // 是否为无效的商品组
        List<CartGoodsComponent> goodsList;

        boolean isAllChecked() {
            return mCheckCount >= mGoodsTotalCount;
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

        public void onShopItemControllerClick(ShopItemAction action, int position);
    }
}