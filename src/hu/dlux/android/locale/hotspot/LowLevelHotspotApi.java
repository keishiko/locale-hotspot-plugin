package hu.dlux.android.locale.hotspot;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

// This code is based on the Android source code from here:
// http://www.google.com/codesearch/p?hl=en#ohAXAHj6Njg/src/com/android/settings/wifi/WifiApEnabler.java&q=Settings%20WifiApEnabler&l=110

public class LowLevelHotspotApi {

	public void changeHotspotState(Context context, boolean enable) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		// Turn off Wifi if the hotspot is to be enabled.
		// TODO(dlux): save/restore state
		if (enable) {
			wifiManager.setWifiEnabled(false);
		}
		try {
			wifiManager.getClass()
				.getMethod("setWifiApEnabled",
						   WifiConfiguration.class,
						   boolean.class)
			    .invoke(wifiManager, null, enable);
		} catch (Exception e) {
			Log.e(Constants.LOG_TAG, "Cannot call: setWifiApEnabled", e);
		}
	}
}
