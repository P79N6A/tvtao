package com.yunos.tv.app.widget.Interpolator;

/**
 * 指数衰减的反弹缓动
 * @author zhangle
 *
 */
public class Bounce {

    public static class EaseIn extends TweenInterpolator {

        @Override
        public float interpolation(float t, float b, float c, float d) {
            return c - new easeOut().interpolation(d - t, 0, c, d) + b;
        }
    }

    public static class easeOut extends TweenInterpolator {

        @Override
        public float interpolation(float t, float b, float c, float d) {
            if ((t /= d) < (1 / 2.75f)) {
                return c * (7.5625f * t * t) + b;
            } else if (t < (2 / 2.75f)) {
                return c * (7.5625f * (t -= (1.5f / 2.75f)) * t + .75f) + b;
            } else if (t < (2.5 / 2.75)) {
                return c * (7.5625f * (t -= (2.25f / 2.75f)) * t + .9375f) + b;
            } else {
                return c * (7.5625f * (t -= (2.625f / 2.75f)) * t + .984375f) + b;
            }
        }
    }

    public static class easeInOut extends TweenInterpolator {

        @Override
        public float interpolation(float t, float b, float c, float d) {
            if (t < d / 2)
                return new EaseIn().interpolation(t * 2, 0, c, d) * .5f + b;
            else
                return new EaseIn().interpolation(t * 2 - d, 0, c, d) * .5f + c * .5f + b;
        }
    }
}
