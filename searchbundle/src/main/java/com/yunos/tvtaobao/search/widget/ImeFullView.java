package com.yunos.tvtaobao.search.widget;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.yunos.tv.app.widget.FrameLayout;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.AppInfo;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.search.R;
import com.yunos.tvtaobao.search.activity.KeySearchActivity;
import com.yunos.tvtaobao.search.bean.GridItem;

import java.util.Map;

/**
 * <pre>
 *     author : pan
 *     time   : 2018/12/06
 *     desc   : 输入法全键盘
 *     version: 1.0
 * </pre>
 */
public class ImeFullView extends ConstraintLayout implements View.OnClickListener {
    private final String TAG = "ImeFullView";

    private ImeExpandView mPinyinExtendView;
    private ConstraintLayout mImeBoard;
    private ImageView item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, item12;
    // 弹出窗口
    private PopupWindow mPopupWindow;
    private SparseArray<GridItem> sGridItems;

    private OnKeyWordDetermineListener mImeKeyWordListener;
    public View curFocusView;

    public interface OnKeyWordDetermineListener {
        void onClicked(String display);

        void onDelete();

        void onClear();
    }

    public ImeFullView(Context context) {
        super(context);
        initView(context);
    }

    public ImeFullView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ImeFullView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    void initView(Context context) {

        try {
            mImeBoard = (ConstraintLayout) LayoutInflater.from(context).inflate(
                    R.layout.ytm_ime_keyboard_layout, this);

            mPinyinExtendView = (ImeExpandView) LayoutInflater.from(context).inflate(
                    R.layout.ytm_ime_expand_view, null);
        } catch (InflateException e) {
            e.printStackTrace();
            return;
        }

        initGridViewData();

        item1 = (ImageView) mImeBoard.findViewById(R.id.item1);
        item2 = (ImageView) mImeBoard.findViewById(R.id.item2);
        item3 = (ImageView) mImeBoard.findViewById(R.id.item3);
        item4 = (ImageView) mImeBoard.findViewById(R.id.item4);
        item5 = (ImageView) mImeBoard.findViewById(R.id.item5);
        item6 = (ImageView) mImeBoard.findViewById(R.id.item6);
        item7 = (ImageView) mImeBoard.findViewById(R.id.item7);
        item8 = (ImageView) mImeBoard.findViewById(R.id.item8);
        item9 = (ImageView) mImeBoard.findViewById(R.id.item9);
        item10 = (ImageView) mImeBoard.findViewById(R.id.item10);
        item11 = (ImageView) mImeBoard.findViewById(R.id.item11);
        item12 = (ImageView) mImeBoard.findViewById(R.id.item12);


        item1.setOnClickListener(this);
        item2.setOnClickListener(this);
        item3.setOnClickListener(this);
        item4.setOnClickListener(this);
        item5.setOnClickListener(this);
        item6.setOnClickListener(this);
        item7.setOnClickListener(this);
        item8.setOnClickListener(this);
        item9.setOnClickListener(this);
        item10.setOnClickListener(this);
        item11.setOnClickListener(this);
        item12.setOnClickListener(this);

        item1.setOnFocusChangeListener(new FocusChangeListener());
        item2.setOnFocusChangeListener(new FocusChangeListener());
        item3.setOnFocusChangeListener(new FocusChangeListener());
        item4.setOnFocusChangeListener(new FocusChangeListener());
        item5.setOnFocusChangeListener(new FocusChangeListener());
        item6.setOnFocusChangeListener(new FocusChangeListener());
        item7.setOnFocusChangeListener(new FocusChangeListener());
        item8.setOnFocusChangeListener(new FocusChangeListener());
        item9.setOnFocusChangeListener(new FocusChangeListener());
        item10.setOnFocusChangeListener(new FocusChangeListener());
        item11.setOnFocusChangeListener(new FocusChangeListener());
        item12.setOnFocusChangeListener(new FocusChangeListener());

        setFocusable(true);
    }

    @Override
    public void onClick(View view) {
        onImeItemClicked(view);
    }

    public void requestFocused() {
        if (curFocusView != null) {
            curFocusView.requestFocus();
        } else {
            item5.requestFocus();
        }
    }

    /**
     * 焦点回到键盘页时，获取上一次离开时的焦点
     * @param focused
     * @param direction
     * @return
     */
    public View findLastFocused(View focused, int direction) {
        if (direction == FOCUS_LEFT) {
            ViewParent parent = focused.getParent();
            if (!(parent instanceof ImeFullView)) {
                if (curFocusView != null) {
                    return curFocusView;
                }
            }
        }
        return null;
    }

    /**
     * 设置键盘点击监听
     * @param listener
     */
    public void setImeKeyWordListener(OnKeyWordDetermineListener listener) {
        this.mImeKeyWordListener = listener;
    }

    /**
     * 初始化搜索头字母
     */
    private void initGridViewData() {
        if (sGridItems == null) {
            sGridItems = new SparseArray<GridItem>();
            sGridItems.put(1, new GridItem("1"));
            sGridItems.put(2, new GridItem("B", "A", "2", "C", null, true));
            sGridItems.put(3, new GridItem("E", "D", "3", "F", null, true));
            sGridItems.put(4, new GridItem("H", "G", "4", "I", null, true));
            sGridItems.put(5, new GridItem("K", "J", "5", "L", null, true));
            sGridItems.put(6, new GridItem("N", "M", "6", "O", null, true));
            sGridItems.put(7, new GridItem("Q", "P", "7", "R", "S", true));
            sGridItems.put(8, new GridItem("U", "T", "8", "V", null, true));
            sGridItems.put(9, new GridItem("X", "W", "9", "Y", "Z", true));
            sGridItems.put(10, new GridItem("清空", GridItem.DISPATCH_CLEAR));
            sGridItems.put(11, new GridItem("0"));
            sGridItems.put(12, new GridItem("删除", GridItem.DISPATCH_DELETE));
        }
    }

    /**
     * 在XML中配置 键盘的点击事件
     * @param v
     */
    public void onImeItemClicked(View v) {
        AppDebug.i(TAG, TAG + ".onImeItemClicked vId : " + v.getId());
        Object tag = v.getTag();
        if (tag != null && TextUtils.isDigitsOnly(String.valueOf(tag))) {
            int itemPosition = Integer.parseInt(String.valueOf(tag));
            GridItem gridItem = sGridItems.get(itemPosition);
            if (gridItem.dispatchMode == GridItem.DISPATCH_CLEAR) {
                if (mImeKeyWordListener != null) {
                    mImeKeyWordListener.onClear();
                }
            } else if (gridItem.dispatchMode == GridItem.DISPATCH_DELETE) {
                if (mImeKeyWordListener != null) {
                    mImeKeyWordListener.onDelete();
                }
            } else {
                if (!gridItem.isExpand) {
                    if (mImeKeyWordListener != null) {
                        mImeKeyWordListener.onClicked(gridItem.center);
                    }
                } else {
                    showExpandPopup(v, gridItem);
                }
            }
        }
    }

    /**
     * 显示键盘的扩展界面
     * @param v
     * @param gridItem
     */
    private void showExpandPopup(View v, GridItem gridItem) {
        if (mPinyinExtendView == null) {
            return;
        }
        hidePopWindow();
        if (mPopupWindow == null) {
            mPopupWindow = new PopupWindow(mPinyinExtendView, getResources().getDimensionPixelSize(
                    R.dimen.dp_213), getResources().getDimensionPixelSize(R.dimen.dp_213));
        }
        mPinyinExtendView.setParams(mPopupWindow, gridItem, new ImeExpandView.OnTextSelectedListener() {

            @Override
            public void onTextSelected(String text) {
                if (mImeKeyWordListener != null) {
                    mImeKeyWordListener.onClicked(text);
                }
            }
        });

        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setFocusable(true);

        AppDebug.v(TAG, TAG + ".showExpandPopup v = " + v + " width=" + v.getWidth() + " height=" + v.getHeight());
        mPopupWindow.showAsDropDown(v, -v.getWidth() / 2,
                -(v.getHeight() / 2 + (int) getResources().getDimension(R.dimen.dp_106_67)));

        //当popWindow出来后改变键盘状态
        changeImeBoardStatus(true);
        //当popWindow消失后 恢复键盘状态
        mPinyinExtendView.setOnImeExpandViewDismiss(new ImeExpandView.OnImeExpandViewDismiss() {

            @Override
            public void onImeExpandViewDismiss() {
                    changeImeBoardStatus(false);
            }
        });
    }

    /**
     * 隐藏键盘的扩展界面
     */
    void hidePopWindow() {
        if (mPinyinExtendView != null) {
            mPinyinExtendView.hidePopWindow();
        }
    }

    // 改变键盘状态
    void changeImeBoardStatus(boolean bShowPopWindow) {
        for (int i = 0; i < mImeBoard.getChildCount(); i++) {
            View view = mImeBoard.getChildAt(i);
            if (bShowPopWindow) {
                view.setEnabled(false);
            } else {
                view.setEnabled(true);
            }
        }
    }

    private class FocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View view, boolean b) {
            AppDebug.e(TAG, TAG + ".onFocusChange : " + view);
            if (b) {
                curFocusView = view;
            }
        }
    }

    /**
     * 设置键盘右移聚焦到的id上
     * @param id
     */
    public void changeNextFocusRightId(int id) {
        item3.setNextFocusRightId(id);
        item6.setNextFocusRightId(id);
        item9.setNextFocusRightId(id);
        item12.setNextFocusRightId(id);
    }

    /**
     * 设置键盘左移聚焦到的id上
     * @param id
     */
    public void changeNextFocusLeftId(int id) {
        item1.setNextFocusLeftId(id);
        item4.setNextFocusLeftId(id);
        item7.setNextFocusLeftId(id);
        item10.setNextFocusLeftId(id);
    }
}
