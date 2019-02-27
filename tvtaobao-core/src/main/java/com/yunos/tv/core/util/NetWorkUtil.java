package com.yunos.tv.core.util;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.yunos.tv.core.CoreApplication;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class NetWorkUtil {

    /**
     * 判断网络类型
     *
     * @return -1表示沒有網絡,9表示有线网络,3表示WIFI
     */
    public static int getNetWorkType() {
        if (!isNetWorkAvailable()) {
            return -1;
        }
        ConnectivityManager conn = (ConnectivityManager) CoreApplication.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (networkInfo.isConnected()) {
            return ConnectivityManager.TYPE_ETHERNET;
        }

        if (isWifi()) {
            return ConnectivityManager.TYPE_WIFI;
        }

        return -1;
    }

    /**
     * 网络是否可用
     *
     * @return
     */
    public static String getIpAddress() {
        String ip = null;
        try {
            @SuppressLint("WifiManagerLeak") WifiManager wifiManager = (WifiManager) CoreApplication.getApplication().getSystemService(Context.WIFI_SERVICE);
            wifiManager.getConnectionInfo().getIpAddress();
            if (wifiManager != null) {
                WifiInfo info = wifiManager.getConnectionInfo();
                if (info != null) {
                    ip = intIP2StringIP(info.getIpAddress());
                    return ip;
                }
            } else {
                return getEthIpAddress();
            }
        } catch (Exception e) {
        }

        return null;
    }

    private static String getEthIpAddress() throws SocketException {
        String ipaddress = "";
        Enumeration<NetworkInterface> netInterfaces = null;
        try {
            netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                NetworkInterface intf = netInterfaces.nextElement();
                if (intf.getName().toLowerCase().equals("eth0") ||
                        intf.getName().toLowerCase().equals("wlan0")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            ipaddress = inetAddress.getHostAddress().toString();
                            if (!ipaddress.contains("::")) {// ipV6的地址
                                ipaddress = ipaddress;
                            }
                        }
                    }
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ipaddress;
    }

    private static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /**
     * 网络是否可用
     *
     * @return
     */
    public static boolean isNetWorkAvailable() {
        boolean isAvailable = false;
        try {
            ConnectivityManager cm = getConnectivityManager();
            if (cm != null) {
                NetworkInfo info = cm.getActiveNetworkInfo();
                if (info != null) {
                    isAvailable = info.isAvailable();
                }
            }
        } catch (Exception e) {
        }
        return isAvailable;
    }

    /**
     * 判断是否是wifi网络
     *
     * @return
     */
    public static boolean isWifi() {
        ConnectivityManager cm = getConnectivityManager();
        if (cm != null) {
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni != null && ni.getTypeName().equals("WIFI")) {
                return true;
            }
        }
        return false;
    }

    private static ConnectivityManager getConnectivityManager() {
        ConnectivityManager cm = null;
        try {
            cm = (ConnectivityManager) CoreApplication.getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);
        } catch (Exception e) {
        }
        return cm;
    }

}
