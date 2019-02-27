package com.tvtaobao.voicesdk.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tvtaobao.voicesdk.R;
import com.tvtaobao.voicesdk.bo.BillDo;
import com.tvtaobao.voicesdk.utils.DateUtil;
import com.tvtaobao.voicesdk.utils.PriceUtil;
import com.tvtaobao.voicesdk.view.RoundImageView;
import com.yunos.tv.core.common.ImageLoaderManager;

import java.util.List;


/**
 * Created by xutingting on 2017/9/16.
 */

public class BillQueryAdapter extends RecyclerView.Adapter<BillQueryAdapter.BillQueryViewHolder> {
    private Context mContext;
    private List<BillDo> mList;
    private ImageLoaderManager mImageLoaderManager;


    public BillQueryAdapter(Context context) {
        this.mContext = context;
        mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(mContext);

    }

    @Override
    public BillQueryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BillQueryViewHolder holder = new BillQueryViewHolder(LayoutInflater.from(mContext).inflate(R.layout.dialog_bill_query_item, parent,
                false));
        return holder;
    }

    @Override
    public void onBindViewHolder(BillQueryViewHolder holder, int position) {
        BillDo billDo = mList.get(position);
        if (!TextUtils.isEmpty(billDo.getAuctionTitle())) {
            if (billDo.getHasDetail().equals("true")) {
                String titleBase = billDo.getAuctionTitle();
                String title = "";
                char[] array = titleBase.toCharArray();
                int chineseCount = 0;
                int englishCount = 0;
                int subCount = 0;
                for (int i = 0; i < array.length; i++) {
                    if ((char) (byte) array[i] != array[i]) {
                        chineseCount++;
                    } else {
                        englishCount++;
                    }
                    subCount = i + 1;
                    if (chineseCount * 2 + englishCount >= 17) {
                        break;
                    }
                }

                title = titleBase.substring(0, subCount);
                if(subCount<titleBase.length()) {
                    holder.tvName.setText(title + "...等多件");
                }else {
                    holder.tvName.setText(title + "等多件");
                }
            } else {
                holder.tvName.setText(billDo.getAuctionTitle());
            }
        }
        if (!TextUtils.isEmpty(billDo.getAuctionPicUrl())) {
            mImageLoaderManager.displayImage(billDo.getAuctionPicUrl(), holder.ivItem);
        }

        if (!TextUtils.isEmpty(billDo.getGmtCreate())) {
            String date = DateUtil.dateToStampYearAndMonth(DateUtil.dateToStamp(billDo.getGmtCreate()));
            holder.tvDate.setText(date);
        }

        if (!TextUtils.isEmpty(billDo.getActualPaidFee())) {
            holder.tvPrice.setText("¥ " + PriceUtil.getPrice(Integer.parseInt(billDo.getActualPaidFee())));
        }
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();

        } else {
            return 0;
        }
    }

    public void initData(List<BillDo> billDos) {
        this.mList = billDos;
        notifyDataSetChanged();

    }

    class BillQueryViewHolder extends RecyclerView.ViewHolder {

        private RoundImageView ivItem;
        private TextView tvName;
        private TextView tvPrice;
        private TextView tvDate;

        public BillQueryViewHolder(View itemView) {
            super(itemView);
            ivItem = (RoundImageView) itemView.findViewById(R.id.iv_item);
            ivItem.setRound(true, true, false, false);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            tvDate = (TextView) itemView.findViewById(R.id.tv_date);
        }
    }


}
