package com.gzplanet.xposed.xperianavbarbuttons;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.gzplanet.xposed.xperianavbarbuttons.Buttons.CustomButton.AppListView;
import com.gzplanet.xposed.xperianavbarbuttons.Util.Utils;

import de.robv.android.xposed.XposedBridge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class XposedSettings extends PreferenceActivity {
	@SuppressWarnings("unused")
	private static final String TAG = "XposedSettings";
	int mScreenWidth;
	int mButtonWidth;
	int mButtonsCount = 2;

	String mCustomAppName;
	String mCustomAppPackageName;
	String mLongpressDelayString;
	String mNavbarHeightString;

	Preference mPrefCustomButton;
	Preference mPrefButtonSettings;
	ListPreference mPrefLongpressDelay;
	ListPreference mPrefNavibarHeight;
	Preference mPrefRestartSystemUI;

	ButtonSettings mSettings;

	static int[] mIconId = { R.id.iv1, R.id.iv2, R.id.iv3, R.id.iv4, R.id.iv5, R.id.iv6 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setTitle(R.string.app_name);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		addPreferencesFromResource(R.xml.preferences);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		// get screen width
		final Display defaultDisplay = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		final Point point = new Point();
		defaultDisplay.getSize(point);
		mScreenWidth = point.x;

		String order = getPreferenceManager().getSharedPreferences().getString("pref_order", null);
		mSettings = new ButtonSettings(this, order);
		mButtonsCount = mSettings.mNavBarButton.getValueOfFlagIsTrue();

		updatePreviewPanel();

		// this is important because although the handler classes that read
		// these settings
		// are in the same package, they are executed in the context of the
		// hooked package
		getPreferenceManager().setSharedPreferencesMode(MODE_WORLD_READABLE);

		mCustomAppName = sharedPreferences.getString("pref_custom_button_appname", "Not selected");
		mCustomAppPackageName = sharedPreferences.getString("pref_custom_button_packagename", null);
		mPrefCustomButton = (Preference) findPreference("pref_custom_button");
		mPrefCustomButton.setSummary("Current : "+ mCustomAppName);
		// Set AppIcon
		if(mCustomAppPackageName != null){
			try {
				PackageManager pm = getPackageManager();
				ApplicationInfo appInfo = pm.getApplicationInfo(mCustomAppPackageName, 0);
				if(appInfo != null){
					Drawable appIcon = appInfo.loadIcon(pm);
		            if (appIcon != null) {
		            	Bitmap bitmap = Utils.drawableToBitmap(appIcon);
		                int appIconSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, this.getResources().getDisplayMetrics());
		            	bitmap = Bitmap.createScaledBitmap(bitmap, appIconSizePx, appIconSizePx, false);
		            	appIcon = new BitmapDrawable(this.getResources(), bitmap);
						mPrefCustomButton.setIcon(appIcon);
		            }
				}
			} catch (NameNotFoundException e) {
				XposedBridge.log(e);
			}
		}
		mPrefCustomButton.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent(XposedSettings.this, AppListView.class);
				startActivityForResult(intent, 2);
				return true;
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

		mLongpressDelayString = sharedPreferences.getString("ld_e_list_preference", "Defaults");
		mPrefLongpressDelay = (ListPreference) findPreference("ld_list_preference");
		mPrefLongpressDelay.setSummary(getString(R.string.button_longpress_delay_summary,mLongpressDelayString));
		mPrefLongpressDelay.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				CharSequence[] entries,entryValues;
				String strEntries,strEntryValues;
				int listId = 0;

				if(newValue != null){
					listId = mPrefLongpressDelay.findIndexOfValue((String) newValue);
					entries = mPrefLongpressDelay.getEntries();
					entryValues = mPrefLongpressDelay.getEntryValues();
					strEntries = (String) entries[listId];
					strEntryValues = (String) entryValues[listId];
					getPreferenceManager().getSharedPreferences().edit().putString("ld_e_list_preference", strEntries).commit();
					getPreferenceManager().getSharedPreferences().edit().putString("ld_ev_list_preference", strEntryValues).commit();
					preference.setSummary(getString(R.string.button_longpress_delay_summary,strEntries));
					return true;
				}
				return false;
		      }
		});

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

		mPrefRestartSystemUI = (Preference) findPreference("pref_restart_systemui");
		mPrefRestartSystemUI.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				try {
					Toast.makeText(XposedSettings.this, "Restarting SystemUI...", Toast.LENGTH_SHORT).show();

					final String pkgName = XposedSettings.this.getPackageName();
					final String pkgFilename = pkgName + "_preferences";
					final File prefFile = new File(Environment.getDataDirectory(), "data/" + pkgName + "/shared_prefs/" + pkgFilename + ".xml");
//					Log.d("XposedSettings", prefFile.getAbsolutePath());

					// make shared preference world-readable
					Process sh = Runtime.getRuntime().exec("su", null, null);
					OutputStream os = sh.getOutputStream();
					os.write(("chmod 664 " + prefFile.getAbsolutePath()).getBytes("ASCII"));
					os.flush();
					os.close();
					try {
						sh.waitFor();
					} catch (Exception e) {
						XposedBridge.log(e);
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
						XposedBridge.log(e);
					}
				} catch (IOException e) {
					XposedBridge.log(e);
				}
				return true;
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
			String[] items = data.getStringExtra("order_list").split(",");
			mButtonsCount = items.length;
			String settings = data.getStringExtra("order_list");
			getPreferenceManager().getSharedPreferences().edit().putString("pref_order", settings).commit();
			mSettings = new ButtonSettings(this, settings);
			updatePreviewPanel();
		}else if (requestCode == 2 && resultCode == Activity.RESULT_OK) {
			String pName = data.getStringExtra("select_custom_app");
			getPreferenceManager().getSharedPreferences().edit().putString("pref_custom_button_packagename", pName).commit();
			try{
				PackageManager pm = getPackageManager();
				ApplicationInfo ai = pm.getApplicationInfo(pName, 0);
				// Set AppName
				String appName = pm.getApplicationLabel(ai).toString();
				getPreferenceManager().getSharedPreferences().edit().putString("pref_custom_button_appname", appName).commit();
				mPrefCustomButton.setSummary("Current : "+ appName);
				// Set AppIcon
				Drawable appIcon = ai.loadIcon(pm);
	            if (appIcon != null) {
	            	Bitmap bitmap = Utils.drawableToBitmap(appIcon);
	                int appIconSizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, this.getResources().getDisplayMetrics());
	            	bitmap = Bitmap.createScaledBitmap(bitmap, appIconSizePx, appIconSizePx, false);
	            	appIcon = new BitmapDrawable(this.getResources(), bitmap);
					mPrefCustomButton.setIcon(appIcon);
	            }
			}catch(NameNotFoundException e) {
				XposedBridge.log(e);
		    }
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