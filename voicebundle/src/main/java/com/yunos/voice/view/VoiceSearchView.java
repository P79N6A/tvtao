package com.yunos.voice.view;

import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tvtaobao.voicesdk.ASRNotify;
import com.tvtaobao.voicesdk.bo.JinnangDo;
import com.tvtaobao.voicesdk.bo.ProductDo;
import com.tvtaobao.voicesdk.bo.SearchObject;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.voice.R;
import com.yunos.voice.activity.VoiceSearchActivity;
import com.yunos.voice.adapter.VoiceSearchAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : panbeixing
 *     time : 2018/7/31
 *     desc :
 *     version : 1.0
 * </pre>
 */

public class VoiceSearchView {
    private final String TAG = "VoiceSearchActivity";
    private RecyclerView mGoodsList;
    private LinearLayout voice_chat_jinnang, voice_chat_remind_layout, voice_search_not_result;
    private TextView voice_chat_content;

    private AutoTextView voice_chat_remind;
    private VoiceSearchAdapter mAdapter;

    private WeakReference<VoiceSearchActivity> mWeakReference;

    private List<ProductDo> totalProduct;
    private int currentPage = 1;

    public VoiceSearchView(VoiceSearchActivity activity) {
        this.mWeakReference = new WeakReference<>(activity);
        this.totalProduct = new ArrayList<>();

        initView();
    }

    private void initView() {
        if (mWeakReference != null && mWeakReference.get() != null) {
            VoiceSearchActivity mActivity = mWeakReference.get();
            voice_chat_jinnang = mActivity.findViewById(R.id.voice_chat_jinnang);
            mGoodsList = mActivity.findViewById(R.id.voice_search_list);
            voice_chat_content = mActivity.findViewById(R.id.voice_chat_content);
            voice_chat_remind = mActivity.findViewById(R.id.voice_chat_remind);
            voice_chat_remind_layout = mActivity.findViewById(R.id.voice_chat_remind_layout);
            voice_search_not_result = mActivity.findViewById(R.id.voice_search_not_result);

            mAdapter = new VoiceSearchAdapter(mActivity);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
            layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            mGoodsList.setLayoutManager(layoutManager);
            mGoodsList.addItemDecoration(new SpaceItemDecoration(mActivity.getResources().getDimensionPixelSize(R.dimen.dp_8)));
            mGoodsList.setAdapter(mAdapter);
        }
    }

    /**
     * 对item进行点击操作。
     * @param type 0 购买；1 查看
     * @param position 位置
     */
    public void notifyClickType(int type, int position) {
        mAdapter.setClickType(type);

        try {
//            mask.setDig(mGoodsList.getChildAt(num - 1));
            RecyclerView.ViewHolder mHolder = mGoodsList.getChildViewHolder(mGoodsList.getChildAt(position - 1));
            AppDebug.e(TAG, TAG + ".gotoAction ItemHolder : " + mHolder);
            mHolder.itemView.performClick();
        } catch (Exception e) {
            AppDebug.e(TAG, TAG + ".gotoAction Exception : " + e.getMessage());
        }
    }

    public void notifyData(List<ProductDo> data, List<JinnangDo> jinnang, boolean newSearch) {
        goneNotResultPrompt();
        if (newSearch) {
            currentPage = 1;
            totalProduct.clear();

            notifyJinnang(jinnang);
        } else {
            currentPage++;
        }

        List<ProductDo> productDos;
        if (data.size() > 6) {
            productDos = data.subList(0, 6);
        } else {
            productDos = data;
        }

        mAdapter.setProductList(productDos);

        totalProduct.addAll(data);
        AppDebug.e(TAG, TAG + ".VoiceSearchListener.onSuccess size : " + data.size() + " ,total size : " + totalProduct.size());

        mGoodsList.post(new Runnable() {
            @Override
            public void run() {
                if (mGoodsList.getChildAt(0) != null) {
                    mGoodsList.getChildAt(0).requestFocus();
                }
            }
        });
    }

    /**
     * 换页，上一页
     */
    public void onPreviousPage() {
        if (currentPage > 1) {
            currentPage--;

            int startIndex = 6 * (currentPage - 1);

            List<ProductDo> productDos = totalProduct.subList(startIndex, startIndex + 6);
            mAdapter.setProductList(productDos);

            mGoodsList.post(new Runnable() {
                @Override
                public void run() {
                    if (mGoodsList.getChildAt(0) != null) {
                        mGoodsList.getChildAt(0).requestFocus();
                    }
                }
            });

            setPrompt("已为您返回上一页", null);

        } else {
            setPrompt("已经到第一页", null);
        }
    }

    /**
     * 换页，下一页
     */
    public boolean onNextPage(SearchObject searchObject) {
        currentPage++;
        if (totalProduct.size() - 6 * (currentPage - 1) < 6) {
            int size = searchObject.endIndex - searchObject.startIndex;
            if (size > 0) {
                searchObject.startIndex = searchObject.startIndex + size;
            } else {
                searchObject.startIndex = searchObject.startIndex + 30;
            }
            searchObject.endIndex = searchObject.startIndex + 30;
            mWeakReference.get().requestSearch(searchObject);
            return true;
        }

        int startIndex = 6 * (currentPage - 1);
        List<ProductDo> productDos = totalProduct.subList(startIndex, startIndex + 6);
        mAdapter.setProductList(productDos);

        mGoodsList.post(new Runnable() {
            @Override
            public void run() {
                if (mGoodsList.getChildAt(0) != null) {
                    mGoodsList.getChildAt(0).requestFocus();
                }
            }
        });

        setPrompt("您看看这一批怎么样?", null);
        return true;
    }

    /**
     * 更新显示锦囊接口
     *
     * @param jn
     */
    private void notifyJinnang(List<JinnangDo> jn) {
        if (jn == null || jn.size() == 0) {
            voice_chat_jinnang.removeAllViews();
            return;
        }

        int size = jn.size();
        AppDebug.e(TAG, TAG + ".notifyJinnang size : " + size);

        voice_chat_jinnang.removeAllViews();
        final VoiceSearchActivity mActivity = mWeakReference.get();
        int textsize = mActivity.getResources().getDimensionPixelSize(R.dimen.sp_22);
        int margin = mActivity.getResources().getDimensionPixelSize(R.dimen.dp_10);
        int padding = mActivity.getResources().getDimensionPixelSize(R.dimen.dp_24);
        int height = mActivity.getResources().getDimensionPixelSize(R.dimen.dp_40);
        int rightPadding = mActivity.getResources().getDimensionPixelSize(R.dimen.dp_6);

        TextView mJinnangPrompt = new TextView(mWeakReference.get());
        mJinnangPrompt.setText("您还可以说");
        mJinnangPrompt.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
        mJinnangPrompt.setTextColor(0xffffffff);
        mJinnangPrompt.setIncludeFontPadding(false);
        mJinnangPrompt.setPadding(0, 0, rightPadding, 0);
        mJinnangPrompt.setGravity(Gravity.CENTER_HORIZONTAL);
        voice_chat_jinnang.addView(mJinnangPrompt);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, height);
        lp.setMargins(margin, 0, 0, 0);
        lp.gravity = Gravity.CENTER;

        int textLenght = 6;
        for (int i = 0; i < size; i++) {
            final JinnangDo jinnangDo = jn.get(i);
            TextView m = new TextView(mWeakReference.get());
            m.setText(jinnangDo.getName());
            m.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
            m.setTextColor(0xffffffff);
            m.setIncludeFontPadding(false);
            m.setFocusable(true);
            m.setPadding(padding, 0, padding, 0);
            m.setGravity(Gravity.CENTER);
            m.setBackgroundDrawable(mActivity.getResources().getDrawable(R.drawable.item_jingnang_background));
            m.setLayoutParams(lp);
            m.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mActivity.requestSearch(jinnangDo.getContent());
                }
            });
            voice_chat_jinnang.addView(m);

            if (i >= 7)
                break;

            if (textLenght > 26) {
                break;
            } else {
                textLenght += jn.get(i).getName().length();
            }
        }
    }

    /**
     * 搜索提示
     */
    public void searchPrompt(String product, String spoken, List<String> tips) {
        String prompt = null;
        if (TextUtils.isEmpty(spoken)) {
            prompt = "为您推荐如下几款" + product + "，您要买第几个？";
        } else {
            prompt = spoken;
        }

        if (tips.size() <= 0) {
            tips.add("没有喜欢的？可以说“换一批”");
            tips.add("喜欢第三个？可以说“我要买第三个”");
            tips.add("想看第三个商品的详情，可以说“看看第三个”");
        }
        setPrompt(prompt, tips);
    }

    /**
     * 聊天回复UI 显示
     *
     * @param str
     */
    public void setPrompt(String str, List<String> tips) {
        voice_chat_content.setText(str);

        if (tips != null && tips.size() > 0) {
            voice_chat_remind.autoScroll(tips);
        } else {
            voice_chat_remind_layout.setVisibility(View.GONE);
        }

        ASRNotify.getInstance().playTTS(str);
    }

    public int getItemCount() {
        return mAdapter.getItemCount();
    }

    /**
     * 当没有搜索到商品，并且界面没有商品的时候
     */
    public void showNotResultPrompt() {
        if (voice_search_not_result.getVisibility() == View.GONE) {
            voice_search_not_result.setVisibility(View.VISIBLE);
        }
        if (voice_chat_jinnang.getVisibility() == View.VISIBLE) {
            voice_chat_jinnang.setVisibility(View.INVISIBLE);
        }
        if (mGoodsList.getVisibility() == View.VISIBLE) {
            mGoodsList.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 当搜索到商品时，隐藏没有结果的提示
     */
    public void goneNotResultPrompt() {
        if (voice_search_not_result.getVisibility() == View.VISIBLE) {
            voice_search_not_result.setVisibility(View.GONE);
        }
        if (voice_chat_jinnang.getVisibility() == View.INVISIBLE) {
            voice_chat_jinnang.setVisibility(View.VISIBLE);
        }
        if (mGoodsList.getVisibility() == View.INVISIBLE) {
            mGoodsList.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置items间距
     */
    class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        int mSpace;

        /**
         * Retrieve any offsets for the given item. Each field of <code>outRect</code> specifies
         * the number of pixels that the item view should be inset by, similar to padding or margin.
         * The default implementation sets the bounds of outRect to 0 and returns.
         * <p>
         * <p>
         * If this ItemDecoration does not affect the positioning of item views, it should set
         * all four fields of <code>outRect</code> (left, top, right, bottom) to zero
         * before returning.
         * <p>
         * <p>
         * If you need to access Adapter for additional data, you can call
         * {@link RecyclerView#getChildAdapterPosition(View)} to get the adapter position of the
         * View.
         *
         * @param outRect Rect to receive the output.
         * @param view    The child view to decorate
         * @param parent  RecyclerView this ItemDecoration is decorating
         * @param state   The current state of RecyclerView.
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = mSpace;
            outRect.right = mSpace;
        }

        public SpaceItemDecoration(int space) {
            this.mSpace = space;
        }
    }


}
