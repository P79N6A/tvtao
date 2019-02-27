package com.yunos.tvtaobao.newcart.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tvtaobao.newcart.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuhaoteng on 2018/8/23.
 */

public class FullScreenDialog extends Dialog{
    private RecyclerView mRecyclerView = null;
    private Context mContext = null;
    private FullScreenAdapter mFullScreenAdapter = null;

    public FullScreenDialog(Context context,List<String> urls) {
        super(context,R.style.FullScreenDialog);
        this.mContext = context;
        setContentView(R.layout.dialog_full_screen);
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.dimAmount = 0.0f;
        getWindow().setAttributes(lp);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mFullScreenAdapter = new FullScreenAdapter(urls);
        mRecyclerView.setAdapter(mFullScreenAdapter);
    }


    private class FullScreenAdapter extends RecyclerView.Adapter<FullScreenHolder>{
        private List<String> mUrls = new ArrayList<>();

        public FullScreenAdapter(List<String> urls) {
            mUrls.clear();
            mUrls.addAll(urls);
        }

        @Override
        public FullScreenHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_full_screen_dialog, parent, false);
            FullScreenHolder holder = new FullScreenHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(FullScreenHolder holder, int position) {
            holder.bindView(mUrls.get(position));
        }

        @Override
        public int getItemCount() {
            return mUrls.size();
        }
    }


    private class FullScreenHolder extends RecyclerView.ViewHolder{
        ImageView mImageView = null;

        public FullScreenHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.single_image);
        }

        private void bindView(String url){
            ImageLoaderManager.getImageLoaderManager(mContext).displayImage(url, mImageView);
        }
    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置全屏，要设置在show的后面
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;

        getWindow().getDecorView().setPadding(0, 0, 0, 0);

        getWindow().setAttributes(layoutParams);

        //设置默认第一个item聚焦
        if(mRecyclerView != null){
            View firstView = mRecyclerView.getChildAt(0);
            if(firstView!=null){
                firstView.requestFocus();
            }
        }
    }
}
