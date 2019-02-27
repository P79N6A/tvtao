package com.aliyun.base.ui.text;

import android.text.Layout;
import android.text.NoCopySpan;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class LinkImgMovementMethod extends LinkMovementMethod {

	public interface ClickStyle {

		public void onClick(View widget);

		public boolean checkIn(int x, int y);

	}

	private static final int CLICK = 1;
	private static final int UP = 2;
	private static final int DOWN = 3;

	private boolean action(int what, TextView widget, Spannable buffer) {
		boolean handled = false;

		Layout layout = widget.getLayout();

		int padding = widget.getTotalPaddingTop() + widget.getTotalPaddingBottom();
		int areatop = widget.getScrollY();
		int areabot = areatop + widget.getHeight() - padding;

		int linetop = layout.getLineForVertical(areatop);
		int linebot = layout.getLineForVertical(areabot);

		int first = layout.getLineStart(linetop);
		int last = layout.getLineEnd(linebot);

		ClickableSpan[] candidates = buffer.getSpans(first, last, ClickableSpan.class);

		int a = Selection.getSelectionStart(buffer);
		int b = Selection.getSelectionEnd(buffer);

		int selStart = Math.min(a, b);
		int selEnd = Math.max(a, b);

		if (selStart < 0) {
			if (buffer.getSpanStart(FROM_BELOW) >= 0) {
				selStart = selEnd = buffer.length();
			}
		}

		if (selStart > last)
			selStart = selEnd = Integer.MAX_VALUE;
		if (selEnd < first)
			selStart = selEnd = -1;

		switch (what) {
		case CLICK:
			if (selStart == selEnd) {
				return false;
			}

			ClickableSpan[] link = buffer.getSpans(selStart, selEnd, ClickableSpan.class);

			if (link.length != 1)
				return false;

			link[0].onClick(widget);
			break;

		case UP:
			int beststart,
			bestend;

			beststart = -1;
			bestend = -1;

			for (int i = 0; i < candidates.length; i++) {
				int end = buffer.getSpanEnd(candidates[i]);

				if (end < selEnd || selStart == selEnd) {
					if (end > bestend) {
						beststart = buffer.getSpanStart(candidates[i]);
						bestend = end;
					}
				}
			}

			if (beststart >= 0) {
				Selection.setSelection(buffer, bestend, beststart);
				return true;
			}

			break;

		case DOWN:
			beststart = Integer.MAX_VALUE;
			bestend = Integer.MAX_VALUE;

			for (int i = 0; i < candidates.length; i++) {
				int start = buffer.getSpanStart(candidates[i]);

				if (start > selStart || selStart == selEnd) {
					if (start < beststart) {
						beststart = start;
						bestend = buffer.getSpanEnd(candidates[i]);
					}
				}
			}

			if (bestend < Integer.MAX_VALUE) {
				Selection.setSelection(buffer, beststart, bestend);
				return true;
			}

			break;
		}

		return false;
	}

	@Override
	public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
		int action = event.getAction();
		if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
			int x = (int) event.getX();
			int y = (int) event.getY();

			x -= widget.getTotalPaddingLeft();
			y -= widget.getTotalPaddingTop();

			x += widget.getScrollX();
			y += widget.getScrollY();

			Layout layout = widget.getLayout();
			int line = layout.getLineForVertical(y);
			int off = layout.getOffsetForHorizontal(line, x);
			ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

			if (link.length != 0) {
				if (action == MotionEvent.ACTION_UP) {
					link[0].onClick(widget);
					return false;
				} else if (action == MotionEvent.ACTION_DOWN) {
					Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]));
					return true;
				}

			}
			ClickStyle[] images = buffer.getSpans(off, off, ClickStyle.class);
			if (images.length != 0) {
				for (ClickStyle style : images) {
					if (style.checkIn(x, y)) {
						if (action == MotionEvent.ACTION_UP) {
							style.onClick(widget);
							break;
						} else if (action == MotionEvent.ACTION_DOWN) {
							return true;
						} else {
							Selection.removeSelection(buffer);
						}
					}
				}

			} else {
				Selection.removeSelection(buffer);
			}
		}
		return false;
	}

	public void initialize(TextView widget, Spannable text) {

		Selection.removeSelection(text);
		text.removeSpan(FROM_BELOW);
	}

	public void onTakeFocus(TextView view, Spannable text, int dir) {
		Selection.removeSelection(text);
		if ((dir & View.FOCUS_BACKWARD) != 0) {
			text.setSpan(FROM_BELOW, 0, 0, Spannable.SPAN_POINT_POINT);
		} else {
			text.removeSpan(FROM_BELOW);
		}
	}

	public static LinkImgMovementMethod getInstance() {
		if (sInstance == null)
			sInstance = new LinkImgMovementMethod();

		return sInstance;
	}

	private static LinkImgMovementMethod sInstance;
	private static Object FROM_BELOW = new NoCopySpan.Concrete();
}
