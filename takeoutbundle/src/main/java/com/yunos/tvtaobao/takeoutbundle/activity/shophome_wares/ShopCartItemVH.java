package com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.focus_impl.ConsumerB;
import com.yunos.tvtaobao.biz.focus_impl.FakeListView;
import com.yunos.tvtaobao.biz.focus_impl.FocusArea;
import com.yunos.tvtaobao.biz.focus_impl.FocusNode;
import com.yunos.tvtaobao.biz.request.bo.TakeOutBag;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.R2;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by GuoLiDong on 2018/10/26.
 */
public class ShopCartItemVH extends FakeListView.ViewHolder {
    private final static String TAG = "ShopCartItemVH";
    private final static DecimalFormat format = new DecimalFormat("¥#.##");

    public static boolean refocusOnReduce = false;

    Context context;
    Unbinder unbinder;
    @BindView(R2.id.good_name)
    TextView goodName;
    @BindView(R2.id.good_reduce)
    ImageView goodReduce;
    @BindView(R2.id.good_reduce_wrapper)
    FocusArea goodReduceWrapper;
    @BindView(R2.id.good_count)
    TextView goodCount;
    @BindView(R2.id.good_add)
    ImageView goodAdd;
    @BindView(R2.id.good_add_wrapper)
    FocusArea goodAddWrapper;
    @BindView(R2.id.good_price)
    TextView goodPrice;
    @BindView(R2.id.good_ori_price)
    TextView goodOriPrice;
    @BindView(R2.id.good_num_info)
    TextView goodNumInfo;
    @BindView(R2.id.good_sku)
    TextView goodSku;
    @BindView(R2.id.item_focus_status)
    ImageView itemFocusStatus;

    private StringBuilder stringBuilder = new StringBuilder();

    ValueAnimator valueAnimator;
    Animator.AnimatorListener endHandler;
    Drawable focusEnter, focusLeave, unfocus;

    public ShopCartItemVH(Context context, ViewGroup parent) {
        super(LayoutInflater.from(context).inflate(R.layout.item_takeout_good_collection, parent, false));
        this.context = context;
        unbinder = ButterKnife.bind(this, itemView);
        valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(300);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setTarget(itemFocusStatus);
        focusEnter = context.getResources().getDrawable(R.drawable.good_collection_item_selected_bg);
        focusLeave = context.getResources().getDrawable(R.drawable.good_collection_item_bg);
        unfocus = context.getResources().getDrawable(R.drawable.bg_empty);
        endHandler = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                valueAnimator.removeAllUpdateListeners();
                valueAnimator.removeAllListeners();
                itemFocusStatus.setAlpha(1.0f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
    }

    public void loadData(final TakeOutBag.CartItemListBean mData) {
        goodAddWrapper.setFocusConsumer(new ConsumerB() {
            @Override
            public boolean onFocusEnter() {
                goodAdd.setImageLevel(0);
                return true;
            }

            @Override
            public boolean onFocusLeave() {
                goodAdd.setImageLevel(1);
                return true;
            }

            @Override
            public boolean onFocusClick() {
                AppDebug.e(TAG, "goodAdd position = " + getPosNow());
                EventBus.getDefault().post(Event.Add.wrap(mData, getPosNow()));
                return true;
            }
        });

        goodReduceWrapper.setFocusConsumer(new ConsumerB() {
            @Override
            public boolean onFocusEnter() {
                goodReduce.setImageLevel(0);
                return true;
            }

            @Override
            public boolean onFocusLeave() {
                goodReduce.setImageLevel(1);
                return true;
            }

            @Override
            public boolean onFocusClick() {
                AppDebug.e(TAG, "goodReduce position = " + getPosNow());
                EventBus.getDefault().post(Event.Reduce.wrap(mData, getPosNow()));
                return true;
            }
        });

        goodName.setText(mData.title);
        goodName.setEllipsize(TextUtils.TruncateAt.END);
        goodName.setSelected(false);
        if (!mData.isPackingFee) {
            itemFocusStatus.setImageDrawable(focusLeave);
            goodCount.setText(String.valueOf("x" + mData.amount));
        } else {
            itemFocusStatus.setImageDrawable(unfocus);
            goodCount.setText("");
        }
        if (itemView instanceof FocusArea) {
            if (((FocusArea) itemView).getNode().isNodeHasFocus()) {
                if (!mData.isPackingFee) {
                    goodCount.setText(String.valueOf(mData.amount));
                } else {
                    goodCount.setText("");
                }
            } else {
                if (!mData.isPackingFee) {
                    goodCount.setText(String.valueOf("x" + mData.amount));
                } else {
                    goodCount.setText("");
                }
            }

        }
        if (goodAddWrapper.getNode().isNodeHasFocus()) {
            goodAdd.setImageLevel(0);
        } else {
            goodAdd.setImageLevel(1);
        }

        if (goodReduceWrapper.getNode().isNodeHasFocus()) {
            goodReduce.setImageLevel(0);
        } else {
            goodReduce.setImageLevel(1);
        }

        mData.totalPromotionPrice = mData.totalPromotionPrice == 0 ? mData.totalPrice : mData.totalPromotionPrice;

        goodPrice.setText(format.format((mData.totalPromotionPrice) / 100f));
        goodOriPrice.setText(format.format((mData.totalPrice) / 100f));
        if (mData.totalPromotionPrice < mData.totalPrice) {
            goodOriPrice.setVisibility(View.GONE);
        } else {
            goodOriPrice.setVisibility(View.GONE);
        }

        stringBuilder.setLength(0);
        if (!TextUtils.isEmpty(mData.skuName)) {
            stringBuilder.append(mData.skuName);
            stringBuilder.append(", ");
        }
        for (int i = 0; mData.skuProperties != null && i < mData.skuProperties.size(); i++) {
            TakeOutBag.CartItemListBean.SkuPropertiesBean skuPropertiesBean = mData.skuProperties.get(i);
            String value = skuPropertiesBean.value;
            if (value != null && value.equals(mData.skuName)) { // 解决sku 重复显示 bug #13793663
                continue;
            }
            if (value != null && value.length() > 0) {
                stringBuilder.append(value);
                stringBuilder.append(", ");
            }
        }
        if (stringBuilder.length() > 1) {
            stringBuilder.setLength(stringBuilder.length() - 2);
            goodSku.setVisibility(View.VISIBLE);
            goodSku.setText(stringBuilder.toString());
        } else {
            goodSku.setVisibility(View.GONE);
        }


        if (!TextUtils.isEmpty(mData.priceDesc)) {
            goodNumInfo.setVisibility(View.VISIBLE);
            goodNumInfo.setText(mData.priceDesc);
        } else if (!TextUtils.isEmpty(mData.limitQuantity)) {
            goodNumInfo.setVisibility(View.VISIBLE);
            goodNumInfo.setText(String.format(context.getString(R.string.take_out_sku_num_limit), mData.limitQuantity));
        } else if (mData.quantity > 0 && mData.quantity <= 10) {
            goodNumInfo.setVisibility(View.VISIBLE);
            goodNumInfo.setText(String.format(context.getString(R.string.take_out_sku_num_stock), String.valueOf(mData.quantity)));
        } else {
            goodNumInfo.setText("");
            goodNumInfo.setVisibility(View.INVISIBLE);
        }

        if (itemView instanceof FocusArea) {
            if (mData.isPackingFee) {
                itemFocusStatus.setImageDrawable(unfocus);
                ((FocusArea) itemView).getNode().setNodeFocusable(false);
            } else {
                ((FocusArea) itemView).getNode().setNodeFocusable(true);
            }
            ((FocusArea) itemView).getNode().setRoutineCallBack(new FocusNode.RCBImpl() {
                @Override
                public void onPreFindNext() {
                    if (!((FocusArea) itemView).getNode().isNodeHasFocus()) {
                        goodAddWrapper.getNode().setPriority(System.currentTimeMillis());
                    } else {
                        goodAddWrapper.getNode().setPriority(0);
                    }
                }

                @Override
                public void onPreFittestLeaf() {
                    if (refocusOnReduce) {
                        goodReduceWrapper.getNode().setPriority(System.currentTimeMillis());
                    } else {
                        goodReduceWrapper.getNode().setPriority(0);
                    }
                }
            });
            ((FocusArea) itemView).setFocusConsumer(new ConsumerB() {
                @Override
                public boolean onFocusEnter() {
                    if (((FocusArea) itemView).getNode().getFocusBalance() == 1) {
                        endHandler.onAnimationEnd(valueAnimator);
                        valueAnimator.setFloatValues(0f, 1f);
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                itemFocusStatus.setAlpha((Float) animation.getAnimatedValue());
                            }
                        });
                        valueAnimator.addListener(endHandler);
                        valueAnimator.start();
                    }


                    itemFocusStatus.setImageDrawable(focusEnter);
                    goodName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    goodName.setMarqueeRepeatLimit(-1);
                    goodName.setSelected(true);
                    goodNumInfo.setBackgroundResource(R.drawable.good_collection_stock_bg);
                    goodPrice.setTextColor(context.getResources().getColor(R.color.white_ffffffff));
                    goodSku.setTextColor(context.getResources().getColor(R.color.white_ffffffff));
                    if (!mData.isPackingFee) {
                        goodCount.setText(String.valueOf(mData.amount));
                    } else {
                        goodCount.setText("");
                    }
                    if (mData.isPackingFee) {
                        goodAddWrapper.setVisibility(View.GONE);
                        goodReduceWrapper.setVisibility(View.GONE);
                    } else {
                        goodAddWrapper.setVisibility(View.VISIBLE);
                        goodReduceWrapper.setVisibility(View.VISIBLE);
                    }
                    if (!((FocusArea) itemView).getNode().refocusToLeaf()) {
                        goodAddWrapper.getNode().requestFocus(((FocusArea) itemView).getNode());
                    }
                    if (refocusOnReduce) {
                        refocusOnReduce = false;
                    }
                    return true;
                }

                @Override
                public boolean onFocusLeave() {
                    goodAddWrapper.setVisibility(View.GONE);
                    goodReduceWrapper.setVisibility(View.GONE);
                    goodName.setEllipsize(TextUtils.TruncateAt.END);
                    goodName.setSelected(false);
                    if (!mData.isPackingFee) {
                        goodCount.setText(String.valueOf("x" + mData.amount));
                    } else {
                        goodCount.setText("");
                    }
                    goodNumInfo.setBackgroundResource(R.drawable.good_collection_stock_unfocus_bg);
                    goodPrice.setTextColor(context.getResources().getColor(R.color.red_ff6000));
                    goodSku.setTextColor(context.getResources().getColor(R.color.white_8396ae));
                    itemFocusStatus.setImageDrawable(focusLeave);
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
        private TakeOutBag.CartItemListBean data;
        private int pos;

        public Event wrap(TakeOutBag.CartItemListBean data, int pos) {
            this.data = data;
            this.pos = pos;
            return this;
        }

        public TakeOutBag.CartItemListBean getData() {
            return data;
        }

        public int getPos() {
            return pos;
        }
    }
}
