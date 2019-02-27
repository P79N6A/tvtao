package com.yunos.tvtaobao.search.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yunos.tvtaobao.search.R;

import java.util.ArrayList;

/**
 * <pre>
 *     author : panbeixing
 *     time   : 2018/12/10
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class AssociateWordsAdapter extends RecyclerView.Adapter {
    private Context context;
    private ArrayList<String> words;
    private OnItemClickListener itemClickListener;

    private int TYPE_HEADER = 0;
    private int TYPE_CONTEXT = 1;
    private int TYPE_FOOTER = 2;

    public AssociateWordsAdapter(Context context) {
        this.context= context;
    }

    public void setItemData(ArrayList<String> words) {
        this.words = words;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, String word, int position);
    }

    public void setItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        }
//        if (getItemCount() - 1 > 7) { //少于7条时，不展示没有更多提示
            if (position == getItemCount() - 1) {
                return TYPE_FOOTER;
            }
//        }

        return TYPE_CONTEXT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            return new HeaderHolder(new TextView(context));
        } else if (viewType == TYPE_FOOTER) {
            return new FooterHolder(new TextView(context));
        } else {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.ytm_associate_result, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            final ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.tvAssociateWord.setText(words.get(position - 1));
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(view, words.get(position - 1), position);
                    }
                }
            });
            viewHolder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        viewHolder.cuttingLine.setVisibility(View.GONE);
                    } else {
                        viewHolder.cuttingLine.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return words == null ? 0 : words.size() == 0 ? 0 : words.size() + 2;
    }

    private class HeaderHolder extends RecyclerView.ViewHolder {
        private TextView headerTip;
        public HeaderHolder(View itemView) {
            super(itemView);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.dp_63));
            lp.topMargin = (int) context.getResources().getDimension(R.dimen.dp_64);
//            lp.gravity = Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM;

            headerTip = (TextView) itemView;
            headerTip.setText(context.getResources().getString(R.string.ytm_think_you_like));
            headerTip.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.sp_32));
            headerTip.setTextColor(Color.parseColor("#ffffff"));
            headerTip.setLayoutParams(lp);
            headerTip.setFocusable(false);
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvAssociateWord;
        public View cuttingLine;
        public ViewHolder(View itemView) {
            super(itemView);
            tvAssociateWord = (TextView) itemView.findViewById(R.id.associate_word);
            cuttingLine = itemView.findViewById(R.id.cutting_line);
        }
    }

    private class FooterHolder extends RecyclerView.ViewHolder {
        private TextView footerTip;
        public FooterHolder(View itemView) {
            super(itemView);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) context.getResources().getDimension(R.dimen.dp_158_7));
//            lp.topMargin = (int) context.getResources().getDimension(R.dimen.dp_62);
            lp.bottomMargin = (int) context.getResources().getDimension(R.dimen.dp_58);
//            lp.gravity = Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM;

            footerTip = (TextView) itemView;
            footerTip.setText(context.getResources().getString(R.string.ytm_search_not_you_like));
            footerTip.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources().getDimensionPixelSize(R.dimen.sp_28));
            footerTip.setTextColor(Color.parseColor("#8c94a3"));
            footerTip.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
            footerTip.setLayoutParams(lp);
            footerTip.setFocusable(false);
        }
    }
}
