package hu.dlux.android.locale.hotspot.setting;

import android.content.Intent;
import android.os.Bundle;

/**
 * Bundle object to store the hotspot setting state. This is automatically passed by Locale.
 *  
 * @author dlux
 */
public class StateBundle {
	private static final String KEY = "on";
	private boolean on;

	public StateBundle() {};

	public static StateBundle from(Intent intent) {
		final StateBundle retval = new StateBundle();
		final Bundle forwardedBundle = intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
		if (forwardedBundle == null) {
			return retval;
		}
		retval.on = forwardedBundle.getBoolean(KEY);
		return retval;
	}
	
	public Bundle build() {
		final Bundle bundle = new Bundle();
		bundle.putBoolean(KEY, on);
		return bundle;
	}
	
	public boolean isOn() {
		return on;
	}
	
	public StateBundle setOn(boolean on) {
		this.on = on;
		return this;
	}
}