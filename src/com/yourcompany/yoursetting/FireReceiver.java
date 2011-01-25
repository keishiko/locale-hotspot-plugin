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

package com.yourcompany.yoursetting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
		/*
		 * Always be sure to be strict on input parameters! A malicious third-party app could always send an empty or otherwise
		 * malformed Intent. And since Locale applies settings in the background, the plug-in definitely shouldn't crash in the
		 * background
		 */

		/*
		 * Locale guarantees that the Intent action will be ACTION_FIRE_SETTING
		 */
		if (!com.twofortyfouram.locale.Intent.ACTION_FIRE_SETTING.equals(intent.getAction()))
		{
			Log.e(Constants.LOG_TAG, String.format("Received unexpected Intent action %s", intent.getAction())); //$NON-NLS-1$
			return;
		}

		/*
		 * This is a hack to work around a custom serializable classloader attack. This check must come before any of the Intent
		 * extras are examined.
		 */
		try
		{
			final Bundle extras = intent.getExtras();

			if (extras != null)
			{
				// if a custom serializable subclass exists, this will throw an exception
				extras.containsKey(null);
			}
		}
		catch (final Exception e)
		{
			Log.e(Constants.LOG_TAG, "Custom serializable attack detected; do not send custom Serializable subclasses to this receiver", e); //$NON-NLS-1$
			return;
		}

		final Bundle bundle = intent.getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);
		if (bundle == null)
		{
			Log.e(Constants.LOG_TAG, "Received null BUNDLE"); //$NON-NLS-1$
			return;
		}

		/*
		 * Note: This is a hack to work around a custom serializable classloader attack via the EXTRA_BUNDLE. This check must come
		 * before any of the Bundle extras are examined.
		 */
		try
		{
			// if a custom serializable subclass exists, this will throw an exception
			bundle.containsKey(null);
		}
		catch (final Exception e)
		{
			Log.e(Constants.LOG_TAG, "Custom serializable attack detected; do not send custom Serializable subclasses to this receiver", e); //$NON-NLS-1$
			return;
		}

		if (!bundle.containsKey(Constants.BUNDLE_EXTRA_STRING_MESSAGE))
		{
			Log.e(Constants.LOG_TAG, "Missing STATE param in Bundle"); //$NON-NLS-1$
			return;
		}

		final String message = bundle.getString(Constants.BUNDLE_EXTRA_STRING_MESSAGE);

		if (TextUtils.isEmpty(message))
		{
			Log.e(Constants.LOG_TAG, "BUNDLE_EXTRA_STRING_MESSAGE was empty"); //$NON-NLS-1$
			return;
		}

		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}
}