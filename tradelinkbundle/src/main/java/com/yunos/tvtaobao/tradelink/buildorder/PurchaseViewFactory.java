package com.yunos.tvtaobao.tradelink.buildorder;


import android.content.Context;

import com.taobao.wireless.trade.mbuy.sdk.co.Component;
import com.taobao.wireless.trade.mbuy.sdk.co.ComponentTag;
import com.taobao.wireless.trade.mbuy.sdk.co.ComponentType;
import com.yunos.tvtaobao.tradelink.buildorder.component.GoodsSyntheticComponent;
import com.yunos.tvtaobao.tradelink.buildorder.component.Vip88CardComponent;
import com.yunos.tvtaobao.tradelink.buildorder.view.CardPromotionViewHolder;
import com.yunos.tvtaobao.tradelink.buildorder.view.CheckBoxViewHolder;
import com.yunos.tvtaobao.tradelink.buildorder.view.CouponViewHolder;
import com.yunos.tvtaobao.tradelink.buildorder.view.DeliverySelectViewHolder;
import com.yunos.tvtaobao.tradelink.buildorder.view.EntryViewHolder;
import com.yunos.tvtaobao.tradelink.buildorder.view.FakeViewHolder;
import com.yunos.tvtaobao.tradelink.buildorder.view.GiftViewHolder;
import com.yunos.tvtaobao.tradelink.buildorder.view.InputViewHolder;
import com.yunos.tvtaobao.tradelink.buildorder.view.InstallmentToggleViewHolder;
import com.yunos.tvtaobao.tradelink.buildorder.view.PurchaseViewHolder;
import com.yunos.tvtaobao.tradelink.buildorder.view.SelectViewHolder;
import com.yunos.tvtaobao.tradelink.buildorder.view.TermsViewHolder;
import com.yunos.tvtaobao.tradelink.buildorder.view.VoucherViewHolder;

public class PurchaseViewFactory {

    public static PurchaseViewHolder make(int index, Context mContext) {
        if (index >= PurchaseViewType.size() || index < 0) {
            return null;
        }

        PurchaseViewType type = PurchaseViewType.getPurchaseViewTypeByIndex(index);
        switch (type) {
            case INPUT:
                return new InputViewHolder(mContext);
            case SELECT:
                return new SelectViewHolder(mContext);
            case TOGGLE:
                return new CheckBoxViewHolder(mContext);
            case ACTIVITY:
                return new GiftViewHolder(mContext);
            case COUPON:
                return new CouponViewHolder(mContext);
            case DELIVERY_METHOD:
                return new DeliverySelectViewHolder(mContext);
            case VOUCHER:
                return new VoucherViewHolder(mContext);
            case TERMS:
                return new TermsViewHolder(mContext);
            case INSTALLMENT_PICKER:
                return new EntryViewHolder(mContext);
            case VIP_88:
                return new EntryViewHolder(mContext);
            case INSTALLMENT_TOGGLE:
                return new InstallmentToggleViewHolder(mContext);
            case CARD_PROMOTION:
                return new CardPromotionViewHolder(mContext);
            default:
                return new FakeViewHolder(mContext);
        }
    }

    public static int getItemViewType(Component component) {
        ComponentType type = component.getType();
        ComponentTag tag = ComponentTag.getComponentTagByDesc(component.getTag());

        switch (type) {
            case BRIDGE:
                return PurchaseViewType.BRIDGE.getIndex();
            case CASCADE:
                return PurchaseViewType.CASCADE.getIndex();
            case DATEPICKER:
                return PurchaseViewType.DATEPICKER.getIndex();
            case INPUT:
                return PurchaseViewType.INPUT.getIndex();
            case LABEL:
                return PurchaseViewType.LABEL.getIndex();
            case MULTISELECT:
                return PurchaseViewType.MULTISELECT.getIndex();
            case SELECT: {
                if ("voucher".equals(component.getTag()))
                    return PurchaseViewType.VOUCHER.getIndex();
                if ("taxCoupon".equals(component.getTag()))
                    return PurchaseViewType.VOUCHER.getIndex();
                return PurchaseViewType.SELECT.getIndex();
            }
            case TABLE:
                return PurchaseViewType.TABLE.getIndex();
            case TIPS:
                return PurchaseViewType.TIPS.getIndex();
            case TOGGLE:
                return PurchaseViewType.TOGGLE.getIndex();
            case BIZ:
                switch (tag) {
                    case ACTIVITY:
                        return PurchaseViewType.ACTIVITY.getIndex();
                    case ADDRESS:
                        return PurchaseViewType.ADDRESS.getIndex();
                    case DELIVERY_METHOD:
                        return PurchaseViewType.DELIVERY_METHOD.getIndex();
                    case INSTALLMENT:
                        return PurchaseViewType.INSTALLMENT.getIndex();
                    case INVALID_GROUP:
                        return PurchaseViewType.INVALID_GROUP.getIndex();
                    case ORDER_INFO:
                        return PurchaseViewType.ORDER_INFO.getIndex();
                    case ORDER_GROUP:
                        return PurchaseViewType.ORDER_GROUP.getIndex();
                    case ORDER_PAY:
                        return PurchaseViewType.ORDER_PAY.getIndex();
                    case QUANTITY:
                        return PurchaseViewType.QUANTITY.getIndex();
                    case TERMS:
                        return PurchaseViewType.TERMS.getIndex();
                    case ITEM_INFO:
                        return PurchaseViewType.ITEM_INFO.getIndex();
                    //TODO improve
                    case COUPON:
                        return PurchaseViewType.COUPON.getIndex();
                    case INSTALLMENT_PICKER:
                        return PurchaseViewType.INSTALLMENT_PICKER.getIndex();
                    case INSTALLMENT_TOGGLE:
                        return PurchaseViewType.INSTALLMENT_TOGGLE.getIndex();
                    //END TODO
                    case REAL_PAY:
                        return PurchaseViewType.REAL_PAY.getIndex();
                    case ITEM_PAY:
                        return PurchaseViewType.ITEM_PAY.getIndex();
                    default:
                        break;
                }
                break;
            case SYNTHETIC:
                if (component instanceof GoodsSyntheticComponent) {
                    return PurchaseViewType.ITEM_INFO.getIndex();
                } else if (component instanceof Vip88CardComponent) {
                    return PurchaseViewType.VIP_88.getIndex();
                }
                break;
            case RICHSELECT:
                if ("voucher".equals(component.getTag())) {
                    return PurchaseViewType.VOUCHER.getIndex();
                } else if ("cardPromotion".equals(component.getTag())) {
                    return PurchaseViewType.CARD_PROMOTION.getIndex();
                }

            default:
                break;
        }

        return PurchaseViewType.UNKNOWN.getIndex();
    }

}
