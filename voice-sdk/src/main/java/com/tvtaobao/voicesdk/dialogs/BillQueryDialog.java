package com.tvtaobao.voicesdk.dialogs;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.R;
import com.tvtaobao.voicesdk.adapter.BillQueryAdapter;
import com.tvtaobao.voicesdk.dialogs.base.BaseDialog;
import com.tvtaobao.voicesdk.bo.BillData;
import com.tvtaobao.voicesdk.bo.BillDo;
import com.tvtaobao.voicesdk.utils.DialogManager;
import com.tvtaobao.voicesdk.view.AutoTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pan on 2017/10/27.
 */

public class BillQueryDialog extends BaseDialog {
    private final String TAG = "BillQueryDialog";
    private Context context;
    private RecyclerView recyclerView;
    private BillQueryAdapter billQueryAdapter;
    private List<BillDo> totalBill;
    private AutoTextView mReply;
    private ImageView ivEmpty;
    private RelativeLayout rlBillInfo;
    private List<String> tips = new ArrayList<>();

    public BillQueryDialog(Context context) {
        super(context);
        this.context = context;

        this.setContentView(R.layout.dialog_bill_query);
        initView();
    }

    public void initView() {
        mReply = (AutoTextView) findViewById(R.id.voice_card_search_reply);
        ivEmpty = (ImageView) findViewById(R.id.iv_empty);
        rlBillInfo = (RelativeLayout) findViewById(R.id.rl_bill_info);
        recyclerView = (RecyclerView) findViewById(R.id.rv_bill);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        //设置item间距，30dp
        recyclerView.addItemDecoration(new SpaceItemDecoration(30));
        billQueryAdapter = new BillQueryAdapter(context);
        recyclerView.setAdapter(billQueryAdapter);


    }

    @Override
    public void show() {
        super.show();
        DialogManager.getManager().pushDialog(this);
        delayDismiss(10 * 1000);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        mReply.clear();
    }

    public void setData(BillData billData) {

        if (billData.getTts() != null) {
            tips.add(billData.getTts());
        }
        setPrompt(tips);

        if (!TextUtils.isEmpty(billData.getCode())) {
            if (billData.getCode().equals("OK")) {
                showBillView(billData);
            } else if (billData.getCode().equals("DATETIME_NOT_IDENTIFY")) {
                showEmpty(R.drawable.icon_bill_datetime_not_identify);
            } else if (billData.getCode().equals("EMPTY_DATA")) {
                showEmpty(R.drawable.icon_bill_empty_data);
            } else {
                showEmpty(R.drawable.icon_bill_other_problem);
            }
        }

    }

    public void showBillView(BillData billData) {
        ivEmpty.setVisibility(View.GONE);
        rlBillInfo.setVisibility(View.VISIBLE);
        if (totalBill == null) {
            totalBill = new ArrayList<>();
        }
        totalBill.clear();
        List<BillDo> billDos = new ArrayList<>();

        if (billData.getModel() != null && billData.getModel().getDetailDOList() != null) {
            totalBill.addAll(billData.getModel().getDetailDOList());
            if (billData.getModel().getDetailDOList().size() > 3) {
                billDos = billData.getModel().getDetailDOList().subList(0, 3);
            } else {
                billDos = billData.getModel().getDetailDOList();
            }
        }
//        if(billData!=null&&billData.getModel()!=null&&billData.getModel().getAmounts()!=null) {
//            tvSumOfMoney.setText("￥" + PriceUtil.getPrice(Integer.parseInt(billData.getModel().getAmounts())));
//        }
        billQueryAdapter.initData(billDos);
        Log.e(TAG, TAG + ".VoiceSearchListener.onSuccess size : " + billData.getModel().getDetailDOList().size());
    }

    public void showEmpty(int drawableResource) {
        ivEmpty.setVisibility(View.VISIBLE);
        rlBillInfo.setVisibility(View.GONE);
        ivEmpty.setImageResource(drawableResource);

    }

    public void setPrompt(List<String> reply) {
        if (reply != null && reply.size() > 0) {
            mReply.autoScroll(reply);

            ASRNotify.getInstance().playTTS(reply.get(0));
        }
    }


    class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        int mSpace;

        /**
         * @param outRect Rect to receive the output.
         * @param view    The child view to decorate
         * @param parent  RecyclerView this ItemDecoration is decorating
         * @param state   The current state of RecyclerView.
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = mSpace;
            outRect.right = mSpace;
            outRect.bottom = 0;
            if (parent.getChildAdapterPosition(view) == 0) {
//                outRect.top = mSpace;
                outRect.top = 0;
            }

        }

        public SpaceItemDecoration(int space) {
            this.mSpace = space;
        }
    }

}
