package com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.assist.ImageScaleType;
import com.tvlife.imageloader.utils.ClassicOptions;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.focus_impl.ConsumerB;
import com.yunos.tvtaobao.biz.focus_impl.FocusArea;
import com.yunos.tvtaobao.biz.focus_impl.FocusFakeListView;
import com.yunos.tvtaobao.biz.focus_impl.FocusNode;
import com.yunos.tvtaobao.biz.request.bo.Collect;
import com.yunos.tvtaobao.biz.request.bo.ItemListBean;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.R2;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by GuoLiDong on 2018/10/20.
 */
public class ShopGoodItemVH extends FocusFakeListView.ViewHolder {

    private final static DecimalFormat format = new DecimalFormat("¥#.##");

    private final static DisplayImageOptions dio565 = new DisplayImageOptions.Builder()
            .cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
            .cacheOnDisc(true).cacheInMemory(true)
            .showImageOnLoading(R.drawable.good_icon_no_pic)
            .showImageOnFail(R.drawable.good_icon_no_pic)
            .showImageForEmptyUri(R.drawable.good_icon_no_pic)
            .imageScaleType(ImageScaleType.EXACTLY).build();

    private static int layoutResId = (Build.VERSION.SDK_INT >= 21) ? (R.layout.item_takeout_good_api_high) : (R.layout.item_takeout_good_api_low);

    Context context;
    Unbinder unbinder;

    TextView sale_tip[] = new TextView[6];
    @BindView(R2.id.good_focus_status)
    ImageView goodFocusStatus;
    @BindView(R2.id.good_pic)
    ImageView goodPic;
    @BindView(R2.id.good_pic_toast)
    FrameLayout goodPicToast;
    @BindView(R2.id.good_name)
    TextView goodName;
    @BindView(R2.id.good_make)
    TextView goodMake;
    @BindView(R2.id.good_sell_count)
    TextView goodSellCount;
    @BindView(R2.id.sale_tip_0)
    TextView saleTip0;
    @BindView(R2.id.sale_tip_0_more)
    TextView saleTip0More;
    @BindView(R2.id.sale_tip_1)
    TextView saleTip1;
    @BindView(R2.id.sale_tip_1_more)
    TextView saleTip1More;
    @BindView(R2.id.sale_tip_2)
    TextView saleTip2;
    @BindView(R2.id.sale_tip_2_more)
    TextView saleTip2More;
    @BindView(R2.id.good_limit_count)
    TextView goodLimitCount;
    @BindView(R2.id.good_real_price)
    TextView goodRealPrice;
    @BindView(R2.id.good_price)
    TextView goodPrice;
    @BindView(R2.id.good_reduce)
    ImageView goodReduce;
    @BindView(R2.id.good_count)
    TextView goodCount;
    @BindView(R2.id.good_add)
    ImageView goodAdd;
    @BindView(R2.id.good_card_view)
    View goodCardView;
    @BindView(R2.id.good_shop_mark)
    ImageView goodShopMark;
    @BindView(R2.id.good_reduce_wrapper)
    FocusArea goodReduceWrapper;
    @BindView(R2.id.good_add_wrapper)
    FocusArea goodAddWrapper;

    ValueAnimator animator = null;

    public ShopGoodItemVH(final Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(layoutResId, parent, false));
        this.context = context;
        unbinder = ButterKnife.bind(this, itemView);

        sale_tip[0] = saleTip0;
        sale_tip[1] = saleTip0More;
        sale_tip[2] = saleTip1;
        sale_tip[3] = saleTip1More;
        sale_tip[4] = saleTip2;
        sale_tip[5] = saleTip2More;

        animator = new ValueAnimator();
        animator.setDuration(300);
        animator.setFloatValues(1.0f, 1.08f);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (itemView != null) {
                    itemView.setPivotX(itemView.getWidth() / 2);
                    itemView.setPivotY(itemView.getHeight() / 2);
                    itemView.setScaleX((Float) animation.getAnimatedValue());
                    itemView.setScaleY((Float) animation.getAnimatedValue());
                }
                invalidatePath();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                goodName.invalidate();
                invalidatePath();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (itemView != null) {
                    itemView.setPivotX(itemView.getWidth() / 2);
                    itemView.setPivotY(itemView.getHeight() / 2);
                    itemView.setScaleX(1.0f);
                    itemView.setScaleY(1.0f);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setTarget(this);

        itemView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {

            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                animator.cancel();
            }
        });

        AppDebug.e("ShopGoodItemVH", "(Build.VERSION.SDK_INT >= 21) =" + (Build.VERSION.SDK_INT >= 21));
    }

    /**
     * 有些盒子存在动画缓存，所以刷新一下
     */
    private void invalidatePath(){
        if (itemView!=null){
            itemView.invalidate();
            ViewParent vp = itemView.getParent();
            int loop = 5;
            while (vp!=null){
                loop--;
                if (loop<0){
                    break;
                }
                if (vp instanceof View){
                    ((View) vp).invalidate();
                }
                vp = vp.getParent();
            }
        }
    }

    public void fillWith(final ItemListBean itemListBean) {

        goodAddWrapper.setFocusConsumer(new ConsumerB() {
            @Override
            public boolean onFocusEnter() {
                goodAdd.setImageLevel(2);
                return true;
            }

            @Override
            public boolean onFocusLeave() {
                goodAdd.setImageLevel(1);
                return true;
            }

            @Override
            public boolean onFocusClick() {
                EventBus.getDefault().post(Event.Add.wrap(itemListBean, getPosNow()));
                return true;
            }
        });

        goodReduceWrapper.setFocusConsumer(new ConsumerB() {
            @Override
            public boolean onFocusEnter() {
                goodReduce.setImageLevel(2);
                return true;
            }

            @Override
            public boolean onFocusLeave() {
                goodReduce.setImageLevel(1);
                return true;
            }

            @Override
            public boolean onFocusClick() {
                EventBus.getDefault().post(Event.Reduce.wrap(itemListBean, getPosNow()));
                return true;
            }
        });

        goodShopMark.setImageLevel(0);

        if (goodAddWrapper.getNode().isNodeHasFocus()) {
            goodAdd.setImageLevel(2);
        } else {
            goodAdd.setImageLevel(1);
        }

        if (goodReduceWrapper.getNode().isNodeHasFocus()) {
            goodReduce.setImageLevel(2);
        } else {
            goodReduce.setImageLevel(1);
        }

        goodPicToast.setVisibility(View.INVISIBLE);

        // 商品名字
        goodName.setText(itemListBean.getTitle());

        String hasSku = itemListBean.getHasSku();
        if ("true".equals(hasSku)) {
            //如果包含sku，则不展示限制数量
            goodLimitCount.setVisibility(View.GONE);
        } else {
            //库存
            if (itemListBean.__limitQuantity != 0) {
                goodLimitCount.setVisibility(View.VISIBLE);
                goodLimitCount.setText(String.format(context.getString(R.string.take_out_sku_num_limit), String.valueOf(itemListBean.__limitQuantity)));

            } else if (itemListBean.getStock() > 0 && itemListBean.getStock() <= 10) {
                goodLimitCount.setVisibility(View.VISIBLE);
                goodLimitCount.setText(String.format(context.getString(R.string.take_out_sku_num_stock), String.valueOf(itemListBean.getStock())));

            } else {
                goodLimitCount.setText("");
                goodLimitCount.setVisibility(View.GONE);

            }
        }


        // 商品描述
        goodMake.setText(itemListBean.getDescription());
        // 商品图片
        if (!TextUtils.isEmpty(itemListBean.getItemPicts())) {
            ImageLoaderManager.getImageLoaderManager(context).displayImage(itemListBean.getItemPicts(), goodPic, dio565);
        } else {
            goodPic.setImageResource(R.drawable.good_icon_no_pic);
        }
        // 购物车数量
        goodCount.setText(String.valueOf(itemListBean.__intentCount));

        // 新品特性 新品 招牌
        for (int i = 0; itemListBean.getItemAttrList() != null && i < itemListBean.getItemAttrList().size(); i++) {
            ItemListBean.ItemAttrListBean itemAttrListBean = itemListBean.getItemAttrList().get(i);
            if (itemAttrListBean != null && "1".equals(itemAttrListBean.getAttrType())) {
                goodShopMark.setImageLevel(1);
                break;
            }
            if (itemAttrListBean != null && "2".equals(itemAttrListBean.getAttrType())) {
                goodShopMark.setImageLevel(2);
                break;
            }
            if (itemAttrListBean != null && "3".equals(itemAttrListBean.getAttrType())) {
                goodShopMark.setImageLevel(3);
                break;
            }
        }

        // 价格
        int promotionPrice = itemListBean.getPromotionPrice() != null && itemListBean.getPromotionPrice().matches("^[0-9]*$") ? Integer.valueOf(itemListBean.getPromotionPrice()) : -1;
        int price = itemListBean.getPrice() != null && itemListBean.getPrice().matches("^[0-9]*$") ? Integer.valueOf(itemListBean.getPrice()) : -1;

        promotionPrice = promotionPrice > 0 ? promotionPrice : price;
        price = price > 0 ? price : promotionPrice;

        if (promotionPrice < 0) {
            goodRealPrice.setText(format.format(price / 100f));
            goodPrice.setText(format.format(price / 100f));
        } else {
            goodRealPrice.setText(format.format(promotionPrice / 100f));
            goodPrice.setText(format.format(price / 100f));
        }

        goodPrice.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
        if (promotionPrice < price) {
            goodPrice.setVisibility(View.VISIBLE);
        } else {
            goodPrice.setVisibility(View.GONE);
        }

        // 月销
        goodSellCount.setText(String.format("月销%s笔", TextUtils.isEmpty(itemListBean.getSaleCount()) ? "0" : itemListBean.getSaleCount()));

        // 优惠提示 tip
        int index = 0;
        for (int i = 0; itemListBean.tagList != null && i < itemListBean.tagList.size() && index < sale_tip.length; i++) {
            ItemListBean.Tag tag = itemListBean.tagList.get(i);
            boolean hasContent = false;
            if (tag != null && !TextUtils.isEmpty(tag.text)) {
                sale_tip[index].setText(tag.text);
                sale_tip[index].setVisibility(View.VISIBLE);
                hasContent = true;
            } else {
                sale_tip[index].setVisibility(View.GONE);
            }
            if (tag != null && !TextUtils.isEmpty(tag.subText)) {
                sale_tip[index + 1].setText(tag.subText);
                sale_tip[index + 1].setVisibility(View.VISIBLE);
                hasContent = true;
            } else {
                sale_tip[index + 1].setVisibility(View.GONE);
            }
            if (hasContent) {
                index += 2;
            }
        }
        for (; index < sale_tip.length; index++) {
            sale_tip[index].setVisibility(View.GONE);
        }

        switch (itemListBean.getGoodStatus()) {
            case normal:
                goodCount.setVisibility(View.GONE);
                goodReduceWrapper.setVisibility(View.GONE);
                goodAddWrapper.setVisibility(View.VISIBLE);
                goodRealPrice.setTextColor(context.getResources().getColor(R.color.color_ff6000));
                break;
            case edit:
                goodCount.setVisibility(View.VISIBLE);
                goodReduceWrapper.setVisibility(View.VISIBLE);
                goodAddWrapper.setVisibility(View.VISIBLE);
                goodRealPrice.setTextColor(context.getResources().getColor(R.color.color_ff6000));
                break;
            case outStock:
                goodCount.setVisibility(View.GONE);
                goodReduceWrapper.setVisibility(View.GONE);
                goodAddWrapper.setVisibility(View.GONE);
                goodPicToast.setVisibility(View.VISIBLE);
                goodRealPrice.setTextColor(context.getResources().getColor(R.color.color_a2aaba));

                break;
            case rest:
                goodCount.setVisibility(View.GONE);
                goodReduceWrapper.setVisibility(View.GONE);
                goodAddWrapper.setVisibility(View.GONE);
                goodRealPrice.setTextColor(context.getResources().getColor(R.color.color_a2aaba));
                break;
        }

        if (itemView instanceof FocusArea) {
            ((FocusArea) itemView).getNode().setRoutineCallBack(new FocusNode.RCBImpl() {
                @Override
                public void onPreFindNext() {
                    super.onPreFindNext();
                    if (!((FocusArea) itemView).getNode().isNodeHasFocus()) {
                        goodAddWrapper.getNode().setPriority(System.currentTimeMillis());
                    } else {
                        goodAddWrapper.getNode().setPriority(0);
                    }
                }

                @Override
                public void onPostFindNext() {
                    super.onPostFindNext();
                }
            });
            ((FocusArea) itemView).setFocusConsumer(new ConsumerB() {
                @Override
                public boolean onFocusEnter() {
                    goodFocusStatus.setVisibility(View.VISIBLE);
                    goodName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    goodName.setMarqueeRepeatLimit(-1);
                    goodName.setSelected(true);
                    if (((FocusArea) itemView).getNode().getFocusBalance() == 1) {
                        itemView.setPivotX(itemView.getWidth() / 2);
                        itemView.setPivotY(itemView.getHeight() / 2);
                        itemView.setScaleX(1.0f);
                        itemView.setScaleY(1.0f);
                        animator.start();
                    } else if (((FocusArea) itemView).getNode().getFocusBalance() > 1) {
                        itemView.setPivotX(itemView.getWidth() / 2);
                        itemView.setPivotY(itemView.getHeight() / 2);
                        itemView.setScaleX(1.08f);
                        itemView.setScaleY(1.08f);
                    }

                    if (!((FocusArea) itemView).getNode().refocusToLeaf()) {
                        goodAddWrapper.getNode().requestFocus(((FocusArea) itemView).getNode());
                    }
                    if (itemListBean != null) {
                        // todo 埋点
                        Map<String, String> properties = Utils.getProperties();
                        properties.put("shop id", itemListBean.getShopId());
                        BaseActivity baseActivity = (BaseActivity) context;
                        Utils.utControlHit(baseActivity.getFullPageName(), "Focus_waimai_shop_floor_name_p" + getPosNow(), properties);
                        AppDebug.e("ViewHolder", "focus");
                    }
                    return true;
                }

                @Override
                public boolean onFocusLeave() {
                    goodFocusStatus.setVisibility(View.INVISIBLE);
                    goodName.setSelected(false);
                    goodName.setEllipsize(TextUtils.TruncateAt.END);
                    animator.cancel();
                    itemView.setPivotX(itemView.getWidth() / 2);
                    itemView.setPivotY(itemView.getHeight() / 2);
                    itemView.setScaleX(1.0f);
                    itemView.setScaleY(1.0f);
                    invalidatePath();
                    return true;
                }
            });
            if (((FocusArea) itemView).getNode().isNodeFocusable()) {
                if (((FocusArea) itemView).getNode().isNodeHasFocus()) {
                    ((FocusArea) itemView).getNode().onFocusEnter();
                } else {
                    ((FocusArea) itemView).getNode().onFocusLeave();
                }
            }
        }


    }

    public enum Event {
        Add, Reduce;
        private ItemListBean data;
        private int pos;

        public Event wrap(ItemListBean data, int pos) {
            this.data = data;
            this.pos = pos;
            return this;
        }

        public ItemListBean getData() {
            return data;
        }

        public int getPos() {
            return pos;
        }
    }
}
