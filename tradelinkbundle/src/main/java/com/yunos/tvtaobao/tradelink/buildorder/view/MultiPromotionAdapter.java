package com.yunos.tvtaobao.tradelink.buildorder.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.yunos.tvtaobao.tradelink.R;
import com.yunos.tvtaobao.tradelink.buildorder.bean.MultiOptionComponent;

import java.lang.ref.WeakReference;

public class MultiPromotionAdapter<T> extends RecyclerView.Adapter<MultiOptionViewHolder<MultiOptionComponent<T>>> implements MultiOptionViewHolder.OnOptionChangeListener {


    private MultiOptionComponent<T> optionComponents;

    public void setOptionComponents(MultiOptionComponent optionComponents) {
        this.optionComponents = optionComponents;
    }

    public interface OptionChangedListener<C, O> {
        void onOptionChanged(MultiOptionComponent component, C subComponent, O option);
    }

    private WeakReference<OptionChangedListener> optionChangedListenerRef;

    public void setOptionChangedListener(OptionChangedListener optionChangedListener) {
        this.optionChangedListenerRef = optionChangedListener == null ? null : new WeakReference<OptionChangedListener>(optionChangedListener);
    }

    public OptionChangedListener getOptionChangedListenerRef() {
        return optionChangedListenerRef == null ? null : optionChangedListenerRef.get();
    }

    @Override
    public MultiOptionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType != 99) {
            MultiOptionViewHolder vh = MultiOptionViewHolderFactory.createViewHolder(optionComponents, parent, viewType, this);
            return vh;
        } else {
            View view = new View(parent.getContext());
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(1, parent.getResources().getDimensionPixelSize(R.dimen.dp_30));
            view.setLayoutParams(layoutParams);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(MultiOptionViewHolder holder, int position) {
        if (position == getItemCount() - 1) {
            return;
        }
        T component = optionComponents.getComponentAt(position / 2);//both 0,1 positions share same component
        holder.bindData(component);
    }

    @Override
    public void onOptionChanged(Object component, Object selectedOption) {
        if (optionChangedListenerRef != null && optionChangedListenerRef.get() != null) {
            optionChangedListenerRef.get().onOptionChanged(optionComponents, component, selectedOption);
        }
    }

    @Override //todo 可以把option和title合并为一个item，减少*2和/2导致的错误
    public int getItemCount() {
        return optionComponents == null ? 0 : optionComponents.getComponentCount() * 2 + 1;//title + options + footer
    }

    @Override
    public int getItemViewType(int position) {
        return (position < getItemCount() - 1) ? position % 2 : 99;//title | options
    }

    private static class ViewHolder extends MultiOptionViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindData(Object data) {

        }
    }

}
