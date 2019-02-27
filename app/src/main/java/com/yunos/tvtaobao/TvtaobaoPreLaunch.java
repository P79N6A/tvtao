package com.yunos.tvtaobao;

import android.content.Context;
import android.taobao.atlas.runtime.AtlasPreLauncher;
import android.util.Log;

/**
 * Created by huangdaju on 17/5/22.
 */

public class TvtaobaoPreLaunch implements AtlasPreLauncher {
    @Override
    public void initBeforeAtlas(Context context) {
        Log.d("prelaunch", "prelaunch invokded");
    }
}
