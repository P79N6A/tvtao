package com.yunos.voice.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.VideoView;

import com.tvtaobao.voicesdk.utils.LogPrint;
import com.yunos.voice.R;

/**
 * <pre>
 *     author : pan
 *     QQ : 1065475118
 *     time   : 2018/03/13
 *     desc   :
 *     version: 1.0
 * </pre>
 */

public class VoiceVideoDemoActivity extends Activity {

    private VideoView videoView;
    private String videoUrl;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        videoUrl = bundle.getString("url");

        setContentView(R.layout.activity_voice_video);

        videoView = (VideoView) findViewById(R.id.videoview);

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.setVideoURI(Uri.parse(videoUrl));
                videoView.start();
            }
        });

        videoView.setVideoURI(Uri.parse(videoUrl));
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogPrint.i("VoiceVideo", "onResume");

        videoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.pause();
    }
}
