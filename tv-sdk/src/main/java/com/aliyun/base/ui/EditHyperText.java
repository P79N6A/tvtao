package com.aliyun.base.ui;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

import com.aliyun.base.ui.text.ImageSpan;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditHyperText extends EditText {

	

	/**
	 * 图片支持
	 * 
	 */
	public interface ImageSupport {

		public String getRegex();

		public void getImageByString(ImageSpan span, String text);

	}

	private ImageSupport mImageSupport;

	public EditHyperText(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.editTextStyle);
	}

	public EditHyperText(Context context) {
		this(context, null);
	}

	public EditHyperText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setEllipsize(TextUtils.TruncateAt ellipsis) {
		if (ellipsis == TextUtils.TruncateAt.MARQUEE) {
			throw new IllegalArgumentException("EditText cannot use the ellipsize mode " + "TextUtils.TruncateAt.MARQUEE");
		}
		super.setEllipsize(ellipsis);
	}

	public void setImageSupport(ImageSupport imageSupport) {
		mImageSupport = imageSupport;
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int before, int after) {
		// Pattern p = Pattern.compile(target);
		// Matcher m = p.matcher(temp);
		// while (m.find()) {
		// span = new ForegroundColorSpan(Color.RED);// 需要重复！
		// spannable.setSpan(span, m.start(), m.end(),
		// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		// }
		// Log.i("" , "----onTextChanged--------");

		super.onTextChanged(text, start, before, after);

		if (mImageSupport != null) {
			int s, e;
			if (before > 0) {
				s = start;
				e = start + before;
			} else {
				s = start;
				e = start + after;
			}
			replaceWithpic(text, start, before, after);
		}
	}

	private void replaceWithpic(CharSequence text, int start, int before, int after) {// \[:*\]
		SpannableStringBuilder spannable = (SpannableStringBuilder) text;
		Log.i("", spannable.getSpans(0, text.length(), ImageSpan.class).length + "----onTextChanged-------- " + text + " start:" + start + " before:" + before + " after:" + after);
		if (before > 0) {

			Object[] spans = spannable.getSpans(start, start + before, ImageSpan.class);
			for (int i = 0; i < spans.length; i++) {
				spannable.removeSpan(spans[i]);
			}
		}
		Pattern pattern = Pattern.compile(mImageSupport.getRegex());
		Matcher m = pattern.matcher(text.subSequence(start, start + after));

		while (m.find()) {
			spannable.setSpan(new ImageSpan(m.group(), ImageSpan.ALIGN_CENTER) {

				@Override
				public void getDrawable(String path) {
					mImageSupport.getImageByString(this, mSource);
				}
			}, m.start() + start, m.end() + start, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
}
