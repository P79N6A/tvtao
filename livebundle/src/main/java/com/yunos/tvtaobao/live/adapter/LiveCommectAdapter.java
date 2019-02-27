package com.yunos.tvtaobao.live.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yunos.tv.core.common.User;
import com.yunos.tvtaobao.live.R;
import com.yunos.tvtaobao.biz.request.bo.GlobalConfig;
import com.yunos.tvtaobao.biz.request.info.GlobalConfigInfo;
import com.yunos.tvtaobao.live.data.CommentBase;
import com.yunos.tvtaobao.live.utils.Tools;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pan on 16/11/29.
 */

public class LiveCommectAdapter extends BaseAdapter {
    private String TAG = LiveCommectAdapter.class.getName();
    private Context mContext;
    private List<CommentBase> mCommentList;
    private LayoutInflater inflater;
    private String tvLogo;

    public LiveCommectAdapter(Context context) {
        mContext = context;
        mCommentList = new ArrayList<>();
        inflater = LayoutInflater.from(context);

        GlobalConfig gc = GlobalConfigInfo.getInstance().getGlobalConfig();
        if (gc != null && gc.getTMallLive() != null && gc.getTMallLive().postfix() != null) {
            tvLogo = gc.getTMallLive().postfix();
        }
    }

    public void addItem(List<CommentBase> list) {
        try {
            synchronized (mCommentList) {
                mCommentList.addAll(list);
                if (mCommentList.size() > 50) {
                    removeItem();
                }
            }
            notifyDataSetChanged();
        } catch (Exception e) {
            mCommentList.clear();
            mCommentList.addAll(list);
        }
    }

    public void removeItem() {
        mCommentList = mCommentList.subList(15, mCommentList.size());
    }

    @Override
    public int getCount() {
        return mCommentList == null ? 0 : mCommentList.size();
    }

    @Override
    public Object getItem(int position) {
        return mCommentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CommentHolder holder;
        if (convertView == null) {
            holder = new CommentHolder();
            convertView = inflater.inflate(R.layout.item_taobao_live_comment, parent, false);
            holder.textView = (TextView) convertView.findViewById(R.id.item_tbao_live_comment);
            convertView.setTag(holder);
        } else {
            holder = (CommentHolder) convertView.getTag();
        }

        GradientDrawable background = (GradientDrawable) convertView.getBackground();
        if (mCommentList.get(position).getNick().equals(User.getNick())) {
            background.setColor(0xCCFF0055);

            holder.textView.setTextColor(0xffffffff);
            String comment = mCommentList.get(position).getNick() + " " + mCommentList.get(position).getComment();
            if (tvLogo != null && comment.contains(tvLogo)) {
                SpannableString sss = Tools.setTextImg(mContext, comment, comment.indexOf(tvLogo), comment.length(), mContext.getResources().getDrawable(R.drawable.live_comment_tv_icon));
                holder.textView.setText(sss);
            } else {
                holder.textView.setText(comment);
            }

        } else {
            background.setColor(0xCCFFFFFF);
            holder.textView.setTextColor(0xff000000);

            String text = mCommentList.get(position).getNick() + " " + mCommentList.get(position).getComment();
            SpannableString ss = null;
            if (tvLogo != null && text.contains(tvLogo)) {
                ss = Tools.setTextImg(mContext, text, text.indexOf(tvLogo), text.length(), mContext.getResources().getDrawable(R.drawable.live_comment_tv_icon));
            } else {
                ss = new SpannableString(text);
            }
            ForegroundColorSpan numspan = new ForegroundColorSpan(mCommentList.get(position).getColor());
            ss.setSpan(numspan, 0, text.indexOf(" "), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            holder.textView.setText(ss);
        }
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        convertView.setLayoutParams(lp);
        return convertView;
    }

    public class CommentHolder {
        private TextView textView;
    }

    public void clearData() {
        mCommentList.clear();
    }
}
