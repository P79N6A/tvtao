package com.yunos.tvtaobao.newcart.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.config.TvOptionsConfig;
import com.yunos.tv.core.util.ActivityPathRecorder;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.config.TvOptionsChannel;
import com.yunos.tvtaobao.biz.bridge.eventbus.KillGuessLikeActEvent;
import com.yunos.tvtaobao.biz.request.bo.GuessLikeGoodsBean;
import com.yunos.tvtaobao.biz.request.bo.RebateBo;
import com.yunos.tvtaobao.newcart.R;
import com.yunos.tvtaobao.biz.base.BaseMVPActivity;
import com.yunos.tvtaobao.newcart.ui.adapter.GuessLikeAdapter;
import com.yunos.tvtaobao.newcart.ui.contract.GuessLikeContract;
import com.yunos.tvtaobao.newcart.ui.model.GuessLikeModel;
import com.yunos.tvtaobao.newcart.ui.presenter.GuessLikePresenter;
import com.yunos.tvtaobao.newcart.view.MirrorImageView;
import com.yunos.tvtaobao.biz.widget.TvRecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yuanqihui on 2018/6/27.
 */

public class GuessLikeActivity extends BaseMVPActivity<GuessLikePresenter> implements GuessLikeContract.View {
    private static final String TAG = "Cart_Lovely";
    private TvRecyclerView guessLikeRecyclerView;
    private GuessLikeAdapter guessLikeAdapter;
    private List<GuessLikeGoodsBean.ResultVO.RecommendVO> recommemdList;
    private boolean isVisible;
    private int currentPosition;
    private static final int GUESS_LIKE_NUM = 33;
    private String mFrom;
    private String mBgUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (getIntent() != null) {
            mFrom = getIntent().getStringExtra("guess_like_from");
            mBgUrl = getIntent().getStringExtra("guess_like_bg_url");
            TvOptionsConfig.setTvOptionVoiceSystem(getIntent().getStringExtra(CoreIntentKey.URI_FROM));
            TvOptionsConfig.setTvOptionsChannel(TvOptionsChannel.GUESSLIKE);
        }
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.botton_to_top_fade_in, R.anim.top_to_top_fade_out);
        EventBus.getDefault().register(this);
    }

    @Override
    protected GuessLikePresenter createPresenter() {
        return new GuessLikePresenter(new GuessLikeModel(), this);
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_guess_like;
    }

    @Override
    public void initView() {
        ConstraintLayout ctlGuessLike = (ConstraintLayout) findViewById(R.id.ctl_guess_like);
        MirrorImageView imgGuessLike = (MirrorImageView) findViewById(R.id.img_guess_like);
        if ("home".equals(mFrom)) {
            imgGuessLike.setBackgroundResource(R.mipmap.home_guess_like_bg);
            ctlGuessLike.setBackgroundResource(R.drawable.drawable_transparent);
            ImageLoaderManager.getImageLoaderManager(this).displayImage(mBgUrl, imgGuessLike);
        } else {
            imgGuessLike.setBackgroundResource(R.drawable.drawable_transparent);
            ctlGuessLike.setBackgroundResource(R.drawable.bg_new_shop_cart);
        }
        guessLikeRecyclerView = (TvRecyclerView) findViewById(R.id.recyclerview_guesslike);
        final int spacing = getResources().getDimensionPixelSize(R.dimen.dp_0);
        guessLikeRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top = spacing;
                outRect.bottom = spacing; // item bottom

                outRect.left = spacing; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing; // spacing - (column + 1) * ((1f /    spanCount) * spacing)


            }
        });
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
                parentTop = (parent.getHeight() - getPaddingTop() - getPaddingBottom()) / 2 - childHeight / 2;
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

                if (dx != 0 || dy != 0) {
                    parent.smoothScrollBy(dx, dy);
                }

                return true;
            }
        };
        guessLikeAdapter = new GuessLikeAdapter(this, new GuessLikeAdapter.OnItemListener() {
            @Override
            public void onItemSelected(int position) {
                currentPosition = position;
            }
        }, false);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()

        {
            @Override
            public int getSpanSize(int position) {
                return guessLikeAdapter.isFoot(position) || guessLikeAdapter.isHead(position) ? gridLayoutManager.getSpanCount() : 1;
            }
        });

        guessLikeRecyclerView.setLayoutManager(gridLayoutManager);
        guessLikeRecyclerView.setAdapter(guessLikeAdapter);

        guessLikeRecyclerView.setOnItemListener(new TvRecyclerView.OnKeyListener()

        {
            @Override
            public void onKeyEvent(String direction, boolean isNull) {
                if (direction.equals("up") && isNull && currentPosition < 5) {
                    finish();
                    overridePendingTransition(R.anim.top_to_botton_fade_in, R.anim.botton_to_botton_fade_out);
                }
            }
        });
//        SnapHelper snapHelper = new LinearSnapHelper();
//        snapHelper.attachToRecyclerView(guessLikeRecyclerView);
//        guessLikeRecyclerView.setSelectedItemAtCentered(true);
    }

    @Override
    public void initData() {
        showProgressDialog(true);
        if ("home".equals(mFrom)) {
            mPresenter.getHomeGuessLikeGoods("homePage", this);
        } else {
            mPresenter.getGuessLikeGoods("TRADE_CART", this);
        }
    }

    /*
     *加载动画
     */
    @Override
    public void showProgressDialog(boolean show) {
        OnWaitProgressDialog(show);
    }


    /*
     *获取猜你喜欢数据
     */
    @Override
    public void setGuessLikeGoodsData(final GuessLikeGoodsBean guessLikeGoodsData) {
        if (guessLikeGoodsData != null) {
            recommemdList = new ArrayList<>();
            if (guessLikeGoodsData.getResult() != null && guessLikeGoodsData.getResult().getRecommedResult() != null) {
                for (int i = 0; i < guessLikeGoodsData.getResult().getRecommedResult().size(); i++) {
                    if (guessLikeGoodsData.getResult().getRecommedResult().get(i).getType().equals("item")) {
                        recommemdList.add(guessLikeGoodsData.getResult().getRecommedResult().get(i));
                    }
                }

                recommemdList = recommemdList.size() >= GUESS_LIKE_NUM ? recommemdList.subList(0, GUESS_LIKE_NUM) : recommemdList;

                if (recommemdList != null) {

                    try {
                        JSONArray jsonArray = new JSONArray();
                        for (int i = 0; i < recommemdList.size(); i++) {
                            GuessLikeGoodsBean.ResultVO.RecommendVO recommendGoods = recommemdList.get(i);
                            String itemId = recommendGoods.getFields().getItemId();
                            String price = null;
                            if (recommendGoods.getFields().getPrice() != null && (recommendGoods.getFields().getPrice().getCent() != null) ||
                                    recommendGoods.getFields().getPrice().getYuan() != null) {
                                if (!TextUtils.isEmpty(recommendGoods.getFields().getPrice().getCent())) {
                                    price = recommendGoods.getFields().getPrice().getYuan()
                                            + "." + recommendGoods.getFields().getPrice().getCent();
                                } else {
                                    price = recommendGoods.getFields().getPrice().getYuan();
                                }
                            }
//                            String itemS11Pre = goods.getS11Pre();

                            AppDebug.e(TAG, "Rebate itemId = " + itemId + ";itemS11Pre = false" + ";price =" + price);
                            JSONObject object = new JSONObject();
                            object.put("itemId", itemId);
//                        object.put("isPre", itemS11Pre);
                            object.put("price", price);
                            jsonArray.put(object);
                        }
                        AppDebug.e(TAG, "Rebate" + jsonArray.toString());
                        mPresenter.getGuessLikeRebate(jsonArray.toString(),
                                ActivityPathRecorder.getInstance().getCurrentPath(this), recommemdList, this);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    guessLikeAdapter.setData(recommemdList);
                }

            }


        }

    }

    @Override
    public void showFindsameRebateResult(List<GuessLikeGoodsBean.ResultVO.RecommendVO> recommemdList, List<RebateBo> data) {

        if (data != null && data.size() > 0) {
            for (RebateBo rebateBo : data) {
                for (GuessLikeGoodsBean.ResultVO.RecommendVO recommendGoods : recommemdList) {
                    if (rebateBo!=null&&recommendGoods!=null&&rebateBo.getItemId().equals(recommendGoods.getFields().getItemId())) {
                        recommendGoods.setRebateBo(rebateBo);
                        break;
                    }
                }
            }
            guessLikeAdapter.setData(recommemdList);
        }else {
            guessLikeAdapter.setData(recommemdList);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public String getPageName() {
        return TAG;
    }

    @Override
    public Map<String, String> getPageProperties() {
        Map<String, String> p = super.getPageProperties();
        p.put("spm-cnt", SPMConfig.NEW_SHOP_CART_LIST_SPM_ITEM_LOVELY);
        return p;
    }

    @Override
    protected String getAppTag() {
        return "Tb";
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onKillGuessLikeActEvent(KillGuessLikeActEvent event){
        finish();
    }
}
