package com.yunos.tvtaobao.detailbundle.evaluate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.yunos.tv.app.widget.focus.FocusRelativeLayout;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.biz.listener.BizRequestListener;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.ItemRates;
import com.yunos.tvtaobao.biz.request.bo.PaginationItemRates;
import com.yunos.tvtaobao.detailbundle.R;
import com.yunos.tvtaobao.detailbundle.activity.NewDetailActivity;
import com.yunos.tvtaobao.detailbundle.view.DetailBuilder;
import com.yunos.tvtaobao.detailbundle.view.DetailFocusPositionManager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class EvaluationActivity extends BaseActivity {
    private String TAG = "EvaluationActivity";
    //聚焦
    private DetailFocusPositionManager focusPositionManager;
    private Button tvAll;
    private Button tvImage;
    private RelativeLayout llType;
    private BusinessRequest mBusinessRequest;
    public static boolean isFirstRequest;
    private LinearLayout llEmpty;
    private EvaluationAdapter evaluationAdapter;
    private ListView lvImg;

    // 商品ID号
    private String mItemID = null;
    ArrayList<ItemRates> ItemRatesList;
    private int lvPosition = 0;
    private final int DISTANCE = 150;

    private View headView;

    public static void launch(Activity activity, String mItemID) {
        Intent intent = new Intent(activity, EvaluationActivity.class);
        intent.putExtra("mItemID", mItemID);
        activity.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        initHeadView();
        initView();

    }

    private void initHeadView() {
        headView = View.inflate(this, R.layout.activity_evaluation_header, null);
        tvAll = (Button) headView.findViewById(R.id.tv_all);
        tvImage = (Button) headView.findViewById(R.id.tv_img);
        llType = (RelativeLayout) headView.findViewById(R.id.ll_type);
//        lvImg.getSelectedItemPosition(0);
    }


    private void initView() {
        ItemRatesList = new ArrayList<>();

        llEmpty = (LinearLayout) findViewById(R.id.ll_empty);
        lvImg = (ListView) findViewById(R.id.lv_img);
        lvImg.addHeaderView(headView);
        lvImg.setEmptyView(llEmpty);
        mItemID = getIntent().getStringExtra("mItemID");

//        focusPositionManager = (DetailFocusPositionManager) findViewById(R.id.new_detail_evaluation);
//        focusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
//                R.drawable.ytm_touming)));
//        focusPositionManager.setFirstFocusChild(tvAll);

        mBusinessRequest = BusinessRequest.getBusinessRequest();

        evaluationAdapter = new EvaluationAdapter(this);
        lvImg.setAdapter(evaluationAdapter);
        setListener();
        getAllRatesData();
    }

    private void setListener() {
        lvImg.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem<=lvPosition
                        &&firstVisibleItem==0 &&getHeaderY()==0){
                    //到头部后的操作
                    Log.d(TAG, "到顶了~");

                }else
                {
                    //其余操作
                }
                lvPosition=firstVisibleItem;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        lvImg.requestFocus();
        lvImg.requestFocus(lvPosition);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        lvImg.requestFocus();
    }



    //判断
    private float getHeaderY(){
        View headerView= lvImg.getChildAt(0);
        if (headerView!=null){
            Log.d("headerView.getY();",headerView.getY()+"");
            return headerView.getY();
        }
        return -1f;
    }


    /**
     * 初始化评价卡片，填充各类型的总数
     */
    public void handlerGetAllRatesData(PaginationItemRates data, int tabPosition) {

        if (Integer.parseInt(data.getFeedAllCount()) > 0) {
            llEmpty.setVisibility(View.GONE);
            getRatesData(1, 20, DetailBuilder.LAYOUT_INDEX_0);
            tvAll.setText("全部评价(" + data.getFeedAllCount() + ")");
            tvImage.setText("有图评价(" + data.getFeedPicCount() + ")");
        } else {
            llEmpty.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 获取全部的评价
     */
    private void getAllRatesData() {
        AppDebug.i(TAG, "getAllRatesData -->");
        isFirstRequest = true;
        // 主要目的是得到各评价的总数; 所以 pagesize 只需一个； tabnumber = -1
        getRatesData(1, 2, DetailBuilder.LAYOUT_INDEX_F1);
    }

    /**
     * 请求评论的数据
     *
     * @param pageNo    第几页
     * @param tabnumber tab的位置
     */
    private void getRatesData(int pageNo, final int pagesize, final int tabnumber) {
        String rateType = null;
//        if (mDetailBuilder != null) {
//            rateType = mDetailBuilder.getRateType(tabnumber);
//        }

        AppDebug.i(TAG, "getRatesData --> tabnumber = " + tabnumber + "; pageNo = " + pageNo + "; pagesize = "
                + pagesize + "; rateType = " + rateType);
        mBusinessRequest.requestGetItemRates(mItemID, pageNo, pagesize, rateType,
                new GetItemRatesBusinessRequestListener(new WeakReference<BaseActivity>(EvaluationActivity.this), tabnumber));

    }


    /**
     * 评论请求的监听类
     */
    private class GetItemRatesBusinessRequestListener extends BizRequestListener<PaginationItemRates> {

        private final int mTabPosition;

        public GetItemRatesBusinessRequestListener(WeakReference<BaseActivity> mBaseActivityRef, final int position) {
            super(mBaseActivityRef);
            mTabPosition = position;
        }

        @Override
        public boolean onError(int resultCode, String msg) {
            OnWaitProgressDialog(false);
            AppDebug.i(NewDetailActivity.TAG,
                    "GetItemRatesBusinessRequestListener --> onError --> resultCode = " + resultCode + "; msg = "
                            + msg);
//                onHandleRequestGetItemRatesError(mTabPosition);

            if (isFirstRequest) {
                return false;
            }
            isFirstRequest = false;

            return true;
        }

        @Override
        public void onSuccess(PaginationItemRates data) {
            AppDebug.i(NewDetailActivity.TAG, "GetItemRatesBusinessRequestListener --> onSuccess --> data = "
                    + data);
            OnWaitProgressDialog(false);
            if (mTabPosition == DetailBuilder.LAYOUT_INDEX_F1) {
                handlerGetAllRatesData(data, mTabPosition);

            } else {
                if (data.getItemRates() != null) {
                    for (int i = 0; i < data.getItemRates().length; i++) {
                        ItemRatesList.add(data.getItemRates()[i]);
                    }
                    evaluationAdapter.setmItemRates(ItemRatesList);
                }


            }

            isFirstRequest = false;
        }

        @Override
        public boolean ifFinishWhenCloseErrorDialog() {
            if (isFirstRequest) {
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        Log.d(TAG, event.getAction() + "----" + event.getKeyCode() + "+++"+KeyEvent.ACTION_DOWN+KeyEvent.ACTION_UP);
        Log.d(TAG,"item_position"+lvImg.getSelectedItemPosition());
        if(event.getAction()==KeyEvent.ACTION_DOWN){
            switch (event.getKeyCode()){
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if(lvPosition<ItemRatesList.size()){
                        lvPosition++;
                        lvImg.requestFocus();
                        lvImg.setSelection(lvPosition);
//                        lvImg.smoothScrollToPosition(lvPosition);
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if(lvPosition>0){
                        lvPosition--;
                        lvImg.requestFocus();
                        lvImg.setSelection(lvPosition);
//                        lvImg.smoothScrollToPosition(lvPosition);
                        return true;
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_LEFT:

                    break;

                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    break;

            }


        }


        return super.dispatchKeyEvent(event);
    }
}
