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

		if (Preferences.isFirstTime(this)) {
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivityForResult(intent, 0);
		}

		Resources res = getResources(); // Resource object to get Drawables
		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab

		// Create an Intent to launch an Activity for the tab (to be reused)
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			finish();
		}
	}
}