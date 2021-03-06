package com.yunos.tvtaobao.newcart.ui.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.ShopCoupon;
import com.yunos.tvtaobao.newcart.R;
import com.yunos.tvtaobao.newcart.ui.activity.CouponActivity;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

/**
 * Created by xutingting on 2017/9/16.
 */

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder> {
    private Context context;
    private List<ShopCoupon> mShopCouponList;

    private BusinessRequest mBusinessRequest;
    private String mSellerId;

    private AlphaDialog dialog;//自定义样式
    private ImageView ivApplySuccess;
    private TextView tvApplyResult;


    public CouponAdapter(Context context, List<ShopCoupon> mShopCouponList, String mSellerId) {
        this.context = context;
        this.mShopCouponList = mShopCouponList;
        this.mSellerId = mSellerId;
        mBusinessRequest = BusinessRequest.getBusinessRequest();
        View view = LayoutInflater.from(context).inflate(R.layout.newcart_coupon_apply_success, null);
        ivApplySuccess = (ImageView) view.findViewById(R.id.iv_icon);
        tvApplyResult = (TextView) view.findViewById(R.id.tv_apply_result);

        dialog = new AlphaDialog(context, view);
    }

    @Override
    public CouponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.newcart_coupon_item, parent, false);
        CouponViewHolder holder = new CouponViewHolder(view);
        return holder;


    }

    public void setData(List<ShopCoupon> mShopCouponList) {
        this.mShopCouponList = mShopCouponList;
        notifyDataSetChanged();

    }

    @Override
    public void onBindViewHolder(final CouponViewHolder holder, final int position) {
        final ShopCoupon shopCoupon = mShopCouponList.get(position);
        Log.d("shopCoupon", "position = " + position + "num=" + shopCoupon.getDiscountFee());
        int discountFee = Integer.parseInt(shopCoupon.getDiscountFee());
        holder.tvMoney.setText("¥ " + shopCoupon.getDiscountFee());
        holder.tvRequireMoneyUse.setText(shopCoupon.getDesc().replace("（不含邮费）", "").replace("订单", "").replace("元使用", "元可使用"));
        holder.tvRequireTimeUse.setText(shopCoupon.getValidTime()
                .replace("-", "~")
                .replace("使用期限", "有效期"));

        if (shopCoupon.getType() == 1) {
            holder.tvRequireGoodsUsePart.setVisibility(View.GONE);
            if (discountFee >= 50) {
                holder.tvRequireGoodsUsePart.setBackgroundResource(R.drawable.newcart_coupon_all_goods_more_than_fifty);
            } else {
                holder.tvRequireGoodsUsePart.setBackgroundResource(R.drawable.newcart_coupon_all_goods_less_than_fifty);
            }
        } else if (shopCoupon.getType() == 0) {
            holder.tvRequireGoodsUsePart.setVisibility(View.VISIBLE);
            if (discountFee >= 50) {
                holder.tvRequireGoodsUsePart.setBackgroundResource(R.drawable.newcart_coupon_part_goods_more_than_fifty);
            } else {
                holder.tvRequireGoodsUsePart.setBackgroundResource(R.drawable.newcart_coupon_part_goods_less_than_fifty);
            }
        } else {
            holder.tvRequireGoodsUsePart.setVisibility(View.GONE);
        }

        if (discountFee >= 50) {
            holder.tvApply.setTextColor(Color.parseColor("#f16a1b"));

            holder.rlBackground.setBackgroundResource(R.drawable.newcart_coupon_bg_more_than_fifty);
        } else {
            holder.tvApply.setTextColor(Color.parseColor("#4474c4"));
            holder.rlBackground.setBackgroundResource(R.drawable.newcart_coupon_bg_less_than_fifty);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("shopCoupon", "position = " + position + "num=" + shopCoupon.getDiscountFee());

                Utils.utControlHit(((CouponActivity)context).getFullPageName(), "Button", initTBSProperty( SPMConfig.NEW_SHOP_CART_LIST_SPM_COUPON_GET));
                Utils.updateNextPageProperties(SPMConfig.NEW_SHOP_CART_LIST_SPM_COUPON_BUTTON);

                if (CoreApplication.getLoginHelper(context).isLogin()) {
                    mBusinessRequest.applyShopCoupon(mSellerId, shopCoupon.getActivityId(),
                            new ApplyCouponBusinessRequestListener(new WeakReference<BaseActivity>((CouponActivity) context),
                                    position, holder));
                } else {
                    CoreApplication.getLoginHelper(context).startYunosAccountActivity(context, false);

                }

            }
        });

        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    holder.rlBackgroundItemFocused.setBackgroundResource(R.drawable.newcart_coupon_item_focused);
                } else {
                    holder.rlBackgroundItemFocused.setBackgroundResource(0);

                }
            }
        });

        if (position == 0) {
            holder.itemView.requestFocus();
            holder.rlBackgroundItemFocused.setBackgroundResource(R.drawable.newcart_coupon_item_focused);
        }

    }

    @Override
    public int getItemCount() {
        if (mShopCouponList != null && mShopCouponList.size() > 0) {
            return mShopCouponList.size();
        } else {
            return 0;
        }
    }

    class CouponViewHolder extends RecyclerView.ViewHolder {

        private ImageView rlBackgroundItemFocused;
        private ConstraintLayout rlBackground;
        private TextView tvMoney;
        private TextView tvRequireMoneyUse;
        private ImageView tvRequireGoodsUsePart;
        private TextView tvRequireTimeUse;

        private TextView tvApply;


        public CouponViewHolder(View view) {
            super(view);
            rlBackgroundItemFocused = (ImageView) view.findViewById(R.id.rl_bg_item);
            rlBackground = (ConstraintLayout) view.findViewById(R.id.rl_bg);
            tvMoney = (TextView) view.findViewById(R.id.tv_money);
            tvRequireMoneyUse = (TextView) view.findViewById(R.id.tv_require_money_use);
            tvRequireGoodsUsePart = (ImageView) view.findViewById(R.id.iv_coupon_fit_flag);
            tvRequireTimeUse = (TextView) view.findViewById(R.id.tv_require_time_use);
            tvApply = (TextView) view.findViewById(R.id.tv_apply);

        }
    }


    private class ApplyCouponBusinessRequestListener extends BizRequestListener<JSONObject> {

        private int position;
        private CouponViewHolder holer;

        public ApplyCouponBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef, int position, CouponViewHolder holder) {
            super(mBaseActivityRef);
            this.position = position;
            this.holer = holder;
        }


        @Override
        public boolean onError(int resultCode, String msg) {
            CouponActivity couponActivity = (CouponActivity) mBaseActivityRef.get();
            if (couponActivity != null) {
                couponActivity.OnWaitProgressDialog(false);
                ivApplySuccess.setVisibility(View.GONE);
                String hintText = msg;
                //特殊处理，提示文字转化
                if(msg.equals("亲，您已领过了哦！")){
                    hintText = "您已领取过啦";
                }else if(msg.equals("亲，券已领完了，下次早点来哦！")){
                    hintText = "优惠券被抢光啦";
                }
                tvApplyResult.setText(hintText);
                dialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 2000);


            }
            return true;
        }

        @Override
        public void onSuccess(JSONObject data) {
            CouponActivity couponActivity = (CouponActivity) mBaseActivityRef.get();
            if (couponActivity != null) {
                couponActivity.OnWaitProgressDialog(false);
//                AppDebug.showToast(couponActivity, "领取成功");
                holer.tvApply.setText("已领取");
                tvApplyResult.setText("领取成功");
                dialog.show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 2000);


            }
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }


    class AlphaDialog extends Dialog {

        private Window window = null;

        public AlphaDialog(Context context, View view) {
            super(context);

            setContentView(view);

            windowDeploy();
        }

        //设置窗口显示
        public void windowDeploy() {
            window = getWindow(); //得到对话框
            window.setWindowAnimations(R.style.AlphaAnimDialog); //设置窗口弹出动画
            window.setBackgroundDrawableResource(R.color.transparent); //设置对话框背景为透明
            WindowManager.LayoutParams wl = window.getAttributes();
            //根据x，y坐标设置窗口需要显示的位置
//            wl.alpha = 0.6f; //设置透明度
//            wl.gravity = Gravity.BOTTOM; //设置重力
            window.setAttributes(wl);
        }
    }


    public Map<String, String> initTBSProperty( String spm) {

        Map<String, String> p = Utils.getProperties();

        if(!TextUtils.isEmpty(spm)){
            p.put("spm",spm);
        }

        return p;

    }

}
