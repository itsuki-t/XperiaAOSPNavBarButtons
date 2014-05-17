package com.gzplanet.xposed.xperianavbarbuttons.CustomButton;

import android.graphics.drawable.Drawable;

public class AppData {
    private Drawable appIcon_;
    private String appName_;
    private String packageName_;
    
    public void setAppIcon(Drawable icon) {
        appIcon_ = icon;
    }
 
    public Drawable getAppIcon() {
        return appIcon_;
    }
 
    public void setAppName(String name) {
        appName_ = name;
    }
 
    public String getAppName() {
        return appName_;
    }
    
    public void setPackageName(String name) {
        packageName_ = name;
    }
 
    public String getPackageName() {
        return packageName_;
    }
}