/**
 * $
 * PROJECT NAME: business-view
 * PACKAGE NAME: com.yunos.tvtaobao.biz.graphics
 * FILE NAME: TvCanvas.java
 * CREATED TIME: 2015-1-27
 * COPYRIGHT: Copyright(c) 2013 ~ 2015 All Rights Reserved.
 */
/**
 * 
 */
package com.yunos.tvtaobao.biz.graphics;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.yunos.tv.core.common.AppDebug;

/**
 * 补充画圆角等方法
 * @version
 * @author hanqi
 * @data 2015-1-27 上午10:38:39
 */
public class TvCanvas extends Canvas {

    public TvCanvas() {
        super();
    }

    public TvCanvas(Bitmap output) {
        super(output);
    }

    /**
     * 画左上、右上、右下、左下4个角 的 任意位置任意数量的圆角
     * @param rectf 矩形框
     * @param radius 圆角半径
     * @param paint 画笔
     * @param corners 数组，矩形4个角从左上角顺时针方向依次对应下标0,1,2,3，值true表示对应位置的角画圆角，false表示不画圆角
     */
    public void drawCornerRect(RectF rectf, float radius, Paint paint, Boolean... corners) {
        AppDebug.i("TvCanvas", "TvCanvas.drawCornerRect rectf=" + rectf + ", radius=" + radius + ", corners=" + corners);
        if (null == corners || corners.length <= 0) {
            drawRect(rectf, paint);
            return;
        }
        boolean alltrue = true;//如果全部都是true
        boolean allfalse = true;//如果全部都是false
        float[] radii = new float[8];// 下标0-8，每顺序2个参数表示左上，右上，右下，左下的圆角x，y轴半径
        int size = Math.min(corners.length, 4);
        if (size < 4) {
            alltrue = false;
            allfalse = false;
        }
        for (int i = 0; i < size; i++) {
            if (corners[i]) {
                allfalse = false;
                radii[i * 2] = radius; //1-4个圆角的x轴半径
                radii[i * 2 + 1] = radius;//1-4个圆角的y轴半径
            } else {
                alltrue = false;
            }
            AppDebug.i("TvCanvas", "TvCanvas.drawCornerRect corners[" + i + "]=" + corners[i]);
        }
        if (allfalse || alltrue) {
            if (allfalse) {//如果全部都是false 则画矩形
                drawRect(rectf, paint);
            } else { //如果全部都是true 则画圆角矩形
                drawRoundRect(rectf, radius, radius, paint);
            }
            return;
        }

        Path path = new Path();
        path.addRoundRect(rectf, radii, Path.Direction.CW);
        drawPath(path, paint);
    }
}
