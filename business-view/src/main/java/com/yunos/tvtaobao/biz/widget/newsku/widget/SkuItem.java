package com.yunos.tvtaobao.biz.widget.newsku.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

import com.yunos.tvtaobao.businessview.R;


/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/6/8
 *     desc :
 *     version : 1.0
 * </pre>
 */

@SuppressLint("AppCompatCustomView")
public class SkuItem extends TextView {
    private long valueId;
    private State currentState = State.UNSELECT;
    private Bitmap selectFocused, selectUnfocus, disableUnfocusBG;

    private int pading = getResources().getDimensionPixelSize(R.dimen.dp_1);

    /**
     * DEFAULT 默认
     * UNSELECT 未选择
     * SELECT 已选择
     **/
    enum State {
        UNSELECT, SELECT, DISABLE
    }

    public SkuItem(Context context) {
        super(context);
        initView(context);
    }

    public SkuItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SkuItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initBitmap() {
        selectFocused = BitmapFactory.decodeResource(getResources(), R.drawable.iv_sku_item_select_focused);
        selectUnfocus = BitmapFactory.decodeResource(getResources(), R.drawable.iv_sku_item_select_unfocus);
        disableUnfocusBG = BitmapFactory.decodeResource(getResources(), R.drawable.iv_sku_item_disable_bg);
    }

    private void initView(final Context context) {
        initBitmap();
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.END);
        setBackgroundResource(R.drawable.bg_sku_item_unfocused_enable);
        setTextColor(Color.parseColor("#202020"));
    }

    /**
     * 设置valueId
     **/
    public void setValueId(long vid) {
        this.valueId = vid;
    }

    public long getValueId() {
        return valueId;
    }

    /**
     * 设置item的选择状态。
     **/
    public void setState(State state) {
        if (currentState != state) {
            this.currentState = state;
            updateBackground(isFocused());
            invalidate();
        }
    }

    public State getCurrentState() {
        return currentState;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        startMarquee(gainFocus);
        updateBackground(gainFocus);
    }

    /**
     * 更新非聚焦情况下
     */
    private void updateBackground(boolean isFocused) {
        if (isFocused) {
            setBackgroundResource(R.drawable.bg_sku_item_focused_color);
            setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            switch (currentState) {
                case SELECT:
                case UNSELECT:
                    setBackgroundResource(R.drawable.bg_sku_item_unfocused_enable);
                    setTextColor(Color.parseColor("#202020"));
                    break;
                case DISABLE:
                    setBackgroundResource(R.drawable.bg_sku_item_unfocused_disable);
                    setTextColor(Color.parseColor("#afafaf"));
                    break;
            }
        }
    }

    /**
     * 当焦点聚焦的时候，
     * 如果文字长度长于控件长度，执行跑马灯效果
     */
    private void startMarquee(boolean gainFocus) {
        if (gainFocus) {
            setSelected(true);
            setEllipsize(TextUtils.TruncateAt.MARQUEE);
        } else {
            setEllipsize(TextUtils.TruncateAt.END);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (State.SELECT == currentState) {
            if (isFocused()) {
                /***画bitmap的小区域******/
                Rect src = new Rect();
                src.left = 0;
                src.top = 0;
                src.right = selectUnfocus.getWidth();
                src.bottom = selectUnfocus.getHeight();
                /***将画好的图放置在屏幕上的区域**/
                Rect target = new Rect();
                target.left = getMeasuredWidth() - getMeasuredHeight();
                target.top = pading;
                target.right = getMeasuredWidth() - pading;
                target.bottom = getMeasuredHeight() - pading;
                canvas.drawBitmap(selectFocused, src, target, getPaint());
            } else {
                /***画bitmap的小区域******/
                Rect src = new Rect();
                src.left = 0;
                src.top = 0;
                src.right = selectUnfocus.getWidth();
                src.bottom = selectUnfocus.getHeight();
                /***将画好的图放置在屏幕上的区域**/
                Rect target = new Rect();
                target.left = getMeasuredWidth() - getMeasuredHeight();
                target.top = pading;
                target.right = getMeasuredWidth() - pading;
                target.bottom = getMeasuredHeight() - pading;
                canvas.drawBitmap(selectUnfocus, src, target, getPaint());
            }
        }

//        if (State.DISABLE == currentState) {
//            if (!isFocused()) {
//                /***画bitmap的小区域******/
//                Rect src = new Rect();
//                src.left = 0;
//                src.top = 0;
//                src.right = disableUnfocusBG.getWidth();
//                src.bottom = disableUnfocusBG.getHeight();
//                /***将画好的图放置在屏幕上的区域**/
//                Rect target = new Rect();
//                target.left = pading;
//                target.top = pading;
//                target.right = getMeasuredWidth() - pading;
//                target.bottom = getMeasuredHeight() - pading;
//                canvas.drawBitmap(disableUnfocusBG, src, target, getPaint());
//            }
//        }
        super.onDraw(canvas);
    }
}
