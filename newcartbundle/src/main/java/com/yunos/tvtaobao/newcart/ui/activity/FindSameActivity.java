package com.yunos.tvtaobao.newcart.ui.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.config.TvOptionsChannel;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.ActivityPathRecorder;
import com.yunos.tvtaobao.biz.common.BaseConfig;
import com.yunos.tvtaobao.biz.request.bo.FindSameBean;
import com.yunos.tvtaobao.biz.request.bo.FindSameContainerBean;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.biz.widget.newsku.TradeType;
import com.yunos.tvtaobao.newsku.TvTaoSkuActivity;
import com.yunos.tvtaobao.newcart.R;
import com.yunos.tvtaobao.biz.base.BaseMVPActivity;
import com.yunos.tvtaobao.newcart.entity.FindSameIntentBean;
import com.yunos.tvtaobao.newcart.ui.adapter.NewShopCartFindSameAdapter;
import com.yunos.tvtaobao.newcart.ui.contract.IFindSameContract;
import com.yunos.tvtaobao.newcart.ui.model.FindSameModel;
import com.yunos.tvtaobao.newcart.ui.presenter.FindSamePresenter;
import com.yunos.tvtaobao.biz.widget.TvRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by zhoubo on 2018/7/11.
 * describe 找相似
 */

public class FindSameActivity extends BaseMVPActivity<FindSamePresenter> implements IFindSameContract.IFindSameView, NewShopCartFindSameAdapter.IFindSameAddCartClickListener {

    private static final String TAG = "FindSameActivity";

    private TvRecyclerView ryFindSame;
    private FindSameIntentBean mFindSameBean;

    @Override
    public void showProgressDialog(boolean show) {
        OnWaitProgressDialog(show);

    }

    @Override
    protected FindSamePresenter createPresenter() {
        return new FindSamePresenter(new FindSameModel(), this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_find_same;
    }

    @Override
    public void initView() {
        TvOptionsConfig.setTvOptionVoiceSystem(getIntent().getStringExtra(CoreIntentKey.URI_FROM));
        TvOptionsConfig.setTvOptionsChannel(TvOptionsChannel.FIND_SAME);
        initViewId();
        initViewData();
    }

    private void initViewData() {
        Intent intent = getIntent();
        mFindSameBean = intent.getParcelableExtra(FindSameIntentBean.class.getName());
    }

    @Override
    public void initViewConfig(FindSameContainerBean findSameContainerBean) {

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4) {
            @Override
            public boolean onRequestChildFocus(RecyclerView parent, RecyclerView.State state, View child, View focused) {
                boolean sp = super.onRequestChildFocus(parent, state, child, focused);
                int position = getPosition(child);


                Rect mTempRect = new Rect();
                mTempRect.set(0, 0, child.getWidth(), child.getHeight());
                Log.i(TAG, "fixed position: " + mTempRect);

                // todo 获取父容器居中显示位置
                int parentLeft = 0;
                int parentRight;
                int parentTop = 0;
                int parentBottom;
                int childWidth = child.getWidth();
                int childHeight = child.getHeight();
                parentLeft = (parent.getWidth() - getPaddingLeft() - getPaddingRight()) / 2 - childWidth / 2;
                parentTop = (parent.getHeight() - getPaddingTop() - getPaddingBottom()) / 2 - childHeight / 2+getResources().getDimensionPixelSize(R.dimen.dp_25);
                parentRight = parentLeft + childWidth;
                parentBottom = parentTop + childHeight;

                Log.i(TAG, "ffffffffff :  " + child);
                final int childLeft = child.getLeft() + mTempRect.left;
                final int childTop = child.getTop() + mTempRect.top;
                final int childRight = childLeft + mTempRect.width();
                final int childBottom = childTop + mTempRect.height();

                final int offScreenLeft = Math.min(0, childLeft - parentLeft);
                final int offScreenTop = Math.min(0, childTop - parentTop);
                final int offScreenRight = Math.max(0, childRight - parentRight);
                final int offScreenBottom = Math.max(0, childBottom - parentBottom);

                // todo 计算需要的偏移量
                final int dx;
                dx = offScreenLeft != 0 ? offScreenLeft
                        : Math.min(childLeft - parentLeft, offScreenRight);
                final int dy = offScreenTop != 0 ? offScreenTop : Math.min(childTop - parentTop, offScreenBottom);

                AppDebug.e("findSame dy = ", dy + "");
                if (position < 5) {
                    parent.scrollToPosition(0);

                } else {
                    if (dx != 0 || dy != 0) {
                        parent.smoothScrollBy(dx, dy);
                    }

                }

                return true;
            }
        };
//        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        final NewShopCartFindSameAdapter findSameAdapter = new NewShopCartFindSameAdapter(this, findSameContainerBean, mFindSameBean);

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int spanSize = findSameAdapter.isHead(position) ? gridLayoutManager.getSpanCount() : 1;
                AppDebug.e("findSame SpanSize=", spanSize + "");
                return findSameAdapter.isHead(position) ? gridLayoutManager.getSpanCount() : 1;
            }
        });
//        ((SimpleItemAnimator)ryFindSame.getItemAnimator()).setSupportsChangeAnimations(false);
        ryFindSame.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int space = getResources().getDimensionPixelSize(R.dimen.dp_0);
//                outRect.left = space;
//                outRect.right = space;
                outRect.top = space;
                outRect.bottom = space;
                super.getItemOffsets(outRect, view, parent, state);
            }
        });
        ryFindSame.setLayoutManager(gridLayoutManager);
        ryFindSame.setAdapter(findSameAdapter);

    }

    private void initViewId() {
        ryFindSame = (TvRecyclerView) findViewById(R.id.recyclerview_findsame);
    }

    @Override
    public void initData() {
        showProgressDialog(true);
        if (mPresenter != null) {
            mPresenter.getFindSame(10, 1, mFindSameBean.getCatid(), mFindSameBean.getNid(), this);
        } else {
            AppDebug.d(TAG, "findSamepresenter is  null");
        }
    }

    @Override
    public void showFindsameResult(FindSameContainerBean findSameContainerBean) {
        if (findSameContainerBean != null) {
            if(findSameContainerBean.getFindSameBeanList()!=null&&findSameContainerBean.getFindSameBeanList().size()>0) {
                List<FindSameBean> findSameBeanList = findSameContainerBean.getFindSameBeanList();

                try {
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < findSameBeanList.size(); i++) {
                        FindSameBean goods = findSameBeanList.get(i);
                        String itemId = goods.getItemId();
//                        String itemS11Pre = goods.getS11Pre();
                        String price = goods.getPrice();
                        AppDebug.e(TAG, "Rebate itemId = " + itemId + ";itemS11Pre = false"   + ";price =" + price);
                        JSONObject object = new JSONObject();
                        object.put("itemId", itemId);
//                        object.put("isPre", itemS11Pre);
                        object.put("price", price);
                        jsonArray.put(object);
                    }
                    AppDebug.e(TAG, "Rebate" + jsonArray.toString());
                    mPresenter.getFindSameRebate(jsonArray.toString(), ActivityPathRecorder.getInstance().getCurrentPath(this),findSameContainerBean,this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else {
                initViewConfig(findSameContainerBean);
            }

        }else {
            initViewConfig(findSameContainerBean);

        }
    }

    @Override
    public void showFindsameRebateResult(FindSameContainerBean findSameContainerBean,List<RebateBo> data) {
        List<FindSameBean> findSameBeanList = findSameContainerBean.getFindSameBeanList();
        if (data != null && data.size() > 0) {
            for (RebateBo rebateBo : data) {
                for (FindSameBean findSameBean : findSameBeanList) {
                    if (rebateBo!=null&&findSameBean!=null&&rebateBo.getItemId().equals(findSameBean.getItemId())) {
                        findSameBean.setRebateBo(rebateBo);
                        break;
                    }
                }
            }
        }
        initViewConfig(findSameContainerBean);
    }

    /**
     * 加入购物车 SKU
     *
     * @param findSameBean ItemBean
     */
    @Override
    public void onFindSameAddCartClick(FindSameBean findSameBean) {
        if (findSameBean != null) {
            Intent intent = new Intent(this, TvTaoSkuActivity.class);
            intent.putExtra(BaseConfig.INTENT_KEY_ITEMID, findSameBean.getItemId());
            intent.putExtra(BaseConfig.INTENT_KEY_SKUID, mFindSameBean.getSkuId());
            intent.putExtra(BaseConfig.INTENT_KEY_SKU_TYPE, TradeType.ADD_CART);
            startActivity(intent);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 购物车详情
     *
     * @param findSameBean ItemBean
     */
    @Override
    public void onFindSameDetailClick(FindSameBean findSameBean) {
        if (findSameBean != null) {
            Intent intent = new Intent();
            intent.setClassName(this, BaseConfig.SWITCH_TO_DETAIL_ACTIVITY);
            intent.putExtra(BaseConfig.INTENT_KEY_ITEMID, findSameBean.getItemId());
            intent.putExtra(BaseConfig.INTENT_KEY_PRICE, findSameBean.getPrice());
            startActivity(intent);
        }
    }

    @Override
    public String getPageName() {
        return "Cart_searchSimilarity";
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        p.put("spm-cnt", SPMConfig.NEW_SHOP_CART_LIST_SPM_ITEM_SIMILARITY);
        return p;
    }

    @Override
    protected String getAppTag() {
        return "Tb";
    }

}
