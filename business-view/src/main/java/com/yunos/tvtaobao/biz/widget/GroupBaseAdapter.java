package com.yunos.tvtaobao.biz.widget;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 分组的列表适配器
 * @author tingmeng.ytm
 *
 */
public abstract class GroupBaseAdapter extends BaseAdapter{
    private final String TAG = "GroupBaseAdapter";
    private List<Long> mGroupPositionList;
    private int mTotalItemCount;
    public GroupBaseAdapter(){
    }
    
    /**
     * 取得组的总数
     * @return
     */
    public abstract int getGroupCount();
    
    /**
     * 取得item的总数（不包含组标识的view）
     * @param groupId
     * @return
     */
    public abstract int getItemCount(int groupId);
    
    /**
     * 取得组标识的View
     * @param position 列表中总序号
     * @param groupId 组号
     * @param view
     * @return
     */
    public abstract View getGroupHintView(int position, int groupId, View view);
    
    /**
     * 取得组内的itemView
     * @param position 列表中总序号
     * @param groupId 组号
     * @param itemId 组内序号
     * @param convertView
     * @return
     */
    public abstract View getGroupItemView(int position, int groupId, int itemId, View view);

    /**
     * 取得组标识整体View区域大小信息
     * 注：是整个View的区域非Focus框显示的区域，两者有可以不同
     * @return
     */
    public abstract Rect getGroupHintRect();
    
    /**
     * 取得组内itemView的区域大小信息
     * 注：是整个View的区域非Focus框显示的区域，两者有可以不同
     * @return
     */
    public abstract Rect getGroupItemRect();
        
    /**
     * 取得所有组的item的总和
     * @return
     */
    public int getTotalItemCount(){
        return mTotalItemCount;
    }
    
    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    
    @Override
    public final int getCount(){
        if (mGroupPositionList != null) {
            return mGroupPositionList.size();
        }
        return 0;
    }
    @Override
    public int getItemViewType(int position) {
        // 不同的类型值为了判断是组标识的组的item
        if (isGroupHint(position)) {
            return 0;
        }
        return 1;
    }
    
    @Override
    public int getViewTypeCount() {
        // 区域组内item跟组标识View的不同类型
        return 2;
    }
    
    @Override
    public final View getView(int position, View convertView, ViewGroup parent){
        boolean hint = false;
        int itemId = -1;
        int groupId = -1;
        if (mGroupPositionList != null) {
            Long groupPosition = mGroupPositionList.get(position);
            if (groupPosition != null) {
                itemId = getItemFromGroupPos(groupPosition);
                groupId = getGroupFromGroupPos(groupPosition);
                if (itemId == Integer.MAX_VALUE) {
                    hint = true;
                }
            }
        }
        if (hint) {
            convertView = getGroupHintView(position, groupId, convertView);
        } else {
            convertView = getGroupItemView(position, groupId, itemId, convertView);
        }
        return convertView;
    }
    
    /**
     * 当前序号为组标识
     * @param position
     * @return
     */
    public boolean isGroupHint(int position){
        if (mGroupPositionList != null) {
            Long groupPosition = mGroupPositionList.get(position);
            if (groupPosition != null) {
                int item = getItemFromGroupPos(groupPosition);
                Log.i(TAG, "isGroupHint position="+position+" groupPosition="+groupPosition+" item="+item);
                if (item == Integer.MAX_VALUE) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * 取得组的序号
     * @param position
     * @return
     */
    public int getGroupPos(int position){
        if (mGroupPositionList != null && position >= 0 && position < mGroupPositionList.size()) {
            Long groupPos = mGroupPositionList.get(position);
            if (groupPos != null) {
                return getGroupFromGroupPos(groupPos);
            }
        }
        return -1;
    }
    
    /**
     * 取得组内的序号
     * @param position
     * @return
     */
    public int getGroupItemPos(int position){
        if (mGroupPositionList != null && position >= 0 && position < mGroupPositionList.size()) {
            Long groupPos = mGroupPositionList.get(position);
            if (groupPos != null) {
                return getItemFromGroupPos(groupPos);
            }
        }
        return -1;
    }
    
    
    /**
     * 创建二维数据模型
     * @return
     */
    public void buildGroup(){
        List<Long> positionList = new ArrayList<Long>();
        int groupCount = getGroupCount();
        // 组数至少到两组以上才能构成分组列表
        if (groupCount <= 1) {
            groupCount = 1;
        }
        mTotalItemCount = 0;
        for (int group = 0; group < groupCount; group++) {
            int itemCount = getItemCount(group);
            if (itemCount > 0) {
                mTotalItemCount += itemCount;
                // 加入组标识的序号
                long groupHintPos = buildGroupPosition(group, Integer.MAX_VALUE);     
                positionList.add(groupHintPos);                
                for (int item = 0; item < itemCount; item++) {
                    long groupPos = buildGroupPosition(group, item);
                    positionList.add(groupPos);
                }
            }
        }
        mGroupPositionList = positionList;
    }
    
    /**
     * 生成带有组位置跟组内序号的long值
     * @param group
     * @param item
     * @return
     */
    private long buildGroupPosition(int group, int item){
        long groupPos = group;
        return (groupPos << 32) | item;
    }
    
    /**
     * 通过组合值来取得组的位置
     * @param groupPos
     * @return
     */
    private int getGroupFromGroupPos(long groupPos){
        return (int)(groupPos >> 32);
    }
    
    /**
     * 通过组合值来取得组内的位置
     * @param groupPos
     * @return
     */
    private int getItemFromGroupPos(long groupPos){
        return (int)(groupPos & (long)Integer.MAX_VALUE);
    }
}
