package com.yunos.tvtaobao.takeoutbundle.activity.shophome_wares;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;
import android.widget.TextView;

import com.tvtaobao.voicesdk.utils.Util;
import com.yunos.tv.core.util.Utils;
import com.yunos.tvtaobao.biz.focus_impl.FocusArea;
import com.yunos.tvtaobao.biz.focus_impl.FocusConsumer;
import com.yunos.tvtaobao.takeoutbundle.R;
import com.yunos.tvtaobao.takeoutbundle.R2;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by GuoLiDong on 2018/10/18.
 */

public class RightMenuViewHolder {
    public static final String TAG = RightMenuViewHolder.class.getSimpleName();

    Context context;
    ConstraintLayout container;
    @BindView(R2.id.background)
    View background;
    @BindView(R2.id.start)
    Space start;

    int bgColor = Color.parseColor("#B2000000");
    int lineColor = Color.parseColor("#59AABBCC");
    int lineHeight = 1;

    MenuItem[] menuItems;
    int[] ids = new int[]{
            R.id.id_place_holder_1,R.id.id_place_holder_2,R.id.id_place_holder_3,
            R.id.id_place_holder_4,R.id.id_place_holder_5,R.id.id_place_holder_6,
            R.id.id_place_holder_7,R.id.id_place_holder_8,R.id.id_place_holder_9,
            R.id.id_place_holder_10,R.id.id_place_holder_11,R.id.id_place_holder_12,
            R.id.id_place_holder_13,R.id.id_place_holder_14,R.id.id_place_holder_15,
            R.id.id_place_holder_16,R.id.id_place_holder_17,R.id.id_place_holder_18,
            R.id.id_place_holder_19,R.id.id_place_holder_20,R.id.id_place_holder_21,
    };
    private Unbinder unbinder;

    public RightMenuViewHolder(final Context context, ViewGroup place) {
        this.context = context;
        container = (ConstraintLayout) LayoutInflater.from(this.context).inflate(R.layout.right_menu_layout, place).findViewById(R.id.right_menu);
        unbinder = ButterKnife.bind(this, container);
        lineHeight = Util.dip2px(context,0.7f);
        background.setBackgroundColor(bgColor);
    }

    public static class MenuItem {
        public View menuItemView; // 传入的布局
        public FocusConsumer focusConsumer; // 焦点消费
        public String tipTxt; // 聚焦时的提示文案
    }


    public void fillMenu(MenuItem... menuItems) {
        this.menuItems = menuItems;
        doRealFill();
    }

    private void doRealFill() {
        if (menuItems != null) {
            for (int i = container.getChildCount() - 1; i >= 0; i--) {
                View tmp = container.getChildAt(i);
                if (tmp == background || tmp == start) {
                    // 保留
                } else {
                    container.removeView(tmp);
                }
            }

            View iterator = start;
            int idIndex = 0;
            for (int i=0;i<menuItems.length && idIndex<ids.length;i++) {
                final MenuItem item = menuItems[i];
                final FocusArea area = new FocusArea(context);
                area.setId(ids[idIndex++]);
                ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.rightToRight=ConstraintLayout.LayoutParams.PARENT_ID;
                lp.topToBottom = iterator.getId();
                area.addView(item.menuItemView);
                area.setFocusConsumer(new FocusConsumer() {
                    @Override
                    public boolean onFocusEnter() {
                        tipOn(item.tipTxt,area);
                        if (item.focusConsumer!=null){
                            item.focusConsumer.onFocusEnter();
                        }
                        return true;
                    }

                    @Override
                    public boolean onFocusLeave() {
                        tipOff();
                        if (item.focusConsumer!=null){
                            item.focusConsumer.onFocusLeave();
                        }
                        return true;
                    }

                    @Override
                    public boolean onFocusClick() {
                        if (item.focusConsumer!=null){
                            item.focusConsumer.onFocusClick();
                        }
                        return true;
                    }

                    @Override
                    public boolean onFocusDraw(Canvas canvas) {
                        if (item.focusConsumer!=null){
                            return item.focusConsumer.onFocusDraw(canvas);
                        }
                        return false;
                    }
                });
                container.addView(area,lp);
                iterator=area;
                if (i<menuItems.length-1){
                    View line = new View(context);
                    line.setId(ids[idIndex++]);
                    line.setBackgroundColor(lineColor);
                    ConstraintLayout.LayoutParams lp2 = new ConstraintLayout.LayoutParams(0,lineHeight);
                    lp2.rightToRight=background.getId();
                    lp2.leftToLeft=background.getId();
                    lp2.topToBottom = iterator.getId();
                    container.addView(line,lp2);
                    iterator=line;
                }
            }
        }
    }

    public void setBackground(int resId) {
        background.setBackgroundDrawable(context.getResources().getDrawable(resId));
    }

    TextView tipTextView = null;

    private TextView getTipView() {
        TextView tmp = new TextView(context);
        tmp.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.getResources().getDimension(R.dimen.dp_17));
        tmp.setTextColor(Color.WHITE);
        tmp.setGravity(Gravity.CENTER_VERTICAL);
        TipBgDrawable tipBgDrawable = new TipBgDrawable();
        Rect padding = tipBgDrawable.getPadding();
        tmp.setPadding(padding.left, padding.top, padding.right, padding.bottom);
        tmp.setBackgroundDrawable(tipBgDrawable);
        return tmp;
    }

    private void tipOff() {
        if (tipTextView != null) {
            container.removeView(tipTextView);
        }
    }

    private void tipOn(String tip, View anchor) {
        if (tipTextView == null) {
            tipTextView = getTipView();
        }
        tipTextView.setText(tip);
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.rightToLeft = anchor.getId();
        lp.topToTop = anchor.getId();
        lp.bottomToBottom = anchor.getId();
        lp.rightMargin = (int) context.getResources().getDimension(R.dimen.dp_8);

        container.addView(tipTextView, lp);
    }

    public void destroy() {
        unbinder.unbind();
    }

    class TipBgDrawable extends Drawable {

        Paint paint;

        int radius = 8;
        int triangleHeight = 12; // 等腰直角三角形
        int paddingLeft = 8;
        int paddingRight = 8;
        RectF tmp = new RectF();

        public Rect getPadding() {
            Rect rtn = new Rect();
            rtn.set(radius+paddingLeft, radius, radius+paddingRight + triangleHeight, radius);
            return rtn;
        }

        public TipBgDrawable() {
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor("#B2000000"));
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            Rect bounds = getBounds();
            if (bounds != null) {
                Path path = new Path();
                path.moveTo(0, radius);

                tmp.set(0, 0, radius * 2, radius * 2);
                path.arcTo(tmp, 180, 90);

                path.lineTo(bounds.right - triangleHeight - radius, 0);

                tmp.set(bounds.right - triangleHeight - radius - radius, 0, bounds.right - triangleHeight, radius * 2);
                path.arcTo(tmp, 270, 90);

                path.lineTo(bounds.right - triangleHeight, bounds.centerY() - triangleHeight);

                path.lineTo(bounds.right, bounds.centerY());

                path.lineTo(bounds.right - triangleHeight, bounds.centerY() + triangleHeight);

                path.lineTo(bounds.right - triangleHeight, bounds.bottom - radius);

                tmp.set(bounds.right - triangleHeight - radius - radius, bounds.bottom - radius * 2, bounds.right - triangleHeight, bounds.bottom);
                path.arcTo(tmp, 0, 90);

                path.lineTo(bounds.left - radius, bounds.bottom);

                tmp.set(0, bounds.bottom - radius * 2, radius * 2, bounds.bottom);
                path.arcTo(tmp, 90, 90);

                path.close();

                canvas.drawPath(path, paint);
            }
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }
}
