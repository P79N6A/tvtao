package com.yunos.voice.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.yunos.voice.R;

import java.lang.ref.WeakReference;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pan on 2017/10/23.
 */

public class AutoTextView extends TextSwitcher implements
        ViewSwitcher.ViewFactory {
    private final int gravity;
    private float mHeight = 36;
    private int lines = 0;
    private int textColor;
    private Context mContext;
    //mInUp,mOutUp分离构成向下翻页的进出动画
    private Rotate3dAnimation mInUp;
    private Rotate3dAnimation mOutUp;

    //mInDown,mOutDown分离构成向下翻页的进出动画
    private Rotate3dAnimation mInDown;
    private Rotate3dAnimation mOutDown;

    private TimeHandler timeHandler;

    public AutoTextView(Context context) {
        this(context, null);
        // TODO Auto-generated constructor stub
    }
    public AutoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.autotext);
        mHeight = (float) (a.getDimension(R.styleable.autotext_textSize, 36)/1.5);
        textColor = a.getColor(R.styleable.autotext_textcolor, 0xffffffff);
        lines = a.getInt(R.styleable.autotext_lines, 0);
        gravity=a.getInt(R.styleable.autotext_gravity,0);
        a.recycle();
        mContext = context;
        init(context);

        timeHandler = new TimeHandler(this);
    }
    private void init(Context context) {
        // TODO Auto-generated method stub
        setFactory(this);
//        mInUp = createAnim(-90, 0 , true, true);
//        mOutUp = createAnim(0, 90, false, true);
//        mInDown = createAnim(90, 0 , true , false);
//        mOutDown = createAnim(0, -90, false, false);
        //TextSwitcher重要用于文件切换，比如 从文字A 切换到 文字 B，
        //setInAnimation()后，A将执行inAnimation，
        //setOutAnimation()后，B将执行OutAnimation

//        setInAnimation(mInUp);
//        setOutAnimation(mOutUp);
        setInAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_down_in));
        setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_up_out));
    }

//    private Rotate3dAnimation createAnim(float start, float end, boolean turnIn, boolean turnUp){
//        final Rotate3dAnimation rotation = new Rotate3dAnimation(start, end, turnIn, turnUp);
//        rotation.setDuration(800);
//        rotation.setFillAfter(false);
//        rotation.setInterpolator(new AccelerateInterpolator());
//        return rotation;
//    }

    //这里返回的TextView，就是我们看到的View
    @Override
    public View makeView() {
        // TODO Auto-generated method stub
        TextView t = new TextView(mContext);
//        t.setGravity(gravity);
        t.setTextSize(mHeight);
        if (lines != 0) {
            t.setMaxLines(lines);
            t.setEllipsize(TextUtils.TruncateAt.END);
        }
        t.setTextColor(textColor);
        t.setMaxLines(2);
        t.setGravity(Gravity.CENTER_VERTICAL|gravity);
        return t;
    }

    private List<String> strings = new ArrayList<>();
    public void autoScroll(List<String> s) {
        if (s == null || s.size() == 0) {
            return;
        }

        clear();
        strings.addAll(s);

        if (s.size() == 1) {
            setText(s.get(0));
            return;
        }

        timeHandler.sendEmptyMessage(0);
    }

    public void clear() {
        timeHandler.removeCallbacksAndMessages(null);
        timeHandler.clearCount();
        strings.clear();
    }

    //定义动作，向下滚动翻页
    public void previous(){
        if(getInAnimation() != mInDown){
            setInAnimation(mInDown);
        }
        if(getOutAnimation() != mOutDown){
            setOutAnimation(mOutDown);
        }
    }

    //定义动作，向上滚动翻页
    public void next(){
        if(getInAnimation() != mInUp){
            setInAnimation(mInUp);
        }
        if(getOutAnimation() != mOutUp){
            setOutAnimation(mOutUp);
        }
    }

    private static class TimeHandler extends Handler {
        private WeakReference<AutoTextView> mWeakReference;
        private int sCount = 0;
        public TimeHandler(AutoTextView view) {
            this.mWeakReference = new WeakReference<>(view);
        }

        public void clearCount() {
            sCount = 0;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 0:
                    if (mWeakReference != null && mWeakReference.get() != null) {
                        AutoTextView view = mWeakReference.get();
                        List<String> strings = view.strings;
                        view.setText(strings.get(sCount % strings.size()));

                        sCount++;
                        view.timeHandler.sendEmptyMessageDelayed(0, 8 * 1000);
                    }
                    break;
            }
        }
    }

    class Rotate3dAnimation extends Animation {
        private final float mFromDegrees;
        private final float mToDegrees;
        private float mCenterX;
        private float mCenterY;
        private final boolean mTurnIn;
        private final boolean mTurnUp;
        private Camera mCamera;
        public Rotate3dAnimation(float fromDegrees, float toDegrees, boolean turnIn, boolean turnUp) {
            mFromDegrees = fromDegrees;
            mToDegrees = toDegrees;
            mTurnIn = turnIn;
            mTurnUp = turnUp;
        }
        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            mCamera = new Camera();
            mCenterY = getHeight() / 2;
            mCenterX = getWidth() / 2;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            final float fromDegrees = mFromDegrees;
            float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);
            final float centerX = mCenterX ;
            final float centerY = mCenterY ;
            final Camera camera = mCamera;
            final int derection = mTurnUp ? 1: -1;
            final Matrix matrix = t.getMatrix();
            camera.save();
            if (mTurnIn) {
                camera.translate(0.0f, derection *mCenterY * (interpolatedTime - 1.0f), 0.0f);
            } else {
                camera.translate(0.0f, derection *mCenterY * (interpolatedTime), 0.0f);
            }
            camera.rotateX(degrees);
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate(-centerX, -centerY);
            matrix.postTranslate(centerX, centerY);
        }
    }
}
