package com.yunos.tvtaobao.search.bean;

/**
 * Created by huangdaju on 17/6/15.
 */

public class GridItem {

    public static final int DISPATCH_WORD = 0;
    public static final int DISPATCH_CLEAR = 1;
    public static final int DISPATCH_DELETE = 2;
    public int dispatchMode = DISPATCH_WORD;

    public String center = null;
    public String left = null;
    public String top = null;
    public String right = null;
    public String bottom = null;
    public String tipWords = null;
    public String value = null;
    public int iconRes = -1;
    public boolean isExpand = false;

    public GridItem() {
    }

    public GridItem(String n) {
        center = n;
    }

    public GridItem(int i, String tip, int d) {
        iconRes = i;
        tipWords = tip;
        dispatchMode = d;
    }

    public GridItem(String n, String l, String t, String r, String b, boolean e) {
        center = n;
        left = l;
        top = t;
        right = r;
        bottom = b;
        isExpand = e;
    }

    public GridItem(int drawableId, String n) {
        iconRes = drawableId;
        center = n;
    }

    public GridItem(int drawableId, String n, String l, String t, String r, String b, boolean e) {
        iconRes = drawableId;
        center = n;
        left = l;
        top = t;
        right = r;
        bottom = b;
        isExpand = e;
    }

    public GridItem(String tip, int d) {
        tipWords = tip;
        dispatchMode = d;
    }
}
