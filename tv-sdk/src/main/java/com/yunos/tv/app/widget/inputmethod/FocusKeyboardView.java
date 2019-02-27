package com.yunos.tv.app.widget.inputmethod;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.Keyboard.Key;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yunos.tv.aliTvSdk.R;
import com.yunos.tv.app.widget.ViewGroup;
import com.yunos.tv.app.widget.focus.params.FocusRectParams;
import com.yunos.tv.app.widget.utils.ReflectUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 键盘布局展示与键值分发
 * @author quanqing.hqq
 *
 */
public class FocusKeyboardView extends ViewGroup {

	/**
	 * Listener for virtual keyboard events.
	 */
	public interface OnKeyboardActionListener {

		/**
		 * Called when the user presses a key. This is sent before the {@link #onKey} is called. For keys that repeat, this is only called once.
		 * 
		 * @param primaryCode
		 *            the unicode of the key being pressed. If the touch is not on a valid key, the value will be zero.
		 */
		void onPress(int primaryCode);

		/**
		 * Called when the user releases a key. This is sent after the {@link #onKey} is called. For keys that repeat, this is only called once.
		 * 
		 * @param primaryCode
		 *            the code of the key that was released
		 */
		void onRelease(int primaryCode);

		/**
		 * Send a key press to the listener.
		 * 
		 * @param primaryCode
		 *            this is the key that was pressed
		 * @param keyCodes
		 *            the codes for all the possible alternative keys with the primary code being the first. If the primary key code is a single character such as an alphabet or number or symbol, the
		 *            alternatives will include other characters that may be on the same key or adjacent keys. These codes are useful to correct for accidental presses of a key adjacent to the
		 *            intended key.
		 */
		void onKey(int primaryCode, int[] keyCodes);

		/**
		 * Sends a sequence of characters to the listener.
		 * 
		 * @param text
		 *            the sequence of characters to be displayed.
		 */
		void onText(CharSequence text);

		/**
		 * Called when the user quickly moves the finger from right to left.
		 */
		void swipeLeft();

		/**
		 * Called when the user quickly moves the finger from left to right.
		 */
		void swipeRight();

		/**
		 * Called when the user quickly moves the finger from up to down.
		 */
		void swipeDown();

		/**
		 * Called when the user quickly moves the finger from down to up.
		 */
		void swipeUp();
	}

	private static final boolean DEBUG = true;
	private final String TAG = getClass().getSimpleName();
	private static final int NOT_A_KEY = -1;
	private static final int[] KEY_DELETE = { Keyboard.KEYCODE_DELETE };
	private static final int[] LONG_PRESSABLE_STATE_SET = { ResInternalR.attr.state_long_pressable };

	private KeyboardData mKeyboard;
	private int mCurrentKeyIndex = NOT_A_KEY;
	private int mLabelTextSize;
	private int mKeyTextSize;
	private int mKeyTextColor;
	private float mShadowRadius;
	private int mShadowColor;
	private float mBackgroundDimAmount;

	private TextView mPreviewText;
	private PopupWindow mPreviewPopup;
	private int mPreviewTextSizeLarge;
	private int mPreviewOffset;
	private int mPreviewHeight;
	// Working variable
	private final int[] mCoordinates = new int[2];

	private PopupWindow mPopupKeyboard;//显示小键盘
	private IKeyboardExpand mPopupContainer;
	private int mPopupWidth;
	private int mPopupHeight;
	private View mPopupParent;
	private int mMiniKeyboardOffsetX;
	private int mMiniKeyboardOffsetY;
	private Map<Key, View> mMiniKeyboardCache;
	private Key[] mKeys;

	/** Listener for {@link OnKeyboardActionListener}. */
	private OnKeyboardActionListener mKeyboardActionListener;

	private static final int MSG_SHOW_PREVIEW = 1;
	private static final int MSG_REMOVE_PREVIEW = 2;
	private static final int MSG_REPEAT = 3;
	private static final int MSG_LONGPRESS = 4;

	private static final int DELAY_BEFORE_PREVIEW = 0;
	private static final int DELAY_AFTER_PREVIEW = 70;

	private int mVerticalCorrection;
	private int mProximityThreshold;

	private boolean mPreviewCentered = false;
	private boolean mShowPreview = true;
	private int mPopupPreviewX;
	private int mPopupPreviewY;

	private boolean mProximityCorrectOn;

	private Rect mPadding;

	private int mPopupX;
	private int mPopupY;
	private int mPopupLayout;

	private Drawable mKeyBackground;

	private static int MAX_NEARBY_KEYS = 12;
	private int[] mDistances = new int[MAX_NEARBY_KEYS];

	private int mLastSentIndex;
	private int mTapCount;
	private long mLastTapTime;
	private boolean mInMultiTap;

	private boolean mDrawPending;
	private Rect mDirtyRect = new Rect();
	private Bitmap mBuffer;
	
	/**
	 * Notes if the keyboard just changed, so that we could possibly reallocate the mBuffer.
	 */
	private boolean mKeyboardChanged;
	private Canvas mCanvas;
	private AccessibilityManager mAccessibilityManager;
	private AudioManager mAudioManager;
	
	private boolean mHeadsetRequiredToHearPasswordsAnnounced;
	
	protected ArrayList<View> mAdapterViews = new ArrayList<View>();
	
	
	KeyboardAdapter mAdapter = null;
	private DataSetObserver mDataSetObserver;

	private int mItemCount;
	private int mOldItemCount;
	private boolean mDataChanged;
	private int mOldSelectedPosition;

	private boolean mBlockLayoutRequests;
	private int mFirstPosition = 0;

	private RecycleBin mRecycler = new RecycleBin();
	protected final boolean[] mIsScrap = new boolean[1];

	boolean mAdapterHasStableIds;

	protected Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SHOW_PREVIEW:
				showKey(msg.arg1);
				break;
			case MSG_REMOVE_PREVIEW:
				mPreviewText.setVisibility(INVISIBLE);
				break;
			case MSG_REPEAT:
				// 如果是触屏时可以在些做repeatkey操作
				break;
			case MSG_LONGPRESS:
				// 如果是触屏时可以在此做长按事件的操作，比如 openPopupIfRequired
				break;
			}
		}
	};

	public FocusKeyboardView(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.keyboardViewStyle);
	}

	public FocusKeyboardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs, ResAndroidR.styleable.KeyboardView, defStyle, 0);

		LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		int previewLayout = 0;

		int n = a.getIndexCount();

		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);

			if (attr == ResInternalR.styleable.KeyboardView_keyBackground) {
				mKeyBackground = a.getDrawable(attr);
			} else if (attr == ResInternalR.styleable.KeyboardView_verticalCorrection) {
				mVerticalCorrection = a.getDimensionPixelOffset(attr, 0);
			} else if (attr == ResInternalR.styleable.KeyboardView_keyPreviewLayout) {
				previewLayout = a.getResourceId(attr, 0);
			} else if (attr == ResInternalR.styleable.KeyboardView_keyPreviewOffset) {
				mPreviewOffset = a.getDimensionPixelOffset(attr, 0);
			} else if (attr == ResInternalR.styleable.KeyboardView_keyPreviewHeight) {
				mPreviewHeight = a.getDimensionPixelSize(attr, 80);
			} else if (attr == ResInternalR.styleable.KeyboardView_keyTextSize) {
				mKeyTextSize = a.getDimensionPixelSize(attr, 18);
			} else if (attr == ResInternalR.styleable.KeyboardView_keyTextColor) {
				mKeyTextColor = a.getColor(attr, 0xFF000000);
			} else if (attr == ResInternalR.styleable.KeyboardView_labelTextSize) {
				mLabelTextSize = a.getDimensionPixelSize(attr, 14);
			} else if (attr == ResInternalR.styleable.KeyboardView_popupLayout) {
				mPopupLayout = a.getResourceId(attr, 0);
			} else if (attr == ResInternalR.styleable.KeyboardView_shadowColor) {
				mShadowColor = a.getColor(attr, 0);
			} else if (attr == ResInternalR.styleable.KeyboardView_shadowRadius) {
				mShadowRadius = a.getFloat(attr, 0f);
			}
		}
		a.recycle();
		
		a = context.obtainStyledAttributes(attrs, R.styleable.KeyboardView);
		n = a.getIndexCount();

		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			if(attr == R.styleable.KeyboardView_popupWidth){
				mPopupWidth = a.getDimensionPixelSize(attr, 213);
			} else if(attr == R.styleable.KeyboardView_popupHeight){
				mPopupHeight = a.getDimensionPixelSize(attr, 213);
			}
		}
		a.recycle();

		a = context.obtainStyledAttributes(ResInternalR.styleable.Theme);
		mBackgroundDimAmount = a.getFloat(ResAndroidR.styleable.Theme_backgroundDimAmount, 0.5f);
		a.recycle();

		mPreviewPopup = new PopupWindow(context);
		if (previewLayout != 0) {
			mPreviewText = (TextView) inflate.inflate(previewLayout, null);
			mPreviewTextSizeLarge = (int) mPreviewText.getTextSize();
			mPreviewPopup.setContentView(mPreviewText);
			mPreviewPopup.setBackgroundDrawable(null);
		} else {
			mShowPreview = false;
		}

		mPreviewPopup.setTouchable(false);

		mPopupParent = this;
		// mPredicting = true;

		mPadding = new Rect(0, 0, 0, 0);
		mMiniKeyboardCache = new HashMap<Key, View>();
		if(mKeyBackground != null){
			mKeyBackground.getPadding(mPadding);
		}

		try {
			mAccessibilityManager = (AccessibilityManager) ReflectUtils
					.invokeStaticMethod(AccessibilityManager.class.getName(), "getInstance", new Class[] { Context.class }, new Object[] { context });
		} catch (Exception e) {
			e.printStackTrace();
		}
		mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

		mLastSentIndex = NOT_A_KEY;
		init();
	}
	
	private void init() {
		resetLocation();
	}

	public void setOnKeyboardActionListener(OnKeyboardActionListener listener) {
		mKeyboardActionListener = listener;
	}

	protected OnKeyboardActionListener getOnKeyboardActionListener() {
		return mKeyboardActionListener;
	}

	private void setKeyboard(KeyboardData keyboard) {
		if(keyboard == null) return ;
		
		if (mKeyboard != null) {
			showPreview(NOT_A_KEY);
		}

		removeMessages();
		mKeyboard = keyboard;
		List<Key> keys = mKeyboard.getKeys();
		mKeys = keys.toArray(new Key[keys.size()]);
		//requestLayout();

		mKeyboardChanged = true;
		invalidateAllKeys();
		mMiniKeyboardCache.clear();
	}

	public KeyboardAdapter getAdapter() {
		return mAdapter;
	}
	
	public Keyboard getKeyboard() {
		return mKeyboard;
	}
	
	public int getLabelTextSize() {
		return mLabelTextSize;
	}
	
	public int getKeyTextSize() {
		return mKeyTextSize;
	}
	
	public int getKeyTextColor() {
		return mKeyTextColor;
	}
	
	public float getShadowRadius() {
		return mShadowRadius;
	}
	
	public float getShadowColor() {
		return mShadowColor;
	}
	
	public float getBackgroundDimAmount() {
		return mBackgroundDimAmount;
	}

	public boolean setShifted(boolean shifted) {
		if (mKeyboard != null) {
			if (mKeyboard.setShifted(shifted)) {
				// The whole keyboard probably needs to be redrawn
				invalidateAllKeys();
				return true;
			}
		}
		return false;
	}

	public boolean isShifted() {
		if (mKeyboard != null) {
			return mKeyboard.isShifted();
		}
		return false;
	}

	public void setPreviewEnabled(boolean previewEnabled) {
		mShowPreview = previewEnabled;
	}

	public boolean isPreviewEnabled() {
		return mShowPreview;
	}

	public int getVerticalCorrection(){
		return mVerticalCorrection;
	}
	
	public void setVerticalCorrection(int verticalOffset) {
		mVerticalCorrection = verticalOffset;
	}

	public void setPopupParent(View v) {
		mPopupParent = v;
	}

	public void setPopupOffset(int x, int y) {
		mMiniKeyboardOffsetX = x;
		mMiniKeyboardOffsetY = y;
		if (mPreviewPopup.isShowing()) {
			mPreviewPopup.dismiss();
		}
	}

	private CharSequence adjustCase(CharSequence label) {
		if (mKeyboard.isShifted() && label != null && label.length() < 3 && Character.isLowerCase(label.charAt(0))) {
			label = label.toString().toUpperCase();
		}
		return label;
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if(DEBUG){
			Log.d(TAG, "FocusKeyboard onMeasure ... ");
		}
	}

	public void onClick(View v) {
		dismissPopupKeyboard();
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (mKeyboard != null) {
			if (DEBUG) {
				Log.d(TAG, "FocusKeyboard onSizeChanged w = " + w + " h = " + h + " oldw = " + oldw + " oldh = " + oldh);
			}
			mKeyboard.resizeData(w, h);
		}
		// Release the buffer, if any and it will be reallocated on the next draw
		mBuffer = null;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if(DEBUG){
			Log.d(TAG, "FocusKeyboardView dispatchDraw ...");
		}
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		if(DEBUG){
			Log.d(TAG, "FocusKeyboardView onDraw ...");
		}
		super.onDraw(canvas);
		if (mDrawPending || mBuffer == null || mKeyboardChanged) {
			onBufferDraw();
		}
		canvas.drawBitmap(mBuffer, 0, 0, null);
	}

	// 主要绘制背景
	private void onBufferDraw() {
		if (mBuffer == null || mKeyboardChanged) {
			if (mBuffer == null || mKeyboardChanged && (mBuffer.getWidth() != getWidth() || mBuffer.getHeight() != getHeight())) {
				// Make sure our bitmap is at least 1x1
				final int width = Math.max(1, getWidth());
				final int height = Math.max(1, getHeight());
				mBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
				mCanvas = new Canvas(mBuffer);
			}
			invalidateAllKeys();
			mKeyboardChanged = false;
		}

		final Canvas canvas = mCanvas;
		canvas.clipRect(mDirtyRect, Op.REPLACE);

		if (mKeyboard == null)
			return;

		final Drawable keyBackground = mKeyBackground;
		final int kbdPaddingLeft = getPaddingLeft();
		final int kbdPaddingTop = getPaddingTop();
		final Key[] keys = mKeys;

		canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
		if(keyBackground != null){
			final int keyCount = keys.length;
			for (int i = 0; i < keyCount; i++) {
				final Key key = keys[i];
				if (key == null)
					continue;
				int[] drawableState = key.getCurrentDrawableState();
				keyBackground.setState(drawableState);

				final Rect bounds = keyBackground.getBounds();
				if (key.width != bounds.right || key.height != bounds.bottom) {
					keyBackground.setBounds(0, 0, key.width, key.height);
				}

				canvas.translate(key.x + kbdPaddingLeft, key.y + kbdPaddingTop);
				keyBackground.draw(canvas);
				canvas.translate(-key.x - kbdPaddingLeft, -key.y - kbdPaddingTop);
			}
		}

		mDrawPending = false;
		mDirtyRect.setEmpty();
	}

	private int getKeyIndices(int x, int y, int[] allKeys) {
		final Key[] keys = mKeys;
		int primaryIndex = NOT_A_KEY;
		int closestKey = NOT_A_KEY;
		int closestKeyDist = mProximityThreshold + 1;
		Arrays.fill(mDistances, Integer.MAX_VALUE);
		int[] nearestKeyIndices = mKeyboard.getNearestKeys(x, y);
		final int keyCount = nearestKeyIndices.length;
		for (int i = 0; i < keyCount; i++) {
			final Key key = keys[nearestKeyIndices[i]];
			int dist = 0;
			boolean isInside = key.isInside(x, y);
			if (isInside) {
				primaryIndex = nearestKeyIndices[i];
			}

			if (((mProximityCorrectOn && (dist = key.squaredDistanceFrom(x, y)) < mProximityThreshold) || isInside) && key.codes[0] > 32) {
				// Find insertion point
				final int nCodes = key.codes.length;
				if (dist < closestKeyDist) {
					closestKeyDist = dist;
					closestKey = nearestKeyIndices[i];
				}

				if (allKeys == null)
					continue;

				for (int j = 0; j < mDistances.length; j++) {
					if (mDistances[j] > dist) {
						// Make space for nCodes codes
						System.arraycopy(mDistances, j, mDistances, j + nCodes, mDistances.length - j - nCodes);
						System.arraycopy(allKeys, j, allKeys, j + nCodes, allKeys.length - j - nCodes);
						for (int c = 0; c < nCodes; c++) {
							allKeys[j + c] = key.codes[c];
							mDistances[j + c] = dist;
						}
						break;
					}
				}
			}
		}
		if (primaryIndex == NOT_A_KEY) {
			primaryIndex = closestKey;
		}
		return primaryIndex;
	}

	private void detectAndSendKey(int index, int x, int y, long eventTime) {
		if (index != NOT_A_KEY && index < mKeys.length) {
			final Key key = mKeys[index];
			if (key.text != null) {
				mKeyboardActionListener.onText(key.text);
				mKeyboardActionListener.onRelease(NOT_A_KEY);
			} else {
				int code = key.codes[0];
				// TextEntryState.keyPressedAt(key, x, y);
				int[] codes = new int[MAX_NEARBY_KEYS];
				Arrays.fill(codes, NOT_A_KEY);
				getKeyIndices(x, y, codes);
				// Multi-tap
				if (mInMultiTap) {
					if (mTapCount != -1) {
						mKeyboardActionListener.onKey(Keyboard.KEYCODE_DELETE, KEY_DELETE);
					} else {
						mTapCount = 0;
					}
					code = key.codes[mTapCount];
				}
				mKeyboardActionListener.onKey(code, codes);
				mKeyboardActionListener.onRelease(code);
			}
			mLastSentIndex = index;
			mLastTapTime = eventTime;
		}
	}

	private CharSequence getPreviewText(Key key) {
		return adjustCase(key.label);
	}

	private void showMiniKeyboard(int keyIndex){
		mCurrentKeyIndex = keyIndex;
		if (keyIndex != NOT_A_KEY) {
			initMiniPopup();
			showMiniKey(keyIndex);
		}
	}
	
	private void initMiniPopup(){
		dismissPopupKeyboard();
		
		if(mPopupLayout != 0){
			View container = inflate(getContext(), mPopupLayout, null);
			if(container instanceof IKeyboardExpand){
				mPopupContainer = (IKeyboardExpand)container;
			}
			mPopupKeyboard = new PopupWindow(container, mPopupWidth, mPopupHeight);
			mPopupKeyboard.setBackgroundDrawable(null);
			mPopupKeyboard.setOutsideTouchable(false);
			mPopupKeyboard.setFocusable(true);
		}
		// mPopupKeyboard.setClippingEnabled(false);
	}
	
	private void showMiniKey(final int keyIndex) {
		final Key[] keys = mKeys;
		if (keyIndex < 0 || keyIndex >= mKeys.length)
			return ;
		if(mPopupKeyboard == null)
			return ;
		
		final PopupWindow popupKeyboard = mPopupKeyboard;
		Key key = keys[keyIndex];
		
		if(mPopupContainer != null){
			mPopupContainer.setKeyboardExpand(popupKeyboard, key, mKeyboardActionListener);
		}
		mPopupX = key.x + key.width / 2 - mPopupWidth / 2;
		mPopupY = key.y + key.height / 2 - mPopupHeight / 2;
		
		getLocationInWindow(mCoordinates);
		mCoordinates[0] += mMiniKeyboardOffsetX;
		mCoordinates[1] += mMiniKeyboardOffsetY;

		mPopupX += mCoordinates[0] + getPaddingLeft();
		mPopupY += mCoordinates[1] + getPaddingTop();
		
		if (popupKeyboard.isShowing()) {
			popupKeyboard.update(mPopupX, mPopupY, mPopupWidth, mPopupHeight);
		} else {
			popupKeyboard.showAtLocation(this, Gravity.NO_GRAVITY, mPopupX, mPopupY);
		}
	}
	
	private void showPreview(int keyIndex) {
		int oldKeyIndex = mCurrentKeyIndex;
		final PopupWindow previewPopup = mPreviewPopup;

		mCurrentKeyIndex = keyIndex;
		// Release the old key and press the new key
		final Key[] keys = mKeys;
		if (oldKeyIndex != mCurrentKeyIndex) {
			if (oldKeyIndex != NOT_A_KEY && keys.length > oldKeyIndex) {
				Key oldKey = keys[oldKeyIndex];
				oldKey.onReleased(mCurrentKeyIndex == NOT_A_KEY);
				invalidateKey(oldKeyIndex);
				final int keyCode = oldKey.codes[0];
				sendAccessibilityEventForUnicodeCharacter(AccessibilityEvent.TYPE_VIEW_HOVER_EXIT, keyCode);
			}
			if (mCurrentKeyIndex != NOT_A_KEY && keys.length > mCurrentKeyIndex) {
				Key newKey = keys[mCurrentKeyIndex];
				newKey.onPressed();
				invalidateKey(mCurrentKeyIndex);
				final int keyCode = newKey.codes[0];
				sendAccessibilityEventForUnicodeCharacter(AccessibilityEvent.TYPE_VIEW_HOVER_ENTER, keyCode);
			}
		}
		// If key changed and preview is on ...
		if (oldKeyIndex != mCurrentKeyIndex && mShowPreview) {
			mHandler.removeMessages(MSG_SHOW_PREVIEW);
			if (previewPopup.isShowing()) {
				if (keyIndex == NOT_A_KEY) {
					mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_REMOVE_PREVIEW), DELAY_AFTER_PREVIEW);
				}
			}
			if (keyIndex != NOT_A_KEY) {
				if (previewPopup.isShowing() && mPreviewText.getVisibility() == VISIBLE) {
					showKey(keyIndex);
				} else {
					mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SHOW_PREVIEW, keyIndex, 0), DELAY_BEFORE_PREVIEW);
				}
			}
		}
	}

	private void showKey(final int keyIndex) {
		final PopupWindow previewPopup = mPreviewPopup;
		final Key[] keys = mKeys;
		if (keyIndex < 0 || keyIndex >= mKeys.length)
			return;
		Key key = keys[keyIndex];
		if (key.icon != null) {
			mPreviewText.setCompoundDrawables(null, null, null, key.iconPreview != null ? key.iconPreview : key.icon);
			mPreviewText.setText(null);
		} else {
			mPreviewText.setCompoundDrawables(null, null, null, null);
			mPreviewText.setText(getPreviewText(key));
			if (key.label.length() > 1 && key.codes.length < 2) {
				mPreviewText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mKeyTextSize);
				mPreviewText.setTypeface(Typeface.DEFAULT_BOLD);
			} else {
				mPreviewText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mPreviewTextSizeLarge);
				mPreviewText.setTypeface(Typeface.DEFAULT);
			}
		}
		mPreviewText.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		int popupWidth = Math.max(mPreviewText.getMeasuredWidth(), key.width + mPreviewText.getPaddingLeft() + mPreviewText.getPaddingRight());
		final int popupHeight = mPreviewHeight;
		LayoutParams lp = (LayoutParams) mPreviewText.getLayoutParams();
		if (lp != null) {
			lp.width = popupWidth;
			lp.height = popupHeight;
		}
		if (!mPreviewCentered) {
			mPopupPreviewX = key.x - mPreviewText.getPaddingLeft() + getPaddingLeft();
			mPopupPreviewY = key.y - popupHeight + mPreviewOffset;
		} else {
			mPopupPreviewX = 160 - mPreviewText.getMeasuredWidth() / 2;
			mPopupPreviewY = -mPreviewText.getMeasuredHeight();
		}
		mHandler.removeMessages(MSG_REMOVE_PREVIEW);
		getLocationInWindow(mCoordinates);
		mCoordinates[0] += mMiniKeyboardOffsetX; // Offset may be zero
		mCoordinates[1] += mMiniKeyboardOffsetY; // Offset may be zero

		// Set the preview background state
		mPreviewText.getBackground().setState(key.popupResId != 0 ? LONG_PRESSABLE_STATE_SET : EMPTY_STATE_SET);
		mPopupPreviewX += mCoordinates[0];
		mPopupPreviewY += mCoordinates[1];

		// If the popup cannot be shown above the key, put it on the side
		getLocationOnScreen(mCoordinates);
		if (mPopupPreviewY + mCoordinates[1] < 0) {
			// If the key you're pressing is on the left side of the keyboard,
			// show the popup on
			// the right, offset by enough to see at least one key to the
			// left/right.
			if (key.x + key.width <= getWidth() / 2) {
				mPopupPreviewX += (int) (key.width * 2.5);
			} else {
				mPopupPreviewX -= (int) (key.width * 2.5);
			}
			mPopupPreviewY += popupHeight;
		}

		if (previewPopup.isShowing()) {
			previewPopup.update(mPopupPreviewX, mPopupPreviewY, popupWidth, popupHeight);
		} else {
			previewPopup.setWidth(popupWidth);
			previewPopup.setHeight(popupHeight);
			previewPopup.showAtLocation(mPopupParent, Gravity.NO_GRAVITY, mPopupPreviewX, mPopupPreviewY);
		}
		mPreviewText.setVisibility(VISIBLE);
	}

	private void sendAccessibilityEventForUnicodeCharacter(int eventType, int code) {
		if (mAccessibilityManager.isEnabled()) {
			Context mContext = getContext();
			AccessibilityEvent event = AccessibilityEvent.obtain(eventType);
			onInitializeAccessibilityEvent(event);
			String text = null;
			// This is very efficient since the properties are cached.
			final boolean speakPassword = Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.ACCESSIBILITY_SPEAK_PASSWORD, 0) != 0;
			// Add text only if password announcement is enabled or if headset is
			// used to avoid leaking passwords.
			if (speakPassword || mAudioManager.isBluetoothA2dpOn() || mAudioManager.isWiredHeadsetOn()) {
				switch (code) {
				case Keyboard.KEYCODE_ALT:
					text = mContext.getString(ResInternalR.string.keyboardview_keycode_alt);
					break;
				case Keyboard.KEYCODE_CANCEL:
					text = mContext.getString(ResInternalR.string.keyboardview_keycode_cancel);
					break;
				case Keyboard.KEYCODE_DELETE:
					text = mContext.getString(ResInternalR.string.keyboardview_keycode_delete);
					break;
				case Keyboard.KEYCODE_DONE:
					text = mContext.getString(ResInternalR.string.keyboardview_keycode_done);
					break;
				case Keyboard.KEYCODE_MODE_CHANGE:
					text = mContext.getString(ResInternalR.string.keyboardview_keycode_mode_change);
					break;
				case Keyboard.KEYCODE_SHIFT:
					text = mContext.getString(ResInternalR.string.keyboardview_keycode_shift);
					break;
				case '\n':
					text = mContext.getString(ResInternalR.string.keyboardview_keycode_enter);
					break;
				default:
					text = String.valueOf((char) code);
				}
			} else if (!mHeadsetRequiredToHearPasswordsAnnounced) {
				// We want the waring for required head set to be send with both the
				// hover enter and hover exit event, so set the flag after the exit.
				if (eventType == AccessibilityEvent.TYPE_VIEW_HOVER_EXIT) {
					mHeadsetRequiredToHearPasswordsAnnounced = true;
				}
				text = mContext.getString(ResInternalR.string.keyboard_headset_required_to_hear_password);
			} else {
				text = mContext.getString(ResInternalR.string.keyboard_password_character_no_headset);
			}
			event.getText().add(text);
			mAccessibilityManager.sendAccessibilityEvent(event);
		}
	}

	public void invalidateAllKeys() {
		mDirtyRect.union(0, 0, getWidth(), getHeight());
		mDrawPending = true;
		invalidate();
	}

	public void invalidateKey(int keyIndex) {
		if (mKeys == null)
			return;
		if (keyIndex < 0 || keyIndex >= mKeys.length) {
			return;
		}
		final Key key = mKeys[keyIndex];
		mDirtyRect.union(key.x + getPaddingLeft(), key.y + getPaddingTop(), key.x + key.width + getPaddingLeft(), key.y + key.height + getPaddingTop());
		onBufferDraw();
		invalidate(key.x + getPaddingLeft(), key.y + getPaddingTop(), key.x + key.width + getPaddingLeft(), key.y + key.height + getPaddingTop());
	}

	protected void swipeRight() {
		mKeyboardActionListener.swipeRight();
	}

	protected void swipeLeft() {
		mKeyboardActionListener.swipeLeft();
	}

	protected void swipeUp() {
		mKeyboardActionListener.swipeUp();
	}

	protected void swipeDown() {
		mKeyboardActionListener.swipeDown();
	}

	public void closing() {
		if (mPreviewPopup.isShowing()) {
			mPreviewPopup.dismiss();
		}
		removeMessages();

		dismissPopupKeyboard();
		mBuffer = null;
		mCanvas = null;
		mMiniKeyboardCache.clear();
	}

	private void removeMessages() {
		mHandler.removeMessages(MSG_REPEAT);
		mHandler.removeMessages(MSG_LONGPRESS);
		mHandler.removeMessages(MSG_SHOW_PREVIEW);
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		closing();
	}

	private void dismissPopupKeyboard() {
		if (mPopupKeyboard != null && mPopupKeyboard.isShowing()) {
			mPopupKeyboard.dismiss();
			invalidateAllKeys();
		}
	}

	public boolean handleBack() {
		if (mPopupKeyboard.isShowing()) {
			dismissPopupKeyboard();
			return true;
		}
		return false;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (DEBUG) {
			Log.d(TAG, "FocusKeyboard onLayout... width = " + getMeasuredWidth() + " height = " + getMeasuredHeight());
		}

		if (!isLayoutRequested()) {
			return;
		}
		
		int measureWidth = getMeasuredWidth();
		int measureHeight = getMeasuredHeight();
		if(measureWidth * measureHeight != 0){
			if(mAdapter != null && mAdapter.getkeyboardData() == null){
				int padHrizontal = getPaddingLeft() + getPaddingRight();
				int padVertical = getPaddingTop() + getPaddingBottom();
				mAdapter.resetKeyboard(measureWidth - padHrizontal, measureHeight - padVertical);
				setKeyboard(mAdapter.getkeyboardData());
				mItemCount = mAdapter.getCount();
			}
		}

		if (mKeyboard != null && mKeyboardChanged) {
			layoutChildren();
			setNeedInitNode(true);
		}

		afterLayout(changed, l, t, r, b);
	}
	
	private boolean isNeedShowPopupKeyboard(int keyIndex){
		final Key[] keys = mKeys;
		if (keyIndex < 0 || keyIndex >= mKeys.length)
			return false;
		Key key = keys[keyIndex];
		return mPopupLayout != 0 && (key.popupCharacters == null ? false : key.popupCharacters.length() > 1);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(!isLayouted()) return true;
		dismissPopupKeyboard();
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
			if(isNeedShowPopupKeyboard(mIndex)){
				showMiniKeyboard(mIndex);
			} else if(event.getRepeatCount() > 1){
				final View selectView = getSelectedView();
				if(selectView != null){
					int x = (selectView.getLeft() + selectView.getRight()) / 2;
					int y = (selectView.getTop() + selectView.getBottom()) / 2;
					detectAndSendKey(mIndex, x, y, event != null ? event.getEventTime() : 0);
				}
			}
			this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(FOCUS_LEFT));
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(!isLayouted()) return true;

		if ((KeyEvent.KEYCODE_DPAD_CENTER == keyCode || KeyEvent.KEYCODE_ENTER == keyCode || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) && getSelectedView() != null) {
			if(!isNeedShowPopupKeyboard(mIndex)){
				final View selectView = getSelectedView();
				if(selectView != null){
					int x = (selectView.getLeft() + selectView.getRight()) / 2;
					int y = (selectView.getTop() + selectView.getBottom()) / 2;
					detectAndSendKey(mIndex, x, y, event != null ? event.getEventTime() : 0);
				}
			}
			this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(FOCUS_LEFT));
			return true;
		} else {
			return super.onKeyUp(keyCode, event);
		}
	}
	
	public Key getKey(int position){
		Key key = null;
		int keyCount = mKeys == null ? 0 : mKeys.length;
		if(position >= 0 && position < keyCount){
			key = mKeys[position];
		}
		return key;
	}
	
	final int getKeyTop(int position){
		Key key = getKey(position);
		return key == null ? getPaddingTop() : getPaddingTop() + key.y;
	}
	
	final int getKeyLeft(int position){
		Key key = getKey(position);
		return key == null ? getPaddingLeft() : getPaddingLeft() + key.x;
	}
	
	final int getKeyRight(int position){
		Key key = getKey(position);
		return key == null ? getPaddingLeft() : getPaddingLeft() + key.x + key.width;
	}
	
	final int getKeyBottom(int position){
		Key key = getKey(position);
		return key == null ? getPaddingTop() : (getPaddingTop() + key.y + key.height);
	}
	
	final int getKeyWidth(int position){
		Key key = getKey(position);
		return key == null ? 0 : key.width;
	}
	
	final int getKeyHeight(int position){
		Key key = getKey(position);
		return key == null ? 0 : key.height;
	}
	
	/**
	 * 根据适配器数据布局视图，利用RecycleBin重用视图
	 */
	private void layoutChildren() {
        if(DEBUG){
            Log.d(TAG, "layoutChildren mIndex = " + mIndex);
        }
		final boolean blockLayoutRequests = mBlockLayoutRequests;
		if (!blockLayoutRequests) {
			mBlockLayoutRequests = true;
		} else {
			return;
		}

		try {
			invalidate();
			if (mAdapter == null) {
				resetLocation();
				return;
			}

			int childrenTop = getPaddingTop();
			int childCount = getChildCount();

			if (mItemCount == 0) {
				resetLocation();
				return;
			} else if (mItemCount != mAdapter.getCount()) {
				throw new IllegalStateException("The content of the adapter has changed but " + "FocusSpecificLocationLayout did not receive a notification. Make sure the content of "
						+ "your adapter is not modified from a background thread, but only " + "from the UI thread. [in FocusSpecificLocationLayout(" + getId() + ", " + getClass() + ") with Adapter("
						+ mAdapter.getClass() + ")]");
			}

			final int firstPosition = mFirstPosition;
			final RecycleBin recycleBin = mRecycler;

			boolean dataChanged = mDataChanged;
			if (dataChanged) {
				int firstvisibleChildIndex = firstPosition;
				for (int i = firstvisibleChildIndex; i >= 0; i--) {
					recycleBin.addScrapView(getChildAt(i), firstPosition + i);
				}
				for (int i = firstvisibleChildIndex + 1; i < childCount; i++) {
					recycleBin.addScrapView(getChildAt(i), firstPosition + i);
				}
			} else {
				recycleBin.fillActiveViews(childCount, firstPosition);
			}

			detachAllViewsFromParent();
			recycleBin.removeSkippedScrap();

			if (childCount == 0) {
				fillFromTop(childrenTop);
			} else {
                if(mIndex >= mItemCount){
                    mIndex = mItemCount - 1;
                }
				int startIndex = mIndex;
				if (startIndex < 0) {
					startIndex = getFocusableItemIndex();
					mIndex = startIndex;
				}
				fillSpecific(startIndex, getKeyTop(startIndex));
			}

			recycleBin.scrapActiveViews();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!blockLayoutRequests) {
				mBlockLayoutRequests = false;
			}
			if (DEBUG) {
				Log.d(TAG, "layoutChildren child count = " + getChildCount());
			}
		}
	}
	
	/**
	 * 从指定位置开始填充视图
	 * @param position 指定位置（有效）
	 * @param top 开始位置最上方坐标
	 * @return 返回最后填充的视图
	 */
	protected View fillSpecific(int position, int top) {
		boolean tempIsSelected = position == mIndex;
		View temp = makeAndAddView(position, top, true, getKeyLeft(position), tempIsSelected);

		View above;
		View below;

		above = fillUp(position - 1, getKeyBottom(position - 1));
		below = fillDown(position + 1, getKeyTop(position + 1));

		if (tempIsSelected) {
			return temp;
		} else if (above != null) {
			return above;
		} else {
			return below;
		}
	}

	/**
	 * 从指定位置开始向上填充视图
	 * @param pos 指定位置（有效）
	 * @param nextBottom 下一位置最下方坐标
	 * @return 返回选中填充的视图
	 */
	protected View fillUp(int pos, int nextBottom) {
		View selectedView = null;

		int end = 0;
		if ((getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
			end = getPaddingTop();
		}

		while (nextBottom > end && pos >= 0) {
			boolean selected = pos == mIndex;
			nextBottom = getKeyBottom(pos);
			View child = makeAndAddView(pos, nextBottom, false, getKeyLeft(pos), selected);
			if (selected) {
				selectedView = child;
			}
			pos--;
		}

		return selectedView;
	}

	/**
	 * 从指定位置开始向下填充视图
	 * @param nextTop 下一位置最上方坐标
	 * @return 返回选中填充的视图
	 */
	protected View fillFromTop(int nextTop) {
		mFirstPosition = Math.min(mFirstPosition, mIndex);
		mFirstPosition = Math.min(mFirstPosition, mItemCount - 1);
		if (mFirstPosition < 0) {
			mFirstPosition = 0;
		}
		return fillDown(mFirstPosition, nextTop);
	}

	/**
	 * 从指定位置开始向下填充视图
	 * @param position 指定位置（有效）
	 * @param nextTop 下一位置最上方坐标
	 * @return 返回选中填充的视图
	 */
	private View fillDown(int position, int nextTop) {
		View selectedView = null;

		int end = (getBottom() - getTop());
		if ((getGroupFlags() & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK) {
			end -= getPaddingBottom();
		}

		while (nextTop < end && position < mItemCount) {
			boolean selected = position == mIndex;
			nextTop = getKeyTop(position);
			View child = makeAndAddView(position, nextTop, true, getKeyLeft(position), selected);

			if (selected) {
				selectedView = child;
			}
			position++;
		}

		return selectedView;
	}

	/**
	 * 初始化视图
	 * @param position
	 * @param y
	 * @param flow
	 * @param childrenLeft
	 * @param selected
	 * @return
	 */
	private View makeAndAddView(int position, int y, boolean flow, int childrenLeft, boolean selected) {
		View child;

		if (!mDataChanged) {
			child = mRecycler.getActiveView(position);
			if (child != null) {
				setupChild(child, position, y, flow, childrenLeft, selected, true);
				return child;
			}
		}

		child = obtainView(position, mIsScrap);
		setupChild(child, position, y, flow, childrenLeft, selected, mIsScrap[0]);

		return child;
	}
	
	/**
	 * 测量和布局视图，并将视图添加到父视图
	 * @param child
	 * @param position
	 * @param y
	 * @param flowDown
	 * @param childrenLeft
	 * @param selected
	 * @param recycled
	 */
	private void setupChild(View child, int position, int y, boolean flowDown, int childrenLeft, boolean selected, boolean recycled) {
		final boolean isSelected = selected && shouldShowSelector();
		final boolean updateChildSelected = isSelected != child.isSelected();
		final boolean needToMeasure = !recycled || updateChildSelected || child.isLayoutRequested();

		int lastWidth = 0;
		int lastHeight = 0;
		LayoutParams p = (LayoutParams) child.getLayoutParams();
		if (p == null) {
			p = generateDefaultLayoutParams();
		} else {
			lastWidth = p.width;
			lastHeight = p.height;
		}
		p.width = getKeyWidth(position);
		p.height = getKeyHeight(position);
		p.viewType = mAdapter.getItemViewType(position);
		p.itemId = mAdapter.getItemId(position);

        if(DEBUG){
            Log.d(TAG, "mIndex = " + position + " recycled = " + recycled);
        }
        if(recycled){
            attachViewToParent(child, flowDown ? -1 : 0, p);
        } else {
            addViewInLayout(child, flowDown ? -1 : 0, p);
        }

		if (updateChildSelected) {
			child.setSelected(isSelected);
		}

		if (needToMeasure || (p.width != lastWidth || p.height != lastHeight)) {
			int childWidthSpec = View.MeasureSpec.makeMeasureSpec(p.width, View.MeasureSpec.EXACTLY);
			int childHeightSpec = View.MeasureSpec.makeMeasureSpec(p.height, View.MeasureSpec.EXACTLY);
			child.measure(childWidthSpec, childHeightSpec);
		} else {
			cleanupLayoutState(child);
		}

		final int w = child.getMeasuredWidth();
		final int h = child.getMeasuredHeight();
		final int childTop = flowDown ? y : y - h;

		if (needToMeasure || (p.width != lastWidth || p.height != lastHeight)) {
			final int childRight = childrenLeft + w;
			final int childBottom = childTop + h;
			if(DEBUG){
	            Log.d(TAG, "mIndex = " + position + " left = " + childrenLeft + " top = " + childTop + " right = " + childRight + " bottom = " + childBottom);
	        }
			child.layout(childrenLeft, childTop, childRight, childBottom);
		} else {
			child.offsetLeftAndRight(childrenLeft - child.getLeft());
			child.offsetTopAndBottom(childTop - child.getTop());
		}

		if (!child.isDrawingCacheEnabled()) {
			child.setDrawingCacheEnabled(true);
		}

		if (recycled && (((LayoutParams) child.getLayoutParams()).scrappedFromPosition) != position) {
			child.jumpDrawablesToCurrentState();
		}
	}

	boolean shouldShowSelector() {
		return hasFocus() && !isInTouchMode();
	}

	protected View obtainView(int position, boolean[] isScrap) {
		isScrap[0] = false;
		View scrapView;

		scrapView = mRecycler.getTransientStateView(position);
		if (scrapView != null) {
			return scrapView;
		}

		scrapView = mRecycler.getScrapView(position);
		if (DEBUG) {
			Log.d(TAG, "obtainView->getScrapView position = " + position + " scrapView=" + scrapView);
		}

		View child;
		if (scrapView != null) {
			child = mAdapter.getView(position, scrapView, this);

			if (child != scrapView) {
				mRecycler.addScrapView(scrapView, position);
			} else {
				isScrap[0] = true;
			}
		} else {
			child = mAdapter.getView(position, null, this);
		}

		if (mAdapterHasStableIds) {
			final ViewGroup.LayoutParams vlp = child.getLayoutParams();
			LayoutParams lp;
			if (vlp == null) {
				lp = (LayoutParams) generateDefaultLayoutParams();
			} else if (!checkLayoutParams(vlp)) {
				lp = (LayoutParams) generateLayoutParams(vlp);
			} else {
				lp = (LayoutParams) vlp;
			}
			lp.width = getKeyWidth(position);
			lp.height = getKeyHeight(position);
			lp.itemId = mAdapter.getItemId(position);
			lp.viewType = mAdapter.getItemViewType(position);
			child.setLayoutParams(lp);
		}
		return child;
	}
	
	@Override
	public FocusRectParams getFocusParams() {
		FocusRectParams params = super.getFocusParams();
		Log.d("===", "mIndex = " + mIndex + " rect = " + params.focusRect());
		return params;
	}
	
	/**
	 * 设置布局适配器
	 * @param adapter
	 */
	public void setAdapter(KeyboardAdapter adapter) {
		if (null != mAdapter) {
			mAdapter.unregisterDataSetObserver(mDataSetObserver);
			mAdapter.clearKeyboardData();
			resetLocation();
		}

		mRecycler.clear();
		mAdapter = adapter;

		if (mAdapter != null) {
			mAdapterHasStableIds = mAdapter.hasStableIds();
			mItemCount = mAdapter.getCount();
			mDataSetObserver = new AdapterDataSetObserver();
			mAdapter.registerDataSetObserver(mDataSetObserver);
			mRecycler.setViewTypeCount(mAdapter.getViewTypeCount());
		} else {
			resetLocation();
		}

		setNeedInitNode(true);
		requestLayout();
	}

	private void resetLocation() {
		mDataChanged = false;
		removeAllViewsInLayout();
		mOldSelectedPosition = mIndex;
		mOldItemCount = mItemCount;
		mItemCount = 0;
	}

	private void notifyDataSetChanged() {
		mDataChanged = true;
		mItemCount = mAdapter.getCount();
		setNeedInitNode(true);
		requestLayout();
	}

	protected int getGroupFlags() {
		try {
			Class<?> c = Class.forName("android.view.ViewGroup");
			Field flags = c.getDeclaredField("mGroupFlags");
			flags.setAccessible(true);
			return flags.getInt(this);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * 观察者
	 * @author quanqing.hqq
	 *
	 */
	class AdapterDataSetObserver extends DataSetObserver {
		@Override
		public void onChanged() {
			notifyDataSetChanged();
		}

		@Override
		public void onInvalidated() {
			invalidate();
		}
	}
	
	static boolean hasTransientState(View view) {
		try {
			return (Boolean) ReflectUtils.invokeMethod(view, "hasTransientState", new Object[] {});
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 根据position，从mScrapView中找： 1. 如果有view.scrappedFromPosition =
	 * position的，直接返回该view； 2. 否则返回mScrapView中最后一个； 3. 如果缓存中没有view，则返回null；
	 * 
	 * @param scrapViews
	 *            视图垃圾回收池
	 * @param position
	 *            在视图列表中的位置
	 * @return 返回匹配的视图
	 */
	static View retrieveFromScrap(ArrayList<View> scrapViews, int position) {
		int size = scrapViews.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				View view = scrapViews.get(i);
				int fromPosition = ((LayoutParams) view.getLayoutParams()).scrappedFromPosition;
				if (fromPosition == position) {
					scrapViews.remove(i);
					return view;
				}
			}
			return scrapViews.remove(size - 1);
		} else {
			return null;
		}
	}
	
	/**
	 * 视图布局参数
	 * @author quanqing.hqq
	 *
	 */
	public static class LayoutParams extends ViewGroup.LayoutParams {
		int viewType;// 视图类型
		int scrappedFromPosition;// 回收的索引
		long itemId = -1;

		public LayoutParams(Context c, AttributeSet attrs) {
			super(c, attrs);
		}

		public LayoutParams(int w, int h) {
			super(w, h);
		}

		public LayoutParams(int w, int h, int viewType) {
			super(w, h);
			this.viewType = viewType;
		}

		public LayoutParams(ViewGroup.LayoutParams source) {
			super(source);
		}
	}
	
	public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }
	
	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	/**
	 * 垃圾回收机制
	 * @author quanqing.hqq
	 *
	 */
	public class RecycleBin {
		private RecyclerListener mRecyclerListener;
		private int mFirstActivePosition;// 第一个可见的视图位置
		private View[] mActiveViews = new View[0];// 可见视图数组
		private ArrayList<View>[] mScrapViews;// 不同类型视图垃圾回收池
		private int mViewTypeCount;// 视图类型
		private ArrayList<View> mCurrentScrap;
		private ArrayList<View> mSkippedScrap;
		private SparseArray<View> mTransientStateViews;

		/**
		 * 设置ViewTypeCount，然后初始化类成员变量
		 * 
		 * @param viewTypeCount
		 *            视图类型个数
		 */
		public void setViewTypeCount(int viewTypeCount) {
			if (viewTypeCount < 1) {
				throw new IllegalArgumentException("Can't have a viewTypeCount < 1");
			}

			@SuppressWarnings("unchecked")
			ArrayList<View>[] scrapViews = new ArrayList[viewTypeCount];
			for (int i = 0; i < viewTypeCount; i++) {
				scrapViews[i] = new ArrayList<View>();
			}
			mViewTypeCount = viewTypeCount;
			mCurrentScrap = scrapViews[0];
			mScrapViews = scrapViews;
		}

		/**
		 * 将mScrapView中回收回来的View设置一样标志，在下次被复用时，告诉ViewRoot重新layout该view
		 */
		public void markChildrenDirty() {
			if (mViewTypeCount == 1) {
				final ArrayList<View> scrap = mCurrentScrap;
				final int scrapCount = scrap.size();
				for (int i = 0; i < scrapCount; i++) {
					scrap.get(i).forceLayout();
				}
			} else {
				final int typeCount = mViewTypeCount;
				for (int i = 0; i < typeCount; i++) {
					final ArrayList<View> scrap = mScrapViews[i];
					final int scrapCount = scrap.size();
					for (int j = 0; j < scrapCount; j++) {
						scrap.get(j).forceLayout();
					}
				}
			}
			if (mTransientStateViews != null) {
				final int count = mTransientStateViews.size();
				for (int i = 0; i < count; i++) {
					mTransientStateViews.valueAt(i).forceLayout();
				}
			}
		}

		/**
		 * 判断给定的view的viewType指明是否可以回收回, 这里默认所有的视图都可以回收。除非设计header和footer
		 * 
		 * @param viewType
		 *            视图类型
		 * @return 可以回收的视图类型返回真
		 */
		public boolean shouldRecycleViewType(int viewType) {
			return viewType >= 0;
		}

		/**
		 * 清理ScrapView中的View，并将这些View从窗口中Detach
		 */
		void clear() {
			if (mViewTypeCount == 1) {
				final ArrayList<View> scrap = mCurrentScrap;
				final int scrapCount = scrap.size();
				for (int i = 0; i < scrapCount; i++) {
					removeDetachedView(scrap.remove(scrapCount - 1 - i), false);
				}
			} else {
				final int typeCount = mViewTypeCount;
				for (int i = 0; i < typeCount; i++) {
					final ArrayList<View> scrap = mScrapViews[i];
					final int scrapCount = scrap.size();
					for (int j = 0; j < scrapCount; j++) {
						removeDetachedView(scrap.remove(scrapCount - 1 - j), false);
					}
				}
			}
			clearTransientStateViews();
		}

		/**
		 * 填充mActiveView数组。当Adapter中的数据个数未发生变化时，此时用户可能只是滚动，或点击等操作，
		 * 视图中item的个数会发生变化，因此，需要将可视的item加入到mActiveView中来管理
		 * 
		 * @param childCount
		 *            child数量
		 * @param firstActivePosition
		 *            第一个可见视图位置
		 */
		public void fillActiveViews(int childCount, int firstActivePosition) {
			if (mActiveViews.length < childCount) {
				mActiveViews = new View[childCount];
			}
			mFirstActivePosition = firstActivePosition;

			final View[] activeViews = mActiveViews;
			for (int i = 0; i < childCount; i++) {
				View child = getChildAt(i);
				LayoutParams lp = (LayoutParams) child.getLayoutParams();
				if (lp != null) {
					activeViews[i] = child;
				}
			}
		}

		/**
		 * mFirstActivePosition是当前可视区域第一个视图的下标值，对应在adapter中的绝对值，
		 * 如果找到，则返回找到的View，并将mActiveView对应的位置设置为null
		 * 
		 * @param position
		 *            adpater中的绝对下标值
		 * @return 返回匹配的视图，如果没有则为Null
		 */
		View getActiveView(int position) {
			int index = position - mFirstActivePosition;
			final View[] activeViews = mActiveViews;
			if (index >= 0 && index < activeViews.length) {
				final View match = activeViews[index];
				activeViews[index] = null;
				return match;
			}
			return null;
		}

		View getTransientStateView(int position) {
			if (mTransientStateViews == null) {
				return null;
			}
			final int index = mTransientStateViews.indexOfKey(position);
			if (index < 0) {
				return null;
			}
			final View result = mTransientStateViews.valueAt(index);
			mTransientStateViews.removeAt(index);
			return result;
		}

		void clearTransientStateViews() {
			if (mTransientStateViews != null) {
				mTransientStateViews.clear();
			}
		}

		View getScrapView(int position) {
			if (mViewTypeCount == 1) {
				return retrieveFromScrap(mCurrentScrap, position);
			} else {
				int whichScrap = mAdapter.getItemViewType(position);
				if (whichScrap >= 0 && whichScrap < mScrapViews.length) {
					return retrieveFromScrap(mScrapViews[whichScrap], position);
				}
			}
			return null;
		}

		public void addScrapView(View scrap, int position) {
			LayoutParams lp = (LayoutParams) scrap.getLayoutParams();
			if (lp == null) {
				return;
			}

			lp.scrappedFromPosition = position;
			int viewType = lp.viewType;
			final boolean scrapHasTransientState = hasTransientState(scrap);
			if (!shouldRecycleViewType(viewType) || scrapHasTransientState) {
				if (scrapHasTransientState) {
					if (mSkippedScrap == null) {
						mSkippedScrap = new ArrayList<View>();
					}
					mSkippedScrap.add(scrap);
				}
				if (scrapHasTransientState) {
					initTransientStateViewsIfNeed();
					// scrap.dispatchStartTemporaryDetach();
					try {
						ReflectUtils.invokeMethod(scrap, "dispatchStartTemporaryDetach", new Object[0]);
					} catch (Exception e) {
						e.printStackTrace();
					}
					mTransientStateViews.put(position, scrap);
				}
				return;
			}

			// scrap.dispatchStartTemporaryDetach();
			try {
				ReflectUtils.invokeMethod(scrap, "dispatchStartTemporaryDetach", new Object[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (mViewTypeCount == 1) {
				mCurrentScrap.add(scrap);
			} else {
				mScrapViews[viewType].add(scrap);
			}

			scrap.setAccessibilityDelegate(null);
			if (mRecyclerListener != null) {
				mRecyclerListener.onMovedToScrapHeap(scrap);
			}
		}

		public void removeSkippedScrap() {
			if (mSkippedScrap == null) {
				return;
			}
			final int count = mSkippedScrap.size();
			for (int i = 0; i < count; i++) {
				removeDetachedView(mSkippedScrap.get(i), false);
			}
			mSkippedScrap.clear();
		}

		/**
		 * 将mActiveView中未使用的view回收
		 */
		public void scrapActiveViews() {
			final View[] activeViews = mActiveViews;
			final boolean hasListener = mRecyclerListener != null;
			final boolean multipleScraps = mViewTypeCount > 1;

			ArrayList<View> scrapViews = mCurrentScrap;
			final int count = activeViews.length;
			for (int i = count - 1; i >= 0; i--) {
				final View victim = activeViews[i];
				if (victim != null) {
					final LayoutParams lp = (LayoutParams) victim.getLayoutParams();
					int whichScrap = lp.viewType;

					activeViews[i] = null;

					final boolean scrapHasTransientState = hasTransientState(victim);
					if (!shouldRecycleViewType(whichScrap) || scrapHasTransientState) {
						if (scrapHasTransientState) {
							removeDetachedView(victim, false);
						}
						if (scrapHasTransientState) {
							initTransientStateViewsIfNeed();
							mTransientStateViews.put(mFirstActivePosition + i, victim);
						}
						continue;
					}

					if (multipleScraps) {
						scrapViews = mScrapViews[whichScrap];
					}

					// victim.dispatchStartTemporaryDetach();
					try {
						ReflectUtils.invokeMethod(victim, "dispatchStartTemporaryDetach", new Object[] {});
					} catch (Exception e) {
						e.printStackTrace();
					}

					lp.scrappedFromPosition = mFirstActivePosition + i;
					scrapViews.add(victim);

					victim.setAccessibilityDelegate(null);
					if (hasListener) {
						mRecyclerListener.onMovedToScrapHeap(victim);
					}
				}
			}

			pruneScrapViews();
		}

		/**
		 * mScrapView中每个ScrapView数组大小不应该超过mActiveView的大小，如果超过，
		 * 系统认为程序并没有复用convertView，
		 * 而是每次都是创建一个新的view，为了避免产生大量的闲置内存且增加OOM的风险，系统会在每次回收后，去检查一下，
		 * 将超过的部分释放掉，节约内存降低OOM风险
		 */
		private void pruneScrapViews() {
			final int maxViews = mActiveViews.length;
			final int viewTypeCount = mViewTypeCount;
			final ArrayList<View>[] scrapViews = mScrapViews;
			for (int i = 0; i < viewTypeCount; ++i) {
				final ArrayList<View> scrapPile = scrapViews[i];
				int size = scrapPile.size();
				final int extras = size - maxViews;
				size--;
				for (int j = 0; j < extras; j++) {
					removeDetachedView(scrapPile.remove(size--), false);
				}
			}

			if (mTransientStateViews != null) {
				for (int i = 0; i < mTransientStateViews.size(); i++) {
					final View v = mTransientStateViews.valueAt(i);
					if (!hasTransientState(v)) {
						mTransientStateViews.removeAt(i);
						i--;
					}
				}
			}
		}

		private void initTransientStateViewsIfNeed() {
			if (mTransientStateViews == null) {
				mTransientStateViews = new SparseArray<View>();
			}
		}
	}
	
	public static interface RecyclerListener {
		void onMovedToScrapHeap(View view);
	}

	@Override
	public Rect getClipFocusRect() {
		// TODO Auto-generated method stub
		return null;
	}
}
