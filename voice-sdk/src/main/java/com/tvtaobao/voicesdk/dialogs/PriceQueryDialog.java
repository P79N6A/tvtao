package com.tvtaobao.voicesdk.dialogs;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.R;
import com.tvtaobao.voicesdk.dialogs.base.BaseDialog;
import com.tvtaobao.voicesdk.bo.PriceData;
import com.tvtaobao.voicesdk.utils.DialogManager;
import com.tvtaobao.voicesdk.view.AutoTextView;
import com.tvtaobao.voicesdk.view.RoundImageView;
import com.yunos.tv.core.common.ImageLoaderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pan on 2017/10/27.
 */

public class PriceQueryDialog extends BaseDialog {
    private final String TAG = "PriceQueryDialog";
    private Context context;
    private AutoTextView mReply;

    private TextView tvTitle;
    private TextView tvPrice;
    private ImageView ivDirection;
    private RoundImageView ivPic;
    private ImageView ivTendencyChartPrice;

    private ImageView ivEmpty;
    private RelativeLayout rlPriceInfo;
    private ImageLoaderManager mImageLoaderManager;
    private List<String> tips = new ArrayList<>();


    public PriceQueryDialog(Context context) {
        super(context);
        this.context = context;

        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(context);
        this.setContentView(R.layout.dialog_price_query);
        initView();
    }

    public void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvPrice = (TextView) findViewById(R.id.tv_price);
        ivDirection = (ImageView) findViewById(R.id.iv_direction);
        ivPic = (RoundImageView) findViewById(R.id.iv_pic);
        ivTendencyChartPrice  = (ImageView) findViewById(R.id.iv_tendency_chart_price);
        mReply = (AutoTextView) findViewById(R.id.voice_card_search_reply);
        ivEmpty = (ImageView) findViewById(R.id.iv_empty);
        rlPriceInfo = (RelativeLayout) findViewById(R.id.rl_price_info);
    }

    @Override
    public void show() {
        super.show();
        DialogManager.getManager().pushDialog(this);
    }

    public void setData(PriceData priceData) {
        if (priceData.getTts() != null) {
            tips.add(priceData.getTts());
        }
        if (priceData.getTip() != null) {
            tips.add(priceData.getTip());
        }
        setPrompt(tips);

        if (!TextUtils.isEmpty(priceData.getCode())) {
            if(priceData.getCode().equals("OK")){
                showBillView(priceData);
            }else if (priceData.getCode().equals("EXPRESS_NOT_CLEAR_ENOUGH")) {
                showEmpty(R.drawable.icon_price_express_not_clear);
            } else if (priceData.getCode().equals("EMPTY_DATA")) {
                showEmpty(R.drawable.icon_price_empty_data);
            } else {
                showEmpty(R.drawable.icon_price_other_problem);
            }
        }
    }

    public void showBillView(PriceData priceData){
        ivEmpty.setVisibility(View.GONE);
        rlPriceInfo.setVisibility(View.VISIBLE);
        if(priceData!=null&&priceData.getModel()!=null) {
            if(priceData.getModel().getTitle()!=null) {
                tvTitle.setText(priceData.getModel().getTitle());
            }
            if(priceData.getModel().getPrice()!=null){
                tvPrice.setText("¥ "+priceData.getModel().getPrice());
            }
            if(priceData.getModel().getPicUrl()!=null){
                mImageLoaderManager.displayImage(priceData.getModel().getPicUrl(), ivPic);
            }

            // 价格趋势上升、下降标记
            if (priceData.getTts().contains("下降")) {
                ivDirection.setVisibility(View.VISIBLE);
                ivDirection. setImageResource(R.drawable.icon_price_down);
            } else if (priceData.getTts().contains("上升")) {
                ivDirection.setVisibility(View.VISIBLE);
                ivDirection.setImageResource(R.drawable.icon_price_up);
            } else if (priceData.getModel().getBalance()!=null) {
                if(Integer.parseInt(priceData.getModel().getBalance())>0){
                    ivDirection.setVisibility(View.VISIBLE);
                    ivDirection.setImageResource(R.drawable.icon_price_up);
                }else if(Integer.parseInt(priceData.getModel().getBalance())<0){
                    ivDirection.setVisibility(View.VISIBLE);
                    ivDirection. setImageResource(R.drawable.icon_price_down);
                }else {
                    ivDirection.setVisibility(View.GONE);
                }
            }

            if(priceData.getModel().getTrendRenderPicUrl()!=null){
                mImageLoaderManager.displayImage(priceData.getModel().getTrendRenderPicUrl(), ivTendencyChartPrice);
            }
        }
    }

    public void showEmpty(int drawableResource){
        ivEmpty.setVisibility(View.VISIBLE);
        rlPriceInfo.setVisibility(View.GONE);
        ivEmpty.setImageResource(drawableResource);

    }

    public void setPrompt(List<String> reply) {

        if (reply != null && reply.size() > 0) {
            mReply.autoScroll(reply);

            ASRNotify.getInstance().playTTS(reply.get(0));
        }
    }



}
