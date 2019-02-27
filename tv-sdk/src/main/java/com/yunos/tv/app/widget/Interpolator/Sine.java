package com.yunos.tv.app.widget.Interpolator;

/**
 * 正弦曲线的缓动
 * @author zhangle
 *
 */
public class Sine {

    public static class EaseIn extends TweenInterpolator {

        @Override
        public float interpolation(float t, float b, float c, float d) {
            return -c * (float) Math.cos(t / d * (Math.PI / 2)) + c + b;
        }
    }

    public static class easeOut extends TweenInterpolator {

        @Override
        public float interpolation(float t, float b, float c, float d) {
            return c * (float) Math.sin(t / d * (Math.PI / 2)) + b;
        }
    }

    public static class easeInOut extends TweenInterpolator {

        @Override
        public float interpolation(float t, float b, float c, float d) {
            return -c / 2 * ((float) Math.cos(Math.PI * t / d) - 1) + b;
        }
    }
}
