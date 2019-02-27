package com.yunos.tv.app.widget.focus.listener;


/**
 * 在影视详情页中，整个大的框架是横向滚动的FocusSrollRelativeLayout，焦点一直是在它上面，里面的child没有发生focuschange事件，所以要做到焦点无动画，需要在
 * child上实现此接口。
 * @author leiming32
 *
 */
public interface AnimateWhenGainFocusListener {
	public boolean fromLeft();//焦点框从左侧过来不需要动画。以下类同。
	public boolean fromTop();
	public boolean fromRight();
	public boolean fromBottom();
}
