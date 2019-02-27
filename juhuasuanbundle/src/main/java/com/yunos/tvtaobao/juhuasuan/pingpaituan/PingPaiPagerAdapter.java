package com.yunos.tvtaobao.juhuasuan.pingpaituan;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.AppHandler;
import com.yunos.tvtaobao.juhuasuan.activity.BrandHomeActivity.Item_Info;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.BrandMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CountList;

import java.util.ArrayList;
import java.util.List;

public class PingPaiPagerAdapter extends PagerAdapter {

    private final String TAG = "GoodListPagerAdapter";

    private Activity mActivity = null;

    private Context mContext = null;
    private int mTotalCount = 1;

    private OnPingpaiItemHandleListener mOnItemHandleListener = null;

    public CountList<BrandMO> mListData = null;

    private Item_Info mItem_Info = null;

    private SparseArray<PingPaiFocusedFrameLayout> mLoadImageClassficationFocusedRelativeLayout = null;
    private List<PingPaiFocusedFrameLayout> mRecycleClassficationFocusedRelativeLayout = null;
    private SparseArray<PingPaiFocusedFrameLayout> mActivateClassficationFocusedRelativeLayout = null;

    private int mOldSelectPage = -1;
    private int mOldSelectIndex = -1;

    private boolean mFristInstantiate = true;

    //    private Thread                                 mRecycleThread                               = null;
    //    private Thread                                 mActivateThread                              = null;

    private Thread mRunThread = null;
    private Handler mLoadBitmapHandler = null;

    private Thread mRunDispalyThread = null;
    private Handler mDispalyHandler = null;

    private boolean mFirstLoadBitmap = true;

    private PingPaiPagerAdapterHandle mMainHandler = new PingPaiPagerAdapterHandle(this);

    private static final class PingPaiPagerAdapterHandle extends AppHandler<PingPaiPagerAdapter> {

        public PingPaiPagerAdapterHandle(PingPaiPagerAdapter t) {
            super(t);
        }

    }

    public PingPaiPagerAdapter(Context context) {
        mContext = context;

        // 建立视图回收队列
        mRecycleClassficationFocusedRelativeLayout = new ArrayList<PingPaiFocusedFrameLayout>();
        mRecycleClassficationFocusedRelativeLayout.clear();

        // 建立视图激活队列
        mActivateClassficationFocusedRelativeLayout = new SparseArray<PingPaiFocusedFrameLayout>();
        mActivateClassficationFocusedRelativeLayout.clear();

        // 建立要刷新数据的队列
        mLoadImageClassficationFocusedRelativeLayout = new SparseArray<PingPaiFocusedFrameLayout>();
        mLoadImageClassficationFocusedRelativeLayout.clear();

        mFirstLoadBitmap = true;

        onCreatRunLoadImageHandle();
        onCreatRunDisplayBitmapHandle();
    }

    //设置监听
    public void onSetOnItemHandleListener(OnPingpaiItemHandleListener l) {
        mOnItemHandleListener = l;
    }

    public void onSetViewPagerInfo(Item_Info info) {
        mItem_Info = info;
        if (mItem_Info != null) {
            mTotalCount = mItem_Info.mTotalPage;
        }
    }

    // 设置List数据
    public void setListData(CountList<BrandMO> listData) {
        mListData = listData;
    }

    public CountList<BrandMO> getListData() {
        return mListData;
    }

    @Override
    public int getCount() {
        if (mItem_Info != null) {
            mTotalCount = mItem_Info.mTotalPage;
        }
        return mTotalCount;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        PingPaiFocusedFrameLayout classficationFocusedRelativeLayout = (PingPaiFocusedFrameLayout) object;
        if (classficationFocusedRelativeLayout != null) {

            // 移除View
            container.removeView(classficationFocusedRelativeLayout);

            //释放资源
            classficationFocusedRelativeLayout.onRecycleRes();

            if (mOnItemHandleListener != null) {
                mOnItemHandleListener.onRemoveItemOfHandle(container, null, position);
            }

            // 添加到回收队列中
            if (mRecycleClassficationFocusedRelativeLayout != null) {
                mRecycleClassficationFocusedRelativeLayout.add(classficationFocusedRelativeLayout);
            }

            // 从视图激活队列中移除
            if (mActivateClassficationFocusedRelativeLayout != null) {
                mActivateClassficationFocusedRelativeLayout.delete(position);
            }

            // 从数据刷新队列中移除
            if (mLoadImageClassficationFocusedRelativeLayout != null) {
                mLoadImageClassficationFocusedRelativeLayout.delete(position);
            }
        }

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        AppDebug.i(TAG, "container = " + container);
        AppDebug.i(TAG, "position = " + position);

        PingPaiFocusedFrameLayout classficationFocusedRelativeLayout = null;

        boolean isLatestPagee = isLatestPage(position);

        if (mRecycleClassficationFocusedRelativeLayout != null) {

            // 如果视图回收队列中，有回收的视图，则从队列中取
            int count = mRecycleClassficationFocusedRelativeLayout.size();
            if (count > 0 && !isLatestPagee) {
                classficationFocusedRelativeLayout = mRecycleClassficationFocusedRelativeLayout.get(count - 1);
                if (classficationFocusedRelativeLayout != null
                        && classficationFocusedRelativeLayout.getPageCount() != mItem_Info.PAGE_COUNT) {
                    classficationFocusedRelativeLayout = null;
                }
            }
        }

        if (classficationFocusedRelativeLayout == null) {

            // 回收队列中没有可用的视图，则新建

            if (isLatestPagee) {
                classficationFocusedRelativeLayout = new PingPaiFocusedFrameLayout(mContext, getLatestPageCount());
            } else {
                classficationFocusedRelativeLayout = new PingPaiFocusedFrameLayout(mContext);
            }

            classficationFocusedRelativeLayout.onSetOnItemHandleListener(mOnItemHandleListener);
            classficationFocusedRelativeLayout.onSetActivity(mActivity);

        } else {

            // 回收队列中，有回收视图，则从回收队列中移除
            if (mRecycleClassficationFocusedRelativeLayout != null) {
                mRecycleClassficationFocusedRelativeLayout.remove(classficationFocusedRelativeLayout);
            }
        }

        // 设置新页的参数
        classficationFocusedRelativeLayout.onSetPagerAdapter(this);
        classficationFocusedRelativeLayout.onSetPageIndex(position);

        //        classficationFocusedRelativeLayout.onRefreshItem(mListData, position);

        // 如果是第一次添加页，则立即刷新数据
        if ((position < 2) && (mFirstLoadBitmap)) {
            classficationFocusedRelativeLayout.onRefreshItem(mListData, position);
        } else {
            // 否则放入后期的请求队列中
            mFirstLoadBitmap = false;
            mLoadImageClassficationFocusedRelativeLayout.put(position, classficationFocusedRelativeLayout);
        }

        container.addView(classficationFocusedRelativeLayout);

        if (mActivateClassficationFocusedRelativeLayout != null) {
            mActivateClassficationFocusedRelativeLayout.put(position, classficationFocusedRelativeLayout);
        }

        if (mOnItemHandleListener != null) {
            mOnItemHandleListener.onInstantiateItemOfHandle(container, null, position);
        }

        if (mFristInstantiate) {
            mFristInstantiate = false;
            classficationFocusedRelativeLayout.onSetSelectView();
        }

        return classficationFocusedRelativeLayout;

    }

    public void onSetActivity(Activity activity) {
        mActivity = activity;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.view.PagerAdapter#notifyDataSetChanged()
     */
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        onCheckBitmapAndRefreshItem();
    }

    /**
     * 判断是否是最后一页
     * @return
     */
    public boolean isLatestPage(int position) {
        int pageNum = 0;
        if (mListData != null) {
            int size = mListData.size();
            if (size % Item_Info.PAGE_COUNT == 0) {
                pageNum = size / Item_Info.PAGE_COUNT;
            } else {
                pageNum = size / Item_Info.PAGE_COUNT + 1;
            }
            if (position + 1 == pageNum) {
                return true;
            }
        }
        return false;
    }

    public int getLatestPageCount() {
        int pageCount = Item_Info.PAGE_COUNT;
        if (mListData != null) {
            int size = mListData.size();
            if (size % Item_Info.PAGE_COUNT != 0) {
                pageCount = size % Item_Info.PAGE_COUNT;
            }
        }
        return pageCount;
    }

    /**
     * 显示当前页时，检查当前条目是否显示图片，若没有，则更新
     * @param position
     */
    public void upDataBitmapInfo(int position) {
        if (mActivateClassficationFocusedRelativeLayout == null)
            return;

        PingPaiFocusedFrameLayout goodlistF = mActivateClassficationFocusedRelativeLayout.get(position);
        if (goodlistF != null) {
            goodlistF.onRefreshItem(mListData, position);
        }
    }

    /**
     * 为了重新设置焦点，弥补FocusedFrameLayout自定义控件中，对焦点处理的不足
     * @param gainFocus
     * @param currentPage
     */
    public void upDateFoucs(boolean gainFocus, int currentPage) {

        if (mActivateClassficationFocusedRelativeLayout == null)
            return;

        if (!gainFocus) {
            mOldSelectPage = currentPage;
            PingPaiFocusedFrameLayout goodlistF = mActivateClassficationFocusedRelativeLayout.get(currentPage);
            if (goodlistF != null) {
                mOldSelectIndex = goodlistF.getIndexOfSelectedView();
            }

        } else {
            int setIndex = 0;
            if (mOldSelectIndex > 1) {
                setIndex = 2;
            }

            if (mOldSelectPage > currentPage) {
                setIndex += 1;
            }
            PingPaiFocusedFrameLayout goodlistF = mActivateClassficationFocusedRelativeLayout.get(currentPage);
            if (goodlistF != null) {
                View view = goodlistF.getChildAt(setIndex);
                if (null != view && view.isFocusable()) {// 如果用户切换分类，第一页只有1个商品，那么会导致view的值为null
                    goodlistF.setIndexOfSelectedView(setIndex);
                }
            }
        }

    }

    /**
     * 清理资源
     */
    public void onClearAndDestroy() {
        if (mActivateClassficationFocusedRelativeLayout != null) {

            int count = mActivateClassficationFocusedRelativeLayout.size();
            for (int i = 0; i < count; i++) {
                int key = mActivateClassficationFocusedRelativeLayout.keyAt(i);
                PingPaiFocusedFrameLayout goodlistLayout = mActivateClassficationFocusedRelativeLayout.get(key);
                goodlistLayout.ClearAndDestroy();
                goodlistLayout = null;
            }
            mActivateClassficationFocusedRelativeLayout.clear();
            mActivateClassficationFocusedRelativeLayout = null;
        }

        if (mRecycleClassficationFocusedRelativeLayout != null) {

            int count = mRecycleClassficationFocusedRelativeLayout.size();
            for (int i = 0; i < count; i++) {
                PingPaiFocusedFrameLayout goodlistLayout = mRecycleClassficationFocusedRelativeLayout.get(i);
                goodlistLayout.ClearAndDestroy();
                goodlistLayout = null;
            }

            mRecycleClassficationFocusedRelativeLayout.clear();
            mRecycleClassficationFocusedRelativeLayout = null;
        }

        // 退出
        if (mLoadBitmapHandler != null) {

            mLoadBitmapHandler.getLooper().quit();
        }

        if (mDispalyHandler != null) {
            mDispalyHandler.removeCallbacksAndMessages(null);
            mDispalyHandler.getLooper().quit();
        }
    }

    public void onUpdataHandleLoading() {

        //        new Thread() {
        //
        //            public void run() {
        //
        //                if (mLoadImageClassficationFocusedRelativeLayout != null) {
        //                    int count = mLoadImageClassficationFocusedRelativeLayout.size();
        //
        //                    for (int i = 0; i < count; i++) {
        //                        final int key = mLoadImageClassficationFocusedRelativeLayout.keyAt(i);
        //                        final PingPaiFocusedFrameLayout goodlistLayout = mLoadImageClassficationFocusedRelativeLayout.get(key);
        //
        //                        mMainHandler.post(new Runnable() {
        //
        //                            @Override
        //                            public void run() {
        //                                // TODO Auto-generated method stub
        //                                if (goodlistLayout != null) {
        //                                    goodlistLayout.onRefreshItem(mListData, key);
        //                                }
        //                            }
        //
        //                        });
        //
        ////                        mLoadImageClassficationFocusedRelativeLayout.remove(key);
        //                    }
        //                     
        //                    mLoadImageClassficationFocusedRelativeLayout.clear();
        //                }
        //
        //            }
        //        }.start();

        //        if(mRunThread != null)
        //        {
        //            mRunThread.start();
        //        }

        if (mLoadBitmapHandler != null) {
            mLoadBitmapHandler.postDelayed(new LoadBitmapRun(), 100);
        }

    }

    /**
     * 检查刷新队列中需要刷新的信息和图片
     */
    public void onCheckBitmapAndRefreshItem() {

        if (mLoadImageClassficationFocusedRelativeLayout != null) {
            int count = mLoadImageClassficationFocusedRelativeLayout.size();

            for (int i = 0; i < count; i++) {
                final int key = mLoadImageClassficationFocusedRelativeLayout.keyAt(i);
                final PingPaiFocusedFrameLayout goodlistLayout = mLoadImageClassficationFocusedRelativeLayout.get(key);

                mMainHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (goodlistLayout != null) {
                            goodlistLayout.onRefreshItem(mListData, key);
                        }
                    }

                }, 100);
            }

            mLoadImageClassficationFocusedRelativeLayout.clear();
        }

    }

    /**
     * 创建图片加载线程，并进入循环状态
     */
    public void onCreatRunLoadImageHandle() {

        mRunThread = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Looper.prepare();

                mLoadBitmapHandler = new Handler() {

                    public void handleMessage(Message msg) {
                        // 处理图片加载事情
                    }
                };

                Looper.loop();
            }
        });

        mRunThread.start();

    };

    /**
     * 创建图片处理线程，并进入循环状态
     */
    public void onCreatRunDisplayBitmapHandle() {

        mRunDispalyThread = new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                Looper.prepare();

                mDispalyHandler = new Handler() {

                    public void handleMessage(Message msg) {

                    }
                };

                Looper.loop();
            }
        });

        mRunDispalyThread.start();

    };

    /**
     * 获取显示图片处理的Handler
     * @return
     */
    public Handler onGetDispalyHandler() {
        return mDispalyHandler;
    }

    public class LoadBitmapRun implements Runnable {

        @Override
        public void run() {
            onCheckBitmapAndRefreshItem();
        }

    };

}
