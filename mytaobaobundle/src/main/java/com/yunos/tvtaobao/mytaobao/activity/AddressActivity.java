package com.yunos.tvtaobao.mytaobao.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.AdapterView.OnItemClickListener;
import com.yunos.tv.app.widget.FlipGridView.OnFlipRunnableListener;
import com.yunos.tv.app.widget.focus.FocusFlipGridView;
import com.yunos.tv.app.widget.focus.FocusFlipGridView.OnFocusFlipGridViewListener;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.app.widget.focus.listener.ItemSelectedListener;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.Address;
import com.yunos.tvtaobao.biz.request.core.ServiceCode;
import com.yunos.tvtaobao.biz.widget.GridViewFocusPositionManager;
import com.yunos.tvtaobao.mytaobao.R;
import com.yunos.tvtaobao.mytaobao.adapter.AddressGridViewAdapter;
import com.yunos.tvtaobao.mytaobao.adapter.AddressGridViewAdapter.*;
import com.yunos.tvtaobao.mytaobao.view.ItemLayoutForAddress;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AddressActivity extends BaseActivity {

    private final String TAG = "Address";

    public Context mContext;
    private final int COLUMNS_COUNT = 3;
    private ImageView topShadowArea;
    private BusinessRequest mBusinessRequest;
    private List<Address> addresses = new LinkedList<Address>();
    private GridViewFocusPositionManager mFocusPositionManager;
    private FocusFlipGridView mGridView;
    private boolean mRefreshData;

    public AddressGridViewAdapter mAdapter;
    public int currentSelectedPosition = -1;
    private int mSelPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onKeepActivityOnlyOne(AddressActivity.class.getName());
        mContext = this;
        setContentView(R.layout.ytsdk_activity_address);
        registerLoginListener();

        // 初始化控件
        initViews();

        // 初始化网络模块
        mBusinessRequest = BusinessRequest.getBusinessRequest();

        // 请求地址
        requestAddress();
        mFocusPositionManager.requestFocus();

    }

    /**
     * 初始化控件
     */
    private void initViews() {
        mFocusPositionManager = (GridViewFocusPositionManager) this.findViewById(R.id.focus_flip_gridView);
        topShadowArea = (ImageView) findViewById(R.id.top_shadow_area);
        mGridView = (FocusFlipGridView) findViewById(R.id.gridview);
        mGridView.setClipToPadding(false);
        mFocusPositionManager.setGridView(mGridView);
        onInitFocusFlipGridView();

    }

    private void onInitFocusFlipGridView() {

        mFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                R.drawable.ytbv_common_focus)));
        mGridView.setAnimateWhenGainFocus(false, false, false, false);

        mGridView.setNumColumns(COLUMNS_COUNT);

        int VerticalSpacing = this.getResources().getDimensionPixelSize(R.dimen.dp_60);
        int horizontalSpacing = this.getResources().getDimensionPixelSize(R.dimen.dp_20);
        mGridView.setVerticalSpacing(VerticalSpacing);
        mGridView.setHorizontalSpacing(horizontalSpacing);

        mGridView.setOnFocusFlipGridViewListener(new OnFocusFlipGridViewListener() {

            @Override
            public void onLayoutDone(boolean first) {

                if (first) {
                    mFocusPositionManager.resetFocused();
                    mGridView.invalidate();
                }

                if (mRefreshData) {
                    mRefreshData = false;
                    mFocusPositionManager.reset();
                    mGridView.setSelection(0);

                    if (topShadowArea.isShown()) {
                        topShadowArea.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onOutAnimationDone() {

            }

            @Override
            public void onReachGridViewBottom() {

            }

            @Override
            public void onReachGridViewTop() {

            }

        });

        mGridView.setOnItemSelectedListener(mItemSelectedListener);
        mGridView.setOnItemClickListener(mOnItemClickListener);
        // 设置滚动监听
        mGridView.setOnFlipGridViewRunnableListener(new OnFlipRunnableListener() {

            @Override
            public void onFinished() {
                // 当滚动停下来时,才隐藏上面的遮盖
                //                if (mGridView.getLastVisiblePosition() <= (COLUMNS_COUNT * 2 - 1) && topShadowArea.isShown()) {
                //                    topShadowArea.setVisibility(View.INVISIBLE);
                //                }

                if ((mSelPosition < COLUMNS_COUNT) && (topShadowArea.isShown())) {
                    topShadowArea.setVisibility(View.INVISIBLE);
                } else if (mSelPosition >= COLUMNS_COUNT) {
                    topShadowArea.setVisibility(View.VISIBLE);
                }

                int lastSelPosition = mGridView.getLastVisiblePosition();
                View view = mGridView.getSelectedView();
                Address address = mAdapter.getItem(mSelPosition);
                AppDebug.i(TAG, "setOnFlipGridViewRunnableListener view = " + view + ", address = " + address
                        + ", mSelPosition = " + mSelPosition + ", lastSelPosition = " + lastSelPosition);
                if (view != null) {
                    if (address != null) {
                        AddressHolder holder = (AddressHolder) view.getTag();
                        if (holder != null) {
                            if (mAdapter.currentSelectedPosition == mSelPosition) {
                                mAdapter.setBackground(holder, true, true);
                            } else {
                                mAdapter.setBackground(holder, false, true);
                            }
                        }
                    } else if (mSelPosition == mAdapter.getCount() - 1) {
                        try{
                            AddressHolder holder = (AddressHolder) view.getTag();
                            mAdapter.setManageBackground(holder, true);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }

                if (mGridView != null) {
                    View selectview = mGridView.getSelectedView();
                    // 设置item蒙版
                    if (selectview != null && selectview instanceof ItemLayoutForAddress) {
                        ItemLayoutForAddress itemLayout = (ItemLayoutForAddress) selectview;
                        if (itemLayout != null) {
                            itemLayout.setSelect(true);
                        }
                    }
                }
            }

            @Override
            public void onFlipItemRunnable(float arg0, View arg1, int arg2) {

            }

            @Override
            public void onStart() {

            }
        });
    }

    /**
     * 获取地址信息
     */
    private void requestAddress() {

        if (mBusinessRequest == null) {
            return;
        }
        OnWaitProgressDialog(true);

        // 取用户的收货地址，并判断是否有收货地址
        mBusinessRequest.requestGetAddressList(new AddressListBusinessRequestListener(new WeakReference<BaseActivity>(
                this)));
    }

    /**
     * 适配数据
     */
    private void matchData(List<Address> data) {

        if (mGridView != null) {
            mGridView.setVisibility(View.VISIBLE);
        }
        if (data != null && !data.isEmpty()) {
            // 将默认地址放在第一个
            LinkedList<Address> linkedList = new LinkedList<Address>();
            for (Address address : data) {
                int status = address.getStatus();
                if (status == Address.DEFAULT_DELIVER_ADDRESS_STATUS) {
                    linkedList.addFirst(address);
                } else {
                    linkedList.add(address);
                }
            }

            if (addresses != null) {
                addresses.clear();
                addresses.addAll(linkedList);
            }

            linkedList = null;
        }

        if (mAdapter == null) {
            mAdapter = new AddressGridViewAdapter(this, addresses);
            mFocusPositionManager.requestLayout();// 强制请求重新布局，因为这个时候在onLayout时会重新找聚焦的点
            mGridView.setAdapter(mAdapter);
        } else {
            if (addresses != null && addresses.size() > 0 && addresses.get(0) != null
                    && addresses.get(0).getStatus() == Address.DEFAULT_DELIVER_ADDRESS_STATUS) {
                mAdapter.currentSelectedPosition = 0;
                mAdapter.defaultAddressPosition = 0;
            } else {
                mAdapter.currentSelectedPosition = -1;
            }
            mAdapter.update();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void refreshData() {
        if (mAdapter != null) {
            mRefreshData = true;
            if (addresses != null) {
                addresses.clear();
            }
            // 请求地址
            requestAddress();

        }
    }

    /**
     * item选中事件
     */
    ItemSelectedListener mItemSelectedListener = new ItemSelectedListener() {

        @Override
        public void onItemSelected(View view, int position, boolean selected, View parent) {
            AppDebug.i(TAG, "onItemSelected:view=" + view + ",position=" + position + ",selected=" + selected
                    + ",mAdapter=" + mAdapter);
            if (mAdapter == null) {
                return;
            }

            if (selected) {
                mSelPosition = position;
            }

            // 如果是选中并且焦点在第二行以上,立即显示上面的遮盖
            if (selected && position >= COLUMNS_COUNT && !topShadowArea.isShown()) {
                topShadowArea.setVisibility(View.VISIBLE);
            }
            if (view == null) {
                return;
            }

            // 设置item蒙版
            if (view instanceof ItemLayoutForAddress) {
                ItemLayoutForAddress itemLayout = (ItemLayoutForAddress) view;
                if (itemLayout != null) {
                    itemLayout.setSelect(selected);
                }
            }

            Address address = mAdapter.getItem(position);
            if (address != null) {
                AddressHolder holder = (AddressHolder) view.getTag();
                if (mAdapter.currentSelectedPosition == position) {
                    mAdapter.setBackground(holder, true, selected);
                } else {
                    mAdapter.setBackground(holder, false, selected);
                }
            } else if (position == mAdapter.getCount() - 1) {
                AddressHolder holder = (AddressHolder) view.getTag();
                mAdapter.setManageBackground(holder, selected);
            }
        }
    };

    /**
     * 条目点击事件
     */
    OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mAdapter == null) {
                return;
            }
            Address address = mAdapter.getItem(position);
            if (address != null) {
                if (mAdapter.currentSelectedPosition == position) {
                    return;
                }
                if (address.getAddressType() == 1) {
                    showErrorDialog("服务站不能设置为默认地址", false);
                    return;
                }
                currentSelectedPosition = position;
                String deliverId = address.getDeliverId();
                setDefaultAddress(deliverId);

            } else if (address == null && position == mAdapter.getCount() - 1) {
                Intent intent = new Intent();
                intent.setClassName(mContext, BaseConfig.SWITCH_TO_ADDRESSEDIT_ACTIVITY);
                startNeedLoginActivity(intent);
            }

        }

    };

    /**
     * 设置默认地址
     *
     * @param deliverId
     */
    private void setDefaultAddress(String deliverId) {

        if (mBusinessRequest == null) {
            return;
        }
        mBusinessRequest.requestSetDefaultAddress(deliverId, new DefaultAddressBusinessRequestListener(
                new WeakReference<BaseActivity>(this)));
    }

    @Override
    protected void onDestroy() {
        unRegisterLoginListener();
        if (addresses != null) {
            addresses.clear();
            addresses = null;
        }

        if (mAdapter != null) {
            mAdapter = null;
            mGridView.setAdapter(null);
        }

        if (mGridView != null) {
            mGridView.removeAllViewsInLayout();
        }

        mGridView = null;
        mFocusPositionManager = null;
        mBusinessRequest = null;
        mContext = null;
        onRemoveKeepedActivity(AddressActivity.class.getName());
        super.onDestroy();
    }

    @Override
    public String getPageName() {
        return TAG;
    }

    @Override
    public Map<String, String> getPageProperties() {

        Map<String, String> p = super.getPageProperties();
        p.put(SPMConfig.SPM_CNT, SPMConfig.ADDRESS_SPM + ".0.0");
        return p;
    }

    /**
     * 请求地址列表数据
     *
     * @author yunzhong.qyz
     */
    public static class AddressListBusinessRequestListener extends BizRequestListener<List<Address>> {

        public AddressListBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            AddressActivity addressActivity = (AddressActivity) mBaseActivityRef.get();
            if (addressActivity != null) {
                addressActivity.OnWaitProgressDialog(false);
                addressActivity.matchData(null);
            }
            if (resultCode == ServiceCode.NO_ADDRESS.getCode()) {
                return true;
            }
            return false;
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return true;
        }

        @Override
        public void onSuccess(List<Address> data) {
            AddressActivity addressActivity = (AddressActivity) mBaseActivityRef.get();
            if (addressActivity != null) {
                addressActivity.OnWaitProgressDialog(false);
                addressActivity.matchData(data);
            }
        }
    }

    /**
     * 默认地址
     *
     * @author yunzhong.qyz
     */
    public static class DefaultAddressBusinessRequestListener extends BizRequestListener<String> {

        public DefaultAddressBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef) {
            super(mBaseActivityRef);
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            AddressActivity addressActivity = (AddressActivity) mBaseActivityRef.get();
            if (addressActivity != null) {
                Toast.makeText(addressActivity.mContext, R.string.ytsdk_set_defalut_faile, Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        @Override
        public void onSuccess(String data) {
            AddressActivity addressActivity = (AddressActivity) mBaseActivityRef.get();
            if (addressActivity.mAdapter != null) {
                addressActivity.mAdapter.currentSelectedPosition = addressActivity.currentSelectedPosition;
                addressActivity.mAdapter.defaultAddressPosition = addressActivity.currentSelectedPosition;
                addressActivity.mAdapter.update();
            }
            Toast.makeText(addressActivity.mContext, R.string.ytsdk_set_defalut_success, Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            return false;
        }
    }

    @Override
    protected void onLoginCancel() {
        super.onLoginCancel();
        finish();
    }

    @Override
    protected FocusPositionManager getFocusPositionManager() {
        return (FocusPositionManager) findViewById(R.id.focus_flip_gridView);
    }
}
