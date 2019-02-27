package com.yunos.tvtaobao.live.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.yunos.tv.core.config.SPMConfig;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.request.bo.TBaoLiveListBean;
import com.yunos.tvtaobao.live.R;
import com.yunos.tvtaobao.live.activity.TBaoLiveActivity;
import com.yunos.tvtaobao.live.activity.TMallLiveActivity;
import com.yunos.tvtaobao.live.adapter.TBaoLiveListAdapter;
import com.yunos.tvtaobao.live.controller.LiveDataController;
import com.yunos.tvtaobao.live.controller.LiveRequestCallBack;
import com.yunos.tvtaobao.live.utils.Tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
     * Created by pan on 16/12/15.
 */

public class LiveListDialog extends Dialog {
    private String TAG = LiveListDialog.class.getName();
    private Context mContext;
    private static volatile LiveListDialog listDialog;
    private ImageView dl_live_unlist_prompt;
    private ZPListView dl_live_list;
    private TBaoLiveListAdapter tbaoAdapter;
    private List<TBaoLiveListBean> dataBeanList;
    private LinearLayoutManagerTV linearLayoutManager;
    private LiveDataController mLiveDataController;
    private boolean isClearData = true;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                isClearData = true;
            }
        }
    };

    public static LiveListDialog getInstance(Context context) {
        if (listDialog == null) {
            synchronized (LiveListDialog.class) {
                if (listDialog == null) {
                    listDialog = new LiveListDialog(context);
                }
            }
        }
        return listDialog;
    }

    public void destory() {
        if (isShowing())
            this.dismiss();
        listDialog = null;
        dl_live_unlist_prompt = null;
        dl_live_list = null;
        tbaoAdapter = null;
        linearLayoutManager = null;
        isClearData = true;
        handler.removeMessages(0);
        handler = null;
    }

    private LiveListDialog(Context context) {
        this(context, R.style.live_Dialog_Fullscreen);
    }

    private LiveListDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        WindowManager.LayoutParams l = getWindow().getAttributes();
        //0.0f完全不暗，即背景是可见的 ，1.0f时候，背景全部变黑暗。
        l.dimAmount = 0.0f;
        //设置背景全部变暗的效果
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setWindowAnimations(R.style.dialog_animation);
        getWindow().setAttributes(l);

        mLiveDataController = LiveDataController.getInstance();

        setContentView(R.layout.live_list_view);
        dl_live_unlist_prompt = (ImageView) findViewById(R.id.dl_live_unlist_prompt);
        dl_live_list = (ZPListView) findViewById(R.id.dl_live_list);

        linearLayoutManager = new LinearLayoutManagerTV(context);
        linearLayoutManager.setTopPadding(Tools.compatiblePx(context, 260));
        linearLayoutManager.setBottomPadding(Tools.compatiblePx(context, 260));
        dl_live_list.setLayoutManager(linearLayoutManager);
        dataBeanList = new ArrayList<>();
        tbaoAdapter = new TBaoLiveListAdapter(context,dataBeanList);
        dl_live_list.setAdapter(tbaoAdapter);
    }

    private View focusView;
    private boolean isJustShow = false;

    public void setFocusView(View view) {
        this.focusView = view;
    }

    public void show(View view) {
        if (view != null) {
            this.focusView = view;
            view.setFocusable(false);
            view.clearFocus();
        }
        isJustShow = true;
        requestTBaoLiveList();
        if (!isShowing()){
            Map<String, String> properties = Utils.getProperties();
            properties.put("spm", SPMConfig.LIVE_LIST_SHOW_SPM);
            Utils.utCustomHit(((TBaoLiveActivity)mContext).getFullPageName(),"Expose_VideoList", properties);
            show();
        }
    }

    @Override
    public void dismiss() {
        if (mContext instanceof TMallLiveActivity) {
            ((TMallLiveActivity) mContext).setFocus();
        }
        if (mContext instanceof TBaoLiveActivity) {
            TBaoLiveActivity activity = (TBaoLiveActivity) mContext;
            activity.setFocusableT();
            activity.requestFocus(focusView);
        }

        if (focusView != null) {
            focusView.setFocusable(true);
            focusView.requestFocus();
        }

        super.dismiss();
    }

    private void requestTBaoLiveList() {
        try{
            if (isClearData) {
                mLiveDataController.requestTBaoLiveList(mContext, new LiveRequestCallBack<List<TBaoLiveListBean>>() {
                    @Override
                    public void success(List<TBaoLiveListBean> data) {
                        if (data == null || data.size() == 0) {
                            handler.removeMessages(0);
                            isClearData = true;
                        }

                        initTBaoLiveList(data);
                    }

                    @Override
                    public boolean error(String msg) {
                        return false;
                    }
                });

                isClearData = false;
                handler.sendEmptyMessageDelayed(0, 1 * 60 * 1000);
            } else {
                initTBaoLiveList(mLiveDataController.getTBaoLiveListBean());
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }


    private void initTBaoLiveList(List<TBaoLiveListBean> dataBeanList) {
        if (dataBeanList != null && dataBeanList.size() > 0) {
            this.dataBeanList.clear();
            this.dataBeanList.addAll(dataBeanList);
            if (tbaoAdapter == null){
                tbaoAdapter = new TBaoLiveListAdapter(mContext,this.dataBeanList);
            }
            dl_live_unlist_prompt.setVisibility(View.GONE);
            dl_live_list.setVisibility(View.VISIBLE);
            tbaoAdapter.notifyDataSetChanged();

            if (isJustShow) {
                dl_live_list.post(new Runnable() {
                    @Override
                    public void run() {
                        if (dl_live_list.getChildAt(0) != null) {
                            dl_live_list.requestFocus();
                        }
                    }
                });

                isJustShow = false;
            }
        } else {
            dl_live_unlist_prompt.setVisibility(View.VISIBLE);
            dl_live_list.setVisibility(View.GONE);
            dl_live_unlist_prompt.setImageResource(R.drawable.tbao_live_unlist_prompt);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                dismiss();
                return true;
            }

            if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
                if (isShowing()) {
                    dismiss();
                }
                return true;
            }

            if (dl_live_list!=null && dl_live_list.getFocusedChild() != null) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

}
