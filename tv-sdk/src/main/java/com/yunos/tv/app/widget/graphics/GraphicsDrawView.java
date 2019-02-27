package com.yunos.tv.app.widget.graphics;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class GraphicsDrawView extends View implements PainterInterface {

	public GraphicsDrawView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public GraphicsDrawView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GraphicsDrawView(Context context) {
		super(context);
	}

	ArrayList<BasePainter> mList = new ArrayList<BasePainter>(20);

	@Override
	protected void onDraw(Canvas canvas) {
		int size = mList.size();
		for (int index = 0; index < size; index++) {
			mList.get(index).draw(canvas);
		}
	}

	public void addPainter(BasePainter painter) {
		painter.resgister(this);
		mList.add(painter);
	}

	public void removePainter(BasePainter painter) {
		painter.unregister();
		mList.remove(painter);
	}

	public void clearPainers() {
		int size = mList.size();
		for (int index = 0; index < size; index++) {
			mList.get(index).unregister();
		}

		mList.clear();
	}

}
