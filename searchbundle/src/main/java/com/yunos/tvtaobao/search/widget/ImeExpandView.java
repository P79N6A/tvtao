package com.yunos.tvtaobao.search.widget;


import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.search.R;
import com.yunos.tvtaobao.search.activity.SearchActivity;
import com.yunos.tvtaobao.search.bean.GridItem;

/**
 * @author yanzhi.pm
 * @date 2014-4-28 下午10:31:02
 */
public class ImeExpandView extends FrameLayout {

    private final String tag = "ImeExpandView";
    private PopupWindow mPopupWindow;
    private OnTextSelectedListener mListener;
    private OnImeExpandViewDismiss mOnImeExpandViewDismiss;
    private GridItem mGridItem;

    public TextView center;
    public TextView left;
    public TextView top;
    public TextView right;
    public TextView bottom;

    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            center.requestFocus();
            hidePopWindow();
        }

        ;
    };

    public interface OnTextSelectedListener {

        void onTextSelected(String text);
    }

    public interface OnImeExpandViewDismiss {

        void onImeExpandViewDismiss();
    }

    public ImeExpandView(Context context) {
        super(context);
    }

    public ImeExpandView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImeExpandView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        center = (TextView) findViewById(R.id.ime_py_center);
        left = (TextView) findViewById(R.id.ime_py_left);
        top = (TextView) findViewById(R.id.ime_py_top);
        right = (TextView) findViewById(R.id.ime_py_right);
        bottom = (TextView) findViewById(R.id.ime_py_bottom);

        center.setOnClickListener(mOnClickListener);
        left.setOnClickListener(mOnClickListener);
        top.setOnClickListener(mOnClickListener);
        right.setOnClickListener(mOnClickListener);
        bottom.setOnClickListener(mOnClickListener);

        left.setOnFocusChangeListener(mTextViewOnFocusChangeListener);
        top.setOnFocusChangeListener(mTextViewOnFocusChangeListener);
        right.setOnFocusChangeListener(mTextViewOnFocusChangeListener);
        bottom.setOnFocusChangeListener(mTextViewOnFocusChangeListener);
    }

    public void setParams(PopupWindow popupWindow, GridItem item, OnTextSelectedListener l) {
        this.mPopupWindow = popupWindow;
        this.mListener = l;
        this.mGridItem = item;
        setTop(item.top);
        setLeft(item.left);
        setCenter(item.center);
        setRight(item.right);
        setBottom(item.bottom);

        center.requestFocus();
    }

    public void setOnImeExpandViewDismiss(OnImeExpandViewDismiss l) {
        mOnImeExpandViewDismiss = l;
    }

    private OnFocusChangeListener mTextViewOnFocusChangeListener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            AppDebug.v(tag, "mTextViewOnFocusChangeListener.onFocusChange  " + hasFocus);
            if (hasFocus) {
                if (v instanceof TextView) {
                    String text = ((TextView) v).getText().toString();
                    mListener.onTextSelected(text);
                    hidePopup();
                }
            }
        }
    };

    private OnClickListener mOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v instanceof TextView) {
                String text = ((TextView) v).getText().toString();
                mListener.onTextSelected(text);
                hidePopup();
            }
        }
    };

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_ESCAPE || event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

                hidePopWindow();
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    ;

    public void hidePopWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            mPopupWindow = null;

            if (mOnImeExpandViewDismiss != null) {
                mOnImeExpandViewDismiss.onImeExpandViewDismiss();
            }
        }
    }

    private void hidePopup() {
        mHandler.sendEmptyMessageDelayed(0, 200);
    }

    private void setCenter(String w) {
        setWords(center, w);
    }

    private void setLeft(String w) {
        setWords(left, w);
    }

    private void setTop(String w) {
        setWords(top, w);
    }

    private void setRight(String w) {
        setWords(right, w);
    }

    private void setBottom(String w) {
        setWords(bottom, w);
    }

    private void setWords(TextView v, String w) {
        if (v != null) {
            if (w != null) {
                v.setText(w);
                v.setFocusable(true);
            } else {
                v.setText("");
                v.setFocusable(false);
            }
        }
    }

}
