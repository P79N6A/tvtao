package com.yunos.tv.app.widget;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.yunos.tv.app.widget.Interpolator.Circ;
import com.yunos.tv.app.widget.Interpolator.TweenInterpolator;
import com.yunos.tv.app.widget.focus.FocusRelativeLayout;

public class RotateLayout extends FocusRelativeLayout {

	private static final String TAG = "RotateLayout";
	private static final boolean DEBUG = true;
	
	private Info mInfo = new Info();
	private ClickInfo mClickInfo = new ClickInfo();
	private boolean isRun;
	private Camera camera;
	private RectF dstRectF;// 设置alpha需要
	private Matrix comMatrix;
	private DisplayMetrics metrics;
	private State mState = State.normal;
	//当前在可视区域的位置
    protected int curVisibleIndex = 0;
    
    /**
     * 入场时第一个可见的item位置,只有入场时才有效,移动时值不一定准确!!!!!!!!!
     * 
     */
    protected int mFirstVisiblePositionOnFlow = 0;
	
	private RotateListener rotateListener;
	    
    private int originLeft = 0;//移动前view的位置
    private int originCenter = 0;
    private int shift;
   
    //默认入场延迟时间列表
    private static int[] xShift =new int[]{100,75,50,25,0,0};
    
//    private static int[] admitDelay =new int[]{0,100,267,367,467,567,667,767,867};
    private static int[] adminDelayList = null;
    private static int adminDelayDetla = 0;
    private int admitDelay = 0;
    //scale相关 暂时未开启
    private static float[] scaleParams=new float[]{0.9f,0.85f,0.8f,0.75f,0.7f,0.65f};//scale参数相关
  
    
	public RotateLayout(Context context) {
		super(context);
		initView(context);
	}

	public RotateLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public RotateLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		camera = new Camera();
		metrics = getResources().getDisplayMetrics();
	}

	
	//设置动画移动圆圈轨迹半径
    public void setAnimateMoveRadius(int xMoveRadius) {
        mInfo.xMoveRadius = xMoveRadius;
    }
	
	/**
	 * 默认为60°和1s
	 * @param rotateDegree
	 * @param rotateDuration
	 */
	public void setRotateParams(int rotateDegree, int rotateDuration) {
		mInfo.rotateDegree = rotateDegree;
		mInfo.rotateDuration = rotateDuration;
	}
	
	public void setAlphaDuration(int alphaDuration){
		mInfo.alphaDuration = alphaDuration;
	}
	
	public void setScaleDuration(int scaleDuration){
		mInfo.scaleDuration = scaleDuration;
	}
	
	public void setTranslateDuration(int translateDuration){
		mInfo.transDuration = translateDuration;
	}
	
	//Z轴移动距离
	public void setTranslateZ(int translateZ) {
		mInfo.translateZ = translateZ;
	}
	
	public void setRotateListener(RotateListener l) {
		rotateListener = l;
	}
	
	/**
	 * 获取当前位置的delay
	 * 第一个可见的item的delay为0,每个item的delay从第一个delay开始重新计算
	 */
	private int getDelay(){
	    int delay = admitDelay - getAdmitDelayByPosition(mFirstVisiblePositionOnFlow);
	    return delay > 0 ? delay : 0;
	}
	
	/**
	 * 设置当前入场的延迟
	 * @param delays
	 */
	public void setRotateDelay(int delay){
	    admitDelay = delay;
	}
	
	/**
	 * 设置入场动画延迟列表
	 * @param delay
	 */
	public static void setRotateDelayList(int[] delayList,int detla){
	    adminDelayList = delayList;
	    adminDelayDetla = detla;
    }
	
    /**
     * 根据位置获取delay的时间
     * @param pos
     * @return
     */
    public int getAdmitDelayByPosition(int pos){
        if(adminDelayList != null && pos >= 0){
            if(pos < adminDelayList.length){
                return adminDelayList[pos];
            } else {
                return adminDelayList[adminDelayList.length -1] + (pos - adminDelayList.length + 1) * adminDelayDetla;
            }
        }
        
        return adminDelayDetla;
    }
	
	/**
	 * 设置旋转轴X坐标(百分比)
	 * @param expandDistance
	 */
	public void setRotateX(float rotateX) {
		mInfo.rotateX = rotateX;
	}
	
	public void setFirstVisiblePositionOnFlow(int pos){
	    mFirstVisiblePositionOnFlow = pos;
	}
	
	/**
	 * 设置当前元素在可视区域的位置
	 * @param i
	 */
	public void setCurrentVisibleIndex(int i){
	    curVisibleIndex = i;
	}
	
	public void setShiftX(int shift){
		this.shift = shift;
	}
	
	public void setClickParams(int clickScaleDuration, int midScale) {
		mClickInfo.clickScaleDuration = clickScaleDuration;
		mClickInfo.midScale = midScale;
	}
	    
    public boolean IsRunning(){
        return isRun;
    }
	
	public enum State {
		normal, rotate, click,
	}
	
	public void start(){
		if(isRun && mState == State.click) {
			isRun = false;
			mState = State.normal;
			mClickInfo.stop();
		}
	    if(!isRun){
            isRun = true;
            mState = State.rotate;
            originLeft = getLeft();
            originCenter = (getLeft()+getRight())/2;
            int delay = getDelay();
    	    postDelayed(new Runnable() {
                @Override
                public void run() {
                    startRotate();
                }
            }, delay);
	    }
	    invalidate();
	}

    public void stop(){
        isRun = false;
        mState = State.normal;
        mWaitStartFlow = false;
        offsetLeftAndRight(originLeft - getLeft());
        invalidate();
        rotateStop();
    }
	
	public void click() {
		if(!isRun) {
			isRun = true;
			mState = State.click;
			frames = 0;
			mClickInfo.start();
		}
		invalidate();
	}
	
	@Override
	protected void onDetachedFromWindow() {
	    super.onDetachedFromWindow();
	}
	
	private int frames = 0;
	/**
	 * 开始旋转
	 */
	private void startRotate() {
		if(DEBUG)
			Log.d(TAG,"index:" + curVisibleIndex + ",startRotate:"+curVisibleIndex);
	    if(isRun){
            if(rotateListener != null) {
                rotateListener.rotateStart();
            }
            frames = 0;
    	    mInfo.start();
//            invalidate();
	    }
	}
	
	/**
	 * 给继承者做逻辑处理
	 */
	protected void rotateStop() {
		if(rotateListener != null) {
            rotateListener.rotateStop();
        }
	}
	
	public boolean mWaitStartFlow = false;
	@Override
	protected void dispatchDraw(Canvas canvas) {
//		Log.d(TAG, "dispatch draw isrun"+isRun +";isStarted:"+mInfo.started+";index:" + curVisibleIndex);
	    if(mWaitStartFlow){
	    	Log.d(TAG, "forgive invilidate  because wait startflow!!!"+curVisibleIndex);
	        return;
	    }
	    
	    if(isRun && !mInfo.started && !mClickInfo.started){
	    	Log.d(TAG, "forgive invilidate because delay!"+curVisibleIndex);
	        return;
	    }
	    
	    if(isRun && mInfo.started){
	    	drawRotate(canvas);
	    } else if (isRun && mClickInfo.started) {
	    	drawClick(canvas);
	    }else {
	        super.dispatchDraw(canvas);
	    }
	}
	
	private void drawClick(Canvas canvas) {
		if(mClickInfo.computeOffset()) {
			frames++;
			setScaleX(mClickInfo.currentS);
			setScaleY(mClickInfo.currentS);
			super.dispatchDraw(canvas);
			invalidate();
		} else {
			if(DEBUG)
	    		Log.d(TAG,"index:" + curVisibleIndex + "interval,finished:frames:" + frames*1000/mClickInfo.clickScaleDuration);
			super.dispatchDraw(canvas);
			isRun = false;
			mState = State.normal;
			if(rotateListener != null) {
                rotateListener.clickFinish();
            }
		}
		if(DEBUG)
			Log.d(TAG, "index:"+curVisibleIndex+"Info:"+mClickInfo.toString());
	}
	
	public boolean isRotateLayoutFinished() {
		return !mInfo.started;
	}
	
	public int getRotateLayoutOffset() {
		boolean hr = mInfo.computeOffset();
		if(hr) {
			mWaitStartFlow = false;
			return mInfo.currentX - (getLeft() - originLeft);
		}
		isRun = false;
		mState = State.normal;
		rotateStop();
		return 0;
	}
	
	private void drawRotate(Canvas canvas) {
		if(mInfo.started) {
			frames++;
			int saveCount = canvas.save();
//	        offsetX = mInfo.currentX - (getLeft() - originLeft);
//	        setScaleX(mInfo.currentS);
//	        setScaleY(mInfo.currentS);
	        rotate(canvas);	        
	        if(mInfo.isAlphaRun) {
	        	canvas.saveLayerAlpha(dstRectF, mInfo.currentA,Canvas.ALL_SAVE_FLAG);
	        }
	        super.dispatchDraw(canvas);
	        canvas.restoreToCount(saveCount);
		} else {
			if(DEBUG)
	    		Log.d(TAG,"index:" + curVisibleIndex + "interval,finished:frames:" + frames*1000/mInfo.rotateDuration);
			super.dispatchDraw(canvas);
		}
//		Log.d(TAG, "index:"+curVisibleIndex+"Info:"+mInfo.toString());
	}

	private void rotate(Canvas canvas){
//		Log.d(TAG, "rotate: "+ mInfo.rotateX+ ";curz:"+mInfo.currentZ +"curD:"+mInfo.currentDegree+"index:" + curVisibleIndex);
	    int centerY = getHeight() / 2;//Y轴中心点
//      int centerX = getWidth() / 2;//X轴中心点
        int centerX = (int)(getWidth() * mInfo.rotateX); 
        // 整体变换矩阵，3D旋转
        if(dstRectF == null)
        	dstRectF = new RectF(0, 0, getWidth(), getHeight());
        comMatrix = new Matrix();
        comMatrix.reset();
        
        camera.save();
        camera.translate(0, 0,mInfo.currentZ);
        camera.rotateY(mInfo.currentDegree);
        camera.getMatrix(comMatrix);
        camera.restore();
        comMatrix.preTranslate(-centerX , -centerY);
        comMatrix.postTranslate(centerX , centerY);
        canvas.concat(comMatrix);
        onRotate(mInfo.currentDegree);
	}
	
	//当前正在翻转的角度
	protected void onRotate(float angle){
	    
	}
	
	public interface RotateListener {
		public void rotateStart();
		public void rotateStop();
		public void clickFinish();
	}
	
	public class Info {
		int rotateDuration = 1000;
		int alphaDuration = 533;
		int scaleDuration = 900;
		int transDuration = 900;
		
		int rotateDegree = 60;
		float rotateX = 0.5f;
		int translateZ;//Z轴移动距离
		int zMoveRadius;//Z轴圆半径(根据translateZ和rotateDegre计算得出)
		int xMoveRadius;//X轴圆半径
		
		int startAlpha = 0;//alpha初始值
		int finalAlpha = 255;//alpha最终值
		float startScale = 1.0f;//放大初始值
		float finalScale = 1.0f;//放大最终值
		int translateX = 0;//平移距离
		
		int currentX = 0;
		float currentDegree = 0f;
		int currentZ = 0;
		int currentA = 0;
		float currentS = 0f;
		boolean isAlphaRun = false;
		boolean started = false;
		
		TweenInterpolator degreeInterpolator = new Circ.easeOut();
		Scroller alphaScroller = new Scroller(getContext(), new LinearInterpolator());
		Scroller scaleScroller = new Scroller(getContext(), new LinearInterpolator());
		Scroller translateScroller = new Scroller(getContext(), new AccelerateDecelerateInterpolator());
		Scroller rotateScroller = new Scroller(getContext(), new LinearInterpolator());
//		Scroller rotateScroller = new Scroller(getContext(), new DecelerateInterpolator(1.5f));
		
		public void start() {
			if(translateZ !=0 && rotateDegree != 0) {
				zMoveRadius = (int)(translateZ/(1-Math.cos(rotateDegree * Math.PI / 180)));
    	    } else {
    	    	zMoveRadius = xMoveRadius;
    	    }
			translateX = getXshift(curVisibleIndex);
    	    isAlphaRun = true;
    	    started = true;
    	    startScale = scaleParams[0];
    	    alphaScroller.startScroll(startAlpha, 0, finalAlpha-startAlpha, 0, alphaDuration);
    	    scaleScroller.startScroll((int)(startScale*1000), 0, (int)((finalScale-startScale)*1000), 0, scaleDuration);
    	    translateScroller.startScroll(0, 0, translateX, 0, transDuration);
//    	    rotateScroller.startScroll(0, 0, rotateDegree*1000, 0, rotateDuration);
    	    degreeInterpolator.setDuration(rotateDuration);
    	    degreeInterpolator.setStartAndTarget(rotateDegree,0);
    	    rotateScroller.startScroll(0, 0, rotateDuration, 0, rotateDuration);
    	    
    	    //赋初值
    	    currentA = startAlpha;
    	    currentS = startScale*1000;
    	    currentDegree = rotateDegree;
    	    currentX = (int)(xMoveRadius * Math.sin(currentDegree * Math.PI / 180));    //x轴移动的距离
    	    currentZ = zMoveRadius - (int)(zMoveRadius * Math.cos(currentDegree * Math.PI / 180));//z轴移动的距离
		}
		
		public int getXshift(int index) {
			if (index >= 0 && index < xShift.length) {
				return xShift[index];
			} else {
				return xShift[xShift.length-1];
			}
		}
		
		public boolean computeOffset() {
			boolean hr = false;
			currentX = 0;
			if(alphaScroller.computeScrollOffset()) {
				currentA = alphaScroller.getCurrX();
				hr = true;
			} else {
				isAlphaRun = false;
			}
			
			if(scaleScroller.computeScrollOffset()) {
				currentS = (float)(scaleScroller.getCurrX())/1000;
				hr = true;
			} else {
				currentS = 1.0f;
			}
			
			if(rotateScroller.computeScrollOffset()) {
//				currentDegree = rotateDegree - (float)rotateScroller.getCurrX()/1000; //移动的度数
//				currentX = (int)(xMoveRadius * Math.sin(currentDegree * Math.PI / 180));    //x轴移动的距离
//	    	    currentZ = zMoveRadius - (int)(zMoveRadius * Math.cos(currentDegree * Math.PI / 180));//z轴移动的距离
				currentDegree = degreeInterpolator.getValue(rotateScroller.getCurrX());
	    	    currentX = (int)(xMoveRadius * Math.sin(currentDegree * Math.PI / 180));    //x轴移动的距离
	    	    currentZ = zMoveRadius - (int)(zMoveRadius * Math.cos(currentDegree * Math.PI / 180));//z轴移动的距离
	    	    hr = true;
			}
			
			if(translateScroller.computeScrollOffset()) {
//				currentX += translateX - translateScroller.getCurrX();
				hr = true;
			}
			
			started =hr;
			return hr;
		}
		
		public String toString() {
			return "x:"+currentX+";z:"+currentZ+";a:"+currentA+";d:"+currentDegree+";s:"+currentS;
		}
	}
	
	public class ClickInfo {
		int clickScaleDuration = 300;
		float startScale = 1.0f;
		float midScale = 0.8f;
		float currentS;
		boolean started;
		Scroller clickScroller = new Scroller(getContext(), new LinearInterpolator());
		
		public void start() {
			started = true;
			clickScroller.startScroll((int)((startScale - midScale)*1000), 0, (int)((midScale-startScale)*2000), 0, clickScaleDuration);
		}
		
		public boolean computeOffset() {
			boolean hr = false; 
			if(clickScroller.computeScrollOffset()) {
				hr = true;
				int x = clickScroller.getCurrX();
				if(x > 0) {
					currentS = midScale + (float)x/1000;
				} else {
					currentS = midScale - (float)x/1000;
				}
			} 
			started = hr;
			return hr;
		}
		
		public void stop() {
			clickScroller.forceFinished(true);
			setScaleX(startScale);
			setScaleY(startScale);
			started = false;
		}
		
		public String toString() {
			return "S"+currentS;
		}
	}
}
