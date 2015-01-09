/*
 * Author: Ryan Johnson
 * CS449 Semester Project
 * UMKC - Spring 2014
 * Contact: rjcmf@mail.umkc.edu
 * 
 * Application Name: Dogg
 *
 * Copyright: 2014
 *
 */

package edu.umkc.rjcmf.dogg;

import android.app.ActionBar;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class Dogg extends TabActivity {
	public static final String VISIBLE_TAB = "visible_tab";
	public static final String TAG_POTTY = "potties";
	public static final String TAG_HISTORY = "history";
	public static final String TAG_PETS = "pets";

	private TabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabs);

		setupTabs();

		ActionBar actionBar = getActionBar();
		actionBar.show();

	}

	public void setupTabs() {
		mTabHost = getTabHost();

		// add the tabs here
		mTabHost.addTab(createTabSpec(TAG_POTTY, PottyActivity.class,
				R.string.potty));
		mTabHost.addTab(createTabSpec(TAG_HISTORY, PottyListActivity.class,
				R.string.history));
		mTabHost.addTab(createTabSpec(TAG_PETS, PetListActivity.class,
				R.string.pets));		
		
		mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(
						mTabHost.getApplicationWindowToken(), 0);
			}
		});
		
		String requestedTab = getIntent().getStringExtra(VISIBLE_TAB);
		if (requestedTab != null) {
			switchTo(requestedTab);
		}
	}

	public void switchToHistoryTab() {
		switchTo(TAG_HISTORY);
	}

	public void switchTo(String tag) {
		mTabHost.setCurrentTabByTag(tag);
	}

	private TabSpec createTabSpec(String tag, Class<? extends Activity> cls,
			int string) {
		TabSpec spec = mTabHost.newTabSpec(tag);
		spec.setContent(new Intent(this, cls));
		spec.setIndicator(getString(string));
		return spec;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		add(menu, R.string.add_pet, PetActivity.class);
		add(menu, R.string.edit_pet_types, PetTypeListActivity.class);
		add(menu, R.string.settings, SettingsActivity.class);

		return super.onCreateOptionsMenu(menu);
	}

	private final MenuItem add(final Menu menu, final int string,
			final Class<? extends Activity> cls) {
		return menu.add(Menu.NONE, string, Menu.NONE, string).setIntent(
				new Intent(this, cls));
	}
}
