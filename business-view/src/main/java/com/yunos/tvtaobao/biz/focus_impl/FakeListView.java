package com.yunos.tvtaobao.biz.focus_impl;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.LinearInterpolator;

import com.yunos.tv.core.common.AppDebug;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by GuoLiDong on 2018/11/9.
 */
public class FakeListView extends ViewGroup {
    private static final String TAG = FakeListView.class.getSimpleName();
    public static final int NO_POS = -1;
    private static final int MSG_SCROLL_ANIM_END = 0x0001;
    private static final int MSG_SCROLL_TASK_NOT_RUN = 0x0002;
    private static final int MSG_NOTIFY_FINISH = 0x0003;

    private int flag = 0x00000000;
    private static final int FLAG_notify_layout_routine = 0x00000001;
    private static final int FLAG_scroll_doing = 0x00000002;
    private static final int FLAG_view_ready = 0x00000004;
    private static final int FLAG_view_detached = 0x00000008;

    private int flag2 = 0x00000000;
    private static final int FLAG2_need_init = 0x00000004;


    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_NOTIFY_FINISH:
                case MSG_SCROLL_ANIM_END:
                case MSG_SCROLL_TASK_NOT_RUN: {
                    handleFirstTaskInScrollTaskCache();
                    if (msg.what == MSG_NOTIFY_FINISH) {
                        handleTaskCache4PstNotifyAndLayout();
                    }
                }
                break;
            }
        }
    };
    private Adapter adapter;
    private LayoutDoneListener layoutDoneListener = null;
    private List<ViewHolder> cachedViewHolders = new ArrayList<>();
    private List<ViewHolder> usingViewHolders = new ArrayList<>();
    private List<Runnable> taskCache4ScrollDone = new ArrayList<>();
    private List<Runnable> taskCache4BeforeNotifyAndLayout = new ArrayList<>();
    private List<Runnable> taskCache4AfterNotifyAndLayout = new ArrayList<>();
    private List<Animator> animatorsRecord = new ArrayList<>();
    private Pair<Integer, Integer> cachedMS;
    private OnLayoutParams onLayoutParams = null;
    private Style style = Style.noEdgeLimit;
    private int gravity = Gravity.TOP;
    private boolean reverseLayoutFlag = false;
    private int stepY = 0;
    private int itemMargin = 5;
    private int layoutCount = 0;
    private int immediatelyMovePos = NO_POS;


    public FakeListView(Context context) {
        this(context, null);
    }

    public FakeListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FakeListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    protected int tagObjHashCode() {
        return this.hashCode();
    }

    @Override
    public boolean isInEditMode() {
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int width = 0;
        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View tmp = getChildAt(i);
            if (tmp.getMeasuredWidth() > width) {
                width = tmp.getMeasuredWidth();
            }
            height += tmp.getMeasuredHeight();
        }

        int mW = getDefaultSize(width, widthMeasureSpec);
        int mH = getDefaultSize(height, heightMeasureSpec);
        AppDebug.d(TAG, tagObjHashCode() + " onMeasure (" + getChildCount() + "):" + mW + "," + mH);
        setMeasuredDimension(mW, mH);
        cachedMS = new Pair<>(widthMeasureSpec, heightMeasureSpec);

        if (!isViewReady()) {
            flag |= FLAG_view_ready;
            if (adapter != null) {
                adapter.handler.sendEmptyMessage(Adapter.MSG_VIEW_READY);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        synchronized (FakeListView.this) {
            doLayout(changed, l, t, r, b, "onLayout");
        }
    }

    protected void doLayout(final boolean changed, int l, int t, int r, final int b, String from) {
        layoutCount++;

        onLayoutParams = new OnLayoutParams(changed, l, t, r, b);

        layoutStep1();

        layoutStep2();

        layoutStep3();

        stepY = 0;

        AppDebug.d(TAG, tagObjHashCode() + " doLayout from " + from + " " + layoutCount + " done ! " +
                "(children:" + getChildCount() + ", viewHolderSize:" + usingViewHolders.size() + ")");

        if (isNeedInitLayout()) {
            flag2 &= ~FLAG2_need_init;
        }

        if (isNotifyDoing()) {
            flag &= ~FLAG_notify_layout_routine;
            handler.sendEmptyMessage(MSG_NOTIFY_FINISH);
        }

        notifyLayoutDoneListener(changed, l, t, r, b);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        AppDebug.d(TAG, tagObjHashCode() + " dispatchDraw done");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        flag |= FLAG_view_detached;
        handler.removeCallbacks(null);
        clearAnimatorsRecord();
    }

    private void notifyLayoutDoneListener(final boolean changed
            , final int l, final int t, final int r, final int b) {
        post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (getChildCount() > 0) {
                        View top = getChildAt(0);
                        View bottom = getChildAt(getChildCount() - 1);
                        if (layoutDoneListener != null) {
                            layoutDoneListener.onLayoutDone(bottom.getBottom() - top.getTop(), b - t);
                        }
                    } else {
                        if (layoutDoneListener != null) {
                            layoutDoneListener.onLayoutDone(0, b - t);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private boolean isDetachedFromWindow() {
        return (flag & FLAG_view_detached) == FLAG_view_detached;
    }

    private boolean isNeedInitLayout() {
        return (flag2 & FLAG2_need_init) == FLAG2_need_init;
    }

    private ViewHolder getInCache(int viewType) {
        ViewHolder vh = null;
        for (int i = 0; i < cachedViewHolders.size(); i++) {
            ViewHolder tmp = cachedViewHolders.get(i);
            if (tmp.viewType == viewType) {
                vh = tmp;
                break;
            }
        }
        cachedViewHolders.remove(vh);
        return vh;
    }

    public int getGravity() {
        return gravity;
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public void setAdapter(Adapter a) {
        this.adapter = a;
        if (adapter != null) {
            adapter.fakeListViewWeakReference = new WeakReference(this);
            adapter.dataSet.addObserver(new Observer() {
                @Override
                public void update(Observable o, final Object arg) {
                    flag |= FLAG_notify_layout_routine;
                    post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                handleTaskCache4BeforeNotifyAndLayout();
                                synchronized (FakeListView.this) {
                                    onAdapterDataSetChange((Adapter.NotifyParam) arg);
                                }
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                            requestLayout();
                            invalidate();
                        }
                    });
                }
            });
            if (isViewReady()) {
                adapter.handler.sendEmptyMessage(Adapter.MSG_VIEW_READY);
            }
        }
    }

    public Adapter getAdapter() {
        return adapter;
    }

    public LayoutDoneListener getLayoutDoneListener() {
        return layoutDoneListener;
    }

    public void setLayoutDoneListener(LayoutDoneListener layoutDoneListener) {
        this.layoutDoneListener = layoutDoneListener;
    }

    public int getItemMargin() {
        return itemMargin;
    }

    public void setItemMargin(int itemMargin) {
        this.itemMargin = itemMargin;
    }

    public boolean isReverseLayoutFlag() {
        return reverseLayoutFlag;
    }

    public void setReverseLayoutFlag(boolean reverseLayoutFlag) {
        this.reverseLayoutFlag = reverseLayoutFlag;
    }

    public Style getStyle() {
        return style;
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public boolean isViewReady() {
        return (flag & FLAG_view_ready) == FLAG_view_ready;
    }

    public boolean isNotifyDoing() {
        return (flag & FLAG_notify_layout_routine) == FLAG_notify_layout_routine;
    }

    public boolean isScrollDoing() {
        return (flag & FLAG_scroll_doing) == FLAG_scroll_doing;
    }

    private static void clearMeasure(View itemView) {
        if (itemView != null) {
            if (itemView instanceof ViewGroup) {
                itemView.forceLayout();
                for (int i = 0; i < ((ViewGroup) itemView).getChildCount(); i++) {
                    clearMeasure(((ViewGroup) itemView).getChildAt(i));
                }
            } else {
                itemView.forceLayout();
            }
        }
    }

    protected void onAdapterDataSetChange(Adapter.NotifyParam notifyParam) {
        AppDebug.d(TAG, tagObjHashCode() + " onAdapterDataSetChange : " +
                "reverseLayoutFlag=" + reverseLayoutFlag +
                ", itemCount=" + getAdapter().getItemCount() +
                " " + notifyParam.toString());

        clearAnimatorsRecord();

        if (notifyParam.notifyType == Adapter.NotifyType.all) {
            if (getAdapter().getItemCount() == 0) {
                flag2 |= FLAG2_need_init;
                // step1.清理失效viewHolders
                cachedViewHolders.addAll(usingViewHolders);
                removeAllViews();
                usingViewHolders.clear();
                AppDebug.e(TAG, tagObjHashCode() + " onAdapterDataSetChange : " + Adapter.NotifyType.all + " clear !");
                return;
            }
            int bgn = NO_POS;
            int end = NO_POS;
            for (int i = 0; i < usingViewHolders.size(); i++) {
                ViewHolder tmp = usingViewHolders.get(i);
                if (i == 0) {
                    bgn = tmp.posNow;
                }
                if (i == usingViewHolders.size() - 1) {
                    end = tmp.posNow;
                }
            }
            AppDebug.e(TAG, tagObjHashCode() + " onAdapterDataSetChange : " + Adapter.NotifyType.all + "(" + bgn + "," + end + ")");

            if (bgn > adapter.getItemCount() - 1) {

                AppDebug.e(TAG, tagObjHashCode() + " onAdapterDataSetChange : bgn > adapter.getItemCount() - 1  | " + (adapter.getItemCount() - 1));

                flag2 |= FLAG2_need_init;
                // step1.清理失效viewHolders
                cachedViewHolders.addAll(usingViewHolders);
                removeAllViews();
                usingViewHolders.clear();

                // step2.重新构建viewHolders
                int height = getMeasuredHeight() - getPaddingBottom() - getPaddingTop();
                int loopCount = 100;
                for (int i = adapter.getItemCount() - 1, j = 1; i > 0 && j > 0; i--) {
                    loopCount--;
                    int viewType = adapter.getItemViewType(i);
                    ViewHolder vh = getInCache(viewType);
                    if (vh == null) {
                        vh = adapter.onCreateViewHolder(this, viewType);
                    }

                    vh.posNow = i;
                    vh.viewType = viewType;
                    addView(vh.itemView, 0);
                    adapter.onBindViewHolder(vh, vh.posNow);
                    clearMeasure(vh.itemView);
                    measureChild(vh.itemView, cachedMS.first, cachedMS.second);

                    height -= vh.itemView.getMeasuredHeight();
                    vh.posNow = i;
                    usingViewHolders.add(0, vh);
                    if (height < 0) {
                        j--;
                    }
                    if (loopCount <= 0) {
                        AppDebug.e(TAG, tagObjHashCode() + " onAdapterDataSetChange : may be too more item visible .");
                        break;
                    }
                }
            } else {
                if (bgn <= adapter.getItemCount() - 1 && bgn >= 0) {

                    AppDebug.e(TAG, tagObjHashCode() + " onAdapterDataSetChange : bgn <= adapter.getItemCount() - 1 && bgn >= 0  | " + (adapter.getItemCount() - 1));

                    // step1.清理失效viewHolders
                    for (int i = usingViewHolders.size() - 1; i >= 0; i--) {
                        ViewHolder tmp = usingViewHolders.get(i);
                        if (tmp.posNow > adapter.getItemCount() - 1) {
                            usingViewHolders.remove(i);
                            removeView(tmp.itemView);
                            cachedViewHolders.add(tmp);
                        } else {
                            if (tmp.viewType != adapter.getItemViewType(tmp.posNow)) {
                                usingViewHolders.remove(i);
                                removeView(tmp.itemView);
                                cachedViewHolders.add(tmp);
                            }
                        }
                    }

                    if (usingViewHolders.isEmpty()) {
                        flag2 |= FLAG2_need_init;
                    }

                    int shouldStartForm = (usingViewHolders.isEmpty()) ? (bgn) : (usingViewHolders.get(usingViewHolders.size() - 1).posNow + 1);

                    AppDebug.e(TAG, tagObjHashCode() + " onAdapterDataSetChange : shouldStartForm " + shouldStartForm);

                    // step2.重新构建viewHolders
                    for (int i = 0; i < usingViewHolders.size(); i++) {
                        ViewHolder vh = usingViewHolders.get(i);
                        adapter.onBindViewHolder(vh, vh.posNow);
                        clearMeasure(vh.itemView);
                        measureChild(vh.itemView, cachedMS.first, cachedMS.second);
                    }

                    // step3.统计已有内容的布局情况
                    int nowContentBottom = 0;
                    int leftStart = getPaddingLeft();
                    int topStart = Integer.MIN_VALUE;
                    if (getChildCount() > 0) {
                        nowContentBottom = (getChildAt(getChildCount() - 1).getBottom());
                        topStart = nowContentBottom + itemMargin;
                    }

                    int height = getMeasuredHeight() - nowContentBottom;
                    int loopCount = 100;
                    for (int i = shouldStartForm, j = 1; i < adapter.getItemCount() && j > 0; i++) {
                        loopCount--;
                        if (height < 0) {
                            break;
                        }
                        int viewType = adapter.getItemViewType(i);
                        ViewHolder vh = getInCache(viewType);
                        if (vh == null) {
                            vh = adapter.onCreateViewHolder(this, viewType);
                        }

                        vh.posNow = i;
                        vh.viewType = viewType;
                        addView(vh.itemView);
                        adapter.onBindViewHolder(vh, vh.posNow);
                        clearMeasure(vh.itemView);
                        measureChild(vh.itemView, cachedMS.first, cachedMS.second);
                        height -= vh.itemView.getMeasuredHeight();
                        usingViewHolders.add(vh);

                        if (topStart != Integer.MIN_VALUE) {
                            // 列表内已有内容，顺手布局，方便后续布局计算
                            vh.itemView.layout(leftStart, topStart
                                    , leftStart + vh.itemView.getMeasuredWidth()
                                    , topStart + vh.itemView.getMeasuredHeight());
                            topStart += vh.itemView.getMeasuredHeight();
                            topStart += itemMargin;
                        }

                        if (height < 0) {
                            j--;
                        }
                        if (loopCount <= 0) {
                            AppDebug.e(TAG, tagObjHashCode() + " onAdapterDataSetChange : may be too more item visible .");
                            break;
                        }
                    }
                } else {
                    flag2 |= FLAG2_need_init;
                    // step1.清理失效viewHolders
                    // 这种情况不需要清理了，肯定是NO_POS

                    // step2.重新构建viewHolders
                    int height = getMeasuredHeight() - getPaddingBottom() - getPaddingTop();
                    for (int i = 0; i < usingViewHolders.size(); i++) {
                        ViewHolder vh = usingViewHolders.get(i);
                        adapter.onBindViewHolder(vh, vh.posNow);
                        clearMeasure(vh.itemView);
                        measureChild(vh.itemView, cachedMS.first, cachedMS.second);
                        vh.itemView.invalidate();
                        height -= vh.itemView.getMeasuredHeight();
                    }
                    int loopCount = 100;
                    if (!reverseLayoutFlag) {
                        for (int i = 0, j = 1; i < adapter.getItemCount() && j > 0; i++) {
                            loopCount--;
                            int viewType = adapter.getItemViewType(i);
                            ViewHolder vh = getInCache(viewType);
                            if (vh == null) {
                                vh = adapter.onCreateViewHolder(this, viewType);
                            }

                            vh.posNow = i;
                            vh.viewType = viewType;
                            addView(vh.itemView);
                            adapter.onBindViewHolder(vh, vh.posNow);
                            clearMeasure(vh.itemView);
                            measureChild(vh.itemView, cachedMS.first, cachedMS.second);
                            vh.itemView.invalidate();

                            height -= vh.itemView.getMeasuredHeight();
                            usingViewHolders.add(vh);
                            if (height < 0) {
                                j--;
                            }
                            if (loopCount <= 0) {
                                AppDebug.e(TAG, tagObjHashCode() + " onAdapterDataSetChange : may be too more item visible .");
                                break;
                            }
                        }
                    } else {
                        for (int i = adapter.getItemCount() - 1, j = 1; i >= 0 && j > 0; i--) {
                            loopCount--;
                            int viewType = adapter.getItemViewType(i);
                            ViewHolder vh = getInCache(viewType);
                            if (vh == null) {
                                vh = adapter.onCreateViewHolder(this, viewType);
                            }

                            vh.posNow = i;
                            vh.viewType = viewType;
                            addView(vh.itemView, 0);
                            adapter.onBindViewHolder(vh, vh.posNow);
                            clearMeasure(vh.itemView);
                            measureChild(vh.itemView, cachedMS.first, cachedMS.second);
                            vh.itemView.invalidate();

                            height -= vh.itemView.getMeasuredHeight();
                            vh.posNow = i;
                            usingViewHolders.add(0, vh);
                            if (height < 0) {
                                j--;
                            }
                            if (loopCount <= 0) {
                                AppDebug.e(TAG, tagObjHashCode() + " onAdapterDataSetChange : may be too more item visible .");
                                break;
                            }
                        }
                    }
                }
            }
        } else if (notifyParam.notifyType == Adapter.NotifyType.single) {
            ViewHolder vh = getViewHolder(notifyParam.singlePos);
            if (vh != null) {
                adapter.onBindViewHolder(vh, notifyParam.singlePos);
                clearMeasure(vh.itemView);
                measureChild(vh.itemView, cachedMS.first, cachedMS.second);
                vh.itemView.invalidate();
                AppDebug.e(TAG, tagObjHashCode() + " onAdapterDataSetChange : " + Adapter.NotifyType.single + "(+notifyParam.singlePos+)");
            }
        }
    }

    protected void layoutStep1() {
        int leftStart = getPaddingLeft();
        int topStart = 0;
        int bottomStart = 0;
        AppDebug.d(TAG, tagObjHashCode() + " layoutStep1:"
                + "usingViewHolders-size=" + usingViewHolders.size()
                + " , paddings=[" + getPaddingLeft() + "," + getPaddingTop()
                + "," + getPaddingRight() + "," + getPaddingBottom() + "]"
                + " , reverseLayoutFlag=" + reverseLayoutFlag
                + " , style=" + style
                + " , stepY=" + stepY
                + " , immediatelyMovePos=" + immediatelyMovePos);

        if (immediatelyMovePos != NO_POS) {
            int initPos = immediatelyMovePos;
            immediatelyMovePos = NO_POS;
            if (initPos >= 0 && initPos < getAdapter().getItemCount()) {
                ViewHolder vh4InitPos = null;
                for (int i = 0; i < usingViewHolders.size(); i++) {
                    ViewHolder vh = usingViewHolders.get(i);
                    if (vh.posNow == initPos) {
                        vh4InitPos = vh;
                    }
                }

                Rect myRect = new Rect();
                getDrawingRect(myRect);

                if (vh4InitPos != null) {
                    Rect itemRect = new Rect();
                    vh4InitPos.itemView.getDrawingRect(itemRect);
                    offsetDescendantRectToMyCoords(vh4InitPos.itemView, itemRect);

                    int distance = myRect.centerY() - itemRect.centerY();

                    for (int i = 0; i < getChildCount(); i++) {
                        View tmp = getChildAt(i);
                        if (i == 0) {
                            topStart = tmp.getTop() + distance;
                            AppDebug.d(TAG, tagObjHashCode() + " layoutStep1:"
                                    + " real topStart=" + topStart);
                        }
                        tmp.layout(leftStart, topStart
                                , leftStart + tmp.getMeasuredWidth()
                                , topStart + tmp.getMeasuredHeight());
                        topStart += tmp.getMeasuredHeight();
                        if (i != getChildCount() - 1) {
                            topStart += itemMargin;
                        }
                    }
                } else {
                    // step1.清理失效viewHolders
                    cachedViewHolders.addAll(usingViewHolders);
                    removeAllViews();
                    usingViewHolders.clear();

                    int contentTop = myRect.centerY();
                    int contentBottom = myRect.centerY();
                    for (int pre = initPos - 1, pst = initPos + 1, loopCount = 20;
                         loopCount > 0; pre--, pst++, loopCount--) {
                        if (loopCount == 20) {
                            int viewType = getAdapter().getItemViewType(initPos);
                            ViewHolder vh = getInCache(viewType);
                            if (vh == null) {
                                vh = getAdapter().onCreateViewHolder(this, viewType);
                            }
                            vh.posNow = initPos;
                            addViewInLayout(vh.itemView, 0, vh.itemView.getLayoutParams());
                            usingViewHolders.add(0, vh);
                            getAdapter().onBindViewHolder(vh, vh.posNow);
                            clearMeasure(vh.itemView);
                            measureChild(vh.itemView, cachedMS.first, cachedMS.second);

                            contentTop = contentTop - vh.itemView.getMeasuredHeight() / 2;
                            contentBottom = contentBottom + vh.itemView.getMeasuredHeight() / 2;

                            vh.itemView.layout(leftStart, contentTop
                                    , leftStart + vh.itemView.getMeasuredWidth()
                                    , contentTop + vh.itemView.getMeasuredHeight());
                        }

                        if (pre >= 0) {
                            int viewType = getAdapter().getItemViewType(pre);
                            ViewHolder vh = getInCache(viewType);
                            if (vh == null) {
                                vh = getAdapter().onCreateViewHolder(this, viewType);
                            }
                            vh.posNow = pre;
                            addViewInLayout(vh.itemView, 0, vh.itemView.getLayoutParams());
                            usingViewHolders.add(0, vh);
                            getAdapter().onBindViewHolder(vh, vh.posNow);
                            clearMeasure(vh.itemView);
                            measureChild(vh.itemView, cachedMS.first, cachedMS.second);

                            contentTop = contentTop - vh.itemView.getMeasuredHeight() - itemMargin;

                            vh.itemView.layout(leftStart, contentTop
                                    , leftStart + vh.itemView.getMeasuredWidth()
                                    , contentTop + vh.itemView.getMeasuredHeight());
                        }

                        if (pst < getAdapter().getItemCount()) {
                            int viewType = getAdapter().getItemViewType(pst);
                            ViewHolder vh = getInCache(viewType);
                            if (vh == null) {
                                vh = getAdapter().onCreateViewHolder(this, viewType);
                            }
                            vh.posNow = pst;
                            addViewInLayout(vh.itemView, -1, vh.itemView.getLayoutParams());
                            usingViewHolders.add(vh);
                            getAdapter().onBindViewHolder(vh, vh.posNow);
                            clearMeasure(vh.itemView);
                            measureChild(vh.itemView, cachedMS.first, cachedMS.second);

                            contentBottom = contentBottom + vh.itemView.getMeasuredHeight() + itemMargin;

                            vh.itemView.layout(leftStart, contentBottom - vh.itemView.getMeasuredHeight()
                                    , leftStart + vh.itemView.getMeasuredWidth()
                                    , contentBottom);
                        }

                        if (contentBottom - contentTop > myRect.height()) {
                            break;
                        }
                    }

                }

                return;
            }
        }

        if (!reverseLayoutFlag) {
            for (int i = 0; i < getChildCount(); i++) {
                View tmp = getChildAt(i);
                if (i == 0) {
                    if (isNeedInitLayout()) {
                        topStart = getPaddingTop();
                    } else {
                        topStart = tmp.getTop();
                    }
                    topStart += stepY;
                    AppDebug.d(TAG, tagObjHashCode() + " layoutStep1:"
                            + " real topStart=" + topStart);
                }
                AppDebug.d(TAG, tagObjHashCode() + " layoutStep1:"
                        + " old child(" + i + ") [" + tmp.getLeft() + "," + tmp.getTop() + "," + tmp.getRight() + "," + tmp.getBottom() + "]");
                tmp.layout(leftStart, topStart
                        , leftStart + tmp.getMeasuredWidth()
                        , topStart + tmp.getMeasuredHeight());
                AppDebug.d(TAG, tagObjHashCode() + " layoutStep1:"
                        + " new child(" + i + ") [" + tmp.getLeft() + "," + tmp.getTop() + "," + tmp.getRight() + "," + tmp.getBottom() + "]");
                topStart += tmp.getMeasuredHeight();
                if (i != getChildCount() - 1) {
                    topStart += itemMargin;
                }
            }
        } else {
            for (int i = getChildCount() - 1; i >= 0; i--) {
                View tmp = getChildAt(i);
                if (i == getChildCount() - 1) {
                    if (isNeedInitLayout()) {
                        bottomStart = getMeasuredHeight() - getPaddingBottom();
                    } else {
                        bottomStart = tmp.getBottom();
                    }
                    bottomStart += stepY;
                    AppDebug.d(TAG, tagObjHashCode() + " layoutStep1:"
                            + " real bottomStart=" + bottomStart);
                }
                AppDebug.d(TAG, tagObjHashCode() + " layoutStep1:"
                        + " old child(" + i + ") [" + tmp.getLeft() + "," + tmp.getTop() + "," + tmp.getRight() + "," + tmp.getBottom() + "]");
                tmp.layout(leftStart, bottomStart - tmp.getMeasuredHeight()
                        , leftStart + tmp.getMeasuredWidth()
                        , bottomStart);
                AppDebug.d(TAG, tagObjHashCode() + " layoutStep1:"
                        + " new child(" + i + ") [" + tmp.getLeft() + "," + tmp.getTop() + "," + tmp.getRight() + "," + tmp.getBottom() + "]");
                bottomStart -= tmp.getMeasuredHeight();
                if (i != 0) {
                    bottomStart -= itemMargin;
                }
            }
        }
    }

    protected void layoutMoreAtBottomIfNeed() {
        ViewHolder bgn = usingViewHolders.get(0);
        ViewHolder end = usingViewHolders.get(usingViewHolders.size() - 1);
        if (end.itemView.getBottom() - (getMeasuredHeight() - getPaddingBottom())
                < end.itemView.getHeight()) {
            if (end.posNow < adapter.getItemCount() - 1) {
                int newPos = end.posNow + 1;
                int newPosViewType = adapter.getItemViewType(newPos);
                ViewHolder newVH = getInCache(newPosViewType);
                if (newVH == null) {
                    newVH = adapter.onCreateViewHolder(this, newPosViewType);
                }
                newVH.posNow = newPos;
                usingViewHolders.add(newVH);
                addViewInLayout(newVH.itemView, -1, newVH.itemView.getLayoutParams());
                adapter.onBindViewHolder(newVH, newPos);
                clearMeasure(newVH.itemView);
                measureChild(newVH.itemView, cachedMS.first, cachedMS.second);
                newVH.itemView.invalidate();

                if (newVH.itemView.getVisibility() != GONE) {
                    measureChild(newVH.itemView, cachedMS.first, cachedMS.second);
                }
                Rect tmp = new Rect(getPaddingLeft(), end.itemView.getBottom() + itemMargin
                        , 0, 0);
                tmp.right = tmp.left + newVH.itemView.getMeasuredWidth();
                tmp.bottom = tmp.top + newVH.itemView.getMeasuredHeight();
                AppDebug.d(TAG, tagObjHashCode() + " layoutMoreAtBottomIfNeed:"
                        + " old child(" + (getChildCount() - 1) + ") [" + newVH.itemView.getLeft() + "," + newVH.itemView.getTop() + "," + newVH.itemView.getRight() + "," + newVH.itemView.getBottom() + "]");
                newVH.itemView.layout(tmp.left, tmp.top, tmp.right, tmp.bottom);
                AppDebug.d(TAG, tagObjHashCode() + " layoutMoreAtBottomIfNeed:"
                        + " new child(" + (getChildCount() - 1) + ") [" + newVH.itemView.getLeft() + "," + newVH.itemView.getTop() + "," + newVH.itemView.getRight() + "," + newVH.itemView.getBottom() + "]");
                AppDebug.d(TAG, tagObjHashCode() + " layoutMoreAtBottomIfNeed: layout more at bottom " + newPos);
                layoutStep2();
            }
        }
    }

    protected void layoutMoreAtTopIfNeed() {
        ViewHolder bgn = usingViewHolders.get(0);
        ViewHolder end = usingViewHolders.get(usingViewHolders.size() - 1);
        if (getPaddingTop() - bgn.itemView.getTop() < bgn.itemView.getHeight()) {
            if (bgn.posNow > 0) {
                int newPos = bgn.posNow - 1;
                int newPosViewType = adapter.getItemViewType(newPos);
                ViewHolder newVH = getInCache(newPosViewType);
                if (newVH == null) {
                    newVH = adapter.onCreateViewHolder(this, newPosViewType);
                }
                newVH.posNow = newPos;
                usingViewHolders.add(0, newVH);
                addViewInLayout(newVH.itemView, 0, newVH.itemView.getLayoutParams());
                adapter.onBindViewHolder(newVH, newPos);
                clearMeasure(newVH.itemView);
                measureChild(newVH.itemView, cachedMS.first, cachedMS.second);
                newVH.itemView.invalidate();

                if (newVH.itemView.getVisibility() != GONE) {
                    measureChild(newVH.itemView, cachedMS.first, cachedMS.second);
                }
                Rect tmp = new Rect(getPaddingLeft(), 0
                        , 0, bgn.itemView.getTop() - itemMargin);
                tmp.right = tmp.left + newVH.itemView.getMeasuredWidth();
                tmp.top = tmp.bottom - newVH.itemView.getMeasuredHeight();
                AppDebug.d(TAG, tagObjHashCode() + " layoutMoreAtBottomIfNeed:"
                        + " old child(" + (0) + ") [" + newVH.itemView.getLeft() + "," + newVH.itemView.getTop() + "," + newVH.itemView.getRight() + "," + newVH.itemView.getBottom() + "]");
                newVH.itemView.layout(tmp.left, tmp.top, tmp.right, tmp.bottom);
                AppDebug.d(TAG, tagObjHashCode() + " layoutMoreAtBottomIfNeed:"
                        + " new child(" + (0) + ") [" + newVH.itemView.getLeft() + "," + newVH.itemView.getTop() + "," + newVH.itemView.getRight() + "," + newVH.itemView.getBottom() + "]");
                AppDebug.d(TAG, tagObjHashCode() + " layoutMoreAtTopIfNeed: layout more at top " + newPos);
                layoutStep2();
            }
        }
    }

    protected void layoutStep2() {
        if (!usingViewHolders.isEmpty()) {
            ViewHolder bgn = usingViewHolders.get(0);
            ViewHolder end = usingViewHolders.get(usingViewHolders.size() - 1);
            AppDebug.d(TAG, tagObjHashCode() + " layoutStep2:"
                    + "usingViewHolders-size=" + usingViewHolders.size()
                    + " , bgn-top=" + bgn.itemView.getTop()
                    + " , end-bottom=" + end.itemView.getBottom()
                    + " , height=" + getMeasuredHeight());
            layoutMoreAtBottomIfNeed();
            layoutMoreAtTopIfNeed();
        }
    }

    protected void layoutStep3() {

        // step1.根据gravity，决定最终贴边位置
        if (gravity == Gravity.TOP) {
            if (getChildCount() > 0) {
                View top = getChildAt(0);
                View bottom = getChildAt(getChildCount() - 1);
                AppDebug.d(TAG, tagObjHashCode() + " layoutStep3: gravity=" + gravity
                        + " top [" + top.getLeft() + "," + top.getTop() + "," + top.getRight() + "," + top.getBottom() + "]"
                        + " bottom [" + bottom.getLeft() + "," + bottom.getTop() + "," + bottom.getRight() + "," + bottom.getBottom() + "]");
                if (top.getTop() >= getPaddingTop()) {
                    int stepY = getPaddingTop() - top.getTop();
                    int topStart = top.getTop() + stepY;
                    for (int i = 0; i < getChildCount(); i++) {
                        View tmp = getChildAt(i);
                        tmp.layout(tmp.getLeft(), topStart
                                , tmp.getRight()
                                , topStart + tmp.getMeasuredHeight());
                        topStart += tmp.getMeasuredHeight();
                        if (i != getChildCount() - 1) {
                            topStart += itemMargin;
                        }
                    }
                    AppDebug.d(TAG, tagObjHashCode() + " layoutStep3: align to top " + stepY);
                } else {
                    if (style == Style.edgeLimit) {
                        float contentHeight = bottom.getBottom() - top.getTop();
                        if (contentHeight >= getMeasuredHeight()) {
                            if (bottom.getBottom() < getMeasuredHeight() - getPaddingBottom()) {
                                // 处理多余滚动的问题
                                int stepY = (getMeasuredHeight() - getPaddingBottom()) - bottom.getBottom();
                                int bottomStart = bottom.getBottom() + stepY;
                                for (int i = getChildCount() - 1; i >= 0; i--) {
                                    View tmp = getChildAt(i);
                                    tmp.layout(tmp.getLeft(), bottomStart - tmp.getMeasuredHeight()
                                            , tmp.getRight()
                                            , bottomStart);
                                    bottomStart -= tmp.getMeasuredHeight();
                                    if (i != 0) {
                                        bottomStart -= itemMargin;
                                    }
                                }
                                AppDebug.d(TAG, tagObjHashCode() + " layoutStep3: align to bottom " + stepY);
                            }
                        } else {
                            if (top.getTop() < getPaddingTop()) {
                                int stepY = getPaddingTop() - top.getTop();
                                int topStart = top.getTop() + stepY;
                                for (int i = 0; i < getChildCount(); i++) {
                                    View tmp = getChildAt(i);
                                    tmp.layout(tmp.getLeft(), topStart
                                            , tmp.getRight()
                                            , topStart + tmp.getMeasuredHeight());
                                    topStart += tmp.getMeasuredHeight();
                                    if (i != getChildCount() - 1) {
                                        topStart += itemMargin;
                                    }
                                }
                                AppDebug.d(TAG, tagObjHashCode() + " layoutStep3: align to top " + stepY);
                            }
                        }
                    }
                }
            }
        } else if (gravity == Gravity.BOTTOM) {
            if (getChildCount() > 0) {
                View top = getChildAt(0);
                View bottom = getChildAt(getChildCount() - 1);
                AppDebug.d(TAG, tagObjHashCode() + " layoutStep3: gravity=" + gravity
                        + " top [" + top.getLeft() + "," + top.getTop() + "," + top.getRight() + "," + top.getBottom() + "]"
                        + " bottom [" + bottom.getLeft() + "," + bottom.getTop() + "," + bottom.getRight() + "," + bottom.getBottom() + "]");
                if (bottom.getBottom() <= getMeasuredHeight() - getPaddingBottom()) {
                    int stepY = (getMeasuredHeight() - getPaddingBottom()) - bottom.getBottom();
                    int bottomStart = bottom.getBottom() + stepY;
                    for (int i = getChildCount() - 1; i >= 0; i--) {
                        View tmp = getChildAt(i);
                        tmp.layout(tmp.getLeft(), bottomStart - tmp.getMeasuredHeight()
                                , tmp.getRight()
                                , bottomStart);
                        bottomStart -= tmp.getMeasuredHeight();
                        if (i != 0) {
                            bottomStart -= itemMargin;
                        }
                    }
                    AppDebug.d(TAG, tagObjHashCode() + " layoutStep3: align to bottom " + stepY);
                } else {
                    if (style == Style.edgeLimit) {
                        float contentHeight = bottom.getBottom() - top.getTop();
                        if (contentHeight >= getMeasuredHeight()) {
                            if (top.getTop() > getPaddingTop()) {
                                // 处理多余滚动的问题
                                int stepY = getPaddingTop() - top.getTop();
                                int topStart = top.getTop() + stepY;
                                for (int i = 0; i < getChildCount(); i++) {
                                    View tmp = getChildAt(i);
                                    tmp.layout(tmp.getLeft(), topStart
                                            , tmp.getRight()
                                            , topStart + tmp.getMeasuredHeight());
                                    topStart += tmp.getMeasuredHeight();
                                    if (i != getChildCount() - 1) {
                                        topStart += itemMargin;
                                    }
                                }
                                AppDebug.d(TAG, tagObjHashCode() + " layoutStep3: align to top " + stepY);
                            }
                        } else {
                            if (bottom.getBottom() > getMeasuredHeight() - getPaddingBottom()) {
                                int stepY = (getMeasuredHeight() - getPaddingBottom()) - bottom.getBottom();
                                int bottomStart = bottom.getBottom() + stepY;
                                for (int i = getChildCount() - 1; i >= 0; i--) {
                                    View tmp = getChildAt(i);
                                    tmp.layout(tmp.getLeft(), bottomStart - tmp.getMeasuredHeight()
                                            , tmp.getRight()
                                            , bottomStart);
                                    bottomStart -= tmp.getMeasuredHeight();
                                    if (i != 0) {
                                        bottomStart -= itemMargin;
                                    }
                                }
                                AppDebug.d(TAG, tagObjHashCode() + " layoutStep3: align to bottom " + stepY);
                            }
                        }
                    }
                }
            }
        }

        // step2.最终贴边完成后，计算可回收条目
        List<ViewHolder> pre = new ArrayList<>();
        List<ViewHolder> pst = new ArrayList<>();
        for (int i = 0; i < usingViewHolders.size(); i++) {
            ViewHolder vh = usingViewHolders.get(i);
            if (vh.itemView.getBottom() < getPaddingTop()) {
                pre.add(vh);
            }
            if (vh.itemView.getTop() > getMeasuredHeight()) {
                pst.add(vh);
            }
        }

        // 清理前边不显示的部分
        while (pre.size() > 1) {
            ViewHolder vh = pre.remove(0);
            removeViewInLayout(vh.itemView);
            usingViewHolders.remove(vh);
            cachedViewHolders.add(vh);
            AppDebug.d(TAG, tagObjHashCode() + " layoutStep3: recycle pre " + vh.posNow);
        }

        // 清理后边不显示的部分
        while (pst.size() > 1) {
            ViewHolder vh = pst.remove(pst.size() - 1);
            removeViewInLayout(vh.itemView);
            usingViewHolders.remove(vh);
            cachedViewHolders.add(vh);
            AppDebug.d(TAG, tagObjHashCode() + " layoutStep3: recycle pst " + vh.posNow);
        }

        // 清理过多的缓存
        while (cachedViewHolders.size() > 3) {
            cachedViewHolders.remove(0);
            AppDebug.d(TAG, tagObjHashCode() + " layoutStep3: recycle cache");
        }
    }

    public Pair<Integer, Integer> getLayoutRange() {
        int bgn = NO_POS;
        int end = NO_POS;
        for (int i = 0; i < usingViewHolders.size(); i++) {
            if (i == 0) {
                bgn = usingViewHolders.get(i).posNow;
            }
            if (i == usingViewHolders.size() - 1) {
                end = usingViewHolders.get(i).posNow;
            }
        }
        return new Pair<>(bgn, end);
    }

    public ViewHolder getViewHolder(int pos) {
        for (int i = 0; i < usingViewHolders.size(); i++) {
            ViewHolder tmp = usingViewHolders.get(i);
            if (tmp.posNow == pos) {
                return tmp;
            }
        }
        return null;
    }

    public ViewHolder getViewHolder(View view) {
        View itemView = null;
        ViewParent vp = view.getParent();
        if (vp == this) {
            itemView = view;
        } else {
            int loop = 20;
            while (vp != null) {
                loop--;
                if (loop < 0) {
                    break;
                }
                if (vp.getParent() == this) {
                    if (vp instanceof View) {
                        itemView = (View) vp;
                    }
                    break;
                }
                vp = vp.getParent();
            }

        }

        if (itemView != null) {
            for (int i = 0; i < usingViewHolders.size(); i++) {
                if (usingViewHolders.get(i).itemView == itemView) {
                    return usingViewHolders.get(i);
                }
            }
        }
        return null;
    }

    protected void doLayoutMove() {
        AppDebug.d(TAG, tagObjHashCode() + " doLayoutMove");
        synchronized (FakeListView.this) {
            doLayout(onLayoutParams.changed, onLayoutParams.l, onLayoutParams.t
                    , onLayoutParams.r, onLayoutParams.b, "doLayoutMove");
        }
        invalidate();
    }

    private void clearAnimatorsRecord() {
        synchronized (animatorsRecord) {
            while (!animatorsRecord.isEmpty()) {
                try {
                    AppDebug.d(TAG, tagObjHashCode() + " cancel (clearAnimatorsRecord) ");
                    animatorsRecord.remove(0).cancel();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addToAnimatorsRecord(Animator animator) {
        synchronized (animatorsRecord) {
            while (!animatorsRecord.isEmpty()) {
                try {
                    AppDebug.d(TAG, tagObjHashCode() + " cancel (addToAnimatorsRecord) ");
                    animatorsRecord.remove(0).cancel();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            animatorsRecord.add(animator);
        }
    }

    /**
     * 在滚动完成后，执行任务
     * OSD short for ' on scroll done '
     *
     * @param task
     */
    private void postOSDTask(Runnable task) {
        synchronized (taskCache4ScrollDone) {
            taskCache4ScrollDone.add(task);
            while (taskCache4ScrollDone.size() > 1) {
                taskCache4ScrollDone.remove(0);
            }
        }
    }

    private boolean handleFirstTaskInScrollTaskCache() {
        synchronized (taskCache4ScrollDone) {
            if (!taskCache4ScrollDone.isEmpty()) {
                try {
                    taskCache4ScrollDone.remove(0).run();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 在数据更新并且布局结束后，执行一个任务
     * OANAL short for ' on after notify and layout '
     *
     * @param task
     */
    public void postOANALTask(Runnable task) {
        synchronized (taskCache4AfterNotifyAndLayout) {
            taskCache4AfterNotifyAndLayout.add(task);
            while (taskCache4AfterNotifyAndLayout.size() > 3) {
                taskCache4AfterNotifyAndLayout.remove(0);
            }
        }
    }

    private void handleTaskCache4PstNotifyAndLayout() {
        synchronized (taskCache4AfterNotifyAndLayout) {
            while (!taskCache4AfterNotifyAndLayout.isEmpty()) {
                try {
                    taskCache4AfterNotifyAndLayout.remove(0).run();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 在数据更新前，执行一个任务
     * OBNAL short for ' on before notify and layout '
     *
     * @param task
     */
    public void postOBNALTask(Runnable task) {
        synchronized (taskCache4BeforeNotifyAndLayout) {
            taskCache4BeforeNotifyAndLayout.add(task);
            while (taskCache4BeforeNotifyAndLayout.size() > 3) {
                taskCache4BeforeNotifyAndLayout.remove(0);
            }
        }
    }

    private void handleTaskCache4BeforeNotifyAndLayout() {
        synchronized (taskCache4BeforeNotifyAndLayout) {
            while (!taskCache4BeforeNotifyAndLayout.isEmpty()) {
                try {
                    taskCache4BeforeNotifyAndLayout.remove(0).run();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void layoutMoveBy(final int pos, final boolean immediately, final Runnable r) {
        Runnable scrollTask = new Runnable() {

            private void doImmediatelyMove() {
                immediatelyMovePos = pos;
                doLayoutMove();
            }

            @Override
            public void run() {
                AppDebug.d(TAG, tagObjHashCode() + " layoutMoveBy : " + pos);
                if (pos < 0 || pos > getAdapter().getItemCount() - 1) {
                    AppDebug.d(TAG, tagObjHashCode() + " scroll task not run cause pos not fit :" + pos + "," + (getAdapter().getItemCount() - 1));
                    handler.sendEmptyMessage(MSG_SCROLL_TASK_NOT_RUN);
                    return;
                }
                flag |= FLAG_scroll_doing;
                final AnimParams animParams = new AnimParams(pos, immediately, r);
                ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(10000);
                animator.setTarget(this);
                animator.setInterpolator(new LinearInterpolator());
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                    boolean topArrived = false;
                    boolean bottomArrived = false;

                    private void doAnimScroll(ValueAnimator animation) {
                        Pair<Integer, Integer> range = getLayoutRange();
                        stepY = 0;
                        if (pos < range.first) {
                            AppDebug.d(TAG, tagObjHashCode() + " " + animation.hashCode() + " doAnimScroll : a");
                            stepY = Math.min(getMeasuredHeight() / 2, animParams.getStep()) * 1;
                        } else if (pos > range.second) {
                            AppDebug.d(TAG, tagObjHashCode() + " " + animation.hashCode() + " doAnimScroll : b");
                            stepY = Math.min(getMeasuredHeight() / 2, animParams.getStep()) * -1;
                        } else {
                            AppDebug.d(TAG, tagObjHashCode() + " " + animation.hashCode() + " doAnimScroll : c");
                            ViewHolder vh = getViewHolder(pos);
                            if (vh != null && vh.itemView != null) {
                                Rect itemArea = new Rect();
                                Rect contentArea = new Rect();
                                vh.itemView.getDrawingRect(itemArea);
                                offsetDescendantRectToMyCoords(vh.itemView, itemArea);
                                getDrawingRect(contentArea);
                                int distance = itemArea.centerY() - contentArea.centerY();
                                AppDebug.d(TAG, tagObjHashCode() + " " + animation.hashCode()
                                        + " doAnimScroll : "
                                        + " pos=" + pos
                                        + " distance=" + distance
                                        + " itemArea=" + itemArea.toShortString()
                                        + " contentArea=" + contentArea.toShortString());
                                if (distance != 0) {
                                    int step = animParams.getStep();
                                    int littleStep = Math.min(Math.abs(distance), Math.abs(step));
                                    stepY = littleStep * ((distance > 0) ? (-1) : (1));
                                } else {
                                    stepY = 0;
                                }
                            } else {
                                AppDebug.d(TAG, tagObjHashCode() + " " + animation.hashCode() + " doAnimScroll : usingViewHolders broken !");
                                stepY = 0;
                            }
                        }

                        if (style == Style.edgeLimit) {
                            ViewHolder first = getViewHolder(range.first);
                            ViewHolder last = getViewHolder(range.second);
                            if (range.first == 0) {
                                if (first != null && first.itemView != null
                                        && first.itemView.getTop() + stepY >= getPaddingTop()
                                        && stepY > 0) {
                                    stepY = getPaddingTop() - first.itemView.getTop();
                                    AppDebug.d(TAG, tagObjHashCode() + " " + animation.hashCode() + " topArrived : " + stepY);
                                    topArrived = true;
                                }
                            }
                            if (range.second == getAdapter().getItemCount() - 1 && !topArrived) {
                                if (last != null && last.itemView != null
                                        && last.itemView.getBottom() + stepY <= getMeasuredHeight() - getPaddingBottom()
                                        && stepY < 0) {
                                    stepY = (getMeasuredHeight() - getPaddingBottom()) - last.itemView.getBottom();
                                    AppDebug.d(TAG, tagObjHashCode() + " " + animation.hashCode() + " bottomArrived : " + stepY);
                                    bottomArrived = true;
                                }
                            }
                        }

                        AppDebug.d(TAG, tagObjHashCode() + " " + animation.hashCode() + " doAnimScroll : " + animParams.frameCount
                                + "," + usingViewHolders.size() + "," + stepY);
                        if (stepY == 0) {
                            AppDebug.d(TAG, tagObjHashCode() + " cancel (stepY == 0) ");
                            animParams.endByUs = true;
                            animation.cancel();
                        } else {
                            doLayoutMove();
                        }
                    }

                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        AppDebug.d(TAG, tagObjHashCode() + " onAnimationUpdate " + animation.isStarted() + "&&" + animation.isRunning() +
                                " ," + animParams.toString());
                        if (animation.isStarted() && animation.isRunning()) {
                            animParams.count();
                            if (isDetachedFromWindow() || isNotifyDoing()) {
                                AppDebug.d(TAG, tagObjHashCode() + " cancel (isDetachedFromWindow() || isNotifyDoing()) " + isDetachedFromWindow() + "||" + isNotifyDoing());
                                animParams.endByUs = true;
                                animation.cancel();
                                return;
                            }
                            if (topArrived || bottomArrived) {
                                AppDebug.d(TAG, tagObjHashCode() + " cancel (topArrived || bottomArrived) " + topArrived + "||" + bottomArrived);
                                animParams.endByUs = true;
                                animation.cancel();
                                return;
                            }
                            if (usingViewHolders.size() > 0) {
                                if (!animParams.immediately) {
                                    doAnimScroll(animation);
                                } else {
                                    if (animation != null) {
                                        AppDebug.d(TAG, tagObjHashCode() + " cancel (doImmediatelyMove) ");
                                        animParams.endByUs = true;
                                        animation.cancel();
                                    }
                                    doImmediatelyMove();
                                }
                            } else {
                                AppDebug.d(TAG, tagObjHashCode() + " cancel (usingViewHolders.size() <=0) ");
                                animParams.endByUs = true;
                                animation.cancel();
                            }
                        }
                    }
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        AppDebug.d(TAG, tagObjHashCode() + " " + animation.hashCode() + " scroll start ! ");
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        AppDebug.d(TAG, tagObjHashCode() + " " + animation.hashCode() + " scroll end ! ");
                        flag &= ~FLAG_scroll_doing;
                        stepY = 0;
                        clearAnimatorsRecord();
                        if (!animParams.endByUs) {
                            // 有的系统（海尔HISIV510）存在Bug，自动结束我们的动画。
                            // 所以这里加个识别，立即滚动到那个位置
                            doImmediatelyMove();
                        }
                        if (animParams != null) {
                            try {
                                animParams.runTask();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                        handler.sendEmptyMessage(MSG_SCROLL_ANIM_END);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);
                        AppDebug.d(TAG, tagObjHashCode() + " " + animation.hashCode() + " scroll cancel ! ");
                    }
                });
                addToAnimatorsRecord(animator);
                animator.start();
                AppDebug.d(TAG, tagObjHashCode() + " " + animator.hashCode() + " call start ! ");
            }
        };

        if (isScrollDoing() || isNotifyDoing()) {
            AppDebug.d(TAG, tagObjHashCode() + " layoutMoveBy run delay cause : " +
                    "isScrollDoing()=" + isScrollDoing() +
                    ",isNotifyDoing()=" + isNotifyDoing());
            postOSDTask(scrollTask);
        } else {
            AppDebug.d(TAG, tagObjHashCode() + " layoutMoveBy run immediately ");
            scrollTask.run();
        }
    }

    public void scrollToPos(final int pos, final boolean immediately, final Runnable runnable) {
        if (adapter != null && !usingViewHolders.isEmpty()
                && pos >= 0 && pos < adapter.getItemCount()) {
            Runnable scrollTask = new Runnable() {
                @Override
                public void run() {
                    layoutMoveBy(pos, immediately, runnable);
                }
            };
            if (Looper.myLooper() == Looper.getMainLooper()) {
                scrollTask.run();
            } else {
                post(scrollTask);
            }
        }
    }

    private class AnimParams {
        long startTime = 0;
        long lastStepTime = 0;
        long frameCount = 0;
        float speed = 5;
        Runnable runOnceWhenEnd = null;
        boolean immediately;
        boolean endByUs = false;

        public AnimParams(final int pos, boolean immediately, Runnable task) {
            runOnceWhenEnd = task;
            this.immediately = immediately;
            startTime = System.currentTimeMillis();
            Pair<Integer, Integer> range = getLayoutRange();
            if (range.first != NO_POS && range.second != NO_POS) {
                if (pos < range.first - 10
                        || pos > range.second + 10) {
                    speed = 5;
                } else if (pos < range.first - 5
                        || pos > range.second + 5) {
                    speed = 3;
                } else if (pos < range.first
                        || pos > range.second) {
                    speed = 2;
                } else {
                    speed = 1f;
                }
            }
        }

        int getStep() {
            long now = System.currentTimeMillis();
            if (lastStepTime == 0) {
                lastStepTime = startTime;
            }
            int time = (int) (now - lastStepTime);
            time = (time == 0) ? (1) : (time);
            lastStepTime = now;
            int step = (int) (time * speed);
            step = (step == 0) ? (1) : (step);
            AppDebug.d(TAG, tagObjHashCode() + " " + this.hashCode() + " step:" + step);
            return step;
        }

        void runTask() {
            if (runOnceWhenEnd != null) {
                runOnceWhenEnd.run();
                runOnceWhenEnd = null;
            }
        }

        public void count() {
            frameCount++;
        }

        @Override
        public String toString() {
            return "AnimParams{" +
                    "startTime=" + startTime +
                    ", lastStepTime=" + lastStepTime +
                    ", frameCount=" + frameCount +
                    ", speed=" + speed +
                    ", runOnceWhenEnd=" + runOnceWhenEnd +
                    ", immediately=" + immediately +
                    ", endByUs=" + endByUs +
                    '}';
        }
    }

    private class OnLayoutParams {
        boolean changed;
        int l;
        int t;
        int r;
        int b;

        public OnLayoutParams(boolean changed, int l, int t, int r, int b) {
            this.changed = changed;
            this.l = l;
            this.t = t;
            this.r = r;
            this.b = b;
        }
    }

    public enum Style {
        noEdgeLimit, edgeLimit
    }

    public interface LayoutDoneListener {
        void onLayoutDone(int allItemsHeight, int viewHeight);
    }

    public static abstract class Adapter<VH extends ViewHolder> {
        private static final int MSG_VIEW_READY = 0x0001;

        private class DataSet extends Observable {
            @Override
            protected synchronized void setChanged() {
                super.setChanged();
            }

            @Override
            protected synchronized void clearChanged() {
                super.clearChanged();
            }
        }

        private DataSet dataSet = new DataSet();
        private Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_VIEW_READY: {
                        clearNotifyTaskCache();
                    }
                    break;
                }
            }
        };
        private ArrayList<Runnable> notifyTasksCache = new ArrayList<>();
        private WeakReference<FakeListView> fakeListViewWeakReference = null;

        public abstract VH onCreateViewHolder(ViewGroup parent, int viewType);

        public abstract void onBindViewHolder(VH holder, int position);

        public abstract int getItemCount();

        public int getItemViewType(int position) {
            return 0;
        }

        private void addIntoNotifyTaskCache(Runnable task) {
            synchronized (notifyTasksCache) {
                notifyTasksCache.add(task);
                while (notifyTasksCache.size() > 1) {
                    notifyTasksCache.remove(0);
                }
            }
        }

        private void clearNotifyTaskCache() {
            synchronized (notifyTasksCache) {
                for (int i = 0; i < notifyTasksCache.size(); i++) {
                    try {
                        notifyTasksCache.get(i).run();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                notifyTasksCache.clear();
            }
        }

        public boolean notifyDataSetChanged() {
            if (fakeListViewWeakReference.get() != null) {

                final Runnable safeNotifyTask = new Runnable() {
                    @Override
                    public void run() {
                        Runnable notifyTask = new Runnable() {
                            @Override
                            public void run() {
                                dataSet.setChanged();
                                dataSet.notifyObservers(new NotifyParam(NotifyType.all));
                            }
                        };
                        if (Looper.myLooper() == Looper.getMainLooper()) {
                            notifyTask.run();
                        } else {
                            handler.post(notifyTask);
                        }
                    }
                };

                if (!fakeListViewWeakReference.get().isViewReady()) {
                    addIntoNotifyTaskCache(safeNotifyTask);
                } else {
                    safeNotifyTask.run();
                }
                return true;
            }
            return false;
        }

        public boolean notifyItemChanged(final int pos) {
            if (fakeListViewWeakReference.get() != null) {
                Runnable safeNotifyTask = new Runnable() {
                    @Override
                    public void run() {
                        Runnable notifyTask = new Runnable() {
                            @Override
                            public void run() {
                                dataSet.setChanged();
                                dataSet.notifyObservers(new NotifyParam(NotifyType.single, pos));
                            }
                        };
                        if (Looper.myLooper() == Looper.getMainLooper()) {
                            notifyTask.run();
                        } else {
                            handler.post(notifyTask);
                        }
                    }
                };
                if (!fakeListViewWeakReference.get().isViewReady()) {
                    addIntoNotifyTaskCache(safeNotifyTask);
                } else {
                    safeNotifyTask.run();
                }
                return true;
            }
            return false;
        }

        enum NotifyType {
            all, range, single
        }

        class NotifyParam {
            NotifyType notifyType;
            Pair<Integer, Integer> range;
            int singlePos;

            public NotifyParam(NotifyType notifyType, int singlePos) {
                this.notifyType = notifyType;
                this.singlePos = singlePos;
            }

            public NotifyParam(NotifyType notifyType, int bgn, int end) {
                this.notifyType = notifyType;
                this.range = new Pair<>(bgn, end);
            }

            public NotifyParam(NotifyType notifyType) {
                this.notifyType = notifyType;
            }

            @Override
            public String toString() {
                return "NotifyParam{" +
                        "notifyType=" + notifyType +
                        ", range=" + range +
                        ", singlePos=" + singlePos +
                        '}';
            }
        }
    }

    public static abstract class ViewHolder {
        protected View itemView;

        private int posOld = NO_POS;
        private int posNow = NO_POS;
        private int viewType = 0;

        public int getPosNow() {
            return posNow;
        }

        private void setPosNow(int posNow) {
            this.posOld = this.posNow;
            this.posNow = posNow;
        }

        public int getViewType() {
            return viewType;
        }

        private void setViewType(int viewType) {
            this.viewType = viewType;
        }

        public ViewHolder(View itemView) {
            if (itemView == null) {
                throw new IllegalArgumentException("itemView may not be null");
            }
            this.itemView = itemView;
        }
    }
}
