package hu.dlux.android.locale.hotspot;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

// This code is based on the Android source code from here:
// http://www.google.com/codesearch/p?hl=en#ohAXAHj6Njg/src/com/android/settings/wifi/WifiApEnabler.java&q=Settings%20WifiApEnabler&l=110

public class LowLevelHotspotApi {
	private final Context context;

	public LowLevelHotspotApi(Context context) {
		this.context = context;
	}

	public void changeHotspotState(boolean enable) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		// Turn off Wifi if the hotspot is to be enabled.
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
			Log.e(Constants.TAG, "Cannot call: setWifiApEnabled", e);
		}
	}
}
