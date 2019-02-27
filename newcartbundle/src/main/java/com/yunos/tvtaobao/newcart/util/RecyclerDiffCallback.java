package com.yunos.tvtaobao.newcart.util;

import android.support.v7.util.DiffUtil;

import com.yunos.tvtaobao.biz.request.bo.FindSameBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhoubo on 2018/7/16.
 * zhoubo on 2018/7/16 09:46
 * describition 去除多余的数据渲染
 */

public class RecyclerDiffCallback extends DiffUtil.Callback {


    private List<FindSameBean> mNewList;

    private List<FindSameBean> mOldList;

    public RecyclerDiffCallback(List<FindSameBean> newList, List<FindSameBean> oldList) {
        this.mNewList = newList == null ? new ArrayList<FindSameBean>() : newList;
        this.mOldList = oldList == null ? new ArrayList<FindSameBean>() : oldList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).getClass().equals(mNewList.get(newItemPosition).getClass());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).equals(mNewList.get(newItemPosition));
    }
}
