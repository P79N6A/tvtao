package com.yunos.tvtaobao.biz.base;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tvtaobao.businessview.R;


/**
 * Created by xtt
 * on 2018/12/18
 * desc
 */
public class RightSideView extends RelativeLayout implements View.OnClickListener, View.OnFocusChangeListener {

    public static  String TAG = "RightSideView";
    //右侧穿透栏
    private ImageView fiv_pierce_home_focusd;
    private ImageView fiv_pierce_my_focusd;
    private ImageView fiv_pierce_cart_focusd;
    private ImageView fiv_pierce_red_packet_focusd;
    private ImageView fiv_pierce_block_focusd;
    private ImageView fiv_pierce_contact_focusd;
    private ImageView fiv_pierce_come_back_focusd;
    private ImageView fiv_pierce_red_jifen_focusd;
    private ImageView fiv_pierce_background;

    private TextView tv_pierce_home;
    private TextView tv_pierce_my;
    private TextView tv_pierce_cart;
    private TextView tv_pierce_red_packet;
    private TextView tv_pierce_block;
    private TextView tv_pierce_contact_focusd;
    private TextView tv_pierce_come_back;
    private TextView tv_pierce_red_jifen;

    private ImageView iv_pierce_cart_active;
    private Handler mHandler;

    private OnItemViewClickListener onItemViewClickListener;
    private OnItemViewFocusChangeListener onItemViewFocusChangeListener;
    private Context context;


    public RightSideView(Context context) {
        super(context);
        initView(context);
    }

    public RightSideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);

    }

    public RightSideView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public void setOnItemViewClickListener(OnItemViewClickListener onItemViewClickListener){
        this.onItemViewClickListener = onItemViewClickListener;
    }
    public void setOnItemViewFocusChangeListener(OnItemViewFocusChangeListener onItemViewFocusChangeListener){
        this.onItemViewFocusChangeListener = onItemViewFocusChangeListener;
    }

    public void initView(Context context){
        this.context = context;
        if(context instanceof  BaseTabMVPActivity){

        }
        LayoutInflater.from(context).inflate(R.layout.layout_right_sidebar, this);
        mHandler = new Handler();
        initPierceViews();
        setListener();
    }

    @Override
    public View findFocus() {
        View view = super.findFocus();
        AppDebug.i(TAG, "findFocus view : " + view);
        return view;
    }

    @Override
    public void requestChildFocus(View child, View focused) {

        super.requestChildFocus(child, focused);
        AppDebug.i(TAG, TAG + ".requestChildFocus child : " + child + " ,focused : " + focused);
    }

    @Override
    public View focusSearch(View focused, int direction) {
        AppDebug.i(TAG, TAG + ".focusSearch");

        return super.focusSearch(focused, direction);
    }

    public View findNextFocused(View focused, int direction) {
        AppDebug.i(TAG, TAG + ".findNextFocused lastFocusItem : ");

        return fiv_pierce_home_focusd;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        AppDebug.i(TAG, TAG + ".dispatchKeyEvent  : ");
        return super.dispatchKeyEvent(event);
    }

    private void initPierceViews() {
        //右边穿透
        fiv_pierce_home_focusd = (ImageView) this.findViewById(R.id.fiv_pierce_home_focusd);
        fiv_pierce_my_focusd = (ImageView) this.findViewById(R.id.fiv_pierce_my_focusd);
        fiv_pierce_cart_focusd = (ImageView) this.findViewById(R.id.fiv_pierce_cart_focusd);
        fiv_pierce_red_packet_focusd = (ImageView) this.findViewById(R.id.fiv_pierce_red_packet_focusd);
        fiv_pierce_block_focusd = (ImageView) this.findViewById(R.id.fiv_pierce_block_focusd);
        fiv_pierce_contact_focusd = (ImageView) this.findViewById(R.id.fiv_pierce_contact_focusd);
        fiv_pierce_come_back_focusd = (ImageView) this.findViewById(R.id.fiv_pierce_come_back_focusd);
        fiv_pierce_red_jifen_focusd = (ImageView) this.findViewById(R.id.fiv_pierce_red_jifen_focusd);
        fiv_pierce_background = (ImageView) findViewById(R.id.fiv_pierce_background);

        tv_pierce_home = (TextView) this.findViewById(R.id.tv_pierce_home);
        tv_pierce_my = (TextView) this.findViewById(R.id.tv_pierce_my);
        tv_pierce_cart = (TextView) this.findViewById(R.id.tv_pierce_cart);
        tv_pierce_red_packet = (TextView) this.findViewById(R.id.tv_pierce_red_packet);
        tv_pierce_block = (TextView) this.findViewById(R.id.tv_pierce_block);
        tv_pierce_contact_focusd = (TextView) this.findViewById(R.id.tv_pierce_contact_focusd);
        tv_pierce_come_back = (TextView) this.findViewById(R.id.tv_pierce_come_back);
        tv_pierce_red_jifen = (TextView) this.findViewById(R.id.tv_pierce_red_jifen);
        iv_pierce_cart_active = (ImageView) this.findViewById(R.id.iv_pierce_cart_active);
    }




    public void setListener(){
//        点击监听
        fiv_pierce_home_focusd.setOnClickListener(this);
        fiv_pierce_my_focusd.setOnClickListener(this);
        fiv_pierce_cart_focusd.setOnClickListener(this);
        fiv_pierce_red_packet_focusd.setOnClickListener(this);
        fiv_pierce_block_focusd.setOnClickListener(this);
        fiv_pierce_contact_focusd.setOnClickListener(this);
        fiv_pierce_red_jifen_focusd.setOnClickListener(this);


//        焦点监听
        fiv_pierce_home_focusd.setOnFocusChangeListener(this);
        fiv_pierce_my_focusd.setOnFocusChangeListener(this);
        fiv_pierce_cart_focusd.setOnFocusChangeListener(this);
        fiv_pierce_red_packet_focusd.setOnFocusChangeListener(this);

        fiv_pierce_block_focusd.setOnFocusChangeListener(this);
        fiv_pierce_contact_focusd.setOnFocusChangeListener(this);
        fiv_pierce_come_back_focusd.setOnFocusChangeListener(this);
        fiv_pierce_red_jifen_focusd.setOnFocusChangeListener(this);


    }







    @Override
    public void onClick(View v) {
        onItemViewClickListener.onItemViewClick(v);

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        onItemViewFocusChangeListener.onItemViewFocusChange(v,hasFocus);

    }


    public interface OnItemViewClickListener {
        void onItemViewClick(View view);
    }

    public interface OnItemViewFocusChangeListener {
        void onItemViewFocusChange(View view,boolean hasFocus);
    }

    public ImageView getFiv_pierce_home_focusd() {

        return fiv_pierce_home_focusd;
    }

    public ImageView getFiv_pierce_my_focusd() {
        return fiv_pierce_my_focusd;
    }

    public ImageView getFiv_pierce_cart_focusd() {
        return fiv_pierce_cart_focusd;
    }

    public ImageView getFiv_pierce_red_packet_focusd() {
        return fiv_pierce_red_packet_focusd;
    }

    public ImageView getFiv_pierce_block_focusd() {
        return fiv_pierce_block_focusd;
    }

    public ImageView getFiv_pierce_contact_focusd() {
        return fiv_pierce_contact_focusd;
    }

    public ImageView getFiv_pierce_come_back_focusd() {
        return fiv_pierce_come_back_focusd;
    }

    public ImageView getFiv_pierce_red_jifen_focusd() {
        return fiv_pierce_red_jifen_focusd;
    }

    public ImageView getFiv_pierce_background() {
        return fiv_pierce_background;
    }

    public TextView getTv_pierce_home() {
        return tv_pierce_home;
    }

    public TextView getTv_pierce_my() {
        return tv_pierce_my;
    }

    public TextView getTv_pierce_cart() {
        return tv_pierce_cart;
    }

    public TextView getTv_pierce_red_packet() {
        return tv_pierce_red_packet;
    }

    public TextView getTv_pierce_block() {
        return tv_pierce_block;
    }

    public TextView getTv_pierce_contact_focusd() {
        return tv_pierce_contact_focusd;
    }

    public TextView getTv_pierce_come_back() {
        return tv_pierce_come_back;
    }

    public TextView getTv_pierce_red_jifen() {
        return tv_pierce_red_jifen;
    }

    public ImageView getIv_pierce_cart_active() {
        return iv_pierce_cart_active;
    }
}
