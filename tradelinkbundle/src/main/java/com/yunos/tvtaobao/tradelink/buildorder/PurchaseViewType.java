package com.yunos.tvtaobao.tradelink.buildorder;

import com.google.zxing.client.result.VINParsedResult;

public enum PurchaseViewType {

    BRIDGE           (0),
    CASCADE          (1),
    DATEPICKER       (2),
    DYNAMIC          (3),
    INPUT            (4),
    LABEL            (5),
    MULTISELECT      (6),
    SELECT           (7),
    TABLE            (8),
    TOGGLE           (9),

    ACTIVITY        (10),
    ADDRESS         (11),
    DELIVERY_METHOD (12),
    ITEM_INFO       (13),
    INSTALLMENT     (14),
    INVALID_GROUP   (15),
    ORDER_INFO      (16),
    ORDER_GROUP     (17),
    ORDER_PAY       (18),
    QUANTITY        (19),
    TERMS           (20),
    TIPS            (21),


    LINE            (22),
    REAL_PAY        (23),
    ITEM_PAY        (24),
    PROMOTION       (25),

    COUPON          (26),
    VOUCHER         (27),
    CARD_PROMOTION  (28),
    VIP_88           (29),//88vip


    INSTALLMENT_PICKER(30),
    INSTALLMENT_TOGGLE(31),

    UNKNOWN         (-1);

    private int index;

    private PurchaseViewType(int index) {
        this.index = index;
    }

    public static PurchaseViewType getPurchaseViewTypeByIndex(int index) {
        return PurchaseViewType.values()[index];
    }

    public static int size() {
        return PurchaseViewType.values().length - 1;
    }

    public int getIndex() {
        return index;
    }

}
