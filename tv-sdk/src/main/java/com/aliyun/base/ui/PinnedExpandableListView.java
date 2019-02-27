package com.aliyun.base.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

/**
 *  @author douzifly
 *  
 *  @Date 2011-9-13
 */

/**
 * �ɹ̶������ExpandableListView
 * 
 * @author douzifly
 * @date 2011-9-13
 */
public class PinnedExpandableListView extends ExpandableListView implements OnScrollListener {

	/**
	 * ��ListView��Adapter����ʵ�ָýӿ�
	 * 
	 * @author LiuXiaoyuan@hh.com.cn
	 * @date 2011-9-13
	 */
	public interface PinnedExpandableListViewAdapter {

		/**
		 * �̶�����״̬�����ɼ�
		 */
		public static final int PINNED_HEADER_GONE = 0;
		/**
		 * �̶�����״̬���ɼ�
		 */
		public static final int PINNED_HEADER_VISIBLE = 1;
		/**
		 * �̶�����״̬������������
		 */
		public static final int PINNED_HEADER_PUSHED_UP = 2;

		public int getPinnedHeaderState(int groupPosition, int childPosition);

		public void configurePinnedHeader(View header, int groupPosition, int childPosition, int alpha);

	}

	private static final int MAX_ALPHA = 255;

	private PinnedExpandableListViewAdapter mAdapter;
	private View mHeaderView;
	private boolean mHeaderVisible;
	private int mHeaderViewWidth;
	private int mHeaderViewHeight;

	private OnClickListener mPinnedHeaderClickLisenter;

	public void setOnPinnedHeaderClickLisenter(OnClickListener listener) {
		mPinnedHeaderClickLisenter = listener;
	}

	public PinnedExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnScrollListener(this);
	}

	public void setPinnedHeaderView(View view) {
		mHeaderView = view;
		if (mHeaderView != null) {
			setFadingEdgeLength(0);
		}
		requestLayout();
	}

	@Override
	public void setAdapter(ExpandableListAdapter adapter) {
		super.setAdapter(adapter);
		mAdapter = (PinnedExpandableListViewAdapter) adapter;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mHeaderView != null) {
			measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
			mHeaderViewWidth = mHeaderView.getMeasuredWidth();
			mHeaderViewHeight = mHeaderView.getMeasuredHeight();
		}
	}

	private int mOldState = -1;

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		final long flatPostion = getExpandableListPosition(getFirstVisiblePosition());
		final int groupPos = ExpandableListView.getPackedPositionGroup(flatPostion);
		final int childPos = ExpandableListView.getPackedPositionChild(flatPostion);
		int state = mAdapter.getPinnedHeaderState(groupPos, childPos);// ֻ����״̬�ı�ʱ��layout������൱��Ҫ����Ȼ���ܵ�����ͼ���ϵ�ˢ��
		if (mHeaderView != null && mAdapter != null && state != mOldState) {
			mOldState = state;
			mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
		}

		configureHeaderView(groupPos, childPos);
	}

	public void configureHeaderView(int groupPosition, int childPosition) {
		if (mHeaderView == null || mAdapter == null) {
			return;
		}

		final int state = mAdapter.getPinnedHeaderState(groupPosition, childPosition);
		switch (state) {
		case PinnedExpandableListViewAdapter.PINNED_HEADER_GONE: {
			mHeaderVisible = false;
			break;
		}

		case PinnedExpandableListViewAdapter.PINNED_HEADER_VISIBLE: {
			mAdapter.configurePinnedHeader(mHeaderView, groupPosition, childPosition, MAX_ALPHA);
			if (mHeaderView.getTop() != 0) {
				mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
			}
			mHeaderVisible = true;
			break;
		}

		case PinnedExpandableListViewAdapter.PINNED_HEADER_PUSHED_UP: {
			final View firstView = getChildAt(0);
			if (firstView == null) {
				break;
			}
			int bottom = firstView.getBottom();
			int headerHeight = mHeaderView.getHeight();
			int y;
			int alpha;
			if (bottom < headerHeight) {
				y = bottom - headerHeight;
				alpha = MAX_ALPHA * (headerHeight + y) / headerHeight;
			} else {
				y = 0;
				alpha = MAX_ALPHA;
			}
			mAdapter.configurePinnedHeader(mHeaderView, groupPosition, childPosition, alpha);
			if (mHeaderView.getTop() != y) {
				mHeaderView.layout(0, y, mHeaderViewWidth, mHeaderViewHeight + y);
			}
			mHeaderVisible = true;
			break;
		}

		default:
			break;
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);// ����HeaderView��û����ӵ�ExpandableListView���ӿؼ��У�����Ҫdraw��
		if (mHeaderVisible) {
			drawChild(canvas, mHeaderView, getDrawingTime());
		}
	}

	private float mDownX;
	private float mDownY;

	private static final float FINGER_WIDTH = 20;

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mHeaderVisible) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mDownX = ev.getX();
				mDownY = ev.getY();
				if (mDownX <= mHeaderViewWidth && mDownY <= mHeaderViewHeight) {
					return true;
				}
				break;
			case MotionEvent.ACTION_UP:
				float x = ev.getX();
				float y = ev.getY();

				float offsetX = Math.abs(x - mDownX);
				float offsetY = Math.abs(y - mDownY);
				// ����ڹ̶������ڵ���ˣ���ô�����¼�
				if (x <= mHeaderViewWidth && y <= mHeaderViewHeight && offsetX <= FINGER_WIDTH && offsetY <= FINGER_WIDTH) {

					if (mPinnedHeaderClickLisenter != null) {
						mPinnedHeaderClickLisenter.onClick(mHeaderView);
					}

					return true;
				}

				break;
			default:
				break;
			}
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		final long flatPos = getExpandableListPosition(firstVisibleItem);
		int groupPosition = ExpandableListView.getPackedPositionGroup(flatPos);
		int childPosition = ExpandableListView.getPackedPositionChild(flatPos);
		Log.d("TEST", "configure in onScroll");
		configureHeaderView(groupPosition, childPosition);
	}

}