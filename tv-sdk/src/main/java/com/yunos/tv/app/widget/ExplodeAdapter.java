package com.yunos.tv.app.widget;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

public interface ExplodeAdapter {

	public View getCenterView();

	public ExplodeItem getExplodeItem(int position);

	public int getExplodeCount();

	public Rect getMaxPadding();

	public class ExplodeItem {
		Drawable explode;
		Drawable back;
		
		Rect backgroundPadding;

		Position stPosition;
		Position dyPosition;
		Position faPosition;
		
        int expendAlpha = 230; 
        int collipseAlpha = 102;

		int width;
		int height;

		int dyDuration;
		int faDuration;

		int frame = 0;

		int order;

		public ExplodeItem(Drawable v, Drawable b,Rect bgPadding, Position st, Position dy, Position fa, int dD, int fD, int f, int o, int w, int h,int eAlpha,int cAlpha) {
			explode = v;
			back = b;
			backgroundPadding = bgPadding;
			stPosition = st;
			dyPosition = dy;
			faPosition = fa;

			dyDuration = dD;
			faDuration = fD;

			frame = f;

			order = o;
			
			width = w;
			height = h;
			
            expendAlpha = eAlpha;
            collipseAlpha = cAlpha;
		}

		public Drawable getExplode() {
			return explode;
		}

		public Drawable getBack() {
			return back;
		}
		
		public Rect getBackgroundPadding(){
			return backgroundPadding;
		}
		
		public int getWidth(){
			return width;
		}
		
		public int getHeight(){
			return height;
		}
		
		public int getOrder(){
			return order;
		}

		public Position getStaticPosition() {
			return stPosition;
		}

		public Position getDynamicPosition() {
			return dyPosition;
		}

		public Position getFinalPosition() {
			return faPosition;
		}

		public int getDyncmicDuration() {
			return dyDuration;
		}

		public int getFinalDuration() {
			return faDuration;
		}
		
		public int getExpandAlpha(){
		    return expendAlpha;
		}
		public int getCollipseAlpha(){
		    return collipseAlpha;
		}

		public int getFrame() {
			return frame;
		}
	}
}
