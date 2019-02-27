/**
 * $
 * PROJECT NAME: TVTaobao
 * PACKAGE NAME: com.yunos.tvtaobao.activity.search
 * FILE NAME: HotWordView.java
 * CREATED TIME: 2015年5月20日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.search.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;
import com.yunos.tv.app.widget.focus.listener.FocusListener;
import com.yunos.tv.app.widget.focus.listener.ItemListener;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.focus.params.Params;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.Config;
import com.yunos.tvtaobao.search.R;

import java.util.ArrayList;

/**
 * Class Descripton.
 * @version
 * @author mi.cao
 * @data 2015年5月20日 下午8:42:36
 */
public class HotWordView extends View implements ItemListener, FocusListener {

    private String TAG = "HotWordView";
    private Params mParams = new Params(1.1f, 1.1f, 10, null, true, 20, new AccelerateDecelerateFrameInterpolator());

    // 热门词信息
    private ArrayList<ItemInfo> mItemInfoList;
    // 热门词
    private ArrayList<String> mHotWordList;
    // 画笔
    private Paint mPaint;
    // 字体尺寸
    private FontMetrics mFontMetrics;
    private PaintFlagsDrawFilter mPaintFlagsDrawFilter;
    // 不聚焦时点9图
    private NinePatch mItemUnfocusBg;
    // 聚焦时点9图
    private NinePatch mItemFocusBg;
    // 热门词显示行数，默认为3行
    private int mHotWordlines;

    private Rect mClipFocusRect = new Rect(); // 默认focus框

    public class ItemInfo {

        public int index;// 索引
        public int line; // 所在的行
        public  String text; // 文本
        public Rect rectBg = new Rect(); // 背景图片位置
        public int textLeftMargin; // 文本绘制左边距
        public int textBaseLine; // 文本绘制基线
        public boolean selected; // 是否被选中

        @Override
        public String toString() {
            String str = "index = " + index + ".line = " + line + ".text = " + text + ".rectBg = " + rectBg.toString()
                    + ".textleftMargin = " + textLeftMargin + ". textBaseLine = " + textBaseLine + ". selected = "
                    + selected;

            return str;
        }
    }

    public HotWordView(Context context) {
        super(context);
        init();
    }

    public HotWordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HotWordView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * 出死或坏数据
     */
    private void init() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        mPaint = new Paint();
        mPaint.setTextSize(getResources().getDimension(R.dimen.sp_22));
        mPaint.setColor(getResources().getColor(android.R.color.white));
        mFontMetrics = mPaint.getFontMetrics();
        mHotWordlines = 3;

        mPaintFlagsDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ytm_search_keyword_button_bg);
        mItemUnfocusBg = new NinePatch(bitmap, bitmap.getNinePatchChunk(), null);

        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ytm_search_keyword_button_focus_bg);
        mItemFocusBg = new NinePatch(bitmap, bitmap.getNinePatchChunk(), null);
    }

    /**
     * 设置显示的关键字内容
     * @param hotWordList
     */
    public void setHotWordList(ArrayList<String> hotWordList) {
        mHotWordList = hotWordList;
        if (Config.isDebug()) {
            AppDebug.v(TAG, TAG + ".setHotWordList. getMeasuredWidth = " + getMeasuredWidth()
                    + ", getMeasuredHeight = " + getMeasuredHeight() + ",system_version = "
                    + android.os.Build.VERSION.RELEASE + ", model = " + android.os.Build.MODEL);
        }

        // 重新测量view的尺寸
        measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.AT_MOST));
        // TODO 通过系统版本号来区分是否需要调用requestLayout
        requestLayout();
    }

    /**
     * 设置热门词行数，默认为3行
     * @param lines
     */
    public void setHotWordLines(int lines) {
        mHotWordlines = lines;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (Config.isDebug()) {
            AppDebug.v(TAG, TAG + ".onMeasure.getWidth = " + getWidth() + ", getHeight = " + getHeight());
        }

        if (getWidth() > 0 && getHeight() > 0) {
            setViewLayout();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mItemInfoList == null) {
            return;
        }
        canvas.setDrawFilter(mPaintFlagsDrawFilter);// 抗据此
        for (int i = 0; i < mItemInfoList.size(); i++) {
            ItemInfo itemInfo = mItemInfoList.get(i);
            NinePatch np = mItemUnfocusBg;
            if (itemInfo.selected) {
                AppDebug.v(TAG, TAG + ".onDraw.selected = " + itemInfo.selected + ". i = " + i);
                np = mItemFocusBg;
            }
            // 画背景
            np.draw(canvas, itemInfo.rectBg);
            // 画文字
            canvas.drawText(itemInfo.text, itemInfo.textLeftMargin, itemInfo.textBaseLine, mPaint);
        }

        super.onDraw(canvas);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        AppDebug.v(TAG, TAG + ". ============onFocusChange. gainFocus = " + gainFocus + ". direction = " + direction
                + ".previouslyFocusedRect = " + previouslyFocusedRect);
        if (mItemInfoList != null && mItemInfoList.size() > 0) {
            if (gainFocus) {
                ItemInfo itemInfo = getCurItemInfo();
                if (itemInfo == null) {// 如果当前没有默认焦点，则聚焦在第一个
                    itemInfo = mItemInfoList.get(0);
                }
                itemInfo.selected = true;
            } else {
                // 失去焦点时，清除所有的选择
                for (int i = 0; i < mItemInfoList.size(); i++) {
                    ItemInfo itemInfo = mItemInfoList.get(i);
                    if (itemInfo != null) {
                        itemInfo.selected = false;
                    }
                }
            }
        }

        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            float x = event.getX();// 鼠标的点击位置
            float y = event.getY();
            AppDebug.i(TAG, TAG + ".onTouchEvent.x = " + x + ", y = " + y + ", mItemInfoList = " + mItemInfoList);
            if (mItemInfoList != null && mItemInfoList.size() > 0) {
                for (int i = 0; i < mItemInfoList.size(); i++) {
                    ItemInfo item = mItemInfoList.get(i);
                    if (item != null && item.rectBg != null && item.rectBg.contains((int) x, (int) y)) {
                        if (item != null && mOnHotWordViewKeyDownListener != null) {
                            mOnHotWordViewKeyDownListener.onHotWordViewKeyDown(item);
                        }
                        break;
                    }
                }
            }
        }

        return super.onTouchEvent(event);
    }

    /**
     * 设置绘制位置
     */
    private void setViewLayout() {
        if (mHotWordList == null || mHotWordList.size() == 0 || mHotWordlines == 0) {
            AppDebug.d(TAG, TAG + ".mHotWordList == null || mHotWordList.size() == 0 || mHotWordlines == 0");
            setFocusable(false);
            setVisibility(View.GONE);
            return;
        }

        setFocusable(true);
        setVisibility(View.VISIBLE);

        int viewHeight = 0;// 整个view高度
        int textMargin = (int) getResources().getDimension(R.dimen.dp_18);// 文本两边距胶囊的边距
        int verInterval = (int) getResources().getDimension(R.dimen.dp_20); // 垂直距离
        int horInterval = (int) getResources().getDimension(R.dimen.dp_20); // 水平间距
        int itemHeight = mItemFocusBg.getHeight();// 胶囊的高度
        AppDebug.v(TAG, TAG + ".setViewLayout.itemHeight = " + itemHeight + ". getWidth = " + getWidth()
                + ".getHeight = " + getHeight() + ".getLeft = " + getLeft() + ". getRight = " + getRight());

        int line = 0;//从第一行开始
        int leftMargin = 0;// 胶囊的左边距

        if (mItemInfoList == null) {
            mItemInfoList = new ArrayList<ItemInfo>();
        }
        mItemInfoList.clear();

        for (int i = 0; i < mHotWordList.size(); i++) {
            if (viewHeight == 0) {
                viewHeight = itemHeight + verInterval;// view的高度
            }
            float textWidth = mPaint.measureText(mHotWordList.get(i));
            int itemWidth = (int) (textWidth + textMargin * 2);// 胶囊的宽度

            ItemInfo itemInfo = new ItemInfo();
            int left = leftMargin;
            int top = line * (itemHeight + verInterval);
            int right = left + itemWidth;
            int bottom = top + itemHeight;
            if (right > getRight()) {//如果胶囊的右边距大于view的右边距,则转行
                line++;
                if (line >= mHotWordlines) {
                    break;
                }
                viewHeight += (itemHeight + verInterval);//追加view的高度
                leftMargin = 0;
                left = leftMargin;
                top = line * (itemHeight + verInterval);
                right = left + itemWidth;
                bottom = top + itemHeight;
            }

            leftMargin += (itemWidth + horInterval);// 左边距向右偏移一个水平间距
            itemInfo.index = i;
            itemInfo.line = line;
            itemInfo.rectBg.set(left, top, right, bottom);
            itemInfo.text = mHotWordList.get(i);
            int textBaseLine = (int) (itemHeight + (mFontMetrics.descent - mFontMetrics.ascent)) / 2
                    - (int) getResources().getDimension(R.dimen.dp_4) + top;// 文本的基准线
            itemInfo.textBaseLine = textBaseLine;
            itemInfo.textLeftMargin = left + textMargin;
            itemInfo.selected = false;
            mItemInfoList.add(itemInfo);
        }

        setMeasuredDimension(getWidth(), viewHeight);
    }

    /**
     * 热门词焦点控制事件
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onInnerKey(int keyCode, KeyEvent event) {
        if (mItemInfoList == null) {
            return false;
        }

        AppDebug.v(TAG, TAG + ".onInnerKey.keyCode = " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (moveLeft()) {// 可以向左移动
                    invalidate();
                    return true;
                } else {
                    return false;
                }
            case KeyEvent.KEYCODE_DPAD_UP:
                if (moveTop()) {// 向上移动
                    invalidate();
                    return true;
                } else {
                    return false;
                }
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (moveRight()) {// 向右移动
                    invalidate();
                    return true;
                } else {
                    return false;
                }
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (moveBottom()) {// 向下移动
                    invalidate();
                    return true;
                } else {
                    return false;
                }

            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
                if (mOnHotWordViewKeyDownListener != null) {
                    ItemInfo curItemInfo = getCurItemInfo();
                    if (curItemInfo != null) {
                        mOnHotWordViewKeyDownListener.onHotWordViewKeyDown(curItemInfo);
                        return true;
                    }
                }
                return false;
            default:
                return false;
        }
    }

    /**
     * 向左移动
     * @return
     */
    private boolean moveLeft() {
        if (mItemInfoList == null) {
            return false;
        }

        ItemInfo curItemInfo = getCurItemInfo();
        if (curItemInfo != null) {
            for (int i = 0; i < mItemInfoList.size(); i++) {
                ItemInfo itemInfo = mItemInfoList.get(i);
                // 存在跟当前选择的item处在同一行，索引小1的item项
                if (itemInfo.line == curItemInfo.line && itemInfo.index == curItemInfo.index - 1) {
                    AppDebug.v(TAG, TAG + ".moveLeft.itemInfo ===== " + itemInfo);
                    curItemInfo.selected = false;// 只有找到下一个时才取消当前选择
                    itemInfo.selected = true;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 向右移动
     * @return
     */
    private boolean moveRight() {
        if (mItemInfoList == null) {
            return false;
        }

        ItemInfo curItemInfo = getCurItemInfo();
        if (curItemInfo != null) {
            for (int i = 0; i < mItemInfoList.size(); i++) {
                ItemInfo itemInfo = mItemInfoList.get(i);
                // 存在跟当前选择的item处在同一行，索引大1的item项
                if (itemInfo.line == curItemInfo.line && itemInfo.index == curItemInfo.index + 1) {
                    AppDebug.v(TAG, TAG + ".moveRight.itemInfo ===== " + itemInfo);
                    curItemInfo.selected = false;// 只有找到下一个时才取消当前选择
                    itemInfo.selected = true;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 向上移动
     * @return
     */
    private boolean moveTop() {
        if (mItemInfoList == null) {
            return false;
        }

        ItemInfo curItemInfo = getCurItemInfo();
        if (curItemInfo != null) {
            if (curItemInfo.line == 0) {// 当前已经处在第一行了
                return false;
            }

            // 找出与当前选择项中间线最接近的作为下一个选择项
            int curMidLine = (curItemInfo.rectBg.left + curItemInfo.rectBg.right) / 2;
            int distance = -1;
            ItemInfo nextItemInfo = null;
            for (int i = 0; i < mItemInfoList.size(); i++) {
                ItemInfo itemInfo = mItemInfoList.get(i);
                if (itemInfo.line == curItemInfo.line - 1) {
                    int midLine = (itemInfo.rectBg.left + itemInfo.rectBg.right) / 2;
                    int d = Math.abs(curMidLine - midLine);
                    if (distance < 0) {// 第一次 初始化两个变量
                        distance = d;
                        nextItemInfo = itemInfo;
                    } else {
                        if (d < distance) {// 当下一项时 它们之间的间距更小时，更新备选项
                            distance = d;
                            nextItemInfo = itemInfo;
                        }
                    }
                }
            }
            if (nextItemInfo != null) {
                AppDebug.v(TAG, TAG + ".moveTop.itemInfo ===== " + nextItemInfo);
                nextItemInfo.selected = true;
                curItemInfo.selected = false; // 只有找到下一个时取消当前选择
                return true;
            }
        }
        return false;
    }

    /**
     * 向下移动
     * @return
     */
    private boolean moveBottom() {
        if (mItemInfoList == null) {
            return false;
        }

        ItemInfo curItemInfo = getCurItemInfo();
        if (curItemInfo != null) {
            if (curItemInfo.line == mHotWordlines - 1) {// 当前已经处在最下面一行了
                return false;
            }

            // 找出与当前选择项中间线最接近的作为下一个选择项
            int curMidLine = (curItemInfo.rectBg.left + curItemInfo.rectBg.right) / 2;
            int distance = -1;
            ItemInfo nextItemInfo = null;
            for (int i = 0; i < mItemInfoList.size(); i++) {
                ItemInfo itemInfo = mItemInfoList.get(i);
                if (itemInfo.line == curItemInfo.line + 1) {
                    int midLine = (itemInfo.rectBg.left + itemInfo.rectBg.right) / 2;
                    int d = Math.abs(curMidLine - midLine);
                    if (distance < 0) {// 第一次 初始化两个变量
                        distance = d;
                        nextItemInfo = itemInfo;
                    } else {
                        if (d < distance) {// 当下一项时 它们之间的间距更小时，更新备选项
                            distance = d;
                            nextItemInfo = itemInfo;
                        }
                    }
                }
            }
            if (nextItemInfo != null) {
                AppDebug.v(TAG, TAG + ".moveBottom.itemInfo ===== " + nextItemInfo);
                nextItemInfo.selected = true;
                curItemInfo.selected = false; // 只有找到下一个时取消当前选择
                return true;
            }
        }
        return false;
    }

    /**
     * 得到当前选择的item项
     * @return
     */
    private ItemInfo getCurItemInfo() {
        if (mItemInfoList == null || mItemInfoList.size() == 0) {
            return null;
        }

        for (int i = 0; i < mItemInfoList.size(); i++) {
            ItemInfo itemInfo = mItemInfoList.get(i);
            if (itemInfo != null && itemInfo.selected) {
                return itemInfo;
            }
        }

        return null;
    }

    private OnHotWordViewKeyDownListener mOnHotWordViewKeyDownListener;

    public void setOnHotWordViewKeyDownListener(OnHotWordViewKeyDownListener l) {
        mOnHotWordViewKeyDownListener = l;
    }

    public interface OnHotWordViewKeyDownListener {

        public void onHotWordViewKeyDown(ItemInfo itemInfo);
    }

    @Override
    public boolean isScale() {
        return false;
    }

    @Override
    public FocusRectParams getFocusParams() {
        Rect r = new Rect();
        getFocusedRect(r);
        return new FocusRectParams(r, 0.5f, 0.5f);
    }

    @Override
    public int getItemWidth() {
        return getWidth();
    }

    @Override
    public int getItemHeight() {
        return getHeight();
    }

    @Override
    public Rect getManualPadding() {
        return null;
    }

    @Override
    public void drawBeforeFocus(Canvas canvas) {
    }

    @Override
    public void drawAfterFocus(Canvas canvas) {
    }

    @Override
    public boolean isFinished() {
        return true;
    }

    @Override
    public boolean canDraw() {
        return true;
    }

    @Override
    public boolean isAnimate() {
        return true;
    }

    @Override
    public ItemListener getItem() {
        return this;
    }

    @Override
    public boolean isScrolling() {
        return false;
    }

    @Override
    public Params getParams() {
        if (mParams == null) {
            throw new IllegalArgumentException("The params is null, you must call setScaleParams before it's running");
        }

        return mParams;
    }

    @Override
    public boolean isFocusBackground() {
        return false;
    }

    @Override
    public boolean preOnKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                return true;

            default:
                break;
        }
        return false;
    }

    @Override
    public void onFocusStart() {
    }

    @Override
    public void onFocusFinished() {
    }

    /*
     * (non-Javadoc)
     * @see com.yunos.tv.app.widget.focus.listener.FocusListener#getClipFocusRect()
     */
    @Override
    public Rect getClipFocusRect() {
        //TODO Auto-generated method stub
        if (mClipFocusRect != null) {
            return mClipFocusRect;
        }
        return new Rect();
    }

}
