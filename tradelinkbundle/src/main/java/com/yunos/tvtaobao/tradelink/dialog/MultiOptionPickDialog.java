package com.yunos.tvtaobao.tradelink.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.tradelink.R;
import com.yunos.tvtaobao.tradelink.buildorder.bean.MultiOptionComponent;
import com.yunos.tvtaobao.tradelink.buildorder.view.MultiPromotionAdapter;

import java.util.Map;

public class MultiOptionPickDialog<T extends MultiOptionComponent> extends Dialog implements View.OnFocusChangeListener, View.OnClickListener, MultiPromotionAdapter.OptionChangedListener {

    public interface MultiOptionPickDialogDelegate {
        void onConfirm(MultiOptionPickDialog dialog, MultiOptionComponent component);

        void onCancel(MultiOptionPickDialog dialog, MultiOptionComponent component);
    }

    private MultiOptionPickDialogDelegate delegate;

    public void setDelegate(MultiOptionPickDialogDelegate delegate) {
        this.delegate = delegate;
    }

    private TextView buttonContent;
    private View buttonSeparator;
    private TextView buttonConfirmText;
    private View confirmbutton;

    private TextView title1;
    private TextView title2;
//    private TextView title3;

    public void setButtonContent(CharSequence content) {
        if (buttonContent != null) {
            buttonContent.setText(content);
        }
        boolean emptyButtonContent = TextUtils.isEmpty(content);
        buttonContent.setVisibility(emptyButtonContent ? View.GONE : View.VISIBLE);
        buttonSeparator.setVisibility(emptyButtonContent ? View.GONE : View.VISIBLE);
    }


    public void setButtonConfirmText(CharSequence text) {
        if (buttonConfirmText != null) {
            buttonConfirmText.setText(text);
        }
    }

    RecyclerView optionRecyler;

    private MultiPromotionAdapter<T> adapter;

    private T component;


    public T getComponent() {
        return component;
    }

    public MultiOptionPickDialog(@NonNull Context context) {
        super(context, R.style.trade_dialog_fullscreen);
        initView(context);
    }

    public void setMultiOptionComponent(T component) {
        this.component = component;
        adapter.setOptionComponents(component);
        boolean emptyButtonContent = TextUtils.isEmpty(component.getDetailButtonTip());
        buttonContent.setVisibility(emptyButtonContent ? View.GONE : View.VISIBLE);
        buttonSeparator.setVisibility(emptyButtonContent ? View.GONE : View.VISIBLE);
        if (!emptyButtonContent) {
            buttonContent.setText(component.getDetailButtonTip());
        }
        title1.setText(component.getDetailTitle());
        if (TextUtils.isEmpty(component.getDetailSubtitle())) {
            title2.setVisibility(View.GONE);
        } else {
            title2.setText(component.getDetailSubtitle());
            title2.setVisibility(View.VISIBLE);
        }
//        if (TextUtils.isEmpty(component.getDetailSubtitle2())) {
//            title3.setVisibility(View.GONE);
//        } else {
//            title3.setText(component.getDetailSubtitle2());
//            title3.setVisibility(View.VISIBLE);
//        }

    }

    protected View getConfirmbutton() {
        return confirmbutton;
    }

    private void initView(Context context) {
        setContentView(R.layout.trade_dialog_multioptions);
        optionRecyler = (RecyclerView) findViewById(R.id.optionRecyclerView);
        buttonContent = (TextView) findViewById(R.id.buttoncontent);
        buttonSeparator = findViewById(R.id.buttonseparator);
        buttonConfirmText = (TextView) findViewById(R.id.buttonconfirmtxt);
        title1 = (TextView) findViewById(R.id.title1);
        title2 = (TextView) findViewById(R.id.title2);
//        title3 = (TextView) findViewById(R.id.title3);

        confirmbutton = findViewById(R.id.confirmbutton);
        adapter = new MultiPromotionAdapter();
        adapter.setOptionChangedListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(context) {

            @Override
            public boolean onRequestChildFocus(RecyclerView parent, RecyclerView.State state, View child, View focused) {
                int parentHeight = parent.getHeight();
                int childCenter = (child.getTop() + child.getBottom()) / 2;
                int distance = childCenter - parentHeight / 2;
                parent.smoothScrollBy(0, distance);
                return true;
            }

            @Override
            public void onScrollStateChanged(int state) {
                super.onScrollStateChanged(state);
            }

            @Override
            public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate) {
                return super.requestChildRectangleOnScreen(parent, child, rect, immediate);
            }

            @Override
            public boolean requestChildRectangleOnScreen(RecyclerView parent, View child, Rect rect, boolean immediate, boolean focusedChildVisible) {
                return false;
            }
        };
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        optionRecyler.setLayoutManager(manager);
        optionRecyler.setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);
        optionRecyler.setAdapter(adapter);
        confirmbutton.setOnFocusChangeListener(this);
        confirmbutton.setOnClickListener(this);
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (v == confirmbutton) {
            buttonConfirmText.setTextColor(hasFocus ? Color.WHITE : 0xff99bbdd);
            buttonContent.setTextColor(hasFocus ? Color.WHITE : 0xff99bbdd);
            buttonSeparator.setBackgroundColor(hasFocus ? Color.WHITE : 0xff667a91);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == confirmbutton) {
            Map<String, String> params = Utils.getProperties();
            params.put("title", component.getEntryTitle());
            Utils.utControlHit("MULTI_OPTION_DIALOG_CONFIRM", params);
            confirmAndDismiss();
        }
    }

    @Override
    public void cancel() {
        if (delegate != null) {
            delegate.onCancel(this, component);
        }
        Map<String, String> params = Utils.getProperties();
        params.put("title", component.getEntryTitle());
        Utils.utControlHit("MULTI_OPTION_DIALOG_CONFIRM", params);
        super.cancel();
    }

    public void confirmAndDismiss() {
        if (delegate != null) {
            delegate.onConfirm(this, component);
        }
        dismiss();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
//        confirmbutton.requestFocus();
        optionRecyler.post(new Runnable() {
            @Override
            public void run() {
                if (optionRecyler.getChildCount() > 1) {
//                    optionRecyler.requestFocus(View.FOCUS_LEFT);
                    View child = optionRecyler.getChildAt(1);
                    if (child instanceof ViewGroup) {
                        for (int i = 0; i < ((ViewGroup) child).getChildCount(); i++) {
                            View grandChild = ((ViewGroup) child).getChildAt(i);
                            if (grandChild.requestFocus())
                                return;
                        }
                    }
                } else {
                    confirmbutton.requestFocus();
                }
            }
        });
        Utils.utPageAppear("MULTI_OPTION_DIALOG", component.getEntryTitle());

    }


    //TODO
    @Override
    public void onOptionChanged(MultiOptionComponent component, Object subComponent, Object option) {
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Utils.utPageDisAppear("MULTI_OPTION_DIALOG");
    }

}
