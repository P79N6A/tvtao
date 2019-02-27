package com.yunos.tv.app.widget.Interpolator;

/**
 * 二次方的缓动
 * @author zhangle
 *
 */
public class Quad {

    public static class EaseIn extends TweenInterpolator {

        @Override
        public float interpolation(float t, float b, float c, float d) {
            return c * (t /= d) * t + b;
        }
    }

    public static class easeOut extends TweenInterpolator {

        @Override
        public float interpolation(float t, float b, float c, float d) {
            return -c * (t /= d) * (t - 2) + b;
        }
    }

    public static class easeInOut extends TweenInterpolator {

        @Override
        public float interpolation(float t, float b, float c, float d) {
            if ((t /= d / 2) < 1)
                return c / 2 * t * t + b;
            return -c / 2 * ((--t) * (t - 2) - 1) + b;
        }
    }
}
