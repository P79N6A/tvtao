package com.yunos.tvtaobao.juhuasuan.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.tvlife.imageloader.core.assist.FailReason;
import com.tvlife.imageloader.core.listener.ImageLoadingListener;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.HomeCatesBo;
import com.yunos.tvtaobao.juhuasuan.widget.FocusedBasePositionManager.*;

public class CategoryItemView extends LinearLayout implements ItemInterface {

    public static final String TAG = "CategoryItemView";

    private ImageView categoryIcon;

    private ImageLoaderManager mImageLoaderManager;

    public CategoryItemView(Context contxt, AttributeSet attrs, int defStyle) {
        super(contxt, attrs, defStyle);
        init(contxt);
    }

    public CategoryItemView(Context contxt, AttributeSet attrs) {
        super(contxt, attrs);
        init(contxt);
    }

    public CategoryItemView(Context contxt) {
        super(contxt);
        init(contxt);
    }

    private void init(Context context) {
        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(context);
        setFocusable(true);
        View view = LayoutInflater.from(context).inflate(R.layout.jhs_category_normal_item_new, this, true);
        categoryIcon = (ImageView) view.findViewById(R.id.categoryIcon);
        // 更新图片
        //        iconBg.setBackgroundDrawable(new RoundShapeDrawable(iconBg, 8, false, Color.GRAY));
    }

    public void updateView(HomeCatesBo category) {
        if (category == null) {
            return;
        }
        if (category.getIcon() != null) {
            mImageLoaderManager.displayImage(category.getIcon(), categoryIcon, new CustomsItemIconLoader());
            setVisibility(View.VISIBLE);
        } else {
            setVisibility(View.GONE);
        }
        setTag(category);
    }

    @Override
    public int getItemWidth() {
        // TODO Auto-generated method stub
        return getWidth();
    }

    @Override
    public int getItemHeight() {
        // TODO Auto-generated method stub
        return getHeight();
    }

    @Override
    public Rect getOriginalRect() {
        // TODO Auto-generated method stub
        Rect rect = new Rect();
        rect.left = getLeft();
        rect.right = getRight();
        rect.top = getTop();
        rect.bottom = getBottom();

        return rect;
    }

    @Override
    public Rect getItemScaledRect(float scaledX, float scaledY) {
        // TODO Auto-generated method stub
        Rect rect = new Rect();
        // int[] location = new int[2];
        // getLocationOnScreen(location);

        int imgW = getWidth();
        int imgH = getHeight();

        rect.left = (int) (getLeft() - (scaledX - 1.0f) * imgW / 2 + 0.5f);
        rect.right = (int) (rect.left + imgW * scaledX - 0.5f);
        rect.top = (int) (getTop() - (scaledY - 1.0f) * imgH / 2 + 0.5f);
        rect.bottom = (int) (rect.top + imgH * scaledY - 0.5f);
        return rect;
    }

    @Override
    public boolean getIfScale() {
        return true;
    }

    AccelerateFrameInterpolator mScaleInterpolator = new AccelerateFrameInterpolator();
    AccelerateFrameInterpolator mFocusInterpolator = new AccelerateFrameInterpolator(0.5f);

    @Override
    public FrameInterpolator getFrameScaleInterpolator() {
        // TODO Auto-generated method stub
        return mScaleInterpolator;
    }

    @Override
    public FrameInterpolator getFrameFocusInterpolator() {
        // TODO Auto-generated method stub
        return mFocusInterpolator;
    }

    @Override
    public Rect getFocusPadding(Rect originalRect, Rect lastOriginalRect, int direction, int focusFrameRate) {
        // TODO Auto-generated method stub
        return null;
    }

    class CustomsItemIconLoader implements ImageLoadingListener {

        @Override
        public void onLoadingCancelled(String arg0, View arg1) {

        }

        @Override
        public void onLoadingComplete(String arg0, View view, Bitmap bitmap) {
            if (view instanceof ImageView) {
                if (bitmap != null) {
                    ((ImageView) view).setImageBitmap(bitmap);
                }
            }
        }

        @Override
        public void onLoadingFailed(String arg0, View view, FailReason reason) {

        }

        @Override
        public void onLoadingStarted(String arg0, View view) {

        }
    }

}
