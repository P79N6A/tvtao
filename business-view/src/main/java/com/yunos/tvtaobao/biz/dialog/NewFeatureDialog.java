package com.yunos.tvtaobao.biz.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.StringUtils;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.util.StringUtil;
import com.yunos.tvtaobao.businessview.R;
import com.yunos.tvtaobao.biz.preference.UpdatePreference;

import java.io.File;

/**
 * Created by pan on 2017/6/8.
 * 升级后 新特性展示
 */

public class NewFeatureDialog extends Dialog {

    private Context mContext;
    private LinearLayout layout1, layout2, layout3;
    private TextView feature1, feature2, feature3;
    private String defaultText ="";
    private CallBack mCallBack;

    public NewFeatureDialog(Context context) {
        this(context, R.style.update_top_Dialog);
    }

    public NewFeatureDialog(Context context,String str) {
        this(context, R.style.update_top_Dialog);
        defaultText= StringUtil.isEmpty(str)?"":str;
    }

    public NewFeatureDialog(Context context, int theme) {
        super(context, theme);
        this.mContext = context;
        WindowManager.LayoutParams l = getWindow().getAttributes();
        //0.0f完全不暗，即背景是可见的 ，1.0f时候，背景全部变黑暗。
        l.dimAmount = 0.7f;
        getWindow().setWindowAnimations(R.style.bs_up_dialog_animation);
        getWindow().setAttributes(l);

//        SharedPreferences sp = context.getSharedPreferences(UpdatePreference.SP_FILE_NAME, Context.MODE_PRIVATE);
//        defaultText=sp.getString(UpdatePreference.UPDATE_TIPS,mContext.getString(R.string.bs_after_up_default_memo));
    }

    public void setCallBack(CallBack callBack) {
        this.mCallBack = callBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bs_up_update_new_feature_dialog);
        initView();
        LinearLayout[] layouts = {layout1, layout2, layout3};
        TextView[] textViews = {feature1, feature2, feature3};
        SharedPreferences sp = mContext.getSharedPreferences(UpdatePreference.SP_FILE_NAME, Context.MODE_PRIVATE);
        // 对更新信息进行处理，最多显示前三条
        String updateInfoText = sp.getString(UpdatePreference.INTENT_KEY_UPDATE_INFO, defaultText);
        if (!TextUtils.isEmpty(updateInfoText)) {
            String[] tmp = updateInfoText.split("\n");
            AppDebug.e("NewFeature", "NewFeature.updateInfoText : " + updateInfoText + " ,tmp.length : " + tmp.length);

            for (int i = 0; i < tmp.length; i++) {
                AppDebug.e("NewFeature", "NewFeature.tmp : " + tmp[i] + " ,textview : " + textViews[i]);
                layouts[i].setVisibility(View.VISIBLE);
                textViews[i].setText(tmp[i]);
            }
        }


    }

    @Override
    public void show() {
        super.show();
        Utils.utCustomHit("Update_success_Expore", Utils.getProperties());
        deleteInstallAPK();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (mCallBack != null) {
            mCallBack.onCallBack();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                dismiss();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void initView() {
        layout1 = (LinearLayout) findViewById(R.id.bs_up_new_feature_content1);
        layout2 = (LinearLayout) findViewById(R.id.bs_up_new_feature_content2);
        layout3 = (LinearLayout) findViewById(R.id.bs_up_new_feature_content3);
        feature1 = (TextView) findViewById(R.id.bs_up_fature_text1);
        feature2 = (TextView) findViewById(R.id.bs_up_fature_text2);
        feature3 = (TextView) findViewById(R.id.bs_up_fature_text3);
    }


    public interface CallBack {
        void onCallBack();
    }

    private void deleteInstallAPK(){
        String path =String.valueOf(mContext.getExternalCacheDir());
        File file = new File(path);
        File[] files = file.listFiles();
        for (int j = 0; j < files.length; j++) {
            String name = files[j].getName();
            if (name.endsWith(".apk")) {
                File fl=new File(mContext.getExternalCacheDir(), name);
//                FileUtils.removeDir(fl);
                if(fl.isFile()&&fl.exists()){
                    fl.delete();
                }
            }
        }
    }
}
