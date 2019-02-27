/**
 * $
 * PROJECT NAME: SystemAlertDlgTest
 * PACKAGE NAME: com.example.systemalertdlgtest
 * FILE NAME: list_dialog.java
 * CREATED TIME: 2015年6月23日
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 *
 */
package com.yunos.tvtaobao.tvshoppingbundle.dialog;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.tvlife.imageloader.core.DisplayImageOptions;
import com.tvlife.imageloader.core.assist.FailReason;
import com.tvlife.imageloader.core.display.RoundedBitmapDisplayer;
import com.tvlife.imageloader.core.listener.SimpleImageLoadingListener;
import com.yunos.tv.TYIDManagerWrapper;
import com.yunos.tv.app.widget.AdapterView;
import com.yunos.tv.app.widget.AdapterView.OnItemClickListener;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.CoreIntentKey;
import com.yunos.tv.core.common.ImageLoaderManager;
import com.yunos.tv.core.common.RequestListener;
import com.yunos.tv.core.common.User;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.SharedPreferencesUtils;
import com.yunos.tv.core.util.Utils;
import com.yunos.tv.lib.SystemProUtils;
import com.yunos.tvtaobao.biz.request.BusinessRequest;
import com.yunos.tvtaobao.biz.request.bo.TbTvShoppingItemBo;
import com.yunos.tvtaobao.biz.request.bo.TbTvShoppingItemBo.ShopType;
import com.yunos.tvtaobao.tvshoppingbundle.R;
import com.yunos.tvtaobao.tvshoppingbundle.manager.TbTvShoppingManager;
import com.yunos.tvtaobao.tvshoppingbundle.manager.TbTvShoppingManager.NEED_RESUME_PAGE;
import com.yunos.tvtaobao.tvshoppingbundle.manager.TbTvShoppingManager.OnTbTvShoppingHideListener;
import com.yunos.tvtaobao.tvshoppingbundle.manager.TbTvShoppingManager.TbTvShopNeedResumeInfo;
import com.yunos.tvtaobao.tvshoppingbundle.manager.TbTvShoppingManager.TbTvShoppingItemData;
import com.yunos.tvtaobao.tvshoppingbundle.util.ImageUtil;
import com.yunos.tvtaobao.tvshoppingbundle.view.TvShopFocusHListView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import com.yunos.tvtaobao.blitz.account.LoginHelper;

/**
 * Class Descripton.
 *
 * @author mi.cao
 * @data 2015年6月23日 下午4:15:10
 */
public class TvShopAllCollectDialog extends TvShopBaseDialog {

    private String TAG = "TvShopAllCollectDialog";

    private Context mContext;
    // layout根对象
    private FocusPositionManager mFocusPositionManager;
    // 列表控件对象
    private TvShopFocusHListView mFocusHListView;
    // 适配器对象
    private AllColectAdapter mAllColectAdapter = null;
    // 数据列表 重新排序后的所有列表
    private List<TbTvShoppingItemData> mNewTvShopItemDataList;
    // 界面关闭监听器
    private OnTbTvShoppingHideListener mOnTbTvShoppingHideListener;
    // 推荐商品唯一Id
    private long mId;
    // 列表的默认选择(目前没法实现，因为FocusHListView无法滚动到当前选中项)
    private int mDefaultIndex;

    private Animation mAnimationIn; // 进入时的动画
    private Animation mAnimationOut; // 消失时的动画
    private boolean mIsDismissing; // 动画正在进行
    private int mCurPosition;// 当前位置
    private String bizSource="look_and_tao";
    private long oneDay=86400000;
    private long sevenDay=604800000;


    public TvShopAllCollectDialog(Context context, int theme, long id, int defaultIndex) {
        super(context, theme);
        mContext = context;
        mId = id;
        mDefaultIndex = defaultIndex;
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        setContentView(R.layout.ytshop_all_collect_activity);

        mIsDismissing = false;
        mAnimationIn = AnimationUtils.loadAnimation(mWindow.getContext(), R.anim.ytshop_slide_in_bottom);
        mAnimationOut = AnimationUtils.loadAnimation(mWindow.getContext(), R.anim.ytshop_slide_out_bottom);

        mTbTvShoppingManager.setShopItemVisible(mId, true);
        boolean needHideShop = mTbTvShoppingManager.getNeedHideShop();
        AppDebug.v(TAG, TAG + ".init needHideShop = " + needHideShop + ", mId = " + mId);
        if (needHideShop) {
            mDismissImmediately = true;// 立即消失
            dismiss();
            return;
        }

        mOnTbTvShoppingHideListener = new OnTbTvShoppingHideListener() {

            @Override
            public void onHideShop(long id) {
                AppDebug.i(TAG, TAG + ".init onHideShop id = " + id);
                dismiss();
                if (mNetworkDialog != null) {
                    mNetworkDialog.dismiss();
                    mNetworkDialog = null;
                }
            }
        };
        mTbTvShoppingManager.registerTbTvShoppingHideListener(mOnTbTvShoppingHideListener);

        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mFocusPositionManager = (FocusPositionManager) findViewById(R.id.position_manager_layout);
        mFocusPositionManager.setSelector(new StaticFocusDrawable(mContext.getResources().getDrawable(
                R.drawable.ytshop_common_focus)));
        mFocusPositionManager.requestFocus();

        mFocusHListView = (TvShopFocusHListView) findViewById(R.id.all_collect_list);
        mFocusHListView.setAnimateWhenGainFocus(false, true, false, false);
        mFocusHListView.setFlingSlowDownRatio(8.0f);
        mFocusHListView.setFocusBackground(true);
        updateDataList(mTbTvShoppingManager.getShoppingItemDataList());
        if (mNewTvShopItemDataList == null || mNewTvShopItemDataList.size() == 0) {
            AppDebug.e(TAG, TAG + ".initView.mNewTvShopItemDataList == null");
            mDismissImmediately = true;// 立即消失
            dismiss();// 如果列表不存在，退出界面 
            return;
        }

        mAllColectAdapter = new AllColectAdapter(mContext, mNewTvShopItemDataList);
        mFocusHListView.setAdapter(mAllColectAdapter);
        if (mDefaultIndex < 0) {
            mDefaultIndex = 0;
        } else if (mDefaultIndex > mNewTvShopItemDataList.size() - 1) {
            mDefaultIndex = mNewTvShopItemDataList.size() - 1;
        }
        AppDebug.i(TAG, TAG + ".initView.mDefaultIndex = " + mDefaultIndex);
        mFocusHListView.fristSelection(mDefaultIndex);
        mFocusHListView.setFlipScrollFrameCount(10);
        anaylisysTaoke();
        mFocusHListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppDebug.i(TAG, TAG + ".onItemClick.mAnimationing = " + mIsDismissing);
                if (mIsDismissing) {// 如果动画正在运行
                    return;
                }
                mCurPosition = position;
                if (!NetWorkUtil.isNetWorkAvailable()) {
                    onStartActivityNetWorkError(new OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startNetWorkSettingActivity(mContext,
                                    mContext.getString(R.string.ytbv_open_setting_activity_error));
                            if (mNetworkDialog != null) {
                                mNetworkDialog.dismiss();
                            }

                            onNeedHideDialog();
                        }
                    }, new OnKeyListener() {

                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK) {
                                if (mNetworkDialog != null) {
                                    mNetworkDialog.dismiss();
                                }

                                if (mCloseDialogOfNetworkDialog && mContext != null) {
                                    dismiss();
                                }
                                return true;
                            }
                            return false;
                        }
                    }, false);
                    return;
                }

                Map<String, String> p = Utils.getProperties();
                initProperties(p);
                p.put("position", String.valueOf(position));
                String controlName = Utils.getControlName(getFullPageName(), "ItemBtn", null);

                if (!CoreApplication.getLoginHelper(mContext).isLogin()) {
                    p.put("is_login", "false");
                } else {
                    p.put("is_login", "true");
                }

                if (mNewTvShopItemDataList != null && mNewTvShopItemDataList.size() > 0) {
                    TbTvShoppingItemData item = mNewTvShopItemDataList.get(position);

                    if (item != null) {
                        try {
                            p.put("item_id", String.valueOf(item.getItemId()));
                            if (item.isFavor()) {
                                p.put("isCollected", "true");
                            } else {
                                p.put("isCollected", "false");
                            }

                            String url = null;
                            if (item.getItemId() == 0) {
                                url = item.getItemActionUri();
                            } else {
                                url = "tvtaobao://home?module=detail&itemId=" + item.getItemId() + "&"
                                        + CoreIntentKey.URI_FROM_APP + "=" + getAppName()
                                        + "&notshowloading=true";
                            }
                            AppDebug.i(TAG, TAG + ".onItemClick.url = " + url);
                            Intent intent = new Intent();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            setHuodong("biankanbianmai_end");
                            intent.setData(Uri.parse(url));
                            startActivity(intent);
                            //卡片点击事件添加videoId参数
                            String videoName = mTbTvShoppingManager.getmCurrTvShopReceiverData().getVideoName();
                            p.put("video_Name", videoName);
                            //原影视大点
                            TBS.Adv.ctrlClicked(CT.ListItem, controlName, Utils.getKvs(p));
                            //现电陶打点
                            Utils.utControlHit(getFullPageName(), "Button_" + position, p);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        } finally {
                            onNeedHideDialog();
                        }
                    }
                }
            }
        });
    }

    /**
     * 淘客打点
     */
    private void anaylisysTaoke() {
        if (CoreApplication.getLoginHelper(getContext()) == null) {
            // 处理个别机型上的问题，不清楚是什么原因；
            return;
        }
        //淘客登录打点
        String ykNick = tryGetYkNick();
        if (CoreApplication.getLoginHelper(getContext()).isLogin() || !TextUtils.isEmpty(ykNick)) {
            long historyTime = SharedPreferencesUtils.getTaoKeLogin(mContext);
            long currentTime = System.currentTimeMillis();
            AppDebug.e(TAG, "historyTime = " + historyTime + " currentTime = " + currentTime);
            if (currentTime > historyTime) {
                AppDebug.e(TAG, "isNotLogin--->");
                if (!TextUtils.isEmpty(User.getNick())){
                    BusinessRequest.getBusinessRequest().requestTaokeLoginAnalysis(User.getNick(),bizSource,  new RequestListener<JSONObject>() {
                        @Override
                        public void onRequestDone(JSONObject data, int resultCode, String msg) {
                            long historyTime = System.currentTimeMillis() + sevenDay;//7天
                            SharedPreferencesUtils.saveTvBuyTaoKe(mContext, historyTime);
                        }
                    });
              }else if (!TextUtils.isEmpty(ykNick)) {
                    BusinessRequest.getBusinessRequest().requestTaokeLoginAnalysis(ykNick, bizSource, new RequestListener<JSONObject>() {
                        @Override
                        public void onRequestDone(JSONObject data, int resultCode, String msg) {
                            long historyTime = System.currentTimeMillis() + sevenDay;//7天
                            SharedPreferencesUtils.saveTvBuyTaoKe(mContext, historyTime);
                        }
                    });
                }
            }
        }
        if (mNewTvShopItemDataList == null || mNewTvShopItemDataList.size() == 0) {
            return;
        }
        StringBuilder sbuilder = new StringBuilder();
        for (int i = 0; i < mNewTvShopItemDataList.size(); i++) {
            if (mNewTvShopItemDataList.get(i).getItemId() == 0)
                break;
            sbuilder.append(mNewTvShopItemDataList.get(i).getItemId()).append(",");
        }

        /**
         * 自定义聚划算列表页打点
         */
        if (CoreApplication.getLoginHelper(getContext()).isLogin() || !TextUtils.isEmpty(ykNick)) {
            String stbId = DeviceUtil.initMacAddress(mContext);
            String nick = !TextUtils.isEmpty(User.getNick()) ? User.getNick() : ykNick;
            if (!TextUtils.isEmpty(nick)&&!TextUtils.isEmpty(stbId)&&sbuilder!=null) {

                long historyTime = SharedPreferencesUtils.getTaoKeBtoc(mContext);
                long currentTime = System.currentTimeMillis();
                AppDebug.e(TAG, "BtochistoryTime = " + historyTime + "BtoccurrentTime = " + currentTime);
                if (currentTime > historyTime) {
                    BusinessRequest.getBusinessRequest().requestTaokeJHSListAnalysis(stbId, nick,bizSource, new RequestListener<JSONObject>() {
                        @Override
                        public void onRequestDone(JSONObject data, int resultCode, String msg) {
                            long historyTime = System.currentTimeMillis() + oneDay;//1天
                            SharedPreferencesUtils.saveTaoKeBtoc(mContext, historyTime);
                        }
                    });
                }

                BusinessRequest.getBusinessRequest().requestTaokeDetailAnalysis(stbId, nick, sbuilder.toString(), "10001", "10001", bizSource,null);
            }
        }
    }

    private String tryGetYkNick() {
        try {
            TYIDManagerWrapper mTYIDManager = TYIDManagerWrapper.get(CoreApplication.getApplication());
//            AppDebug.d(TAG, "loginId:" +
            JSONObject hashMap = new JSONObject();
            hashMap.put("api", "yunosGetYouKuLoginInfo");
            hashMap.put("license", SystemProUtils.getLicense());
            String peekToken = mTYIDManager.peekToken(hashMap.toString());
            AppDebug.d(TAG, "yunosGetYouKuLoginInfo info:" + peekToken);
            if(!TextUtils.isEmpty(peekToken)){
                JSONObject jsonObject = new JSONObject(peekToken);
                return jsonObject.optString("username");
            }
        } catch (NoSuchMethodError e) {
            AppDebug.e(TAG, "yunosGetYouKuLoginInfo NoSuchMethodError " + e);
            // 兼容老版本，使用老的接口
//            if (mTYIDManager != null) {
//                String appkey = "21590507";
//                if (Config.getRunMode().compareTo(RunMode.DAILY) == 0) {
//                    appkey = "4272";
//                }
//                try {
//                    AppDebug.i(TAG, "yunosGetUserInfo deviceId=" + CloudUUIDWrapper.getCloudUUID());
//                    mTYIDManager.yunosApplyMtopToken(Config.getTTid(), appkey, "yunostvtaobao",
//                            CloudUUIDWrapper.getCloudUUID(), callback, mHandler);
//                } catch (Exception e2) {
//                    AppDebug.e(TAG, "yunosGetUserInfo TYIDException error e1=" + e2);
//                }
//            } else {
//                SharePreferences.put("account_tr", true);
//            }
        } catch (Exception e1) {
            AppDebug.e(TAG, "yunosGetYouKuLoginInfo TYIDException error e1=" + e1);
        }
        return null;
    }


    /**
     * 当需要隐藏对话框时，记录对话框信息
     */
    private void onNeedHideDialog() {
        TbTvShopNeedResumeInfo tbTvShopNeedResumeInfo = new TbTvShopNeedResumeInfo();
        tbTvShopNeedResumeInfo.mNeedResumePage = NEED_RESUME_PAGE.TVSHOP_ALL_COLLECT_PAGE;
        tbTvShopNeedResumeInfo.mId = mId;
        tbTvShopNeedResumeInfo.mAllCollectDefaultIndex = mCurPosition;
        if (mTbTvShoppingManager != null) {
            mTbTvShoppingManager.setTbTvShopNeedResumeInfo(tbTvShopNeedResumeInfo);
        }
        mDismissImmediately = true;
        dismiss();
    }

    /**
     * 更新数据列表，提取所有的推荐商品，并将已收藏商品放在列表前面
     *
     * @param itemDataList
     */
    private void updateDataList(List<TbTvShoppingItemData> itemDataList) {
        if (mNewTvShopItemDataList == null) {
            mNewTvShopItemDataList = new ArrayList<TbTvShoppingManager.TbTvShoppingItemData>();
        }
        mNewTvShopItemDataList.clear();

        if (itemDataList == null || itemDataList.size() == 0) {
            return;
        }

        List<Long> itemIds = null;
        for (TbTvShoppingItemData itemData : itemDataList) {// 先取得需要显示的商品id
            if (itemData.getTbTvShoppingItemBo() != null) {
                if (itemData.getTbTvShoppingItemBo().getShopType() == ShopType.LIST) {
                    itemIds = itemData.getTbTvShoppingItemBo().getItemIds();
                }
            }
        }

        AppDebug.v(TAG, TAG + ".updateDataList.itemIds = " + itemIds);
        // 将要显示的商品保存在列表中。
        List<TbTvShoppingItemData> tempDataList = new ArrayList<TbTvShoppingManager.TbTvShoppingItemData>();
//        if (itemIds == null || itemIds.size() == 0) {// 当没有指定要显示的itemId时，显示所有。
        for (TbTvShoppingItemData itemData : itemDataList) {
            if (itemData.getTbTvShoppingItemBo() != null) {
                if (itemData.getTbTvShoppingItemBo().getShopType() == ShopType.SINGLE) {
                    tempDataList.add(itemData);
                }
            }
        }
//        } else {// 当指定有需要显示的itemId时，显示指定商品
//            for (int i = 0; i < itemIds.size(); i++) {
//                long itemid = -1;
//                if (itemIds.get(i) != null) {
//                    itemid = itemIds.get(i).longValue();
//                }
//                if (itemid > 0) {// 取出该商品，保存在列表中
//                    for (TbTvShoppingItemData itemData : itemDataList) {
//                        if (itemData.getTbTvShoppingItemBo() != null) {
//                            if (itemData.getTbTvShoppingItemBo().getShopType() == ShopType.SINGLE
//                                    && itemData.getTbTvShoppingItemBo().getItemId() == itemid) {
//                                tempDataList.add(itemData);
//                                break;
//                            }
//                        }
//                    }
//                }
//            }

//        }

        if (tempDataList == null || tempDataList.size() == 0) {
            return;
        }

        for (TbTvShoppingItemData itemData : tempDataList) {
            if (itemData != null && itemData.isFavor()) {// 先将已经收藏的拿出来
                mNewTvShopItemDataList.add(itemData);
            }
        }

        AppDebug.i(TAG, TAG + ".updateDataList.favor size = " + mNewTvShopItemDataList.size());

        for (TbTvShoppingItemData itemData : tempDataList) {
            if (itemData != null && !itemData.isFavor()) {// 再将剩余的未收藏的拿出来
                mNewTvShopItemDataList.add(itemData);
            }
        }

        AppDebug.i(TAG, TAG + ".updateDataList.!favor size = " + mNewTvShopItemDataList.size());
    }

    /**
     * 获取该卡片的id
     *
     * @return
     */
    public long getId() {
        return mId;
    }

    @Override
    public void show() {
        AppDebug.i(TAG, TAG + ".show.dialog = " + this + ".mTbTvShoppingManager = " + mTbTvShoppingManager);
        if (mAnimationIn != null && mFocusPositionManager != null) {
            mFocusPositionManager.startAnimation(mAnimationIn);
        }
        if (mTbTvShoppingManager != null) {
            mTbTvShoppingManager.clearShowingDialog();
            super.show();
            // 显示商品后开启关闭倒计时
            TbTvShoppingManager.getIntance().setCountDownClearShowingDialogTimer();
            mTbTvShoppingManager.addShowingDialog(this);
        }

        Map<String, String> mapK = Utils.getProperties();
        mapK.put("is_sign", CoreApplication.getLoginHelper(mContext).isLogin() ? "1" : "0");
        String videoName = mTbTvShoppingManager.getmCurrTvShopReceiverData().getVideoName();
        AppDebug.i(TAG, "ut_Expore_BKBT videoName = " + videoName);
        mapK.put("video_name", videoName);
        Utils.utCustomHit(getFullPageName(), "Expore_BKBT", mapK);
    }

    @Override
    public void dismiss() {
        AppDebug.i(TAG, TAG + ".dismiss.dialog = " + this + ".mDismissImmediately = " + mDismissImmediately);
        if (mAnimationOut != null && !mDismissImmediately) {
            mAnimationOut.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                    mIsDismissing = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mIsDismissing = false;
                    TvShopAllCollectDialog.super.dismiss();
                    onDestory();
                }
            });
            if (mFocusPositionManager != null) {
                mFocusPositionManager.startAnimation(mAnimationOut);
            }
        } else {
            mDismissImmediately = false;
            super.dismiss();
            TbTvShoppingManager.getIntance().cancelCountDownClearShowingDialogTimer();
            onDestory();
        }
    }

    @Override
    protected void changedNetworkStatus(boolean available) {
        if (available) {
            if (mNetworkDialog != null && mNetworkDialog.isShowing()) {
                mNetworkDialog.dismiss();
                mNetworkDialog = null;
            }
        }
        super.changedNetworkStatus(available);
    }

    /**
     * 当对话框销毁时
     */
    private void onDestory() {
        AppDebug.v(TAG, TAG + ".onDestory");
        if (mTbTvShoppingManager != null) {
            mTbTvShoppingManager.deleteShowingDailog(this);
            mTbTvShoppingManager.setShopItemVisible(mId, false);
            mTbTvShoppingManager.unregisterTbTvShoppingHideListener(mOnTbTvShoppingHideListener);
        }

        if (mAllColectAdapter != null) {
            mAllColectAdapter.destory();
        }

        if (mNewTvShopItemDataList != null) {
            mNewTvShopItemDataList.clear();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        AppDebug.v(TAG, TAG + ".dispatchKeyEvent.keyCode = " + event.getKeyCode());
        if (mIsDismissing) {// 正在消失
            return true;
        }
        if (mFocusHListView != null) {
            // 此处为了解决，当mFocusHListView获得焦点时，并且在按向下键，出现商品选中项缩小的BUG
            boolean focus = mFocusHListView.isFocused();
            if (focus) {
                int keyCode = event.getKeyCode();
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    return true;
                }
            }
        }

        return super.dispatchKeyEvent(event);
    }

    /**
     * 影片最后的推荐列表
     *
     * @author mi.cao
     * @data 2015年1月13日 上午9:33:17
     */
    public static class AllColectAdapter extends BaseAdapter {

        private String TAG = "AllColectAdapter";
        // layout加载类
        private LayoutInflater mLayoutInflater;
        // 数据列表
        private List<TbTvShoppingItemData> mTvShopItemDataList;
        // 图片下载器
        private ImageLoaderManager mImageLoaderManager;
        // 图片下载参数配置
        private DisplayImageOptions options;
        private Bitmap mDefaultBitmap;// 默认图片
        private int deviceMinMetrics;// 设备分辨率

        public AllColectAdapter(Context context, List<TbTvShoppingItemData> timeShopItemDataList) {
            mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mTvShopItemDataList = new ArrayList<TbTvShoppingItemData>();
            mTvShopItemDataList.addAll(timeShopItemDataList);

            mDefaultBitmap = BitmapFactory
                    .decodeResource(context.getResources(), R.drawable.ytshop_goods_image_default);
            mImageLoaderManager = ImageLoaderManager.getImageLoaderManager(context);
            options = new DisplayImageOptions.Builder().displayer(new RoundedBitmapDisplayer(0)).cacheInMemory(true)
                    .cacheOnDisc(false).showImageOnFail(R.drawable.ytshop_goods_image_default)
                    .showImageForEmptyUri(R.drawable.ytshop_goods_image_default).bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            deviceMinMetrics = dm.widthPixels < dm.heightPixels ? dm.widthPixels : dm.heightPixels;
        }

        @Override
        public int getCount() {
            if (mTvShopItemDataList == null || mTvShopItemDataList.size() == 0) {
                return 0;
            }
            return mTvShopItemDataList.size();
        }

        @Override
        public Object getItem(int position) {
            if (mTvShopItemDataList == null || mTvShopItemDataList.size() == 0) {
                return null;
            }
            return mTvShopItemDataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = mLayoutInflater.inflate(R.layout.ytshop_colect_item, null);
                holder = new Holder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.collect_goods_image);
                holder.biaozhu = (ImageView) convertView.findViewById(R.id.collected_biaozhu);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            if (position < 0 || position > mTvShopItemDataList.size() - 1) {
                AppDebug.e(TAG, TAG + ".getView.position = " + position);
                return convertView;
            }

            TbTvShoppingItemData itemData = mTvShopItemDataList.get(position);
            // 显示商品图片
            String imageUrl = null;
            if (itemData != null) {
                TbTvShoppingItemBo itemBo = itemData.getTbTvShoppingItemBo();
                if (itemBo != null && !TextUtils.isEmpty(itemBo.getItemImage())) {
                    imageUrl = itemBo.getItemImage();
                }

                if (!TextUtils.isEmpty(imageUrl)) {
                    AppDebug.i(TAG, TAG + ".getView.position = " + position + ",imageUrl = " + imageUrl
                            + ", mDefaultBitmap = " + mDefaultBitmap);
                    mImageLoaderManager.displayImage(imageUrl, holder.imageView, options,
                            new SimpleImageLoadingListener() {

                                @Override
                                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                    if (view != null) {
                                        ImageView imageView = (ImageView) view;
                                        if (mDefaultBitmap != null && !mDefaultBitmap.isRecycled()) {
                                            imageView.setImageBitmap(mDefaultBitmap);
                                        }
                                    }
                                    super.onLoadingFailed(imageUri, view, failReason);
                                }

                                @Override
                                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                    ImageView imageView = (ImageView) view;
                                    if (imageView != null && loadedImage != null && !loadedImage.isRecycled()) {
                                        if (deviceMinMetrics > 720) {// 1080p
                                            imageView.setImageBitmap(loadedImage);
                                        } else {
                                            imageUri = ImageUtil.getFixSizeImage(imageUri,
                                                    (int) (loadedImage.getWidth() / 1.5),
                                                    (int) (loadedImage.getHeight() / 1.5));
                                            AppDebug.i(TAG, TAG + ".onLoadingComplete.reLoad imageUri = " + imageUri);
                                            mImageLoaderManager.displayImage(imageUri, imageView, options,
                                                    new SimpleImageLoadingListener() {

                                                        @Override
                                                        public void onLoadingComplete(String imageUri, View view,
                                                                                      Bitmap loadedImage) {
                                                            ImageView imageView = (ImageView) view;
                                                            if (imageView != null && loadedImage != null
                                                                    && !loadedImage.isRecycled()) {
                                                                imageView.setImageBitmap(loadedImage);
                                                            }
                                                        }
                                                    });
                                        }
                                    }
                                    super.onLoadingComplete(imageUri, view, loadedImage);
                                }
                            });
                } else {
                    if (mDefaultBitmap != null && !mDefaultBitmap.isRecycled()) {
                        holder.imageView.setImageBitmap(mDefaultBitmap);
                    }
                }

                if (itemData.isFavor()) {// 如果已经收藏
                    AppDebug.v(TAG,
                            TAG + ".getView.isFavor.position = " + position + ".itemId = " + itemData.getItemId());
                    holder.biaozhu.setVisibility(View.VISIBLE);
                } else {
                    holder.biaozhu.setVisibility(View.INVISIBLE);
                }
            }

            return convertView;
        }

        public void destory() {
            try {
                if (mDefaultBitmap != null && !mDefaultBitmap.isRecycled()) {
                    mDefaultBitmap.recycle();
                    mDefaultBitmap = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public static class Holder {

            ImageView imageView;// 图片
            ImageView biaozhu;// 是否已收藏的标注
        }
    }

    public static class Builder {

        private Context mContext;
        // 推荐商品唯一ID
        private long mId;
        private int mDefaultIndex;

        public Builder(Context context) {
            mContext = context;
            mId = -1;
            mDefaultIndex = 0;
        }

        /**
         * 设置所有商品的id
         *
         * @param id
         */
        public Builder setAllCollectId(long id) {
            mId = id;
            return this;
        }

        public Builder setAllCollectDefaultIndex(int index) {
            mDefaultIndex = index;
            return this;
        }

        public TvShopAllCollectDialog create() {
            TvShopAllCollectDialog dialog = new TvShopAllCollectDialog(mContext, R.style.ytbv_CustomDialog, mId,
                    mDefaultIndex);

            return dialog;
        }
    }

    @Override
    public Map<String, String> initProperties(Map<String, String> p) {
        p = super.initProperties(p);
        if (null != mTbTvShoppingManager) {
            p.put("item_num", String.valueOf(mTbTvShoppingManager.getSingleItemNum()));//item_num ：全部商品数

            List<TbTvShoppingItemData> favItems = mTbTvShoppingManager.getFavorItemList();
            if (null != favItems) {
                p.put("fav_item_num", String.valueOf(favItems.size()));//fav_item_num：收藏的商品数
            }
        }
        return p;
    }
}
