package com.yunos.tv.app.widget.focus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.Log;

import com.yunos.tv.app.widget.focus.listener.DrawListener;

/**
 * 围绕着方形的区域边沿按顺时针的方向移动
 * 可以设置移动线的长度，粗细跟颜色的渐变效果
 * @author tim
 */
public class SnakeFocusDrawable implements DrawListener{
	private final String TAG = "AroundImageAreaView";
	private final boolean DEBUG = false;
	private final int AROUND_ARED_TOTAL_FRAME = 150;//贪食蛇跑一圈需要的总帧数
	private final int AROUND_AREA_STROKE_SIZE = 4;//移动线的粗细，需要为偶数
	private Paint mPaint;//画笔
	private Rect mAroundPadding;
	private int mAroundAreaLength = 1000;//长度必须大于mAroundAreadCorner*2的值
	private float mAroundAreaPos;//移动线在区域的位置，左上角为0，0点，按顺序针方向加上宽跟高
	private boolean mStop = true;//是否在停止状态
	private int mAroundAreadCorner = 24;//圆角的大小
	private RectF mCornerRectF;//画圆角的区域
	private int mOffsetX = 0, mOffsetY = 0;//移动线的偏移值
	private int mWidth, mHeight;//设置方形区域的宽跟高
	private Path mPath;
	private int mPathShader;
	private float mSnakeStep = 4.0f;
	//移动线的渐变色
	private int[] mSharderColor = {
			0x00ffffff, Color.WHITE, 0xff2ab6ff, 
//			Color.BLACK, Color.RED
	};
	private int[] mPaintSharderColor;
	private int mSpeedSamplingCount = 0;//测试速度的采样数
	private long mSpeedStartTime;//测试速度的开始时间
	
	/**
	 * 构造方法
	 */
	public SnakeFocusDrawable(Context context) {
		init(context);
	}


	/**
	 * 开始移动动画（只有宽度跟高度不为0， 多次调用只会启动一次，必先stop）
	 */
	public void start(){
		mStop = false;
	}
	
	
	/**
	 * 停止动画
	 */
	public void stop(){
		mStop = true;
		//mAroundAreaPos = 0;
	}
	

	/**
	 * 设置偏移位置（建议为偶数值）
	 * @param offsetX
	 * @param offsetY
	 */
	public void setOffsetPoint(int offsetX, int offsetY){
		mOffsetX = offsetX;
		mOffsetY = offsetY;
	}
	
	
	/**
	 * 画区域周围的移动线
	 * @param canvas
	 */
	public void drawAroundArea(Canvas canvas){
//		if(mStop){
//			return;
//		}
		if(DEBUG){
			if(mSpeedSamplingCount == 0){
				mSpeedStartTime = System.currentTimeMillis();
			}
		}
		mPaint.setColor(Color.WHITE);
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(AROUND_AREA_STROKE_SIZE);
		int width = getAroundViewWidth();
		int height = getAroundViewHeight();
		int arroundPos = (int)(mAroundAreaPos) % (width*2 + height*2);
		mPathShader = 0;
		drawLineCorner(canvas, arroundPos, mAroundAreaLength);
		stepSnake();
		if(DEBUG){
		//测试刷新速度用的采样点
			mSpeedSamplingCount ++;
			if(mSpeedSamplingCount >= 50){
				Log.i(TAG, "onDraw spend="+(System.currentTimeMillis() - mSpeedStartTime));
				mSpeedSamplingCount = 0;
			}
		}
	}

	
	/**
	 * 画线跟角度
	 * @param canvas
	 * @param pos
	 * @param length
	 */
	private void drawLineCorner(Canvas canvas, int pos, int length){
		int width = getAroundViewWidth();
		int height = getAroundViewHeight();
		int otherDirectionPos = pos;
		int otherDirectionLength = length;
		if(pos < width){
			//top
			mPath.reset();
			int shaderStart = 0;
			int shaderEnd = 0;
			int topPos = pos + mAroundAreadCorner;
			int lineLength = length;
			int drawLineLength = 0;
			if(topPos < width - mAroundAreadCorner){
				//line
				mPath.moveTo(topPos + mOffsetX, mOffsetY);
				drawLineLength = (width - mAroundAreadCorner) - topPos;
				if(drawLineLength > lineLength){
					drawLineLength = lineLength;
				}
				mPath.lineTo(topPos + mOffsetX + drawLineLength, mOffsetY);
				shaderStart = topPos;
				shaderEnd = topPos + drawLineLength;
			}
			
			int cornerPos = topPos + drawLineLength;
			int cornerLength = length - drawLineLength;
			int drawCornerLength = 0;
			if(cornerPos >= width - mAroundAreadCorner && cornerPos <= (width + mAroundAreadCorner)){
				//top right corner
				int start = (width + mAroundAreadCorner) - cornerPos;
				drawCornerLength = start;
				if(drawCornerLength > cornerLength){
					drawCornerLength = cornerLength;
				}
				float startAngle = 90 * ((float)start / (float)(mAroundAreadCorner * 2));
				float sweepAngle = 90 * ((float)drawCornerLength / (float)(mAroundAreadCorner * 2));
				
				int left = mOffsetX + width - mAroundAreadCorner * 2;
				int top = mOffsetY;
				int right = mOffsetX + width;
				int bottom = mOffsetY +  mAroundAreadCorner * 2;
				mCornerRectF.set(left, top, right, bottom);
				mPath.addArc(mCornerRectF, 360 - startAngle, sweepAngle);
				if(shaderStart <= 0){
					shaderStart = width - mAroundAreadCorner;
				}
				if(shaderEnd < width){
					shaderEnd = width;
				}
			}
			setPaintShader(mOffsetX + shaderStart, mOffsetY, mOffsetX + shaderEnd, mOffsetY + mAroundAreadCorner, drawLineLength + drawCornerLength);
			canvas.drawPath(mPath, mPaint);
			otherDirectionPos = width;
			otherDirectionLength -= drawLineLength + drawCornerLength;
		}
		else if(pos >= width && pos < (width + height)){
			//right
			mPath.reset();
			int shaderStart = 0;
			int shaderEnd = 0;
			int rightPos = pos - width + mAroundAreadCorner;
			int lineLength = length;
			int drawLineLength = 0;
			if(rightPos < height - mAroundAreadCorner){
				//line
				mPath.moveTo(mOffsetX + width, mOffsetY + rightPos);
				drawLineLength = (height - mAroundAreadCorner) - rightPos;
				if(drawLineLength > lineLength){
					drawLineLength = lineLength;
				}
				mPath.lineTo(mOffsetX + width, mOffsetY + rightPos + drawLineLength);
				shaderStart = rightPos;
				shaderEnd = rightPos + drawLineLength;
			}
			
			int cornerPos = rightPos + drawLineLength;
			int cornerLength = length - drawLineLength;
			int drawCornerLength = 0;
			if(cornerPos >= height - mAroundAreadCorner && cornerPos <= (height + mAroundAreadCorner)){
				//right bottom corner
				int start = (height + mAroundAreadCorner) - cornerPos;
				drawCornerLength = start;
				if(drawCornerLength > cornerLength){
					drawCornerLength = cornerLength;
				}
				float startAngle = 90 * ((float)start / (float)(mAroundAreadCorner * 2));
				float sweepAngle = 90 * ((float)drawCornerLength / (float)(mAroundAreadCorner * 2));
				
				int left = mOffsetX + width - mAroundAreadCorner * 2;
				int top = mOffsetY + height - mAroundAreadCorner * 2;
				int right = mOffsetX + width;
				int bottom = mOffsetY + height;
				mCornerRectF.set(left, top, right, bottom);
				mPath.addArc(mCornerRectF, 90 - startAngle, sweepAngle);
				if(shaderStart <= 0){
					shaderStart = height - mAroundAreadCorner;
				}
				if(shaderEnd < height){
					shaderEnd = height;
				}
			}
			
			setPaintShader(mOffsetX + width - mAroundAreadCorner, 
					mOffsetY + shaderStart, mOffsetX + width, mOffsetY + shaderEnd, drawLineLength + drawCornerLength);
			canvas.drawPath(mPath, mPaint);
			otherDirectionPos = width + height;
			otherDirectionLength -= drawLineLength + drawCornerLength;
		}
		else if(pos >= (width + height) && pos < (width*2 + height)){
			//bottom
			mPath.reset();
			int shaderStart = 0;
			int shaderEnd = 0;
			int bottomPos = (width - mAroundAreadCorner) - (pos - (width + height));
			int lineLength = length;
			int drawLineLength = 0;
			if(bottomPos > mAroundAreadCorner){
				//从右往左画
				//line
				mPath.moveTo(mOffsetX + bottomPos, mOffsetY + height);
				drawLineLength = bottomPos - mAroundAreadCorner;
				if(drawLineLength > lineLength){
					drawLineLength = lineLength;
				}
				mPath.lineTo(mOffsetX + (bottomPos - drawLineLength), mOffsetY + height);
				shaderStart = bottomPos;
				shaderEnd = bottomPos - drawLineLength;
			}
			
			int cornerPos = bottomPos - drawLineLength;
			int cornerLength = length - drawLineLength;
			int drawCornerLength = 0;
			if(cornerPos <= mAroundAreadCorner && cornerPos >= -mAroundAreadCorner){
				//bottom left corner
				int start = cornerPos + mAroundAreadCorner;
				drawCornerLength = start;
				if(drawCornerLength > cornerLength){
					drawCornerLength = cornerLength;
				}
				float startAngle = 90 * ((float)start / (float)(mAroundAreadCorner * 2));
				float sweepAngle = 90 * ((float)drawCornerLength / (float)(mAroundAreadCorner * 2));
				
				int left = mOffsetX;
				int top = mOffsetY + height - mAroundAreadCorner * 2;
				int right = mOffsetX + mAroundAreadCorner * 2;
				int bottom = mOffsetY + height;
				mCornerRectF.set(left, top, right, bottom);
				mPath.addArc(mCornerRectF, 180 - startAngle, sweepAngle);
				if(shaderStart <= 0){
					shaderStart = mAroundAreadCorner;
				}
				if(shaderEnd > 0){
					shaderEnd = 0;
				}
			}
			setPaintShader(mOffsetX + shaderStart, mOffsetY + height - mAroundAreadCorner, 
					mOffsetX + shaderEnd, mOffsetY + height, drawLineLength + drawCornerLength);
			canvas.drawPath(mPath, mPaint);
			otherDirectionPos = (width * 2) + height;
			otherDirectionLength -= drawLineLength + drawCornerLength;
		}
		else{
			//left
			mPath.reset();
			int shaderStart = 0;
			int shaderEnd = 0;
			int leftPos = (height - mAroundAreadCorner) - (pos - (width *2 + height));
			int lineLength = length;
			int drawLineLength = 0;
			if(leftPos > mAroundAreadCorner){
				//从右往左画
				//line
				mPath.moveTo(mOffsetX, mOffsetY + leftPos);
				drawLineLength = leftPos - mAroundAreadCorner;
				if(drawLineLength > lineLength){
					drawLineLength = lineLength;
				}
				mPath.lineTo(mOffsetX, mOffsetY + (leftPos - drawLineLength));
				shaderStart = leftPos;
				shaderEnd = leftPos - drawLineLength;
			}
			
			int cornerPos = leftPos - drawLineLength;
			int cornerLength = length - drawLineLength;
			int drawCornerLength = 0;
			if(cornerPos <= mAroundAreadCorner && cornerPos >= -mAroundAreadCorner){
				//left top corner
				int start = cornerPos + mAroundAreadCorner;
				drawCornerLength = start;
				if(drawCornerLength > cornerLength){
					drawCornerLength = cornerLength;
				}
				float startAngle = 90 * ((float)start / (float)(mAroundAreadCorner * 2));
				float sweepAngle = 90 * ((float)drawCornerLength / (float)(mAroundAreadCorner * 2));
				
				int left = mOffsetX;
				int top = mOffsetY;
				int right = mOffsetX + mAroundAreadCorner * 2;
				int bottom = mOffsetY + mAroundAreadCorner * 2;
				mCornerRectF.set(left, top, right, bottom);
				mPath.addArc(mCornerRectF, 270 - startAngle, sweepAngle);
				if(shaderStart <= 0){
					shaderStart = mAroundAreadCorner;
				}
				if(shaderEnd > 0){
					shaderEnd = 0;
				}
			}
			setPaintShader(mOffsetX, mOffsetY + shaderStart, 
					mOffsetX + mAroundAreadCorner, mOffsetY + shaderEnd, drawLineLength + drawCornerLength);
			canvas.drawPath(mPath, mPaint);
			otherDirectionPos = (width * 2) + (height * 2);
			otherDirectionLength -= drawLineLength + drawCornerLength;
		}
		if(otherDirectionLength > 0){
			otherDirectionPos = otherDirectionPos % ((width * 2) + (height * 2));
			drawLineCorner(canvas, otherDirectionPos, otherDirectionLength);
		}
	}
	
	
	/**
	 * 设置线性渐变颜色的区域，使其的开始点在移动线运动的头部
	 * @param arroundPos
	 */
	private void setPaintShader(int left, int top, int right, int bottom, int length){
		mPaintSharderColor[0] = getPaintShaderColor(mPathShader);
		mPathShader += length;
		mPaintSharderColor[1] = getPaintShaderColor(mPathShader);
		Shader shader = new LinearGradient(left, top, right, bottom,
				mPaintSharderColor,null,Shader.TileMode.CLAMP);
		mPaint.setShader(shader);
	}
	
	
	/**
	 * 取得某个位置的渐变色的颜色
	 * @param pos
	 * @return
	 */
	private int getPaintShaderColor(int pos){
		float posRate = (float)(pos) / mAroundAreaLength;
		float eachRate = 1.0f / (mSharderColor.length - 1);
		int colorIndex = (int)(posRate / eachRate);
		if(colorIndex >= (mSharderColor.length - 1)){
			//最后一个颜色
			return mSharderColor[mSharderColor.length - 1];
		}
		
		float preValue = posRate - eachRate * colorIndex;
		float nextValue = (eachRate * (colorIndex + 1)) - posRate;
		float totalValue = nextValue + preValue;
		int preColor = mSharderColor[colorIndex];
		int nextColor = mSharderColor[colorIndex + 1];
		int a = (int)((nextValue / totalValue) * Color.alpha(preColor) + 
					(preValue / totalValue) * Color.alpha(nextColor));
		int r = (int)((nextValue / totalValue) * Color.red(preColor) + 
					(preValue / totalValue) * Color.red(nextColor));
		int g = (int)((nextValue / totalValue) * Color.green(preColor) + 
				(preValue / totalValue) * Color.green(nextColor));
		int b = (int)((nextValue / totalValue) * Color.blue(preColor) + 
				(preValue / totalValue) * Color.blue(nextColor));
//		Log.i(TAG, "getPaintShaderColor  pos="+pos+" colorIndex="+colorIndex+" preValue="+preValue+
//				" nextValue="+nextValue+" posRate="+posRate+" eachRate="+eachRate);
		return Color.argb(a, r, g, b);
	}
	
	
	/**
	 * 初始化
	 * @param context
	 */
	private void init(Context context){
		mPaint = new Paint();
		mPath = new Path();
		mPaintSharderColor = new int[2];
		int padding = AROUND_AREA_STROKE_SIZE / 2;
		mAroundPadding = new Rect(padding, padding,
				padding ,padding);
		mCornerRectF = new RectF();
		mCornerRectF.setEmpty();		
	}
	
	
	private void stepSnake(){
		if(mStop == false){
			int width = getAroundViewWidth();
			int height = getAroundViewHeight();
			if(width > 0 && height > 0){
				//区域整圈的长度
				int aroundSize = width * 2 + height * 2;
				if(mAroundAreaPos >= aroundSize){
					if(DEBUG){
						Log.d(TAG, "mHandler reset mAroundAreaPos="+mAroundAreaPos+" aroundSize="+aroundSize);
					}
					mAroundAreaPos = mAroundAreaPos % aroundSize;
				}
				mAroundAreaPos += mSnakeStep;
			}
		}
	}
	
	
	/**
	 * 取得方形区域的宽度
	 * @return
	 */
	private int getAroundViewWidth(){
		return mWidth;
	}
	
	
	/**
	 * 取得方形区域的高度
	 * @return
	 */
	private int getAroundViewHeight(){
		return mHeight;
	}
	
	
	/**
	 * 设置贪食蛇的长度（不能大于区域一圈的长度）
	 * @param width
	 * @param height
	 * @return
	 */
	private int snakeLength(int width, int height){
		return (int)(((width * 2) + (height * 2)) * 0.7);
	}


	@Override
	public boolean isDynamicFocus() {
		return true;
	}


	@Override
	public void setRect(Rect r) {
		if(r != null){
			setOffsetPoint(r.left - mAroundPadding.left, r.top - mAroundPadding.top);
			mWidth = r.right + mAroundPadding.right - (r.left - mAroundPadding.left);
			mHeight = r.bottom + mAroundPadding.bottom - (r.top - mAroundPadding.top);
			mAroundAreaLength = snakeLength(mWidth, mHeight);
			mSnakeStep = (float)mAroundAreaLength / AROUND_ARED_TOTAL_FRAME;
		}
	}


	@Override
	public void setRadius(int r) {
		mAroundAreadCorner = r;
	}


	@Override
	public void setVisible(boolean visible) {
		if(visible == false){
			stop();
		}
	}


	@Override
	public void draw(Canvas canvas) {
		drawAroundArea(canvas);
	}


	@Override
	public void setAlpha(float alpha) {
		// TODO Auto-generated method stub
		
	}

}
