package com.yunos.tvtaobao.live.activity;

import android.os.Bundle;
import android.text.TextUtils;

import com.media.ijkmediaplayer.IjkVideoView;
import com.yunos.tvtaobao.biz.activity.BaseActivity;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/9/6
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class TestLiveActivity extends BaseActivity {
    private IjkVideoView ijkVideoView;
    private String uri = "http://tslp.zptvmall.com/tblv/340427.m3u8?lhs_start=1&lhs_start_human_s_8=20180906150529&aliyun_uuid=87E96B5072101FF040485976209BEDA9";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ijkVideoView = new IjkVideoView(this);
        setContentView(ijkVideoView);
        String url = getIntent().getStringExtra("url");
        if (!TextUtils.isEmpty(url)) {
            uri = url;
        }
        ijkVideoView.setVideoPath(uri);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ijkVideoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ijkVideoView.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ijkVideoView.stop();
    }
}
