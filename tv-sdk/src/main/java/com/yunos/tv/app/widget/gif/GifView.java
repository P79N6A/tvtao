package com.yunos.tv.app.widget.gif;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.Queue;

/**
 * GifView，一个显示gif图片的view。<br>
 * Gif图片可以是字节流，资源或者文件的形式，可以设置播放次数，也可以设置循环播放。在播放过程中可以进行暂停<br>
 * 本类进行了各种优化设计，并且能支持帧数超过100以上的大Gif图片的播放。 请注意在适当的时候要调用destroy方法来释放资源<br>
 * 对Gifview的其它使用（如设置大小等），和ImageView一样
 * 
 * @author linghu
 */
public class GifView extends ImageView implements GifAction {
	public static final String TAG = "GifView";
	private static final boolean DEBUG = true;
	
	private final int DRAW_BITMAP = 101; //绘制一针
	private final int START_DECODE = 102; //启动解码
	private final int DRAW_FIRST_IMAGE = 103; //绘制第一针
	private final static int MAX_FRAME_TIME_MSC = 1000; //两帧间隔最大毫秒数
	
	/** gif解码器 */
	private GifDecoder gifDecoder = null;
	private PlayThread gifPlayer = null;
	/** 当前要画的帧的图 */
	private Bitmap currentImage = null;
	// 上一次绘制的的图
	// private Bitmap lastImage = null;
	private Bitmap firstImage = null;
	/** 动画显示的次数 */
	private int loopNum = 999999;
	// 当前播放的gif动画的循环次数
	private int mCurrentLoop = 0;
	private boolean isStart = false;

	FrameQueue frameQueue = null;
	
	private boolean isGif = true;

	public boolean isGif() {
		return isGif;
	}

	public void setGif(boolean isGif) {
		this.isGif = isGif;
	}

	public GifView(Context context) {
		super(context);
		setScaleType(ScaleType.FIT_XY);
	}

	public GifView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GifView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setScaleType(ScaleType.FIT_XY);
	}
	
	/**
	 * 判断当前是否正在解码和绘制
	 * 
	 * @return
	 */
	public boolean isRun() {
		return isStart;
	}

	/**
	 * 是否已经设置了Gif资源
	 * 
	 * @return
	 */
	public boolean hasSetImageSource() {
		if (mFilePath == null && mResId == -1) {
			return false;
		}
		return true;
	}

	/**
	 * 设置播放循环次数
	 * 
	 * @param num
	 */
	public void setLoopNum(int num) {
		this.loopNum = num;
	}

	// public void drawFirstFrame(){
	// init();
	// setForFirstFrame(true);
	// isStart = true;
	// gifDecoder.start();
	// }

	/**
	 * GifView的初始化
	 */
	public void init(boolean onlyFirst) {
		if (currentImage != null) {
			if (!currentImage.isRecycled()) {
				currentImage.recycle();
			}
			currentImage = null;
		}

		synchronized (this) {
			if (gifDecoder != null) {
				gifDecoder.abort();
				gifDecoder = null;
			}
			gifDecoder = new GifDecoder(this);
			gifDecoder.setOnlyFirstFrame(onlyFirst);
		}

		// currentLoop = 0;// 循环次数清零
		if (this.mFilePath != null) {
			gifDecoder.setGifImage(this.mFilePath);
		} else {
			gifDecoder.setGifImage(getResources(), this.mResId);
		}

		if (!onlyFirst) {//如果不只是为了画第一针
			if (gifPlayer == null) {
				gifPlayer = new PlayThread();
			}

			if (frameQueue == null) {
				frameQueue = new FrameQueue();
			}
		}
		
		gifDecoder.setDrawCorner(mIsDrawCorner);
		gifDecoder.setCornerRadius(mCornerRadius);
	}

	/**
	 * 以字节数据形式设置gif图片<br>
	 * 如果图片太大，请不要采用本方法，而应该采用setGifImage(String strFileName)或setGifImage(int
	 * resId)方法
	 * 
	 * @param gif图片
	 */
	String mFilePath = null;
	int mResId = -1;

	public void setGifImage(String strFileName) {
		boolean start = isStart;
		firstImage = null;
		stopDecode();
		mCurrentLoop = 0;// 循环次数清零
		mFilePath = strFileName;
		mResId = -1;
		if (!start) {
			getFirstFrame();
		} else {
			startDecode();
		}
	}

	public void setGifImage(int resId) {
		boolean start = isStart;
		firstImage = null;
		stopDecode();
		mCurrentLoop = 0;// 循环次数清零
		mResId = resId;
		mFilePath = null;
		if (!start) {
			getFirstFrame();
		} else {
			startDecode();
		}
	}

	public void clearHasLoopNum() {
		mCurrentLoop = 0;
	}

	void getFirstFrame() {
		if (firstImage != null || isSelected()) {
			return;
		}

		Log.d(TAG, "getFirstFrame");
		isStart = true;
		init(true);
		gifDecoder.start();
	}

	/**
	 * 设置了Gif图片源之后，要调用startDecode才能解码
	 */
	public void startDecode() {
		Log.d(TAG, "startDecode");
		mCurrentLoop = 0;
		start();
	}

	void start() {
		if (!hasSetImageSource()) {
			return;
		}
		Log.i(TAG, "start -- mCurrentLoop:" + mCurrentLoop);
		
		synchronized (this) {
			isStart = true;
		}
		init(false);
		gifDecoder.start();
		gifPlayer.start();
	}

	public void stopDecode() {
		Log.d(TAG, "stopDecode");
		synchronized (this) {
			isStart = false;
		}

		if (gifDecoder != null) {
			gifDecoder.abort();
		}
		gifDecoder = null;

		if (gifPlayer != null) {
			gifPlayer.abort();
		}
		gifPlayer = null;

		if (frameQueue != null) {
			frameQueue.abandon();
		}

		frameQueue = null;

		// setImageBitmap(firstImage);
		if (firstImage != null) {
			mHandler.sendEmptyMessageDelayed(DRAW_FIRST_IMAGE, 33);
		}
	}

	/**
	 * 获取第一帧图片
	 */
	public Bitmap getFirstFramge() {
		return firstImage;
	}

	long mTime = -1;

	/**
	 * 解析过程的回调接口
	 */
	public void parseReturn(int iResult, Bitmap bitmap, GifDecoder decoder) {
		synchronized (this) {
			if (getVisibility() == GONE || getVisibility() == INVISIBLE || !isStart || this.gifDecoder != decoder) {
				return;
			}
		}

		switch (iResult) {
		case RETURN_FRAME_FIRST:// 解码第一帧图片完成
			synchronized (this) {
				if (isStart) {
					if (firstImage == null) {
						firstImage = bitmap;
					}
					if (gifPlayer != null && gifPlayer.isRunning()) {
						addQueue(bitmap, decoder.getFrameDelay());
					} else {
						drawImage(bitmap);
					}

					if (decoder.onlyFirstFrame()) {
						isStart = false;
					}
				}
			}
			break;
		case RETURN_FRAME_FINISH: //解码一针完成
			synchronized (this) {
				if (isStart) {
					addQueue(bitmap, decoder.getFrameDelay());
				}
			}
			break;
		case RETURN_DECODE_FINISH: //解码完成
			if (DEBUG) {
				Log.d(TAG, "parseReturn -- currentLoop:" + mCurrentLoop);
			}
			this.mCurrentLoop++;
			if (this.mCurrentLoop < this.loopNum) {
				mHandler.sendEmptyMessageDelayed(START_DECODE, 100);
			}
		}

		mTime = System.currentTimeMillis();
	}

	void addQueue(Bitmap bitmap, int delay) {
		int decodeTime = 0;

		if (mTime > 0) {
			long dTime = System.currentTimeMillis() - mTime;
            decodeTime = (int)(dTime > MAX_FRAME_TIME_MSC ? delay : dTime);
		}

		if (DEBUG) {
			Log.d(TAG, "addQueue -- delay: " + delay + ", decodeTime: " + decodeTime);
		}
		frameQueue.put(new GifFrame(bitmap, delay, decodeTime));
	}

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DRAW_BITMAP:
				if (DEBUG) {
					Log.d(TAG, "h -- handleMessage DRAW_BITMAP");
				}
				setImageBitmap((Bitmap) msg.obj);
				break;
			case START_DECODE:
				if (DEBUG) {
					Log.d(TAG, "h -- handleMessage START_DECODE isSelected():" + isSelected());
				}
				if (isSelectedPlay()) {
					if (isSelected()) {
						start();
					}
				} else {
					start();
				}
				break;
			case DRAW_FIRST_IMAGE:
				if (DEBUG) {
					Log.d(TAG, "h -- handleMessage DRAW_FIRST_IMAGE");
				}
				setImageBitmap(firstImage);
				break;
			}
		}
	};

	// invalidate View进行重绘
	private void drawImage(Bitmap bitmap) {
		// setImageBitmap(bitmap);
		Message msg = new Message();
		msg.what = DRAW_BITMAP;
		msg.obj = bitmap;

		mHandler.sendMessage(msg);
	}

	@Override
	public void loopEnd() {

	}

	public void reset() {
		firstImage = null;
	}

	class GifFrame {
		Bitmap bitmap;
		int delay;
		int decodeTime;

		public GifFrame(Bitmap bitmap, int delay, int decodeTime) {
			this.bitmap = bitmap;
			this.delay = delay;
			this.decodeTime = decodeTime;
		}

		public Bitmap getBitmap() {
			return this.bitmap;
		}

		public int getDelay() {
			return this.delay;
		}

		public int getDecodeTime() {
			return this.decodeTime;
		}
	}

	class FrameQueue {
		Object lock = new Object();
		Queue<GifFrame> frameQueue = new LinkedList<GifFrame>();
		int cap = 3;
		boolean isPutWait = false;
		boolean isPollWait = false;
		boolean isQuit = false;

		public void abandon() {
			synchronized (lock) {
				frameQueue.clear();
				isQuit = true;
				if (isPutWait || isPollWait) {
					lock.notifyAll();
				}
			}
		}

		public int size() {
			synchronized (lock) {
				return frameQueue.size();
			}
		}

		public boolean isEmpty() {
			synchronized (lock) {
				return this.frameQueue.isEmpty();
			}
		}

		public void put(GifFrame frame) {
			synchronized (lock) {
				try {
					if (frameQueue.size() >= cap) {
						isPutWait = true;
						lock.wait();
						isPutWait = false;
					}

					if (!isQuit) {
						frameQueue.add(frame);
					}

					if (isPollWait) {
						lock.notify();
					}

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public GifFrame poll() {
			synchronized (lock) {
				try {
					if (frameQueue.size() <= 0) {
						isPollWait = true;
						lock.wait();
						isPollWait = false;
					}

					GifFrame frame = frameQueue.poll();
					if (isPutWait) {
						lock.notify();
					}

					if (isQuit) {
						return null;
					}

					return frame;

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			return null;
		}
	}

	class PlayThread extends Thread {
		boolean isRunning = false;

		@Override
		public void start() {
			if (!this.isRunning) {
				super.start();
			}
			this.isRunning = true;
		}

		public void abort() {
			synchronized (this) {
				this.isRunning = false;
			}
		}

		boolean isRunning() {
			synchronized (this) {
				return this.isRunning;
			}
		}

		@Override
		public void run() {
			while (isRunning()) {
				try {
					boolean needWaitDecode = frameQueue.isEmpty();

					GifFrame frmae = frameQueue.poll();
					if (frmae == null) {
						continue;
					}

					if (!isRunning()) {
						return;
					}
					if (needWaitDecode && frmae != null && frmae.getDelay() > frmae.getDecodeTime()) {
						int delay = frmae.getDelay() - frmae.getDecodeTime();
						if (DEBUG) {
							Log.d(TAG, "player thread: wait decoder delay = " + delay + ", queue size = " + frameQueue.size());
						}
						sleep(delay);
					} else {
						if (DEBUG) {
							Log.d(TAG, "player thread delay = " + frmae.getDelay() + ", queue size = " + frameQueue.size());
						}
						sleep(frmae.getDelay());
					}

					if (!isRunning()) {
						return;
					}
					drawImage(frmae.getBitmap());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/************************关于画圆角 by leiming.yanlm**********************************/
	private float mCornerRadius = 10f;
	private boolean mIsDrawCorner = false;
	
	/**
	 * 是否画带圆角。
	 * @param isDrawCorner 默认false 
	 */
	public void setDrawCorner(boolean isDrawCorner) {
		this.mIsDrawCorner = isDrawCorner;
		if (gifDecoder != null) {
			gifDecoder.setDrawCorner(isDrawCorner);
		}
	}
	public void setCornerRadius(float cornerRadius) {
		this.mCornerRadius = cornerRadius;
		if (gifDecoder != null) {
			gifDecoder.setCornerRadius(cornerRadius);
		}
	}

//	
//    @Override
//    protected void onDraw(Canvas canvas) {
//        if(isGif()){
//            super.onDraw(canvas);
//            return;
//        }
//    	 Drawable maiDrawable = getDrawable();
//        if (maiDrawable instanceof BitmapDrawable) {
//            Paint paint = ((BitmapDrawable) maiDrawable).getPaint();
//            final int color = 0xff000000;
//
//            RectF rectF = new RectF(0, 0, getWidth(), getHeight());
//            rectF.left += getPaddingLeft();
//            rectF.top += getPaddingTop(); 
//            rectF.right -= getPaddingRight(); 
//            rectF.bottom -= getPaddingBottom(); 
//            int saveCount = canvas.saveLayer(rectF, null, Canvas.MATRIX_SAVE_FLAG | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
//                    | Canvas.CLIP_TO_LAYER_SAVE_FLAG);
//            canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
//
//            paint.setAntiAlias(true);
//            canvas.drawARGB(0, 0, 0, 0);
//            paint.setColor(color);
//            canvas.drawRoundRect(rectF, mCornerRadius, mCornerRadius, paint);
//
//            Xfermode oldMode = paint.getXfermode();
//            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//            super.onDraw(canvas);
//            paint.setXfermode(oldMode);
//            canvas.restoreToCount(saveCount);
//        } else {
//            super.onDraw(canvas);
//        }
//    }

//
//	@Override
//	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//		super.onSizeChanged(w, h, oldw, oldh);
//		if (isGif) {
//			return;
//		}
//		if ((w != oldw) || (h != oldh)) {
//			generateMaskPath(w, h);
//		}
//	}
//
	//使用path, 只对非gif图片画corner
//	private Path mMaskPath;
//	private Paint mMaskPaint;
//
//	private Paint getMaskPaint() {
//		if (null == mMaskPaint) {
//			mMaskPaint = new Paint();
//			mMaskPaint.setDither(true);// 设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰
//			mMaskPaint.setAntiAlias(true);// 非锯齿效果
//			mMaskPaint.setFilterBitmap(true);// 如果该项设置为true，则图像在动画进行中会滤掉对Bitmap图像的优化操作，加快显示速度
//			mMaskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//		}
//		return mMaskPaint;
//	}
//	private Path generateMaskPath(int width, int height) {
//		if (mMaskPath == null) {
//			this.mMaskPath = new Path();
//			this.mMaskPath.addRoundRect(new RectF(0.0F, 0.0F, width, height), this.mCornerRadius, this.mCornerRadius, Path.Direction.CW);
//			this.mMaskPath.setFillType(Path.FillType.INVERSE_WINDING);
//		}
//		return mMaskPath;
//	}
//	protected void onDraw(Canvas canvas) {
//		if (isGif) {
//			super.onDraw(canvas);
//			return;
//		}
//		Drawable drawable = getDrawable();
//		if (drawable == null) {
//			return ;
//		}
//		long t1 = System.currentTimeMillis();
//		Log.i(TAG, "time1:" + t1);
//		int w = this.getWidth();
//		int h = this.getHeight();
//		// 保存当前layer的透明橡树到离屏缓冲区。并新创建一个透明度爲255的新layer
//		int saveCount = canvas.saveLayerAlpha(0.0F, 0.0F, w, h, 255, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG);
////		super.onDraw(canvas);
//		drawable.draw(canvas);
//		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
//		canvas.drawPath(generateMaskPath(w, h), getMaskPaint());
//		canvas.restoreToCount(saveCount);
//		
//		long t2 = System.currentTimeMillis();
//		Log.i(TAG, "time1:" + (t1 - t2));
//	}
//	
	
	private boolean isSelectedPlay = true;
	public boolean isSelectedPlay() {
		return isSelectedPlay;
	}
	/**
	 * 设置是否GifView是选中状态的时候才进行播放。不选中时停止播放。
	 * @param isSelectedPlay true代表是选中的时候才播放，否则不播放。默认true
	 */
	public void setSelectedPlay(boolean isSelectedPlay) {
		this.isSelectedPlay = isSelectedPlay;
	}
	
}