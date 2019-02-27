package com.yunos.tv.app.widget.Interpolator;
 
/**
 * 指数曲线的缓动
 * @author zhangle
 *
 */
public class Expo {
    public static class EaseIn extends TweenInterpolator {

        @Override
        public float interpolation(float t, float b, float c, float d) {
            return (t==0) ? b : c * (float)Math.pow(2, 10 * (t/d - 1)) + b;
        }
    }

    public static class easeOut extends TweenInterpolator {

        @Override
        public float interpolation(float t, float b, float c, float d) {
            return (t==d) ? b+c : c * (-(float)Math.pow(2, -10 * t/d) + 1) + b;
        }
    }

    public static class easeInOut extends TweenInterpolator {

        @Override
        public float interpolation(float t, float b, float c, float d) {
            if (t==0) return b;
            if (t==d) return b+c;
            if ((t/=d/2) < 1) return c/2 * (float)Math.pow(2, 10 * (t - 1)) + b;
            return c/2 * (-(float)Math.pow(2, -10 * --t) + 2) + b;
        }
    }
}
