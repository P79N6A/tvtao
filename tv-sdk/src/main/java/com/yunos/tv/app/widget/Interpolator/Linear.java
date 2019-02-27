package com.yunos.tv.app.widget.Interpolator;

/**
 * 无缓动效果
 *
 */
public class Linear {
    public static class EaseNone extends TweenInterpolator {

        @Override
        public float interpolation(float t, float b, float c, float d) {
            return c * t / d + b;
        }
    }
}
