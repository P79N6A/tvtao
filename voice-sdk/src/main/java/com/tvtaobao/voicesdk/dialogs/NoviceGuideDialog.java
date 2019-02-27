package com.tvtaobao.voicesdk.dialogs;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.tvtaobao.voicesdk.R;
import com.tvtaobao.voicesdk.dialogs.base.BaseDialog;
import com.tvtaobao.voicesdk.utils.DialogManager;
import com.tvtaobao.voicesdk.view.AutoTextView;
import com.yunos.tv.core.common.SharePreferences;

import java.lang.ref.WeakReference;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/6/27
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class NoviceGuideDialog extends BaseDialog {

    private String itemId;
    private String rebate;
    private Context context;
    private AutoTextView reply;
    private String tts = "购买带有快捷标签的商品，可直接生成订单并寄送到默认地址。语音购物就是这么简单。继续为您下单。";
    private TimerHandler timerHandler;
    private String price;

    public NoviceGuideDialog(Context context) {
        super(context);

        this.context = context;

        this.setContentView(R.layout.dialog_novice_guide);
        initView();

        onTTS(tts);

        timerHandler = new TimerHandler(this);
        timerHandler.sendEmptyMessageDelayed(0, 12 * 1000);
    }

    public void initView() {
        reply = (AutoTextView) findViewById(R.id.voice_card_search_reply);
    }

    public void onTTS(String tts) {
        reply.setText(tts);

        playTTS(tts);
    }

    @Override
    public void show() {
        super.show();
        SharePreferences.put("isShowNoviceGuide", false);
        DialogManager.getManager().pushDialog(this);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        reply.clear();
        timerHandler.removeCallbacksAndMessages(null);
    }

    public void setData(String itemId,String rebate,String price) {
        this.itemId = itemId;
        this.rebate = rebate;
        this.price = price;
    }

    private static class TimerHandler extends Handler {
        private WeakReference<NoviceGuideDialog> noviceGuideActivityWR;
        public TimerHandler(NoviceGuideDialog noviceGuideActivity) {
            noviceGuideActivityWR = new WeakReference<NoviceGuideDialog>(noviceGuideActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (noviceGuideActivityWR != null && noviceGuideActivityWR.get() != null) {
                        noviceGuideActivityWR.get().dismiss();
                        CreateOrderDialog createOrderDialog = new CreateOrderDialog(noviceGuideActivityWR.get().context);
                        createOrderDialog.setItemId(noviceGuideActivityWR.get().itemId);
                        createOrderDialog.setPrice(noviceGuideActivityWR.get().price);
                        createOrderDialog.show();
                    }
                    break;
            }
        }
    }
}
