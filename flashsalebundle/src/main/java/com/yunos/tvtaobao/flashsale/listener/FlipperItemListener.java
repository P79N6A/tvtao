/**
 *  Copyright (C) 2015 The ALI OS Project
 *
 *  Version     Date            Author
 *  
 *             2015-4-19       lizhi.ywp
 *
 */
package com.yunos.tvtaobao.flashsale.listener;

import android.view.View;

public interface FlipperItemListener {

	public final static byte TYPE_MYCONCERN = 0;
    public final static byte TYPE_PERIOD_BUY = 1;
    public final static byte TYPE_FINALLY_BUY = 2;
    public final static byte TYPE_MAX = 3;
	
	/**
	 * This method will be invoked when the page becomes unselected. if it has
	 * Animation, Animation is necessarily complete, if not, directly callback.
	 * 
	 * @param unselectView
	 *            instance of the unselected view.
	 * @param unselectPos
	 *            Position index of the unselected view.
	 * 
	 */
	public void onPageUnselected(View unselectView, int unselectPos);

	/**
	 * This method will be invoked when the page will be gone
	 * 
	 * 
	 * @param selectView
	 *            instance of the new selected view.
	 * @param selectPos
	 *            Position index of the new unselected view.
	 * @param unselectView
	 *            instance of the previous unselected view.
	 * @param unselectPos
	 *            Position index of the previous unselected view.
	 * 
	 */
	public void onPageWillUnselected(View selectView, int selectPos,
                                     View unselectView, int unselectPos);

	/**
	 * This method will be invoked when a new page becomes selected. if it has
	 * Animation, Animation is necessarily complete, if not, directly callback.
	 * 
	 * @param selectView
	 *            instance of the new selected view.
	 * @param selectPos
	 *            Position index of the new selected view.
	 * 
	 */
	public void onPageSelected(View selectView, int selectPos);

	/**
	 * This method will be invoked when a new page will be selected
	 * 
	 * 
	 * @param selectView
	 *            instance of the new selected view.
	 * @param selectPos
	 *            Position index of the new unselected view.
	 * @param unselectView
	 *            instance of the previous unselected view.
	 * @param unselectPos
	 *            Position index of the previous unselected view.
	 * 
	 */
	public void onPageWillSelected(View selectView, int selectPos,
                                   View unselectView, int unselectPos);

	/**
	 * This method will be invoked when the parent view remove or add a child
	 * view
	 * 
	 * 
	 * @param selectView
	 *            instance of the selected view.
	 * @param selectPos
	 *            Position index of the selected view.
	 */
	public void OnViewChange(View selectView, int selectPos);

	/**
	 * This method will be invoked when KeyEvent.KEYCODE_DPAD_RIGHT or
	 * KeyEvent.KEYCODE_DPAD_LEFT
	 * 
	 * 
	 * @param keyCode
	 *            key code when the key event is KeyEvent.ACTION_DOWN.
	 * @return true: switch page false: the container processing
	 */
	public boolean OnSwitch(int keyCode);

	/**
	 * This method will get current page type
	 * 
	 * @return
	 */
	public byte getPageType();

	/**
	 * This method will resume
	 * 
	 * @return
	 */
	public void onResume();
	

	/**
	 * This method will destroy
	 * 
	 * @return
	 */
	public void onDestroy();

}
