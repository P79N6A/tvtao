package com.yunos.tv.lib;


public class CpuPowerUtils {
	
	private final static String CPU_POWER_KEY = "persist.sys.cpupower";
	
	public final static int CPU_POWER_LEVEL_STRONG = 3;
	public final static int CPU_POWER_LEVEL_NORMAL = 2;
	public final static int CPU_POWER_LEVEL_LOWER = 1;
	
	public static boolean isLowerCpuPower() {
    	int cpuLevel = getCpuPowerLevel();
    	return CPU_POWER_LEVEL_LOWER == cpuLevel ? true : false;
    }
    
    public static int getCpuPowerLevel() {
    	int level = CPU_POWER_LEVEL_NORMAL;
    	String levelStr = SystemProUtils.getSystemProperties(CPU_POWER_KEY);
    	if("1".equals(levelStr)){
    		level = CPU_POWER_LEVEL_LOWER;
    	}
    	return level;
    }
    
}
