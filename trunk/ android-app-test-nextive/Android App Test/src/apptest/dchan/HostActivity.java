package apptest.dchan;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class HostActivity extends TabActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Checks to see if this is the first time the app has been opened. If
		// it is then go to the settings page.
		if (Preferences.isFirstTime(this)) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivityForResult(intent, 0);
		}

		Resources res = getResources();
		TabHost tabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		// Create an Intent to launch an Activity for the tab
		intent = new Intent().setClass(this, LogActivity.class);

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("Log")
				.setIndicator("Log", res.getDrawable(R.drawable.ic_tab_log)).setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, HistoryActivity.class);
		spec = tabHost.newTabSpec("History")
				.setIndicator("History", res.getDrawable(R.drawable.ic_tab_history))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, ShareActivity.class);
		spec = tabHost.newTabSpec("Share")
				.setIndicator("Share", res.getDrawable(R.drawable.ic_tab_share)).setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, SettingsActivity.class);
		spec = tabHost.newTabSpec("Settings")
				.setIndicator("Settings", res.getDrawable(R.drawable.ic_tab_settings))
				.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(0);

	}

	/**
	 * If the settings were not saved successfully on the first use then exit
	 * the app.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			finish();
		}
	}
}