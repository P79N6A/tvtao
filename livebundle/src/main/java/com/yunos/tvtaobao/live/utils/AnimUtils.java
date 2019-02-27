package com.yunos.tvtaobao.live.utils;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

public class AnimUtils {
	public static final int TIME_FADE = 300;
	
	public static final void fadeIn(View v) {
		fadeIn(v, TIME_FADE);
	}
	public static final void fadeIn(View v, int durationMillis) {
		if (v != null) {
			AlphaAnimation anim = new AlphaAnimation(0, 1);
			anim.setDuration(durationMillis);
			anim.setInterpolator(new AccelerateDecelerateInterpolator());
			anim.setRepeatMode(-1);
			v.setVisibility(View.VISIBLE);
			v.startAnimation(anim);
		}
	}
	
	public static final void fadeOut(View v) {
		fadeOut(v, TIME_FADE);
	}
	public static final void fadeOut(View v, int durationMillis) {
		if (v != null) {
			AlphaAnimation anim = new AlphaAnimation(1, 0);
			anim.setDuration(durationMillis);
			v.setVisibility(View.INVISIBLE);
			anim.setRepeatMode(-1);
			anim.setInterpolator(new AccelerateDecelerateInterpolator());
			v.startAnimation(anim);
		}
	}
	
	public static final void fadeInOut(final View v, final int durationMillis) {
		if (v != null) {
			AlphaAnimation anim = new AlphaAnimation(0, 1);
			anim.setDuration(durationMillis);
			anim.setInterpolator(new AccelerateDecelerateInterpolator());
			anim.setRepeatMode(-1);
			v.setVisibility(View.VISIBLE);
			v.startAnimation(anim);
			anim.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					fadeOut(v);
				}
			});
		}
	}

	public static final void translateLeftIn(final View v, int duration) {
		if (v != null) {
			TranslateAnimation animation = new TranslateAnimation(-v.getWidth(), 0, 0, 0);
			animation.setDuration(duration);
			animation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					v.setVisibility(View.VISIBLE);
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					v.clearAnimation();
				}

				@Override
				public void onAnimationRepeat(Animation animation) {}
			});
			v.setAnimation(animation);
		}
	}

}
