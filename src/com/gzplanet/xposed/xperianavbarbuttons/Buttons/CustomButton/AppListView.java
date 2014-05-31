package com.gzplanet.xposed.xperianavbarbuttons.Buttons.CustomButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gzplanet.xposed.xperianavbarbuttons.R;

import de.robv.android.xposed.XposedBridge;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class AppListView extends Activity implements OnItemClickListener  {
	@SuppressWarnings("unused")
	private static final String TAG = "AppListView";
	private ListView listView = null;
	private AppData appData;
	private AppDataAdapter appDataAdapter = null;
	private Drawable appIcon = null;
	@SuppressWarnings("unused")
	private AsyncTask<Void, Void, ArrayList<AppData>> mAsyncTask;
	private ProgressDialog mProgressDialog;	
 	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setTitle("Custom button select");
		setContentView(R.layout.applistview);

		listView = (ListView) findViewById(R.id.appListView);
		listView.setOnItemClickListener(this);
		
		setAppList();
	}

	private void setAppList(){
		mAsyncTask = new AsyncTask<Void,Void,ArrayList<AppData>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                showProgressDialog();
            }
            
            @Override
            protected ArrayList<AppData> doInBackground(Void... arg0) {
        		ArrayList<AppData> objects = new ArrayList<AppData>();
        		PackageManager pm = AppListView.this.getPackageManager();
        		List<ApplicationInfo> list = pm.getInstalledApplications(0);
        		Collections.sort(list, new ApplicationInfo.DisplayNameComparator(pm));
        		for (ApplicationInfo ai : list) {
                    // Exclude system app
                    if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) continue;
                    // Exclude this app
                    if (ai.packageName.equals(AppListView.this.getPackageName())) continue;
        			appData = new AppData();
        			appData.setAppPackageName(ai.packageName);
        			if (ai.loadLabel(pm).toString() != null) {
        				appData.setAppName(ai.loadLabel(pm).toString());
        			} else {
        				appData.setAppName("NoName");
        			}
        			try {
        				appIcon = pm.getApplicationIcon(ai.packageName);
        			} catch (NameNotFoundException e) {
        				XposedBridge.log(e);
        			}
        			appData.setAppIcon(appIcon);

        			objects.add(appData);
        			appDataAdapter = new AppDataAdapter(AppListView.this, 0, objects);
        		}
				return objects;
            }
        		
            @Override
            protected void onCancelled() {
            	dismissProgressDialog();
            }
            
            @Override
            protected void onPostExecute(ArrayList<AppData> result) {
                dismissProgressDialog();
    			listView.setAdapter(appDataAdapter);
            }
		}.execute();
	}
	
    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(AppListView.this);
        mProgressDialog.setMessage(getString(R.string.app_loading));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListView listView = (ListView) parent;
        appData = (AppData) listView.getItemAtPosition(position);
        String appPackageName = appData.getAppPackageName();
		Intent intent = new Intent();
		intent.putExtra("select_custom_app", appPackageName);
		setResult(RESULT_OK, intent);
		finish();
    }
}
