package com.yunos.tvtaobao.live.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Xfermode;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by pan on 16/9/29.
 */

public class LiveCommentView extends RecyclerView {
    private Paint mPaint;
    private LinearGradient linearGradient;
    private int layerId;

    public LiveCommentView(Context context) {
        this(context, null);
    }

    public LiveCommentView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LiveCommentView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    /**
     * 每次轮询到的评论条数,关系到滚动速度
     * @param size
     */
    public void setNewData(int count, int size) {
        if (size > 0)
            index = count;

        if (size > 10) {
            speed = size / 10;
        } else {
            speed = 1;
        }
    }

    private int index = 0;
    /**
     * 设置自动滚动
     */
    public void setAutoScroll() {
        mHandler.post(run);
    }

    /**
     * 关闭全屏的时候,停止滚动
     */
    public void stopScroll() {
        index = 0;
        mHandler.removeCallbacks(run);
    }

    private int speed = 1;
    private Handler mHandler = new Handler();

    Runnable run = new Runnable() {
        @Override
        public void run() {
            index = index + speed;

//            scrollToPosition(index);
            smoothScrollToPosition(index);
            mHandler.postDelayed(run, 500);
        }
    };


    /**
     * 设置item间距
     * @param space
     */
    public void setItemDecoration(int space) {
        addItemDecoration(new SpaceItemDecoration(space));
    }

    private class SpaceItemDecoration extends ItemDecoration {
        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {

            if(parent.getChildPosition(view) != 0)
                outRect.top = space;
        }
    }

    public void doTopGradualEffect(){

        mPaint = new Paint();
        // 融合器
        final Xfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        mPaint.setXfermode(xfermode);
        // 创造一个颜色渐变，作为聊天区顶部效果
        linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, 100.0f, new int[]{0, Color.BLACK}, null, Shader.TileMode.CLAMP);

        this.addItemDecoration(new ItemDecoration() {
            // 滑动RecyclerView，渲染之后每次都会回调这个方法，就在这里进行融合
            @Override
            public void onDrawOver(Canvas canvas, RecyclerView parent, State state) {
                super.onDrawOver(canvas, parent, state);

                mPaint.setXfermode(xfermode);
                mPaint.setShader(linearGradient);
                canvas.drawRect(0.0f, 0.0f, parent.getRight(), 200.0f, mPaint);
                mPaint.setXfermode(null);
                canvas.restoreToCount(layerId);
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, State state) {
                super.onDraw(c, parent, state);
                layerId = c.saveLayer(0.0f, 0.0f, (float) parent.getWidth(), (float) parent.getHeight(), mPaint, Canvas.ALL_SAVE_FLAG);
            }

            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, State state) {
                super.getItemOffsets(outRect, view, parent, state);
            }
        });
    }
}
