package com.yunos.tvtaobao.tradelink.buildorder.view;


import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.taobao.wireless.trade.mbuy.sdk.co.basic.InputComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.InputComponentPlugin;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.tradelink.R;

public class InputViewHolder extends PurchaseViewHolder {

    public static String cellPhoneNum;
    protected EditText editText;

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String text = s.toString();

            InputComponent inputComponent = (InputComponent) component;
            inputComponent.setValue(text);

            InputComponentPlugin plugin = inputComponent.getPlugin();
            if (plugin == InputComponentPlugin.CONTACTS) {
                cellPhoneNum = text;
            }
            AppDebug.i(TAG, "afterTextChanged -->  text = " + text);
        }
    };

    public InputViewHolder(Context context) {
        super(context);
    }

    @Override
    protected View makeView() {
        View view = View.inflate(context, R.layout.ytm_buildorder_buy_input_layout, null);
        if (view instanceof BuildOrderItemView && view != null) {
            BuildOrderItemView buildOrderItemView = (BuildOrderItemView) view;
            Rect manualRect = new Rect();
            manualRect.setEmpty();
            int topPadding = context.getResources().getDimensionPixelSize(R.dimen.dp_10);
            manualRect.top = topPadding;
            manualRect.bottom =  topPadding;
            buildOrderItemView.setAjustFocus(true, manualRect);
        }
        editText = (EditText) view.findViewById(R.id.goods_buy_edit_input);
        return view;
    }

    @Override
    protected void bindData() {
        InputComponent inputComponent = (InputComponent) component;

        editText.removeTextChangedListener(textWatcher);

        String placeholder = inputComponent.getPlaceholder();
        if (placeholder != null && !placeholder.isEmpty()) {
            editText.setHint(placeholder);
        }

        String value = inputComponent.getValue();
        if (value != null && !value.isEmpty()) {
            editText.setText(value);
        }
        AppDebug.i(TAG, "InputViewHolder --> placeholder = " + placeholder + "; value = " + value);

        InputComponentPlugin plugin = inputComponent.getPlugin();
        switch (plugin) {
            case CONTACTS:
                editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
                break;
            default:
                editText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                break;
        }

        editText.addTextChangedListener(textWatcher);
    }

    @Override
    protected boolean onKeyListener(View v, int keyCode, KeyEvent event) {
        AppDebug.i(TAG, "onKeyListener --> v = " + v + "; keyCode = " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            
            Editable editable = editText.getText();
            if (editable != null) {
                // 当点击OK键时，把光标设置到文本的最后
                editText.setSelection(editable.length());
            }
        } else if (keyCode == KeyEvent.KEYCODE_DEL){
            if (editText != null) {
                editText.onKeyDown(keyCode, event);
            }
        }
        return false;
    }

}
