package com.gzplanet.xposed.xperianavbarbuttons.Buttons.CustomButton;

import android.graphics.drawable.Drawable;

public class AppData {
	@SuppressWarnings("unused")
	private static final String TAG = "AppData";
    private String appName;
    private String appPackageName;
    private Drawable appIcon;

    public void setAppName(String name) {
        appName = name;
    }
 
    public String getAppName() {
        return appName;
    }
    
    public void setAppPackageName(String name) {
    	appPackageName = name;
    }
 
    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppIcon(Drawable icon) {
        appIcon = icon;
    }
 
    public Drawable getAppIcon() {
        return appIcon;
    }
}