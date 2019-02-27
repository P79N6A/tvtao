package com.yunos.tv.core;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.SharePreferences;
import com.yunos.tv.core.config.RunMode;

public class DebugTestDialog extends Dialog {


    private EditText etName;
    private Button clickBtn;
    private Boolean bl_env=false;

    public DebugTestDialog(Context context) {
        super(context);
    }

    public DebugTestDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public DebugTestDialog(Context context,Boolean bl) {
        super(context);
        bl_env=bl;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDebug.d("DebugTestDialog","onCreate ++++++");
        setContentView(R.layout.debug_dialog);
        etName = (EditText) findViewById(R.id.debug_editText);
        etName.requestFocus();

        clickBtn = (Button) findViewById(R.id.debug_button);
        clickBtn.setOnClickListener(clickListener);
    }


    private View.OnClickListener clickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (etName != null && !TextUtils.isEmpty(etName.getText().toString())) {
                if(!bl_env){
                    SharePreferences.put("device_appkey", etName.getText().toString());
                    DebugTestDialog.this.dismiss();
                }else {
                    SharePreferences.put("device_env", etName.getText().toString().equals("0")?RunMode.DAILY.toString():etName.getText().toString().equals("1")?RunMode.PREDEPLOY.toString():RunMode.PRODUCTION.toString());
                    DebugTestDialog.this.dismiss();
                }
            }
        }
    };
}