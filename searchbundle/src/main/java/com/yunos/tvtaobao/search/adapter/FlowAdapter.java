package com.yunos.tvtaobao.search.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yunos.tvtaobao.search.R;
import com.yunos.tvtaobao.search.listener.OnKeyWordItemClickListener;

import java.util.ArrayList;

public class FlowAdapter extends RecyclerView.Adapter<FlowAdapter.MyHolder>  {

    private ArrayList<String> list;
    private Context context;

    public FlowAdapter(ArrayList<String> list,Context context) {
        this.context = context;
        this.list = list;
    }

    private OnKeyWordItemClickListener onKeyWordItemClickListener;

    public void setTextClickListener(OnKeyWordItemClickListener onKeyWordItemClickListener) {
        this.onKeyWordItemClickListener = onKeyWordItemClickListener;
    }

    public void setList(ArrayList<String> list){
        this.list = list;
        notifyDataSetChanged();

    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.flowlayout_item, parent,false);
        return new MyHolder(view);
    }


    @Override
    public void onBindViewHolder(final MyHolder holder, final int position) {
        holder.text.setText(list.get(position));

        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, list.get(position), Toast.LENGTH_SHORT).show();
            }
        });


        holder.text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    holder.text.setTextColor(context.getResources().getColor(R.color.takeout_color_ffffff));
                }else {
                    holder.text.setTextColor(context.getResources().getColor(R.color.takeout_color_a2aaba));
                }
            }
        });

        holder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onKeyWordItemClickListener.onItemClick(list.get(position),position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {

        private TextView text;

        public MyHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.tv_flowlayout_item);
        }
    }


}