package com.yunos.tv.net.network;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/* I haven't found a way to get some of the values without resorting
 * to system tools.  Please let me know if you have found a different
 * way because I'd like to remove all use of mySystem below.
 * 
 * mySystem uses reflection to get access to the system binaries.
 */

/**
 * Helper class to provide network information to NetworkMonitor
 */
public class NetInfoAdapter {
	private static final String TAG = "NetInfoAdapter";
	private static Map<String, String> netMap = new HashMap<String, String>();
	private static Map<Integer, String> phoneType = new HashMap<Integer, String>();
	private static Map<Integer, String> networkType = new HashMap<Integer, String>();
	private boolean netExists = false;
	private boolean wifiConnected = false;
	private boolean mobileConnected = false;
	private boolean ethConnected = false;
	private boolean isRoaming = false;
	private String n_a = "n/a";
	private String strUnknown = "Unknown";

	/**
	 * Constructor. Generates a HashMap used by the class to return information
	 * about the network.
	 * 
	 * @param context
	 *            context we're working under
	 */
	public NetInfoAdapter(Context context) {
		// Initialise some mappings
		phoneType.put(0, "None");
		phoneType.put(1, "GSM");
		phoneType.put(2, "CDMA");

		networkType.put(0, strUnknown);
		networkType.put(1, "GPRS");
		networkType.put(2, "EDGE");
		networkType.put(3, "UMTS");
		networkType.put(4, "CDMA");
		networkType.put(5, "EVDO_0");
		networkType.put(6, "EVDO_A");
		networkType.put(7, "1xRTT");
		networkType.put(8, "HSDPA");
		networkType.put(9, "HSUPA");
		networkType.put(10, "HSPA");
		networkType.put(11, "IDEN");

		// Initialise the network information mapping
		netMap.put("state", "");
		netMap.put("interface", "");
		netMap.put("type", "");
		netMap.put("netID", "");
		// netMap.put("speed", "");
		netMap.put("roaming", "");
		netMap.put("ip", "");
		// netMap.put("gateway", "");
		// netMap.put("dns", "");
		netMap.put("bgdata", "");
		netMap.put("data_activity", n_a);
		netMap.put("cell_location", n_a);
		netMap.put("cell_type", n_a);
		netMap.put("Phone_type", n_a);

		netExists = false;
		wifiConnected = false;
		mobileConnected = false;
		isRoaming = false; 
		
		// Find out if we're connected to a network
        Context contextTemp = context;
        if(context instanceof Activity){
            Context applicationContext = context.getApplicationContext();
            if(applicationContext != null){
                contextTemp = applicationContext;
            }
        }
		ConnectivityManager cm = null;
		try {
			Log.v(TAG, "GET ConnectivityManager");
			cm = (ConnectivityManager) contextTemp.getSystemService(Context.CONNECTIVITY_SERVICE);
		} catch (Exception e) {
			Log.v(TAG, "NetInfoAdapter.NetInfoAdapter6");
			Log.w(TAG, "Cannot get connectivity service! except: " + e.getMessage());
			cm = null;
		}

		NetworkInfo ni = null;
		if (null != cm) {
			try {
				ni = (NetworkInfo) cm.getActiveNetworkInfo();
			} catch (Exception e) {
				Log.v(TAG, "NetInfoAdapter.NetInfoAdapter7");
				Log.w(TAG, "Cannot get active network info! except: " + e.getMessage());
				ni = null;
			}
		}

		Log.v(TAG, "check connection states");
		boolean bIsConnected = false;
		try {
			bIsConnected = (ni != null && ni.isConnected());
		} catch (Exception e) {
			Log.v(TAG, "NetInfoAdapter.NetInfoAdapter8");
			bIsConnected = false;
		}

		if (bIsConnected) {
			Log.v(TAG, "Network is connected");
			netExists = true;
			netMap.put("state", "connected");

			WifiManager wifi = null;
			NetworkInterface intf = getInternetInterface();
			try {
				wifi = (WifiManager) contextTemp.getSystemService(Context.WIFI_SERVICE);
			} catch (Exception e) {
				Log.v(TAG, "NetInfoAdapter.NetInfoAdapter9");
				Log.w(TAG, "Cannot get Wifi service! except: " + e.getMessage());
				wifi = null;
			}
			if (null != intf) {
				try {
					netMap.put("interface", intf.getName());
					netMap.put("ip", getIPAddress(intf));
				} catch (Exception e) {
					Log.v(TAG, "NetInfoAdapter.NetInfoAdapter10");
					Log.w(TAG, e.toString());
				}
			}

			String type = "";
			try {
				type = (String) ni.getTypeName();
				Log.v(TAG, "Connection type is " + type);
			} catch (Exception e) {
				Log.v(TAG, "NetInfoAdapter.NetInfoAdapter11");
				type = "";
				Log.w(TAG, e.toString());
			}

			if ((null != wifi) && (wifi.isWifiEnabled())) {
				Log.v(TAG, "Wifi connected");
				netMap.put("type", "net_type_wifi");
				WifiInfo wi = null;
				wifiConnected = true;
				try {
					wi = wifi.getConnectionInfo();
				} catch (Exception e) {
					Log.v(TAG, "NetInfoAdapter.NetInfoAdapter12");
					wi = null;
				}
				if (null != wi) {
					try {
						netMap.put("netID", wi.getSSID());
						netMap.put("speed", Integer.toString(wi.getLinkSpeed()) + "Mbps");
					} catch (Exception e) {
						Log.v(TAG, "NetInfoAdapter.NetInfoAdapter13");
					}
				}
				// netMap.put("gateway", Tools.mySystem("/system/bin/getprop",
				// "dhcp." + intf.getName() + ".gateway", "").trim());
			} else {
				if (type.equalsIgnoreCase("MOBILE")) {
					mobileConnected = true;
					Log.v(TAG, "Mobile connected");
					netMap.put("type", "net_type_mobile");
				} else if(type.equalsIgnoreCase("ETH")){
					ethConnected = true;
					Log.v(TAG, "Ethernet connected");
					netMap.put("type", "net_type_ethernet");
				} else {
					Log.v(TAG, "Unknown/unsupported network type");
					netMap.put("type", type + " net_type_unsupported");
				}
				try {
					String netId = ni.getExtraInfo();
					netMap.put("netID", netId);
				} catch (Exception e) {
					Log.v(TAG, "NetInfoAdapter.NetInfoAdapter14");
				}
				try {
					netMap.put("bgdata", cm.getBackgroundDataSetting() ? "permitted" : "denied");
					Log.v(TAG, "bgdata: " + netMap.get("bgdata"));
				} catch (Exception e) {
					Log.v(TAG, "NetInfoAdapter.NetInfoAdapter15");
				}
				// netMap.put("gateway", Tools.mySystem("/system/bin/getprop",
				// "net.rmnet0.gw", "").trim());

				try {
					isRoaming = ni.isRoaming();
				} catch (Exception e) {
					Log.v(TAG, "NetInfoAdapter.NetInfoAdapter16");
					isRoaming = false;
				}
				if (isRoaming) {
					netMap.put("roaming", "roaming_yes");
				} else {
					netMap.put("roaming", "roaming_no");
				}
			}
			// else {
			// //Unsupported network type
			// Log.v("Unknown/unsupported network type");
			// netMap.put("type", type + " net_type_unsupported");
			// }
			// netMap.put("dns", Tools.mySystem("/system/bin/getprop",
			// "net.dns1", "").trim());
		} else {
			netMap.put("state", "not_connected");
			netMap.put("dns", "");
		}
	}

	/**
	 * Return a string representation of the phone type given the integer
	 * returned from TelephonyManager.getPhoneType()
	 * 
	 * @param key
	 *            key for the info required
	 * @return string information relating to the key
	 */
	public String getPhoneType(Integer key) {
		if (phoneType.containsKey(key)) {
			return phoneType.get(key);
		} else {
			return strUnknown;
		}
	}

	/**
	 * Return a string representation of the network type type given the integer
	 * returned from TelephonyManager.getNetworkType()
	 * 
	 * @param key
	 *            key for the info required
	 * @return string information relating to the key
	 */
	public String getNetworkType(Integer key) {
		if (networkType.containsKey(key)) {
			return networkType.get(key);
		} else {
			return strUnknown;
		}
	}

	/**
	 * Return information relating to a key
	 * 
	 * @param key
	 *            key for the info required
	 * @return string information relating to the key
	 */
	public String getInfo(String key) {
		return exists(key) ? netMap.get(key) : "";
	}

	/**
	 * Returns if this key exists in the HashMap
	 * 
	 * @param key
	 *            key to look for
	 * @return boolean exits or not
	 */
	public boolean exists(String key) {
		return netMap.containsKey(key);
	}

	/**
	 * Returns the network state
	 * 
	 * @return boolean connected or not connected
	 */
	public boolean isConnected() {
		return netExists;
	}

	public boolean isEthConnected(){
		return ethConnected;
	}
	
	public boolean isWifiConnected() {
		return wifiConnected;
	}

	public boolean isMobileConnected() {
		return mobileConnected;
	}

	/**
	 * Returns the IP address of the supplied network interface
	 * 
	 * @param intf
	 *            interface to check
	 * @return dotted quad IP address
	 */
	private static String getIPAddress(NetworkInterface intf) {
		String result = "";
		for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
			InetAddress inetAddress = enumIpAddr.nextElement();
			result = inetAddress.getHostAddress();
		}
		return result;
	}

	/**
	 * Return the first network interface found that isn't localhost
	 * 
	 * @return first working internet interface found
	 */
	private static NetworkInterface getInternetInterface() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				if (!intf.equals(NetworkInterface.getByName("lo"))) {
					return intf;
				}
			}
		} catch (SocketException e) {
			Log.e(TAG, "getInternetInterface ERROR:" + e.toString());
		} catch (Exception e) {
			Log.v(TAG, "NetInfoAdapter.getInternetInterface");
			Log.e(TAG, "getInternetInterface ERROR:" + e.toString());
		}
		return null;
	}
}
