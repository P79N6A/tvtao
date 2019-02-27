package com.yunos.tvtaobao.takeoutbundle.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.powyin.scroll.adapter.AdapterDelegate;
import com.powyin.scroll.adapter.PowViewHolder;
import com.yunos.tvtaobao.biz.request.bo.ShopDetailData;
import com.yunos.tvtaobao.takeoutbundle.R;

/**
 * Created by haoxiang on 2017/12/12. 商品种类 item
 */

public class ViewHolderKind extends PowViewHolder<ShopDetailData.ItemGenreWithItemsListBean> {

    private TextView good_kind_select_count;
    private TextView good_kind;
    public ViewHolderKind(Activity activity, ViewGroup viewGroup) {
        super(activity, viewGroup);
        good_kind_select_count = findViewById(R.id.good_kind_select_count);
        good_kind = findViewById(R.id.good_kind);
        good_kind.setHorizontallyScrolling(true);
    }

    @Override
    protected int getItemViewRes() {
        return R.layout.item_takeout_good_kind;
    }

    @Override
    public void loadData(AdapterDelegate<? super ShopDetailData.ItemGenreWithItemsListBean> multipleAdapter, ShopDetailData.ItemGenreWithItemsListBean data, int position) {
        if(data.focusStatus==null ){
            data.focusStatus = ShopDetailData.ItemGenreWithItemsListBean.FocusStatus.NO;
        }
        switch (data.focusStatus){
            case NO:
                good_kind.setBackgroundResource(R.drawable.good_kind_text_focus_no);
                good_kind.setTextColor(mActivity.getResources().getColor(R.color.white_909ca7));
                good_kind.setEllipsize(TextUtils.TruncateAt.END);
                good_kind.setSelected(false);
                break;
            case FOCUS:
                good_kind.setBackgroundResource(R.drawable.good_kind_text_focus_yes);
                good_kind.setTextColor(mActivity.getResources().getColor(R.color.white_ffffffff));
                good_kind.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                good_kind.setSelected(true);
                break;
            case TEMPLETE:
                good_kind.setBackgroundResource(R.drawable.good_kind_text_focus_hint);
                good_kind.setTextColor(mActivity.getResources().getColor(R.color.white_ededed));
                good_kind.setEllipsize(TextUtils.TruncateAt.END);
                good_kind.setSelected(false);
                break;
        }

        good_kind.setText(data.getName());

        if(data.totleGoodCount >0){
            good_kind_select_count.setVisibility(View.VISIBLE);
            good_kind_select_count.setText(String.valueOf(data.totleGoodCount));
        }else {
            good_kind_select_count.setVisibility(View.INVISIBLE);
        }
    }
}
