package com.yunos.tvtaobao.search.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.search.R;
import com.yunos.tvtaobao.search.bean.TabData;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <pre>
 *     author : panbeixing
 *     time   : 2018/12/12
 *     desc   : 搜索页面左侧Tab
 *     version: 1.0
 * </pre>
 */
public class TabView extends LinearLayout implements View.OnFocusChangeListener {
    private static final String TAG = "TabView";
    private View curFocusItem, lastFocusItem;
    private View firstItem, lastItem;
    private TimerHandler timerHandler;
    private long DELAY_TIME = 500;
    private Context context;

    private OnItemLongFocusListener focusListener;

    private TabData tabData;

    public interface OnItemLongFocusListener {
        void onItemLongFocus(int tabType);
    }

    public TabView(Context context) {
        super(context);
        init(context);
    }

    public TabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TabView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setTabData(TabData data) {
        this.tabData = data;
        refreshView();
    }

    /**
     *
     */
    private void refreshView() {

        int size = tabData.Datas.size();
        for (int i = 0 ; i < size ; i++) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.height = context.getResources().getDimensionPixelOffset(R.dimen.dp_44);
            lp.width = context.getResources().getDimensionPixelOffset(R.dimen.dp_160);
            TextView textView = new TextView(context);
            textView.setText(tabData.Datas.get(i).getContent());
            textView.setPadding(0, 0, context.getResources().getDimensionPixelOffset(R.dimen.dp_22), 0);
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            if (tabData.Datas.get(i).hasFocus()) {
                lp.topMargin = context.getResources().getDimensionPixelOffset(R.dimen.dp_4);
                if (firstItem == null) {
                    firstItem = textView;
                }
                textView.setFocusable(true);
                textView.setTextColor(Color.parseColor("#ffffff"));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.sp_20));
                textView.setTag(i);
                textView.setOnFocusChangeListener(this);
                lastItem = textView;
            } else {
                textView.setTextColor(Color.parseColor("#7898ba"));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimension(R.dimen.sp_24));
                if (i > 0) {
                    lp.topMargin = context.getResources().getDimensionPixelOffset(R.dimen.dp_28);
                }
            }
            textView.setLayoutParams(lp);

            addView(textView);
        }

        firstItem.requestFocus();
    }

    private void init(Context context) {
        this.context = context;

        timerHandler = new TimerHandler(new WeakReference<TabView>(this));
        setOrientation(VERTICAL);
        setGravity(Gravity.RIGHT);
        setPadding(0, context.getResources().getDimensionPixelOffset(R.dimen.dp_114), 0, 0);
    }

    public void setItemLongFocusListener(OnItemLongFocusListener listener) {
        this.focusListener = listener;
    }

    @Override
    public View focusSearch(View focused, int direction) {
        View view = super.focusSearch(focused, direction);
        AppDebug.i(TAG, "focusSearch view : " + view + " ,direction : " + direction);
        AppDebug.i(TAG, "focusSearch curFocusItem : " + curFocusItem);
        if (view == null) {
            return curFocusItem;
        }
        if (view instanceof TextView) {
            if (curFocusItem != null) {
                curFocusItem.setBackgroundDrawable(null);
            }

            if (direction == FOCUS_DOWN || direction == FOCUS_UP) {
                curTime = System.currentTimeMillis();

                AppDebug.e(TAG, TAG + " curTime : " + curTime + " ,lastTime : " + lastTime);
                if (curTime - lastTime < DELAY_TIME) {
                    timerHandler.removeMessages(0);
                }


                int tag = (int) view.getTag();
                timerHandler.sendMessageDelayed(timerHandler.obtainMessage(0, tag), DELAY_TIME);
                lastTime = curTime;
            }
        } else {
            if (curFocusItem != null) {
                curFocusItem.setBackgroundResource(R.drawable.bg_tab_text_unfocus);
            }
        }

        return view;
    }

    @Override
    public View findFocus() {
        View view = super.findFocus();
        AppDebug.i(TAG, "findFocus view : " + view);
        return view;
    }

    private long curTime = 0;
    private long lastTime = 0;

    @Override
    public void onFocusChange(View view, boolean b) {
        AppDebug.i(TAG, "onFocusChange view : " + view + " ,b : " + b);
        if (view instanceof TextView) {
            if (b) {
                if (lastFocusItem != null) {
                    lastFocusItem.setBackgroundDrawable(null);
                }

                curFocusItem = view;
                view.setBackgroundResource(R.drawable.bg_tab_text_focus);
            } else {
                lastFocusItem = view;

                view.setBackgroundResource(R.drawable.bg_tab_text_unfocus);
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (firstItem.hasFocus() && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            return true;
        }
        if (lastItem.hasFocus() && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public View findNextFocused(View focused, int direction) {
        AppDebug.i(TAG, TAG + ".findNextFocused lastFocusItem : " + lastFocusItem);
        if (direction == FOCUS_LEFT) {
            if (lastFocusItem != null) {
                return lastFocusItem;
            } else {
                return firstItem;
            }
        }
        return null;
    }

    private static class TimerHandler extends Handler {
        private WeakReference<TabView> tabViewWeakReference;

        public TimerHandler(WeakReference<TabView> weakReference) {
            this.tabViewWeakReference = weakReference;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AppDebug.e(TAG, TAG + ".TimerHandler 超过了500ms");
            if (tabViewWeakReference.get() != null) {
                TabView tabView = tabViewWeakReference.get();
                if (tabView.focusListener != null) {
                    tabView.focusListener.onItemLongFocus((Integer) msg.obj);
                }
            }
        }
    }
}
