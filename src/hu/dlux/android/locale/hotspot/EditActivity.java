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

package hu.dlux.android.locale.hotspot;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.twofortyfouram.locale.BreadCrumber;
import com.twofortyfouram.locale.SharedResources;

/**
 * This is the "Edit" activity for a <i>Locale</i> plug-in.
 */
public final class EditActivity extends Activity
{

	/**
	 * Dialog ID for displaying the license agreement.
	 */
	private static final int DIALOG_LICENSE = 0;

	/**
	 * Help URL, used for the {@link com.twofortyfouram.locale.platform.R.id#twofortyfouram_locale_menu_help} menu item.
	 */
	// TODO: Place a real help URL here
	private static final String HELP_URL = "http://www.yourcompany.com/yourhelp.html"; //$NON-NLS-1$

	/**
	 * Flag boolean that can only be set to true via the "Don't Save"
	 * {@link com.twofortyfouram.locale.platform.R.id#twofortyfouram_locale_menu_dontsave} menu item in
	 * {@link #onMenuItemSelected(int, MenuItem)}.
	 * <p>
	 * If true, then this {@code Activity} should return {@link Activity#RESULT_CANCELED} in {@link #finish()}.
	 * <p>
	 * If false, then this {@code Activity} should generally return {@link Activity#RESULT_OK} with extras
	 * {@link com.twofortyfouram.locale.Intent#EXTRA_BUNDLE} and {@link com.twofortyfouram.locale.Intent#EXTRA_STRING_BLURB}.
	 * <p>
	 * There is no need to save/restore this field's state when the {@code Activity} is paused.
	 */
	private boolean mIsCancelled = false;

	/**
	 * AsyncTask to read SharedPreferences on a background thread. This is started in {@link #onResume()} and stopped in
	 * {@link #onPause()}.
	 */
	private AsyncTask<SharedPreferences, Void, Boolean> mPreferenceTask;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		/*
		 * This is a hack to work around a custom serializable classloader attack. This check must come before any of the Intent
		 * extras are examined.
		 */
		try
		{
			final Bundle extras = getIntent().getExtras();

			if (extras != null)
			{
				// if a custom serializable exists, this will throw an exception
				extras.containsKey(null);
			}
		}
		catch (final Exception e)
		{
			Log.e(Constants.LOG_TAG, "Custom serializable attack detected; do not send custom Serializable subclasses to this Activity", e); //$NON-NLS-1$
			getIntent().replaceExtras((Bundle) null);
		}

		/*
		 * Note: This is a hack to work around a custom serializable classloader attack via the EXTRA_BUNDLE. This check must come
		 * before any of the Bundle extras are examined.
		 */
		try
		{
			// if a custom serializable exists, this will throw an exception
			final Bundle extras = getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);

			if (extras != null)
			{
				// if a custom serializable exists, this will throw an exception
				extras.containsKey(null);
			}
		}
		catch (final Exception e)
		{
			Log.e(Constants.LOG_TAG, "Custom serializable attack detected; do not send custom Serializable subclasses to this Activity", e); //$NON-NLS-1$
			getIntent().putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, (Bundle) null);
		}

		setContentView(R.layout.main);

		setTitle(BreadCrumber.generateBreadcrumb(getApplicationContext(), getIntent(), getString(R.string.plugin_name)));

		/*
		 * Load the background frame from the host APK. Normally, the host APK should provide all of the necessary resources.
		 * However, a non-compliant host APK could potentially not provide the needed resources. The null checks performed here
		 * allow the use of default values, while also permitting the host APK to also customize the look-and-feel of the UI frame
		 */
		final Drawable borderDrawable = SharedResources.getDrawableResource(getPackageManager(), getCallingPackage(), SharedResources.DRAWABLE_LOCALE_BORDER);
		if (borderDrawable == null)
		{
			// this is ugly, but it maintains compatibility
			((FrameLayout) findViewById(R.id.frame)).setBackgroundColor(Color.WHITE);
		}
		else
		{
			((FrameLayout) findViewById(R.id.frame)).setBackgroundDrawable(borderDrawable);
		}

		/*
		 * if savedInstanceState == null, then then this is a new Activity instance and a check for EXTRA_BUNDLE is needed
		 */
		if (savedInstanceState == null)
		{
			final Bundle forwardedBundle = getIntent().getBundleExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE);

			/*
			 * the forwardedBundle would be null if this was a new setting
			 */
			if (forwardedBundle != null)
			{
				final String text = forwardedBundle.getString(Constants.BUNDLE_EXTRA_STRING_MESSAGE);

				/*
				 * this null test is unnecessary, because Locale won't modify the bundle. But this just protects against possible
				 * coding errors or malicious apps launching the Activity
				 */
				if (text != null)
				{
					((EditText) findViewById(R.id.message)).setText(text);
				}
			}
		}
		/*
		 * if savedInstanceState != null, there is no need to restore any Activity state directly via onSaveInstanceState()), as
		 * the TextView object handles that automatically
		 */
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onResume()
	{
		/*
		 * Reading SharedPreferences may involve a disk read, so this is performed on a background thread. Note that it might be
		 * possible for the user to do something in the UI before the license is agreed to (since the dialog is displayed at some
		 * time in the future after the disk read completes), however this really isn't worth worrying about.
		 */
		mPreferenceTask = new AsyncTask<SharedPreferences, Void, Boolean>()
		{

			@Override
			protected Boolean doInBackground(final SharedPreferences... params)
			{
				return params[0].getBoolean(Constants.PREFERENCE_BOOLEAN_IS_LICENSE_AGREED, false);
			}

			@Override
			protected void onPostExecute(final Boolean result)
			{
				if (!result)
				{
					showDialog(DIALOG_LICENSE);
				}
			}
		};

		mPreferenceTask.execute(getPreferences(MODE_PRIVATE));

		super.onResume();
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		/*
		 * The task must be canceled, otherwise the call to showDialog will throw an exception if the Activity has finished
		 */
		mPreferenceTask.cancel(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Dialog onCreateDialog(final int id)
	{
		switch (id)
		{
			case DIALOG_LICENSE:
			{
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.license_title);
				builder.setMessage(R.string.license_message);
				builder.setNegativeButton(R.string.license_disagree, new DialogInterface.OnClickListener()
				{
					@Override
					@SuppressWarnings("synthetic-access")
					public void onClick(final DialogInterface dialog, final int which)
					{
						mIsCancelled = true;
						finish();
					}
				});
				builder.setPositiveButton(R.string.license_agree, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(final DialogInterface dialog, final int which)
					{
						// the user hit "Agree", so the license shouldn't be displayed again
						final SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit().putBoolean(Constants.PREFERENCE_BOOLEAN_IS_LICENSE_AGREED, true);

						/*
						 * In SDK 9 and greater, use the new apply method to improve performance by avoiding a disk write on the
						 * main thread
						 */
						if (Build.VERSION.SDK_INT >= 9)
						{
							try
							{
								SharedPreferences.Editor.class.getMethod("apply").invoke(editor); //$NON-NLS-1$
							}
							catch (final Exception e)
							{
								/*
								 * This should always succeed under SDK 9 or greater
								 */
								throw new RuntimeException("Unable to perform Editor.apply under SDK 9 or greater", e); //$NON-NLS-1$
							}
						}
						else
						{
							/*
							 * This will perform a disk write on the main thread, which could cause a brief performance hiccup
							 */
							editor.commit();
						}
					}
				});

				/*
				 * Cover the back button
				 */
				builder.setOnCancelListener(new DialogInterface.OnCancelListener()
				{
					@Override
					@SuppressWarnings("synthetic-access")
					public void onCancel(final DialogInterface dialog)
					{
						mIsCancelled = true;
						finish();
					}
				});

				return builder.create();
			}
		}
		return super.onCreateDialog(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void finish()
	{
		if (mIsCancelled)
		{
			setResult(RESULT_CANCELED);
		}
		else
		{
			final String message = ((EditText) findViewById(R.id.message)).getText().toString();

			/*
			 * If the message is of 0 length, then there isn't a setting to save.
			 */
			if (0 == message.length())
			{
				setResult(RESULT_CANCELED);
			}
			else
			{
				/*
				 * This is the return Intent, into which we'll put all the required extras
				 */
				final Intent returnIntent = new Intent();

				/*
				 * This extra is the data to ourselves: either for the Activity or the BroadcastReceiver. Note that anything
				 * placed in this Bundle must be available to Locale's class loader. So storing String, int, and other standard
				 * objects will work just fine. However Parcelable objects must also be Serializable. And Serializable objects
				 * must be standard Java objects (e.g. a custom object private to this plug-in cannot be stored in the Bundle, as
				 * Locale's classloader will not recognize it).
				 */
				final Bundle storeAndForwardExtras = new Bundle();
				storeAndForwardExtras.putString(Constants.BUNDLE_EXTRA_STRING_MESSAGE, message);

				returnIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE, storeAndForwardExtras);

				/*
				 * This is the blurb concisely describing what your setting's state is. This is simply used for display in the UI.
				 */
				if (message.length() > getResources().getInteger(R.integer.twofortyfouram_locale_maximum_blurb_length))
				{
					returnIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, message.substring(0, getResources().getInteger(R.integer.twofortyfouram_locale_maximum_blurb_length)));
				}
				else
				{
					returnIntent.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, message);
				}

				setResult(RESULT_OK, returnIntent);
			}
		}

		super.finish();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu)
	{
		super.onCreateOptionsMenu(menu);

		/*
		 * inflate the default menu layout from XML
		 */
		getMenuInflater().inflate(R.menu.twofortyfouram_locale_help_save_dontsave, menu);

		/*
		 * Resources will be dynamically loaded from the host APK (e.g. Locale). Normally, the host APK should provide all of the
		 * necessary resources. However, a non-compliant host APK could potentially not provide the needed resources. The null
		 * checks performed here allow the use of default icons and strings from the inflated XML file, while also permitting the
		 * host APK to also customize the look-and-feel of the menu items.
		 */
		final PackageManager manager = getPackageManager();

		final MenuItem helpItem = menu.findItem(R.id.twofortyfouram_locale_menu_help);
		final CharSequence helpString = SharedResources.getTextResource(manager, getCallingPackage(), SharedResources.STRING_MENU_HELP);
		final Drawable helpIcon = SharedResources.getDrawableResource(manager, getCallingPackage(), SharedResources.DRAWABLE_MENU_HELP);
		if (helpString != null)
		{
			helpItem.setTitle(SharedResources.getTextResource(manager, getCallingPackage(), SharedResources.STRING_MENU_HELP));
		}
		if (helpIcon != null)
		{
			helpItem.setIcon(helpIcon);
		}

		final MenuItem dontSaveItem = menu.findItem(R.id.twofortyfouram_locale_menu_dontsave);
		final CharSequence dontSaveTitle = SharedResources.getTextResource(manager, getCallingPackage(), SharedResources.STRING_MENU_DONTSAVE);
		final Drawable dontSaveIcon = SharedResources.getDrawableResource(manager, getCallingPackage(), SharedResources.DRAWABLE_MENU_DONTSAVE);
		if (dontSaveTitle != null)
		{
			dontSaveItem.setTitle(dontSaveTitle);
		}
		if (dontSaveIcon != null)
		{
			dontSaveItem.setIcon(dontSaveIcon);
		}

		final MenuItem saveItem = menu.findItem(R.id.twofortyfouram_locale_menu_save);
		final CharSequence saveTitle = SharedResources.getTextResource(manager, getCallingPackage(), SharedResources.STRING_MENU_SAVE);
		final Drawable saveIcon = SharedResources.getDrawableResource(manager, getCallingPackage(), SharedResources.DRAWABLE_MENU_SAVE);
		if (saveTitle != null)
		{
			saveItem.setTitle(saveTitle);
		}
		if (saveIcon != null)
		{
			saveItem.setIcon(saveIcon);
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.twofortyfouram_locale_menu_help:
			{
				try
				{
					startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(HELP_URL)));
				}
				catch (final Exception e)
				{
					Toast.makeText(getApplicationContext(), com.twofortyfouram.locale.platform.R.string.twofortyfouram_locale_application_not_available, Toast.LENGTH_LONG).show();
				}

				return true;
			}
			case R.id.twofortyfouram_locale_menu_dontsave:
			{
				mIsCancelled = true;
				finish();
				return true;
			}
			case R.id.twofortyfouram_locale_menu_save:
			{
				finish();
				return true;
			}
		}

		return super.onOptionsItemSelected(item);
	}
}