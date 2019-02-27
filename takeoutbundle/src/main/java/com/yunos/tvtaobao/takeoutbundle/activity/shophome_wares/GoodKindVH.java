package com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunos.tvtaobao.biz.focus_impl.ConsumerB;
import com.yunos.tvtaobao.biz.focus_impl.FakeListView;
import com.yunos.tvtaobao.biz.focus_impl.FocusArea;
import com.yunos.tvtaobao.biz.focus_impl.FocusConsumer;
import com.yunos.tvtaobao.biz.request.bo.ShopDetailData;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by GuoLiDong on 2018/10/21.
 */
public class GoodKindVH extends FakeListView.ViewHolder {
    Context context;
    Unbinder unbinder;
    @BindView(R2.id.good_kind)
    TextView goodKind;
    @BindView(R2.id.good_kind_select_count)
    TextView goodKindSelectCount;

    public GoodKindVH(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.item_takeout_good_kind, parent, false));
        this.context = context;
        unbinder = ButterKnife.bind(this, itemView);
    }

    public void fillWith(final Data itemData) {
        if (itemView instanceof FocusArea) {
            ((FocusArea) itemView).setFocusConsumer(new ConsumerB() {
                @Override
                public boolean onFocusEnter() {
                    itemData.data.focusStatus = ShopDetailData.ItemGenreWithItemsListBean.FocusStatus.FOCUS;
                    setKindName(itemData.data);
                    return true;
                }

                @Override
                public boolean onFocusLeave() {
                    itemData.data.focusStatus = ShopDetailData.ItemGenreWithItemsListBean.FocusStatus.NO;
                    setKindName(itemData.data);
                    return true;
                }
            });
        }
        if (itemData.data.focusStatus == null) {
            itemData.data.focusStatus = ShopDetailData.ItemGenreWithItemsListBean.FocusStatus.NO;
        }

        setKindName(itemData.data);

        if (itemData.data.totleGoodCount > 0) {
            goodKindSelectCount.setVisibility(View.VISIBLE);
            goodKindSelectCount.setText(String.valueOf(itemData.data.totleGoodCount));
        } else {
            goodKindSelectCount.setVisibility(View.INVISIBLE);
        }
    }

    private void setKindName(final ShopDetailData.ItemGenreWithItemsListBean itemData) {
        goodKind.setText(itemData.getName());

        goodKind.setGravity(Gravity.CENTER_VERTICAL);

        switch (itemData.focusStatus) {
            case NO:
                goodKind.setBackgroundResource(R.drawable.good_kind_text_focus_no);
                goodKind.setTextColor(context.getResources().getColor(R.color.white_909ca7));
                goodKind.setEllipsize(TextUtils.TruncateAt.END);
                goodKind.setSelected(false);
                break;
            case FOCUS:
                goodKind.setBackgroundResource(R.drawable.good_kind_text_focus_yes);
                goodKind.setTextColor(context.getResources().getColor(R.color.white_ffffffff));
                goodKind.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                goodKind.setSelected(true);
                break;
            case TEMPLETE:
                goodKind.setBackgroundResource(R.drawable.good_kind_text_focus_hint);
                goodKind.setTextColor(context.getResources().getColor(R.color.white_ededed));
                goodKind.setEllipsize(TextUtils.TruncateAt.END);
                goodKind.setSelected(false);
                break;
        }

        goodKind.setGravity(Gravity.CENTER);
    }

    public static class Data {
        public ShopDetailData.ItemGenreWithItemsListBean data;
    }
}
