package com.yunos.tvtaobao.menu.activity;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.taobao.statistic.CT;
import com.taobao.statistic.TBS;
import com.yunos.tv.app.widget.focus.FocusPositionManager;
import com.yunos.tv.app.widget.focus.StaticFocusDrawable;
import com.yunos.tv.blitz.account.LoginHelper;
import com.yunos.tv.core.CoreApplication;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.NetWorkUtil;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.activity.BaseActivity;
import com.yunos.tvtaobao.menu.R;

import java.util.Map;

public class MenuActivity extends BaseActivity implements OnClickListener, OnFocusChangeListener {

    private FocusPositionManager mFocusPositionManager;
    private boolean mHasGoneOtherActivity;// 是否已经进入其他界面了
    private boolean mManualFromLogin;// 手动的从login界面返回
    private Intent mIntent;
    private LoginHelper mLoginHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onKeepActivityOnlyOne(MenuActivity.class.getName());
        setContentView(R.layout.ytm_menu_activity);
        mFocusPositionManager = (FocusPositionManager) findViewById(R.id.menu_root_layout);
        mFocusPositionManager.setSelector(new StaticFocusDrawable(getResources().getDrawable(
                R.drawable.ytbv_common_focus)));
        mFocusPositionManager.requestFocus();

        View menuSearchLayout = findViewById(R.id.menu_search_layout);
        View menuShoppingCartLayout = findViewById(R.id.menu_shopping_cart_layout);
        View menuCollectLayout = findViewById(R.id.menu_collect_layout);
        View menuMyTaobaoLayout = findViewById(R.id.menu_my_taobao_layout);
        View menuSortLayout = findViewById(R.id.menu_sort_layout);

        menuSearchLayout.setOnClickListener(this);
        menuShoppingCartLayout.setOnClickListener(this);
        menuCollectLayout.setOnClickListener(this);
        menuMyTaobaoLayout.setOnClickListener(this);
        menuSortLayout.setOnClickListener(this);

        menuSearchLayout.setOnFocusChangeListener(this);
        menuShoppingCartLayout.setOnFocusChangeListener(this);
        menuCollectLayout.setOnFocusChangeListener(this);
        menuMyTaobaoLayout.setOnFocusChangeListener(this);
        menuSortLayout.setOnFocusChangeListener(this);

        ImageView itemImage = (ImageView) menuSearchLayout.findViewById(R.id.menu_item_image);
        itemImage.setImageResource(R.drawable.ytm_menu_search);
        TextView itemText = (TextView) menuSearchLayout.findViewById(R.id.menu_item_text);
        itemText.setText(getString(R.string.ytm_menu_search_text));
        itemText.setVisibility(View.VISIBLE);// 首次启动时 显示

        itemImage = (ImageView) menuShoppingCartLayout.findViewById(R.id.menu_item_image);
        itemImage.setImageResource(R.drawable.ytm_menu_shopping_cart);
        itemText = (TextView) menuShoppingCartLayout.findViewById(R.id.menu_item_text);
        itemText.setText(getString(R.string.ytm_menu_shopping_cart_text));

        itemImage = (ImageView) menuCollectLayout.findViewById(R.id.menu_item_image);
        itemImage.setImageResource(R.drawable.ytm_menu_collect);
        itemText = (TextView) menuCollectLayout.findViewById(R.id.menu_item_text);
        itemText.setText(getString(R.string.ytm_menu_collect_text));

        itemImage = (ImageView) menuMyTaobaoLayout.findViewById(R.id.menu_item_image);
        itemImage.setImageResource(R.drawable.ytm_menu_my_taobao);
        itemText = (TextView) menuMyTaobaoLayout.findViewById(R.id.menu_item_text);
        itemText.setText(getString(R.string.ytm_menu_my_taobao_text));

        itemImage = (ImageView) menuSortLayout.findViewById(R.id.menu_item_image);
        itemImage.setImageResource(R.drawable.ytm_menu_sort);
        itemText = (TextView) menuSortLayout.findViewById(R.id.menu_item_text);
        itemText.setText(getString(R.string.ytm_menu_sort_text));

        Animation animationIn = AnimationUtils.loadAnimation(this, R.anim.ytm_slide_in_bottom);
        mFocusPositionManager.startAnimation(animationIn);
        mFocusPositionManager.setVisibility(View.VISIBLE);

        mHasGoneOtherActivity = false;
        mManualFromLogin = false;
    }

    @Override
    protected void onDestroy() {
        AppDebug.v(TAG, TAG + ".onDestory");
        onRemoveKeepedActivity(MenuActivity.class.getName());
        if (mLoginHelper != null) {
            mLoginHelper.removeReceiveLoginListener(mSyncLoginListener);
        }

        super.onDestroy();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU && event.getAction() == KeyEvent.ACTION_DOWN) {
            onBackPressed();
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View v) {
        AppDebug.v(TAG, TAG + ".onClick.v = " + v);
        if (!NetWorkUtil.isNetWorkAvailable()) {
            showNetworkErrorDialog(false);
            return;
        }

        Map<String, String> p = Utils.getProperties();
        String controlName = "";
        mIntent = new Intent();

        int i = v.getId();
        if (i == R.id.menu_search_layout) {
            controlName = Utils.getControlName(getFullPageName(), "Seach", null, "Click");
            mIntent.setData(Uri.parse("tvtaobao://home?module=search&notshowloading=true"));

        } else if (i == R.id.menu_shopping_cart_layout) {
            controlName = Utils.getControlName(getFullPageName(), "Cart", null, "Click");
            mIntent.setData(Uri.parse("tvtaobao://home?module=cart&notshowloading=true"));

        } else if (i == R.id.menu_collect_layout) {
            controlName = Utils.getControlName(getFullPageName(), "Collect", null, "Click");
            setHuodong("column_collect");
            mIntent.setData(Uri.parse("tvtaobao://home?app=taobaosdk&module=collects&notshowloading=true"));

        } else if (i == R.id.menu_my_taobao_layout) {
            controlName = Utils.getControlName(getFullPageName(), "My_taobao", null, "Click");
            mIntent.setData(Uri.parse("tvtaobao://home?app=taobaosdk&module=mytaobao&notshowloading=true"));

        } else if (i == R.id.menu_sort_layout) {
            controlName = Utils.getControlName(getFullPageName(), "Category", null, "Click");
            mIntent.putExtra("category", "0");
            setHuodong("column_class");
            mIntent.setData(Uri.parse("tvtaobao://home?module=recommend&category=0&notshowloading=true"));

        }

        boolean bLogin = true;
        p.put("is_login", "true");
        mLoginHelper = CoreApplication.getLoginHelper(CoreApplication.getApplication());
        if (!mLoginHelper.isLogin()) {
            bLogin = false;
            p.put("is_login", "false");
        }

        TBS.Adv.ctrlClicked(CT.Button, controlName, Utils.getKvs(p));

        int i1 = v.getId();
        if (i1 == R.id.menu_shopping_cart_layout || i1 == R.id.menu_collect_layout || i1 == R.id.menu_my_taobao_layout) {
            if (!bLogin) {
                mManualFromLogin = true;// 先假设为ture
                mHasGoneOtherActivity = false;
                setLoginActivityStartShowing();
                mLoginHelper.addReceiveLoginListener(mSyncLoginListener);
                mLoginHelper.startYunosAccountActivity(this, false);

                return;
            }
        }

        gotoOtherActivity(mIntent);
    }

    /**
     * 登入成功监听
     */
    private LoginHelper.SyncLoginListener mSyncLoginListener = new LoginHelper.SyncLoginListener() {

        @Override
        public void onLogin(boolean isSuccess) {
            AppDebug.v(TAG, TAG + ".mSyncLoginListener.onLogin.isSuccess = " + isSuccess + ".intent = " + mIntent
                    + ".mLoginHelper = " + mLoginHelper + ".mManualFromLogin = " + mManualFromLogin
                    + ".mHasGoneOtherActivity = " + mHasGoneOtherActivity);

            if (isSuccess && !mHasGoneOtherActivity) {// 如果登入成功，则跳转到另一个界面
                mManualFromLogin = false;// 登入成功返回后置为false
                mHasGoneOtherActivity = true;
                gotoOtherActivity(mIntent);
            }
        }
    };

    @Override
    protected void onResume() {
        AppDebug.v(TAG, TAG + ".onResume.mManualFromLogin = " + mManualFromLogin);
        if (mManualFromLogin) {//如果从登入界面手动返回，表明没有登入，则销毁改界面
            mManualFromLogin = false;
            finish();
        }
        super.onResume();
    }

    ;

    /**
     * 进入其他activity
     *
     * @param intent
     */
    private void gotoOtherActivity(Intent intent) {
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }

        finish();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        AppDebug.v(TAG, TAG + ".onFocusChange.v = " + v + ".hasFocus = " + hasFocus);
        View textView = v.findViewById(R.id.menu_item_text);
        if (hasFocus) {
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        Animation animationOut = AnimationUtils.loadAnimation(this, R.anim.ytm_slide_out_bottom);
        mFocusPositionManager.startAnimation(animationOut);

        super.onBackPressed();
    }

    @Override
    public String getPageName() {
        return "Short_caut";
    }

    @Override
    protected FocusPositionManager getFocusPositionManager() {
        return (FocusPositionManager) findViewById(R.id.menu_root_layout);
    }

    @Override
    protected void onPause() {
        finish();
        super.onPause();
    }

    @Override
    public boolean isFirstNode() {
        return true;
    }
}
