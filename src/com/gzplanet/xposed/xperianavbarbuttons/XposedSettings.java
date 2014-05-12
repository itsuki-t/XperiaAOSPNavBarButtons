package com.gzplanet.xposed.xperianavbarbuttons;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class XposedSettings extends PreferenceActivity {

	int mScreenWidth;
	int mButtonWidth;
	int mButtonsCount = 2;
	boolean mShowMenu;
	boolean mShowSearch;
	boolean mShowRecent;
	boolean mShowPower;
	boolean mShowExpand;
	boolean mShowSpace;

	String mNavbarHeightString;
	String mNavbarHeight;
	
	ListPreference mPrefNavibarHeight;
	Preference mPrefRestartSystemUI;
	Preference mPrefButtonSettings;

	ButtonSettings mSettings;

	static int[] mIconId = { R.id.iv1, R.id.iv2, R.id.iv3, R.id.iv4, R.id.iv5, R.id.iv6 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTitle(R.string.app_name);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		addPreferencesFromResource(R.xml.preferences);

		// get screen width
		final Display defaultDisplay = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		final Point point = new Point();
		defaultDisplay.getSize(point);
		mScreenWidth = point.x;

		String order = getPreferenceManager().getSharedPreferences().getString("pref_order", null);
		mSettings = new ButtonSettings(this, order);

		mShowMenu = mSettings.isShowMenu();
		if (mShowMenu)
			mButtonsCount++;
		mShowSearch = mSettings.isShowSearch();
		if (mShowSearch)
			mButtonsCount++;
		mShowRecent = mSettings.isShowRecent();
		if (mShowRecent)
			mButtonsCount++;
		mShowPower = mSettings.isShowPower();
		if (mShowPower)
			mButtonsCount++;
		mShowExpand = mSettings.isShowExpand();
		if (mShowExpand)
			mButtonsCount++;
		mShowSpace = mSettings.isShowSpace();
		if (mShowSpace)
			mButtonsCount++;

		updatePreviewPanel();

		// this is important because although the handler classes that read
		// these settings
		// are in the same package, they are executed in the context of the
		// hooked package
		getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mNavbarHeightString = sharedPreferences.getString("nh_e_list_preference", "Defaults");
		mPrefNavibarHeight = (ListPreference) findPreference("nh_list_preference");
		mPrefNavibarHeight.setSummary("Current Height : "+ mNavbarHeightString);

		mPrefNavibarHeight.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				CharSequence[] entries,entryValues;
				String strEntries,strEntryValues;
				int listId = 0;

				if(newValue != null){
					listId = mPrefNavibarHeight.findIndexOfValue((String) newValue);
					entries = mPrefNavibarHeight.getEntries();
					entryValues = mPrefNavibarHeight.getEntryValues();
					strEntries = (String) entries[listId];
					strEntryValues = (String) entryValues[listId];
					getPreferenceManager().getSharedPreferences().edit().putString("nh_e_list_preference", strEntries).commit();
					getPreferenceManager().getSharedPreferences().edit().putString("nh_ev_list_preference", strEntryValues).commit();
					preference.setSummary("Current Height : " + strEntries);
					return true;
				}
				return false;
		      }
		});
		
		mPrefButtonSettings = (Preference) findPreference("pref_button_settings");

		mPrefButtonSettings.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(XposedSettings.this, ButtonSettingsActivity.class);
				intent.putExtra("order_list", mSettings.getOrderListString());
				startActivityForResult(intent, 1);
				return true;
			}
		});

		mPrefRestartSystemUI = (Preference) findPreference("pref_restart_systemui");

		mPrefRestartSystemUI.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				try {
					Toast.makeText(XposedSettings.this, "Restarting SystemUI...", Toast.LENGTH_SHORT).show();

					final String pkgName = XposedSettings.this.getPackageName();
					final String pkgFilename = pkgName + "_preferences";
					final File prefFile = new File(Environment.getDataDirectory(), "data/" + pkgName + "/shared_prefs/" + pkgFilename + ".xml");
					Log.d("XposedSettings", prefFile.getAbsolutePath());

					// make shared preference world-readable
					Process sh = Runtime.getRuntime().exec("su", null, null);
					OutputStream os = sh.getOutputStream();
					os.write(("chmod 664 " + prefFile.getAbsolutePath()).getBytes("ASCII"));
					os.flush();
					os.close();
					try {
						sh.waitFor();
					} catch (Exception e) {
						e.printStackTrace();
					}

					// restart SystemUI process
					sh = Runtime.getRuntime().exec("su", null, null);
					os = sh.getOutputStream();
					os.write(("pkill com.android.systemui").getBytes("ASCII"));
					os.flush();
					os.close();
					try {
						sh.waitFor();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			String[] items = data.getStringExtra("order_list").split(",");
			mButtonsCount = items.length;
			String settings = data.getStringExtra("order_list");
			getPreferenceManager().getSharedPreferences().edit().putString("pref_order", settings).commit();
			mSettings = new ButtonSettings(this, settings);
			updatePreviewPanel();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	void updatePreviewPanel() {
		mButtonWidth = Math.round((float) mScreenWidth / (float) mButtonsCount);

		LinearLayout panel = (LinearLayout) findViewById(R.id.previewPanel);
		for (int i = 0; i < mIconId.length; i++) {
			ImageView iv = (ImageView) panel.findViewById(mIconId[i]);
			if (i < mButtonsCount) {
				iv.setLayoutParams(new LinearLayout.LayoutParams(mButtonWidth, LinearLayout.LayoutParams.FILL_PARENT, 0.0f));
				iv.setImageDrawable(mSettings.getButtonDrawable(i));
				iv.setVisibility(View.VISIBLE);
			} else {
				iv.setVisibility(View.GONE);
			}
		}
	}
}