package com.yunos.tvtaobao.search.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.request.bo.GoodsSearchResultDo;
import com.yunos.tvtaobao.search.R;
import com.yunos.tvtaobao.search.activity.SearchResultActivity;
import com.yunos.tvtaobao.search.adapter.GoodsListAdapter;
import com.yunos.tvtaobao.search.view.CenterRecyclerView;

/**
 * <pre>
 *     author : panbeixing
 *     time   : 2018/12/14
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class GoodsListFragment extends Fragment {
    private static final String TAG = "GoodsListFragment";

    private CenterRecyclerView goodsListLayout;
    private GoodsListAdapter goodsListAdapter;
    private GoodsSearchResultDo data;
    private ImageView ivNoData;

    private String keyword = null;
    private int currentPage = 1;
    private boolean hasNextPage = false;
    private String sales = null;
    private boolean isTmall = false;
    private boolean initFoucs = false; //第一次初始化页面，取第一个焦点

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        AppDebug.i(TAG, TAG + ".onCreateView");

        Bundle bundle = getArguments();
        keyword = bundle.getString("keyword");
        data = (GoodsSearchResultDo) bundle.getSerializable("searchData");

        hasNextPage = data.hasNextPage();
        sales = bundle.getString("sales");
        isTmall = bundle.getBoolean("isTmall", false);
        boolean isFirstInit = bundle.getBoolean("isFirstInit", false);

        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        goodsListLayout = (CenterRecyclerView) view.findViewById(R.id.rv_products_layout);
        ivNoData = (ImageView) view.findViewById(R.id.iv_no_data);
        final GridLayoutManager layoutManager = new GridLayoutManager(this.getActivity(), 4);
        layoutManager.setOrientation(GridLayoutManager.VERTICAL);
        goodsListAdapter = new GoodsListAdapter(this.getActivity());

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return goodsListAdapter.isHead(position) || goodsListAdapter.isFoot(position) ? layoutManager.getSpanCount() : 1;
            }
        });
        goodsListLayout.setLayoutManager(layoutManager);

        goodsListAdapter.setData(keyword, data);
        goodsListLayout.setAdapter(goodsListAdapter);
        if (data == null || data.getGoodList() == null || data.getGoodList().length <= 0) {
            goodsListLayout.setVisibility(View.GONE);
            ivNoData.setVisibility(View.VISIBLE);
        } else {
            goodsListLayout.setVisibility(View.VISIBLE);
            ivNoData.setVisibility(View.GONE);
        }

        if (!initFoucs && isFirstInit) {
            initFoucs = true;
            goodsListLayout.post(new Runnable() {
                @Override
                public void run() {
                    View view = goodsListLayout.getChildAt(1);
                    if (view != null) {
                        view.requestFocus();
                    }
                }
            });
        }
        goodsListLayout.setOnLoadMoreListener(new CenterRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                AppDebug.d(TAG, TAG + ".onLoadMore");
                if (hasNextPage) {
                    loadMoreGoodsData();

                }
            }
        });
        return view;
    }


    private void loadMoreGoodsData() {
        currentPage++;
        ((SearchResultActivity) getActivity()).requestSearchProduct(keyword, currentPage, sales, isTmall);
    }

    public void addData(GoodsSearchResultDo data) {
        if (data == null) {
            return;
        }

        hasNextPage = data.hasNextPage();
        if (goodsListAdapter != null) {
            goodsListAdapter.addData(data);
        }
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public boolean hasData() {
        if (goodsListAdapter != null && goodsListAdapter.getItemCount() > 0){
            AppDebug.i(TAG, TAG + ".hasData data.length =  " + data.getGoodList().length);
            return true;
        }
        AppDebug.i(TAG, TAG + ".hasData  = false");

        return false;
    }
}
