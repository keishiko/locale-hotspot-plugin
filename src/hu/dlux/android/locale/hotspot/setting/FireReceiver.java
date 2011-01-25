// Copyright 2011 two forty four a.m. LLC <http://www.twofortyfouram.com>
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package hu.dlux.android.locale.hotspot.setting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * This is the "fire" {@code BroadcastReceiver} for a <i>Locale</i> plug-in setting.
 */
public final class FireReceiver extends BroadcastReceiver
{

	/**
	 * @param context {@inheritDoc}.
	 * @param intent the incoming {@link com.twofortyfouram.locale.Intent#ACTION_FIRE_SETTING} {@code Intent}. This should always
	 *            contain the store-and-forward {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE} that was saved by
	 *            {@link EditActivity} and later broadcast by <i>Locale</i>.
	 */
	@Override
	public void onReceive(final Context context, final Intent intent)
	{
		if (com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING.equals(intent.getAction())) {
			boolean on = StateBundle.from(intent).isOn();
			Toast.makeText(context, "Turning Wifi Hotspot " + (on ? "ON" : "OFF"), 3).show();
		}
	}
}