package com.yunos.tv.app.widget.Interpolator;

/**
 * 圆形曲线的缓动
 * @author zhangle
 *
 */
public class Circ {

    public static class EaseIn extends TweenInterpolator {

        @Override
        public float interpolation(float t, float b, float c, float d) {
            return -c * ((float) Math.sqrt(1 - (t /= d) * t) - 1) + b;
        }
    }

    public static class easeOut extends TweenInterpolator {

        @Override
        public float interpolation(float t, float b, float c, float d) {
            return c * (float) Math.sqrt(1 - (t = t / d - 1) * t) + b;
        }
    }

    public static class easeInOut extends TweenInterpolator {

        @Override
        public float interpolation(float t, float b, float c, float d) {
            if ((t /= d / 2) < 1)
                return -c / 2 * ((float) Math.sqrt(1 - t * t) - 1) + b;
            return c / 2 * ((float) Math.sqrt(1 - (t -= 2) * t) + 1) + b;
        }
    }
}
