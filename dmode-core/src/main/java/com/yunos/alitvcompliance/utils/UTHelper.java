package com.yunos.alitvcompliance.utils;

import java.util.Map;

/**
 * empty implementation in dmode
 */
public class UTHelper {

    public interface IUTCustomEventSender {
        void sendCustomEvent(String var1, Map<String, String> var2);
    }

    public interface IUTInitializer {
        void initUT();
    }
}