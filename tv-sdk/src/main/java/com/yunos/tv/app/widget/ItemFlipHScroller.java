package com.yunos.tv.app.widget;

import android.util.Log;
import android.util.SparseArray;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.yunos.tv.app.widget.Interpolator.AccelerateDecelerateFrameInterpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * 嵌入到GridView里面，实现水平方向上的从左到右的二维错位滚动算法
 * 
 * 注：
 * 1.先进入正常按帧数错开移动
 * 2.如果在上个动画还未停止再请求滚动就会进入快速滚动模式
 * 
 * 需要保证的方法调用时序：setFastScrollOffset()->addGridView()->resetItemData()
 * 
 * 主要方法功能介绍：
 * resetItemData()计算出每次start请求的时候真正需要叠加滚动的距离并设置每个item的参数
 * addGridView()根据外面GridView的变化动态添加跟删除自己算法所管理的item的序号。
 * 
 * 注意：
 * 1.每次都会多添加一列的item，为了提早计算出动画每帧的位移
 * 2.要注意方法相互调用的顺序
 * 
 * 注2：
 * 1.后续考虑动画进入快递模式的启动过程中考虑的偏移值去掉因为现在启动已经修改成同步的，但是不一定还没有验证过
 * 2.FlipItem的元素mTotalMoveDistance跟mLastDistance现在的设计是一样的功能，后续考虑去掉
 * @author tim
 *
 */
public class ItemFlipHScroller {
	/**
	 * item加入到列表中的动作类型
	 * @author tim
	 *
	 */
	private enum FlipListChangeType{
		INIT,
		ADD_BEFORE,
		ADD_AFTER,
		TEMP,
		REFRESH,
		UNKNOWN;
	};
	
	
	/**
	 * item的类型，是headerView，adapterView, footerView
	 * @author tim
	 *
	 */
	private enum FlipItemPositionType{
		HEATER,
		ADAPTER,
		FOOTER
	}
	
	/**
	 * 快速滚动的状态
	 * @author tim
	 *
	 */
	private enum FlipItemFastStatus{
		UNSTART,
		START_UNSTOP,
		STOP
	}
	private final String TAG = "ItemFlipHScroller";
	private boolean DEBUG = false;
	
	private int hor_delay_distance = 25;//50;//横向快速模式相差的距离
	private int ver_delay_distance = 50;//25;//竖向快速模式相差的距离
	private int min_fast_setp_discance = 16;//快速模式最小的单步距离
	private int flip_scroll_frame_count = 20;//每次请求的动画帧数
	private int hor_delay_frame_count = 1;//2;//行跟行之间的帧数间隔
	private int ver_delay_frame_count = 2;//1;//列跟列之间的帧数间隔
	
	private int mFinalFrameCount;//目标帧数（是指每个child的目标帧数，非总帧数）
	private int mCurrFrameIndex;//当前的总帧数，因为列跟行之间有间隔所以该会大于mFinalFrameCount
	private boolean mFinished = true;//是否已经结束
	
	/* mFlipItemRowList结构
	 * |Row1| |Row2|
	 * |itm1| |itm1|
	 * |itm2| |itm2|
	 * |itm3| |itm3|
	 * |itm4| |itm4|
	 * |itm5| |itm5|
	 * |itm6| |itm6|
	 * |itm7| |itm7|
	 * |itm8| |itm8|
	 * |itm9| |itm9|
	 */
	private List<List<FlipItem>> mFlipItemRowList;//按每列排序的child列表
	
	private SparseArray<FlipItem> mFlipItemMap;//child map 按显示列表的位置索引做为key进行查找
	private int mStartListIndex = -1;//进行动画child的开始序号（指显示在列表的开始位置）
	private int mEndListIndex = -1;//进行动画child的结束序号（指显示在列表的结束位置）
	private int mRowCount;//一列的child个数
	private boolean mIsRight;//是否是向右滚动
	private ItemFlipScrollerListener mItemFlipScrollerListener;//监听器
	private int mPreSelectedPosition = -1;
	private int mCurrSelectedPosition = -1;
	private boolean mFastFlip;//是否正在快速滚动
	private boolean mStartingFlipScroll;//正在启动滚动动画过程中
	private List<Integer> mHeaderViewList;//保存headerView行跟列数据信息列表
	private List<Integer> mFooterViewList;//保存footerView行跟列数据信息列表
	private int mTotalItemCount;//总共item的个数
	private FastStep mFastStep;//进入快速模式单步距离
	private boolean mItemDelayAnim = true;//是否需要item错开二维进行滚动
	private boolean mIsFastStepComeDown = true;//是否需要进行快速移动的时候做减速
	private int mPreAddTempItemIndex = -1;//上次添加的临时item的序号
	private AccelerateDecelerateFrameInterpolator mAccelerateDecelerateFrameInterpolator;
	
	/**
	 * 构造方法
	 */
	public ItemFlipHScroller(){
		mFlipItemMap = new SparseArray<FlipItem>();
		mFlipItemRowList = new ArrayList<List<FlipItem>>();
		mAccelerateDecelerateFrameInterpolator = new AccelerateDecelerateFrameInterpolator();
		mFastStep = new FastStep();
	}
	
	
	/**
	 * 设置一列的个数
	 * @param numLines
	 */
	public void setRowCount(int numLines){
		mRowCount = numLines;
	}
	
	
	/**
	 * 是否需要每个item延时滚动（是一列一列还是错开滚动）
	 * @param delay
	 */
	public void setDelayAnim(boolean delay){
		if(mFinished){
			mItemDelayAnim = delay;
		}
		else{
			Log.e(TAG, "setDelayAnim must in Finished use");
		}
	}
	
	
	/**
	 * 设置是否在启动动画的过程中（因为启动动画的是异步的（现修改为同步的可以考虑去除））
	 */
	public void setStartingFlipScroll(){
		mStartingFlipScroll = true;
	}
	
	
	/**
	 * 设置监听器
	 * @param listener
	 */
	public void setItemFlipScrollerListener(ItemFlipScrollerListener listener){
		mItemFlipScrollerListener = listener;
	}
	
	
	/**
	 * 设置选中的child的位置
	 * @param selectedPosition
	 */
	public void setSelectedPosition(int selectedPosition){
		if(DEBUG){
			Log.i(TAG, "setSelectedPosition selectedPosition="+selectedPosition+" mPreSelectedPosition="+mPreSelectedPosition);
		}
		mPreSelectedPosition = mCurrSelectedPosition;
		mCurrSelectedPosition = selectedPosition;
	}
	
	
	/**
	 * 添加headerView
	 * @param viewInfo 合成后的信息
	 */
	public void setHeaderViewInfo(List<Integer> headerInfo){
		mHeaderViewList = headerInfo;
	}
	
	
	/**
	 * 添加footerView
	 * @param viewInfo
	 */
	public void setFooterViewInfo(List<Integer> footerInfo){
		mFooterViewList = footerInfo;
	}
	
	
	/**
	 * 设置总的所有item的个数
	 * @param totalCount
	 */
	public void setTotalItemCount(int totalCount){
		if(DEBUG){
			Log.i(TAG, "setTotalItemCount totalCount="+totalCount);
		}
		mTotalItemCount = totalCount;
	}
	
	
	/**
	 * 开始做减速运动
	 * （通过对屏幕内已有的item跟选中的item之前的距离来做为减速的运动时间轴，
	 *  速度从当前减速到最小速度）
	 */
	public void startComeDown(){
		if(mFastFlip && mIsFastStepComeDown){
			if(mIsRight){
				FlipItem item = getFlipItemRow(0, 0);
				if(item != null){
					int distance = 0;
					if(mItemDelayAnim){
						//主要是计算行跟行之前的延迟距离差*2，包含启动时的距离差跟结束的距离差
						//再加上行的头部和行的尾部 * 2的距离
						int firstColumnStart = getColumnStart(item.mIndex);
						int selectedColumnStart = getColumnStart(mCurrSelectedPosition);
						int otherRow = 0;
						int headerCount = mHeaderViewList.size();
						if(headerCount > 0 && firstColumnStart < headerCount){
							int totalCount = Math.min(headerCount - 1, selectedColumnStart);
							for(int i = firstColumnStart; i <= totalCount; i++){
								int header = mHeaderViewList.get(i);
								int headerRowCount = header >> 16;
								otherRow = headerRowCount - 1;
							}
						}
						
						int footerCount = mFooterViewList.size();
						if(footerCount > 0 && selectedColumnStart >= (mTotalItemCount - footerCount)){
							int totalCount = Math.max(mTotalItemCount - footerCount, firstColumnStart);
							for(int i = selectedColumnStart; i >= totalCount; i--){
								int footer = mFooterViewList.get(i);
								int footerRowCount = footer >> 16;
								otherRow = footerRowCount - 1;
							}
						}
						int deltaRow = (selectedColumnStart - firstColumnStart) / mRowCount + otherRow;
						distance = ((Math.abs(deltaRow) + 1) * ver_delay_distance + (mRowCount - 1) *  hor_delay_distance) * 2;
					}
					mFastStep.resetComeDown(item.mFinalDistance - item.mLastDistance - distance);
				}
			}
			else{
				FlipItem item = getLastRowFirsterItem();
				if(item != null){
					int distance = 0;
					if(mItemDelayAnim){
						//主要是计算列跟列之前的延迟距离差*2，包含启动时的距离差跟结束的距离差
						int lastColumnStart = getColumnStart(item.mIndex);
						int selectedColumnStart = getColumnStart(mCurrSelectedPosition);
						int otherRow = 0;
						int headerCount = mHeaderViewList.size();
						if(headerCount > 0 && selectedColumnStart < headerCount){
							int totalCount = Math.min(headerCount - 1, selectedColumnStart);
							for(int i = selectedColumnStart; i <= totalCount; i++){
								int header = mHeaderViewList.get(i);
								int headerRowCount = header >> 16;
								otherRow = headerRowCount - 1;
							}
						}
						
						int footerCount = mFooterViewList.size();
						if(footerCount > 0 && lastColumnStart >= (mTotalItemCount - footerCount)){
							int totalCount = Math.max(mTotalItemCount - footerCount, selectedColumnStart);
							for(int i = lastColumnStart; i >= totalCount; i--){
								int footer = mFooterViewList.get(i);
								int footerRowCount = footer >> 16;
								otherRow = footerRowCount - 1;
							}
						}
						int deltaRow = (lastColumnStart - selectedColumnStart) / mRowCount + otherRow;
						distance = ((deltaRow + 1)* ver_delay_distance) * 2;
					}
					mFastStep.resetComeDown(item.mFinalDistance - item.mLastDistance + distance);
				}
			}
		}
	}
	
	/**
	 * 设置快速滚动模式新布局跟动画要求位置的偏移值
	 * @param index
	 * @param secondIndex
	 * @param offset
	 */
	public void setFastScrollOffset(int index, int secondIndex, int offset){
		if(DEBUG){
			Log.i(TAG, "setFastScrollOffset index="+index+" secondIndex="+secondIndex+
					" offset="+offset+" mStartingFlipScroll="+mStartingFlipScroll);
		}
		//只有在启动过程中才需要取得进入快速模式的偏移值，
		//因为按照流程进入快速模式前是先addView里面使用偏移值才会启动完成进入快速模式。
		if(mStartingFlipScroll){
			FlipItem item = mFlipItemMap.get(getFlipItemMapKey(index, secondIndex));
			if(item != null){
				item.mFastScrollOffset = offset;
			}
		}
	}
	
	
	/**
	 * 取得当前item剩余的移动距离
	 * @param index
	 * @param secondIndex
	 * @return
	 */
	public int getFlipItemLeftMoveDistance(int index, int secondIndex){
		FlipItem item = getFlipItem(index, secondIndex);
		if(item != null){
			if(DEBUG){
				Log.i(TAG, "getFlipItemLeftMoveDistance mFinalDistance="+item.mFinalDistance
						+" mLastDistance="+item.mLastDistance);
			}
			return item.mFinalDistance - item.mLastDistance;
		}
		return 0;
	}
	
	
	/**
	 * 本列的首个item的剩余滚动距离
	 * @param index
	 * @return
	 */
	public int getFlipRowFirstItemLeftMoveDistance(int index){
		//如果大于最大值就使用最大值
		if(index > mEndListIndex){
			index = mEndListIndex;
		}
		else if(index < mStartListIndex){
			index = mStartListIndex;
		}
		
		int secondIndex = 0;
		if(mHeaderViewList.size() > 0 && index < mHeaderViewList.size()){
			secondIndex = -1;
		}
		else if(mFooterViewList.size() > 0 && index >= mTotalItemCount - mFooterViewList.size()){
			secondIndex = -1;
		}
		FlipItem item = getFlipItem(index, secondIndex);
		if(item != null){
			if(DEBUG){
				Log.i(TAG, "getFlipColumnFirstItemLeftMoveDistance mFinalDistance="+item.mFinalDistance
						+" mLastDistance="+item.mLastDistance);
			}
			FlipItem firstItem = getFlipItemRow(item.mRowIndex, 0);
			if(firstItem != null){
				return item.mFinalDistance - item.mLastDistance;
			}
		}
		return 0;
	}
	
	
	/**
	 * 取得item的运动的进展比率
	 * @param index
	 * @param secondIndex
	 * @return
	 */
	public float getFlipItemMoveRatio(int index, int secondIndex){
		FlipItem item = getFlipItem(index, secondIndex);
		if(item != null){
			return (float)(item.mLastDistance) / (float)(item.mFinalDistance);
		}
		return 1.0f;
	}
	
	
	/**
	 * 取得快速滚动的时候单步的步长
	 * @return
	 */
	public int getFastFlipStep(){
		return mFastStep.getCurrStep();
	}
	
	
	/**
	 * 是否完成
	 * @return
	 */
	public boolean isFinished(){
		return mFinished;
	}
	
	
	/**
	 * 清空动画的child列表
	 */
	public void clearChild(){
		mFlipItemRowList.clear();
		mFlipItemMap.clear();
		mStartListIndex = -1;
		mEndListIndex = -1;
	}
	
	
	/**
	 * 是否正在向右
	 * @return
	 */
	public boolean isRight(){
		return mIsRight;
	}
	
	/**
	 * 开始滚动(启动开始之前必须先添加child item在mFlipItemList)
	 * @param distance
	 * @param frameCount
	 * @param computerRealDistance 是否需要计算真正的距离
	 */
	public void startScroll(int distance, int frameCount, boolean computerRealDistance){
		if(mRowCount <= 0){
			Log.e(TAG, "error must set mRowCount before start scroll");
		}
		
		mStartingFlipScroll = false;
		mCurrFrameIndex = 0;
		mFinalFrameCount = frameCount;
		if(distance < 0){
			mIsRight = true;
		}
		else{
			mIsRight = false;
		}
		
		//如果上次还没有结束就进入快速滚动的模式
		if(mFinished == false){
			mFastFlip = true;
		}
		
		int realDistance = distance;
		if(computerRealDistance){
			realDistance = getRealFinalDistance(distance);
		}
		resetItemData(realDistance);
		mFinished = false;
		Log.i(TAG, "startScroll distance="+distance+" frameCount="+frameCount+" mFastFlip="+mFastFlip);
	}
	
	
	/**
	 * 开始动画（参数是需要计算偏移值）
	 * @param distance
	 * @param frameCount
	 */
	public void startComputDistanceScroll(int distance, int frameCount){
		startScroll(distance, frameCount, true);
	}
	
	
	/**
	 * 开始动画（参数是需要计算偏移值）
	 * @param distance
	 * @param frameCount
	 */
	public void startComputDistanceScroll(int distance){
		startScroll(distance, flip_scroll_frame_count, true);
	}
	
	
	/**
	 * 开始动画（参数为直接滚动的距离）
	 * @param distance
	 * @param frameCount
	 */
	public void startRealScroll(int distance, int frameCount){
		startScroll(distance, frameCount, false);
	}
	
	
	/**
	 * 开始动画（参数为直接滚动的距离）
	 * @param distance
	 * @param frameCount
	 */
	public void startRealScroll(int distance){
		startScroll(distance, flip_scroll_frame_count, false);
	}
	
	/**
	 * 结束
	 */
	public void finish(){
		mPreSelectedPosition = -1;
		mStartingFlipScroll = false;
		mFinalFrameCount = 0;
		if (mFastFlip) {
			mFastStep.finished();
		}
		mFastFlip = false;
		clearChild();
		mFinished = true;
		if(mItemFlipScrollerListener != null){
			mItemFlipScrollerListener.onFinished();
		}
		Log.i(TAG, "finish");
	}
	
	
	/**
	 * 计算滚动的值
	 * @return
	 */
	public boolean computeScrollOffset(){
		if(mFinished){
			return false;
		}
		boolean finished;
		if(mFastFlip){
			//计算减速
			mFastStep.computerOffset();
		}
		if(mIsRight){
			finished = computerFlipScrollRight();
		}
		else{
			finished = computerFlipScrollLeft();
		}
		
		//判断是否需要结束
		boolean ret = true;
		if(finished == true){
			finish();
			ret = false;
		}
		return ret;
	}
	
	
	/**
	 * 取得指定child的偏移值
	 * @param index
	 * @param secondIndex
	 * @return
	 */
	public int getCurrDelta(int index, int secondIndex){
		if(mFinished){
			return 0;
		}
		FlipItem item = mFlipItemMap.get(getFlipItemMapKey(index, secondIndex));
		if(item != null){
			return item.mCurrDelta;
		}
		return 0;
	}

	
	/**
	 * 加入gridView的item
	 * @param start 当前item的开始序号
	 * @param end 当前item的开始序号
	 * @param columnCount 列表的单排的个数
	 * @param verticalSpacing 排之前的间隙
	 * （因为是垂直错位滚动，再加入item的个数在滚动的过程中是动态变化的，所以每个item的目标值是不同的）
	 */
	public void addGridView(int start, int end, boolean isDown){
		//需要保证start是某行的起始点
		if(start > end){
			return;
		}
		//添加全新的列表
		if(mStartListIndex < 0 || mEndListIndex < 0){
			//add all
			makeInitFlipItemList(start, end, isDown);
			mStartListIndex = start;
			mEndListIndex = end;
		}
		else{
			
			//向上，start减少
			if(start < mStartListIndex){
				addBefore(start);
			}
			else if(start > mStartListIndex){
				//只有切换到快速滚动的时候才进行删除
				if(mFastFlip){
					int diff = start - mStartListIndex;
					boolean lockClear = false;
					//当上次里面还有包含有headerView的时候，除headerView以外按一行进行删除
					if((mStartListIndex < mHeaderViewList.size() || diff % mRowCount == 0) &&
							lockClear == false){
						if(DEBUG){
							Log.i(TAG, "clear start="+start+" mEndListIndex="+mEndListIndex);
						}
						mFlipItemRowList.clear();
						makeInitFlipItemList(start, mEndListIndex, mIsRight);
						mStartListIndex = start;
					}
				}
			}
			//向下, end 增加
			if(end > mEndListIndex){
				addAfter(end);
			}
			else if(end < mEndListIndex){
				//只有切换到快速滚动的时候才进行删除
				if(mFastFlip){
					int headerSize = mHeaderViewList.size();
					//当上次里面还有包含有footerView的时候，除footerView以外按一行进行删除
					int adapter = headerSize;
					if(end >= headerSize){
						boolean lockClear = false;
						//(end - headerIndex) % mColumnCount) == 0为了只要end为一行的最后一个
						if((mEndListIndex >= (mTotalItemCount - mFooterViewList.size())
								|| ((end - adapter + 1) % mRowCount) == 0) &&
								lockClear == false){
							if(DEBUG){
								Log.i(TAG, "clear mStartListIndex="+mStartListIndex+
										" end="+end+" mEndListIndex="+mEndListIndex+" mTotalItemCount="+mTotalItemCount);
							}
							mFlipItemRowList.clear();
							makeInitFlipItemList(mStartListIndex, end, mIsRight);
							mEndListIndex = end;
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * 检查是否需要增加item
	 * @param start
	 * @param end
	 */
	public void checkAddView(int start, int end){
		if(DEBUG){
			Log.i(TAG, "checkAddView start="+start+" end="+end+" mFinished="+mFinished+" mIsDown="+mIsRight);
		}
		if(mFinished == false){
			//向上，start减少
			if(start < mStartListIndex && mIsRight == false){
				addBefore(start);
			}
			
			//向下, end 增加
			if(end > mEndListIndex && mIsRight == true){
				addAfter(end);
			}
		}
	}
	
	/**
	 * 取得当前的距离值
	 * @param finalDistance
	 * @param input
	 * @return
	 */
	private int getCurrDistance(int distance, float input, boolean needInterpolation){
		float output;
		if(needInterpolation){
			output = getInterpolation(input);
		}
		else{
			output = input;
		}
		return (int)(distance * output);
	}
	
	
	/**
	 * 取得变化后的比率值
	 * @param input
	 * @return
	 */
    private float getInterpolation(float input) {
    	return mAccelerateDecelerateFrameInterpolator.getInterpolation(input);
//        return (float)(Math.cos((input + 1) * Math.PI) / 2.0f) + 0.5f;
    }
    
    
    /**
     * 计算向下滚动时的item位置
     * @return true 为停止 or false
     */
    private boolean computerFlipScrollLeft(){
		mCurrFrameIndex ++;
		if(mFastFlip){
			return computerLeftFast();
		}
		else{
			return computerLeftNormal();
		}
    }
    
    
    private boolean computerLeftNormal(){
       	int horCount = 0;
		int verCount = 0;
		boolean finished = true;
		int listCount = mFlipItemRowList.size();
		for(int i = listCount - 1; i >= 0; i--){
			List<FlipItem> itemList = mFlipItemRowList.get(i);
			if(itemList == null){
				return finished;
			}
			horCount = 0;
			int itemCount = itemList.size();
			for(int j = itemCount - 1; j >= 0; j--){
				FlipItem item = itemList.get(j);
				//当前要延迟的帧数
				int delayFrameCount = verCount * ver_delay_frame_count + horCount * hor_delay_frame_count;
				int itemCurrFrameCount = mCurrFrameIndex;
				if(mItemDelayAnim){
					itemCurrFrameCount -= delayFrameCount;
				}
				//此item说明还无需启动
				if(itemCurrFrameCount <= 0){
					continue;
				}
				else if(itemCurrFrameCount >= item.mFinalFrameCount){
					//当在最后一帧的时候直接就等于最终的值，这样可以消除后的小数点误差
					if(item.mLastDistance > 0 && item.mLastDistance < item.mFinalDistance){
						//只要还有未到达终点的item就说明这个动画还未完全结束
						finished = false;
					}
					else if(item.mLastDistance < 0 && item.mLastDistance > item.mFinalDistance){
						finished = false;
					}
					item.mCurrDelta = item.mFinalDistance - item.mLastDistance;
					item.mLastDistance += item.mCurrDelta;
					item.mCurrFrameCount = item.mFinalFrameCount;
					item.mTotalMoveDistance += item.mCurrDelta;
				}
				else{
					//计算出当前item，需要移动的距离
					float ratio = (float)itemCurrFrameCount / (float)item.mFinalFrameCount;
					int currDistance = getCurrDistance(item.mFinalDistance, ratio, true);    					
					item.mCurrDelta = currDistance - item.mLastDistance;
					if(item.mCurrDelta < 0){
						item.mCurrDelta = 0;
					}
					item.mLastDistance += item.mCurrDelta;
					item.mCurrFrameCount = itemCurrFrameCount;
					item.mTotalMoveDistance += item.mCurrDelta;
					finished = false;
				}

				//计算出在当前同一行中当前item位于第几个
				horCount ++;
				//计算快速滚动的偏移值，因为启动滚动动画是异步的
				if(mStartingFlipScroll){
					item.mFastScrollStartingOffset += item.mCurrDelta;
				}
				
			}
			verCount ++;
		}
		return finished;
    }
    
    private boolean computerLeftFast(){
    	boolean finished = true;
		boolean needDelay;
    	if(mItemDelayAnim){
    		needDelay = true;
    	}
    	else{
    		needDelay = false;
    	}
		int preColumnDistance = 0;
		int preColumnDelta = 0;
		int preVerticalDistance = 0;
		int preVerticalDelta = 0;
		FlipItemFastStatus preColumnItemStatus = FlipItemFastStatus.UNSTART;
		FlipItemFastStatus preVertiaclItemStatus = FlipItemFastStatus.UNSTART;
		int listSize = mFlipItemRowList.size();
		for(int rowIndex = listSize - 1; rowIndex >= 0; rowIndex--){
			List<FlipItem> itemList = mFlipItemRowList.get(rowIndex);
			if(itemList == null || itemList.size() <= 0){
				return finished;
			}
			//每列的最后一行
			FlipItem rowLastItem = itemList.get(itemList.size() - 1);
			rowLastItem.mCurrDelta = 0;
			if(rowIndex == listSize - 1){
				//先移动最后一列最后一行
				if(rowLastItem.mLastDistance == 0){
					//刚开始
					finished = computerLeftFastEachItem(rowLastItem, 0, true);
				}
				else if(rowLastItem.mLastDistance >= rowLastItem.mFinalDistance){
					//已经结束;
					rowLastItem.mCurrDelta = 0;
				}
				else{
					finished = computerLeftFastEachItem(rowLastItem, 0, true);
				}
				//太小了不作延迟启动
				if(rowLastItem.mFinalDistance <= (hor_delay_distance * 2) ||
						rowLastItem.mFinalDistance <= (ver_delay_distance * 2)){
					needDelay = false;
				}
//				Log.i("test", "into "+" index="+columnLastItem.mIndex+
//						" mTotalMoveDistance="+columnLastItem.mTotalMoveDistance+" preVerticalDistance="+preVerticalDistance+
//						" mCurrFrameCount="+ columnLastItem.mCurrFrameCount+" mFinalFrameCount="+columnLastItem.mFinalFrameCount+
//						" mCurrDelta=" + columnLastItem.mCurrDelta+
//						" mCurrTotalMoveDistance="+columnLastItem.mCurrTotalMoveDistance+
//						" columnStatus="+preColumnItemStatus+" vertical="+preVertiaclItemStatus);
			}
			else{
				//如果参照物未开始，其也应该是未开始状态
				if(preColumnItemStatus.compareTo(FlipItemFastStatus.STOP) == 0){
					boolean itemFinished = computerLeftFastEachItem(rowLastItem, 0, true);
					if(itemFinished == false){
						finished = false;
					}
				}
				else if(preColumnItemStatus.compareTo(FlipItemFastStatus.START_UNSTOP) == 0){
					//每行的第一列之间进行比较
					int diff = preColumnDistance - rowLastItem.mTotalMoveDistance;
					if(needDelay == true && diff <= ver_delay_distance){
						//将后续移动位移清0
						for(int clearColumnIndex = rowIndex; clearColumnIndex >= 0; clearColumnIndex--){
							List<FlipItem> clearItemList = mFlipItemRowList.get(clearColumnIndex);
							for(int clearVerticalIndex = clearItemList.size() - 1; clearVerticalIndex >= 0; clearVerticalIndex--){
								FlipItem clearItem = clearItemList.get(clearVerticalIndex);
								clearItem.mCurrDelta = 0;
							}
						}
						break;
					}
					else{
						int moveDistance = diff - ver_delay_distance;
						if(needDelay == false){
							moveDistance = preColumnDelta;
						}
						boolean itemFinished = computerLeftFastEachItem(rowLastItem, moveDistance, false);
						if(itemFinished == false){
							finished = false;
						}
					}
				}
				else{
					//pre item unstart;
					//在进入快速模式的过程中为了防止跳变
					int diff = preColumnDistance - rowLastItem.mTotalMoveDistance;
					if(needDelay == true && diff > hor_delay_distance){
						boolean itemFinished = computerLeftFastEachItem(rowLastItem, 0, true);
						if(itemFinished == false){
							finished = false;
						}
					}
				}
			}
			preColumnDelta = rowLastItem.mCurrDelta;
			preVerticalDelta = rowLastItem.mCurrDelta;
			preColumnDistance = rowLastItem.mTotalMoveDistance;
			preVerticalDistance = rowLastItem.mTotalMoveDistance;
			//计算快速滚动的偏移值，因为启动滚动动画是异步的
			if(mStartingFlipScroll){
				rowLastItem.mFastScrollStartingOffset += rowLastItem.mCurrDelta;
			}
			
			if(rowLastItem.mLastDistance >= rowLastItem.mFinalDistance){
				preColumnItemStatus = FlipItemFastStatus.STOP;
				preVertiaclItemStatus = FlipItemFastStatus.STOP;
			}
			else if(rowLastItem.mCurrTotalMoveDistance != 0){
				preColumnItemStatus = FlipItemFastStatus.START_UNSTOP;
				preVertiaclItemStatus = FlipItemFastStatus.START_UNSTOP;
			}
			else{
				preColumnItemStatus = FlipItemFastStatus.UNSTART;
				preVertiaclItemStatus = FlipItemFastStatus.UNSTART;
			}
			
//			if(DEBUG){
//				if(columnLastItem.mIndex == 6 || columnLastItem.mIndex == 13){
//					Log.i("test", "into "+" index="+columnLastItem.mIndex+
//							" mTotalMoveDistance="+columnLastItem.mTotalMoveDistance+" preVerticalDistance="+preVerticalDistance+
//							" mCurrFrameCount="+ columnLastItem.mCurrFrameCount+" mFinalFrameCount="+columnLastItem.mFinalFrameCount+" mCurrDelta=" + columnLastItem.mCurrDelta+
//							" mTotalMoveDistance="+columnLastItem.mTotalMoveDistance+
//							" columnStatus="+preColumnItemStatus+" vertical="+preVertiaclItemStatus);
//				}
//			}
			//从倒数第2个开始, 倒数第1个已经在上面处理过
			for(int verticalIndex = itemList.size() - 2; verticalIndex >= 0; verticalIndex--){
				FlipItem item = itemList.get(verticalIndex);
//				if(DEBUG){
//					if(item.mIndex == 0 || item.mIndex == 3){
//						Log.i("test", "into "+" index="+item.mIndex+
//								" mTotalMoveDistance="+item.mTotalMoveDistance+" preVerticalDistance="+preVerticalDistance+
//								" mCurrFrameCount="+ item.mCurrFrameCount+" mFinalFrameCount="+item.mFinalFrameCount+" mCurrDelta=" + item.mCurrDelta+
//								" mTotalMoveDistance="+item.mTotalMoveDistance+
//								" columnStatus="+preColumnItemStatus+" vertical="+preVertiaclItemStatus);
//					}
//				}
				item.mCurrDelta = 0;
				//比较列跟列
				//如果参照物未开始，其也应该是未开始状态
				if(preVertiaclItemStatus.compareTo(FlipItemFastStatus.STOP) == 0){
					boolean itemFinished = computerLeftFastEachItem(item, 0, true);
					if(itemFinished == false){
						finished = false;
					}
				}
				else if(preVertiaclItemStatus.compareTo(FlipItemFastStatus.START_UNSTOP) == 0){
					int diff = preVerticalDistance - item.mTotalMoveDistance;
					if(needDelay == true && diff <= hor_delay_distance){
						//将后继的值都清0
						for(int clearIndex = verticalIndex; clearIndex >= 0; clearIndex--){
							FlipItem clearItem = itemList.get(clearIndex);
							clearItem.mCurrDelta = 0;
						}
						break;
					}
					else{
						int moveDistance = diff - hor_delay_distance;
						if(needDelay == false){
							moveDistance = preVerticalDelta;
						}
						boolean itemFinished = computerLeftFastEachItem(item, moveDistance, false);
						if(itemFinished == false){
							finished = false;
						}
					}
				}
				else{
					int diff = preVerticalDistance - item.mTotalMoveDistance;
					if(needDelay == true && diff > hor_delay_distance){
						boolean itemFinished = computerLeftFastEachItem(item, 0, true);
						if(itemFinished == false){
							finished = false;
						}
					}
				}
				preVerticalDelta = item.mCurrDelta;
				preVerticalDistance = item.mTotalMoveDistance;
				//计算快速滚动的偏移值，因为启动滚动动画是异步的
				if(mStartingFlipScroll){
					item.mFastScrollStartingOffset += item.mCurrDelta;
				}
				
				if(item.mLastDistance >= item.mFinalDistance){
					preVertiaclItemStatus = FlipItemFastStatus.STOP;
				}
				else if(item.mCurrTotalMoveDistance != 0){
					preVertiaclItemStatus = FlipItemFastStatus.START_UNSTOP;
				}
				else{
					preVertiaclItemStatus = FlipItemFastStatus.UNSTART;
				}
				
			}
		}
		return finished;
    }
    
    
    /**
     * 计算在快速向上的状态下单个item运动的参数
     * @param item
     * @param moveDistance
     * @param preItemStoped
     * @return
     */
    private boolean computerLeftFastEachItem(FlipItem item, int moveDistance, boolean preItemStoped){
    	boolean finished = true;
//    	int logIndex = 1;
//    	int logIndex1 = 20;
//    	int logIndex2 = 27;
//    	int logIndex3 = 0;
    	if(preItemStoped == false){
			//开始启动
			item.mCurrDelta = moveDistance;
			item.mCurrFrameCount ++;
			if(item.mCurrFrameCount > item.mFinalFrameCount){
				item.mCurrFrameCount = item.mFinalFrameCount;
			}
			int left = item.mFinalDistance - item.mLastDistance;
			if(left < item.mCurrDelta){
				item.mCurrDelta = left;
			}
			item.mCurrTotalMoveDistance += item.mCurrDelta;
			item.mLastDistance += item.mCurrDelta;
			item.mTotalMoveDistance += item.mCurrDelta;
			finished = false;
//			if(DEBUG){
//				if(item.mIndex == logIndex || item.mIndex == logIndex1 ||
//						item.mIndex == logIndex2 || item.mIndex == logIndex3){
//					Log.i(TAG, "dragged index="+item.mIndex+" mLastDistance="+item.mLastDistance+
//							" delta="+ item.mCurrDelta + " mFinalDistance="+item.mFinalDistance+
//							" mTotalMoveDistance="+item.mTotalMoveDistance+" mCurrFrameCount="+item.mCurrFrameCount);
//				}
//			}
		}
		else{
			if(item.mLastDistance < item.mFinalDistance){
				int currDistance = item.mLastDistance + mFastStep.getCurrStep();
				//int currDistance = item.mLastDistance + mFastStepDistance;
				if(currDistance > item.mFinalDistance){
					currDistance = item.mFinalDistance;
				}
				item.mCurrDelta = currDistance - item.mLastDistance;
				item.mCurrTotalMoveDistance += item.mCurrDelta;
				item.mLastDistance += item.mCurrDelta;
				item.mTotalMoveDistance += item.mCurrDelta;
				item.mCurrFrameCount ++;
				if(item.mCurrFrameCount > item.mFinalFrameCount){
					item.mCurrFrameCount = item.mFinalFrameCount;
				}
				finished = false;
//				if(DEBUG){
//					if(item.mIndex == logIndex || item.mIndex == logIndex1 ||
//							item.mIndex == logIndex2 || item.mIndex == logIndex3){
//						Log.i(TAG, "index="+item.mIndex+" mLastDistance="+item.mLastDistance+
//								" delta="+ item.mCurrDelta+" mCurrFrameCount="+item.mCurrFrameCount+
//								" mFinalFrameCount="+item.mFinalFrameCount+" mFinalDistance="+item.mFinalDistance+" mTotalMoveDistance="+item.mTotalMoveDistance);
//					}
//				}
			}
			else{
				item.mCurrDelta = 0;
				item.mCurrFrameCount = item.mFinalFrameCount;
				item.mLastDistance = item.mFinalDistance;
//				if(DEBUG){
//					if(logIndex == item.mIndex || item.mIndex == logIndex1){
//						Log.i(TAG, "index="+item.mIndex+" finished" + " mFinalDistance="+item.mFinalDistance+" mTotalMoveDistance="+item.mTotalMoveDistance);
//					}
//				}
			}
		}
    	return finished;
    }
    
    
    /**
     * 计算向下运动的参数
     * （由正常模式转化到快速模式）
     * @return true 为停止 or false
     */
    private boolean computerFlipScrollRight(){
		mCurrFrameIndex ++;
		if(mFastFlip){
			return computerRightFast();
		}
		else{
			return computerRightNormal();
		}
    }
    
    
    /**
     * 计算正常模式下的运动参数
     * （行跟列相差一定的帧数，做变速运动）
     * @return true为停止 否则为false
     */
    private boolean computerRightNormal(){
    	int verCount = 0;
		int horCount = 0;
		boolean finished = true;
		for(List<FlipItem> itemList : mFlipItemRowList){
			if(itemList == null){
				return finished;
			}
			horCount = 0;
			for(FlipItem item : itemList){
				int delayFrameCount = (verCount * ver_delay_frame_count) + (horCount * hor_delay_frame_count);
				int itemCurrFrameCount = mCurrFrameIndex;
				if(mItemDelayAnim){
					itemCurrFrameCount -= delayFrameCount;
				}
				//此item说明还无需启动
				if(itemCurrFrameCount <= 0){
					continue;
				}
				else if(itemCurrFrameCount >= item.mFinalFrameCount){
					//当在最后一帧的时候直接就等于最终的值，这样可以消除后的小数点误差
					if(item.mLastDistance > 0 && item.mLastDistance < item.mFinalDistance){
						//只要还有未到达终点的item就说明这个动画还未完全结束
						finished = false;
					}
					else if(item.mLastDistance < 0 && item.mLastDistance > item.mFinalDistance){
						finished = false;
					}
					item.mCurrDelta = item.mFinalDistance - item.mLastDistance;
					item.mLastDistance += item.mCurrDelta;
					item.mCurrFrameCount = item.mFinalFrameCount;
					item.mTotalMoveDistance += item.mCurrDelta;
				}
				else{
					//计算出当前item，需要移动的距离
					float ratio = (float)itemCurrFrameCount / (float)item.mFinalFrameCount;
					int currDistance = getCurrDistance(item.mFinalDistance, ratio, true);
					item.mCurrDelta = currDistance - item.mLastDistance;
					if(item.mCurrDelta > 0){
						item.mCurrDelta = 0;
					}
					item.mLastDistance += item.mCurrDelta;
					item.mCurrFrameCount = itemCurrFrameCount;
					item.mTotalMoveDistance += item.mCurrDelta;
					finished = false;
				}
				//计算出在当前同一列中当前item位于第几个
				horCount ++;
				//计算快速滚动的偏移值，因为启动滚动动画是异步的
				if(mStartingFlipScroll){
					item.mFastScrollStartingOffset += item.mCurrDelta;
				}
			}
			verCount ++;
		}
		return finished;
    }
    
    
    /**
     * 计算快速向右的item的运动参数
     * （以第一列的第一个做为参考头带动后续的item，每列的第一个参照上一列第一个，同一列后续item参照前一行，
     *   两个item在运动的过程始终处于一定的距离匀速运动，直到前面参照的item完成运动并停止，才会进入自己已同样的匀速运动）
     * @return true为停止 否则为false
     */
    private boolean computerRightFast(){
    	boolean finished = true;
    	boolean needDelay;
    	if(mItemDelayAnim){
    		needDelay = true;
    	}
    	else{
    		needDelay = false;
    	}
		int preRowDistance = 0;
		int preRowDelta = 0;
		int preHorizontalDistance = 0;
		int preHorizontalDelta = 0;
		FlipItemFastStatus preRowItemStatus = FlipItemFastStatus.UNSTART;
		FlipItemFastStatus preHorizontalItemStatus = FlipItemFastStatus.UNSTART;
		
		for(int rowIndex = 0; rowIndex < mFlipItemRowList.size(); rowIndex++){
			List<FlipItem> itemList = mFlipItemRowList.get(rowIndex);
			//每一列
			if(itemList == null || itemList.size() <= 0){
				return finished;
			}
			FlipItem rowFirstItem = itemList.get(0);
			rowFirstItem.mCurrDelta = 0;
			if(rowIndex == 0){
				//先移动第一列的第一行
				if(rowFirstItem.mLastDistance == 0){
					//刚开始
					finished = computerRightFastEachItem(rowFirstItem, 0, true);
				}
				else if(rowFirstItem.mLastDistance <= rowFirstItem.mFinalDistance){
					//已经结束;
					rowFirstItem.mCurrDelta = 0;
				}
				else{
					finished = computerRightFastEachItem(rowFirstItem, 0, true);
				}
				//太小了不作延迟启动
				if(rowFirstItem.mFinalDistance >= -(ver_delay_distance * 2) ||
						rowFirstItem.mFinalDistance >= -(hor_delay_distance * 2)){
					needDelay = false;
				}
			}
			else{
				//如果参照物未开始，其也应该是未开始状态
				if(preRowItemStatus.compareTo(FlipItemFastStatus.STOP) == 0){
					boolean itemFinished = computerRightFastEachItem(rowFirstItem, 0, true);
					if(itemFinished == false){
						finished = false;
					}
				}
				else if(preRowItemStatus.compareTo(FlipItemFastStatus.START_UNSTOP) == 0){
					//每列的第一行之间进行比较
					int diff = rowFirstItem.mTotalMoveDistance - preRowDistance;
					if(needDelay == true && diff <= ver_delay_distance){
						for(int clearRowIndex = rowIndex; clearRowIndex < mFlipItemRowList.size(); clearRowIndex++){
							List<FlipItem> clearItemList = mFlipItemRowList.get(clearRowIndex);
							for(int clearhorizontalIndex = 0; clearhorizontalIndex < clearItemList.size(); clearhorizontalIndex++){
								FlipItem clearItem = clearItemList.get(clearhorizontalIndex);
								clearItem.mCurrDelta = 0;
							}
						}
						break;		
					}
					else{
						int moveDistance = ver_delay_distance - diff;
						if(needDelay == false){
							moveDistance = preRowDelta;
						}
						boolean itemFinished = computerRightFastEachItem(rowFirstItem, moveDistance, false);
						if(itemFinished == false){
							finished = false;
						}
					}
				}
				else{
					//pre item unstart;
					//在进入快速模式的过程中为了防止跳变
					int diff = rowFirstItem.mTotalMoveDistance - preRowDistance;
					if(needDelay == true && diff > hor_delay_distance){
						boolean itemFinished = computerRightFastEachItem(rowFirstItem, 0, true);
						if(itemFinished == false){
							finished = false;
						}
					}
				}
			}
			preRowDelta = rowFirstItem.mCurrDelta;
			preHorizontalDelta = rowFirstItem.mCurrDelta;
			preRowDistance = rowFirstItem.mTotalMoveDistance;
			preHorizontalDistance = rowFirstItem.mTotalMoveDistance;
			//计算快速滚动的偏移值，因为启动滚动动画是异步的
			if(mStartingFlipScroll){
				rowFirstItem.mFastScrollStartingOffset += rowFirstItem.mCurrDelta;
			}
			
			if(rowFirstItem.mLastDistance <= rowFirstItem.mFinalDistance){
				preRowItemStatus = FlipItemFastStatus.STOP;
				preHorizontalItemStatus = FlipItemFastStatus.STOP;
			}
			else if(rowFirstItem.mCurrTotalMoveDistance != 0){
				preRowItemStatus = FlipItemFastStatus.START_UNSTOP;
				preHorizontalItemStatus = FlipItemFastStatus.START_UNSTOP;
			}
			else{
				preRowItemStatus = FlipItemFastStatus.UNSTART;
				preHorizontalItemStatus = FlipItemFastStatus.UNSTART;
			}
			
//			if(DEBUG){
//				if(columnFirstItem.mIndex == 21 || columnFirstItem.mIndex == 28){
//					Log.i("test", "into "+" index="+columnFirstItem.mIndex+
//							" mTotalMoveDistance="+columnFirstItem.mTotalMoveDistance+" preVerticalDistance="+preVerticalDistance+
//							" mCurrFrameCount="+ columnFirstItem.mCurrFrameCount+" mFinalFrameCount="+columnFirstItem.mFinalFrameCount+" mCurrDelta=" + columnFirstItem.mCurrDelta+
//							" mCurrTotalMoveDistance="+columnFirstItem.mCurrTotalMoveDistance+
//							" columnStatus="+preColumnItemStatus+" vertical="+preVertiaclItemStatus);
//				}
//			}
			//从1开始，第0个已经在上面处理过
			for(int verticalIndex = 1; verticalIndex < itemList.size(); verticalIndex++){
				FlipItem item = itemList.get(verticalIndex);
				item.mCurrDelta = 0;
				//比较列跟列
				//如果参照物未开始，其也应该是未开始状态
				if(preHorizontalItemStatus.compareTo(FlipItemFastStatus.STOP) == 0){
					boolean itemFinished = computerRightFastEachItem(item, 0, true);
					if(itemFinished == false){
						finished = false;
					}
				}
				else if(preHorizontalItemStatus.compareTo(FlipItemFastStatus.START_UNSTOP) == 0){
					int diff = item.mTotalMoveDistance - preHorizontalDistance;
					if(needDelay == true && diff <= hor_delay_distance){
						//将后继的值都清0
						for(int clearIndex = verticalIndex; clearIndex < itemList.size(); clearIndex++){
							FlipItem clearItem = itemList.get(clearIndex);
							clearItem.mCurrDelta = 0;
						}
						break;
					}
					else{
						int moveDistance = hor_delay_distance - diff;
						if(needDelay == false){
							moveDistance = preHorizontalDelta;
						}
						boolean itemFinished = computerRightFastEachItem(item, moveDistance, false);
						if(itemFinished == false){
							finished = false;
						}
					}
				}
				else{
					//pre item unstart;
					//在进入快速模式的过程中为了防止跳变
					int diff = item.mTotalMoveDistance - preHorizontalDistance;
					if(needDelay == true && diff > hor_delay_distance){
						boolean itemFinished = computerRightFastEachItem(item, 0, true);
						if(itemFinished == false){
							finished = false;
						}
					}
					
				}
				preHorizontalDelta = item.mCurrDelta;
				preHorizontalDistance = item.mTotalMoveDistance;
				//计算快速滚动的偏移值，因为启动滚动动画是异步的
				if(mStartingFlipScroll){
					item.mFastScrollStartingOffset += item.mCurrDelta;
				}
				
				if(item.mLastDistance <= item.mFinalDistance){
					preHorizontalItemStatus = FlipItemFastStatus.STOP;
				}
				else if(item.mCurrTotalMoveDistance != 0){
					preHorizontalItemStatus = FlipItemFastStatus.START_UNSTOP;
				}
				else{
					preHorizontalItemStatus = FlipItemFastStatus.UNSTART;
				}
//				if(DEBUG){
//					if(item.mIndex == 15 || item.mIndex == 16 ||
//							item.mIndex == 17 || item.mIndex == 18){
//						Log.i("test", "into "+" index="+item.mIndex+
//								" mTotalMoveDistance="+item.mTotalMoveDistance+" preVerticalDistance="+preVerticalDistance+
//								" mCurrFrameCount="+ item.mCurrFrameCount+" mFinalFrameCount="+item.mFinalFrameCount+" mCurrDelta=" + item.mCurrDelta+
//								" mCurrTotalMoveDistance="+item.mCurrTotalMoveDistance+
//								" columnStatus="+preColumnItemStatus+" vertical="+preVertiaclItemStatus);
//					}
//				}
			}
		}
		return finished;
    }
    
    
    /**
     * 计算在快速向右的状态下单个item运动的参数
     * @param item
     * @param moveDistance
     * @param preItemStoped
     * @return
     */
    private boolean computerRightFastEachItem(FlipItem item, int moveDistance, boolean preItemStoped){
    	boolean finished = true;
//    	int logIndex = 21;
//    	int logIndex1 =28;
//    	int logIndex2 = -1;
//    	int logIndex3 = -1;
    	if(preItemStoped == false){
			//开始启动
			item.mCurrDelta = moveDistance;
			item.mCurrFrameCount ++;
			if(item.mCurrFrameCount > item.mFinalFrameCount){
				item.mCurrFrameCount = item.mFinalFrameCount;
			}
			int left = item.mFinalDistance - item.mLastDistance;
			if(left > item.mCurrDelta){
				item.mCurrDelta = left;
			}
			item.mCurrTotalMoveDistance += item.mCurrDelta;
//			item.mDragDistance += item.mCurrDelta;
			item.mLastDistance += item.mCurrDelta;
			item.mTotalMoveDistance += item.mCurrDelta;
			finished = false;
//			if(DEBUG){
//				if(item.mIndex == logIndex || item.mIndex == logIndex1 ||
//						item.mIndex == logIndex2 || item.mIndex == logIndex3){
//					Log.i(TAG, "dragged index="+item.mIndex+" mLastDistance="+item.mLastDistance+
//							" horCount="+item.mColumnIndex + " verCount="+ item.mVerticalIndex+
//							" delta="+ item.mCurrDelta + " mFinalDistance="+item.mFinalDistance+" mTotalMoveDistance="+item.mTotalMoveDistance);
//				}
//			}
		}
		else{
			if(item.mLastDistance > item.mFinalDistance){
//				if(item.mCurrFrameCount <= item.mFinalFrameCount){
//					float ratio = (float)(item.mCurrFrameCount - (item.mDragFrameCount - 1)) / (float)(item.mFinalFrameCount - (item.mDragFrameCount - 1));
				int currDistance = item.mLastDistance + mFastStep.getCurrStep();
//				int currDistance = item.mLastDistance + mFastStepDistance;
				if(currDistance < item.mFinalDistance){
					currDistance = item.mFinalDistance;
				}
				item.mCurrDelta = currDistance - item.mLastDistance;
				item.mCurrTotalMoveDistance += item.mCurrDelta;
				item.mLastDistance += item.mCurrDelta;
				item.mTotalMoveDistance += item.mCurrDelta;
				item.mCurrFrameCount ++;
				if(item.mCurrFrameCount > item.mFinalFrameCount){
					item.mCurrFrameCount = item.mFinalFrameCount;
				}
				finished = false;
//				if(DEBUG){
//					if(item.mIndex == logIndex || item.mIndex == logIndex1 ||
//							item.mIndex == logIndex2 || item.mIndex == logIndex3){
//						Log.i(TAG, "index="+item.mIndex+" mLastDistance="+item.mLastDistance+
//								" horCount="+item.mColumnIndex + " verCount="+ item.mVerticalIndex+
//								" delta="+ item.mCurrDelta+" mCurrFrameCount="+item.mCurrFrameCount+
//								" mFinalFrameCount="+item.mFinalFrameCount+" mFinalDistance="+item.mFinalDistance+" mTotalMoveDistance="+item.mTotalMoveDistance);
//					}
//				}
			}
			else{
				item.mCurrDelta = 0;
				item.mCurrFrameCount = item.mFinalFrameCount;
				item.mLastDistance = item.mFinalDistance;
//				if(DEBUG){
//					if(logIndex == item.mIndex || item.mIndex == logIndex1){
//						Log.i(TAG, "index="+item.mIndex+" finished" + " mFinalDistance="+item.mFinalDistance+" mTotalMoveDistance="+item.mTotalMoveDistance);
//					}
//				}
			}
		}
    	return finished;
    }
    
    
    
    /**
     * 生成item的顺序列表（以行为单个列表的列表）
     * @param type
     * @param start
     * @param end
     * @param obj
     */
    private void makeFlipItemList(FlipListChangeType type, int start, int end, Object obj){
    	int headerCount = mHeaderViewList != null ? mHeaderViewList.size() : 0;
    	int footerCount = mFooterViewList != null ? mFooterViewList.size() : 0;
    	int currAdapterRow = -1;
    	
    	int firstIndex = -1;
    	int lastIndex = -1;

    	if(mFlipItemRowList.size() > 0){
    		//取得开始index
	    	List<FlipItem> firstList = mFlipItemRowList.get(0);
	    	if(firstList != null){
	    		FlipItem first = firstList.get(0);
	    		if(first != null){
	    			firstIndex = first.mIndex;
	    		}
	    	}
	    	//取得结束的index
	    	List<FlipItem> lastList = mFlipItemRowList.get(mFlipItemRowList.size() - 1);
	    	if(lastList != null){
	    		FlipItem last = lastList.get(lastList.size() - 1);
	    		if(last != null){
	    			lastIndex = last.mIndex;
	    		}
	    	}
    	}
    	if(DEBUG){
    		Log.i(TAG, "makeFlipItemList firstIndex="+firstIndex+" lastIndex="+lastIndex+
    				" start="+start+" end="+end+" type="+type);
    	}
    	for(int i = start; i <= end; i++){
    		if(i >= firstIndex && i <= lastIndex){
    			//已存在
    			if(headerCount > 0 && i < headerCount){
	    			//is header view;
	    			int currHeader = mHeaderViewList.get(i);
	    			int headerRowCount = currHeader >> 16;
	    			int headerHorizontalCount = currHeader & 0x00ff;
	    			for(int c = 0; c < headerRowCount; c++){
	    				for(int v = 0; v < headerHorizontalCount; v++){
	    					int headIndex = c * headerHorizontalCount + v;
	    					refreshFlipItem(i, headIndex, obj, type);
	    				}
	    			}
	    		}
    			else if(footerCount > 0 && i >= (mTotalItemCount - footerCount)){
	    			//is footer view;
	    			int currFooter = mFooterViewList.get(i);
	    			int footerRowCount = currFooter >> 16;
	    			int footerHorizontalCount = currFooter & 0x00ff;
	    			for(int c = 0; c < footerRowCount; c++){
	    				//==================速度还有优化空间=================
	    				//整理到列表里面
	    				List<FlipItem> columnFlipItemList = new ArrayList<FlipItem>();
	        			mFlipItemRowList.add(columnFlipItemList);
	    				for(int v = 0; v < footerHorizontalCount; v++){
	    					int footerIndex = c * footerHorizontalCount + v;
	    					refreshFlipItem(i, footerIndex, obj, type);
	    				}
	    			}
	    		}
    			else{
    				//is adapter
    				refreshFlipItem(i, 0, obj, type);
    			}
    			
    		}
    		else{
    			//不存在
	    		if(headerCount > 0 && i < headerCount){
	    			//is header view;
	    			int currHeader = mHeaderViewList.get(i);
	    			int headerRowCount = currHeader >> 16;
	    			int headerHorizontalCount = currHeader & 0x00ff;
	    			int columnIndex = mFlipItemRowList.size();
	    			for(int c = 0; c < headerRowCount; c++){
	    				//==================速度还有优化空间=================
	    				//整理到列表里面
	    				List<FlipItem> columnFlipItemList = new ArrayList<FlipItem>();
	        			mFlipItemRowList.add(columnFlipItemList);
	    				for(int v = 0; v < headerHorizontalCount; v++){
	    					int headIndex = c * headerHorizontalCount + v;
	    					FlipItem item = getMakedFlipItem(type, FlipItemPositionType.HEATER,
	    							i, headIndex, c + columnIndex, v, obj);
	    	    			if(item != null){
	    	    				columnFlipItemList.add(item);
	    	    			}
	    				}
	    			}
	    		}
	    		else if(footerCount > 0 && i >= (mTotalItemCount - footerCount)){
	    			//is footer view;
	    			int currFooter = mFooterViewList.get(i);
	    			int footerRowCount = currFooter >> 16;
	    			int footerHorizontalCount = currFooter & 0x00ff;
	    			int columnIndex = mFlipItemRowList.size();
	    			for(int c = 0; c < footerRowCount; c++){
	    				//==================速度还有优化空间=================
	    				//整理到列表里面
	    				List<FlipItem> columnFlipItemList = new ArrayList<FlipItem>();
	        			mFlipItemRowList.add(columnFlipItemList);
	    				for(int v = 0; v < footerHorizontalCount; v++){
	    					int footerIndex = c * footerHorizontalCount + v;
	    					FlipItem item = getMakedFlipItem(type, FlipItemPositionType.FOOTER,
	    							i, footerIndex, c + columnIndex, v, obj);
	    	    			if(item != null){
	    	    				columnFlipItemList.add(item);
	    	    			}
	    				}
	    			}
	    		}
	    		else{
	    			//is adapter view;
	    			int adatperStart = start;
	    			if(type == FlipListChangeType.ADD_AFTER){
	    				adatperStart = mStartListIndex;
	    			}
	    			
	    			//需要去掉当前在屏幕内的headerView的个数
	    			int inScreenHeaderCount = headerCount - adatperStart;
	    			if(inScreenHeaderCount <= 0){
	    				inScreenHeaderCount = 0;
	    			}
	    			
	    			int adapterRowIndex = ((i - adatperStart) - inScreenHeaderCount) / mRowCount;
	    			int adapterHorizontalIndex = ((i - adatperStart) - inScreenHeaderCount) % mRowCount;
	    			List<FlipItem> rowFlipItemList;
	    			int rowIndex;
	    			if(adapterRowIndex != currAdapterRow){
						currAdapterRow = adapterRowIndex;
						//==================速度还有优化空间=================
	    				//整理到列表里面
						rowFlipItemList = new ArrayList<FlipItem>();
	        			mFlipItemRowList.add(rowFlipItemList);	
	        			rowIndex = mFlipItemRowList.size() - 1;
					}
	    			else{
	    				rowIndex = mFlipItemRowList.size() - 1;
	    				rowFlipItemList = mFlipItemRowList.get(rowIndex);
	    			}
	    			FlipItem item = getMakedFlipItem(type, FlipItemPositionType.ADAPTER,
	    					i, 0, rowIndex, adapterHorizontalIndex, obj);
	    			if(item != null){
	    				rowFlipItemList.add(item);
	    			}
	    		}
    		}
    	}
    }
    
    
    /**
     * 取得每个item的值
     * @param changeType
     * @param posType
     * @param index
     * @param secondIndex
     * @param rowIndex
     * @param horizontalIndex
     * @param object
     * @return
     */
    private FlipItem getMakedFlipItem(FlipListChangeType changeType, FlipItemPositionType posType,
    		int index, int secondIndex, 
    		int rowIndex, int horizontalIndex,
    		Object object){
    	FlipItem item = null;
    	switch (changeType) {
		case INIT:
		case ADD_BEFORE:
			item = getFlipItem(index, secondIndex);
			//只有列表里不存在才加入
			if(item == null){
				item = new FlipItem(index, 0);
				mFlipItemMap.put(getFlipItemMapKey(index, secondIndex), item);
			}
			item.mRowIndex = rowIndex;
			item.mHorizontalIndex = horizontalIndex;
//			if(DEBUG){
//				Log.i(TAG, "getMakedFlipItem changeType="+changeType+" columnIndex="+columnIndex+" verticalIndex="+verticalIndex+
//					" Index="+index+" secondIndex="+secondIndex+" startingOffset="+item.mFastScrollStartingOffset);
//			}
			break;
		case ADD_AFTER:
			if(object != null && object instanceof FlipItem){
				item = addAfterItem((FlipItem)object, index, secondIndex);
				if(item == null){
					item = new FlipItem(index, 0);
					mFlipItemMap.put(getFlipItemMapKey(index, secondIndex), item);
				}
				item.mRowIndex = rowIndex;
				item.mHorizontalIndex = horizontalIndex;
//				if(DEBUG){
//					Log.i(TAG, "getMakedFlipItem ADD_AFTER columnIndex="+columnIndex+" verticalIndex="+verticalIndex+
//						" Index="+index+" secondIndex="+secondIndex);
//				}
			}
			break;
		case TEMP:
			item = getFlipItem(index, secondIndex);
			//只有列表里不存在才加入
			if(item == null){
				item = new FlipItem(index, 0);
				mFlipItemMap.put(getFlipItemMapKey(index, secondIndex), item);
			}
			item.mRowIndex = rowIndex;
			item.mHorizontalIndex = horizontalIndex;
			FlipItem preItem;
			if(mIsRight){
				//上一列的第一个
				preItem = getFlipItemRow(rowIndex - 1, 0);
			}
			else{
				//第一列的最后一个
				preItem = getFirstRowLasterItem();
			}
			if(preItem != null){
				int left = preItem.mFinalDistance - preItem.mLastDistance;
				int leftCount = (int)(preItem.mFinalFrameCount - preItem.mCurrFrameCount);
				item.mFinalFrameCount = preItem.mFinalFrameCount;
				item.mLastDistance = preItem.mLastDistance;
				item.mFinalDistance = preItem.mFinalDistance;
				item.mTotalMoveDistance = preItem.mTotalMoveDistance;
				if(DEBUG){
					Log.i(TAG, "getMakedFlipItem unknown left="+left+" index="+preItem.mIndex+
							" mFinalDistance="+preItem.mFinalDistance+
							" mLastDistance="+preItem.mLastDistance+" leftCount="+leftCount+
							" mFinalFrameCount="+preItem.mFinalFrameCount+
							" mCurrFrameCount="+preItem.mCurrFrameCount+
							" mTotalMoveDistance="+preItem.mTotalMoveDistance);
				}
			}
			break;
		default:
			break;
		}
    	return item;
    }
    
    
    private void refreshFlipItem(int index, int secondIndex, Object obj, FlipListChangeType type){
    	if(type.compareTo(FlipListChangeType.ADD_AFTER) == 0){
	    	addAfterItem((FlipItem)obj, index, 0);
    	}
    	else if(type.compareTo(FlipListChangeType.ADD_BEFORE) == 0){
        	addBeforeItem((FlipItem)obj, index, 0);	
    	}
    	else if(type.compareTo(FlipListChangeType.TEMP) == 0){
			if(mIsRight == false){
	    		FlipItem item = getFlipItem(index, secondIndex);
	    		if(item != null){
	    			FlipItem preItem = null;
	    			//第一列的最后一个
	    			for(int i = 0; i < mFlipItemRowList.size(); i++){
	    		    	List<FlipItem> itemList = mFlipItemRowList.get(i);
	    		    	if(itemList != null && itemList.size() > 0){
	    		    		FlipItem first = itemList.get(0);
	    		    		if(first.mIndex >= (Integer)obj){
	    		    			preItem = itemList.get(itemList.size() - 1);
	    		    			break;
	    		    		}
	    		    	}
	    	    	}
					if(preItem != null){
						int left = preItem.mFinalDistance - preItem.mLastDistance;
						int leftCount = (int)(preItem.mFinalFrameCount - preItem.mCurrFrameCount);
						item.mFinalFrameCount = preItem.mFinalFrameCount;
						item.mLastDistance = preItem.mLastDistance;
						item.mFinalDistance = preItem.mFinalDistance;
						item.mTotalMoveDistance = preItem.mTotalMoveDistance;
						if(DEBUG){
							Log.i(TAG, "getMakedFlipItem unknown left="+left+" index="+preItem.mIndex+
									" mFinalDistance="+preItem.mFinalDistance+
									" mLastDistance="+preItem.mLastDistance+" leftCount="+leftCount+
									" mFinalFrameCount="+preItem.mFinalFrameCount+
									" mCurrFrameCount="+preItem.mCurrFrameCount+
									" mTotalMoveDistance="+preItem.mTotalMoveDistance);
						}
					}
	    		}
			}
    	}
    }
    
    
    /**
     * 创建初始化的列表
     * @param start
     * @param end
     */
    private void makeInitFlipItemList(int start, int end, boolean isRight){
    	if(isRight){
    		makeFlipItemList(FlipListChangeType.INIT, start, end + mRowCount, null);
    		mPreAddTempItemIndex = start;
    	}
    	else{
    		int tempStart;
	    	if(start <= mHeaderViewList.size()){
	    		tempStart = start - 1;
	    	}
	    	else{
	    		tempStart = start - mRowCount;
	    	}
	    	if(tempStart < 0){
	    		tempStart = 0;
	    	}
    		makeFlipItemList(FlipListChangeType.INIT, tempStart, end, null);
    		mPreAddTempItemIndex = tempStart;
    	}
    }
    
    
    /**
     * 创建向左滚动的列表（将所有的item按顺序加入到表里面）
     * @param start
     * @param end
     */
    private void makeBeforeFlipItemList(int start, int end){
//    	makeTempFlipItemList(start, end, false, FlipListChangeType.TEMP);
    	int tempStart;
    	if(start <= mHeaderViewList.size()){
    		tempStart = start - 1;
    	}
    	else{
    		tempStart = start - mRowCount;
    		if(tempStart < mHeaderViewList.size()){
    			tempStart = mHeaderViewList.size() - 1;
    		}
    	}
    	if(tempStart < 0){
    		tempStart = 0;
    	}
    	//需要顺序添加，所以先从tempStart开始，然后再去添加临时相关的数据
    	makeFlipItemList(FlipListChangeType.ADD_BEFORE, tempStart, end, null);
    	int tempEnd = Math.max(mPreAddTempItemIndex, start - 1);
    	if(tempStart <= tempEnd && tempStart < mPreAddTempItemIndex){
    		makeFlipItemList(FlipListChangeType.TEMP, tempStart, tempEnd, mPreAddTempItemIndex);
    	}
    	if(DEBUG){
    		Log.i(TAG, "makeBeforeFlipItemList start="+start+" end="+end+
    				" tempStart="+tempStart+" tempEnd="+tempEnd+" mPreAddTempItemIndex="+mPreAddTempItemIndex);
    	}
    	mPreAddTempItemIndex = tempStart;
    }
    
    
    /**
     * 创建向右滚动的列表（只添加新增的内容）
     * @param start
     * @param end
     * @param lastHeaderItem
     */
    private void makeAfterFlipItemList(int start, int end, FlipItem lastHeaderItem){
    	makeFlipItemList(FlipListChangeType.ADD_AFTER, start, end, lastHeaderItem);
    	makeTempFlipItemList(start, end, true, FlipListChangeType.TEMP);
    }
    
    
    private void makeTempFlipItemList(int start, int end, boolean after, FlipListChangeType type){
    	if(after == false){
	    	int tempStart = start - mRowCount;
	    	if(tempStart < 0){
	    		tempStart = 0;
	    	}
	    	int tempEnd = start - 1;
	    	if(tempEnd < 0){
	    		tempEnd = 0;
	    	}
	    	
	    	if(tempEnd <= mHeaderViewList.size() - 1){
	    		//只加一行的HeaderView
	    		makeFlipItemList(type, tempEnd, tempEnd, null);
	    	}
	    	else{
	    		//加adapter的View
	    		makeFlipItemList(type, tempStart, tempEnd, null);
	    	}
    	}
    	else{
    		makeFlipItemList(type, end + 1, end + mRowCount, null);
    	}
    }
    
    /**
     * 取得指定的item
     * @param index 列
     * @param secondIndex 行
     * @return
     */
    private FlipItem getFlipItem(int index, int secondIndex){
    	//-1代表headerView或footerView的父级本身
    	if(secondIndex == -1){
    		if(mHeaderViewList.size() > 0 && index < mHeaderViewList.size()){
    			int currHeader = mHeaderViewList.get(index);
    			int headerVerticalCount = currHeader >> 16;
    			int headerHorizontalCount = currHeader & 0x00ff;
    			secondIndex = (headerHorizontalCount * headerVerticalCount) - 1;
    		}
    		else if(mFooterViewList.size() > 0 && index >= mTotalItemCount - mFooterViewList.size()){
    			int currFooter = mFooterViewList.get(index);
    			int footerVerticalCount = currFooter >> 16;
    			int footerHorizontalCount = currFooter & 0x00ff;
    			secondIndex = (footerHorizontalCount * footerVerticalCount) - 1;
    		}
    	}
    	return mFlipItemMap.get(getFlipItemMapKey(index, secondIndex));
    }
    
    /**
     * 根据列跟行取得列表的item
     * @param rowListIndex
     * @param vertical
     * @return
     */
    
    private FlipItem getFlipItemRow(int rowListIndex, int vertical){
    	List<FlipItem> itemList = mFlipItemRowList.get(rowListIndex);
    	if(itemList != null && itemList.size() > 0){
    		return itemList.get(vertical);
    	}
    	return null;
    }
    
    
    /**
     * 取得当指定列的最后一个item
     * @param column
     * @return
     */
    private FlipItem getCurrRowLastItem(int rowIndex){
    	List<FlipItem> itemList = mFlipItemRowList.get(rowIndex);
    	if(itemList != null && itemList.size() > 0){
    		return itemList.get(itemList.size() - 1);
    	}
    	return null;
    }
    
    
    /**
     * 取得有效的（有可能最上一排临时添加的）最开始一行的最后一列的item
     * @return
     */
    private FlipItem getFirstRowLasterItem(){
    	for(int i = 0; i < mFlipItemRowList.size(); i++){
	    	List<FlipItem> itemList = mFlipItemRowList.get(i);
	    	if(itemList != null && itemList.size() > 0){
	    		FlipItem first = itemList.get(0);
	    		if(first.mIndex >= mStartListIndex){
	    			return itemList.get(itemList.size() - 1);
	    		}
	    	}
    	}
    	return null;
    }
    
    
    /**
     * 取得有效的（有可能最上一排临时添加的）最后一行最开始一列的item
     * @return
     */
    private FlipItem getLastRowFirsterItem(){
    	for(int i = 1; i < mFlipItemRowList.size(); i++){
	    	List<FlipItem> itemList = mFlipItemRowList.get(mFlipItemRowList.size() - i);
	    	if(itemList != null && itemList.size() > 0){
	    		FlipItem lastItem = itemList.get(itemList.size() - 1);
	    		if(lastItem.mIndex <= mEndListIndex){
	    			return itemList.get(0);
	    		}
	    	}
    	}
    	return null;
    }
    
    
    /**
     * 取得合成的map的键值
     * @param index
     * @param secondIndex
     * @return
     */
    private int getFlipItemMapKey(int index, int secondIndex){
    	//secondIndex max 2^8
    	return (index << 8) | secondIndex;
    }
    
    
    /**
     * 重新设置动画item的参数值
     * @param newFinalDistance
     */
    private void resetItemData(int newFinalDistance){
    	if(DEBUG){
    		Log.i(TAG, "resetItemData newFinalDistance="+newFinalDistance);
    	}
		boolean first = true;
    	for(List<FlipItem> itemList : mFlipItemRowList){
    		for(FlipItem item : itemList){
	    		item.mCurrDelta = 0;
	    		item.mCurrTotalMoveDistance = 0;
		    	item.mFinalFrameCount += mFinalFrameCount;
	    		//初始化启动过程中的偏移值，因为这个时候动画已经启动，偏移值的处理在此之前已经处理完成
	    		item.mFastScrollStartingOffset = 0;
	    		item.mFastScrollOffset = 0;
	    		item.mFinalDistance += newFinalDistance;
	    		if(first && mFastFlip){
	    			int step = (int)((float)(item.mFinalDistance - item.mLastDistance) / (float)(flip_scroll_frame_count));
    				if(mIsRight){
    					if(step > -min_fast_setp_discance){
    						step = -min_fast_setp_discance;
    					}
    				}
    				else{
    					if(step < min_fast_setp_discance){
    						step = min_fast_setp_discance;
    					}
    				}
	    			mFastStep.setStartStep(step);
	    			first = false;
	    		}
	    		if(DEBUG){
	    			Log.i(TAG, "resetItemData index="+item.mIndex+" newFinalDistance="+newFinalDistance+
	    					" mTotalMoveDistance="+item.mTotalMoveDistance+
	    					" mLastDistance="+item.mLastDistance+
	    					" mFinalDistance="+item.mFinalDistance+
	    					" mFinalFrameCount="+item.mFinalFrameCount+" mFastStep="+mFastStep);
	    		}
    		}
    	}
    }

    
    /**
     * 计算出真正需要偏移的距离
     * @param newFinalDistance
     * @return
     */
    private int getRealFinalDistance(int newFinalDistance){
    	int realFinalDistance = newFinalDistance;
    	int preSelectedIndex = mPreSelectedPosition;
    	if(preSelectedIndex > mEndListIndex){
    		preSelectedIndex = mEndListIndex;
    	}
    	else if(preSelectedIndex < mStartListIndex){
    		preSelectedIndex = mStartListIndex;
    	}
    	FlipItem preSelectedItem = mFlipItemMap.get(getFlipItemMapKey(preSelectedIndex, 0));
    	if(preSelectedItem != null){
    		//取得当前行的最后一个item，为什么不是像其它地方那样参考第一个呢，因为在run中的fillGap是参考第一个
    		//然而这个方法是在layoutChild后回调的,layoutChild就是参考最后一个的。
    		FlipItem rowLastItem = getCurrRowLastItem(preSelectedItem.mRowIndex);
    		if(rowLastItem != null){
		    	//上次选中的item剩余滚动的距离
		    	int selectedItemLeft = rowLastItem.mFinalDistance - rowLastItem.mLastDistance;
		    	//考虑快速启动过程中已经运行的偏移值
		    	realFinalDistance = newFinalDistance - rowLastItem.mFastScrollStartingOffset - selectedItemLeft;
				if(DEBUG){
					Log.i(TAG, "getRealFinalDistance columnFirstItem="+rowLastItem.mIndex+
							" selectedItemLeft="+selectedItemLeft+ " mFastOffset="+rowLastItem.mFastScrollStartingOffset+
							" realFinalDistance="+realFinalDistance+" newFinalDistance="+newFinalDistance+
							" mFinalDistance="+rowLastItem.mFinalDistance+" mLastDistance="+rowLastItem.mLastDistance +
							" preSelectedIndex="+preSelectedIndex);
				}
    		}
    	}
    	else{
    		//如果未完成，又找不到上次选中的item，就无法计算出上次未完成的滚动距离为多少，因此报错返回
    		if(mFinished == false){
    			Log.e(TAG, "error getRealFinalDistance selectedItem == null position="+preSelectedIndex);
    			return realFinalDistance;
    		}
    	}
    	return realFinalDistance;
    }
    
    
    
    /**
     * 增加头部序列
     * @param start
     */
    private void addBefore(int start){
    	if(DEBUG){
			Log.i(TAG, "addGridView start < mStartListIndex start="+start+" mStartListIndex="+mStartListIndex);
		}
		//取得最上一行的最后一个
		FlipItem firstFooterItem = getFirstRowLasterItem();
		if(firstFooterItem == null){
			return;
		}
		mFlipItemRowList.clear();
		makeBeforeFlipItemList(start, mEndListIndex);
		
		int headerCount = mHeaderViewList != null ? mHeaderViewList.size() : 0;
		int footerCount = mFooterViewList != null ? mFooterViewList.size() : 0;
		for(int i = mStartListIndex - 1; i >= start ; i--){
			if(headerCount > 0 && i < headerCount){
    			//is header view;
    			int currHeader = mHeaderViewList.get(i);
    			int headerRowCount = currHeader >> 16;
    			int headerHorizontalCount = currHeader & 0x00ff;
    			for(int c = headerRowCount - 1; c >= 0; c--){
    				for(int v = headerHorizontalCount - 1; v >= 0 ; v--){
    					int headIndex = c * headerHorizontalCount + v;
    					addBeforeItem(firstFooterItem, i, headIndex);
    				}
    			}
    		}
    		else if(footerCount > 0 && i >= (mTotalItemCount - footerCount)){
    			//is footer view;
    			int currFooter = mFooterViewList.get(i);
    			int footerRowCount = currFooter >> 16;
    			int footerHorizontalCount = currFooter & 0x00ff;
    			for(int c = footerRowCount - 1; c >= 0; c--){
    				for(int v = footerHorizontalCount - 1; v >= 0; v--){
    					int footerIndex = c * footerHorizontalCount + v;
    					addBeforeItem(firstFooterItem, i, footerIndex);
    				}
    			}
    		}
    		else{
    			//is adapter view;
				addBeforeItem(firstFooterItem, i, 0);
    		}
		}
		mStartListIndex = start;
    }
    
    /**
     * 修改新增头部item的值
     * @param firstFooterItem
     * @param index
     * @param secondIndex
     * @return
     */
    private FlipItem addBeforeItem(FlipItem firstFooterItem, int index, int secondIndex){
    	FlipItem item = null;
    	if(firstFooterItem == null){
    		return item;
    	}
		item = getFlipItem(index, secondIndex);
		if(item != null){
			int currLastDistance = item.mLastDistance;
			//因为向上运动所以不能小于0
			if(currLastDistance < 0){
				currLastDistance = 0;
			}
			if(mItemFlipScrollerListener != null){
				//currLastDistance：在此时应该已运动了多少距离
				//firstFooterItem.mLastDistance：这个item的布局的参考item（下一行也就是之前的第一行的最后一个）已经运动了多少距离
				//firstFooterItem.mFastScrollStartingOffset：因为是异步启动的所以在这段时间内已经运动了多少距离
				//firstFooterItem.mFastScrollOffset：布局参考的item的在布局完成同样被偏移了动画的当时位置，
				//										偏移的距离同样也要在当前item中进行考虑
				//item.mFastScrollStartingOffset：需要去掉当前item在启动动画的过程中已经移动的距离，
				//										因为布局完成后当前item就已经加入到动画里面进行运动
				int delta = currLastDistance - firstFooterItem.mLastDistance + 
						firstFooterItem.mFastScrollStartingOffset + firstFooterItem.mFastScrollOffset
						- item.mFastScrollStartingOffset;
				if(DEBUG){
					Log.i(TAG, "addGridView  start < mStartListIndex index="+index+
							" currLastDistance="+currLastDistance+
							" firstFooterItem index="+firstFooterItem.mIndex+
							" mLastDistance="+firstFooterItem.mLastDistance+
							" mFastScrollStartingOffset="+firstFooterItem.mFastScrollStartingOffset+
							" mFastScrollOffset="+firstFooterItem.mFastScrollOffset+
							" item.mFastScrollStartingOffset="+item.mFastScrollStartingOffset+
							" delta="+delta);
				}
				if(delta != 0){
					mItemFlipScrollerListener.onOffsetNewChild(index, secondIndex, delta);
				}
			}
		}
		return item;
    }
    
    
    private void addAfter(int end){
    	if(DEBUG){
			Log.i(TAG, "addGridView end > mEndListIndex end="+end+" mEndListIndex="+mEndListIndex+" mFastFlip="+mFastFlip);
		}
		//需要增加列表的item
		//取得最后一排的第一个item的index
		FlipItem lastHeaderItem = getLastRowFirsterItem();
		if(lastHeaderItem == null){
			return;
		}
		makeAfterFlipItemList(mEndListIndex + 1, end, lastHeaderItem);
		mEndListIndex = end;
    }
    
    
    /**
     * 修改新增尾部的item的值
     * @param lastHeaderItem
     * @param index
     * @param secondIndex
     * @param columnIndex
     * @param verticalIndex
     * @return
     */
    private FlipItem addAfterItem(FlipItem lastHeaderItem, int index, int secondIndex){
    	FlipItem item = null;
    	if(lastHeaderItem == null){
    		return item;
    	}
		item = getFlipItem(index, secondIndex);
		if(item != null){
			int currLastDistance = item.mLastDistance;
			//因为向右运动所以不能大于0
			if(currLastDistance > 0){
				currLastDistance = 0;
			}
			if(mItemFlipScrollerListener != null){
				//currLastDistance：在此时应该已运动了多少距离
				//lastHeaderItem.mLastDistance：这个item的布局的参考item（上一行的第一个）已经运动了多少距离
				//lastHeaderItem.mFastScrollStartingOffset：因为是异步启动的所以在这段时间内已经运动了多少距离
				//lastHeaderItem.mFastScrollOffset：布局参考的item的在布局完成同样被偏移了动画的当时位置，
				//										偏移的距离同样也要在当前item中进行考虑
				//item.mFastScrollStartingOffset：需要去掉当前item在启动动画的过程中已经移动的距离，
				//										因为布局完成后当前item就已经加入到动画里面进行运动
				int delta = currLastDistance - lastHeaderItem.mLastDistance + 
						lastHeaderItem.mFastScrollStartingOffset + lastHeaderItem.mFastScrollOffset
						- item.mFastScrollStartingOffset;
					if(DEBUG){
						Log.i(TAG, "addGridView  end > mEndListIndex onOffsetNewChild index="+index+
								" currLastDistance="+currLastDistance+
								" mLastDistance="+lastHeaderItem.mLastDistance+
								" last Index="+lastHeaderItem.mIndex+
								" curr Index="+item.mIndex+
								" curr startingOffset="+item.mFastScrollStartingOffset+
								" mFastScrollStartingOffset="+lastHeaderItem.mFastScrollStartingOffset+
								" mFastScrollOffset="+lastHeaderItem.mFastScrollOffset+
								" delta="+delta);
					}
				if(delta != 0){
					mItemFlipScrollerListener.onOffsetNewChild(index, secondIndex, delta);
				}
			}
		}
		return item;
    }
    
    
    /**
     * 判断指定的item是否已经结束
     * @param index
     * @return
     */
    private boolean itemIsFinished(int index){
    	if(index < mHeaderViewList.size()){
    		//headerView
    		int header = mHeaderViewList.get(index);
    		int headerRowCount = header >> 16;
			int headerHorizontalCount = header & 0x00ff;
			int secondIndex = (headerRowCount * headerHorizontalCount) - 1;
			FlipItem headerLastItem = getFlipItem(index, secondIndex);
			if(headerLastItem != null){
				int left = headerLastItem.mFinalDistance - headerLastItem.mLastDistance;
				if(left != 0){
					return false;
				}
			}
    	}
    	else if(index >= mTotalItemCount - mFooterViewList.size()){
    		//footerView
    		int footer = mFooterViewList.get(index);
    		int footerRowCount = footer >> 16;
			int footerHorizontalCount = footer & 0x00ff;
			int secondIndex = (footerRowCount * footerHorizontalCount) - 1;
			FlipItem footerLastItem = getFlipItem(index, secondIndex);
			if(footerLastItem != null){
				int left = footerLastItem.mFinalDistance - footerLastItem.mLastDistance;
				if(left != 0){
					return false;
				}
			}
    	}
    	else{
    		//adatper
    		FlipItem item = getFlipItem(index, 0);
			if(item != null){
				int left = item.mFinalDistance - item.mLastDistance;
				if(left != 0){
					return false;
				}
			}
    	}
    	return true;
    }
    
    
	
	/**
	 * 取得当前位置列首行的位置
	 * @param position
	 * @return
	 */
	protected int getColumnStart(int position) {
		int columnStart = position;
		if (position < mHeaderViewList.size()) {
			columnStart = position;
		} else {
			if (position < mTotalItemCount - mFooterViewList.size()) {
				int newPosition = position - mHeaderViewList.size();
				columnStart = newPosition - (newPosition % mRowCount) + mHeaderViewList.size();
			} else {
				columnStart = position;
			}
		}

		return columnStart;
	}
	
	
    /**
     * 拉绳效果的item的数据
     * @author tim
     */
    private class FlipItem{
    	public int mIndex;//当前item在列表中的位置
    	public int mRowIndex;
    	public int mHorizontalIndex;
    	//public int mSecondIndex;//二维序列，用来表示是headerView跟footerView里面的序列
    	public int mCurrFrameCount;//运行了多少帧
    	public int mLastDistance;//截止上次为止已经移动的距离（现在跟mTotalMoveDistance一样，后续有时间可以考虑去掉）
    	public int mCurrDelta;//当前帧的移动距离
    	public int mFinalDistance;//运行的最终的距离
    	public int mFastScrollOffset;//快速滚动的时候layout跟动画位置偏移的距离
    	public int mFastScrollStartingOffset;//快速滚动，在启动过程中动画已经运行的距离
    	public int mFinalFrameCount;//最后的完成帧数
    	public int mTotalMoveDistance;//此item一共完成走了多少距离（现在跟mLastDistance一样，后续有时间可以考虑去掉）
//    	public int mDragFrameCount;//在快速滚动的时候此item被上一个参照的item一共拖拽了多少帧
//    	public int mDragDistance;//在快速滚动的时候此item被上一个参照的item一共拖拽了多少距离
    	public int mCurrTotalMoveDistance;
    	public FlipItem(int index, int finalDistance){
    		mIndex = index;
    		mFinalDistance = finalDistance;
    	}    	
    }
    
    
    /**
     * 回调监听器
     * @author tim
     *
     */
    public interface ItemFlipScrollerListener{
    	/**
    	 * 重新布局后因为各种原因会跟动画要求的位置不同，需要回调进行偏移，偏移时注意参照视图的位置
    	 * @param childIndex
    	 * @param secondIndex
    	 * @param delta
    	 */
    	public void onOffsetNewChild(int childIndex, int secondIndex, int delta);
    	
    	public void onFinished();
    }
    
    
    /**
     * 计算快速滚动后做减速的算法
     * @author tim
     *
     */
    private class FastStep{
    	private int mCurrDelta;//当前位移
    	private int mStartDelta;//开始的位移
    	private int mStopDelta;//目标的目前位移
    	private boolean mIsComeDown;//是否进入减速运动
    	private boolean mIsPositive;//是否为正向的，也就是delta是否为正
    	private int mCurrDistance;//当前的移动距离
    	private int mStopDistance;//结束的距离
    	private Interpolator mInterpolator = new DecelerateInterpolator(0.4f);//加速器
    	
    	/**
    	 * 设置开始的位移
    	 * @param step
    	 */
    	private void setStartStep(int step){
    		mIsComeDown = false;
    		mStartDelta = step; 
    		mCurrDelta = mStartDelta;
    	}
    	
    	/**
    	 * 重新设置减速的相关参数
    	 * @param target
    	 */
    	private void resetComeDown(int target){
    		if(DEBUG){
    			Log.i(TAG, "resetComeDown target="+target+" mCurrDelta="+mCurrDelta);
    		}
    		if(mIsComeDown){
    			return;
    		}
    		mIsComeDown = true;
    		mCurrDistance = mCurrDelta;
    		mStopDistance = target;
    		if(mStartDelta > 0){
        		mStopDelta = min_fast_setp_discance;
    			mIsPositive = true;
    		}
    		else{
        		mStopDelta = -min_fast_setp_discance;
    			mIsPositive = false;
    		}
    	}
    	
    	/**
    	 * 计算位移
    	 * @return
    	 */
    	private boolean computerOffset(){
    		if(!mIsComeDown){
    			return true;
    		}
    		else{
	    		if((mIsPositive && mCurrDistance > mStopDistance) ||
	    				(!mIsPositive && mCurrDistance < mStopDistance)){
	    			return false;
	    		}
	    		mCurrDelta = 0;
	    		float input = (float)mCurrDistance / (float)mStopDistance;
	    		float output = input;
	    		if(mInterpolator != null){
	    			output = mInterpolator.getInterpolation(input);
	    		}
	    		mCurrDelta = mStartDelta + (int)((mStopDelta - mStartDelta)* output);
	    		if(mIsPositive && mCurrDelta < min_fast_setp_discance){
	    			mCurrDelta = min_fast_setp_discance;
	    		}
	    		else if(!mIsPositive && mCurrDelta > (-min_fast_setp_discance)){
	    			mCurrDelta = -min_fast_setp_discance;
	    		}
	    		mCurrDistance += mCurrDelta;
	    		if(DEBUG){
		    		Log.i(TAG, "computerOffset input="+input+" output="+output+" mCurrDelta="+mCurrDelta
		    				+" mCurrDistance="+mCurrDistance+" mStopDistance="+mStopDistance);
	    		}
    		}
    		return true;
    	}
    	
    	/**
    	 * 取得当前位移
    	 * @return
    	 */
    	private int getCurrStep(){
    		return mCurrDelta;
    	}
    	
    	/**
    	 * 停止
    	 */
    	private void finished(){
    		mCurrDelta = 0;
    	}
    }
    
    public int getHor_delay_distance() {
		return hor_delay_distance;
	}

	public void setHor_delay_distance(int hor_delay_distance) {
		this.hor_delay_distance = hor_delay_distance;
	}

	public int getVer_delay_distance() {
		return ver_delay_distance;
	}

	public void setVer_delay_distance(int ver_delay_distance) {
		this.ver_delay_distance = ver_delay_distance;
	}

	public int getMin_fast_setp_discance() {
		return min_fast_setp_discance;
	}

	public void setMin_fast_setp_discance(int min_fast_setp_discance) {
		this.min_fast_setp_discance = min_fast_setp_discance;
	}

	public int getFlip_scroll_frame_count() {
		return flip_scroll_frame_count;
	}

	public void setFlip_scroll_frame_count(int flip_scroll_frame_count) {
		this.flip_scroll_frame_count = flip_scroll_frame_count;
	}

	public int getHor_delay_frame_count() {
		return hor_delay_frame_count;
	}

	public void setHor_delay_frame_count(int hor_delay_frame_count) {
		this.hor_delay_frame_count = hor_delay_frame_count;
	}

	public int getVer_delay_frame_count() {
		return ver_delay_frame_count;
	}

	public void setVer_delay_frame_count(int ver_delay_frame_count) {
		this.ver_delay_frame_count = ver_delay_frame_count;
	}
}
