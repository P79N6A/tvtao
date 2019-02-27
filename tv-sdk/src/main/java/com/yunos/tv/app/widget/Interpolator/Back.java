package com.yunos.tv.app.widget.Interpolator;

/**
 * 超过范围的三次方缓动
 * @author zhangle
 *
 */
public class Back {
	
    public static class EaseIn extends TweenInterpolator {

        @Override
        public float interpolation(float t, float b, float c, float d) {
            float s = 1.70158f;
            return c*(t/=d)*t*((s+1)*t - s) + b;
        }
    }

    public static class easeOut extends TweenInterpolator {

        @Override
        public float interpolation(float t, float b, float c, float d) {
            float s = 1.70158f;
            return c*((t=t/d-1)*t*((s+1)*t + s) + 1) + b;
        }
    }

    public static class easeInOut extends TweenInterpolator {

        @Override
        public float interpolation(float t, float b, float c, float d) {
            float s = 1.70158f;
            if ((t/=d/2) < 1) return c/2*(t*t*(((s*=(1.525f))+1)*t - s)) + b;
            return c/2*((t-=2)*t*(((s*=(1.525f))+1)*t + s) + 2) + b;
        }
    }
    
    /*	
	public static float  easeIn(float t,float b , float c, float d, float s) {
		return c*(t/=d)*t*((s+1)*t - s) + b;
	}
	
	public static float  easeOut(float t,float b , float c, float d, float s) {
		return c*((t=t/d-1)*t*((s+1)*t + s) + 1) + b;
	}
	
	public static float  easeInOut(float t,float b , float c, float d, float s) {	
		if ((t/=d/2) < 1) return c/2*(t*t*(((s*=(1.525f))+1)*t - s)) + b;
		return c/2*((t-=2)*t*(((s*=(1.525f))+1)*t + s) + 2) + b;
	}
	*/
}
