package com.yunos.tvtaobao.tradelink.buildorder.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taobao.wireless.trade.mbuy.sdk.co.basic.RichSelectComponent;
import com.taobao.wireless.trade.mbuy.sdk.co.basic.RichSelectOption;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.InstallmentOption;
import com.taobao.wireless.trade.mbuy.sdk.co.biz.InstallmentPickerComponent;
import com.yunos.tv.app.widget.Interpolator.Linear;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.tradelink.R;
import com.yunos.tvtaobao.tradelink.activity.BuildOrderActivity;
import com.yunos.tvtaobao.tradelink.buildorder.component.InstallmentChoiceComponent;
import com.yunos.tvtaobao.tradelink.buildorder.component.Vip88CardComponent;

import java.lang.ref.WeakReference;
import java.util.Map;

public abstract class MultiOptionViewHolder<V> extends RecyclerView.ViewHolder {
    public MultiOptionViewHolder(View itemView) {

        super(itemView);
    }

    public interface OnOptionChangeListener<C, O> {
        void onOptionChanged(C component, O selectedOption);
    }

    private WeakReference<OnOptionChangeListener> listenerRef;

    public void setOptionChangeListener(OnOptionChangeListener listenerRef) {
        this.listenerRef = listenerRef == null ? null : new WeakReference<OnOptionChangeListener>(listenerRef);
    }

    public OnOptionChangeListener getListenerRef() {
        return listenerRef == null ? null : listenerRef.get();
    }

    public abstract void bindData(V data);


    public static class Vip88TitleViewHolder extends MultiOptionViewHolder<Vip88CardComponent.VipOptionComponent> {
        private TextView title;
        private TextView subTitle1;
        private TextView subTitle2;
        private ImageView icon;


        public Vip88TitleViewHolder(Context context) {
            super(View.inflate(context, R.layout.trade_multioption_item_title, null));
            title = (TextView) itemView.findViewById(R.id.title);
            subTitle1 = (TextView) itemView.findViewById(R.id.subtitle1);
            subTitle2 = (TextView) itemView.findViewById(R.id.subtitle2);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            subTitle1.setVisibility(View.GONE);
            subTitle2.setVisibility(View.GONE);
        }

        @Override
        public void bindData(Vip88CardComponent.VipOptionComponent data) {
            this.title.setText(data.infoComponent.getTitle());
            if (data.infoComponent.getIcon() != null) {
                icon.setVisibility(View.VISIBLE);
                ImageLoaderManager.getImageLoaderManager(itemView.getContext()).displayImage(data.infoComponent.getIcon(), icon);
            } else {
                icon.setVisibility(View.GONE);
            }
        }
    }

    public static class Vip88ContentViewHolder extends MultiOptionViewHolder<Vip88CardComponent.VipOptionComponent> implements View.OnClickListener, View.OnFocusChangeListener {
        TextView titleView;
        TextView subTitleView;
        TextView tag;
        ImageView check;

        private Vip88CardComponent.VipOptionComponent component;

        public Vip88ContentViewHolder(Context context) {
            super(View.inflate(context, R.layout.trade_multioption_item, null));
            titleView = (TextView) itemView.findViewById(R.id.title);
            subTitleView = (TextView) itemView.findViewById(R.id.subtitle);
            tag = (TextView) itemView.findViewById(R.id.tag);
            check = (ImageView) itemView.findViewById(R.id.check);
            itemView.setOnClickListener(this);
            itemView.setOnFocusChangeListener(this);
        }

        @Override
        public void bindData(Vip88CardComponent.VipOptionComponent data) {
            component = data;
            tag.setVisibility(View.GONE);
            titleView.setText(data.getTitle());
            subTitleView.setText(data.getTip());
            renderStatus();
        }

        @Override
        public void onClick(View v) {

            if (v == itemView) {
                boolean checked = component.isChecked();
                component.setChecked(!checked);
                renderStatus();
                Map<String, String> params = Utils.getProperties();
                params.put("controltag", "88VIP_OPTION");
                params.put("controlname", component.getTip());
                if (itemView.getContext() instanceof BuildOrderActivity) {
                    BuildOrderActivity activity = (BuildOrderActivity) itemView.getContext();
                    activity.utControlHit("88VIP_OPTION", params);
                } else {
                    Utils.utControlHit("88VIP_OPTION", params);
                }
            }
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v == itemView) {
                renderStatus();
            }
        }

        private void renderStatus() {
            titleView.setTextColor(itemView.hasFocus() ? Color.WHITE : Color.BLACK);
            subTitleView.setTextColor(itemView.hasFocus() ? Color.WHITE : 0xff8c94a3);
            if (!component.isChecked()) {
                check.setVisibility(View.GONE);
            } else {
                check.setImageResource(itemView.hasFocus() ?
                        R.drawable.trade_multioption_check_focus : R.drawable.trade_multioption_check);
                check.setVisibility(View.VISIBLE);
            }
        }

    }

    public static class InstallmentTitleViewHolder extends MultiOptionViewHolder<InstallmentPickerComponent.OrderInstallmentPicker> {
        private TextView title;
        private ImageView icon;

        public InstallmentTitleViewHolder(Context context) {
            super(View.inflate(context, R.layout.trade_multioption_installmentitem_title, null));
            title = (TextView) itemView.findViewById(R.id.title);
            icon = (ImageView) itemView.findViewById(R.id.icon);
        }

        @Override
        public void bindData(InstallmentPickerComponent.OrderInstallmentPicker data) {
            this.title.setText(data.getShopName());
            if (!TextUtils.isEmpty(data.getShopIcon())) {
                icon.setVisibility(View.VISIBLE);
                ImageLoaderManager.getImageLoaderManager(itemView.getContext()).displayImage(data.getShopIcon(), icon);
            } else {
                icon.setVisibility(View.GONE);
            }
        }
    }

    public static class InstallmentContentViewHolder extends MultiOptionViewHolder<InstallmentPickerComponent.OrderInstallmentPicker> implements View.OnClickListener, View.OnFocusChangeListener {
        TextView subtitle1;
        TextView subtitle2;
        //        ImageView check;
        LinearLayout container;

        private InstallmentOption selectedOption = null;

        private InstallmentPickerComponent.OrderInstallmentPicker component;

        public InstallmentContentViewHolder(Context context) {

            super(View.inflate(context, R.layout.trade_multioption_installment_content, null));
            container = itemView.findViewById(R.id.container);
            subtitle1 = itemView.findViewById(R.id.subtitle1);
            subtitle2 = itemView.findViewById(R.id.subtitle2);
//            super(View.inflate(context, R.layout.trade_multioption_item, null));
//            titleView = (TextView) itemView.findViewById(R.id.title);
//            subTitleView = (TextView) itemView.findViewById(R.id.subtitle);
//            tag = (TextView) itemView.findViewById(R.id.tag);
//            check = (ImageView) itemView.findViewById(R.id.check);
//            itemView.setOnClickListener(this);
//            itemView.setOnFocusChangeListener(this);
        }

        @Override
        public void bindData(InstallmentPickerComponent.OrderInstallmentPicker data) {
            component = data;
            selectedOption = data.getOptionBySelectedNum(data.getSelectedNum());
            container.removeAllViews();
            int size = component.getOptions().size();
            int lines = size / 3 + (size % 3 == 0 ? 0 : 1);
            for (int i = 0; i < lines; i++) {
                LinearLayout linearLayout = new LinearLayout(container.getContext());
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                for (int j = 0; j + i * 3 < size && j < 3; j++) {
                    InstallmentOption option = data.getOptions().get((j + i * 3));
                    View vg = View.inflate(container.getContext(), R.layout.trade_multioption_item, null);
                    ViewGroup.LayoutParams lp = vg.getLayoutParams();
                    vg.setOnFocusChangeListener(this);
                    vg.setOnClickListener(this);
                    vg.setTag(option);
                    if (lp != null) {
                        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(lp.width, lp.height);
                        llp.leftMargin = j == 0 ? 0 : container.getResources().getDimensionPixelSize(R.dimen.dp_f_0_7);
                        llp.topMargin = i == 0 ? 0 : container.getResources().getDimensionPixelSize(R.dimen.dp_f_0_7);
                        linearLayout.addView(vg, llp);
                    } else {
                        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        llp.leftMargin = j == 0 ? 0 : container.getResources().getDimensionPixelSize(R.dimen.dp_f_0_7);
                        llp.topMargin = i == 0 ? 0 : container.getResources().getDimensionPixelSize(R.dimen.dp_f_0_7);
                        linearLayout.addView(vg, llp);
                    }
                }
                container.addView(linearLayout);//TODO
            }
//            tag.setVisibility(View.GONE);
//            titleView.setText(data.getTitle());
//            subTitleView.setText(data.getTip());
//            renderStatus();
            refresh();
        }

        @Override
        public void onClick(View v) {
            if (v.getTag() != null && v.getTag() instanceof InstallmentOption) {
                InstallmentOption option = (InstallmentOption) v.getTag();
                if (selectedOption != option) {
                    selectedOption = option;
                } else {
                    selectedOption = null;//取消选择
                }
                if (getListenerRef() != null) {
                    getListenerRef().onOptionChanged(component, selectedOption);
                }
                refresh();
            }
        }

        public void refresh() {
            String priceString = null;
            String poundageString = null;
            if (selectedOption != null) {
                priceString = String.format("分期金额 %s %s", component.getCurrencySymbol(), component.getOrderPriceText());
                poundageString = String.format("手续费 %s %s", selectedOption.getCurrencySymbol(), selectedOption.getPoundageText());
            } else {
                priceString = String.format("分期金额 %s 0", component.getCurrencySymbol());
                poundageString = String.format("手续费 %s 0", component.getCurrencySymbol());
            }
            subtitle1.setText(convertPriceText(priceString, component.getCurrencySymbol(), 0xffff6000));
            subtitle2.setText(poundageString);
            for (int i = 0; i < container.getChildCount(); i++) {
                View subContainer = container.getChildAt(i);
                if (subContainer instanceof ViewGroup) {
                    for (int j = 0; j < ((ViewGroup) subContainer).getChildCount(); j++) {
                        View view = ((ViewGroup) subContainer).getChildAt(j);
                        renderStatus(view, null, component);
                    }
                }
            }
        }

        private SpannableString convertPriceText(String text, String currencySymbol, int color) {
            SpannableString spannableString = new SpannableString(text);
            int start = text.indexOf(currencySymbol);
            ForegroundColorSpan span = new ForegroundColorSpan(color);
            spannableString.setSpan(span, start, text.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            return spannableString;
        }

        public InstallmentOption getSelectedOption() {
            return selectedOption;
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            renderStatus(v, null, component);
        }

        private void renderStatus(View itemView, InstallmentOption option, InstallmentPickerComponent.OrderInstallmentPicker picker) {
            TextView titleView = (TextView) itemView.findViewById(R.id.title);
            TextView subTitleView = (TextView) itemView.findViewById(R.id.subtitle);
            TextView tag = (TextView) itemView.findViewById(R.id.tag);
            ImageView check = (ImageView) itemView.findViewById(R.id.check);
            if (titleView == null || subTitleView == null || tag == null || check == null) {
                return;
            }
            tag.setVisibility(View.GONE);

            titleView.setTextColor(itemView.hasFocus() ? Color.WHITE : Color.BLACK);
            subTitleView.setTextColor(itemView.hasFocus() ? Color.WHITE : 0xff8c94a3);
            try {
                if (option == null)
                    option = (InstallmentOption) itemView.getTag();

            } catch (Exception e) {
            } finally {
                if (option != null && picker != null) {
                    titleView.setText(option.getTitle());
                    subTitleView.setText(option.getSubtitle());
                    if (selectedOption != option) {
                        check.setVisibility(View.GONE);
                    } else {
                        //TODO
                        check.setImageResource(itemView.hasFocus() ?
                                R.drawable.trade_multioption_check_focus : R.drawable.trade_multioption_check);
                        check.setVisibility(View.VISIBLE);
                    }
                    tag.setText(option.getTip());
                    if (!TextUtils.isEmpty(option.getTip())) {
                        tag.setVisibility(View.VISIBLE);
                    }
                }
            }

        }

    }

}
