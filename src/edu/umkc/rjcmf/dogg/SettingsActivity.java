package edu.umkc.rjcmf.dogg;

import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity implements
		Preference.OnPreferenceClickListener {
	
	public static final String NAME = "edu.umkc.rjcmf.dogg_preferences";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings);
		//setContentView(R.layout.settings);

		Preference about = findPreference("about");
		String version;
		try {
			version = getPackageManager().getPackageInfo(getPackageName(),
					PackageManager.GET_ACTIVITIES).versionName;
		} catch (NameNotFoundException e) {
			version = "<unknown version>";
		}
		about.setSummary(getString(R.string.settings_about_summary, version));
		about.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				startActivity(new Intent(SettingsActivity.this,
						AboutActivity.class));
				return true;
			}
		});
		
		ActionBar actionBar = getActionBar();
		actionBar.show();
		actionBar.setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onPreferenceClick(Preference arg0) {
		// TODO Auto-generated method stub
		return false;
	}
}
