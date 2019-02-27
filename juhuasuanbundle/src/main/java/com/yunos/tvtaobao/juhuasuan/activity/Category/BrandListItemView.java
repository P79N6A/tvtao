package com.yunos.tvtaobao.juhuasuan.activity.Category;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;
import com.yunos.tvtaobao.juhuasuan.view.FocusedRelativeLayout;

/**
 * 一般的商品显示信息 1.本商品展示信息 2.带有是否可以前后聚焦的信息 3.商品的图片跟其它信息是分开加载，为提高动画的速度
 * @author tim
 */
public class BrandListItemView extends FocusedRelativeLayout {

    private static final String TAG = "TAG";

    /**
     * 构造方法
     * @param context
     * @param attrs
     */
    public BrandListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setFrameRate(2);
        LayoutInflater.from(context).inflate(R.layout.jhs_category_goods_normal_item, this, true);
        setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                AppDebug.i(TAG, "onFocusChange=" + hasFocus);
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        AppDebug.i(TAG, "ItemListGoodsNormalItemView onLayout changed=" + changed);
        if (changed) {
            super.onLayout(changed, l, t, r, b);
        }
    }

    // @Override
    // protected void dispatchDraw(Canvas canvas) {
    // super.dispatchDraw(canvas);
    // //将加载完成后的信息bitmap显示在商品里面
    //
    // }

    /**
     * 更新图片(只有在图片还未加载过的时候显示)
     */
    public void refreshImage() {
    }

    /**
     * 创建商品的信息
     */
    private void createGoodsInfor() {
    }

    /**
     * 刷新商品的信息
     */
    public void refreshGoodsInfo() {
        AppDebug.i(TAG, "refreshGoodsInfo mGoodsInfoShowed=");
        // 未显示

    }

    /**
     * 强制刷新商品信息
     * @param itemData
     * @param position
     */
    public void enforceRefreshGoodsInfo(ItemMO itemData, int position) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                return true;
            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * 商品信息的请求数据
     * @author tim
     */
    public class GoodsNormalItemViewInfoRequestData {

        public Long mCategoryId; // 商品分类的ID
        public int mWith; // 商品的宽度
        public int mHeight; // 商品的高度
        public ItemMO mItemData; // 商品的信息数据
    }
}
