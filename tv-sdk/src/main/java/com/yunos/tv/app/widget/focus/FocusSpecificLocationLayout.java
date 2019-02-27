package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

import com.yunos.tv.app.widget.ViewGroup;
import com.yunos.tv.app.widget.adapter.*;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class FocusSpecificLocationLayout extends ViewGroup {
	public static final boolean DEBUG = false;
	public static final int INVALID_POSITION = -1;

	protected final String TAG = "[" + ((Object) this).getClass().getSimpleName() + "]";

	protected ArrayList<View> mAdapterViews = new ArrayList<View>();
	protected LocationAdapter mAdapter = null;// 包含的数据必须含有中心坐标和宽高
	private DataSetObserver mDataSetObserver;

	private int mItemCount;
	private int mOldItemCount;
	private boolean mDataChanged;
	private int mOldSelectedPosition;

	// private int mMinLeft = Integer.MAX_VALUE;
	// private int mMinTop = Integer.MAX_VALUE;
	// private int mMaxRight = Integer.MIN_VALUE;
	// private int mMaxBottom = Integer.MIN_VALUE;

	private boolean mInLayout;
	private boolean mBlockLayoutRequests;
	private int mFirstPosition = 0;

	private RecycleBin mRecycler = new RecycleBin();
	protected final boolean[] mIsScrap = new boolean[1];

	boolean mAdapterHasStableIds;
    private OnLocationItemClickListener mOnItemClickListener;

    public FocusSpecificLocationLayout(Context context) {
		super(context);
		init();
	}

	public FocusSpecificLocationLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FocusSpecificLocationLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		resetLocation();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (DEBUG) {
			Log.d(TAG, String.format("onMeasure width = %d, height = %d, count = %d", getMeasuredWidth(), getMeasuredHeight(), getChildCount()));
		}
	}

	@Override
	protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
		super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
	}

	@Override
	public boolean preOnKeyDown(int keyCode, KeyEvent event) {
		return super.preOnKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mInLayout) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mAdapter == null) {
            return false;
        }
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER: {
                if (!isEnabled()) {
                    return true;
                }
                if (mIndex >= 0 && mAdapter != null && mIndex < mAdapter.getCount()) {
                    final View view = getChildAt(mIndex);
                    if (view != null) {
                        performItemClick(view, mIndex, 0);
                        view.setPressed(false);
                    }
                    setPressed(false);
                    return true;
                }
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    @Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (!isLayoutRequested()) {
			return;
		}
		mInLayout = true;
		layoutChildren();
		mInLayout = false;

		afterLayout(changed, l, t, r, b);
	}

	/**
	 * 根据适配器数据布局视图，利用RecycleBin重用视图
	 */
	private void layoutChildren() {
        if(DEBUG){
            Log.d(TAG, "layoutChildren mIndex = " + mIndex);
        }
		final boolean blockLayoutRequests = mBlockLayoutRequests;
		if (!blockLayoutRequests) {
			mBlockLayoutRequests = true;
		} else {
			return;
		}

		try {
			invalidate();
			if (mAdapter == null) {
				resetLocation();
				return;
			}

			int childrenTop = getPaddingTop();
			int childCount = getChildCount();

			if (mItemCount == 0) {
				resetLocation();
				return;
			} else if (mItemCount != mAdapter.getCount()) {
				throw new IllegalStateException("The content of the adapter has changed but " + "FocusSpecificLocationLayout did not receive a notification. Make sure the content of "
						+ "your adapter is not modified from a background thread, but only " + "from the UI thread. [in FocusSpecificLocationLayout(" + getId() + ", " + getClass() + ") with Adapter("
						+ mAdapter.getClass() + ")]");
			}

			final int firstPosition = mFirstPosition;
			final RecycleBin recycleBin = mRecycler;

			boolean dataChanged = mDataChanged;
			if (dataChanged) {
				int firstvisibleChildIndex = firstPosition;
				for (int i = firstvisibleChildIndex; i >= 0; i--) {
					recycleBin.addScrapView(getChildAt(i), firstPosition + i);
				}
				for (int i = firstvisibleChildIndex + 1; i < childCount; i++) {
					recycleBin.addScrapView(getChildAt(i), firstPosition + i);
				}
			} else {
				recycleBin.fillActiveViews(childCount, firstPosition);
			}

			detachAllViewsFromParent();
			recycleBin.removeSkippedScrap();

			if (childCount == 0) {
				fillFromTop(childrenTop);
			} else {
                if(mIndex >= mItemCount){
                    mIndex = mItemCount - 1;
                }
				int startIndex = mIndex;
				if (startIndex < 0) {
					startIndex = getFocusableItemIndex();
					mIndex = startIndex;
				}
				fillSpecific(startIndex, mAdapter.getItem(startIndex).getTop());
			}

			recycleBin.scrapActiveViews();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!blockLayoutRequests) {
				mBlockLayoutRequests = false;
			}
			if (DEBUG) {
				Log.d(TAG, "layoutChildren child count = " + getChildCount());
			}
		}
	}

	/**
	 * 从指定位置开始填充视图
	 * @param position 指定位置（有效）
	 * @param top 开始位置最上方坐标
	 * @return 返回最后填充的视图
	 */
	protected View fillSpecific(int position, int top) {
		mFirstPosition = Math.min(mFirstPosition, mIndex);
		mFirstPosition = Math.min(mFirstPosition, mItemCount - 1);
		if (mFirstPosition < 0) {
			mFirstPosition = 0;
		}
		
		boolean tempIsSelected = position == mIndex;
		View temp = makeAndAddView(position, top, true, mAdapter.getItem(position).getLeft(), tempIsSelected);

		View above;
		View below;

		above = fillUp(position - 1, mAdapter.getItem(position - 1).getTop() + mAdapter.getItem(position - 1).getHeight());
		below = fillDown(position + 1, mAdapter.getItem(position + 1).getTop());

		if (tempIsSelected) {
			return temp;
		} else if (above != null) {
			return above;
		} else {
			return below;
		}
	}

	/**
	 * 从指定位置开始向上填充视图
	 * @param pos 指定位置（有效）
	 * @param nextBottom 下一位置最下方坐标
	 * @return 返回选中填充的视图
	 */
	protected View fillUp(int pos, int nextBottom) {
		View selectedView = null;

		int end = 0;
		if ((getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
			end = getPaddingTop();
		}

		while (nextBottom > end && pos >= 0) {
			boolean selected = pos == mIndex;
			nextBottom = mAdapter.getItem(pos).getTop() + mAdapter.getItem(pos).getHeight();
			View child = makeAndAddView(pos, nextBottom, false, mAdapter.getItem(pos).getLeft(), selected);
			if (selected) {
				selectedView = child;
			}
			pos--;
		}

		return selectedView;
	}

	/**
	 * 从指定位置开始向下填充视图
	 * @param nextTop 下一位置最上方坐标
	 * @return 返回选中填充的视图
	 */
	protected View fillFromTop(int nextTop) {
		mFirstPosition = Math.min(mFirstPosition, mIndex);
		mFirstPosition = Math.min(mFirstPosition, mItemCount - 1);
		if (mFirstPosition < 0) {
			mFirstPosition = 0;
		}
		return fillDown(mFirstPosition, nextTop);
	}

	/**
	 * 从指定位置开始向下填充视图
	 * @param position 指定位置（有效）
	 * @param nextTop 下一位置最上方坐标
	 * @return 返回选中填充的视图
	 */
	private View fillDown(int position, int nextTop) {
		View selectedView = null;

		int end = (getBottom() - getTop());
		if ((getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
			end -= getPaddingBottom();
		}

		while (nextTop < end && position < mItemCount) {
			boolean selected = position == mIndex;
			nextTop = mAdapter.getItem(position).getTop();
			View child = makeAndAddView(position, nextTop, true, mAdapter.getItem(position).getLeft(), selected);

			if (selected) {
				selectedView = child;
			}
			position++;
		}

		return selectedView;
	}

	/**
	 * 初始化视图
	 * @param position
	 * @param y
	 * @param flow
	 * @param childrenLeft
	 * @param selected
	 * @return
	 */
	private View makeAndAddView(int position, int y, boolean flow, int childrenLeft, boolean selected) {
		View child;

		if (!mDataChanged) {
			child = mRecycler.getActiveView(position);
			if (child != null) {
				setupChild(child, position, y, flow, childrenLeft, selected, true);
				return child;
			}
		}

		child = obtainView(position, mIsScrap);
		setupChild(child, position, y, flow, childrenLeft, selected, mIsScrap[0]);

		return child;
	}

    @Override
    public FocusRectParams getFocusParams() {
        return super.getFocusParams();
    }

    @Override
    public FocusRectParams getFocusRectParams() {
        return super.getFocusRectParams();
    }

    /**
	 * 测量和布局视图，并将视图添加到父视图
	 * @param child
	 * @param position
	 * @param y
	 * @param flowDown
	 * @param childrenLeft
	 * @param selected
	 * @param recycled
	 */
	private void setupChild(View child, int position, int y, boolean flowDown, int childrenLeft, boolean selected, boolean recycled) {
		final boolean isSelected = selected && shouldShowSelector();
		final boolean updateChildSelected = isSelected != child.isSelected();
		final boolean needToMeasure = !recycled || updateChildSelected || child.isLayoutRequested();

		int lastWidth = 0;
		int lastHeight = 0;
		LayoutParams p = (LayoutParams) child.getLayoutParams();
		if (p == null) {
			p = generateDefaultLayoutParams();
		} else {
			lastWidth = p.width;
			lastHeight = p.height;
		}
		p.width = mAdapter.getItem(position).getWidth();
		p.height = mAdapter.getItem(position).getHeight();
		p.viewType = mAdapter.getItemViewType(position);
		p.itemId = mAdapter.getItemId(position);

        if(DEBUG){
            Log.d(TAG, "mIndex = " + position + " recycled = " + recycled);
        }
        if(recycled){
            attachViewToParent(child, flowDown ? -1 : 0, p);
        } else {
            addViewInLayout(child, flowDown ? -1 : 0, p);
        }

		if (updateChildSelected) {
			child.setSelected(isSelected);
		}

		if (needToMeasure || (p.width != lastWidth || p.height != lastHeight)) {
			int childWidthSpec = View.MeasureSpec.makeMeasureSpec(p.width, View.MeasureSpec.EXACTLY);
			int childHeightSpec = View.MeasureSpec.makeMeasureSpec(p.height, View.MeasureSpec.EXACTLY);
			child.measure(childWidthSpec, childHeightSpec);
		} else {
			cleanupLayoutState(child);
		}

		final int w = child.getMeasuredWidth();
		final int h = child.getMeasuredHeight();
		final int childTop = flowDown ? y : y - h;

		if (needToMeasure || (p.width != lastWidth || p.height != lastHeight)) {
			final int childRight = childrenLeft + w;
			final int childBottom = childTop + h;
			child.layout(childrenLeft, childTop, childRight, childBottom);
		} else {
			child.offsetLeftAndRight(childrenLeft - child.getLeft());
			child.offsetTopAndBottom(childTop - child.getTop());
		}

		if (!child.isDrawingCacheEnabled()) {
			child.setDrawingCacheEnabled(true);
		}

		if (recycled && (((LayoutParams) child.getLayoutParams()).scrappedFromPosition) != position) {
			child.jumpDrawablesToCurrentState();
		}
	}

	boolean shouldShowSelector() {
		return hasFocus() && !isInTouchMode();
	}

	protected View obtainView(int position, boolean[] isScrap) {
		isScrap[0] = false;
		View scrapView;

		scrapView = mRecycler.getTransientStateView(position);
		if (scrapView != null) {
			return scrapView;
		}

		scrapView = mRecycler.getScrapView(position);
		if (DEBUG) {
			Log.d(TAG, "obtainView->getScrapView position = " + position + " scrapView=" + scrapView);
		}

		View child;
		if (scrapView != null) {
			child = mAdapter.getView(position, scrapView, this);

			if (child != scrapView) {
				mRecycler.addScrapView(scrapView, position);
			} else {
				isScrap[0] = true;
			}
		} else {
			child = mAdapter.getView(position, null, this);
		}

		if (mAdapterHasStableIds) {
			final ViewGroup.LayoutParams vlp = child.getLayoutParams();
			LayoutParams lp;
			if (vlp == null) {
				lp = (LayoutParams) generateDefaultLayoutParams();
			} else if (!checkLayoutParams(vlp)) {
				lp = (LayoutParams) generateLayoutParams(vlp);
			} else {
				lp = (LayoutParams) vlp;
			}
			lp.width = mAdapter.getItem(position).getWidth();
			lp.height = mAdapter.getItem(position).getHeight();
			lp.itemId = mAdapter.getItemId(position);
			lp.viewType = mAdapter.getItemViewType(position);
			child.setLayoutParams(lp);
		}
		return child;
	}

	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	/**
	 * 设置布局适配器
	 * @param adapter
	 */
	public void setAdapter(LocationAdapter adapter) {
		if (null != mAdapter) {
			mAdapter.unregisterDataSetObserver(mDataSetObserver);
			resetLocation();
		}

		mRecycler.clear();
		mAdapter = adapter;

		if (mAdapter != null) {
			mAdapterHasStableIds = mAdapter.hasStableIds();
			mItemCount = mAdapter.getCount();
			mDataSetObserver = new AdapterDataSetObserver();
			mAdapter.registerDataSetObserver(mDataSetObserver);
			mRecycler.setViewTypeCount(mAdapter.getViewTypeCount());
		} else {
			resetLocation();
		}

		setNeedInitNode(true);
		requestLayout();
	}

	private void resetLocation() {
		mDataChanged = false;
		removeAllViewsInLayout();
		mOldSelectedPosition = mIndex;
		mOldItemCount = mItemCount;
		mItemCount = 0;
	}

	private void notifyDataSetChanged() {
		mDataChanged = true;
		mItemCount = mAdapter.getCount();
		setNeedInitNode(true);
		requestLayout();
	}

	protected int getGroupFlags() {
		try {
			Class<?> c = Class.forName("android.view.ViewGroup");
			Field flags = c.getDeclaredField("mGroupFlags");
			flags.setAccessible(true);
			return flags.getInt(this);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * 观察者
	 * @author quanqing.hqq
	 *
	 */
	class AdapterDataSetObserver extends DataSetObserver {
		@Override
		public void onChanged() {
			notifyDataSetChanged();
		}

		@Override
		public void onInvalidated() {
			invalidate();
		}
	}

	/**
	 * 垃圾回收机制
	 * @author quanqing.hqq
	 *
	 */
	public class RecycleBin {
		private RecyclerListener mRecyclerListener;
		private int mFirstActivePosition;// 第一个可见的视图位置
		private View[] mActiveViews = new View[0];// 可见视图数组
		private ArrayList<View>[] mScrapViews;// 不同类型视图垃圾回收池
		private int mViewTypeCount;// 视图类型
		private ArrayList<View> mCurrentScrap;
		private ArrayList<View> mSkippedScrap;
		private SparseArray<View> mTransientStateViews;

		/**
		 * 设置ViewTypeCount，然后初始化类成员变量
		 * 
		 * @param viewTypeCount
		 *            视图类型个数
		 */
		public void setViewTypeCount(int viewTypeCount) {
			if (viewTypeCount < 1) {
				throw new IllegalArgumentException("Can't have a viewTypeCount < 1");
			}

			@SuppressWarnings("unchecked")
			ArrayList<View>[] scrapViews = new ArrayList[viewTypeCount];
			for (int i = 0; i < viewTypeCount; i++) {
				scrapViews[i] = new ArrayList<View>();
			}
			mViewTypeCount = viewTypeCount;
			mCurrentScrap = scrapViews[0];
			mScrapViews = scrapViews;
		}

		/**
		 * 将mScrapView中回收回来的View设置一样标志，在下次被复用时，告诉ViewRoot重新layout该view
		 */
		public void markChildrenDirty() {
			if (mViewTypeCount == 1) {
				final ArrayList<View> scrap = mCurrentScrap;
				final int scrapCount = scrap.size();
				for (int i = 0; i < scrapCount; i++) {
					scrap.get(i).forceLayout();
				}
			} else {
				final int typeCount = mViewTypeCount;
				for (int i = 0; i < typeCount; i++) {
					final ArrayList<View> scrap = mScrapViews[i];
					final int scrapCount = scrap.size();
					for (int j = 0; j < scrapCount; j++) {
						scrap.get(j).forceLayout();
					}
				}
			}
			if (mTransientStateViews != null) {
				final int count = mTransientStateViews.size();
				for (int i = 0; i < count; i++) {
					mTransientStateViews.valueAt(i).forceLayout();
				}
			}
		}

		/**
		 * 判断给定的view的viewType指明是否可以回收回, 这里默认所有的视图都可以回收。除非设计header和footer
		 * 
		 * @param viewType
		 *            视图类型
		 * @return 可以回收的视图类型返回真
		 */
		public boolean shouldRecycleViewType(int viewType) {
			return viewType >= 0;
		}

		/**
		 * 清理ScrapView中的View，并将这些View从窗口中Detach
		 */
		void clear() {
			if (mViewTypeCount == 1) {
				final ArrayList<View> scrap = mCurrentScrap;
				final int scrapCount = scrap.size();
				for (int i = 0; i < scrapCount; i++) {
					removeDetachedView(scrap.remove(scrapCount - 1 - i), false);
				}
			} else {
				final int typeCount = mViewTypeCount;
				for (int i = 0; i < typeCount; i++) {
					final ArrayList<View> scrap = mScrapViews[i];
					final int scrapCount = scrap.size();
					for (int j = 0; j < scrapCount; j++) {
						removeDetachedView(scrap.remove(scrapCount - 1 - j), false);
					}
				}
			}
			clearTransientStateViews();
		}

		/**
		 * 填充mActiveView数组。当Adapter中的数据个数未发生变化时，此时用户可能只是滚动，或点击等操作，
		 * 视图中item的个数会发生变化，因此，需要将可视的item加入到mActiveView中来管理
		 * 
		 * @param childCount
		 *            child数量
		 * @param firstActivePosition
		 *            第一个可见视图位置
		 */
		public void fillActiveViews(int childCount, int firstActivePosition) {
			if (mActiveViews.length < childCount) {
				mActiveViews = new View[childCount];
			}
			mFirstActivePosition = firstActivePosition;

			final View[] activeViews = mActiveViews;
			for (int i = 0; i < childCount; i++) {
				View child = getChildAt(i);
				LayoutParams lp = (LayoutParams) child.getLayoutParams();
				if (lp != null) {
					activeViews[i] = child;
				}
			}
		}

		/**
		 * mFirstActivePosition是当前可视区域第一个视图的下标值，对应在adapter中的绝对值，
		 * 如果找到，则返回找到的View，并将mActiveView对应的位置设置为null
		 * 
		 * @param position
		 *            adpater中的绝对下标值
		 * @return 返回匹配的视图，如果没有则为Null
		 */
		View getActiveView(int position) {
			int index = position - mFirstActivePosition;
			final View[] activeViews = mActiveViews;
			if (index >= 0 && index < activeViews.length) {
				final View match = activeViews[index];
				activeViews[index] = null;
				return match;
			}
			return null;
		}

		View getTransientStateView(int position) {
			if (mTransientStateViews == null) {
				return null;
			}
			final int index = mTransientStateViews.indexOfKey(position);
			if (index < 0) {
				return null;
			}
			final View result = mTransientStateViews.valueAt(index);
			mTransientStateViews.removeAt(index);
			return result;
		}

		void clearTransientStateViews() {
			if (mTransientStateViews != null) {
				mTransientStateViews.clear();
			}
		}

		View getScrapView(int position) {
			if (mViewTypeCount == 1) {
				return retrieveFromScrap(mCurrentScrap, position);
			} else {
				int whichScrap = mAdapter.getItemViewType(position);
				if (whichScrap >= 0 && whichScrap < mScrapViews.length) {
					return retrieveFromScrap(mScrapViews[whichScrap], position);
				}
			}
			return null;
		}

		public void addScrapView(View scrap, int position) {
			LayoutParams lp = (LayoutParams) scrap.getLayoutParams();
			if (lp == null) {
				return;
			}

			lp.scrappedFromPosition = position;
			int viewType = lp.viewType;
			final boolean scrapHasTransientState = hasTransientState(scrap);
			if (!shouldRecycleViewType(viewType) || scrapHasTransientState) {
				if (scrapHasTransientState) {
					if (mSkippedScrap == null) {
						mSkippedScrap = new ArrayList<View>();
					}
					mSkippedScrap.add(scrap);
				}
				if (scrapHasTransientState) {
					initTransientStateViewsIfNeed();
					// scrap.dispatchStartTemporaryDetach();
					try {
						ReflectUtils.invokeMethod(scrap, "dispatchStartTemporaryDetach", new Object[0]);
					} catch (Exception e) {
						e.printStackTrace();
					}
					mTransientStateViews.put(position, scrap);
				}
				return;
			}

			// scrap.dispatchStartTemporaryDetach();
			try {
				ReflectUtils.invokeMethod(scrap, "dispatchStartTemporaryDetach", new Object[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (mViewTypeCount == 1) {
				mCurrentScrap.add(scrap);
			} else {
				mScrapViews[viewType].add(scrap);
			}

			scrap.setAccessibilityDelegate(null);
			if (mRecyclerListener != null) {
				mRecyclerListener.onMovedToScrapHeap(scrap);
			}
		}

		public void removeSkippedScrap() {
			if (mSkippedScrap == null) {
				return;
			}
			final int count = mSkippedScrap.size();
			for (int i = 0; i < count; i++) {
				removeDetachedView(mSkippedScrap.get(i), false);
			}
			mSkippedScrap.clear();
		}

		/**
		 * 将mActiveView中未使用的view回收
		 */
		public void scrapActiveViews() {
			final View[] activeViews = mActiveViews;
			final boolean hasListener = mRecyclerListener != null;
			final boolean multipleScraps = mViewTypeCount > 1;

			ArrayList<View> scrapViews = mCurrentScrap;
			final int count = activeViews.length;
			for (int i = count - 1; i >= 0; i--) {
				final View victim = activeViews[i];
				if (victim != null) {
					final LayoutParams lp = (LayoutParams) victim.getLayoutParams();
					int whichScrap = lp.viewType;

					activeViews[i] = null;

					final boolean scrapHasTransientState = hasTransientState(victim);
					if (!shouldRecycleViewType(whichScrap) || scrapHasTransientState) {
						if (scrapHasTransientState) {
							removeDetachedView(victim, false);
						}
						if (scrapHasTransientState) {
							initTransientStateViewsIfNeed();
							mTransientStateViews.put(mFirstActivePosition + i, victim);
						}
						continue;
					}

					if (multipleScraps) {
						scrapViews = mScrapViews[whichScrap];
					}

					// victim.dispatchStartTemporaryDetach();
					try {
						ReflectUtils.invokeMethod(victim, "dispatchStartTemporaryDetach", new Object[] {});
					} catch (Exception e) {
						e.printStackTrace();
					}

					lp.scrappedFromPosition = mFirstActivePosition + i;
					scrapViews.add(victim);

					victim.setAccessibilityDelegate(null);
					if (hasListener) {
						mRecyclerListener.onMovedToScrapHeap(victim);
					}
				}
			}

			pruneScrapViews();
		}

		/**
		 * mScrapView中每个ScrapView数组大小不应该超过mActiveView的大小，如果超过，
		 * 系统认为程序并没有复用convertView，
		 * 而是每次都是创建一个新的view，为了避免产生大量的闲置内存且增加OOM的风险，系统会在每次回收后，去检查一下，
		 * 将超过的部分释放掉，节约内存降低OOM风险
		 */
		private void pruneScrapViews() {
			final int maxViews = mActiveViews.length;
			final int viewTypeCount = mViewTypeCount;
			final ArrayList<View>[] scrapViews = mScrapViews;
			for (int i = 0; i < viewTypeCount; ++i) {
				final ArrayList<View> scrapPile = scrapViews[i];
				int size = scrapPile.size();
				final int extras = size - maxViews;
				size--;
				for (int j = 0; j < extras; j++) {
					removeDetachedView(scrapPile.remove(size--), false);
				}
			}

			if (mTransientStateViews != null) {
				for (int i = 0; i < mTransientStateViews.size(); i++) {
					final View v = mTransientStateViews.valueAt(i);
					if (!hasTransientState(v)) {
						mTransientStateViews.removeAt(i);
						i--;
					}
				}
			}
		}

		private void initTransientStateViewsIfNeed() {
			if (mTransientStateViews == null) {
				mTransientStateViews = new SparseArray<View>();
			}
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mRecycler.clear();
	}

	static boolean hasTransientState(View view) {
		try {
			return (Boolean) ReflectUtils.invokeMethod(view, "hasTransientState", new Object[] {});
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 根据position，从mScrapView中找： 1. 如果有view.scrappedFromPosition =
	 * position的，直接返回该view； 2. 否则返回mScrapView中最后一个； 3. 如果缓存中没有view，则返回null；
	 * 
	 * @param scrapViews
	 *            视图垃圾回收池
	 * @param position
	 *            在视图列表中的位置
	 * @return 返回匹配的视图
	 */
	static View retrieveFromScrap(ArrayList<View> scrapViews, int position) {
		int size = scrapViews.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				View view = scrapViews.get(i);
				int fromPosition = ((LayoutParams) view.getLayoutParams()).scrappedFromPosition;
				if (fromPosition == position) {
					scrapViews.remove(i);
					return view;
				}
			}
			return scrapViews.remove(size - 1);
		} else {
			return null;
		}
	}

	public static interface RecyclerListener {
		void onMovedToScrapHeap(View view);
	}

	/**
	 * 视图布局参数
	 * @author quanqing.hqq
	 *
	 */
	public static class LayoutParams extends ViewGroup.LayoutParams {
		int viewType;// 视图类型
		int scrappedFromPosition;// 回收的索引
		long itemId = -1;

		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
		}

		public LayoutParams(int w, int h) {
			super(w, h);
		}

		public LayoutParams(int w, int h, int viewType) {
			super(w, h);
			this.viewType = viewType;
		}

		public LayoutParams(ViewGroup.LayoutParams source) {
			super(source);
		}
	}

    public interface OnLocationItemClickListener {
        void onItemClick(ViewGroup parent, View view, int position, long id);
    }

    public void setOnLocationItemClickListener(OnLocationItemClickListener listener){
        mOnItemClickListener = listener;
    }

    public boolean performItemClick(View view, int position, long id) {
        if (mDeep != null) {
            mDeep.onItemClick();
            return true;
        }

        if (mOnItemClickListener != null) {
            this.playSoundEffect(SoundEffectConstants.CLICK);
            if (view != null) {
                view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
            }
            mOnItemClickListener.onItemClick(this, view, position, id);
            return true;
        } else if (getOnItemClickListener() != null) {
            this.playSoundEffect(SoundEffectConstants.CLICK);
            if (view != null) {
                view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
            }
            performItemClick();
        }

        return false;
    }
    
    private boolean isFocusedOrSelected(){
        return isFocused() || isSelected();
    }

    @Override
    public boolean isFocused() {
        return super.isFocused() || hasFocus() || hasDeepFocus();
    }

	@Override
	public Rect getClipFocusRect() {
		if (mDeep != null && isFocusedOrSelected() && isScrolling()){
			Rect clipFocusRect = new Rect();
			Rect deepRect = mDeep.getClipFocusRect();
			if (deepRect != null){
				clipFocusRect.set(deepRect);
				offsetDescendantRectToMyCoords((View)mDeep, clipFocusRect);
			}
			return clipFocusRect;
		}
		return null;
	}
}
