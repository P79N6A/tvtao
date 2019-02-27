/**
 * $
 * PROJECT NAME: juhuasuan
 * PACKAGE NAME: com.yunos.tvtaobao.juhuasuan.activity
 * FILE NAME: BrandItemsActivity.java
 * CREATED TIME: 2015-1-13
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.juhuasuan.activity;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunos.tv.core.util.IntentDataUtil;
import com.yunos.tvtaobao.juhuasuan.R;
import com.yunos.tvtaobao.juhuasuan.adapter.BrandItemAdapter;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.BrandMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.CountList;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.ItemMO;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.Option;
import com.yunos.tvtaobao.biz.request.bo.juhuasuan.bo.enumeration.OptionType;
import com.yunos.tvtaobao.juhuasuan.clickcommon.ToItemDetail;
import com.yunos.tvtaobao.juhuasuan.common.NetWorkCheck;
import com.yunos.tvtaobao.juhuasuan.config.SystemConfig;
import com.yunos.tvtaobao.juhuasuan.request.MyBusinessRequest;
import com.yunos.tvtaobao.juhuasuan.request.listener.JuRetryRequestListener;
import com.yunos.tvtaobao.juhuasuan.util.DialogUtils;
import com.yunos.tvtaobao.juhuasuan.util.ModelTranslator;
import com.yunos.tvtaobao.juhuasuan.util.StringUtil;
import com.yunos.tvtaobao.juhuasuan.view.BrandFocusGallery;
import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.AdapterView.OnItemClickListener;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.app.widget.focus.listener.OnScrollListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.dialog.WaitProgressDialog;

import java.util.Map;

/**
 * Class Descripton.
 * @version
 * @author hanqi
 * @data 2015-1-13 上午11:10:19
 */
public class BrandDetailActivity extends JuBaseActivity {

    private final String TAG = "BrandItemsActivity";

    private ViewHolder mViewHolder;
    private String mFromCate;
    // 品牌团信息
    private BrandMO mData;
    // 商品列表
    public CountList<ItemMO> items;
    private boolean isScrolling;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewHolder = new ViewHolder();
        setContentView(mViewHolder.viewContents);
        showWaitDialog();

        mFromCate = IntentDataUtil.getString(getIntent(), "fromCate", null);
        mData = (BrandMO) getIntent().getExtras().getSerializable("data");
        if (mData == null) {
            DialogUtils.show(this, R.string.jhs_data_error, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            return;
        }
        if (!NetWorkUtil.isNetWorkAvailable()) {
            NetWorkCheck.netWorkError(this, new NetWorkCheck.NetWorkConnectedCallBack() {

                @Override
                public void connected() {
                    loadData();
                }
            });
        } else {
            loadData();
        }
        if (SystemConfig.DIPEI_BOX) {
            setBackToHome(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (null != mViewHolder) {
            mViewHolder.destroy();
        }
        super.onDestroy();
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        if (!StringUtil.isEmpty(mFromCate)) {
            p.put("fromCate", mFromCate);
        }
        if (null != mData) {
            if (!StringUtil.isEmpty(mData.getCode())) {
                p.put("brand_code", mData.getCode());
            }
            if (!StringUtil.isEmpty(mData.getName())) {
                p.put("brand_name", mData.getName());
            }
        }
        return p;
    }

    private void loadData() {
        //判断是否是包含完整数据的品牌团信息，如果完全，就直接请求商品列表，如果不完全则请求品牌团列表，匹配到对应的品牌团后请求商品列表
        if (null != mData.getName()) {
            initBrandTitleBar();
            MyBusinessRequest.getInstance().requestGetBrandItems(mData.getCode(), 0, 50,
                    new JuRetryRequestListener<CountList<ItemMO>>(this) {

                        @Override
                        public void onSuccess(BaseActivity baseActivity, CountList<ItemMO> items) {
                            if (!(baseActivity instanceof BrandDetailActivity)) {
                                return;
                            }
                            BrandDetailActivity activity = (BrandDetailActivity) baseActivity;
                            activity.items = items;
                            if (activity.items.size() < 1) {
                                activity.showNoData();
                            } else {
                                activity.createView();
                            }
                        }
                    });
        } else {
            //            JuBusinessRequest.getBusinessRequest().requestGetOption(this, OptionType.Brand.getplatformId(), null, 0,
            //                    999, new GetBranksBusinessRequestListener(this));
            MyBusinessRequest.getInstance().requestGetOption(OptionType.Brand.getplatformId(), null, 0, 999,
                    new JuRetryRequestListener<CountList<Option>>(this) {

                        @Override
                        public void onSuccess(BaseActivity baseActivity, CountList<Option> data) {
                            BrandDetailActivity activity = null;
                            if (baseActivity instanceof BrandDetailActivity) {
                                activity = (BrandDetailActivity) baseActivity;
                            }
                            if (null == activity) {
                                return;
                            }
                            if (null == data || null == activity) {
                                activity.showNoData();
                                return;
                            }
                            CountList<BrandMO> brands = ModelTranslator.translateBrand(data);
                            if (brands == null) {
                                activity.showNoData();
                            } else {
                                boolean findFlag = false;
                                for (BrandMO brand : brands) {
                                    AppDebug.i(activity.TAG,
                                            activity.TAG + ".loadData.JuRetryRequestListener.onSuccess brandCode = "
                                                    + brand.getCode());
                                    if (brand.getCode().equals(activity.mData.getCode())) {
                                        findFlag = true;
                                        activity.mData = brand;
                                        activity.loadData();
                                        break;
                                    }
                                }

                                if (!findFlag) {
                                    final BrandDetailActivity brandDetailActivity = activity;
                                    DialogUtils.show(brandDetailActivity, R.string.jhs_jump_error,
                                            new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    brandDetailActivity.finish();
                                                }
                                            });
                                }
                            }

                        }
                    });
        }
    }

    private void createView() {
        if (null == mViewHolder) {
            return;
        }
        hideWaitDialog();
        hideNoData();
        BrandItemAdapter myGalleryAdapter = new BrandItemAdapter(items, this);
        mViewHolder.itemsParentFocusPositionManager.setVisibility(View.VISIBLE);
        mViewHolder.itemsFocusGallery.setAdapter(myGalleryAdapter);
        mViewHolder.itemsFocusGallery.setSelection(getDefaultSelection());
        mViewHolder.itemsParentFocusPositionManager.requestFocus();
    }

    /**
     * 取得默认选中的index
     * @return
     */
    public int getDefaultSelection() {
        int defaultSelection = 0;
        if (null == items) {
            return defaultSelection;
        }
        int dataSize = items.size();
        if (dataSize > 3) {
            defaultSelection = Integer.MAX_VALUE / 2 - ((Integer.MAX_VALUE / 2) % dataSize);
        }
        AppDebug.i(TAG, TAG + ".getDefaultSelection defaultSelection=" + defaultSelection);
        return defaultSelection;
    }

    public void showNoData() {
        hideNoData();
        if (null != mViewHolder) {
            mViewHolder.showNoData();
        }
    }

    public void hideNoData() {
        if (null != mViewHolder) {
            mViewHolder.hideNoData();
        }
    }

    /**
     * 显示品牌团名称、折扣等信息
     */
    public void initBrandTitleBar() {
        if (null != mViewHolder) {
            mViewHolder.initBrandTitleBar();
        }
    }

    public void showWaitDialog() {
        if (null != mViewHolder) {
            mViewHolder.showWaitDialog();
        }
    }

    public void hideWaitDialog() {
        if (null != mViewHolder) {
            mViewHolder.hideWaitDialog();
        }
    }

    class ViewHolder {

        public ViewGroup viewContents;
        //品牌团名称
        public TextView brandNameTextView;
        //品牌团标题
        public TextView brandTitleTextView;
        //品牌团的折扣信息
        public TextView brandDiscounterTextView;
        //没有数据时显示的View
        public View noDataView;
        public FocusPositionManager itemsParentFocusPositionManager;
        //商品列表
        public BrandFocusGallery itemsFocusGallery;
        //加载中蒙版对话框
        public WaitProgressDialog mWaitProgressDialog;

        public ViewHolder() {
            viewContents = (ViewGroup) getLayoutInflater().inflate(R.layout.jhs_brand_detail_activity, null);
            brandNameTextView = (TextView) viewContents.findViewById(R.id.brandName);
            brandTitleTextView = (TextView) viewContents.findViewById(R.id.brandTitle);
            brandDiscounterTextView = (TextView) viewContents.findViewById(R.id.brandDiscounter);
            noDataView = viewContents.findViewById(R.id.no_data);

            itemsFocusGallery = (BrandFocusGallery) viewContents.findViewById(R.id.items);
            itemsParentFocusPositionManager = (FocusPositionManager) viewContents.findViewById(R.id.items_parent);

            initGallery();
        }

        public void showWaitDialog() {
            if (null == mWaitProgressDialog) {
                mWaitProgressDialog = new WaitProgressDialog(BrandDetailActivity.this);
            }
            mWaitProgressDialog.show();
        }

        public void hideWaitDialog() {
            if (null != mWaitProgressDialog) {
                mWaitProgressDialog.dismiss();
            }
        }

        public void initGallery() {
            itemsParentFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                    R.drawable.jhs_brand_detail_focus)));

            itemsFocusGallery.setSpacing(getResources().getDimensionPixelSize(R.dimen.dp_166));
            itemsFocusGallery.setSelectScale(1.5f);
            itemsFocusGallery.setFlingScrollFrameCount(10);
            itemsFocusGallery.setFlingScrollMaxStep(100); // 设置步长
            itemsFocusGallery.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    AppDebug.i(TAG, TAG + ".ViewHolder.initGallery position=" + position + ", id=" + id + ", view="
                            + view + ", parent=" + parent);
                    if (!isScrolling) {
                        BrandItemAdapter adapter = (BrandItemAdapter) parent.getAdapter();
                        ItemMO summary = adapter.getItem(position);
                        ToItemDetail.detail(BrandDetailActivity.this, summary.getJuId(), summary.getItemId());
                    }
                }
            });
            itemsFocusGallery.setOnScrollListener(new OnScrollListener() {

                @Override
                public void onScrollStateChanged(ViewGroup view, int scrollState) {
                    AppDebug.i(
                            TAG,
                            TAG
                                    + ".ViewHolder.initGallery.itemsFocusGallery.OnScrollListener.onScrollStateChanged scrollState="
                                    + scrollState + ", view=" + view);
                    if (view instanceof BrandFocusGallery) {
                        BrandFocusGallery gallery = (BrandFocusGallery) view;
                        BrandItemAdapter adapter = (BrandItemAdapter) gallery.getAdapter();
                        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                            if (null == adapter) {
                                return;
                            }
                            itemsParentFocusPositionManager.focusShow();
                            adapter.stopScorll();
                            isScrolling = false;
                            int childCount = gallery.getChildCount();
                            for (int i = 0; i < childCount; i++) {
                                adapter.loadImage(gallery.getChildAt(i));
                            }
                        } else {
                            itemsParentFocusPositionManager.focusHide();
                            adapter.stopLoadImage();
                            isScrolling = true;
                        }
                    }
                }

                @Override
                public void onScroll(ViewGroup view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                }
            });
        }

        public void initBrandTitleBar() {
            brandNameTextView.setText(mData.getName());
            brandTitleTextView.setText(mData.getJuSlogo());
            brandDiscounterTextView
                    .setText("限时大促");
        }

        public void showNoData() {
            if (null == noDataView) {
                return;
            }
            noDataView.setVisibility(View.VISIBLE);
        }

        public void hideNoData() {
            if (null == noDataView) {
                return;
            }
            noDataView.setVisibility(View.GONE);
        }

        public void destroy() {
            if (null != itemsFocusGallery) {
                BrandItemAdapter adapter = (BrandItemAdapter) itemsFocusGallery.getAdapter();
                if (null != adapter) {
                    adapter.onDestroy();
                }
            }
            if (null != mWaitProgressDialog) {
                hideWaitDialog();
                mWaitProgressDialog = null;
            }
        }
    }

}
