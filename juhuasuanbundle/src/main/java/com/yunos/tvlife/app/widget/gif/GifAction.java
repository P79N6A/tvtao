package com.yunos.tvlife.app.widget.gif;

import android.graphics.Bitmap;

/**
 * 动画解码成功或者失败的回调接口
 * @author linghu
 */
public interface GifAction {
	
	public static final int RETURN_FRAME_FIRST = 1;/**第一帧解码成功*/
	public static final int RETURN_FRAME_FINISH = 2;/**所有解码成功*/
	public static final int RETURN_DECODE_FINISH = 3;/**缓存解码成功*/
	public static final int RETURN_ERROR = 4;/**解码失败*/
	
	/**
	 * 动画解码结果	
	 * @param iResult 结果
	 */
	public void parseReturn(int iResult, Bitmap bitmap, GifDecoder decoder);
	
	/**
	 * @hide
	 * Gif动画循环播放，是否结束了一轮的显示，每一轮结束，都会有本事件触发
	 */
	public void loopEnd();
	
}
