package com.yunos.tv.app.widget.egl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class BackgroundEglView extends GLSurfaceView implements Renderer {

	private static final float[] GVertices = new float[] { -1.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, };

	private static final float[] GTextures = new float[] { 0.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, };
	
	private final int ALPHA_ANIM_DEFAULT_FRAME_COUNT = 100;
	int textureId = -1;
	private FloatBuffer mVerticesBuffer;
	private FloatBuffer mTexturesBuffer;
	TextureList mList = new TextureList();
	int mWidth = 0;
	int mHeight = 0;
	Scroller mScroller = new Scroller(getContext());
	int mScrollX = 0;
	int mScrollY = 0;
	private AlphaAnim mAlphaAnim;
	private int mAlphaAnimIndex = -1;
	private boolean mIsSurfaceCreate;
	private int mAlphaAnimFrameCount = ALPHA_ANIM_DEFAULT_FRAME_COUNT;
	public BackgroundEglView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public BackgroundEglView(Context context) {
		super(context);
		init();
	}

	public void startHorzontalScroll(int dx, int duration) {
		if (!mScroller.isFinished()) {
			mScroller.forceFinished(true);
		}
		mScroller.startScroll(mScrollX, mScrollY, dx, 0, duration);
		requestRender();
	}

	public void startVerticalScroll(int dx, int duration) {
		if (!mScroller.isFinished()) {
			mScroller.forceFinished(true);
		}
		mScroller.startScroll(mScrollX, mScrollY, 0, dx, duration);
		requestRender();
	}

	void init() {
		setRenderer(this);
		setRenderMode(RENDERMODE_WHEN_DIRTY);

		mVerticesBuffer = makeFloatBuffer(GVertices);
		mTexturesBuffer = makeFloatBuffer(GTextures);

	}

	@Override
	public void onResume() {
		super.onResume();
		mList.invalidate();
		requestRender();
	}

	public void addBackground(Bitmap bitmap, Rect rect) {
		mList.add(new Texture(bitmap, rect));
	}

	public void setAlphaAnim(int backgroundIndex, float startAlpha, float endAlpha){
		setAlphaAnim(backgroundIndex, startAlpha, endAlpha, ALPHA_ANIM_DEFAULT_FRAME_COUNT);
	}
	
	public void setAlphaAnim(int backgroundIndex, float startAlpha, float endAlpha, int frameCount){
		synchronized (this) {
			if(!mIsSurfaceCreate){
				mAlphaAnimIndex = backgroundIndex;
				mAlphaAnim = new AlphaAnim();
				mAlphaAnim.start(startAlpha, endAlpha, frameCount);
			}
		}
	}
	public void clear() {
		mList.clear();
	}

	public void notifyBackgroundChanged() {
		requestRender();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mWidth = getMeasuredWidth();
		mHeight = getMeasuredHeight();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		synchronized (this) {
			mIsSurfaceCreate = true;
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glOrthof(-1.0f, 1.0f, -1.0f, 1.0f, 1, 3);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		mList.deleteTexture(gl);
		mList.loadTexture(gl);
		mList.draw(gl);
	}

	private static FloatBuffer makeFloatBuffer(final float[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}

	class TextureList extends LinkedList<Texture> {
		boolean isInvalidate = false;

		@Override
		public boolean add(Texture e) {
			synchronized (this) {
				boolean hr = super.add(e);
				isInvalidate = true;
				return hr;
			}
		}

		public void invalidate() {
			isInvalidate = true;
			for (int index = 0; index < size(); index++) {
				get(index).invalidate();
			}
		}

		@Override
		public void clear() {
			synchronized (this) {
				super.clear();
			}
		}

		// public boolean invalidate() {
		// return isInvalidate;
		// }

		public void deleteTexture(GL10 gl){
			synchronized (this) {
				for (int index = 0; index < size(); index++) {
					get(index).deleteTxture(gl);
				}
			}
		}
		public void loadTexture(GL10 gl) {
			synchronized (this) {
				if (isInvalidate) {
					for (int index = 0; index < size(); index++) {
						get(index).loadTexture(gl);
					}
				}

				isInvalidate = false;
			}
		}

		public void draw(GL10 gl) {
			synchronized (this) {
				if (size() <= 0) {
					return;
				}

				gl.glMatrixMode(GL10.GL_MODELVIEW);
				gl.glLoadIdentity();
				GLU.gluLookAt(gl, 0, 0, 2, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

				gl.glDisable(GL10.GL_DEPTH_TEST);
				gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
				gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

				gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
				gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

				gl.glPushMatrix();

				gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVerticesBuffer); // vertices
																			// of
																			// square
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexturesBuffer); // texture
																			// vertices
				gl.glEnable(GL10.GL_TEXTURE_2D);

				gl.glEnable(GL10.GL_BLEND);
				gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
				boolean isRequestRender = false;
				if (!mScroller.isFinished() && mScroller.computeScrollOffset()) {
					mScrollX = mScroller.getCurrX();
					mScrollY = mScroller.getCurrY();
					isRequestRender = true;
					requestRender();
				}
				boolean needRequesRender = false;
				for (int index = 0; index < size(); index++) {
					needRequesRender = get(index).draw(index, gl);
				}
				if(!isRequestRender && needRequesRender){
					isRequestRender = true;
					requestRender();
				}
			}
		}

	}

	class Texture {
		int textureId = -1;
		int deleteTexture = -1;
		Rect rect = new Rect();
		Bitmap bitmap;

		public Texture(Bitmap bit, Rect r) {
			rect.set(r);
			textureId = -1;
			bitmap = bit;
		}

		public void setTextureId(int id) {
			textureId = id;
		}

		public int getTextureId() {
			return textureId;
		}

		public void invalidate() {
			deleteTexture = textureId;
			textureId = -1;
		}

		public Rect rect() {
			return rect;
		}

		public void deleteTxture(GL10 gl) {
			if (deleteTexture >= 0) {
				gl.glDeleteTextures(1, new int[] { deleteTexture }, 0);
				deleteTexture = -1;
			}
		}

		public void loadTexture(GL10 gl) {
			if (textureId < 0) {
				int ids[] = new int[1];
				gl.glGenTextures(1, ids, 0);
				int id = ids[0];
				gl.glBindTexture(GL10.GL_TEXTURE_2D, id);
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);
				gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);

				textureId = id;
			}
		}

		public boolean draw(int index, GL10 gl) {
			boolean render = false;
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			gl.glViewport(rect.left + mScrollX, mHeight - rect.top - rect.height() + mScrollY, rect.width(), rect.height());
			gl.glPushMatrix();
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId); // bind texture
			if(index == mAlphaAnimIndex){
				render = mAlphaAnim.computerAnim();
				float alpha  = mAlphaAnim.getCurrAlpha();
				gl.glColor4f(alpha, alpha, alpha, alpha);
				gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);
			}
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
			gl.glPopMatrix();
			return render;
		}
	}
	
	class AlphaAnim{
		private boolean mIsFinished;
		private int mCurrFames;
		private int mTotalFrames = ALPHA_ANIM_DEFAULT_FRAME_COUNT;
		private float mStartAlpha;
		private float mCurrAlpha;
		private float mEndAlpha;
		private Interpolator mInterpolator  = new DecelerateInterpolator();
		void start(float startAlpha, float endAlpha, int fames){
			mStartAlpha = startAlpha;
			mCurrAlpha = startAlpha;
			mEndAlpha = endAlpha;
			mCurrFames = 0;
			mTotalFrames = fames;
			mIsFinished = false;
		}
		
		boolean computerAnim(){
			if(mIsFinished){
				return false;
			}
			mCurrFames ++;
			if(mCurrFames > mTotalFrames){
				finish();
				return false;
			}
			else{
				float input = (float)mCurrFames / (float)mTotalFrames;
				float output = input;
				if(mInterpolator != null){
					output = mInterpolator.getInterpolation(input);
				}
				
				mCurrAlpha = mStartAlpha + ((mEndAlpha - mStartAlpha) * output);
				if(mStartAlpha > mEndAlpha){
					if(mCurrAlpha > mStartAlpha){
						mCurrAlpha = mStartAlpha;
					}
					else if(mCurrAlpha < mEndAlpha){
						mCurrAlpha = mEndAlpha;
					}
				}
				else if(mStartAlpha < mEndAlpha){
					if(mCurrAlpha < mStartAlpha){
						mCurrAlpha = mStartAlpha;
					}
					else if(mCurrAlpha > mEndAlpha){
						mCurrAlpha = mEndAlpha;
					}
				}
				return true;
			}
		}
		
		float getCurrAlpha(){
			Log.i("test", "getCurrAlpha mCurrAlpha="+mCurrAlpha);
			return mCurrAlpha;
		}
		
		
		void finish(){
			mIsFinished = true;
		}
	}
}
