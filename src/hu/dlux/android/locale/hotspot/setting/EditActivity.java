package hu.dlux.android.locale.hotspot.setting;

import hu.dlux.android.locale.hotspot.R;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import com.twofortyfouram.locale.BreadCrumber;
import com.twofortyfouram.locale.SharedResources;

public class EditActivity extends Activity {

	private boolean isCancelled = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_setting);

		// Sets the state of the widget from the saved instance state or from
		// the extra provided from Locale. If savedInstanceState is not null, then
		// the state is automatically restored.
		if (savedInstanceState == null) {
			((ToggleButton) findViewById(R.id.toggle)).setChecked(StateBundle.from(getIntent()).isOn());
		}

		// Boilerplate from the Locale example application:
		setTitle(BreadCrumber.generateBreadcrumb(getApplicationContext(),
				getIntent(), getString(R.string.app_name)));

		final Drawable borderDrawable = SharedResources.getDrawableResource(
				getPackageManager(), getCallingPackage(),
				SharedResources.DRAWABLE_LOCALE_BORDER);
		final FrameLayout frame = (FrameLayout) findViewById(R.id.frame); 
		if (borderDrawable == null) {
			frame.setBackgroundColor(Color.WHITE);
		} else {
			frame.setBackgroundDrawable(borderDrawable);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.twofortyfouram_locale_help_save_dontsave, menu);

		// Disable "Help" for now.
		menu.findItem(R.id.twofortyfouram_locale_menu_help).setVisible(false);

		// Boilerplate from the Locale example application:
		final PackageManager manager = getPackageManager();

		final MenuItem dontSaveItem = menu.findItem(R.id.twofortyfouram_locale_menu_dontsave);
		final CharSequence dontSaveTitle = SharedResources.getTextResource(manager, getCallingPackage(),
				SharedResources.STRING_MENU_DONTSAVE);
		final Drawable dontSaveIcon = SharedResources.getDrawableResource(manager, getCallingPackage(),
				SharedResources.DRAWABLE_MENU_DONTSAVE);
		if (dontSaveTitle != null) {
			dontSaveItem.setTitle(dontSaveTitle);
		}
		if (dontSaveIcon != null) {
			dontSaveItem.setIcon(dontSaveIcon);
		}

		final MenuItem saveItem = menu.findItem(R.id.twofortyfouram_locale_menu_save);
		final CharSequence saveTitle = SharedResources.getTextResource(manager,
				getCallingPackage(), SharedResources.STRING_MENU_SAVE);
		final Drawable saveIcon = SharedResources.getDrawableResource(manager,
				getCallingPackage(), SharedResources.DRAWABLE_MENU_SAVE);
		if (saveTitle != null) {
			saveItem.setTitle(saveTitle);
		}
		if (saveIcon != null) {
			saveItem.setIcon(saveIcon);
		}

		return onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.twofortyfouram_locale_menu_save:
			finish();
			return true;
		case R.id.twofortyfouram_locale_menu_dontsave:
			isCancelled = true;
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void prepareResultForFinish() {
		if (isCancelled) {
			setResult(RESULT_CANCELED);
			return;
		}
		final Intent result = new Intent();
		final ToggleButton toggle = ((ToggleButton) findViewById(R.id.toggle));
		result.putExtra(com.twofortyfouram.locale.Intent.EXTRA_BUNDLE,
				new StateBundle().setOn(toggle.isChecked()).build());
		result.putExtra(com.twofortyfouram.locale.Intent.EXTRA_STRING_BLURB, toggle.getText());
		setResult(RESULT_OK, result);
	}
	
	@Override
	public void finish() {
		prepareResultForFinish();
		super.finish();
	}
}
