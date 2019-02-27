package com.yunos.tvtaobao.biz.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.PositionManager;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.core.common.AppDebug;

/**
 * 画蒙板区域的FocusPositionManager
 * @author tingmeng.ytm
 */
public class DrawRectFocusPositionManager extends FocusPositionManager {
    private final String TAG = "DrawRectFocusPositionManager";
    private RectF mFocusItemRectF;
    private Path mRectPath;
    private Paint mPaint;
    private Handler mHandler;
    private Rect mRectPadding;
    private boolean mInitedFocus; // 是否已经初始化完成Focus
    private RectF mTmpRectF; // draw时临时使用的RectF
    
    public DrawRectFocusPositionManager(Context context) {
        super(context);
        init();
    }
    
    public DrawRectFocusPositionManager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public DrawRectFocusPositionManager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    //    @Override
    //    protected void drawForeFocus(Canvas canvas) {
    //        drawFocusRect(canvas);
    //        super.drawForeFocus(canvas);
    //    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawFocusRect(canvas);
    }
    
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        // 未初始化才请求
        if (!mInitedFocus){
            requestChangeFocusItemRect();
        }
    }
    
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        // 未初始化才请求
        if (!mInitedFocus){
            requestChangeFocusItemRect();
        }
    }
    
    /**
     * 设置颜色
     * @param color
     */
    public void setDrawRectColor(int color){
        mPaint.setColor(color);
    }
    
    /**
     * 设置透明度
     * @param alpha
     */
    public void setDrawRectAlpha(float alpha){
        mPaint.setAlpha((int)(255 * 0.2f));
    }
    
    /**
     * 清除覆盖
     */
    public void clearFocusItemRect(){
        AppDebug.i(TAG, "clearFocusItemRect");
        setFocusItemRect(new RectF(0, 0, getWidth(), getHeight()));
    }
    
    /**
     * 设置满屏覆盖
     */
    public void setFullFocusItemRect(){
        AppDebug.i(TAG, "setFullFocusItemRect");
        setFocusItemRect(null);
    }
    
    /**
     * 直接设置区域
     * @param rectF
     */
    public void setFocusItemRect(RectF rectF){
        AppDebug.i(TAG, "setFocusItemRect rectF="+rectF);
        if (mFocusItemRectF != null) {
            if (rectF != null) {
                mFocusItemRectF.set(rectF);
            } else {
                mFocusItemRectF.setEmpty();
            }
        } else {
            mFocusItemRectF = new RectF(rectF);
        }
        invalidate();
    }
    
    /**
     * 设置带padding的Focus引起的区域变化
     * @param rect
     */
    public void changeFocusItemRect(Rect rect){
        mRectPadding = rect;
        changeFocusItemRect();
    }
    
    /**
     * 修改Focus变化引起的区域变化
     */
    public void changeFocusItemRect(){
        AppDebug.i(TAG, "changeFocusItemRect");
        View focus = getFocused();
        PositionManager pm = getPositionManager();
        if (pm != null && focus instanceof FocusListener) {
            FocusListener focusListener = (FocusListener)focus;
            ItemListener itemListener = focusListener.getItem();
            if (itemListener != null && focusListener.getParams() != null && focusListener.getParams().getScaleParams() != null) {
                float scaleX = focusListener.getParams().getScaleParams().getScaleX();
                float scaleY = focusListener.getParams().getScaleParams().getScaleY();
                try {
                    mFocusItemRectF.set(pm.getDstRect(scaleX, scaleY, itemListener.isScale()));
                    offsetManualPadding(mFocusItemRectF, itemListener.getManualPadding());
                    offsetManualPadding(mFocusItemRectF, mRectPadding);
                    mInitedFocus = true;
                    invalidate();
                } catch (Exception e) {
                    AppDebug.w(TAG, "changeFocusItemRect error");
                }
            }
        }
    }
    
    /**
     * 目的是为了在下个消息时再请求（因为有时候里面的数据还未准备好）
     */
    private void requestChangeFocusItemRect(){
        AppDebug.i(TAG, "requestChangeFocusItemRect");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                changeFocusItemRect();
            }
        });
    }
    
    /**
     * 画蒙板区域
     * @param canvas
     */
    private void drawFocusRect(Canvas canvas){
        mRectPath.reset();
        if (checkRectValid()){
            mRectPath.addRect(mFocusItemRectF, Direction.CW);
            mTmpRectF.set(0, 0, getWidth(), getHeight());
            mRectPath.addRect(mTmpRectF, Direction.CCW);
            mRectPath.close();
            canvas.drawPath(mRectPath, mPaint);
        } else {
            mTmpRectF.set(0, 0, getWidth(), getHeight());
            canvas.drawRect(mTmpRectF, mPaint);
        }
    }
    
    /**
     * 手动修改的区域大小
     * @param r
     * @param padding
     */
    private void offsetManualPadding(RectF r, Rect padding) {
        if (padding != null && !padding.isEmpty()) {
            r.left += padding.left;
            r.right += padding.right;
            r.top += padding.top;
            r.bottom += padding.bottom;
        }
    }
    
    /**
     * 区域是否有效
     * @return
     */
    private boolean checkRectValid(){
        if (mFocusItemRectF != null) {
            if (!mFocusItemRectF.isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 初始化
     */
    private void init(){
        mHandler = new Handler();
        mFocusItemRectF = new RectF();
        mTmpRectF = new RectF();
        mRectPath = new Path();
        mPaint = new Paint();
        // 默认黑色，透明度为0.2f
        mPaint.setColor(Color.BLACK);
        mPaint.setAlpha((int)(255 * 0.2f));
        mPaint.setStyle(Paint.Style.FILL);
    }
}
