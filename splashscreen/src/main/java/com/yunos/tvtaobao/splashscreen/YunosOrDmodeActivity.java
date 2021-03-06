package com.yunos.tvtaobao.splashscreen;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.yunos.RunMode;
import com.yunos.ott.sdk.core.Environment;

/**
 * Created by LJY on 18/4/24.
 */

public class YunosOrDmodeActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (RunMode.isYunos()) {//fixme 此处应当判断当前系统
            setContentView(R.layout.yunos_bundle_activity);
        } else {
            setContentView(R.layout.dmode_bundle_activity);
        }


        findViewById(R.id.back_desk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


}
