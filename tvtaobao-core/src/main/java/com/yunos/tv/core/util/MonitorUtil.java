package com.yunos.tv.core.util;

import android.content.Context;
import android.os.Build;

import com.alibaba.appmonitor.delegate.AppMonitorDelegate;
import com.alibaba.mtl.appmonitor.AppMonitor;
import com.alibaba.mtl.appmonitor.model.DimensionSet;
import com.alibaba.mtl.appmonitor.model.DimensionValueSet;
import com.alibaba.mtl.appmonitor.model.Measure;
import com.alibaba.mtl.appmonitor.model.MeasureSet;
import com.alibaba.mtl.appmonitor.model.MeasureValueSet;
import com.yunos.tv.core.common.AppDebug;
import com.yunos.tv.core.common.DeviceJudge;
import com.yunos.tv.core.config.Config;

public class MonitorUtil {
    private static boolean init = false;

    public static void init() {
        if (init)
            return;
        init = true;
        AppMonitorDelegate.IS_DEBUG = Config.isDebug(); // 放开注释能很快的看到一些数据上报
        registerAppMonitors();
    }


    private static void registerAppMonitors() {
        //blitz loading time
        DimensionSet dimensionSet = DimensionSet.create();
        dimensionSet.addDimension("channelName");//渠道
        dimensionSet.addDimension("version");//版本名
        dimensionSet.addDimension("debug");//是否debug版本
        dimensionSet.addDimension("domain");//域名
        dimensionSet.addDimension("url");//url
        dimensionSet.addDimension("success");
        MeasureSet measureSet = MeasureSet.create();
        measureSet.addMeasure(new Measure("loadTime", 0d));
        AppMonitor.register("tvtaobao", "pageload", measureSet, dimensionSet);

        //activity display time
        DimensionSet activityDimensionSet = DimensionSet.create();
        activityDimensionSet.addDimension("channelName");//渠道
        activityDimensionSet.addDimension("version");//版本名
        activityDimensionSet.addDimension("debug");//是否debug版本
        activityDimensionSet.addDimension("activityName");//activity名
        activityDimensionSet.addDimension("model");
        activityDimensionSet.addDimension("brand");
        activityDimensionSet.addDimension("product");
        activityDimensionSet.addDimension("api");
        activityDimensionSet.addDimension("memory");
        MeasureSet activityMeasureSet = MeasureSet.create();
        activityMeasureSet.addMeasure(new Measure("loadTime", 0d));
        AppMonitor.register("tvtaobao", "activityLoad", measureSet, dimensionSet);

        //app display time
        DimensionSet appLoadTimeDimensionSet = DimensionSet.create();
        activityDimensionSet.addDimension("channelName");//渠道
        activityDimensionSet.addDimension("version");//版本名
        activityDimensionSet.addDimension("model"); // 型号
        activityDimensionSet.addDimension("product"); // 产品
        activityDimensionSet.addDimension("api");
        activityDimensionSet.addDimension("memorySize");
        activityDimensionSet.addDimension("cpuCoresCount");
        activityDimensionSet.addDimension("cpuInfoMaxFreq");
        activityDimensionSet.addDimension("loadTime");
        MeasureSet appLoadTimeSet = MeasureSet.create();
        activityMeasureSet.addMeasure(new Measure("appLoadTime", 0d));
        AppMonitor.register("tvtaobao", "appLoad", appLoadTimeSet, appLoadTimeDimensionSet);
    }

    public static DimensionValueSet createDimensionValueSet(Context context) {
        DimensionValueSet dimensionValueSet = DimensionValueSet.create();
        dimensionValueSet.setValue("channelName", Config.getChannelName());
        dimensionValueSet.setValue("version", Config.getVersionName(context));
        dimensionValueSet.setValue("debug", Config.isDebug() ? "true" : "false");
        dimensionValueSet.setValue("success", "true");
        dimensionValueSet.setValue("model", Build.MODEL);
        dimensionValueSet.setValue("product", Build.PRODUCT);
        dimensionValueSet.setValue("api", "" + Build.VERSION.SDK_INT);
        dimensionValueSet.setValue("brand", Build.BRAND);
        dimensionValueSet.setValue("memory", DeviceJudge.getMemoryType().name());
        return dimensionValueSet;
    }

    public static void reportAppLoadTime(Context context, double loadTime) {
        try {
            DimensionValueSet dimensionValueSet = DimensionValueSet.create();
            dimensionValueSet.setValue("channelName", Config.getChannelName());
            dimensionValueSet.setValue("version", Config.getVersionName(context));
            dimensionValueSet.setValue("model", Build.MODEL);
            dimensionValueSet.setValue("product", Build.PRODUCT);
            dimensionValueSet.setValue("api", "" + Build.VERSION.SDK_INT);
            dimensionValueSet.setValue("memorySize", ""+DeviceJudge.getTotalMemorySizeInMB());
            dimensionValueSet.setValue("cpuCoresCount", ""+DeviceJudge.getCoreCountCached());
            dimensionValueSet.setValue("cpuInfoMaxFreq", ""+DeviceJudge.getMaxCpuFreq());
            dimensionValueSet.setValue("loadTime", ""+loadTime);

            MeasureValueSet measureValueSet = MeasureValueSet.create();
            measureValueSet.setValue("appLoadTime", loadTime);

            AppMonitor.Stat.commit("tvtaobao", "appLoad", dimensionValueSet, measureValueSet);

            AppDebug.i(MonitorUtil.class.getSimpleName(), " reportAppLoadTime " + loadTime);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
