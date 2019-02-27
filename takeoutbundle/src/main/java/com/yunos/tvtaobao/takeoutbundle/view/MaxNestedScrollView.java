package com.yunos.tvtaobao.takeoutbundle.view;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.util.DeviceUtil;
import com.yunos.tvtaobao.takeoutbundle.R;

/**
 * Created by chenjiajuan on 17/12/25.
 *
 * @describe
 */

public class MaxNestedScrollView extends NestedScrollView {

    private String TAG="MaxNestedScrollView";
    private int maxHeight=(int) getResources().getDimension(R.dimen.dp_566);

    public MaxNestedScrollView(Context context) {
        super(context);
    }

    public MaxNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        maxHeight= (int) getResources().getDimension(R.dimen.dp_566);

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
