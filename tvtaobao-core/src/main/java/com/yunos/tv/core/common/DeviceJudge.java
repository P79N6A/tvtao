package com.yunos.tv.core.common;


import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.yunos.tv.core.BuildConfig;
import com.yunos.tv.core.config.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by jiejing on 14-11-12.
 */
public class DeviceJudge {

    static int cpuCoreCache_ = -1;

    private static final int MB_UNIT = 1024 * 1024;
    private static final String TAG = "DevicePerformance";
    private static final float DEFAULT_MAX_CPU_REEQ = 1.5f; // 默认的CPU最高频率
    private static int mTotalMemorySize = 0;
    private static ScreenResolutionType mScreenResolutionType = ScreenResolutionType.Resulution720P;
    private static boolean mLowDevice;
    private static int totalMem = 0;

    /**
     * 反映设备中heap相关设置中 App可用内存的大小。
     * 这个类反应一个综合指标
     */
    public enum MemoryType {
        LowMemoryDevice, MediumMemoryDevice, HighMemoryDevice,
    }

    /**
     * 反映CPU的能力，Low 表示单核， Medium表示双核， High 表示4核以上
     */
    public enum CpuType {
        LowCpuDevice, MediumCpuDevice, HighCpuDevice,
    }

    /**
     * 屏幕分辨率
     */
    public enum ScreenResolutionType {
        Resulution720P, Resulution1080P, ResulutionOTHERS,
    }

    public final static String getDevicePerformanceString() {
        StringBuilder builder = new StringBuilder();
        return builder.append(TAG + ": " + getCoreCountCached() + " Cores; ").append(getCpuType() + ";")
                .append("Max Heap Size(MB) " + readMemoryInfoCfg() + ";").append(getMemoryType())
                .append("screen resolution:" + mScreenResolutionType).toString();
    }

    /**
     * 使用这些值来决定你要做多大的cache等。
     * @return 返回 LowMemoryDevice, MediumMemoryDevice, HighMemoryDevice, 返回设备内存大小的属性， 没有绝对值， 根据返回的类型来做判断。
     */
    public final static MemoryType getMemoryType() {
        int tmp = getTotalMemorySizeInMB();
        if (tmp <= 768) {
            return MemoryType.LowMemoryDevice;
        } else if (tmp <= 1024) {
            return MemoryType.MediumMemoryDevice;
        } else {
            return MemoryType.HighMemoryDevice;
        }

    }

    public static int getTotalMemorySizeInMB() {
        if (BuildConfig.DeviceFake.equals("real")){

        } else if (BuildConfig.DeviceFake.equals("fake_low")){
            return 448;
        } else if (BuildConfig.DeviceFake.equals("fake_medium")){
            return 888;
        } else if (BuildConfig.DeviceFake.equals("fake_high")){
            return 2048;
        }
        return mTotalMemorySize;
    }

    private static int readMemoryInfoCfg() {
        if (totalMem > 0) {
            return totalMem;
        }
        // /proc/meminfo读出的内核信息进行解释
        String path = "/proc/meminfo";
        String content = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path), 8);
            String line;
            if ((line = br.readLine()) != null) {
                content = line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // beginIndex
        int begin = content.indexOf("MemTotal:");
        // endIndex
        int end = content.indexOf('k');
        // 截取字符串信息

        content = content.substring(begin + 9, end).trim();
        try {
            long toatal = Long.parseLong(content);
            totalMem = (int) (toatal / 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalMem;

        //        Runtime runtime = Runtime.getRuntime();
        //        return (int) (runtime.maxMemory() / MB_UNIT);
    }

    /**
     * 得到是CPU的能力， 更具CPU的能力来决定是否使用一些CPU来做的动画， 比如android的zoom类动画等。
     * @return 返回 LowCpuDevice, MediumCpuDevice, HighCpuDevice
     */
    public final static CpuType getCpuType() {
        // Only scan the CPU  file once, and store.
        int cores = getCoreCountCached();
        if (mScreenResolutionType == ScreenResolutionType.Resulution1080P) {
            if (cores <= 2)
                return CpuType.LowCpuDevice;
            else
                return CpuType.HighCpuDevice;
        } else if (mScreenResolutionType == ScreenResolutionType.Resulution720P) {
            if (cores <= 1)
                return CpuType.LowCpuDevice;
            else
                return CpuType.HighCpuDevice;
        }
        return CpuType.LowCpuDevice;
    }

    /**
     * 取得CPU的最高频率
     * @return
     */
    public static final float getMaxCpuFreq() {
        int maxCpu = getFirstMaxCpuFreq();

        if (Config.isDebug()) {
            AppDebug.i(TAG, "getMaxCpuFreq maxCpu=" + maxCpu);
        }
        if (maxCpu <= 0) {
            return DEFAULT_MAX_CPU_REEQ;
        }
        return (float) maxCpu / MB_UNIT;
    }

    /**
     * 传统的判断是否是低配设备
     */
    public static final void initNormalJudgeDevice() {
        DeviceJudge.MemoryType memoryType = getMemoryType();
        float maxCpuFreq = getMaxCpuFreq();
        int cores = getCoreCountCached();
        float cpuValue = maxCpuFreq * cores;
        // 双核1.5为判断CPU高低配的基准
        float cpuBaseValue = DEFAULT_MAX_CPU_REEQ * 2;
        if (mScreenResolutionType == ScreenResolutionType.Resulution1080P) {
            if (cpuValue >= cpuBaseValue && memoryType.compareTo(DeviceJudge.MemoryType.LowMemoryDevice) != 0) {
                mLowDevice = false;
            } else {
                mLowDevice = true;
            }
        } else if (mScreenResolutionType == ScreenResolutionType.Resulution720P) {
            if (cpuValue >= cpuBaseValue && memoryType.compareTo(DeviceJudge.MemoryType.LowMemoryDevice) != 0) {
                mLowDevice = false;
            } else {
                mLowDevice = true;
            }
        }
        if (Config.isDebug()) {
            AppDebug.i(TAG, "initNormalJudgeDevice memoryType=" + memoryType + " maxCpuFreq=" + maxCpuFreq + " cores="
                    + cores + " mScreenResolutionType=" + mScreenResolutionType + " mLowDevice=" + mLowDevice);
        }
    }

    /**
     * 是否是低配置
     * @return
     */
    public static final boolean isLowDevice() {
        return mLowDevice;
    }

    public static int getCoreCountCached() {
        if (cpuCoreCache_ < 0)
            cpuCoreCache_ = getCoresCount();

        return cpuCoreCache_;
    }

    private static int getCoresCount() {
        class CpuFilter implements FileFilter {

            @Override
            public boolean accept(final File pathname) {
                if (Pattern.matches("cpu[0-9]+", pathname.getName()))
                    return true;
                return false;
            }
        }
        try {
            final File dir = new File("/sys/devices/system/cpu/");
            final File[] files = dir.listFiles(new CpuFilter());
            return files.length;
        } catch (final Exception e) {
            return Math.max(1, Runtime.getRuntime().availableProcessors());
        }
    }

    public static void initSystemInfo(Context context) {
        DisplayMetrics metric = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels; // 宽度（PX）
        int height = metric.heightPixels; // 高度（PX）
        if (width == 1920 && height == 1080) {
            mScreenResolutionType = ScreenResolutionType.Resulution1080P;
        } else if (width == 1280 && height == 720) {
            mScreenResolutionType = ScreenResolutionType.Resulution720P;
        } else {
            mScreenResolutionType = ScreenResolutionType.ResulutionOTHERS;
        }
        mTotalMemorySize = readMemoryInfoCfg();
        initNormalJudgeDevice();
    }

    /**
     * 取得第一个CPU的最大主频，有的机型可能取不到这个参数，返回为0
     * @return
     */
    public static int getFirstMaxCpuFreq() {
        String kCpuInfoMaxFreqFilePath = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
        int result = 0;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(kCpuInfoMaxFreqFilePath);
            br = new BufferedReader(fr);
            String text = br.readLine();
            result = Integer.parseInt(text.trim());
        } catch (FileNotFoundException e) {
            if (Config.isDebug()) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            if (Config.isDebug()) {
                e.printStackTrace();
            }
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    if (Config.isDebug()) {
                        e.printStackTrace();
                    }
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    if (Config.isDebug()) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }
}
